<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">

    <!-- User Header Section -->
    <header class="bg-gradient-to-br from-primary to-primary-container text-on-primary pb-12 pt-8 px-4">
      <div class="max-w-7xl mx-auto">
        <div class="bg-surface text-on-surface rounded-[32px] p-6 md:p-8 shadow-xl shadow-primary/20 relative overflow-hidden">

          <!-- Back Home Button (Absolute) -->
          <button
            @click="goToHome"
            class="fixed top-4 left-4 z-[90] inline-flex items-center gap-2 px-4 py-2.5 rounded-full bg-surface-container-high text-on-surface font-bold text-sm shadow-lg border border-outline-variant/30 hover:bg-surface-container-highest hover:text-primary transition-all duration-300"
          >
            <span class="material-symbols-outlined text-base">home</span>
            <span class="hidden sm:inline">返回首页</span>
          </button>

 

          <div class="flex flex-col md:flex-row items-center md:items-start gap-6 md:gap-8 mt-4 md:mt-0">

            <!-- Avatar -->
            <div class="relative group cursor-pointer" @click="$router.push('/user/profile')">
              <div class="w-24 h-24 md:w-28 md:h-28 rounded-full bg-surface-container-highest overflow-hidden border-4 border-surface shadow-lg">
                <img
                  :src="userStore.avatar || '/default-avatar.png'"
                  alt="Avatar"
                  class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  @error="$event.target.src='/default-avatar.png'"
                />
              </div>
              <div class="absolute bottom-0 right-0 w-8 h-8 bg-primary text-on-primary rounded-full flex items-center justify-center shadow-md border-2 border-surface">
                <span class="material-symbols-outlined text-sm">edit</span>
              </div>
            </div>

            <!-- User Info -->
            <div class="flex-1 text-center md:text-left">
              <h1 class="text-2xl md:text-3xl font-black text-on-surface mb-1">
                {{ userStore.realName || userStore.userName || '用户' }}
              </h1>
              <p class="text-on-surface-variant font-medium mb-6">@{{ userStore.userName || 'unknown' }}</p>

              <!-- Stats Grid -->
              <div class="grid grid-cols-3 gap-4 md:gap-8 max-w-lg mx-auto md:mx-0">
                <div class="text-center">
                  <div class="text-2xl md:text-3xl font-black text-primary">{{ userStats.lostCount || 0 }}</div>
                  <div class="text-xs md:text-sm font-medium text-on-surface-variant mt-1">失物发布</div>
                </div>
                <div class="text-center">
                  <div class="text-2xl md:text-3xl font-black text-secondary">{{ userStats.foundCount || 0 }}</div>
                  <div class="text-xs md:text-sm font-medium text-on-surface-variant mt-1">招领发布</div>
                </div>
                <div class="text-center relative">
                  <div class="text-2xl md:text-3xl font-black text-tertiary">{{ userStats.messageCount || 0 }}</div>
                  <div class="text-xs md:text-sm font-medium text-on-surface-variant mt-1">未读消息</div>
                  <!-- Badge Dot -->
                  <span v-if="userStats.messageCount > 0" class="absolute -top-1 right-1/2 translate-x-1/2 w-3 h-3 bg-error rounded-full border-2 border-surface animate-pulse"></span>
                </div>
              </div>
            </div>

            <!-- Actions -->
            <div class="flex flex-col gap-3 w-full md:w-auto">
              <button
                @click="$router.push('/user/profile')"
                class="w-full md:w-auto px-6 py-3 bg-primary text-on-primary rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 flex items-center justify-center gap-2"
              >
                <span class="material-symbols-outlined">person_edit</span>
                编辑资料
              </button>
              <button
                @click="$router.push('/user/password')"
                class="w-full md:w-auto px-6 py-3 bg-surface-container-high text-on-surface-variant hover:bg-surface-container-highest hover:text-on-surface rounded-full font-bold transition-all duration-300 flex items-center justify-center gap-2"
              >
                <span class="material-symbols-outlined">lock_reset</span>
                修改密码
              </button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content Area -->
    <main class="max-w-7xl mx-auto px-4 py-8">
      <div class="flex flex-col lg:flex-row gap-6">

        <!-- Sidebar Navigation -->
        <aside class="w-full lg:w-64 flex-shrink-0">
          <nav class="bg-surface-container-low rounded-3xl p-4 border border-surface-container-high shadow-sm sticky top-6">
            <ul class="space-y-1">

              <!-- Profile -->
              <li>
                <button
                  @click="handleMenuSelect('profile')"
                  :class="['w-full flex items-center gap-4 px-4 py-3.5 rounded-2xl font-bold text-sm transition-all duration-300', activeMenu === 'profile' ? 'bg-secondary-container text-secondary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface']"
                >
                  <span class="material-symbols-outlined" :style="materialIconFill(activeMenu === 'profile')">person</span>
                  个人信息
                </button>
              </li>

              <!-- Lost Items Submenu -->
              <li>
                <button
                  @click="toggleSubmenu('lost')"
                  :class="['w-full flex items-center justify-between px-4 py-3.5 rounded-2xl font-bold text-sm transition-all duration-300', lostOpen ? 'bg-surface-container-high text-on-surface' : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface']"
                >
                  <div class="flex items-center gap-4">
                    <span class="material-symbols-outlined" :style="materialIconFill(lostOpen)">search</span>
                    失物管理
                  </div>
                  <span class="material-symbols-outlined text-base transition-transform duration-300" :class="lostOpen ? 'rotate-180' : ''">expand_more</span>
                </button>
                <ul v-show="lostOpen" class="mt-1 ml-4 pl-4 border-l-2 border-surface-container-high space-y-1 overflow-hidden transition-all">
                  <li>
                    <button @click="handleMenuSelect('lost-list')" :class="['w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium transition-colors', activeMenu === 'lost-list' ? 'text-primary bg-primary/10' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high']">
                      我的失物
                    </button>
                  </li>
                  <li>
                    <button @click="handleMenuSelect('lost-add')" :class="['w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium transition-colors', activeMenu === 'lost-add' ? 'text-primary bg-primary/10' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high']">
                      发布失物
                    </button>
                  </li>
                </ul>
              </li>

              <!-- Found Items Submenu -->
              <li>
                <button
                  @click="toggleSubmenu('found')"
                  :class="['w-full flex items-center justify-between px-4 py-3.5 rounded-2xl font-bold text-sm transition-all duration-300', foundOpen ? 'bg-surface-container-high text-on-surface' : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface']"
                >
                  <div class="flex items-center gap-4">
                    <span class="material-symbols-outlined" :style="materialIconFill(foundOpen)">handshake</span>
                    招领管理
                  </div>
                  <span class="material-symbols-outlined text-base transition-transform duration-300" :class="foundOpen ? 'rotate-180' : ''">expand_more</span>
                </button>
                <ul v-show="foundOpen" class="mt-1 ml-4 pl-4 border-l-2 border-surface-container-high space-y-1 overflow-hidden transition-all">
                  <li>
                    <button @click="handleMenuSelect('found-list')" :class="['w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium transition-colors', activeMenu === 'found-list' ? 'text-primary bg-primary/10' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high']">
                      我的招领
                    </button>
                  </li>
                  <li>
                    <button @click="handleMenuSelect('found-add')" :class="['w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium transition-colors', activeMenu === 'found-add' ? 'text-primary bg-primary/10' : 'text-on-surface-variant hover:text-on-surface hover:bg-surface-container-high']">
                      发布招领
                    </button>
                  </li>
                </ul>
              </li>

              <!-- Messages -->
              <li>
                <button
                  @click="handleMenuSelect('messages')"
                  :class="['w-full flex items-center gap-4 px-4 py-3.5 rounded-2xl font-bold text-sm transition-all duration-300', activeMenu === 'messages' ? 'bg-secondary-container text-secondary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface']"
                >
                  <div class="relative">
                    <span class="material-symbols-outlined" :style="materialIconFill(activeMenu === 'messages')">mail</span>
                    <span v-if="userStats.messageCount > 0" class="absolute -top-1.5 -right-1.5 w-2.5 h-2.5 bg-error rounded-full border-2 border-surface-container-low"></span>
                  </div>
                  我的消息
                </button>
              </li>

              <!-- Suggestions -->
              <li>
                <button
                  @click="handleMenuSelect('suggestions')"
                  :class="['w-full flex items-center gap-4 px-4 py-3.5 rounded-2xl font-bold text-sm transition-all duration-300', activeMenu === 'suggestions' ? 'bg-secondary-container text-secondary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface']"
                >
                  <span class="material-symbols-outlined" :style="materialIconFill(activeMenu === 'suggestions')">lightbulb</span>
                  我的建议
                </button>
              </li>

              <!-- Password -->
              <li>
                <button
                  @click="handleMenuSelect('password')"
                  :class="['w-full flex items-center gap-4 px-4 py-3.5 rounded-2xl font-bold text-sm transition-all duration-300', activeMenu === 'password' ? 'bg-secondary-container text-secondary shadow-sm' : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface']"
                >
                  <span class="material-symbols-outlined" :style="materialIconFill(activeMenu === 'password')">lock</span>
                  修改密码
                </button>
              </li>

            </ul>
          </nav>
        </aside>

        <!-- Content Area -->
        <section class="flex-1 min-h-[500px]">
          <div class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
            <router-view />
          </div>
        </section>

      </div>
    </main>
  </div>
</template>

<script setup>
import { getUserFoundList } from '@/api/found'
import { getUserLostList } from '@/api/lost'
import { getUnreadCount } from '@/api/message'
import { useUserStore } from '@/store/user'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

/** Material Symbols: FILL axis — avoid escaped quotes in template (breaks Vue compiler) */
const materialIconFill = (active) => ({
  fontVariationSettings: active ? "'FILL' 1" : "'FILL' 0"
})

const userStats = ref({})
const lostOpen = ref(false)
const foundOpen = ref(false)

// Active Menu Logic
const activeMenu = computed(() => {
  const path = route.path
  if (path.includes('/user/profile')) return 'profile'
  if (path.includes('/user/lost/UserLostAdd')) return 'lost-add'
  if (path.includes('/user/UserLostList') || path.includes('/user/lost/edit/')) return 'lost-list'
  if (path.includes('/user/found/add')) return 'found-add'
  if (path.includes('/user/found') || path.includes('/user/found/edit/')) return 'found-list'
  if (path.includes('/user/messages')) return 'messages'
  if (path.includes('/user/suggestions')) return 'suggestions'
  if (path.includes('/user/password')) return 'password'
  return 'profile'
})

// Auto open submenu if active item is inside
onMounted(() => {
  loadUserStats()
  if (activeMenu.value === 'lost-list' || activeMenu.value === 'lost-add') lostOpen.value = true
  if (activeMenu.value === 'found-list' || activeMenu.value === 'found-add') foundOpen.value = true
})

watch(() => route.fullPath, () => {
  loadUserStats()
})

const loadUserStats = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      userStats.value = { lostCount: 0, foundCount: 0, messageCount: 0 }
      return
    }

    const [lostResp, foundResp, unreadResp] = await Promise.all([
      getUserLostList({ userId }),
      getUserFoundList({ userId }),
      getUnreadCount(userId)
    ])

    const lostItems = lostResp?.code === 200 ? (lostResp.data?.items || []) : []
    const foundItems = foundResp?.code === 200 ? (foundResp.data?.items || []) : []
    const unreadCount = unreadResp?.code === 200 ? (unreadResp.data?.unreadCount || 0) : 0

    userStats.value = {
      lostCount: lostItems.length,
      foundCount: foundItems.length,
      messageCount: unreadCount
    }
  } catch (error) {
    console.error('加载用户统计数据失败:', error)
    userStats.value = { lostCount: 0, foundCount: 0, messageCount: 0 }
  }
}

const toggleSubmenu = (type) => {
  if (type === 'lost') lostOpen.value = !lostOpen.value
  if (type === 'found') foundOpen.value = !foundOpen.value
}

const handleMenuSelect = (index) => {
  const routes = {
    'profile': '/user/profile',
    'lost-list': '/user/UserLostList',
    'lost-add': '/user/lost/UserLostAdd',
    'found-list': '/user/found',
    'found-add': '/user/found/add',
    'messages': '/user/messages',
    'suggestions': '/user/suggestions',
    'password': '/user/password'
  }

  if (routes[index]) {
    router.push(routes[index])
  } else {
    console.warn('未知的菜单项:', index)
  }
}

const goToHome = () => {
  router.push('/')
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
.bg-secondary { background-color: #625B71; }
.bg-tertiary { background-color: #7D5260; }
.bg-error { background-color: #B3261E; }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-on-primary { color: #FFFFFF; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-tertiary { color: #7D5260; }

.border-surface-container-high { border-color: #ECE6F0; }

/* Gradient Background Utility */
.bg-gradient-to-br {
  background-image: linear-gradient(to bottom right, var(--tw-gradient-stops));
}
.from-primary { --tw-gradient-from: #6750A4 var(--tw-gradient-from-position); --tw-gradient-to: rgba(103, 80, 164, 0) var(--tw-gradient-to-position); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to); }
.to-primary-container { --tw-gradient-to: #EADDFF var(--tw-gradient-to-position); }

.shadow-primary\/20 { --tw-shadow-color: rgba(103, 80, 164, 0.2); }
.shadow-primary\/30 { --tw-shadow-color: rgba(103, 80, 164, 0.3); }

.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.1); }
</style>