<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Loading Overlay -->
    <div v-if="loading" class="fixed inset-0 z-[60] flex items-center justify-center bg-surface/80 backdrop-blur-sm">
      <div class="flex flex-col items-center gap-4">
        <span class="material-symbols-outlined text-5xl text-primary animate-spin">progress_activity</span>
        <p class="text-on-surface-variant font-medium">加载中...</p>
      </div>
    </div>

    <main class="max-w-4xl mx-auto px-4 md:px-6 py-8">

      <!-- Header -->
      <header class="mb-8">
        <button @click="goBack" class="flex items-center gap-2 text-on-surface-variant hover:text-primary mb-4 transition-colors font-medium">
          <span class="material-symbols-outlined">arrow_back</span>
          返回列表
        </button>
        <div class="bg-surface-container-low rounded-3xl p-8 border border-surface-container-high">
          <div class="inline-flex items-center justify-center w-14 h-14 bg-secondary-container text-secondary rounded-2xl mb-4 shadow-lg shadow-secondary/10">
            <span class="material-symbols-outlined text-3xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">edit_document</span>
          </div>
          <h1 class="text-3xl font-black text-on-surface font-headline mb-2">编辑招领信息</h1>
          <p class="text-on-surface-variant font-medium">修改拾获物品信息，确保准确性</p>
        </div>
      </header>

      <div v-if="!loading">
        <div v-if="!isOwner" class="bg-error/10 text-error text-lg font-bold rounded-2xl p-8 text-center mb-8">
          <span class="material-symbols-outlined text-3xl align-middle mr-2">block</span>
          {{ forbiddenMsg }}
        </div>

        <div v-else class="bg-surface-container-low rounded-3xl p-6 md:p-10 border border-surface-container-high shadow-sm">
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
                    placeholder="请输入物品名称"
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
                    class="w-full pl-12 pr-10 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none appearance-none cursor-pointer font-medium transition-all duration-300"
                    :class="{ 'border-error': errors.category }"
                  >
                    <option value="" disabled>请选择分类</option>
                    <option value="electronics">电子产品</option>
                    <option value="study">学习用品</option>
                    <option value="cards">证件卡类</option>
                    <option value="clothing">衣物饰品</option>
                    <option value="books">书籍资料</option>
                    <option value="others">其他物品</option>
                  </select>
                  <span class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">
                    <span class="material-symbols-outlined">expand_more</span>
                  </span>
                </div>
                <p v-if="errors.category" class="text-xs text-error font-medium ml-1">{{ errors.category }}</p>
              </div>

              <!-- 【新增】图片上传区域 -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                  物品图片（可选，重新上传将替换原图）
                </label>
                <div class="flex flex-col md:flex-row gap-4 items-start">
                  <label class="px-4 py-2 rounded-xl bg-surface-container-high hover:bg-surface-container-highest cursor-pointer text-sm font-bold text-on-surface-variant">
                    选择新图片
                    <input type="file" class="hidden" accept="image/*" @change="handleImageChange" />
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
                <p class="text-xs text-on-surface-variant">支持 jpg/png/webp，建议小于 5MB</p>

                <!-- 图片预览 -->
                <div v-if="form.imagePreview" class="w-40 h-40 rounded-2xl overflow-hidden border border-surface-container-high">
                  <img :src="form.imagePreview" alt="预览图" class="w-full h-full object-cover" />
                </div>
                <div v-else-if="originalImage" class="w-40 h-40 rounded-2xl overflow-hidden border border-surface-container-high">
                  <img :src="originalImage" alt="当前图片" class="w-full h-full object-cover" />
                </div>
              </div>
            </div>

            <!-- Section: Found Info -->
            <div class="space-y-6">
              <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2 pb-2 border-b border-surface-container-high">
                <span class="material-symbols-outlined text-secondary">location_on</span>
                拾获信息
              </h3>

              <!-- Location -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                  拾获地点 <span class="text-error">*</span>
                </label>
                <div class="relative group">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-secondary transition-colors">
                    <span class="material-symbols-outlined">map_pin</span>
                  </span>
                  <input
                    v-model="form.foundLocation"
                    type="text"
                    placeholder="请输入详细拾获地点"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-secondary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                    :class="{ 'border-error': errors.foundLocation }"
                  />
                </div>
                <p v-if="errors.foundLocation" class="text-xs text-error font-medium ml-1">{{ errors.foundLocation }}</p>
              </div>

              <!-- Time -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                  拾获时间 <span class="text-error">*</span>
                </label>
                <div class="relative group">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-secondary transition-colors pointer-events-none">
                    <span class="material-symbols-outlined">schedule</span>
                  </span>
                  <input
                    v-model="form.foundTime"
                    type="datetime-local"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-secondary focus:bg-surface outline-none transition-all duration-300 font-medium accent-primary"
                    :class="{ 'border-error': errors.foundTime }"
                  />
                </div>
                <p v-if="errors.foundTime" class="text-xs text-error font-medium ml-1">{{ errors.foundTime }}</p>
              </div>

              <!-- Storage Location -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                  保管地点
                </label>
                <div class="relative group">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-secondary transition-colors">
                    <span class="material-symbols-outlined">home_storage</span>
                  </span>
                  <input
                    v-model="form.storageLocation"
                    type="text"
                    placeholder="请输入物品当前保管地点"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-secondary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                  />
                </div>
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
                  placeholder="请详细描述物品特征..."
                  class="w-full p-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 resize-none"
                  :class="{ 'border-error': errors.description }"
                  maxlength="500"
                ></textarea>
                <div class="flex justify-end pt-2">
                  <button
                    type="button"
                    :disabled="aiAssistLoading"
                    @click="handleAiAssistDescription"
                    class="px-4 py-2 rounded-xl bg-secondary/20 text-secondary text-sm font-bold hover:bg-secondary/30 transition-colors disabled:opacity-60"
                  >
                    AI润色描述
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
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-secondary transition-colors">
                    <span class="material-symbols-outlined">mail</span>
                  </span>
                  <input
                    v-model="form.contactInfo"
                    type="text"
                    placeholder="请输入您的联系方式"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-secondary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                  />
                </div>
              </div>

              <!-- Status -->
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                  物品状态 <span class="text-error">*</span>
                </label>
                <div class="relative group">
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors pointer-events-none">
                    <span class="material-symbols-outlined">toggle_on</span>
                  </span>
                  <select
                    v-model="form.status"
                    class="w-full pl-12 pr-10 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none appearance-none cursor-pointer font-medium transition-all duration-300"
                    :class="{ 'border-error': errors.status }"
                  >
                    <option value="pending">待处理</option>
                    <option value="published">已发布</option>
                    <option value="claimed">已认领</option>
                    <option value="closed">已关闭</option>
                  </select>
                  <span class="absolute right-4 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">
                    <span class="material-symbols-outlined">expand_more</span>
                  </span>
                </div>
                <p v-if="errors.status" class="text-xs text-error font-medium ml-1">{{ errors.status }}</p>
              </div>
            </div>

            <!-- Actions -->
            <div class="pt-6 border-t border-surface-container-high flex flex-col sm:flex-row gap-4">
              <button
                type="submit"
                :disabled="submitting"
                class="flex-1 py-4 bg-primary text-on-primary rounded-2xl font-bold text-lg shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 flex items-center justify-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed disabled:hover:translate-y-0"
              >
                <span v-if="submitting" class="material-symbols-outlined animate-spin">progress_activity</span>
                <span v-else class="material-symbols-outlined">save</span>
                {{ submitting ? '保存中...' : '保存修改' }}
              </button>

              <button
                type="button"
                @click="goBack"
                :disabled="submitting"
                class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors disabled:opacity-50"
              >
                取消
              </button>
            </div>

          </form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { getFoundDetail, updateFoundItem, uploadImage } from '@/api/found'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const submitting = ref(false)
const isOwner = ref(true)
const forbiddenMsg = ref('')
const aiAssistLoading = ref(false)

// 【新增】存放用户选择的新图片文件
let selectedImageFile = null

const form = reactive({
  title: '',
  category: '',
  foundLocation: '',
  foundTime: '',
  storageLocation: '',
  description: '',
  contactInfo: '',
  status: 'pending',
  userId: 0,
  imagePreview: '',     // 用于预览（新上传的图片）
  images: ''            // 最终要提交给后端的图片路径
})

// 保存原始图片路径（用于显示当前图片）
const originalImage = ref('')

const errors = reactive({
  title: '',
  category: '',
  foundLocation: '',
  foundTime: '',
  description: '',
  status: ''
})

onMounted(() => {
  fetchItemDetail()
})

const fetchItemDetail = async () => {
  const itemId = route.params.id
  if (!itemId) {
    ElMessage.error('物品 ID 不存在')
    router.push('/user/found')
    return
  }

  try {
    const response = await getFoundDetail(itemId)
    const data = response.data || response

    form.title = data.title || ''
    form.category = data.category || ''
    form.foundLocation = data.foundLocation || ''
    form.storageLocation = data.storageLocation || ''
    form.description = data.description || ''
    form.contactInfo = data.contactInfo || data.contact_info || ''
    form.status = data.status || 'pending'
    form.userId = data.userId || userStore.userInfo?.id || 0

    // 处理时间格式
    if (data.foundTime) {
      const date = new Date(data.foundTime)
      if (!isNaN(date.getTime())) {
        date.setMinutes(date.getMinutes() - date.getTimezoneOffset())
        form.foundTime = date.toISOString().slice(0, 16)
      }
    }

    // 【重要】取出当前图片路径并显示
    if (data.images) {
      form.images = data.images
      originalImage.value = data.images
    }

    // 权限校验
    if (userStore.userInfo?.id && form.userId && userStore.userInfo.id !== form.userId) {
      isOwner.value = false
      forbiddenMsg.value = '您无权编辑他人的招领信息！'
    }
  } catch (error) {
    console.error('获取物品详情失败:', error)
    ElMessage.error('加载失败')
    router.push('/user/found')
  } finally {
    loading.value = false
  }
}

const handleImageChange = (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    return
  }

  selectedImageFile = file

  // 生成本地预览
  if (form.imagePreview) URL.revokeObjectURL(form.imagePreview)
  form.imagePreview = URL.createObjectURL(file)
  
  event.target.value = ''
}

const clearImage = () => {
  if (form.imagePreview) {
    URL.revokeObjectURL(form.imagePreview)
  }
  form.imagePreview = ''
  selectedImageFile = null
}

const validateForm = () => {
  let isValid = true
  errors.title = ''
  errors.category = ''
  errors.foundLocation = ''
  errors.foundTime = ''
  errors.description = ''
  errors.status = ''

  if (!form.title.trim()) {
    errors.title = '请输入物品名称'
    isValid = false
  }
  if (!form.category) {
    errors.category = '请选择物品分类'
    isValid = false
  }
  if (!form.foundLocation.trim()) {
    errors.foundLocation = '请输入拾获地点'
    isValid = false
  }
  if (!form.foundTime) {
    errors.foundTime = '请选择拾获时间'
    isValid = false
  }
  if (!form.description.trim() || form.description.length < 10) {
    errors.description = '详细描述至少需要 10 个字符'
    isValid = false
  }
  if (!form.status) {
    errors.status = '请选择物品状态'
    isValid = false
  }

  return isValid
}

const submitForm = async () => {
  if (!isOwner.value) {
    ElMessage.error('无权限操作')
    return
  }

  if (!validateForm()) {
    ElMessage.warning('请填写完整且正确的信息')
    const firstError = document.querySelector('.border-error')
    if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' })
    return
  }

  submitting.value = true

  try {
    let finalImageUrl = form.images // 默认使用原来的图片路径

    // 如果用户选择了新图片，则先上传
    if (selectedImageFile) {
      const uploadRes = await uploadImage(selectedImageFile)
      if (uploadRes.code === 200) {
        finalImageUrl = uploadRes.data
      } else {
        ElMessage.error(uploadRes.message || '图片上传失败')
        submitting.value = false
        return
      }
    }

    const itemId = route.params.id

    const submitData = {
      id: itemId,
      title: form.title,
      category: form.category,
      foundLocation: form.foundLocation,
      foundTime: form.foundTime,
      storageLocation: form.storageLocation,
      description: form.description,
      contactInfo: form.contactInfo,
      status: form.status,
      userId: form.userId,
      images: finalImageUrl   // 关键：把最终图片路径传给后端
    }

    const response = await updateFoundItem(itemId, submitData)

    if (response.success) {
      ElMessage.success('修改成功')
      router.push('/user/found')
    } else {
      ElMessage.error(response.error || '修改失败')
    }
  } catch (error) {
    console.error('修改失败:', error)
    ElMessage.error('修改失败，请重试')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/user/found')
}
</script>

<style scoped>
/* 样式保持不变 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-primary { background-color: #6750A4; }
.bg-secondary { background-color: #625B71; }
.bg-secondary-container { background-color: #E8DEF8; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-error { color: #B3261E; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-error { border-color: #B3261E !important; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }

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

input[type="datetime-local"]::-webkit-calendar-picker-indicator {
  filter: invert(0.5);
  cursor: pointer;
}
</style>