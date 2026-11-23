package com.campus.servlet.admin;

import com.campus.entity.User;
import com.campus.service.UserService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员专用：用户管理 Servlet
 * 
 * 功能：
 * 1. 分页查询用户列表 (支持筛选)
 * 2. 修改用户状态 (封禁/解封)
 * 3. 修改用户角色 (提拔/降级)
 * 
 * 访问路径: /admin/user
 * 安全机制: 依赖 AdminAuthFilter 拦截，非 admin 角色无法访问
 */
@WebServlet("/admin/user")
public class AdminUserServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
    }

    /**
     * 统一请求处理入口
     */
    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 【双重安全检查】
        // 虽然 AdminAuthFilter 已经拦截了 /admin/*，但在这里再次检查是防御性编程的好习惯
        HttpSession session = req.getSession(false);
        User currentUser = null;

        if (session != null) {
            Object obj = session.getAttribute("currentUser");
            if (obj instanceof User) {
                currentUser = (User) obj;
            } else {
                // 兼容可能的 "user" key
                obj = session.getAttribute("user");
                if (obj instanceof User) {
                    currentUser = (User) obj;
                }
            }
        }

        if (currentUser == null) {
            currentUser = resolveUserFromToken(req);
        }

        // 如果用户未登录或不是管理员，直接拒绝
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            resp.setStatus(403); // Forbidden
            Map<String, Object> error = new HashMap<>();
            error.put("code", 403);
            error.put("msg", "禁止访问：需要管理员权限");
            out.write(gson.toJson(error));
            return;
        }

        // 获取操作类型参数
        String action = req.getParameter("action");

        try {
            if ("list".equals(action)) {
                handleGetList(req, out);
            } else if ("updateStatus".equals(action)) {
                handleUpdateStatus(req, out);
            } else if ("updateRole".equals(action)) {
                handleUpdateRole(req, out);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("msg", "未知操作类型: " + action);
                out.write(gson.toJson(error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "服务器内部错误: " + e.getMessage());
            out.write(gson.toJson(error));
        }
    }

    /**
     * 处理：获取用户列表 (分页 + 筛选)
     * URL 示例: /admin/user?action=list&page=1&limit=10&username=zhang&status=1
     */
    private void handleGetList(HttpServletRequest req, PrintWriter out) {
        int page = 1;
        int limit = 10;

        // 解析分页参数
        try {
            if (req.getParameter("page") != null) {
                page = Integer.parseInt(req.getParameter("page"));
            }
            if (req.getParameter("limit") != null) {
                limit = Integer.parseInt(req.getParameter("limit"));
            }
        } catch (NumberFormatException e) {
            // 格式错误则使用默认值
        }

        String username = req.getParameter("username"); // 模糊搜索
        String role = req.getParameter("role");         // 精确匹配
        String statusStr = req.getParameter("status");  // 精确匹配
        
        Integer status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                status = Integer.parseInt(statusStr);
            } catch (NumberFormatException ignored) {}
        }

        // 调用 Service 层获取数据
        Map<String, Object> data = userService.getUserPage(page, limit, username, role, status);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "success");
        result.put("data", data);

        out.write(gson.toJson(result));
    }

    /**
     * 处理：修改用户状态 (封禁/解封)
     * URL 示例 (POST): /admin/user?action=updateStatus&id=5&status=0
     */
    private void handleUpdateStatus(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        String statusStr = req.getParameter("status");

        if (idStr == null || statusStr == null) {
            out.write(gson.toJson(Map.of("code", 400, "msg", "缺少必要参数: id 或 status")));
            return;
        }

        try {
            int userId = Integer.parseInt(idStr);
            int status = Integer.parseInt(statusStr);

            // 校验状态值 (通常 1=正常, 0=禁用)
            if (status != 0 && status != 1) {
                out.write(gson.toJson(Map.of("code", 400, "msg", "状态值无效，必须为 0 或 1")));
                return;
            }

            // 防止管理员把自己封了 (可选逻辑，视需求而定)
            // HttpSession session = req.getSession(false);
            // User currentUser = (User) session.getAttribute("currentUser");
            // if (currentUser.getId() == userId) { ... }

            boolean success = userService.updateUserStatus(userId, status);

            if (success) {
                String msg = status == 0 ? "用户已封禁" : "用户已解封";
                out.write(gson.toJson(Map.of("code", 200, "msg", msg)));
            } else {
                out.write(gson.toJson(Map.of("code", 500, "msg", "数据库更新失败，可能用户不存在")));
            }

        } catch (NumberFormatException e) {
            out.write(gson.toJson(Map.of("code", 400, "msg", "参数格式错误: ID 和 Status 必须是数字")));
        }
    }

    /**
     * 处理：修改用户角色 (提拔/降级)
     * URL 示例 (POST): /admin/user?action=updateRole&id=5&role=admin
     */
    private void handleUpdateRole(HttpServletRequest req, PrintWriter out) {
        String idStr = req.getParameter("id");
        String role = req.getParameter("role");

        if (idStr == null || role == null) {
            out.write(gson.toJson(Map.of("code", 400, "msg", "缺少必要参数: id 或 role")));
            return;
        }

        try {
            int userId = Integer.parseInt(idStr);

            // 严格校验角色值
            if (!"admin".equals(role) && !"user".equals(role)) {
                out.write(gson.toJson(Map.of("code", 400, "msg", "非法角色值，只能是 'admin' 或 'user'")));
                return;
            }

            boolean success = userService.updateUserRole(userId, role);

            if (success) {
                String msg = "admin".equals(role) ? "已提拔为管理员" : "已降级为普通用户";
                out.write(gson.toJson(Map.of("code", 200, "msg", msg)));
            } else {
                out.write(gson.toJson(Map.of("code", 500, "msg", "数据库更新失败")));
            }

        } catch (NumberFormatException e) {
            out.write(gson.toJson(Map.of("code", 400, "msg", "参数格式错误: ID 必须是数字")));
        }
    }

    private User resolveUserFromToken(HttpServletRequest req) {
        try {
            String auth = req.getHeader("Authorization");
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
}