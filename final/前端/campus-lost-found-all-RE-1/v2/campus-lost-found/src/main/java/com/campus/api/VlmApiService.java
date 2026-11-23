package com.campus.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class VlmApiService {
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";

    public JSONObject generateItemInfo(String imageUrl) {
        try {
            Properties props = loadProperties();
            String apiKey = props.getProperty("dashscope.api.key");
            String model = props.getProperty("dashscope.vlm.model");

            if (apiKey == null || apiKey.trim().isEmpty()) {
                return buildFailResult("未配置 dashscope.api.key");
            }
            if (model == null || model.trim().isEmpty()) {
                return buildFailResult("未配置 dashscope.vlm.model");
            }
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return buildFailResult("图片地址不能为空");
            }

            String prompt = "你是校园失物招领助手，请分析这张图片中的物品。"
                    + "你必须只返回一个标准JSON对象，不要返回Markdown代码块，不要返回```json，不要返回解释文字。"
                    + "返回格式如下："
                    + "{\"title\":\"物品名\",\"category\":\"分类\",\"description\":\"描述\"}";

            JSONObject reqJson = new JSONObject();
            reqJson.put("model", model);

            JSONObject textObj = new JSONObject();
            textObj.put("type", "text");
            textObj.put("text", prompt);

            JSONObject imageObj = new JSONObject();
            imageObj.put("type", "image");
            imageObj.put("image", imageUrl);

            JSONArray contentArr = new JSONArray();
            contentArr.add(textObj);
            contentArr.add(imageObj);

            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", contentArr);

            JSONArray messages = new JSONArray();
            messages.add(messageObj);

            JSONObject inputObj = new JSONObject();
            inputObj.put("messages", messages);
            reqJson.put("input", inputObj);

            JSONObject parametersObj = new JSONObject();
            parametersObj.put("result_format", "json");
            parametersObj.put("temperature", 0.1);
            reqJson.put("parameters", parametersObj);

            String requestBody = reqJson.toJSONString();

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String responseText = readStream(inputStream);

            if (responseCode < 200 || responseCode >= 300) {
                return buildFailResult("HTTP " + responseCode + ": " + responseText);
            }

            JSONObject result = JSON.parseObject(responseText);
            JSONObject outputObj = result.getJSONObject("output");
            if (outputObj == null) {
                return buildFailResult("响应缺少 output 字段");
            }

            JSONArray choices = outputObj.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return buildFailResult("响应缺少 choices 字段");
            }

            JSONObject firstChoice = choices.getJSONObject(0);
            if (firstChoice == null) {
                return buildFailResult("choices[0] 为空");
            }

            JSONObject message = firstChoice.getJSONObject("message");
            if (message == null) {
                return buildFailResult("响应缺少 message 字段");
            }

            Object contentObj = message.get("content");
            if (contentObj == null) {
                return buildFailResult("模型返回 content 为空");
            }

            JSONObject parsedResult = parseModelContent(contentObj);
            if (parsedResult != null) {
                if (!parsedResult.containsKey("title")) {
                    parsedResult.put("title", "拾获物品");
                }
                if (!parsedResult.containsKey("category")) {
                    parsedResult.put("category", "其他");
                }
                if (!parsedResult.containsKey("description")) {
                    parsedResult.put("description", "AI已识别，但描述缺失");
                }
                return parsedResult;
            }

            JSONObject failResult = buildFailResult("模型返回内容不是标准JSON");
            failResult.put("rawContent", contentObj);
            return failResult;

        } catch (Throwable e) {
            e.printStackTrace();
            return buildFailResult(e.getMessage() == null ? "AI识别异常" : e.getMessage());
        }
    }

    /**
     * 解析模型返回内容，兼容多种格式：
     * 1. 直接返回 JSON 字符串
     * 2. 返回 ```json ... ``` 代码块
     * 3. 返回普通文本中夹带 JSON
     * 4. 返回 {"text":"```json ... ```"} 这种嵌套格式
     */
    private JSONObject parseModelContent(Object contentObj) {
        try {
            if (contentObj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) contentObj;

                if (looksLikeItemInfo(jsonObject)) {
                    return jsonObject;
                }

                String text = jsonObject.getString("text");
                if (text != null && !text.trim().isEmpty()) {
                    JSONObject nested = parseJsonText(text);
                    if (nested != null) {
                        return nested;
                    }
                }

                String content = jsonObject.getString("content");
                if (content != null && !content.trim().isEmpty()) {
                    JSONObject nested = parseJsonText(content);
                    if (nested != null) {
                        return nested;
                    }
                }
            }

            if (contentObj instanceof JSONArray) {
                JSONArray arr = (JSONArray) contentObj;
                for (int i = 0; i < arr.size(); i++) {
                    Object item = arr.get(i);
                    JSONObject parsed = parseModelContent(item);
                    if (parsed != null) {
                        return parsed;
                    }
                }
            }

            if (contentObj instanceof String) {
                return parseJsonText((String) contentObj);
            }

            return parseJsonText(String.valueOf(contentObj));
        } catch (Exception e) {
            return null;
        }
    }

    private JSONObject parseJsonText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String cleaned = cleanMarkdownJson(text);

        try {
            JSONObject obj = JSON.parseObject(cleaned);
            if (obj != null) {
                if (looksLikeItemInfo(obj)) {
                    return obj;
                }

                String nestedText = obj.getString("text");
                if (nestedText != null && !nestedText.trim().isEmpty()) {
                    JSONObject nested = parseJsonText(nestedText);
                    if (nested != null) {
                        return nested;
                    }
                }

                String nestedContent = obj.getString("content");
                if (nestedContent != null && !nestedContent.trim().isEmpty()) {
                    JSONObject nested = parseJsonText(nestedContent);
                    if (nested != null) {
                        return nested;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        String extracted = extractJsonObject(cleaned);
        if (extracted != null) {
            try {
                JSONObject obj = JSON.parseObject(extracted);
                if (obj != null) {
                    if (looksLikeItemInfo(obj)) {
                        return obj;
                    }

                    String nestedText = obj.getString("text");
                    if (nestedText != null && !nestedText.trim().isEmpty()) {
                        JSONObject nested = parseJsonText(nestedText);
                        if (nested != null) {
                            return nested;
                        }
                    }

                    String nestedContent = obj.getString("content");
                    if (nestedContent != null && !nestedContent.trim().isEmpty()) {
                        JSONObject nested = parseJsonText(nestedContent);
                        if (nested != null) {
                            return nested;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private boolean looksLikeItemInfo(JSONObject obj) {
        return obj.containsKey("title") || obj.containsKey("category") || obj.containsKey("description");
    }

    /**
     * 清理 Markdown 代码块包裹
     */
    private String cleanMarkdownJson(String text) {
        String cleaned = text.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        return cleaned;
    }

    /**
     * 读取 resources 下的 api.properties
     */
    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("api.properties")) {
            if (in == null) {
                throw new IOException("找不到配置文件 api.properties");
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        return props;
    }

    private JSONObject buildFailResult(String errorMsg) {
        JSONObject result = new JSONObject();
        result.put("title", "拾获物品");
        result.put("category", "其他");
        result.put("description", "AI识别异常");
        result.put("error", errorMsg);
        return result;
    }

    private String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 尝试从模型返回文本中提取 JSON 对象
     */
    private String extractJsonObject(String text) {
        if (text == null) {
            return null;
        }
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return null;
    }
}