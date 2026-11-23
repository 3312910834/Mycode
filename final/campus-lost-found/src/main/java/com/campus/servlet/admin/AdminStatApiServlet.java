package com.campus.servlet.admin;

import com.alibaba.fastjson2.JSON;
import com.campus.service.StatService;
import com.google.gson.Gson; // 换成了 Gson

//如果你的项目原来用的是 javax，请把下面 5 行的 jakarta 换回 javax
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/stat/dashboard")
public class AdminStatApiServlet extends HttpServlet {
    
    private StatService statService = new StatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 设置响应格式为 JSON，并处理中文乱码
        resp.setContentType("application/json;charset=UTF-8");
        
        // 允许前端跨域请求
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        
        try {
            // 1. 获取打包好的统计数据
            Map<String, Object> dashboardData = statService.getDashboardData();
            
            // 2. 包装成标准 API 返回格式
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", dashboardData);
            
            // 3. 转成 JSON 并输出
            resp.getWriter().write(JSON.toJSONString(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"code\": 500, \"message\": \"获取统计数据失败\"}");
        }
    }
}