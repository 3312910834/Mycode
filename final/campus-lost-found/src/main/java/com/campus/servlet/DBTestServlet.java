package com.campus.servlet;

import com.campus.utils.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test/db")
public class DBTestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>数据库测试</title></head><body>");
        out.println("<h1>数据库连接测试</h1>");
        
        // 测试数据库连接
        String result = DBUtil.testConnection();
        
        out.println("<pre style='background:#f5f5f5;padding:10px;border-radius:5px;'>");
        out.println(result);
        out.println("</pre>");
        
        out.println("<p><a href='/campus-lost-found/test'>返回首页</a></p>");
        out.println("</body></html>");
    }
}