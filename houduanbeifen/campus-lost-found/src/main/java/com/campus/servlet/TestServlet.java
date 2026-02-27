package com.campus.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><body>");
        out.println("<h1>✅ Maven项目测试成功！</h1>");
        out.println("<p>时间：" + new java.util.Date() + "</p>");
        out.println("<p>Servlet API版本：Jakarta EE 10</p>");
        out.println("<p>Java版本：21</p>");
        out.println("</body></html>");
    }
}