package com.campus.dao;

import com.campus.entity.Suggestion;
import com.campus.utils.DBUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SuggestionDaoImpl implements SuggestionDao {
    
    @Override
    public int insert(Suggestion suggestion) {
        String sql = "INSERT INTO suggestion (user_id, content, status) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, suggestion.getUserId());
            pstmt.setString(2, suggestion.getContent());
            pstmt.setString(3, suggestion.getStatus() != null ? suggestion.getStatus() : "pending");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // 返回生成的ID
                }
            }
            return affectedRows;
            
        } catch (SQLException e) {
            System.err.println("插入建议失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    @Override
    public Suggestion findById(Integer id) {
        String sql = "SELECT s.*, u.username " +
                    "FROM suggestion s " +
                    "LEFT JOIN user u ON s.user_id = u.id " +
                    "WHERE s.id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractSuggestion(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询建议失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<Suggestion> findByUserId(Integer userId) {
        String sql = "SELECT s.*, u.username " +
                    "FROM suggestion s " +
                    "LEFT JOIN user u ON s.user_id = u.id " +
                    "WHERE s.user_id = ? " +
                    "ORDER BY s.create_time DESC";
        
        List<Suggestion> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractSuggestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询用户建议失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Suggestion> findAll(int offset, int limit) {
        String sql = "SELECT s.*, u.username " +
                    "FROM suggestion s " +
                    "LEFT JOIN user u ON s.user_id = u.id " +
                    "ORDER BY s.create_time DESC LIMIT ? OFFSET ?";
        
        List<Suggestion> list = new ArrayList<>();
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
                list.add(extractSuggestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有建议失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Suggestion> findByStatus(String status) {
        String sql = "SELECT s.*, u.username " +
                    "FROM suggestion s " +
                    "LEFT JOIN user u ON s.user_id = u.id " +
                    "WHERE s.status = ? " +
                    "ORDER BY s.create_time DESC";
        
        List<Suggestion> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractSuggestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("按状态查询建议失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public int update(Suggestion suggestion) {
        String sql = "UPDATE suggestion SET content = ?, status = ?, reply = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, suggestion.getContent());
            pstmt.setString(2, suggestion.getStatus());
            pstmt.setString(3, suggestion.getReply());
            pstmt.setInt(4, suggestion.getId());
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新建议失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE suggestion SET status = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新建议状态失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int addReply(Integer id, String reply) {
        String sql = "UPDATE suggestion SET reply = ?, status = 'processed' WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reply);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("添加回复失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM suggestion WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("删除建议失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM suggestion";
        
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
            System.err.println("统计建议数量失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return 0;
    }
    
    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM suggestion WHERE status = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("按状态统计建议失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }
    
    /**
     * 从ResultSet中提取Suggestion对象
     */
    private Suggestion extractSuggestion(ResultSet rs) throws SQLException {
        Suggestion suggestion = new Suggestion();
        
        suggestion.setId(rs.getInt("id"));
        suggestion.setUserId(rs.getInt("user_id"));
        suggestion.setContent(rs.getString("content"));
        suggestion.setStatus(rs.getString("status"));
        suggestion.setReply(rs.getString("reply"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            suggestion.setCreateTime(createTime.toLocalDateTime());
        }
        
        // 关联的用户信息
        suggestion.setUsername(rs.getString("username"));
        
        return suggestion;
    }
}