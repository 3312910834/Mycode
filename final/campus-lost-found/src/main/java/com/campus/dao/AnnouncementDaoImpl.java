package com.campus.dao;

import com.campus.entity.Announcement;
import com.campus.utils.DBUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDaoImpl implements AnnouncementDao {
    
    @Override
    public int insert(Announcement announcement) {
        String sql = "INSERT INTO announcement (admin_id, title, content, is_top, status) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, announcement.getAdminId());
            pstmt.setString(2, announcement.getTitle());
            pstmt.setString(3, announcement.getContent());
            pstmt.setInt(4, announcement.getIsTop() != null ? announcement.getIsTop() : 0);
            pstmt.setInt(5, announcement.getStatus() != null ? announcement.getStatus() : 1);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // 返回生成的ID
                }
            }
            return affectedRows;
            
        } catch (SQLException e) {
            System.err.println("插入公告失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    @Override
    public Announcement findById(Integer id) {
        String sql = "SELECT a.*, u.username as admin_name " +
                    "FROM announcement a " +
                    "LEFT JOIN user u ON a.admin_id = u.id " +
                    "WHERE a.id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAnnouncement(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询公告失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<Announcement> findAll(int offset, int limit) {
        String sql = "SELECT a.*, u.username as admin_name " +
                    "FROM announcement a " +
                    "LEFT JOIN user u ON a.admin_id = u.id " +
                    "ORDER BY a.is_top DESC, a.create_time DESC LIMIT ? OFFSET ?";
        
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractAnnouncement(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有公告失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Announcement> findVisible(int offset, int limit) {
        String sql = "SELECT a.*, u.username as admin_name " +
                    "FROM announcement a " +
                    "LEFT JOIN user u ON a.admin_id = u.id " +
                    "WHERE a.status = 1 " +
                    "ORDER BY a.is_top DESC, a.create_time DESC LIMIT ? OFFSET ?";
        
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractAnnouncement(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询显示公告失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Announcement> findTopAnnouncements() {
        String sql = "SELECT a.*, u.username as admin_name " +
                    "FROM announcement a " +
                    "LEFT JOIN user u ON a.admin_id = u.id " +
                    "WHERE a.status = 1 AND a.is_top = 1 " +
                    "ORDER BY a.create_time DESC";
        
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractAnnouncement(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询置顶公告失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Announcement> findByAdminId(Integer adminId) {
        String sql = "SELECT a.*, u.username as admin_name " +
                    "FROM announcement a " +
                    "LEFT JOIN user u ON a.admin_id = u.id " +
                    "WHERE a.admin_id = ? " +
                    "ORDER BY a.create_time DESC";
        
        List<Announcement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, adminId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractAnnouncement(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询管理员公告失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public int update(Announcement announcement) {
        String sql = "UPDATE announcement SET title = ?, content = ?, is_top = ?, status = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setInt(3, announcement.getIsTop());
            pstmt.setInt(4, announcement.getStatus());
            pstmt.setInt(5, announcement.getId());
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新公告失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int updateStatus(Integer id, Integer status) {
        String sql = "UPDATE announcement SET status = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, status);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新公告状态失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int updateTopStatus(Integer id, Integer isTop) {
        String sql = "UPDATE announcement SET is_top = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, isTop);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新置顶状态失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM announcement WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("删除公告失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM announcement";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("统计公告数量失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return 0;
    }
    
    @Override
    public int countVisible() {
        String sql = "SELECT COUNT(*) FROM announcement WHERE status = 1";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("统计显示公告数量失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return 0;
    }
    
    /**
     * 从ResultSet中提取Announcement对象
     */
    private Announcement extractAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        
        announcement.setId(rs.getInt("id"));
        announcement.setAdminId(rs.getInt("admin_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setIsTop(rs.getInt("is_top"));
        announcement.setStatus(rs.getInt("status"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            announcement.setCreateTime(createTime.toLocalDateTime());
        }
        
        // 关联的管理员信息
        announcement.setAdminName(rs.getString("admin_name"));
        
        return announcement;
    }
}