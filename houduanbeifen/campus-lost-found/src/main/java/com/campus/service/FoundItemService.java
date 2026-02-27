package com.campus.service;

import com.campus.dao.FoundItemDao;
import com.campus.dao.FoundItemDaoImpl;
import com.campus.entity.FoundItem;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 招领信息业务逻辑层
 */
public class FoundItemService {
    private FoundItemDao foundItemDao = new FoundItemDaoImpl();
    
    /**
     * 发布招领信息
     */
    public int publishFoundItem(FoundItem item) {
        // 设置默认值
        if (item.getStatus() == null || item.getStatus().isEmpty()) {
            item.setStatus("pending"); // 待审核
        }
        if (item.getIsDeleted() == null) {
            item.setIsDeleted(0);
        }
        if (item.getFoundTime() == null) {
            item.setFoundTime(LocalDateTime.now());
        }
        
        return foundItemDao.insert(item);
    }
    
    /**
     * 获取招领详情
     */
    public FoundItem getFoundItemDetail(Integer itemId) {
        return foundItemDao.findById(itemId);
    }
    
    /**
     * 获取用户发布的招领列表
     */
    public List<FoundItem> getUserFoundItems(Integer userId) {
        return foundItemDao.findByUserId(userId);
    }
    
    /**
     * 获取所有招领列表（分页）
     */
    public List<FoundItem> getAllFoundItems(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return foundItemDao.findAll(offset, pageSize);
    }
    
    /**
     * 搜索招领
     */
    public List<FoundItem> searchFoundItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllFoundItems(1, 20); // 返回第一页
        }
        return foundItemDao.search(keyword.trim());
    }
    
    /**
     * 更新招领信息
     */
    public boolean updateFoundItem(FoundItem item) {
        int result = foundItemDao.update(item);
        return result > 0;
    }
    
    /**
     * 更新状态（如：已认领）
     */
    public boolean updateStatus(Integer itemId, String status) {
        int result = foundItemDao.updateStatus(itemId, status);
        return result > 0;
    }
    
    /**
     * 删除招领（软删除）
     */
    public boolean deleteFoundItem(Integer itemId) {
        int result = foundItemDao.delete(itemId);
        return result > 0;
    }
    
    /**
     * 获取招领数量
     */
    public int getFoundItemCount() {
        return foundItemDao.count();
    }
    
    /**
     * 测试方法
     */
    public static void test() {
        FoundItemService service = new FoundItemService();
        
        System.out.println("=== 测试招领服务 ===");
        
        // 测试发布招领
        FoundItem item = new FoundItem();
        item.setUserId(8); // 使用您注册的testuser
        item.setTitle("拾获：校园卡");
        item.setFoundLocation("教学楼B栋门口");
        item.setDescription("捡到一张校园卡，姓名：张三");
        item.setContactInfo("电话：13888888888");
        item.setCategory("证件类");
        item.setStorageLocation("失物招领处");
        
        int itemId = service.publishFoundItem(item);
        System.out.println("发布招领结果: " + (itemId > 0 ? "成功，ID=" + itemId : "失败"));
        
        // 测试查询
        if (itemId > 0) {
            FoundItem detail = service.getFoundItemDetail(itemId);
            System.out.println("查询招领详情: " + 
                (detail != null ? detail.getTitle() : "失败"));
            
            List<FoundItem> userItems = service.getUserFoundItems(8);
            System.out.println("用户招领数量: " + userItems.size());
        }
    }
}