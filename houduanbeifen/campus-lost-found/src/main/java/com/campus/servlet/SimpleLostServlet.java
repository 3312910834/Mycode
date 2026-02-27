package com.campus.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 最简单的失物Servlet，避免复杂逻辑
@WebServlet("/simplelost")
public class SimpleLostServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        
        out.println("{");
        out.println("\"status\": \"SimpleLostServlet is working\",");
        out.println("\"timestamp\": " + System.currentTimeMillis());
        out.println("}");
    }
}