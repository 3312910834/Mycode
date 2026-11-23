<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Loading Overlay -->
    <div v-if="loading" class="fixed inset-0 z-[60] flex items-center justify-center bg-surface/80 backdrop-blur-sm">
      <div class="flex flex-col items-center gap-4">
        <span class="material-symbols-outlined text-5xl text-primary animate-spin">progress_activity</span>
        <p class="text-on-surface-variant font-medium">正在处理...</p>
      </div>
    </div>

    <main class="max-w-6xl mx-auto px-4 md:px-6 py-8">

      <!-- Header -->
      <header class="mb-8">
        <button @click="$router.push('/user/found')" class="flex items-center gap-2 text-on-surface-variant hover:text-primary mb-4 transition-colors font-medium">
          <span class="material-symbols-outlined">arrow_back</span>
          返回列表
        </button>
        <div class="bg-surface-container-low rounded-3xl p-8 border border-surface-container-high">
          <div class="inline-flex items-center justify-center w-14 h-14 bg-secondary-container text-secondary rounded-2xl mb-4 shadow-lg shadow-secondary/10">
            <span class="material-symbols-outlined text-3xl" style="font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 48;">find_in_page</span>
          </div>
          <h1 class="text-3xl font-black text-on-surface font-headline mb-2">发布招领信息</h1>
          <p class="text-on-surface-variant font-medium">详细描述拾获物品信息，帮助失主找回</p>
        </div>
      </header>

      <!-- Main Content -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start">
        <!-- Left: Upload + AI（重点演示模块） -->
        <section class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm lg:sticky lg:top-24">
          <div class="flex items-center justify-between gap-4">
            <h3 class="text-lg font-black text-on-surface font-headline flex items-center gap-2">
              <span class="material-symbols-outlined text-secondary">add_photo_alternate</span>
              图片上传与AI识别
            </h3>
            <span class="px-3 py-1 rounded-full text-xs font-black tracking-wider bg-secondary-container text-on-secondary-container border border-secondary/10">
              AI一键发布
            </span>
          </div>

          <p class="text-sm text-on-surface-variant mt-2 leading-relaxed">
            上传拾获物品图片后，点击“AI自动识别”，系统将自动识别并回填标题、分类、描述等信息。
          </p>

          <!-- Dropzone -->
          <div
            class="mt-6 rounded-3xl border-2 border-dashed bg-surface overflow-hidden transition-all"
            :class="isDragging ? 'border-primary bg-primary-container/30' : 'border-outline-variant/60 hover:border-primary/60 hover:bg-surface-container-lowest'"
            @dragenter.prevent="onDragEnter"
            @dragover.prevent="onDragOver"
            @dragleave.prevent="onDragLeave"
            @drop.prevent="onDrop"
            @click="triggerFilePick"
            role="button"
            tabindex="0"
          >
            <div v-if="!form.imagePreview" class="p-10 text-center cursor-pointer">
              <div class="mx-auto w-16 h-16 rounded-3xl bg-secondary-container text-secondary flex items-center justify-center border border-secondary/10">
                <span class="material-symbols-outlined text-3xl">cloud_upload</span>
              </div>
              <div class="mt-4 text-base font-black text-on-surface">拖拽图片到这里</div>
              <div class="mt-1 text-sm text-on-surface-variant">或点击选择图片（支持 jpg/png/webp，建议 &lt; 5MB）</div>
            </div>

            <div v-else class="relative">
              <img :src="form.imagePreview" alt="预览图" class="w-full aspect-[4/3] object-cover" />
              <div class="absolute inset-0 bg-gradient-to-t from-black/55 via-black/0 to-black/0"></div>
              <div class="absolute bottom-4 left-4 right-4 flex items-end justify-between gap-3">
                <div class="min-w-0">
                  <div class="text-white text-sm font-black line-clamp-1">已上传图片</div>
                  <div class="text-white/80 text-xs mt-1">点击可重新选择 / 拖拽可替换</div>
                </div>
                <button
                  type="button"
                  class="shrink-0 px-4 py-2 rounded-2xl bg-white/15 text-white text-sm font-black border border-white/20 hover:bg-white/20 transition-colors"
                  @click.stop="clearImage"
                >
                  清除
                </button>
              </div>
            </div>
          </div>

          <input ref="fileInputRef" type="file" class="hidden" accept="image/*" @change="handleImageChange" />

          <!-- Image URL (Optional) -->
          <div class="mt-6 space-y-2">
            <label class="block text-xs font-black text-on-surface-variant uppercase tracking-wide">图片URL（可选）</label>
            <input
              v-model="form.imageUrl"
              type="text"
              placeholder="粘贴可访问图片地址，或上传后自动填入"
              class="w-full px-4 py-3 bg-surface text-on-surface rounded-2xl border border-surface-container-high outline-none focus:border-primary transition-colors"
            />
          </div>

          <!-- AI Action -->
          <div class="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-3">
            <button
              type="button"
              :disabled="aiFilling"
              @click="handleAiAutoFill"
              class="px-5 py-4 rounded-2xl bg-primary text-on-primary font-black shadow-lg shadow-primary/20 hover:shadow-xl hover:shadow-primary/25 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 disabled:opacity-60 disabled:transform-none flex items-center justify-center gap-2"
            >
              <span v-if="aiFilling" class="material-symbols-outlined animate-spin text-base">progress_activity</span>
              <span v-else class="material-symbols-outlined text-base" style="font-variation-settings: 'FILL' 1, 'wght' 600, 'GRAD' 0, 'opsz' 48;">auto_awesome</span>
              {{ aiFilling ? 'AI识别中...' : 'AI自动识别' }}
            </button>
            <button
              type="button"
              :disabled="aiTitleFilling"
              @click="handleAiFillByTitle"
              class="px-5 py-4 rounded-2xl bg-secondary-container text-on-secondary-container font-black border border-secondary/10 hover:bg-secondary-container/80 transition-colors disabled:opacity-60 flex items-center justify-center gap-2"
            >
              <span v-if="aiTitleFilling" class="material-symbols-outlined animate-spin text-base">progress_activity</span>
              <span v-else class="material-symbols-outlined text-base">magic_button</span>
              AI补全描述
            </button>
          </div>

          <div class="mt-4 p-4 rounded-2xl bg-surface border border-surface-container-high">
            <div class="flex items-start gap-3">
              <span class="material-symbols-outlined text-secondary shrink-0">tips_and_updates</span>
              <div class="text-sm text-on-surface-variant leading-relaxed">
                AI 自动填充仅作为建议，不会强制覆盖你已填写的内容；被AI填入的字段会出现蓝色边框与“AI已填写”提示。
              </div>
            </div>
          </div>
        </section>

        <!-- Right: Form -->
        <section class="bg-surface-container-low rounded-3xl p-6 md:p-10 border border-surface-container-high shadow-sm">
          <!-- AI Hint Bar -->
          <div
            v-if="aiFilledCount > 0"
            class="mb-8 p-4 rounded-2xl border border-primary/15 bg-primary-container/40 text-on-primary-container flex items-start gap-3"
          >
            <span class="material-symbols-outlined text-primary shrink-0">auto_awesome</span>
            <div class="text-sm">
              <p class="font-black">AI已识别出物品信息</p>
              <p class="mt-1 text-on-surface-variant">已自动填充 {{ aiFilledCount }} 个字段，请确认无误后发布。</p>
            </div>
          </div>

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
                <span v-if="isAiFilled('title')" class="ml-2 inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-primary/10 text-primary text-xs font-black">
                  <span class="material-symbols-outlined text-sm">smart_toy</span>
                  AI已填写
                </span>
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
                  :class="[{ 'border-error': errors.title }, aiFieldClass('title')]"
                />
              </div>
              <p v-if="errors.title" class="text-xs text-error font-medium ml-1">{{ errors.title }}</p>
            </div>

            <!-- Category -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                物品分类 <span class="text-error">*</span>
                <span v-if="isAiFilled('category')" class="ml-2 inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-primary/10 text-primary text-xs font-black">
                  <span class="material-symbols-outlined text-sm">smart_toy</span>
                  AI已填写
                </span>
              </label>
              <div class="relative group">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors pointer-events-none">
                  <span class="material-symbols-outlined">folder</span>
                </span>
                <select
                  v-model="form.category"
                  @change="handleCategoryTouched"
                  class="w-full pl-12 pr-10 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none appearance-none cursor-pointer font-medium transition-all duration-300"
                  :class="[{ 'border-error': errors.category }, aiFieldClass('category')]"
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
                  placeholder="如：图书馆三楼阅览室"
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
                  placeholder="如：宿舍楼 XXX 室（可选）"
                  class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-secondary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                />
              </div>
            </div>
          </div>

          <!-- Section: Details -->
          <div class="space-y-6">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2 pb-2 border-b border-surface-container-high">
              <span class="material-symbols-outlined text-primary">description</span>
              详细描述
            </h3>

            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                描述内容 <span class="text-error">*</span>
                <span v-if="isAiFilled('description')" class="ml-2 inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-primary/10 text-primary text-xs font-black">
                  <span class="material-symbols-outlined text-sm">smart_toy</span>
                  AI已填写
                </span>
              </label>
              <textarea
                v-model="form.description"
                rows="5"
                placeholder="请详细描述物品特征、品牌、颜色、大小、内含物品等信息..."
                class="w-full p-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 resize-none"
                :class="[{ 'border-error': errors.description }, aiFieldClass('description')]"
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
          </div>

          <!-- Section: Contact -->
          <div class="space-y-6">
            <h3 class="text-lg font-bold text-on-surface font-headline flex items-center gap-2 pb-2 border-b border-surface-container-high">
              <span class="material-symbols-outlined text-secondary">contact_phone</span>
              联系方式
            </h3>

            <div class="space-y-2">
              <label class="block text-sm font-bold text-on-surface-variant uppercase tracking-wide">
                联系信息
              </label>
              <div class="relative group">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-secondary transition-colors">
                  <span class="material-symbols-outlined">mail</span>
                </span>
                <input
                  v-model="form.contactInfo"
                  type="text"
                  placeholder="电话、微信或 QQ（留空则使用账户默认联系方式）"
                  class="w-full pl-12 pr-4 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-secondary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50"
                />
              </div>
              <div class="bg-secondary-container/20 border border-secondary-container/30 rounded-xl p-3 flex items-start gap-3">
                <span class="material-symbols-outlined text-secondary text-sm mt-0.5">info</span>
                <p class="text-xs text-secondary font-medium leading-relaxed">
                  系统将默认使用您的账户联系方式。如需使用其他联系方式（如备用手机号），请在此填写。
                </p>
              </div>
            </div>
          </div>

          <!-- 图片上传已移动至左侧演示区 -->

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
              @click="$router.push('/user/found?refresh=1')"
              class="flex-1 sm:flex-none px-8 py-4 bg-surface text-on-surface-variant border border-surface-container-high rounded-2xl font-bold hover:bg-surface-container-high transition-colors"
            >
              取消
            </button>
          </div>

          </form>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
// 【修改1：引入 uploadImage 接口】
import { createFoundItem, uploadImage, autoFillFoundByImage } from '@/api/found'
import { useItemCategories } from '@/composables/useItemCategories'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const { categories, loadCategories } = useItemCategories()

const formRef = ref()
const loading = ref(false)
const aiFilling = ref(false)
const aiTitleFilling = ref(false)
const categoryTouched = ref(false)
const isDragging = ref(false)
const fileInputRef = ref(null)

// 【修改2：增加存放真实图片文件的变量】
let selectedImageFile = null

const aiFilledAt = reactive({
  title: 0,
  category: 0,
  description: 0
})

const aiFilledCount = computed(() => Object.values(aiFilledAt).filter((t) => Date.now() - t < 10 * 60 * 1000).length)

const markAiFilled = (field) => {
  aiFilledAt[field] = Date.now()
}

const isAiFilled = (field) => Date.now() - Number(aiFilledAt[field] || 0) < 10 * 60 * 1000
const isAiFlash = (field) => Date.now() - Number(aiFilledAt[field] || 0) < 2200
const aiFieldClass = (field) => (isAiFlash(field) ? 'ai-flash' : (isAiFilled(field) ? 'ai-filled' : ''))

const form = reactive({
  title: '',
  category: 'others',
  foundLocation: '',
  foundTime: '',
  storageLocation: '',
  description: '',
  contactInfo: '',
  imageUrl: '',
  imagePreview: '',
  imageBase64: ''
})

const errors = reactive({
  title: '',
  category: '',
  foundLocation: '',
  foundTime: '',
  description: ''
})

const initTime = () => {
  const now = new Date()
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset())
  form.foundTime = now.toISOString().slice(0, 16)
}

initTime()

onMounted(() => {
  loadCategories()
})

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

const buildFoundDescriptionByTitle = () => {
  const title = form.title?.trim()
  const category = form.category
  const loc = form.foundLocation?.trim() || '（拾获地点）'
  const time = form.foundTime ? formatLocalDateTime(form.foundTime) : '（拾获时间）'

  const categorySpecific = (() => {
    switch (category) {
      case 'cards': return '内含银行卡/校园卡/身份证等（如有请补充：卡的数量与特征、姓名或证件号后几位）'
      case 'electronics': return '外观特征（颜色/型号/是否有保护壳）+ 可能伴随配件（如数据线/充电器/耳机盒）'
      case 'books': return '书名/版本/封面颜色 + 是否有书签/笔记 + 关键标识（如姓名、图案）'
      case 'study': return '书包/资料的外形（颜色/品牌/尺寸）+ 内含物品清单（教材、笔记、文件等）'
      case 'clothing': return '关键特征（材质/颜色/图案/明显磨损或配件）+ 是否带钥匙扣/挂件等'
      default: return '颜色/材质/尺寸 + 明显标识或独特特征 + 内含物品（如有）'
    }
  })()

  return `我在${loc}于${time}拾获“${title}”。\n物品特征：${categorySpecific}。\n如失主可提供对应信息（例如描述中的特征/照片），我将协助核对并归还。`
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
    const guessed = guessCategoryByTitle(title)
    if (!categoryTouched.value) {
      form.category = guessed || 'others'
      markAiFilled('category')
    }

    if (!form.description?.trim() || form.description.trim().length < 10) {
      form.description = buildFoundDescriptionByTitle()
      markAiFilled('description')
    }

    ElMessage.success('已根据物品名称生成分类与描述，可继续编辑')
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

const validateForm = () => {
  let isValid = true
  errors.title = ''
  errors.category = ''
  errors.foundLocation = ''
  errors.foundTime = ''
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

  if (!form.foundLocation.trim()) {
    errors.foundLocation = '请输入拾获地点'
    isValid = false
  }

  if (!form.foundTime) {
    errors.foundTime = '请选择拾获时间'
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

// 【修改3：真正结合上传逻辑的表单提交】
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

    let finalImageUrl = ''
    
    // 若选择了真实的图片，先请求图片上传接口
    if (selectedImageFile) {
      try {
        const uploadRes = await uploadImage(selectedImageFile)
        if (uploadRes.code === 200) {
          finalImageUrl = uploadRes.data // 拿到后端的虚拟路径 /uploads/xxx.webp
        } else {
          ElMessage.error(uploadRes.message || '图片上传失败')
          loading.value = false
          return 
        }
      } catch (err) {
        console.error('上传出错', err)
        ElMessage.error('图片上传服务异常')
        loading.value = false
        return
      }
    }

    const formattedTime = formatTimeForBackend(form.foundTime)

    // 构建传递给后端的发布数据
    const submitData = {
      userId: userId,
      title: form.title,
      category: form.category,
      foundLocation: form.foundLocation,
      foundTime: formattedTime,
      storageLocation: form.storageLocation || '',
      description: form.description,
      contactInfo: form.contactInfo || userStore.userInfo?.phone || '',
      images: finalImageUrl // 将服务器拿到的真实 URL 赋值给 images
    }

    console.log('发布数据:', submitData)

    const response = await createFoundItem(submitData)
    console.log('发布响应:', response)

    if (response.success) {
      ElMessage.success('发布成功！')
      resetForm()
      setTimeout(() => {
        router.push('/user/found?refresh=1')
      }, 1500)
    } else if (response.error) {
      ElMessage.error(response.error)
    } else {
      ElMessage.error('发布失败，请重试')
    }
  } catch (error) {
    console.error('发布失败:', error)
    if (error.message) {
      try {
        const errorObj = JSON.parse(error.message)
        if (errorObj.error) {
          ElMessage.error(errorObj.error)
        } else {
          ElMessage.error('发布失败，请重试')
        }
      } catch (e) {
        ElMessage.error(error.message || '发布失败，请重试')
      }
    } else {
      ElMessage.error('发布失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

const handleAiAutoFill = async () => {
  const imageUrl = (form.imageUrl || form.imagePreview || '').trim()
  if (!imageUrl) {
    ElMessage.warning('请先提供图片URL或上传图片')
    return
  }

  aiFilling.value = true
  try {
    const response = await autoFillFoundByImage(imageUrl)
    if (response?.code === 200 && response?.data) {
      if (!form.title?.trim() && response.data.title) {
        form.title = response.data.title || ''
        markAiFilled('title')
      }
      if (!categoryTouched.value && response.data.category) {
        form.category = mapAiCategory(response.data.category)
        markAiFilled('category')
      }
      if (!form.description?.trim() && response.data.description) {
        form.description = response.data.description || ''
        markAiFilled('description')
      }
      if (response.data.error) {
        ElMessage.warning('AI识别失败，请手动补充信息')
      } else {
        ElMessage.success('AI识别成功，已自动填充可用字段')
      }
    } else {
      ElMessage.warning(response?.message || 'AI识别失败，请手动填写')
    }
  } catch (error) {
    console.error('AI识别失败:', error)
    ElMessage.error('AI识别请求失败，请稍后重试')
  } finally {
    aiFilling.value = false
  }
}

// 【修改4：重写图片选择与清除逻辑】
const handleImageChange = (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  // 图片体积放宽到 5MB
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    return
  }

  // 存下真实文件，用于提交时物理上传
  selectedImageFile = file

  const reader = new FileReader()
  reader.onload = (e) => {
    // 预览使用高性能本地 blob 路径
    form.imagePreview = URL.createObjectURL(file) 
    // AI 识别仍然需要 base64
    form.imageBase64 = e.target?.result || ''
    if (!form.imageUrl) form.imageUrl = form.imageBase64
  }
  reader.readAsDataURL(file)
  
  event.target.value = ''
}

const clearImage = () => {
  if (form.imagePreview && form.imagePreview.startsWith('blob:')) {
    URL.revokeObjectURL(form.imagePreview)
  }
  form.imagePreview = ''
  form.imageBase64 = ''
  form.imageUrl = ''
  selectedImageFile = null
}

const resetForm = () => {
  form.title = ''
  form.category = 'others'
  form.foundLocation = ''
  form.storageLocation = ''
  form.description = ''
  form.contactInfo = ''
  clearImage()
  categoryTouched.value = false
  initTime()

  Object.keys(errors).forEach(key => errors[key] = '')
  ElMessage.info('表单已重置')
}

const triggerFilePick = () => fileInputRef.value?.click()
const onDragEnter = () => { isDragging.value = true }
const onDragOver = () => { isDragging.value = true }
const onDragLeave = () => { isDragging.value = false }
const onDrop = (e) => {
  isDragging.value = false
  const file = e.dataTransfer?.files?.[0]
  if (!file) return
  handleImageChange({ target: { files: [file], value: '' } })
}

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
</script>

<style scoped>
/* Fallback Colors for MD3 */
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-primary { background-color: #1677ff; }
.bg-secondary { background-color: #06b6d4; }
.bg-secondary-container { background-color: #cffafe; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #1677ff; }
.text-secondary { color: #06b6d4; }
.text-error { color: #B3261E; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-error { border-color: #B3261E !important; }
.border-secondary-container\/30 { border-color: rgba(232, 222, 248, 0.3); }

.shadow-primary\/30 { --tw-shadow-color: rgba(22, 119, 255, 0.3); }
.shadow-primary\/40 { --tw-shadow-color: rgba(22, 119, 255, 0.4); }
.shadow-secondary\/10 { --tw-shadow-color: rgba(98, 91, 113, 0.1); }

.bg-secondary-container\/20 { background-color: rgba(232, 222, 248, 0.2); }

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

/* AI 自动填充反馈（短暂高亮 + 蓝色边框） */
.ai-filled {
  border-color: rgba(22, 119, 255, 0.45) !important;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.10);
}
.ai-flash {
  border-color: rgba(22, 119, 255, 0.85) !important;
  box-shadow: 0 0 0 4px rgba(22, 119, 255, 0.16);
  background: rgba(219, 234, 254, 0.35) !important;
  transition: box-shadow 220ms ease, border-color 220ms ease, background 220ms ease;
}
</style>