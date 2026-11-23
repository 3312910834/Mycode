<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Custom Toast Notification -->
    <div v-if="toast.show" class="fixed top-6 left-1/2 -translate-x-1/2 z-[70] animate-in fade-in slide-in-from-top-4 duration-300">
      <div
        class="px-6 py-3 rounded-full shadow-lg flex items-center gap-3 font-medium text-sm"
        :class="{
          'bg-error text-on-error': toast.type === 'error',
          'bg-success text-on-success': toast.type === 'success',
          'bg-secondary-container text-secondary': toast.type === 'info'
        }"
      >
        <span class="material-symbols-outlined text-base">
          {{ toast.type === 'error' ? 'error' : toast.type === 'success' ? 'check_circle' : 'info' }}
        </span>
        {{ toast.message }}
      </div>
    </div>

    <main class="max-w-7xl mx-auto px-4 md:px-6 py-8">

      <!-- Header -->
      <header class="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 class="text-3xl font-black text-on-surface font-headline mb-2">我的招领</h1>
          <p class="text-on-surface-variant font-medium">管理您发布的拾获物品信息</p>
        </div>
        <button
          @click="$router.push('/user/found/add')"
          class="flex items-center justify-center gap-2 px-6 py-3 bg-primary text-on-primary rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300"
        >
          <span class="material-symbols-outlined">add_circle</span>
          发布新招领
        </button>
      </header>

      <!-- Search & Filter Bar -->
      <div class="bg-surface-container-low rounded-3xl p-4 md:p-6 border border-surface-container-high shadow-sm mb-8">
        <div class="flex flex-col lg:flex-row gap-4 items-center justify-between">
          <div class="w-full lg:flex-1 relative group">
            <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">search</span>
            </span>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索我的招领..."
              @keyup.enter="handleSearch"
              class="w-full pl-12 pr-12 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
            />
            <button
              v-if="searchKeyword"
              @click="searchKeyword = ''; handleSearch()"
              class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant hover:text-error transition-colors p-1"
            >
              <span class="material-symbols-outlined text-base">close</span>
            </button>
          </div>

          <div class="flex p-1 bg-surface-container-high rounded-2xl overflow-x-auto max-w-full no-scrollbar">
            <button
              v-for="opt in statusOptions"
              :key="opt.value"
              @click="setFilterStatus(opt.value)"
              class="px-4 py-2 rounded-xl text-sm font-bold whitespace-nowrap transition-all duration-300"
              :class="filterStatus === opt.value ? 'bg-surface text-on-surface shadow-md' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-highest'"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- Status Stats Cards -->
      <div v-if="!loading && allItems.length > 0" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4 mb-8">
        <div
          v-for="(stat, status) in statusStats"
          :key="status"
          @click="setFilterStatus(status === 'total' ? '' : status)"
          class="group relative bg-surface-container-low hover:bg-surface-container-high border border-surface-container-high rounded-3xl p-5 cursor-pointer transition-all duration-300 hover:-translate-y-1 hover:shadow-lg overflow-hidden"
          :class="{ 'ring-2 ring-primary ring-offset-2 ring-offset-surface': (status === 'total' && !filterStatus) || filterStatus === status }"
        >
          <div class="relative z-10">
            <div class="text-3xl font-black text-on-surface mb-1">{{ stat.count }}</div>
            <div class="text-sm font-bold text-on-surface-variant uppercase tracking-wide">{{ stat.label }}</div>
            <div class="text-xs font-medium mt-2 opacity-70" :style="{ color: stat.color }">{{ stat.percent }}%</div>
          </div>
          <div class="absolute right-[-20px] bottom-[-20px] w-24 h-24 rounded-full opacity-10 group-hover:scale-110 transition-transform" :style="{ backgroundColor: stat.color }"></div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high">
        <div v-for="i in 5" :key="i" class="animate-pulse flex items-center gap-4 py-4 border-b border-surface-container-high last:border-0">
          <div class="h-12 w-12 bg-surface-container-highest rounded-full"></div>
          <div class="flex-1 space-y-2">
            <div class="h-4 bg-surface-container-highest rounded-lg w-1/3"></div>
            <div class="h-3 bg-surface-container-highest rounded-lg w-1/4"></div>
          </div>
          <div class="h-8 w-24 bg-surface-container-highest rounded-full"></div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="items.length === 0" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
        <div class="inline-flex items-center justify-center w-24 h-24 bg-surface-container-highest rounded-full mb-6 text-outline-variant">
          <span class="material-symbols-outlined text-5xl">inbox</span>
        </div>
        <h3 class="text-xl font-bold text-on-surface mb-2">暂无招领信息</h3>
        <p class="text-on-surface-variant mb-8">您还没有发布任何招领信息</p>
        <button
          @click="$router.push('/user/found/add')"
          class="inline-flex items-center gap-2 px-6 py-3 bg-primary text-on-primary rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-xl hover:-translate-y-0.5 transition-all duration-300"
        >
          <span class="material-symbols-outlined">add_circle</span>
          发布一个招领
        </button>
      </div>

      <!-- Items List -->
      <div v-else class="bg-surface-container-low rounded-3xl border border-surface-container-high overflow-hidden shadow-sm">
        <div class="overflow-x-auto">
          <table class="w-full text-left border-collapse">
            <thead>
              <tr class="bg-surface-container-highest/50 border-b border-surface-container-high">
                <th class="p-4 font-bold text-on-surface-variant text-sm uppercase tracking-wide">物品信息</th>
                <th class="p-4 font-bold text-on-surface-variant text-sm uppercase tracking-wide hidden md:table-cell">分类</th>
                <th class="p-4 font-bold text-on-surface-variant text-sm uppercase tracking-wide hidden sm:table-cell">拾获地点</th>
                <th class="p-4 font-bold text-on-surface-variant text-sm uppercase tracking-wide hidden lg:table-cell">拾获时间</th>
                <th class="p-4 font-bold text-on-surface-variant text-sm uppercase tracking-wide">状态</th>
                <th class="p-4 font-bold text-on-surface-variant text-sm uppercase tracking-wide hidden lg:table-cell">发布时间</th>
                <th class="p-4 font-bold text-on-surface-variant text-sm uppercase tracking-wide text-right">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="item in items"
                :key="item.id"
                class="group hover:bg-surface-container-high/50 border-b border-surface-container-high last:border-0 transition-colors"
              >
                <!-- 【核心修改】：在物品名称这一列，加上图片缩略图的显示 -->
                <td class="p-4 min-w-[250px]">
                  <div class="flex items-center gap-3">
                    <div class="w-12 h-12 rounded-lg bg-surface-container-highest overflow-hidden shrink-0 border border-surface-container-high flex items-center justify-center">
                      <img v-if="item.images" :src="item.images" alt="物品图片" class="w-full h-full object-cover" />
                      <span v-else class="material-symbols-outlined text-outline-variant">image</span>
                    </div>
                    <div>
                      <div class="font-bold text-on-surface line-clamp-1">{{ item.title }}</div>
                      <div class="text-xs text-on-surface-variant md:hidden mt-1">
                        {{ getCategoryText(item.category) }} • {{ formatDate(item.foundTime) }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="p-4 text-on-surface-variant text-sm hidden md:table-cell">
                  {{ getCategoryText(item.category) }}
                </td>
                <td class="p-4 text-on-surface-variant text-sm hidden sm:table-cell max-w-[200px] truncate">
                  {{ item.foundLocation }}
                </td>
                <td class="p-4 text-on-surface-variant text-sm hidden lg:table-cell whitespace-nowrap">
                  {{ formatDate(item.foundTime) }}
                </td>
                <td class="p-4">
                  <span
                    class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide"
                    :class="{
                      'bg-warning/20 text-warning': item.status === 'pending',
                      'bg-secondary/20 text-secondary': item.status === 'published',
                      'bg-success/20 text-success': item.status === 'claimed',
                      'bg-error/20 text-error': item.status === 'closed'
                    }"
                  >
                    {{ getStatusText(item.status) }}
                  </span>
                </td>
                <td class="p-4 text-on-surface-variant text-sm hidden lg:table-cell whitespace-nowrap">
                  {{ formatDate(item.createTime) }}
                </td>
                <td class="p-4 text-right">
                  <div class="flex items-center justify-end gap-2">
                    <button
                      @click="viewDetail(item.id)"
                      class="p-2 text-primary hover:bg-primary/10 rounded-full transition-colors"
                      title="查看"
                    >
                      <span class="material-symbols-outlined text-base">visibility</span>
                    </button>
                    <button
                      v-if="item.status !== 'closed'"
                      @click="editItem(item.id)"
                      class="p-2 text-secondary hover:bg-secondary/10 rounded-full transition-colors"
                      title="编辑"
                    >
                      <span class="material-symbols-outlined text-base">edit</span>
                    </button>
                    <button
                      @click="deleteItem(item)"
                      class="p-2 text-error hover:bg-error/10 rounded-full transition-colors"
                      title="删除"
                    >
                      <span class="material-symbols-outlined text-base">delete</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div v-if="hasPagination" class="p-4 border-t border-surface-container-high flex flex-col sm:flex-row items-center justify-between gap-4 bg-surface-container-lowest">
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
  </div>
</template>

<script setup>
import { deleteFoundItem, getUserFoundList } from '@/api/found'
import { useUserStore } from '@/store/user'
import { computed, onActivated, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const toast = reactive({ show: false, message: '', type: 'info' })
const showToast = (message, type = 'info') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => {
    toast.show = false
  }, 3000)
}

const searchKeyword = ref('')
const filterStatus = ref('')
const pagination = reactive({ current: 1, size: 10, total: 0 })
const loading = ref(false)
const items = ref([])
const allItems = ref([])

const categoryMap = {
  electronics: '电子产品',
  study: '学习用品',
  cards: '证件卡类',
  clothing: '衣物饰品',
  books: '书籍资料',
  others: '其他物品',
  证件类: '证件类',
  生活用品: '生活用品',
  个人物品: '个人物品',
  '钱包/卡包': '钱包/卡包',
  书籍类: '书籍类',
  电子产品: '电子产品'
}

const statusMap = {
  pending: { text: '待处理', color: '#DCBC43' },
  published: { text: '已发布', color: '#625B71' },
  claimed: { text: '已认领', color: '#006E1C' },
  closed: { text: '已关闭', color: '#B3261E' }
}

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待处理', value: 'pending' },
  { label: '已发布', value: 'published' },
  { label: '已认领', value: 'claimed' },
  { label: '已关闭', value: 'closed' }
]

const statusStats = computed(() => {
  const stats = {}
  const total = allItems.value.length

  Object.keys(statusMap).forEach((status) => {
    const count = allItems.value.filter((item) => item.status === status).length
    const percent = total > 0 ? ((count / total) * 100).toFixed(1) : 0
    stats[status] = {
      count,
      label: statusMap[status].text,
      percent,
      color: statusMap[status].color
    }
  })

  stats.total = { count: total, label: '全部', percent: 100, color: '#6750A4' }
  return stats
})

const totalPages = computed(() => Math.ceil(pagination.total / pagination.size))
const hasPagination = computed(() => pagination.total > pagination.size)

const visiblePages = computed(() => {
  const total = totalPages.value
  const current = pagination.current
  let pages = []

  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    if (current <= 3) pages = [1, 2, 3, 4, 5]
    else if (current >= total - 2) pages = [total - 4, total - 3, total - 2, total - 1, total]
    else pages = [current - 2, current - 1, current, current + 1, current + 2]
  }

  return pages.filter((p) => p >= 1 && p <= total)
})

// 【核心修改】：在这里加上 images，让前端能够拿到并识别后端传过来的图片字段
const normalizeItem = (item) => ({
  id: item.id ?? item.itemId ?? item.foundId ?? item.found_id ?? '',
  title: item.title ?? item.name ?? '',
  category: item.category ?? 'others',
  images: item.images ?? item.image ?? item.imageUrl ?? '', // 加上这行提取图片
  foundLocation: item.foundLocation ?? item.found_location ?? item.location ?? '',
  foundTime: item.foundTime ?? item.found_time ?? item.time ?? '',
  status: item.status ?? 'pending',
  createTime: item.createTime ?? item.create_time ?? item.publishTime ?? item.publish_time ?? ''
})

const fetchItems = async () => {
  loading.value = true
  try {
    const currentUserId = userStore.userInfo?.id
    if (!currentUserId) {
      showToast('请先登录', 'error')
      items.value = []
      allItems.value = []
      pagination.total = 0
      return
    }

    const response = await getUserFoundList({
      userId: currentUserId,
      keyword: searchKeyword.value.trim(),
      status: filterStatus.value
    })

    const list = response?.data?.items || response?.data || []

    if (response?.code === 200 && Array.isArray(list)) {
      const processedItems = list.map(normalizeItem)

      allItems.value = processedItems
      pagination.total = processedItems.length

      const start = (pagination.current - 1) * pagination.size
      items.value = processedItems.slice(start, start + pagination.size)
    } else {
      items.value = []
      allItems.value = []
      pagination.total = 0
      showToast(response?.message || response?.error || '获取列表失败', 'error')
    }
  } catch (error) {
    console.error('Fetch error:', error)
    showToast(error.message || '网络请求失败', 'error')
    items.value = []
    allItems.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchItems()
})

onActivated(() => {
  fetchItems()
})

watch(
  () => route.fullPath,
  () => {
    fetchItems()
  }
)

const setFilterStatus = (status) => {
  filterStatus.value = status
  pagination.current = 1
  fetchItems()
}

const handleSearch = () => {
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
    let value = dateString
    if (String(value).includes('T')) value = String(value).replace('T', ' ')
    const date = new Date(value)
    if (Number.isNaN(date.getTime())) return String(dateString)

    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${month}-${day} ${hours}:${minutes}`
  } catch {
    return String(dateString)
  }
}

const getCategoryText = (category) => categoryMap[category] || category || '未分类'
const getStatusText = (status) => statusMap[status]?.text || status || '未知'

const viewDetail = (id) => router.push(`/found/${id}`)
const editItem = (id) => router.push(`/user/found/edit/${id}`)

const deleteItem = async (item) => {
  if (!window.confirm(`确定要删除"${item.title}"吗？此操作不可恢复。`)) return

  try {
    const response = await deleteFoundItem(item.id)
    if (response && (response.success || response.code === 200)) {
      showToast('删除成功', 'success')
      fetchItems()
    } else {
      showToast(response?.message || response?.error || '删除失败', 'error')
    }
  } catch (error) {
    console.error('Delete error:', error)
    showToast(error.message || '删除失败', 'error')
  }
}
</script>

<style scoped>
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
.text-on-error { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-success { color: #006E1C; }
.text-warning { color: #63500A; }
.text-error { color: #B3261E; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.ring-primary { --tw-ring-color: #6750A4; }
.ring-offset-surface { --tw-ring-offset-color: #FEF7FF; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.shadow-primary\/20 { --tw-shadow-color: rgba(103, 80, 164, 0.2); }

.bg-warning\/20 { background-color: rgba(220, 188, 67, 0.2); }
.bg-secondary\/20 { background-color: rgba(98, 91, 113, 0.2); }
.bg-success\/20 { background-color: rgba(0, 110, 28, 0.2); }
.bg-error\/20 { background-color: rgba(179, 38, 30, 0.2); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
.bg-secondary\/10 { background-color: rgba(98, 91, 113, 0.1); }
.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }

.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-in { animation: fadeIn 0.3s ease-out forwards; }
</style>