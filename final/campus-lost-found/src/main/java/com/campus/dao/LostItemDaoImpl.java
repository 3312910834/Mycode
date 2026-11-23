package com.campus.dao;

import com.campus.entity.LostItem;
import com.campus.utils.DBUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LostItemDaoImpl implements LostItemDao {
    
    @Override
    public int insert(LostItem item) {
        // 根据您的表结构修改SQL，字段名完全对应
        String sql = "INSERT INTO lost_item (user_id, title, category, lost_location, lost_time, " +
                    "description, images, contact_info, status, is_deleted) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // 设置参数，顺序与SQL中的字段顺序一致
            pstmt.setInt(1, item.getUserId());
            pstmt.setString(2, item.getTitle());
            pstmt.setString(3, item.getCategory());
            pstmt.setString(4, item.getLostLocation());
            
            // lost_time 是 NOT NULL，必须提供值
            if (item.getLostTime() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(item.getLostTime()));
            } else {
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }
            
            pstmt.setString(6, item.getDescription());
            pstmt.setString(7, item.getImages());
            pstmt.setString(8, item.getContactInfo());
            pstmt.setString(9, item.getStatus() != null ? item.getStatus() : "pending");
            pstmt.setInt(10, item.getIsDeleted() != null ? item.getIsDeleted() : 0);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // 返回生成的ID
                }
            }
            return affectedRows;
            
        } catch (SQLException e) {
            System.err.println("插入失物信息失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    @Override
    public LostItem findById(Integer id) {
        // 使用数据库的实际字段名，注意别名对应Java属性名
        String sql = "SELECT li.*, u.username, u.phone as userPhone " +
                    "FROM lost_item li " +
                    "LEFT JOIN user u ON li.user_id = u.id " +
                    "WHERE li.id = ? AND li.is_deleted = 0";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractLostItem(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询失物信息失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<LostItem> findByUserId(Integer userId) {
        String sql = "SELECT li.*, u.username, u.phone as userPhone " +
                    "FROM lost_item li " +
                    "LEFT JOIN user u ON li.user_id = u.id " +
                    "WHERE li.user_id = ? AND li.is_deleted = 0 " +
                    "ORDER BY li.create_time DESC";
        
        List<LostItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractLostItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询用户失物列表失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
//    @Override
//    public List<LostItem> findAll(int offset, int limit) {
//        String sql = "SELECT li.*, u.username, u.phone as userPhone " +
//                    "FROM lost_item li " +
//                    "LEFT JOIN user u ON li.user_id = u.id " +
//                    "WHERE li.is_deleted = 0 AND li.status = 'published' " +
//                    "ORDER BY li.create_time DESC LIMIT ? OFFSET ?";
//        
//        List<LostItem> list = new ArrayList<>();
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        
//        try {
//            conn = DBUtil.getConnection();
//            pstmt = conn.prepareStatement(sql);
//            pstmt.setInt(1, limit);
//            pstmt.setInt(2, offset);
//            rs = pstmt.executeQuery();
//            
//            while (rs.next()) {
//                list.add(extractLostItem(rs));
//            }
//        } catch (SQLException e) {
//            System.err.println("查询所有失物失败: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            DBUtil.close(conn, pstmt, rs);
//        }
//        return list;
//    }
//    
    @Override
    public List<LostItem> findAll(int offset, int limit) {
        // 删除了 AND li.status = 'published'
        String sql = "SELECT li.*, u.username, u.phone as userPhone " +
                    "FROM lost_item li " +
                    "LEFT JOIN user u ON li.user_id = u.id " +
                    "WHERE li.is_deleted = 0 " +
                    "ORDER BY li.create_time DESC LIMIT ? OFFSET ?";
        
        List<LostItem> list = new ArrayList<>();
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
                list.add(extractLostItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有失物失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<LostItem> findByStatus(String status) {
        String sql = "SELECT li.*, u.username, u.phone as userPhone " +
                    "FROM lost_item li " +
                    "LEFT JOIN user u ON li.user_id = u.id " +
                    "WHERE li.status = ? AND li.is_deleted = 0 " +
                    "ORDER BY li.create_time DESC";
        
        List<LostItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractLostItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("按状态查询失物失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
//    @Override
//    public List<LostItem> search(String keyword) {
//        String sql = "SELECT li.*, u.username, u.phone as userPhone " +
//                    "FROM lost_item li " +
//                    "LEFT JOIN user u ON li.user_id = u.id " +
//                    "WHERE li.is_deleted = 0 AND li.status = 'published' " +
//                    "AND (li.title LIKE ? OR li.description LIKE ? OR li.lost_location LIKE ?) " +
//                    "ORDER BY li.create_time DESC";
//        
//        List<LostItem> list = new ArrayList<>();
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        
//        try {
//            conn = DBUtil.getConnection();
//            pstmt = conn.prepareStatement(sql);
//            String likeKeyword = "%" + keyword + "%";
//            pstmt.setString(1, likeKeyword);
//            pstmt.setString(2, likeKeyword);
//            pstmt.setString(3, likeKeyword);
//            rs = pstmt.executeQuery();
//            
//            while (rs.next()) {
//                list.add(extractLostItem(rs));
//            }
//        } catch (SQLException e) {
//            System.err.println("搜索失物失败: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            DBUtil.close(conn, pstmt, rs);
//        }
//        return list;
//    }
//    
    @Override
    public List<LostItem> search(String keyword) {
        // 删除了 AND li.status = 'published'
        String sql = "SELECT li.*, u.username, u.phone as userPhone " +
                    "FROM lost_item li " +
                    "LEFT JOIN user u ON li.user_id = u.id " +
                    "WHERE li.is_deleted = 0 " +
                    "AND (li.title LIKE ? OR li.description LIKE ? OR li.lost_location LIKE ?) " +
                    "ORDER BY li.create_time DESC";
        
        List<LostItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractLostItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("搜索失物失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public int update(LostItem item) {
        String sql = "UPDATE lost_item SET title = ?, category = ?, lost_location = ?, " +
                    "lost_time = ?, description = ?, images = ?, contact_info = ?, status = ? " +
                    "WHERE id = ? AND is_deleted = 0";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, item.getTitle());
            pstmt.setString(2, item.getCategory());
            pstmt.setString(3, item.getLostLocation());
            
            if (item.getLostTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(item.getLostTime()));
            } else {
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            }
            
            pstmt.setString(5, item.getDescription());
            pstmt.setString(6, item.getImages());
            pstmt.setString(7, item.getContactInfo());
            pstmt.setString(8, item.getStatus());
            pstmt.setInt(9, item.getId());
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新失物信息失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE lost_item SET status = ? WHERE id = ? AND is_deleted = 0";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新状态失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int delete(Integer id) {
        String sql = "UPDATE lost_item SET is_deleted = 1 WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("删除失物失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM lost_item WHERE is_deleted = 0";
        
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
            System.err.println("统计失物数量失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return 0;
    }
    
    /**
     * 从ResultSet中提取LostItem对象
     * 注意：字段名必须与数据库列名一致
     */
    private LostItem extractLostItem(ResultSet rs) throws SQLException {
        LostItem item = new LostItem();
        
        // 直接使用数据库列名
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setTitle(rs.getString("title"));
        item.setCategory(rs.getString("category"));
        item.setLostLocation(rs.getString("lost_location"));
        
        Timestamp lostTime = rs.getTimestamp("lost_time");
        if (lostTime != null) {
            item.setLostTime(lostTime.toLocalDateTime());
        }
        
        item.setDescription(rs.getString("description"));
        item.setImages(rs.getString("images"));
        item.setContactInfo(rs.getString("contact_info"));
        item.setStatus(rs.getString("status"));
        item.setIsDeleted(rs.getInt("is_deleted"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            item.setCreateTime(createTime.toLocalDateTime());
        }
        
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            item.setUpdateTime(updateTime.toLocalDateTime());
        }
        
        // 关联的用户信息（注意：userPhone对应SQL中的别名）
        item.setUsername(rs.getString("username"));
        item.setUserPhone(rs.getString("userPhone")); // 注意：这里是userPhone，不是user_phone
        
        return item;
    }
}