<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- Hero Header -->
    <header class="relative bg-surface-container-lowest border-b border-surface-container-high overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-secondary-container/20 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 pointer-events-none"></div>
      <div class="absolute bottom-0 left-0 w-48 h-48 bg-primary-container/20 rounded-full blur-3xl translate-y-1/2 -translate-x-1/2 pointer-events-none"></div>
      <div class="relative max-w-4xl mx-auto px-6 py-12 md:py-16 text-center">
        <h1 class="text-3xl md:text-4xl font-black text-on-surface tracking-tight font-headline mb-2">搜索结果</h1>
        <p v-if="searchKeyword" class="text-lg text-on-surface-variant font-medium">
          关键词：<span class="text-primary font-bold">"{{ searchKeyword }}"</span>
        </p>
      </div>
    </header>

    <main class="max-w-4xl mx-auto px-6 py-8 space-y-8">

      <!-- ============ 主搜索区 ============ -->
      <div class="bg-surface-container-low rounded-3xl border border-surface-container-high shadow-sm overflow-hidden">

        <!-- 主搜索框 -->
        <div class="p-6">
          <div class="relative group">
            <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant group-focus-within:text-primary transition-colors">
              <span class="material-symbols-outlined">search</span>
            </span>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索物品名称、地点、描述..."
              @keyup.enter="handleMainSearch"
              class="w-full pl-12 pr-32 py-4 bg-surface-container-high text-on-surface rounded-2xl border-2 border-transparent focus:border-primary focus:bg-surface outline-none transition-all duration-300 font-medium placeholder:text-on-surface-variant/50 text-lg"
            />
            <button
              @click="handleMainSearch"
              :disabled="loading || aiLoading"
              class="absolute right-2 top-2 bottom-2 px-6 bg-primary text-on-primary rounded-xl font-bold text-sm hover:bg-primary-dim hover:shadow-lg hover:shadow-primary/30 transition-all duration-300 flex items-center gap-2 disabled:opacity-60"
            >
              <span class="material-symbols-outlined text-base">search</span>
              <span class="hidden sm:inline">搜索</span>
            </button>
          </div>
        </div>

        <!-- AI 辅助区（可折叠） -->
        <div class="border-t border-surface-container-high">
          <button
            @click="showAiPanel = !showAiPanel"
            class="w-full px-6 py-3 flex items-center justify-between text-sm font-bold transition-colors"
            :class="showAiPanel ? 'text-secondary bg-secondary/5' : 'text-on-surface-variant hover:text-secondary hover:bg-secondary/5'"
          >
            <span class="flex items-center gap-2">
              <span class="material-symbols-outlined text-base">auto_awesome</span>
              AI 辅助描述找物
              <span class="text-[10px] px-2 py-0.5 rounded-full bg-secondary/15 text-secondary font-bold ml-1">可选</span>
            </span>
            <span class="material-symbols-outlined text-base transition-transform" :class="showAiPanel ? 'rotate-180' : ''">
              expand_more
            </span>
          </button>

          <div v-if="showAiPanel" class="px-6 pb-6 pt-2">
            <p class="text-xs text-on-surface-variant mb-3">
              用自然语言描述丢失物品的特征，AI 将同时在招领库和失物库中智能匹配
            </p>
            <div class="flex gap-3">
              <textarea
                v-model="aiDescription"
                rows="2"
                placeholder="例如：我丢了一个白色苹果无线耳机，在图书馆三楼..."
                class="flex-1 p-3 bg-surface-container-high text-on-surface rounded-xl border border-surface-container-high focus:border-secondary outline-none text-sm resize-none"
                @keyup.ctrl.enter="handleAiSearch"
              ></textarea>
              <button
                @click="handleAiSearch"
                :disabled="aiLoading || !aiDescription.trim()"
                class="px-5 py-3 bg-secondary text-white rounded-xl font-bold text-sm whitespace-nowrap disabled:opacity-50 hover:bg-secondary/90 transition-colors self-end"
              >
                {{ aiLoading ? '匹配中...' : 'AI匹配' }}
              </button>
            </div>
            <p class="text-[11px] text-on-surface-variant mt-2 flex items-center gap-1">
              <span class="material-symbols-outlined text-[11px]">keyboard</span>
              Ctrl + Enter 快捷发送
            </p>
          </div>
        </div>
      </div>

      <!-- ============ 结果标签栏 ============ -->
      <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div class="inline-flex p-1 bg-surface-container-high rounded-2xl border border-surface-container-high">
          <button
            v-for="tab in tabs"
            :key="tab.name"
            @click="activeTab = tab.name"
            class="relative px-5 py-2 rounded-xl text-sm font-bold transition-all duration-300 flex items-center gap-1.5"
            :class="activeTab === tab.name ? 'bg-surface text-on-surface shadow-md' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-highest'"
          >
            <span class="material-symbols-outlined text-sm">{{ tab.icon }}</span>
            <span>{{ tab.label }}</span>
            <span v-if="tab.count > 0" class="text-[11px] opacity-70">({{ tab.count }})</span>
          </button>
        </div>

        <!-- AI子筛选（仅AI标签下显示） -->
        <div v-if="(activeTab === 'all' || activeTab === 'ai') && allAiResults.length > 0"
             class="flex gap-1.5">
          <button
            @click="aiSubFilter = 'all'"
            class="px-3 py-1.5 rounded-lg text-[11px] font-bold transition-all"
            :class="aiSubFilter === 'all' ? 'bg-secondary text-white' : 'bg-surface-container-high text-on-surface-variant'"
          >全部</button>
          <button
            v-if="aiFoundMatches.length > 0"
            @click="aiSubFilter = 'found'"
            class="px-3 py-1.5 rounded-lg text-[11px] font-bold transition-all"
            :class="aiSubFilter === 'found' ? 'bg-success text-white' : 'bg-success/10 text-success'"
          >招领 {{ aiFoundMatches.length }}</button>
          <button
            v-if="aiLostMatches.length > 0"
            @click="aiSubFilter = 'lost'"
            class="px-3 py-1.5 rounded-lg text-[11px] font-bold transition-all"
            :class="aiSubFilter === 'lost' ? 'bg-error text-white' : 'bg-error/10 text-error'"
          >失物 {{ aiLostMatches.length }}</button>
        </div>
      </div>

      <!-- ============ Loading ============ -->
      <div v-if="loading || aiLoading" class="space-y-4">
        <div v-for="i in 3" :key="i" class="bg-surface-container-low rounded-2xl p-5 animate-pulse">
          <div class="flex gap-4">
            <div class="w-20 h-20 bg-surface-container-highest rounded-xl shrink-0"></div>
            <div class="flex-1 space-y-3">
              <div class="h-5 bg-surface-container-highest rounded-lg w-2/3"></div>
              <div class="h-4 bg-surface-container-highest rounded-lg w-full"></div>
              <div class="h-4 bg-surface-container-highest rounded-lg w-1/2"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- ============ Empty ============ -->
      <div v-else-if="!hasResults" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
        <div class="inline-flex items-center justify-center w-20 h-20 bg-surface-container-highest rounded-full mb-5 text-outline-variant">
          <span class="material-symbols-outlined text-4xl">search_off</span>
        </div>
        <h3 class="text-xl font-bold text-on-surface mb-2">暂无搜索结果</h3>
        <p class="text-on-surface-variant text-sm mb-6">尝试更换关键词，或使用 AI 描述功能</p>
        <button
          v-if="!showAiPanel"
          @click="showAiPanel = true"
          class="inline-flex items-center gap-2 px-6 py-3 bg-secondary text-white rounded-xl font-bold text-sm hover:bg-secondary/90 transition-colors"
        >
          <span class="material-symbols-outlined text-base">auto_awesome</span>
          试试 AI 智能匹配
        </button>
      </div>

      <!-- ============ Results ============ -->
      <div v-else class="space-y-8">

        <!-- AI 匹配结果（招领+失物合并） -->
        <div v-if="(activeTab === 'all' || activeTab === 'ai') && filteredAiResults.length > 0">
          <div class="flex items-center gap-2 mb-4">
            <span class="material-symbols-outlined text-secondary text-lg">auto_awesome</span>
            <h3 class="text-lg font-bold text-on-surface">
              AI 智能匹配
              <span class="text-sm font-normal text-on-surface-variant ml-1">({{ filteredAiResults.length }})</span>
            </h3>
          </div>

          <div v-if="aiResolving" class="flex items-center gap-2 text-sm text-secondary mb-4 px-4 py-2.5 bg-secondary/8 rounded-xl">
            <span class="material-symbols-outlined animate-spin text-sm">progress_activity</span>
            正在加载物品详情...
          </div>

          <div class="space-y-3">
            <div
              v-for="(item, idx) in filteredAiResults"
              :key="'ai-' + item._type + '-' + item._id + '-' + idx"
              @click="goToDetail(item)"
              class="group flex gap-4 p-4 rounded-2xl bg-surface-container-low border border-surface-container-high transition-all duration-300 cursor-pointer hover:-translate-y-0.5"
              :class="item._type === 'lost'
                ? 'hover:border-error/30 hover:shadow-lg hover:shadow-error/8'
                : 'hover:border-success/30 hover:shadow-lg hover:shadow-success/8'"
            >
              <!-- 左侧色条 -->
              <div class="w-1 rounded-full shrink-0 self-stretch"
                   :class="item._type === 'lost' ? 'bg-error' : 'bg-success'"></div>

              <!-- 内容 -->
              <div class="flex-1 min-w-0">
                <!-- 第一行：标签 + 相似度 -->
                <div class="flex items-center justify-between mb-2">
                  <div class="flex items-center gap-2 flex-wrap">
                    <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider"
                          :class="item._type === 'lost' ? 'bg-error/15 text-error' : 'bg-success/15 text-success'">
                      {{ item._type === 'lost' ? '失物' : '招领' }}
                    </span>
                    <span v-if="item._similarity != null"
                          class="px-2 py-0.5 rounded-full bg-secondary/10 text-secondary text-[10px] font-bold flex items-center gap-0.5">
                      <span class="material-symbols-outlined text-[10px]">auto_awesome</span>
                      AI {{ formatSimilarity(item._similarity) }}
                    </span>
                    <span v-if="item._source === 'keyword'"
                          class="px-2 py-0.5 rounded-full bg-primary/10 text-primary text-[10px] font-bold">
                      关联
                    </span>
                  </div>
                  <span v-if="item._time" class="text-[11px] text-on-surface-variant flex items-center gap-1 shrink-0">
                    <span class="material-symbols-outlined text-[11px]">schedule</span>
                    {{ formatDate(item._time) }}
                  </span>
                </div>

                <!-- 标题 -->
                <h4 class="font-bold text-on-surface text-base mb-1.5 line-clamp-1 transition-colors"
                    :class="item._type === 'lost' ? 'group-hover:text-error' : 'group-hover:text-success'">
                  {{ item.title || `物品 #${item._id}` }}
                </h4>

                <!-- 地点 + 分类 -->
                <div class="flex flex-wrap gap-x-4 gap-y-1 text-[13px] text-on-surface-variant mb-1.5">
                  <span v-if="item._location" class="flex items-center gap-1">
                    <span class="material-symbols-outlined text-[13px]"
                          :class="item._type === 'lost' ? 'text-error' : 'text-success'">location_on</span>
                    {{ item._location }}
                  </span>
                  <span v-if="item.category" class="flex items-center gap-1">
                    <span class="material-symbols-outlined text-[13px] text-on-surface-variant">folder</span>
                    {{ item.category }}
                  </span>
                </div>

                <!-- 描述 -->
                <p v-if="item.description" class="text-sm text-on-surface-variant/80 line-clamp-2 leading-relaxed mb-1.5">
                  {{ truncateText(item.description, 100) }}
                </p>

                <!-- AI匹配原因 -->
                <div v-if="item._reason" class="mt-2 px-3 py-1.5 rounded-lg bg-secondary/5 border border-secondary/8 inline-block">
                  <p class="text-[11px] text-secondary flex items-start gap-1">
                    <span class="material-symbols-outlined text-[11px] mt-0.5 shrink-0">auto_awesome</span>
                    <span class="leading-relaxed line-clamp-2">{{ item._reason }}</span>
                  </p>
                </div>
              </div>

              <!-- 右侧箭头 -->
              <div class="flex items-center shrink-0 opacity-0 group-hover:opacity-100 transition-opacity">
                <span class="material-symbols-outlined text-lg group-hover:translate-x-1 transition-transform"
                      :class="item._type === 'lost' ? 'text-error' : 'text-success'">
                  arrow_forward
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 普通搜索：失物列表 -->
        <div v-if="(activeTab === 'all' || activeTab === 'lost') && lostItems.length > 0">
          <div class="flex items-center gap-2 mb-4">
            <div class="w-6 h-6 rounded-full bg-error/15 text-error flex items-center justify-center">
              <span class="material-symbols-outlined text-sm">report_gmailerrorred</span>
            </div>
            <h3 class="text-lg font-bold text-on-surface">
              失物信息
              <span class="text-sm font-normal text-on-surface-variant ml-1">({{ lostItems.length }})</span>
            </h3>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="item in lostItems"
              :key="`lost-${item.id}`"
              @click="viewLostDetail(item.id)"
              class="group bg-surface-container-low hover:bg-surface-container-high border border-surface-container-high hover:border-error/30 rounded-2xl p-4 transition-all duration-300 hover:shadow-lg hover:shadow-error/5 hover:-translate-y-0.5 cursor-pointer"
            >
              <div class="flex items-center justify-between mb-2">
                <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase bg-error/15 text-error">失物</span>
                <span class="text-[11px] text-on-surface-variant">{{ formatDate(item.lost_time || item.lostTime) }}</span>
              </div>
              <h4 class="font-bold text-on-surface mb-2 line-clamp-1 group-hover:text-error transition-colors">{{ item.title }}</h4>
              <div class="flex items-center gap-1 text-[13px] text-on-surface-variant mb-1.5">
                <span class="material-symbols-outlined text-[13px] text-error">location_on</span>
                <span class="line-clamp-1">{{ item.lost_location || item.lostLocation }}</span>
              </div>
              <p v-if="item.description" class="text-sm text-on-surface-variant/70 line-clamp-2">{{ truncateText(item.description, 60) }}</p>
            </div>
          </div>
        </div>

        <!-- 普通搜索：招领列表 -->
        <div v-if="(activeTab === 'all' || activeTab === 'found') && foundItems.length > 0">
          <div class="flex items-center gap-2 mb-4">
            <div class="w-6 h-6 rounded-full bg-success/15 text-success flex items-center justify-center">
              <span class="material-symbols-outlined text-sm">check_circle</span>
            </div>
            <h3 class="text-lg font-bold text-on-surface">
              招领信息
              <span class="text-sm font-normal text-on-surface-variant ml-1">({{ foundItems.length }})</span>
            </h3>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="item in foundItems"
              :key="`found-${item.id}`"
              @click="viewFoundDetail(item.id)"
              class="group bg-surface-container-low hover:bg-surface-container-high border border-surface-container-high hover:border-success/30 rounded-2xl p-4 transition-all duration-300 hover:shadow-lg hover:shadow-success/5 hover:-translate-y-0.5 cursor-pointer"
            >
              <div class="flex items-center justify-between mb-2">
                <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase bg-success/15 text-success">招领</span>
                <span class="text-[11px] text-on-surface-variant">{{ formatDate(item.found_time || item.foundTime) }}</span>
              </div>
              <h4 class="font-bold text-on-surface mb-2 line-clamp-1 group-hover:text-success transition-colors">{{ item.title }}</h4>
              <div class="flex items-center gap-1 text-[13px] text-on-surface-variant mb-1.5">
                <span class="material-symbols-outlined text-[13px] text-success">location_on</span>
                <span class="line-clamp-1">{{ item.found_location || item.foundLocation }}</span>
              </div>
              <p v-if="item.description" class="text-sm text-on-surface-variant/70 line-clamp-2">{{ truncateText(item.description, 60) }}</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { searchItems } from '@/api/home'
import { smartMatchLostText, getLostDetail, searchLostItems } from '@/api/lost'
import { getFoundDetail, searchFoundItems } from '@/api/found'

const route = useRoute()
const router = useRouter()

const searchKeyword = ref(route.query.keyword || '')
const activeTab = ref('all')
const loading = ref(false)
const aiLoading = ref(false)
const aiResolving = ref(false)
const showAiPanel = ref(false)
const aiSubFilter = ref('all')
const lostItems = ref([])
const foundItems = ref([])
const aiDescription = ref('')
const aiFoundMatches = ref([])
const aiLostMatches = ref([])

// 合并 AI 结果
const allAiResults = computed(() => [...aiFoundMatches.value, ...aiLostMatches.value])
const filteredAiResults = computed(() => {
  if (aiSubFilter.value === 'found') return aiFoundMatches.value
  if (aiSubFilter.value === 'lost') return aiLostMatches.value
  return allAiResults.value
})

const tabs = computed(() => [
  { name: 'all', label: '全部', icon: 'apps', count: lostItems.value.length + foundItems.value.length + allAiResults.value.length },
  { name: 'ai', label: 'AI匹配', icon: 'auto_awesome', count: allAiResults.value.length },
  { name: 'lost', label: '失物', icon: 'report_gmailerrorred', count: lostItems.value.length },
  { name: 'found', label: '招领', icon: 'check_circle', count: foundItems.value.length }
])

const hasResults = computed(() => {
  return lostItems.value.length > 0 || foundItems.value.length > 0 || allAiResults.value.length > 0
})

onMounted(() => {
  if (searchKeyword.value) handleMainSearch()
  if (route.query.aiText) {
    aiDescription.value = String(route.query.aiText)
    showAiPanel.value = true
    handleAiSearch()
  }
})

// ==================== 主搜索（关键词） ====================
const handleMainSearch = async () => {
  const kw = searchKeyword.value.trim()
  if (!kw) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  loading.value = true
  try {
    const [lostResp, foundResp] = await Promise.all([
      searchItems(kw, 'lost'),
      searchItems(kw, 'found')
    ])
    lostItems.value = lostResp?.code === 200 ? (lostResp.data?.items || lostResp.data?.records || []) : []
    foundItems.value = foundResp?.code === 200 ? (foundResp.data?.items || foundResp.data?.records || []) : []
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
  router.replace({ path: '/search', query: { ...route.query, keyword: kw } })

  // 如果 AI 面板已打开且有描述，同时执行 AI 匹配
  if (showAiPanel.value && aiDescription.value.trim()) {
    handleAiSearch()
  }
}

// ==================== AI 辅助匹配 ====================
const handleAiSearch = async () => {
  const desc = aiDescription.value.trim()
  if (!desc) {
    ElMessage.warning('请输入失物描述')
    return
  }

  aiLoading.value = true
  aiFoundMatches.value = []
  aiLostMatches.value = []
  aiSubFilter.value = 'all'

  try {
    // ① AI 匹配招领库
    const aiResp = await smartMatchLostText(desc)
    console.log('🤖 AI返回:', JSON.stringify(aiResp, null, 2))

    if (aiResp?.code === 200) {
      const raw = aiResp?.data?.matches || []
      aiFoundMatches.value = raw.map(m => ({
        _id: m.id || m.itemId || m.index || null,
        _type: 'found',
        _source: 'ai',
        _similarity: m.similarity,
        _reason: m.reason,
        title: null,
        description: null,
        category: null,
        _location: null,
        _time: null
      })).filter(m => m._id != null)
    }

    // ② 从描述中提取关键词搜索失物库
    const keywords = extractKeywords(desc)
    console.log('🔑 关键词:', keywords)

    const lostMap = new Map()
    for (const kw of keywords) {
      try {
        const resp = await searchLostItems(kw)
        if (resp?.code === 200) {
          const items = resp.data?.items || resp.data?.records || []
          items.forEach(item => {
            if (item.id && !lostMap.has(item.id)) {
              lostMap.set(item.id, {
                _id: item.id,
                _type: 'lost',
                _source: 'keyword',
                _similarity: null,
                _reason: null,
                title: item.title,
                description: item.description,
                category: item.category,
                _location: item.lost_location || item.lostLocation || '',
                _time: item.lost_time || item.lostTime || ''
              })
            }
          })
        }
      } catch (e) { console.warn(`搜 "${kw}" 失败:`, e) }
    }
    aiLostMatches.value = Array.from(lostMap.values())

    // 切到 AI 标签
    if (allAiResults.value.length > 0) {
      activeTab.value = 'ai'
      router.replace({ path: '/search', query: { ...route.query, aiText: desc } })
    } else {
      ElMessage.info('暂未匹配到相关信息')
    }

    // ③ 异步加载招领详情
    if (aiFoundMatches.value.length > 0) await fetchFoundDetails()

  } catch (error) {
    console.error('AI匹配失败:', error)
    ElMessage.error('AI匹配失败')
  } finally {
    aiLoading.value = false
  }
}

// ==================== 提取关键词 ====================
const extractKeywords = (text) => {
  if (!text) return []
  const kws = []
  if (text.length <= 20) kws.push(text)
  const segs = text.split(/[，,。.！!？?、；;\s\n]+/).filter(s => s.length >= 2)
  kws.push(...segs)
  const patterns = [
    /(?:黑色|白色|红色|蓝色|银色|金色|灰色|粉色)?[\u4e00-\u9fa5]{1,4}(?:手机|耳机|电脑|笔记本|钱包|书包|水杯|雨伞|钥匙|眼镜|手表|充电器|U盘|相机|平板)/g,
    /(?:苹果|华为|小米|三星|索尼|联想)[\u4e00-\u9fa5a-zA-Z0-9]*/g,
    /AirPods|iPhone|iPad|MacBook|Galaxy/gi
  ]
  patterns.forEach(p => { const m = text.match(p); if (m) kws.push(...m) })
  return [...new Set(kws.map(k => k.trim()).filter(k => k.length >= 2))].slice(0, 5)
}

// ==================== 加载招领详情 ====================
const fetchFoundDetails = async () => {
  aiResolving.value = true
  try {
    await Promise.all(aiFoundMatches.value.map(async (match) => {
      if (!match._id) return
      try {
        const resp = await getFoundDetail(match._id)
        const d = extractDetail(resp)
        if (d) {
          match.title = d.title
          match.description = d.description
          match.category = d.category
          match._location = d.foundLocation || d.found_location || ''
          match._time = d.foundTime || d.found_time || d.createTime || ''
        } else {
          // 招领没有，尝试失物
          const alt = await getLostDetail(match._id)
          const ad = extractDetail(alt)
          if (ad) {
            match._type = 'lost'
            match.title = ad.title
            match.description = ad.description
            match.category = ad.category
            match._location = ad.lostLocation || ad.lost_location || ''
            match._time = ad.lostTime || ad.lost_time || ''
          }
        }
      } catch (e) {
        try {
          const alt = await getLostDetail(match._id)
          const ad = extractDetail(alt)
          if (ad) {
            match._type = 'lost'
            match.title = ad.title
            match.description = ad.description
            match._location = ad.lostLocation || ad.lost_location || ''
            match._time = ad.lostTime || ad.lost_time || ''
          }
        } catch (e2) { /* ignore */ }
      }
    }))
  } finally {
    aiResolving.value = false
    aiFoundMatches.value = [...aiFoundMatches.value]
  }
}

const extractDetail = (resp) => {
  if (!resp) return null
  if (resp.code === 200 && resp.data) {
    if (resp.data.id || resp.data.title) return resp.data
    if (resp.data.item) return resp.data.item
    return resp.data
  }
  if (resp.code === 200 && resp.item) return resp.item
  if (resp.id && resp.title) return resp
  return null
}

// ==================== 跳转 ====================
const goToDetail = (item) => {
  if (!item._id) { ElMessage.warning('无法定位物品'); return }
  router.push(`/${item._type}/${item._id}`)
}
const viewLostDetail = (id) => router.push(`/lost/${id}`)
const viewFoundDetail = (id) => router.push(`/found/${id}`)

// ==================== 工具函数 ====================
const getSimilarityBarClass = (s) => { const v = Number(s) || 0; return v >= 0.8 ? 'bg-success' : v >= 0.5 ? 'bg-secondary' : 'bg-on-surface-variant' }
const getSimilarityTextClass = (s) => { const v = Number(s) || 0; return v >= 0.8 ? 'text-success' : v >= 0.5 ? 'text-secondary' : 'text-on-surface-variant' }

const formatDate = (ds) => {
  if (!ds) return ''
  try {
    const d = new Date(ds); if (isNaN(d.getTime())) return ds
    const diff = Math.abs(Date.now() - d) / 1000
    if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
    if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
    if (diff < 604800) return `${Math.floor(diff / 86400)}天前`
    return d.toLocaleDateString('zh-CN')
  } catch { return ds }
}

const truncateText = (t, l) => (!t ? '' : t.length > l ? t.substring(0, l) + '...' : t)
const formatSimilarity = (v) => (v == null || isNaN(Number(v))) ? '0%' : `${Math.round(Number(v) * 100)}%`
</script>

<style scoped>
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }
.bg-surface-container-lowest { background-color: #F3EDF7; }
.bg-primary { background-color: #6750A4; }
.bg-secondary { background-color: #625B71; }
.bg-success { background-color: #006E1C; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-success { color: #006E1C; }
.text-error { color: #B3261E; }
.text-outline-variant { color: #CAC4D0; }

.border-surface-container-high { border-color: #ECE6F0; }
.bg-error\/15 { background-color: rgba(179, 38, 30, 0.15); }
.bg-error\/10 { background-color: rgba(179, 38, 30, 0.1); }
.bg-success\/15 { background-color: rgba(0, 110, 28, 0.15); }
.bg-success\/10 { background-color: rgba(0, 110, 28, 0.1); }
.bg-secondary\/10 { background-color: rgba(98, 91, 113, 0.1); }
.bg-secondary\/8 { background-color: rgba(98, 91, 113, 0.08); }
.bg-secondary\/5 { background-color: rgba(98, 91, 113, 0.05); }
.bg-secondary\/15 { background-color: rgba(98, 91, 113, 0.15); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
.bg-secondary-container\/20 { background-color: rgba(232, 222, 248, 0.2); }
.bg-primary-container\/20 { background-color: rgba(234, 221, 255, 0.2); }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-in { animation: fadeIn 0.4s ease-out forwards; }
</style>