package com.campus.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 物品分类（内存存储，与管理员「分类配置」共用一份数据；首页/发布页可公开读取）。
 */
public final class ItemCategoryStore {

    private static final List<Map<String, Object>> STORE = new CopyOnWriteArrayList<>();

    static {
        resetToDefaults();
    }

    private ItemCategoryStore() {
    }

    public static void resetToDefaults() {
        synchronized (STORE) {
            STORE.clear();
            STORE.add(buildCategory(1, "electronics", "电子产品", "手机、耳机、平板等电子设备"));
            STORE.add(buildCategory(2, "cards", "证件卡片", "校园卡、身份证、银行卡等"));
            STORE.add(buildCategory(3, "books", "书籍资料", "教材、笔记、文件袋等"));
            STORE.add(buildCategory(4, "clothing", "服饰配件", "衣物、眼镜、钥匙、饰品等"));
            STORE.add(buildCategory(5, "study", "学习用品", "书包、文具、资料等"));
            STORE.add(buildCategory(6, "others", "其他物品", "未归入以上类别的物品"));
        }
    }

    private static Map<String, Object> buildCategory(int id, String code, String name, String description) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("code", code);
        m.put("name", name);
        m.put("description", description);
        return m;
    }

    public static List<Map<String, Object>> snapshot() {
        synchronized (STORE) {
            return new ArrayList<>(STORE);
        }
    }

    public static void replaceAll(List<Map<String, Object>> categories) {
        synchronized (STORE) {
            STORE.clear();
            if (categories != null) {
                for (Map<String, Object> c : categories) {
                    if (c == null) {
                        continue;
                    }
                    Map<String, Object> copy = new LinkedHashMap<>(c);
                    if (!copy.containsKey("code") || copy.get("code") == null
                            || String.valueOf(copy.get("code")).isBlank()) {
                        Object name = copy.get("name");
                        copy.put("code", name == null ? "custom" : slug(String.valueOf(name)));
                    }
                    STORE.add(copy);
                }
            }
        }
    }

    private static String slug(String name) {
        String s = name.trim().toLowerCase().replaceAll("\\s+", "_");
        if (s.length() > 48) {
            s = s.substring(0, 48);
        }
        return s.isEmpty() ? "custom" : s;
    }
}
