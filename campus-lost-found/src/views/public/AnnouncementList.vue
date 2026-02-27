<template>
  <div class="announcement-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <h1>系统公告</h1>
        <p>最新通知和系统公告</p>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-container">
      <div class="container">
        <!-- 置顶公告区域 -->
        <div v-if="topAnnouncements.length > 0" class="top-announcements-section">
          <h2 class="section-title">
            <el-icon><StarFilled /></el-icon>
            置顶公告
          </h2>
          <div class="top-announcements">
            <div v-for="announcement in topAnnouncements" :key="'top-' + announcement.id" 
                 class="announcement-card top-announcement">
              <div class="announcement-header">
                <div class="announcement-title">
                  <h3>
                    <el-tag type="warning" size="small" effect="dark">
                      置顶
                    </el-tag>
                    <span>{{ announcement.title }}</span>
                  </h3>
                </div>
                <div class="announcement-time">
                  {{ formatDate(announcement.createTime) }}
                </div>
              </div>
              
              <div class="announcement-content">
                <p>{{ announcement.content }}</p>
              </div>
              
              <div class="announcement-footer">
                <div class="announcement-meta">
                  <span class="admin-name">
                    <el-icon><User /></el-icon>
                    发布人：{{ announcement.adminName || '系统管理员' }}
                  </span>
                </div>
                
                <div class="announcement-actions">
                  <el-button type="primary" size="small" @click="showDetail(announcement)">
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 所有公告区域 -->
        <div class="all-announcements-section">
          <h2 class="section-title">
            <el-icon><Bell /></el-icon>
            所有公告
          </h2>
          
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 空状态 -->
          <div v-else-if="announcements.length === 0" class="empty-state">
            <el-empty description="暂无公告" />
          </div>

          <!-- 公告列表 -->
          <div v-else class="announcements-list">
            <div v-for="announcement in announcements" 
                 :key="announcement.id" 
                 class="announcement-card"
                 :class="{ 'hidden-announcement': announcement.status === 0 }">
              <div class="announcement-header">
                <div class="announcement-title">
                  <h3>
                    <el-tag v-if="announcement.isTop === 1" type="danger" size="small" effect="dark">
                      置顶
                    </el-tag>
                    <span>{{ announcement.title }}</span>
                  </h3>
                </div>
                <div class="announcement-time">
                  {{ formatDate(announcement.createTime) }}
                </div>
              </div>
              
              <div class="announcement-content">
                <p>{{ announcement.content }}</p>
              </div>
              
              <div class="announcement-footer">
                <div class="announcement-meta">
                  <span class="admin-name">
                    <el-icon><User /></el-icon>
                    发布人：{{ announcement.adminName || '系统管理员' }}
                  </span>
                  <el-tag v-if="announcement.status === 1" type="success" size="small">
                    显示中
                  </el-tag>
                  <el-tag v-else type="info" size="small">
                    已隐藏
                  </el-tag>
                </div>
                
                <div class="announcement-actions">
                  <el-button type="primary" size="small" @click="showDetail(announcement)">
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="pagination.total > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[5, 10, 15, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentAnnouncement?.title"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentAnnouncement" class="announcement-detail">
        <div class="detail-meta">
          <span>
            <el-icon><Calendar /></el-icon>
            发布时间：{{ formatDate(currentAnnouncement.createTime) }}
          </span>
          <span>
            <el-icon><User /></el-icon>
            发布人：{{ currentAnnouncement.adminName || '系统管理员' }}
          </span>
          <span>
            <el-icon><View /></el-icon>
            状态：{{ currentAnnouncement.status === 1 ? '显示中' : '已隐藏' }}
          </span>
          <span v-if="currentAnnouncement.isTop === 1" class="top-badge">
            <el-icon><StarFilled /></el-icon>
            置顶公告
          </span>
        </div>
        
        <div class="detail-content">
          <p>{{ currentAnnouncement.content }}</p>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Calendar, View, StarFilled, Bell } from '@element-plus/icons-vue'

// 导入API - 使用修正后的路径
import { getAnnouncements, getTopAnnouncements } from '@/api/announcement'

// 响应式数据
const loading = ref(false)
const announcements = ref([])
const topAnnouncements = ref([])
const detailDialogVisible = ref(false)
const currentAnnouncement = ref(null)

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 生命周期钩子
onMounted(() => {
  console.log('公告页面加载')
  loadAnnouncements()
  loadTopAnnouncements()
})

// 加载公告列表
const loadAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.size
    }
    
    console.log('请求公告参数:', params)
    
    const response = await getAnnouncements(params)
    console.log('公告API响应:', response)
    
    // 处理响应结构 - 根据后端代码，响应格式为 { code: 200, message: "success", data: { announcements: [], total: xx } }
    if (response && response.code === 200) {
      announcements.value = response.data?.announcements || []
      pagination.total = response.data?.total || 0
    } else if (response && response.data) {
      // 兼容其他可能的响应结构
      announcements.value = response.data.announcements || []
      pagination.total = response.data.total || 0
    } else {
      console.warn('未知的响应结构:', response)
      announcements.value = []
      pagination.total = 0
    }
    
    console.log('处理后的公告数据:', announcements.value)
    console.log('总条数:', pagination.total)
    
    // 如果没有数据，使用模拟数据
    if (announcements.value.length === 0) {
      console.log('使用模拟数据')
      announcements.value = getMockAnnouncements()
      pagination.total = announcements.value.length
    }
    
  } catch (error) {
    console.error('加载公告列表失败:', error)
    ElMessage.error('加载失败：' + (error.message || '网络错误'))
    
    // 出错时使用模拟数据
    announcements.value = getMockAnnouncements()
    pagination.total = announcements.value.length
  } finally {
    loading.value = false
  }
}

// 加载置顶公告
const loadTopAnnouncements = async () => {
  try {
    const response = await getTopAnnouncements()
    console.log('置顶公告响应:', response)
    
    if (response && response.code === 200) {
      topAnnouncements.value = response.data?.announcements || []
    } else if (response && response.data) {
      topAnnouncements.value = response.data.announcements || []
    } else {
      topAnnouncements.value = []
    }
    
    console.log('置顶公告数据:', topAnnouncements.value)
  } catch (error) {
    console.error('加载置顶公告失败:', error)
    topAnnouncements.value = []
  }
}

// 模拟数据（当API不可用时）
const getMockAnnouncements = () => {
  return [
    {
      id: 1,
      title: '欢迎使用校园失物招领系统',
      content: '本系统旨在帮助同学们找回失物，请大家诚信使用。如有任何问题，请联系管理员。',
      isTop: 1,
      status: 1,
      adminName: 'admin',
      createTime: '2026-01-05 14:27:01'
    },
    {
      id: 2,
      title: '物品发布规范',
      content: '发布物品时请提供详细信息和清晰图片，确保信息准确无误。',
      isTop: 0,
      status: 1,
      adminName: 'admin',
      createTime: '2026-01-05 14:27:01'
    },
    {
      id: 3,
      title: '联系我们',
      content: '如有问题请联系管理员：admin@campus.com 或拨打热线电话：12345678。',
      isTop: 0,
      status: 1,
      adminName: 'admin',
      createTime: '2026-01-05 14:27:01'
    }
  ]
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadAnnouncements()
}

const handleCurrentChange = (page) => {
  pagination.current = page
  loadAnnouncements()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知时间'
  
  try {
    // 处理各种可能的日期格式
    const date = new Date(dateString)
    if (isNaN(date.getTime())) {
      return dateString
    }
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (e) {
    return dateString
  }
}

// 显示公告详情
const showDetail = (announcement) => {
  currentAnnouncement.value = announcement
  detailDialogVisible.value = true
}

// 调试函数
const testAPI = async () => {
  try {
    const response = await getTopAnnouncements()
    console.log('API测试结果:', response)
    ElMessage.success('API连接成功！')
  } catch (error) {
    console.error('API连接失败:', error)
    ElMessage.error('API连接失败：' + error.message)
  }
}
</script>

<style scoped>
.announcement-list-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #3498db 0%, #2c3e50 100%);
  color: white;
  padding: 60px 0;
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 36px;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.page-header p {
  font-size: 18px;
  opacity: 0.9;
  margin: 0;
}

/* 内容容器 */
.content-container {
  padding: 0 0 50px;
}

/* 区域标题 */
.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #2c3e50;
  font-size: 22px;
  margin: 30px 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #3498db;
}

.top-announcements-section .section-title {
  color: #e6a23c;
  border-bottom-color: #e6a23c;
}

/* 置顶公告区域 */
.top-announcements {
  margin-bottom: 40px;
}

/* 加载状态 */
.loading-state {
  background: white;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 空状态 */
.empty-state {
  background: white;
  border-radius: 12px;
  padding: 80px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 公告列表 */
.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 公告卡片 */
.announcement-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border-left: 4px solid #3498db;
  transition: all 0.3s ease;
}

.announcement-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  border-left-color: #2980b9;
}

.top-announcement {
  border-left-color: #ffcc00;
  background: linear-gradient(to right, #fff9e6, #fff);
  position: relative;
}

.hidden-announcement {
  opacity: 0.7;
  background: #f9f9f9;
}

.top-announcement::before {
  content: '置顶';
  position: absolute;
  top: -10px;
  right: 20px;
  background: #e6a23c;
  color: white;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.announcement-title h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.announcement-time {
  font-size: 14px;
  color: #7f8c8d;
  white-space: nowrap;
}

.announcement-content {
  margin-bottom: 20px;
}

.announcement-content p {
  margin: 0;
  color: #555;
  line-height: 1.6;
  font-size: 15px;
  word-break: break-word;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.admin-name {
  font-size: 14px;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 5px;
}

.top-badge {
  color: #e6a23c;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
}

.announcement-actions {
  display: flex;
  gap: 10px;
}

/* 分页 */
.pagination-container {
  margin-top: 30px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 详情对话框样式 */
.announcement-detail {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #666;
}

.detail-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.detail-content {
  font-size: 15px;
  line-height: 1.6;
  color: #333;
  white-space: pre-line;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 40px 0;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .page-header p {
    font-size: 16px;
  }
  
  .announcement-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .announcement-card {
    padding: 20px;
  }
  
  .announcement-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .announcement-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .detail-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .top-announcement::before {
    right: 10px;
    font-size: 10px;
    padding: 2px 6px;
  }
}

/* 滚动条样式 */
.announcement-detail::-webkit-scrollbar {
  width: 6px;
}

.announcement-detail::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.announcement-detail::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.announcement-detail::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>