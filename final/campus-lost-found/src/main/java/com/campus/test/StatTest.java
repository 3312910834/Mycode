package com.campus.test; // 根据你实际存放的位置修改包名

import com.campus.service.StatService;
import java.util.Map;

/**
 * 仪表盘统计数据后端独立测试类
 */
public class StatTest {
    
    public static void main(String[] args) {
        System.out.println("========== 🚀 开始测试仪表盘统计数据 ==========");
        
        try {
            // 1. 实例化我们刚写好的 Service
            StatService statService = new StatService();
            
            // 2. 调用聚合方法获取所有数据
            Map<String, Object> dashboardData = statService.getDashboardData();
            
            System.out.println("✅ 数据库查询成功！以下是获取到的各个图表数据：\n");
            
            // 3. 遍历并漂亮地打印出来
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                System.out.println("▶ 【" + key + "】:");
                System.out.println("   " + value);
                System.out.println("--------------------------------------------------");
            }
            
            System.out.println("========== 🎉 测试全部通过 ==========");
            
        } catch (Exception e) {
            System.err.println("❌ 测试过程中发生异常！");
            e.printStackTrace();
        }
    }
}