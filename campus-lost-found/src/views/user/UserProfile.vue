<template>
  <div class="user-profile-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>个人信息</h2>
      <p>管理您的个人资料</p>
    </div>

    <!-- 成功提示 -->
    <el-alert
      v-if="showSuccessMessage"
      title="更新成功"
      type="success"
      :description="successMessage"
      show-icon
      center
      @close="showSuccessMessage = false"
      class="success-alert"
    />

    <!-- 用户卡片 -->
    <div class="user-card">
      <!-- 左侧：头像和信息概览 -->
      <div class="user-sidebar">
        <div class="avatar-section">
          <el-avatar :src="avatarPreview" :size="120" class="main-avatar" />
          <div class="avatar-actions">
            <el-upload
              action="/api/upload/avatar"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-button type="primary" size="small" icon="Upload">
                更换头像
              </el-button>
            </el-upload>
            <el-button type="text" size="small" @click="resetAvatar">
              恢复默认
            </el-button>
          </div>
        </div>

        <div class="user-quick-info">
          <h3>{{ userStore.userName }}</h3>
          <div class="user-role">
            <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'success'" size="small">
              {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </div>
          <div class="user-id">用户ID: {{ userStore.userInfo?.id }}</div>
        </div>
      </div>

      <!-- 右侧：详细信息表单 -->
      <div class="user-main">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="基本信息" name="basic">
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="用户名">
                <el-input v-model="form.username" disabled class="disabled-input">
                  <template #append>
                    <el-tag size="small" type="info">不可修改</el-tag>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="真实姓名" prop="real_name">
                <el-input 
                  v-model="form.real_name" 
                  placeholder="请输入真实姓名"
                  :maxlength="20"
                  show-word-limit
                  clearable
                />
                <div class="field-tip" v-if="originalForm.real_name !== form.real_name">
                  <el-icon color="#67c23a"><Check /></el-icon>
                  已修改
                </div>
              </el-form-item>

              <el-form-item label="学号" prop="student_id">
                <el-input 
                  v-model="form.student_id" 
                  placeholder="请输入学号"
                  clearable
                />
              </el-form-item>

              <el-form-item label="手机号码" prop="phone">
                <el-input 
                  v-model="form.phone" 
                  placeholder="请输入手机号码"
                  maxlength="11"
                  clearable
                />
                <div class="field-tip" v-if="originalForm.phone !== form.phone">
                  <el-icon color="#67c23a"><Check /></el-icon>
                  已修改
                </div>
              </el-form-item>

              <el-form-item label="邮箱地址" prop="email">
                <el-input 
                  v-model="form.email" 
                  placeholder="请输入邮箱地址"
                  type="email"
                  clearable
                />
                <div class="field-tip" v-if="originalForm.email !== form.email">
                  <el-icon color="#67c23a"><Check /></el-icon>
                  已修改
                </div>
              </el-form-item>

              <el-form-item>
                <div class="form-actions">
                  <el-button 
                    type="primary" 
                    :loading="loading" 
                    @click="submitForm"
                    :disabled="!hasChanges"
                    icon="Check"
                  >
                    保存修改
                  </el-button>
                  <el-button 
                    @click="resetForm"
                    :disabled="!hasChanges"
                    icon="Refresh"
                  >
                    重置
                  </el-button>
                  <el-button 
                    type="info" 
                    @click="refreshUserInfo"
                    icon="RefreshRight"
                  >
                    刷新信息
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="账号信息" name="account">
            <div class="account-info-grid">
              <div class="info-item">
                <div class="info-label">
                  <el-icon><User /></el-icon>
                  <span>用户名</span>
                </div>
                <div class="info-value">{{ userInfo.username }}</div>
              </div>
              
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Calendar /></el-icon>
                  <span>注册时间</span>
                </div>
                <div class="info-value">{{ formatDate(userInfo.create_time) }}</div>
              </div>
              
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Clock /></el-icon>
                  <span>最后登录</span>
                </div>
                <div class="info-value">{{ formatDate(userInfo.last_login) }}</div>
              </div>
              
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Setting /></el-icon>
                  <span>用户角色</span>
                </div>
                <div class="info-value">
                  <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'success'" size="small">
                    {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
                  </el-tag>
                </div>
              </div>
              
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Lock /></el-icon>
                  <span>账号状态</span>
                </div>
                <div class="info-value">
                  <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'" size="small">
                    {{ userInfo.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </div>
              </div>
              
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Message /></el-icon>
                  <span>邮箱</span>
                </div>
                <div class="info-value">{{ userInfo.email || '未设置' }}</div>
              </div>
              
              <div class="info-item">
                <div class="info-label">
                  <el-icon><Iphone /></el-icon>
                  <span>手机号</span>
                </div>
                <div class="info-value">{{ userInfo.phone || '未设置' }}</div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 修改记录（可选） -->
    <div class="update-history" v-if="updateHistory.length > 0">
      <h3>最近更新记录</h3>
      <el-timeline>
        <el-timeline-item
          v-for="(record, index) in updateHistory.slice(0, 3)"
          :key="index"
          :timestamp="record.time"
          placement="top"
        >
          <el-card>
            <h4>{{ record.field }} 已更新</h4>
            <p>从 "{{ record.oldValue }}" 改为 "{{ record.newValue }}"</p>
            <p class="update-time">{{ formatTime(record.time) }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup>
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { 
  onMounted, reactive, ref, computed, watch 
} from 'vue'
import { 
  Upload, Check, Refresh, RefreshRight, 
  User, Calendar, Clock, Setting, Lock, Message, Iphone 
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const avatarPreview = ref('')
const activeTab = ref('basic')
const showSuccessMessage = ref(false)
const successMessage = ref('')
const updateHistory = ref([])

// 原始表单数据（用于比较变化）
const originalForm = reactive({
  username: '',
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

// 表单数据
const form = reactive({
  username: '',
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

// 用户信息
const userInfo = reactive({
  username: '',
  create_time: '',
  last_login: '',
  role: '',
  status: 1,
  email: '',
  phone: ''
})

// 计算属性：检查是否有更改
const hasChanges = computed(() => {
  return (
    form.real_name !== originalForm.real_name ||
    form.student_id !== originalForm.student_id ||
    form.phone !== originalForm.phone ||
    form.email !== originalForm.email
  )
})

// 表单验证规则
const rules = {
  real_name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  student_id: [
    { pattern: /^\d{8,15}$/, message: '学号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

onMounted(async () => {
  await loadUserInfo()
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userData = await userStore.getUserInfo()
    
    // 填充表单数据
    form.username = userData.username || ''
    form.real_name = userData.real_name || ''
    form.student_id = userData.student_id || ''
    form.phone = userData.phone || ''
    form.email = userData.email || ''
    
    // 保存原始数据
    Object.assign(originalForm, { ...form })
    
    // 填充用户信息
    userInfo.username = userData.username || ''
    userInfo.create_time = userData.create_time || ''
    userInfo.last_login = userData.last_login || ''
    userInfo.role = userData.role || 'user'
    userInfo.status = userData.status || 1
    userInfo.email = userData.email || ''
    userInfo.phone = userData.phone || ''
    
    // 设置头像预览
    avatarPreview.value = userData.avatar || '/default-avatar.png'
    
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

// 头像上传成功
const handleAvatarSuccess = (response) => {
  if (response.code === 0) {
    avatarPreview.value = response.data.url
    ElMessage.success('头像上传成功')
    addUpdateHistory('头像', '原头像', response.data.url)
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB')
    return false
  }

  return true
}

// 重置头像
const resetAvatar = () => {
  avatarPreview.value = '/default-avatar.png'
  ElMessage.info('已恢复默认头像')
}

// 添加更新记录
const addUpdateHistory = (field, oldValue, newValue) => {
  updateHistory.value.unshift({
    field,
    oldValue: oldValue || '空',
    newValue: newValue || '空',
    time: new Date().toISOString()
  })
  
  // 只保留最近10条记录
  if (updateHistory.value.length > 10) {
    updateHistory.value = updateHistory.value.slice(0, 10)
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value || !hasChanges.value) return
  
  try {
    await formRef.value.validate()
    
    loading.value = true
    
    // 收集变更的字段
    const changes = {}
    if (form.real_name !== originalForm.real_name) {
      changes.real_name = form.real_name
    }
    if (form.student_id !== originalForm.student_id) {
      changes.student_id = form.student_id
    }
    if (form.phone !== originalForm.phone) {
      changes.phone = form.phone
    }
    if (form.email !== originalForm.email) {
      changes.email = form.email
    }
    
    const updateData = {
      id: userStore.userInfo?.id,
      ...changes,
      avatar: avatarPreview.value
    }
    
    console.log('提交更新数据:', updateData)
    
    const response = await updateUserInfo(updateData)
    console.log('更新响应:', response)
    
    if (response.code === 200) {
      // 添加更新记录
      Object.keys(changes).forEach(field => {
        addUpdateHistory(
          getFieldLabel(field),
          originalForm[field] || '空',
          form[field] || '空'
        )
      })
      
      // 显示成功消息
      successMessage.value = `成功更新了 ${Object.keys(changes).length} 个字段`
      showSuccessMessage.value = true
      
      // 更新store中的用户信息
      userStore.updateUserInfo(updateData)
      
      // 更新原始数据
      Object.assign(originalForm, { ...form })
      
      // 3秒后自动隐藏成功消息
      setTimeout(() => {
        showSuccessMessage.value = false
      }, 3000)
      
      ElMessage.success('个人信息更新成功！')
      
      // 刷新显示的用户信息
      await loadUserInfo()
      
    } else {
      throw new Error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取字段标签
const getFieldLabel = (field) => {
  const labels = {
    real_name: '真实姓名',
    student_id: '学号',
    phone: '手机号',
    email: '邮箱',
    avatar: '头像'
  }
  return labels[field] || field
}

// 重置表单
const resetForm = () => {
  Object.assign(form, { ...originalForm })
  ElMessage.info('已重置表单')
}

// 刷新用户信息
const refreshUserInfo = async () => {
  try {
    await loadUserInfo()
    ElMessage.success('用户信息已刷新')
  } catch (error) {
    ElMessage.error('刷新失败: ' + error.message)
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未记录'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 监听表单变化
watch(() => form, (newVal) => {
  console.log('表单已修改:', hasChanges.value)
}, { deep: true })
</script>

<style scoped>
.user-profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.page-title {
  margin-bottom: 30px;
  text-align: center;
}

.page-title h2 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 600;
}

.page-title p {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
}

/* 成功提示 */
.success-alert {
  margin-bottom: 20px;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 用户卡片 */
.user-card {
  display: flex;
  gap: 40px;
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

@media (max-width: 992px) {
  .user-card {
    flex-direction: column;
    gap: 30px;
  }
}

/* 左侧边栏 */
.user-sidebar {
  flex: 0 0 250px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.avatar-section {
  text-align: center;
}

.main-avatar {
  border: 4px solid #f0f2f5;
  margin-bottom: 15px;
  transition: transform 0.3s;
}

.main-avatar:hover {
  transform: scale(1.05);
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.user-quick-info {
  text-align: center;
  width: 100%;
}

.user-quick-info h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 20px;
}

.user-role {
  margin-bottom: 10px;
}

.user-id {
  font-size: 14px;
  color: #95a5a6;
  background: #f8f9fa;
  padding: 6px 12px;
  border-radius: 20px;
  display: inline-block;
}

/* 右侧主内容 */
.user-main {
  flex: 1;
}

.profile-tabs {
  min-height: 400px;
}

.profile-form {
  padding: 20px 0;
}

.disabled-input {
  opacity: 0.7;
}

.field-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 4px;
}

.form-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  margin-top: 20px;
}

/* 账号信息网格 */
.account-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.info-item:hover {
  background: #e9ecef;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6c757d;
  font-weight: 500;
  min-width: 100px;
}

.info-label .el-icon {
  font-size: 18px;
}

.info-value {
  color: #2c3e50;
  font-weight: 500;
}

/* 更新记录 */
.update-history {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.update-history h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f2f5;
}

.update-time {
  font-size: 12px;
  color: #95a5a6;
  margin-top: 5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile-container {
    padding: 15px;
  }
  
  .user-card {
    padding: 20px;
  }
  
  .profile-form {
    padding: 15px 0;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .account-info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>