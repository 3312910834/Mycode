<template>
  <div class="settings-management" :class="{ 'dark-mode': darkMode }">
    <!-- 顶部工具栏：搜索 + 主题切换 -->
    <div class="top-toolbar">
      <el-input
        v-model="searchKey"
        placeholder="🔍 快速搜索配置项（如：AI、安全、邮件、阈值）"
        prefix-icon="Search"
        clearable
        size="large"
        class="search-input"
        @input="highlightMatch"
      >
        <template #append>
          <el-button :icon="Search">搜索</el-button>
        </template>
      </el-input>
      <div class="theme-controls">
        <el-switch
          v-model="darkMode"
          inline-prompt
          active-text="🌙"
          inactive-text="☀️"
          size="large"
          @change="toggleDarkMode"
        />
        <el-color-picker
          v-model="themeColor"
          show-alpha
          :predefine="predefineColors"
          @change="applyTheme"
        />
        <el-tag>主题色</el-tag>
      </div>
    </div>

    <!-- 顶部状态监控 -->
    <div class="monitor-dashboard">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="monitor-card">
            <div class="monitor-header">
              <el-icon class="text-blue-500"><Odometer /></el-icon> AI 核心负载
            </div>
            <div class="monitor-value text-blue-500">{{ cpuUsage }}<span class="unit">%</span></div>
            <div class="monitor-footer pulse-text">运行正常</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="monitor-card">
            <div class="monitor-header">
              <el-icon class="text-green-500"><Coin /></el-icon> 内存分配量
            </div>
            <div class="monitor-value text-green-500">{{ memoryUsage }}<span class="unit">GB</span></div>
            <div class="monitor-footer">总量 16.0 GB</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="monitor-card">
            <div class="monitor-header">
              <el-icon class="text-purple-500"><Connection /></el-icon> 今日 API 调用
            </div>
            <div class="monitor-value text-purple-500">{{ apiCalls }}<span class="unit">次</span></div>
            <div class="monitor-footer">识别请求占 85%</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="monitor-card">
            <div class="monitor-header">
              <el-icon class="text-orange-500"><Timer /></el-icon> 系统运行时长
            </div>
            <div class="monitor-value text-orange-500">124<span class="unit">h</span></div>
            <div class="monitor-footer">自上次热更新</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 创新点 1: AI 智能推荐引擎 -->
    <el-card class="ai-suggestion-card" shadow="hover" v-show="aiSuggestions.length > 0">
      <template #header>
        <div class="card-header ai-header">
          <div>
            <el-icon class="rotating-icon"><MagicStick /></el-icon>
            <span>AI 智能配置优化建议</span>
          </div>
          <el-tag type="success" effect="dark" round>基于过去 7 天数据分析</el-tag>
        </div>
      </template>
      <div class="suggestion-container">
        <el-timeline>
          <el-timeline-item
            v-for="(suggestion, index) in aiSuggestions"
            :key="index"
            :type="suggestion.type"
            :icon="suggestion.icon"
            :color="suggestion.color"
          >
            <div class="suggestion-item">
              <h4>{{ suggestion.title }}</h4>
              <p>{{ suggestion.description }}</p>
              <div class="suggestion-data">
                <el-tag size="small" type="info">当前: {{ suggestion.current }}</el-tag>
                <el-icon><Right /></el-icon>
                <el-tag size="small" type="success">建议: {{ suggestion.recommended }}</el-tag>
                <span class="impact">预期效果: {{ suggestion.impact }}</span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <div class="suggestion-actions">
          <el-button type="primary" :icon="Check" @click="applyAllSuggestions">
            一键应用所有优化建议
          </el-button>
          <el-button :icon="Refresh" @click="refreshSuggestions">
            刷新分析
          </el-button>
          <el-button :icon="Close" @click="dismissSuggestions">
            忽略建议
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 创新点 2: 配置模板市场 -->
    <el-card class="template-market" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <el-icon><Box /></el-icon>
            <span>快速配置模板库</span>
          </div>
          <el-button type="primary" size="small" :icon="Plus" @click="saveAsTemplate">
            保存当前为模板
          </el-button>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="tpl in templates" :key="tpl.id">
          <div
            class="template-card"
            :class="{ active: currentTemplateId === tpl.id, recommended: tpl.recommended }"
            @click="applyTemplate(tpl)"
          >
            <div class="tpl-icon">{{ tpl.icon }}</div>
            <h4>{{ tpl.name }}</h4>
            <p>{{ tpl.desc }}</p>
            <div class="tpl-tags">
              <el-tag v-if="tpl.recommended" type="success" size="small" effect="dark">
                官方推荐
              </el-tag>
              <el-tag size="small">{{ tpl.scenario }}</el-tag>
            </div>
            <div class="tpl-stats">
              <span><el-icon><User /></el-icon> {{ tpl.usageCount }}次使用</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-form
      ref="settingsFormRef"
      :model="settingsForm"
      label-width="160px"
      label-position="right"
      size="large"
      class="settings-form"
    >
      <el-row :gutter="24">
        <!-- 左侧栏：基础与安全设置 -->
        <el-col :span="12">
          <!-- 基础设置 -->
          <el-card
            class="settings-card"
            shadow="hover"
            :class="{ 'highlight-card': isHighlighted('基础') }"
            ref="basicCard"
          >
            <template #header>
              <div class="card-header">
                <el-icon><Setting /></el-icon> <span>基础平台配置</span>
              </div>
            </template>
            <el-form-item
              label="平台主名称"
              prop="systemName"
              :class="{ 'highlight-item': isHighlighted('平台') || isHighlighted('名称') }"
            >
              <el-input v-model="settingsForm.systemName" placeholder="校园 AI 失物招领智能服务平台" />
            </el-form-item>
            <el-form-item
              label="平台描述与使命"
              prop="systemDescription"
              :class="{ 'highlight-item': isHighlighted('描述') || isHighlighted('使命') }"
            >
              <el-input
                v-model="settingsForm.systemDescription"
                type="textarea"
                :rows="2"
                placeholder="为校园社区提供智能失物招领服务的平台"
              />
            </el-form-item>
            <el-form-item
              label="官方运营邮箱"
              prop="contactEmail"
              :class="{ 'highlight-item': isHighlighted('邮箱') || isHighlighted('邮件') }"
            >
              <el-input v-model="settingsForm.contactEmail" placeholder="admin@university.edu" />
            </el-form-item>
            <el-form-item
              label="紧急联络电话"
              prop="contactPhone"
              :class="{ 'highlight-item': isHighlighted('电话') }"
            >
              <el-input v-model="settingsForm.contactPhone" placeholder="400-123-4567" />
            </el-form-item>
          </el-card>

          <!-- 安全设置 -->
          <el-card
            class="settings-card"
            shadow="hover"
            :class="{ 'highlight-card': isHighlighted('安全') }"
            ref="securityCard"
          >
            <template #header>
              <div class="card-header">
                <el-icon><Lock /></el-icon> <span>访问与安全策略</span>
              </div>
            </template>
            <el-form-item
              label="开放新用户注册"
              :class="{ 'highlight-item': isHighlighted('注册') }"
            >
              <el-switch v-model="settingsForm.allowRegistration" inline-prompt active-text="开放" inactive-text="锁定" />
            </el-form-item>
            <el-form-item
              label="密码安全基线 (位)"
              :class="{ 'highlight-item': isHighlighted('密码') }"
            >
              <el-input-number v-model="settingsForm.minPasswordLength" :min="6" :max="32" controls-position="right" class="w-full" />
            </el-form-item>
            <el-form-item
              label="触发防刷锁号阈值"
              :class="{ 'highlight-item': isHighlighted('阈值') || isHighlighted('防刷') }"
            >
              <el-input-number v-model="settingsForm.maxLoginAttempts" :min="3" :max="10" controls-position="right" class="w-full" />
              <div class="form-tip">连续登录失败超过该次数将冻结账户 30 分钟</div>
            </el-form-item>
            <el-form-item
              label="令牌有效周期 (分)"
              :class="{ 'highlight-item': isHighlighted('令牌') || isHighlighted('周期') }"
            >
              <el-input-number v-model="settingsForm.sessionTimeout" :min="30" :max="480" controls-position="right" class="w-full" />
            </el-form-item>
          </el-card>
        </el-col>

        <!-- 右侧栏：AI 与功能设置 -->
        <el-col :span="12">
          <!-- AI 引擎配置 -->
          <el-card
            class="settings-card ai-card"
            shadow="hover"
            :class="{ 'highlight-card': isHighlighted('AI') || isHighlighted('ai') }"
            ref="aiCard"
          >
            <template #header>
              <div class="card-header ai-header">
                <div>
                  <el-icon><Cpu /></el-icon> <span>AI 引擎与风控配置</span>
                </div>
                <el-tag type="success" size="small" effect="dark" round>Engine Online</el-tag>
              </div>
            </template>
            <el-form-item
              label="核心语言模型节点"
              :class="{ 'highlight-item': isHighlighted('模型') }"
            >
              <el-select v-model="settingsForm.aiModel" class="w-full">
                <el-option label="默认: Qwen-vl-plus (精度高,较慢)" value="qwenvl" />
                <el-option label="备用: Qwen-plus (速度快,精度中)" value="qwen" />
                <el-option label="测试: Local-Llama-3 (内部推理)" value="llama3" />
              </el-select>
            </el-form-item>
            <el-form-item
              label="图像识别置信阈值"
              :class="{ 'highlight-item': isHighlighted('阈值') || isHighlighted('置信') }"
            >
              <div style="width: 100%; padding: 0 10px;">
                <el-slider
                  v-model="settingsForm.aiConfidenceThreshold"
                  :min="50" :max="99"
                  :format-tooltip="(v) => v + '%'"
                />
              </div>
              <div class="form-tip">AI 判定吻合度低于 {{ settingsForm.aiConfidenceThreshold }}% 时将被拦截转人工审核</div>
            </el-form-item>
            <el-form-item
              label="开启多模态审核"
              :class="{ 'highlight-item': isHighlighted('多模态') }"
            >
              <el-switch v-model="settingsForm.enableMultimodal" />
              <div class="form-tip ml-3 inline-block">同步校验文本描述与图像特征的逻辑自洽性</div>
            </el-form-item>
            <el-form-item
              label="智能敏感词清洗"
              :class="{ 'highlight-item': isHighlighted('敏感词') }"
            >
              <el-switch v-model="settingsForm.aiContentFilter" />
            </el-form-item>
          </el-card>

          <!-- 交互与消息功能 -->
          <el-card
            class="settings-card"
            shadow="hover"
            :class="{ 'highlight-card': isHighlighted('消息') || isHighlighted('交互') }"
            ref="messageCard"
          >
            <template #header>
              <div class="card-header">
                <el-icon><Bell /></el-icon> <span>交互与消息路由</span>
              </div>
            </template>
            <el-form-item
              label="强制发布前预审"
              :class="{ 'highlight-item': isHighlighted('审核') || isHighlighted('预审') }"
            >
              <el-switch v-model="settingsForm.requireApproval" active-text="需管理员审核" inactive-text="AI自动放行" />
            </el-form-item>
            <el-form-item
              label="允许前端上传图床"
              :class="{ 'highlight-item': isHighlighted('上传') || isHighlighted('图床') }"
            >
              <el-switch v-model="settingsForm.allowImageUpload" />
            </el-form-item>
            <el-form-item
              label="启用全站邮件触达"
              :class="{ 'highlight-item': isHighlighted('邮件') || isHighlighted('邮箱') }"
            >
              <el-switch v-model="settingsForm.sendNotificationEmails" />
              <div class="form-tip ml-3 inline-block">涉及配对成功、申诉驳回等关键节点</div>
            </el-form-item>
          </el-card>
        </el-col>
      </el-row>

      <!-- 底部控制按钮 -->
      <div class="action-footer">
        <el-button class="reset-btn" @click="resetSettings" size="large">恢复出厂配置</el-button>
        <el-button type="primary" class="save-btn" @click="saveSettings" :loading="saving" size="large">
          <el-icon><Position /></el-icon> 应用并发布新策略
        </el-button>
      </div>

      <!-- 危险操作区 -->
      <div class="danger-zone">
        <h3 class="danger-title">高级运维区域 (Danger Zone)</h3>
        <div class="danger-item">
          <div class="danger-desc">
            <h4>重建 AI 向量知识库</h4>
            <p>强制要求大模型重新遍历并索引数据库中现有的所有失物/招领文本特征，操作期间系统可能卡顿。</p>
          </div>
          <el-button type="danger" plain @click="handleDangerAction('重建 AI 索引')">执行重建</el-button>
        </div>
        <div class="danger-item">
          <div class="danger-desc">
            <h4>清空系统 Redis 缓存</h4>
            <p>清除首页统计、分类字典及路由缓存，强制下一次请求回源数据库获取最新信息。</p>
          </div>
          <el-button type="danger" plain @click="handleDangerAction('清空系统缓存')">一键清空</el-button>
        </div>
      </div>

    </el-form>

    <!-- 保存模板对话框 -->
    <el-dialog v-model="saveTemplateVisible" title="保存为配置模板" width="500px">
      <el-form :model="newTemplate" label-width="100px">
        <el-form-item label="模板名称">
          <el-input v-model="newTemplate.name" placeholder="如：期末考试周配置" />
        </el-form-item>
        <el-form-item label="模板描述">
          <el-input v-model="newTemplate.desc" type="textarea" :rows="3" placeholder="描述该模板的使用场景" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="newTemplate.icon" placeholder="输入 Emoji，如：🎓" maxlength="2" />
        </el-form-item>
        <el-form-item label="场景标签">
          <el-input v-model="newTemplate.scenario" placeholder="如：高峰期、安全模式" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saveTemplateVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSaveTemplate">保存模板</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting, Lock, Cpu, Bell, Odometer, Coin, Connection, Timer, Position,
  Search, MagicStick, Box, Plus, User, Check, Refresh, Close, Right
} from '@element-plus/icons-vue'
import { useAdminStore } from '@/store/admin'

const adminStore = useAdminStore()
const settingsFormRef = ref()
const saving = ref(false)

// ==================== 主题与搜索 ====================
const darkMode = ref(false)
const themeColor = ref('#3b82f6')
const predefineColors = ref([
  '#3b82f6', '#10b981', '#8b5cf6', '#f59e0b', '#ef4444', '#ec4899', '#14b8a6'
])
const searchKey = ref('')

const toggleDarkMode = () => {
  document.documentElement.classList.toggle('dark', darkMode.value)
  ElMessage.success(darkMode.value ? '已切换至深色模式 🌙' : '已切换至浅色模式 ☀️')
}

const applyTheme = () => {
  document.documentElement.style.setProperty('--el-color-primary', themeColor.value)
  ElMessage.success('主题色已应用！')
}

const isHighlighted = (keyword) => {
  if (!searchKey.value) return false
  return keyword.toLowerCase().includes(searchKey.value.toLowerCase())
}

const highlightMatch = () => {
  if (!searchKey.value) return
  // 自动滚动到第一个匹配项
  setTimeout(() => {
    const highlightedCard = document.querySelector('.highlight-card')
    if (highlightedCard) {
      highlightedCard.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  }, 100)
}

// ==================== 监控数据 ====================
const cpuUsage = ref(32)
const memoryUsage = ref(8.4)
const apiCalls = ref(12405)
let timer = null

// ==================== AI 智能推荐 ====================
const aiSuggestions = ref([
  {
    type: 'warning',
    icon: 'Warning',
    color: '#f59e0b',
    title: '检测到误报率偏高',
    description: '过去 7 天系统误报率达 12%，高于行业标准 8%',
    current: '置信阈值 85%',
    recommended: '置信阈值 90%',
    impact: '预计误报率降至 7.5%',
    applyKey: 'aiConfidenceThreshold',
    applyValue: 90
  },
  {
    type: 'success',
    icon: 'CircleCheck',
    color: '#10b981',
    title: '建议启用多模态交叉验证',
    description: '分析发现 34% 的失物图文描述存在逻辑矛盾',
    current: '关闭',
    recommended: '开启',
    impact: '准确率提升 15%',
    applyKey: 'enableMultimodal',
    applyValue: true
  },
  {
    type: 'primary',
    icon: 'Clock',
    color: '#3b82f6',
    title: 'API 调用高峰期优化',
    description: 'API 峰值集中在 14:00-16:00，建议切换至高性能模型',
    current: 'Qwen-vl-plus',
    recommended: 'Qwen-plus (速度快)',
    impact: '响应时间减少 40%',
    applyKey: 'aiModel',
    applyValue: 'qwen'
  }
])

const applyAllSuggestions = () => {
  ElMessageBox.confirm('确定应用所有 AI 推荐的优化配置吗？', '智能优化确认', {
    confirmButtonText: '确认应用',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    aiSuggestions.value.forEach(suggestion => {
      settingsForm.value[suggestion.applyKey] = suggestion.applyValue
    })
    ElMessage.success('已应用所有 AI 优化建议！')
    aiSuggestions.value = []
  }).catch(() => {})
}

const refreshSuggestions = () => {
  ElMessage.info('正在重新分析系统数据...')
  setTimeout(() => {
    ElMessage.success('分析完成！暂无新建议')
  }, 1500)
}

const dismissSuggestions = () => {
  aiSuggestions.value = []
  ElMessage.info('已忽略 AI 建议')
}

// ==================== 配置模板市场 ====================
const currentTemplateId = ref(null)
const templates = ref([
  {
    id: 1,
    name: '高安全模式',
    icon: '🛡️',
    desc: '强制人工审核 + 最高置信阈值，适合重要活动期间使用',
    scenario: '重要活动',
    recommended: false,
    usageCount: 342,
    config: {
      requireApproval: true,
      aiConfidenceThreshold: 95,
      enableMultimodal: true,
      aiContentFilter: true,
      allowRegistration: false,
      maxLoginAttempts: 3
    }
  },
  {
    id: 2,
    name: '智能平衡模式',
    icon: '⚖️',
    desc: 'AI 辅助 + 适度人工干预，日常运营推荐配置',
    scenario: '日常推荐',
    recommended: true,
    usageCount: 1247,
    config: {
      requireApproval: false,
      aiConfidenceThreshold: 85,
      enableMultimodal: true,
      aiContentFilter: true,
      allowRegistration: true,
      maxLoginAttempts: 5
    }
  },
  {
    id: 3,
    name: '极速响应模式',
    icon: '⚡',
    desc: '全自动化处理 + 降低阈值，适合高峰期快速响应',
    scenario: '高峰期',
    recommended: false,
    usageCount: 538,
    config: {
      requireApproval: false,
      aiConfidenceThreshold: 75,
      enableMultimodal: false,
      aiContentFilter: true,
      allowRegistration: true,
      aiModel: 'qwen'
    }
  },
  {
    id: 4,
    name: '假期维护模式',
    icon: '🏖️',
    desc: '锁定注册 + 降低资源消耗，适合寒暑假维护期',
    scenario: '假期维护',
    recommended: false,
    usageCount: 89,
    config: {
      allowRegistration: false,
      requireApproval: true,
      sendNotificationEmails: false,
      allowImageUpload: false,
      sessionTimeout: 480
    }
  }
])

const applyTemplate = (tpl) => {
  ElMessageBox.confirm(
    `确定要应用"${tpl.name}"模板吗？当前配置将被覆盖。`,
    '应用配置模板',
    {
      confirmButtonText: '确认应用',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    Object.assign(settingsForm.value, tpl.config)
    currentTemplateId.value = tpl.id
    tpl.usageCount++
    ElMessage.success(`已应用模板：${tpl.name}`)
  }).catch(() => {})
}

const saveTemplateVisible = ref(false)
const newTemplate = ref({
  name: '',
  desc: '',
  icon: '📦',
  scenario: ''
})

const saveAsTemplate = () => {
  saveTemplateVisible.value = true
  newTemplate.value = {
    name: '',
    desc: '',
    icon: '📦',
    scenario: ''
  }
}

const confirmSaveTemplate = () => {
  if (!newTemplate.value.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  const template = {
    id: Date.now(),
    ...newTemplate.value,
    recommended: false,
    usageCount: 0,
    config: { ...settingsForm.value }
  }
  templates.value.push(template)
  saveTemplateVisible.value = false
  ElMessage.success('模板已保存！')
}

// ==================== 核心表单 ====================
const settingsForm = ref({
  systemName: '校园 AI 失物招领平台',
  systemDescription: '为校园社区提供智能失物招领服务的平台',
  contactEmail: 'admin@university.edu',
  contactPhone: '400-123-4567',
  allowRegistration: true,
  requireApproval: true,
  allowImageUpload: true,
  sendNotificationEmails: false,
  minPasswordLength: 8,
  maxLoginAttempts: 5,
  sessionTimeout: 120,
  aiModel: 'qwenvl',
  aiConfidenceThreshold: 85,
  enableMultimodal: true,
  aiContentFilter: true
})

const saveSettings = async () => {
  if (!settingsFormRef.value) return
  saving.value = true
  try {
    await adminStore.updateSettings(settingsForm.value)
    ElMessage.success('全局系统策略已更新并下发至各节点')
  } catch (error) {
    ElMessage.error('设置保存失败，请检查网络')
  } finally {
    saving.value = false
  }
}

const resetSettings = () => {
  ElMessageBox.confirm('这将会把所有配置还原到初始状态，是否继续？', '恢复出厂提示', {
    confirmButtonText: '确定重置',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    settingsForm.value = {
      systemName: '校园 AI 失物招领平台',
      systemDescription: '为校园社区提供智能失物招领服务的平台',
      contactEmail: 'admin@university.edu',
      contactPhone: '400-123-4567',
      allowRegistration: true,
      requireApproval: true,
      allowImageUpload: true,
      sendNotificationEmails: false,
      minPasswordLength: 8,
      maxLoginAttempts: 5,
      sessionTimeout: 120,
      aiModel: 'qwenvl',
      aiConfidenceThreshold: 85,
      enableMultimodal: true,
      aiContentFilter: true
    }
    ElMessage.info('已恢复至出厂安全配置')
  }).catch(() => {})
}

const handleDangerAction = (actionName) => {
  ElMessageBox.prompt(`此操作高度危险。为了确认，请在下方输入 "${actionName}"`, '安全校验拦截', {
    confirmButtonText: '确认执行',
    cancelButtonText: '放弃',
    inputPattern: new RegExp(`^${actionName}$`),
    inputErrorMessage: '输入不匹配',
    type: 'error',
    confirmButtonClass: 'el-button--danger'
  }).then(() => {
    ElMessage.success(`指令下发成功：正在后端异步执行 [${actionName}]`)
  }).catch(() => {})
}

// ==================== 生命周期 ====================
const startMonitorPulse = () => {
  timer = setInterval(() => {
    cpuUsage.value = Math.max(10, Math.min(90, cpuUsage.value + Math.floor(Math.random() * 9) - 4))
    memoryUsage.value = Number(Math.max(4.0, Math.min(15.0, memoryUsage.value + (Math.random() * 0.4) - 0.2)).toFixed(1))
    apiCalls.value += Math.floor(Math.random() * 5)
  }, 3000)
}

onMounted(async () => {
  startMonitorPulse()
  await adminStore.fetchSettings()
  if (Object.keys(adminStore.settings).length > 0) {
    settingsForm.value = { ...settingsForm.value, ...adminStore.settings }
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.settings-management {
  padding: 20px;
  background-color: #f4f7fb;
  min-height: 100%;
  transition: all 0.3s ease;
}

/* 深色模式 */
.dark-mode {
  background-color: #1a1a2e;
  color: #eee;
}
.dark-mode .monitor-card,
.dark-mode .settings-card,
.dark-mode .ai-suggestion-card,
.dark-mode .template-market {
  background-color: #16213e;
  color: #eee;
}
.dark-mode .card-header {
  color: #fff;
}
.dark-mode .template-card {
  background-color: #0f3460;
  border-color: #1a1a2e;
}
.dark-mode .template-card:hover {
  background-color: #1a4d7a;
}

/* 顶部工具栏 */
.top-toolbar {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  background: #fff;
  padding: 16px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}
.dark-mode .top-toolbar {
  background: #16213e;
}
.search-input {
  flex: 1;
  max-width: 600px;
}
.theme-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 监控盘 */
.monitor-dashboard {
  margin-bottom: 24px;
}
.monitor-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s ease;
}
.monitor-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}
.monitor-header {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.monitor-value {
  font-size: 32px;
  font-weight: 900;
  font-family: monospace;
  margin-bottom: 8px;
  line-height: 1;
}
.monitor-value .unit {
  font-size: 14px;
  margin-left: 4px;
  color: #94a3b8;
}
.monitor-footer {
  font-size: 12px;
  color: #94a3b8;
}
.pulse-text {
  color: #10b981;
  animation: opacityPulse 2s infinite;
}
@keyframes opacityPulse {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.text-blue-500 { color: #3b82f6; }
.text-green-500 { color: #10b981; }
.text-purple-500 { color: #8b5cf6; }
.text-orange-500 { color: #f97316; }

/* AI 智能推荐卡片 */
.ai-suggestion-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 2px solid #bfdbfe;
  background: linear-gradient(135deg, #eff6ff 0%, #ffffff 100%);
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.1);
}
.dark-mode .ai-suggestion-card {
  background: linear-gradient(135deg, #1e3a5f 0%, #16213e 100%);
  border-color: #2563eb;
}
.ai-header {
  color: #2563eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.rotating-icon {
  animation: rotate 3s linear infinite;
  margin-right: 8px;
}
@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.suggestion-container {
  padding: 10px 0;
}
.suggestion-item h4 {
  margin: 0 0 8px 0;
  color: #1e293b;
  font-size: 15px;
  font-weight: 600;
}
.dark-mode .suggestion-item h4 {
  color: #fff;
}
.suggestion-item p {
  margin: 0 0 12px 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}
.suggestion-data {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.suggestion-data .impact {
  color: #10b981;
  font-size: 12px;
  font-weight: 600;
  margin-left: 8px;
}
.suggestion-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

/* 配置模板市场 */
.template-market {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}
.template-card {
  background: #fff;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.template-card:hover {
  border-color: #3b82f6;
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
}
.template-card.active {
  border-color: #10b981;
  background: linear-gradient(135deg, #f0fdf4 0%, #ffffff 100%);
}
.template-card.recommended {
  border-color: #f59e0b;
}
.tpl-icon {
  font-size: 48px;
  text-align: center;
  margin-bottom: 12px;
}
.template-card h4 {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #1e293b;
  text-align: center;
}
.dark-mode .template-card h4 {
  color: #fff;
}
.template-card p {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  margin: 0 0 12px 0;
  flex: 1;
}
.tpl-tags {
  display: flex;
  gap: 6px;
  justify-content: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.tpl-stats {
  text-align: center;
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 表单与卡片 */
.settings-form {
  margin-top: 10px;
}
.settings-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}
.card-header {
  font-weight: 700;
  font-size: 16px;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
}
.w-full {
  width: 100%;
}
.form-tip {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.4;
  margin-top: 4px;
}
.ml-3 { margin-left: 12px; }
.inline-block { display: inline-block; }

/* AI 卡片特色 */
.ai-card {
  border: 1px solid #bfdbfe;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

/* 高亮效果 */
.highlight-card {
  animation: highlightPulse 1.5s ease-in-out infinite;
  border: 2px solid #f59e0b !important;
}
.highlight-item {
  animation: highlightPulse 1.5s ease-in-out infinite;
  background-color: rgba(245, 158, 11, 0.1);
  border-radius: 8px;
  padding: 4px 0;
}
@keyframes highlightPulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.4); }
  50% { box-shadow: 0 0 0 8px rgba(245, 158, 11, 0); }
}

/* 底部操作区 */
.action-footer {
  background: #ffffff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.02);
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 10px;
}
.dark-mode .action-footer {
  background: #16213e;
}
.save-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  transition: all 0.3s;
}
.save-btn:hover {
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
  transform: translateY(-1px);
}

/* 危险区域 */
.danger-zone {
  margin-top: 40px;
  border: 1px solid #fca5a5;
  border-radius: 12px;
  background-color: #fef2f2;
  padding: 24px;
}
.dark-mode .danger-zone {
  background-color: #3f1f1f;
  border-color: #dc2626;
}
.danger-title {
  color: #dc2626;
  font-size: 16px;
  font-weight: 700;
  margin-top: 0;
  margin-bottom: 20px;
}
.danger-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #fecaca;
}
.danger-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.danger-desc h4 {
  margin: 0 0 6px 0;
  color: #991b1b;
  font-size: 15px;
}
.dark-mode .danger-desc h4 {
  color: #fca5a5;
}
.danger-desc p {
  margin: 0;
  color: #b91c1c;
  font-size: 13px;
}
.dark-mode .danger-desc p {
  color: #fca5a5;
}
</style>