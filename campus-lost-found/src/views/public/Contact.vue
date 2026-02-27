<template>
  <div class="contact-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">联系我们</h1>
        <p class="page-subtitle">您的反馈对我们非常重要</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="container">
        <div class="content-wrapper">
          <!-- 左侧：联系信息 -->
          <div class="contact-info">
            <el-card shadow="hover" class="info-card">
              <template #header>
                <div class="card-header">
                  <span class="card-title">联系方式</span>
                </div>
              </template>
              
              <div class="info-items">
                <div class="info-item">
                  <el-icon class="item-icon"><Location /></el-icon>
                  <div class="item-content">
                    <h3>办公地址</h3>
                    <p>北京市海淀区校园路123号</p>
                  </div>
                </div>
                
                <div class="info-item">
                  <el-icon class="item-icon"><Phone /></el-icon>
                  <div class="item-content">
                    <h3>联系电话</h3>
                    <p>010-12345678</p>
                    <p class="sub-text">工作日 9:00-18:00</p>
                  </div>
                </div>
                
                <div class="info-item">
                  <el-icon class="item-icon"><Message /></el-icon>
                  <div class="item-content">
                    <h3>电子邮箱</h3>
                    <p>contact@campus.com</p>
                  </div>
                </div>
                
                <div class="info-item">
                  <el-icon class="item-icon"><Clock /></el-icon>
                  <div class="item-content">
                    <h3>服务时间</h3>
                    <p>周一至周五 9:00-18:00</p>
                    <p>周六 10:00-16:00</p>
                  </div>
                </div>
              </div>
            </el-card>
            
            <!-- 统计信息卡片 -->
            <el-card shadow="hover" class="stats-card">
              <template #header>
                <div class="card-header">
                  <span class="card-title">反馈统计</span>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="testApiConnection"
                    :loading="loading"
                  >
                    测试API
                  </el-button>
                </div>
              </template>
              
              <div v-if="stats" class="stats-items">
                <div class="stat-item">
                  <span class="stat-label">总反馈数</span>
                  <span class="stat-value">{{ stats.total }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">待处理</span>
                  <span class="stat-value">{{ stats.pending }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">已处理</span>
                  <span class="stat-value">{{ stats.processed }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">已查看</span>
                  <span class="stat-value">{{ stats.read }}</span>
                </div>
              </div>
              <div v-else class="empty-stats">
                <el-button type="text" @click="loadStats">加载统计</el-button>
              </div>
            </el-card>
            
            <!-- 用户建议列表 -->
            <el-card v-if="userSuggestions.length > 0" shadow="hover" class="suggestions-card">
              <template #header>
                <div class="card-header">
                  <span class="card-title">我的建议</span>
                  <el-button 
                    type="text" 
                    @click="gotoUserSuggestions"
                  >
                    查看全部
                  </el-button>
                </div>
              </template>
              
              <div class="suggestions-list">
                <div 
                  v-for="item in userSuggestions.slice(0, 3)" 
                  :key="item.id"
                  class="suggestion-item"
                >
                  <div class="suggestion-content">
                    <div class="suggestion-title">{{ item.content ? item.content.substring(0, 30) : '' }}...</div>
                    <div class="suggestion-meta">
                      <el-tag 
                        :type="getStatusType(item.status)" 
                        size="small"
                      >
                        {{ getStatusText(item.status) }}
                      </el-tag>
                      <span class="suggestion-time">{{ formatTime(item.createTime) }}</span>
                    </div>
                  </div>
                  <el-button 
                    v-if="item.reply" 
                    type="text" 
                    size="small"
                    @click="showReply(item)"
                  >
                    查看回复
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 右侧：反馈表单 -->
          <div class="contact-form">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <span class="card-title">意见反馈</span>
                  <el-button 
                    v-if="isLoggedIn" 
                    type="text" 
                    @click="loadUserSuggestions"
                  >
                    我的反馈记录
                  </el-button>
                </div>
              </template>
              
              <!-- 折叠面板用于反馈说明 -->
              <el-collapse v-model="activeNames">
                <el-collapse-item name="1">
                  <template #title>
                    <div class="collapse-title">
                      <el-icon><InfoFilled /></el-icon>
                      <span>反馈说明</span>
                    </div>
                  </template>
                  <div class="collapse-content">
                    <p>1. 请详细描述您遇到的问题或建议</p>
                    <p>2. 我们会尽快处理您的反馈（通常1-3个工作日）</p>
                    <p>3. 您可以在"我的反馈"中查看处理进度和回复</p>
                    <p>4. 请保持联系方式畅通以便我们与您沟通</p>
                  </div>
                </el-collapse-item>
              </el-collapse>
              
              <!-- 反馈表单 -->
              <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="80px"
                class="feedback-form"
              >
                <el-form-item label="姓名" prop="name">
                  <el-input
                    v-model="form.name"
                    placeholder="请输入您的姓名"
                    :disabled="loading"
                  />
                </el-form-item>
                
                <el-form-item label="联系方式" prop="contact">
                  <el-input
                    v-model="form.contact"
                    placeholder="请输入手机号或邮箱"
                    :disabled="loading"
                  />
                </el-form-item>
                
                <el-form-item label="反馈内容" prop="content">
                  <el-input
                    v-model="form.content"
                    type="textarea"
                    :rows="6"
                    placeholder="请详细描述您的问题或建议..."
                    :disabled="loading"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
                
                <el-form-item>
                  <div class="form-actions">
                    <el-button 
                      type="primary" 
                      @click="submitFeedback"
                      :loading="loading"
                      :disabled="!form.content"
                    >
                      提交反馈
                    </el-button>
                    <el-button @click="resetForm">重置</el-button>
                    <el-button 
                      type="success" 
                      plain
                      @click="testApiConnection"
                    >
                      测试连接
                    </el-button>
                  </div>
                </el-form-item>
              </el-form>
            </el-card>
            
            <!-- 提交结果提示 -->
            <div v-if="submitResult" class="result-message">
              <el-alert
                :title="submitResult.title"
                :type="submitResult.type"
                :description="submitResult.message"
                show-icon
                @close="submitResult = null"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- API测试结果对话框 -->
    <el-dialog
      v-model="showApiTestDialog"
      title="API测试结果"
      width="600px"
    >
      <div v-if="apiTestResult" class="test-result-content">
        <div class="test-method">
          <strong>测试方法：</strong>{{ apiTestResult.method }}
        </div>
        <div class="test-status">
          <strong>测试状态：</strong>
          <el-tag :type="apiTestResult.status === '成功' ? 'success' : 'danger'">
            {{ apiTestResult.status }}
          </el-tag>
        </div>
        <div class="test-data">
          <strong>返回数据：</strong>
          <pre class="result-pre">{{ JSON.stringify(apiTestResult.data, null, 2) }}</pre>
        </div>
        <div v-if="apiTestResult.error" class="test-error">
          <strong>错误信息：</strong>
          <pre class="error-pre">{{ apiTestResult.error }}</pre>
        </div>
      </div>
      <div v-else class="loading-result">
        <el-skeleton :rows="5" animated />
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showApiTestDialog = false">关闭</el-button>
          <el-button type="primary" @click="testApiConnection">重新测试</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 回复详情对话框 -->
    <el-dialog
      v-model="showReplyDialog"
      title="回复详情"
      width="500px"
    >
      <div v-if="currentSuggestion" class="reply-content">
        <div class="original-content">
          <h4>您的反馈：</h4>
          <p>{{ currentSuggestion.content }}</p>
        </div>
        <div class="admin-reply">
          <h4>管理员回复：</h4>
          <p>{{ currentSuggestion.reply || '暂无回复' }}</p>
        </div>
        <div class="reply-time">
          <span class="time-label">回复时间：</span>
          <span>{{ formatTime(currentSuggestion.updateTime) }}</span>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showReplyDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Location, 
  Phone, 
  Message, 
  Clock,
  InfoFilled
} from '@element-plus/icons-vue'

// 导入建议API - 使用修复后的函数
import { 
  submitSuggestion, 
  testSuggestionApi, 
  getUserSuggestions,
  getSuggestionStats
} from '@/api/suggestion'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const activeNames = ref(['1'])
const showApiTestDialog = ref(false)
const showReplyDialog = ref(false)
const apiTestResult = ref(null)
const currentSuggestion = ref(null)
const stats = ref(null)
const userSuggestions = ref([])
const submitResult = ref(null)

// 检查用户是否登录
const isLoggedIn = computed(() => {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  return !!(token || user)
})

// 获取当前用户ID
const getCurrentUserId = () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    console.log('获取用户信息:', user)
    
    // 优先使用用户ID
    if (user.id) {
      return user.id
    }
    
    // 如果没有id，根据用户名判断
    if (user.username === 'zhangsan') return 2
    if (user.username === 'lisi') return 3
    if (user.username === 'wangwu') return 4
    if (user.username === 'admin') return 1
    
    // 默认返回测试用户ID
    return 8
  } catch (error) {
    console.error('获取用户ID失败:', error)
    return 8 // 默认用户ID
  }
}

// 表单数据
const form = reactive({
  name: '',
  contact: '',
  content: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^\w+@\w+\.\w+$/, message: '请输入正确的手机号或邮箱', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 10, message: '反馈内容至少10个字符', trigger: 'blur' },
    { max: 500, message: '反馈内容不能超过500个字符', trigger: 'blur' }
  ]
}

// 页面加载时自动填充用户信息和加载数据
onMounted(() => {
  console.log('Contact页面已加载')
  if (isLoggedIn.value) {
    loadUserInfo()
    loadUserSuggestions()
  }
  loadStats()
})

// 加载用户信息
const loadUserInfo = () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    console.log('加载用户信息:', user)
    
    if (!form.name && user.realName) {
      form.name = user.realName
    }
    if (!form.name && user.username) {
      form.name = user.username
    }
    if (!form.contact && user.email) {
      form.contact = user.email
    }
    if (!form.contact && user.phone) {
      form.contact = user.phone
    }
    
    console.log('填充后的表单数据:', form)
  } catch (error) {
    console.error('解析用户信息失败:', error)
  }
}

// 加载用户建议
const loadUserSuggestions = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录查看反馈记录')
    return
  }
  
  try {
    const userId = getCurrentUserId()
    console.log('正在加载用户建议，用户ID:', userId)
    
    const response = await getUserSuggestions(userId)
    console.log('获取用户建议响应:', response)
    
    if (response.code === 200 && response.data) {
      // 注意后端返回的数据结构：response.data.suggestions
      userSuggestions.value = response.data.suggestions || []
      console.log('加载的用户建议:', userSuggestions.value)
      
      if (userSuggestions.value.length === 0) {
        ElMessage.info('您还没有提交过反馈')
      } else {
        ElMessage.success(`加载了${userSuggestions.value.length}条反馈记录`)
      }
    } else {
      console.warn('获取用户建议返回非200状态:', response)
      userSuggestions.value = []
    }
  } catch (error) {
    console.error('加载用户建议失败:', error)
    ElMessage.error('加载建议失败: ' + error.message)
    userSuggestions.value = []
  }
}

// 加载统计信息
const loadStats = async () => {
  try {
    console.log('正在加载统计信息...')
    const response = await getSuggestionStats()
    console.log('统计信息响应:', response)
    
    if (response.code === 200 && response.data) {
      stats.value = response.data
      console.log('统计信息:', stats.value)
    } else {
      console.warn('统计信息返回非200状态:', response)
      stats.value = null
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
    ElMessage.error('加载统计信息失败: ' + error.message)
    stats.value = null
  }
}

// 提交反馈到数据库
const submitFeedback = async () => {
  if (!formRef.value) return
  
  // 验证表单
  try {
    await formRef.value.validate()
    console.log('表单验证通过')
  } catch (error) {
    console.log('表单验证失败:', error)
    ElMessage.warning('请正确填写表单信息')
    return
  }
  
  loading.value = true
  submitResult.value = null
  
  try {
    // 获取用户ID
    const userId = getCurrentUserId()
    
    console.log('提交反馈数据:', {
      userId,
      ...form
    })
    
    // 调用API提交到数据库
    const response = await submitSuggestion({
      userId,
      name: form.name,
      contact: form.contact,
      content: form.content
    })
    
    console.log('后端响应:', response)
    
    // 处理响应结果 - 根据后端返回的结构
    if (response.success || response.suggestionId) {
      const suggestionId = response.suggestionId || response.data?.id
      
      ElMessage.success({
        message: `反馈提交成功！反馈编号：${suggestionId}`,
        duration: 3000
      })
      
      submitResult.value = {
        type: 'success',
        title: '提交成功',
        message: `您的反馈已成功提交，反馈编号：${suggestionId}。我们会尽快处理您的反馈。`
      }
      
      // 重置表单
      resetForm()
      
      // 重新加载用户建议和统计
      loadUserSuggestions()
      loadStats()
      
    } else {
      // 如果后端返回错误信息
      const errorMsg = response.message || response.error || '提交失败'
      throw new Error(errorMsg)
    }
    
  } catch (error) {
    console.error('API调用失败:', error)
    
    submitResult.value = {
      type: 'error',
      title: '提交失败',
      message: error.message || '提交失败，请稍后重试或联系管理员。'
    }
    
    ElMessage.error('提交失败：' + error.message)
    
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  // 重新加载用户信息（保留用户信息）
  if (isLoggedIn.value) {
    loadUserInfo()
  }
}

// 测试建议API连接
const testApiConnection = async () => {
  loading.value = true
  apiTestResult.value = null
  showApiTestDialog.value = true
  
  try {
    console.log('开始测试建议API连接...')
    
    // 测试API连接
    const response = await testSuggestionApi()
    console.log('API测试响应:', response)
    
    apiTestResult.value = {
      method: 'testSuggestionApi',
      status: '成功',
      data: response
    }
    
    ElMessage.success('建议API连接正常')
    
  } catch (error) {
    console.error('API测试失败:', error)
    
    apiTestResult.value = {
      method: 'testSuggestionApi',
      status: '失败',
      error: error.message,
      data: null
    }
    
    ElMessage.error('建议API连接失败：' + error.message)
    
  } finally {
    loading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  if (!status) return 'default'
  
  switch (status.toLowerCase()) {
    case 'pending': return 'warning'
    case 'read': return 'info'
    case 'processed': return 'success'
    default: return 'default'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  if (!status) return '未知'
  
  switch (status.toLowerCase()) {
    case 'pending': return '待处理'
    case 'read': return '已查看'
    case 'processed': return '已处理'
    default: return status
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未知时间'
  
  try {
    // 如果已经是格式化好的时间字符串，直接返回
    if (typeof time === 'string' && time.includes('-')) {
      return time
    }
    
    const date = new Date(time)
    if (isNaN(date.getTime())) {
      return time
    }
    
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    console.error('格式化时间失败:', error, time)
    return time
  }
}

// 显示回复详情
const showReply = (suggestion) => {
  console.log('显示回复详情:', suggestion)
  currentSuggestion.value = suggestion
  showReplyDialog.value = true
}

// 跳转到用户建议列表
const gotoUserSuggestions = () => {
  if (isLoggedIn.value) {
    router.push('/user/suggestions')
  } else {
    ElMessage.warning('请先登录查看您的建议')
    router.push('/login')
  }
}
</script>

<style scoped>
.contact-container {
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 0;
  text-align: center;
}

.page-title {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 18px;
  opacity: 0.9;
}

/* 主要内容 */
.main-content {
  padding: 40px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 30px;
}

@media (max-width: 992px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
}

/* 卡片样式 */
.info-card, .stats-card, .suggestions-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

/* 联系信息项目 */
.info-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.item-icon {
  font-size: 24px;
  color: #667eea;
  margin-top: 4px;
}

.item-content h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #2c3e50;
}

.item-content p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.sub-text {
  font-size: 12px;
  color: #999;
}

/* 统计信息 */
.stats-items {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #667eea;
}

.empty-stats {
  text-align: center;
  padding: 20px;
}

/* 建议列表 */
.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.suggestion-item:hover {
  background: #eef2ff;
}

.suggestion-title {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
}

.suggestion-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.suggestion-time {
  font-size: 12px;
  color: #999;
}

/* 反馈表单 */
.feedback-form {
  margin-top: 20px;
}

.collapse-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.collapse-content {
  padding-left: 24px;
  color: #666;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-start;
}

/* 结果消息 */
.result-message {
  margin-top: 20px;
}

/* 测试结果对话框 */
.test-result-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.test-method, .test-status, .test-data, .test-error {
  line-height: 1.6;
}

.result-pre, .error-pre {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 6px;
  font-size: 12px;
  overflow: auto;
  margin-top: 5px;
  max-height: 300px;
}

.result-pre {
  border-left: 3px solid #4CAF50;
}

.error-pre {
  border-left: 3px solid #f56c6c;
}

.loading-result {
  padding: 20px 0;
}

/* 回复详情 */
.reply-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.original-content, .admin-reply {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.original-content h4, .admin-reply h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.original-content p, .admin-reply p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}

.reply-time {
  text-align: right;
  font-size: 14px;
  color: #999;
}

.time-label {
  color: #666;
}

/* 对话框页脚 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid #e8e8e8;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .page-title {
    font-size: 28px;
  }
  
  .page-subtitle {
    font-size: 16px;
  }
  
  .stats-items {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .form-actions .el-button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>