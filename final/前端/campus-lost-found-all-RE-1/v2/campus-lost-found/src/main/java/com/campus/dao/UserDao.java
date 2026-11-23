
package com.campus.dao;

import java.util.List;

import com.campus.entity.User;

/**
 * 用户数据访问接口
 */
public interface UserDao {
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查询用户
     */
    User findById(Integer id);
    
    /**
     * 插入新用户
     * @return 插入的行数，或者返回生成的ID
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     */
    int update(User user);
    
    /**
     * 验证用户名和密码
     */
    User findByUsernameAndPassword(String username, String password);
    
 // --- 【新增】管理员专用方法 ---

    /**
     * 分页查询用户列表（支持按用户名、角色、状态筛选）
     * @param username 用户名模糊查询（可为null）
     * @param role 角色筛选（可为null）
     * @param status 状态筛选（可为null）
     * @param offset 偏移量 (页码-1)*每页数量
     * @param limit 每页数量
     * @return 用户列表
     */
    List<User> findAllByPage(String username, String role, Integer status, int offset, int limit);

    /**
     * 统计符合条件的用户总数
     */
    int countByCondition(String username, String role, Integer status);

    /**
     * 单独更新用户状态 (启用/禁用)
     * @param id 用户ID
     * @param status 新状态 (1正常, 0禁用)
     * @return 影响行数
     */
    int updateStatus(Integer id, Integer status);

    /**
     * 单独更新用户角色
     * @param id 用户ID
     * @param role 新角色 ("admin" 或 "user")
     * @return 影响行数
     */
    int updateRole(Integer id, String role);

    /**
     * 更新用户密码（传入已加密后的密码）
     */
    int updatePassword(Integer id, String encryptedPassword);
}