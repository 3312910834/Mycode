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
import java.util.List;
import java.util.Properties;

public class LlmMatchApiService {
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public JSONObject match(String lostDesc, List<String> foundList) {
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

            StringBuilder sb = new StringBuilder();
            if (foundList != null) {
                for (int i = 0; i < foundList.size(); i++) {
                    sb.append(i + 1).append("、").append(foundList.get(i)).append("\n");
                }
            }

            String prompt = "你是失物智能匹配助手，只返回JSON：\n"
                    + "失主描述：" + (lostDesc == null ? "" : lostDesc) + "\n"
                    + "招领列表：\n" + sb
                    + "\n返回格式：{\"matchCount\":数量,\"matches\":[{\"index\":序号,\"similarity\":0~1,\"reason\":\"原因\"}]}";

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

            String content = message.getString("content");
            if (content == null || content.trim().isEmpty()) {
                return buildFailResult("模型返回 content 为空");
            }

            try {
                return JSON.parseObject(content);
            } catch (Exception e) {
                JSONObject failResult = buildFailResult("模型返回内容不是标准JSON");
                failResult.put("rawContent", content);
                return failResult;
            }

        } catch (Throwable e) {
            e.printStackTrace();
            return buildFailResult(e.getMessage() == null ? "未知异常" : e.getMessage());
        }
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
        result.put("matchCount", 0);
        result.put("matches", new JSONArray());
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