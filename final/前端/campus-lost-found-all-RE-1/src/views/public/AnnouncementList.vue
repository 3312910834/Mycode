<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Hero Header -->
    <header class="relative bg-surface-container-lowest border-b border-surface-container-high overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-primary-container/20 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 pointer-events-none"></div>
      <div class="absolute bottom-0 left-0 w-48 h-48 bg-secondary-container/20 rounded-full blur-3xl translate-y-1/2 -translate-x-1/2 pointer-events-none"></div>

      <div class="relative max-w-5xl mx-auto px-6 py-16 md:py-20 text-center">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-primary-container text-primary rounded-2xl mb-6 shadow-lg shadow-primary/10">
          <!-- 使用 Material Symbols 替代 el-icon -->
          <span class="material-symbols-outlined text-4xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">campaign</span>
        </div>
        <h1 class="text-4xl md:text-5xl font-black text-on-surface tracking-tight font-headline mb-4">系统公告</h1>
        <p class="text-lg text-on-surface-variant font-medium max-w-2xl mx-auto">获取最新的平台动态、维护通知及使用指南</p>
      </div>
    </header>

    <main class="max-w-5xl mx-auto px-6 py-12 space-y-16">

      <!-- Top Announcements Section -->
      <section v-if="topAnnouncements.length > 0">
        <div class="flex items-center gap-3 mb-6">
          <span class="relative flex h-3 w-3">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-tertiary opacity-75"></span>
            <span class="relative inline-flex rounded-full h-3 w-3 bg-tertiary"></span>
          </span>
          <h2 class="text-2xl font-bold text-on-surface font-headline tracking-tight">置顶公告</h2>
        </div>

        <div class="grid gap-6">
          <div v-for="announcement in topAnnouncements" :key="'top-' + announcement.id"
               class="group relative bg-tertiary-container/30 hover:bg-tertiary-container/50 border border-tertiary-fixed/20 rounded-3xl p-6 md:p-8 transition-all duration-300 hover:shadow-xl hover:shadow-tertiary/5 hover:-translate-y-1 overflow-hidden">

            <div class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
              <span class="material-symbols-outlined text-[8rem] text-tertiary" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">star</span>
            </div>

            <div class="relative z-10">
              <div class="flex flex-col md:flex-row md:items-start justify-between gap-4 mb-4">
                <div class="flex items-center gap-3">
                  <span class="px-3 py-1 bg-tertiary text-on-tertiary text-xs font-bold rounded-full uppercase tracking-wider shadow-sm">
                    重要置顶
                  </span>
                  <h3 class="text-xl md:text-2xl font-bold text-on-surface font-headline leading-tight">
                    {{ announcement.title }}
                  </h3>
                </div>
                <span class="text-sm font-medium text-on-surface-variant whitespace-nowrap flex items-center gap-1">
                  <span class="material-symbols-outlined text-base">schedule</span>
                  {{ formatDate(announcement.createTime) }}
                </span>
              </div>

              <p class="text-on-surface-variant/90 text-base md:text-lg leading-relaxed mb-6 line-clamp-2 md:line-clamp-none">
                {{ announcement.content }}
              </p>

              <div class="flex items-center justify-between pt-4 border-t border-tertiary-fixed/20">
                <div class="flex items-center gap-2 text-sm font-medium text-on-surface-variant">
                  <div class="w-8 h-8 rounded-full bg-surface-container-highest flex items-center justify-center text-tertiary">
                    <span class="material-symbols-outlined text-sm">person</span>
                  </div>
                  <span>{{ announcement.adminName || '系统管理员' }}</span>
                </div>

                <button @click="showDetail(announcement)" class="text-tertiary font-bold text-sm hover:text-tertiary-fixed variant flex items-center gap-1 group/btn transition-colors">
                  阅读全文
                  <span class="material-symbols-outlined text-sm group-hover/btn:translate-x-1 transition-transform">arrow_forward</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- All Announcements Section -->
      <section>
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-2xl font-bold text-on-surface font-headline tracking-tight flex items-center gap-3">
            <span class="material-symbols-outlined text-primary">notifications</span>
            所有公告
          </h2>
        </div>

        <!-- Loading State (Custom Skeleton) -->
        <div v-if="loading" class="space-y-4">
          <div v-for="i in 3" :key="i" class="bg-surface-container-low rounded-3xl p-6 animate-pulse">
            <div class="h-6 bg-surface-container-highest rounded-lg w-3/4 mb-4"></div>
            <div class="h-4 bg-surface-container-highest rounded-lg w-full mb-2"></div>
            <div class="h-4 bg-surface-container-highest rounded-lg w-5/6"></div>
          </div>
        </div>

        <!-- Empty State (Custom) -->
        <div v-else-if="announcements.length === 0" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
          <div class="inline-flex items-center justify-center w-20 h-20 bg-surface-container-highest rounded-full mb-4 text-outline-variant">
            <span class="material-symbols-outlined text-4xl">inbox</span>
          </div>
          <p class="text-on-surface-variant font-medium text-lg">暂无公告</p>
          <p class="text-on-surface-variant/60 text-sm mt-2">请稍后再来查看最新通知</p>
        </div>

        <!-- List -->
        <div v-else class="space-y-4">
          <div v-for="announcement in announcements"
               :key="announcement.id"
               class="group bg-surface-container-low hover:bg-surface-container-high border border-transparent hover:border-outline-variant/20 rounded-2xl p-6 transition-all duration-300 hover:shadow-lg hover:-translate-y-0.5"
               :class="{ 'opacity-60 grayscale': announcement.status === 0 }">

            <div class="flex flex-col md:flex-row justify-between gap-4 mb-3">
              <div class="flex items-center gap-3 flex-wrap">
                <h3 class="text-lg font-bold text-on-surface font-headline leading-snug">
                  {{ announcement.title }}
                </h3>
                <span v-if="announcement.isTop === 1" class="px-2 py-0.5 bg-error/10 text-error text-[10px] font-bold rounded uppercase tracking-wider">
                  置顶
                </span>
                <span v-if="announcement.status === 0" class="px-2 py-0.5 bg-outline/10 text-outline text-[10px] font-bold rounded uppercase tracking-wider">
                  已隐藏
                </span>
                <span v-else class="px-2 py-0.5 bg-secondary/10 text-secondary text-[10px] font-bold rounded uppercase tracking-wider">
                  显示中
                </span>
              </div>
              <span class="text-xs font-medium text-on-surface-variant flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">calendar_today</span>
                {{ formatDate(announcement.createTime) }}
              </span>
            </div>

            <p class="text-on-surface-variant text-sm leading-relaxed mb-4 line-clamp-2">
              {{ announcement.content }}
            </p>

            <div class="flex items-center justify-between pt-2">
              <div class="flex items-center gap-2 text-xs font-medium text-outline">
                <span class="material-symbols-outlined text-sm">person_outline</span>
                {{ announcement.adminName || '系统管理员' }}
              </div>

              <button @click="showDetail(announcement)" class="px-4 py-2 text-sm font-bold text-primary bg-primary-container/50 hover:bg-primary-container rounded-full transition-colors">
                查看详情
              </button>
            </div>
          </div>
        </div>

        <!-- Pagination (Custom Implementation) -->
        <div v-if="pagination.total > 0 && !loading" class="mt-12 flex flex-col md:flex-row items-center justify-between gap-4 bg-surface-container-low p-4 rounded-2xl">
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

            <div class="flex gap-1">
              <button
                v-for="page in totalPages"
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
      </section>
    </main>

    <!-- Detail Dialog (Custom Modal Implementation) -->
    <div v-if="detailDialogVisible" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-on-surface/60 backdrop-blur-sm transition-opacity" @click="detailDialogVisible = false"></div>

      <!-- Modal Content -->
      <div class="relative bg-surface-container-low w-full max-w-2xl rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh] animate-in fade-in zoom-in-95 duration-200">

        <!-- Header -->
        <div class="p-6 md:p-8 border-b border-surface-container-high flex justify-between items-start bg-surface-container-highest/50">
          <div>
            <h3 class="text-2xl font-bold text-on-surface font-headline leading-tight mb-2">
              {{ currentAnnouncement?.title }}
            </h3>
            <div class="flex flex-wrap gap-4 text-xs font-medium text-on-surface-variant">
              <span class="flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">calendar_today</span>
                {{ currentAnnouncement ? formatDate(currentAnnouncement.createTime) : '' }}
              </span>
              <span class="flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">person</span>
                {{ currentAnnouncement?.adminName || '系统管理员' }}
              </span>
              <span v-if="currentAnnouncement?.isTop === 1" class="flex items-center gap-1 text-tertiary">
                <span class="material-symbols-outlined text-sm" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">star</span>
                置顶
              </span>
            </div>
          </div>
          <button @click="detailDialogVisible = false" class="p-2 hover:bg-surface-container-high rounded-full transition-colors text-on-surface-variant">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <!-- Content -->
        <div class="p-6 md:p-8 overflow-y-auto custom-scrollbar">
          <div class="prose prose-slate max-w-none">
            <p class="text-on-surface text-base md:text-lg leading-relaxed whitespace-pre-line">
              {{ currentAnnouncement?.content }}
            </p>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-4 md:p-6 border-t border-surface-container-high bg-surface-container-highest/30 flex justify-end">
          <button @click="detailDialogVisible = false" class="px-6 py-2.5 bg-primary text-on-primary rounded-full font-bold text-sm hover:bg-primary-dim transition-colors shadow-lg shadow-primary/20">
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watchEffect } from 'vue'
import { ElMessage } from 'element-plus'
// 仅导入 API，不再导入图标
import { getVisibleAnnouncements, getTopAnnouncements } from '@/api/announcement'

const loading = ref(false)
const announcements = ref([])
const topAnnouncements = ref([])
const detailDialogVisible = ref(false)
const currentAnnouncement = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 计算总页数
const totalPages = computed(() => Math.ceil(pagination.total / pagination.size))

onMounted(() => {
  loadAnnouncements()
  loadTopAnnouncements()
})

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const params = { page: pagination.current, size: pagination.size }
    const response = await getVisibleAnnouncements(params)

    if (response && response.code === 200) {
      announcements.value = response.data?.announcements || []
      pagination.total = response.data?.total || 0
    } else if (response && response.data) {
      announcements.value = response.data.announcements || []
      pagination.total = response.data.total || 0
    } else {
      announcements.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('加载公告列表失败:', error)
    ElMessage.error('加载失败：' + (error.message || '网络错误'))
    announcements.value = getMockAnnouncements()
    pagination.total = announcements.value.length
  } finally {
    loading.value = false
  }
}

const loadTopAnnouncements = async () => {
  try {
    const response = await getTopAnnouncements()
    if (response && response.code === 200) {
      topAnnouncements.value = response.data?.announcements || []
    } else if (response && response.data) {
      topAnnouncements.value = response.data.announcements || []
    } else {
      topAnnouncements.value = []
    }
  } catch (error) {
    console.error('加载置顶公告失败:', error)
    topAnnouncements.value = []
  }
}

const getMockAnnouncements = () => {
  return [
    {
      id: 1,
      title: '欢迎使用校园失物招领系统',
      content: '本系统旨在帮助同学们找回失物，请大家诚信使用。如有任何问题，请联系管理员。\n\n我们致力于打造一个安全、高效的校园环境，希望每一位同学都能在这里找到属于自己的便利。',
      isTop: 1,
      status: 1,
      adminName: 'Admin',
      createTime: '2026-01-05 14:27:01'
    },
    {
      id: 2,
      title: '物品发布规范',
      content: '发布物品时请提供详细信息和清晰图片，确保信息准确无误。禁止发布虚假广告或违规物品。',
      isTop: 0,
      status: 1,
      adminName: 'Admin',
      createTime: '2026-01-06 09:15:00'
    },
    {
      id: 3,
      title: '系统维护通知',
      content: '我们将于本周日凌晨进行系统维护，届时服务可能短暂中断，敬请谅解。',
      isTop: 0,
      status: 1,
      adminName: 'System',
      createTime: '2026-01-07 16:30:00'
    },
    {
      id: 4,
      title: '期末失物高峰提示',
      content: '期末考试期间是失物高发期，请同学们离开教室、图书馆时务必检查随身物品。',
      isTop: 0,
      status: 1,
      adminName: 'StudentUnion',
      createTime: '2026-01-08 10:00:00'
    }
  ]
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadAnnouncements()
}

const handleCurrentChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  pagination.current = page
  loadAnnouncements()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const formatDate = (dateString) => {
  if (!dateString) return '未知时间'
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return dateString
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (e) {
    return dateString
  }
}

const showDetail = (announcement) => {
  currentAnnouncement.value = announcement
  detailDialogVisible.value = true
  document.body.style.overflow = 'hidden'
}

// 恢复滚动
watchEffect(() => {
  if (!detailDialogVisible.value) {
    document.body.style.overflow = ''
  }
})
</script>

<style scoped>
/*
  【重要】硬编码 Fallback 颜色，确保即使 tailwind.config.js 未配置也能正常显示
  防止页面白屏
*/
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-surface-container-lowest { background-color: #F3EDF7; }
.bg-primary { background-color: #6750A4; }
.bg-primary-container { background-color: #EADDFF; }
.bg-tertiary { background-color: #7D5260; }
.bg-tertiary-container { background-color: #FFD8E4; }
.bg-secondary { background-color: #625B71; }
.bg-error { background-color: #B3261E; }
.bg-outline { background-color: #79747E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-on-tertiary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-tertiary { color: #7D5260; }
.text-secondary { color: #625B71; }
.text-error { color: #B3261E; }
.text-outline { color: #79747E; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-tertiary-fixed\/20 { border-color: rgba(125, 82, 96, 0.2); }
.border-outline-variant { border-color: #CAC4D0; }

.shadow-primary\/20 { --tw-shadow-color: rgba(103, 80, 164, 0.2); }
.shadow-tertiary\/5 { --tw-shadow-color: rgba(125, 82, 96, 0.05); }

/* Custom Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 8px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #CAC4D0;
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #79747E;
}

/* Simple Animation Fallback */
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-in {
  animation: fadeIn 0.2s ease-out forwards;
}
</style>