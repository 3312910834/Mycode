<template>
  <div class="bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Custom Toast Notification -->
    <div v-if="showSuccessMessage" class="fixed top-6 left-1/2 -translate-x-1/2 z-[70] animate-in fade-in slide-in-from-top-4 duration-300">
      <div class="px-6 py-3 rounded-full shadow-lg flex items-center gap-3 font-medium text-sm bg-success text-on-success">
        <span class="material-symbols-outlined text-base">check_circle</span>
        {{ successMessage }}
      </div>
    </div>

    <main class="w-full px-0 py-2">

      <!-- Header -->
      <header class="mb-8 text-center">
        <h1 class="text-3xl font-black text-on-surface font-headline mb-2">个人信息</h1>
        <p class="text-on-surface-variant font-medium">管理您的个人资料和账号安全</p>
      </header>

      <div class="flex flex-col lg:flex-row gap-6">

        <!-- Left Sidebar: User Overview -->
        <aside class="w-full lg:w-80 flex-shrink-0">
          <div class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high shadow-sm sticky top-6">
            <!-- Avatar Section -->
            <div class="flex flex-col items-center mb-6">
              <div class="relative group mb-4">
                <div class="w-32 h-32 rounded-full bg-surface-container-highest overflow-hidden border-4 border-surface shadow-lg">
                  <img
                    :src="avatarPreview"
                    alt="Avatar"
                    class="w-full h-full object-cover"
                    @error="$event.target.src='/default-avatar.png'"
                  />
                </div>
                <label class="absolute bottom-0 right-0 w-10 h-10 bg-primary text-on-primary rounded-full flex items-center justify-center shadow-md cursor-pointer hover:bg-primary-container hover:text-on-primary-container transition-colors border-2 border-surface">
                  <input type="file" class="hidden" accept="image/*" @change="handleFileChange" />
                  <span class="material-symbols-outlined text-base">camera_alt</span>
                </label>
              </div>

              <div class="flex gap-2">
                <button
                  @click="triggerFileInput"
                  class="px-4 py-2 bg-primary/10 text-primary rounded-full text-sm font-bold hover:bg-primary/20 transition-colors"
                >
                  更换头像
                </button>
                <button
                  @click="resetAvatar"
                  class="px-4 py-2 bg-surface-container-high text-on-surface-variant rounded-full text-sm font-bold hover:bg-surface-container-highest transition-colors"
                >
                  恢复默认
                </button>
              </div>
              <input ref="fileInputRef" type="file" class="hidden" accept="image/*" @change="handleFileChange" />
            </div>

            <!-- Quick Info -->
            <div class="text-center border-t border-surface-container-high pt-6">
              <h3 class="text-xl font-bold text-on-surface mb-2">{{ userStore.userName || '用户' }}</h3>
              <div class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide mb-3"
                   :class="userInfo.role === 'admin' ? 'bg-error/10 text-error' : 'bg-success/10 text-success'">
                {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
              </div>
              <div class="text-xs font-medium text-on-surface-variant bg-surface-container-high inline-block px-3 py-1 rounded-full">
                ID: {{ userStore.userInfo?.id }}
              </div>
            </div>
          </div>
        </aside>

        <!-- Right Content Area -->
        <section class="flex-1">

          <!-- Tab Navigation (Custom) -->
          <div class="flex gap-2 mb-6 bg-surface-container-low p-1.5 rounded-2xl inline-flex w-full sm:w-auto">
            <button
              @click="activeTab = 'basic'"
              :class="['flex-1 sm:flex-none px-6 py-3 rounded-xl text-sm font-bold transition-all duration-300', activeTab === 'basic' ? 'bg-surface text-on-surface shadow-sm' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high']"
            >
              基本信息
            </button>
            <button
              @click="activeTab = 'account'"
              :class="['flex-1 sm:flex-none px-6 py-3 rounded-xl text-sm font-bold transition-all duration-300', activeTab === 'account' ? 'bg-surface text-on-surface shadow-sm' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high']"
            >
              账号信息
            </button>
          </div>

          <!-- Basic Info Form -->
          <div v-show="activeTab === 'basic'" class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm animate-in fade-in zoom-in-95 duration-300">
            <form @submit.prevent="submitForm" class="space-y-6">

              <!-- Username (Disabled) -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">用户名</label>
                <div class="relative">
                  <input
                    v-model="form.username"
                    disabled
                    class="w-full px-4 py-4 bg-surface-container-highest text-on-surface-variant rounded-2xl border-2 border-transparent outline-none cursor-not-allowed opacity-70"
                  />
                  <span class="absolute right-4 top-1/2 -translate-y-1/2 px-3 py-1 bg-secondary-container text-secondary text-xs font-bold rounded-full">
                    不可修改
                  </span>
                </div>
              </div>

              <!-- Real Name -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">真实姓名</label>
                <div class="relative">
                  <input
                    v-model="form.real_name"
                    placeholder="请输入真实姓名"
                    maxlength="20"
                    class="w-full px-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                    :class="{ 'border-error': errors.real_name }"
                  />
                  <div v-if="originalForm.real_name !== form.real_name && form.real_name" class="absolute right-4 top-1/2 -translate-y-1/2 flex items-center gap-1 text-success text-xs font-bold">
                    <span class="material-symbols-outlined text-base">check_circle</span>
                    已修改
                  </div>
                </div>
                <p v-if="errors.real_name" class="text-xs text-error font-medium ml-1">{{ errors.real_name }}</p>
              </div>

              <!-- Student ID -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">学号</label>
                <input
                  v-model="form.student_id"
                  placeholder="请输入学号"
                  class="w-full px-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                  :class="{ 'border-error': errors.student_id }"
                />
                <p v-if="errors.student_id" class="text-xs text-error font-medium ml-1">{{ errors.student_id }}</p>
              </div>

              <!-- Phone -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">手机号码</label>
                <div class="relative">
                  <input
                    v-model="form.phone"
                    placeholder="请输入手机号码"
                    maxlength="11"
                    class="w-full px-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 pr-20"
                    :class="{ 'border-error': errors.phone }"
                  />
                  <div v-if="originalForm.phone !== form.phone && form.phone" class="absolute right-4 top-1/2 -translate-y-1/2 flex items-center gap-1 text-success text-xs font-bold">
                    <span class="material-symbols-outlined text-base">check_circle</span>
                    已修改
                  </div>
                </div>
                <p v-if="errors.phone" class="text-xs text-error font-medium ml-1">{{ errors.phone }}</p>
              </div>

              <!-- Email -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">邮箱地址</label>
                <div class="relative">
                  <input
                    v-model="form.email"
                    type="email"
                    placeholder="请输入邮箱地址"
                    class="w-full px-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 pr-20"
                    :class="{ 'border-error': errors.email }"
                  />
                  <div v-if="originalForm.email !== form.email && form.email" class="absolute right-4 top-1/2 -translate-y-1/2 flex items-center gap-1 text-success text-xs font-bold">
                    <span class="material-symbols-outlined text-base">check_circle</span>
                    已修改
                  </div>
                </div>
                <p v-if="errors.email" class="text-xs text-error font-medium ml-1">{{ errors.email }}</p>
              </div>

              <!-- Actions -->
              <div class="pt-6 flex flex-col sm:flex-row gap-4 border-t border-surface-container-high">
                <button
                  type="submit"
                  :disabled="loading || (!hasChanges && !avatarChanged)"
                  class="flex-1 py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
                >
                  <span v-if="loading" class="material-symbols-outlined animate-spin">progress_activity</span>
                  <span v-else class="material-symbols-outlined">save</span>
                  保存修改
                </button>

                <button
                  type="button"
                  @click="resetForm"
                  :disabled="(!hasChanges && !avatarChanged) || loading"
                  class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                >
                  <span class="material-symbols-outlined">refresh</span>
                  重置
                </button>

                <button
                  type="button"
                  @click="refreshUserInfo"
                  :disabled="loading"
                  class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                >
                  <span class="material-symbols-outlined">sync</span>
                  刷新
                </button>
              </div>
            </form>
          </div>

          <!-- Account Info Grid -->
          <div v-show="activeTab === 'account'" class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm animate-in fade-in zoom-in-95 duration-300">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div v-for="(item, idx) in accountInfoList" :key="idx" class="bg-surface p-5 rounded-2xl border border-surface-container-high flex items-center gap-4 hover:bg-surface-container-high transition-colors">
                <div class="w-12 h-12 rounded-full bg-primary/10 text-primary flex items-center justify-center flex-shrink-0">
                  <span class="material-symbols-outlined text-xl">{{ item.icon }}</span>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-xs font-bold text-on-surface-variant uppercase tracking-wide mb-1">{{ item.label }}</div>
                  <div class="font-bold text-on-surface break-all">
                    <span v-if="item.isTag"
                          class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold uppercase"
                          :class="item.tagType === 'error' ? 'bg-error/10 text-error' : 'bg-success/10 text-success'">
                      {{ item.value }}
                    </span>
                    <span v-else>{{ item.value }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Update History -->
          <div v-if="updateHistory.length > 0" class="mt-6 bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
            <h3 class="text-lg font-bold text-on-surface mb-6 flex items-center gap-2">
              <span class="material-symbols-outlined text-primary">history</span>
              最近更新记录
            </h3>
            <div class="space-y-0 relative pl-4">
              <!-- Vertical Line -->
              <div class="absolute left-[21px] top-4 bottom-4 w-0.5 bg-surface-container-high"></div>

              <div v-for="(record, index) in updateHistory.slice(0, 3)" :key="index" class="relative pl-8 pb-8 last:pb-0 animate-in slide-in-from-left-4 duration-500" :style="{ animationDelay: index * 100 + 'ms' }">
                <!-- Dot -->
                <div class="absolute left-0 top-1.5 w-3 h-3 rounded-full bg-primary border-4 border-surface-container-low shadow-sm"></div>

                <div class="bg-surface p-4 rounded-2xl border border-surface-container-high shadow-sm">
                  <div class="flex justify-between items-start mb-2">
                    <h4 class="font-bold text-on-surface">{{ record.field }} 已更新</h4>
                    <span class="text-xs font-medium text-on-surface-variant bg-surface-container-high px-2 py-1 rounded-lg">{{ formatTime(record.time) }}</span>
                  </div>
                  <p class="text-sm text-on-surface-variant">
                    从 <span class="font-mono bg-surface-container-highest px-1 rounded">{{ record.oldValue }}</span>
                    改为 <span class="font-mono bg-surface-container-highest px-1 rounded">{{ record.newValue }}</span>
                  </p>
                </div>
              </div>
            </div>
          </div>

        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref, computed } from 'vue'

const userStore = useUserStore()
const fileInputRef = ref()
const loading = ref(false)
const avatarPreview = ref('')
const activeTab = ref('basic')
const showSuccessMessage = ref(false)
const successMessage = ref('')
const updateHistory = ref([])

const originalForm = reactive({
  username: '',
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

const form = reactive({
  username: '',
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

const errors = reactive({
  real_name: '',
  student_id: '',
  phone: '',
  email: ''
})

const userInfo = reactive({
  username: '',
  create_time: '',
  last_login: '',
  role: '',
  status: 1,
  email: '',
  phone: ''
})

const hasChanges = computed(() => {
  return (
    form.real_name !== originalForm.real_name ||
    form.student_id !== originalForm.student_id ||
    form.phone !== originalForm.phone ||
    form.email !== originalForm.email
  )
})
const avatarChanged = computed(() => avatarPreview.value !== (userStore.userInfo?.avatar || '/default-avatar.png'))

const accountInfoList = computed(() => [
  { label: '用户名', value: userInfo.username, icon: 'person' },
  { label: '注册时间', value: formatDate(userInfo.create_time), icon: 'calendar_today' },
  { label: '最后登录', value: formatDate(userInfo.last_login), icon: 'schedule' },
  { label: '用户角色', value: userInfo.role === 'admin' ? '管理员' : '普通用户', isTag: true, tagType: userInfo.role === 'admin' ? 'error' : 'success', icon: 'badge' },
  { label: '账号状态', value: userInfo.status === 1 ? '正常' : '禁用', isTag: true, tagType: userInfo.status === 1 ? 'success' : 'error', icon: 'shield' },
  { label: '邮箱', value: userInfo.email || '未设置', icon: 'mail' },
  { label: '手机号', value: userInfo.phone || '未设置', icon: 'smartphone' },
])

onMounted(async () => {
  await loadUserInfo()
})

const loadUserInfo = async () => {
  try {
    const userData = await userStore.getUserInfo()

    form.username = userData.username || ''
    form.real_name = userData.real_name || ''
    form.student_id = userData.student_id || ''
    form.phone = userData.phone || ''
    form.email = userData.email || ''

    Object.assign(originalForm, { ...form })

    userInfo.username = userData.username || ''
    userInfo.create_time = userData.create_time || ''
    userInfo.last_login = userData.last_login || ''
    userInfo.role = userData.role || 'user'
    userInfo.status = userData.status || 1
    userInfo.email = userData.email || ''
    userInfo.phone = userData.phone || ''

    avatarPreview.value = userData.avatar || userStore.userInfo?.avatar || '/default-avatar.png'

  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

const triggerFileInput = () => {
  fileInputRef.value?.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) return

  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.warning('只能上传图片文件')
    return
  }
  if (!isLt2M) {
    ElMessage.warning('头像大小不能超过 2MB')
    return
  }

  // Simulate upload logic for demo (replace with actual API call)
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target.result
    // 先本地预览，最终由保存按钮统一提交
  }
  reader.readAsDataURL(file)

  // Reset input so same file can be selected again
  event.target.value = ''
}

const resetAvatar = () => {
  avatarPreview.value = userStore.userInfo?.avatar || '/default-avatar.png'
}

const addUpdateHistory = (field, oldValue, newValue) => {
  updateHistory.value.unshift({
    field,
    oldValue: oldValue || '空',
    newValue: newValue || '空',
    time: new Date().toISOString()
  })
  if (updateHistory.value.length > 10) {
    updateHistory.value = updateHistory.value.slice(0, 10)
  }
}

const validateForm = () => {
  let isValid = true
  errors.real_name = ''
  errors.student_id = ''
  errors.phone = ''
  errors.email = ''

  if (!form.real_name) {
    errors.real_name = '请输入真实姓名'
    isValid = false
  } else if (form.real_name.length < 2 || form.real_name.length > 20) {
    errors.real_name = '姓名长度在 2 到 20 个字符'
    isValid = false
  }

  if (form.student_id && !/^\d{8,15}$/.test(form.student_id)) {
    errors.student_id = '学号格式不正确'
    isValid = false
  }

  if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
    errors.phone = '手机号码格式不正确'
    isValid = false
  }

  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = '邮箱格式不正确'
    isValid = false
  }

  return isValid
}

const submitForm = async () => {
  if (!hasChanges.value && !avatarChanged.value) {
    ElMessage.info('未检测到可保存的修改')
    return
  }

  if (!validateForm()) return

  loading.value = true

  try {
    const changes = {}
    if (form.real_name !== originalForm.real_name) changes.real_name = form.real_name
    if (form.student_id !== originalForm.student_id) changes.student_id = form.student_id
    if (form.phone !== originalForm.phone) changes.phone = form.phone
    if (form.email !== originalForm.email) changes.email = form.email

    let profileUpdateOk = true
    if (Object.keys(changes).length > 0) {
      const updateData = {
        id: userStore.userInfo?.id,
        ...changes
      }
      const response = await updateUserInfo(updateData)
      profileUpdateOk = !!(response?.code === 200 || response?.success)
      if (!profileUpdateOk) {
        throw new Error(response?.message || response?.error || '更新失败')
      }
    }

    if (profileUpdateOk) {
      Object.keys(changes).forEach(field => {
        addUpdateHistory(getFieldLabel(field), originalForm[field] || '空', form[field] || '空')
      })

      if (avatarChanged.value) {
        addUpdateHistory('头像', userStore.userInfo?.avatar || '默认头像', '已更新')
      }

      const changedCount = Object.keys(changes).length + (avatarChanged.value ? 1 : 0)
      successMessage.value = `成功更新了 ${changedCount} 个字段`
      showSuccessMessage.value = true

      userStore.updateUserInfo({
        ...changes,
        ...(avatarChanged.value ? { avatar: avatarPreview.value } : {})
      })
      Object.assign(originalForm, { ...form })

      setTimeout(() => { showSuccessMessage.value = false }, 3000)
      await loadUserInfo()
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const getFieldLabel = (field) => {
  const labels = { real_name: '真实姓名', student_id: '学号', phone: '手机号', email: '邮箱', avatar: '头像' }
  return labels[field] || field
}

const resetForm = () => {
  Object.assign(form, { ...originalForm })
}

const refreshUserInfo = async () => {
  await loadUserInfo()
}

const formatDate = (dateString) => {
  if (!dateString) return '未记录'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
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
.bg-primary-container { background-color: #EADDFF; }
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
.border-surface-container-low { border-color: #F7F2FA; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }

.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
.bg-primary\/20 { background-color: rgba(103, 80, 164, 0.2); }
.bg-success\/10 { background-color: rgba(0, 110, 28, 0.1); }
.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }

/* Animations */
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
.animate-in { animation: fadeIn 0.3s ease-out forwards; }
.zoom-in-95 { --tw-scale-x: .95; --tw-scale-y: .95; }
.slide-in-from-left-4 { --tw-translate-x: -1rem; }
</style>