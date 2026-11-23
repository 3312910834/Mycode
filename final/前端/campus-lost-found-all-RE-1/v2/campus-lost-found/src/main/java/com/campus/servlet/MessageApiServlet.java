package com.campus.servlet;

import com.campus.entity.ContactMessage;
import com.campus.service.ContactMessageService;
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
 * 联系消息API接口
 * 访问地址：/messageapi/*
 */
@WebServlet("/messageapi/*")
public class MessageApiServlet extends HttpServlet {
    private ContactMessageService messageService = new ContactMessageService();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath(); // /campus-lost-found
        String servletPath = req.getServletPath(); // /messageapi
        
        // 手动解析路径
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        System.out.println("消息API请求路径: " + remainingPath);
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        // 处理根路径
        if (remainingPath.isEmpty() || "/".equals(remainingPath) || "/*".equals(remainingPath)) {
            out.println("{");
            out.println("\"available_endpoints\": [");
            out.println("  {\"method\": \"GET\", \"path\": \"/messageapi/received?userId=\", \"desc\": \"获取收到的消息\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/messageapi/sent?userId=\", \"desc\": \"获取发送的消息\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/messageapi/conversation?user1Id=&user2Id=\", \"desc\": \"获取对话\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/messageapi/item?itemId=&itemType=\", \"desc\": \"获取物品相关消息\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/messageapi/unread?userId=\", \"desc\": \"获取未读消息数\"}");
            out.println("]");
            out.println("}");
            return;
        }
        
        try {
            if (remainingPath.startsWith("/received")) {
                handleReceived(req, out);
                
            } else if (remainingPath.startsWith("/sent")) {
                handleSent(req, out);
                
            } else if (remainingPath.startsWith("/conversation")) {
                handleConversation(req, out);
                
            } else if (remainingPath.startsWith("/item")) {
                handleItemMessages(req, out);
                
            } else if (remainingPath.startsWith("/unread")) {
                handleUnreadCount(req, out);
                
            } else if (remainingPath.startsWith("/detail")) {
                handleDetail(req, out);
                
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
        
        if ("/send".equals(remainingPath)) {
            handleSend(req, out);
        } else if ("/read".equals(remainingPath)) {
            handleMarkAsRead(req, out);
        } else if ("/delete".equals(remainingPath)) {
            handleDelete(req, out);
        } else {
            out.println("{\"error\":\"POST接口不存在: " + remainingPath + "\"}");
        }
    }
    
    /**
     * 获取收到的消息
     */
    private void handleReceived(HttpServletRequest req, PrintWriter out) {
        String userIdStr = req.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要userId参数\"}");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            List<ContactMessage> messages = messageService.getReceivedMessages(userId);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"userId\": " + userId + ",");
            out.println("  \"count\": " + messages.size() + ",");
            out.println("  \"messages\": [");
            
            for (int i = 0; i < messages.size(); i++) {
                ContactMessage msg = messages.get(i);
                out.println("    {");
                out.println("      \"id\": " + msg.getId() + ",");
                out.println("      \"fromUserId\": " + msg.getFromUserId() + ",");
                out.println("      \"fromUserName\": \"" + escapeJson(msg.getFromUserName()) + "\",");
                out.println("      \"itemId\": " + msg.getItemId() + ",");
                out.println("      \"itemType\": \"" + escapeJson(msg.getItemType()) + "\",");
                out.println("      \"itemTitle\": \"" + escapeJson(msg.getItemTitle()) + "\",");
                out.println("      \"content\": \"" + escapeJson(msg.getContent()) + "\",");
                out.println("      \"status\": \"" + escapeJson(msg.getStatus()) + "\",");
                out.println("      \"createTime\": \"" + (msg.getCreateTime() != null ? msg.getCreateTime().format(formatter) : "") + "\"");
                out.println("    }" + (i < messages.size() - 1 ? "," : ""));
            }
            
            out.println("  ]");
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"userId参数格式错误\"}");
        }
    }
    
    /**
     * 获取发送的消息
     */
    private void handleSent(HttpServletRequest req, PrintWriter out) {
        String userIdStr = req.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要userId参数\"}");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            List<ContactMessage> messages = messageService.getSentMessages(userId);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"userId\": " + userId + ",");
            out.println("  \"count\": " + messages.size() + ",");
            out.println("  \"messages\": [");
            
            for (int i = 0; i < messages.size(); i++) {
                ContactMessage msg = messages.get(i);
                out.println("    {");
                out.println("      \"id\": " + msg.getId() + ",");
                out.println("      \"toUserId\": " + msg.getToUserId() + ",");
                out.println("      \"toUserName\": \"" + escapeJson(msg.getToUserName()) + "\",");
                out.println("      \"itemId\": " + msg.getItemId() + ",");
                out.println("      \"itemType\": \"" + escapeJson(msg.getItemType()) + "\",");
                out.println("      \"itemTitle\": \"" + escapeJson(msg.getItemTitle()) + "\",");
                out.println("      \"content\": \"" + escapeJson(msg.getContent()) + "\",");
                out.println("      \"status\": \"" + escapeJson(msg.getStatus()) + "\",");
                out.println("      \"createTime\": \"" + (msg.getCreateTime() != null ? msg.getCreateTime().format(formatter) : "") + "\"");
                out.println("    }" + (i < messages.size() - 1 ? "," : ""));
            }
            
            out.println("  ]");
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"userId参数格式错误\"}");
        }
    }
    
    /**
     * 获取对话
     */
    private void handleConversation(HttpServletRequest req, PrintWriter out) {
        String user1IdStr = req.getParameter("user1Id");
        String user2IdStr = req.getParameter("user2Id");
        
        if (user1IdStr == null || user2IdStr == null) {
            out.println("{\"error\":\"需要user1Id和user2Id参数\"}");
            return;
        }
        
        try {
            int user1Id = Integer.parseInt(user1IdStr);
            int user2Id = Integer.parseInt(user2IdStr);
            List<ContactMessage> messages = messageService.getConversation(user1Id, user2Id);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"user1Id\": " + user1Id + ",");
            out.println("  \"user2Id\": " + user2Id + ",");
            out.println("  \"count\": " + messages.size() + ",");
            out.println("  \"messages\": [");
            
            for (int i = 0; i < messages.size(); i++) {
                ContactMessage msg = messages.get(i);
                out.println("    {");
                out.println("      \"id\": " + msg.getId() + ",");
                out.println("      \"fromUserId\": " + msg.getFromUserId() + ",");
                out.println("      \"fromUserName\": \"" + escapeJson(msg.getFromUserName()) + "\",");
                out.println("      \"toUserId\": " + msg.getToUserId() + ",");
                out.println("      \"toUserName\": \"" + escapeJson(msg.getToUserName()) + "\",");
                out.println("      \"content\": \"" + escapeJson(msg.getContent()) + "\",");
                out.println("      \"createTime\": \"" + (msg.getCreateTime() != null ? msg.getCreateTime().format(formatter) : "") + "\"");
                out.println("    }" + (i < messages.size() - 1 ? "," : ""));
            }
            
            out.println("  ]");
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"参数格式错误\"}");
        }
    }
    
    /**
     * 获取物品相关消息
     */
    private void handleItemMessages(HttpServletRequest req, PrintWriter out) {
        String itemIdStr = req.getParameter("itemId");
        String itemType = req.getParameter("itemType");
        
        if (itemIdStr == null || itemType == null) {
            out.println("{\"error\":\"需要itemId和itemType参数\"}");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(itemIdStr);
            List<ContactMessage> messages = messageService.getItemMessages(itemId, itemType);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"itemId\": " + itemId + ",");
            out.println("  \"itemType\": \"" + escapeJson(itemType) + "\",");
            out.println("  \"count\": " + messages.size() + ",");
            out.println("  \"messages\": [");
            
            for (int i = 0; i < messages.size(); i++) {
                ContactMessage msg = messages.get(i);
                out.println("    {");
                out.println("      \"id\": " + msg.getId() + ",");
                out.println("      \"fromUserId\": " + msg.getFromUserId() + ",");
                out.println("      \"fromUserName\": \"" + escapeJson(msg.getFromUserName()) + "\",");
                out.println("      \"toUserId\": " + msg.getToUserId() + ",");
                out.println("      \"toUserName\": \"" + escapeJson(msg.getToUserName()) + "\",");
                out.println("      \"content\": \"" + escapeJson(msg.getContent()) + "\",");
                out.println("      \"createTime\": \"" + (msg.getCreateTime() != null ? msg.getCreateTime().format(formatter) : "") + "\"");
                out.println("    }" + (i < messages.size() - 1 ? "," : ""));
            }
            
            out.println("  ]");
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"itemId参数格式错误\"}");
        }
    }
    
    /**
     * 获取未读消息数
     */
    private void handleUnreadCount(HttpServletRequest req, PrintWriter out) {
        String userIdStr = req.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要userId参数\"}");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            int unreadCount = messageService.getUnreadCount(userId);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"userId\": " + userId + ",");
            out.println("  \"unreadCount\": " + unreadCount);
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"userId参数格式错误\"}");
        }
    }
    
    /**
     * 获取消息详情
     */
    private void handleDetail(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int messageId = Integer.parseInt(idStr);
            ContactMessage message = messageService.getMessageDetail(messageId);
            
            if (message != null) {
                out.println("{");
                out.println("\"code\": 200,");
                out.println("\"message\": \"success\",");
                out.println("\"data\": {");
                out.println("  \"id\": " + message.getId() + ",");
                out.println("  \"fromUserId\": " + message.getFromUserId() + ",");
                out.println("  \"fromUserName\": \"" + escapeJson(message.getFromUserName()) + "\",");
                out.println("  \"toUserId\": " + message.getToUserId() + ",");
                out.println("  \"toUserName\": \"" + escapeJson(message.getToUserName()) + "\",");
                out.println("  \"itemId\": " + message.getItemId() + ",");
                out.println("  \"itemType\": \"" + escapeJson(message.getItemType()) + "\",");
                out.println("  \"itemTitle\": \"" + escapeJson(message.getItemTitle()) + "\",");
                out.println("  \"content\": \"" + escapeJson(message.getContent()) + "\",");
                out.println("  \"status\": \"" + escapeJson(message.getStatus()) + "\",");
                out.println("  \"createTime\": \"" + (message.getCreateTime() != null ? message.getCreateTime().format(formatter) : "") + "\"");
                out.println("}");
                out.println("}");
            } else {
                out.println("{\"error\":\"消息不存在\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 发送消息
     */
    private void handleSend(HttpServletRequest req, PrintWriter out) {
        try {
            String fromUserIdStr = req.getParameter("fromUserId");
            String toUserIdStr = req.getParameter("toUserId");
            String itemIdStr = req.getParameter("itemId");
            String itemType = req.getParameter("itemType");
            String content = req.getParameter("content");
            
            if (fromUserIdStr == null || toUserIdStr == null || itemIdStr == null || itemType == null || content == null) {
                out.println("{\"error\":\"缺少必要参数：fromUserId, toUserId, itemId, itemType, content\"}");
                return;
            }
            
            int fromUserId = Integer.parseInt(fromUserIdStr);
            int toUserId = Integer.parseInt(toUserIdStr);
            int itemId = Integer.parseInt(itemIdStr);
            
            int messageId = messageService.sendContactMessage(fromUserId, toUserId, itemId, itemType, content);
            if (messageId > 0) {
                out.println("{\"success\":true,\"messageId\":" + messageId + "}");
            } else {
                out.println("{\"error\":\"发送失败\"}");
            }
        } catch (Exception e) {
            out.println("{\"error\":\"发送失败: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * 标记为已读
     */
    private void handleMarkAsRead(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int messageId = Integer.parseInt(idStr);
            boolean success = messageService.markAsRead(messageId);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 删除消息
     */
    private void handleDelete(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int messageId = Integer.parseInt(idStr);
            boolean success = messageService.deleteMessage(messageId);
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