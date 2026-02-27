<template>
  <div class="user-lost-list-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>我的失物</h2>
      <p>管理您发布的丢失物品信息</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="$router.push('/user/lost/UserLostAdd')">
        <el-icon><Plus /></el-icon>发布新失物
      </el-button>
      
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索我的失物..."
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 状态筛选 -->
    <div class="status-filter">
      <el-radio-group v-model="filterStatus" @change="handleStatusFilter">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="pending">待处理</el-radio-button>
        <el-radio-button label="published">已发布</el-radio-button>
        <el-radio-button label="found">已找到</el-radio-button>
        <el-radio-button label="closed">已关闭</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 状态统计卡片 -->
    <div v-if="!loading && items.length > 0" class="status-cards">
      <el-card 
        v-for="(stat, status) in statusStats" 
        :key="status"
        class="status-card"
        :class="`status-${status}`"
        shadow="hover"
        @click="setFilterStatus(status)"
      >
        <div class="stat-content">
          <div class="stat-count">{{ stat.count }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
        <div class="stat-percent">
          {{ stat.percent }}%
        </div>
      </el-card>
    </div>

    <!-- 物品列表 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="items.length === 0" class="empty-container">
      <el-empty description="暂无失物信息" />
      <div class="empty-action">
        <el-button type="primary" @click="$router.push('/user/lost/UserLostAdd')">
          发布一个失物
        </el-button>
      </div>
    </div>

    <div v-else class="items-table">
      <el-table :data="items" style="width: 100%">
        <el-table-column prop="title" label="物品名称" width="200">
          <template #default="{ row }">
            <div class="item-title-cell">
              <span class="title-text">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryText(row.category) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="lostLocation" label="丢失地点" />
        
        <el-table-column prop="lostTime" label="丢失时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.lostTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="发布时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="viewDetail(row.id)">
                查看
              </el-button>
              <el-button
                v-if="row.status !== 'closed'"
                type="warning"
                size="small"
                @click="editItem(row.id)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="deleteItem(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
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
import { deleteLostItem, getUserLostList } from '@/api/lost'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// 搜索关键词
const searchKeyword = ref('')

// 筛选状态
const filterStatus = ref('')

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const loading = ref(false)
const items = ref([])
const allItems = ref([]) // 存储所有数据，用于统计

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
  pending: { text: '待处理', type: 'warning', color: '#e6a23c' },
  published: { text: '已发布', type: 'info', color: '#909399' },
  found: { text: '已找到', type: 'success', color: '#67c23a' },
  closed: { text: '已关闭', type: 'danger', color: '#f56c6c' }
}

// 计算状态统计数据
const statusStats = computed(() => {
  const stats = {}
  const total = allItems.value.length
  
  // 初始化所有状态
  Object.keys(statusMap).forEach(status => {
    const count = allItems.value.filter(item => item.status === status).length
    const percent = total > 0 ? ((count / total) * 100).toFixed(1) : 0
    stats[status] = {
      count,
      label: statusMap[status].text,
      percent,
      color: statusMap[status].color
    }
  })
  
  // 添加总数
  stats.total = {
    count: total,
    label: '全部',
    percent: 100,
    color: '#409eff'
  }
  
  return stats
})

onMounted(() => {
  fetchItems()
})

// 获取数据
const fetchItems = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.size,
      keyword: searchKeyword.value,
      status: filterStatus.value,
      userId: userStore.userInfo?.id
    }
    
    const response = await getUserLostList(params)
    
    if (response.code === 200 && response.data) {
      const itemsData = response.data.items || []
      
      // 处理数据
      const processedItems = itemsData.map(item => ({
        id: item.id || item.itemId,
        title: item.title || '',
        category: item.category || 'others',
        lostLocation: item.lostLocation || item.lost_location || '',
        lostTime: item.lostTime || item.lost_time || '',
        status: item.status || 'pending',
        createTime: item.createTime || item.create_time || ''
      }))
      
      // 存储所有数据
      allItems.value = processedItems
      
      // 应用前端筛选
      let filteredItems = processedItems
      
      if (filterStatus.value) {
        filteredItems = filteredItems.filter(item => item.status === filterStatus.value)
      }
      
      if (searchKeyword.value.trim()) {
        const keyword = searchKeyword.value.toLowerCase()
        filteredItems = filteredItems.filter(item => 
          item.title.toLowerCase().includes(keyword) ||
          item.lostLocation.toLowerCase().includes(keyword)
        )
      }
      
      // 应用前端分页
      const totalItems = filteredItems.length
      const start = (pagination.current - 1) * pagination.size
      const end = start + pagination.size
      
      items.value = filteredItems.slice(start, end)
      pagination.total = totalItems
      
    } else {
      items.value = []
      allItems.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取用户失物列表失败:', error)
    ElMessage.error('加载失败')
    items.value = []
    allItems.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 设置筛选状态
const setFilterStatus = (status) => {
  filterStatus.value = status === 'total' ? '' : status
  pagination.current = 1
  fetchItems()
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchItems()
}

// 状态筛选
const handleStatusFilter = () => {
  pagination.current = 1
  fetchItems()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchItems()
}

const handleCurrentChange = (page) => {
  pagination.current = page
  fetchItems()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return dateString
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString().slice(0, 5)
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

// 查看详情
const viewDetail = (id) => {
  router.push(`/lost/${id}`)
}

// 编辑
const editItem = (id) => {
  router.push(`/user/lost/edit/${id}`)
}

// 删除
const deleteItem = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除"${item.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteLostItem(item.id)
    if (response.success) {
      ElMessage.success('删除成功')
      fetchItems()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.user-lost-list-container {
  padding: 20px;
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
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-box {
  width: 300px;
}

/* 状态筛选 */
.status-filter {
  margin-bottom: 20px;
}

/* 状态统计卡片 */
.status-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.status-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid #409eff;
}

.status-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.status-card.status-pending {
  border-left-color: #e6a23c;
}

.status-card.status-published {
  border-left-color: #909399;
}

.status-card.status-found {
  border-left-color: #67c23a;
}

.status-card.status-closed {
  border-left-color: #f56c6c;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-count {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
}

.stat-percent {
  font-size: 12px;
  color: #409eff;
  margin-top: 8px;
  text-align: right;
}

/* 加载状态 */
.loading-container {
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 20px;
}

/* 空状态 */
.empty-container {
  background: white;
  border-radius: 10px;
  padding: 60px 20px;
  text-align: center;
}

.empty-action {
  margin-top: 20px;
}

/* 表格 */
.items-table {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.item-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 分页 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    width: 100%;
  }
  
  .status-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .items-table {
    padding: 10px;
    overflow-x: auto;
  }
  
  .action-buttons {
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .status-cards {
    grid-template-columns: 1fr;
  }
}
</style>