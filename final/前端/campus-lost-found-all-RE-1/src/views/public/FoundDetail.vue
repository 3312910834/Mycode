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
              'bg-success/20 text-success': detail.status === 'published',
              'bg-secondary/20 text-secondary': detail.status === 'claimed',
              'bg-primary/20 text-primary': detail.status === 'completed',
              'bg-error/20 text-error': detail.status === 'closed'
            }"
          >
            {{ getStatusText(detail.status) }}
          </span>

          <button
            v-if="canClaim"
            @click="claimItem"
            class="flex items-center gap-2 bg-primary text-on-primary px-5 py-2.5 rounded-full font-bold text-sm shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300"
          >
            <span class="material-symbols-outlined text-base">check_circle</span>
            认领物品
          </button>

          <button
            v-if="isOwner && detail.status === 'published'"
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
                <div class="flex items-center gap-1.5 bg-surface-container-highest px-3 py-1.5 rounded-lg font-mono">
                  <span class="material-symbols-outlined text-base">tag</span>
                  <span>ID: F{{ formatNumber(detail.id) }}</span>
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
                  <dd class="text-sm font-bold text-on-surface text-right">{{ detail.category }}</dd>
                </div>
                <div class="flex justify-between border-b border-surface-container-high pb-2 last:border-0">
                  <dt class="text-sm font-medium text-on-surface-variant">拾获地点</dt>
                  <dd class="text-sm font-bold text-on-surface text-right">{{ detail.found_location || detail.foundLocation }}</dd>
                </div>
                <div class="flex justify-between border-b border-surface-container-high pb-2 last:border-0">
                  <dt class="text-sm font-medium text-on-surface-variant">拾获时间</dt>
                  <dd class="text-sm font-bold text-on-surface text-right">{{ formatDateTime(detail.found_time || detail.foundTime) }}</dd>
                </div>
                <div v-if="detail.storage_location || detail.storageLocation" class="flex justify-between border-b border-surface-container-high pb-2 last:border-0">
                  <dt class="text-sm font-medium text-on-surface-variant">保管地点</dt>
                  <dd class="text-sm font-bold text-on-surface text-right">{{ detail.storage_location || detail.storageLocation }}</dd>
                </div>
                <div v-if="detail.claim_requirement || detail.claimRequirement" class="pt-2">
                  <dt class="text-sm font-medium text-on-surface-variant mb-1">认领要求</dt>
                  <dd class="text-sm text-on-surface bg-surface-container-low p-3 rounded-xl">{{ detail.claim_requirement || detail.claimRequirement }}</dd>
                </div>
              </dl>
            </div>

            <!-- Finder Info -->
            <div class="bg-surface-container-highest/50 rounded-2xl p-6 border border-surface-container-high">
              <h3 class="text-lg font-bold text-on-surface font-headline mb-6 flex items-center gap-2">
                <span class="material-symbols-outlined text-secondary">person</span>
                拾获者信息
              </h3>

              <div class="flex items-center gap-4 mb-6">
                <div class="w-16 h-16 rounded-full bg-primary-container text-primary flex items-center justify-center text-2xl font-bold overflow-hidden shrink-0">
                  <img v-if="detail.user?.avatar" :src="detail.user.avatar" alt="Avatar" class="w-full h-full object-cover" />
                  <span v-else>{{ (detail.user?.username || detail.username || 'A')[0].toUpperCase() }}</span>
                </div>
                <div>
                  <div class="text-lg font-bold text-on-surface">{{ detail.user?.username || detail.username || '匿名' }}</div>
                  <div v-if="detail.user?.college" class="text-sm text-on-surface-variant flex items-center gap-1 mt-1">
                    <span class="material-symbols-outlined text-sm">school</span>
                    {{ detail.user.college }}
                  </div>
                </div>
              </div>

              <div v-if="detail.contact_info || detail.contactInfo" class="space-y-3 mb-6">
                <div class="flex items-center gap-3 p-3 bg-surface rounded-xl">
                  <span class="material-symbols-outlined text-primary">phone</span>
                  <span class="text-sm font-medium text-on-surface">{{ detail.contact_info || detail.contactInfo }}</span>
                </div>
                <div class="flex items-center gap-3 p-3 bg-surface rounded-xl">
                  <span class="material-symbols-outlined text-primary">schedule</span>
                  <span class="text-sm font-medium text-on-surface">有效期：{{ detail.claim_period || detail.claimPeriod || 30 }}天</span>
                </div>
              </div>

              <div v-else-if="!isOwner" class="p-4 bg-primary-container/20 border border-primary-container/30 rounded-xl text-center mb-6">
                <p class="text-sm font-medium text-primary">请使用系统消息联系拾获者</p>
              </div>

              <div v-if="!isOwner && detail.status === 'published'" class="p-4 bg-tertiary-container/20 border border-tertiary-container/30 rounded-xl flex items-start gap-3">
                <span class="material-symbols-outlined text-tertiary shrink-0">info</span>
                <p class="text-sm text-tertiary font-medium">请仔细核对物品信息后再进行认领操作</p>
              </div>
            </div>
          </div>

          <!-- Description -->
          <div v-if="detail.description" class="space-y-4">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">description</span>
              详细描述
            </h3>
            <div class="bg-surface-container-highest/30 rounded-2xl p-6 border border-surface-container-high">
              <p class="text-on-surface whitespace-pre-wrap leading-relaxed">{{ detail.description }}</p>
              <div v-if="detail.contact_info || detail.contactInfo" class="mt-4 pt-4 border-t border-surface-container-high">
                <p class="text-xs text-on-surface-variant italic flex items-center gap-2">
                  <span class="material-symbols-outlined text-sm">lock</span>
                  注：为保护隐私，部分个人信息已隐藏。认领时请提供有效证件进行核对。
                </p>
              </div>
            </div>
          </div>

          <!-- Images Gallery -->
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

          <!-- Claim Process Steps -->
          <div v-if="!isOwner && detail.status === 'published'" class="bg-surface-container-low rounded-2xl p-6 md:p-8 border border-surface-container-high">
            <h3 class="text-lg font-bold text-on-surface font-headline mb-8 flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">fact_check</span>
              认领流程
            </h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              <div v-for="i in 4" :key="i" class="relative flex flex-col items-center text-center group">
                <!-- Connector Line (Desktop) -->
                <div v-if="i < 4" class="hidden lg:block absolute top-6 left-1/2 w-full h-0.5 bg-surface-container-high -z-10"></div>

                <div
                  class="w-12 h-12 rounded-full flex items-center justify-center text-lg font-bold border-4 transition-all duration-500 mb-4 z-10"
                  :class="step >= i ? 'bg-primary border-primary text-on-primary shadow-lg shadow-primary/30' : 'bg-surface-container-high border-surface-container-high text-on-surface-variant'"
                >
                  {{ i }}
                </div>
                <div class="font-bold text-on-surface mb-1">{{ ['核对信息', '在线认领', '联系确认', '线下领取'][i-1] }}</div>
                <div class="text-xs text-on-surface-variant">{{ ['确认物品为本人所有', '填写认领申请', '与拾获者取得联系', '前往保管地点领取'][i-1] }}</div>
              </div>
            </div>
          </div>

          <!-- Tips -->
          <div class="bg-secondary-container/20 border border-secondary-container/30 rounded-2xl p-6">
            <h3 class="text-lg font-bold text-secondary font-headline mb-4 flex items-center gap-2">
              <span class="material-symbols-outlined">lightbulb</span>
              温馨提示
            </h3>
            <ul class="space-y-3">
              <li class="flex items-start gap-3 text-on-surface-variant text-sm">
                <span class="material-symbols-outlined text-success shrink-0 mt-0.5">check_circle</span>
                <span>请妥善保管个人物品，避免丢失</span>
              </li>
              <li class="flex items-start gap-3 text-on-surface-variant text-sm">
                <span class="material-symbols-outlined text-success shrink-0 mt-0.5">check_circle</span>
                <span>认领时请携带有效身份证件</span>
              </li>
              <li class="flex items-start gap-3 text-on-surface-variant text-sm">
                <span class="material-symbols-outlined text-success shrink-0 mt-0.5">check_circle</span>
                <span>请在有效期内认领，逾期物品将上交学校处理</span>
              </li>
            </ul>
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

    <!-- Claim Dialog -->
    <div v-if="showClaimDialog" class="fixed inset-0 z-[80] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-on-surface/60 backdrop-blur-sm" @click="showClaimDialog = false"></div>

      <div class="relative bg-surface-container-low w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh] animate-in fade-in zoom-in-95 duration-200">
        <div class="p-6 border-b border-surface-container-high bg-surface-container-highest/30">
          <h3 class="text-xl font-bold text-on-surface font-headline">确认认领</h3>
          <p class="text-sm text-on-surface-variant mt-1">请仔细核对以下信息</p>
        </div>

        <div class="p-6 overflow-y-auto custom-scrollbar space-y-4">
          <div class="bg-surface-container-highest rounded-xl p-4 space-y-2 text-sm">
            <div class="flex justify-between">
              <span class="text-on-surface-variant">物品名称</span>
              <span class="font-bold text-on-surface">{{ detail?.title }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-on-surface-variant">拾获地点</span>
              <span class="font-bold text-on-surface">{{ detail?.found_location || detail?.foundLocation }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-on-surface-variant">保管地点</span>
              <span class="font-bold text-on-surface">{{ detail?.storage_location || detail?.storageLocation || detail?.found_location || detail?.foundLocation }}</span>
            </div>
          </div>

          <div class="p-4 bg-error/10 border border-error/20 rounded-xl flex items-start gap-3">
            <span class="material-symbols-outlined text-error shrink-0">warning</span>
            <p class="text-sm text-error font-medium">请确保此物品为您所有，虚假认领将影响您的个人信誉记录。</p>
          </div>

          <div class="space-y-4 pt-2">
            <div class="group relative">
              <label class="block text-xs font-bold text-on-surface-variant uppercase tracking-wide mb-2 ml-1">联系方式 <span class="text-error">*</span></label>
              <div class="relative">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                  <span class="material-symbols-outlined">phone</span>
                </span>
                <input
                  v-model="claimForm.contact"
                  type="text"
                  placeholder="请输入您的联系电话"
                  class="w-full pl-12 pr-4 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                />
              </div>
            </div>

            <div class="group relative">
              <label class="block text-xs font-bold text-on-surface-variant uppercase tracking-wide mb-2 ml-1">认领说明</label>
              <div class="relative">
                <span class="absolute left-4 top-4 text-on-surface-variant group-focus-within:text-primary transition-colors">
                  <span class="material-symbols-outlined">comment</span>
                </span>
                <textarea
                  v-model="claimForm.description"
                  rows="3"
                  placeholder="请描述您的认领理由或物品特征"
                  class="w-full pl-12 pr-4 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 resize-none"
                ></textarea>
              </div>
            </div>
          </div>
        </div>

        <div class="p-4 border-t border-surface-container-high bg-surface-container-highest/30 flex justify-end gap-3">
          <button @click="showClaimDialog = false" class="px-6 py-2.5 rounded-full font-bold text-sm text-on-surface-variant hover:bg-surface-container-high transition-colors">取消</button>
          <button
            @click="submitClaim"
            :disabled="claimLoading || !claimForm.contact.trim()"
            class="px-8 py-2.5 bg-primary text-on-primary rounded-full font-bold text-sm shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex items-center gap-2"
          >
            <span v-if="claimLoading" class="material-symbols-outlined animate-spin text-base">progress_activity</span>
            <span>{{ claimLoading ? '提交中...' : '确认认领' }}</span>
          </button>
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
import { getFoundDetail } from '@/api/found'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const showClaimDialog = ref(false)
const claimLoading = ref(false)
const step = ref(1)
const detail = ref({})
const previewIndex = ref(null)

const statusMap = {
  pending: { text: '待审核', color: 'warning' },
  published: { text: '待认领', color: 'success' },
  claimed: { text: '已认领', color: 'secondary' },
  completed: { text: '已完成', color: 'primary' },
  closed: { text: '已关闭', color: 'error' }
}

const claimForm = ref({
  contact: '',
  description: ''
})

const isLogin = computed(() => userStore.isLogin)
const isOwner = computed(() => {
  return detail.value.user?.id === userStore.userId || detail.value.userId === userStore.userId
})

const canClaim = computed(() => {
  if (!isLogin.value || isOwner.value || !detail.value) return false
  return detail.value.status === 'published'
})

const images = computed(() => {
  if (!detail.value.images) return []
  return Array.isArray(detail.value.images)
    ? detail.value.images
    : detail.value.images.split(',').filter(img => img.trim())
})

onMounted(() => {
  loadDetail()
})

const loadDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const result = await getFoundDetail(id)

    console.log('招领详情 API 响应:', result)

    if (result.code === 200) {
      detail.value = result.data || {}
    } else {
      ElMessage.error(result.msg || '获取招领详情失败')
      router.back()
    }
  } catch (error) {
    console.error('加载招领详情失败:', error)
    ElMessage.error('加载失败')
    router.back()
  } finally {
    loading.value = false
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

const formatNumber = (id) => {
  if (!id) return '00000000'
  return String(id).padStart(8, '0')
}

const getStatusText = (status) => {
  return statusMap[status]?.text || status || '未知'
}

const openImagePreview = (index) => {
  previewIndex.value = index
}

const claimItem = () => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (detail.value.status !== 'published') {
    ElMessage.warning('该物品当前不可认领')
    return
  }

  showClaimDialog.value = true
}

const submitClaim = async () => {
  if (!claimForm.value.contact.trim()) {
    ElMessage.warning('请输入联系方式')
    return
  }

  claimLoading.value = true
  try {
    const result = await request({
      url: `/foundapi/${detail.value.id}/claim`,
      method: 'post',
      data: claimForm.value
    })

    if (result.code === 200) {
      ElMessage.success('认领申请已提交，请等待拾获者确认')
      showClaimDialog.value = false
      await loadDetail()

      step.value = 2
      setTimeout(() => {
        step.value = 3
        ElMessage.info('拾获者已收到您的认领申请，请保持电话畅通')
      }, 2000)
    } else {
      ElMessage.error(result.msg || '认领失败')
    }
  } catch (error) {
    console.error('认领失败:', error)
    ElMessage.error('认领失败，请稍后重试')
  } finally {
    claimLoading.value = false
  }
}

const editItem = () => {
  router.push(`/user/found/edit/${route.params.id}`)
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
.bg-tertiary { background-color: #7D5260; }
.bg-tertiary-container { background-color: #FFD8E4; }
.bg-warning { background-color: #DCBC43; }
.bg-success { background-color: #006E1C; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-tertiary { color: #7D5260; }
.text-warning { color: #63500A; }
.text-success { color: #006E1C; }
.text-error { color: #B3261E; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-primary-container\/30 { border-color: rgba(234, 221, 255, 0.3); }
.border-error\/20 { border-color: rgba(179, 38, 30, 0.2); }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.bg-primary-container\/10 { background-color: rgba(234, 221, 255, 0.1); }
.bg-primary-container\/20 { background-color: rgba(234, 221, 255, 0.2); }
.bg-secondary-container\/20 { background-color: rgba(232, 222, 248, 0.2); }
.bg-success\/20 { background-color: rgba(0, 110, 28, 0.2); }
.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }
.bg-warning\/20 { background-color: rgba(220, 188, 67, 0.2); }

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-in {
  animation: fadeIn 0.2s ease-out forwards;
}

/* Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #CAC4D0;
  border-radius: 4px;
}
</style>