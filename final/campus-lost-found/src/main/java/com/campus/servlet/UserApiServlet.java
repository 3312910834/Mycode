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
import com.campus.utils.MD5Util;
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
    
//    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) 
//            throws IOException {
//        String username = req.getParameter("username");
//        String password = req.getParameter("password");
//        
//        resp.setContentType("application/json;charset=utf-8");
//        PrintWriter out = resp.getWriter();
//        
//        if (username == null || password == null) {
//            out.println("{\"error\":\"需要用户名和密码参数\"}");
//            return;
//        }
//        
//        User user = userService.login(username, password);
//        if (user != null) {
//            out.println("{");
//            out.println("\"code\": 200,");
//            out.println("\"message\": \"登录成功\",");
//            out.println("\"data\": {");
//            out.println("  \"id\": " + user.getId() + ",");
//            out.println("  \"username\": \"" + user.getUsername() + "\",");
//            out.println("  \"role\": \"" + user.getRole() + "\",");
//            out.println("  \"phone\": \"" + (user.getPhone() != null ? user.getPhone() : "") + "\"");
//            out.println("}");
//            out.println("}");
//        } else {
//            out.println("{\"error\":\"用户名或密码错误\"}");
//        }
//    }
//    后
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        // 1. 获取参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        // 设置响应格式
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        // 2. 基础非空校验
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.out.println("❌ 登录失败：用户名或密码为空");
            out.println("{\"code\": 400, \"message\": \"用户名和密码不能为空\"}");
            return;
        }

        // 【关键调试】去除可能存在的首尾空格
        username = username.trim();
        password = password.trim();

        // 3. 【核心调试日志】打印接收到的数据
        System.out.println("=== 🔍 登录调试开始 ===");
        System.out.println("接收到的用户名: [" + username + "]");
        System.out.println("接收到的原始密码: [" + password + "]");
        
        // 4. 【关键修改】强制使用明文，不进行任何 MD5 加密
        // 此时 checkPassword 应该和数据库里的 '123456' 完全一致
        String checkPassword = password; 
        
        System.out.println("将要传入 Service 进行比对的密码: [" + checkPassword + "]");
        System.out.println("预期数据库中的密码应为: [123456]");

        // 5. 调用 Service 层
        // 注意：确保你的 UserService.login 内部是直接拿这个密码去 SQL 里比对
        User user = userService.login(username, checkPassword);

        if (user != null) {
            // ✅ 登录成功
            System.out.println("✅ 登录成功！找到用户: " + user.getUsername() + ", 角色: " + user.getRole());
            
            // 将用户存入 Session
            req.getSession().setAttribute("currentUser", user);
            
            // 返回成功 JSON
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
            // ❌ 登录失败
            System.out.println("❌ 登录失败！userService.login 返回了 null");
            System.out.println("   可能原因：");
            System.out.println("   1. 数据库中用户名 '" + username + "' 不存在");
            System.out.println("   2. 数据库中该用户的密码不是 '" + checkPassword + "' (请检查是否有空格或大小写)");
            System.out.println("   3. UserService 中的 SQL 语句写错了");
            
            out.println("{\"code\": 401, \"message\": \"用户名或密码错误\"}");
        }
        
        System.out.println("=== 🔍 登录调试结束 ===");
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
        
        if (remainingPath.startsWith("/updatePassword")) {
            handleUpdatePassword(req, resp);
        } else if (remainingPath.startsWith("/update")) {
            handleUpdate(req, resp);
        } else if (remainingPath.startsWith("/logout")) {
            handleLogout(req, resp);
        } else if (remainingPath.startsWith("/register")) {
            handleRegister(req, resp);
        } else {
            // 其他POST请求转发到GET处理
            doGet(req, resp);
        }
    }

    private void handleUpdatePassword(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();

        String userIdStr = req.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            userIdStr = req.getParameter("id");
        }
        String oldPassword = req.getParameter("old_password");
        if (oldPassword == null) {
            oldPassword = req.getParameter("oldPassword");
        }
        String newPassword = req.getParameter("new_password");
        if (newPassword == null) {
            newPassword = req.getParameter("newPassword");
        }

        if (userIdStr == null || oldPassword == null || newPassword == null
                || oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            out.println("{\"code\":400,\"message\":\"缺少必要参数\"}");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr.trim());
            boolean success = userService.updatePassword(userId, oldPassword.trim(), newPassword.trim());
            if (success) {
                out.println("{\"code\":200,\"message\":\"密码修改成功\",\"success\":true}");
            } else {
                out.println("{\"code\":400,\"message\":\"原密码错误或新密码不合法\"}");
            }
        } catch (NumberFormatException e) {
            out.println("{\"code\":400,\"message\":\"用户ID格式错误\"}");
        } catch (Exception e) {
            out.println("{\"code\":500,\"message\":\"服务器内部错误\"}");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        try {
            if (req.getSession(false) != null) {
                req.getSession(false).invalidate();
            }
            out.println("{\"code\":200,\"message\":\"退出成功\",\"success\":true}");
        } catch (Exception e) {
            out.println("{\"code\":500,\"message\":\"退出失败\"}");
        }
    }
}