<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Hero Header -->
    <header class="relative bg-surface-container-lowest border-b border-surface-container-high overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-secondary-container/20 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 pointer-events-none"></div>
      <div class="absolute bottom-0 left-0 w-48 h-48 bg-primary-container/20 rounded-full blur-3xl translate-y-1/2 -translate-x-1/2 pointer-events-none"></div>

      <div class="relative max-w-6xl mx-auto px-6 py-16 md:py-20 text-center">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-secondary-container text-secondary rounded-2xl mb-6 shadow-lg shadow-secondary/10">
          <span class="material-symbols-outlined text-4xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">find_in_page</span>
        </div>
        <h1 class="text-4xl md:text-5xl font-black text-on-surface tracking-tight font-headline mb-4">招领公示</h1>
        <p class="text-lg text-on-surface-variant font-medium max-w-2xl mx-auto">拾获物品公示平台，等待失主认领</p>
      </div>
    </header>

    <main class="max-w-6xl mx-auto px-6 py-8 space-y-8">

      <!-- Filter Section -->
      <div class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
        <div class="flex flex-col lg:flex-row gap-4 items-center justify-between">

          <!-- Search Input -->
          <div class="w-full lg:flex-1 relative group">
            <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">search</span>
            </span>
            <input
              v-model="filterForm.keyword"
              type="text"
              placeholder="搜索物品名称、地点、描述..."
              @keyup.enter="handleSearch"
              class="w-full pl-12 pr-12 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
            />
            <button
              v-if="filterForm.keyword"
              @click="filterForm.keyword = ''; handleSearch()"
              class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant hover:text-error transition-colors p-1"
            >
              <span class="material-symbols-outlined text-base">close</span>
            </button>
          </div>

          <!-- Filters -->
          <div class="flex flex-wrap gap-3 w-full lg:w-auto justify-center lg:justify-end">
            <!-- Category Select -->
            <div class="relative min-w-[140px]">
              <select
                v-model="filterForm.category"
                @change="handleSearch"
                class="w-full appearance-none pl-4 pr-10 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary outline-none cursor-pointer font-medium text-sm"
              >
                <option value="">全部分类</option>
                <option v-for="c in categories" :key="c.code" :value="c.code">{{ c.name }}</option>
              </select>
              <span class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">
                <span class="material-symbols-outlined text-base">expand_more</span>
              </span>
            </div>

            <!-- Status Select -->
            <div class="relative min-w-[120px]">
              <select
                v-model="filterForm.status"
                @change="handleSearch"
                class="w-full appearance-none pl-4 pr-10 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary outline-none cursor-pointer font-medium text-sm"
              >
                <option value="">全部状态</option>
                <option value="pending">待处理</option>
                <option value="published">待认领</option>
                <option value="claimed">已认领</option>
                <option value="closed">已关闭</option>
              </select>
              <span class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">
                <span class="material-symbols-outlined text-base">expand_more</span>
              </span>
            </div>

            <!-- Actions -->
            <button
              @click="resetFilter"
              class="px-6 py-4 bg-surface-container-high text-on-surface-variant rounded-2xl font-bold hover:bg-surface-container-highest transition-colors whitespace-nowrap"
            >
              重置
            </button>

            <button
              v-if="isLogin"
              @click="$router.push('/user/found/add')"
              class="flex items-center gap-2 px-6 py-4 bg-success text-on-success rounded-2xl font-bold shadow-lg shadow-success/30 hover:shadow-xl hover:shadow-success/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 whitespace-nowrap"
            >
              <span class="material-symbols-outlined">add_circle</span>
              发布招领
            </button>
          </div>
        </div>
      </div>

      <!-- Debug Info (Dev Only) -->
      <div v-if="isDevelopment" class="bg-warning/10 border border-warning/20 rounded-2xl p-4 text-xs text-warning font-mono">
        <p>调试：总条数 {{ pagination.total }}，当前页 {{ pagination.current }}/{{ totalPages }}，每页 {{ pagination.size }} 条</p>
        <p>分页状态：{{ hasPagination ? '启用' : '禁用' }}，当前数据 {{ items.length }} 条</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="i in 6" :key="i" class="bg-surface-container-low rounded-3xl p-6 animate-pulse h-80">
          <div class="h-6 bg-surface-container-highest rounded-lg w-1/3 mb-4"></div>
          <div class="h-8 bg-surface-container-highest rounded-lg w-3/4 mb-4"></div>
          <div class="space-y-2 mb-4">
            <div class="h-4 bg-surface-container-highest rounded-lg w-full"></div>
            <div class="h-4 bg-surface-container-highest rounded-lg w-5/6"></div>
          </div>
          <div class="h-20 bg-surface-container-highest rounded-xl w-full mt-auto"></div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="items.length === 0" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
        <div class="inline-flex items-center justify-center w-24 h-24 bg-surface-container-highest rounded-full mb-6 text-outline-variant">
          <span class="material-symbols-outlined text-5xl">inbox</span>
        </div>
        <h3 class="text-xl font-bold text-on-surface mb-2">暂无招领信息</h3>
        <p class="text-on-surface-variant mb-8">还没有人发布招领信息，快来成为第一个吧！</p>
        <button
          v-if="isLogin"
          @click="$router.push('/user/found/add')"
          class="inline-flex items-center gap-2 px-8 py-4 bg-success text-on-success rounded-full font-bold shadow-lg shadow-success/30 hover:shadow-xl hover:-translate-y-0.5 transition-all duration-300"
        >
          <span class="material-symbols-outlined">add_circle</span>
          发布第一条招领信息
        </button>
      </div>

      <!-- Items Grid -->
      <div v-else>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          <div
            v-for="item in items"
            :key="item.id"
            class="group bg-surface-container-low hover:bg-surface-container-high border border-surface-container-high hover:border-primary/30 rounded-3xl overflow-hidden transition-all duration-300 hover:shadow-xl hover:shadow-primary/5 hover:-translate-y-1 flex flex-col"
          >
            <!-- Card Header -->
            <div class="p-5 border-b border-surface-container-high flex justify-between items-center bg-surface-container-highest/30">
              <span
                class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider"
                :class="{
                  'bg-warning/20 text-warning': item.status === 'pending' || item.status === '0',
                  'bg-secondary/20 text-secondary': item.status === 'published' || item.status === '1',
                  'bg-success/20 text-success': item.status === 'claimed' || item.status === '2',
                  'bg-error/20 text-error': item.status === 'closed' || item.status === '3'
                }"
              >
                {{ getStatusText(item.status) }}
              </span>
              <span class="text-xs font-medium text-on-surface-variant flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">schedule</span>
                {{ formatDate(item.found_time || item.foundTime || item.create_time || item.createTime) }}
              </span>
            </div>

            <!-- Card Body -->
            <div class="p-5 flex-1 flex flex-col">
              <h3
                @click="viewDetail(item.id)"
                class="text-xl font-bold text-on-surface font-headline mb-4 line-clamp-2 cursor-pointer hover:text-primary transition-colors"
              >
                {{ item.title }}
              </h3>

              <div class="space-y-2 mb-4 text-sm text-on-surface-variant">
                <div class="flex items-center gap-2">
                  <span class="material-symbols-outlined text-primary text-base">location_on</span>
                  <span class="line-clamp-1">{{ item.found_location || item.foundLocation }}</span>
                </div>
                <div v-if="item.category" class="flex items-center gap-2">
                  <span class="material-symbols-outlined text-primary text-base">folder</span>
                  <span>{{ getCategoryText(item.category) }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <span class="material-symbols-outlined text-primary text-base">person</span>
                  <span>{{ item.user?.username || item.founder_name || item.username || '匿名' }}</span>
                </div>
              </div>

              <p v-if="item.description" class="text-sm text-on-surface-variant/80 line-clamp-3 mb-4 leading-relaxed">
                {{ truncateText(item.description, 100) }}
              </p>

              <!-- 【保留并激活】Images 图片展示区域 -->
              <div v-if="item.images && item.images !== ''" class="mt-auto pt-4">
                <div class="flex gap-2 overflow-x-auto pb-2 no-scrollbar">
                  <div
                    v-for="(img, index) in item.images.split(',')"
                    :key="index"
                    @click="openImagePreview(item.images.split(','), index)"
                    class="shrink-0 w-16 h-16 rounded-xl overflow-hidden cursor-pointer border border-surface-container-high hover:border-primary transition-colors"
                  >
                    <!-- 显示图片 -->
                    <img :src="img" alt="Item image" class="w-full h-full object-cover hover:scale-110 transition-transform duration-500" />
                  </div>
                </div>
              </div>
            </div>

            <!-- Card Footer -->
            <div class="p-5 bg-surface-container-highest/30 border-t border-surface-container-high flex flex-wrap gap-2">
              <button
                @click="viewDetail(item.id)"
                class="flex-1 min-w-[100px] py-2.5 bg-primary text-on-primary rounded-xl font-bold text-sm hover:bg-primary-dim transition-colors"
              >
                查看详情
              </button>

              <button
                v-if="isLogin && (item.status === 'published' || item.status === '1')"
                @click="claimItem(item)"
                class="flex-1 min-w-[100px] py-2.5 bg-success text-on-success rounded-xl font-bold text-sm hover:bg-success/90 transition-colors"
              >
                我要认领
              </button>

              <button
                v-if="isLogin"
                @click="contactFinder(item)"
                class="flex-1 min-w-[100px] py-2.5 bg-surface-container-high text-on-surface-variant rounded-xl font-bold text-sm hover:bg-surface-container hover:text-on-surface transition-colors"
              >
                联系拾主
              </button>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="hasPagination" class="flex flex-col md:flex-row items-center justify-between gap-4 bg-surface-container-low p-4 rounded-2xl border border-surface-container-high">
          <span class="text-sm font-medium text-on-surface-variant">
            共 {{ pagination.total }} 条记录
          </span>

          <div class="flex items-center gap-2">
            <button
              @click="handleCurrentChange(pagination.current - 1)"
              :disabled="pagination.current === 1"
              class="w-10 h-10 flex items-center justify-center rounded-full bg-surface-container hover:bg-surface-container-high text-on-surface disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            >
              <span class="material-symbols-outlined">chevron_left</span>
            </button>

            <div class="hidden sm:flex gap-1">
              <button
                v-for="page in visiblePages"
                :key="page"
                @click="handleCurrentChange(page)"
                class="w-10 h-10 flex items-center justify-center rounded-full text-sm font-bold transition-all"
                :class="pagination.current === page ? 'bg-primary text-on-primary shadow-md shadow-primary/20' : 'text-on-surface-variant hover:bg-surface-container-high'"
              >
                {{ page }}
              </button>
            </div>

            <button
              @click="handleCurrentChange(pagination.current + 1)"
              :disabled="pagination.current === totalPages"
              class="w-10 h-10 flex items-center justify-center rounded-full bg-surface-container hover:bg-surface-container-high text-on-surface disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            >
              <span class="material-symbols-outlined">chevron_right</span>
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Image Preview Modal -->
    <div v-if="previewImages.length > 0 && previewIndex !== null" class="fixed inset-0 z-[70] flex items-center justify-center bg-black/90 backdrop-blur-md p-4" @click="closeImagePreview">
      <button @click="closeImagePreview" class="absolute top-6 right-6 text-white/70 hover:text-white p-2 z-10">
        <span class="material-symbols-outlined text-4xl">close</span>
      </button>

      <button @click="prevImage" class="absolute left-4 text-white/70 hover:text-white p-2 z-10 hidden md:block">
        <span class="material-symbols-outlined text-4xl">chevron_left</span>
      </button>

      <img :src="previewImages[previewIndex]" class="max-w-full max-h-full object-contain rounded-lg shadow-2xl" @click.stop />

      <button @click="nextImage" class="absolute right-4 text-white/70 hover:text-white p-2 z-10 hidden md:block">
        <span class="material-symbols-outlined text-4xl">chevron_right</span>
      </button>

      <div class="absolute bottom-6 text-white/80 text-sm font-medium bg-black/50 px-4 py-2 rounded-full">
        {{ previewIndex + 1 }} / {{ previewImages.length }}
      </div>
    </div>

  </div>
</template>

<script setup>
import { getFoundList, searchFoundItems } from '@/api/found'
import { useItemCategories } from '@/composables/useItemCategories'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { categories, loadCategories, aliasMapForCode } = useItemCategories()

const isDevelopment = process.env.NODE_ENV === 'development'
const isLogin = computed(() => userStore.isLogin)

const filterForm = reactive({
  keyword: '',
  category: '',
  status: '',
  startTime: '',
  endTime: ''
})

const pagination = reactive({
  current: 1,
  size: 9,
  total: 0
})

const loading = ref(false)
const items = ref([])
const allFiltered = ref([])
const previewImages = ref([])
const previewIndex = ref(null)

const categoryMap = {
  electronics: '电子产品',
  study: '学习用品',
  cards: '证件卡类',
  clothing: '衣物饰品',
  books: '书籍资料',
  others: '其他物品'
}

const statusMap = {
  '0': { text: '待处理', color: 'warning' },
  '1': { text: '待认领', color: 'secondary' },
  '2': { text: '已认领', color: 'success' },
  '3': { text: '已关闭', color: 'error' },
  'pending': { text: '待处理', color: 'warning' },
  'published': { text: '待认领', color: 'secondary' },
  'claimed': { text: '已认领', color: 'success' },
  'closed': { text: '已关闭', color: 'error' }
}

const totalPages = computed(() => Math.ceil(pagination.total / pagination.size))
const hasPagination = computed(() => pagination.total > pagination.size)

// 【核心修改点：加入 images 的提取】
const normalizeFoundItem = (raw) => {
  if (!raw || typeof raw !== 'object') return {}
  return {
    ...raw,
    found_location: raw.found_location ?? raw.foundLocation,
    found_time: raw.found_time ?? raw.foundTime,
    create_time: raw.create_time ?? raw.createTime,
    username: raw.username ?? raw.user?.username ?? raw.founder_name,
    images: raw.images ?? raw.image ?? raw.imageUrl ?? '' // 提取图片数据
  }
}

const categoryAliases = {
  electronics: ['electronics', '电子产品', '数码', '电子设备'],
  study: ['study', '学习用品', '学习', '文具'],
  cards: ['cards', '证件类', '证件卡类', '证件', '校园卡', '身份证', '银行卡'],
  clothing: ['clothing', '衣物饰品', '服饰配件', '个人物品', '钥匙', '眼镜', '雨伞'],
  books: ['books', '书籍资料', '书籍类', '教材', '图书'],
  others: ['others', '其他', '生活用品', '钱包/卡包', '钱包', '卡包']
}

const matchCategory = (itemCategory, selectedKey) => {
  if (!selectedKey) return true
  const c = String(itemCategory || '').trim()
  if (!c) return false
  const fromApi = aliasMapForCode(selectedKey)
  const legacy = categoryAliases[selectedKey] || []
  const aliases = [...new Set([...fromApi, ...legacy, selectedKey].filter(Boolean))]
  return aliases.some((a) => {
    const s = String(a).trim()
    return s && (c === s || c.includes(s))
  })
}

const matchStatus = (itemStatus, selectedStatus) => {
  if (!selectedStatus) return true
  const s = String(itemStatus || '').trim()
  return s === selectedStatus
}

// Simple pagination logic for visible pages
const visiblePages = computed(() => {
  const total = totalPages.value
  const current = pagination.current
  let pages = []

  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    if (current <= 3) {
      pages = [1, 2, 3, 4, 5]
    } else if (current >= total - 2) {
      pages = [total - 4, total - 3, total - 2, total - 1, total]
    } else {
      pages = [current - 2, current - 1, current, current + 1, current + 2]
    }
  }
  return pages.filter(p => p >= 1 && p <= total)
})

onMounted(async () => {
  await loadCategories()
  const kw = String(route.query.keyword || '').trim()
  if (kw) {
    filterForm.keyword = kw
    pagination.current = 1
  }
  fetchItems()
})

const fetchItems = async () => {
  loading.value = true
  try {
    const keyword = String(filterForm.keyword || '').trim()
    const categoryKey = String(filterForm.category || '').trim()
    const statusKey = String(filterForm.status || '').trim()

    if (keyword) {
      const resp = await searchFoundItems(keyword)
      const raw = resp?.code === 200 ? (resp.data?.items || []) : []
      const normalized = raw.map(normalizeFoundItem)
      const filtered = normalized.filter((it) => matchCategory(it.category, categoryKey) && matchStatus(it.status, statusKey))
      allFiltered.value = filtered
      pagination.total = filtered.length
      const start = (pagination.current - 1) * pagination.size
      items.value = filtered.slice(start, start + pagination.size)
      return
    }

    if (categoryKey || statusKey) {
      const pageSize = 200
      const maxPages = 30
      let page = 1
      let merged = []
      for (; page <= maxPages; page++) {
        const resp = await getFoundList({ page, size: pageSize })
        const raw = resp?.code === 200 ? (resp.data?.items || []) : []
        const normalized = raw.map(normalizeFoundItem)
        merged = merged.concat(normalized)
        if (raw.length < pageSize) break
      }
      const filtered = merged.filter((it) => matchCategory(it.category, categoryKey) && matchStatus(it.status, statusKey))
      allFiltered.value = filtered
      pagination.total = filtered.length
      const start = (pagination.current - 1) * pagination.size
      items.value = filtered.slice(start, start + pagination.size)
      return
    }

    const response = await getFoundList({ page: pagination.current, size: pagination.size })
    const raw = response?.code === 200 ? (response.data?.items || []) : []
    items.value = raw.map(normalizeFoundItem)

    const totalResp = await getFoundList({ page: 1, size: 2000 })
    const totalRaw = totalResp?.code === 200 ? (totalResp.data?.items || []) : []
    pagination.total = totalRaw.length
  } catch (error) {
    console.error('获取招领列表失败:', error)
    ElMessage.error('加载失败：' + (error.message || '网络错误'))
    items.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchItems()
}

const resetFilter = () => {
  Object.keys(filterForm).forEach(key => {
    filterForm[key] = ''
  })
  pagination.current = 1
  fetchItems()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchItems()
}

const handleCurrentChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  pagination.current = page
  fetchItems()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

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

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  } catch {
    return dateString
  }
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const getCategoryText = (category) => {
  const hit = categories.value.find((x) => x.code === category || x.name === category)
  if (hit) return hit.name
  return categoryMap[category] || category
}

const getStatusText = (status) => {
  const statusKey = String(status)
  return statusMap[statusKey]?.text || statusKey
}

const viewDetail = (id) => {
  router.push(`/found/${id}`)
}

const claimItem = (item) => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  ElMessageBox.confirm(
    `确定要认领【${item.title}】吗？请确认这是您丢失的物品。`,
    '认领确认',
    {
      confirmButtonText: '确认认领',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    ElMessage.success('已提交认领申请，请等待拾主确认')
    fetchItems()
  }).catch(() => {})
}

const contactFinder = (item) => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  ElMessage.info('联系功能开发中')
}

const openImagePreview = (images, index) => {
  previewImages.value = images
  previewIndex.value = index
}

const closeImagePreview = () => {
  previewImages.value = []
  previewIndex.value = null
}

const prevImage = () => {
  if (previewIndex.value > 0) previewIndex.value--
  else previewIndex.value = previewImages.value.length - 1
}

const nextImage = () => {
  if (previewIndex.value < previewImages.value.length - 1) previewIndex.value++
  else previewIndex.value = 0
}
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-surface-container-lowest { background-color: #F3EDF7; }
.bg-primary { background-color: #6750A4; }
.bg-secondary { background-color: #625B71; }
.bg-success { background-color: #006E1C; }
.bg-warning { background-color: #DCBC43; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-on-success { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-success { color: #006E1C; }
.text-warning { color: #63500A; }
.text-error { color: #B3261E; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-primary\/30 { border-color: rgba(103, 80, 164, 0.3); }
.border-warning\/20 { border-color: rgba(220, 188, 67, 0.2); }

.shadow-primary\/5 { --tw-shadow-color: rgba(103, 80, 164, 0.05); }
.shadow-primary\/20 { --tw-shadow-color: rgba(103, 80, 164, 0.2); }
.shadow-success\/30 { --tw-shadow-color: rgba(0, 110, 28, 0.3); }
.shadow-success\/40 { --tw-shadow-color: rgba(0, 110, 28, 0.4); }

.bg-primary-container\/20 { background-color: rgba(234, 221, 255, 0.2); }
.bg-secondary-container\/20 { background-color: rgba(232, 222, 248, 0.2); }
.bg-warning\/10 { background-color: rgba(220, 188, 67, 0.1); }
.bg-warning\/20 { background-color: rgba(220, 188, 67, 0.2); }
.bg-success\/20 { background-color: rgba(0, 110, 28, 0.2); }
.bg-error\/20 { background-color: rgba(179, 38, 30, 0.2); }
.bg-secondary\/20 { background-color: rgba(98, 91, 113, 0.2); }

/* Utilities */
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-in {
  animation: fadeIn 0.2s ease-out forwards;
}
</style>