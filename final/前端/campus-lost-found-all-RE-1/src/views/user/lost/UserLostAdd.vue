<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Loading Overlay -->
    <div v-if="loading" class="fixed inset-0 z-[60] flex items-center justify-center bg-surface/80 backdrop-blur-sm">
      <div class="flex flex-col items-center gap-4">
        <span class="material-symbols-outlined text-5xl text-primary animate-spin">progress_activity</span>
        <p class="text-on-surface-variant font-medium">正在发布...</p>
      </div>
    </div>

    <main class="max-w-4xl mx-auto px-4 md:px-6 py-8">

      <!-- Header -->
      <header class="mb-8">
        <button @click="$router.push('/user/UserLostList')" class="flex items-center gap-2 text-on-surface-variant hover:text-primary mb-4 transition-colors font-medium">
          <span class="material-symbols-outlined">arrow_back</span>
          返回列表
        </button>
        <div class="bg-surface-container-low rounded-3xl p-8 border border-surface-container-high">
          <div class="inline-flex items-center justify-center w-14 h-14 bg-error/20 text-error rounded-2xl mb-4 shadow-lg shadow-error/10">
            <span class="material-symbols-outlined text-3xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">report_gmailerrorred</span>
          </div>
          <h1 class="text-3xl font-black text-on-surface font-headline mb-2">发布失物信息</h1>
          <p class="text-on-surface-variant font-medium">详细描述丢失物品信息，提高找回几率</p>
        </div>
      </header>

      <!-- Form Card -->
      <div class="bg-surface-container-low rounded-3xl p-6 md:p-10 border border-surface-container-high shadow-sm">
        <form @submit.prevent="submitForm" class="space-y-8">

          <!-- Section: Basic Info -->
          <div class="space-y-6">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2 pb-2 border-b border-surface-container-high">
              <span class="material-symbols-outlined text-primary">inventory_2</span>
              基本物品信息
            </h3>

            <!-- Title -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                物品名称 <span class="text-error">*</span>
              </label>
              <div class="relative group">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                  <span class="material-symbols-outlined">tag</span>
                </span>
                <input
                  v-model="form.title"
                  type="text"
                  placeholder="如：黑色钱包、iPhone 13"
                  class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                  :class="{ 'border-error': errors.title }"
                />
              </div>
              <p v-if="errors.title" class="text-xs text-error font-medium ml-1">{{ errors.title }}</p>
            </div>

            <!-- Category -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                物品分类 <span class="text-error">*</span>
              </label>
              <div class="relative group">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors pointer-events-none">
                  <span class="material-symbols-outlined">folder</span>
                </span>
                <select
                  v-model="form.category"
                  @change="handleCategoryTouched"
                  class="w-full pl-12 pr-10 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none appearance-none cursor-pointer font-medium transition-all duration-300"
                  :class="{ 'border-error': errors.category }"
                >
                  <option value="" disabled>请选择分类</option>
                  <option v-for="c in categories" :key="c.code" :value="c.code">{{ c.name }}</option>
                </select>
                <span class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">
                  <span class="material-symbols-outlined">expand_more</span>
                </span>
              </div>
              <p v-if="errors.category" class="text-xs text-error font-medium ml-1">{{ errors.category }}</p>
            </div>
          </div>

          <!-- Section: Lost Info -->
          <div class="space-y-6">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2 pb-2 border-b border-surface-container-high">
              <span class="material-symbols-outlined text-error">location_on</span>
              丢失信息
            </h3>

            <!-- Location -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                丢失地点 <span class="text-error">*</span>
              </label>
              <div class="relative group">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-error transition-colors">
                  <span class="material-symbols-outlined">map_pin</span>
                </span>
                <input
                  v-model="form.lostLocation"
                  type="text"
                  placeholder="如：图书馆三楼阅览室"
                  class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-error focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                  :class="{ 'border-error': errors.lostLocation }"
                />
              </div>
              <p v-if="errors.lostLocation" class="text-xs text-error font-medium ml-1">{{ errors.lostLocation }}</p>
            </div>

            <!-- Time -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                丢失时间 <span class="text-error">*</span>
              </label>
              <div class="relative group">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-error transition-colors pointer-events-none">
                  <span class="material-symbols-outlined">schedule</span>
                </span>
                <input
                  v-model="form.lostTime"
                  type="datetime-local"
                  class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-error focus:bg-surface outline-none transition-all duration-300 font-medium accent-error"
                  :class="{ 'border-error': errors.lostTime }"
                />
              </div>
              <p v-if="errors.lostTime" class="text-xs text-error font-medium ml-1">{{ errors.lostTime }}</p>
            </div>
          </div>

          <!-- Section: Details & Contact -->
          <div class="space-y-6">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2 pb-2 border-b border-surface-container-high">
              <span class="material-symbols-outlined text-primary">description</span>
              详细信息
            </h3>

            <!-- Description -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                详细描述 <span class="text-error">*</span>
              </label>
              <textarea
                v-model="form.description"
                rows="5"
                placeholder="请详细描述物品特征、品牌、颜色、大小、内含物品等信息..."
                class="w-full p-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 resize-none"
                :class="{ 'border-error': errors.description }"
                maxlength="500"
              ></textarea>
              <div class="flex justify-end pt-2">
                <button
                  type="button"
                  :disabled="aiFilling || aiTitleFilling"
                  @click="handleAiFillByTitle"
                  class="px-4 py-2 rounded-xl bg-secondary/20 text-secondary text-sm font-bold hover:bg-secondary/30 transition-colors disabled:opacity-60"
                >
                  AI根据物品名称补全
                </button>
              </div>
              <div class="flex justify-between items-center px-1">
                <p v-if="errors.description" class="text-xs text-error font-medium">{{ errors.description }}</p>
                <span class="text-xs text-on-surface-variant ml-auto">{{ form.description.length }}/500</span>
              </div>
            </div>

            <!-- Contact Info -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                联系方式
              </label>
              <div class="relative group">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                  <span class="material-symbols-outlined">mail</span>
                </span>
                <input
                  v-model="form.contactInfo"
                  type="text"
                  placeholder="电话、微信或 QQ（留空则使用账户默认联系方式）"
                  class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                />
              </div>
              <div class="bg-primary/10 border border-primary/20 rounded-xl p-3 flex items-start gap-3">
                <span class="material-symbols-outlined text-primary text-sm mt-0.5">info</span>
                <p class="text-xs text-primary font-medium leading-relaxed">
                  系统将默认使用您的账户联系方式。如需使用其他联系方式（如备用手机号），请在此填写。
                </p>
              </div>
            </div>

            <!-- Image -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                物品图片（可选）
              </label>
              <div class="flex flex-col md:flex-row gap-4 items-start">
                <label class="px-4 py-2 rounded-xl bg-surface-container-high hover:bg-surface-container-highest cursor-pointer text-sm font-bold text-on-surface-variant">
                  选择图片
                  <!-- 【修改点 1】修改文件选择限制 -->
                  <input type="file" class="hidden" accept=".jpg,.jpeg,.png,.webp,.gif,.bmp" @change="handleImageChange" />
                </label>
                <button
                  v-if="form.imagePreview"
                  type="button"
                  class="px-4 py-2 rounded-xl bg-error/10 text-error text-sm font-bold"
                  @click="clearImage"
                >
                  清除图片
                </button>
              </div>
              <!-- 【修改点补充】同步修改提示文字，原为2MB，现为5MB -->
              <p class="text-xs text-on-surface-variant">支持 jpg/png/webp，建议小于 5MB。</p>
              <div v-if="form.imagePreview" class="w-40 h-40 rounded-2xl overflow-hidden border border-surface-container-high">
                <img :src="form.imagePreview" alt="预览图" class="w-full h-full object-cover" />
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="pt-6 border-t border-surface-container-high flex flex-col sm:flex-row gap-4">
            <button
              type="submit"
              class="flex-1 py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 flex items-center justify-center gap-2"
            >
              <span class="material-symbols-outlined">send</span>
              提交发布
            </button>

            <button
              type="button"
              @click="resetForm"
              class="flex-1 sm:flex-none px-8 py-4 bg-surface-container-high text-on-surface-variant rounded-2xl font-bold hover:bg-surface-container-highest transition-colors"
            >
              重置
            </button>

            <button
              type="button"
              @click="$router.push('/user/UserLostList')"
              class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors"
            >
              取消
            </button>
          </div>

        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
// 【修改点 2】引入 uploadImage
import { autoFillLostByTitle, createLostItem, uploadImage } from '@/api/lost'
import { useItemCategories } from '@/composables/useItemCategories'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

// 【新增：用来存放用户选择的真实文件流】
let selectedImageFile = null

const router = useRouter()
const userStore = useUserStore()
const { categories, loadCategories } = useItemCategories()

const loading = ref(false)
const aiFilling = ref(false)
const aiTitleFilling = ref(false)
const categoryTouched = ref(false)

const form = reactive({
  title: '',
  category: 'others',
  lostLocation: '',
  lostTime: '',
  description: '',
  contactInfo: '',
  userId: userStore.userInfo?.id || 0,
  imagePreview: '',
  // 删除了没用的 imageBase64
})

const errors = reactive({
  title: '',
  category: '',
  lostLocation: '',
  lostTime: '',
  description: ''
})

const initTime = () => {
  const now = new Date()
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset())
  form.lostTime = now.toISOString().slice(0, 16)
}

initTime()

onMounted(() => {
  loadCategories()
})

const mapAiCategory = (category) => {
  const raw = String(category || '').trim()
  const hit = categories.value.find((c) => c.name === raw || c.code === raw)
  if (hit) return hit.code

  const map = {
    '卡包/钱包': 'cards',
    '证件类': 'cards',
    '证件卡片': 'cards',
    '电子产品': 'electronics',
    '学习用品': 'study',
    '书籍类': 'books',
    '书籍资料': 'books',
    '衣物饰品': 'clothing',
    '服饰配件': 'clothing',
    '其他': 'others',
    '其他物品': 'others'
  }

  return map[raw] || raw || 'others'
}

const handleCategoryTouched = () => {
  categoryTouched.value = true
}

const handleAiFillByTitle = async () => {
  const title = form.title?.trim()
  if (!title) {
    ElMessage.warning('请先填写物品名称')
    return
  }

  aiTitleFilling.value = true
  try {
    const response = await autoFillLostByTitle(title)

    if (response?.code === 200 && response?.data) {
      if (!categoryTouched.value && response.data.category) {
        form.category = mapAiCategory(response.data.category)
      }

      if (!form.description?.trim() || form.description.trim().length < 10) {
        form.description = response.data.description || ''
      }

      if (response.data.error) {
        ElMessage.warning('AI补全已返回，但结果可能不完整，请手动检查')
      } else {
        ElMessage.success('AI已根据物品名称补全分类与描述')
      }
    } else {
      ElMessage.warning(response?.message || 'AI补全失败，请稍后重试')
    }
  } catch (error) {
    console.error('AI补全失败:', error)
    ElMessage.error('AI补全失败，请稍后重试')
  } finally {
    aiTitleFilling.value = false
  }
}

let titleAiTimer = null
watch(
  () => form.title,
  () => {
    clearTimeout(titleAiTimer)
    titleAiTimer = setTimeout(() => {
      if (form.title?.trim()?.length < 2) return
      if (form.description?.trim() && form.description.trim().length >= 10) return
      handleAiFillByTitle()
    }, 600)
  }
)

const formatTimeForBackend = (time) => {
  if (!time) {
    const now = new Date()
    return now.toISOString().replace('T', ' ').substring(0, 19)
  }

  if (time.includes('T')) {
    const date = new Date(time)
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    const seconds = date.getSeconds().toString().padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }

  if (typeof time === 'string' && time.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/)) {
    return time
  }

  const date = new Date(time)
  if (isNaN(date.getTime())) return time

  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const validateForm = () => {
  let isValid = true
  errors.title = ''
  errors.category = ''
  errors.lostLocation = ''
  errors.lostTime = ''
  errors.description = ''

  if (!form.title.trim()) {
    errors.title = '请输入物品名称'
    isValid = false
  } else if (form.title.length < 2 || form.title.length > 50) {
    errors.title = '长度在 2 到 50 个字符'
    isValid = false
  }

  if (!form.category) {
    errors.category = '请选择物品分类'
    isValid = false
  }

  if (!form.lostLocation.trim()) {
    errors.lostLocation = '请输入丢失地点'
    isValid = false
  }

  if (!form.lostTime) {
    errors.lostTime = '请选择丢失时间'
    isValid = false
  }

  if (!form.description.trim()) {
    errors.description = '请输入详细描述'
    isValid = false
  } else if (form.description.length < 10) {
    errors.description = '描述至少需要 10 个字符'
    isValid = false
  }

  return isValid
}

// 【修改点 4】替换核心的 submitForm 函数
const submitForm = async () => {
  if (!validateForm()) {
    ElMessage.warning('请填写完整且正确的信息')
    const firstError = document.querySelector('.border-error')
    if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' })
    return
  }

  loading.value = true
  try {
    const userId = userStore.userInfo?.id || 0
    if (!userId) {
      ElMessage.error('用户信息异常，请重新登录')
      loading.value = false
      return
    }

    // ====== 新增：如果有图片，先调接口上传图片 ======
    let finalImageUrl = ''
    if (selectedImageFile) {
      try {
        const uploadRes = await uploadImage(selectedImageFile)
        if (uploadRes.code === 200) {
          finalImageUrl = uploadRes.data // 拿到 /uploads/xxx.webp
        } else {
          ElMessage.error(uploadRes.message || '图片上传失败')
          loading.value = false
          return // 上传失败就不往下走了
        }
      } catch (err) {
        console.error('上传图片出错', err)
        ElMessage.error('图片上传服务异常')
        loading.value = false
        return
      }
    }
    // ==============================================

    const formattedTime = formatTimeForBackend(form.lostTime)

    const submitData = {
      userId: userId,
      title: form.title,
      category: form.category,
      lostLocation: form.lostLocation,
      lostTime: formattedTime,
      description: form.description,
      contactInfo: form.contactInfo || userStore.userInfo?.phone || '',
      images: finalImageUrl // 【修改：把拿到的图片URL传给后端】
    }

    const response = await createLostItem(submitData)

    if (response.success) {
      ElMessage.success('发布成功！')
      resetForm()
      setTimeout(() => {
        router.push('/user/UserLostList')
      }, 1500)
    } else {
      ElMessage.error(response.error || '发布失败，请重试')
    }
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败，请重试')
  } finally {
    loading.value = false
  }
}

// 【修改点 5】替换 resetForm 函数
const resetForm = () => {
  form.title = ''
  form.category = 'others'
  form.lostLocation = ''
  form.description = ''
  form.contactInfo = ''
  form.userId = userStore.userInfo?.id || 0
  
  if (form.imagePreview) {
    URL.revokeObjectURL(form.imagePreview)
  }
  form.imagePreview = ''
  selectedImageFile = null
  
  categoryTouched.value = false
  initTime()

  Object.keys(errors).forEach(key => errors[key] = '')
  ElMessage.info('表单已重置')
}

// 【修改点 3】替换 handleImageChange 和 clearImage 函数
const handleImageChange = (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  // 1. 严格校验格式
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif', 'image/bmp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.warning('请选择 JPG/PNG/WEBP 等正确格式的图片')
    event.target.value = ''
    return
  }
  
  // 2. 大小放宽到 5MB
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    event.target.value = ''
    return
  }

  // 3. 把真实文件保存下来，等待提交时上传
  selectedImageFile = file

  // 4. 高性能预览（替代 FileReader）
  if (form.imagePreview) {
    URL.revokeObjectURL(form.imagePreview)
  }
  form.imagePreview = URL.createObjectURL(file)
  
  event.target.value = ''
}

const clearImage = () => {
  if (form.imagePreview) {
    URL.revokeObjectURL(form.imagePreview)
  }
  form.imagePreview = ''
  selectedImageFile = null // 清空待上传文件
}
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-primary { background-color: #6750A4; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-error { color: #B3261E; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-error { border-color: #B3261E !important; }
.border-primary\/20 { border-color: rgba(103, 80, 164, 0.2); }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.shadow-error\/10 { --tw-shadow-color: rgba(179, 38, 30, 0.1); }

.bg-error\/20 { background-color: rgba(179, 38, 30, 0.2); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }

/* Custom Scrollbar for Textarea */
textarea::-webkit-scrollbar {
  width: 8px;
}
textarea::-webkit-scrollbar-track {
  background: transparent;
}
textarea::-webkit-scrollbar-thumb {
  background-color: #CAC4D0;
  border-radius: 4px;
}

/* Date Input Icon Color */
input[type="datetime-local"]::-webkit-calendar-picker-indicator {
  filter: invert(0.5);
  cursor: pointer;
}
</style>