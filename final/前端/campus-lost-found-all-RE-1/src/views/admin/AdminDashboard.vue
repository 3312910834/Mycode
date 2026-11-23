<template>
  <div class="dashboard tech-theme">
    <!-- 动态流动背景 -->
    <div class="ambient-background">
      <div class="glow-blob blob-1"></div>
      <div class="glow-blob blob-2"></div>
      <div class="glow-blob blob-3"></div>
    </div>

    <div class="dashboard-content">
      <!-- 顶部与AI状态指示器 -->
      <div class="header-wrap">
        <h2 class="page-title">数据视界 <span class="title-accent">NEXUS</span></h2>
        <div class="ai-status glass-panel">
          <div class="radar-scan"></div>
          <span class="pulse-dot"></span>
          <span class="ai-text">AI 神经元引擎实时计算中</span>
        </div>
      </div>

      <!-- ================= 创新点 1：AI 事件预警与预测引擎 ================= -->
      <transition name="fade">
        <div class="ai-alert-banner glass-card animate-item stagger-1">
          <div class="alert-icon pulse-red-icon"><el-icon><Warning /></el-icon></div>
          <div class="alert-content">
            <div class="alert-title">【AI 预测神谕】高阶环境风险推演</div>
            <div class="alert-desc">
              气象网络反馈明日有暴雨，结合历史多维数据，预计 <span class="highlight-danger">"雨伞/电子设备"</span> 类资产在 <span class="highlight-danger">"图书馆" 与 "教学楼A"</span> 的遗失率将激增 <span class="highlight-danger">300%</span>。
            </div>
          </div>
          <div class="alert-action">
            <el-button color="#f43f5e" plain class="cyber-btn-danger" round>
              下发区域防范广播
            </el-button>
          </div>
        </div>
      </transition>

      <!-- 搜索卡片 (高级动画版) -->
      <el-card class="search-card premium-search glass-card animate-item stagger-2" shadow="never">
        <div class="search-row">
          <el-input
            v-model="globalKeyword"
            placeholder="输入物品特征、地点或发布者进行智能检索..."
            clearable
            class="animated-input cyber-input"
            :prefix-icon="Search"
            @keyup.enter="runGlobalSearch"
            @clear="clearSearch"
          />
          <el-button type="primary" class="animated-btn search-btn cyber-btn" :icon="Search" :loading="searchLoading" @click="runGlobalSearch" round>
            神经检索
          </el-button>
          
          <transition name="fade">
            <el-button v-if="searchHitItems.length || searchHitUsers.length" class="animated-btn clear-btn glass-btn" :icon="Delete" @click="clearSearch" round>
              清空矩阵
            </el-button>
          </transition>
        </div>

        <transition name="slide-fade">
          <div v-if="searchHitItems.length || searchHitUsers.length" class="search-results-wrapper">
            <el-divider border-style="dashed" class="cyber-divider" />
            
            <el-row v-if="searchHitItems.length" :gutter="16" class="search-results">
              <el-col :span="24">
                <h4 class="sub-title cyber-text"><el-icon><Box /></el-icon> 匹配物理资产</h4>
                <el-table :data="searchHitItems" size="small" max-height="280" class="animated-table cyber-table">
                  <el-table-column prop="type" label="类型" width="90" align="center">
                    <template #default="{ row }">
                      <el-tag :type="row.type === 'lost' ? 'danger' : 'success'" size="small" effect="dark" class="neon-tag">
                        {{ row.type === 'lost' ? '寻物' : '招领' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
                  <el-table-column prop="poster" label="发布者" width="120" show-overflow-tooltip />
                  <el-table-column prop="date" label="时间" width="160" />
                </el-table>
              </el-col>
            </el-row>

            <el-row v-if="searchHitUsers.length" :gutter="16" class="search-results" style="margin-top: 15px;">
              <el-col :span="24">
                <h4 class="sub-title cyber-text"><el-icon><User /></el-icon> 匹配用户实体</h4>
                <el-table :data="searchHitUsers" size="small" max-height="240" class="animated-table cyber-table">
                  <el-table-column prop="id" label="ID" width="70" align="center" />
                  <el-table-column prop="username" label="用户名" width="140" />
                  <el-table-column prop="role" label="权限层级" width="100" align="center">
                    <template #default="{ row }">
                      <el-tag :type="row.role === 'admin' ? 'warning' : 'info'" size="small" effect="dark" class="neon-tag">
                        {{ row.role === 'admin' ? '管理员' : '普通用户' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="email" label="通信地址" min-width="180" show-overflow-tooltip />
                </el-table>
              </el-col>
            </el-row>
          </div>
        </transition>
      </el-card>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6" class="animate-item stagger-3">
          <el-card class="stat-card glass-card hover-lift" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon neon-blue"><el-icon size="28"><Box /></el-icon></div>
              <div class="stat-info"><h3 class="counter-num">{{ stats.totalItems }}</h3><p>数据总量</p></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" class="animate-item stagger-4">
          <el-card class="stat-card glass-card hover-lift" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon neon-orange"><el-icon size="28"><Clock /></el-icon></div>
              <div class="stat-info"><h3 class="counter-num">{{ stats.pendingItems }}</h3><p>待处理列队</p></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" class="animate-item stagger-5">
          <el-card class="stat-card glass-card hover-lift" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon neon-green"><el-icon size="28"><Check /></el-icon></div>
              <div class="stat-info"><h3 class="counter-num">{{ stats.resolvedItems }}</h3><p>已解决节点</p></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" class="animate-item stagger-6">
          <el-card class="stat-card glass-card hover-lift" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon neon-purple"><el-icon size="28"><User /></el-icon></div>
              <div class="stat-info"><h3 class="counter-num">{{ stats.totalUsers }}</h3><p>活跃身份数</p></div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- ================= 创新点 2：万物互联网络图谱 ================= -->
      <el-row :gutter="20" class="charts-row">
        <el-col :span="24" class="animate-item stagger-7">
          <el-card header="NEXUS 神经元生态拓扑 (地-人-物链路关系)" class="chart-card glass-card hover-lift" shadow="hover" style="height: 500px;">
            <div ref="networkGraphRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 原有图表行 -->
      <el-row :gutter="20" class="charts-row">
        <el-col :span="12" class="animate-item stagger-8">
          <el-card header="各楼栋资产遗失热力分布" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="heatmapRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12" class="animate-item stagger-9">
          <el-card header="物品类型 AI 聚类分析" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="pieRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="16" class="animate-item stagger-10">
          <el-card header="遗失 vs 寻回 演进趋势 (近6月)" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="monthlyCompareRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8" class="animate-item stagger-11">
          <el-card header="高频异常时间雷达" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="radarRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 用户排行与画像入口 -->
      <el-row :gutter="20" class="charts-row">
        <el-col :span="16" class="animate-item stagger-12">
          <el-card header="全网身份实体增长曲线 (30天)" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="userGrowthRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8" class="animate-item stagger-13">
          <el-card header="高优贡献节点 TOP 10 (点击解码画像)" class="chart-card list-card glass-card hover-lift" shadow="hover">
            <ul class="top-list">
              <!-- ================= 创新点 3 触发区域 ================= -->
              <li 
                v-for="(item, index) in backendStats.topPublishers" 
                :key="index" 
                class="top-item animated-list-item clickable-node" 
                :style="{ animationDelay: `${index * 0.05 + 0.5}s` }"
                @click="openUserProfile(item)"
              >
                <span :class="['rank-badge', index < 3 ? 'top-' + (index + 1) : '']">{{ index + 1 }}</span>
                <span class="user-name">{{ item.name }}</span>
                <span class="post-count">{{ item.value }} 次</span>
              </li>
            </ul>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="8" class="animate-item stagger-14">
          <el-card header="系统消息流转频率" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="messageTrendRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8" class="animate-item stagger-15">
          <el-card header="系统生态健康度 (反馈处理)" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="suggestionRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8" class="animate-item stagger-16">
          <el-card header="AI 智能匹配转化漏斗" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="funnelRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="16" class="animate-item stagger-17">
          <el-card header="全平台资产找回率趋势" class="chart-card glass-card hover-lift" shadow="hover">
            <div ref="lineRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="8" class="animate-item stagger-18">
          <el-card header="网络平均匹配耗时" class="chart-card number-card glass-card hover-lift" shadow="hover">
            <div class="hologram-bg"></div>
            <div class="big-number-wrap">
              <span class="num cyber-glitch-text" :data-text="(backendStats.avgResolveHours / 24).toFixed(1)">
                {{ (backendStats.avgResolveHours / 24).toFixed(1) }}
              </span>
              <span class="unit">Days</span>
            </div>
            <div class="sub-number">等效运算时间 <span class="highlight-text">{{ backendStats.avgResolveHours.toFixed(1) }}</span> HRS</div>
          </el-card>
        </el-col>
      </el-row>

      <el-card header="系统实时动态日志" class="activity-card glass-card animate-item stagger-19 hover-lift" shadow="hover">
        <el-timeline class="cyber-timeline">
          <el-timeline-item v-for="activity in recentActivities" :key="activity.id" :timestamp="activity.timestamp" :type="activity.type" hollow>
            <div class="activity-content">
              <el-tag :type="activity.type" size="small" effect="dark" round class="pulse-tag neon-tag">{{ activity.action }}</el-tag>
              <span class="log-text">{{ activity.description }}</span>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

    <!-- ================= 创新点 3：全息特工画像解码弹窗 ================= -->
    <transition name="cyber-modal">
      <div v-if="showProfileModal" class="cyber-modal-overlay" @click.self="closeUserProfile">
        <div class="cyber-modal-content glass-card">
          <div class="modal-header">
            <h3 class="modal-title">
              <span class="glitch-icon"><el-icon><UserFilled /></el-icon></span> 
              节点实体档案解码: [ <span class="highlight-name">{{ selectedUser?.name }}</span> ]
            </h3>
            <el-button class="close-btn" @click="closeUserProfile" circle plain>
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          
          <div class="modal-body">
            <!-- 钢铁侠电弧反应炉背景特效 -->
            <div class="arc-reactor">
              <div class="reactor-ring ring-outer"></div>
              <div class="reactor-ring ring-middle"></div>
              <div class="reactor-ring ring-inner"></div>
              <div class="reactor-core"></div>
            </div>
            
            <!-- ECharts 雷达图容器 -->
            <div ref="userRadarRef" class="user-radar-chart"></div>
          </div>
          
          <div class="modal-footer">
            <div class="status-bar">
              <span class="status-dot"></span> 链路连接稳定 | AI 综合信用评级：
              <span :class="getUserCreditClass()">{{ getUserCreditLabel() }}</span>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useAdminStore } from '@/store/admin'
import { getAllItems, getAllUsers, getDashboardStats } from '@/api/admin'
import * as echarts from 'echarts'
import { Box, Clock, Check, User, Search, Delete, Warning, UserFilled, Close } from '@element-plus/icons-vue'

const adminStore = useAdminStore()
const { stats } = storeToRefs(adminStore)

// 图表 Refs
const heatmapRef = ref(null)
const pieRef = ref(null)
const lineRef = ref(null)
const monthlyCompareRef = ref(null)
const radarRef = ref(null)
const userGrowthRef = ref(null)
const messageTrendRef = ref(null)
const suggestionRef = ref(null)
const funnelRef = ref(null)
const networkGraphRef = ref(null) // 创新点 2 Ref
const userRadarRef = ref(null)    // 创新点 3 Ref

const echartInstances = []
let userRadarInstance = null

const recentActivities = ref([])
const globalKeyword = ref('')
const searchLoading = ref(false)
const searchHitItems = ref([])
const searchHitUsers = ref([])

const backendStats = ref({ topPublishers: [], avgResolveHours: 0 })

// ================= 创新点 3：弹窗与雷达图逻辑 =================
const showProfileModal = ref(false)
const selectedUser = ref(null)
const creditScore = ref(0)

const openUserProfile = async (user) => {
  selectedUser.value = user
  showProfileModal.value = true
  await nextTick()
  initUserRadarChart(user)
}

const closeUserProfile = () => {
  showProfileModal.value = false
  if (userRadarInstance) {
    userRadarInstance.dispose()
    userRadarInstance = null
  }
}

const getUserCreditLabel = () => {
  if (creditScore.value >= 95) return '极优 (S+)'
  if (creditScore.value >= 85) return '优秀 (S)'
  return '良好 (A)'
}
const getUserCreditClass = () => {
  if (creditScore.value >= 95) return 'text-purple'
  if (creditScore.value >= 85) return 'text-green'
  return 'text-blue'
}

const initUserRadarChart = (user) => {
  if (!userRadarRef.value) return
  userRadarInstance = echarts.init(userRadarRef.value)
  
  // 基于用户发帖量伪造逼真的能力数据
  const baseVal = Math.min(80 + (user.value || 0) * 2, 99)
  creditScore.value = 90 + Math.floor(Math.random() * 10)
  
  const data = [
    Math.min(baseVal + Math.floor(Math.random() * 10 - 5), 99), // 拾金不昧
    Math.min(baseVal + 15, 99),                                 // 活跃度
    Math.min(baseVal + Math.floor(Math.random() * 10), 99),     // 准确率
    Math.min(baseVal + Math.floor(Math.random() * 15 - 5), 99), // 响应速度
    Math.min(baseVal + Math.floor(Math.random() * 8), 99),      // 社区贡献
    creditScore.value                                           // 信用评级
  ]

  userRadarInstance.setOption({
    tooltip: { backgroundColor: 'rgba(15, 23, 42, 0.9)', borderColor: '#0ea5e9', textStyle: { color: '#fff' } },
    radar: {
      indicator: [
        { name: '拾金不昧', max: 100 },
        { name: '活跃频率', max: 100 },
        { name: '寻回准确率', max: 100 },
        { name: '响应速度', max: 100 },
        { name: '生态贡献', max: 100 },
        { name: '系统信用', max: 100 }
      ],
      radius: '65%',
      center: ['50%', '50%'],
      splitNumber: 4,
      axisName: { color: '#7dd3fc', fontSize: 14, fontWeight: 'bold', textShadowColor: '#0ea5e9', textShadowBlur: 10 },
      splitLine: { 
        lineStyle: { 
          color: ['rgba(14, 165, 233, 0.1)', 'rgba(14, 165, 233, 0.2)', 'rgba(14, 165, 233, 0.4)', 'rgba(14, 165, 233, 0.8)'], 
          shadowBlur: 15, shadowColor: '#0ea5e9' 
        } 
      },
      splitArea: { show: false },
      axisLine: { lineStyle: { color: 'rgba(14, 165, 233, 0.5)' } }
    },
    series: [{
      type: 'radar',
      data: [{ value: data, name: '实体能力图谱' }],
      symbol: 'circle', symbolSize: 8,
      itemStyle: { color: '#fff', borderColor: '#0ea5e9', borderWidth: 2, shadowBlur: 15, shadowColor: '#fff' },
      lineStyle: { color: '#0ea5e9', width: 2, shadowBlur: 15, shadowColor: '#0ea5e9' },
      areaStyle: { color: 'rgba(14, 165, 233, 0.4)' }
    }]
  })
}

// ================= 原有业务逻辑与图表 =================
const clearSearch = () => {
  globalKeyword.value = ''
  searchHitItems.value = []
  searchHitUsers.value = []
  if (adminStore.setSearchTerm) adminStore.setSearchTerm('')
}

const runGlobalSearch = async () => {
  const kw = globalKeyword.value.trim()
  if (!kw) return clearSearch()
  searchLoading.value = true
  try {
    const [ir, ur] = await Promise.all([
      getAllItems({ page: 1, size: 80, type: 'all', status: 'all', keyword: kw }),
      getAllUsers({ page: 1, limit: 50, username: kw })
    ])
    searchHitItems.value = Array.isArray(ir?.data?.items) ? ir.data.items : []
    searchHitUsers.value = Array.isArray(ur?.data?.list) ? ur.data.list : []
  } catch (e) {
    console.error(e)
    searchHitItems.value = []
    searchHitUsers.value = []
  } finally {
    searchLoading.value = false
  }
}

watch(() => adminStore.searchTerm, (newVal) => {
    if (newVal !== undefined && newVal !== globalKeyword.value) {
      globalKeyword.value = newVal
      if (newVal.trim() !== '') runGlobalSearch()
      else clearSearch()
    }
  }, { immediate: true }
)

const safeParseDate = (value) => {
  const s = String(value ?? '')
  if (!s) return null
  const d = new Date(s.replace(' ', 'T'))
  return Number.isNaN(d.getTime()) ? null : d
}

const extractBuildingName = (location) => {
  const loc = String(location ?? '')
  if (loc.includes('教学楼')) {
    const m = loc.match(/教学楼\s*([A-C])/)
    return m?.[1] ? `教学楼${m[1]}` : '教学楼'
  }
  if (loc.includes('图书馆')) return '图书馆'
  if (loc.includes('体育馆')) return '体育馆'
  if (loc.includes('宿舍楼')) {
    const m = loc.match(/宿舍楼\s*([1-9一二三四五六七八九十])/)
    return m?.[1] ? `宿舍楼${m[1]}` : '宿舍楼'
  }
  return '其他区域'
}

const computeReturnRateSeries = (items) => {
  const dated = items.map((i) => ({ raw: i, date: safeParseDate(i.date) })).filter((x) => x.date)
  const latestYear = dated.reduce((max, x) => Math.max(max, x.date.getFullYear()), dated[0]?.date?.getFullYear?.() ?? new Date().getFullYear())
  const monthCount = Array.from({ length: 12 }, () => ({ total: 0, resolved: 0 }))
  for (const { raw, date } of dated) {
    if (date.getFullYear() !== latestYear) continue
    const mIndex = date.getMonth()
    monthCount[mIndex].total += 1
    if (raw.status === 'resolved') monthCount[mIndex].resolved += 1
  }
  return monthCount.map(({ total, resolved }) => (total ? Math.round((resolved / total) * 100) : 0))
}

const tooltipDark = {
  backgroundColor: 'rgba(15, 23, 42, 0.85)',
  borderColor: 'rgba(56, 189, 248, 0.3)',
  textStyle: { color: '#e2e8f0' },
  backdropFilter: 'blur(4px)'
}

const axisDark = {
  axisLabel: { color: '#94a3b8' },
  axisLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
  splitLine: { lineStyle: { type: 'dashed', color: 'rgba(255,255,255,0.05)' } }
}

const createChart = (domRef, option) => {
  if (!domRef.value) return
  const chart = echarts.init(domRef.value)
  option.animationDuration = 2000
  option.animationEasing = 'cubicOut'
  chart.setOption(option)
  echartInstances.push(chart)
}

// 创新点 2：万物互联知识图谱 (全量真实数据版)
const initNetworkGraph = (heatmapData, rawItems) => {
  if (!networkGraphRef.value) return
  
  // 1. 初始化中心节点
  const nodes = [{ 
    id: 'NEXUS 中枢', name: 'NEXUS 中枢', 
    symbolSize: 60, 
    itemStyle: { color: '#38bdf8', shadowBlur: 30, shadowColor: '#38bdf8' },
    category: 0
  }]
  const links = []
  
  // 2. 加载所有真实的楼栋/地点 (解除原来只取 Top 4 的限制)
  const allLocations = heatmapData.map(d => d.name)
  allLocations.forEach(loc => {
    nodes.push({ 
      id: loc, name: loc, 
      symbolSize: 35, 
      itemStyle: { color: '#a78bfa', shadowBlur: 15, shadowColor: '#a78bfa' },
      category: 1
    })
    links.push({ source: 'NEXUS 中枢', target: loc })
  })

  // 3. 加载真实用户和物品
  // 【安全阀】为了防止真实数据达到数千条导致浏览器崩溃，这里设置最大渲染 300 条
  // 如果你的电脑性能好，且想看全部，可以把 .slice(0, 300) 删掉，直接用 rawItems
  const sampleItems = rawItems.slice(0, 300) 
  
  sampleItems.forEach((item, idx) => {
    const loc = extractBuildingName(item.location)
    const userName = item.poster ? String(item.poster) : `匿名节点_${idx}`
    // 缩短物品标题防止文字互相遮挡
    const itemName = item.title ? item.title.slice(0, 6) + '..' : `资产_${idx}`

    // 建立真实用户节点 (去重)
    if (!nodes.find(n => n.id === userName)) {
      nodes.push({ 
        id: userName, name: userName, 
        symbolSize: 18, 
        itemStyle: { color: '#34d399', shadowBlur: 10, shadowColor: '#34d399' },
        category: 2
      })
      // 连接：如果地点存在就连地点，不存在就直连中枢
      links.push({ source: allLocations.includes(loc) ? loc : 'NEXUS 中枢', target: userName })
    }
    
    // 建立真实物品节点 (去重)
    if (!nodes.find(n => n.id === itemName)) {
      nodes.push({ 
        id: itemName, name: itemName, 
        symbolSize: 10, 
        itemStyle: { color: '#f472b6' },
        category: 3
      })
      links.push({ source: userName, target: itemName })
    }
  })

  createChart(networkGraphRef, {
    tooltip: { ...tooltipDark },
    series: [{
      type: 'graph',
      layout: 'force',
      // 【关键优化】：针对海量节点的物理引擎调优
      force: { 
        repulsion: 80,       // 降低斥力，让庞大的节点群更紧凑
        edgeLength: [15, 50],// 缩短连线距离
        gravity: 0.15,       // 增加向心引力，防止节点飞出屏幕
        layoutAnimation: true // 允许看到节点聚拢的动画过程
      },
      roam: true, // 允许鼠标滚轮缩放和平移
      draggable: true,
      // 只有在大缩放比例下才显示物品文字，防止文字糊成一团
      label: { show: nodes.length < 100, position: 'right', color: '#cbd5e1', fontSize: 10 },
      itemStyle: { borderColor: '#020617', borderWidth: 1 },
      lineStyle: { color: 'source', curveness: 0.2, width: 1, opacity: 0.4 },
      emphasis: { 
        focus: 'adjacency', 
        lineStyle: { width: 3, opacity: 1 },
        label: { show: true } // 鼠标悬停时强制显示名字
      },
      data: nodes,
      links: links
    }]
  })
}

// 展开还原的普通图表配置
const initHeatmap = (data) => {
  createChart(heatmapRef, {
    tooltip: { trigger: 'axis', ...tooltipDark, axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(56, 189, 248, 0.1)' } } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map((item) => item.name), ...axisDark, axisLabel: { rotate: 45, color: '#94a3b8' } },
    yAxis: { type: 'value', ...axisDark },
    series: [{
      name: '丢失数量', type: 'bar', data: data.map((item) => item.value),
      barWidth: '35%',
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#38bdf8' }, { offset: 1, color: 'rgba(56, 189, 248, 0.1)' }
        ]),
        shadowColor: 'rgba(56, 189, 248, 0.5)', shadowBlur: 10
      },
      label: { show: true, position: 'top', color: '#38bdf8', fontWeight: 'bold' }
    }]
  })
}

const initPieChart = (data) => {
  createChart(pieRef, {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)', ...tooltipDark },
    legend: { orient: 'vertical', left: 'left', textStyle: { color: '#cbd5e1' } },
    color: ['#38bdf8', '#818cf8', '#c084fc', '#f472b6', '#34d399', '#fbbf24', '#f87171'],
    series: [{
      name: '物品类型', type: 'pie', radius: ['45%', '75%'], center: ['65%', '50%'],
      roseType: 'radius',
      itemStyle: { borderRadius: 8, borderColor: 'rgba(15, 23, 42, 0.8)', borderWidth: 2, shadowBlur: 15, shadowColor: 'rgba(0,0,0,0.5)' },
      label: { color: '#e2e8f0' },
      data
    }]
  })
}

const initLineChart = (data) => {
  createChart(lineRef, {
    tooltip: { trigger: 'axis', ...tooltipDark },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'], ...axisDark },
    yAxis: { type: 'value', axisLabel: { formatter: '{value}%', color: '#94a3b8' }, splitLine: axisDark.splitLine },
    series: [{
      name: '找回率', type: 'line', data, smooth: true, symbol: 'circle', symbolSize: 8,
      itemStyle: { color: '#34d399', borderWidth: 2, borderColor: '#fff' },
      lineStyle: { width: 4, color: '#34d399', shadowColor: 'rgba(52, 211, 153, 0.5)', shadowBlur: 10, shadowOffsetY: 5 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(52, 211, 153, 0.4)' }, { offset: 1, color: 'rgba(52, 211, 153, 0)' }
        ])
      }
    }]
  })
}

const initMonthlyCompare = (data) => {
  if(!data) return
  createChart(monthlyCompareRef, {
    tooltip: { trigger: 'axis', ...tooltipDark },
    legend: { data: ['丢失资产', '寻回资产'], textStyle: { color: '#cbd5e1' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.months, ...axisDark },
    yAxis: { type: 'value', ...axisDark },
    series: [
      { name: '丢失资产', type: 'line', smooth: true, data: data.lostData, itemStyle: { color: '#f43f5e' }, lineStyle: { width: 3, shadowColor: 'rgba(244, 63, 94, 0.4)', shadowBlur: 10 } },
      { name: '寻回资产', type: 'line', smooth: true, data: data.foundData, itemStyle: { color: '#38bdf8' }, lineStyle: { width: 3, shadowColor: 'rgba(56, 189, 248, 0.4)', shadowBlur: 10 } }
    ]
  })
}

const initRadarChart = (data) => {
  if(!data) return
  createChart(radarRef, {
    tooltip: { ...tooltipDark },
    radar: {
      indicator: [{name:'周一', max:100}, {name:'周二', max:100}, {name:'周三', max:100}, {name:'周四', max:100}, {name:'周五', max:100}, {name:'周六', max:100}, {name:'周日', max:100}],
      splitArea: { areaStyle: { color: ['rgba(139, 92, 246, 0.05)', 'rgba(139, 92, 246, 0.1)'] } },
      axisLine: { lineStyle: { color: 'rgba(139, 92, 246, 0.3)' } },
      splitLine: { lineStyle: { color: 'rgba(139, 92, 246, 0.4)' } },
      axisName: { color: '#c4b5fd' }
    },
    series: [{
      type: 'radar',
      itemStyle: { color: '#a78bfa' },
      lineStyle: { width: 2, shadowColor: 'rgba(167, 139, 250, 0.8)', shadowBlur: 10 },
      areaStyle: { color: 'rgba(139, 92, 246, 0.4)' },
      data: [{ value: data, name: '高频异常时段' }]
    }]
  })
}

const initUserGrowth = (data) => {
  if(!data) return
  createChart(userGrowthRef, {
    tooltip: { trigger: 'axis', ...tooltipDark },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: data.dates, ...axisDark },
    yAxis: { type: 'value', ...axisDark },
    series: [{
      data: data.counts, type: 'line', smooth: true,
      itemStyle: { color: '#818cf8' },
      lineStyle: { width: 3, shadowColor: 'rgba(129, 140, 248, 0.5)', shadowBlur: 8 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(129, 140, 248, 0.5)' }, { offset: 1, color: 'rgba(129, 140, 248, 0)' }
        ])
      }
    }]
  })
}

const initMessageTrend = (data) => {
  if(!data) return
  createChart(messageTrendRef, {
    tooltip: { trigger: 'axis', ...tooltipDark, axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.dates, ...axisDark },
    yAxis: { type: 'value', ...axisDark },
    series: [{
      name: '流转量', type: 'bar', data: data.counts,
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#fbbf24' }, { offset: 1, color: 'rgba(251, 191, 36, 0.1)' }
        ])
      }
    }]
  })
}

const initSuggestion = (data) => {
  if(!data) return
  createChart(suggestionRef, {
    tooltip: { trigger: 'item', ...tooltipDark },
    legend: { top: 'bottom', textStyle: { color: '#cbd5e1' } },
    color: ['#34d399', '#fbbf24', '#f87171'],
    series: [{
      type: 'pie', radius: ['50%', '75%'], avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: 'rgba(15, 23, 42, 0.8)', borderWidth: 3 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold', color: '#fff' } },
      data: data
    }]
  })
}

const initFunnel = (data) => {
  if(!data) return
  createChart(funnelRef, {
    tooltip: { trigger: 'item', formatter: '{b} : {c}', ...tooltipDark },
    color: ['#38bdf8', '#818cf8', '#c084fc', '#f472b6', '#34d399'],
    series: [{
      type: 'funnel', left: '10%', top: '5%', bottom: '5%', width: '80%',
      label: { show: true, position: 'inside', color: '#fff', fontWeight: 'bold' },
      itemStyle: { borderColor: 'rgba(15, 23, 42, 0.8)', borderWidth: 2, shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' },
      data: data
    }]
  })
}

const handleResize = () => echartInstances.forEach(chart => chart.resize())

onMounted(async () => {
  await adminStore.fetchStats()
  window.addEventListener('resize', handleResize)
  try {
    const res = await getDashboardStats()
    if (res.code === 200 && res.data) {
      backendStats.value = res.data
      await nextTick()
      initMonthlyCompare(res.data.monthlyCompare)
      initRadarChart(res.data.weekRadar)
      initUserGrowth(res.data.userGrowth)
      initMessageTrend(res.data.messageTrend)
      initSuggestion(res.data.suggestionStatus)
      initFunnel(res.data.itemStatus)
    }
  } catch (error) {
    console.error('加载后端数据失败:', error)
  }

  try {
    const resp = await getAllItems({ page: 1, size: 10000, type: 'all', status: 'all', keyword: '' })
    const items = Array.isArray(resp?.data?.items) ? resp.data.items : []
    const lostItems = items.filter((i) => i.type === 'lost')
    const foundItems = items.filter((i) => i.type === 'found')

    recentActivities.value = items.map((item) => ({
      id: `${item.type}-${item.id}`,
      timestamp: item.date || '',
      action: item.status === 'resolved' ? '闭环' : item.status === 'approved' ? '合规' : '入网',
      type: item.status === 'resolved' ? 'success' : item.status === 'approved' ? 'warning' : 'primary',
      description: (() => {
        const poster = item.poster ? String(item.poster) : '实体'
        const thingType = item.type === 'lost' ? '遗失' : '寻回'
        const title = item.title ? `[${item.title}]` : ''
        if (item.status === 'resolved') return `物理资产 ${title} 闭环达成`
        if (item.status === 'approved') return `节点网络放行 ${thingType}资产 ${title}`
        return `节点 ${poster} 广播了 ${thingType}事件 ${title}`
      })()
    })).sort((a, b) => (safeParseDate(b.timestamp)?.getTime() ?? 0) - (safeParseDate(a.timestamp)?.getTime() ?? 0)).slice(0, 8)

    const buildingMap = new Map()
    for (const item of lostItems) buildingMap.set(extractBuildingName(item.location), (buildingMap.get(extractBuildingName(item.location)) || 0) + 1)
    const heatmapData = Array.from(buildingMap.entries()).map(([name, value]) => ({ name, value })).sort((a, b) => b.value - a.value)

    const categoryMap = new Map()
    for (const item of lostItems) {
      const category = item.category ? String(item.category) : '未知'
      categoryMap.set(category, (categoryMap.get(category) || 0) + 1)
    }
    const pieData = Array.from(categoryMap.entries()).map(([name, value]) => ({ name, value })).sort((a, b) => b.value - a.value).slice(0, 8)

    await nextTick()
    initHeatmap(heatmapData)
    initPieChart(pieData)
    initLineChart(computeReturnRateSeries(foundItems))
    initNetworkGraph(heatmapData, items)
    
  } catch (e) {
    console.error('加载原有数据失败:', e)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  echartInstances.forEach(chart => chart.dispose())
  if (userRadarInstance) userRadarInstance.dispose()
})
</script>

<style scoped>
/* ================= 全局暗黑科技风 & 流动背景 ================= */
.dashboard.tech-theme { 
  min-height: 100vh; padding: 20px; background-color: #020617; 
  color: #e2e8f0; font-family: 'Inter', system-ui, -apple-system, sans-serif; 
  position: relative; overflow: hidden; z-index: 1;
}
.ambient-background { position: absolute; top: 0; left: 0; width: 100%; height: 100%; overflow: hidden; z-index: -1; pointer-events: none;}
.glow-blob { position: absolute; border-radius: 50%; filter: blur(120px); opacity: 0.4; animation: float 20s infinite ease-in-out;}
.blob-1 { width: 500px; height: 500px; background: #6498eb; top: -10%; left: -10%; }
.blob-2 { width: 400px; height: 400px; background: #8b5cf6; bottom: -10%; right: -5%; animation-delay: -5s;}
.blob-3 { width: 600px; height: 600px; background: #0ea5e9; top: 40%; left: 40%; animation-delay: -10s;}
@keyframes float { 0%, 100% { transform: translate(0, 0) scale(1); } 50% { transform: translate(50px, 30px) scale(1.1); } }
.dashboard-content { position: relative; z-index: 2; max-width: 1600px; margin: 0 auto;}

/* ================= 头部与 AI 状态 ================= */
.header-wrap { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 800; color: #f8fafc; margin: 0; letter-spacing: 1px;}
.title-accent { background: linear-gradient(to right, #38bdf8, #818cf8); -webkit-background-clip: text; -webkit-text-fill-color: transparent;}
.ai-status { display: flex; align-items: center; gap: 12px; padding: 8px 20px; border-radius: 30px; position: relative; overflow: hidden;}
.radar-scan { position: absolute; left: 0; top: 0; width: 20%; height: 100%; background: linear-gradient(90deg, transparent, rgba(56, 189, 248, 0.4), transparent); animation: scan-line 3s linear infinite;}
@keyframes scan-line { 0% { left: -20%; } 100% { left: 120%; } }
.pulse-dot { width: 8px; height: 8px; background-color: #38bdf8; border-radius: 50%; animation: pulse-glow 2s infinite; }
.ai-text { font-size: 13px; font-weight: 600; color: #7dd3fc; letter-spacing: 1px;}

/* ================= 创新点 1：AI 预警横幅 ================= */
.ai-alert-banner { display: flex; align-items: center; justify-content: space-between; background: rgba(244, 63, 94, 0.08); border: 1px solid rgba(244, 63, 94, 0.3); border-radius: 16px; padding: 18px 24px; margin-bottom: 24px; box-shadow: 0 0 20px rgba(244, 63, 94, 0.1) inset;}
.pulse-red-icon { font-size: 28px; color: #f43f5e; animation: pulse-red-glow 2s infinite; display: flex;}
@keyframes pulse-red-glow { 0%, 100% { opacity: 1; text-shadow: 0 0 10px #f43f5e; transform: scale(1);} 50% { opacity: 0.6; text-shadow: 0 0 20px #f43f5e; transform: scale(1.1);} }
.alert-content { flex: 1; margin: 0 20px; }
.alert-title { font-weight: 800; font-size: 16px; color: #fda4af; margin-bottom: 4px; letter-spacing: 1px;}
.alert-desc { font-size: 14px; color: #e2e8f0; line-height: 1.5;}
.highlight-danger { color: #f43f5e; font-weight: bold; text-shadow: 0 0 8px rgba(244, 63, 94, 0.6); background: rgba(244,63,94,0.1); padding: 0 4px; border-radius: 4px;}
.cyber-btn-danger { background: rgba(244, 63, 94, 0.1) !important; border: 1px solid rgba(244, 63, 94, 0.5) !important; color: #fda4af !important; font-weight: bold;}
.cyber-btn-danger:hover { background: rgba(244, 63, 94, 0.3) !important; box-shadow: 0 0 15px rgba(244,63,94,0.5); color: #fff !important;}

/* ================= 毛玻璃与组件通用 ================= */
.glass-card { background: rgba(15, 23, 42, 0.4) !important; backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px); border: 1px solid rgba(255, 255, 255, 0.08) !important; color: #e2e8f0;}
:deep(.glass-card .el-card__header) { border-bottom: 1px solid rgba(255, 255, 255, 0.05); font-weight: 700; color: #f8fafc; font-size: 16px; letter-spacing: 0.5px;}
.hover-lift { transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); }
.hover-lift:hover { transform: translateY(-5px); box-shadow: 0 15px 30px -5px rgba(0, 0, 0, 0.5), 0 0 15px rgba(56, 189, 248, 0.2) !important; border-color: rgba(56, 189, 248, 0.3) !important;}

/* 搜索与表格 */
.premium-search { margin-bottom: 24px; border-radius: 16px; }
.search-row { display: flex; flex-wrap: wrap; align-items: center; gap: 16px; }
.animated-input { width: 350px; transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1); }
.animated-input:focus-within { width: 500px; }
:deep(.cyber-input .el-input__wrapper) { background-color: rgba(15, 23, 42, 0.6); border: 1px solid rgba(255,255,255,0.1); box-shadow: none; border-radius: 20px; padding-left: 15px; transition: all 0.3s;}
:deep(.cyber-input .el-input__inner) { color: #e2e8f0; }
:deep(.cyber-input .el-input__wrapper.is-focus) { border-color: #38bdf8; box-shadow: 0 0 15px rgba(56, 189, 248, 0.3) inset, 0 0 10px rgba(56, 189, 248, 0.2) !important;}
.cyber-btn { background: linear-gradient(135deg, #0ea5e9, #3b82f6); border: none; font-weight: bold; letter-spacing: 1px; padding: 10px 24px; box-shadow: 0 4px 15px rgba(14, 165, 233, 0.4);}
.cyber-btn:hover { box-shadow: 0 6px 20px rgba(14, 165, 233, 0.6); transform: translateY(-2px); }
.glass-btn { background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: #e2e8f0; }
.glass-btn:hover { background: rgba(255,255,255,0.1); color: #fff; }
.cyber-divider { border-color: rgba(255,255,255,0.1); }
.cyber-text { color: #94a3b8; }
:deep(.cyber-table) { background-color: transparent !important; color: #cbd5e1; --el-table-border-color: rgba(255,255,255,0.05); --el-table-header-bg-color: rgba(15, 23, 42, 0.6); --el-table-header-text-color: #94a3b8; --el-table-tr-bg-color: transparent; --el-table-row-hover-bg-color: rgba(56, 189, 248, 0.1);}
:deep(.cyber-table th.el-table__cell) { background-color: rgba(15, 23, 42, 0.6) !important; border-bottom: 1px solid rgba(255,255,255,0.1); }
:deep(.cyber-table td.el-table__cell) { border-bottom: 1px solid rgba(255,255,255,0.05); }

/* 四个统计卡片 */
.stats-row { margin-bottom: 24px; }
.stat-card { border-radius: 16px; }
.stat-content { display: flex; align-items: center; gap: 20px; }
.stat-icon { width: 60px; height: 60px; border-radius: 16px; display: flex; justify-content: center; align-items: center; transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);}
.stat-card:hover .stat-icon { transform: scale(1.15) rotate(10deg); }
.neon-blue { background: rgba(56, 189, 248, 0.1); color: #38bdf8; border: 1px solid rgba(56,189,248,0.3); box-shadow: 0 0 15px rgba(56,189,248,0.2) inset; }
.neon-orange { background: rgba(251, 191, 36, 0.1); color: #fbbf24; border: 1px solid rgba(251,191,36,0.3); box-shadow: 0 0 15px rgba(251,191,36,0.2) inset; }
.neon-green { background: rgba(52, 211, 153, 0.1); color: #34d399; border: 1px solid rgba(52,211,153,0.3); box-shadow: 0 0 15px rgba(52,211,153,0.2) inset; }
.neon-purple { background: rgba(167, 139, 250, 0.1); color: #a78bfa; border: 1px solid rgba(167,139,250,0.3); box-shadow: 0 0 15px rgba(167,139,250,0.2) inset; }
.stat-info h3 { margin: 0; font-size: 36px; font-weight: 900; color: #f8fafc; line-height: 1; font-family: 'Courier New', Courier, monospace; }
.stat-info p { margin: 8px 0 0 0; font-size: 14px; color: #94a3b8; font-weight: 500; letter-spacing: 1px; }

/* 图表容器通用 */
.charts-row { margin-bottom: 24px; }
.chart-card { border-radius: 16px; height: 380px; display: flex; flex-direction: column;}
:deep(.chart-card .el-card__body) { flex: 1; padding: 15px; height: 100%; box-sizing: border-box; }
.chart-container { width: 100%; height: 100%; min-height: 280px; }

/* ================= 排行榜点击交互 ================= */
.list-card { overflow: hidden; }
.top-list { list-style: none; padding: 0; margin: 0; height: 100%; overflow-y: auto; padding-right: 5px;}
.top-item { display: flex; align-items: center; padding: 14px; border-bottom: 1px solid rgba(255,255,255,0.05); transition: all 0.3s ease; border-radius: 8px; margin-bottom: 4px;}
.clickable-node { cursor: pointer; position: relative; overflow: hidden; }
.clickable-node::before { content: ''; position: absolute; left: 0; top: 0; width: 3px; height: 100%; background: #0ea5e9; transform: scaleY(0); transition: transform 0.3s ease; }
.clickable-node:hover::before { transform: scaleY(1); }
.clickable-node:hover { background: rgba(14, 165, 233, 0.1); transform: translateX(8px); box-shadow: 0 0 15px rgba(14, 165, 233, 0.2) inset; }

.rank-badge { width: 28px; height: 28px; line-height: 28px; text-align: center; border-radius: 6px; background: rgba(255,255,255,0.1); color: #cbd5e1; font-weight: bold; margin-right: 14px; font-size: 13px; }
.top-1 { background: rgba(244, 63, 94, 0.2); color: #fb7185; border: 1px solid rgba(244,63,94,0.5); box-shadow: 0 0 10px rgba(244,63,94,0.3); }
.top-2 { background: rgba(251, 191, 36, 0.2); color: #fcd34d; border: 1px solid rgba(251,191,36,0.5); box-shadow: 0 0 10px rgba(251,191,36,0.3); }
.top-3 { background: rgba(52, 211, 153, 0.2); color: #6ee7b7; border: 1px solid rgba(52,211,153,0.5); box-shadow: 0 0 10px rgba(52,211,153,0.3); }
.user-name { flex: 1; font-size: 14px; color: #e2e8f0; font-weight: 600; font-family: monospace; letter-spacing: 0.5px;}
.post-count { color: #38bdf8; font-weight: 800; font-size: 14px; background: rgba(56, 189, 248, 0.1); padding: 4px 10px; border-radius: 12px; border: 1px solid rgba(56,189,248,0.2);}

/* 全息耗时数字卡片 */
.number-card { position: relative; overflow: hidden; }
.number-card :deep(.el-card__body) { display: flex; flex-direction: column; justify-content: center; align-items: center; z-index: 2; position: relative;}
.hologram-bg { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: radial-gradient(circle at center, rgba(56, 189, 248, 0.15) 0%, transparent 70%); z-index: 0; pointer-events: none;}
.big-number-wrap { margin-bottom: 12px; position: relative;}
.cyber-glitch-text { font-size: 72px; font-weight: 900; color: #fff; text-shadow: 0 0 20px #38bdf8, 0 0 40px #0ea5e9; line-height: 1; font-family: 'Courier New', Courier, monospace;}
.big-number-wrap .unit { font-size: 20px; color: #38bdf8; margin-left: 8px; font-weight: 800; text-transform: uppercase; letter-spacing: 2px;}
.sub-number { font-size: 14px; color: #94a3b8; background: rgba(0,0,0,0.3); padding: 8px 20px; border-radius: 20px; font-weight: 500; border: 1px solid rgba(255,255,255,0.05);}
.highlight-text { color: #38bdf8; font-weight: bold; font-size: 16px; }

/* 动态时间线 */
.activity-card { border-radius: 16px; margin-bottom: 40px;}
:deep(.cyber-timeline .el-timeline-item__tail) { border-left: 2px solid rgba(255,255,255,0.1); }
:deep(.cyber-timeline .el-timeline-item__timestamp) { color: #64748b; }
.activity-content { display: flex; align-items: center; gap: 12px; padding: 4px 0; transition: transform 0.2s; }
.activity-content:hover { transform: translateX(8px); }
.log-text { font-size: 14px; color: #cbd5e1; font-weight: 500; }
.neon-tag { border: none; box-shadow: 0 0 10px currentColor; }

/* ================= 动画 Keyframes 与进场效果 ================= */
@keyframes pulse-glow { 0%, 100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(56, 189, 248, 0.7); } 50% { transform: scale(1); box-shadow: 0 0 0 6px rgba(56, 189, 248, 0); } }
.animate-item { opacity: 0; animation: fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeUp { 0% { opacity: 0; transform: translateY(30px); } 100% { opacity: 1; transform: translateY(0); } }
.animated-list-item { opacity: 0; animation: slideInRight 0.5s forwards; }
@keyframes slideInRight { from { opacity: 0; transform: translateX(30px); } to { opacity: 1; transform: translateX(0); } }

/* 瀑布流进场延迟控制 */
.stagger-1 { animation-delay: 0.05s;} .stagger-2 { animation-delay: 0.1s; } 
.stagger-3 { animation-delay: 0.15s; } .stagger-4 { animation-delay: 0.2s; }
.stagger-5 { animation-delay: 0.25s; } .stagger-6 { animation-delay: 0.3s; } 
.stagger-7 { animation-delay: 0.35s; } .stagger-8 { animation-delay: 0.4s; }
.stagger-9 { animation-delay: 0.45s; } .stagger-10 { animation-delay: 0.5s; } 
.stagger-11 { animation-delay: 0.55s; } .stagger-12 { animation-delay: 0.6s; }
.stagger-13 { animation-delay: 0.65s; } .stagger-14 { animation-delay: 0.7s; } 
.stagger-15 { animation-delay: 0.75s; } .stagger-16 { animation-delay: 0.8s; }
.stagger-17 { animation-delay: 0.85s; } .stagger-18 { animation-delay: 0.9s; } 
.stagger-19 { animation-delay: 0.95s; }

/* 滚动条 */
::-webkit-scrollbar { width: 8px; height: 8px; }
::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.2); border-radius: 4px; }
::-webkit-scrollbar-thumb:hover { background: rgba(56, 189, 248, 0.5); }
::-webkit-scrollbar-track { background: transparent; }

/* ================= 创新点 3：特工全息画像弹窗样式 ================= */
.cyber-modal-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(2, 6, 23, 0.7); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  display: flex; justify-content: center; align-items: center; z-index: 9999;
}

.cyber-modal-enter-active, .cyber-modal-leave-active { transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.cyber-modal-enter-from, .cyber-modal-leave-to { opacity: 0; transform: scale(0.85) translateY(30px); }

.cyber-modal-content {
  width: 600px; max-width: 90vw; background: rgba(15, 23, 42, 0.75) !important;
  border: 1px solid rgba(14, 165, 233, 0.4) !important; border-radius: 20px;
  box-shadow: 0 0 50px rgba(14, 165, 233, 0.2), inset 0 0 20px rgba(14, 165, 233, 0.1);
  display: flex; flex-direction: column; overflow: hidden; position: relative;
}

.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; border-bottom: 1px solid rgba(14, 165, 233, 0.2);
  background: linear-gradient(90deg, rgba(14, 165, 233, 0.1), transparent);
}

.modal-title { margin: 0; color: #e0f2fe; font-size: 18px; display: flex; align-items: center; gap: 10px; font-family: monospace;}
.highlight-name { color: #38bdf8; font-weight: bold; text-shadow: 0 0 10px #38bdf8; font-size: 22px;}
.glitch-icon { color: #0ea5e9; animation: glitch 2s infinite; }

.close-btn { background: transparent !important; border: 1px solid rgba(14,165,233,0.3) !important; color: #7dd3fc !important; transition: all 0.3s;}
.close-btn:hover { background: rgba(14, 165, 233, 0.2) !important; color: #fff !important; transform: rotate(90deg); box-shadow: 0 0 10px #0ea5e9;}

.modal-body { position: relative; width: 100%; height: 450px; display: flex; justify-content: center; align-items: center; overflow: hidden; }

/* 钢铁侠反应炉特效 */
.arc-reactor { position: absolute; width: 300px; height: 300px; top: 50%; left: 50%; transform: translate(-50%, -50%); pointer-events: none; z-index: 1;}
.reactor-ring { position: absolute; top: 50%; left: 50%; border-radius: 50%; transform: translate(-50%, -50%); border: 2px solid transparent;}
.ring-outer { width: 100%; height: 100%; border-top: 2px dashed #0ea5e9; border-bottom: 2px dashed #0ea5e9; animation: spin 15s linear infinite; opacity: 0.5; box-shadow: 0 0 20px rgba(14,165,233,0.2) inset;}
.ring-middle { width: 80%; height: 80%; border-left: 3px solid #38bdf8; border-right: 3px solid #38bdf8; animation: spin-reverse 10s linear infinite; opacity: 0.7;}
.ring-inner { width: 60%; height: 60%; border: 2px dotted #7dd3fc; animation: spin 5s linear infinite; box-shadow: 0 0 30px #0ea5e9;}
.reactor-core { position: absolute; width: 20%; height: 20%; top: 50%; left: 50%; transform: translate(-50%, -50%); background: radial-gradient(circle, #fff 0%, #38bdf8 40%, transparent 80%); border-radius: 50%; opacity: 0.3; animation: pulse-core 2s ease-in-out infinite;}

@keyframes spin { 100% { transform: translate(-50%, -50%) rotate(360deg); } }
@keyframes spin-reverse { 100% { transform: translate(-50%, -50%) rotate(-360deg); } }
@keyframes pulse-core { 0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.2; } 50% { transform: translate(-50%, -50%) scale(1.5); opacity: 0.6; } }
@keyframes glitch { 0% { transform: translate(0) } 20% { transform: translate(-2px, 2px) } 40% { transform: translate(-2px, -2px) } 60% { transform: translate(2px, 2px) } 80% { transform: translate(2px, -2px) } 100% { transform: translate(0) } }

.user-radar-chart { width: 100%; height: 100%; z-index: 2; position: relative;}

.modal-footer { padding: 15px 24px; border-top: 1px solid rgba(14, 165, 233, 0.2); background: rgba(15, 23, 42, 0.8); }
.status-bar { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #94a3b8; font-family: monospace;}
.status-dot { width: 8px; height: 8px; background-color: #10b981; border-radius: 50%; box-shadow: 0 0 8px #10b981; animation: pulse-green 2s infinite;}
@keyframes pulse-green { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }
.text-purple { color: #c084fc; font-weight: bold; text-shadow: 0 0 8px #c084fc; font-size: 14px;}
.text-green { color: #34d399; font-weight: bold; text-shadow: 0 0 8px #34d399; font-size: 14px;}
.text-blue { color: #38bdf8; font-weight: bold; text-shadow: 0 0 8px #38bdf8; font-size: 14px;}
</style>