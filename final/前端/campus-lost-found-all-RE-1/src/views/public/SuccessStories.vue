<template>
  <div class="min-h-screen bg-surface text-on-surface font-body">
    <header class="bg-surface-container-low border-b border-surface-container-high">
      <div class="max-w-6xl mx-auto px-6 py-10 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-3xl font-black font-headline text-on-surface">成功案例</h1>
          <p class="text-on-surface-variant mt-2">展示已认领、已找到等完结状态的失物与招领信息</p>
        </div>
        <button
          type="button"
          @click="$router.push('/')"
          class="inline-flex items-center gap-2 px-5 py-3 rounded-2xl bg-primary text-on-primary font-bold shadow-lg shadow-primary/20 hover:opacity-95 transition-opacity"
        >
          <span class="material-symbols-outlined text-base">home</span>
          返回首页
        </button>
      </div>
    </header>

    <main class="max-w-6xl mx-auto px-6 py-10">
      <div v-if="loading" class="text-center py-20 text-on-surface-variant">
        <span class="material-symbols-outlined animate-spin text-4xl">progress_activity</span>
        <p class="mt-2">加载中...</p>
      </div>

      <div v-else-if="items.length === 0" class="text-center py-20 bg-surface-container-low rounded-3xl border border-dashed border-outline-variant">
        <p class="text-on-surface-variant">暂无成功案例公示</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <article
          v-for="row in items"
          :key="row._kind + '-' + row.id"
          class="bg-surface-container-low rounded-3xl border border-surface-container-high p-6 hover:shadow-md transition-shadow cursor-pointer"
          @click="goDetail(row)"
        >
          <div class="flex items-center justify-between gap-3 mb-3">
            <span
              class="text-xs font-black px-3 py-1 rounded-full"
              :class="row._kind === 'lost' ? 'bg-error/15 text-error' : 'bg-tertiary-container text-on-tertiary-container'"
            >
              {{ row._kind === 'lost' ? '失物' : '招领' }}
            </span>
            <span class="text-xs font-bold text-primary">{{ statusLabel(row.status) }}</span>
          </div>
          <h2 class="text-xl font-bold font-headline line-clamp-2">{{ row.title }}</h2>
          <p class="text-sm text-on-surface-variant mt-2 line-clamp-2">
            {{ row._kind === 'lost' ? row.lostLocation : row.foundLocation }}
          </p>
          <p class="text-xs text-outline mt-4">{{ row.createTime || '' }}</p>
        </article>
      </div>
    </main>
  </div>
</template>

<script setup>
import { getLatestFound, getLatestLost } from '@/api/home'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const items = ref([])

const isSuccessStatus = (s) => {
  const x = String(s || '').trim().toLowerCase()
  if (!x) return false
  if (['claimed', 'found', 'closed', 'completed'].includes(x)) return true
  if (x.includes('认领') || x.includes('找到') || x.includes('完结')) return true
  return false
}

const statusLabel = (s) => {
  const x = String(s || '').trim()
  const map = {
    claimed: '已认领',
    found: '已找到',
    closed: '已关闭',
    completed: '已完成'
  }
  return map[x] || x || '已完结'
}

const goDetail = (row) => {
  if (row._kind === 'lost') router.push(`/lost/${row.id}`)
  else router.push(`/found/${row.id}`)
}

onMounted(async () => {
  loading.value = true
  try {
    const [lr, fr] = await Promise.all([
      getLatestLost({ page: 1, size: 800 }),
      getLatestFound({ page: 1, size: 800 })
    ])
    const lostRaw = lr?.code === 200 ? lr.data?.items || [] : []
    const foundRaw = fr?.code === 200 ? fr.data?.items || [] : []
    const lost = lostRaw.filter((i) => isSuccessStatus(i.status)).map((i) => ({ ...i, _kind: 'lost' }))
    const found = foundRaw.filter((i) => isSuccessStatus(i.status)).map((i) => ({ ...i, _kind: 'found' }))
    const merged = [...lost, ...found].sort((a, b) => {
      const ta = new Date(a.createTime || 0).getTime()
      const tb = new Date(b.createTime || 0).getTime()
      return tb - ta
    })
    items.value = merged
  } catch (e) {
    console.error(e)
    items.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-high { border-color: #ECE6F0; }
.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-primary { color: #6750A4; }
.text-outline { color: #79747E; }
.bg-primary { background-color: #6750A4; }
.text-on-primary { color: #fff; }
.bg-tertiary-container { background-color: #FFD8E4; }
.text-on-tertiary-container { color: #31111D; }
</style>
