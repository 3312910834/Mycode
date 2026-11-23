package com.campus.service;

import com.campus.dao.LostItemDao;
import com.campus.dao.LostItemDaoImpl;
import com.campus.entity.LostItem;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 失物信息业务逻辑层
 */
public class LostItemService {
    private LostItemDao lostItemDao = new LostItemDaoImpl();
    
    /**
     * 发布失物信息
     */
    public int publishLostItem(LostItem item) {
        // 设置默认值
        if (item.getStatus() == null || item.getStatus().isEmpty()) {
            item.setStatus("pending"); // 待审核
        }
        if (item.getIsDeleted() == null) {
            item.setIsDeleted(0);
        }
        if (item.getLostTime() == null) {
            item.setLostTime(LocalDateTime.now());
        }
        
        return lostItemDao.insert(item);
    }
    
    /**
     * 获取失物详情
     */
    public LostItem getLostItemDetail(Integer itemId) {
        return lostItemDao.findById(itemId);
    }
    
    /**
     * 获取用户发布的失物列表
     */
    public List<LostItem> getUserLostItems(Integer userId) {
        return lostItemDao.findByUserId(userId);
    }
    
    /**
     * 获取所有失物列表（分页）
     */
    public List<LostItem> getAllLostItems(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return lostItemDao.findAll(offset, pageSize);
    }
    
    /**
     * 搜索失物
     */
    public List<LostItem> searchLostItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllLostItems(1, 20); // 返回第一页
        }
        return lostItemDao.search(keyword.trim());
    }
    
    /**
     * 更新失物信息
     */
    public boolean updateLostItem(LostItem item) {
        int result = lostItemDao.update(item);
        return result > 0;
    }
    
    /**
     * 更新状态（如：已找到）
     */
    public boolean updateStatus(Integer itemId, String status) {
        int result = lostItemDao.updateStatus(itemId, status);
        return result > 0;
    }
    
    /**
     * 删除失物（软删除）
     */
    public boolean deleteLostItem(Integer itemId) {
        int result = lostItemDao.delete(itemId);
        return result > 0;
    }
    
    /**
     * 获取失物数量
     */
    public int getLostItemCount() {
        return lostItemDao.count();
    }
    
    /**
     * 测试方法
     */
    public static void test() {
        LostItemService service = new LostItemService();
        
        System.out.println("=== 测试失物服务 ===");
        
        // 测试发布失物
        LostItem item = new LostItem();
        item.setUserId(8); // 使用您注册的testuser
        item.setTitle("测试丢失物品：钱包");
        item.setLostLocation("教学楼A栋101");
        item.setDescription("黑色钱包，内有身份证和校园卡");
        item.setContactInfo("电话：13888888888");
        item.setCategory("证件类");
        
        int itemId = service.publishLostItem(item);
        System.out.println("发布失物结果: " + (itemId > 0 ? "成功，ID=" + itemId : "失败"));
        
        // 测试查询
        if (itemId > 0) {
            LostItem detail = service.getLostItemDetail(itemId);
            System.out.println("查询失物详情: " + 
                (detail != null ? detail.getTitle() : "失败"));
            
            List<LostItem> userItems = service.getUserLostItems(8);
            System.out.println("用户失物数量: " + userItems.size());
        }
    }
}