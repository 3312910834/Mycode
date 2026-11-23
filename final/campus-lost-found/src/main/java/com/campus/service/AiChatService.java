package com.campus.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.campus.api.LlmChatApiService;

public class AiChatService {

    private final LlmChatApiService llmChatApiService = new LlmChatApiService();

    /**
     * 单轮聊天
     */
    public JSONObject chat(String message) {
        String text = safeTrim(message);

        if (text.isEmpty()) {
            return buildFail("message不能为空");
        }

        if (text.length() > 1000) {
            return buildFail("message过长，请控制在1000字以内");
        }

        return llmChatApiService.chat(text);
    }

    /**
     * 多轮聊天（预留，前端以后可传 history）
     */
    public JSONObject chatWithHistory(String message, JSONArray history) {
        String text = safeTrim(message);

        if (text.isEmpty()) {
            return buildFail("message不能为空");
        }

        if (text.length() > 1000) {
            return buildFail("message过长，请控制在1000字以内");
        }

        JSONArray safeHistory = normalizeHistory(history);

        return llmChatApiService.chatWithHistory(safeHistory, text);
    }

    private JSONArray normalizeHistory(JSONArray history) {
        JSONArray result = new JSONArray();
        if (history == null || history.isEmpty()) {
            return result;
        }

        int maxCount = Math.min(history.size(), 10); // 最多带10条上下文
        int start = Math.max(0, history.size() - maxCount);

        for (int i = start; i < history.size(); i++) {
            JSONObject item = history.getJSONObject(i);
            if (item == null) continue;

            String role = safeTrim(item.getString("role"));
            String content = safeTrim(item.getString("content"));

            if (content.isEmpty()) continue;
            if (!"user".equals(role) && !"assistant".equals(role)) continue;

            JSONObject msg = new JSONObject();
            msg.put("role", role);
            msg.put("content", content);
            result.add(msg);
        }

        return result;
    }

    private JSONObject buildFail(String errorMsg) {
        JSONObject result = new JSONObject();
        result.put("success", false);
        result.put("reply", "");
        result.put("error", errorMsg);
        return result;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}