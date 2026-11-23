package com.campus.servlet;

import com.campus.config.ItemCategoryStore;
import com.campus.entity.FoundItem;
import com.campus.entity.LostItem;
import com.campus.service.FoundItemService;
import com.campus.service.LostItemService;
import com.campus.service.UserService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/homeapi/*")
public class HomeApiServlet extends HttpServlet {
    private final LostItemService lostItemService = new LostItemService();
    private final FoundItemService foundItemService = new FoundItemService();
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String remainingPath = requestURI.substring(contextPath.length() + servletPath.length());

        if (remainingPath == null || remainingPath.isEmpty() || "/".equals(remainingPath)) {
            writeJson(resp, Map.of("code", 200, "message", "success", "data", Map.of("endpoints", new String[]{"/homeapi/stats", "/homeapi/categories"})));
            return;
        }

        if (remainingPath.startsWith("/stats")) {
            handleStats(resp);
            return;
        }

        if (remainingPath.startsWith("/categories")) {
            writeJson(resp, Map.of("code", 200, "message", "success", "data", ItemCategoryStore.snapshot()));
            return;
        }

        writeJson(resp, Map.of("code", 404, "message", "接口不存在: " + remainingPath));
    }

    private void handleStats(HttpServletResponse resp) throws IOException {
        int lostCount = Math.max(lostItemService.getLostItemCount(), 1);
        int foundCount = Math.max(foundItemService.getFoundItemCount(), 1);

        List<LostItem> lostItems = lostItemService.getAllLostItems(1, lostCount);
        List<FoundItem> foundItems = foundItemService.getAllFoundItems(1, foundCount);

        int successCount = 0;
        for (LostItem i : lostItems) {
            if (isResolved(i.getStatus())) {
                successCount++;
            }
        }
        for (FoundItem i : foundItems) {
            if (isResolved(i.getStatus())) {
                successCount++;
            }
        }

        Map<String, Object> userPage = userService.getUserPage(1, 1, null, null, null);
        Object userCount = userPage.getOrDefault("total", 0);

        Map<String, Object> data = new HashMap<>();
        data.put("lostCount", lostItems.size());
        data.put("foundCount", foundItems.size());
        data.put("successCount", successCount);
        data.put("userCount", userCount);

        writeJson(resp, Map.of("code", 200, "message", "success", "data", data));
    }

    private boolean isResolved(String status) {
        if (status == null) return false;
        return "closed".equals(status) || "claimed".equals(status) || "completed".equals(status) || "found".equals(status);
    }

    private void writeJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(body));
    }
}
