package com.campus.entity;

import java.time.LocalDateTime;

public class FoundItem {
    private Integer id;
    private Integer userId;
    private String title;
    private String category;
    private String foundLocation;  // 拾获地点
    private LocalDateTime foundTime; // 拾获时间
    private String description;
    private String images;
    private String storageLocation; // 保管地点
    private String contactInfo;
    private String status;         // pending, published, claimed, closed
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联信息
    private String username;
    private String userPhone;
    
    // 构造器和getter/setter（类似LostItem）
    public FoundItem() {}
    
    public FoundItem(Integer userId, String title, String foundLocation, LocalDateTime foundTime) {
        this.userId = userId;
        this.title = title;
        this.foundLocation = foundLocation;
        this.foundTime = foundTime;
        this.status = "pending";
        this.isDeleted = 0;
    }
    
    // getters 和 setters（示例，需要完整实现）
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getFoundLocation() { return foundLocation; }
    public void setFoundLocation(String foundLocation) { this.foundLocation = foundLocation; }
    
    public LocalDateTime getFoundTime() { return foundTime; }
    public void setFoundTime(LocalDateTime foundTime) { this.foundTime = foundTime; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    
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
}