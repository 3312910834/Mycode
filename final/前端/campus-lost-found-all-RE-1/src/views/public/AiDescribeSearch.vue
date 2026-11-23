<template>
  <div class="min-h-screen bg-[#f8f7ff] text-slate-900 selection:bg-indigo-200/60">
    <!-- Background -->
    <div class="fixed inset-0 pointer-events-none overflow-hidden">
      <div class="absolute -top-20 -left-20 w-80 h-80 bg-indigo-200/35 rounded-full blur-3xl"></div>
      <div class="absolute top-1/3 -right-20 w-96 h-96 bg-cyan-200/25 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-20 left-1/3 w-96 h-96 bg-fuchsia-200/20 rounded-full blur-3xl"></div>
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_top,rgba(255,255,255,0.8),transparent_45%)]"></div>
    </div>

    <!-- Header -->
    <header class="relative z-10 border-b border-white/40 backdrop-blur-xl bg-white/55">
      <div class="max-w-7xl mx-auto px-6 py-5 flex flex-col lg:flex-row lg:items-center justify-between gap-5">
        <div class="flex items-start gap-4">
          <div class="w-14 h-14 rounded-2xl bg-gradient-to-br from-indigo-600 to-cyan-500 text-white flex items-center justify-center shadow-xl shadow-indigo-500/20">
            <span class="material-symbols-outlined text-3xl" style="font-variation-settings: 'FILL' 1, 'wght' 700;">smart_toy</span>
          </div>
          <div>
            <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-50 text-indigo-700 text-xs font-black tracking-wider border border-indigo-100">
              <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              AI 智能体工作台
            </div>
            <h1 class="mt-3 text-3xl md:text-4xl font-black tracking-tight">
              校园 AI 聊天助手
            </h1>
            <p class="mt-2 text-slate-600 text-sm md:text-base font-medium max-w-2xl">
              这是一个更像“智能体控制台”的对话界面。你可以问平台功能、操作方法，也可以像聊天一样提出问题。
            </p>
          </div>
        </div>

        <div class="flex items-center gap-3">
          <button
            type="button"
            @click="$router.push('/')"
            class="px-5 py-3 rounded-2xl bg-white text-slate-700 font-bold border border-slate-200 hover:bg-slate-50 transition-colors shadow-sm"
          >
            返回首页
          </button>
          <button
            type="button"
            @click="resetChat"
            class="px-5 py-3 rounded-2xl bg-slate-900 text-white font-bold hover:bg-slate-800 transition-colors shadow-lg shadow-slate-900/10"
          >
            新建会话
          </button>
        </div>
      </div>
    </header>

    <main class="relative z-10 max-w-7xl mx-auto px-6 py-8 grid grid-cols-1 xl:grid-cols-[320px_1fr_300px] gap-6">
      <!-- Left Panel -->
      <aside class="space-y-6">
        <!-- Agent Card -->
        <section class="rounded-[28px] border border-white/70 bg-white/80 backdrop-blur-xl shadow-xl shadow-slate-200/60 overflow-hidden">
          <div class="p-6 bg-gradient-to-br from-indigo-600 via-indigo-500 to-cyan-500 text-white relative">
            <div class="absolute top-3 right-3 px-2.5 py-1 rounded-full bg-white/15 text-[11px] font-black tracking-wider border border-white/15">
              ONLINE
            </div>
            <div class="w-16 h-16 rounded-3xl bg-white/15 backdrop-blur flex items-center justify-center">
              <span class="material-symbols-outlined text-4xl" style="font-variation-settings: 'FILL' 1;">psychology</span>
            </div>
            <h2 class="mt-4 text-2xl font-black">AI 助手核心体</h2>
            <p class="mt-2 text-white/85 text-sm leading-7">
              提供平台咨询、流程指导、建议分析与智能对话服务。
            </p>
          </div>

          <div class="p-6 space-y-4">
            <div class="grid grid-cols-2 gap-3">
              <div class="rounded-2xl bg-slate-50 p-4 border border-slate-100">
                <div class="text-[11px] uppercase tracking-wide text-slate-500 font-black">工作模式</div>
                <div class="mt-2 text-sm font-black text-slate-900">校园助手</div>
              </div>
              <div class="rounded-2xl bg-slate-50 p-4 border border-slate-100">
                <div class="text-[11px] uppercase tracking-wide text-slate-500 font-black">会话状态</div>
                <div class="mt-2 text-sm font-black text-emerald-600">实时在线</div>
              </div>
              <div class="rounded-2xl bg-slate-50 p-4 border border-slate-100">
                <div class="text-[11px] uppercase tracking-wide text-slate-500 font-black">对话轮次</div>
                <div class="mt-2 text-sm font-black text-slate-900">{{ messages.length }}</div>
              </div>
              <div class="rounded-2xl bg-slate-50 p-4 border border-slate-100">
                <div class="text-[11px] uppercase tracking-wide text-slate-500 font-black">支持能力</div>
                <div class="mt-2 text-sm font-black text-slate-900">问答 / 指导</div>
              </div>
            </div>

            <div class="rounded-2xl bg-indigo-50 border border-indigo-100 p-4">
              <div class="flex items-center gap-2 text-indigo-700 font-black text-sm">
                <span class="material-symbols-outlined text-base">bolt</span>
                能力标签
              </div>
              <div class="mt-3 flex flex-wrap gap-2">
                <span class="px-3 py-1 rounded-full bg-white text-indigo-700 text-xs font-black border border-indigo-100">平台介绍</span>
                <span class="px-3 py-1 rounded-full bg-white text-indigo-700 text-xs font-black border border-indigo-100">操作指导</span>
                <span class="px-3 py-1 rounded-full bg-white text-indigo-700 text-xs font-black border border-indigo-100">智能建议</span>
                <span class="px-3 py-1 rounded-full bg-white text-indigo-700 text-xs font-black border border-indigo-100">多轮对话</span>
              </div>
            </div>
          </div>
        </section>

        <!-- Quick Questions -->
        <section class="rounded-[28px] border border-white/70 bg-white/80 backdrop-blur-xl shadow-xl shadow-slate-200/60 p-6">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-indigo-600">dashboard_customize</span>
            <h3 class="text-lg font-black">快捷问题库</h3>
          </div>
          <p class="mt-2 text-sm text-slate-500">点击卡片快速发起智能对话</p>

          <div class="mt-5 grid grid-cols-1 gap-3">
            <button
              v-for="q in quickQuestions"
              :key="q"
              type="button"
              @click="quickAsk(q)"
              class="text-left rounded-2xl p-4 bg-slate-50 hover:bg-indigo-50 border border-slate-200 hover:border-indigo-200 transition-all duration-300 group"
            >
              <div class="flex items-start justify-between gap-3">
                <div class="text-sm font-bold text-slate-800 leading-6">{{ q }}</div>
                <span class="material-symbols-outlined text-slate-400 group-hover:text-indigo-600 transition-colors">north_east</span>
              </div>
            </button>
          </div>
        </section>
      </aside>

      <!-- Center Chat -->
      <section class="rounded-[32px] border border-white/70 bg-white/80 backdrop-blur-xl shadow-2xl shadow-slate-200/70 overflow-hidden flex flex-col min-h-[760px]">
        <!-- Chat Top -->
        <div class="px-6 py-5 border-b border-slate-200 bg-gradient-to-r from-slate-50 to-white">
          <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <div class="text-lg font-black text-slate-900">智能对话主控台</div>
              <div class="text-sm text-slate-500 mt-1">
                你可以自由提问，我会按上下文连续回复。
              </div>
            </div>

            <div class="flex items-center gap-3 flex-wrap">
              <div class="px-3 py-2 rounded-full bg-emerald-50 text-emerald-700 text-xs font-black border border-emerald-100">
                模型在线
              </div>
              <div class="px-3 py-2 rounded-full bg-indigo-50 text-indigo-700 text-xs font-black border border-indigo-100">
                多轮对话已启用
              </div>
            </div>
          </div>
        </div>

        <!-- Chat Body -->
        <div ref="chatBodyRef" class="flex-1 overflow-y-auto p-6 md:p-8 space-y-5 bg-[linear-gradient(180deg,rgba(248,250,252,0.7),rgba(255,255,255,0.95))]">
          <!-- Welcome Card -->
          <div v-if="messages.length <= 1" class="rounded-[28px] bg-gradient-to-br from-indigo-600 to-cyan-500 text-white p-6 shadow-xl shadow-indigo-500/20">
            <div class="flex items-center gap-3">
              <div class="w-12 h-12 rounded-2xl bg-white/15 flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings: 'FILL' 1;">rocket_launch</span>
              </div>
              <div>
                <div class="text-lg font-black">会话已启动</div>
                <div class="text-sm text-white/80">欢迎使用校园 AI 智能体工作台</div>
              </div>
            </div>

            <div class="mt-5 grid grid-cols-1 md:grid-cols-3 gap-3">
              <div class="rounded-2xl bg-white/10 p-4 border border-white/10">
                <div class="text-xs uppercase tracking-wide font-black text-white/70">功能介绍</div>
                <div class="mt-2 text-sm font-bold">快速讲解平台用途</div>
              </div>
              <div class="rounded-2xl bg-white/10 p-4 border border-white/10">
                <div class="text-xs uppercase tracking-wide font-black text-white/70">操作指导</div>
                <div class="mt-2 text-sm font-bold">教你如何发布与查看</div>
              </div>
              <div class="rounded-2xl bg-white/10 p-4 border border-white/10">
                <div class="text-xs uppercase tracking-wide font-black text-white/70">自然对话</div>
                <div class="mt-2 text-sm font-bold">直接像聊天一样问我</div>
              </div>
            </div>
          </div>

          <!-- Messages -->
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="flex"
            :class="msg.role === 'user' ? 'justify-end' : 'justify-start'"
          >
            <div
              class="max-w-[88%] md:max-w-[76%] rounded-[28px] px-5 py-4 border shadow-sm"
              :class="msg.role === 'user'
                ? 'bg-slate-900 text-white border-slate-900 shadow-slate-900/10 rounded-br-md'
                : 'bg-white text-slate-900 border-slate-200 rounded-bl-md'"
            >
              <div class="flex items-center gap-2 mb-2 text-xs font-black tracking-wide opacity-80">
                <span class="material-symbols-outlined text-sm">
                  {{ msg.role === 'user' ? 'person' : 'smart_toy' }}
                </span>
                <span>{{ msg.role === 'user' ? '你' : 'AI 智能体' }}</span>
              </div>
              <div class="whitespace-pre-wrap leading-7 text-sm md:text-[15px]">
                {{ msg.content }}
              </div>
            </div>
          </div>

          <!-- Loading -->
          <div v-if="loading" class="flex justify-start">
            <div class="max-w-[88%] md:max-w-[76%] rounded-[28px] rounded-bl-md px-5 py-4 border shadow-sm bg-white text-slate-900 border-slate-200">
              <div class="flex items-center gap-2 mb-2 text-xs font-black tracking-wide opacity-80">
                <span class="material-symbols-outlined text-sm">smart_toy</span>
                <span>AI 智能体</span>
              </div>
              <div class="flex items-center gap-2 text-sm text-slate-500">
                <span class="w-2 h-2 rounded-full bg-indigo-500 animate-bounce"></span>
                <span class="w-2 h-2 rounded-full bg-indigo-500 animate-bounce [animation-delay:120ms]"></span>
                <span class="w-2 h-2 rounded-full bg-indigo-500 animate-bounce [animation-delay:240ms]"></span>
                <span class="ml-2">正在分析你的问题...</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Input Area -->
        <div class="p-5 border-t border-slate-200 bg-white">
          <div class="rounded-[28px] border border-slate-200 bg-slate-50 p-3 shadow-inner">
            <div class="grid grid-cols-1 lg:grid-cols-[1fr_auto] gap-3">
              <div class="relative">
                <textarea
                  v-model="queryText"
                  rows="3"
                  maxlength="300"
                  class="w-full px-5 py-4 bg-white text-slate-900 rounded-3xl outline-none text-base shadow-sm border border-slate-200 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 transition-all placeholder:text-slate-400 resize-none"
                  :placeholder="placeholder"
                  @keyup.ctrl.enter="handleSend"
                ></textarea>
                <div class="absolute right-4 bottom-4 text-xs text-slate-400">
                  <span class="font-mono">{{ queryText.length }}</span> / 300
                </div>
              </div>

              <div class="flex flex-col gap-3">
                <button
                  type="button"
                  @click="handleSend"
                  :disabled="loading || !queryText.trim()"
                  class="px-8 py-4 rounded-3xl bg-gradient-to-r from-indigo-600 to-cyan-500 text-white font-black text-base shadow-xl shadow-indigo-500/20 hover:shadow-2xl hover:-translate-y-0.5 active:translate-y-0 transition-all duration-300 disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none flex items-center justify-center gap-2"
                >
                  <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1, 'wght' 600;">send</span>
                  发送指令
                </button>

                <button
                  type="button"
                  @click="resetChat"
                  class="px-8 py-4 rounded-3xl bg-white text-slate-600 font-bold border border-slate-200 hover:bg-slate-50 transition-colors"
                >
                  清空会话
                </button>
              </div>
            </div>

            <div class="mt-3 flex flex-wrap items-center justify-between gap-3 text-xs text-slate-500 px-2">
              <span class="inline-flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">keyboard</span>
                支持 Ctrl + Enter 快速发送
              </span>
              <span class="inline-flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">chat</span>
                推荐像自然聊天一样直接提问
              </span>
            </div>
          </div>
        </div>
      </section>

      <!-- Right Panel -->
      <aside class="space-y-6">
        <!-- Session Metrics -->
        <section class="rounded-[28px] border border-white/70 bg-white/80 backdrop-blur-xl shadow-xl shadow-slate-200/60 p-6">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-cyan-600">monitoring</span>
            <h3 class="text-lg font-black">会话指标</h3>
          </div>

          <div class="mt-5 grid grid-cols-2 gap-3">
            <div class="rounded-2xl bg-slate-50 border border-slate-100 p-4">
              <div class="text-[11px] uppercase tracking-wide font-black text-slate-500">消息数</div>
              <div class="mt-2 text-2xl font-black">{{ messages.length }}</div>
            </div>
            <div class="rounded-2xl bg-slate-50 border border-slate-100 p-4">
              <div class="text-[11px] uppercase tracking-wide font-black text-slate-500">用户问题</div>
              <div class="mt-2 text-2xl font-black">{{ userMessageCount }}</div>
            </div>
            <div class="rounded-2xl bg-slate-50 border border-slate-100 p-4">
              <div class="text-[11px] uppercase tracking-wide font-black text-slate-500">AI回复</div>
              <div class="mt-2 text-2xl font-black">{{ assistantMessageCount }}</div>
            </div>
            <div class="rounded-2xl bg-slate-50 border border-slate-100 p-4">
              <div class="text-[11px] uppercase tracking-wide font-black text-slate-500">当前状态</div>
              <div class="mt-2 text-sm font-black" :class="loading ? 'text-indigo-600' : 'text-emerald-600'">
                {{ loading ? '思考中' : '空闲中' }}
              </div>
            </div>
          </div>
        </section>

        <!-- Recommended Topics -->
        <section class="rounded-[28px] border border-white/70 bg-white/80 backdrop-blur-xl shadow-xl shadow-slate-200/60 p-6">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-fuchsia-600">auto_awesome</span>
            <h3 class="text-lg font-black">推荐话题</h3>
          </div>
          <p class="mt-2 text-sm text-slate-500">适合立即体验的智能问答方向</p>

          <div class="mt-5 space-y-3">
            <div
              v-for="topic in hotTopics"
              :key="topic.title"
              class="rounded-2xl border border-slate-200 bg-slate-50 p-4 hover:bg-white transition-colors"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <div class="text-sm font-black text-slate-900">{{ topic.title }}</div>
                  <div class="mt-1 text-xs text-slate-500 leading-6">{{ topic.desc }}</div>
                </div>
                <button
                  type="button"
                  @click="quickAsk(topic.ask)"
                  class="shrink-0 w-9 h-9 rounded-full bg-white border border-slate-200 text-slate-700 hover:text-indigo-600 hover:border-indigo-200 transition-colors flex items-center justify-center"
                >
                  <span class="material-symbols-outlined text-base">arrow_upward</span>
                </button>
              </div>
            </div>
          </div>
        </section>
      </aside>
    </main>
  </div>
</template>

<script setup>
import { chatAi } from '@/api/ai'
import { ElMessage } from 'element-plus'
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const placeholder = '输入你的问题，例如：这个平台可以做什么？怎么发布失物信息？'
const queryText = ref('')
const loading = ref(false)
const chatBodyRef = ref(null)

const quickQuestions = [
  '这个平台可以做什么？',
  '怎么发布失物信息？',
  '怎么发布招领信息？',
  '怎么查看招领公示？',
  '我丢了校园卡，在食堂附近。'
]

const hotTopics = [
  {
    title: '平台功能总览',
    desc: '快速了解平台都支持哪些功能和入口。',
    ask: '这个平台可以做什么？'
  },
  {
    title: '发布失物流程',
    desc: '适合第一次使用平台的同学。',
    ask: '怎么发布失物信息？'
  },
  {
    title: '发布招领流程',
    desc: '拾到物品后如何规范发布。',
    ask: '怎么发布招领信息？'
  },
  {
    title: '招领查看方式',
    desc: '如何快速浏览公示和认领信息。',
    ask: '怎么查看招领公示？'
  }
]

const messages = ref([
  {
    id: 1,
    role: 'assistant',
    content:
      '你好，我是校园 AI 智能体 👋\n\n这里是一个更像“智能体工作台”的对话界面。\n我可以帮助你：\n1. 介绍平台功能\n2. 指导发布失物/招领信息\n3. 说明如何查看招领公示\n4. 提供场景化建议\n\n你可以直接像聊天一样问我。'
  }
])

const userMessageCount = computed(() => messages.value.filter(m => m.role === 'user').length)
const assistantMessageCount = computed(() => messages.value.filter(m => m.role === 'assistant').length)

const buildAiHistory = () => {
  return messages.value
    .filter(msg => msg.role === 'user' || msg.role === 'assistant')
    .slice(-10)
    .map(msg => ({
      role: msg.role === 'assistant' ? 'assistant' : 'user',
      content: msg.content
    }))
}

const chatWithAi = async (text) => {
  const resp = await chatAi({
    message: text,
    history: buildAiHistory()
  })

  if (resp?.code !== 200) {
    throw new Error(resp?.message || 'AI接口调用失败')
  }

  return resp?.data?.reply || '抱歉，我暂时无法回答这个问题。'
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatBodyRef.value) {
    chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  }
}

const appendMessage = async (role, content) => {
  messages.value.push({
    id: Date.now() + Math.random(),
    role,
    content
  })
  await scrollToBottom()
}

const handleSend = async () => {
  const text = queryText.value.trim()
  if (!text) {
    ElMessage.warning('请输入内容')
    return
  }

  await appendMessage('user', text)
  queryText.value = ''
  loading.value = true

  try {
    const reply = await chatWithAi(text)
    await appendMessage('assistant', reply)
  } catch (e) {
    console.error('AI助手处理失败:', e)
    ElMessage.error(e?.message || '处理失败，请稍后再试')
    await appendMessage('assistant', '抱歉，我刚刚没有成功处理你的请求。你可以稍后再试。')
  } finally {
    loading.value = false
  }
}

const quickAsk = async (text) => {
  queryText.value = text
  await handleSend()
}

const resetChat = async () => {
  queryText.value = ''
  messages.value = [
    {
      id: 1,
      role: 'assistant',
      content:
        '你好，我是校园 AI 智能体 👋\n\n这里是一个更像“智能体工作台”的对话界面。\n我可以帮助你：\n1. 介绍平台功能\n2. 指导发布失物/招领信息\n3. 说明如何查看招领公示\n4. 提供场景化建议\n\n你可以直接像聊天一样问我。'
    }
  ]
  router.replace({ path: '/ai-search' })
  await scrollToBottom()
}

onMounted(async () => {
  await scrollToBottom()

  const q = String(route.query.q || '').trim()
  if (q) {
    queryText.value = q
    handleSend()
  }
})
</script>

<style scoped>
.shadow-primary\/10 { --tw-shadow-color: rgba(99, 102, 241, 0.10); }
.shadow-primary\/20 { --tw-shadow-color: rgba(99, 102, 241, 0.20); }
.shadow-primary\/25 { --tw-shadow-color: rgba(99, 102, 241, 0.25); }
</style>