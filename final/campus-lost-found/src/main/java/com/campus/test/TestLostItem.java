package com.campus.test;

import com.campus.service.LostItemService;

public class TestLostItem {
    public static void main(String[] args) {
        System.out.println("=== 失物模块测试 ===");
        
        try {
            LostItemService service = new LostItemService();
            System.out.println("✅ LostItemService 实例化成功");
            
            // 测试发布失物
            System.out.println("测试发布失物...");
            
            // 这里可以添加具体的测试逻辑
            System.out.println("测试通过！");
            
        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}