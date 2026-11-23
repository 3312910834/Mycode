package com.campus.filter;

import com.campus.entity.User;
import com.campus.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 管理员权限验证过滤器
 * 兼容 "currentUser" 和 "user" 两种 Session 键名
 */
@WebFilter(filterName = "AdminAuthFilter", urlPatterns = {"/admin/*", "/adminapi/*"})
public class AdminAuthFilter implements Filter {
    private final UserService userService = new UserService();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println(">>> AdminAuthFilter 初始化成功：已开启管理员权限校验");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");

        User user = null;

        // 🛡️【双保险策略】尝试从两个可能的键名中获取用户对象
        if (session != null) {
            Object obj1 = session.getAttribute("currentUser");
            Object obj2 = session.getAttribute("user");
            
            if (obj1 instanceof User) {
                user = (User) obj1;
            } else if (obj2 instanceof User) {
                user = (User) obj2;
            }
        }

        if (user == null) {
            user = resolveUserFromToken(httpRequest);
        }

        // 1. 检查是否获取到有效用户
        if (user == null) {
            httpResponse.setStatus(401);
            PrintWriter out = httpResponse.getWriter();
            out.write("{\"code\": 401, \"msg\": \"未登录或会话过期，请先登录\"}");
            out.flush();
            return;
        }

        // 2. 检查角色是否为 admin
        if (!"admin".equals(user.getRole())) {
            httpResponse.setStatus(403);
            PrintWriter out = httpResponse.getWriter();
            out.write("{\"code\": 403, \"msg\": \"拒绝访问：需要管理员权限\"}");
            out.flush();
            return;
        }

        // 3. 检查账号状态 (1=正常, 0=禁用)
        if (user.getStatus() != null && user.getStatus() == 0) {
            httpResponse.setStatus(403);
            PrintWriter out = httpResponse.getWriter();
            out.write("{\"code\": 403, \"msg\": \"账号已被禁用\"}");
            out.flush();
            return;
        }

        // ✅ 验证通过，放行
        chain.doFilter(request, response);
    }

    private User resolveUserFromToken(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                return null;
            }
            String token = auth.substring(7).trim();
            if (!token.startsWith("user_")) {
                return null;
            }
            String[] parts = token.split("_");
            if (parts.length < 2) {
                return null;
            }
            int userId = Integer.parseInt(parts[1]);
            return userService.getUserInfo(userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void destroy() {
        System.out.println(">>> AdminAuthFilter 销毁");
    }
}