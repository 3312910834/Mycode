package com.campus.servlet;

import com.campus.entity.FoundItem;
import com.campus.service.FoundItemService;
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
 * 招领信息API接口
 * 访问地址：/foundapi/*
 */
@WebServlet("/foundapi/*")
public class FoundItemApiServlet extends HttpServlet {
    private FoundItemService foundItemService = new FoundItemService();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath(); // /campus-lost-found
        String servletPath = req.getServletPath(); // /foundapi
        
        // 手动解析路径
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        System.out.println("招领API请求路径: " + remainingPath);
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        // 处理根路径
        if (remainingPath.isEmpty() || "/".equals(remainingPath) || "/*".equals(remainingPath)) {
            out.println("{");
            out.println("\"available_endpoints\": [");
            out.println("  {\"method\": \"GET\", \"path\": \"/foundapi/list\", \"desc\": \"获取招领列表\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/foundapi/detail?id=\", \"desc\": \"获取招领详情\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/foundapi/my?userId=\", \"desc\": \"获取我的招领\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/foundapi/search?keyword=\", \"desc\": \"搜索招领\"}");
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
        } else {
            out.println("{\"error\":\"POST接口不存在: " + remainingPath + "\"}");
        }
    }
    
    /**
     * 获取招领列表
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
        
        List<FoundItem> items = foundItemService.getAllFoundItems(page, size);
        int total = foundItemService.getFoundItemCount();
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"page\": " + page + ",");
        out.println("  \"size\": " + size + ",");
        out.println("  \"total\": " + total + ",");
        out.println("  \"items\": [");
        
        for (int i = 0; i < items.size(); i++) {
            FoundItem item = items.get(i);
            out.println("    {");
            out.println("      \"id\": " + item.getId() + ",");
            out.println("      \"title\": \"" + escapeJson(item.getTitle()) + "\",");
            out.println("      \"category\": \"" + escapeJson(item.getCategory()) + "\",");
            out.println("      \"foundLocation\": \"" + escapeJson(item.getFoundLocation()) + "\",");
            out.println("      \"storageLocation\": \"" + escapeJson(item.getStorageLocation()) + "\",");
            if (item.getFoundTime() != null) {
                out.println("      \"foundTime\": \"" + item.getFoundTime().format(formatter) + "\",");
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
     * 获取招领详情
     */
    private void handleDetail(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(idStr);
            FoundItem item = foundItemService.getFoundItemDetail(itemId);
            
            if (item != null) {
                out.println("{");
                out.println("\"code\": 200,");
                out.println("\"message\": \"success\",");
                out.println("\"data\": {");
                out.println("  \"id\": " + item.getId() + ",");
                out.println("  \"userId\": " + item.getUserId() + ",");
                out.println("  \"title\": \"" + escapeJson(item.getTitle()) + "\",");
                out.println("  \"category\": \"" + escapeJson(item.getCategory()) + "\",");
                out.println("  \"foundLocation\": \"" + escapeJson(item.getFoundLocation()) + "\",");
                if (item.getFoundTime() != null) {
                    out.println("  \"foundTime\": \"" + item.getFoundTime().format(formatter) + "\",");
                }
                out.println("  \"storageLocation\": \"" + escapeJson(item.getStorageLocation()) + "\",");
                out.println("  \"description\": \"" + escapeJson(item.getDescription()) + "\",");
                out.println("  \"contactInfo\": \"" + escapeJson(item.getContactInfo()) + "\",");
                out.println("  \"status\": \"" + escapeJson(item.getStatus()) + "\",");
                out.println("  \"username\": \"" + escapeJson(item.getUsername()) + "\",");
                out.println("  \"userPhone\": \"" + escapeJson(item.getUserPhone()) + "\",");
                out.println("  \"createTime\": \"" + (item.getCreateTime() != null ? item.getCreateTime().format(formatter) : "") + "\"");
                out.println("}");
                out.println("}");
            } else {
                out.println("{\"error\":\"招领信息不存在或已被删除\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 获取我的招领
     */
    private void handleMyItems(HttpServletRequest req, PrintWriter out) {
        String userIdStr = req.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要userId参数\"}");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            List<FoundItem> items = foundItemService.getUserFoundItems(userId);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"userId\": " + userId + ",");
            out.println("  \"count\": " + items.size() + ",");
            out.println("  \"items\": [");
            
            for (int i = 0; i < items.size(); i++) {
                FoundItem item = items.get(i);
                out.println("    {");
                out.println("      \"id\": " + item.getId() + ",");
                out.println("      \"title\": \"" + escapeJson(item.getTitle()) + "\",");
                out.println("      \"foundLocation\": \"" + escapeJson(item.getFoundLocation()) + "\",");
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
     * 搜索招领
     */
    private void handleSearch(HttpServletRequest req, PrintWriter out) {
        String keyword = req.getParameter("keyword");
        
        List<FoundItem> items = foundItemService.searchFoundItems(keyword);
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"keyword\": \"" + (keyword != null ? escapeJson(keyword) : "") + "\",");
        out.println("  \"count\": " + items.size() + ",");
        out.println("  \"items\": [");
        
        for (int i = 0; i < items.size(); i++) {
            FoundItem item = items.get(i);
            out.println("    {");
            out.println("      \"id\": " + item.getId() + ",");
            out.println("      \"title\": \"" + escapeJson(item.getTitle()) + "\",");
            out.println("      \"category\": \"" + escapeJson(item.getCategory()) + "\",");
            out.println("      \"foundLocation\": \"" + escapeJson(item.getFoundLocation()) + "\",");
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
     * 发布招领
     */
    private void handlePublish(HttpServletRequest req, PrintWriter out) {
        try {
            String userIdStr = req.getParameter("userId");
            String title = req.getParameter("title");
            String foundLocation = req.getParameter("foundLocation");
            
            if (userIdStr == null || title == null || foundLocation == null) {
                out.println("{\"error\":\"缺少必要参数：userId, title, foundLocation\"}");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            FoundItem item = new FoundItem();
            item.setUserId(userId);
            item.setTitle(title);
            item.setFoundLocation(foundLocation);
            item.setCategory(req.getParameter("category"));
            item.setDescription(req.getParameter("description"));
            item.setContactInfo(req.getParameter("contactInfo"));
            item.setStorageLocation(req.getParameter("storageLocation"));
            
            String foundTimeStr = req.getParameter("foundTime");
            if (foundTimeStr != null && !foundTimeStr.isEmpty()) {
                item.setFoundTime(LocalDateTime.parse(foundTimeStr.replace(" ", "T")));
            }
            
            int itemId = foundItemService.publishFoundItem(item);
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
     * 更新招领信息
     */
    private void handleUpdate(HttpServletRequest req, PrintWriter out) {
        try {
            String idStr = req.getParameter("id");
            
            if (idStr == null || idStr.trim().isEmpty()) {
                out.println("{\"error\":\"需要id参数\"}");
                return;
            }
            
            // 1. 解析ID
            int itemId = Integer.parseInt(idStr);
            
            // 2. 先获取原有的招领信息
            FoundItem item = foundItemService.getFoundItemDetail(itemId);
            if (item == null) {
                out.println("{\"error\":\"招领信息不存在或已被删除\"}");
                return;
            }
            
            // 3. 更新字段（只更新前端传入的字段）
            String title = req.getParameter("title");
            if (title != null && !title.trim().isEmpty()) {
                item.setTitle(title.trim());
            }
            
            String category = req.getParameter("category");
            if (category != null && !category.trim().isEmpty()) {
                item.setCategory(category.trim());
            }
            
            String foundLocation = req.getParameter("foundLocation");
            if (foundLocation != null && !foundLocation.trim().isEmpty()) {
                item.setFoundLocation(foundLocation.trim());
            }
            
            String storageLocation = req.getParameter("storageLocation");
            if (storageLocation != null) {
                item.setStorageLocation(storageLocation);
            }
            
            String foundTimeStr = req.getParameter("foundTime");
            if (foundTimeStr != null && !foundTimeStr.trim().isEmpty()) {
                try {
                    // 解析时间格式：yyyy-MM-dd HH:mm:ss
                    LocalDateTime foundTime = LocalDateTime.parse(
                        foundTimeStr.trim().replace(" ", "T")
                    );
                    item.setFoundTime(foundTime);
                } catch (Exception e) {
                    // 时间格式错误，忽略或记录日志
                    System.out.println("时间格式错误: " + foundTimeStr);
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
            boolean success = foundItemService.updateFoundItem(item);
            
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
     * 删除招领
     */
    private void handleDelete(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(idStr);
            boolean success = foundItemService.deleteFoundItem(itemId);
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