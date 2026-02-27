package com.campus.dao;

import com.campus.entity.User;
import com.campus.utils.DBUtil;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * 用户数据访问实现类
 */
public class UserDaoImpl implements UserDao {
    
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public User findById(Integer id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public int insert(User user) {
        String sql = "INSERT INTO user (username, password, real_name, student_id, phone, email, avatar, role, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRealName());
            pstmt.setString(4, user.getStudentId());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getAvatar());
            pstmt.setString(8, user.getRole());
            pstmt.setInt(9, user.getStatus() != null ? user.getStatus() : 1);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // 返回生成的ID
                }
            }
            return affectedRows;
            
        } catch (SQLException e) {
            System.err.println("插入用户失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    @Override
    public int update(User user) {
        String sql = "UPDATE user SET real_name = ?, student_id = ?, phone = ?, email = ?, avatar = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getRealName());
            pstmt.setString(2, user.getStudentId());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getAvatar());
            pstmt.setInt(6, user.getId());
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新用户失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ? AND status = 1";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("登录验证失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    /**
     * 从ResultSet中提取用户对象
     */
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setStudentId(rs.getString("student_id"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setAvatar(rs.getString("avatar"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getInt("status"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            user.setCreateTime(createTime.toLocalDateTime());
        }
        
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            user.setUpdateTime(updateTime.toLocalDateTime());
        }
        
        return user;
    }
}