package com.campus.dao;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计数据访问接口
 */
public interface StatDao {
    
    // 1. 行4：获取近6个月失物与招领对比
    Map<String, Object> getMonthlyItemCompare();
    
    // 2. 行4：获取星期丢失雷达图数据 (周一到周日)
    List<Integer> getLostWeekRadar();
    
    // 3. 行6：获取近30天用户增长趋势
    Map<String, Object> getUserGrowth();
    
    // 4. 行6：获取发布排行榜 TOP10
    List<Map<String, Object>> getTopPublishers();
    
    // 5. 行7：获取近30天站内消息趋势 (ContactMessage)
    Map<String, Object> getMessageTrend();
    
    // 6. 行7：获取建议反馈状态分布 (Suggestion)
    List<Map<String, Object>> getSuggestionStatus();
    
    // 7. 行8：获取物品状态漏斗 (LostItem)
    List<Map<String, Object>> getItemStatus();
    
    // 8. 行8：获取平均找回时长 (小时)
    Double getAvgResolveHours();
}