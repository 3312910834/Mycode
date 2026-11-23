package com.campus.test;

import com.campus.service.AnnouncementService;
import com.campus.service.UserService;
import com.campus.entity.User;

public class SimpleAnnouncementTest {
    public static void main(String[] args) {
        System.out.println("=== 简单公告模块测试 ===\n");
        
        try {
            // 1. 测试用户服务
            System.out.println("1. 测试用户服务...");
            UserService userService = new UserService();
            
            // 尝试登录（使用您数据库脚本中的测试账户）
            User user = userService.login("admin", "admin123");
            if (user != null) {
                System.out.println("   ✅ 登录成功！用户名: " + user.getUsername());
            } else {
                System.out.println("   ❌ 登录失败，尝试另一个账户...");
                user = userService.login("zhangsan", "user123");
                if (user != null) {
                    System.out.println("   ✅ 登录成功！用户名: " + user.getUsername());
                } else {
                    System.out.println("   ❌ 所有登录尝试都失败");
                }
            }
            
            // 2. 测试公告服务
            System.out.println("\n2. 测试公告服务...");
            AnnouncementService announcementService = new AnnouncementService();
            
            // 测试统计
            int totalCount = announcementService.getAnnouncementCount();
            System.out.println("   总公告数量: " + totalCount);
            
            // 测试查询显示公告
            var visibleAnnouncements = announcementService.getVisibleAnnouncements(1, 10);
            System.out.println("   显示公告数量: " + visibleAnnouncements.size());
            
            if (!visibleAnnouncements.isEmpty()) {
                System.out.println("   第一条公告标题: " + visibleAnnouncements.get(0).getTitle());
            }
            
            // 测试置顶公告
            var topAnnouncements = announcementService.getTopAnnouncements();
            System.out.println("   置顶公告数量: " + topAnnouncements.size());
            
            // 3. 简单功能测试
            System.out.println("\n3. 简单功能测试...");
            
            if (user != null) {
                // 尝试发布一个简单公告
                System.out.println("   尝试发布测试公告...");
                int newId = announcementService.publishAnnouncement(
                    user.getId(),
                    "测试公告 " + System.currentTimeMillis(),
                    "这是一个测试公告"
                );
                
                if (newId > 0) {
                    System.out.println("   ✅ 发布成功！公告ID: " + newId);
                    
                    // 查询刚发布的公告
                    var newAnn = announcementService.getAnnouncementDetail(newId);
                    if (newAnn != null) {
                        System.out.println("   ✅ 查询成功！标题: " + newAnn.getTitle());
                    }
                } else {
                    System.out.println("   ❌ 发布失败");
                }
            }
            
            System.out.println("\n✅ 基本功能测试完成！");
            
        } catch (Exception e) {
            System.out.println("\n❌ 测试出错！");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 4. 给出访问地址
        System.out.println("\n====================================");
        System.out.println("📢 访问地址:");
        System.out.println("公告页面: http://localhost:8080/campus-lost-found/announcement.html");
        System.out.println("公告API: http://localhost:8080/campus-lost-found/announcementapi/visible");
        System.out.println("====================================\n");
    }
}