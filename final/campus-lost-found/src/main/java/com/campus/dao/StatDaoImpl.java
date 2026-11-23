package com.campus.dao;

import com.campus.utils.DBUtil; // 确保引入了你的数据库工具类

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class StatDaoImpl implements StatDao {

    @Override
    public Map<String, Object> getMonthlyItemCompare() {
        Map<String, Object> result = new HashMap<>();
        List<String> months = new ArrayList<>();
        List<Integer> lostCounts = new ArrayList<>();
        List<Integer> foundCounts = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;

        try {
            conn = DBUtil.getConnection();
            
            // 查失物 (近6个月)
            String sqlLost = "SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS count FROM lost_item WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) GROUP BY month ORDER BY month";
            ps1 = conn.prepareStatement(sqlLost);
            rs1 = ps1.executeQuery();
            while (rs1.next()) {
                months.add(rs1.getString("month"));
                lostCounts.add(rs1.getInt("count"));
            }
            
            // 查招领 (近6个月)
            String sqlFound = "SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS count FROM found_item WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) GROUP BY month ORDER BY month";
            ps2 = conn.prepareStatement(sqlFound);
            rs2 = ps2.executeQuery();
            while (rs2.next()) {
                foundCounts.add(rs2.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps2, rs2);
            DBUtil.close(conn, ps1, rs1);
        }
        
        result.put("months", months);
        result.put("lostData", lostCounts);
        result.put("foundData", foundCounts);
        return result;
    }

    @Override
    public List<Integer> getLostWeekRadar() {
        Integer[] weekData = new Integer[]{0, 0, 0, 0, 0, 0, 0};
        String sql = "SELECT DAYOFWEEK(lost_time) AS day_of_week, COUNT(*) AS count FROM lost_item WHERE lost_time IS NOT NULL GROUP BY day_of_week";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int dayOfWeek = rs.getInt("day_of_week"); 
                int count = rs.getInt("count");
                int index = (dayOfWeek + 5) % 7; // MySQL 1是周日，转为前端 0是周一
                weekData[index] = count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return Arrays.asList(weekData);
    }

    @Override
    public Map<String, Object> getUserGrowth() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        String sql = "SELECT DATE_FORMAT(create_time, '%m-%d') AS date, COUNT(*) AS count FROM user WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY date ORDER BY date";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                dates.add(rs.getString("date"));
                counts.add(rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        result.put("dates", dates);
        result.put("counts", counts);
        return result;
    }

    @Override
    public List<Map<String, Object>> getTopPublishers() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT u.username, SUM(combined.post_count) as value " +
                     "FROM user u JOIN (" +
                     "SELECT user_id, COUNT(*) as post_count FROM lost_item WHERE is_deleted = 0 GROUP BY user_id " +
                     "UNION ALL " +
                     "SELECT user_id, COUNT(*) as post_count FROM found_item WHERE is_deleted = 0 GROUP BY user_id" +
                     ") combined ON u.id = combined.user_id " +
                     "GROUP BY u.id ORDER BY value DESC LIMIT 10";
                     
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", rs.getString("username"));
                map.put("value", rs.getInt("value"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    @Override
    public Map<String, Object> getMessageTrend() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        String sql = "SELECT DATE_FORMAT(create_time, '%m-%d') AS date, COUNT(*) AS count FROM contact_message WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY date ORDER BY date";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                dates.add(rs.getString("date"));
                counts.add(rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        result.put("dates", dates);
        result.put("counts", counts);
        return result;
    }

    @Override
    public List<Map<String, Object>> getSuggestionStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) AS value FROM suggestion GROUP BY status";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                String status = rs.getString("status");
                String name = "pending".equals(status) ? "待处理" : ("read".equals(status) ? "已读" : "已处理");
                map.put("name", name);
                map.put("value", rs.getInt("value"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getItemStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) AS value FROM lost_item WHERE is_deleted = 0 GROUP BY status";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                String status = rs.getString("status");
                String name = "pending".equals(status) ? "待审核" : 
                             ("published".equals(status) ? "发布中" : 
                             ("found".equals(status) ? "已找回" : "已关闭"));
                map.put("name", name);
                map.put("value", rs.getInt("value"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    @Override
    public Double getAvgResolveHours() {
        Double avg = 0.0;
        String sql = "SELECT AVG(TIMESTAMPDIFF(HOUR, create_time, update_time)) AS avg_time FROM lost_item WHERE status IN ('found', 'closed') AND update_time IS NOT NULL";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                avg = rs.getDouble("avg_time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return avg != null ? avg : 0.0;
    }
}