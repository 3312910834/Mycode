package com.campus.servlet;

import com.campus.entity.User;
import com.campus.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/userapi/*")
public class UserApiServlet extends HttpServlet {
    private UserService userService = new UserService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 获取请求URI并解析路径
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath(); // /campus-lost-found
        String servletPath = req.getServletPath(); // /userapi
        
        System.out.println("请求URI: " + requestURI);
        System.out.println("上下文路径: " + contextPath);
        System.out.println("Servlet路径: " + servletPath);
        
        // 手动解析路径
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        System.out.println("剩余路径: " + remainingPath);
        
        // 处理路径
        if (remainingPath.isEmpty() || "/".equals(remainingPath) || "/*".equals(remainingPath)) {
            // 返回可用接口列表
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.println("{");
            out.println("\"available_endpoints\": [");
            out.println("  {\"method\": \"GET\", \"path\": \"/userapi/test\", \"desc\": \"测试接口\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/userapi/login?username=&password=\", \"desc\": \"用户登录\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/userapi/register?username=&password=&phone=\", \"desc\": \"用户注册\"},");
            out.println("  {\"method\": \"GET\", \"path\": \"/userapi/info?id=\", \"desc\": \"获取用户信息\"},");
            out.println("  {\"method\": \"POST\", \"path\": \"/userapi/update\", \"desc\": \"更新用户信息\"}");
            out.println("]");
            out.println("}");
            return;
        }
        
        // 根据路径调用对应方法
        if (remainingPath.startsWith("/test")) {
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.println("{\"status\":\"OK\",\"message\":\"API服务正常\"}");
            
        } else if (remainingPath.startsWith("/login")) {
            handleLogin(req, resp);
            
        } else if (remainingPath.startsWith("/register")) {
            handleRegister(req, resp);
            
        } else if (remainingPath.startsWith("/info")) {
            handleGetInfo(req, resp);
            
        } else if (remainingPath.startsWith("/update")) {
            handleUpdate(req, resp);
            
        } else {
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.println("{\"error\":\"路径不存在: " + remainingPath + "\"}");
        }
    }
    
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        if (username == null || password == null) {
            out.println("{\"error\":\"需要用户名和密码参数\"}");
            return;
        }
        
        User user = userService.login(username, password);
        if (user != null) {
            out.println("{");
            out.println("\"code\": 200,");
            out.println("\"message\": \"登录成功\",");
            out.println("\"data\": {");
            out.println("  \"id\": " + user.getId() + ",");
            out.println("  \"username\": \"" + user.getUsername() + "\",");
            out.println("  \"role\": \"" + user.getRole() + "\",");
            out.println("  \"phone\": \"" + (user.getPhone() != null ? user.getPhone() : "") + "\"");
            out.println("}");
            out.println("}");
        } else {
            out.println("{\"error\":\"用户名或密码错误\"}");
        }
    }
    
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        if (username == null || password == null) {
            out.println("{\"error\":\"需要用户名和密码参数\"}");
            return;
        }
        
        User user = new User(username, password, phone, "user");
        user.setEmail(username + "@example.com");
        
        int userId = userService.register(user);
        if (userId > 0) {
            out.println("{\"success\":true,\"userId\":" + userId + "}");
        } else {
            out.println("{\"error\":\"注册失败，用户名可能已存在\"}");
        }
    }
    
    private void handleGetInfo(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        String idStr = req.getParameter("id");
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        if (idStr == null) {
            out.println("{\"error\":\"需要id参数\"}");
            return;
        }
        
        try {
            int userId = Integer.parseInt(idStr);
            User user = userService.getUserInfo(userId);
            if (user != null) {
                out.println("{");
                out.println("\"id\": " + user.getId() + ",");
                out.println("\"username\": \"" + user.getUsername() + "\",");
                out.println("\"phone\": \"" + (user.getPhone() != null ? user.getPhone() : "") + "\",");
                out.println("\"role\": \"" + user.getRole() + "\",");
                out.println("\"real_name\": \"" + (user.getRealName() != null ? user.getRealName() : "") + "\",");
                out.println("\"email\": \"" + (user.getEmail() != null ? user.getEmail() : "") + "\",");
                out.println("\"avatar\": \"" + (user.getAvatar() != null ? user.getAvatar() : "") + "\"");
                out.println("}");
            } else {
                out.println("{\"error\":\"用户不存在\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"error\":\"id必须是数字\"}");
        }
    }
    
    // 添加的 handleUpdate 方法
    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        try {
            // 读取请求体（POST请求）
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            System.out.println("更新用户请求: " + requestBody);
            
            // 检查请求体是否为空
            if (requestBody == null || requestBody.trim().isEmpty()) {
                out.println("{\"code\": 400, \"message\": \"请求体为空\", \"success\": false}");
                return;
            }
            
            // 简单的JSON解析（实际项目应该使用Jackson/Gson）
            // 解析用户ID
            if (!requestBody.contains("\"id\"")) {
                out.println("{\"code\": 400, \"message\": \"缺少用户ID\", \"success\": false}");
                return;
            }
            
            User user = new User();
            
            // 解析ID
            int idIndex = requestBody.indexOf("\"id\":");
            if (idIndex != -1) {
                int idStart = requestBody.indexOf(":", idIndex) + 1;
                int idEnd = requestBody.indexOf(",", idStart);
                if (idEnd == -1) idEnd = requestBody.indexOf("}", idStart);
                String idStr = requestBody.substring(idStart, idEnd).trim();
                try {
                    user.setId(Integer.parseInt(idStr.replace("\"", "")));
                } catch (NumberFormatException e) {
                    out.println("{\"code\": 400, \"message\": \"ID格式错误\", \"success\": false}");
                    return;
                }
            }
            
            // 解析real_name
            if (requestBody.contains("\"real_name\"")) {
                int start = requestBody.indexOf("\"real_name\":\"") + 13;
                int end = requestBody.indexOf("\"", start);
                if (start > 13 && end > start) {
                    user.setRealName(requestBody.substring(start, end));
                }
            }
            
            // 解析phone
            if (requestBody.contains("\"phone\"")) {
                int start = requestBody.indexOf("\"phone\":\"") + 9;
                int end = requestBody.indexOf("\"", start);
                if (start > 9 && end > start) {
                    user.setPhone(requestBody.substring(start, end));
                }
            }
            
            // 解析email
            if (requestBody.contains("\"email\"")) {
                int start = requestBody.indexOf("\"email\":\"") + 9;
                int end = requestBody.indexOf("\"", start);
                if (start > 9 && end > start) {
                    user.setEmail(requestBody.substring(start, end));
                }
            }
            
            // 解析avatar
            if (requestBody.contains("\"avatar\"")) {
                int start = requestBody.indexOf("\"avatar\":\"") + 10;
                int end = requestBody.indexOf("\"", start);
                if (start > 10 && end > start) {
                    user.setAvatar(requestBody.substring(start, end));
                }
            }
            
            System.out.println("解析后的用户对象: " + user);
            
            // 调用服务更新用户信息
            boolean success = userService.updateUserInfo(user);
            
            if (success) {
                out.println("{");
                out.println("\"code\": 200,");
                out.println("\"message\": \"用户信息更新成功\",");
                out.println("\"data\": {");
                out.println("  \"id\": " + user.getId() + ",");
                out.println("  \"success\": true");
                out.println("}");
                out.println("}");
            } else {
                out.println("{\"code\": 500, \"message\": \"更新失败，请稍后重试\", \"success\": false}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"code\": 500, \"message\": \"服务器内部错误: " + e.getMessage() + "\", \"success\": false}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // 获取请求URI
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());
        
        System.out.println("POST请求路径: " + remainingPath);
        
        if (remainingPath.startsWith("/update")) {
            handleUpdate(req, resp);
        } else if (remainingPath.startsWith("/register")) {
            handleRegister(req, resp);
        } else {
            // 其他POST请求转发到GET处理
            doGet(req, resp);
        }
    }
}