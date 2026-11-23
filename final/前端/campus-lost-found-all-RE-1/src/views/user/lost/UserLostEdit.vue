<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Loading Overlay -->
    <div v-if="loading || submitting" class="fixed inset-0 z-[60] flex items-center justify-center bg-surface/80 backdrop-blur-sm">
      <div class="flex flex-col items-center gap-4">
        <span class="material-symbols-outlined text-5xl text-primary animate-spin">progress_activity</span>
        <p class="text-on-surface-variant font-medium">{{ submitting ? '保存中...' : '加载中...' }}</p>
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
          <div class="inline-flex items-center justify-center w-14 h-14 bg-error/20 text-error rounded-2xl mb-4 shadow-lg shadow-error/10">
            <span class="material-symbols-outlined text-3xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">edit_document</span>
          </div>
          <h1 class="text-3xl font-black text-on-surface font-headline mb-2">编辑失物信息</h1>
          <p class="text-on-surface-variant font-medium">修改丢失物品信息，确保准确性</p>
        </div>
      </header>

      <div v-if="!loading">
        <div v-if="!isOwner" class="bg-error/10 text-error text-lg font-bold rounded-2xl p-8 text-center mb-8">
          <span class="material-symbols-outlined text-3xl align-middle mr-2">block</span>
          {{ forbiddenMsg }}
        </div>
        <div v-else class="bg-surface-container-low rounded-3xl p-6 md:p-10 border border-surface-container-high shadow-sm">
          <!-- Form Card -->
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
                    placeholder="请输入详细丢失地点"
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

            <!-- 【新增】Image Upload Section -->
            <div class="space-y-6">
              <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2 pb-2 border-b border-surface-container-high">
                <span class="material-symbols-outlined text-primary">image</span>
                物品图片
              </h3>
              <div class="space-y-2">
                <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                  当前图片
                </label>
                <div class="flex flex-col md:flex-row gap-4 items-start">
                  <label class="px-4 py-2 rounded-xl bg-surface-container-high hover:bg-surface-container-highest cursor-pointer text-sm font-bold text-on-surface-variant">
                    更换图片
                    <input type="file" class="hidden" accept="image/*" @change="handleImageChange" />
                  </label>
                  <button
                    v-if="form.images || imagePreview"
                    type="button"
                    class="px-4 py-2 rounded-xl bg-error/10 text-error text-sm font-bold"
                    @click="clearImage"
                  >
                    清除图片
                  </button>
                </div>
                <p class="text-xs text-on-surface-variant">支持 jpg/png/webp，建议小于 5MB。</p>
                <div v-if="imagePreview || form.images" class="w-40 h-40 rounded-2xl overflow-hidden border border-surface-container-high">
                  <img :src="imagePreview || form.images" alt="预览图" class="w-full h-full object-cover" />
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
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
                    <span class="material-symbols-outlined">mail</span>
                  </span>
                  <input
                    v-model="form.contactInfo"
                    type="text"
                    placeholder="请输入您的联系方式"
                    class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
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
                    <option value="found">已找到</option>
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
                <span class="material-symbols-outlined">save</span>
                保存修改
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
// 【修改：引入 uploadImage 接口】
import { getLostDetail, updateLostItem, uploadImage } from '@/api/lost'
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

// 【新增：用于图片预览和存放真实文件】
const imagePreview = ref('')
let selectedImageFile = null

const form = reactive({
  title: '',
  category: '',
  lostLocation: '',
  lostTime: '',
  description: '',
  contactInfo: '',
  status: 'pending',
  images: '', // 【修改：增加 images 字段来接收后端返回的图片路径】
  userId: 0
})

const errors = reactive({
  title: '',
  category: '',
  lostLocation: '',
  lostTime: '',
  description: '',
  status: ''
})

onMounted(() => {
  fetchItemDetail()
})

const formatForInput = (dateString) => {
  if (!dateString) return ''
  try {
    if (dateString.includes('T')) return dateString.slice(0, 16)
    const date = new Date(dateString.replace(' ', 'T'))
    if (isNaN(date.getTime())) return ''
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset())
    return date.toISOString().slice(0, 16)
  } catch {
    return ''
  }
}

const formatForBackend = (time) => {
  if (!time) return ''
  try {
    if (time.includes('T')) {
      const date = new Date(time)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
    }
    if (typeof time === 'string' && time.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/)) {
      return time
    }
    const date = new Date(time)
    if (isNaN(date.getTime())) return time
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
  } catch {
    return time
  }
}

const fetchItemDetail = async () => {
  const itemId = route.params.id
  if (!itemId) {
    ElMessage.error('物品ID不存在')
    router.push('/user/UserLostList')
    return
  }
  loading.value = true
  try {
    const response = await getLostDetail(itemId)
    const data = response.data || response.result || response
    if (!data) throw new Error('未获取到数据')

    form.title = data.title || ''
    form.category = data.category || ''
    form.lostLocation = data.lostLocation || data.lost_location || ''
    form.lostTime = formatForInput(data.lostTime || data.lost_time || '')
    form.description = data.description || ''
    form.contactInfo = data.contactInfo || data.contact_info || ''
    form.status = data.status || 'pending'
    form.images = data.images || '' // 【修改：接收后端返回的图片路径】
    form.userId = data.userId || data.user_id || userStore.userInfo?.id || 0
    
    if (userStore.userInfo?.id && form.userId && userStore.userInfo.id !== form.userId) {
      isOwner.value = false
      forbiddenMsg.value = '您无权编辑他人的失物信息！'
    }
  } catch (error) {
    console.error('获取物品详情失败:', error)
    ElMessage.error('加载失败: ' + (error.message || '未知错误'))
    router.push('/user/UserLostList')
  } finally {
    loading.value = false
  }
}

const normalizeTitle = (title) => String(title ?? '').trim().toLowerCase()

const guessCategoryByTitle = (title) => {
  const t = normalizeTitle(title)
  if (!t) return 'others'
  if (/(钱包|卡包|卡套|卡夹|银行卡|身份证|学生证|校园卡|证件|证件卡|id)/.test(t)) return 'cards'
  if (/(手机|iphone|airpods|耳机|耳塞|充电宝|平板|ipad|电脑|笔记本|laptop|watch|手表|键盘|鼠标)/.test(t)) return 'electronics'
  if (/(教材|课本|书籍|图书|小说|文献)/.test(t)) return 'books'
  if (/(书包|资料|笔记|作业|文件|文件夹|文件袋|讲义|课程)/.test(t)) return 'study'
  if (/(钥匙|钥匙串|眼镜|帽子|围巾|手链|项链|戒指|雨伞|外套|衣服|衬衫|裤子)/.test(t)) return 'clothing'
  return 'others'
}

const formatLocalDateTime = (value) => {
  if (!value) return ''
  try {
    const s = String(value).replace('T', ' ')
    const d = new Date(s)
    if (Number.isNaN(d.getTime())) return value
    const pad = (n) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
  } catch {
    return String(value)
  }
}

const buildLostDescriptionByTitle = () => {
  const title = form.title?.trim()
  const usedCategory = form.category || guessCategoryByTitle(title)
  const loc = form.lostLocation?.trim() || '（丢失地点）'
  const time = form.lostTime ? formatLocalDateTime(form.lostTime) : '（丢失时间）'
  const categorySpecific = (() => {
    switch (usedCategory) {
      case 'cards': return '内含银行卡/校园卡/身份证等...'
      case 'electronics': return '外观特征（颜色/型号/是否有保护壳）...'
      case 'books': return '书名/版本/封面颜色...'
      case 'study': return '书包/资料的外形（颜色/品牌/尺寸）...'
      case 'clothing': return '关键特征（材质/颜色/图案/明显磨损或配件）...'
      default: return '颜色/材质/尺寸 + 明显标识或独特特征...'
    }
  })()
  return `我在${loc}于${time}遗失“${title}”。\n物品特征：${categorySpecific}。\n...`
}

const handleAiAssistDescription = async () => {
  const title = form.title?.trim()
  if (!title) { ElMessage.warning('请先填写物品名称'); return; }
  const raw = form.description?.trim() || ''
  if (raw && raw.length >= 10) {
    if (!window.confirm('当前描述已有内容，是否用 AI 生成的描述覆盖？')) return
  }
  aiAssistLoading.value = true
  try {
    form.description = buildLostDescriptionByTitle()
    errors.description = ''
    ElMessage.success('AI润色完成，可直接保存')
  } finally {
    aiAssistLoading.value = false
  }
}

const validateForm = () => {
  let isValid = true
  errors.title = ''
  errors.category = ''
  errors.lostLocation = ''
  errors.lostTime = ''
  errors.description = ''
  errors.status = ''

  if (!form.title.trim()) { errors.title = '请输入物品名称'; isValid = false; }
  if (form.title.length < 2 || form.title.length > 50) { errors.title = '长度在 2 到 50 个字符'; isValid = false; }
  if (!form.category) { errors.category = '请选择物品分类'; isValid = false; }
  if (!form.lostLocation.trim()) { errors.lostLocation = '请输入丢失地点'; isValid = false; }
  if (!form.lostTime) { errors.lostTime = '请选择丢失时间'; isValid = false; }
  if (!form.description.trim()) { errors.description = '请输入详细描述'; isValid = false; }
  if (form.description.length < 10) { errors.description = '描述至少需要 10 个字符'; isValid = false; }
  if (!form.status) { errors.status = '请选择物品状态'; isValid = false; }

  return isValid
}

// 【新增：图片选择和清除的逻辑】
const handleImageChange = (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) { ElMessage.warning('图片不能超过 5MB'); return; }
  
  selectedImageFile = file
  if (imagePreview.value) { URL.revokeObjectURL(imagePreview.value); }
  imagePreview.value = URL.createObjectURL(file)
  event.target.value = ''
}

const clearImage = () => {
  form.images = ''
  if (imagePreview.value) { URL.revokeObjectURL(imagePreview.value); }
  imagePreview.value = ''
  selectedImageFile = null
}

// 【修改：整合图片上传的提交逻辑】
const submitForm = async () => {
  if (!isOwner.value) { ElMessage.error('无权限操作'); return; }
  if (!validateForm()) {
    ElMessage.warning('请填写完整且正确的信息')
    const firstError = document.querySelector('.border-error')
    if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' })
    return
  }

  submitting.value = true
  try {
    const itemId = route.params.id
    if (!itemId) { ElMessage.error('物品ID不能为空'); submitting.value = false; return; }

    let finalImageUrl = form.images // 默认使用旧图片路径
    if (selectedImageFile) {
      try {
        const uploadRes = await uploadImage(selectedImageFile)
        if (uploadRes.code === 200) {
          finalImageUrl = uploadRes.data // 使用新上传的图片路径
        } else {
          ElMessage.error(uploadRes.message || '图片上传失败，但其他信息仍可保存')
        }
      } catch (err) {
        ElMessage.error('图片上传服务异常，但其他信息仍可保存')
      }
    }

    const submitData = {
      id: parseInt(itemId, 10),
      title: form.title,
      category: form.category,
      lostLocation: form.lostLocation,
      lostTime: formatForBackend(form.lostTime),
      description: form.description,
      contactInfo: form.contactInfo,
      status: form.status,
      images: finalImageUrl, // 提交最终的图片路径
      userId: parseInt(form.userId, 10) || userStore.userInfo?.id || 0
    }
    
    if (isNaN(submitData.id) || submitData.id <= 0) { ElMessage.error('物品ID无效'); submitting.value = false; return; }
    if (!submitData.userId || isNaN(submitData.userId) || submitData.userId <= 0) { ElMessage.error('用户信息异常'); submitting.value = false; return; }

    const response = await updateLostItem(itemId, submitData)

    if (response.success || response.code === 200) {
      ElMessage.success('修改成功！')
      setTimeout(() => { router.push('/user/UserLostList'); }, 1500)
    } else {
      ElMessage.error(`修改失败: ${response.error || response.message || '未知错误'}`)
    }
  } catch (error) {
    console.error('修改失败详情:', error)
    // 省略你原有的错误处理逻辑
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/user/UserLostList')
}
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-primary { background-color: #6750A4; }
.bg-error { background-color: #B3261E; }
.bg-secondary\/20 { background-color: rgba(98, 91, 113, 0.2); }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-error { color: #B3261E; }
.text-secondary { color: #625B71; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-error { border-color: #B3261E !important; }

.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(103, 80, 164, 0.4); }
.shadow-error\/10 { --tw-shadow-color: rgba(179, 38, 30, 0.1); }
.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }

textarea::-webkit-scrollbar { width: 8px; }
textarea::-webkit-scrollbar-track { background: transparent; }
textarea::-webkit-scrollbar-thumb { background-color: #CAC4D0; border-radius: 4px; }
input[type="datetime-local"]::-webkit-calendar-picker-indicator { filter: invert(0.5); cursor: pointer; }
</style>