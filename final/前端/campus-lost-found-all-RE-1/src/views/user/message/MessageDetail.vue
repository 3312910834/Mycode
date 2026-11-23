<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <button
      type="button"
      class="fixed top-4 left-4 z-[90] inline-flex items-center gap-2 px-4 py-2.5 rounded-full bg-surface-container-high text-on-surface font-bold text-sm shadow-lg border border-outline-variant/30 hover:bg-surface-container-highest transition-colors"
      @click="router.push('/')"
    >
      <span class="material-symbols-outlined text-base">home</span>
      首页
    </button>

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

    <main class="max-w-4xl mx-auto px-4 md:px-6 py-8">

      <!-- Header -->
      <header class="mb-6">
        <button @click="goBack" class="inline-flex items-center gap-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-high px-4 py-2 rounded-full transition-all duration-300 font-medium">
          <span class="material-symbols-outlined">arrow_back</span>
          返回列表
        </button>
      </header>

      <!-- Loading State -->
      <div v-if="loading" class="bg-surface-container-low rounded-3xl p-8 border border-surface-container-high">
        <div class="animate-pulse space-y-6">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 bg-surface-container-highest rounded-full"></div>
            <div class="space-y-2">
              <div class="h-5 w-32 bg-surface-container-highest rounded-lg"></div>
              <div class="h-3 w-24 bg-surface-container-highest rounded-lg"></div>
            </div>
          </div>
          <div class="h-32 bg-surface-container-highest rounded-2xl"></div>
          <div class="space-y-3 pt-6">
            <div class="h-16 bg-surface-container-highest rounded-2xl w-3/4 ml-auto"></div>
            <div class="h-16 bg-surface-container-highest rounded-2xl w-3/4"></div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="!message.id" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
        <div class="inline-flex items-center justify-center w-24 h-24 bg-surface-container-highest rounded-full mb-6 text-outline-variant">
          <span class="material-symbols-outlined text-5xl">mail_off</span>
        </div>
        <h3 class="text-xl font-bold text-on-surface mb-2">消息不存在</h3>
        <p class="text-on-surface-variant mb-8">该消息可能已被删除或链接无效</p>
        <button
          @click="goBack"
          class="inline-flex items-center gap-2 px-6 py-3 bg-primary text-on-primary rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-xl hover:-translate-y-0.5 transition-all duration-300"
        >
          返回消息列表
        </button>
      </div>

      <!-- Message Detail -->
      <div v-else class="space-y-8">

        <!-- Main Message Card -->
        <div class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
          <!-- Sender Info -->
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-6 pb-6 border-b border-surface-container-high">
            <div class="flex items-center gap-4">
              <div class="w-14 h-14 rounded-full bg-surface-container-highest overflow-hidden flex-shrink-0 border-2 border-surface-container-high">
                <img
                  :src="message.from_user?.avatar || '/default-avatar.png'"
                  alt="Avatar"
                  class="w-full h-full object-cover"
                  @error="$event.target.src='/default-avatar.png'"
                />
              </div>
              <div>
                <h2 class="text-xl font-bold text-on-surface font-headline">
                  {{ message.fromUserName || message.from_user?.real_name || message.from_user?.username || '系统用户' }}
                </h2>
                <p class="text-sm text-on-surface-variant font-medium mt-1">
                  {{ formatDateTime(message.createTime || message.create_time) }}
                </p>
              </div>
            </div>

            <!-- Item Link Badge -->
            <div
              @click="viewRelatedItem"
              class="inline-flex items-center gap-2 px-4 py-2 bg-surface-container-highest rounded-full cursor-pointer hover:bg-surface-container-high transition-colors group"
            >
              <span
                class="w-2 h-2 rounded-full"
                :class="(message.itemType || message.item_type) === 'lost' ? 'bg-warning' : 'bg-success'"
              ></span>
              <span class="text-sm font-bold text-on-surface-variant group-hover:text-on-surface">
                {{ (message.itemType || message.item_type) === 'lost' ? '失物' : '招领' }}
              </span>
              <span class="text-sm font-medium text-primary truncate max-w-[150px]">
                {{ message.itemTitle || message.item_title || '未知物品' }}
              </span>
              <span class="material-symbols-outlined text-on-surface-variant text-base group-hover:translate-x-1 transition-transform">open_in_new</span>
            </div>
          </div>

          <!-- Message Content -->
          <div class="py-8">
            <div class="bg-surface-container-high text-on-surface rounded-2xl rounded-tl-none p-6 leading-relaxed text-base shadow-sm whitespace-pre-wrap">
              {{ message.content }}
            </div>
          </div>
        </div>

        <!-- Reply Form -->
        <div v-if="message.fromUserId !== currentUserId" class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
          <h3 class="text-lg font-bold text-on-surface font-headline mb-4 flex items-center gap-2">
            <span class="material-symbols-outlined text-primary">reply</span>
            回复消息
          </h3>

          <div class="relative">
            <textarea
              v-model="replyForm.content"
              rows="4"
              placeholder="请输入回复内容..."
              class="w-full p-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 resize-none"
              maxlength="500"
            ></textarea>
            <div class="absolute bottom-3 right-4 text-xs text-on-surface-variant font-medium">
              {{ replyForm.content.length }}/500
            </div>
          </div>

          <div class="mt-4 flex justify-end">
            <button
              @click="sendReply"
              :disabled="!replyForm.content.trim() || replying"
              class="flex items-center gap-2 px-6 py-3 bg-primary text-on-primary rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
            >
              <span v-if="replying" class="material-symbols-outlined animate-spin">progress_activity</span>
              <span v-else class="material-symbols-outlined">send</span>
              {{ replying ? '发送中...' : '发送回复' }}
            </button>
          </div>
        </div>

        <!-- History Chat -->
        <div v-if="messageHistory.length > 0" class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
          <h3 class="text-lg font-bold text-on-surface font-headline mb-6 flex items-center gap-2">
            <span class="material-symbols-outlined text-secondary">history</span>
            历史对话
          </h3>

          <div ref="historyContainer" class="flex flex-col gap-4">
            <div
              v-for="(history, index) in messageHistory"
              :key="index"
              :class="['flex w-full', history.fromUserId === currentUserId ? 'justify-end' : 'justify-start']"
            >
              <div
                class="max-w-[85%] sm:max-w-[70%] px-5 py-3 rounded-2xl shadow-sm relative"
                :class="history.fromUserId === currentUserId
                  ? 'bg-primary text-on-primary rounded-br-none'
                  : 'bg-surface-container-high text-on-surface rounded-bl-none'"
              >
                <p class="text-sm leading-relaxed whitespace-pre-wrap">{{ history.content }}</p>
                <span
                  class="text-[10px] font-medium block mt-1 text-right opacity-70"
                  :class="history.fromUserId === currentUserId ? 'text-on-primary' : 'text-on-surface-variant'"
                >
                  {{ formatTime(history.createTime || history.create_time) }}
                </span>
              </div>
            </div>
          </div>
        </div>

      </div>
    </main>
  </div>
</template>

<script setup>
import { getConversation, getMessageDetail, markMessageAsRead, sendMessage } from '@/api/message'
import { useUserStore } from '@/store/user'
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const toast = reactive({ show: false, message: '', type: 'info' })
const showToast = (message, type = 'info') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

const loading = ref(false)
const replying = ref(false)
const currentUserId = computed(() => userStore.userInfo?.id || 0)
const historyContainer = ref(null)

const message = ref({})
const messageHistory = ref([])

const replyForm = reactive({
  content: ''
})

onMounted(() => {
  fetchMessageDetail()
})

const fetchMessageDetail = async () => {
  const messageId = route.params.id
  if (!messageId) {
    showToast('消息ID不存在', 'error')
    router.push('/user/messages?refresh=1')
    return
  }

  loading.value = true
  try {
    const detailResponse = await getMessageDetail(messageId)

    if (detailResponse.code === 200) {
      message.value = detailResponse.data || {}

      const msgStatus = message.value.status
      const msgToUserId = message.value.toUserId || message.value.to_user_id

      if (msgStatus === 'unread' && Number(msgToUserId) === Number(currentUserId.value)) {
        try {
          const readResp = await markMessageAsRead(message.value.id)
          if (readResp?.success || readResp?.code === 200) {
            message.value.status = 'read'
          }
        } catch (e) {
          console.warn('标记已读失败', e)
        }
      }

      const fromUserId = message.value.fromUserId
      const toUserId = message.value.toUserId

      if (fromUserId && toUserId) {
        const historyResponse = await getConversation(fromUserId, toUserId)
        if (historyResponse.code === 200) {
          messageHistory.value = historyResponse.data.messages || []
          await nextTick()
          scrollToBottom()
        }
      }
    } else {
      showToast(detailResponse.error || detailResponse.message || '消息不存在', 'error')
      router.push('/user/messages?refresh=1')
    }
  } catch (error) {
    console.error('获取消息详情失败:', error)
    showToast('加载失败', 'error')
    router.push('/user/messages?refresh=1')
  } finally {
    loading.value = false
  }
}

const scrollToBottom = () => {
  if (historyContainer.value) {
    historyContainer.value.scrollTop = historyContainer.value.scrollHeight
  }
}

const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const goBack = () => {
  router.push('/user/messages?refresh=1')
}

const viewRelatedItem = () => {
  const itemType = message.value.itemType || message.value.item_type
  const itemId = message.value.itemId || message.value.item_id

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

const sendReply = async () => {
  if (!replyForm.content.trim()) {
    showToast('请输入回复内容', 'warning')
    return
  }

  replying.value = true
  try {
    const replyData = {
      fromUserId: currentUserId.value,
      toUserId: message.value.fromUserId,
      itemId: message.value.itemId || message.value.item_id,
      itemType: message.value.itemType || message.value.item_type,
      content: replyForm.content
    }

    const response = await sendMessage(replyData)

    if (response.success || response.code === 200) {
      showToast('回复发送成功', 'success')

      messageHistory.value.push({
        id: response.messageId || Date.now(),
        fromUserId: currentUserId.value,
        content: replyForm.content,
        createTime: new Date().toISOString()
      })

      replyForm.content = ''

      await nextTick()
      scrollToBottom()
    } else {
      showToast(response.error || response.message || '发送失败', 'error')
    }
  } catch (error) {
    console.error('发送回复失败:', error)
    showToast('发送失败', 'error')
  } finally {
    replying.value = false
  }
}
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-primary { background-color: #6750A4; }
.bg-secondary { background-color: #625B71; }
.bg-success { background-color: #006E1C; }
.bg-warning { background-color: #DCBC43; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-outline-variant { border-color: #CAC4D0; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }

/* Animations */
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
.animate-in { animation: fadeIn 0.3s ease-out forwards; }
</style>