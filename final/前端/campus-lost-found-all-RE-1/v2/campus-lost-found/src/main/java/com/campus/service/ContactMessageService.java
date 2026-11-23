package com.campus.service;

import com.campus.dao.ContactMessageDao;
import com.campus.dao.ContactMessageDaoImpl;
import com.campus.entity.ContactMessage;
import java.util.List;

/**
 * 联系消息业务逻辑层
 */
public class ContactMessageService {
    private ContactMessageDao messageDao = new ContactMessageDaoImpl();
    
    /**
     * 发送消息
     */
    public int sendMessage(ContactMessage message) {
        // 设置默认值
        if (message.getStatus() == null || message.getStatus().isEmpty()) {
            message.setStatus("unread");
        }
        
        return messageDao.insert(message);
    }
    
    /**
     * 获取消息详情
     */
    public ContactMessage getMessageDetail(Integer messageId) {
        return messageDao.findById(messageId);
    }
    
    /**
     * 获取用户收到的消息
     */
    public List<ContactMessage> getReceivedMessages(Integer userId) {
        return messageDao.findReceivedMessages(userId);
    }
    
    /**
     * 获取用户发送的消息
     */
    public List<ContactMessage> getSentMessages(Integer userId) {
        return messageDao.findSentMessages(userId);
    }
    
    /**
     * 获取与物品相关的消息
     */
    public List<ContactMessage> getItemMessages(Integer itemId, String itemType) {
        return messageDao.findByItem(itemId, itemType);
    }
    
    /**
     * 获取两个用户之间的对话
     */
    public List<ContactMessage> getConversation(Integer user1Id, Integer user2Id) {
        return messageDao.findConversation(user1Id, user2Id);
    }
    
    /**
     * 标记消息为已读
     */
    public boolean markAsRead(Integer messageId) {
        int result = messageDao.updateStatus(messageId, "read");
        return result > 0;
    }
    
    /**
     * 删除消息
     */
    public boolean deleteMessage(Integer messageId) {
        int result = messageDao.delete(messageId);
        return result > 0;
    }
    
    /**
     * 获取用户未读消息数量
     */
    public int getUnreadCount(Integer userId) {
        return messageDao.getUnreadCount(userId);
    }
    
    /**
     * 发送联系消息（简化方法）
     */
    public int sendContactMessage(Integer fromUserId, Integer toUserId, 
                                 Integer itemId, String itemType, String content) {
        ContactMessage message = new ContactMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setItemId(itemId);
        message.setItemType(itemType);
        message.setContent(content);
        
        return sendMessage(message);
    }
    
    /**
     * 测试方法
     */
    public static void test() {
        ContactMessageService service = new ContactMessageService();
        
        System.out.println("=== 测试消息服务 ===");
        
        // 测试发送消息
        int messageId = service.sendContactMessage(8, 3, 1, "lost", "您好，我可能看到您的钱包了");
        System.out.println("发送消息结果: " + (messageId > 0 ? "成功，ID=" + messageId : "失败"));
        
        // 测试查询
        if (messageId > 0) {
            ContactMessage detail = service.getMessageDetail(messageId);
            System.out.println("查询消息详情: " + 
                (detail != null ? detail.getContent() : "失败"));
            
            List<ContactMessage> received = service.getReceivedMessages(3);
            System.out.println("用户3收到的消息数量: " + received.size());
            
            int unreadCount = service.getUnreadCount(3);
            System.out.println("用户3未读消息数量: " + unreadCount);
        }
    }
}