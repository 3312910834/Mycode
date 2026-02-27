package com.campus.dao;

import com.campus.entity.ContactMessage;
import com.campus.utils.DBUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContactMessageDaoImpl implements ContactMessageDao {
    
    @Override
    public int insert(ContactMessage message) {
        String sql = "INSERT INTO contact_message (from_user_id, to_user_id, item_id, item_type, content, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, message.getFromUserId());
            pstmt.setInt(2, message.getToUserId());
            pstmt.setInt(3, message.getItemId());
            pstmt.setString(4, message.getItemType());
            pstmt.setString(5, message.getContent());
            pstmt.setString(6, message.getStatus() != null ? message.getStatus() : "unread");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // 返回生成的ID
                }
            }
            return affectedRows;
            
        } catch (SQLException e) {
            System.err.println("插入消息失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    @Override
    public ContactMessage findById(Integer id) {
        String sql = "SELECT cm.*, fu.username as from_username, tu.username as to_username, " +
                    "CASE WHEN cm.item_type = 'lost' THEN li.title ELSE fi.title END as item_title " +
                    "FROM contact_message cm " +
                    "LEFT JOIN user fu ON cm.from_user_id = fu.id " +
                    "LEFT JOIN user tu ON cm.to_user_id = tu.id " +
                    "LEFT JOIN lost_item li ON cm.item_type = 'lost' AND cm.item_id = li.id " +
                    "LEFT JOIN found_item fi ON cm.item_type = 'found' AND cm.item_id = fi.id " +
                    "WHERE cm.id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractContactMessage(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询消息失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<ContactMessage> findReceivedMessages(Integer userId) {
        String sql = "SELECT cm.*, fu.username as from_username, tu.username as to_username, " +
                    "CASE WHEN cm.item_type = 'lost' THEN li.title ELSE fi.title END as item_title " +
                    "FROM contact_message cm " +
                    "LEFT JOIN user fu ON cm.from_user_id = fu.id " +
                    "LEFT JOIN user tu ON cm.to_user_id = tu.id " +
                    "LEFT JOIN lost_item li ON cm.item_type = 'lost' AND cm.item_id = li.id " +
                    "LEFT JOIN found_item fi ON cm.item_type = 'found' AND cm.item_id = fi.id " +
                    "WHERE cm.to_user_id = ? " +
                    "ORDER BY cm.create_time DESC";
        
        List<ContactMessage> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractContactMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询收到的消息失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<ContactMessage> findSentMessages(Integer userId) {
        String sql = "SELECT cm.*, fu.username as from_username, tu.username as to_username, " +
                    "CASE WHEN cm.item_type = 'lost' THEN li.title ELSE fi.title END as item_title " +
                    "FROM contact_message cm " +
                    "LEFT JOIN user fu ON cm.from_user_id = fu.id " +
                    "LEFT JOIN user tu ON cm.to_user_id = tu.id " +
                    "LEFT JOIN lost_item li ON cm.item_type = 'lost' AND cm.item_id = li.id " +
                    "LEFT JOIN found_item fi ON cm.item_type = 'found' AND cm.item_id = fi.id " +
                    "WHERE cm.from_user_id = ? " +
                    "ORDER BY cm.create_time DESC";
        
        List<ContactMessage> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractContactMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询发送的消息失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<ContactMessage> findByItem(Integer itemId, String itemType) {
        String sql = "SELECT cm.*, fu.username as from_username, tu.username as to_username, " +
                    "CASE WHEN cm.item_type = 'lost' THEN li.title ELSE fi.title END as item_title " +
                    "FROM contact_message cm " +
                    "LEFT JOIN user fu ON cm.from_user_id = fu.id " +
                    "LEFT JOIN user tu ON cm.to_user_id = tu.id " +
                    "LEFT JOIN lost_item li ON cm.item_type = 'lost' AND cm.item_id = li.id " +
                    "LEFT JOIN found_item fi ON cm.item_type = 'found' AND cm.item_id = fi.id " +
                    "WHERE cm.item_id = ? AND cm.item_type = ? " +
                    "ORDER BY cm.create_time ASC";
        
        List<ContactMessage> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            pstmt.setString(2, itemType);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractContactMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询物品相关消息失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<ContactMessage> findConversation(Integer user1Id, Integer user2Id) {
        String sql = "SELECT cm.*, fu.username as from_username, tu.username as to_username, " +
                    "CASE WHEN cm.item_type = 'lost' THEN li.title ELSE fi.title END as item_title " +
                    "FROM contact_message cm " +
                    "LEFT JOIN user fu ON cm.from_user_id = fu.id " +
                    "LEFT JOIN user tu ON cm.to_user_id = tu.id " +
                    "LEFT JOIN lost_item li ON cm.item_type = 'lost' AND cm.item_id = li.id " +
                    "LEFT JOIN found_item fi ON cm.item_type = 'found' AND cm.item_id = fi.id " +
                    "WHERE (cm.from_user_id = ? AND cm.to_user_id = ?) " +
                    "   OR (cm.from_user_id = ? AND cm.to_user_id = ?) " +
                    "ORDER BY cm.create_time ASC";
        
        List<ContactMessage> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);
            pstmt.setInt(3, user2Id);
            pstmt.setInt(4, user1Id);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(extractContactMessage(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询对话失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE contact_message SET status = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("更新消息状态失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM contact_message WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("删除消息失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    @Override
    public int getUnreadCount(Integer userId) {
        String sql = "SELECT COUNT(*) FROM contact_message WHERE to_user_id = ? AND status = 'unread'";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("统计未读消息失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }
    
    /**
     * 从ResultSet中提取ContactMessage对象
     */
    private ContactMessage extractContactMessage(ResultSet rs) throws SQLException {
        ContactMessage message = new ContactMessage();
        
        message.setId(rs.getInt("id"));
        message.setFromUserId(rs.getInt("from_user_id"));
        message.setToUserId(rs.getInt("to_user_id"));
        message.setItemId(rs.getInt("item_id"));
        message.setItemType(rs.getString("item_type"));
        message.setContent(rs.getString("content"));
        message.setStatus(rs.getString("status"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            message.setCreateTime(createTime.toLocalDateTime());
        }
        
        // 关联信息
        message.setFromUserName(rs.getString("from_username"));
        message.setToUserName(rs.getString("to_username"));
        message.setItemTitle(rs.getString("item_title"));
        
        return message;
    }
}