<template>
  <div class="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container">
    <!-- Hero Banner -->
    <header class="relative overflow-hidden bg-gradient-to-br from-primary to-primary-dim text-on-primary">
      <div class="absolute inset-0 opacity-10 pointer-events-none">
        <div class="absolute top-10 left-10 w-40 h-40 border border-white/20 rounded-full"></div>
        <div class="absolute top-24 right-20 w-56 h-56 border border-white/10 rounded-full"></div>
        <div class="absolute bottom-[-40px] left-1/3 w-72 h-72 border border-white/10 rounded-full"></div>
      </div>

      <div class="relative max-w-7xl mx-auto px-6 py-10 md:py-16">
        <button
          type="button"
          @click="$router.push('/')"
          class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 text-white font-bold hover:bg-white/15 transition-colors backdrop-blur"
        >
          <span class="material-symbols-outlined text-base">arrow_back</span>
          返回首页
        </button>

        <div class="mt-8 grid grid-cols-1 lg:grid-cols-[1.2fr_0.8fr] gap-10 items-center">
          <div>
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 border border-white/15">
              <span class="material-symbols-outlined text-base" style="font-variation-settings: 'FILL' 1;">gavel</span>
              <span class="text-xs font-black tracking-wide">平台使用规则</span>
            </div>

            <h1 class="mt-5 text-4xl md:text-5xl font-black tracking-tight font-headline leading-tight">
              服务条款
            </h1>

            <p class="mt-4 text-white/85 text-lg font-medium max-w-2xl leading-relaxed">
              为了保障校园失物招领平台的正常运行、用户体验与信息可信度，请在使用平台前了解并遵守以下服务规则。
            </p>

            <div class="mt-6 flex flex-wrap gap-3">
              <span class="px-4 py-2 rounded-full bg-white/10 text-white text-sm font-bold border border-white/15">
                真实发布
              </span>
              <span class="px-4 py-2 rounded-full bg-white/10 text-white text-sm font-bold border border-white/15">
                文明沟通
              </span>
              <span class="px-4 py-2 rounded-full bg-white/10 text-white text-sm font-bold border border-white/15">
                规范认领
              </span>
            </div>
          </div>

          <!-- Highlight Panel -->
          <div class="bg-white/10 backdrop-blur-xl rounded-3xl p-6 border border-white/15 shadow-2xl">
            <div class="grid grid-cols-2 gap-4">
              <div class="rounded-2xl bg-white/10 p-4 border border-white/10">
                <div class="text-xs uppercase tracking-wide font-black text-white/70">使用原则</div>
                <div class="mt-2 text-2xl font-black">真实</div>
              </div>
              <div class="rounded-2xl bg-white/10 p-4 border border-white/10">
                <div class="text-xs uppercase tracking-wide font-black text-white/70">沟通要求</div>
                <div class="mt-2 text-2xl font-black">礼貌</div>
              </div>
              <div class="rounded-2xl bg-white/10 p-4 border border-white/10">
                <div class="text-xs uppercase tracking-wide font-black text-white/70">认领方式</div>
                <div class="mt-2 text-2xl font-black">核验</div>
              </div>
              <div class="rounded-2xl bg-white/10 p-4 border border-white/10">
                <div class="text-xs uppercase tracking-wide font-black text-white/70">平台态度</div>
                <div class="mt-2 text-2xl font-black">规范</div>
              </div>
            </div>

            <div class="mt-5 p-4 rounded-2xl bg-white/10 border border-white/10">
              <div class="text-sm font-black">一句话说明</div>
              <p class="mt-2 text-sm text-white/80 leading-7">
                平台鼓励校园互助，但所有找回与认领流程都应建立在真实信息、合理核验和文明沟通的基础上。
              </p>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-6 py-10 space-y-10">
      <!-- Rule Cards -->
      <section>
        <div class="flex items-center justify-between gap-4 flex-wrap">
          <div>
            <h2 class="text-2xl md:text-3xl font-black font-headline">核心规则</h2>
            <p class="mt-2 text-on-surface-variant">以下规则适用于平台主要使用场景</p>
          </div>
        </div>

        <div class="mt-6 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          <article
            v-for="rule in ruleCards"
            :key="rule.title"
            class="group bg-surface-container-low rounded-3xl p-6 border border-surface-container-high hover:border-primary/20 hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
          >
            <div class="w-12 h-12 rounded-2xl flex items-center justify-center" :class="rule.iconBg">
              <span class="material-symbols-outlined" :class="rule.iconColor">{{ rule.icon }}</span>
            </div>

            <h3 class="mt-5 text-xl font-black font-headline">{{ rule.title }}</h3>
            <p class="mt-3 text-on-surface-variant text-sm leading-7">
              {{ rule.desc }}
            </p>

            <div class="mt-5 flex flex-wrap gap-2">
              <span
                v-for="tag in rule.tags"
                :key="tag"
                class="px-3 py-1 rounded-full text-[11px] font-black tracking-wide bg-surface text-on-surface-variant border border-surface-container-high"
              >
                {{ tag }}
              </span>
            </div>
          </article>
        </div>
      </section>

      <!-- Timeline -->
      <section class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
        <div class="flex items-center justify-between gap-4 flex-wrap">
          <div>
            <h2 class="text-2xl font-black font-headline">推荐使用流程</h2>
            <p class="mt-2 text-on-surface-variant">建议按规范步骤完成失物或招领流程</p>
          </div>
          <span class="px-4 py-2 rounded-full bg-primary/10 text-primary text-sm font-bold border border-primary/15">
            流程可视化
          </span>
        </div>

        <div class="mt-8 grid grid-cols-1 md:grid-cols-4 gap-5">
          <div
            v-for="(step, index) in flowSteps"
            :key="step.title"
            class="relative rounded-2xl bg-surface p-5 border border-surface-container-high"
          >
            <div class="w-12 h-12 rounded-full bg-primary text-white flex items-center justify-center text-lg font-black">
              {{ index + 1 }}
            </div>
            <h3 class="mt-4 text-lg font-black">{{ step.title }}</h3>
            <p class="mt-2 text-sm text-on-surface-variant leading-7">
              {{ step.desc }}
            </p>
          </div>
        </div>
      </section>

      <!-- Risk + Rights -->
      <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-2xl bg-warning/15 text-warning flex items-center justify-center">
              <span class="material-symbols-outlined">warning</span>
            </div>
            <div>
              <h2 class="text-2xl font-black font-headline">违规风险提示</h2>
              <p class="text-on-surface-variant text-sm mt-1">以下行为可能被限制处理</p>
            </div>
          </div>

          <div class="mt-6 space-y-4">
            <div
              v-for="risk in risks"
              :key="risk.title"
              class="rounded-2xl bg-surface p-4 border border-surface-container-high"
            >
              <div class="font-black text-on-surface">{{ risk.title }}</div>
              <div class="mt-2 text-sm text-on-surface-variant leading-7">{{ risk.desc }}</div>
            </div>
          </div>
        </div>

        <div class="bg-surface-container-low rounded-3xl p-6 md:p-8 border border-surface-container-high shadow-sm">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-2xl bg-success/10 text-success flex items-center justify-center">
              <span class="material-symbols-outlined">verified</span>
            </div>
            <div>
              <h2 class="text-2xl font-black font-headline">用户权益说明</h2>
              <p class="text-on-surface-variant text-sm mt-1">平台也会保障正常用户的使用体验</p>
            </div>
          </div>

          <div class="mt-6 grid grid-cols-1 gap-4">
            <div
              v-for="right in rights"
              :key="right"
              class="rounded-2xl bg-surface p-4 border border-surface-container-high text-sm leading-7 text-on-surface-variant"
            >
              <span class="inline-flex items-center gap-2 font-bold text-on-surface">
                <span class="material-symbols-outlined text-success text-base">check_circle</span>
                {{ right }}
              </span>
            </div>
          </div>
        </div>
      </section>

      <!-- Bottom CTA -->
      <section class="rounded-3xl p-6 md:p-8 bg-primary/6 border border-primary/10">
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <h3 class="text-2xl font-black font-headline">继续使用即视为同意服务规则</h3>
            <p class="mt-3 text-on-surface-variant leading-8 max-w-2xl">
              如果你在使用平台过程中遇到争议、问题或对规则有疑问，欢迎通过“联系我们”页面反馈，我们会持续优化平台体验。
            </p>
          </div>

          <div class="flex flex-wrap gap-3">
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
      </section>
    </main>

    <!-- 悬浮AI讲解助手 -->
    <div class="fixed right-5 bottom-5 z-[90] flex items-end gap-3">
      <!-- 讲解面板 -->
      <transition name="fade-slide">
        <div
          v-if="assistantOpen"
          class="w-[320px] max-w-[80vw] rounded-3xl bg-surface-container-low border border-surface-container-high shadow-2xl p-5"
        >
          <div class="flex items-start justify-between gap-3">
            <div>
              <div class="text-sm font-black text-primary">AI 条款导读助手</div>
              <p class="mt-1 text-xs text-on-surface-variant leading-6">
                我可以为你朗读服务条款摘要，帮助快速理解平台规则。
              </p>
            </div>
            <button
              type="button"
              @click="assistantOpen = false"
              class="text-on-surface-variant hover:text-primary"
            >
              <span class="material-symbols-outlined text-base">close</span>
            </button>
          </div>

          <div class="mt-4 p-4 rounded-2xl bg-surface border border-surface-container-high">
            <div class="text-xs font-black text-on-surface-variant uppercase tracking-wide">语音摘要</div>
            <p class="mt-2 text-sm text-on-surface leading-7">
              {{ speechSummary }}
            </p>
          </div>

          <div class="mt-4 grid grid-cols-2 gap-3">
            <button
              type="button"
              @click="startSpeech"
              class="px-4 py-3 rounded-2xl bg-primary text-on-primary font-bold hover:opacity-95 transition-opacity flex items-center justify-center gap-2"
            >
              <span class="material-symbols-outlined text-base">play_arrow</span>
              播放讲解
            </button>
            <button
              type="button"
              @click="stopSpeech"
              class="px-4 py-3 rounded-2xl bg-surface text-on-surface font-bold border border-surface-container-high hover:bg-surface-container-high transition-colors flex items-center justify-center gap-2"
            >
              <span class="material-symbols-outlined text-base">stop</span>
              停止播放
            </button>
          </div>

          <div class="mt-3 text-xs text-on-surface-variant">
            状态：
            <span class="font-bold" :class="isSpeaking ? 'text-success' : 'text-on-surface-variant'">
              {{ isSpeaking ? '正在播报中' : '未播放' }}
            </span>
          </div>
        </div>
      </transition>

      <!-- 悬浮小人 -->
      <div class="relative">
        <!-- 小气泡 -->
        <transition name="fade-slide">
          <div
            v-if="!assistantOpen"
            class="absolute bottom-full right-2 mb-3 px-4 py-2 rounded-2xl bg-surface text-on-surface text-sm font-bold border border-surface-container-high shadow-lg whitespace-nowrap"
          >
            需要我讲解条款吗？
          </div>
        </transition>

        <button
          type="button"
          @click="assistantOpen = !assistantOpen"
          class="assistant-float w-20 h-20 rounded-full bg-gradient-to-br from-primary to-primary-dim shadow-2xl border-4 border-white flex items-center justify-center overflow-hidden"
        >
          <div class="relative w-14 h-14">
            <div class="absolute inset-0 rounded-full bg-white/20"></div>
            <svg viewBox="0 0 120 120" class="w-full h-full">
              <rect x="28" y="34" width="64" height="52" rx="16" fill="#ffffff" opacity="0.95" />
              <circle cx="48" cy="58" r="6" fill="#6750A4" />
              <circle cx="72" cy="58" r="6" fill="#6750A4" />
              <rect x="48" y="72" width="24" height="6" rx="3" fill="#6750A4" opacity="0.8" />
              <rect x="56" y="18" width="8" height="14" rx="4" fill="#ffffff" opacity="0.95" />
              <circle cx="60" cy="16" r="7" fill="#EADDFF" />
            </svg>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, ref } from 'vue'

const ruleCards = [
  {
    icon: 'fact_check',
    iconBg: 'bg-primary/10',
    iconColor: 'text-primary',
    title: '发布信息应真实',
    desc: '无论是失物还是招领信息，都应尽量基于真实时间、地点、特征进行发布，避免误导他人或造成无效匹配。',
    tags: ['真实发布', '信息准确']
  },
  {
    icon: 'forum',
    iconBg: 'bg-secondary/10',
    iconColor: 'text-secondary',
    title: '沟通应文明理性',
    desc: '消息交流应围绕失物招领本身进行，不得进行骚扰、侮辱、恶意索要信息或与平台无关的营销行为。',
    tags: ['文明沟通', '禁止骚扰']
  },
  {
    icon: 'badge',
    iconBg: 'bg-warning/15',
    iconColor: 'text-warning',
    title: '认领前需核验',
    desc: '失主认领物品前，应提供必要特征进行确认；拾到者也应保留必要细节用于核验，避免误领和冒领。',
    tags: ['核验优先', '防止冒领']
  },
  {
    icon: 'smart_toy',
    iconBg: 'bg-tertiary/10',
    iconColor: 'text-tertiary',
    title: 'AI结果仅供参考',
    desc: '平台的图片识别、分类和匹配推荐仅是辅助功能，不替代最终人工判断与双方确认。',
    tags: ['AI辅助', '人工确认']
  },
  {
    icon: 'shield',
    iconBg: 'bg-success/10',
    iconColor: 'text-success',
    title: '平台维护正常秩序',
    desc: '对于明显违规、虚假、恶意或扰乱平台秩序的内容，平台有权进行限制、删除或封禁处理。',
    tags: ['秩序维护', '违规处理']
  },
  {
    icon: 'balance',
    iconBg: 'bg-primary/10',
    iconColor: 'text-primary',
    title: '责任边界清晰',
    desc: '平台会尽力提供稳定服务，但不对用户线下私下交易、虚假认领或不可抗因素造成的风险承担无限责任。',
    tags: ['责任说明', '风险边界']
  }
]

const flowSteps = [
  {
    title: '发布信息',
    desc: '根据实际情况填写物品名称、地点、时间与明显特征。'
  },
  {
    title: '获取线索',
    desc: '通过浏览、搜索或AI辅助匹配，查找可能相关的信息。'
  },
  {
    title: '联系核验',
    desc: '通过平台消息联系对方，并根据特征进行必要核对。'
  },
  {
    title: '完成处理',
    desc: '确认认领或找回后，及时更新状态，形成闭环。'
  }
]

const risks = [
  {
    title: '虚假发布',
    desc: '恶意编造失物或招领信息，会影响他人判断与平台秩序。'
  },
  {
    title: '恶意认领',
    desc: '故意冒充失主或恶意套取物品特征，可能被限制账号使用。'
  },
  {
    title: '骚扰信息',
    desc: '利用平台消息功能进行骚扰、辱骂、广告营销等行为将被处理。'
  },
  {
    title: '违规用途',
    desc: '不得将平台用于与校园失物招领无关的违法或不当活动。'
  }
]

const rights = [
  '正常用户有权在规则范围内发布失物与招领信息。',
  '用户有权了解平台如何使用与展示其提交的信息。',
  '用户有权通过消息功能与对方进行合理沟通与核验。',
  '如发现异常信息、违规用户或错误数据，可向平台反馈处理。'
]

const assistantOpen = ref(false)
const isSpeaking = ref(false)
const speechSummary =
  '欢迎使用服务条款导读。使用本平台时，请保证发布信息真实、沟通方式文明、认领前充分核验。AI功能仅作辅助参考，最终认领与找回应以人工确认结果为准。对于虚假发布、恶意认领、骚扰消息或其他违规行为，平台有权限制处理。'

let currentUtterance = null

const startSpeech = () => {
  if (!('speechSynthesis' in window)) return
  stopSpeech()

  currentUtterance = new SpeechSynthesisUtterance(speechSummary)
  currentUtterance.lang = 'zh-CN'
  currentUtterance.rate = 1
  currentUtterance.pitch = 1
  currentUtterance.volume = 1

  currentUtterance.onstart = () => {
    isSpeaking.value = true
  }

  currentUtterance.onend = () => {
    isSpeaking.value = false
  }

  currentUtterance.onerror = () => {
    isSpeaking.value = false
  }

  window.speechSynthesis.speak(currentUtterance)
}

const stopSpeech = () => {
  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel()
  }
  isSpeaking.value = false
}

onBeforeUnmount(() => {
  stopSpeech()
})
</script>

<style scoped>
.bg-surface { background-color: #FEF7FF; }
.bg-surface-container-low { background-color: #F7F2FA; }
.bg-surface-container-lowest { background-color: #FFFBFE; }
.bg-surface-container-high { background-color: #ECE6F0; }
.bg-surface-container-highest { background-color: #E6E0E9; }

.bg-primary { background-color: #6750A4; }
.bg-primary-dim { background-color: #5B4696; }
.bg-primary\/6 { background-color: rgba(103, 80, 164, 0.06); }
.bg-primary\/10 { background-color: rgba(103, 80, 164, 0.10); }
.bg-primary-container\/40 { background-color: rgba(234, 221, 255, 0.40); }

.bg-secondary\/10 { background-color: rgba(98, 91, 113, 0.10); }
.bg-success\/10 { background-color: rgba(0, 110, 28, 0.10); }
.bg-warning\/15 { background-color: rgba(220, 188, 67, 0.15); }
.bg-tertiary\/10 { background-color: rgba(125, 82, 96, 0.10); }

.text-on-surface { color: #1D1B20; }
.text-on-surface-variant { color: #49454F; }
.text-primary { color: #6750A4; }
.text-secondary { color: #625B71; }
.text-success { color: #006E1C; }
.text-warning { color: #8A6B00; }
.text-tertiary { color: #7D5260; }
.text-on-primary { color: #FFFFFF; }

.border-surface-container-high { border-color: #ECE6F0; }
.border-primary\/10 { border-color: rgba(103, 80, 164, 0.10); }
.border-primary\/15 { border-color: rgba(103, 80, 164, 0.15); }
.border-warning\/20 { border-color: rgba(220, 188, 67, 0.20); }
.border-success\/15 { border-color: rgba(0, 110, 28, 0.15); }

.assistant-float {
  animation: floaty 2.8s ease-in-out infinite;
}

@keyframes floaty {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-8px); }
  100% { transform: translateY(0px); }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>