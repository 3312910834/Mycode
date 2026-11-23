<template>
  <div class="bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Custom Toast Notification -->
    <div v-if="toast.show" class="fixed top-6 left-1/2 -translate-x-1/2 z-[70] animate-in fade-in slide-in-from-top-4 duration-300">
      <div class="px-6 py-3 rounded-full shadow-lg flex items-center gap-3 font-medium text-sm"
           :class="{
             'bg-error text-on-error': toast.type === 'error',
             'bg-success text-on-success': toast.type === 'success',
             'bg-warning text-on-surface': toast.type === 'warning',
             'bg-secondary-container text-secondary': toast.type === 'info'
           }">
        <span class="material-symbols-outlined text-base">
          {{ toast.type === 'error' ? 'error' : toast.type === 'success' ? 'check_circle' : 'info' }}
        </span>
        {{ toast.message }}
      </div>
    </div>

    <main class="w-full px-0 py-2">

      <!-- Header -->
      <header class="mb-8">
        <h1 class="text-3xl font-black text-on-surface font-headline mb-2">修改密码</h1>
        <p class="text-on-surface-variant font-medium">定期修改密码，保护账号安全</p>
      </header>

      <!-- Form Card -->
      <div class="bg-surface-container-low rounded-3xl p-6 md:p-10 border border-surface-container-high shadow-sm mb-8">
        <form @submit.prevent="submitForm" class="max-w-xl mx-auto space-y-8">

          <!-- Current Password -->
          <div class="space-y-2">
            <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">当前密码</label>
            <div class="relative group">
              <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                <span class="material-symbols-outlined">lock</span>
              </span>
              <input
                v-model="form.old_password"
                :type="showOldPassword ? 'text' : 'password'"
                placeholder="请输入当前密码"
                class="w-full pl-12 pr-12 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                :class="{ 'border-error': errors.old_password }"
              />
              <button
                type="button"
                @click="showOldPassword = !showOldPassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant hover:text-primary transition-colors p-1"
              >
                <span class="material-symbols-outlined">{{ showOldPassword ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
            <p v-if="errors.old_password" class="text-xs text-error font-medium ml-1">{{ errors.old_password }}</p>
          </div>

          <!-- New Password -->
          <div class="space-y-2">
            <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">新密码</label>
            <div class="relative group">
              <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                <span class="material-symbols-outlined">key</span>
              </span>
              <input
                v-model="form.new_password"
                :type="showNewPassword ? 'text' : 'password'"
                placeholder="请输入新密码（至少6位）"
                class="w-full pl-12 pr-12 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                :class="{ 'border-error': errors.new_password }"
              />
              <button
                type="button"
                @click="showNewPassword = !showNewPassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant hover:text-primary transition-colors p-1"
              >
                <span class="material-symbols-outlined">{{ showNewPassword ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>

            <!-- Strength Bar -->
            <div v-if="form.new_password" class="mt-3 space-y-1">
              <div class="flex justify-between text-xs font-bold mb-1">
                <span :class="strengthColorClass">{{ strengthText }}</span>
                <span class="text-on-surface-variant">{{ strengthPercentage }}%</span>
              </div>
              <div class="h-2 w-full bg-surface-container-highest rounded-full overflow-hidden">
                <div
                  class="h-full rounded-full transition-all duration-500 ease-out"
                  :class="strengthColorClass.replace('text-', 'bg-')"
                  :style="{ width: strengthPercentage + '%' }"
                ></div>
              </div>
            </div>
            <p v-if="errors.new_password" class="text-xs text-error font-medium ml-1">{{ errors.new_password }}</p>
          </div>

          <!-- Confirm Password -->
          <div class="space-y-2">
            <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">确认新密码</label>
            <div class="relative group">
              <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                <span class="material-symbols-outlined">key</span>
              </span>
              <input
                v-model="form.confirm_password"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="请再次输入新密码"
                class="w-full pl-12 pr-12 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                :class="{ 'border-error': errors.confirm_password }"
              />
              <button
                type="button"
                @click="showConfirmPassword = !showConfirmPassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant hover:text-primary transition-colors p-1"
              >
                <span class="material-symbols-outlined">{{ showConfirmPassword ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
            <p v-if="errors.confirm_password" class="text-xs text-error font-medium ml-1">{{ errors.confirm_password }}</p>
          </div>

          <!-- Requirements List -->
          <div class="bg-surface-container-high/50 rounded-2xl p-5 border border-surface-container-high">
            <h4 class="text-sm font-bold text-on-surface mb-4 flex items-center gap-2">
              <span class="material-symbols-outlined text-primary text-base">security</span>
              密码安全要求
            </h4>
            <ul class="space-y-2">
              <li v-for="(req, idx) in requirements" :key="idx"
                  class="flex items-center gap-3 text-sm transition-colors duration-300"
                  :class="req.valid ? 'text-success' : 'text-on-surface-variant'">
                <span class="material-symbols-outlined text-base flex-shrink-0">
                  {{ req.valid ? 'check_circle' : 'radio_button_unchecked' }}
                </span>
                {{ req.text }}
              </li>
            </ul>
          </div>

          <!-- Actions -->
          <div class="pt-4 flex flex-col sm:flex-row gap-4">
            <button
              type="submit"
              :disabled="loading || !isFormValid"
              class="flex-1 py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
            >
              <span v-if="loading" class="material-symbols-outlined animate-spin">progress_activity</span>
              <span v-else class="material-symbols-outlined">save</span>
              {{ loading ? '修改中...' : '确认修改' }}
            </button>

            <button
              type="button"
              @click="resetForm"
              :disabled="loading"
              class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors disabled:opacity-50"
            >
              重置
            </button>
          </div>
        </form>
      </div>

      <!-- Security Tips -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="(tip, index) in tipsList" :key="index" class="bg-surface-container-low p-5 rounded-2xl border border-surface-container-high flex gap-4 items-start hover:bg-surface-container-high transition-colors">
          <div class="w-10 h-10 rounded-full bg-primary/10 text-primary flex items-center justify-center flex-shrink-0">
            <span class="material-symbols-outlined">{{ tip.icon }}</span>
          </div>
          <div>
            <h4 class="font-bold text-on-surface mb-1">{{ tip.title }}</h4>
            <p class="text-sm text-on-surface-variant leading-relaxed">{{ tip.desc }}</p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { updatePassword } from '@/api/user'
import { useUserStore } from '@/store/user'
import { computed, reactive, ref } from 'vue'

// Simple Toast System
const toast = reactive({ show: false, message: '', type: 'info' })
const showToast = (message, type = 'info') => {
  toast.message = message
  toast.type = type
  toast.show = true
  setTimeout(() => { toast.show = false }, 3000)
}

const loading = ref(false)
const userStore = useUserStore()
const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const form = reactive({
  old_password: '',
  new_password: '',
  confirm_password: ''
})

const errors = reactive({
  old_password: '',
  new_password: '',
  confirm_password: ''
})

// Requirements Logic
const passwordHasLength = computed(() => form.new_password.length >= 6)
const passwordHasUppercase = computed(() => /[A-Z]/.test(form.new_password))
const passwordHasLowercase = computed(() => /[a-z]/.test(form.new_password))
const passwordHasNumber = computed(() => /[0-9]/.test(form.new_password))
const passwordHasSpecial = computed(() => /[^A-Za-z0-9]/.test(form.new_password))

const requirements = computed(() => [
  { text: '长度至少6位字符', valid: passwordHasLength.value },
  { text: '包含大写字母', valid: passwordHasUppercase.value },
  { text: '包含小写字母', valid: passwordHasLowercase.value },
  { text: '包含数字', valid: passwordHasNumber.value },
  { text: '包含特殊字符（可选）', valid: passwordHasSpecial.value }
])

// Strength Calculation
const strengthScore = computed(() => {
  let score = 0
  if (passwordHasLength.value) score += 20
  if (passwordHasUppercase.value) score += 20
  if (passwordHasLowercase.value) score += 20
  if (passwordHasNumber.value) score += 20
  if (passwordHasSpecial.value) score += 20
  return Math.min(score, 100)
})

const strengthPercentage = computed(() => strengthScore.value)
const strengthText = computed(() => {
  if (!form.new_password) return '请输入密码'
  if (strengthScore.value < 40) return '弱'
  if (strengthScore.value < 70) return '中'
  return '强'
})

const strengthColorClass = computed(() => {
  if (strengthScore.value < 40) return 'text-error bg-error'
  if (strengthScore.value < 70) return 'text-warning bg-warning'
  return 'text-success bg-success'
})

// Validation Logic
const validateForm = () => {
  let isValid = true
  errors.old_password = ''
  errors.new_password = ''
  errors.confirm_password = ''

  if (!form.old_password) {
    errors.old_password = '请输入当前密码'
    isValid = false
  } else if (form.old_password.length < 6) {
    errors.old_password = '密码长度至少6位'
    isValid = false
  }

  if (!form.new_password) {
    errors.new_password = '请输入新密码'
    isValid = false
  } else if (form.new_password.length < 6) {
    errors.new_password = '密码长度至少6位'
    isValid = false
  } else if (form.new_password === form.old_password) {
    errors.new_password = '新密码不能与当前密码相同'
    isValid = false
  }

  if (!form.confirm_password) {
    errors.confirm_password = '请确认新密码'
    isValid = false
  } else if (form.confirm_password !== form.new_password) {
    errors.confirm_password = '两次输入的密码不一致'
    isValid = false
  }

  return isValid
}

const isFormValid = computed(() => {
  return (
    form.old_password &&
    form.new_password &&
    form.confirm_password &&
    form.new_password === form.confirm_password &&
    form.new_password.length >= 6 &&
    form.new_password !== form.old_password
  )
})

const submitForm = async () => {
  if (!validateForm()) {
    showToast('请填写完整且正确的信息', 'warning')
    const firstError = document.querySelector('.border-error')
    if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' })
    return
  }

  loading.value = true
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      showToast('用户信息丢失，请重新登录', 'error')
      return
    }

    const response = await updatePassword({
      userId,
      old_password: form.old_password,
      new_password: form.new_password
    })

    if (response?.code === 200 || response?.success) {
      showToast('密码修改成功', 'success')
      resetForm()
    } else {
      showToast(response?.message || response?.error || '修改失败', 'error')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    if (error?.response?.status === 404) {
      showToast('当前后端未提供修改密码接口，请联系管理员开启该功能', 'error')
    } else {
      showToast(error.response?.data?.message || error.message || '修改失败', 'error')
    }
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.old_password = ''
  form.new_password = ''
  form.confirm_password = ''
  errors.old_password = ''
  errors.new_password = ''
  errors.confirm_password = ''
}

const tipsList = [
  { icon: 'schedule', title: '定期更换密码', desc: '建议每3-6个月修改一次密码，提高账号安全性' },
  { icon: 'shield', title: '使用强密码', desc: '使用包含大小写字母、数字和特殊字符的组合密码' },
  { icon: 'person_off', title: '不要泄露密码', desc: '不要将密码告诉他人，包括系统管理员' },
  { icon: 'no_accounts', title: '避免重复使用', desc: '不要在多个网站使用相同的密码' }
]
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-primary { background-color: #6750A4; }
.bg-success { background-color: #006E1C; }
.bg-warning { background-color: #DCBC43; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-on-success { color: #FFFFFF; }
.text-on-error { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-success { color: #006E1C; }
.text-warning { color: #63500A; } /* Darker for text readability */
.text-error { color: #B3261E; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-error { border-color: #B3261E !important; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }

.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
.bg-surface-container-high\/50 { background-color: rgba(236, 230, 240, 0.5); }

/* Animations */
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
.animate-in { animation: fadeIn 0.3s ease-out forwards; }
</style>