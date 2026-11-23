package com.campus.dao;

import com.campus.entity.FoundItem;
import java.util.List;

/**
 * 招领信息数据访问接口
 */
public interface FoundItemDao {
    
    /**
     * 插入招领信息
     * @return 插入的行数，如果成功返回生成的ID
     */
    int insert(FoundItem item);
    
    /**
     * 根据ID查询招领信息
     */
    FoundItem findById(Integer id);
    
    /**
     * 查询用户发布的所有招领
     */
    List<FoundItem> findByUserId(Integer userId);
    
    /**
     * 查询所有招领（分页）
     * @param offset 偏移量
     * @param limit 每页数量
     */
    List<FoundItem> findAll(int offset, int limit);
    
    /**
     * 根据状态查询招领
     */
    List<FoundItem> findByStatus(String status);
    
    /**
     * 搜索招领（按标题、描述、地点）
     */
    List<FoundItem> search(String keyword);
    
    /**
     * 更新招领信息
     */
    int update(FoundItem item);
    
    /**
     * 更新招领状态
     */
    int updateStatus(Integer id, String status);
    
    /**
     * 删除招领（软删除）
     */
    int delete(Integer id);
    
    /**
     * 统计招领数量
     */
    int count();
 // 新增：获取所有招领物品的文本（给LLM智能匹配用）
    List<String> selectAllFoundText();
}