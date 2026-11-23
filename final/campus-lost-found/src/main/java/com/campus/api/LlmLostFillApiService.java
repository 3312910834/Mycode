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

public class LlmLostFillApiService {
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public JSONObject autoFillByTitle(String title) {
        try {
            Properties props = loadProperties();
            String apiKey = props.getProperty("dashscope.api.key");
            String model = props.getProperty("dashscope.llm.model");

            if (apiKey == null || apiKey.trim().isEmpty()) {
                return buildFailResult("未配置 dashscope.api.key");
            }
            if (model == null || model.trim().isEmpty()) {
                return buildFailResult("未配置 dashscope.llm.model");
            }
            if (title == null || title.trim().isEmpty()) {
                return buildFailResult("物品名称不能为空");
            }

            String prompt = "你是校园失物招领平台的AI助手。"
                    + "用户输入了一个失物名称，请你根据这个名称推测最合适的物品分类，并生成一段适合发布失物信息的详细描述。"
                    + "你必须只返回一个标准JSON对象，不要返回Markdown代码块，不要返回```json，不要返回解释文字。"
                    + "返回格式如下："
                    + "{\"category\":\"分类code或分类名\",\"description\":\"详细描述\"}"
                    + "其中description要自然、具体、适合直接用于失物发布。"
                    + "用户输入的物品名称是：" + title;

            JSONObject reqJson = new JSONObject();
            reqJson.put("model", model);

            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", prompt);

            JSONArray messages = new JSONArray();
            messages.add(messageObj);

            JSONObject inputObj = new JSONObject();
            inputObj.put("messages", messages);
            reqJson.put("input", inputObj);

            JSONObject parametersObj = new JSONObject();
            parametersObj.put("result_format", "json");
            parametersObj.put("temperature", 0.3);
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

            String content = message.getString("content");
            if (content == null || content.trim().isEmpty()) {
                return buildFailResult("模型返回 content 为空");
            }

            try {
                JSONObject parsed = JSON.parseObject(cleanMarkdownJson(content));
                if (!parsed.containsKey("category")) {
                    parsed.put("category", "others");
                }
                if (!parsed.containsKey("description")) {
                    parsed.put("description", "请补充物品颜色、品牌、地点、时间等特征信息。");
                }
                return parsed;
            } catch (Exception e) {
                JSONObject failResult = buildFailResult("模型返回内容不是标准JSON");
                failResult.put("rawContent", content);
                return failResult;
            }

        } catch (Throwable e) {
            e.printStackTrace();
            return buildFailResult(e.getMessage() == null ? "AI补全异常" : e.getMessage());
        }
    }

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
        result.put("category", "others");
        result.put("description", "请补充物品颜色、品牌、地点、时间等特征信息。");
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
}