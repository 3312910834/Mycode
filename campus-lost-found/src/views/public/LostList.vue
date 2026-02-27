<template>
  <div class="lost-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <h1>失物公示</h1>
        <p>寻找丢失物品，尽快物归原主</p>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="container">
        <div class="filter-box">
          <el-form :model="filterForm" inline>
            <!-- 关键词搜索 -->
            <el-form-item>
              <el-input
                v-model="filterForm.keyword"
                placeholder="搜索物品名称、地点、描述..."
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
            </el-form-item>

            <!-- 分类多选 -->
            <el-form-item>
              <el-select
                v-model="filterForm.categories"
                placeholder="选择分类"
                multiple
                collapse-tags
                collapse-tags-tooltip
                @change="handleSearch"
              >
                <el-option label="电子产品" value="electronics" />
                <el-option label="学习用品" value="study" />
                <el-option label="证件卡类" value="cards" />
                <el-option label="衣物饰品" value="clothing" />
                <el-option label="书籍资料" value="books" />
                <el-option label="其他物品" value="others" />
              </el-select>
            </el-form-item>

            <!-- 状态多选 -->
            <el-form-item>
              <el-select
                v-model="filterForm.statuses"
                placeholder="选择状态"
                multiple
                collapse-tags
                collapse-tags-tooltip
                @change="handleSearch"
              >
                <el-option label="待处理" value="pending" />
                <el-option label="已发布" value="published" />
                <el-option label="已找到" value="found" />
                <el-option label="已关闭" value="closed" />
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="resetFilter">重置</el-button>
              <el-button v-if="isLogin" type="success" @click="$router.push('/user/UserLostList')">
                <el-icon><Plus /></el-icon>发布失物
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 筛选标签区域 -->
        <div v-if="hasActiveFilters" class="filter-tags">
          <div class="tags-header">
            <span class="tags-title">当前筛选：</span>
            <span class="result-count">
              共 <strong>{{ pagination.total }}</strong> 个结果
            </span>
          </div>
          
          <div class="tags-container">
            <!-- 关键词标签 -->
            <el-tag
              v-if="filterForm.keyword"
              closable
              type="info"
              size="small"
              @close="removeKeyword"
              class="filter-tag"
            >
              关键词：{{ filterForm.keyword }}
            </el-tag>
            
            <!-- 分类标签 -->
            <el-tag
              v-for="category in filterForm.categories"
              :key="`category-${category}`"
              closable
              size="small"
              @close="removeCategory(category)"
              class="filter-tag"
            >
              分类：{{ getCategoryText(category) }}
            </el-tag>
            
            <!-- 状态标签 -->
            <el-tag
              v-for="status in filterForm.statuses"
              :key="`status-${status}`"
              closable
              :type="getStatusType(status)"
              size="small"
              @close="removeStatus(status)"
              class="filter-tag"
            >
              状态：{{ getStatusText(status) }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 物品列表 -->
    <div class="list-section">
      <div class="container">
        <!-- 调试信息区域 -->
        <div v-if="isDevelopment" class="debug-info">
          <p>调试信息：总条数 {{ pagination.total }}，当前页 {{ pagination.current }}/{{ totalPages }}，每页 {{ pagination.size }} 条</p>
          <p>分页状态：{{ hasPagination ? '启用分页' : '数据不足一页' }}，当前数据 {{ items.length }} 条</p>
        </div>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- 搜索结果为空 -->
        <div v-else-if="items.length === 0 && hasActiveFilters" class="search-empty-container">
          <el-empty :description="emptyDescription" />
          <div class="empty-suggestions">
            <p>建议：</p>
            <ul>
              <li>尝试不同的关键词</li>
              <li>减少筛选条件</li>
              <li>检查拼写错误</li>
            </ul>
            <div class="empty-actions">
              <el-button type="primary" @click="resetFilter">
                查看所有失物
              </el-button>
              <el-button type="success" @click="$router.push('/user/UserLostList')">
                发布失物信息
              </el-button>
            </div>
          </div>
        </div>

        <!-- 初始为空（无搜索条件） -->
        <div v-else-if="items.length === 0" class="empty-container">
          <el-empty description="暂无失物信息" />
          <div class="initial-empty-actions">
            <el-button v-if="isLogin" type="success" @click="$router.push('/user/UserLostList')">
              <el-icon><Plus /></el-icon>发布第一条失物信息
            </el-button>
          </div>
        </div>

        <!-- 有数据 -->
        <div v-else>
          <!-- 物品网格 -->
          <div class="items-grid">
            <div v-for="item in items" :key="item.id" class="item-card">
              <div class="item-header">
                <el-tag :type="getStatusType(item.status)" size="small">
                  {{ getStatusText(item.status) }}
                </el-tag>
                <span class="item-time">{{ formatDate(item.lost_time) }}</span>
              </div>

              <div class="item-body">
                <h3 class="item-title" @click="viewDetail(item.id)">
                  <!-- 高亮搜索关键词 -->
                  <span v-if="filterForm.keyword" v-html="highlightKeyword(item.title)"></span>
                  <span v-else>{{ item.title }}</span>
                </h3>
                
                <div class="item-info">
                  <div class="info-row">
                    <el-icon><Location /></el-icon>
                    <!-- 高亮搜索关键词 -->
                    <span v-if="filterForm.keyword" v-html="highlightKeyword(item.lost_location, '丢失地点：')"></span>
                    <span v-else>丢失地点：{{ item.lost_location }}</span>
                  </div>
                  
                  <div v-if="item.category" class="info-row">
                    <el-icon><Folder /></el-icon>
                    <span>分类：{{ getCategoryText(item.category) }}</span>
                  </div>
                  
                  <div class="info-row">
                    <el-icon><User /></el-icon>
                    <span>发布者：{{ item.user?.username || item.loser_name || '匿名' }}</span>
                  </div>
                </div>

                <div v-if="item.description" class="item-description">
                  <!-- 高亮搜索关键词 -->
                  <p v-if="filterForm.keyword" v-html="highlightKeyword(truncateText(item.description, 100))"></p>
                  <p v-else>{{ truncateText(item.description, 100) }}</p>
                </div>

                <div v-if="item.images && item.images !== ''" class="item-images">
                  <el-image
                    v-for="(img, index) in item.images.split(',')"
                    :key="index"
                    :src="img"
                    :preview-src-list="item.images.split(',')"
                    :initial-index="index"
                    fit="cover"
                    class="item-image"
                  />
                </div>
              </div>

              <div class="item-footer">
                <el-button type="primary" size="small" @click="viewDetail(item.id)">
                  查看详情
                </el-button>
                <el-button v-if="isLogin" type="info" size="small" @click="contactOwner(item)">
                  联系失主
                </el-button>
              </div>
            </div>
          </div>

          <!-- 分页 - 修复：只有需要分页时才显示 -->
          <div v-if="hasPagination" class="pagination-container">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              background
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :disabled="pagination.total <= pagination.size"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 导入真实API
import { getLostList } from '@/api/lost'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// 开发环境判断
const isDevelopment = process.env.NODE_ENV === 'development'

const isLogin = computed(() => userStore.isLogin)

// 筛选表单（改为支持多选）
const filterForm = reactive({
  keyword: '',
  categories: [],  // 改为数组支持多选
  statuses: []     // 改为数组支持多选
})

// 分页 - 使用 v-model 绑定
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 数据
const loading = ref(false)
const items = ref([])

// 分类映射
const categoryMap = {
  electronics: '电子产品',
  study: '学习用品',
  cards: '证件卡类',
  clothing: '衣物饰品',
  books: '书籍资料',
  others: '其他物品'
}

// 状态映射 - 支持数字和字符串状态
const statusMap = {
  '0': { text: '待处理', type: 'warning' },
  '1': { text: '已发布', type: 'info' },
  '2': { text: '已找到', type: 'success' },
  '3': { text: '已关闭', type: 'danger' },
  // 支持字符串状态
  'pending': { text: '待处理', type: 'warning' },
  'published': { text: '已发布', type: 'info' },
  'found': { text: '已找到', type: 'success' },
  'closed': { text: '已关闭', type: 'danger' }
}

// 计算属性
const hasActiveFilters = computed(() => {
  return !!filterForm.keyword || 
         (filterForm.categories && filterForm.categories.length > 0) || 
         (filterForm.statuses && filterForm.statuses.length > 0)
})

const emptyDescription = computed(() => {
  if (filterForm.keyword) {
    return `没有找到包含"${filterForm.keyword}"的失物信息`
  } else if (filterForm.categories.length > 0) {
    const categoriesText = filterForm.categories.map(cat => getCategoryText(cat)).join('、')
    return `没有找到"${categoriesText}"分类的失物信息`
  } else if (filterForm.statuses.length > 0) {
    const statusesText = filterForm.statuses.map(status => getStatusText(status)).join('、')
    return `没有找到状态为"${statusesText}"的失物信息`
  }
  return '暂无失物信息'
})

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(pagination.total / pagination.size)
})

// 是否需要分页
const hasPagination = computed(() => {
  return pagination.total > pagination.size
})

onMounted(() => {
  fetchItems()
})

// 获取数据 - 使用真实API
const fetchItems = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,  // 当前页码
      size: pagination.size,     // 每页数量
      keyword: filterForm.keyword,
      // 将数组转换为逗号分隔的字符串，后端可能需要调整
      category: filterForm.categories.join(','),
      status: filterForm.statuses.join(',')
    }

    // 清理空参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    console.log('请求失物列表参数:', params)

    // 调用真实API
    const response = await getLostList(params)
    console.log('失物API响应:', response)
    
    // 根据后端返回的数据结构处理数据
    if (response && response.code === 200) {
      // 假设后端返回 { code: 200, data: { records: [], total: xx } } 格式
      items.value = response.data?.records || response.data?.list || response.data?.items || []
      pagination.total = Number(response.data?.total) || 0
    } else if (response && response.data) {
      // 兼容其他可能的响应结构
      items.value = response.data.records || response.data.list || response.data.items || []
      pagination.total = Number(response.data.total) || 0
    } else {
      console.error('API响应格式错误:', response)
      items.value = []
      pagination.total = 0
    }
    
    console.log('处理后的数据:', {
      itemsCount: items.value.length,
      total: pagination.total,
      currentPage: pagination.current,
      pageSize: pagination.size,
      hasPagination: hasPagination.value
    })
    
  } catch (error) {
    console.error('获取失物列表失败:', error)
    ElMessage.error('加载失败：' + (error.message || '网络错误'))
    
    // 错误时使用空数据
    items.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1 // 搜索时重置到第一页
  fetchItems()
}

// 重置筛选
const resetFilter = () => {
  filterForm.keyword = ''
  filterForm.categories = []
  filterForm.statuses = []
  pagination.current = 1
  fetchItems()
}

// 移除关键词
const removeKeyword = () => {
  filterForm.keyword = ''
  handleSearch()
}

// 移除分类
const removeCategory = (category) => {
  const index = filterForm.categories.indexOf(category)
  if (index > -1) {
    filterForm.categories.splice(index, 1)
    handleSearch()
  }
}

// 移除状态
const removeStatus = (status) => {
  const index = filterForm.statuses.indexOf(status)
  if (index > -1) {
    filterForm.statuses.splice(index, 1)
    handleSearch()
  }
}

// 分页处理
const handleSizeChange = (size) => {
  console.log('每页条数改变:', size)
  pagination.size = size
  pagination.current = 1
  fetchItems()
}

const handleCurrentChange = (page) => {
  console.log('当前页改变:', page)
  pagination.current = page
  fetchItems()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return dateString
    
    const now = new Date()
    const diffTime = Math.abs(now - date)
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
    
    if (diffDays === 0) {
      const diffHours = Math.floor(diffTime / (1000 * 60 * 60))
      if (diffHours === 0) {
        const diffMinutes = Math.floor(diffTime / (1000 * 60))
        return diffMinutes === 0 ? '刚刚' : `${diffMinutes}分钟前`
      }
      return `${diffHours}小时前`
    } else if (diffDays === 1) {
      return '昨天'
    } else if (diffDays < 7) {
      return `${diffDays}天前`
    }
    
    // 超过一周显示具体日期
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  } catch {
    return dateString
  }
}

// 截断文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 获取分类文本
const getCategoryText = (category) => {
  return categoryMap[category] || category
}

// 获取状态文本和类型
const getStatusText = (status) => {
  const statusKey = String(status) // 确保转为字符串
  return statusMap[statusKey]?.text || statusKey
}

const getStatusType = (status) => {
  const statusKey = String(status) // 确保转为字符串
  return statusMap[statusKey]?.type || 'info'
}

// 关键词高亮
const highlightKeyword = (text, prefix = '') => {
  if (!text || !filterForm.keyword) return prefix + text
  
  const keyword = filterForm.keyword.trim()
  if (!keyword) return prefix + text
  
  try {
    const regex = new RegExp(`(${keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi')
    const highlighted = text.replace(regex, '<mark class="highlight">$1</mark>')
    return prefix + highlighted
  } catch (error) {
    console.error('关键词高亮失败:', error)
    return prefix + text
  }
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/lost/${id}`)
}

// 联系失主
const contactOwner = (item) => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 这里可以跳转到联系页面或打开对话框
  ElMessage.info('联系功能开发中')
}
</script>

<style scoped>
/* 样式部分保持不变，与之前的相同 */
.lost-list-container {
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
  padding: 60px 0;
  text-align: center;
}

.page-header h1 {
  font-size: 36px;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 18px;
  opacity: 0.9;
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 30px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.filter-box {
  max-width: 1000px;
  margin: 0 auto;
}

.filter-box .el-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
  justify-content: center;
}

.filter-box .el-input {
  width: 300px;
}

/* 筛选标签区域 */
.filter-tags {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.tags-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.tags-title {
  font-weight: bold;
  color: #495057;
}

.result-count {
  font-size: 14px;
  color: #495057;
}

.result-count strong {
  color: #3498db;
  font-size: 16px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tag:hover {
  opacity: 0.8;
  transform: scale(1.05);
}

/* 调试信息 */
.debug-info {
  background: #f8f9fa;
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 8px;
  font-size: 12px;
  color: #666;
  border-left: 3px solid #409EFF;
}

.debug-info p {
  margin: 5px 0;
}

/* 列表区域 */
.list-section {
  padding: 20px 0 40px;
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

.initial-empty-actions {
  margin-top: 20px;
}

/* 搜索结果为空 */
.search-empty-container {
  background: white;
  border-radius: 10px;
  padding: 40px 20px;
  text-align: center;
}

.empty-suggestions {
  max-width: 400px;
  margin: 20px auto 0;
  text-align: left;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.empty-suggestions p {
  margin: 0 0 10px 0;
  font-weight: bold;
  color: #495057;
}

.empty-suggestions ul {
  margin: 0 0 15px 0;
  padding-left: 20px;
  color: #6c757d;
}

.empty-suggestions li {
  margin-bottom: 5px;
}

.empty-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

/* 物品网格 */
.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

/* 物品卡片 */
.item-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
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
  flex: 1;
  padding: 20px;
}

.item-title {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #2c3e50;
  cursor: pointer;
  transition: color 0.3s;
}

.item-title:hover {
  color: #3498db;
}

.item-info {
  margin-bottom: 15px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #495057;
}

.info-row .el-icon {
  margin-right: 8px;
  color: #6c757d;
}

.item-description {
  margin-bottom: 15px;
}

.item-description p {
  margin: 0;
  font-size: 14px;
  color: #6c757d;
  line-height: 1.6;
}

.item-images {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  cursor: pointer;
  object-fit: cover;
}

.item-footer {
  padding: 15px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
}

/* 关键词高亮 */
.highlight {
  background-color: #fff566;
  padding: 0 2px;
  border-radius: 2px;
}

/* 分页容器 */
.pagination-container {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 分页深度样式 */
:deep(.el-pagination) {
  font-size: 14px;
}

:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  background-color: #f4f4f5;
  color: #606266;
  min-width: 30px;
  border-radius: 4px;
  margin: 0 4px;
  border: 1px solid #dcdfe6;
}

:deep(.el-pagination.is-background .btn-next:hover:not(:disabled)),
:deep(.el-pagination.is-background .btn-prev:hover:not(:disabled)),
:deep(.el-pagination.is-background .el-pager li:not(.disabled):hover) {
  color: #3498db;
  border-color: #3498db;
}

:deep(.el-pagination.is-background .el-pager li.active) {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

:deep(.el-pagination.is-background .btn-next:disabled),
:deep(.el-pagination.is-background .btn-prev:disabled) {
  color: #C0C4CC;
  background-color: #f4f4f5;
  border-color: #e4e7ed;
  cursor: not-allowed;
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
  
  .filter-box .el-input {
    width: 100%;
  }
  
  .items-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-box .el-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .tags-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .empty-actions {
    flex-direction: column;
  }
  
  .empty-actions .el-button {
    width: 100%;
  }
  
  .item-footer {
    flex-direction: column;
    gap: 10px;
  }
  
  .item-footer .el-button {
    width: 100%;
  }
}
</style>