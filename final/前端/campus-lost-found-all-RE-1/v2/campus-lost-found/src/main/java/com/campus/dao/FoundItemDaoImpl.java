package com.campus.dao;

import com.campus.entity.FoundItem;
import com.campus.utils.DBUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FoundItemDaoImpl implements FoundItemDao {
    
    @Override
    public int insert(FoundItem item) {
        String sql = "INSERT INTO found_item (user_id, title, category, found_location, found_time, " +
                    "description, images, storage_location, contact_info, status, is_deleted) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, item.getUserId());
            pstmt.setString(2, item.getTitle());
            pstmt.setString(3, item.getCategory());
            pstmt.setString(4, item.getFoundLocation());
            
            // found_time 是 NOT NULL，必须提供值
            if (item.getFoundTime() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(item.getFoundTime()));
            } else {
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }
            
            pstmt.setString(6, item.getDescription());
            pstmt.setString(7, item.getImages());
            pstmt.setString(8, item.getStorageLocation());
            pstmt.setString(9, item.getContactInfo());
            pstmt.setString(10, item.getStatus() != null ? item.getStatus() : "pending");
            pstmt.setInt(11, item.getIsDeleted() != null ? item.getIsDeleted() : 0);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // 返回生成的ID
                }
            }
            return affectedRows;
            
        } catch (SQLException e) {
            System.err.println("插入招领信息失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    @Override
    public FoundItem findById(Integer id) {
        String sql = "SELECT fi.*, u.username, u.phone as userPhone " +
                    "FROM found_item fi " +
                    "LEFT JOIN user u ON fi.user_id = u.id " +
                    "WHERE fi.id = ? AND fi.is_deleted = 0";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFoundItem(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询招领信息失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<FoundItem> findByUserId(Integer userId) {
        String sql = "SELECT fi.*, u.username, u.phone as userPhone " +
                    "FROM found_item fi " +
                    "LEFT JOIN user u ON fi.user_id = u.id " +
                    "WHERE fi.user_id = ? AND fi.is_deleted = 0 " +
                    "ORDER BY fi.create_time DESC";
        
        List<FoundItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractFoundItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询用户招领列表失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<FoundItem> findAll(int offset, int limit) {
        String sql = "SELECT fi.*, u.username, u.phone as userPhone " +
                    "FROM found_item fi " +
                    "LEFT JOIN user u ON fi.user_id = u.id " +
                    "WHERE fi.is_deleted = 0 AND fi.status = 'published' " +
                    "ORDER BY fi.create_time DESC LIMIT ? OFFSET ?";
        
        List<FoundItem> list = new ArrayList<>();
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
                list.add(extractFoundItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有招领失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<FoundItem> findByStatus(String status) {
        String sql = "SELECT fi.*, u.username, u.phone as userPhone " +
                    "FROM found_item fi " +
                    "LEFT JOIN user u ON fi.user_id = u.id " +
                    "WHERE fi.status = ? AND fi.is_deleted = 0 " +
                    "ORDER BY fi.create_time DESC";
        
        List<FoundItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractFoundItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("按状态查询招领失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<FoundItem> search(String keyword) {
        String sql = "SELECT fi.*, u.username, u.phone as userPhone " +
                    "FROM found_item fi " +
                    "LEFT JOIN user u ON fi.user_id = u.id " +
                    "WHERE fi.is_deleted = 0 AND fi.status = 'published' " +
                    "AND (fi.title LIKE ? OR fi.description LIKE ? OR fi.found_location LIKE ?) " +
                    "ORDER BY fi.create_time DESC";
        
        List<FoundItem> list = new ArrayList<>();
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
                list.add(extractFoundItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("搜索招领失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public int update(FoundItem item) {
        String sql = "UPDATE found_item SET title = ?, category = ?, found_location = ?, " +
                    "found_time = ?, description = ?, images = ?, storage_location = ?, " +
                    "contact_info = ?, status = ? WHERE id = ? AND is_deleted = 0";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, item.getTitle());
            pstmt.setString(2, item.getCategory());
            pstmt.setString(3, item.getFoundLocation());
            
            if (item.getFoundTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(item.getFoundTime()));
            } else {
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            }
            
            pstmt.setString(5, item.getDescription());
            pstmt.setString(6, item.getImages());
            pstmt.setString(7, item.getStorageLocation());
            pstmt.setString(8, item.getContactInfo());
            pstmt.setString(9, item.getStatus());
            pstmt.setInt(10, item.getId());
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新招领信息失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE found_item SET status = ? WHERE id = ? AND is_deleted = 0";
        
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
        String sql = "UPDATE found_item SET is_deleted = 1 WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("删除招领失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM found_item WHERE is_deleted = 0";
        
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
            System.err.println("统计招领数量失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }
        return 0;
    }
    
    /**
     * 从ResultSet中提取FoundItem对象
     */
    private FoundItem extractFoundItem(ResultSet rs) throws SQLException {
        FoundItem item = new FoundItem();
        
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setTitle(rs.getString("title"));
        item.setCategory(rs.getString("category"));
        item.setFoundLocation(rs.getString("found_location"));
        
        Timestamp foundTime = rs.getTimestamp("found_time");
        if (foundTime != null) {
            item.setFoundTime(foundTime.toLocalDateTime());
        }
        
        item.setDescription(rs.getString("description"));
        item.setImages(rs.getString("images"));
        item.setStorageLocation(rs.getString("storage_location"));
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
        
        // 关联的用户信息
        item.setUsername(rs.getString("username"));
        item.setUserPhone(rs.getString("userPhone"));
        
        return item;
    }
    @Override
    public List<String> selectAllFoundText() {
        List<String> textList = new ArrayList<>();
        // SQL：拼接 标题+分类+描述，过滤已删除数据
        String sql = "SELECT CONCAT(title, '，', category, '，', description) AS info FROM found_item WHERE is_deleted = 0";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                textList.add(rs.getString("info"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return textList;
    }
}