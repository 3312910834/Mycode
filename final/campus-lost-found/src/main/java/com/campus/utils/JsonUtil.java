package com.campus.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON工具类 - 用于API响应
 */
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }
    
    /**
     * 返回成功响应
     */
    public static void success(HttpServletResponse resp, Object data) throws IOException {
        success(resp, "success", data);
    }
    
    public static void success(HttpServletResponse resp, String message, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", message);
        result.put("data", data);
        result.put("timestamp", System.currentTimeMillis());
        
        sendJson(resp, result);
    }
    
    /**
     * 返回错误响应
     */
    public static void error(HttpServletResponse resp, String message) throws IOException {
        error(resp, 500, message);
    }
    
    public static void error(HttpServletResponse resp, int code, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("msg", message);
        result.put("timestamp", System.currentTimeMillis());
        
        sendJson(resp, result);
    }
    
    /**
     * 发送JSON响应
     */
    private static void sendJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        resp.setCharacterEncoding("UTF-8");
        
        PrintWriter out = resp.getWriter();
        out.write(toJson(data));
        out.flush();
    }
}