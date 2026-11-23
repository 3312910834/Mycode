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

    <main class="max-w-4xl mx-auto px-4 md:px-6 py-8">

      <!-- Header -->
      <header class="mb-8">
        <button @click="goBack" class="inline-flex items-center gap-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-high px-4 py-2 rounded-full transition-all duration-300 font-medium mb-4">
          <span class="material-symbols-outlined">arrow_back</span>
          返回列表
        </button>
        <div class="bg-surface-container-low rounded-3xl p-8 border border-surface-container-high">
          <div class="inline-flex items-center justify-center w-14 h-14 bg-primary/20 text-primary rounded-2xl mb-4 shadow-lg shadow-primary/10">
            <span class="material-symbols-outlined text-3xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">lightbulb</span>
          </div>
          <h1 class="text-3xl font-black text-on-surface font-headline mb-2">提交建议</h1>
          <p class="text-on-surface-variant font-medium">为系统改进提供宝贵意见，帮助我们做得更好</p>
        </div>
      </header>

      <!-- Form Card -->
      <div class="bg-surface-container-low rounded-3xl p-6 md:p-10 border border-surface-container-high shadow-sm mb-8">
        <form @submit.prevent="submitForm" class="space-y-8">

          <!-- Content -->
          <div class="space-y-2">
            <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
              建议内容 <span class="text-error">*</span>
            </label>
            <div class="relative group">
              <textarea
                v-model="form.content"
                rows="8"
                placeholder="请输入您的建议或意见，例如功能改进、界面优化、bug反馈等..."
                class="w-full p-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 resize-none"
                :class="{ 'border-error': errors.content }"
                maxlength="1000"
              ></textarea>
              <div class="absolute bottom-3 right-4 text-xs font-medium"
                   :class="form.content.length > 1000 ? 'text-error' : 'text-on-surface-variant'">
                {{ form.content.length }}/1000
              </div>
            </div>
            <div class="flex justify-end">
              <button
                type="button"
                @click="handleAiAssist"
                :disabled="aiAssistLoading || !form.content.trim()"
                class="px-4 py-2 rounded-xl bg-secondary/20 text-secondary text-sm font-bold hover:bg-secondary/30 transition-colors disabled:opacity-60"
              >
                AI润色建议内容
              </button>
            </div>
            <p v-if="errors.content" class="text-xs text-error font-medium ml-1">{{ errors.content }}</p>
          </div>

          <!-- Type Selection (Segmented Control) -->
          <div class="space-y-2">
            <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
              建议分类 <span class="text-error">*</span>
            </label>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
              <button
                v-for="opt in typeOptions"
                :key="opt.value"
                type="button"
                @click="form.type = opt.value; errors.type = ''"
                class="relative px-4 py-3 rounded-xl border-2 font-bold text-sm transition-all duration-300 flex flex-col items-center justify-center gap-1"
                :class="form.type === opt.value
                  ? 'bg-primary/10 border-primary text-primary shadow-md shadow-primary/10'
                  : 'bg-surface-container-high border-transparent text-on-surface-variant hover:bg-surface-container hover:text-on-surface'"
              >
                <span class="material-symbols-outlined text-xl" :style="materialIconFill(form.type === opt.value)">
                  {{ opt.icon }}
                </span>
                {{ opt.label }}
              </button>
            </div>
            <p v-if="errors.type" class="text-xs text-error font-medium ml-1">{{ errors.type }}</p>
          </div>

          <!-- Contact Info -->
          <div class="space-y-2">
            <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
              联系方式 <span class="text-on-surface-variant/50 font-normal">(可选)</span>
            </label>
            <div class="relative group">
              <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                <span class="material-symbols-outlined">mail</span>
              </span>
              <input
                v-model="form.contact"
                type="text"
                placeholder="如需回复，请留下联系方式（电话/邮箱）"
                class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
              />
            </div>
          </div>

          <!-- Actions -->
          <div class="pt-6 border-t border-surface-container-high flex flex-col sm:flex-row gap-4">
            <button
              type="submit"
              :disabled="loading"
              class="flex-1 py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 flex items-center justify-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed disabled:hover:translate-y-0"
            >
              <span v-if="loading" class="material-symbols-outlined animate-spin">progress_activity</span>
              <span v-else class="material-symbols-outlined">send</span>
              {{ loading ? '提交中...' : '提交建议' }}
            </button>

            <button
              type="button"
              @click="resetForm"
              :disabled="loading"
              class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors disabled:opacity-50"
            >
              重置
            </button>

            <button
              type="button"
              @click="goBack"
              :disabled="loading"
              class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors disabled:opacity-50"
            >
              返回
            </button>
          </div>
        </form>
      </div>

      <!-- Info Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="(info, index) in infoList" :key="index" class="bg-surface-container-low p-5 rounded-2xl border border-surface-container-high flex gap-4 items-start hover:bg-surface-container-high transition-colors">
          <div class="w-10 h-10 rounded-full bg-success/10 text-success flex items-center justify-center flex-shrink-0">
            <span class="material-symbols-outlined">check_circle</span>
          </div>
          <div>
            <h4 class="font-bold text-on-surface mb-1">{{ info.title }}</h4>
            <p class="text-sm text-on-surface-variant leading-relaxed">{{ info.desc }}</p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { createSuggestion } from '@/api/suggestion'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const materialIconFill = (active) => ({
  fontVariationSettings: active ? "'FILL' 1" : "'FILL' 0"
})

// Simple Toast System
const toast = reactive({ show: false, message: '', type: 'info' })
const showToast = (message, type = 'info') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

const loading = ref(false)
const aiAssistLoading = ref(false)

const form = reactive({
  content: '',
  type: 'function',
  contact: ''
})

const errors = reactive({
  content: '',
  type: ''
})

const typeOptions = [
  { label: '功能建议', value: 'function', icon: 'build_circle' },
  { label: '界面优化', value: 'ui', icon: 'palette' },
  { label: 'Bug反馈', value: 'bug', icon: 'bug_report' },
  { label: '其他', value: 'other', icon: 'more_horiz' }
]

const infoList = [
  { title: '建议明确', desc: '请尽量详细描述您的建议，包含具体场景和期望效果' },
  { title: '问题重现', desc: '如果是Bug反馈，请说明操作步骤和重现条件' },
  { title: '联系方式', desc: '留下联系方式以便我们及时与您沟通反馈' },
  { title: '处理时间', desc: '我们会在3个工作日内处理您的建议并给予回复' }
]

const validateForm = () => {
  let isValid = true
  errors.content = ''
  errors.type = ''

  if (!form.content.trim()) {
    errors.content = '请输入建议内容'
    isValid = false
  } else if (form.content.length < 10) {
    errors.content = '建议内容至少10个字符'
    isValid = false
  } else if (form.content.length > 1000) {
    errors.content = '建议内容不能超过1000个字符'
    isValid = false
  }

  if (!form.type) {
    errors.type = '请选择建议分类'
    isValid = false
  }

  return isValid
}

const submitForm = async () => {
  if (!validateForm()) {
    showToast('请填写完整且正确的信息', 'warning')
    const firstError = document.querySelector('.border-error')
    if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' })
    return
  }

  loading.value = true
  try {
    const submitData = {
      content: form.content,
      type: form.type
    }

    if (form.contact) {
      submitData.contact = form.contact
    }

    await createSuggestion(submitData)
    showToast('建议提交成功！感谢您的宝贵意见', 'success')

    resetForm()

    setTimeout(() => {
      router.push('/user/suggestions')
    }, 1500)
  } catch (error) {
    console.error('提交建议失败:', error)
    showToast(error.response?.data?.message || '提交失败，请重试', 'error')
  } finally {
    loading.value = false
  }
}

const handleAiAssist = async () => {
  const raw = form.content?.trim() || ''
  if (!raw) return

  aiAssistLoading.value = true
  try {
    const type = form.type
    let improved = ''

    if (type === 'function') {
      improved = `功能建议：${raw}\n\n使用场景：请补充你希望在什么情况下使用该功能。\n期望效果：请补充更具体的收益或结果。\n补充说明：如有约束/优先级也可以写在这里。`
    } else if (type === 'ui') {
      improved = `界面优化：${raw}\n\n问题点：当前界面哪里让你感到困扰？\n建议方案：你希望如何调整（布局/文案/交互）？\n补充说明：可以附上截图或期望风格。`
    } else if (type === 'bug') {
      improved = `Bug反馈：${raw}\n\n重现步骤：1）…\n2）…\n预期结果：…\n实际结果：…\n补充信息：设备/浏览器版本、发生时间等。`
    } else {
      improved = `其他建议：${raw}\n\n背景说明：…\n你期望的改进方向：…\n补充信息：…`
    }

    // 兼容用户已输入的内容，避免超长直接截断
    form.content = improved.slice(0, 1000)
    errors.content = ''
    showToast('AI润色完成，可直接提交', 'success')
  } finally {
    aiAssistLoading.value = false
  }
}

const resetForm = () => {
  form.content = ''
  form.type = 'function'
  form.contact = ''
  errors.content = ''
  errors.type = ''
}

const goBack = () => {
  router.push('/user/suggestions')
}
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-primary { background-color: #6750A4; }
.bg-success { background-color: #006E1C; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-on-success { color: #FFFFFF; }
.text-on-error { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-success { color: #006E1C; }
.text-error { color: #B3261E; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-error { border-color: #B3261E !important; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.shadow-primary\/10 { --tw-shadow-color: rgba(103, 80, 164, 0.1); }

.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
.bg-primary\/20 { background-color: rgba(103, 80, 164, 0.2); }
.bg-success\/10 { background-color: rgba(0, 110, 28, 0.1); }

/* Animations */
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
.animate-in { animation: fadeIn 0.3s ease-out forwards; }
</style>