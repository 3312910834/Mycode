package com.campus.service;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.campus.dao.UserDao;
import com.campus.dao.UserDaoImpl;
import com.campus.entity.User;
import com.campus.utils.MD5Util;

/**
 * 用户业务逻辑层
 */
public class UserService {
    private UserDao userDao = new UserDaoImpl();
    
    /**
     * 用户注册
     * @return 注册成功返回用户ID，失败返回0
     */
    public int register(User user) {
        // 1. 检查用户名是否已存在
        if (userDao.findByUsername(user.getUsername()) != null) {
            System.out.println("用户名已存在: " + user.getUsername());
            return 0;
        }
        
//         2. 密码加密
        user.setPassword(MD5Util.md5(user.getPassword()));

        
        // 3. 设置默认值
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        // 4. 插入数据库
        int userId = userDao.insert(user);
        if (userId > 0) {
            System.out.println("用户注册成功: " + user.getUsername() + ", ID: " + userId);
        }
        return userId;
    }
    
    /**
     * 用户登录
     * @return 登录成功返回用户对象（密码已清空），失败返回null
     */
    public User login(String username, String password) {
        // 密码加密后查询
        String encryptedPassword = MD5Util.md5(password);
        User user = userDao.findByUsernameAndPassword(username, encryptedPassword);
//    		User user = userDao.findByUsernameAndPassword(username, password);
        if (user != null) {
            // 登录成功后清空密码，确保安全
            user.setPassword(null);
            System.out.println("用户登录成功: " + username);
        } else {
            System.out.println("用户登录失败: " + username);
        }
        return user;
    }
    
    /**
     * 获取用户信息（不包含密码）
     */
    public User getUserInfo(Integer userId) {
        User user = userDao.findById(userId);
        if (user != null) {
            user.setPassword(null); // 移除密码
        }
        return user;
    }
    
    /**
     * 更新用户信息
     */
    public boolean updateUserInfo(User user) {
        int result = userDao.update(user);
        return result > 0;
    }
    
    /**
     * 【管理员】获取用户分页列表
     * @param page 页码 (从1开始)
     * @param limit 每页数量
     * @param username 搜索关键词 (可选)
     * @param role 角色筛选 (可选)
     * @param status 状态筛选 (可选)
     * @return 包含 list, total, page, limit 的 Map
     */
    public Map<String, Object> getUserPage(int page, int limit, String username, String role, Integer status) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算偏移量
        int offset = (page - 1) * limit;
        
        // 1. 查询数据列表
        List<User> userList = userDao.findAllByPage(username, role, status, offset, limit);
        
        // 2. 查询总数
        int total = userDao.countByCondition(username, role, status);
        
        result.put("list", userList);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        
        return result;
    }

    /**
     * 【管理员】修改用户状态 (封禁/解封)
     * @param userId 用户ID
     * @param status 新状态 (1:正常, 0:禁用)
     * @return 操作是否成功
     */
    public boolean updateUserStatus(Integer userId, int status) {
        // 这里可以加逻辑：防止管理员封禁自己 (如果需要的话)
        // if (currentUser.getId().equals(userId)) return false;
        return userDao.updateStatus(userId, status) > 0;
    }

    /**
     * 【管理员】修改用户角色 (提拔/降级)
     * @param userId 用户ID
     * @param role 新角色 ("admin" 或 "user")
     * @return 操作是否成功
     */
    public boolean updateUserRole(Integer userId, String role) {
        // 简单校验角色值
        if (!"admin".equals(role) && !"user".equals(role)) {
            return false;
        }
        return userDao.updateRole(userId, role) > 0;
    }

    /**
     * 修改密码
     */
    public boolean updatePassword(Integer userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            return false;
        }
        if (newPassword.trim().length() < 6) {
            return false;
        }
        User dbUser = userDao.findById(userId);
        if (dbUser == null || dbUser.getPassword() == null) {
            return false;
        }
        String oldEncrypted = MD5Util.md5(oldPassword);
        if (!oldEncrypted.equals(dbUser.getPassword())) {
            return false;
        }
        String newEncrypted = MD5Util.md5(newPassword);
        return userDao.updatePassword(userId, newEncrypted) > 0;
    }
    /**
     * 测试方法
     */
    public static void test() {
        UserService userService = new UserService();
        
        // 测试注册
        User newUser = new User("testuser", "123456", "13888888888", "user");
        newUser.setEmail("test@example.com");
        
        int userId = userService.register(newUser);
        System.out.println("注册结果: " + (userId > 0 ? "成功，ID=" + userId : "失败"));
        
        // 测试登录
        User loggedInUser = userService.login("testuser", "123456");
        System.out.println("登录结果: " + (loggedInUser != null ? "成功" : "失败"));
        
        // 测试获取用户信息
        if (loggedInUser != null) {
            User userInfo = userService.getUserInfo(loggedInUser.getId());
            System.out.println("用户信息: " + userInfo.getUsername() + ", " + userInfo.getPhone());
        }
    }
}