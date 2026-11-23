package com.campus.dao;

import com.campus.entity.Announcement;
import java.util.List;

/**
 * 公告数据访问接口
 */
public interface AnnouncementDao {
    
    /**
     * 插入公告
     */
    int insert(Announcement announcement);
    
    /**
     * 根据ID查询公告
     */
    Announcement findById(Integer id);
    
    /**
     * 查询所有公告（分页）
     */
    List<Announcement> findAll(int offset, int limit);
    
    /**
     * 查询显示的公告（status=1）
     */
    List<Announcement> findVisible(int offset, int limit);
    
    /**
     * 查询置顶公告
     */
    List<Announcement> findTopAnnouncements();
    
    /**
     * 查询管理员发布的公告
     */
    List<Announcement> findByAdminId(Integer adminId);
    
    /**
     * 更新公告
     */
    int update(Announcement announcement);
    
    /**
     * 更新公告状态
     */
    int updateStatus(Integer id, Integer status);
    
    /**
     * 更新置顶状态
     */
    int updateTopStatus(Integer id, Integer isTop);
    
    /**
     * 删除公告
     */
    int delete(Integer id);
    
    /**
     * 统计公告数量
     */
    int count();
    
    /**
     * 统计显示的公告数量
     */
    int countVisible();
}