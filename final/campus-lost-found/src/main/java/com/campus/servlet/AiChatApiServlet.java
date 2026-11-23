package com.campus.servlet;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.campus.service.AiChatService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/ai/chat")
public class AiChatApiServlet extends HttpServlet {

    private final AiChatService aiChatService = new AiChatService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        JSONObject response = new JSONObject();

        try {
            String body = readBody(req);
            JSONObject reqJson = parseJson(body);

            String message = reqJson.getString("message");
            JSONArray history = reqJson.getJSONArray("history");

            JSONObject result;
            if (history != null && !history.isEmpty()) {
                result = aiChatService.chatWithHistory(message, history);
            } else {
                result = aiChatService.chat(message);
            }

            if (result.getBooleanValue("success")) {
                response.put("code", 200);
                response.put("message", "success");

                JSONObject data = new JSONObject();
                data.put("reply", result.getString("reply"));
                response.put("data", data);
            } else {
                response.put("code", 500);
                response.put("message", defaultIfBlank(result.getString("error"), "AI处理失败"));
                response.put("data", null);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", e.getMessage() == null ? "服务器异常" : e.getMessage());
            response.put("data", null);
        }

        resp.getOutputStream().write(response.toJSONString().getBytes(StandardCharsets.UTF_8));
        resp.getOutputStream().flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        JSONObject response = new JSONObject();
        response.put("code", 405);
        response.put("message", "请使用POST请求");
        response.put("data", null);

        resp.getOutputStream().write(response.toJSONString().getBytes(StandardCharsets.UTF_8));
        resp.getOutputStream().flush();
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private JSONObject parseJson(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new JSONObject();
        }
        try {
            JSONObject obj = JSON.parseObject(text);
            return obj == null ? new JSONObject() : obj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }
}