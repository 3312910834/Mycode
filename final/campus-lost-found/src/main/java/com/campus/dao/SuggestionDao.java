package com.campus.dao;

import com.campus.entity.Suggestion;
import java.util.List;

/**
 * 建议反馈数据访问接口
 */
public interface SuggestionDao {
    
    /**
     * 插入建议
     */
    int insert(Suggestion suggestion);
    
    /**
     * 根据ID查询建议
     */
    Suggestion findById(Integer id);
    
    /**
     * 查询用户的建议
     */
    List<Suggestion> findByUserId(Integer userId);
    
    /**
     * 查询所有建议（分页）
     */
    List<Suggestion> findAll(int offset, int limit);
    
    /**
     * 根据状态查询建议
     */
    List<Suggestion> findByStatus(String status);
    
    /**
     * 更新建议（主要是回复和状态）
     */
    int update(Suggestion suggestion);
    
    /**
     * 更新建议状态
     */
    int updateStatus(Integer id, String status);
    
    /**
     * 添加回复
     */
    int addReply(Integer id, String reply);
    
    /**
     * 删除建议
     */
    int delete(Integer id);
    
    /**
     * 统计建议数量
     */
    int count();
    
    /**
     * 按状态统计
     */
    int countByStatus(String status);
}