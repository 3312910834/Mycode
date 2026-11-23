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
      <header class="mb-8">
        <h1 class="text-3xl font-black text-on-surface font-headline mb-2">我的消息</h1>
        <p class="text-on-surface-variant font-medium">查看和管理您的站内消息</p>
      </header>

      <!-- Tabs -->
      <div class="flex items-center gap-2 mb-6 overflow-x-auto no-scrollbar">
        <button
          v-for="tab in tabs"
          :key="tab.name"
          @click="activeTab = tab.name; handleTabChange()"
          class="relative px-5 py-2.5 rounded-full text-sm font-bold whitespace-nowrap transition-all duration-300 flex items-center gap-2"
          :class="activeTab === tab.name
            ? 'bg-secondary-container text-secondary'
            : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface'"
        >
          {{ tab.label }}
          <span
            v-if="tab.name !== 'read' && unreadCount > 0"
            class="flex items-center justify-center min-w-[1.25rem] h-5 px-1.5 rounded-full bg-error text-on-error text-[10px] font-black"
          >
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </button>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high">
        <div v-for="i in 5" :key="i" class="animate-pulse flex items-start gap-4 py-4 border-b border-surface-container-high last:border-0">
          <div class="w-10 h-10 bg-surface-container-highest rounded-full"></div>
          <div class="flex-1 space-y-3">
            <div class="h-4 bg-surface-container-highest rounded-lg w-1/3"></div>
            <div class="h-3 bg-surface-container-highest rounded-lg w-2/3"></div>
            <div class="h-3 bg-surface-container-highest rounded-lg w-1/2"></div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredMessages.length === 0" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
        <div class="inline-flex items-center justify-center w-24 h-24 bg-surface-container-highest rounded-full mb-6 text-outline-variant">
          <span class="material-symbols-outlined text-5xl">{{ activeTab === 'unread' ? 'mark_email_read' : 'inbox' }}</span>
        </div>
        <h3 class="text-xl font-bold text-on-surface mb-2">{{ emptyDescription }}</h3>
        <p class="text-on-surface-variant">当有新消息时，它们会出现在这里</p>
      </div>

      <!-- Messages List -->
      <div v-else class="space-y-4">
        <div
          v-for="message in paginatedMessages"
          :key="message.id"
          class="group bg-surface-container-low hover:bg-surface-container-high border border-surface-container-high rounded-3xl p-5 transition-all duration-300 hover:shadow-md hover:-translate-y-0.5"
        >
          <!-- Header -->
          <div class="flex justify-between items-start mb-3 cursor-pointer" @click="viewMessage(message)">
            <div class="flex items-center gap-3">
              <div class="relative">
                <div class="w-10 h-10 rounded-full bg-surface-container-highest overflow-hidden border-2 border-surface-container-high">
                  <img
                    :src="message.from_user?.avatar || '/default-avatar.png'"
                    alt="Avatar"
                    class="w-full h-full object-cover"
                    @error="$event.target.src='/default-avatar.png'"
                  />
                </div>
                <span
                  v-if="message.status === 'unread'"
                  class="absolute -top-1 -right-1 w-3 h-3 bg-error border-2 border-surface rounded-full animate-pulse"
                ></span>
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <h4 class="font-bold text-on-surface text-sm md:text-base">
                    {{ message.fromUserName || message.from_user?.real_name || message.from_user?.username || '系统' }}
                  </h4>
                  <span
                    v-if="message.status === 'unread'"
                    class="px-2 py-0.5 rounded-md bg-error/10 text-error text-[10px] font-black uppercase tracking-wide"
                  >
                    新
                  </span>
                </div>
                <p class="text-xs text-on-surface-variant font-medium mt-0.5">{{ formatDate(message.createTime || message.create_time) }}</p>
              </div>
            </div>

            <!-- Item Badge -->
            <div
              @click.stop="viewRelatedItem(message)"
              class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-surface rounded-full cursor-pointer hover:bg-surface-container-high transition-colors group/badge"
            >
              <span
                class="w-2 h-2 rounded-full flex-shrink-0"
                :class="(message.itemType || message.item_type) === 'lost' ? 'bg-warning' : 'bg-success'"
              ></span>
              <span class="text-xs font-bold text-on-surface-variant group-hover/badge:text-on-surface truncate max-w-[120px]">
                {{ message.itemTitle || message.item_title || '未知物品' }}
              </span>
              <span class="material-symbols-outlined text-on-surface-variant text-xs group-hover/badge:translate-x-0.5 transition-transform">open_in_new</span>
            </div>
          </div>

          <!-- Content -->
          <div
            class="text-sm text-on-surface-variant leading-relaxed mb-4 line-clamp-2 cursor-pointer pl-13 sm:pl-0"
            @click="viewMessage(message)"
          >
            {{ truncateText(message.content, 100) }}
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-end gap-1 pt-3 border-t border-surface-container-high/50">
            <button
              @click="viewMessage(message)"
              class="p-2 text-primary hover:bg-primary/10 rounded-full transition-colors tooltip"
              title="查看详情"
            >
              <span class="material-symbols-outlined text-base">visibility</span>
            </button>

            <button
              v-if="message.status === 'unread'"
              @click="markAsRead(message)"
              class="p-2 text-secondary hover:bg-secondary/10 rounded-full transition-colors"
              title="标记为已读"
            >
              <span class="material-symbols-outlined text-base">mark_email_read</span>
            </button>

            <button
              @click="deleteMessage(message)"
              class="p-2 text-error hover:bg-error/10 rounded-full transition-colors"
              title="删除"
            >
              <span class="material-symbols-outlined text-base">delete</span>
            </button>
          </div>

          <!-- Mobile Item Link -->
          <div
            v-if="!isMobile"
            class="sm:hidden mt-3 pt-3 border-t border-surface-container-high/50 flex items-center gap-2 text-xs font-bold text-primary"
            @click.stop="viewRelatedItem(message)"
          >
            <span class="material-symbols-outlined text-sm">link</span>
            查看相关物品：{{ message.itemTitle || message.item_title || '未知' }}
          </div>
        </div>

        <!-- Pagination -->
        <div class="pt-6 flex flex-col sm:flex-row items-center justify-between gap-4">
          <span class="text-sm font-medium text-on-surface-variant">
            共 {{ filteredMessages.length }} 条记录
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
    </main>
  </div>
</template>

<script setup>
import { deleteMessage as deleteMessageApi, getMessages, markMessageAsRead, getUnreadCount } from '@/api/message'
import { computed, onActivated, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const toast = reactive({ show: false, message: '', type: 'info' })
const showToast = (message, type = 'info') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

const isMobile = ref(window.innerWidth < 640)
window.addEventListener('resize', () => {
  isMobile.value = window.innerWidth < 640
})

const activeTab = ref('all')
const pagination = reactive({ current: 1, size: 10, total: 0 })
const loading = ref(false)
const allMessages = ref([])
const unreadCount = ref(0)

const tabs = [
  { label: '全部消息', name: 'all' },
  { label: '未读消息', name: 'unread' },
  { label: '已读消息', name: 'read' }
]

const filteredMessages = computed(() => {
  if (!allMessages.value.length) return []
  switch (activeTab.value) {
    case 'unread': return allMessages.value.filter(msg => msg.status === 'unread')
    case 'read': return allMessages.value.filter(msg => msg.status === 'read')
    default: return allMessages.value
  }
})

const paginatedMessages = computed(() => {
  const start = (pagination.current - 1) * pagination.size
  const end = start + pagination.size
  return filteredMessages.value.slice(start, end)
})

const emptyDescription = computed(() => {
  switch (activeTab.value) {
    case 'unread': return '暂无未读消息'
    case 'read': return '暂无已读消息'
    default: return '暂无消息'
  }
})

const totalPages = computed(() => Math.ceil(filteredMessages.value.length / pagination.size))

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
  fetchMessages()
  fetchUnreadCount()
})

onActivated(() => {
  fetchMessages()
  fetchUnreadCount()
})

const fetchMessages = async () => {
  loading.value = true
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      showToast('请先登录', 'error')
      allMessages.value = []
      return
    }

    const response = await getMessages({ userId })

    if (response.code === 200) {
      allMessages.value = (response.data.messages || []).map(msg => ({
        id: msg.id,
        fromUserId: msg.fromUserId,
        fromUserName: msg.fromUserName,
        toUserId: msg.toUserId,
        item_id: msg.itemId,
        itemId: msg.itemId,
        item_type: msg.itemType,
        itemType: msg.itemType,
        item_title: msg.itemTitle,
        itemTitle: msg.itemTitle,
        content: msg.content,
        status: msg.status,
        create_time: msg.createTime,
        createTime: msg.createTime
      }))
    } else {
      allMessages.value = []
      showToast(response.message || '获取消息失败', 'error')
    }
  } catch (error) {
    console.error('获取消息失败:', error)
    showToast('加载失败', 'error')
    allMessages.value = []
  } finally {
    loading.value = false
  }
}

const fetchUnreadCount = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) return
    const response = await getUnreadCount(userId)
    if (response.code === 200) {
      unreadCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    console.error('获取未读消息数失败:', error)
  }
}

const handleTabChange = () => {
  pagination.current = 1
}

const handleCurrentChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  pagination.current = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  if (diff < 24 * 60 * 60 * 1000) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }
  if (diff < 2 * 24 * 60 * 60 * 1000) {
    return '昨天'
  }
  return date.toLocaleDateString()
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const viewMessage = async (message) => {
  if (message.status === 'unread') {
    await markAsRead(message, false)
    await fetchMessages()
    await fetchUnreadCount()
  }
  router.push(`/user/messages/${message.id}`)
}

const viewRelatedItem = (message) => {
  const itemType = message.itemType || message.item_type
  const itemId = message.itemId || message.item_id

  if (!itemId) {
    showToast('未找到关联物品', 'warning')
    return
  }

  if (itemType === 'lost') {
    router.push(`/lost/${itemId}`)
  } else {
    router.push(`/found/${itemId}`)
  }
}

const markAsRead = async (message, showFeedback = true) => {
  try {
    const response = await markMessageAsRead(message.id)
    if (response.success || response.code === 200) {
      const msg = allMessages.value.find(m => m.id === message.id)
      if (msg) {
        msg.status = 'read'
      }
      unreadCount.value = Math.max(0, unreadCount.value - 1)
      await fetchMessages()
      await fetchUnreadCount()
      if (showFeedback) showToast('标记为已读', 'success')
    } else if (showFeedback) {
      showToast(response?.message || '操作失败', 'error')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    if (showFeedback) showToast('操作失败', 'error')
  }
}

const deleteMessage = async (message) => {
  if (!window.confirm('确定要删除这条消息吗？')) return

  try {
    const response = await deleteMessageApi(message.id)
    if (response.success || response.code === 200) {
      showToast('删除成功', 'success')
      allMessages.value = allMessages.value.filter(m => m.id !== message.id)
      await fetchUnreadCount()
    } else {
      showToast(response?.message || '删除失败', 'error')
    }
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
.text-on-error { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-outline-variant { border-color: #CAC4D0; }

.shadow-md { --tw-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); }

.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
.bg-secondary\/10 { background-color: rgba(98, 91, 113, 0.1); }

.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }

/* Animations */
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
.animate-in { animation: fadeIn 0.3s ease-out forwards; }

/* Line Clamp Utility */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>