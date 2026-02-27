package com.campus.entity;

import java.time.LocalDateTime;

public class LostItem {
    private Integer id;
    private Integer userId;
    private String title;
    private String category;
    private String lostLocation;  // 注意：对应数据库的 lost_location
    private LocalDateTime lostTime; // 注意：对应数据库的 lost_time
    private String description;
    private String images;
    private String contactInfo;   // 注意：对应数据库的 contact_info
    private String status;        // pending, published, found, closed
    private Integer isDeleted;    // 对应数据库的 is_deleted
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联信息
    private String username;
    private String userPhone;
    
    // 构造器
    public LostItem() {}
    
    public LostItem(Integer userId, String title, String lostLocation, LocalDateTime lostTime) {
        this.userId = userId;
        this.title = title;
        this.lostLocation = lostLocation;
        this.lostTime = lostTime;
        this.status = "pending";
        this.isDeleted = 0;
    }
    
    // getters 和 setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getLostLocation() { return lostLocation; }
    public void setLostLocation(String lostLocation) { this.lostLocation = lostLocation; }
    
    public LocalDateTime getLostTime() { return lostTime; }
    public void setLostTime(LocalDateTime lostTime) { this.lostTime = lostTime; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    
    @Override
    public String toString() {
        return "LostItem{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", lostLocation='" + lostLocation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}