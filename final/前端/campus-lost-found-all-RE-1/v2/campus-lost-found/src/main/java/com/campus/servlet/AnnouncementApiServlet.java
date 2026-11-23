package com.campus.servlet;

import com.campus.entity.Announcement;
import com.campus.service.AnnouncementService;
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
 * 公告API接口
 * 访问地址：/announcementapi/*
 */
@WebServlet("/announcementapi/*")
//使用WebServlet注解定义Servlet的访问路径
public class AnnouncementApiServlet extends HttpServlet {
//	创建公告服务层实例
	private AnnouncementService announcementService = new AnnouncementService();
//	定义日期时间格式化器
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
//    	获取请求的完整URI
        String requestURI = req.getRequestURI();
//      获取应用上下文路径
        String contextPath = req.getContextPath(); // /campus-lost-found
//      获取Servlet映射路径
        String servletPath = req.getServletPath(); // /announcementapi
        
        // 手动解析路径：从完整URI中移除上下文路径和Servlet路径，得到剩余路径
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        System.out.println("公告API请求路径: " + remainingPath);
//        设置响应内容类型为JSON，字符编码为UTF-8  
        resp.setContentType("application/json;charset=utf-8");
//      获取输出流
        PrintWriter out = resp.getWriter();
        
        // 处理根路径：当剩余路径为空或为根路径时，返回API文档
        if (remainingPath.isEmpty() || "/".equals(remainingPath) || "/*".equals(remainingPath)) {
            out.println("{");
            out.println("\"available_endpoints\": [");
            out.println("  {\"method\": \"GET\", \"path\": \"/announcementapi/list\", \"desc\": \"获取公告列表\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/announcementapi/visible\", \"desc\": \"获取显示公告\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/announcementapi/top\", \"desc\": \"获取置顶公告\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/announcementapi/detail?id=\", \"desc\": \"获取公告详情\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/announcementapi/admin?adminId=\", \"desc\": \"获取管理员公告\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/announcementapi/count\", \"desc\": \"获取公告数量\"}");
            out.println("]");
            out.println("}");
            return;
        }
        
        try {
        	 // 根据剩余路径分发到不同的处理方法
            if (remainingPath.startsWith("/list")) {
                handleList(req, out);// 处理获取公告列表请求
                
            } else if (remainingPath.startsWith("/visible")) {
                handleVisible(req, out);// 处理获取显示公告请求
                
            } else if (remainingPath.startsWith("/top")) {
                handleTop(req, out); // 处理获取置顶公告请求
                
            } else if (remainingPath.startsWith("/detail")) {
                handleDetail(req, out);// 处理获取公告详情请求
                
            } else if (remainingPath.startsWith("/admin")) {
                handleAdmin(req, out);// 处理获取管理员公告请求
                
            } else if (remainingPath.startsWith("/count")) {
                handleCount(req, out);// 处理获取公告数量请求
                
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
        
        System.out.println("公告API POST请求路径: " + remainingPath);
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        try {
        	// 根据剩余路径分发到不同的POST处理方法
            if ("/publish".equals(remainingPath)) {
                handlePublish(req, out); // 处理发布公告请求
            } else if ("/update".equals(remainingPath)) {
                handleUpdate(req, out);// 处理更新公告请求
            } else if ("/updateStatus".equals(remainingPath)) {
                handleUpdateStatus(req, out);// 处理更新状态请求
            } else if ("/updateTop".equals(remainingPath)) {
                handleUpdateTop(req, out);// 处理更新置顶状态请求
            } else if ("/delete".equals(remainingPath)) {
                handleDelete(req, out);// 处理删除公告请求
            } else if ("/test".equals(remainingPath)) {
                handleTest(req, out);// 处理测试接口请求
            } else {
                out.println("{\"error\":\"POST接口不存在: " + remainingPath + "\"}");
            }
        } catch (Exception e) {
            out.println("{\"error\":\"服务器错误: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
    
    /**
     * 获取公告列表（所有）
     */
    private void handleList(HttpServletRequest req, PrintWriter out) {
        String pageStr = req.getParameter("page");
        String sizeStr = req.getParameter("size");
        
        int page = 1;
        int size = 10;
        
        try {
        	 // 尝试解析页码和大小参数，如果解析失败则使用默认值
            if (pageStr != null) page = Integer.parseInt(pageStr);
            if (sizeStr != null) size = Integer.parseInt(sizeStr);
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"页码或大小参数格式错误\"}");
            return;
        }
     // 调用服务层获取公告列表
        List<Announcement> announcements = announcementService.getAllAnnouncements(page, size);
     // 获取公告总数
        int total = announcementService.getAnnouncementCount();
     // 构建JSON响应
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"page\": " + page + ",");
        out.println("  \"size\": " + size + ",");
        out.println("  \"total\": " + total + ",");
        out.println("  \"announcements\": [");
     // 遍历公告列表，输出每个公告的详细信息
        for (int i = 0; i < announcements.size(); i++) {
            Announcement announcement = announcements.get(i);
            out.println("    {");
            out.println("      \"id\": " + announcement.getId() + ",");
            out.println("      \"title\": \"" + escapeJson(announcement.getTitle()) + "\",");
            out.println("      \"content\": \"" + escapeJson(announcement.getContent()) + "\",");
            out.println("      \"isTop\": " + announcement.getIsTop() + ",");
            out.println("      \"status\": " + announcement.getStatus() + ",");
            out.println("      \"adminName\": \"" + escapeJson(announcement.getAdminName()) + "\",");
            out.println("      \"createTime\": \"" + (announcement.getCreateTime() != null ? announcement.getCreateTime().format(formatter) : "") + "\"");
            out.println("    }" + (i < announcements.size() - 1 ? "," : ""));
        }
        
        out.println("  ]");
        out.println("}");
        out.println("}");
    }
    
    /**
     * 获取显示公告
     */
    private void handleVisible(HttpServletRequest req, PrintWriter out) {
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
        
        List<Announcement> announcements = announcementService.getVisibleAnnouncements(page, size);
        int total = announcementService.getVisibleAnnouncementCount();
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"page\": " + page + ",");
        out.println("  \"size\": " + size + ",");
        out.println("  \"total\": " + total + ",");
        out.println("  \"announcements\": [");
        
        for (int i = 0; i < announcements.size(); i++) {
            Announcement announcement = announcements.get(i);
            out.println("    {");
            out.println("      \"id\": " + announcement.getId() + ",");
            out.println("      \"title\": \"" + escapeJson(announcement.getTitle()) + "\",");
            out.println("      \"content\": \"" + escapeJson(announcement.getContent()) + "\",");
            out.println("      \"isTop\": " + announcement.getIsTop() + ",");
            out.println("      \"adminName\": \"" + escapeJson(announcement.getAdminName()) + "\",");
            out.println("      \"createTime\": \"" + (announcement.getCreateTime() != null ? announcement.getCreateTime().format(formatter) : "") + "\"");
            out.println("    }" + (i < announcements.size() - 1 ? "," : ""));
        }
        
        out.println("  ]");
        out.println("}");
        out.println("}");
    }
    
    /**
     * 获取置顶公告
     */
    private void handleTop(HttpServletRequest req, PrintWriter out) {
        List<Announcement> announcements = announcementService.getTopAnnouncements();
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"count\": " + announcements.size() + ",");
        out.println("  \"announcements\": [");
        
        for (int i = 0; i < announcements.size(); i++) {
            Announcement announcement = announcements.get(i);
            out.println("    {");
            out.println("      \"id\": " + announcement.getId() + ",");
            out.println("      \"title\": \"" + escapeJson(announcement.getTitle()) + "\",");
            out.println("      \"content\": \"" + escapeJson(announcement.getContent()) + "\",");
            out.println("      \"adminName\": \"" + escapeJson(announcement.getAdminName()) + "\",");
            out.println("      \"createTime\": \"" + (announcement.getCreateTime() != null ? announcement.getCreateTime().format(formatter) : "") + "\"");
            out.println("    }" + (i < announcements.size() - 1 ? "," : ""));
        }
        
        out.println("  ]");
        out.println("}");
        out.println("}");
    }
    
    /**
     * 获取公告详情
     */
    private void handleDetail(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
     // 获取公告ID参数
     // 验证参数是否为空
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
        	// 解析公告ID
            int announcementId = Integer.parseInt(idStr);
            // 调用服务层获取公告详情
            Announcement announcement = announcementService.getAnnouncementDetail(announcementId);
         // 如果找到公告，返回详细信息
            if (announcement != null) {
                out.println("{");
                out.println("\"code\": 200,");
                out.println("\"message\": \"success\",");
                out.println("\"data\": {");
                out.println("  \"id\": " + announcement.getId() + ",");
                out.println("  \"adminId\": " + announcement.getAdminId() + ",");
                out.println("  \"adminName\": \"" + escapeJson(announcement.getAdminName()) + "\",");
                out.println("  \"title\": \"" + escapeJson(announcement.getTitle()) + "\",");
                out.println("  \"content\": \"" + escapeJson(announcement.getContent()) + "\",");
                out.println("  \"isTop\": " + announcement.getIsTop() + ",");
                out.println("  \"status\": " + announcement.getStatus() + ",");
                out.println("  \"createTime\": \"" + (announcement.getCreateTime() != null ? announcement.getCreateTime().format(formatter) : "") + "\"");
                out.println("}");
                out.println("}");
            } else {
                out.println("{\"error\":\"公告不存在\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 获取管理员公告
     */
    private void handleAdmin(HttpServletRequest req, PrintWriter out) {
    	// 获取管理员ID参数
    	String adminIdStr = req.getParameter("adminId");
        
        if (adminIdStr == null || adminIdStr.trim().isEmpty()) {
            out.println("{\"error\":\"需要adminId参数\"}");
            return;
        }
        
        try {
            int adminId = Integer.parseInt(adminIdStr);
         // 获取指定管理员发布的公告列表
            List<Announcement> announcements = announcementService.getAdminAnnouncements(adminId);
            
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"success\",");
            out.println("\"data\": {");
            out.println("  \"adminId\": " + adminId + ",");
            out.println("  \"count\": " + announcements.size() + ",");
            out.println("  \"announcements\": [");
            
            for (int i = 0; i < announcements.size(); i++) {
                Announcement announcement = announcements.get(i);
                out.println("    {");
                out.println("      \"id\": " + announcement.getId() + ",");
                out.println("      \"title\": \"" + escapeJson(announcement.getTitle()) + "\",");
                out.println("      \"content\": \"" + escapeJson(announcement.getContent()) + "\",");
                out.println("      \"isTop\": " + announcement.getIsTop() + ",");
                out.println("      \"status\": " + announcement.getStatus() + ",");
                out.println("      \"createTime\": \"" + (announcement.getCreateTime() != null ? announcement.getCreateTime().format(formatter) : "") + "\"");
                out.println("    }" + (i < announcements.size() - 1 ? "," : ""));
            }
            
            out.println("  ]");
            out.println("}");
            out.println("}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"adminId参数格式错误\"}");
        }
    }
    
    /**
     * 获取公告数量统计
     */
    private void handleCount(HttpServletRequest req, PrintWriter out) {
    	// 获取各种统计数量
    	int total = announcementService.getAnnouncementCount();
        int visible = announcementService.getVisibleAnnouncementCount();
        List<Announcement> topAnnouncements = announcementService.getTopAnnouncements();
        
        out.println("{");
        out.println("\"code\": 200,");
        out.println("\"message\": \"success\",");
        out.println("\"data\": {");
        out.println("  \"totalCount\": " + total + ",");
        out.println("  \"visibleCount\": " + visible + ",");
        out.println("  \"topCount\": " + topAnnouncements.size() + ",");
        out.println("  \"hiddenCount\": " + (total - visible));
        out.println("}");
        out.println("}");
    }
    
    /**
     * 发布公告
     */
    private void handlePublish(HttpServletRequest req, PrintWriter out) {
        try {
        	// 获取所有必要参数
            String adminIdStr = req.getParameter("adminId");
            String title = req.getParameter("title");
            String content = req.getParameter("content");
            String isTopStr = req.getParameter("isTop");
            String statusStr = req.getParameter("status");
            
            System.out.println("发布公告参数: adminId=" + adminIdStr + 
                             ", title=" + title + 
                             ", content=" + content + 
                             ", isTop=" + isTopStr + 
                             ", status=" + statusStr);
            
            if (adminIdStr == null || title == null || content == null) {
                out.println("{\"error\":\"缺少必要参数：adminId, title, content\"}");
                return;
            }
         // 解析管理员ID
            int adminId = Integer.parseInt(adminIdStr);
         // 创建公告对象并设置属性
            Announcement announcement = new Announcement();
            announcement.setAdminId(adminId);
            announcement.setTitle(title);
            announcement.setContent(content);
         // 设置置顶状态（默认值为0）
            if (isTopStr != null && !isTopStr.trim().isEmpty()) {
                announcement.setIsTop(Integer.parseInt(isTopStr));
            } else {
                announcement.setIsTop(0);
            }
            // 设置状态（默认值为1-显示状态）
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                announcement.setStatus(Integer.parseInt(statusStr));
            } else {
                announcement.setStatus(1);
            }
            // 调用服务层发布公告
            int announcementId = announcementService.publishAnnouncement(announcement);
         // 返回发布结果
            if (announcementId > 0) {
                out.println("{\"success\":true,\"announcementId\":" + announcementId + "}");
            } else {
                out.println("{\"error\":\"发布失败\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"参数格式错误: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            out.println("{\"error\":\"发布失败: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * 更新公告
     */
    private void handleUpdate(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String isTopStr = req.getParameter("isTop");
        String statusStr = req.getParameter("status");
        
        if (idStr == null || title == null || content == null) {
            out.println("{\"error\":\"缺少必要参数：id, title, content\"}");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            Announcement announcement = new Announcement();
            announcement.setId(id);
            announcement.setTitle(title);
            announcement.setContent(content);
            
            if (isTopStr != null && !isTopStr.trim().isEmpty()) {
                announcement.setIsTop(Integer.parseInt(isTopStr));
            }
            
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                announcement.setStatus(Integer.parseInt(statusStr));
            }
            
            // 调用服务层更新公告
            boolean success = announcementService.updateAnnouncement(announcement);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"参数格式错误: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * 更新状态
     */
    private void handleUpdateStatus(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        String statusStr = req.getParameter("status");
        
        if (idStr == null || statusStr == null) {
            out.println("{\"error\":\"需要id和status参数\"}");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            int status = Integer.parseInt(statusStr);
            // 调用服务层更新公告状态
            boolean success = announcementService.updateStatus(id, status);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"参数格式错误\"}");
        }
    }
    
    /**
     * 更新置顶状态
     */
    private void handleUpdateTop(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        String isTopStr = req.getParameter("isTop");
        
        if (idStr == null || isTopStr == null) {
            out.println("{\"error\":\"需要id和isTop参数\"}");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            int isTop = Integer.parseInt(isTopStr);
         // 调用服务层更新置顶状态
            boolean success = announcementService.updateTopStatus(id, isTop);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"参数格式错误\"}");
        }
    }
    
    /**
     * 删除公告
     */
    private void handleDelete(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        
        if (idStr == null) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
         // 调用服务层删除公告
            boolean success = announcementService.deleteAnnouncement(id);
            out.println("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id参数格式错误\"}");
        }
    }
    
    /**
     * 测试接口
     */
    private void handleTest(HttpServletRequest req, PrintWriter out) {
        try {
            // 创建测试公告
            Announcement announcement = new Announcement();
            announcement.setAdminId(1);
            announcement.setTitle("测试公告标题");
            announcement.setContent("这是测试公告内容，用于验证公告功能是否正常。");
            announcement.setIsTop(1);
            announcement.setStatus(1);
            
            int id = announcementService.publishAnnouncement(announcement);
            
            out.println("{");
            out.println("\"test\": true,");
            out.println("\"operation\": \"publish test announcement\",");
            out.println("\"result\": " + (id > 0) + ",");
            out.println("\"announcementId\": " + id + ",");
            out.println("\"totalCount\": " + announcementService.getAnnouncementCount() + ",");
            out.println("\"visibleCount\": " + announcementService.getVisibleAnnouncementCount());
            out.println("}");
        } catch (Exception e) {
            out.println("{\"error\":\"测试失败: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * JSON转义
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * 初始化方法
     */
    @Override
    public void init() throws ServletException {
        System.out.println("AnnouncementApiServlet 初始化完成");
        System.out.println("API端点:");
        System.out.println("  GET  /announcementapi/list     - 获取所有公告");
        System.out.println("  GET  /announcementapi/visible  - 获取显示公告");
        System.out.println("  GET  /announcementapi/top      - 获取置顶公告");
        System.out.println("  GET  /announcementapi/detail   - 获取公告详情");
        System.out.println("  GET  /announcementapi/admin    - 获取管理员公告");
        System.out.println("  GET  /announcementapi/count    - 获取公告统计");
        System.out.println("  POST /announcementapi/publish  - 发布公告");
        System.out.println("  POST /announcementapi/update   - 更新公告");
        System.out.println("  POST /announcementapi/updateStatus - 更新状态");
        System.out.println("  POST /announcementapi/updateTop - 更新置顶状态");
        System.out.println("  POST /announcementapi/delete   - 删除公告");
        System.out.println("  POST /announcementapi/test     - 测试接口");
    }
}