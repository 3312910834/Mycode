package com.campus.servlet;

import com.campus.entity.Suggestion;
import com.campus.service.SuggestionService;
import com.campus.service.SuggestionService.SuggestionStats;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 建议反馈API接口
 * 访问地址：/suggestionapi/*
 */
@WebServlet("/suggestionapi/*")
public class SuggestionApiServlet extends HttpServlet {
    private SuggestionService suggestionService = new SuggestionService();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath(); // /campus-lost-found
        String servletPath = req.getServletPath(); // /suggestionapi
        
        // 手动解析路径
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        System.out.println("建议API请求路径: " + remainingPath);
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        // 处理根路径
        if (remainingPath.isEmpty() || "/".equals(remainingPath) || "/*".equals(remainingPath)) {
            out.println("{");
            out.println("\"available_endpoints\": [");
            out.println("  {\"method\": \"GET\", \"path\": \"/suggestionapi/list\", \"desc\": \"获取建议列表\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/suggestionapi/my?userId=\", \"desc\": \"获取我的建议\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/suggestionapi/detail?id=\", \"desc\": \"获取建议详情\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/suggestionapi/stats\", \"desc\": \"获取建议统计\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/suggestionapi/byStatus?status=\", \"desc\": \"按状态获取建议\"}");
            out.println("]");
            out.println("}");
            return;
        }
        
        try {
            if (remainingPath.startsWith("/list")) {
                handleList(req, out);
                
            } else if (remainingPath.startsWith("/my")) {
                handleMySuggestions(req, out);
                
            } else if (remainingPath.startsWith("/detail")) {
                handleDetail(req, out);
                
            } else if (remainingPath.startsWith("/stats")) {
                handleStats(req, out);
                
            } else if (remainingPath.startsWith("/byStatus")) {
                handleByStatus(req, out);
                
            } else {
                out.println("{\"error\":\"路径不存在: " + remainingPath + "\"}");
            }
        } catch (Exception e) {
            out.println("{\"error\":\"服务器错误: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        if ("/submit".equals(remainingPath)) {
            handleSubmit(req, out);
        } else if ("/reply".equals(remainingPath)) {
            handleReply(req, out);
        } else if ("/updateStatus".equals(remainingPath)) {
            handleUpdateStatus(req, out);
        } else if ("/delete".equals(remainingPath)) {
            handleDelete(req, out);
        } else {
            out.println("{\"error\":\"POST接口不存在: " + remainingPath + "\"}");
        }
    }
    
    /**
     * 获取建议列表
     */
    private void handleList(HttpServletRequest req, PrintWriter out) {
        String pageStr = req.getParameter("page");
        String sizeStr = req.getParameter("size");
        
        int page = 1;
        int size = 10;
        
        try {
            if (pageStr != null) page = Integer.parseInt(pageStr);
            if (sizeStr != null) size = Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"页码或大小参数格式错误\"}");
            return;
        }
        
        List<Suggestion> suggestions = suggestionService.getAllSuggestions(page, size);
        int total = suggestionService.getSuggestionCount();
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"page\": " + page + ",");
        out.println("  \"size\": " + size + ",");
        out.println("  \"total\": " + total + ",");
        out.println("  \"suggestions\": [");
        
        for (int i = 0; i < suggestions.size(); i++) {
            Suggestion suggestion = suggestions.get(i);
            out.println("    {");
            out.println("      \"id\": " + suggestion.getId() + ",");
            out.println("      \"userId\": " + suggestion.getUserId() + ",");
            out.println("      \"username\": \"" + escapeJson(suggestion.getUsername()) + "\",");
            out.println("      \"content\": \"" + escapeJson(suggestion.getContent()) + "\",");
            out.println("      \"status\": \"" + escapeJson(suggestion.getStatus()) + "\",");
            out.println("      \"hasReply\": " + (suggestion.getReply() != null && !suggestion.getReply().isEmpty()) + ",");
            out.println("      \"createTime\": \"" + (suggestion.getCreateTime() != null ? suggestion.getCreateTime().format(formatter) : "") + "\"");
            out.println("    }" + (i < suggestions.size() - 1 ? "," : ""));
        }
        
        out.println("  ]");
        out.println("}");
        out.println("}");
    }
    
    /**
     * 获取我的建议
     */
    private void handleMySuggestions(HttpServletRequest req, PrintWriter out) {
        String userIdStr = req.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要userId参数\"}");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            List<Suggestion> suggestions = suggestionService.getUserSuggestions(userId);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"userId\": " + userId + ",");
            out.println("  \"count\": " + suggestions.size() + ",");
            out.println("  \"suggestions\": [");
            
            for (int i = 0; i < suggestions.size(); i++) {
                Suggestion suggestion = suggestions.get(i);
                out.println("    {");
                out.println("      \"id\": " + suggestion.getId() + ",");
                out.println("      \"content\": \"" + escapeJson(suggestion.getContent()) + "\",");
                out.println("      \"status\": \"" + escapeJson(suggestion.getStatus()) + "\",");
                out.println("      \"reply\": \"" + escapeJson(suggestion.getReply()) + "\",");
                out.println("      \"createTime\": \"" + (suggestion.getCreateTime() != null ? suggestion.getCreateTime().format(formatter) : "") + "\"");
                out.println("    }" + (i < suggestions.size() - 1 ? "," : ""));
            }
            
            out.println("  ]");
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"userId参数格式错误\"}");
        }
    }
    
    /**
     * 获取建议详情
     */
    private void handleDetail(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int suggestionId = Integer.parseInt(idStr);
            Suggestion suggestion = suggestionService.getSuggestionDetail(suggestionId);
            
            if (suggestion != null) {
                out.println("{");
                out.println("\"code\": 200,");
                out.println("\"message\": \"success\",");
                out.println("\"data\": {");
                out.println("  \"id\": " + suggestion.getId() + ",");
                out.println("  \"userId\": " + suggestion.getUserId() + ",");
                out.println("  \"username\": \"" + escapeJson(suggestion.getUsername()) + "\",");
                out.println("  \"content\": \"" + escapeJson(suggestion.getContent()) + "\",");
                out.println("  \"status\": \"" + escapeJson(suggestion.getStatus()) + "\",");
                out.println("  \"reply\": \"" + escapeJson(suggestion.getReply()) + "\",");
                out.println("  \"createTime\": \"" + (suggestion.getCreateTime() != null ? suggestion.getCreateTime().format(formatter) : "") + "\"");
                out.println("}");
                out.println("}");
            } else {
                out.println("{\"error\":\"建议不存在\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 获取建议统计
     */
    private void handleStats(HttpServletRequest req, PrintWriter out) {
        SuggestionStats stats = suggestionService.getSuggestionStats();
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"total\": " + stats.getTotal() + ",");
        out.println("  \"pending\": " + stats.getPending() + ",");
        out.println("  \"read\": " + stats.getRead() + ",");
        out.println("  \"processed\": " + stats.getProcessed());
        out.println("}");
        out.println("}");
    }
    
    /**
     * 按状态获取建议
     */
    private void handleByStatus(HttpServletRequest req, PrintWriter out) {
        String status = req.getParameter("status");
        
        if (status == null || status.trim().isEmpty()) {
            out.println("{\"error\":\"需要status参数\"}");
            return;
        }
        
        List<Suggestion> suggestions = suggestionService.getSuggestionsByStatus(status);
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"status\": \"" + escapeJson(status) + "\",");
        out.println("  \"count\": " + suggestions.size() + ",");
        out.println("  \"suggestions\": [");
        
        for (int i = 0; i < suggestions.size(); i++) {
            Suggestion suggestion = suggestions.get(i);
            out.println("    {");
            out.println("      \"id\": " + suggestion.getId() + ",");
            out.println("      \"userId\": " + suggestion.getUserId() + ",");
            out.println("      \"username\": \"" + escapeJson(suggestion.getUsername()) + "\",");
            out.println("      \"content\": \"" + escapeJson(suggestion.getContent()) + "\",");
            out.println("      \"createTime\": \"" + (suggestion.getCreateTime() != null ? suggestion.getCreateTime().format(formatter) : "") + "\"");
            out.println("    }" + (i < suggestions.size() - 1 ? "," : ""));
        }
        
        out.println("  ]");
        out.println("}");
        out.println("}");
    }
    
    /**
     * 提交建议
     */
    private void handleSubmit(HttpServletRequest req, PrintWriter out) {
        try {
            String userIdStr = req.getParameter("userId");
            String content = req.getParameter("content");
            
            if (userIdStr == null || content == null) {
                out.println("{\"error\":\"缺少必要参数：userId, content\"}");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            int suggestionId = suggestionService.submitSuggestion(userId, content);
            if (suggestionId > 0) {
                out.println("{\"success\":true,\"suggestionId\":" + suggestionId + "}");
            } else {
                out.println("{\"error\":\"提交失败\"}");
            }
        } catch (Exception e) {
            out.println("{\"error\":\"提交失败: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * 添加回复
     */
    private void handleReply(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        String reply = req.getParameter("reply");
        
        if (idStr == null || reply == null) {
            out.println("{\"error\":\"需要id和reply参数\"}");
            return;
        }
        
        try {
            int suggestionId = Integer.parseInt(idStr);
            boolean success = suggestionService.addReply(suggestionId, reply);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 更新状态
     */
    private void handleUpdateStatus(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        String status = req.getParameter("status");
        
        if (idStr == null || status == null) {
            out.println("{\"error\":\"需要id和status参数\"}");
            return;
        }
        
        try {
            int suggestionId = Integer.parseInt(idStr);
            boolean success = suggestionService.updateSuggestionStatus(suggestionId, status);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"参数格式错误\"}");
        }
    }
    
    /**
     * 删除建议
     */
    private void handleDelete(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int suggestionId = Integer.parseInt(idStr);
            boolean success = suggestionService.deleteSuggestion(suggestionId);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * JSON字符串转义
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
}