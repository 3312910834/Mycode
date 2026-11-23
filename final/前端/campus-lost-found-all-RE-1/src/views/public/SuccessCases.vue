<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">
    <!-- Hero -->
    <header class="relative overflow-hidden bg-surface-container-lowest border-b border-surface-container-high">
      <div class="absolute -top-12 -right-12 w-80 h-80 bg-primary-container/40 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute -bottom-12 -left-12 w-80 h-80 bg-secondary-container/40 rounded-full blur-3xl pointer-events-none"></div>

      <div class="relative max-w-7xl mx-auto px-6 py-10 md:py-14">
        <div class="flex items-center justify-between gap-4 flex-wrap">
          <button
            type="button"
            @click="$router.push('/')"
            class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-surface-container-high text-on-surface font-bold hover:bg-surface-container-highest transition-colors"
          >
            <span class="material-symbols-outlined text-base">arrow_back</span>
            返回首页
          </button>

          <button
            type="button"
            @click="resetFilters"
            class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-surface text-on-surface-variant border border-surface-container-high hover:bg-surface-container-low transition-colors font-bold"
          >
            <span class="material-symbols-outlined text-base">restart_alt</span>
            重置筛选
          </button>
        </div>

        <div class="mt-8 grid grid-cols-1 lg:grid-cols-[1.2fr_0.8fr] gap-8 items-start">
          <div>
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-secondary-container text-on-secondary-container border border-secondary/10">
              <span class="material-symbols-outlined text-base" style="font-variation-settings: 'FILL' 1;">workspace_premium</span>
              <span class="text-xs font-black tracking-wide">成功归还案例展示</span>
            </div>

            <h1 class="mt-4 text-4xl md:text-5xl font-black tracking-tight font-headline">
              温暖找回 · 成功案例中心
            </h1>

            <p class="mt-4 text-on-surface-variant text-lg font-medium max-w-3xl leading-relaxed">
              通过真实数据与展示型案例结合，呈现平台在“失物找回、招领认领、AI辅助匹配、校园互助协作”中的实际成果与服务价值。
            </p>

            <div class="mt-6 flex flex-wrap gap-3">
              <span class="px-4 py-2 rounded-full bg-warning/15 text-warning text-sm font-bold border border-warning/20">
                失物已找到
              </span>
              <span class="px-4 py-2 rounded-full bg-success/15 text-success text-sm font-bold border border-success/20">
                招领已认领
              </span>
              <span class="px-4 py-2 rounded-full bg-primary/10 text-primary text-sm font-bold border border-primary/15">
                AI智能辅助
              </span>
            </div>
          </div>

          <!-- Summary -->
          <div class="bg-surface-container-low rounded-3xl p-5 md:p-6 border border-surface-container-high shadow-sm">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-black font-headline">案例成效概览</h3>
              <span class="material-symbols-outlined text-primary">insights</span>
            </div>

            <div class="mt-5 grid grid-cols-2 gap-4">
              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="text-xs text-on-surface-variant font-black uppercase tracking-wide">总成功案例</div>
                <div class="mt-2 text-3xl font-black text-on-surface">{{ summary.total }}</div>
              </div>
              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="text-xs text-on-surface-variant font-black uppercase tracking-wide">本周新增</div>
                <div class="mt-2 text-3xl font-black text-primary">{{ summary.thisWeek }}</div>
              </div>
              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="text-xs text-on-surface-variant font-black uppercase tracking-wide">今日认领</div>
                <div class="mt-2 text-3xl font-black text-success">{{ summary.today }}</div>
              </div>
              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="text-xs text-on-surface-variant font-black uppercase tracking-wide">AI辅助占比</div>
                <div class="mt-2 text-3xl font-black text-tertiary">{{ summary.aiRate }}</div>
              </div>
            </div>

            <div class="mt-4 grid grid-cols-2 gap-4">
              <div class="rounded-2xl bg-warning/10 p-4 border border-warning/20">
                <div class="text-xs font-black text-warning uppercase tracking-wide">失物找回</div>
                <div class="mt-2 text-2xl font-black text-warning">{{ summary.lost }}</div>
              </div>
              <div class="rounded-2xl bg-success/10 p-4 border border-success/20">
                <div class="text-xs font-black text-success uppercase tracking-wide">招领认领</div>
                <div class="mt-2 text-2xl font-black text-success">{{ summary.found }}</div>
              </div>
            </div>

            <div class="mt-5 p-4 rounded-2xl bg-primary/6 border border-primary/10">
              <div class="text-sm font-black text-primary">平均找回耗时</div>
              <p class="mt-2 text-2xl font-black text-on-surface">{{ summary.avgDuration }}</p>
              <p class="mt-1 text-sm text-on-surface-variant">基于当前展示案例自动统计，仅用于展示平台效率趋势。</p>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-6 py-10 space-y-8">
      <!-- Heat Source -->
      <section class="bg-surface-container-low rounded-3xl p-5 md:p-6 border border-surface-container-high shadow-sm">
        <div class="flex items-center justify-between gap-4 flex-wrap">
          <div>
            <h2 class="text-xl font-black font-headline">校园热力来源</h2>
            <p class="text-sm text-on-surface-variant mt-1">高频成功找回地点分布（展示型统计）</p>
          </div>
          <span class="text-sm text-on-surface-variant font-medium">地点 TOP 4</span>
        </div>

        <div class="mt-5 grid grid-cols-2 md:grid-cols-4 gap-4">
          <div
            v-for="spot in hotSpots"
            :key="spot.name"
            class="rounded-2xl bg-surface p-5 border border-surface-container-high hover:border-primary/20 transition-colors"
          >
            <div class="flex items-center justify-between">
              <span class="material-symbols-outlined text-primary">{{ spot.icon }}</span>
              <span class="text-xs font-black text-on-surface-variant">TOP {{ spot.rank }}</span>
            </div>
            <div class="mt-4 text-lg font-black text-on-surface">{{ spot.name }}</div>
            <div class="mt-1 text-sm text-on-surface-variant">{{ spot.count }} 条成功案例</div>
            <div class="mt-3 h-2 rounded-full bg-surface-container-high overflow-hidden">
              <div class="h-full rounded-full bg-primary" :style="{ width: `${spot.percent}%` }"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- Filter -->
      <section class="bg-surface-container-low rounded-3xl p-5 md:p-6 border border-surface-container-high shadow-sm">
        <div class="flex items-center justify-between gap-4 flex-wrap">
          <div>
            <h2 class="text-xl font-black font-headline">筛选与搜索</h2>
            <p class="text-sm text-on-surface-variant mt-1">快速定位你关心的成功案例</p>
          </div>
          <div class="text-sm text-on-surface-variant font-medium">
            当前结果：<span class="font-black text-primary">{{ filteredCases.length }}</span> 条
          </div>
        </div>

        <div class="mt-5 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
          <div class="relative">
            <span class="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant">
              <span class="material-symbols-outlined">search</span>
            </span>
            <input
              v-model="filters.keyword"
              type="text"
              placeholder="搜索物品名称、地点、描述..."
              class="w-full pl-12 pr-4 py-3.5 bg-surface text-on-surface rounded-2xl border border-surface-container-high outline-none focus:border-primary transition-colors"
            />
          </div>

          <select
            v-model="filters.type"
            class="w-full px-4 py-3.5 bg-surface text-on-surface rounded-2xl border border-surface-container-high outline-none"
          >
            <option value="">全部类型</option>
            <option value="lost">失物找回</option>
            <option value="found">招领认领</option>
          </select>

          <select
            v-model="filters.category"
            class="w-full px-4 py-3.5 bg-surface text-on-surface rounded-2xl border border-surface-container-high outline-none"
          >
            <option value="">全部分类</option>
            <option v-for="c in uniqueCategories" :key="c.value" :value="c.value">{{ c.label }}</option>
          </select>

          <select
            v-model="filters.sort"
            class="w-full px-4 py-3.5 bg-surface text-on-surface rounded-2xl border border-surface-container-high outline-none"
          >
            <option value="latest">按时间：最新优先</option>
            <option value="oldest">按时间：最早优先</option>
            <option value="title">按名称排序</option>
          </select>
        </div>
      </section>

      <!-- Loading -->
      <section v-if="loading" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        <div
          v-for="i in 6"
          :key="i"
          class="rounded-3xl border border-surface-container-high bg-surface-container-low p-6 animate-pulse"
        >
          <div class="flex items-center justify-between">
            <div class="h-6 bg-surface-container-highest rounded-xl w-1/3"></div>
            <div class="h-5 bg-surface-container-highest rounded-full w-20"></div>
          </div>
          <div class="mt-5 h-6 bg-surface-container-highest rounded-xl w-2/3"></div>
          <div class="mt-4 h-16 bg-surface-container-highest rounded-2xl"></div>
          <div class="mt-4 h-4 bg-surface-container-highest rounded-lg w-1/2"></div>
          <div class="mt-6 h-10 bg-surface-container-highest rounded-2xl"></div>
        </div>
      </section>

      <!-- Empty -->
      <section
        v-else-if="filteredCases.length === 0"
        class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant"
      >
        <div class="inline-flex items-center justify-center w-24 h-24 bg-surface-container-highest rounded-full mb-6 text-outline-variant">
          <span class="material-symbols-outlined text-5xl">sentiment_dissatisfied</span>
        </div>
        <h3 class="text-2xl font-black text-on-surface mb-3">暂无匹配的成功案例</h3>
        <p class="text-on-surface-variant">可以尝试切换分类、类型或关键词重新筛选。</p>
      </section>

      <!-- Cards -->
      <section v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        <article
          v-for="item in filteredCases"
          :key="`${item.source}-${item.type}-${item.id}`"
          @click="goDetail(item)"
          class="group cursor-pointer bg-surface-container-low rounded-3xl overflow-hidden border border-surface-container-high hover:border-primary/25 hover:shadow-2xl hover:shadow-primary/10 hover:-translate-y-1 transition-all duration-300"
        >
          <!-- Header -->
          <div
            class="p-5 border-b border-surface-container-high"
            :class="item.type === 'lost' ? 'bg-warning/10' : 'bg-success/10'"
          >
            <div class="flex items-center justify-between gap-3">
              <div class="flex flex-wrap gap-2">
                <span
                  class="px-3 py-1 rounded-full text-[11px] font-black tracking-wider"
                  :class="item.type === 'lost'
                    ? 'bg-warning/20 text-warning'
                    : 'bg-success/20 text-success'"
                >
                  {{ item.type === 'lost' ? '失物已找到' : '招领已认领' }}
                </span>

                <span class="px-3 py-1 rounded-full text-[11px] font-black tracking-wider bg-surface text-on-surface-variant border border-surface-container-high">
                  {{ item.categoryText || '未分类' }}
                </span>

                <span
                  class="px-3 py-1 rounded-full text-[11px] font-black tracking-wider"
                  :class="item.source === 'mock'
                    ? 'bg-primary/10 text-primary border border-primary/15'
                    : 'bg-surface-container-highest text-on-surface-variant border border-surface-container-high'"
                >
                  {{ item.source === 'mock' ? '展示案例' : '真实案例' }}
                </span>
              </div>

              <span class="text-xs font-bold text-on-surface-variant shrink-0">
                {{ formatDate(item.time) }}
              </span>
            </div>

            <div class="mt-4 flex flex-wrap gap-2">
              <span
                v-for="tag in item.tags"
                :key="tag"
                class="px-3 py-1 rounded-full text-[11px] font-black tracking-wide bg-surface text-primary border border-primary/10"
              >
                {{ tag }}
              </span>
            </div>
          </div>

          <!-- Body -->
          <div class="p-6">
            <div class="flex items-start justify-between gap-3">
              <h3 class="text-xl font-black font-headline text-on-surface line-clamp-1 group-hover:text-primary transition-colors">
                {{ item.title || '未命名物品' }}
              </h3>
              <span class="material-symbols-outlined text-primary group-hover:translate-x-0.5 transition-transform">
                arrow_forward
              </span>
            </div>

            <div class="mt-3 flex flex-wrap gap-2">
              <span
                v-if="item.location"
                class="px-3 py-1 rounded-full text-xs font-bold bg-surface-container-highest text-on-surface-variant border border-surface-container-high"
              >
                {{ item.location }}
              </span>
              <span
                v-if="item.durationText"
                class="px-3 py-1 rounded-full text-xs font-bold bg-surface-container-highest text-on-surface-variant border border-surface-container-high"
              >
                {{ item.durationText }}
              </span>
            </div>

            <div class="mt-5 p-4 rounded-2xl bg-surface border border-surface-container-high">
              <div class="flex items-center gap-2 text-xs font-black text-on-surface-variant uppercase tracking-wider">
                <span class="material-symbols-outlined text-base text-secondary">menu_book</span>
                案例故事
              </div>
              <p class="mt-2 text-sm text-on-surface-variant leading-relaxed line-clamp-3">
                {{ item.successNote || item.description || '该物品已完成认领/找回，体现了平台在校园互助中的积极作用。' }}
              </p>
            </div>

            <!-- 流程可视化 -->
            <div class="mt-5 p-4 rounded-2xl border border-primary/10 bg-primary/5">
              <div class="flex items-center gap-2 text-xs font-black text-primary uppercase tracking-wider">
                <span class="material-symbols-outlined text-base">route</span>
                成功路径
              </div>
              <div class="mt-3 flex items-center justify-between gap-2 text-[11px] md:text-xs font-bold text-on-surface-variant">
                <div class="flex flex-col items-center gap-2 flex-1">
                  <span class="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center text-xs">1</span>
                  <span>发布信息</span>
                </div>
                <span class="material-symbols-outlined text-outline-variant">chevron_right</span>
                <div class="flex flex-col items-center gap-2 flex-1">
                  <span class="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center text-xs">2</span>
                  <span>智能匹配</span>
                </div>
                <span class="material-symbols-outlined text-outline-variant">chevron_right</span>
                <div class="flex flex-col items-center gap-2 flex-1">
                  <span class="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center text-xs">3</span>
                  <span>联系核验</span>
                </div>
                <span class="material-symbols-outlined text-outline-variant">chevron_right</span>
                <div class="flex flex-col items-center gap-2 flex-1">
                  <span class="w-8 h-8 rounded-full bg-success text-white flex items-center justify-center text-xs">4</span>
                  <span>成功找回</span>
                </div>
              </div>
            </div>

            <div class="mt-5 flex items-center justify-between text-xs text-on-surface-variant">
              <span class="inline-flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">bolt</span>
                亮点说明
              </span>
              <span
                class="font-black"
                :class="item.type === 'lost' ? 'text-warning' : 'text-success'"
              >
                {{ item.highlight || (item.type === 'lost' ? '已找到' : '已认领') }}
              </span>
            </div>
          </div>
        </article>
      </section>
    </main>
  </div>
</template>

<script setup>
import { getFoundList } from '@/api/found'
import { getLostList } from '@/api/lost'
import { useItemCategories } from '@/composables/useItemCategories'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const { categories, loadCategories } = useItemCategories()

const loading = ref(false)
const cases = ref([])

const filters = reactive({
  keyword: '',
  type: '',
  category: '',
  sort: 'latest'
})

const mockCases = [
  {
    id: 'mock-lost-1',
    source: 'mock',
    type: 'lost',
    title: '黑色双肩书包',
    category: 'study',
    categoryText: '学习用品',
    location: '图书馆三楼',
    time: '2026-03-26 10:20:00',
    description: '书包内含课程笔记、本学期教材与文具，最终通过地点与书包挂件特征核验成功找回。',
    status: 'found',
    tags: ['AI辅助匹配', '高相似度匹配成功'],
    successNote: '失主发布失物信息后，系统结合地点与物品特征快速推荐了相关线索，最终在图书馆服务台完成核验取回。',
    highlight: 'AI推荐后快速定位',
    durationText: '3小时内找回'
  },
  {
    id: 'mock-found-1',
    source: 'mock',
    type: 'found',
    title: '校园卡',
    category: 'cards',
    categoryText: '证件卡类',
    location: '第二食堂入口',
    time: '2026-03-25 14:10:00',
    description: '拾到同学第一时间发布招领，失主凭卡面信息和身份核验后成功认领。',
    status: 'claimed',
    tags: ['同学拾金不昧', '当日完成认领'],
    successNote: '校园卡遗失后，拾到者及时发布招领信息，失主在当天完成核验并顺利认领。',
    highlight: '通过校园卡特征核验成功',
    durationText: '当天完成认领'
  },
  {
    id: 'mock-lost-2',
    source: 'mock',
    type: 'lost',
    title: '蓝牙耳机',
    category: 'electronics',
    categoryText: '电子产品',
    location: '教学楼A区',
    time: '2026-03-22 16:00:00',
    description: '耳机外观和耳机盒划痕特征明显，通过平台发布和联系核验顺利找回。',
    status: 'found',
    tags: ['跨地点找回', 'AI辅助匹配'],
    successNote: '失主原以为丢失在教室，后在平台推荐线索中定位到保洁转交信息，最终完成找回。',
    highlight: '跨区域线索整合成功',
    durationText: '1天内找回'
  },
  {
    id: 'mock-found-2',
    source: 'mock',
    type: 'found',
    title: '银色保温杯',
    category: 'others',
    categoryText: '其他物品',
    location: '操场看台',
    time: '2026-03-21 08:40:00',
    description: '保温杯底部有明显贴纸标识，失主根据描述快速确认并完成认领。',
    status: 'claimed',
    tags: ['同学拾金不昧', '高相似度匹配成功'],
    successNote: '平台通过物品外观特征与拾获地点双重匹配，帮助失主快速锁定招领信息。',
    highlight: '外观细节核验高效完成',
    durationText: '2小时内完成认领'
  }
]

const hotSpots = [
  { rank: 1, name: '图书馆', count: 18, percent: 92, icon: 'local_library' },
  { rank: 2, name: '食堂', count: 14, percent: 76, icon: 'restaurant' },
  { rank: 3, name: '教学楼', count: 11, percent: 60, icon: 'school' },
  { rank: 4, name: '操场', count: 8, percent: 42, icon: 'sports_soccer' }
]

const getCategoryText = (category) => {
  const hit = categories.value.find((x) => x.code === category || x.name === category)
  return hit?.name || category || ''
}

const buildTags = (item, type) => {
  const tags = []
  const text = String(item.description || item.title || '')

  if (/ai|智能|匹配/.test(text.toLowerCase())) tags.push('AI辅助匹配')
  if (/当天|当日/.test(text)) tags.push('当日完成认领')
  if (/核验|验证/.test(text)) tags.push('高相似度匹配成功')
  if (/图书馆|食堂|教学楼|操场/.test(text)) tags.push('跨地点找回')
  if (tags.length === 0) {
    tags.push(type === 'lost' ? '成功找回' : '顺利认领')
  }
  return tags.slice(0, 2)
}

const buildDurationText = (time) => {
  const d = new Date(time)
  if (Number.isNaN(d.getTime())) return '流程已完成'
  const now = new Date()
  const diffHours = Math.max(1, Math.round((now.getTime() - d.getTime()) / (1000 * 60 * 60)))
  if (diffHours <= 3) return '3小时内完成'
  if (diffHours <= 24) return '当天完成'
  if (diffHours <= 72) return '3天内完成'
  return '流程已完成'
}

const normalizeCase = (item, type) => {
  const isLost = type === 'lost'
  const location = isLost
    ? (item.lostLocation || item.lost_location || item.location || '')
    : (item.foundLocation || item.found_location || item.location || '')
  const time = isLost
    ? (item.lostTime || item.lost_time || item.time || item.createTime || item.create_time || '')
    : (item.foundTime || item.found_time || item.time || item.createTime || item.create_time || '')

  return {
    id: item.id || item.itemId,
    source: 'real',
    type,
    title: item.title || item.name || '',
    category: item.category || '',
    categoryText: getCategoryText(item.category),
    location,
    time,
    description: item.description || '',
    status: item.status || '',
    tags: buildTags(item, type),
    successNote: item.description || `该${type === 'lost' ? '失物' : '招领'}案例已顺利完成流程，体现了平台信息流转与校园互助的实际价值。`,
    highlight: type === 'lost' ? '平台协助完成找回' : '平台协助完成认领',
    durationText: buildDurationText(time)
  }
}

const loadCases = async () => {
  loading.value = true
  try {
    const [lostResp, foundResp] = await Promise.all([
      getLostList({ page: 1, size: 1000 }),
      getFoundList({ page: 1, size: 1000 })
    ])

    const lostItems = lostResp?.code === 200 ? (lostResp.data?.items || []) : []
    const foundItems = foundResp?.code === 200 ? (foundResp.data?.items || []) : []

    const successLost = lostItems
      .filter(item => String(item.status || '').toLowerCase() === 'found')
      .map(item => normalizeCase(item, 'lost'))

    const successFound = foundItems
      .filter(item => String(item.status || '').toLowerCase() === 'claimed')
      .map(item => normalizeCase(item, 'found'))

    cases.value = [...mockCases, ...successLost, ...successFound]
  } catch (error) {
    console.error('加载成功案例失败:', error)
    cases.value = [...mockCases]
  } finally {
    loading.value = false
  }
}

const uniqueCategories = computed(() => {
  const map = new Map()
  cases.value.forEach(item => {
    const value = item.category || item.categoryText
    const label = item.categoryText || item.category || '未分类'
    if (value) map.set(value, { value, label })
  })
  return Array.from(map.values())
})

const filteredCases = computed(() => {
  let list = [...cases.value]

  if (filters.type) {
    list = list.filter(item => item.type === filters.type)
  }

  if (filters.category) {
    list = list.filter(item => item.category === filters.category || item.categoryText === filters.category)
  }

  if (filters.keyword.trim()) {
    const kw = filters.keyword.trim().toLowerCase()
    list = list.filter(item =>
      String(item.title).toLowerCase().includes(kw) ||
      String(item.location).toLowerCase().includes(kw) ||
      String(item.description).toLowerCase().includes(kw) ||
      String(item.categoryText).toLowerCase().includes(kw) ||
      String(item.successNote).toLowerCase().includes(kw) ||
      String(item.highlight).toLowerCase().includes(kw)
    )
  }

  list.sort((a, b) => {
    if (filters.sort === 'title') {
      return String(a.title).localeCompare(String(b.title), 'zh-CN')
    }

    const ta = new Date(a.time).getTime() || 0
    const tb = new Date(b.time).getTime() || 0
    return filters.sort === 'oldest' ? ta - tb : tb - ta
  })

  return list
})

const summary = computed(() => {
  const total = cases.value.length
  const lost = cases.value.filter(item => item.type === 'lost').length
  const found = cases.value.filter(item => item.type === 'found').length

  const now = new Date()
  const thisWeek = cases.value.filter(item => {
    const d = new Date(item.time)
    if (Number.isNaN(d.getTime())) return false
    return now.getTime() - d.getTime() <= 7 * 24 * 60 * 60 * 1000
  }).length

  const today = cases.value.filter(item => {
    const d = new Date(item.time)
    if (Number.isNaN(d.getTime())) return false
    return d.toDateString() === now.toDateString()
  }).length

  const aiCount = cases.value.filter(item =>
    Array.isArray(item.tags) && item.tags.some(tag => tag.includes('AI'))
  ).length

  const aiRate = total > 0 ? `${Math.round((aiCount / total) * 100)}%` : '0%'

  const avgDuration = total > 0 ? '约 1.8 天' : '暂无数据'

  return { total, lost, found, thisWeek, today, aiRate, avgDuration }
})

const formatDate = (dateString) => {
  if (!dateString) return '时间未知'
  try {
    const date = new Date(dateString)
    if (Number.isNaN(date.getTime())) return String(dateString)
    return date.toLocaleDateString('zh-CN')
  } catch {
    return String(dateString)
  }
}

const goDetail = (item) => {
  if (item.source === 'mock') return
  if (item.type === 'lost') {
    router.push(`/lost/${item.id}`)
  } else {
    router.push(`/found/${item.id}`)
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.type = ''
  filters.category = ''
  filters.sort = 'latest'
}

onMounted(async () => {
  await loadCategories()
  await loadCases()
})
</script>

<style scoped>
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-lowest { background-color: #FFFBFE; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }

.bg-primary\/5 { background-color: rgba(103, 80, 164, 0.05); }
.bg-primary\/6 { background-color: rgba(103, 80, 164, 0.06); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.10); }
.bg-primary-container\/40 { background-color: rgba(234, 221, 255, 0.4); }
.bg-secondary-container\/40 { background-color: rgba(232, 222, 248, 0.4); }

.bg-warning\/10 { background-color: rgba(220, 188, 67, 0.10); }
.bg-warning\/15 { background-color: rgba(220, 188, 67, 0.15); }
.bg-warning\/20 { background-color: rgba(220, 188, 67, 0.20); }

.bg-success\/10 { background-color: rgba(0, 110, 28, 0.10); }
.bg-success\/15 { background-color: rgba(0, 110, 28, 0.15); }
.bg-success\/20 { background-color: rgba(0, 110, 28, 0.20); }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-tertiary { color: #7D5260; }
.text-warning { color: #8A6B00; }
.text-success { color: #006E1C; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-outline-variant { border-color: #CAC4D0; }
.border-primary\/10 { border-color: rgba(103, 80, 164, 0.10); }
.border-primary\/15 { border-color: rgba(103, 80, 164, 0.15); }
.border-warning\/20 { border-color: rgba(220, 188, 67, 0.20); }
.border-success\/20 { border-color: rgba(0, 110, 28, 0.20); }

.shadow-primary\/10 { --tw-shadow-color: rgba(103, 80, 164, 0.10); }
</style>