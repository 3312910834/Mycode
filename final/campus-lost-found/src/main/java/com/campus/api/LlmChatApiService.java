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

public class LlmChatApiService {

    /**
     * 与你现有 LlmMatchApiService 一致，继续使用这个接口地址
     */
    private static final String API_URL =
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    /**
     * 单轮聊天
     */
    public JSONObject chat(String userMessage) {
        try {
            Properties props = loadProperties();
            String apiKey = trimToEmpty(props.getProperty("dashscope.api.key"));
            String model = trimToEmpty(props.getProperty("dashscope.llm.model"));

            if (apiKey.isEmpty()) {
                return buildFailResult("未配置 dashscope.api.key");
            }
            if (model.isEmpty()) {
                return buildFailResult("未配置 dashscope.llm.model");
            }

            String systemPrompt =
                    "你是校园失物招领平台的AI助手。"
                            + "请使用简洁、友好、准确的中文回答。"
                            + "你的职责包括："
                            + "1. 介绍平台功能；"
                            + "2. 指导用户如何发布失物信息；"
                            + "3. 指导用户如何发布招领信息；"
                            + "4. 告诉用户如何查看招领公示；"
                            + "5. 当用户描述丢失物品时，提醒他可以使用智能匹配功能。"
                            + "回答要求："
                            + "（1）不要编造不存在的后台功能；"
                            + "（2）优先围绕校园失物招领场景；"
                            + "（3）回答尽量分点、清晰、简洁；"
                            + "（4）如果用户问题很模糊，先给出通用建议。";

            String userContent = trimToEmpty(userMessage);
            if (userContent.isEmpty()) {
                return buildFailResult("用户消息不能为空");
            }

            JSONObject reqJson = new JSONObject();
            reqJson.put("model", model);

            JSONArray messages = new JSONArray();

            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userContent);
            messages.add(userMsg);

            JSONObject inputObj = new JSONObject();
            inputObj.put("messages", messages);
            reqJson.put("input", inputObj);

            JSONObject parametersObj = new JSONObject();
            parametersObj.put("result_format", "message");
            parametersObj.put("temperature", 0.7);
            reqJson.put("parameters", parametersObj);

            String requestBody = reqJson.toJSONString();

            String responseText = doPost(apiKey, requestBody);
            JSONObject result = JSON.parseObject(responseText);

            return parseChatResponse(result);

        } catch (Throwable e) {
            e.printStackTrace();
            return buildFailResult(e.getMessage() == null ? "调用AI失败" : e.getMessage());
        }
    }

    /**
     * 可选：支持带历史上下文聊天
     * 前端如果以后要做多轮上下文，可以直接用这个方法
     */
    public JSONObject chatWithHistory(JSONArray history, String userMessage) {
        try {
            Properties props = loadProperties();
            String apiKey = trimToEmpty(props.getProperty("dashscope.api.key"));
            String model = trimToEmpty(props.getProperty("dashscope.llm.model"));

            if (apiKey.isEmpty()) {
                return buildFailResult("未配置 dashscope.api.key");
            }
            if (model.isEmpty()) {
                return buildFailResult("未配置 dashscope.llm.model");
            }

            String systemPrompt =
                    "你是校园失物招领平台的AI助手。"
                            + "请使用简洁、友好、准确的中文回答。"
                            + "你的职责包括："
                            + "1. 介绍平台功能；"
                            + "2. 指导用户如何发布失物信息；"
                            + "3. 指导用户如何发布招领信息；"
                            + "4. 告诉用户如何查看招领公示；"
                            + "5. 当用户描述丢失物品时，提醒他可以使用智能匹配功能。"
                            + "回答要求："
                            + "（1）不要编造不存在的后台功能；"
                            + "（2）优先围绕校园失物招领场景；"
                            + "（3）回答尽量分点、清晰、简洁；"
                            + "（4）如果用户问题很模糊，先给出通用建议。";

            String userContent = trimToEmpty(userMessage);
            if (userContent.isEmpty()) {
                return buildFailResult("用户消息不能为空");
            }

            JSONObject reqJson = new JSONObject();
            reqJson.put("model", model);

            JSONArray messages = new JSONArray();

            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            if (history != null && !history.isEmpty()) {
                for (int i = 0; i < history.size(); i++) {
                    JSONObject item = history.getJSONObject(i);
                    if (item == null) continue;

                    String role = trimToEmpty(item.getString("role"));
                    String content = trimToEmpty(item.getString("content"));

                    if (content.isEmpty()) continue;
                    if (!"user".equals(role) && !"assistant".equals(role)) continue;

                    JSONObject msg = new JSONObject();
                    msg.put("role", role);
                    msg.put("content", content);
                    messages.add(msg);
                }
            }

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userContent);
            messages.add(userMsg);

            JSONObject inputObj = new JSONObject();
            inputObj.put("messages", messages);
            reqJson.put("input", inputObj);

            JSONObject parametersObj = new JSONObject();
            parametersObj.put("result_format", "message");
            parametersObj.put("temperature", 0.7);
            reqJson.put("parameters", parametersObj);

            String requestBody = reqJson.toJSONString();

            String responseText = doPost(apiKey, requestBody);
            JSONObject result = JSON.parseObject(responseText);

            return parseChatResponse(result);

        } catch (Throwable e) {
            e.printStackTrace();
            return buildFailResult(e.getMessage() == null ? "调用AI失败" : e.getMessage());
        }
    }

    private String doPost(String apiKey, String requestBody) throws IOException {
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
            throw new IOException("HTTP " + responseCode + ": " + responseText);
        }

        return responseText;
    }

    private JSONObject parseChatResponse(JSONObject result) {
        if (result == null) {
            return buildFailResult("AI响应为空");
        }

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

        String content = trimToEmpty(message.getString("content"));
        if (content.isEmpty()) {
            return buildFailResult("模型返回 content 为空");
        }

        JSONObject success = new JSONObject();
        success.put("success", true);
        success.put("reply", content);
        return success;
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
        result.put("success", false);
        result.put("reply", "");
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

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}