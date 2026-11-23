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

@WebServlet("/test/user")
public class UserTestServlet extends HttpServlet {
    private UserService userService = new UserService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>用户模块测试</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".success { color: green; background: #e8f5e8; padding: 10px; border-radius: 5px; }");
        out.println(".error { color: red; background: #ffebee; padding: 10px; border-radius: 5px; }");
        out.println(".test-box { border: 1px solid #ddd; padding: 20px; margin: 20px 0; border-radius: 5px; }");
        out.println("</style></head><body>");
        
        out.println("<h1>用户模块测试</h1>");
        
        // 测试1：测试MD5加密
        out.println("<div class='test-box'>");
        out.println("<h3>测试1: MD5加密</h3>");
        String testPassword = "123456";
        String encrypted = com.campus.utils.MD5Util.md5(testPassword);
        out.println("<p>原始密码: " + testPassword + "</p>");
        out.println("<p>MD5加密后: " + encrypted + "</p>");
        out.println("</div>");
        
        // 测试2：测试注册功能
        out.println("<div class='test-box'>");
        out.println("<h3>测试2: 用户注册</h3>");
        out.println("<form method='post' action=''>");
        out.println("<input type='hidden' name='action' value='register'>");
        out.println("用户名: <input type='text' name='username' value='testuser_" + System.currentTimeMillis() % 1000 + "'><br>");
        out.println("密码: <input type='password' name='password' value='123456'><br>");
        out.println("手机号: <input type='text' name='phone' value='13800000000'><br>");
        out.println("<input type='submit' value='测试注册'>");
        out.println("</form>");
        out.println("</div>");
        
        // 测试3：测试登录功能
        out.println("<div class='test-box'>");
        out.println("<h3>测试3: 用户登录</h3>");
        out.println("<form method='post' action=''>");
        out.println("<input type='hidden' name='action' value='login'>");
        out.println("用户名: <input type='text' name='username' value='admin'><br>");
        out.println("密码: <input type='password' name='password' value='admin123'><br>");
        out.println("<input type='submit' value='测试登录'>");
        out.println("</form>");
        out.println("</div>");
        
        out.println("<p><a href='/campus-lost-found/test/db'>← 返回数据库测试</a></p>");
        out.println("<p><a href='/campus-lost-found/test'>← 返回首页</a></p>");
        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><body>");
        out.println("<h1>测试结果</h1>");
        
        if ("register".equals(action)) {
            // 测试注册
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String phone = request.getParameter("phone");
            
            User user = new User(username, password, phone, "user");
            user.setEmail(username + "@test.com");
            
            int userId = userService.register(user);
            if (userId > 0) {
                out.println("<div class='success'>✅ 注册成功！用户ID: " + userId + "</div>");
            } else {
                out.println("<div class='error'>❌ 注册失败！用户名可能已存在</div>");
            }
            
        } else if ("login".equals(action)) {
            // 测试登录
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            User user = userService.login(username, password);
            if (user != null) {
                out.println("<div class='success'>✅ 登录成功！</div>");
                out.println("<p>用户名: " + user.getUsername() + "</p>");
                out.println("<p>角色: " + user.getRole() + "</p>");
                out.println("<p>手机号: " + user.getPhone() + "</p>");
            } else {
                out.println("<div class='error'>❌ 登录失败！用户名或密码错误</div>");
            }
        }
        
        out.println("<p><a href='/campus-lost-found/test/user'>← 返回测试页面</a></p>");
        out.println("</body></html>");
    }
}