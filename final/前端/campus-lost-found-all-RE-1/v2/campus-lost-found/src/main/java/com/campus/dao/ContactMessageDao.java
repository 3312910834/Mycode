package com.campus.dao;

import com.campus.entity.ContactMessage;
import java.util.List;

/**
 * 联系消息数据访问接口
 */
public interface ContactMessageDao {
    
    /**
     * 插入消息
     */
    int insert(ContactMessage message);
    
    /**
     * 根据ID查询消息
     */
    ContactMessage findById(Integer id);
    
    /**
     * 查询用户收到的消息
     */
    List<ContactMessage> findReceivedMessages(Integer userId);
    
    /**
     * 查询用户发送的消息
     */
    List<ContactMessage> findSentMessages(Integer userId);
    
    /**
     * 查询与特定物品相关的消息
     */
    List<ContactMessage> findByItem(Integer itemId, String itemType);
    
    /**
     * 查询两个用户之间的消息
     */
    List<ContactMessage> findConversation(Integer user1Id, Integer user2Id);
    
    /**
     * 更新消息状态（如标记为已读）
     */
    int updateStatus(Integer id, String status);
    
    /**
     * 删除消息
     */
    int delete(Integer id);
    
    /**
     * 获取用户未读消息数量
     */
    int getUnreadCount(Integer userId);
}