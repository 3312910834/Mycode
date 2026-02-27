<template>
    <div class="detail-container" v-loading="loading">
      <!-- 返回按钮 -->
      <div class="back-section">
        <div class="container">
          <el-button type="text" @click="$router.back()">
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
                </div>
              </div>
              <div class="action-section">
                <el-button type="primary" v-if="!isOwner && isLogin" @click="contactOwner">
                  <el-icon><Message /></el-icon>联系失主
                </el-button>
                <el-button v-if="isOwner" type="success" @click="editItem">
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
                      <span class="info-value">{{ getCategoryText(detail.category) }}</span>
                    </div>
                    <div class="info-item">
                      <span class="info-label">丢失地点：</span>
                      <span class="info-value">{{ detail.lost_location || detail.lostLocation }}</span>
                    </div>
                    <div class="info-item">
                      <span class="info-label">丢失时间：</span>
                      <span class="info-value">{{ formatDateTime(detail.lost_time || detail.lostTime) }}</span>
                    </div>
                    <div v-if="detail.features" class="info-item">
                      <span class="info-label">物品特征：</span>
                      <span class="info-value">{{ detail.features }}</span>
                    </div>
                  </div>
                </div>
  
                <!-- 发布者信息 -->
                <div class="info-section">
                  <h3>发布者信息</h3>
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
                    <div v-if="isOwner" class="contact-info">
                      <div class="contact-item">
                        <el-icon><Phone /></el-icon>
                        <span>{{ detail.user?.phone || detail.phone || '未公开' }}</span>
                      </div>
                      <div class="contact-item">
                        <el-icon><Message /></el-icon>
                        <span>{{ detail.user?.email || detail.email || '未公开' }}</span>
                      </div>
                    </div>
                    <div v-else class="contact-tip">
                      请先联系拾主确认物品信息
                    </div>
                  </div>
                </div>
              </div>
  
              <!-- 详细描述 -->
              <div v-if="detail.description" class="description-section">
                <h3>详细描述</h3>
                <div class="description-content">
                  <pre>{{ detail.description }}</pre>
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
  
              <!-- 状态变化记录 -->
              <div v-if="statusHistory && statusHistory.length > 0" class="status-section">
                <h3>状态记录</h3>
                <el-timeline>
                  <el-timeline-item
                    v-for="record in statusHistory"
                    :key="record.id"
                    :timestamp="formatDateTime(record.create_time)"
                    :type="record.status === 'found' ? 'success' : 'primary'"
                  >
                    {{ getStatusRecordText(record) }}
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { useUserStore } from '@/store/user'
  
  const route = useRoute()
  const router = useRouter()
  const userStore = useUserStore()
  
  const loading = ref(true)
  const detail = ref({})
  
  // 分类映射
  const categoryMap = {
    electronics: '电子产品',
    study: '学习用品',
    cards: '证件卡类',
    clothing: '衣物饰品',
    books: '书籍资料',
    others: '其他物品'
  }
  
  // 状态映射
  const statusMap = {
    pending: { text: '待处理', type: 'warning' },
    published: { text: '已发布', type: 'info' },
    found: { text: '已找到', type: 'success' },
    closed: { text: '已关闭', type: 'danger' }
  }
  
  const isLogin = computed(() => userStore.isLogin)
  const isOwner = computed(() => {
    return detail.value.user?.id === userStore.userId || detail.value.userId === userStore.userId
  })
  
  const images = computed(() => {
    if (!detail.value.images) return []
    return Array.isArray(detail.value.images) 
      ? detail.value.images 
      : detail.value.images.split(',').filter(img => img.trim())
  })
  
  const statusHistory = computed(() => {
    return detail.value.statusHistory || []
  })
  
  onMounted(() => {
    loadDetail()
  })
  
  const loadDetail = async () => {
    loading.value = true
    try {
      const id = route.params.id
      // 这里调用后端接口获取详情
      const response = await fetch(`/api/lostapi/detail?id=${id}`)
      const result = await response.json()
      
      if (result.code === 200) {
        detail.value = result.data || {}
      } else {
        ElMessage.error('获取详情失败')
        router.back()
      }
    } catch (error) {
      console.error('加载详情失败:', error)
      ElMessage.error('加载失败')
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
  
  // 获取分类文本
  const getCategoryText = (category) => {
    return categoryMap[category] || category || '未分类'
  }
  
  // 获取状态文本和类型
  const getStatusText = (status) => {
    return statusMap[status]?.text || status || '未知'
  }
  
  const getStatusType = (status) => {
    return statusMap[status]?.type || 'info'
  }
  
  // 获取状态记录文本
  const getStatusRecordText = (record) => {
    const statusText = getStatusText(record.status)
    return `${statusText} - ${record.remark || '状态更新'}`
  }
  
  // 联系失主
  const contactOwner = () => {
    if (!isLogin.value) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    ElMessage.info('联系功能开发中')
  }
  
  // 编辑物品
  const editItem = () => {
    router.push(`/user/lost/edit/${route.params.id}`)
  }
  </script>
  
  <style scoped>
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
  }
  
  .publish-time {
    color: #7f8c8d;
    font-size: 14px;
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
  
  /* 状态区块 */
  .status-section {
    margin-bottom: 20px;
  }
  
  .status-section h3 {
    margin: 0 0 15px 0;
    color: #2c3e50;
    font-size: 18px;
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
  }
  </style>