package com.campus.service;

import com.campus.dao.StatDao;
import com.campus.dao.StatDaoImpl;
import java.util.HashMap;
import java.util.Map;

public class StatService {
    
    private StatDao statDao = new StatDaoImpl();

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // 行4 图表数据
        data.put("monthlyCompare", statDao.getMonthlyItemCompare());
        data.put("weekRadar", statDao.getLostWeekRadar());
        
        // 行6 图表数据
        data.put("userGrowth", statDao.getUserGrowth());
        data.put("topPublishers", statDao.getTopPublishers());
        
        // 行7 图表数据
        data.put("messageTrend", statDao.getMessageTrend());
        data.put("suggestionStatus", statDao.getSuggestionStatus());
        
        // 行8 图表数据
        data.put("itemStatus", statDao.getItemStatus());
        data.put("avgResolveHours", statDao.getAvgResolveHours());
        
        return data;
    }
}