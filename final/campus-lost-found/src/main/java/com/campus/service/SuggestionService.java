package com.campus.service;

import com.campus.dao.SuggestionDao;
import com.campus.dao.SuggestionDaoImpl;
import com.campus.entity.Suggestion;
import java.util.List;

/**
 * 建议反馈业务逻辑层
 */
public class SuggestionService {
    private SuggestionDao suggestionDao = new SuggestionDaoImpl();
    
    /**
     * 提交建议
     */
    public int submitSuggestion(Suggestion suggestion) {
        // 设置默认值
        if (suggestion.getStatus() == null || suggestion.getStatus().isEmpty()) {
            suggestion.setStatus("pending");
        }
        
        return suggestionDao.insert(suggestion);
    }
    
    /**
     * 获取建议详情
     */
    public Suggestion getSuggestionDetail(Integer suggestionId) {
        return suggestionDao.findById(suggestionId);
    }
    
    /**
     * 获取用户的建议
     */
    public List<Suggestion> getUserSuggestions(Integer userId) {
        return suggestionDao.findByUserId(userId);
    }
    
    /**
     * 获取所有建议（分页）
     */
    public List<Suggestion> getAllSuggestions(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return suggestionDao.findAll(offset, pageSize);
    }
    
    /**
     * 根据状态获取建议
     */
    public List<Suggestion> getSuggestionsByStatus(String status) {
        return suggestionDao.findByStatus(status);
    }
    
    /**
     * 更新建议
     */
    public boolean updateSuggestion(Suggestion suggestion) {
        int result = suggestionDao.update(suggestion);
        return result > 0;
    }
    
    /**
     * 更新建议状态
     */
    public boolean updateSuggestionStatus(Integer suggestionId, String status) {
        int result = suggestionDao.updateStatus(suggestionId, status);
        return result > 0;
    }
    
    /**
     * 添加回复
     */
    public boolean addReply(Integer suggestionId, String reply) {
        int result = suggestionDao.addReply(suggestionId, reply);
        return result > 0;
    }
    
    /**
     * 删除建议
     */
    public boolean deleteSuggestion(Integer suggestionId) {
        int result = suggestionDao.delete(suggestionId);
        return result > 0;
    }
    
    /**
     * 获取建议数量
     */
    public int getSuggestionCount() {
        return suggestionDao.count();
    }
    
    /**
     * 按状态获取建议数量
     */
    public int getSuggestionCountByStatus(String status) {
        return suggestionDao.countByStatus(status);
    }
    
    /**
     * 获取建议统计
     */
    public SuggestionStats getSuggestionStats() {
        SuggestionStats stats = new SuggestionStats();
        stats.setTotal(suggestionDao.count());
        stats.setPending(suggestionDao.countByStatus("pending"));
        stats.setRead(suggestionDao.countByStatus("read"));
        stats.setProcessed(suggestionDao.countByStatus("processed"));
        return stats;
    }
    
    /**
     * 提交建议（简化方法）
     */
    public int submitSuggestion(Integer userId, String content) {
        Suggestion suggestion = new Suggestion();
        suggestion.setUserId(userId);
        suggestion.setContent(content);
        
        return submitSuggestion(suggestion);
    }
    
    /**
     * 测试方法
     */
    public static void test() {
        SuggestionService service = new SuggestionService();
        
        System.out.println("=== 测试建议服务 ===");
        
        // 测试提交建议
        int suggestionId = service.submitSuggestion(8, "希望增加图片上传功能");
        System.out.println("提交建议结果: " + (suggestionId > 0 ? "成功，ID=" + suggestionId : "失败"));
        
        // 测试查询
        if (suggestionId > 0) {
            Suggestion detail = service.getSuggestionDetail(suggestionId);
            System.out.println("查询建议详情: " + 
                (detail != null ? detail.getContent() : "失败"));
            
            List<Suggestion> userSuggestions = service.getUserSuggestions(8);
            System.out.println("用户8的建议数量: " + userSuggestions.size());
            
            SuggestionStats stats = service.getSuggestionStats();
            System.out.println("建议统计: 总数=" + stats.getTotal() + 
                ", 待处理=" + stats.getPending());
        }
    }
    
    /**
     * 建议统计类
     */
    public static class SuggestionStats {
        private int total;
        private int pending;
        private int read;
        private int processed;
        
        // getters and setters
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public int getPending() { return pending; }
        public void setPending(int pending) { this.pending = pending; }
        
        public int getRead() { return read; }
        public void setRead(int read) { this.read = read; }
        
        public int getProcessed() { return processed; }
        public void setProcessed(int processed) { this.processed = processed; }
    }
}