package com.campus.service;

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
        
        // 2. 密码加密
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