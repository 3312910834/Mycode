<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Loading Overlay -->
    <div v-if="loading" class="fixed inset-0 z-[60] flex items-center justify-center bg-surface/80 backdrop-blur-sm">
      <div class="flex flex-col items-center gap-4">
        <span class="material-symbols-outlined text-5xl text-primary animate-spin">progress_activity</span>
        <p class="text-on-surface-variant font-medium">加载中...</p>
      </div>
    </div>

    <!-- Top Navigation Bar -->
    <header class="sticky top-0 z-40 bg-surface-container-low/90 backdrop-blur-md border-b border-surface-container-high">
      <div class="max-w-6xl mx-auto px-4 md:px-6 h-16 flex items-center justify-between">
        <button
          @click="$router.back()"
          class="flex items-center gap-2 text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high px-4 py-2 rounded-full transition-all duration-200 font-medium"
        >
          <span class="material-symbols-outlined">arrow_back</span>
          <span class="hidden sm:inline">返回列表</span>
        </button>

        <div class="flex items-center gap-3">
          <span
            v-if="detail.status"
            class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider"
            :class="{
              'bg-warning/20 text-warning': detail.status === 'pending',
              'bg-secondary/20 text-secondary': detail.status === 'published',
              'bg-success/20 text-success': detail.status === 'found',
              'bg-error/20 text-error': detail.status === 'closed'
            }"
          >
            {{ getStatusText(detail.status) }}
          </span>

          <button
            v-if="!isOwner && isLogin"
            @click="contactOwner"
            class="flex items-center gap-2 bg-primary text-on-primary px-5 py-2.5 rounded-full font-bold text-sm shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300"
          >
            <span class="material-symbols-outlined text-base">mail</span>
            联系失主
          </button>

          <button
            v-if="isOwner"
            @click="editItem"
            class="flex items-center gap-2 bg-secondary-container text-secondary px-5 py-2.5 rounded-full font-bold text-sm hover:bg-secondary-container/80 transition-colors"
          >
            <span class="material-symbols-outlined text-base">edit</span>
            编辑
          </button>
        </div>
      </div>
    </header>

    <main class="max-w-6xl mx-auto px-4 md:px-6 py-8 space-y-8">

      <!-- Main Content Card -->
      <div class="bg-surface-container-low rounded-3xl overflow-hidden border border-surface-container-high shadow-sm">

        <!-- Header Section -->
        <div class="p-6 md:p-10 border-b border-surface-container-high bg-gradient-to-r from-primary-container/10 to-transparent">
          <div class="flex flex-col md:flex-row md:items-start justify-between gap-6">
            <div class="space-y-4 flex-1">
              <h1 class="text-3xl md:text-4xl font-black text-on-surface font-headline leading-tight">
                {{ detail.title }}
              </h1>

              <div class="flex flex-wrap items-center gap-4 text-sm font-medium text-on-surface-variant">
                <div class="flex items-center gap-1.5 bg-surface-container-highest px-3 py-1.5 rounded-lg">
                  <span class="material-symbols-outlined text-base">calendar_today</span>
                  <span>发布于 {{ formatDateTime(detail.create_time || detail.createTime) }}</span>
                </div>
                <div v-if="detail.category" class="flex items-center gap-1.5 bg-surface-container-highest px-3 py-1.5 rounded-lg">
                  <span class="material-symbols-outlined text-base">folder</span>
                  <span>{{ getCategoryText(detail.category) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Body Section -->
        <div class="p-6 md:p-10 space-y-10">

          <!-- Info Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <!-- Basic Info -->
            <div class="bg-surface-container-highest/50 rounded-2xl p-6 border border-surface-container-high">
              <h3 class="text-lg font-bold text-on-surface font-headline mb-6 flex items-center gap-2">
                <span class="material-symbols-outlined text-primary">info</span>
                基本信息
              </h3>
              <dl class="space-y-4">
                <div class="flex justify-between border-b border-surface-container-high pb-2 last:border-0">
                  <dt class="text-sm font-medium text-on-surface-variant">物品名称</dt>
                  <dd class="text-sm font-bold text-on-surface text-right">{{ detail.title }}</dd>
                </div>
                <div v-if="detail.category" class="flex justify-between border-b border-surface-container-high pb-2 last:border-0">
                  <dt class="text-sm font-medium text-on-surface-variant">物品分类</dt>
                  <dd class="text-sm font-bold text-on-surface text-right">{{ getCategoryText(detail.category) }}</dd>
                </div>
                <div class="flex justify-between border-b border-surface-container-high pb-2 last:border-0">
                  <dt class="text-sm font-medium text-on-surface-variant">丢失地点</dt>
                  <dd class="text-sm font-bold text-on-surface text-right">{{ detail.lost_location || detail.lostLocation }}</dd>
                </div>
                <div class="flex justify-between border-b border-surface-container-high pb-2 last:border-0">
                  <dt class="text-sm font-medium text-on-surface-variant">丢失时间</dt>
                  <dd class="text-sm font-bold text-on-surface text-right">{{ formatDateTime(detail.lost_time || detail.lostTime) }}</dd>
                </div>
                <div v-if="detail.features" class="pt-2">
                  <dt class="text-sm font-medium text-on-surface-variant mb-1">物品特征</dt>
                  <dd class="text-sm text-on-surface bg-surface-container-low p-3 rounded-xl">{{ detail.features }}</dd>
                </div>
              </dl>
            </div>

                        <!-- Owner Info (纯前端美化版) -->
            <div class="bg-surface-container-highest/50 rounded-2xl p-6 border border-surface-container-high flex flex-col">
              <h3 class="text-lg font-bold text-on-surface font-headline mb-6 flex items-center gap-2">
                <span class="material-symbols-outlined text-secondary">person</span>
                发布者信息
              </h3>

              <!-- 【丰满的用户信息展示区】 -->
              <div class="flex items-center gap-4 mb-6">
                <!-- 渐变首字母头像 -->
                <div class="w-16 h-16 rounded-full bg-gradient-to-br from-primary to-primary-container text-white flex items-center justify-center text-2xl font-bold shadow-md shrink-0">
                  <span>{{ (detail.username || 'A')[0].toUpperCase() }}</span>
                </div>
                
                <div class="flex-1">
                  <!-- 姓名与认证徽章 -->
                  <div class="flex items-center gap-2">
                    <div class="text-lg font-bold text-on-surface">{{ detail.username || '校园用户' }}</div>
                    <div class="bg-success/15 text-success px-1.5 py-0.5 rounded-md flex items-center gap-0.5 text-[11px] font-bold border border-success/20">
                      <span class="material-symbols-outlined text-[14px]">verified_user</span>
                      已认证
                    </div>
                  </div>
                  
                  <!-- 补充信息标签栏 (用系统通用标签增加信任感) -->
                  <div class="flex flex-wrap items-center gap-2 mt-2">
                    <div class="text-xs text-on-surface-variant flex items-center gap-1 bg-surface-container-high px-2 py-1 rounded-md">
                      <span class="material-symbols-outlined text-[14px]">badge</span>
                      本校学生
                    </div>
                    <div class="text-xs text-secondary flex items-center gap-1 bg-secondary-container/50 px-2 py-1 rounded-md">
                      <span class="material-symbols-outlined text-[14px]">thumb_up</span>
                      信誉良好
                    </div>
                  </div>
                </div>
              </div>

              <!-- 仅所有者可见 -->
              <div v-if="isOwner" class="space-y-3 mt-auto">
                <div v-if="detail.userPhone" class="flex items-center gap-3 p-3 bg-surface rounded-xl border border-surface-container-high">
                  <span class="material-symbols-outlined text-primary">phone</span>
                  <span class="text-sm font-bold text-on-surface">{{ detail.userPhone }}</span>
                </div>
                <div v-if="detail.contactInfo" class="flex items-center gap-3 p-3 bg-surface rounded-xl border border-surface-container-high">
                  <span class="material-symbols-outlined text-primary">chat</span>
                  <span class="text-sm font-bold text-on-surface">{{ detail.contactInfo }}</span>
                </div>
              </div>

              <!-- 访客视角：获取联系方式模块 -->
              <div v-else class="mt-auto">
                <!-- 未获取时：带锁的区域 -->
                <div 
                  v-if="!contactAcquired" 
                  @click="contactOwner" 
                  class="p-6 bg-surface-container-low hover:bg-surface-container-high cursor-pointer border border-dashed border-surface-container-highest rounded-2xl text-center transition-all duration-300 flex flex-col items-center justify-center gap-3 group"
                >
                  <div class="w-12 h-12 rounded-full bg-surface-container-highest flex items-center justify-center group-hover:scale-110 group-hover:bg-primary-container group-hover:text-primary transition-all duration-300">
                    <span class="material-symbols-outlined text-2xl">lock</span>
                  </div>
                  <div class="space-y-1">
                    <p class="text-base font-bold text-on-surface group-hover:text-primary transition-colors">点击获取联系方式</p>
                    <p class="text-xs text-on-surface-variant">保护隐私安全，实名认证可见</p>
                  </div>
                </div>

                <!-- 成功获取联系方式后 -->
                <div v-else class="space-y-4 animate-in">
                  <div class="space-y-3">
                    <!-- 电话 (读取 userPhone) -->
                    <div v-if="detail.userPhone" class="flex items-center justify-between p-4 bg-primary-container/20 border border-primary/20 rounded-2xl group hover:border-primary/40 transition-colors">
                      <div class="flex items-center gap-4">
                        <div class="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                          <span class="material-symbols-outlined text-primary">phone_iphone</span>
                        </div>
                        <div class="flex flex-col">
                          <span class="text-xs text-on-surface-variant font-bold mb-0.5">注册手机号</span>
                          <span class="text-xl font-black text-on-surface tracking-wider font-mono">{{ detail.userPhone }}</span>
                        </div>
                      </div>
                      <button @click="copyToClipboard(detail.userPhone)" class="text-primary hover:bg-primary/10 p-2.5 rounded-full transition-colors flex items-center gap-1" title="一键复制">
                        <span class="material-symbols-outlined text-[20px]">content_copy</span>
                        <span class="text-xs font-bold hidden sm:inline">复制</span>
                      </button>
                    </div>

                    <!-- 备用联系方式 (读取表单自带的 contactInfo，如微信/QQ) -->
                    <div v-if="detail.contactInfo" class="flex items-center justify-between p-4 bg-secondary-container/20 border border-secondary/20 rounded-2xl group hover:border-secondary/40 transition-colors">
                      <div class="flex items-center gap-4">
                        <div class="w-10 h-10 rounded-full bg-secondary/10 flex items-center justify-center">
                          <span class="material-symbols-outlined text-secondary">forum</span>
                        </div>
                        <div class="flex flex-col">
                          <span class="text-xs text-on-surface-variant font-bold mb-0.5">补充联系方式 (微信/QQ等)</span>
                          <span class="text-lg font-black text-on-surface tracking-wider">{{ detail.contactInfo }}</span>
                        </div>
                      </div>
                      <button @click="copyToClipboard(detail.contactInfo)" class="text-secondary hover:bg-secondary/10 p-2.5 rounded-full transition-colors flex items-center gap-1" title="一键复制">
                        <span class="material-symbols-outlined text-[20px]">content_copy</span>
                        <span class="text-xs font-bold hidden sm:inline">复制</span>
                      </button>
                    </div>

                    <!-- 如果两个都没留 -->
                    <div v-if="!detail.userPhone && !detail.contactInfo" class="text-center p-4 bg-surface-container-low rounded-xl text-on-surface-variant text-sm">
                      该用户未留下任何联系方式，请尝试在留言区留言。
                    </div>
                  </div>
                  
                  <!-- 防诈骗提示 -->
                  <div class="flex items-start gap-2.5 p-3.5 bg-error/10 border border-error/20 rounded-xl">
                    <span class="material-symbols-outlined text-error text-lg mt-0.5">shield_person</span>
                    <p class="text-sm text-error font-medium leading-relaxed">
                      <strong>防诈骗提醒：</strong>切勿轻易给陌生人转账、汇款。交接物品建议在校园人多明亮的公共区域进行。
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Description (保持不变) -->
          <div v-if="detail.description" class="space-y-4">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">description</span>
              详细描述
            </h3>
            <div class="bg-surface-container-highest/30 rounded-2xl p-6 border border-surface-container-high">
              <p class="text-on-surface whitespace-pre-wrap leading-relaxed">{{ detail.description }}</p>
            </div>
          </div>

          <!-- Smart Match Recommendations (保持不变) -->
          <div v-if="smartMatchLoading || smartMatchCards.length > 0" class="space-y-4">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2">
              <span class="material-symbols-outlined text-secondary">auto_awesome</span>
              可能匹配到以下招领信息
            </h3>
            <div v-if="smartMatchLoading" class="text-sm text-on-surface-variant">AI正在分析匹配中...</div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div
                v-for="(item, idx) in smartMatchCards"
                :key="'smart-card-' + idx"
                class="p-4 rounded-2xl bg-surface border border-surface-container-high"
              >
                <div class="flex items-center justify-between mb-2">
                  <h4 class="font-bold text-on-surface line-clamp-1">{{ item.title || '匹配招领信息' }}</h4>
                  <span class="text-xs font-bold text-secondary">{{ formatSimilarity(item.similarity) }}</span>
                </div>
                <p class="text-xs text-on-surface-variant mb-2 line-clamp-2">{{ item.reason || '无匹配原因说明' }}</p>
                <button
                  v-if="item.id"
                  @click="router.push(`/found/${item.id}`)"
                  class="text-xs font-bold text-primary hover:underline"
                >
                  查看招领详情
                </button>
              </div>
            </div>
          </div>

          <!-- Images Gallery (保持不变) -->
          <div v-if="images && images.length > 0" class="space-y-4">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">photo_library</span>
              物品图片
            </h3>
            <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
              <div
                v-for="(img, index) in images"
                :key="index"
                @click="openImagePreview(index)"
                class="group relative aspect-square rounded-2xl overflow-hidden cursor-pointer bg-surface-container-high border border-surface-container-high"
              >
                <img :src="img" alt="Item image" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" />
                <div class="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors duration-300 flex items-center justify-center">
                  <span class="material-symbols-outlined text-white opacity-0 group-hover:opacity-100 transform scale-50 group-hover:scale-100 transition-all duration-300 drop-shadow-lg">zoom_in</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Status History Timeline (保持不变) -->
          <div v-if="statusHistory && statusHistory.length > 0" class="space-y-4">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">history</span>
              状态记录
            </h3>
            <div class="relative pl-8 border-l-2 border-surface-container-high space-y-8 py-4">
              <div
                v-for="(record) in statusHistory"
                :key="record.id"
                class="relative"
              >
                <div
                  class="absolute -left-[41px] top-1 w-6 h-6 rounded-full border-4 border-surface flex items-center justify-center"
                  :class="record.status === 'found' ? 'bg-success border-success' : 'bg-primary border-primary'"
                >
                  <div class="w-2 h-2 rounded-full bg-white"></div>
                </div>

                <div class="bg-surface-container-highest/30 rounded-2xl p-4 border border-surface-container-high">
                  <div class="flex justify-between items-start mb-2">
                    <h4 class="font-bold text-on-surface">{{ getStatusRecordText(record) }}</h4>
                    <span class="text-xs font-medium text-on-surface-variant bg-surface-container-high px-2 py-1 rounded-md">
                      {{ formatDateTime(record.create_time) }}
                    </span>
                  </div>
                  <p v-if="record.remark" class="text-sm text-on-surface-variant mt-1">{{ record.remark }}</p>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </main>

    <!-- Image Preview Modal -->
    <div v-if="previewIndex !== null" class="fixed inset-0 z-[70] flex items-center justify-center bg-black/90 backdrop-blur-md p-4" @click="previewIndex = null">
      <button @click="previewIndex = null" class="absolute top-6 right-6 text-white/70 hover:text-white p-2">
        <span class="material-symbols-outlined text-4xl">close</span>
      </button>
      <img :src="images[previewIndex]" class="max-w-full max-h-full object-contain rounded-lg shadow-2xl" @click.stop />
      <div class="absolute bottom-6 text-white/80 text-sm font-medium">
        {{ previewIndex + 1 }} / {{ images.length }}
      </div>
    </div>

    <!-- 获取联系方式确认弹窗 (新增) -->
    <div v-if="showContactConfirm" class="fixed inset-0 z-[80] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in" @click.self="showContactConfirm = false">
      <div class="bg-surface rounded-3xl w-full max-w-md overflow-hidden shadow-2xl transform transition-all">
        <div class="p-6 md:p-8 space-y-6">
          <div class="w-16 h-16 bg-primary-container rounded-2xl flex items-center justify-center mb-6">
            <span class="material-symbols-outlined text-3xl text-primary">contact_mail</span>
          </div>
          
          <div>
            <h2 class="text-2xl font-black text-on-surface mb-2">获取联系方式</h2>
            <p class="text-on-surface-variant leading-relaxed">
              为了保护用户隐私，联系方式默认隐藏。获取后将直接显示失主的电话或微信/邮箱。
            </p>
          </div>

          <div class="bg-surface-container-low p-4 rounded-xl space-y-3 border border-surface-container-high">
            <div class="flex gap-2.5 text-sm text-on-surface-variant">
              <span class="material-symbols-outlined text-lg text-primary">check_circle</span>
              <span class="leading-relaxed">联系方式仅限用于归还物品沟通</span>
            </div>
            <div class="flex gap-2.5 text-sm text-error">
              <span class="material-symbols-outlined text-lg">security</span>
              <span class="leading-relaxed font-bold">请谨防诈骗，勿向陌生人提前转账</span>
            </div>
          </div>
          
          <div class="flex items-center justify-end gap-3 pt-4">
            <button 
              @click="showContactConfirm = false"
              class="px-5 py-2.5 rounded-full font-bold text-sm text-on-surface-variant hover:bg-surface-container-highest transition-colors"
            >
              再想想
            </button>
            <button 
              @click="confirmGetContact"
              class="px-6 py-2.5 rounded-full font-bold text-sm bg-primary text-on-primary shadow-md shadow-primary/20 hover:shadow-lg hover:-translate-y-0.5 active:translate-y-0 transition-all"
            >
              确认获取
            </button>
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
import { getLostDetail, smartMatchLostText } from '@/api/lost'
import { getFoundList } from '@/api/found'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const detail = ref({})
const previewIndex = ref(null)
const smartMatchLoading = ref(false)
const smartMatchCards = ref([])

// 联系方式状态控制
const showContactConfirm = ref(false)
const contactAcquired = ref(false)

const categoryMap = {
  electronics: '电子产品',
  study: '学习用品',
  cards: '证件卡类',
  clothing: '衣物饰品',
  books: '书籍资料',
  others: '其他物品'
}

const statusMap = {
  pending: { text: '待处理', color: 'warning' },
  published: { text: '已发布', color: 'secondary' },
  found: { text: '已找到', color: 'success' },
  closed: { text: '已关闭', color: 'error' }
}

const isLogin = computed(() => userStore.isLogin)
const isOwner = computed(() => {
  return detail.value.user?.id === userStore.userId || detail.value.userId === userStore.userId
})

const images = computed(() => {
  if (!detail.value.images) return []
  
  const baseUrl = 'http://localhost:8080'
  
  const imgArray = Array.isArray(detail.value.images)
    ? detail.value.images
    : detail.value.images.split(',').filter(img => img.trim())
    
  return imgArray.map(img => {
    if (img.startsWith('http')) return img 
    return baseUrl + img
  })
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
    const result = await getLostDetail(id)

    if (result.code === 200) {
      detail.value = result.data || {}
      await loadSmartMatches()
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

const loadSmartMatches = async () => {
  const lostText = detail.value?.description || detail.value?.title
  if (!lostText) return
  smartMatchLoading.value = true
  try {
    const [matchResp, foundResp] = await Promise.all([
      smartMatchLostText(lostText),
      getFoundList({ page: 1, size: 20 })
    ])
    const matches = matchResp?.code === 200 ? (matchResp.data?.matches || []) : []
    const foundItems = foundResp?.code === 200 ? (foundResp.data?.items || []) : []
    smartMatchCards.value = matches.slice(0, 4).map((m) => {
      const mapped = foundItems[(m.index || 1) - 1] || {}
      return {
        id: mapped.id,
        title: mapped.title,
        reason: m.reason,
        similarity: m.similarity
      }
    })
  } catch (error) {
    console.error('加载智能匹配失败:', error)
    smartMatchCards.value = []
  } finally {
    smartMatchLoading.value = false
  }
}

const formatDateTime = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return dateString
    return date.toLocaleString('zh-CN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit'
    })
  } catch {
    return dateString
  }
}

const getCategoryText = (category) => categoryMap[category] || category || '未分类'

const getStatusText = (status) => statusMap[status]?.text || status || '未知'

const getStatusRecordText = (record) => {
  const statusText = getStatusText(record.status)
  return `${statusText} - ${record.remark || '状态更新'}`
}

// 触发获取联系方式流程
const contactOwner = () => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录后查看联系方式')
    router.push('/login')
    return
  }
  // 若已经获取了，就给个平滑提示
  if (contactAcquired.value) {
    ElMessage.info('已获取联系方式，请查看下方信息')
    return
  }
  showContactConfirm.value = true
}

// 确认获取联系方式
const confirmGetContact = () => {
  // 可选：在此处向后端发送统计/通知请求 (Record action)
  contactAcquired.value = true
  showContactConfirm.value = false
  ElMessage.success('成功获取联系方式')
}

// 一键复制功能
const copyToClipboard = async (text) => {
  if (!text) {
    ElMessage.warning('暂无内容可复制')
    return
  }
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
      ElMessage.success('已成功复制到剪贴板')
    } else {
      const textArea = document.createElement("textarea")
      textArea.value = text
      textArea.style.position = "absolute"
      textArea.style.left = "-999999px"
      document.body.prepend(textArea)
      textArea.select()
      try {
        document.execCommand('copy')
        ElMessage.success('已成功复制到剪贴板')
      } catch (error) {
        ElMessage.error('浏览器不支持快捷复制，请手动选中复制')
      } finally {
        textArea.remove()
      }
    }
  } catch (err) {
    console.error('复制出错', err)
    ElMessage.error('复制失败，请手动选中复制')
  }
}

const editItem = () => {
  router.push(`/user/lost/edit/${route.params.id}`)
}

const openImagePreview = (index) => {
  previewIndex.value = index
}

const formatSimilarity = (value) => {
  if (value === null || value === undefined || Number.isNaN(Number(value))) return '0%'
  return `相似度 ${Math.round(Number(value) * 100)}%`
}
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-primary { background-color: #6750A4; }
.bg-primary-container { background-color: #EADDFF; }
.bg-secondary { background-color: #625B71; }
.bg-secondary-container { background-color: #E8DEF8; }
.bg-success { background-color: #006E1C; }
.bg-warning { background-color: #DCBC43; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-success { color: #006E1C; }
.text-warning { color: #63500A; }
.text-error { color: #B3261E; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-primary-container\/30 { border-color: rgba(234, 221, 255, 0.3); }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.bg-primary-container\/10 { background-color: rgba(234, 221, 255, 0.1); }
.bg-primary-container\/20 { background-color: rgba(234, 221, 255, 0.2); }
.bg-secondary-container\/50 { background-color: rgba(232, 222, 248, 0.5); }
.bg-success\/15 { background-color: rgba(0, 110, 28, 0.15); }
.bg-success\/20 { background-color: rgba(0, 110, 28, 0.2); }
.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }
.bg-error\/20 { background-color: rgba(179, 38, 30, 0.2); }

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-in {
  animation: fadeIn 0.25s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
</style>