<template>
  <div class="min-h-screen bg-surface flex items-center justify-center p-4 relative overflow-hidden font-body">

    <!-- Background Decorative Elements -->
    <div class="absolute top-[-10%] left-[-10%] w-[500px] h-[500px] bg-primary-container/30 rounded-full blur-[100px] pointer-events-none animate-pulse"></div>
    <div class="absolute bottom-[-10%] right-[-10%] w-[600px] h-[600px] bg-secondary-container/20 rounded-full blur-[120px] pointer-events-none"></div>

    <!-- Main Card -->
    <div class="w-full max-w-[440px] bg-surface-container-low backdrop-blur-xl rounded-[2rem] shadow-2xl border border-surface-container-high p-8 md:p-10 relative z-10 transition-all duration-500">

      <!-- Header -->
      <div class="text-center mb-10">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-primary-container text-primary rounded-2xl mb-6 shadow-lg shadow-primary/10">
          <span class="material-symbols-outlined text-4xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">lock</span>
        </div>
        <h2 class="text-3xl font-black text-on-surface tracking-tight font-headline mb-2">欢迎回来</h2>
        <p class="text-on-surface-variant font-medium">登录校园失物招领系统</p>
      </div>

      <!-- Dev Tool: Connection Test (Conditional) -->
      <div v-if="showTest" class="mb-6 flex items-center justify-between bg-surface-container-highest/50 rounded-xl p-3 border border-outline-variant/20">
        <span class="text-xs font-bold text-outline uppercase tracking-wider ml-2">开发者模式</span>
        <button
          @click="testBackendConnection"
          class="flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-bold transition-colors"
          :class="testResult ? (testResult.success ? 'bg-secondary-container text-on-secondary-container' : 'bg-error-container text-on-error-container') : 'bg-surface-container text-on-surface hover:bg-surface-container-high'"
        >
          <span class="material-symbols-outlined text-sm">{{ testResult ? (testResult.success ? 'check_circle' : 'error') : 'wifi_find' }}</span>
          {{ testResult ? (testResult.success ? '连接正常' : '连接失败') : '测试后端' }}
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-6">

        <!-- Username Input -->
        <div class="group relative">
          <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
            <span class="material-symbols-outlined">person</span>
          </div>
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="用户名"
            class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
            :class="{ 'border-error': errors.username }"
            @blur="validateField('username')"
          />
          <p v-if="errors.username" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.username }}
          </p>
        </div>

        <!-- Password Input -->
        <div class="group relative">
          <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
            <span class="material-symbols-outlined">lock</span>
          </div>
          <input
            v-model="loginForm.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="密码"
            class="w-full pl-12 pr-12 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
            :class="{ 'border-error': errors.password }"
            @keyup.enter="handleLogin"
            @blur="validateField('password')"
          />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute inset-y-0 right-4 flex items-center text-on-surface-variant hover:text-primary transition-colors"
          >
            <span class="material-symbols-outlined">{{ showPassword ? 'visibility_off' : 'visibility' }}</span>
          </button>
          <p v-if="errors.password" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
            <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.password }}
          </p>
        </div>

        <!-- Options -->
        <div class="flex items-center justify-between pt-2">
          <label class="flex items-center gap-2 cursor-pointer group">
            <div class="relative flex items-center">
              <input
                v-model="rememberMe"
                type="checkbox"
                class="peer sr-only"
              />
              <div class="w-5 h-5 border-2 border-outline rounded-md peer-checked:bg-primary peer-checked:border-primary transition-all duration-200 flex items-center justify-center">
                <span class="material-symbols-outlined text-white text-sm opacity-0 peer-checked:opacity-100 transition-opacity" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">check</span>
              </div>
            </div>
            <span class="text-sm font-medium text-on-surface-variant group-hover:text-on-surface transition-colors">记住我</span>
          </label>

          <button type="button" @click="$router.push('/forgot-password')"
    class="text-sm font-bold text-primary hover:text-primary-dim transition-colors flex items-center gap-1"
  >
    忘记密码？
  </button>
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          :disabled="loading"
          class="w-full py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.98] transition-all duration-300 disabled:opacity-70 disabled:cursor-not-allowed disabled:transform-none flex items-center justify-center gap-2"
        >
          <span v-if="loading" class="material-symbols-outlined animate-spin">progress_activity</span>
          <span>{{ loading ? '登录中...' : '立即登录' }}</span>
        </button>
      </form>

      <!-- Footer -->
      <div class="mt-10 text-center">
        <p class="text-on-surface-variant font-medium">
          还没有账户？
          <button
            @click="$router.push('/register')"
            class="ml-1 text-primary font-bold hover:underline decoration-2 underline-offset-4 transition-all"
          >
            立即注册
          </button>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const rememberMe = ref(false)
const showTest = ref(true) // 开发环境显示测试按钮
const testResult = ref(null)
const showPassword = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

// 简单的前端验证逻辑
const validateField = (field) => {
  if (field === 'username') {
    if (!loginForm.username.trim()) {
      errors.username = '请输入用户名'
      return false
    }
    errors.username = ''
    return true
  }
  if (field === 'password') {
    if (!loginForm.password) {
      errors.password = '请输入密码'
      return false
    }
    if (loginForm.password.length < 6) {
      errors.password = '密码长度不能少于6位'
      return false
    }
    errors.password = ''
    return true
  }
  return true
}

const validateAll = () => {
  const isUsernameValid = validateField('username')
  const isPasswordValid = validateField('password')
  return isUsernameValid && isPasswordValid
}

// 测试后端连接
const testBackendConnection = async () => {
  try {
    testResult.value = { success: false, message: '测试中...' }
    // 假设 store 中有此方法，若无则需适配
    const result = await userStore.testConnection?.()
    testResult.value = { success: true, message: '后端连接成功' }
    ElMessage.success('后端服务正常')
  } catch (error) {
    testResult.value = {
      success: false,
      message: '后端连接失败'
    }
    ElMessage.error('后端服务异常')
    console.error('后端连接失败详情:', error)
  }
}

// const handleLogin = async () => {
//   if (!validateAll()) {
//     // 震动反馈或提示
//     return
//   }

//   loading.value = true
//   try {
//     await userStore.login(loginForm)
//     ElMessage.success('登录成功')

//     await userStore.getUserInfo()

//     if (userStore.isAdmin) {
//       try {
//         await ElMessageBox.confirm(
//           '检测到当前账号为管理员，请选择进入管理员端或普通用户端。',
//           '选择登录端',
//           {
//             confirmButtonText: '进入管理员端',
//             cancelButtonText: '进入普通用户端',
//             distinguishCancelAndClose: true,
//             type: 'warning'
//           }
//         )
//         userStore.enterAdminMode()
//         router.push('/admin/dashboard')
//       } catch (action) {
//         if (action === 'cancel') {
//           userStore.enterUserMode()
//           router.push('/')
//         }
//       }
//     } else {
//       userStore.enterUserMode()
//       router.push('/')
//     }
//   } catch (error) {
//     console.error('登录失败详情:', error)

//     if (error.message) {
//       try {
//         const errorMatch = error.message.match(/\{.*\}/)
//         if (errorMatch) {
//           const errorObj = JSON.parse(errorMatch[0])
//           ElMessage.error(errorObj.error || '登录失败')
//         } else if (error.message.includes('error')) {
//           ElMessage.error('用户名或密码错误')
//         } else {
//           ElMessage.error('登录失败，请检查网络连接')
//         }
//       } catch (e) {
//         ElMessage.error('用户名或密码错误')
//       }
//     } else {
//       ElMessage.error('登录失败，请检查网络连接')
//     }
//   } finally {
//     loading.value = false
//   }
// }
const handleLogin = async () => {
  if (!validateAll()) {
    return
  }

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')

    await userStore.getUserInfo()

    if (userStore.isAdmin) {
      try {
        await ElMessageBox.confirm(
          '检测到当前账号为管理员，请选择进入管理员端或普通用户端。',
          '选择登录端',
          {
            confirmButtonText: '进入管理员端',
            cancelButtonText: '进入普通用户端',
            distinguishCancelAndClose: true,
            type: 'warning'
          }
        )
        userStore.enterAdminMode()
        router.push('/admin/dashboard')
      } catch (action) {
        if (action === 'cancel') {
          userStore.enterUserMode()
          router.push('/')
        }
      }
    } else {
      userStore.enterUserMode()
      router.push('/')
    }
  } catch (error) {
    console.error('登录失败详情:', error)

    const msg = String(error?.message || '').toLowerCase()

    if (
      msg.includes('network error') ||
      msg.includes('timeout') ||
      msg.includes('failed to fetch') ||
      msg.includes('err_network')
    ) {
      await ElMessageBox.alert(
        '网络连接失败，请检查网络后重试',
        '登录失败',
        {
          confirmButtonText: '确定',
          type: 'error'
        }
      )
    } else {
      await ElMessageBox.alert(
        '账号或密码不匹配，请重新输入',
        '登录失败',
        {
          confirmButtonText: '确定',
          type: 'error'
        }
      )
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/*
  确保 tailwind.config.js 中配置了 Material Design 3 的颜色变量。
  如果未配置，以下提供基础 fallback，但建议完善配置以获得最佳效果。
*/
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-primary { background-color: #6750A4; }
.bg-primary-container { background-color: #EADDFF; }
.bg-secondary-container { background-color: #E8DEF8; }
.bg-error { background-color: #B3261E; }
.bg-error-container { background-color: #F9DEDC; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-on-secondary-container { color: #1D192B; }
.text-on-error-container { color: #410E0B; }
.text-primary { color: #6750A4; }
.text-error { color: #B3261E; }
.text-outline { color: #79747E; }

.border-primary { border-color: #6750A4; }
.border-error { border-color: #B3261E; }
.border-outline { border-color: #79747E; }
.border-outline-variant { border-color: #CAC4D0; }
.border-surface-container-high { border-color: #ECE6F0; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.bg-primary\/30 { background-color: rgba(103, 80, 164, 0.3); }
.bg-secondary-container\/20 { background-color: rgba(232, 222, 248, 0.2); }

/* 自定义滚动条隐藏 (可选) */
::-webkit-scrollbar {
  display: none;
}
</style>