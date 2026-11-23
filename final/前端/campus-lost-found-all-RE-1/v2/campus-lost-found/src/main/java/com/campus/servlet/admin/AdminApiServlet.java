package com.campus.servlet.admin;

import com.campus.config.ItemCategoryStore;
import com.campus.entity.FoundItem;
import com.campus.entity.LostItem;
import com.campus.service.FoundItemService;
import com.campus.service.LostItemService;
import com.campus.service.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/adminapi/*")
public class AdminApiServlet extends HttpServlet {
    private final LostItemService lostItemService = new LostItemService();
    private final FoundItemService foundItemService = new FoundItemService();
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, Object> SETTINGS_STORE = new LinkedHashMap<>();

    static {
        SETTINGS_STORE.put("systemName", "校园失物招领系统");
        SETTINGS_STORE.put("systemDescription", "为校园社区提供失物招领服务的平台");
        SETTINGS_STORE.put("contactEmail", "admin@university.edu");
        SETTINGS_STORE.put("contactPhone", "400-123-4567");
        SETTINGS_STORE.put("allowRegistration", true);
        SETTINGS_STORE.put("requireApproval", true);
        SETTINGS_STORE.put("allowImageUpload", true);
        SETTINGS_STORE.put("sendNotificationEmails", false);
        SETTINGS_STORE.put("minPasswordLength", 8);
        SETTINGS_STORE.put("maxLoginAttempts", 5);
        SETTINGS_STORE.put("sessionTimeout", 120);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = getRemainingPath(req);
        if ("/items".equals(path)) {
            handleGetItems(req, resp);
        } else if ("/stats".equals(path)) {
            handleGetStats(resp);
        } else if ("/categories".equals(path)) {
            writeJson(resp, success(ItemCategoryStore.snapshot()));
        } else if ("/settings".equals(path)) {
            writeJson(resp, success(SETTINGS_STORE));
        } else {
            writeJson(resp, error(404, "接口不存在: " + path));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = getRemainingPath(req);
        if ("/item/status".equals(path)) {
            handleUpdateItemStatus(req, resp);
        } else if ("/item/delete".equals(path)) {
            handleDeleteItem(req, resp);
        } else if ("/categories".equals(path)) {
            handleUpdateCategories(req, resp);
        } else if ("/settings".equals(path)) {
            handleUpdateSettings(req, resp);
        } else {
            writeJson(resp, error(404, "接口不存在: " + path));
        }
    }

    private void handleGetItems(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), 20);
        String typeFilter = safe(req.getParameter("type"));
        String statusFilter = safe(req.getParameter("status"));
        String keyword = safe(req.getParameter("keyword"));

        int lostCount = Math.max(lostItemService.getLostItemCount(), 1);
        int foundCount = Math.max(foundItemService.getFoundItemCount(), 1);

        List<LostItem> lostItems = lostItemService.getAllLostItems(1, lostCount);
        List<FoundItem> foundItems = foundItemService.getAllFoundItems(1, foundCount);

        List<Map<String, Object>> merged = new ArrayList<>();
        for (LostItem item : lostItems) {
            merged.add(toAdminItem(item));
        }
        for (FoundItem item : foundItems) {
            merged.add(toAdminItem(item));
        }

        merged = merged.stream()
                .filter(item -> matchType(item, typeFilter))
                .filter(item -> matchStatus(item, statusFilter))
                .filter(item -> matchKeyword(item, keyword))
                .collect(Collectors.toList());

        merged.sort(Comparator.comparing(
                m -> String.valueOf(m.getOrDefault("date", "")),
                Comparator.reverseOrder()
        ));

        int total = merged.size();
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, total);
        List<Map<String, Object>> items = from >= total ? new ArrayList<>() : merged.subList(from, to);

        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);

        writeJson(resp, success(data));
    }

    private void handleGetStats(HttpServletResponse resp) throws IOException {
        int lostCount = Math.max(lostItemService.getLostItemCount(), 1);
        int foundCount = Math.max(foundItemService.getFoundItemCount(), 1);
        List<LostItem> lostItems = lostItemService.getAllLostItems(1, lostCount);
        List<FoundItem> foundItems = foundItemService.getAllFoundItems(1, foundCount);

        int pending = 0;
        int resolved = 0;
        for (LostItem item : lostItems) {
            String s = toAdminStatus(item.getStatus());
            if ("pending".equals(s)) pending++;
            if ("resolved".equals(s)) resolved++;
        }
        for (FoundItem item : foundItems) {
            String s = toAdminStatus(item.getStatus());
            if ("pending".equals(s)) pending++;
            if ("resolved".equals(s)) resolved++;
        }

        Map<String, Object> userPage = userService.getUserPage(1, 1, null, null, null);
        Object totalUsers = userPage.getOrDefault("total", 0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItems", lostItems.size() + foundItems.size());
        stats.put("pendingItems", pending);
        stats.put("resolvedItems", resolved);
        stats.put("totalUsers", totalUsers);

        writeJson(resp, success(stats));
    }

    private void handleUpdateItemStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        Integer id = parseInt(body.get("id"));
        String type = asString(body.get("type"));
        String status = asString(body.get("status"));

        if (id == null || status == null || status.isBlank()) {
            writeJson(resp, error(400, "缺少必要参数: id/status"));
            return;
        }

        String targetStatus = toPersistenceStatus(status);
        boolean success;
        if ("found".equals(type)) {
            success = foundItemService.updateStatus(id, targetStatus);
        } else if ("lost".equals(type)) {
            success = lostItemService.updateStatus(id, targetStatus);
        } else {
            success = lostItemService.updateStatus(id, targetStatus) || foundItemService.updateStatus(id, targetStatus);
        }

        if (success) {
            writeJson(resp, success(null));
        } else {
            writeJson(resp, error(500, "更新状态失败，记录不存在或状态未变化"));
        }
    }

    private void handleDeleteItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        Integer id = parseInt(body.get("id"));
        String type = asString(body.get("type"));

        if (id == null) {
            writeJson(resp, error(400, "缺少必要参数: id"));
            return;
        }

        boolean success;
        if ("found".equals(type)) {
            success = foundItemService.deleteFoundItem(id);
        } else if ("lost".equals(type)) {
            success = lostItemService.deleteLostItem(id);
        } else {
            success = lostItemService.deleteLostItem(id) || foundItemService.deleteFoundItem(id);
        }

        if (success) {
            writeJson(resp, success(null));
        } else {
            writeJson(resp, error(500, "删除失败，记录不存在"));
        }
    }

    private void handleUpdateCategories(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        Object categoriesObj = body.get("categories");
        if (categoriesObj == null) {
            writeJson(resp, error(400, "缺少 categories 参数"));
            return;
        }

        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> categories = gson.fromJson(gson.toJson(categoriesObj), listType);

        ItemCategoryStore.replaceAll(categories);
        writeJson(resp, success(ItemCategoryStore.snapshot()));
    }

    private void handleUpdateSettings(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = parseBody(req);
        if (body == null || body.isEmpty()) {
            writeJson(resp, error(400, "请求体不能为空"));
            return;
        }
        synchronized (SETTINGS_STORE) {
            SETTINGS_STORE.putAll(body);
        }
        writeJson(resp, success(SETTINGS_STORE));
    }

    private Map<String, Object> toAdminItem(LostItem item) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", item.getId());
        result.put("type", "lost");
        result.put("title", item.getTitle() == null ? "" : item.getTitle());
        result.put("category", item.getCategory() == null ? "" : item.getCategory());
        result.put("location", item.getLostLocation() == null ? "" : item.getLostLocation());
        result.put("poster", item.getUsername() == null ? "" : item.getUsername());
        result.put("date", item.getCreateTime() != null ? item.getCreateTime().format(formatter) : "");
        result.put("status", toAdminStatus(item.getStatus()));
        result.put("image", item.getImages());
        return result;
    }

    private Map<String, Object> toAdminItem(FoundItem item) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", item.getId());
        result.put("type", "found");
        result.put("title", item.getTitle() == null ? "" : item.getTitle());
        result.put("category", item.getCategory() == null ? "" : item.getCategory());
        result.put("location", item.getFoundLocation() == null ? "" : item.getFoundLocation());
        result.put("poster", item.getUsername() == null ? "" : item.getUsername());
        result.put("date", item.getCreateTime() != null ? item.getCreateTime().format(formatter) : "");
        result.put("status", toAdminStatus(item.getStatus()));
        result.put("image", item.getImages());
        return result;
    }

    private String toAdminStatus(String rawStatus) {
        if (rawStatus == null) return "pending";
        switch (rawStatus) {
            case "published":
                return "approved";
            case "claimed":
            case "completed":
            case "closed":
            case "found":
                return "resolved";
            default:
                return rawStatus;
        }
    }

    private String toPersistenceStatus(String adminStatus) {
        if (adminStatus == null) return "pending";
        switch (adminStatus) {
            case "approved":
                return "published";
            case "resolved":
            case "rejected":
                return "closed";
            default:
                return adminStatus;
        }
    }

    private Map<String, Object> parseBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString().trim();
        if (json.isEmpty()) return new HashMap<>();
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> body = gson.fromJson(json, mapType);
        return body != null ? body : new HashMap<>();
    }

    private String getRemainingPath(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String path = requestURI.substring(contextPath.length() + servletPath.length());
        return (path == null || path.isEmpty()) ? "/" : path;
    }

    private Integer parseInt(Object value) {
        if (value == null) return null;
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean matchType(Map<String, Object> item, String type) {
        if (type.isEmpty() || "all".equals(type)) return true;
        return type.equals(String.valueOf(item.getOrDefault("type", "")));
    }

    private boolean matchStatus(Map<String, Object> item, String status) {
        if (status.isEmpty() || "all".equals(status)) return true;
        return status.equals(String.valueOf(item.getOrDefault("status", "")));
    }

    private boolean matchKeyword(Map<String, Object> item, String keyword) {
        if (keyword.isEmpty()) return true;
        String k = keyword.toLowerCase();
        return String.valueOf(item.getOrDefault("title", "")).toLowerCase().contains(k)
                || String.valueOf(item.getOrDefault("location", "")).toLowerCase().contains(k)
                || String.valueOf(item.getOrDefault("poster", "")).toLowerCase().contains(k);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    private Map<String, Object> error(int code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        return result;
    }

    private void writeJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(body));
    }
}
