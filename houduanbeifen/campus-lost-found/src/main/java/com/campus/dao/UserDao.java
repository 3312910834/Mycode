
package com.campus.dao;

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
}