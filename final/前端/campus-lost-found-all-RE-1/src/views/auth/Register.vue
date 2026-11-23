<template>
  <div class="min-h-screen bg-surface flex items-center justify-center p-4 relative overflow-hidden font-body">

    <!-- Background Decorative Elements -->
    <div class="absolute top-[-10%] right-[-10%] w-[500px] h-[500px] bg-secondary-container/30 rounded-full blur-[100px] pointer-events-none animate-pulse"></div>
    <div class="absolute bottom-[-10%] left-[-10%] w-[600px] h-[600px] bg-primary-container/20 rounded-full blur-[120px] pointer-events-none"></div>

    <!-- Main Card -->
    <div class="w-full max-w-[480px] bg-surface-container-low backdrop-blur-xl rounded-[2rem] shadow-2xl border border-surface-container-high p-8 md:p-10 relative z-10 transition-all duration-500 flex flex-col max-h-[90vh]">

      <!-- Header -->
      <div class="text-center mb-8 shrink-0">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-secondary-container text-secondary rounded-2xl mb-6 shadow-lg shadow-secondary/10">
          <span class="material-symbols-outlined text-4xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">person_add</span>
        </div>
        <h2 class="text-3xl font-black text-on-surface tracking-tight font-headline mb-2">创建账户</h2>
        <p class="text-on-surface-variant font-medium">加入校园失物招领社区</p>
      </div>

      <!-- Scrollable Form Area -->
      <div class="overflow-y-auto pr-2 custom-scrollbar flex-grow">
        <form @submit.prevent="handleRegister" class="space-y-5">

          <!-- Username -->
          <div class="group relative">
            <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">badge</span>
            </div>
            <input
              v-model="registerForm.username"
              type="text"
              placeholder="用户名 (4-20位字母数字)"
              class="w-full pl-12 pr-4 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
              :class="{ 'border-error': errors.username }"
              @blur="validateField('username')"
            />
            <p v-if="errors.username" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.username }}
            </p>
          </div>

          <!-- Password -->
          <div class="group relative">
            <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">lock</span>
            </div>
            <input
              v-model="registerForm.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="密码 (至少6位)"
              class="w-full pl-12 pr-12 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
              :class="{ 'border-error': errors.password }"
              @blur="validateField('password')"
            />
            <button type="button" @click="showPassword = !showPassword" class="absolute inset-y-0 right-4 flex items-center text-on-surface-variant hover:text-primary transition-colors">
              <span class="material-symbols-outlined">{{ showPassword ? 'visibility_off' : 'visibility' }}</span>
            </button>
            <p v-if="errors.password" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.password }}
            </p>
          </div>

          <!-- Confirm Password -->
          <div class="group relative">
            <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">lock_reset</span>
            </div>
            <input
              v-model="registerForm.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              placeholder="确认密码"
              class="w-full pl-12 pr-12 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
              :class="{ 'border-error': errors.confirmPassword }"
              @blur="validateField('confirmPassword')"
            />
            <button type="button" @click="showConfirmPassword = !showConfirmPassword" class="absolute inset-y-0 right-4 flex items-center text-on-surface-variant hover:text-primary transition-colors">
              <span class="material-symbols-outlined">{{ showConfirmPassword ? 'visibility_off' : 'visibility' }}</span>
            </button>
            <p v-if="errors.confirmPassword" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.confirmPassword }}
            </p>
          </div>

          <!-- Real Name -->
          <div class="group relative">
            <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">person</span>
            </div>
            <input
              v-model="registerForm.real_name"
              type="text"
              placeholder="真实姓名"
              class="w-full pl-12 pr-4 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
              :class="{ 'border-error': errors.real_name }"
              @blur="validateField('real_name')"
            />
            <p v-if="errors.real_name" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.real_name }}
            </p>
          </div>

          <!-- Student ID -->
          <div class="group relative">
            <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">school</span>
            </div>
            <input
              v-model="registerForm.student_id"
              type="text"
              placeholder="学号"
              class="w-full pl-12 pr-4 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
              :class="{ 'border-error': errors.student_id }"
              @blur="validateField('student_id')"
            />
            <p v-if="errors.student_id" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.student_id }}
            </p>
          </div>

          <!-- Phone -->
          <div class="group relative">
            <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">phone</span>
            </div>
            <input
              v-model="registerForm.phone"
              type="tel"
              placeholder="手机号 (可选)"
              class="w-full pl-12 pr-4 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
              :class="{ 'border-error': errors.phone }"
              @blur="validateField('phone')"
            />
            <p v-if="errors.phone" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.phone }}
            </p>
          </div>

          <!-- Email -->
          <div class="group relative">
            <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">email</span>
            </div>
            <input
              v-model="registerForm.email"
              type="email"
              placeholder="邮箱 (可选)"
              class="w-full pl-12 pr-4 py-3.5 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 shadow-inner"
              :class="{ 'border-error': errors.email }"
              @blur="validateField('email')"
            />
            <p v-if="errors.email" class="absolute -bottom-5 left-4 text-xs text-error font-bold flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">warning</span> {{ errors.email }}
            </p>
          </div>

          <!-- Terms Agreement -->
          <div class="pt-4 pb-2">
            <label class="flex items-start gap-3 cursor-pointer group">
              <div class="relative flex items-center mt-1">
                <input
                  v-model="agreeTerms"
                  type="checkbox"
                  class="peer sr-only"
                />
                <div class="w-5 h-5 border-2 border-outline rounded-md peer-checked:bg-primary peer-checked:border-primary transition-all duration-200 flex items-center justify-center">
                  <span class="material-symbols-outlined text-white text-sm opacity-0 peer-checked:opacity-100 transition-opacity" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">check</span>
                </div>
              </div>
              <span class="text-sm text-on-surface-variant group-hover:text-on-surface transition-colors leading-relaxed">
                我已阅读并同意
                <a href="#" class="text-primary font-bold hover:underline decoration-2 underline-offset-2">《用户协议》</a>
                和
                <a href="#" class="text-primary font-bold hover:underline decoration-2 underline-offset-2">《隐私政策》</a>
              </span>
            </label>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading || !agreeTerms"
            class="w-full py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.98] transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none disabled:shadow-none flex items-center justify-center gap-2 mt-4"
          >
            <span v-if="loading" class="material-symbols-outlined animate-spin">progress_activity</span>
            <span>{{ loading ? '注册中...' : '立即注册' }}</span>
          </button>
        </form>
      </div>

      <!-- Footer -->
      <div class="mt-6 text-center shrink-0">
        <p class="text-on-surface-variant font-medium">
          已有账户？
          <button
            @click="$router.push('/login')"
            class="ml-1 text-primary font-bold hover:underline decoration-2 underline-offset-4 transition-all"
          >
            立即登录
          </button>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const agreeTerms = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

const errors = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

// 验证逻辑
const validateUsername = (value) => {
  if (!value) return '请输入用户名'
  if (!/^[a-zA-Z0-9]{4,20}$/.test(value)) return '用户名必须为4-20位字母数字'
  return ''
}

const validatePassword = (value) => {
  if (!value) return '请输入密码'
  if (value.length < 6) return '密码长度不能少于6位'
  return ''
}

const validateConfirmPassword = (value) => {
  if (!value) return '请再次输入密码'
  if (value !== registerForm.password) return '两次输入的密码不一致'
  return ''
}

const validateRealName = (value) => {
  if (!value) return '请输入真实姓名'
  if (value.length < 2 || value.length > 20) return '姓名长度在 2 到 20 个字符'
  return ''
}

const validateStudentId = (value) => {
  if (!value) return '请输入学号'
  if (!/^\d{8,15}$/.test(value)) return '学号格式不正确'
  return ''
}

const validatePhone = (value) => {
  if (!value) return '' // 可选
  if (!/^1[3-9]\d{9}$/.test(value)) return '手机号格式不正确'
  return ''
}

const validateEmail = (value) => {
  if (!value) return '' // 可选
  if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) return '邮箱格式不正确'
  return ''
}

const validateField = (field) => {
  const value = registerForm[field]
  let error = ''

  switch (field) {
    case 'username': error = validateUsername(value); break;
    case 'password': error = validatePassword(value); break;
    case 'confirmPassword': error = validateConfirmPassword(value); break;
    case 'real_name': error = validateRealName(value); break;
    case 'student_id': error = validateStudentId(value); break;
    case 'phone': error = validatePhone(value); break;
    case 'email': error = validateEmail(value); break;
  }

  errors[field] = error
  return !error
}

const validateAll = () => {
  const fields = ['username', 'password', 'confirmPassword', 'real_name', 'student_id', 'phone', 'email']
  let isValid = true
  fields.forEach(field => {
    if (!validateField(field)) isValid = false
  })
  return isValid
}

const handleRegister = async () => {
  if (!agreeTerms.value) {
    ElMessage.warning('请阅读并同意用户协议和隐私政策')
    return
  }

  if (!validateAll()) {
    // 找到第一个错误字段并滚动（可选优化）
    return
  }

  loading.value = true
  try {
    const registerData = {
      username: registerForm.username,
      password: registerForm.password,
      phone: registerForm.phone,
      real_name: registerForm.real_name,
      student_id: registerForm.student_id,
      email: registerForm.email || `${registerForm.username}@example.com`
    }

    console.log('发送注册数据:', registerData)

    await userStore.register(registerData)
    ElMessage.success('注册成功！请登录')

    // 清空表单
    Object.keys(registerForm).forEach(key => {
      registerForm[key] = ''
    })
    // 清空错误
    Object.keys(errors).forEach(key => {
      errors[key] = ''
    })

    router.push('/login')
  } catch (error) {
    console.error('注册失败详情:', error)

    if (error.message) {
      try {
        const errorMatch = error.message.match(/\{.*\}/)
        if (errorMatch) {
          const errorObj = JSON.parse(errorMatch[0])
          ElMessage.error(errorObj.error || '注册失败')
        } else {
          ElMessage.error('注册失败，用户名可能已存在')
        }
      } catch (e) {
        ElMessage.error('注册失败，请检查网络连接')
      }
    } else {
      ElMessage.error('注册失败，请重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Fallback colors if Tailwind config is missing */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-primary { background-color: #6750A4; }
.bg-secondary-container { background-color: #E8DEF8; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-secondary { color: #625B71; }
.text-primary { color: #6750A4; }
.text-error { color: #B3261E; }
.text-outline { color: #79747E; }

.border-primary { border-color: #6750A4; }
.border-error { border-color: #B3261E; }
.border-outline { border-color: #79747E; }
.border-surface-container-high { border-color: #ECE6F0; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.bg-primary-container\/20 { background-color: rgba(234, 221, 255, 0.2); }
.bg-secondary-container\/30 { background-color: rgba(232, 222, 248, 0.3); }

/* Custom Scrollbar for the form area */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #CAC4D0;
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #79747E;
}
</style>