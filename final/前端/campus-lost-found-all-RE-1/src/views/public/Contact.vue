<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Hero Header -->
    <header class="relative bg-surface-container-lowest border-b border-surface-container-high overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-secondary-container/20 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 pointer-events-none"></div>
      <div class="absolute bottom-0 left-0 w-48 h-48 bg-primary-container/20 rounded-full blur-3xl translate-y-1/2 -translate-x-1/2 pointer-events-none"></div>

      <div class="relative max-w-6xl mx-auto px-6 py-16 md:py-20 text-center">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-secondary-container text-secondary rounded-2xl mb-6 shadow-lg shadow-secondary/10">
          <span class="material-symbols-outlined text-4xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">mail</span>
        </div>
        <h1 class="text-4xl md:text-5xl font-black text-on-surface tracking-tight font-headline mb-4">联系我们</h1>
        <p class="text-lg text-on-surface-variant font-medium max-w-2xl mx-auto">您的反馈是我们进步的动力，请随时与我们沟通</p>
      </div>
    </header>

    <main class="max-w-6xl mx-auto px-6 py-12">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

        <!-- Left Column: Info & Stats -->
        <div class="lg:col-span-1 space-y-6">

          <!-- Contact Info Card -->
          <div class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high shadow-sm">
            <h2 class="text-xl font-bold text-on-surface font-headline mb-6 flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">info</span>
              联系方式
            </h2>

            <div class="space-y-6">
              <!-- Address -->
              <div class="flex gap-4">
                <div class="w-10 h-10 rounded-full bg-primary-container/50 text-primary flex items-center justify-center shrink-0">
                  <span class="material-symbols-outlined">location_on</span>
                </div>
                <div>
                  <h3 class="font-bold text-on-surface text-sm uppercase tracking-wide mb-1">办公地址</h3>
                  <p class="text-on-surface-variant text-sm leading-relaxed">北京市海淀区校园路 123 号</p>
                </div>
              </div>

              <!-- Phone -->
              <div class="flex gap-4">
                <div class="w-10 h-10 rounded-full bg-primary-container/50 text-primary flex items-center justify-center shrink-0">
                  <span class="material-symbols-outlined">phone</span>
                </div>
                <div>
                  <h3 class="font-bold text-on-surface text-sm uppercase tracking-wide mb-1">联系电话</h3>
                  <p class="text-on-surface-variant text-sm font-medium">010-12345678</p>
                  <p class="text-on-surface-variant/60 text-xs mt-0.5">工作日 9:00-18:00</p>
                </div>
              </div>

              <!-- Email -->
              <div class="flex gap-4">
                <div class="w-10 h-10 rounded-full bg-primary-container/50 text-primary flex items-center justify-center shrink-0">
                  <span class="material-symbols-outlined">email</span>
                </div>
                <div>
                  <h3 class="font-bold text-on-surface text-sm uppercase tracking-wide mb-1">电子邮箱</h3>
                  <p class="text-on-surface-variant text-sm">contact@campus.com</p>
                </div>
              </div>

              <!-- Hours -->
              <div class="flex gap-4">
                <div class="w-10 h-10 rounded-full bg-primary-container/50 text-primary flex items-center justify-center shrink-0">
                  <span class="material-symbols-outlined">schedule</span>
                </div>
                <div>
                  <h3 class="font-bold text-on-surface text-sm uppercase tracking-wide mb-1">服务时间</h3>
                  <p class="text-on-surface-variant text-sm">周一至周五 9:00-18:00</p>
                  <p class="text-on-surface-variant text-sm">周六 10:00-16:00</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Stats Card -->
          <div class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high shadow-sm">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-xl font-bold text-on-surface font-headline flex items-center gap-2">
                <span class="material-symbols-outlined text-tertiary">insights</span>
                反馈统计
              </h2>
              <button
                @click="loadStats"
                :disabled="loading"
                class="text-xs font-bold text-primary bg-primary-container/50 hover:bg-primary-container px-3 py-1.5 rounded-full transition-colors disabled:opacity-50"
              >
                {{ stats ? '刷新' : '加载' }}
              </button>
            </div>

            <div v-if="stats" class="grid grid-cols-2 gap-3">
              <div class="bg-surface-container-highest rounded-2xl p-4 text-center">
                <div class="text-2xl font-black text-primary mb-1">{{ stats.total }}</div>
                <div class="text-xs font-medium text-on-surface-variant uppercase">总反馈</div>
              </div>
              <div class="bg-surface-container-highest rounded-2xl p-4 text-center">
                <div class="text-2xl font-black text-warning mb-1">{{ stats.pending }}</div>
                <div class="text-xs font-medium text-on-surface-variant uppercase">待处理</div>
              </div>
              <div class="bg-surface-container-highest rounded-2xl p-4 text-center">
                <div class="text-2xl font-black text-success mb-1">{{ stats.processed }}</div>
                <div class="text-xs font-medium text-on-surface-variant uppercase">已处理</div>
              </div>
              <div class="bg-surface-container-highest rounded-2xl p-4 text-center">
                <div class="text-2xl font-black text-secondary mb-1">{{ stats.read }}</div>
                <div class="text-xs font-medium text-on-surface-variant uppercase">已查看</div>
              </div>
            </div>

            <div v-else class="text-center py-8">
              <button @click="loadStats" class="text-primary font-bold text-sm hover:underline">点击加载统计数据</button>
            </div>
          </div>

          <!-- User Suggestions Preview -->
          <div v-if="userSuggestions.length > 0" class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high shadow-sm">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-lg font-bold text-on-surface font-headline">我的建议</h2>
              <button @click="gotoUserSuggestions" class="text-xs font-bold text-primary hover:underline">查看全部</button>
            </div>

            <div class="space-y-3">
              <div v-for="item in userSuggestions.slice(0, 3)" :key="item.id" class="group bg-surface-container-highest rounded-xl p-3 hover:bg-surface-container-high transition-colors cursor-pointer" @click="showReply(item)">
                <div class="flex justify-between items-start mb-2">
                  <p class="text-sm font-medium text-on-surface line-clamp-1 pr-2">{{ item.content }}</p>
                  <span class="material-symbols-outlined text-xs text-outline-variant">chevron_right</span>
                </div>
                <div class="flex items-center justify-between">
                  <span
                    class="text-[10px] font-bold px-2 py-0.5 rounded-full uppercase"
                    :class="{
                      'bg-warning/20 text-warning': item.status === 'pending',
                      'bg-secondary/20 text-secondary': item.status === 'read',
                      'bg-success/20 text-success': item.status === 'processed'
                    }"
                  >
                    {{ getStatusText(item.status) }}
                  </span>
                  <span class="text-[10px] text-on-surface-variant">{{ formatTime(item.createTime) }}</span>
                </div>
                <div v-if="item.reply" class="mt-2 text-xs text-primary font-medium flex items-center gap-1">
                  <span class="material-symbols-outlined text-sm">reply</span>
                  有回复
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- Right Column: Feedback Form -->
        <div class="lg:col-span-2">
          <div class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm h-full">

            <div class="flex justify-between items-center mb-6">
              <h2 class="text-2xl font-bold text-on-surface font-headline flex items-center gap-2">
                <span class="material-symbols-outlined text-primary">edit_note</span>
                意见反馈
              </h2>
              <button v-if="isLoggedIn" @click="loadUserSuggestions" class="text-sm font-bold text-outline hover:text-on-surface transition-colors flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">history</span>
                我的记录
              </button>
            </div>

            <!-- Info Collapse (Simplified) -->
            <div class="mb-8 bg-primary-container/20 rounded-2xl p-4 border border-primary-container/30">
              <div class="flex items-start gap-3">
                <span class="material-symbols-outlined text-primary mt-0.5">lightbulb</span>
                <div class="text-sm text-on-surface-variant space-y-1">
                  <p>1. 请详细描述您遇到的问题或建议，有助于我们更快解决。</p>
                  <p>2. 我们将在 1-3 个工作日内处理您的反馈。</p>
                  <p>3. 请保持联系方式畅通，以便我们与您沟通。</p>
                </div>
              </div>
            </div>

            <!-- Form -->
            <form @submit.prevent="submitFeedback" class="space-y-6">
              <!-- Name -->
              <div class="group relative">
                <label class="block text-xs font-bold text-on-surface-variant uppercase tracking-wide mb-2 ml-1">姓名</label>
                <div class="relative">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                    <span class="material-symbols-outlined">person</span>
                  </span>
                  <input
                    v-model="form.name"
                    type="text"
                    placeholder="请输入您的姓名"
                    :disabled="loading"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 disabled:opacity-50"
                    :class="{ 'border-error': errors.name }"
                    @blur="validateField('name')"
                  />
                </div>
                <p v-if="errors.name" class="absolute -bottom-5 left-1 text-xs text-error font-bold flex items-center gap-1">
                  <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.name }}
                </p>
              </div>

              <!-- Contact -->
              <div class="group relative">
                <label class="block text-xs font-bold text-on-surface-variant uppercase tracking-wide mb-2 ml-1">联系方式</label>
                <div class="relative">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                    <span class="material-symbols-outlined">contact_phone</span>
                  </span>
                  <input
                    v-model="form.contact"
                    type="text"
                    placeholder="手机号或邮箱"
                    :disabled="loading"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 disabled:opacity-50"
                    :class="{ 'border-error': errors.contact }"
                    @blur="validateField('contact')"
                  />
                </div>
                <p v-if="errors.contact" class="absolute -bottom-5 left-1 text-xs text-error font-bold flex items-center gap-1">
                  <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.contact }}
                </p>
              </div>

              <!-- Content -->
              <div class="group relative">
                <label class="block text-xs font-bold text-on-surface-variant uppercase tracking-wide mb-2 ml-1">反馈内容</label>
                <div class="relative">
                  <span class="absolute left-4 top-4 text-on-surface-variant group-focus-within:text-primary transition-colors">
                    <span class="material-symbols-outlined">comment</span>
                  </span>
                  <textarea
                    v-model="form.content"
                    rows="6"
                    placeholder="请详细描述您的问题或建议..."
                    :disabled="loading"
                    maxlength="500"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 disabled:opacity-50 resize-none"
                    :class="{ 'border-error': errors.content }"
                    @blur="validateField('content')"
                  ></textarea>
                  <div class="absolute bottom-3 right-4 text-xs text-on-surface-variant/60 font-medium">
                    {{ form.content.length }}/500
                  </div>
                </div>
                <p v-if="errors.content" class="absolute -bottom-5 left-1 text-xs text-error font-bold flex items-center gap-1">
                  <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.content }}
                </p>
              </div>
              <div class="flex justify-end mt-3">
                <button
                  type="button"
                  @click="handleAiAssistContent"
                  :disabled="aiAssistLoading || !form.content.trim()"
                  class="px-4 py-2 rounded-xl bg-secondary-container/30 text-secondary text-sm font-bold hover:bg-secondary-container/50 transition-colors disabled:opacity-60"
                >
                  AI润色反馈内容
                </button>
              </div>

              <!-- Actions -->
              <div class="flex flex-wrap gap-4 pt-4">
                <button
                  type="submit"
                  :disabled="loading || !form.content"
                  class="flex-1 min-w-[140px] py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.98] transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex items-center justify-center gap-2"
                >
                  <span v-if="loading" class="material-symbols-outlined animate-spin">progress_activity</span>
                  <span>{{ loading ? '提交中...' : '提交反馈' }}</span>
                </button>

                <button
                  type="button"
                  @click="resetForm"
                  :disabled="loading"
                  class="px-8 py-4 bg-surface-container-high text-on-surface-variant rounded-2xl font-bold hover:bg-surface-container-highest transition-colors disabled:opacity-50"
                >
                  重置
                </button>

                <button
                  type="button"
                  @click="testApiConnection"
                  :disabled="loading"
                  class="px-6 py-4 bg-secondary-container/30 text-secondary rounded-2xl font-bold hover:bg-secondary-container/50 transition-colors disabled:opacity-50 flex items-center gap-2"
                >
                  <span class="material-symbols-outlined text-sm">wifi_find</span>
                  测试连接
                </button>
              </div>
            </form>

            <!-- Result Message -->
            <div v-if="submitResult" class="mt-6 p-4 rounded-2xl flex items-start gap-3 animate-in fade-in slide-in-from-top-2" :class="submitResult.type === 'success' ? 'bg-success/10 text-success' : 'bg-error/10 text-error'">
              <span class="material-symbols-outlined text-xl shrink-0">{{ submitResult.type === 'success' ? 'check_circle' : 'error' }}</span>
              <div>
                <h4 class="font-bold text-sm mb-1">{{ submitResult.title }}</h4>
                <p class="text-sm opacity-90">{{ submitResult.message }}</p>
              </div>
              <button @click="submitResult = null" class="ml-auto opacity-60 hover:opacity-100">
                <span class="material-symbols-outlined">close</span>
              </button>
            </div>

          </div>
        </div>
      </div>
    </main>

    <!-- Custom Dialog: API Test Result -->
    <div v-if="showApiTestDialog" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-on-surface/60 backdrop-blur-sm" @click="showApiTestDialog = false"></div>
      <div class="relative bg-surface-container-low w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh] animate-in fade-in zoom-in-95 duration-200">
        <div class="p-6 border-b border-surface-container-high flex justify-between items-center">
          <h3 class="text-xl font-bold text-on-surface font-headline">API 测试结果</h3>
          <button @click="showApiTestDialog = false" class="p-2 hover:bg-surface-container-high rounded-full transition-colors">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <div class="p-6 overflow-y-auto custom-scrollbar">
          <div v-if="apiTestResult" class="space-y-4">
            <div class="flex items-center justify-between p-3 bg-surface-container-highest rounded-xl">
              <span class="text-sm font-medium text-on-surface-variant">方法</span>
              <span class="text-sm font-bold text-on-surface">{{ apiTestResult.method }}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-surface-container-highest rounded-xl">
              <span class="text-sm font-medium text-on-surface-variant">状态</span>
              <span class="px-3 py-1 rounded-full text-xs font-bold" :class="apiTestResult.status === '成功' ? 'bg-success/20 text-success' : 'bg-error/20 text-error'">
                {{ apiTestResult.status }}
              </span>
            </div>

            <div v-if="apiTestResult.data" class="bg-surface-container-high rounded-xl p-4 overflow-x-auto">
              <div class="text-xs font-bold text-on-surface-variant uppercase mb-2">返回数据</div>
              <pre class="text-xs font-mono text-on-surface whitespace-pre-wrap">{{ JSON.stringify(apiTestResult.data, null, 2) }}</pre>
            </div>

            <div v-if="apiTestResult.error" class="bg-error/10 border border-error/20 rounded-xl p-4">
              <div class="text-xs font-bold text-error uppercase mb-2">错误信息</div>
              <pre class="text-xs font-mono text-error whitespace-pre-wrap">{{ apiTestResult.error }}</pre>
            </div>
          </div>
          <div v-else class="py-10 text-center">
            <span class="material-symbols-outlined text-4xl text-outline-variant animate-pulse">sync</span>
            <p class="text-on-surface-variant mt-4 text-sm">正在测试连接...</p>
          </div>
        </div>

        <div class="p-4 border-t border-surface-container-high bg-surface-container-highest/30 flex justify-end gap-3">
          <button @click="showApiTestDialog = false" class="px-6 py-2.5 rounded-full font-bold text-sm text-on-surface-variant hover:bg-surface-container-high transition-colors">关闭</button>
          <button @click="testApiConnection" class="px-6 py-2.5 bg-primary text-on-primary rounded-full font-bold text-sm hover:bg-primary-dim transition-colors shadow-lg shadow-primary/20">重新测试</button>
        </div>
      </div>
    </div>

    <!-- Custom Dialog: Reply Detail -->
    <div v-if="showReplyDialog && currentSuggestion" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-on-surface/60 backdrop-blur-sm" @click="showReplyDialog = false"></div>
      <div class="relative bg-surface-container-low w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh] animate-in fade-in zoom-in-95 duration-200">
        <div class="p-6 border-b border-surface-container-high">
          <h3 class="text-xl font-bold text-on-surface font-headline">回复详情</h3>
        </div>

        <div class="p-6 overflow-y-auto custom-scrollbar space-y-6">
          <!-- Original -->
          <div class="bg-surface-container-highest rounded-2xl p-4">
            <div class="flex items-center gap-2 mb-2 text-xs font-bold text-on-surface-variant uppercase">
              <span class="material-symbols-outlined text-sm">person</span>
              您的反馈
            </div>
            <p class="text-on-surface text-sm leading-relaxed whitespace-pre-wrap">{{ currentSuggestion.content }}</p>
          </div>

          <!-- Reply -->
          <div class="bg-primary-container/20 border border-primary-container/30 rounded-2xl p-4">
            <div class="flex items-center gap-2 mb-2 text-xs font-bold text-primary uppercase">
              <span class="material-symbols-outlined text-sm">support_agent</span>
              管理员回复
            </div>
            <p class="text-on-surface text-sm leading-relaxed whitespace-pre-wrap">{{ currentSuggestion.reply || '暂无回复内容' }}</p>
          </div>

          <!-- Time -->
          <div class="text-right text-xs text-on-surface-variant flex items-center justify-end gap-1">
            <span class="material-symbols-outlined text-sm">schedule</span>
            {{ formatTime(currentSuggestion.updateTime) }}
          </div>
        </div>

        <div class="p-4 border-t border-surface-container-high bg-surface-container-highest/30 flex justify-end">
          <button @click="showReplyDialog = false" class="px-6 py-2.5 bg-surface-container-high text-on-surface rounded-full font-bold text-sm hover:bg-surface-container hover:text-on-surface transition-colors">关闭</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 仅导入 API，不再导入图标
import {
  submitSuggestion,
  testSuggestionApi,
  getUserSuggestions,
  getSuggestionStats
} from '@/api/suggestion'
import { getUserInfo, isSessionComplete } from '@/utils/auth'

const router = useRouter()
const loading = ref(false)
const showApiTestDialog = ref(false)
const showReplyDialog = ref(false)
const apiTestResult = ref(null)
const currentSuggestion = ref(null)
const stats = ref(null)
const userSuggestions = ref([])
const submitResult = ref(null)
const aiAssistLoading = ref(false)

const errors = reactive({
  name: '',
  contact: '',
  content: ''
})

const isLoggedIn = computed(() => isSessionComplete())

const form = reactive({
  name: '',
  contact: '',
  content: ''
})

onMounted(() => {
  if (isLoggedIn.value) {
    loadUserInfo()
    loadUserSuggestions()
  }
  loadStats()
})

const loadUserInfo = () => {
  const user = getUserInfo()
  if (!user) return
  const realName = user.real_name || user.realName
  if (!form.name && realName) form.name = realName
  if (!form.name && user.username) form.name = user.username
  if (!form.contact && user.email) form.contact = user.email
  if (!form.contact && user.phone) form.contact = user.phone
}

const validateField = (field) => {
  let error = ''
  const value = form[field]

  if (field === 'name') {
    if (!value) error = '请输入姓名'
    else if (value.length < 2 || value.length > 20) error = '姓名长度在 2 到 20 个字符'
  } else if (field === 'contact') {
    if (!value) error = '请输入联系方式'
    else if (!/^1[3-9]\d{9}$|^\w+@\w+\.\w+$/.test(value)) error = '请输入正确的手机号或邮箱'
  } else if (field === 'content') {
    if (!value) error = '请输入反馈内容'
    else if (value.length < 10) error = '反馈内容至少 10 个字符'
    else if (value.length > 500) error = '反馈内容不能超过 500 个字符'
  }

  errors[field] = error
  return !error
}

const handleAiAssistContent = async () => {
  const raw = form.content?.trim() || ''
  if (!raw) return

  aiAssistLoading.value = true
  try {
    const improved = `反馈内容：${raw}\n\n问题/建议要点：请补充更具体的发生场景（例如：何时何地、涉及哪些功能）。\n期望/改进方向：请补充你希望系统如何处理或优化。\n补充信息：如有截图/链接/复现步骤，可在此补充。`

    form.content = improved.slice(0, 500)
    errors.content = ''
    ElMessage.success('AI润色完成，可直接提交')
  } finally {
    aiAssistLoading.value = false
  }
}

const loadUserSuggestions = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录查看反馈记录')
    return
  }
  try {
    const userId = getUserInfo()?.id
    if (userId == null) {
      userSuggestions.value = []
      return
    }
    const response = await getUserSuggestions(userId)
    if (response.code === 200 && response.data) {
      userSuggestions.value = response.data.suggestions || []
      if (userSuggestions.value.length === 0) ElMessage.info('您还没有提交过反馈')
    } else {
      userSuggestions.value = []
    }
  } catch (error) {
    ElMessage.error('加载建议失败：' + error.message)
    userSuggestions.value = []
  }
}

const loadStats = async () => {
  try {
    const response = await getSuggestionStats()
    if (response.code === 200 && response.data) {
      stats.value = response.data
    } else {
      stats.value = null
    }
  } catch (error) {
    ElMessage.error('加载统计信息失败：' + error.message)
    stats.value = null
  }
}

const submitFeedback = async () => {
  const isNameValid = validateField('name')
  const isContactValid = validateField('contact')
  const isContentValid = validateField('content')

  if (!isNameValid || !isContactValid || !isContentValid) {
    ElMessage.warning('请正确填写表单信息')
    return
  }

  loading.value = true
  submitResult.value = null

  try {
    const userId = getUserInfo()?.id
    if (userId == null) {
      ElMessage.warning('请先登录后再提交反馈')
      router.push('/login')
      loading.value = false
      return
    }
    const response = await submitSuggestion({
      userId,
      name: form.name,
      contact: form.contact,
      content: form.content
    })

    if (response.success || response.suggestionId) {
      const suggestionId = response.suggestionId || response.data?.id
      ElMessage.success(`反馈提交成功！编号：${suggestionId}`)

      submitResult.value = {
        type: 'success',
        title: '提交成功',
        message: `您的反馈已成功提交，编号：${suggestionId}。我们会尽快处理。`
      }

      resetForm()
      loadUserSuggestions()
      loadStats()
    } else {
      throw new Error(response.message || response.error || '提交失败')
    }
  } catch (error) {
    submitResult.value = {
      type: 'error',
      title: '提交失败',
      message: error.message || '请稍后重试'
    }
    ElMessage.error('提交失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.name = ''
  form.contact = ''
  form.content = ''
  errors.name = ''
  errors.contact = ''
  errors.content = ''
  if (isLoggedIn.value) loadUserInfo()
}

const testApiConnection = async () => {
  loading.value = true
  apiTestResult.value = null
  showApiTestDialog.value = true

  try {
    const response = await testSuggestionApi()
    apiTestResult.value = {
      method: 'testSuggestionApi',
      status: '成功',
      data: response
    }
    ElMessage.success('API 连接正常')
  } catch (error) {
    apiTestResult.value = {
      method: 'testSuggestionApi',
      status: '失败',
      error: error.message,
      data: null
    }
    ElMessage.error('API 连接失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  if (!status) return 'default'
  switch (status.toLowerCase()) {
    case 'pending': return 'warning'
    case 'read': return 'info'
    case 'processed': return 'success'
    default: return 'default'
  }
}

const getStatusText = (status) => {
  if (!status) return '未知'
  switch (status.toLowerCase()) {
    case 'pending': return '待处理'
    case 'read': return '已查看'
    case 'processed': return '已处理'
    default: return status
  }
}

const formatTime = (time) => {
  if (!time) return '未知时间'
  if (typeof time === 'string' && time.includes('-')) return time
  try {
    const date = new Date(time)
    if (isNaN(date.getTime())) return time
    return date.toLocaleString('zh-CN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit'
    })
  } catch (e) {
    return time
  }
}

const showReply = (suggestion) => {
  currentSuggestion.value = suggestion
  showReplyDialog.value = true
}

const gotoUserSuggestions = () => {
  if (isLoggedIn.value) {
    router.push('/user/suggestions')
  } else {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
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
.bg-primary-container { background-color: #EADDFF; }
.bg-secondary { background-color: #625B71; }
.bg-secondary-container { background-color: #E8DEF8; }
.bg-tertiary { background-color: #7D5260; }
.bg-warning { background-color: #DCBC43; } /* Approximate */
.bg-success { background-color: #006E1C; } /* Approximate */
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-tertiary { color: #7D5260; }
.text-warning { color: #63500A; } /* Darker for text */
.text-success { color: #006E1C; }
.text-error { color: #B3261E; }
.text-outline { color: #79747E; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-primary-container\/30 { border-color: rgba(234, 221, 255, 0.3); }
.border-error\/20 { border-color: rgba(179, 38, 30, 0.2); }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.bg-primary-container\/50 { background-color: rgba(234, 221, 255, 0.5); }
.bg-primary-container\/20 { background-color: rgba(234, 221, 255, 0.2); }
.bg-secondary-container\/20 { background-color: rgba(232, 222, 248, 0.2); }
.bg-success\/10 { background-color: rgba(0, 110, 28, 0.1); }
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
.slide-in-from-top-2 {
  transform-origin: top;
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