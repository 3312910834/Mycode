<template>
  <div class="home-page min-h-screen bg-[#FEF7FF] text-[#1D1B20] selection:bg-[#E8DEF8]">

    <!-- ============ 顶部公告滚动条 ============ -->
    <div v-if="announcementNotices.length > 0"
         class="bg-[#6750A4] text-white py-2 overflow-hidden relative z-[60]">
      <div class="ticker-track flex items-center gap-12 whitespace-nowrap">
        <span v-for="(a, i) in [...announcementNotices, ...announcementNotices, ...announcementNotices]"
              :key="'tk-' + i"
              class="inline-flex items-center gap-2 text-sm font-medium">
          <span class="material-symbols-outlined text-sm">campaign</span>
          {{ a.title }}
          <span class="mx-6 opacity-30">•</span>
        </span>
      </div>
    </div>

    <!-- ============ 导航栏 ============ -->
    <nav class="sticky top-0 z-50 w-full bg-[#FEF7FF]/80 backdrop-blur-2xl border-b border-[#E7E0EC]
                shadow-[0_4px_30px_rgba(103,80,164,0.06)] transition-all duration-300">
      <div class="flex justify-between items-center w-full px-6 md:px-10 py-3.5 max-w-screen-2xl mx-auto">

        <!-- Logo -->
        <div class="flex items-center gap-10">
          <div class="flex items-center gap-3 cursor-pointer group" @click="$router.push('/')">
            <div class="w-10 h-10 rounded-2xl bg-[#6750A4] flex items-center justify-center
                        shadow-lg shadow-[#6750A4]/25 group-hover:shadow-[#6750A4]/40
                        group-hover:scale-105 transition-all duration-300">
              <span class="material-symbols-outlined text-white text-xl"
                    style="font-variation-settings:'FILL' 1,'wght' 600,'GRAD' 0,'opsz' 48">
                search_hands_free
              </span>
            </div>
            <div class="flex flex-col">
              <span class="text-xl font-black text-[#6750A4] tracking-tight leading-none">识归——校园 AI 失物招领智能服务平台</span>
              <span class="text-[15px] text-[#49454F] font-semibold tracking-wider mt-0.5">CAMPUS AI LOST & FOUND</span>
            </div>
          </div>

          <!-- Desktop Nav Links -->
          <div class="hidden lg:flex items-center gap-1">
            <router-link v-for="link in navLinks" :key="link.to" :to="link.to"
                         class="nav-link px-4 py-2 rounded-xl text-sm font-bold transition-all duration-300
                                text-[#49454F] hover:text-[#6750A4] hover:bg-[#6750A4]/8 relative">
              {{ link.label }}
            </router-link>
          </div>
        </div>

        <!-- Right Actions -->
        <div class="flex items-center gap-3">

          <!-- Notification Bell -->
          <div ref="notificationRef" class="relative">
            <button @click="toggleNotifications"
                    class="relative w-10 h-10 rounded-xl hover:bg-[#6750A4]/8 flex items-center justify-center
                           transition-all duration-200 active:scale-90">
              <span class="material-symbols-outlined text-xl text-[#49454F]">notifications</span>
              <span v-if="unreadMessageCount > 0"
                    class="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] px-1 rounded-full
                           bg-[#B3261E] text-white text-[10px] font-bold flex items-center justify-center
                           animate-bounce-subtle">
                {{ unreadMessageCount > 99 ? '99+' : unreadMessageCount }}
              </span>
            </button>

            <!-- Notification Dropdown -->
            <Transition name="dropdown">
              <div v-if="notificationOpen"
                   class="absolute right-0 mt-3 w-[380px] max-w-[92vw] bg-white rounded-3xl
                          border border-[#E7E0EC] shadow-2xl shadow-[#6750A4]/10 overflow-hidden">
                <div class="px-5 py-4 border-b border-[#E7E0EC] flex items-center justify-between">
                  <h4 class="text-base font-black text-[#1D1B20]">通知中心</h4>
                  <button @click="notificationOpen = false"
                          class="w-8 h-8 rounded-lg hover:bg-[#F7F2FA] flex items-center justify-center transition-colors">
                    <span class="material-symbols-outlined text-sm text-[#49454F]">close</span>
                  </button>
                </div>
                <div v-if="notificationsLoading" class="px-5 py-10 text-center text-[#49454F] text-sm">
                  <span class="material-symbols-outlined animate-spin text-2xl text-[#6750A4] mb-2 block">progress_activity</span>
                  正在加载...
                </div>
                <div v-else class="max-h-[440px] overflow-y-auto">
                  <!-- 公告 -->
                  <div class="px-5 pt-4 pb-2">
                    <div class="flex items-center justify-between mb-3">
                      <h5 class="text-xs font-black text-[#79747E] tracking-widest uppercase">系统公告</h5>
                      <router-link to="/announcements" class="text-xs text-[#6750A4] font-bold hover:underline">全部</router-link>
                    </div>
                    <div v-if="announcementNotices.length === 0" class="text-xs text-[#79747E] py-2">暂无公告</div>
                    <button v-for="announcement in announcementNotices" :key="'na-' + announcement.id"
                            @click="openAnnouncementPage"
                            class="w-full text-left px-4 py-3 rounded-2xl hover:bg-[#F7F2FA] transition-colors mb-1">
                      <p class="text-sm font-bold text-[#1D1B20] line-clamp-1">{{ announcement.title }}</p>
                      <p class="text-xs text-[#49454F] mt-1 line-clamp-1">{{ announcement.content }}</p>
                    </button>
                  </div>
                  <!-- 消息 -->
                  <div class="px-5 pt-3 pb-4 border-t border-[#E7E0EC]">
                    <div class="flex items-center justify-between mb-3 mt-2">
                      <h5 class="text-xs font-black text-[#79747E] tracking-widest uppercase">我的消息</h5>
                      <router-link to="/user/messages" class="text-xs text-[#6750A4] font-bold hover:underline">全部</router-link>
                    </div>
                    <div v-if="!isLogin" class="text-xs text-[#79747E] py-2">登录后可查看</div>
                    <div v-else-if="messageNotices.length === 0" class="text-xs text-[#79747E] py-2">暂无消息</div>
                    <button v-for="msg in messageNotices" :key="'nm-' + msg.id"
                            @click="openMessageDetail(msg.id)"
                            class="w-full text-left px-4 py-3 rounded-2xl hover:bg-[#F7F2FA] transition-colors mb-1">
                      <div class="flex items-center justify-between gap-2">
                        <p class="text-sm font-bold text-[#1D1B20] line-clamp-1">{{ msg.fromUserName || '系统' }}</p>
                        <span v-if="msg.status === 'unread'"
                              class="px-2 py-0.5 rounded-full bg-[#B3261E]/10 text-[#B3261E] text-[10px] font-black">
                          新
                        </span>
                      </div>
                      <p class="text-xs text-[#49454F] mt-1 line-clamp-1">{{ msg.content || '无内容' }}</p>
                    </button>
                  </div>
                </div>
              </div>
            </Transition>
          </div>

          <!-- User Avatar / Login -->
          <template v-if="isLogin">
            <div class="relative group">
              <button class="w-10 h-10 rounded-xl bg-[#E8DEF8] flex items-center justify-center overflow-hidden
                             border-2 border-transparent hover:border-[#6750A4] transition-all duration-300
                             group-hover:shadow-lg group-hover:shadow-[#6750A4]/20">
                <img :src="userStore.avatar || '/default-avatar.png'" alt="用户" class="w-full h-full object-cover"/>
              </button>
              <div class="absolute right-0 mt-2 w-52 bg-white rounded-2xl shadow-2xl shadow-[#6750A4]/10
                          opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200
                          transform origin-top-right z-50 overflow-hidden border border-[#E7E0EC] py-2">
                <a @click="$router.push('/user/profile')"
                   class="flex items-center gap-3 px-4 py-2.5 text-sm text-[#1D1B20] hover:bg-[#F7F2FA] cursor-pointer transition-colors">
                  <span class="material-symbols-outlined text-lg text-[#6750A4]">person</span> 个人中心
                </a>
                <a @click="$router.push('/user/UserLostList')"
                   class="flex items-center gap-3 px-4 py-2.5 text-sm text-[#1D1B20] hover:bg-[#F7F2FA] cursor-pointer transition-colors">
                  <span class="material-symbols-outlined text-lg text-[#6750A4]">search</span> 我的遗失
                </a>
                <a @click="$router.push('/user/found')"
                   class="flex items-center gap-3 px-4 py-2.5 text-sm text-[#1D1B20] hover:bg-[#F7F2FA] cursor-pointer transition-colors">
                  <span class="material-symbols-outlined text-lg text-[#6750A4]">inventory_2</span> 我的拾获
                </a>
                <a @click="$router.push('/user/messages')"
                   class="flex items-center gap-3 px-4 py-2.5 text-sm text-[#1D1B20] hover:bg-[#F7F2FA] cursor-pointer transition-colors">
                  <span class="material-symbols-outlined text-lg text-[#6750A4]">mail</span> 我的消息
                </a>
                <div class="border-t border-[#E7E0EC] my-1.5"></div>
                <a @click="handleLogout"
                   class="flex items-center gap-3 px-4 py-2.5 text-sm text-[#B3261E] hover:bg-[#B3261E]/5 cursor-pointer transition-colors">
                  <span class="material-symbols-outlined text-lg">logout</span> 退出登录
                </a>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="hidden sm:flex gap-2">
              <button @click="$router.push('/login')"
                      class="px-5 py-2.5 text-sm font-bold text-[#6750A4] hover:bg-[#6750A4]/8 rounded-xl transition-all">
                登录
              </button>
              <button @click="$router.push('/register')"
                      class="px-5 py-2.5 text-sm font-bold bg-[#6750A4] text-white rounded-xl
                             hover:bg-[#4F378B] transition-all shadow-lg shadow-[#6750A4]/25
                             hover:shadow-[#6750A4]/40 hover:-translate-y-0.5">
                注册
              </button>
            </div>
          </template>

          <!-- Mobile Menu Toggle -->
          <button @click="mobileMenuOpen = !mobileMenuOpen"
                  class="lg:hidden w-10 h-10 rounded-xl hover:bg-[#6750A4]/8 flex items-center justify-center transition-colors">
            <span class="material-symbols-outlined text-xl text-[#49454F]">
              {{ mobileMenuOpen ? 'close' : 'menu' }}
            </span>
          </button>
        </div>
      </div>

      <!-- Mobile Drawer -->
      <Transition name="slide-down">
        <div v-if="mobileMenuOpen"
             class="lg:hidden border-t border-[#E7E0EC] bg-white/95 backdrop-blur-2xl px-6 py-4 space-y-1">
          <router-link v-for="link in navLinks" :key="'m-' + link.to" :to="link.to"
                       @click="mobileMenuOpen = false"
                       class="block px-4 py-3 rounded-xl text-sm font-bold text-[#49454F]
                              hover:text-[#6750A4] hover:bg-[#F7F2FA] transition-all">
            {{ link.label }}
          </router-link>
          <div v-if="!isLogin" class="flex gap-2 pt-3 border-t border-[#E7E0EC] mt-3">
            <button @click="$router.push('/login'); mobileMenuOpen = false"
                    class="flex-1 py-3 text-sm font-bold text-[#6750A4] rounded-xl border border-[#6750A4]/30">
              登录
            </button>
            <button @click="$router.push('/register'); mobileMenuOpen = false"
                    class="flex-1 py-3 text-sm font-bold bg-[#6750A4] text-white rounded-xl">
              注册
            </button>
          </div>
        </div>
      </Transition>
    </nav>

    <!-- ============ MAIN ============ -->
    <main>

      <!-- ============ HERO ============ -->
      <section class="relative overflow-hidden">
        <!-- Animated Gradient Background -->
        <div class="hero-gradient absolute inset-0"></div>

        <!-- Floating Decorations -->
        <div class="absolute inset-0 overflow-hidden pointer-events-none">
          <div class="floating-orb orb-1"></div>
          <div class="floating-orb orb-2"></div>
          <div class="floating-orb orb-3"></div>
          <div class="floating-orb orb-4"></div>
          <!-- Grid Pattern -->
          <div class="absolute inset-0 opacity-[0.03]"
               style="background-image: radial-gradient(circle, white 1px, transparent 1px); background-size: 40px 40px;">
          </div>
        </div>

        <div class="relative z-10 max-w-7xl mx-auto px-6 pt-16 pb-24 md:pt-24 md:pb-32">

          <!-- Hero Text -->
          <div class="text-center mb-14 scroll-animate">
            <div class="inline-flex items-center gap-2.5 px-5 py-2.5 bg-white/15 backdrop-blur-md rounded-full
                        text-white/90 text-sm font-semibold mb-8 border border-white/10">
              <span class="relative flex h-2.5 w-2.5">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-400"></span>
              </span>
              已帮助 <strong class="text-white">{{ stats.successCount || 0 }}</strong> 位同学成功找回物品
            </div>

            <h1 class="text-4xl sm:text-5xl md:text-7xl font-black text-white mb-6 leading-[1.1] tracking-tight">
              丢了东西？<br class="md:hidden"/>
              <span class="hero-gradient-text">AI帮你找回</span>
            </h1>
            <p class="text-lg md:text-xl text-white/75 max-w-2xl mx-auto leading-relaxed font-medium">
              上传图片智能识别 · 自然语言描述匹配 · 一键发布快速找回
            </p>
          </div>

          <!-- Search & AI Dual Cards -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 max-w-5xl mx-auto scroll-animate"
               style="animation-delay: 0.15s">

            <!-- Search Card -->
            <div class="hero-card group relative bg-white/95 backdrop-blur-2xl rounded-[1.75rem] p-7
                        shadow-2xl shadow-black/10 border border-white/50
                        hover:shadow-[0_25px_60px_rgba(103,80,164,0.15)] transition-all duration-500">
              <div class="flex items-center gap-3 mb-5">
                <div class="w-10 h-10 rounded-2xl bg-[#E8DEF8] flex items-center justify-center">
                  <span class="material-symbols-outlined text-[#6750A4] text-xl"
                        style="font-variation-settings:'FILL' 1">search</span>
                </div>
                <div>
                  <h3 class="font-black text-[#1D1B20] text-lg leading-tight">关键词搜索</h3>
                  <p class="text-xs text-[#49454F] mt-0.5">搜索物品名称、地点、描述等</p>
                </div>
              </div>

              <div class="relative mb-4">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-[#79747E]">
                  <span class="material-symbols-outlined text-xl">search</span>
                </span>
                <input v-model="searchKeyword" @keyup.enter="handleSearch" type="text"
                       class="w-full h-14 pl-12 pr-28 bg-[#F7F2FA] text-[#1D1B20] rounded-2xl text-base
                              border border-[#E7E0EC] focus:border-[#6750A4] focus:ring-4
                              focus:ring-[#6750A4]/10 transition-all outline-none
                              placeholder:text-[#79747E]"
                       placeholder="输入关键词搜索..."/>
                <button @click="handleSearch"
                        class="absolute right-2 top-2 bottom-2 px-5 bg-[#6750A4] text-white rounded-xl
                               font-bold text-sm hover:bg-[#4F378B] transition-all
                               shadow-lg shadow-[#6750A4]/25 active:scale-95">
                  搜索
                </button>
              </div>

              <div class="flex gap-2">
                <button @click="$router.push('/lost')"
                        class="flex-1 py-3 rounded-xl bg-[#FEF7FF] text-[#6750A4] text-sm font-bold
                               border border-[#E7E0EC] hover:bg-[#E8DEF8]/50 transition-all">
                  浏览失物
                </button>
                <button @click="$router.push('/found')"
                        class="flex-1 py-3 rounded-xl bg-[#FEF7FF] text-[#6750A4] text-sm font-bold
                               border border-[#E7E0EC] hover:bg-[#E8DEF8]/50 transition-all">
                  浏览招领
                </button>
              </div>
            </div>

            <!-- AI Card -->
            <div class="hero-card group relative bg-white/95 backdrop-blur-2xl rounded-[1.75rem] p-7
                        shadow-2xl shadow-black/10 border border-white/50
                        hover:shadow-[0_25px_60px_rgba(103,80,164,0.15)] transition-all duration-500">
              <div class="flex items-center justify-between mb-5">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-2xl bg-gradient-to-br from-[#6750A4] to-[#7D5260]
                              flex items-center justify-center shadow-lg shadow-[#6750A4]/20">
                    <span class="material-symbols-outlined text-white text-xl"
                          style="font-variation-settings:'FILL' 1">smart_toy</span>
                  </div>
                  <div>
                    <h3 class="font-black text-[#1D1B20] text-lg leading-tight">AI 智能助手</h3>
                    <p class="text-xs text-[#49454F] mt-0.5">用自然语言描述，AI帮你匹配</p>
                  </div>
                </div>
                <div class="px-2.5 py-1 rounded-full bg-gradient-to-r from-[#6750A4] to-[#7D5260]
                            text-white text-[10px] font-black tracking-wider animate-pulse-slow">
                  AI
                </div>
              </div>

              <textarea v-model="aiSearchDescription" rows="2"
                        @keyup.ctrl.enter="handleAiSearch"
                        class="w-full px-4 py-3.5 bg-[#F7F2FA] text-[#1D1B20] rounded-2xl text-sm
                               border border-[#E7E0EC] focus:border-[#6750A4] focus:ring-4
                               focus:ring-[#6750A4]/10 transition-all outline-none resize-none
                               placeholder:text-[#79747E] mb-4"
                        placeholder="例如：我在图书馆三楼丢了一个黑色的苹果耳机..."></textarea>

              <div class="flex gap-2">
                <button @click="handleAiSearch"
                        class="flex-1 py-3 rounded-xl bg-gradient-to-r from-[#6750A4] to-[#7D5260]
                               text-white font-bold text-sm shadow-lg shadow-[#6750A4]/25
                               hover:shadow-xl hover:-translate-y-0.5 transition-all active:scale-95">
                  AI 智能匹配
                </button>
                <button @click="$router.push('/ai-search')"
                        class="py-3 px-5 rounded-xl bg-[#FEF7FF] text-[#6750A4] font-bold text-sm
                               border border-[#E7E0EC] hover:bg-[#E8DEF8]/50 transition-all">
                  进入聊天 →
                </button>
              </div>

              <p class="text-[11px] text-[#79747E] mt-3 flex items-center gap-1">
                <span class="material-symbols-outlined text-xs">keyboard</span>
                Ctrl + Enter 快捷发送
              </p>
            </div>
          </div>
        </div>

        <!-- Wave Divider -->
        <div class="absolute bottom-0 left-0 right-0">
          <svg viewBox="0 0 1440 100" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full">
            <path d="M0 50C360 100 720 0 1080 50C1260 75 1380 60 1440 50V100H0V50Z" fill="#FEF7FF"/>
          </svg>
        </div>
      </section>

      <!-- ============ STATS ============ -->
      <section class="py-16 md:py-20 bg-[#1D1B20] relative overflow-hidden">
        <div class="absolute inset-0 opacity-10"
             style="background-image: radial-gradient(circle at 20% 50%, #6750A4 0%, transparent 50%),
                    radial-gradient(circle at 80% 50%, #7D5260 0%, transparent 50%);">
        </div>
        <div class="relative z-10 max-w-6xl mx-auto px-6">
          <div class="text-center mb-12 scroll-animate">
            <h2 class="text-3xl md:text-4xl font-black text-white mb-3 tracking-tight">平台数据一览</h2>
            <p class="text-white/50 text-sm">实时更新，见证每一次温暖的归还</p>
          </div>
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-6 scroll-animate" style="animation-delay:0.1s">
            <div v-for="stat in statCards" :key="stat.key"
                 class="stat-card group bg-white/5 backdrop-blur border border-white/10 rounded-3xl p-6
                        hover:bg-white/10 transition-all duration-500 text-center cursor-default">
              <div class="w-14 h-14 rounded-2xl mx-auto mb-4 flex items-center justify-center
                          transition-transform duration-500 group-hover:scale-110 group-hover:rotate-3"
                   :class="stat.bgClass">
                <span class="material-symbols-outlined text-2xl" :class="stat.iconClass"
                      style="font-variation-settings:'FILL' 1">{{ stat.icon }}</span>
              </div>
              <div class="text-4xl md:text-5xl font-black mb-2 tracking-tighter" :class="stat.numClass">
                {{ animatedStats[stat.key] || 0 }}
              </div>
              <div class="text-white/50 text-sm font-medium">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- ============ HOW IT WORKS ============ -->
      <section class="py-20 md:py-28 bg-[#FEF7FF]">
        <div class="max-w-6xl mx-auto px-6">
          <div class="text-center mb-16 scroll-animate">
            <span class="inline-block px-4 py-1.5 rounded-full bg-[#E8DEF8] text-[#6750A4] text-xs
                         font-black tracking-widest uppercase mb-4">
              简单四步
            </span>
            <h2 class="text-3xl md:text-5xl font-black text-[#1D1B20] tracking-tight mb-4">
              如何使用识归？
            </h2>
            <p class="text-[#49454F] max-w-xl mx-auto">
              无论你是丢失还是拾获，只需简单几步即可完成
            </p>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8 scroll-animate"
               style="animation-delay:0.1s">
            <div v-for="(step, idx) in howItWorksSteps" :key="idx"
                 class="step-card group relative bg-white rounded-3xl p-7 border border-[#E7E0EC]
                        hover:border-[#6750A4]/30 hover:shadow-xl hover:shadow-[#6750A4]/8
                        transition-all duration-500 hover:-translate-y-2">
              <!-- Step Number -->
              <div class="absolute -top-4 -left-2 w-10 h-10 rounded-2xl bg-[#6750A4] text-white
                          flex items-center justify-center font-black text-sm
                          shadow-lg shadow-[#6750A4]/30 group-hover:scale-110 transition-transform">
                {{ String(idx + 1).padStart(2, '0') }}
              </div>
              <div class="w-16 h-16 rounded-2xl mb-5 mt-2 flex items-center justify-center
                          transition-all duration-500 group-hover:scale-110"
                   :class="step.bgClass">
                <span class="material-symbols-outlined text-3xl"
                      :class="step.iconClass"
                      style="font-variation-settings:'FILL' 1">{{ step.icon }}</span>
              </div>
              <h3 class="font-black text-[#1D1B20] text-lg mb-2">{{ step.title }}</h3>
              <p class="text-[#49454F] text-sm leading-relaxed">{{ step.desc }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- ============ LATEST ITEMS ============ -->
      <section class="py-20 md:py-28 bg-[#F7F2FA]">
        <div class="max-w-7xl mx-auto px-6">
          <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-10 scroll-animate">
            <div>
              <span class="inline-block px-4 py-1.5 rounded-full bg-[#E8DEF8] text-[#6750A4] text-xs
                           font-black tracking-widest uppercase mb-4">
                实时更新
              </span>
              <h2 class="text-3xl md:text-5xl font-black text-[#1D1B20] tracking-tight">最新动态</h2>
            </div>
            <!-- Tab Switcher -->
            <div class="flex bg-white rounded-2xl p-1.5 border border-[#E7E0EC] shadow-sm">
              <button @click="activeItemTab = 'lost'"
                      :class="activeItemTab === 'lost'
                        ? 'bg-[#6750A4] text-white shadow-lg shadow-[#6750A4]/25'
                        : 'text-[#49454F] hover:text-[#6750A4]'"
                      class="px-6 py-2.5 rounded-xl text-sm font-bold transition-all duration-300">
                遗失物品
              </button>
              <button @click="activeItemTab = 'found'"
                      :class="activeItemTab === 'found'
                        ? 'bg-[#6750A4] text-white shadow-lg shadow-[#6750A4]/25'
                        : 'text-[#49454F] hover:text-[#6750A4]'"
                      class="px-6 py-2.5 rounded-xl text-sm font-bold transition-all duration-300">
                拾获物品
              </button>
            </div>
          </div>

          <!-- Loading -->
          <div v-if="loading.lost || loading.found"
               class="text-center py-20 text-[#49454F] scroll-animate">
            <span class="material-symbols-outlined animate-spin text-4xl text-[#6750A4] mb-3 block">
              progress_activity
            </span>
            <p class="text-sm font-medium">正在加载最新物品...</p>
          </div>

          <!-- Empty -->
          <div v-else-if="currentTabItems.length === 0"
               class="text-center py-20 bg-white rounded-3xl border-2 border-dashed border-[#E7E0EC] scroll-animate">
            <span class="material-symbols-outlined text-5xl text-[#CAC4D0] mb-3 block">inbox</span>
            <p class="text-[#49454F] font-medium">暂无{{ activeItemTab === 'lost' ? '遗失' : '拾获' }}物品</p>
          </div>

          <!-- Item Cards Grid -->
          <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 scroll-animate"
               style="animation-delay:0.1s">
            <div v-for="(item, idx) in currentTabItems.slice(0, 6)" :key="activeItemTab + '-' + item.id"
                 class="item-card group bg-white rounded-3xl overflow-hidden border border-[#E7E0EC]
                        hover:border-[#6750A4]/20 shadow-sm hover:shadow-xl hover:shadow-[#6750A4]/8
                        transition-all duration-500 cursor-pointer hover:-translate-y-2"
                 :style="{ animationDelay: idx * 0.05 + 's' }"
                 @click="activeItemTab === 'lost' ? viewLostItem(item.id) : viewFoundItem(item.id)">
              <!-- Image -->
              <div class="relative h-48 overflow-hidden bg-[#F7F2FA]">
                <img :src="getItemImage(item, activeItemTab === 'found')"
                     :alt="item.title"
                     class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"/>
                <div class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent opacity-0
                            group-hover:opacity-100 transition-opacity duration-500"></div>
                <!-- Tag -->
                <div class="absolute top-3 left-3 px-3 py-1 rounded-full text-[11px] font-black uppercase
                            tracking-wider shadow-lg"
                     :class="activeItemTab === 'lost'
                       ? 'bg-[#B3261E] text-white'
                       : 'bg-[#6750A4] text-white'">
                  {{ activeItemTab === 'lost' ? '遗失' : '拾获' }}
                </div>
                <!-- Time -->
                <div class="absolute bottom-3 right-3 px-3 py-1 bg-black/50 backdrop-blur rounded-full
                            text-white text-[11px] font-medium">
                  {{ formatDateRelative(activeItemTab === 'lost' ? item.lostTime : item.foundTime) }}
                </div>
              </div>
              <!-- Content -->
              <div class="p-5">
                <h3 class="font-black text-[#1D1B20] text-lg mb-2 line-clamp-1 group-hover:text-[#6750A4]
                           transition-colors">
                  {{ item.title }}
                </h3>
                <p class="text-[#49454F] text-sm mb-4 line-clamp-2 leading-relaxed">
                  {{ item.description || '暂无描述，点击查看详情...' }}
                </p>
                <div class="flex items-center gap-2 text-xs text-[#79747E]">
                  <span class="material-symbols-outlined text-sm">location_on</span>
                  <span class="line-clamp-1">
                    {{ activeItemTab === 'lost' ? item.lostLocation : item.foundLocation }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- View More -->
          <div class="text-center mt-10 scroll-animate" style="animation-delay:0.2s">
            <button @click="$router.push(activeItemTab === 'lost' ? '/lost' : '/found')"
                    class="inline-flex items-center gap-2 px-8 py-3.5 rounded-2xl bg-white text-[#6750A4]
                           font-bold border border-[#E7E0EC] hover:bg-[#6750A4] hover:text-white
                           hover:border-[#6750A4] transition-all duration-300 shadow-sm
                           hover:shadow-lg hover:shadow-[#6750A4]/20">
              查看全部{{ activeItemTab === 'lost' ? '遗失' : '拾获' }}物品
              <span class="material-symbols-outlined text-lg">arrow_forward</span>
            </button>
          </div>
        </div>
      </section>

      <!-- ============ QUICK ACTIONS ============ -->
      <section class="py-20 md:py-28 bg-[#FEF7FF]">
        <div class="max-w-6xl mx-auto px-6">
          <div class="text-center mb-14 scroll-animate">
            <span class="inline-block px-4 py-1.5 rounded-full bg-[#E8DEF8] text-[#6750A4] text-xs
                         font-black tracking-widest uppercase mb-4">
              快捷入口
            </span>
            <h2 class="text-3xl md:text-5xl font-black text-[#1D1B20] tracking-tight mb-4">
              我想要...
            </h2>
            <p class="text-[#49454F] max-w-xl mx-auto">
              快速发布信息，让丢失的物品更快被找到
            </p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 scroll-animate" style="animation-delay:0.1s">
            <!-- Report Lost -->
            <button @click="handleQuickAction('reportLost')"
                    class="action-card group relative bg-gradient-to-br from-[#B3261E] to-[#8C1D18]
                           rounded-3xl p-8 text-left text-white overflow-hidden
                           hover:shadow-2xl hover:shadow-[#B3261E]/25 transition-all duration-500
                           hover:-translate-y-2 active:scale-[0.98]">
              <div class="absolute -right-6 -top-6 opacity-10 group-hover:opacity-20
                          group-hover:scale-110 transition-all duration-700">
                <span class="material-symbols-outlined text-[10rem]"
                      style="font-variation-settings:'FILL' 1">campaign</span>
              </div>
              <div class="relative z-10">
                <div class="w-14 h-14 rounded-2xl bg-white/20 backdrop-blur flex items-center justify-center mb-6
                            group-hover:scale-110 group-hover:bg-white/30 transition-all">
                  <span class="material-symbols-outlined text-3xl">campaign</span>
                </div>
                <h3 class="text-2xl font-black mb-2">发布失物</h3>
                <p class="text-white/70 text-sm leading-relaxed">
                  丢失了物品？发布寻物启事让更多人帮忙寻找
                </p>
                <div class="mt-6 inline-flex items-center gap-1 text-sm font-bold
                            group-hover:gap-3 transition-all">
                  立即发布 <span class="material-symbols-outlined text-base">arrow_forward</span>
                </div>
              </div>
            </button>

            <!-- Report Found -->
            <button @click="handleQuickAction('reportFound')"
                    class="action-card group relative bg-gradient-to-br from-[#6750A4] to-[#4F378B]
                           rounded-3xl p-8 text-left text-white overflow-hidden
                           hover:shadow-2xl hover:shadow-[#6750A4]/25 transition-all duration-500
                           hover:-translate-y-2 active:scale-[0.98]">
              <div class="absolute -right-6 -top-6 opacity-10 group-hover:opacity-20
                          group-hover:scale-110 transition-all duration-700">
                <span class="material-symbols-outlined text-[10rem]"
                      style="font-variation-settings:'FILL' 1">add_circle</span>
              </div>
              <div class="relative z-10">
                <div class="w-14 h-14 rounded-2xl bg-white/20 backdrop-blur flex items-center justify-center mb-6
                            group-hover:scale-110 group-hover:bg-white/30 transition-all">
                  <span class="material-symbols-outlined text-3xl">add_circle</span>
                </div>
                <h3 class="text-2xl font-black mb-2">发布招领</h3>
                <p class="text-white/70 text-sm leading-relaxed">
                  拍照上传，AI自动识别物品信息并快速发布
                </p>
                <div class="mt-6 inline-flex items-center gap-1 text-sm font-bold
                            group-hover:gap-3 transition-all">
                  立即发布 <span class="material-symbols-outlined text-base">arrow_forward</span>
                </div>
              </div>
            </button>

            <!-- AI Search -->
            <button @click="$router.push('/ai-search')"
                    class="action-card group relative bg-white rounded-3xl p-8 text-left
                           border-2 border-[#E7E0EC] overflow-hidden
                           hover:border-[#6750A4]/30 hover:shadow-2xl hover:shadow-[#6750A4]/10
                           transition-all duration-500 hover:-translate-y-2 active:scale-[0.98]">
              <div class="absolute -right-6 -top-6 opacity-5 group-hover:opacity-10
                          group-hover:scale-110 transition-all duration-700">
                <span class="material-symbols-outlined text-[10rem] text-[#6750A4]"
                      style="font-variation-settings:'FILL' 1">smart_toy</span>
              </div>
              <div class="relative z-10">
                <div class="w-14 h-14 rounded-2xl bg-[#E8DEF8] flex items-center justify-center mb-6
                            group-hover:scale-110 transition-all">
                  <span class="material-symbols-outlined text-3xl text-[#6750A4]">smart_toy</span>
                </div>
                <h3 class="text-2xl font-black text-[#1D1B20] mb-2">AI 智能聊天</h3>
                <p class="text-[#49454F] text-sm leading-relaxed">
                  用日常语言描述你的物品，AI帮你精准匹配
                </p>
                <div class="mt-6 inline-flex items-center gap-1 text-sm font-bold text-[#6750A4]
                            group-hover:gap-3 transition-all">
                  开始对话 <span class="material-symbols-outlined text-base">arrow_forward</span>
                </div>
              </div>
            </button>
          </div>
        </div>
      </section>

      <!-- ============ BENTO FEATURES ============ -->
      <section class="py-20 md:py-28 bg-[#1D1B20] relative overflow-hidden">
        <div class="absolute inset-0 opacity-10"
             style="background-image: radial-gradient(circle at 30% 20%, #6750A4 0%, transparent 40%),
                    radial-gradient(circle at 70% 80%, #7D5260 0%, transparent 40%);">
        </div>
        <div class="relative z-10 max-w-6xl mx-auto px-6">
          <div class="text-center mb-14 scroll-animate">
            <span class="inline-block px-4 py-1.5 rounded-full bg-white/10 text-white/80 text-xs
                         font-black tracking-widest uppercase mb-4">
              平台特色
            </span>
            <h2 class="text-3xl md:text-5xl font-black text-white tracking-tight mb-4">
              为什么选择识归？
            </h2>
          </div>

          <!-- Bento Grid -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-5 scroll-animate" style="animation-delay:0.1s">
            <!-- Large Card -->
            <div class="md:col-span-2 bg-white/5 backdrop-blur border border-white/10 rounded-3xl p-8
                        hover:bg-white/8 transition-all duration-500 group">
              <div class="flex items-start justify-between mb-6">
                <div class="w-14 h-14 rounded-2xl bg-[#E8DEF8]/20 flex items-center justify-center">
                  <span class="material-symbols-outlined text-3xl text-[#D0BCFF]"
                        style="font-variation-settings:'FILL' 1">image_search</span>
                </div>
                <span class="px-3 py-1 rounded-full bg-[#6750A4]/30 text-[#D0BCFF] text-[11px] font-bold">
                  核心功能
                </span>
              </div>
              <h3 class="text-2xl font-black text-white mb-3">AI 图像识别 & 智能匹配</h3>
              <p class="text-white/50 leading-relaxed text-sm">
                上传物品照片，AI自动识别物品类型、颜色、品牌等特征，并与数据库中的失物/招领信息进行智能匹配。
                结合自然语言处理，让找物变得前所未有的简单。
              </p>
            </div>

            <!-- Small Card -->
            <div class="bg-gradient-to-br from-[#6750A4]/30 to-[#7D5260]/20 backdrop-blur border border-white/10
                        rounded-3xl p-8 hover:from-[#6750A4]/40 transition-all duration-500">
              <div class="w-14 h-14 rounded-2xl bg-white/10 flex items-center justify-center mb-6">
                <span class="material-symbols-outlined text-3xl text-[#D0BCFF]"
                      style="font-variation-settings:'FILL' 1">speed</span>
              </div>
              <h3 class="text-xl font-black text-white mb-2">极速响应</h3>
              <p class="text-white/50 text-sm leading-relaxed">
                实时消息通知，第一时间获取匹配结果和认领进展
              </p>
            </div>

            <!-- Small Cards Row -->
            <div class="bg-white/5 backdrop-blur border border-white/10 rounded-3xl p-8
                        hover:bg-white/8 transition-all duration-500">
              <div class="w-14 h-14 rounded-2xl bg-white/10 flex items-center justify-center mb-6">
                <span class="material-symbols-outlined text-3xl text-[#D0BCFF]"
                      style="font-variation-settings:'FILL' 1">shield</span>
              </div>
              <h3 class="text-xl font-black text-white mb-2">安全可信</h3>
              <p class="text-white/50 text-sm leading-relaxed">
                实名认证体系，确保每一次归还安全可靠
              </p>
            </div>

            <div class="bg-white/5 backdrop-blur border border-white/10 rounded-3xl p-8
                        hover:bg-white/8 transition-all duration-500">
              <div class="w-14 h-14 rounded-2xl bg-white/10 flex items-center justify-center mb-6">
                <span class="material-symbols-outlined text-3xl text-[#D0BCFF]"
                      style="font-variation-settings:'FILL' 1">group</span>
              </div>
              <h3 class="text-xl font-black text-white mb-2">社区互助</h3>
              <p class="text-white/50 text-sm leading-relaxed">
                连接全校师生，构建温暖互助的校园社区
              </p>
            </div>

            <div class="bg-white/5 backdrop-blur border border-white/10 rounded-3xl p-8
                        hover:bg-white/8 transition-all duration-500">
              <div class="w-14 h-14 rounded-2xl bg-white/10 flex items-center justify-center mb-6">
                <span class="material-symbols-outlined text-3xl text-[#D0BCFF]"
                      style="font-variation-settings:'FILL' 1">notifications_active</span>
              </div>
              <h3 class="text-xl font-black text-white mb-2">智能推送</h3>
              <p class="text-white/50 text-sm leading-relaxed">
                新信息自动匹配并推送，不错过任何找回机会
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- ============ CTA ============ -->
      <section class="py-20 md:py-28 bg-[#FEF7FF]">
        <div class="max-w-4xl mx-auto px-6 scroll-animate">
          <div class="relative bg-gradient-to-br from-[#6750A4] to-[#4F378B] rounded-[2.5rem] p-10 md:p-16
                      text-center overflow-hidden shadow-2xl shadow-[#6750A4]/20">
            <!-- Decorations -->
            <div class="absolute inset-0 overflow-hidden pointer-events-none">
              <div class="absolute -top-20 -right-20 w-60 h-60 bg-white/5 rounded-full blur-2xl"></div>
              <div class="absolute -bottom-20 -left-20 w-60 h-60 bg-white/5 rounded-full blur-2xl"></div>
            </div>

            <div class="relative z-10">
              <h2 class="text-3xl md:text-5xl font-black text-white mb-4 tracking-tight leading-tight">
                准备好找回你的物品了吗？
              </h2>
              <p class="text-white/70 text-lg mb-10 max-w-lg mx-auto">
                加入识归平台，让我们一起守护校园每一件失物
              </p>
              <div class="flex flex-col sm:flex-row gap-4 justify-center">
                <button @click="handleQuickAction('reportLost')"
                        class="px-8 py-4 bg-white text-[#6750A4] rounded-2xl font-black text-lg
                               shadow-xl hover:shadow-2xl hover:-translate-y-1 transition-all active:scale-95">
                  我丢了东西
                </button>
                <button @click="handleQuickAction('reportFound')"
                        class="px-8 py-4 bg-white/15 backdrop-blur text-white rounded-2xl font-black text-lg
                               border border-white/20 hover:bg-white/25 hover:-translate-y-1
                               transition-all active:scale-95">
                  我捡到了东西
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- ============ 登录用户状态 ============ -->
      <section v-if="isLogin" class="py-12 bg-[#F7F2FA]">
        <div class="max-w-6xl mx-auto px-6">
          <div class="bg-white rounded-3xl border border-[#E7E0EC] p-8 flex flex-col md:flex-row
                      items-center gap-8 scroll-animate">
            <div class="flex items-center gap-4">
              <span class="relative flex h-4 w-4">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#6750A4] opacity-75"></span>
                <span class="relative inline-flex rounded-full h-4 w-4 bg-[#6750A4]"></span>
              </span>
              <div>
                <h3 class="font-black text-[#1D1B20] text-lg">实时监控中</h3>
                <p class="text-[#49454F] text-sm">系统正在为您持续匹配可能的物品信息</p>
              </div>
            </div>
            <div class="flex-1 bg-[#F7F2FA] rounded-2xl p-4 w-full md:w-auto">
              <div class="flex items-center justify-between mb-2">
                <span class="text-xs font-bold text-[#79747E] uppercase tracking-widest">匹配进度</span>
                <span class="text-xs font-bold text-[#6750A4]">67%</span>
              </div>
              <div class="w-full bg-[#E7E0EC] h-2 rounded-full overflow-hidden">
                <div class="bg-gradient-to-r from-[#6750A4] to-[#7D5260] h-full rounded-full w-2/3
                            animate-pulse-slow"></div>
              </div>
            </div>
            <button @click="$router.push('/user/messages')"
                    class="px-6 py-3 bg-[#6750A4] text-white rounded-xl font-bold text-sm
                           hover:bg-[#4F378B] transition-all shadow-lg shadow-[#6750A4]/20 shrink-0">
              查看消息
            </button>
          </div>
        </div>
      </section>
    </main>

    <!-- ============ FOOTER ============ -->
    <footer class="bg-[#1D1B20] text-white">
      <div class="max-w-7xl mx-auto px-6 md:px-10">
        <!-- Main Footer -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-12 py-16">
          <!-- Brand -->
          <div class="lg:col-span-1">
            <div class="flex items-center gap-3 mb-5">
              <div class="w-10 h-10 rounded-2xl bg-[#6750A4] flex items-center justify-center">
                <span class="material-symbols-outlined text-white text-xl"
                      style="font-variation-settings:'FILL' 1">search_hands_free</span>
              </div>
              <span class="text-xl font-black text-[#D0BCFF] tracking-tight">识归</span>
            </div>
            <p class="text-white/40 text-sm leading-relaxed mb-6">
              校园 AI 失物招领智能服务平台，通过社区与科技的力量，让遗失的宝物重回主人怀抱。
            </p>
            <div class="flex gap-3">
              <a class="w-9 h-9 rounded-xl bg-white/10 flex items-center justify-center
                        hover:bg-[#6750A4] transition-all cursor-pointer">
                <span class="material-symbols-outlined text-base text-white/60 hover:text-white">language</span>
              </a>
              <a class="w-9 h-9 rounded-xl bg-white/10 flex items-center justify-center
                        hover:bg-[#6750A4] transition-all cursor-pointer">
                <span class="material-symbols-outlined text-base text-white/60 hover:text-white">share</span>
              </a>
              <a class="w-9 h-9 rounded-xl bg-white/10 flex items-center justify-center
                        hover:bg-[#6750A4] transition-all cursor-pointer">
                <span class="material-symbols-outlined text-base text-white/60 hover:text-white">mail</span>
              </a>
            </div>
          </div>

          <!-- Links -->
          <div>
            <h4 class="font-black text-white/80 mb-5 text-sm tracking-widest uppercase">快速链接</h4>
            <ul class="space-y-3">
              <li><router-link to="/lost" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">遗失物品</router-link></li>
              <li><router-link to="/found" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">拾获物品</router-link></li>
              <li><router-link to="/ai-search" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">AI 描述找物</router-link></li>
              <li><router-link to="/announcements" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">系统公告</router-link></li>
            </ul>
          </div>

          <div>
            <h4 class="font-black text-white/80 mb-5 text-sm tracking-widest uppercase">支持</h4>
            <ul class="space-y-3">
              <li><router-link to="/contact" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">联系我们</router-link></li>
              <li><router-link to="/privacy-policy" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">隐私政策</router-link></li>
              <li><router-link to="/terms-of-service" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">服务条款</router-link></li>
              <li><router-link to="/success-cases" class="text-white/40 hover:text-[#D0BCFF] transition-colors text-sm">成功案例</router-link></li>
            </ul>
          </div>

          <div>
            <h4 class="font-black text-white/80 mb-5 text-sm tracking-widest uppercase">校园合作</h4>
            <div class="flex flex-wrap gap-3 mb-5">
              <div class="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-white/50 text-xs font-bold">图书馆</div>
              <div class="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-white/50 text-xs font-bold">体育馆</div>
              <div class="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-white/50 text-xs font-bold">食堂</div>
              <div class="px-4 py-2 rounded-xl bg-white/5 border border-white/10 text-white/50 text-xs font-bold">教学楼</div>
            </div>
          </div>
        </div>

        <!-- Bottom Bar -->
        <div class="py-6 border-t border-white/10 flex flex-col md:flex-row justify-between items-center gap-4">
          <span class="text-white/30 text-xs">© 2026 识归 · 校园 AI 失物招领智能服务平台</span>
          <span class="text-white/20 text-xs">构建于信任之上 ❤️</span>
        </div>
      </div>
    </footer>

    <!-- ============ 回到顶部 ============ -->
    <Transition name="fade-up">
      <button v-if="showBackToTop" @click="scrollToTop"
              class="fixed bottom-8 right-8 w-12 h-12 rounded-2xl bg-[#6750A4] text-white
                     flex items-center justify-center shadow-xl shadow-[#6750A4]/30
                     hover:bg-[#4F378B] hover:-translate-y-1 transition-all z-50 active:scale-90">
        <span class="material-symbols-outlined">keyboard_arrow_up</span>
      </button>
    </Transition>
  </div>
</template>

<script setup>
import { getTopAnnouncements } from '@/api/announcement'
import { getHomeStats, getLatestFound, getLatestLost } from '@/api/home'
import { getMessageDetail, getMessages, getUnreadCount } from '@/api/message'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { computed, onBeforeUnmount, onMounted, ref, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// ========== 原有状态 ==========
const isLogin = computed(() => userStore.isLogin)
const searchKeyword = ref('')
const aiSearchDescription = ref('')
const stats = ref({})
const latestLost = ref([])
const latestFound = ref([])
const notificationOpen = ref(false)
const notificationsLoading = ref(false)
const announcementNotices = ref([])
const messageNotices = ref([])
const unreadMessageCount = ref(0)
const notificationRef = ref(null)
const loading = ref({ lost: false, found: false })

// ========== 新增状态 ==========
const mobileMenuOpen = ref(false)
const activeItemTab = ref('lost')
const animatedStats = ref({ lostCount: 0, foundCount: 0, successCount: 0, userCount: 0 })
const showBackToTop = ref(false)
let scrollObserver = null

// 导航链接
const navLinks = [
  { to: '/', label: '首页' },
  { to: '/lost', label: '遗失物品' },
  { to: '/found', label: '拾获物品' },
  { to: '/ai-search', label: 'AI找物' },
  { to: '/user/found/add', label: '发布招领' },
  { to: '/announcements', label: '公告' },
  { to: '/contact', label: '联系我们' },
]

// 统计卡片配置
const statCards = [
  { key: 'lostCount', label: '报失物品', icon: 'warning', bgClass: 'bg-[#B3261E]/20', iconClass: 'text-[#F2B8B5]', numClass: 'text-[#F2B8B5]' },
  { key: 'foundCount', label: '当前保管', icon: 'inventory_2', bgClass: 'bg-[#D0BCFF]/20', iconClass: 'text-[#D0BCFF]', numClass: 'text-[#D0BCFF]' },
  { key: 'successCount', label: '成功归还', icon: 'check_circle', bgClass: 'bg-green-500/20', iconClass: 'text-green-400', numClass: 'text-green-400' },
  { key: 'userCount', label: '社区成员', icon: 'group', bgClass: 'bg-white/10', iconClass: 'text-white/70', numClass: 'text-white' },
]

// 使用步骤
const howItWorksSteps = [
  { icon: 'edit_note', title: '描述物品', desc: '填写物品名称、特征、丢失/拾获地点等关键信息', bgClass: 'bg-[#E8DEF8]', iconClass: 'text-[#6750A4]' },
  { icon: 'add_a_photo', title: '上传图片', desc: 'AI自动识别物品类型、颜色、品牌等特征信息', bgClass: 'bg-[#FFD8E4]', iconClass: 'text-[#7D5260]' },
  { icon: 'auto_awesome', title: '智能匹配', desc: '系统自动与数据库中的信息进行智能比对匹配', bgClass: 'bg-[#D0BCFF]/30', iconClass: 'text-[#6750A4]' },
  { icon: 'handshake', title: '成功找回', desc: '联系物品持有人，验证身份后完成物品归还', bgClass: 'bg-green-100', iconClass: 'text-green-600' },
]

// 当前选项卡的物品列表
const currentTabItems = computed(() => {
  return activeItemTab.value === 'lost' ? latestLost.value : latestFound.value
})

// ========== 生命周期 ==========
onMounted(async () => {
  document.addEventListener('click', handleClickOutsideNotification)
  window.addEventListener('scroll', handleScroll)
  await Promise.all([loadHomeData(), loadNotifications()])
  await nextTick()
  setupScrollObserver()
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutsideNotification)
  window.removeEventListener('scroll', handleScroll)
  if (scrollObserver) scrollObserver.disconnect()
})

// ========== 滚动相关 ==========
const handleScroll = () => {
  showBackToTop.value = window.scrollY > 400
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// ========== 滚动动画观察器 ==========
const setupScrollObserver = () => {
  scrollObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('animate-in')
        }
      })
    },
    { threshold: 0.08, rootMargin: '0px 0px -40px 0px' }
  )
  document.querySelectorAll('.scroll-animate').forEach((el) => {
    scrollObserver.observe(el)
  })
}

// ========== 数字计数动画 ==========
const animateCounters = () => {
  const duration = 2000
  const keys = ['lostCount', 'foundCount', 'successCount', 'userCount']
  keys.forEach((key) => {
    const target = stats.value[key] || 0
    if (target === 0) return
    const startTime = performance.now()
    const update = (currentTime) => {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = 1 - Math.pow(1 - progress, 3)
      animatedStats.value[key] = Math.floor(eased * target)
      if (progress < 1) requestAnimationFrame(update)
    }
    requestAnimationFrame(update)
  })
}

// 监听 stats 变化，触发计数动画
watch(stats, () => {
  animateCounters()
}, { deep: true })

// ========== 通知相关（原有逻辑） ==========
const toggleNotifications = async () => {
  notificationOpen.value = !notificationOpen.value
  if (notificationOpen.value && !notificationsLoading.value) {
    await loadNotifications()
  }
}

const handleClickOutsideNotification = (event) => {
  if (!notificationRef.value) return
  if (!notificationRef.value.contains(event.target)) {
    notificationOpen.value = false
  }
}

const loadNotifications = async () => {
  notificationsLoading.value = true
  try {
    await Promise.all([loadAnnouncementNotices(), loadMessageNotices()])
  } finally {
    notificationsLoading.value = false
  }
}

const loadAnnouncementNotices = async () => {
  try {
    const response = await getTopAnnouncements()
    if (response?.code === 200) {
      announcementNotices.value = (response.data?.announcements || []).slice(0, 3)
      return
    }
    announcementNotices.value = []
  } catch (error) {
    console.error('加载公告通知失败:', error)
    announcementNotices.value = []
  }
}

const loadMessageNotices = async () => {
  if (!isLogin.value || !userStore.userInfo?.id) {
    messageNotices.value = []
    unreadMessageCount.value = 0
    return
  }
  try {
    const userId = userStore.userInfo.id
    const [countResponse, listResponse] = await Promise.all([
      getUnreadCount(userId),
      getMessages({ userId })
    ])
    unreadMessageCount.value = countResponse?.code === 200 ? (countResponse.data?.unreadCount || 0) : 0
    const rawMessages = listResponse?.code === 200 ? (listResponse.data?.messages || []) : []
    const prioritized = [...rawMessages]
      .sort((a, b) => {
        if (a.status === b.status) return 0
        return a.status === 'unread' ? -1 : 1
      })
      .slice(0, 3)
    const detailedMessages = await Promise.all(
      prioritized.map(async (msg) => {
        try {
          const detail = await getMessageDetail(msg.id)
          if (detail?.code === 200 && detail.data) return { ...msg, ...detail.data }
        } catch (error) {
          console.error('加载消息详情失败:', error)
        }
        return msg
      })
    )
    messageNotices.value = detailedMessages
  } catch (error) {
    console.error('加载消息通知失败:', error)
    messageNotices.value = []
    unreadMessageCount.value = 0
  }
}

const openAnnouncementPage = () => {
  notificationOpen.value = false
  router.push('/announcements')
}

const openMessageDetail = (id) => {
  notificationOpen.value = false
  router.push(`/user/messages/${id}`)
}

// ========== 数据加载（原有逻辑） ==========
const loadHomeData = async () => {
  try {
    await loadStats()
    loading.value.lost = true
    await loadLatestLost()
    loading.value.found = true
    await loadLatestFound()
  } catch (error) {
    console.error('Failed to load home data:', error)
    ElMessage.error('数据加载失败')
  } finally {
    loading.value.lost = false
    loading.value.found = false
  }
}

const loadStats = async () => {
  try {
    const response = await getHomeStats()
    let targetData = null
    if (response?.code === 200 && response?.data) {
      targetData = response.data
    } else if (response?.data?.code === 200 && response?.data?.data) {
      targetData = response.data.data
    }
    if (targetData) {
      stats.value = {
        lostCount: targetData.lostCount || 0,
        foundCount: targetData.foundCount || 0,
        successCount: targetData.successCount || 0,
        userCount: targetData.userCount || 0
      }
    }
  } catch (error) {
    console.error('获取统计数据报错:', error)
  }
}

const loadLatestLost = async () => {
  try {
    const result = await getLatestLost()
    latestLost.value = result?.code === 200 && result?.data?.items ? result.data.items : []
  } catch (error) {
    latestLost.value = []
  }
}

const loadLatestFound = async () => {
  try {
    const result = await getLatestFound()
    latestFound.value = result?.code === 200 && result?.data?.items ? result.data.items : []
  } catch (error) {
    latestFound.value = []
  }
}

// ========== 工具函数（原有逻辑） ==========
const getPlaceholderImage = (title, isFound = false) => {
  const color = isFound ? 'E8DEF8' : 'FFD8E4'
  const text = encodeURIComponent((title || '未知').substring(0, 2).toUpperCase())
  return `https://placehold.co/300x300/${color}/6750A4?text=${text}&font=roboto`
}

const getItemImage = (item, isFound = false) => {
  const imgStr = item.images || item.image || item.imageUrl || ''
  if (imgStr) return imgStr.split(',')[0]
  return getPlaceholderImage(item.title, isFound)
}

const formatDateRelative = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  if (diffHours < 1) return '刚刚'
  if (diffHours < 24) return `${diffHours}小时前`
  const diffDays = Math.floor(diffHours / 24)
  if (diffDays < 7) return `${diffDays}天前`
  return date.toLocaleDateString()
}

const handleSearch = () => {
  const kw = searchKeyword.value.trim()
  if (!kw) {
    router.push('/found')
    return
  }
  router.push({ path: '/search', query: { keyword: kw } })
}

const handleAiSearch = () => {
  if (!aiSearchDescription.value.trim()) {
    router.push('/ai-search')
    return
  }
  router.push({ path: '/ai-search', query: { q: aiSearchDescription.value.trim() } })
}

const handleLogout = async () => {
  await userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/')
}

const viewLostItem = (id) => router.push(`/lost/${id}`)
const viewFoundItem = (id) => router.push(`/found/${id}`)

const handleQuickAction = (action) => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  const routes = {
    reportLost: '/user/lost/UserLostAdd',
    reportFound: '/user/found/add',
    myItems: '/user/UserLostList',
    suggestion: '/user/suggestions/add'
  }
  if (routes[action]) router.push(routes[action])
}
</script>

<style scoped>
/* ============ HERO 渐变背景动画 ============ */
.hero-gradient {
  background: linear-gradient(-45deg, #6750A4, #4F378B, #7D5260, #6750A4, #625B71);
  background-size: 400% 400%;
  animation: gradientShift 12s ease infinite;
}

@keyframes gradientShift {
  0%   { background-position: 0% 50%; }
  50%  { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* ============ Hero 渐变文字 ============ */
.hero-gradient-text {
  background: linear-gradient(135deg, #E8DEF8 0%, #D0BCFF 40%, #FFD8E4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ============ 浮动装饰球 ============ */
.floating-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.15;
}

.orb-1 {
  width: 400px; height: 400px;
  background: #D0BCFF;
  top: -10%; left: -5%;
  animation: floatOrb 20s ease-in-out infinite;
}

.orb-2 {
  width: 300px; height: 300px;
  background: #FFD8E4;
  bottom: 10%; right: -5%;
  animation: floatOrb 25s ease-in-out infinite reverse;
}

.orb-3 {
  width: 200px; height: 200px;
  background: #E8DEF8;
  top: 40%; left: 50%;
  animation: floatOrb 18s ease-in-out infinite 5s;
}

.orb-4 {
  width: 150px; height: 150px;
  background: #CCC2DC;
  top: 20%; right: 20%;
  animation: floatOrb 22s ease-in-out infinite 3s;
}

@keyframes floatOrb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25%  { transform: translate(40px, -60px) scale(1.1); }
  50%  { transform: translate(-30px, 40px) scale(0.9); }
  75%  { transform: translate(50px, 20px) scale(1.05); }
}

/* ============ 公告滚动条 ============ */
.ticker-track {
  animation: ticker 40s linear infinite;
}

@keyframes ticker {
  0%   { transform: translateX(0); }
  100% { transform: translateX(-33.33%); }
}

/* ============ 滚动入场动画 ============ */
.scroll-animate {
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.9s cubic-bezier(0.16, 1, 0.3, 1);
}

.scroll-animate.animate-in {
  opacity: 1;
  transform: translateY(0);
}

/* ============ 通知下拉过渡 ============ */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.96);
}

/* ============ 移动菜单过渡 ============ */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* ============ 回到顶部过渡 ============ */
.fade-up-enter-active,
.fade-up-leave-active {
  transition: all 0.3s ease;
}
.fade-up-enter-from,
.fade-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* ============ 微动画 ============ */
.animate-bounce-subtle {
  animation: bounceSub 2s ease-in-out infinite;
}

@keyframes bounceSub {
  0%, 100% { transform: scale(1); }
  50%  { transform: scale(1.15); }
}

.animate-pulse-slow {
  animation: pulseSlow 3s ease-in-out infinite;
}

@keyframes pulseSlow {
  0%, 100% { opacity: 1; }
  50%  { opacity: 0.7; }
}

/* ============ Hero 卡片悬浮光效 ============ */
.hero-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, rgba(103,80,164,0.3), transparent 60%);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.5s;
  pointer-events: none;
}

.hero-card:hover::before {
  opacity: 1;
}

/* ============ 统计卡片悬浮 ============ */
.stat-card {
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

.stat-card:hover {
  transform: translateY(-8px);
}

/* ============ 步骤卡片连接线（大屏） ============ */
@media (min-width: 1024px) {
  .step-card:not(:last-child)::after {
    content: '';
    position: absolute;
    top: 50%;
    right: -1rem;
    width: 2rem;
    height: 2px;
    background: linear-gradient(to right, #E7E0EC, transparent);
    pointer-events: none;
  }
}

/* ============ 物品卡片 ============ */
.item-card {
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

/* ============ 导航链接下划线 ============ */
.nav-link.router-link-exact-active {
  color: #6750A4;
  background-color: rgba(103, 80, 164, 0.08);
}
</style>