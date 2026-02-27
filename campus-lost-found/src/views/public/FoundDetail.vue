<template>
  <div class="detail-container" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-section">
      <div class="container">
        <el-button type="link" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-section">
      <div class="container">
        <div class="detail-card">
          <!-- 头部 -->
          <div class="detail-header">
            <div class="title-section">
              <h1>{{ detail.title }}</h1>
              <div class="meta-info">
                <el-tag :type="getStatusType(detail.status)" size="large">
                  {{ getStatusText(detail.status) }}
                </el-tag>
                <span class="publish-time">发布于 {{ formatDateTime(detail.create_time || detail.createTime) }}</span>
                <span class="item-id">编号：F{{ formatNumber(detail.id) }}</span>
              </div>
            </div>
            <div class="action-section">
              <el-button type="primary" v-if="canClaim" @click="claimItem">
                <el-icon><Check /></el-icon>认领物品
              </el-button>
              <el-button v-if="isOwner && detail.status === 'published'" type="success" @click="editItem">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
            </div>
          </div>

          <!-- 主体内容 -->
          <div class="detail-body">
            <div class="info-grid">
              <!-- 基本信息 -->
              <div class="info-section">
                <h3>基本信息</h3>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">物品名称：</span>
                    <span class="info-value">{{ detail.title }}</span>
                  </div>
                  <div v-if="detail.category" class="info-item">
                    <span class="info-label">物品分类：</span>
                    <span class="info-value">{{ detail.category }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">拾获地点：</span>
                    <span class="info-value">{{ detail.found_location || detail.foundLocation }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">拾获时间：</span>
                    <span class="info-value">{{ formatDateTime(detail.found_time || detail.foundTime) }}</span>
                  </div>
                  <div v-if="detail.storage_location || detail.storageLocation" class="info-item">
                    <span class="info-label">保管地点：</span>
                    <span class="info-value">{{ detail.storage_location || detail.storageLocation }}</span>
                  </div>
                  <div v-if="detail.claim_requirement || detail.claimRequirement" class="info-item">
                    <span class="info-label">认领要求：</span>
                    <span class="info-value">{{ detail.claim_requirement || detail.claimRequirement }}</span>
                  </div>
                </div>
              </div>

              <!-- 发布者信息 -->
              <div class="info-section">
                <h3>拾获者信息</h3>
                <div class="user-info">
                  <div class="avatar-name">
                    <el-avatar :src="detail.user?.avatar || '/default-avatar.png'" size="large" />
                    <div>
                      <div class="username">{{ detail.user?.username || detail.username || '匿名' }}</div>
                      <div v-if="detail.user?.college" class="user-college">
                        {{ detail.user.college }}
                      </div>
                    </div>
                  </div>
                  <div v-if="detail.contact_info || detail.contactInfo" class="contact-info">
                    <div class="contact-item">
                      <el-icon><Phone /></el-icon>
                      <span>联系方式：{{ detail.contact_info || detail.contactInfo }}</span>
                    </div>
                    <div class="contact-item">
                      <el-icon><Clock /></el-icon>
                      <span>认领有效期：{{ detail.claim_period || detail.claimPeriod || 30 }}天</span>
                    </div>
                  </div>
                  <div v-else-if="!isOwner" class="contact-tip">
                    请使用系统消息联系拾获者
                  </div>
                  <div class="claim-tip" v-if="!isOwner && detail.status === 'published'">
                    <el-icon><InfoFilled /></el-icon>
                    请仔细核对物品信息后再认领
                  </div>
                </div>
              </div>
            </div>

            <!-- 详细描述 -->
            <div v-if="detail.description" class="description-section">
              <h3>详细描述</h3>
              <div class="description-content">
                <pre>{{ detail.description }}</pre>
                <p class="note-text" v-if="detail.contact_info || detail.contactInfo">
                  注：为保护隐私，部分个人信息已隐藏。认领时请提供有效证件进行核对。
                </p>
              </div>
            </div>

            <!-- 图片展示 -->
            <div v-if="images && images.length > 0" class="images-section">
              <h3>物品图片</h3>
              <div class="images-grid">
                <el-image
                  v-for="(img, index) in images"
                  :key="index"
                  :src="img"
                  :preview-src-list="images"
                  :initial-index="index"
                  fit="cover"
                  class="detail-image"
                />
              </div>
            </div>

            <!-- 认领流程 -->
            <div v-if="!isOwner && detail.status === 'published'" class="claim-process">
              <h3>认领流程</h3>
              <div class="process-steps">
                <div class="process-step" :class="{ active: step >= 1 }">
                  <div class="step-number">1</div>
                  <div class="step-content">
                    <div class="step-title">核对信息</div>
                    <div class="step-desc">确认物品为本人所有</div>
                  </div>
                </div>
                <div class="process-step" :class="{ active: step >= 2 }">
                  <div class="step-number">2</div>
                  <div class="step-content">
                    <div class="step-title">在线认领</div>
                    <div class="step-desc">填写认领申请</div>
                  </div>
                </div>
                <div class="process-step" :class="{ active: step >= 3 }">
                  <div class="step-number">3</div>
                  <div class="step-content">
                    <div class="step-title">联系确认</div>
                    <div class="step-desc">与拾获者取得联系</div>
                  </div>
                </div>
                <div class="process-step" :class="{ active: step >= 4 }">
                  <div class="step-number">4</div>
                  <div class="step-content">
                    <div class="step-title">线下领取</div>
                    <div class="step-desc">前往保管地点领取</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 相关建议 -->
            <div class="tips-section">
              <h3>温馨提示</h3>
              <div class="tips-list">
                <div class="tip-item">
                  <el-icon><SuccessFilled /></el-icon>
                  <span>请妥善保管个人物品，避免丢失</span>
                </div>
                <div class="tip-item">
                  <el-icon><SuccessFilled /></el-icon>
                  <span>认领时请携带有效身份证件</span>
                </div>
                <div class="tip-item">
                  <el-icon><SuccessFilled /></el-icon>
                  <span>请在有效期内认领，逾期物品将上交学校处理</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 认领对话框 -->
    <el-dialog
      v-model="showClaimDialog"
      title="确认认领"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="claim-dialog">
        <p>请确认以下信息：</p>
        <ul>
          <li>物品名称：{{ detail?.title }}</li>
          <li>拾获地点：{{ detail?.found_location || detail?.foundLocation }}</li>
          <li>保管地点：{{ detail?.storage_location || detail?.storageLocation || detail?.found_location || detail?.foundLocation }}</li>
        </ul>
        <p class="warning-text">
          <el-icon><Warning /></el-icon>
          请确保此物品为您所有，虚假认领将影响您的信誉
        </p>
        <el-form :model="claimForm" label-width="80px">
          <el-form-item label="联系方式" required>
            <el-input v-model="claimForm.contact" placeholder="请输入您的联系电话" />
          </el-form-item>
          <el-form-item label="认领说明">
            <el-input
              v-model="claimForm.description"
              type="textarea"
              :rows="3"
              placeholder="请描述您的认领理由或物品特征"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showClaimDialog = false">取消</el-button>
        <el-button type="primary" @click="submitClaim" :loading="claimLoading">
          确认认领
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

// 导入图标
import {
  ArrowLeft,
  Check,
  Edit,
  Message,
  Phone,
  Clock,
  InfoFilled,
  SuccessFilled,
  Warning
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const showClaimDialog = ref(false)
const claimLoading = ref(false)
const step = ref(1)
const detail = ref({})

// 状态映射
const statusMap = {
  pending: { text: '待审核', type: 'warning' },
  published: { text: '待认领', type: 'success' },
  claimed: { text: '已认领', type: 'info' },
  completed: { text: '已完成', type: 'primary' },
  closed: { text: '已关闭', type: 'danger' }
}

const claimForm = ref({
  contact: '',
  description: ''
})

const isLogin = computed(() => userStore.isLogin)
const isOwner = computed(() => {
  return detail.value.user?.id === userStore.userId || detail.value.userId === userStore.userId
})

const canClaim = computed(() => {
  if (!isLogin.value || isOwner.value || !detail.value) return false
  return detail.value.status === 'published'
})

const images = computed(() => {
  if (!detail.value.images) return []
  return Array.isArray(detail.value.images) 
    ? detail.value.images 
    : detail.value.images.split(',').filter(img => img.trim())
})

onMounted(() => {
  loadDetail()
})

// 加载详情
const loadDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    // 调用后端API获取招领详情 - 使用与 LostDetail.vue 相同的方式
    const response = await fetch(`/api/foundapi/detail?id=${id}`)
    const result = await response.json()
    
    console.log('招领详情API响应:', result)
    
    if (result.code === 200) {
      detail.value = result.data || {}
    } else {
      ElMessage.error(result.msg || '获取招领详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载招领详情失败:', error)
    ElMessage.error('加载失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return dateString
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return dateString
  }
}

// 格式化ID编号
const formatNumber = (id) => {
  if (!id) return '00000000'
  return String(id).padStart(8, '0')
}

// 获取状态文本和类型
const getStatusText = (status) => {
  return statusMap[status]?.text || status || '未知'
}

const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

// 认领物品
const claimItem = () => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (detail.value.status !== 'published') {
    ElMessage.warning('该物品当前不可认领')
    return
  }
  
  showClaimDialog.value = true
}

// 提交认领
const submitClaim = async () => {
  if (!claimForm.value.contact.trim()) {
    ElMessage.warning('请输入联系方式')
    return
  }

  claimLoading.value = true
  try {
    // 调用后端认领接口
    const response = await fetch(`/api/foundapi/${detail.value.id}/claim`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(claimForm.value)
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success('认领申请已提交，请等待拾获者确认')
      showClaimDialog.value = false
      
      // 重新加载详情，更新状态
      await loadDetail()
      
      step.value = 2
      setTimeout(() => {
        step.value = 3
        ElMessage.info('拾获者已收到您的认领申请，请保持电话畅通')
      }, 2000)
    } else {
      ElMessage.error(result.msg || '认领失败')
    }
  } catch (error) {
    console.error('认领失败:', error)
    ElMessage.error('认领失败，请稍后重试')
  } finally {
    claimLoading.value = false
  }
}

// 编辑物品
const editItem = () => {
  router.push(`/user/found/edit/${route.params.id}`)
}
</script>

<style scoped>
/* 与 LostDetail.vue 相同的样式，保持一致性 */
.detail-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 返回按钮 */
.back-section {
  background: white;
  padding: 20px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 内容区域 */
.content-section {
  padding: 30px 0;
}

/* 详情卡片 */
.detail-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 头部 */
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 30px;
  border-bottom: 1px solid #f0f2f5;
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
}

.title-section h1 {
  margin: 0 0 15px 0;
  font-size: 28px;
  color: #2c3e50;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.publish-time {
  color: #7f8c8d;
  font-size: 14px;
}

.item-id {
  padding: 4px 8px;
  background: #e3f2fd;
  border-radius: 4px;
  color: #1976d2;
  font-size: 12px;
  font-family: monospace;
}

.action-section {
  display: flex;
  gap: 10px;
}

/* 主体内容 */
.detail-body {
  padding: 30px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

/* 信息区块 */
.info-section {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.info-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e9ecef;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
}

.info-label {
  width: 100px;
  color: #6c757d;
  font-weight: 500;
}

.info-value {
  flex: 1;
  color: #495057;
  word-break: break-word;
}

/* 用户信息 */
.user-info {
  margin-top: 20px;
}

.avatar-name {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.username {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.user-college {
  color: #6c757d;
  font-size: 14px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #495057;
}

.contact-item .el-icon {
  color: #667eea;
}

.contact-tip {
  padding: 15px;
  background: #e3f2fd;
  border-radius: 6px;
  color: #1976d2;
  font-size: 14px;
  text-align: center;
  margin-bottom: 20px;
}

.claim-tip {
  padding: 10px;
  background: #fff3e0;
  border-radius: 6px;
  color: #f57c00;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 描述区块 */
.description-section {
  margin-bottom: 40px;
}

.description-section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 18px;
}

.description-content {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.description-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  color: #495057;
  line-height: 1.6;
}

.note-text {
  margin-top: 15px;
  padding: 10px;
  background: #fff8e1;
  border-radius: 4px;
  color: #ff8f00;
  font-size: 14px;
}

/* 图片区块 */
.images-section {
  margin-bottom: 40px;
}

.images-section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 18px;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.detail-image {
  width: 100%;
  height: 150px;
  border-radius: 8px;
  cursor: pointer;
  object-fit: cover;
  transition: transform 0.3s;
}

.detail-image:hover {
  transform: scale(1.05);
}

/* 认领流程 */
.claim-process {
  margin: 40px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.claim-process h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.process-steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.process-step {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  border: 2px solid #e9ecef;
  transition: all 0.3s;
}

.process-step.active {
  border-color: #4CAF50;
  background: #f1f8e9;
}

.process-step.active .step-number {
  background: #4CAF50;
  color: white;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e9ecef;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  transition: all 0.3s;
}

.step-content {
  flex: 1;
}

.step-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.step-desc {
  font-size: 12px;
  color: #6c757d;
}

/* 温馨提示 */
.tips-section {
  margin: 30px 0;
  padding: 20px;
  background: #e3f2fd;
  border-radius: 8px;
  border-left: 4px solid #2196f3;
}

.tips-section h3 {
  margin: 0 0 15px 0;
  color: #1976d2;
  font-size: 18px;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #0d47a1;
}

.tip-item .el-icon {
  color: #4CAF50;
}

/* 认领对话框 */
.claim-dialog ul {
  margin: 10px 0 20px 20px;
  color: #495057;
}

.claim-dialog li {
  margin-bottom: 5px;
}

.warning-text {
  padding: 10px;
  background: #fff3e0;
  border-radius: 4px;
  color: #f57c00;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 15px 0;
}

.warning-text .el-icon {
  color: #ff9800;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .action-section {
    width: 100%;
    justify-content: flex-start;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-body {
    padding: 20px;
  }
  
  .info-item {
    flex-direction: column;
    gap: 5px;
  }
  
  .info-label {
    width: auto;
  }
  
  .process-steps {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .meta-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>