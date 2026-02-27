<template>
    <div class="search-results-container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="container">
          <h1>搜索结果</h1>
          <p>关键词："{{ searchKeyword }}"</p>
        </div>
      </div>
  
      <!-- 搜索框 -->
      <div class="search-section">
        <div class="container">
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="请输入搜索关键词..."
              size="large"
              @keyup.enter="performSearch"
            >
              <template #append>
                <el-button type="primary" @click="performSearch">
                  <el-icon><Search /></el-icon>搜索
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>
  
      <!-- 搜索结果 -->
      <div class="results-section">
        <div class="container">
          <!-- 标签页切换 -->
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="失物" name="lost"></el-tab-pane>
            <el-tab-pane label="招领" name="found"></el-tab-pane>
          </el-tabs>
  
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="6" animated />
          </div>
  
          <!-- 空状态 -->
          <div v-else-if="!hasResults" class="empty-container">
            <el-empty description="暂无搜索结果" />
          </div>
  
          <!-- 有结果 -->
          <div v-else>
            <!-- 失物结果 -->
            <div v-if="(activeTab === 'all' || activeTab === 'lost') && lostItems.length > 0" class="result-section">
              <h3 class="result-title">
                <el-icon><Lost /></el-icon>
                失物信息 ({{ lostItems.length }})
              </h3>
              <div class="items-grid">
                <div v-for="item in lostItems" :key="`lost-${item.id}`" class="item-card" @click="viewLostDetail(item.id)">
                  <div class="item-header">
                    <el-tag type="danger" size="small">失物</el-tag>
                    <span class="item-time">{{ formatDate(item.lost_time || item.lostTime) }}</span>
                  </div>
                  <div class="item-body">
                    <h4 class="item-title">{{ item.title }}</h4>
                    <div class="item-info">
                      <div class="info-row">
                        <el-icon><Location /></el-icon>
                        <span>丢失地点：{{ item.lost_location || item.lostLocation }}</span>
                      </div>
                      <div v-if="item.description" class="item-description">
                        {{ truncateText(item.description, 80) }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
  
            <!-- 招领结果 -->
            <div v-if="(activeTab === 'all' || activeTab === 'found') && foundItems.length > 0" class="result-section">
              <h3 class="result-title">
                <el-icon><Collection /></el-icon>
                招领信息 ({{ foundItems.length }})
              </h3>
              <div class="items-grid">
                <div v-for="item in foundItems" :key="`found-${item.id}`" class="item-card" @click="viewFoundDetail(item.id)">
                  <div class="item-header">
                    <el-tag type="success" size="small">招领</el-tag>
                    <span class="item-time">{{ formatDate(item.found_time || item.foundTime) }}</span>
                  </div>
                  <div class="item-body">
                    <h4 class="item-title">{{ item.title }}</h4>
                    <div class="item-info">
                      <div class="info-row">
                        <el-icon><Location /></el-icon>
                        <span>拾获地点：{{ item.found_location || item.foundLocation }}</span>
                      </div>
                      <div v-if="item.description" class="item-description">
                        {{ truncateText(item.description, 80) }}
                      </div>
                    </div>
                  </div>
                </div>
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
  import { searchItems } from '@/api/home'
  
  const route = useRoute()
  const router = useRouter()
  
  const searchKeyword = ref(route.query.keyword || '')
  const activeTab = ref('all')
  const loading = ref(false)
  const lostItems = ref([])
  const foundItems = ref([])
  
  const hasResults = computed(() => {
    return lostItems.value.length > 0 || foundItems.value.length > 0
  })
  
  onMounted(() => {
    if (searchKeyword.value) {
      performSearch()
    }
  })
  
  // 执行搜索
  const performSearch = async () => {
    if (!searchKeyword.value.trim()) {
      ElMessage.warning('请输入搜索关键词')
      return
    }
  
    loading.value = true
    try {
      // 搜索失物
      const lostResponse = await searchItems(searchKeyword.value, 'lost')
      if (lostResponse.code === 200 && lostResponse.data) {
        lostItems.value = lostResponse.data.items || lostResponse.data.records || []
      }
  
      // 搜索招领
      const foundResponse = await searchItems(searchKeyword.value, 'found')
      if (foundResponse.code === 200 && foundResponse.data) {
        foundItems.value = foundResponse.data.items || foundResponse.data.records || []
      }
    } catch (error) {
      console.error('搜索失败:', error)
      ElMessage.error('搜索失败，请稍后重试')
    } finally {
      loading.value = false
    }
  
    // 更新URL
    router.replace({
      path: '/search',
      query: { keyword: searchKeyword.value }
    })
  }
  
  // 标签页切换
  const handleTabClick = () => {
    // 可以在这里添加按标签筛选的逻辑
  }
  
  // 查看失物详情
  const viewLostDetail = (id) => {
    router.push(`/lost/${id}`)
  }
  
  // 查看招领详情
  const viewFoundDetail = (id) => {
    router.push(`/found/${id}`)
  }
  
  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return ''
    try {
      const date = new Date(dateString)
      if (isNaN(date.getTime())) return dateString
      return date.toLocaleDateString()
    } catch {
      return dateString
    }
  }
  
  // 截断文本
  const truncateText = (text, length) => {
    if (!text) return ''
    return text.length > length ? text.substring(0, length) + '...' : text
  }
  </script>
  
  <style scoped>
  .search-results-container {
    min-height: 100vh;
    background: #f5f7fa;
  }
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  /* 页面头部 */
  .page-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 40px 0;
    text-align: center;
  }
  
  .page-header h1 {
    font-size: 32px;
    margin-bottom: 10px;
  }
  
  .page-header p {
    font-size: 16px;
    opacity: 0.9;
  }
  
  /* 搜索区域 */
  .search-section {
    background: white;
    padding: 30px 0;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 30px;
  }
  
  .search-box {
    max-width: 800px;
    margin: 0 auto;
  }
  
  /* 结果区域 */
  .results-section {
    padding: 20px 0 40px;
  }
  
  /* 标签页 */
  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
  }
  
  /* 结果标题 */
  .result-title {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #2c3e50;
    margin: 30px 0 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #f0f2f5;
  }
  
  .result-title .el-icon {
    font-size: 20px;
  }
  
  /* 物品网格 */
  .items-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
  }
  
  /* 物品卡片 */
  .item-card {
    background: white;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    cursor: pointer;
  }
  
  .item-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
  }
  
  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background: #f8f9fa;
    border-bottom: 1px solid #e9ecef;
  }
  
  .item-time {
    font-size: 12px;
    color: #6c757d;
  }
  
  .item-body {
    padding: 20px;
  }
  
  .item-title {
    margin: 0 0 15px 0;
    font-size: 16px;
    color: #2c3e50;
  }
  
  .item-info {
    font-size: 14px;
    color: #495057;
  }
  
  .info-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
  
  .info-row .el-icon {
    color: #6c757d;
  }
  
  .item-description {
    margin-top: 10px;
    color: #6c757d;
    line-height: 1.5;
  }
  
  /* 加载状态 */
  .loading-container {
    background: white;
    border-radius: 10px;
    padding: 30px;
    margin: 20px 0;
  }
  
  /* 空状态 */
  .empty-container {
    background: white;
    border-radius: 10px;
    padding: 60px 20px;
    margin: 20px 0;
    text-align: center;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .page-header {
      padding: 30px 0;
    }
    
    .page-header h1 {
      font-size: 24px;
    }
    
    .items-grid {
      grid-template-columns: 1fr;
    }
  }
  </style>