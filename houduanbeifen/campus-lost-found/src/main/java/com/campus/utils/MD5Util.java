package com.campus.utils;

import java.security.MessageDigest;

/**
 * MD5加密工具类
 */
public class MD5Util {
    
    /**
     * MD5加密
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * 测试MD5加密
     */
    public static void main(String[] args) {
        String password = "123456";
        String encrypted = md5(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后: " + encrypted);
        System.out.println("admin123加密: " + md5("admin123"));
    }
}