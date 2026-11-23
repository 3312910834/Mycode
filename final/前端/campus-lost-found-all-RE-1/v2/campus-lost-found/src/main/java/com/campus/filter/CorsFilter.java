package com.campus.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS跨域过滤器
 * 允许前端从不同端口访问后端API
 * 
 * 使用注解方式配置，过滤所有请求
 */
@WebFilter("/*")
public class CorsFilter implements Filter {
    
    /**
     * 初始化方法
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CORS过滤器初始化完成");
        System.out.println("允许跨域访问，支持前端开发调试");
    }
    
    /**
     * 过滤处理方法
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 打印请求信息（调试用）
        String origin = httpRequest.getHeader("Origin");
        System.out.println("CORS请求来源: " + (origin != null ? origin : "未知"));
        System.out.println("请求方法: " + httpRequest.getMethod());
        System.out.println("请求路径: " + httpRequest.getRequestURI());
        
        // ========== CORS核心配置 ==========
        // 1. 允许的源（开发环境允许所有，生产环境要指定具体域名）
        String allowOrigin = "*";
        // 如果要限制特定域名，可以这样设置：
        // String allowOrigin = "http://localhost:3000"; // Vue默认端口
        // String allowOrigin = "http://localhost:8081"; // 其他前端端口
        
        httpResponse.setHeader("Access-Control-Allow-Origin", allowOrigin);
        
        // 2. 允许的HTTP方法
        httpResponse.setHeader("Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        
        // 3. 允许的请求头
        httpResponse.setHeader("Access-Control-Allow-Headers", 
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, " +
            "X-CSRF-Token, X-API-KEY, X-Auth-Token, X-Request-Id");
        
        // 4. 允许前端访问的响应头
        httpResponse.setHeader("Access-Control-Expose-Headers", 
            "Authorization, Content-Disposition, X-Total-Count");
        
        // 5. 允许发送Cookie等凭证信息
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        // 6. 预检请求缓存时间（秒）
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        
        // ========== 处理OPTIONS预检请求 ==========
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            System.out.println("处理OPTIONS预检请求");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            
            // 对于OPTIONS请求，直接返回，不继续传递
            return;
        }
        
        // ========== 其他通用响应头 ==========
        // 防止缓存
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");
        
        // 内容类型
//        httpResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        
        // ========== 继续处理请求 ==========
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            System.err.println("CORS过滤器处理异常: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 销毁方法
     */
    @Override
    public void destroy() {
        System.out.println("CORS过滤器销毁");
    }
}