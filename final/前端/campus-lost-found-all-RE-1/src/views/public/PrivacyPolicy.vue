<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">
    <!-- Hero -->
    <header class="relative overflow-hidden bg-surface-container-lowest border-b border-surface-container-high">
      <div class="absolute -top-10 -right-10 w-72 h-72 bg-primary-container/40 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute -bottom-10 -left-10 w-72 h-72 bg-secondary-container/40 rounded-full blur-3xl pointer-events-none"></div>

      <div class="relative max-w-6xl mx-auto px-6 py-10 md:py-14">
        <button
          type="button"
          @click="$router.push('/')"
          class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-surface-container-high text-on-surface font-bold hover:bg-surface-container-highest transition-colors"
        >
          <span class="material-symbols-outlined text-base">arrow_back</span>
          返回首页
        </button>

        <div class="mt-8 grid grid-cols-1 lg:grid-cols-[1.2fr_0.8fr] gap-8 items-center">
          <div>
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-primary-container text-primary border border-primary/10">
              <span class="material-symbols-outlined text-base" style="font-variation-settings: 'FILL' 1;">shield</span>
              <span class="text-xs font-black tracking-wide">平台安全与隐私</span>
            </div>

            <h1 class="mt-4 text-4xl md:text-5xl font-black tracking-tight font-headline">
              隐私政策
            </h1>

            <p class="mt-4 text-on-surface-variant text-lg font-medium max-w-2xl leading-relaxed">
              我们重视每一位用户的个人信息安全。平台仅在实现校园失物招领服务所必需的范围内收集、使用和保护信息。
            </p>

            <div class="mt-6 flex flex-wrap gap-3">
              <span class="px-4 py-2 rounded-full bg-primary/10 text-primary text-sm font-bold border border-primary/15">
                最小化收集
              </span>
              <span class="px-4 py-2 rounded-full bg-secondary/10 text-secondary text-sm font-bold border border-secondary/15">
                用途透明
              </span>
              <span class="px-4 py-2 rounded-full bg-success/10 text-success text-sm font-bold border border-success/15">
                安全保护
              </span>
            </div>
          </div>

          <!-- Visual Card -->
          <div class="bg-surface-container-low rounded-3xl p-6 border border-surface-container-high shadow-sm">
            <div class="grid grid-cols-2 gap-4">
              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="w-10 h-10 rounded-2xl bg-primary/10 text-primary flex items-center justify-center">
                  <span class="material-symbols-outlined">lock</span>
                </div>
                <div class="mt-3 text-lg font-black">安全存储</div>
                <div class="mt-1 text-sm text-on-surface-variant">仅保留服务必要信息</div>
              </div>

              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="w-10 h-10 rounded-2xl bg-secondary/10 text-secondary flex items-center justify-center">
                  <span class="material-symbols-outlined">visibility_off</span>
                </div>
                <div class="mt-3 text-lg font-black">隐私最小暴露</div>
                <div class="mt-1 text-sm text-on-surface-variant">减少敏感信息公开</div>
              </div>

              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="w-10 h-10 rounded-2xl bg-success/10 text-success flex items-center justify-center">
                  <span class="material-symbols-outlined">verified_user</span>
                </div>
                <div class="mt-3 text-lg font-black">身份核验</div>
                <div class="mt-1 text-sm text-on-surface-variant">认领前进行必要核对</div>
              </div>

              <div class="rounded-2xl bg-surface p-4 border border-surface-container-high">
                <div class="w-10 h-10 rounded-2xl bg-tertiary/10 text-tertiary flex items-center justify-center">
                  <span class="material-symbols-outlined">policy</span>
                </div>
                <div class="mt-3 text-lg font-black">规则透明</div>
                <div class="mt-1 text-sm text-on-surface-variant">明确说明数据用途</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-6xl mx-auto px-6 py-10">
      <div class="grid grid-cols-1 lg:grid-cols-[260px_1fr] gap-8">
        <!-- Side Nav -->
        <aside class="lg:sticky lg:top-24 h-fit">
          <div class="bg-surface-container-low rounded-3xl p-4 border border-surface-container-high shadow-sm">
            <div class="text-sm font-black text-on-surface-variant uppercase tracking-wide px-3 py-2">
              政策目录
            </div>
            <div class="mt-2 space-y-1">
              <button
                v-for="item in sections"
                :key="item.id"
                @click="scrollToSection(item.id)"
                class="w-full text-left px-4 py-3 rounded-2xl text-sm font-bold transition-colors"
                :class="activeSection === item.id
                  ? 'bg-primary/10 text-primary'
                  : 'text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface'"
              >
                {{ item.title }}
              </button>
            </div>
          </div>
        </aside>

        <!-- Content -->
        <section class="space-y-6">
          <div
            v-for="item in sections"
            :key="item.id"
            :ref="el => setSectionRef(item.id, el)"
            class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm scroll-mt-28"
          >
            <div class="flex items-start gap-4">
              <div class="w-12 h-12 rounded-2xl bg-primary/10 text-primary flex items-center justify-center shrink-0">
                <span class="material-symbols-outlined">{{ item.icon }}</span>
              </div>
              <div class="flex-1">
                <h2 class="text-2xl font-black font-headline text-on-surface">
                  {{ item.title }}
                </h2>
                <p class="mt-3 text-on-surface-variant leading-8 text-[15px]">
                  {{ item.content }}
                </p>

                <div
                  v-if="item.points?.length"
                  class="mt-5 grid grid-cols-1 md:grid-cols-2 gap-3"
                >
                  <div
                    v-for="point in item.points"
                    :key="point"
                    class="rounded-2xl bg-surface p-4 border border-surface-container-high text-sm text-on-surface-variant leading-7"
                  >
                    <span class="inline-flex items-center gap-2 font-bold text-on-surface">
                      <span class="material-symbols-outlined text-primary text-base">check_circle</span>
                      {{ point }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Visual Flow -->
          <div class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
            <h2 class="text-2xl font-black font-headline text-on-surface">信息保护流程</h2>
            <p class="mt-2 text-on-surface-variant">平台在处理用户信息时遵循以下原则：</p>

            <div class="mt-6 grid grid-cols-1 md:grid-cols-4 gap-4">
              <div class="rounded-2xl bg-surface p-5 border border-surface-container-high text-center">
                <div class="w-12 h-12 mx-auto rounded-full bg-primary text-white flex items-center justify-center font-black">1</div>
                <div class="mt-4 font-black">提交信息</div>
                <div class="mt-2 text-sm text-on-surface-variant">用户主动填写失物或招领内容</div>
              </div>

              <div class="rounded-2xl bg-surface p-5 border border-surface-container-high text-center">
                <div class="w-12 h-12 mx-auto rounded-full bg-primary text-white flex items-center justify-center font-black">2</div>
                <div class="mt-4 font-black">最小化处理</div>
                <div class="mt-2 text-sm text-on-surface-variant">仅用于平台服务相关功能</div>
              </div>

              <div class="rounded-2xl bg-surface p-5 border border-surface-container-high text-center">
                <div class="w-12 h-12 mx-auto rounded-full bg-primary text-white flex items-center justify-center font-black">3</div>
                <div class="mt-4 font-black">必要展示</div>
                <div class="mt-2 text-sm text-on-surface-variant">控制公开信息范围，保护隐私</div>
              </div>

              <div class="rounded-2xl bg-surface p-5 border border-surface-container-high text-center">
                <div class="w-12 h-12 mx-auto rounded-full bg-success text-white flex items-center justify-center font-black">4</div>
                <div class="mt-4 font-black">安全归档</div>
                <div class="mt-2 text-sm text-on-surface-variant">按规则留存或删除相关数据</div>
              </div>
            </div>
          </div>

          <!-- Bottom Notice -->
          <div class="rounded-3xl p-6 md:p-8 bg-primary/6 border border-primary/10">
            <div class="flex items-start gap-4">
              <span class="material-symbols-outlined text-primary text-3xl shrink-0">info</span>
              <div>
                <h3 class="text-xl font-black font-headline text-on-surface">温馨提示</h3>
                <p class="mt-3 text-on-surface-variant leading-8">
                  如果你对个人信息使用方式、展示范围或平台数据处理机制有任何疑问，可通过“联系我们”页面反馈，我们会持续优化隐私保护设计。
                </p>
                <div class="mt-5 flex flex-wrap gap-3">
                  <button
                    type="button"
                    @click="$router.push('/contact')"
                    class="px-5 py-3 rounded-2xl bg-primary text-on-primary font-bold hover:opacity-95 transition-opacity"
                  >
                    联系我们
                  </button>
                  <button
                    type="button"
                    @click="$router.push('/')"
                    class="px-5 py-3 rounded-2xl bg-surface text-on-surface font-bold border border-surface-container-high hover:bg-surface-container-high transition-colors"
                  >
                    返回首页
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { nextTick, onMounted, onUnmounted, ref } from 'vue'

const sections = [
  {
    id: 'collect',
    title: '1. 信息收集范围',
    icon: 'database',
    content: '为了实现校园失物招领服务，我们可能收集用户主动提交的必要信息，包括用户名、联系方式、失物或招领物品描述、时间、地点以及与认领核验相关的必要资料。',
    points: ['仅收集服务所必需的信息', '不鼓励填写无关敏感内容']
  },
  {
    id: 'usage',
    title: '2. 信息使用方式',
    icon: 'task_alt',
    content: '收集到的信息仅用于平台核心业务，包括失物发布、招领展示、消息通知、信息匹配、身份核验、申诉处理等，不用于与平台功能无关的商业用途。',
    points: ['用于失物招领流程处理', '用于消息提醒和认领核验']
  },
  {
    id: 'display',
    title: '3. 信息展示与保护',
    icon: 'visibility_lock',
    content: '平台在展示失物或招领信息时，会尽量避免过度暴露个人隐私。联系方式、身份特征等敏感内容通常仅在必要场景下显示或进行限制处理。',
    points: ['减少敏感信息外露', '强化信息展示边界']
  },
  {
    id: 'ai',
    title: '4. AI辅助说明',
    icon: 'smart_toy',
    content: '平台可能使用 AI 对文本与图片内容进行识别、分类、相似匹配与辅助推荐。AI 输出结果仅供参考，最终是否认领、是否匹配成功仍需人工核验确认。',
    points: ['AI结果仅作辅助建议', '最终结果以人工核验为准']
  },
  {
    id: 'responsibility',
    title: '5. 用户责任',
    icon: 'gavel',
    content: '用户应确保所提交信息真实、合法、必要，不得发布虚假内容、冒用他人身份、恶意获取他人隐私或滥用平台功能。对违规行为，平台有权采取限制措施。',
    points: ['不得发布虚假信息', '不得恶意获取他人资料']
  },
  {
    id: 'update',
    title: '6. 政策更新',
    icon: 'update',
    content: '平台可能根据校园服务需求、功能变化与管理要求对本政策进行更新。更新后的内容将在页面中公示，用户可随时查阅最新版本。',
    points: ['政策变动将及时展示', '建议定期查看最新内容']
  }
]

const activeSection = ref(sections[0].id)
const sectionRefs = ref({})

const setSectionRef = (id, el) => {
  if (el) sectionRefs.value[id] = el
}

const scrollToSection = async (id) => {
  await nextTick()
  sectionRefs.value[id]?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const updateActiveSection = () => {
  const entries = Object.entries(sectionRefs.value)
  if (!entries.length) return

  let current = sections[0].id
  for (const [id, el] of entries) {
    const rect = el.getBoundingClientRect()
    if (rect.top <= 140) {
      current = id
    }
  }
  activeSection.value = current
}

onMounted(() => {
  window.addEventListener('scroll', updateActiveSection, { passive: true })
  updateActiveSection()
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateActiveSection)
})
</script>

<style scoped>
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-lowest { background-color: #FFFBFE; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }

.bg-primary\/6 { background-color: rgba(103, 80, 164, 0.06); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.10); }
.bg-primary-container\/40 { background-color: rgba(234, 221, 255, 0.40); }
.bg-secondary-container\/40 { background-color: rgba(232, 222, 248, 0.40); }
.bg-secondary\/10 { background-color: rgba(98, 91, 113, 0.10); }
.bg-success\/10 { background-color: rgba(0, 110, 28, 0.10); }
.bg-tertiary\/10 { background-color: rgba(125, 82, 96, 0.10); }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-success { color: #006E1C; }
.text-tertiary { color: #7D5260; }
.text-on-primary { color: #FFFFFF; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-outline-variant { border-color: #CAC4D0; }
.border-primary\/10 { border-color: rgba(103, 80, 164, 0.10); }
.border-primary\/15 { border-color: rgba(103, 80, 164, 0.15); }
.border-secondary\/15 { border-color: rgba(98, 91, 113, 0.15); }
.border-success\/15 { border-color: rgba(0, 110, 28, 0.15); }
</style>