package com.campus.servlet;

import com.alibaba.fastjson2.JSONObject;
import com.campus.api.ImageSearchApiService;
import com.campus.api.LlmMatchApiService;
import com.campus.entity.LostItem;
import com.campus.service.FoundItemService;
import com.campus.service.LostItemService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 失物信息API接口
 * 访问地址：/lostapi/*
 */
@WebServlet("/lostapi/*")
public class LostItemApiServlet extends HttpServlet {
    private LostItemService lostItemService = new LostItemService();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    
    private FoundItemService foundItemService;
    private LlmMatchApiService llmMatchApiService;
    private ImageSearchApiService imageSearchApiService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath(); // /campus-lost-found
        String servletPath = req.getServletPath(); // /lostapi
        
        // 手动解析路径
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        System.out.println("失物API请求路径: " + remainingPath);
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        // 处理根路径
        if (remainingPath.isEmpty() || "/".equals(remainingPath) || "/*".equals(remainingPath)) {
            out.println("{");
            out.println("\"available_endpoints\": [");
            out.println("  {\"method\": \"GET\", \"path\": \"/lostapi/list\", \"desc\": \"获取失物列表\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/lostapi/detail?id=\", \"desc\": \"获取失物详情\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/lostapi/my?userId=\", \"desc\": \"获取我的失物\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/lostapi/search?keyword=\", \"desc\": \"搜索失物\"},");
            out.println("  {\"method\": \"POST\", \"path\": \"/lostapi/smartMatch\", \"desc\": \"AI描述找物/智能匹配，参数：lostText\"}");
            out.println("]");
            out.println("}");
            return;
        }
        
        try {
            if (remainingPath.startsWith("/list")) {
                handleList(req, out);
                
            } else if (remainingPath.startsWith("/detail")) {
                handleDetail(req, out);
                
            } else if (remainingPath.startsWith("/my")) {
                handleMyItems(req, out);
                
            } else if (remainingPath.startsWith("/search")) {
                handleSearch(req, out);
                
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
        
        if ("/publish".equals(remainingPath)) {
            handlePublish(req, out);
        } else if ("/update".equals(remainingPath)) {
            handleUpdate(req, out);
        } else if ("/delete".equals(remainingPath)) {
            handleDelete(req, out);
        } else if ("/smartMatch".equals(remainingPath)) {
            String lostText = req.getParameter("lostText");
            if (lostText == null || lostText.trim().isEmpty()) {
                out.println("{\"code\":400,\"message\":\"寻物描述不能为空\"}");
                return;
            }
            try {
                if (foundItemService == null) {
                    foundItemService = new FoundItemService();
                }
                if (llmMatchApiService == null) {
                    llmMatchApiService = new LlmMatchApiService();
                }

                List<String> foundTextList = foundItemService.getAllFoundItemsText();
                JSONObject matchResult = llmMatchApiService.match(lostText, foundTextList);
                out.println("{\"code\":200,\"message\":\"智能匹配完成\",\"data\":"
                        + (matchResult == null ? "null" : matchResult.toJSONString()) + "}");
            } catch (Throwable e) {
                e.printStackTrace();
                out.println("{\"code\":500,\"message\":\"智能匹配服务异常：" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
            }
        } else if ("/imageSearch".equals(remainingPath)) {
            String imageUrl = req.getParameter("imageUrl");
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                out.println("{\"code\":400,\"message\":\"图片地址不能为空\"}");
                return;
            }
            try {
                if (imageSearchApiService == null) {
                    imageSearchApiService = new ImageSearchApiService();
                }
                JSONObject result = imageSearchApiService.search(imageUrl);
                out.println(result.toJSONString());
            } catch (Throwable e) {
                e.printStackTrace();
                out.println("{\"code\":500,\"message\":\"以图搜图服务异常：" + escapeJson(String.valueOf(e.getMessage())) + "\"}");
            }
        } else {
            out.println("{\"error\":\"POST接口不存在: " + remainingPath + "\"}");
        }
    }
    
    /**
     * 获取失物列表
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
        
        List<LostItem> items = lostItemService.getAllLostItems(page, size);
        int total = lostItemService.getLostItemCount();
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"page\": " + page + ",");
        out.println("  \"size\": " + size + ",");
        out.println("  \"total\": " + total + ",");
        out.println("  \"items\": [");
        
        for (int i = 0; i < items.size(); i++) {
            LostItem item = items.get(i);
            out.println("    {");
            out.println("      \"id\": " + item.getId() + ",");
            out.println("      \"title\": \"" + escapeJson(item.getTitle()) + "\",");
            out.println("      \"category\": \"" + escapeJson(item.getCategory()) + "\",");
            out.println("      \"lostLocation\": \"" + escapeJson(item.getLostLocation()) + "\",");
            if (item.getLostTime() != null) {
                out.println("      \"lostTime\": \"" + item.getLostTime().format(formatter) + "\",");
            }
            out.println("      \"status\": \"" + escapeJson(item.getStatus()) + "\",");
            out.println("      \"username\": \"" + escapeJson(item.getUsername()) + "\",");
            out.println("      \"createTime\": \"" + (item.getCreateTime() != null ? item.getCreateTime().format(formatter) : "") + "\"");
            out.println("    }" + (i < items.size() - 1 ? "," : ""));
        }
        
        out.println("  ]");
        out.println("}");
        out.println("}");
    }
    
    /**
     * 获取失物详情
     */
    private void handleDetail(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(idStr);
            LostItem item = lostItemService.getLostItemDetail(itemId);
            
            if (item != null) {
                out.println("{");
                out.println("\"code\": 200,");
                out.println("\"message\": \"success\",");
                out.println("\"data\": {");
                out.println("  \"id\": " + item.getId() + ",");
                out.println("  \"userId\": " + item.getUserId() + ",");
                out.println("  \"title\": \"" + escapeJson(item.getTitle()) + "\",");
                out.println("  \"category\": \"" + escapeJson(item.getCategory()) + "\",");
                out.println("  \"lostLocation\": \"" + escapeJson(item.getLostLocation()) + "\",");
                if (item.getLostTime() != null) {
                    out.println("  \"lostTime\": \"" + item.getLostTime().format(formatter) + "\",");
                }
                out.println("  \"description\": \"" + escapeJson(item.getDescription()) + "\",");
                out.println("  \"contactInfo\": \"" + escapeJson(item.getContactInfo()) + "\",");
                out.println("  \"status\": \"" + escapeJson(item.getStatus()) + "\",");
                out.println("  \"username\": \"" + escapeJson(item.getUsername()) + "\",");
                out.println("  \"userPhone\": \"" + escapeJson(item.getUserPhone()) + "\",");
                out.println("  \"createTime\": \"" + (item.getCreateTime() != null ? item.getCreateTime().format(formatter) : "") + "\"");
                out.println("}");
                out.println("}");
            } else {
                out.println("{\"error\":\"失物信息不存在或已被删除\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 获取我的失物
     */
    private void handleMyItems(HttpServletRequest req, PrintWriter out) {
        String userIdStr = req.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要userId参数\"}");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            List<LostItem> items = lostItemService.getUserLostItems(userId);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"userId\": " + userId + ",");
            out.println("  \"count\": " + items.size() + ",");
            out.println("  \"items\": [");
            
            for (int i = 0; i < items.size(); i++) {
                LostItem item = items.get(i);
                out.println("    {");
                out.println("      \"id\": " + item.getId() + ",");
                out.println("      \"title\": \"" + escapeJson(item.getTitle()) + "\",");
                out.println("      \"lostLocation\": \"" + escapeJson(item.getLostLocation()) + "\",");
                out.println("      \"status\": \"" + escapeJson(item.getStatus()) + "\",");
                out.println("      \"createTime\": \"" + (item.getCreateTime() != null ? item.getCreateTime().format(formatter) : "") + "\"");
                out.println("    }" + (i < items.size() - 1 ? "," : ""));
            }
            
            out.println("  ]");
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"userId参数格式错误\"}");
        }
    }
    
    /**
     * 搜索失物
     */
    private void handleSearch(HttpServletRequest req, PrintWriter out) {
        String keyword = req.getParameter("keyword");
        
        List<LostItem> items = lostItemService.searchLostItems(keyword);
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"keyword\": \"" + (keyword != null ? escapeJson(keyword) : "") + "\",");
        out.println("  \"count\": " + items.size() + ",");
        out.println("  \"items\": [");
        
        for (int i = 0; i < items.size(); i++) {
            LostItem item = items.get(i);
            out.println("    {");
            out.println("      \"id\": " + item.getId() + ",");
            out.println("      \"title\": \"" + escapeJson(item.getTitle()) + "\",");
            out.println("      \"category\": \"" + escapeJson(item.getCategory()) + "\",");
            out.println("      \"lostLocation\": \"" + escapeJson(item.getLostLocation()) + "\",");
            out.println("      \"status\": \"" + escapeJson(item.getStatus()) + "\",");
            out.println("      \"username\": \"" + escapeJson(item.getUsername()) + "\",");
            out.println("      \"createTime\": \"" + (item.getCreateTime() != null ? item.getCreateTime().format(formatter) : "") + "\"");
            out.println("    }" + (i < items.size() - 1 ? "," : ""));
        }
        
        out.println("  ]");
        out.println("}");
        out.println("}");
    }
    
    /**
     * 发布失物
     */
    private void handlePublish(HttpServletRequest req, PrintWriter out) {
        try {
            String userIdStr = req.getParameter("userId");
            String title = req.getParameter("title");
            String lostLocation = req.getParameter("lostLocation");
            
            if (userIdStr == null || title == null || lostLocation == null) {
                out.println("{\"error\":\"缺少必要参数：userId, title, lostLocation\"}");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            LostItem item = new LostItem();
            item.setUserId(userId);
            item.setTitle(title);
            item.setLostLocation(lostLocation);
            item.setCategory(req.getParameter("category"));
            item.setDescription(req.getParameter("description"));
            item.setContactInfo(req.getParameter("contactInfo"));
            
            String lostTimeStr = req.getParameter("lostTime");
            if (lostTimeStr != null && !lostTimeStr.isEmpty()) {
                item.setLostTime(LocalDateTime.parse(lostTimeStr.replace(" ", "T")));
            }
            
            int itemId = lostItemService.publishLostItem(item);
            if (itemId > 0) {
                out.println("{\"success\":true,\"itemId\":" + itemId + "}");
            } else {
                out.println("{\"error\":\"发布失败\"}");
            }
        } catch (Exception e) {
            out.println("{\"error\":\"发布失败: " + e.getMessage() + "\"}");
        }
    }
    

    /**
     * 更新失物信息
     */
    private void handleUpdate(HttpServletRequest req, PrintWriter out) {
        try {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                out.println("{\"error\":\"需要id参数\"}");
                return;
            }
            int itemId = Integer.parseInt(idStr);
            LostItem item = lostItemService.getLostItemDetail(itemId);
            if (item == null) {
                out.println("{\"error\":\"失物信息不存在或已被删除\"}");
                return;
            }
            // 权限校验：只能本人修改
            HttpSession session = req.getSession(false);
            Object userObj = (session != null) ? session.getAttribute("currentUser") : null;
            int currentUserId = -1;
            String currentUserRole = null;
            if (userObj != null && userObj instanceof com.campus.entity.User) {
                com.campus.entity.User user = (com.campus.entity.User) userObj;
                currentUserId = user.getId();
                currentUserRole = user.getRole();
            }
            if (currentUserId != item.getUserId()) {
                out.println("{\"code\":403,\"message\":\"无权限修改他人失物信息\"}");
                return;
            }
            // ...existing code...
            // 3. 更新字段（只更新前端传入的字段）
            String title = req.getParameter("title");
            if (title != null && !title.trim().isEmpty()) {
                item.setTitle(title.trim());
            }
            
            String category = req.getParameter("category");
            if (category != null && !category.trim().isEmpty()) {
                item.setCategory(category.trim());
            }
            
            String lostLocation = req.getParameter("lostLocation");
            if (lostLocation != null && !lostLocation.trim().isEmpty()) {
                item.setLostLocation(lostLocation.trim());
            }
            
            String lostTimeStr = req.getParameter("lostTime");
            if (lostTimeStr != null && !lostTimeStr.trim().isEmpty()) {
                try {
                    // 解析时间格式：yyyy-MM-dd HH:mm:ss
                    LocalDateTime lostTime = LocalDateTime.parse(
                        lostTimeStr.trim().replace(" ", "T")
                    );
                    item.setLostTime(lostTime);
                } catch (Exception e) {
                    // 时间格式错误，忽略或记录日志
                    System.out.println("时间格式错误: " + lostTimeStr);
                }
            }
            
            String description = req.getParameter("description");
            if (description != null) {
                item.setDescription(description);
            }
            
            String contactInfo = req.getParameter("contactInfo");
            if (contactInfo != null) {
                item.setContactInfo(contactInfo);
            }
            
            String status = req.getParameter("status");
            if (status != null && !status.trim().isEmpty()) {
                item.setStatus(status.trim());
            }
            
            String userIdStr = req.getParameter("userId");
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                try {
                    item.setUserId(Integer.parseInt(userIdStr.trim()));
                } catch (NumberFormatException e) {
                    // 忽略格式错误的userId
                }
            }
            
            // 4. 调用Service层更新
            boolean success = lostItemService.updateLostItem(item);
            
            if (success) {
                out.println("{\"success\":true,\"message\":\"更新成功\",\"id\":" + itemId + "}");
            } else {
                out.println("{\"error\":\"更新失败，可能是数据未改变或数据库错误\"}");
            }
            
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"参数格式错误\"}");
        } catch (Exception e) {
            out.println("{\"error\":\"更新失败: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * 删除失物
     */
    private void handleDelete(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        try {
            int itemId = Integer.parseInt(idStr);
            LostItem item = lostItemService.getLostItemDetail(itemId);
            if (item == null) {
                out.println("{\"error\":\"失物信息不存在或已被删除\"}");
                return;
            }
            // 权限校验：只能本人删除
            HttpSession session = req.getSession(false);
            Object userObj = (session != null) ? session.getAttribute("currentUser") : null;
            int currentUserId = -1;
            if (userObj != null && userObj instanceof com.campus.entity.User) {
                com.campus.entity.User user = (com.campus.entity.User) userObj;
                currentUserId = user.getId();
            }
            if (currentUserId != item.getUserId()) {
                out.println("{\"code\":403,\"message\":\"无权限删除他人失物信息\"}");
                return;
            }
            boolean success = lostItemService.deleteLostItem(itemId);
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