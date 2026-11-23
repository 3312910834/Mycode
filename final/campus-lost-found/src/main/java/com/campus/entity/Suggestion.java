package com.campus.entity;

import java.time.LocalDateTime;

public class Suggestion {
    private Integer id;
    private Integer userId;
    private String content;
    private String status; // pending, read, processed
    private String reply;
    private LocalDateTime createTime;
    
    // 关联信息
    private String username;
    
    // getters 和 setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}