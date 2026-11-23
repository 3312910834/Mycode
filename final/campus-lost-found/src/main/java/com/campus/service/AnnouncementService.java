package com.campus.service;

import com.campus.dao.AnnouncementDao;
import com.campus.dao.AnnouncementDaoImpl;
import com.campus.entity.Announcement;
import java.util.List;

/**
 * 公告业务逻辑层
 */
public class AnnouncementService {
    private AnnouncementDao announcementDao = new AnnouncementDaoImpl();
    
    /**
     * 发布公告
     */
    public int publishAnnouncement(Announcement announcement) {
        // 设置默认值
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(0);
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(1); // 默认显示
        }
        
        return announcementDao.insert(announcement);
    }
    
    /**
     * 获取公告详情
     */
    public Announcement getAnnouncementDetail(Integer announcementId) {
        return announcementDao.findById(announcementId);
    }
    
    /**
     * 获取所有公告（分页）
     */
    public List<Announcement> getAllAnnouncements(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return announcementDao.findAll(offset, pageSize);
    }
    
    /**
     * 获取显示的公告（分页）
     */
    public List<Announcement> getVisibleAnnouncements(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return announcementDao.findVisible(offset, pageSize);
    }
    
    /**
     * 获取置顶公告
     */
    public List<Announcement> getTopAnnouncements() {
        return announcementDao.findTopAnnouncements();
    }
    
    /**
     * 获取管理员发布的公告
     */
    public List<Announcement> getAdminAnnouncements(Integer adminId) {
        return announcementDao.findByAdminId(adminId);
    }
    
    /**
     * 更新公告
     */
    public boolean updateAnnouncement(Announcement announcement) {
        int result = announcementDao.update(announcement);
        return result > 0;
    }
    
    /**
     * 更新公告状态
     */
    public boolean updateStatus(Integer announcementId, Integer status) {
        int result = announcementDao.updateStatus(announcementId, status);
        return result > 0;
    }
    
    /**
     * 更新置顶状态
     */
    public boolean updateTopStatus(Integer announcementId, Integer isTop) {
        int result = announcementDao.updateTopStatus(announcementId, isTop);
        return result > 0;
    }
    
    /**
     * 删除公告
     */
    public boolean deleteAnnouncement(Integer announcementId) {
        int result = announcementDao.delete(announcementId);
        return result > 0;
    }
    
    /**
     * 获取公告数量
     */
    public int getAnnouncementCount() {
        return announcementDao.count();
    }
    
    /**
     * 获取显示的公告数量
     */
    public int getVisibleAnnouncementCount() {
        return announcementDao.countVisible();
    }
    
    /**
     * 发布公告（简化方法）
     */
    public int publishAnnouncement(Integer adminId, String title, String content) {
        Announcement announcement = new Announcement();
        announcement.setAdminId(adminId);
        announcement.setTitle(title);
        announcement.setContent(content);
        
        return publishAnnouncement(announcement);
    }
    
    /**
     * 发布置顶公告
     */
    public int publishTopAnnouncement(Integer adminId, String title, String content) {
        Announcement announcement = new Announcement();
        announcement.setAdminId(adminId);
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setIsTop(1);
        
        return publishAnnouncement(announcement);
    }
    
    /**
     * 测试方法
     */
    public static void test() {
        AnnouncementService service = new AnnouncementService();
        
        System.out.println("=== 测试公告服务 ===");
        
        // 测试发布公告
        int announcementId = service.publishAnnouncement(1, "系统维护通知", "系统将于今晚进行维护，请提前保存数据");
        System.out.println("发布公告结果: " + (announcementId > 0 ? "成功，ID=" + announcementId : "失败"));
        
        // 测试查询
        if (announcementId > 0) {
            Announcement detail = service.getAnnouncementDetail(announcementId);
            System.out.println("查询公告详情: " + 
                (detail != null ? detail.getTitle() : "失败"));
            
            List<Announcement> visible = service.getVisibleAnnouncements(1, 10);
            System.out.println("显示公告数量: " + visible.size());
            
            int total = service.getAnnouncementCount();
            System.out.println("总公告数量: " + total);
        }
    }
}