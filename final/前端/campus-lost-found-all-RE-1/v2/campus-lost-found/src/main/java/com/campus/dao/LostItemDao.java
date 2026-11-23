package com.campus.dao;

import com.campus.entity.LostItem;
import java.util.List;

/**
 * 失物信息数据访问接口
 */
public interface LostItemDao {
    
    /**
     * 插入失物信息
     * @return 插入的行数，如果成功返回生成的ID
     */
    int insert(LostItem item);
    
    /**
     * 根据ID查询失物信息
     */
    LostItem findById(Integer id);
    
    /**
     * 查询用户发布的所有失物
     */
    List<LostItem> findByUserId(Integer userId);
    
    /**
     * 查询所有失物（分页）
     * @param offset 偏移量
     * @param limit 每页数量
     */
    List<LostItem> findAll(int offset, int limit);
    
    /**
     * 根据状态查询失物
     */
    List<LostItem> findByStatus(String status);
    
    /**
     * 搜索失物（按标题、描述、地点）
     */
    List<LostItem> search(String keyword);
    
    /**
     * 更新失物信息
     */
    int update(LostItem item);
    
    /**
     * 更新失物状态
     */
    int updateStatus(Integer id, String status);
    
    /**
     * 删除失物（软删除）
     */
    int delete(Integer id);
    
    /**
     * 统计失物数量
     */
    int count();
}