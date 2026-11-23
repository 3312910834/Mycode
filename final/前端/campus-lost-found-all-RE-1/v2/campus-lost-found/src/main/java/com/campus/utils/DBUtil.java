package com.campus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库连接工具类
 */
public class DBUtil {
    private static String url;
    private static String user;
    private static String password;
    private static String driver;
    
    static {
        try {
            // 从配置文件读取数据库配置
            InputStream is = DBUtil.class.getClassLoader()
                .getResourceAsStream("db.properties");
            
            if (is == null) {
                // 如果没有配置文件，使用默认值（您需要修改这些值）
                System.out.println("⚠ 没有找到db.properties，使用默认配置");
                url = "jdbc:mysql://localhost:3306/campus_lost_found?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
                user = "root";
                password = "123";  // 密码
                driver = "com.mysql.cj.jdbc.Driver";
            } else {
                Properties props = new Properties();
                props.load(is);
                url = props.getProperty("db.url");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");
                driver = props.getProperty("db.driver");
                is.close();
            }
            
            // 加载驱动
            Class.forName(driver);
            System.out.println("✅ 数据库驱动加载成功: " + driver);
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ 数据库驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("❌ 配置文件读取失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    
    /**
     * 关闭连接资源
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 测试数据库连接
     */
    public static String testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                return "✅ 数据库连接成功！\n"
                     + "URL: " + url + "\n"
                     + "用户: " + user;
            }
        } catch (SQLException e) {
            return "❌ 数据库连接失败: " + e.getMessage();
        } finally {
            close(conn, null, null);
        }
        return "❌ 数据库连接未知错误";
    }
}