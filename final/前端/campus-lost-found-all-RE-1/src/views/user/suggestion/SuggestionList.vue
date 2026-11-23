<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Custom Toast Notification -->
    <div v-if="toast.show" class="fixed top-6 left-1/2 -translate-x-1/2 z-[70] animate-in fade-in slide-in-from-top-4 duration-300">
      <div class="px-6 py-3 rounded-full shadow-lg flex items-center gap-3 font-medium text-sm"
           :class="{
             'bg-error text-on-error': toast.type === 'error',
             'bg-success text-on-success': toast.type === 'success',
             'bg-secondary-container text-secondary': toast.type === 'info'
           }">
        <span class="material-symbols-outlined text-base">
          {{ toast.type === 'error' ? 'error' : toast.type === 'success' ? 'check_circle' : 'info' }}
        </span>
        {{ toast.message }}
      </div>
    </div>

    <main class="max-w-5xl mx-auto px-4 md:px-6 py-8">

      <!-- Header -->
      <header class="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 class="text-3xl font-black text-on-surface font-headline mb-2">我的建议反馈</h1>
          <p class="text-on-surface-variant font-medium">查看您提交的建议反馈和处理状态</p>
        </div>
        <button
          @click="$router.push('/user/suggestions/add')"
          class="flex items-center justify-center gap-2 px-6 py-3 bg-primary text-on-primary rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300"
        >
          <span class="material-symbols-outlined">add_circle</span>
          提交新建议
        </button>
      </header>

      <!-- Login Prompt -->
      <div v-if="!isLoggedIn" class="mb-8 bg-warning/10 border border-warning/20 rounded-3xl p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div class="flex items-start gap-4">
          <div class="w-10 h-10 rounded-full bg-warning/20 text-warning flex items-center justify-center flex-shrink-0">
            <span class="material-symbols-outlined">warning</span>
          </div>
          <div>
            <h3 class="font-bold text-on-surface text-lg">请先登录</h3>
            <p class="text-on-surface-variant text-sm mt-1">登录后即可查看和管理您的建议反馈</p>
          </div>
        </div>
        <button
          @click="goToLogin"
          class="w-full sm:w-auto px-6 py-2.5 bg-primary text-on-primary rounded-full font-bold text-sm shadow-md hover:shadow-lg hover:-translate-y-0.5 transition-all duration-300 whitespace-nowrap"
        >
          去登录
        </button>
      </div>

      <!-- Content Area -->
      <div v-else>
        <!-- Loading State -->
        <div v-if="loading" class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high">
          <div v-for="i in 5" :key="i" class="animate-pulse flex flex-col gap-4 py-6 border-b border-surface-container-high last:border-0">
            <div class="flex justify-between">
              <div class="h-6 w-24 bg-surface-container-highest rounded-full"></div>
              <div class="h-4 w-32 bg-surface-container-highest rounded-lg"></div>
            </div>
            <div class="space-y-2">
              <div class="h-4 bg-surface-container-highest rounded-lg w-full"></div>
              <div class="h-4 bg-surface-container-highest rounded-lg w-5/6"></div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="suggestions.length === 0" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
          <div class="inline-flex items-center justify-center w-24 h-24 bg-surface-container-highest rounded-full mb-6 text-outline-variant">
            <span class="material-symbols-outlined text-5xl">lightbulb_circle</span>
          </div>
          <h3 class="text-xl font-bold text-on-surface mb-2">暂无建议反馈</h3>
          <p class="text-on-surface-variant mb-8">您还没有提交过任何建议</p>
          <button
            @click="$router.push('/user/suggestions/add')"
            class="inline-flex items-center gap-2 px-6 py-3 bg-primary text-on-primary rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-xl hover:-translate-y-0.5 transition-all duration-300"
          >
            <span class="material-symbols-outlined">add_circle</span>
            提交一个建议
          </button>
        </div>

        <!-- Suggestions List -->
        <div v-else class="space-y-4">
          <div
            v-for="suggestion in paginatedSuggestions"
            :key="suggestion.id"
            class="group bg-surface-container-low hover:bg-surface-container-high border border-surface-container-high rounded-3xl p-6 transition-all duration-300 hover:shadow-md hover:-translate-y-0.5"
          >
            <!-- Header -->
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4">
              <span
                class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide w-fit"
                :class="{
                  'bg-warning/20 text-warning': suggestion.status === 'pending',
                  'bg-secondary/20 text-secondary': suggestion.status === 'read',
                  'bg-success/20 text-success': suggestion.status === 'processed'
                }"
              >
                {{ getStatusText(suggestion.status) }}
              </span>
              <span class="text-xs font-medium text-on-surface-variant">
                {{ formatDate(suggestion.createTime || suggestion.create_time) }}
              </span>
            </div>

            <!-- Content -->
            <div class="text-on-surface leading-relaxed mb-6 pl-1 border-l-4 border-surface-container-highest">
              {{ truncateText(suggestion.content, 200) }}
            </div>

            <!-- Reply Block -->
            <div v-if="suggestion.reply" class="bg-primary/5 border border-primary/10 rounded-2xl p-5 mb-6 relative overflow-hidden">
              <div class="absolute top-0 left-0 w-1 h-full bg-primary"></div>
              <div class="flex items-center gap-2 mb-3 text-primary font-bold text-sm">
                <span class="material-symbols-outlined text-base">chat_bubble</span>
                管理员回复
              </div>
              <p class="text-on-surface-variant text-sm leading-relaxed mb-2">{{ suggestion.reply }}</p>
              <div v-if="suggestion.reply_time" class="text-xs text-on-surface-variant/70 text-right mt-2">
                回复时间：{{ formatDate(suggestion.reply_time) }}
              </div>
            </div>

            <!-- Actions -->
            <div class="flex items-center justify-end gap-2 pt-4 border-t border-surface-container-high/50">
              <button
                @click="viewSuggestion(suggestion)"
                class="p-2 text-primary hover:bg-primary/10 rounded-full transition-colors"
                title="查看详情"
              >
                <span class="material-symbols-outlined text-base">visibility</span>
              </button>
              <button
                @click="deleteSuggestion(suggestion)"
                class="p-2 text-error hover:bg-error/10 rounded-full transition-colors"
                title="删除"
              >
                <span class="material-symbols-outlined text-base">delete</span>
              </button>
            </div>
          </div>

          <!-- Pagination -->
          <div class="pt-6 flex flex-col sm:flex-row items-center justify-between gap-4">
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
                  :class="pagination.current === page ? 'bg-secondary-container text-secondary shadow-md' : 'text-on-surface-variant hover:bg-surface-container-high'"
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
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getMySuggestions, deleteSuggestion as deleteSuggestionApi } from '@/api/suggestion'

const router = useRouter()
const userStore = useUserStore()

// Simple Toast System
const toast = reactive({ show: false, message: '', type: 'info' })
const showToast = (message, type = 'info') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

const isLoggedIn = computed(() => userStore.isLogin)

const pagination = reactive({ current: 1, size: 10, total: 0 })
const loading = ref(false)
const suggestions = ref([])

const statusMap = {
  pending: { text: '待处理' },
  read: { text: '已查看' },
  processed: { text: '已处理' }
}

const totalPages = computed(() => Math.max(1, Math.ceil(pagination.total / pagination.size)))

const paginatedSuggestions = computed(() => {
  const start = (pagination.current - 1) * pagination.size
  return suggestions.value.slice(start, start + pagination.size)
})

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
  return pages.filter(p => p >= 1 && p <= total)
})

onMounted(() => {
  if (isLoggedIn.value) {
    fetchSuggestions()
  }
})

watch(isLoggedIn, (v) => {
  if (v) fetchSuggestions()
})

const goToLogin = () => {
  router.push('/login')
}

const fetchSuggestions = async () => {
  if (!isLoggedIn.value) {
    showToast('请先登录', 'warning')
    return
  }

  loading.value = true
  try {
    const userId = userStore.userId
    if (!userId) {
      showToast('无法获取用户信息，请重新登录', 'error')
      router.push('/login')
      return
    }

    const response = await getMySuggestions(userId)

    if (response.code === 200 && response.data) {
      suggestions.value = response.data.suggestions || response.data || []
      pagination.total = response.data.count ?? suggestions.value.length
      pagination.current = 1
    } else {
      showToast(response.message || '获取建议失败', 'error')
      suggestions.value = []
    }
  } catch (error) {
    console.error('获取建议列表失败:', error)
    showToast('加载失败: ' + error.message, 'error')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchSuggestions()
}

const handleCurrentChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  pagination.current = page
  fetchSuggestions()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit'
    })
  } catch (e) {
    return dateString
  }
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const getStatusText = (status) => {
  return statusMap[status]?.text || status
}

const viewSuggestion = (suggestion) => {
  router.push({
    path: '/user/suggestions/detail',
    query: { id: suggestion.id }
  })
}

const deleteSuggestion = async (suggestion) => {
  if (!window.confirm('确定要删除这条建议吗？')) return

  try {
    await deleteSuggestionApi(suggestion.id)
    showToast('删除成功', 'success')
    fetchSuggestions()
  } catch (error) {
    console.error('删除失败:', error)
    showToast('删除失败', 'error')
  }
}
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-secondary-container { background-color: #E8DEF8; }
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
.text-warning { color: #63500A; } /* Darker for text readability */
.text-error { color: #B3261E; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-outline-variant { border-color: #CAC4D0; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }

.bg-warning\/10 { background-color: rgba(220, 188, 67, 0.1); }
.bg-warning\/20 { background-color: rgba(220, 188, 67, 0.2); }
.bg-primary\/5 { background-color: rgba(103, 80, 164, 0.05); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
.bg-primary\/20 { background-color: rgba(103, 80, 164, 0.2); }
.bg-secondary\/20 { background-color: rgba(98, 91, 113, 0.2); }
.bg-success\/20 { background-color: rgba(0, 110, 28, 0.2); }
.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }

.border-warning\/20 { border-color: rgba(220, 188, 67, 0.2); }
.border-primary\/10 { border-color: rgba(103, 80, 164, 0.1); }

/* Animations */
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
.animate-in { animation: fadeIn 0.3s ease-out forwards; }
</style>