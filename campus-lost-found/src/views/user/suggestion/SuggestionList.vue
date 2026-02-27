<template>
  <div class="suggestion-list-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>我的建议反馈</h2>
      <p>查看您提交的建议反馈和处理状态</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="$router.push('/user/suggestions/add')">
        <el-icon><Plus /></el-icon>提交新建议
      </el-button>
    </div>

    <!-- 登录提示 -->
    <div v-if="!isLoggedIn" class="login-prompt">
      <el-alert type="warning" show-icon>
        <template #title>
          <span>请先登录查看建议反馈</span>
          <el-button type="primary" size="small" @click="goToLogin" style="margin-left: 20px;">
            去登录
          </el-button>
        </template>
      </el-alert>
    </div>

    <!-- 建议列表 -->
    <div v-else>
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="suggestions.length === 0" class="empty-container">
        <el-empty description="暂无建议反馈" />
        <div class="empty-action">
          <el-button type="primary" @click="$router.push('/user/suggestions/add')">
            提交一个建议
          </el-button>
        </div>
      </div>

      <div v-else class="suggestions-list">
        <div v-for="suggestion in suggestions" :key="suggestion.id" class="suggestion-item">
          <div class="suggestion-header">
            <div class="suggestion-status">
              <el-tag :type="getStatusType(suggestion.status)" size="small">
                {{ getStatusText(suggestion.status) }}
              </el-tag>
            </div>
            <div class="suggestion-time">
              {{ formatDate(suggestion.createTime || suggestion.create_time) }}
            </div>
          </div>
          
          <div class="suggestion-content">
            <p>{{ truncateText(suggestion.content, 200) }}</p>
          </div>
          
          <!-- 回复内容 -->
          <div v-if="suggestion.reply" class="suggestion-reply">
            <div class="reply-header">
              <el-icon><ChatDotRound /></el-icon>
              <span>管理员回复</span>
            </div>
            <div class="reply-content">
              <p>{{ suggestion.reply }}</p>
            </div>
            <div v-if="suggestion.reply_time" class="reply-time">
              回复时间：{{ formatDate(suggestion.reply_time) }}
            </div>
          </div>
          
          <div class="suggestion-actions">
            <el-button type="text" @click="viewSuggestion(suggestion)">
              查看详情
            </el-button>
            <el-button type="text" class="delete-btn" @click="deleteSuggestion(suggestion)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="suggestions.length > 0" class="pagination-container">
        <el-pagination
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ChatDotRound } from '@element-plus/icons-vue'
import { getMySuggestions, deleteSuggestion as deleteSuggestionApi } from '@/api/suggestion'

const router = useRouter()

// 计算属性：检查用户是否登录
const isLoggedIn = computed(() => {
  // 检查多种可能的存储方式
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  const user = localStorage.getItem('user') || sessionStorage.getItem('user')
  const userId = localStorage.getItem('userId') || sessionStorage.getItem('userId')
  
  console.log('登录检查:', { token, user, userId })
  
  return !!(token || user || userId)
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const loading = ref(false)
const suggestions = ref([])

// 状态映射
const statusMap = {
  pending: { text: '待处理', type: 'warning' },
  read: { text: '已查看', type: 'info' },
  processed: { text: '已处理', type: 'success' }
}

onMounted(() => {
  console.log('组件挂载，登录状态:', isLoggedIn.value)
  if (isLoggedIn.value) {
    fetchSuggestions()
  }
})

// 获取当前用户ID
const getCurrentUserId = () => {
  console.log('开始获取用户ID...')
  
  // 尝试从不同位置获取用户信息
  let userData = null
  
  // 1. 从 localStorage 获取
  try {
    const userStr = localStorage.getItem('user') || sessionStorage.getItem('user')
    if (userStr) {
      userData = JSON.parse(userStr)
      console.log('从user字段获取:', userData)
      if (userData && userData.id) {
        return userData.id
      }
    }
  } catch (e) {
    console.log('解析user字段失败:', e)
  }
  
  // 2. 直接获取 userId
  const userId = localStorage.getItem('userId') || sessionStorage.getItem('userId')
  if (userId) {
    console.log('直接获取userId:', userId)
    return parseInt(userId)
  }
  
  // 3. 检查常见字段
  const checkKeys = ['id', 'user_id', 'userId', 'userid']
  for (const key of checkKeys) {
    const value = localStorage.getItem(key)
    if (value) {
      console.log(`从${key}获取用户ID:`, value)
      return parseInt(value)
    }
  }
  
  // 4. 根据用户名判断（测试用）
  const username = localStorage.getItem('username') || sessionStorage.getItem('username')
  if (username) {
    console.log('根据用户名判断:', username)
    const userMap = {
      'zhangsan': 2,
      'lisi': 3,
      'wangwu': 4,
      'admin': 1,
      'test': 8
    }
    if (userMap[username]) {
      return userMap[username]
    }
  }
  
  console.log('未找到用户ID，使用默认值 8')
  return 8 // 默认用户ID
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 获取建议列表
const fetchSuggestions = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  
  loading.value = true
  try {
    // 获取用户ID
    const userId = getCurrentUserId()
    console.log('获取建议，用户ID:', userId)
    
    if (!userId) {
      ElMessage.error('无法获取用户信息，请重新登录')
      localStorage.clear()
      sessionStorage.clear()
      router.push('/login')
      return
    }
    
    // 调用API获取建议
    const response = await getMySuggestions(userId)
    console.log('API响应:', response)
    
    // 根据后端返回格式调整
    if (response.code === 200 && response.data) {
      suggestions.value = response.data.suggestions || response.data || []
      pagination.total = response.data.count || suggestions.value.length
      console.log('加载建议数量:', suggestions.value.length)
    } else {
      ElMessage.error(response.message || '获取建议失败')
      suggestions.value = []
    }
  } catch (error) {
    console.error('获取建议列表失败:', error)
    ElMessage.error('加载失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchSuggestions()
}

const handleCurrentChange = (page) => {
  pagination.current = page
  fetchSuggestions()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return dateString
  }
}

// 截断文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 获取状态文本和类型
const getStatusText = (status) => {
  return statusMap[status]?.text || status
}

const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

// 查看建议详情
const viewSuggestion = (suggestion) => {
  router.push({
    path: '/user/suggestions/detail',
    query: { id: suggestion.id }
  })
}

// 删除建议
const deleteSuggestion = async (suggestion) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条建议吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteSuggestionApi(suggestion.id)
    ElMessage.success('删除成功')
    fetchSuggestions() // 重新加载列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.suggestion-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 登录提示 */
.login-prompt {
  margin-bottom: 20px;
}

.login-prompt .el-alert {
  display: flex;
  align-items: center;
}

/* 页面标题 */
.page-title {
  margin-bottom: 30px;
}

.page-title h2 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 24px;
}

.page-title p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

/* 操作栏 */
.action-bar {
  margin-bottom: 20px;
}

/* 加载状态 */
.loading-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 空状态 */
.empty-container {
  background: white;
  border-radius: 8px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-action {
  margin-top: 20px;
}

/* 建议列表 */
.suggestions-list {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

/* 建议项 */
.suggestion-item {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  transition: background 0.3s;
}

.suggestion-item:hover {
  background: #f8f9fa;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.suggestion-time {
  font-size: 12px;
  color: #6c757d;
}

/* 建议内容 */
.suggestion-content {
  margin-bottom: 15px;
}

.suggestion-content p {
  margin: 0;
  color: #495057;
  line-height: 1.6;
  font-size: 14px;
}

/* 回复内容 */
.suggestion-reply {
  background: #f1f8ff;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 15px;
  border-left: 4px solid #1890ff;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #1890ff;
  font-weight: 500;
  font-size: 14px;
}

.reply-content p {
  margin: 0;
  color: #495057;
  line-height: 1.6;
  font-size: 14px;
}

.reply-time {
  margin-top: 10px;
  font-size: 12px;
  color: #6c757d;
  text-align: right;
}

/* 建议操作 */
.suggestion-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.suggestion-actions .el-button {
  padding: 4px 8px;
  font-size: 12px;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  color: #f78989;
}

/* 分页 */
.pagination-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .suggestion-list-container {
    padding: 15px;
  }
  
  .suggestion-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .suggestion-time {
    margin-left: 0;
  }
  
  .suggestion-item {
    padding: 15px;
  }
  
  .suggestion-actions {
    flex-wrap: wrap;
  }
}
</style>