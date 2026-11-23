<template>
  <div class="users-management">
    <!-- 顶部控制面板卡片 -->
    <div class="control-panel">
      <div class="panel-header">
        <div class="title-section">
          <el-icon class="title-icon"><UserFilled /></el-icon>
          <h2>用户管理 <span class="subtitle">AI 社区账户中心</span></h2>
        </div>
        
        <div class="filters-section">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名 / 邮箱 / 电话"
            clearable
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-button type="primary" class="refresh-btn" @click="refreshData">
            <el-icon><Refresh /></el-icon> 刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格卡片 -->
    <div class="table-container">
      <el-table
        :data="localFilteredUsers"
        v-loading="loading"
        style="width: 100%"
        height="calc(100vh - 240px)"
        stripe
        header-cell-class-name="custom-header"
      >
        <el-table-column prop="id" label="系统 ID" width="100" align="center" />
        
        <!-- 用户身份列（带头像首字母） -->
        <el-table-column label="账户信息" min-width="200">
          <template #default="scope">
            <div class="user-info-cell">
              <div class="user-avatar" :class="scope.row.role === 'admin' ? 'admin-avatar' : ''">
                {{ String(scope.row.username).charAt(0).toUpperCase() }}
              </div>
              <div class="user-name-group">
                <span class="user-name">{{ scope.row.username }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="role" label="系统角色" width="140" align="center">
          <template #default="scope">
            <el-tag 
              :type="scope.row.role === 'admin' ? 'danger' : 'primary'" 
              :effect="scope.row.role === 'admin' ? 'dark' : 'light'"
              round
            >
              <el-icon v-if="scope.row.role === 'admin'"><Key /></el-icon>
              {{ scope.row.role === 'admin' ? '核心管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="绑定邮箱" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <div class="contact-cell">
              <el-icon><Message /></el-icon>
              <span>{{ scope.row.email || '未绑定邮箱' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="联系电话" width="150" align="center">
          <template #default="scope">
            <span class="phone-text">{{ scope.row.phone || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="账户状态" width="120" align="center">
          <template #default="scope">
            <div class="status-wrapper">
              <span v-if="scope.row.status === 1" class="status-dot green"></span>
              <span v-else class="status-dot red"></span>
              <span :class="scope.row.status === 1 ? 'text-green' : 'text-red'">
                {{ scope.row.status === 1 ? '正常运行' : '账号封禁' }}
              </span>
            </div>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="账户控制" width="220" fixed="right" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-button link type="primary" size="small" @click="viewUser(scope.row)">
                <el-icon><View /></el-icon> 详细档案
              </el-button>
              
              <el-divider direction="vertical" />
              
              <el-button
                v-if="scope.row.status === 1"
                link 
                type="danger" 
                size="small"
                @click="banUser(scope.row)"
                :disabled="scope.row.role === 'admin'"
              >
                <el-icon><Lock /></el-icon> 封禁
              </el-button>

              <el-button
                v-else
                link 
                type="success" 
                size="small"
                @click="unbanUser(scope.row)"
              >
                <el-icon><Unlock /></el-icon> 解封
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 查看用户详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="用户数字档案"
      width="550px"
      destroy-on-close
      class="custom-dialog"
    >
      <div v-if="selectedUser" class="user-detail">
        <el-alert
          v-if="selectedUser.status === 0"
          title="该账户目前处于封禁状态，无法登录系统。"
          type="error"
          :closable="false"
          class="ban-alert"
        />

        <el-descriptions :column="1" border class="detail-descriptions">
          <el-descriptions-item label="系统 ID">{{ selectedUser.id }}</el-descriptions-item>
          <el-descriptions-item label="登录名">{{ selectedUser.username }}</el-descriptions-item>
          <el-descriptions-item label="系统权限">
            <el-tag :type="selectedUser.role === 'admin' ? 'danger' : 'primary'" effect="dark">
              {{ selectedUser.role === 'admin' ? '超级管理员' : '社区用户' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ selectedUser.email || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="电话号码">{{ selectedUser.phone || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatUserTime(selectedUser) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入科技感图标
import { Search, View, Lock, Unlock, UserFilled, Refresh, Key, Message } from '@element-plus/icons-vue'
import { useAdminStore } from '@/store/admin'

const adminStore = useAdminStore()
const loading = computed(() => adminStore.loading)
const searchKeyword = ref('')

// 【核心修复】强大的前端本地过滤，规避后端搜索失效的坑
const localFilteredUsers = computed(() => {
  const users = adminStore.users || []
  if (!searchKeyword.value) return users

  const q = searchKeyword.value.toLowerCase().trim()
  return users.filter(user => {
    const name = String(user.username || '').toLowerCase()
    const email = String(user.email || '').toLowerCase()
    const phone = String(user.phone || '')
    return name.includes(q) || email.includes(q) || phone.includes(q)
  })
})

const refreshData = async () => {
  searchKeyword.value = ''
  await adminStore.fetchUsers({ page: 1, limit: 500 })
  ElMessage.success('数据已同步')
}

const viewDialogVisible = ref(false)
const selectedUser = ref(null)

const formatUserTime = (user) => {
  if (!user) return '未知时间'
  const t = user.createTime ?? user.create_time
  if (!t) return '未知时间'
  if (typeof t === 'string') return t
  if (typeof t === 'object' && t.year) {
    const pad = (n) => String(n).padStart(2, '0')
    return `${t.year}-${pad(t.monthValue ?? t.month)}-${pad(t.dayOfMonth ?? t.day)} ${pad(t.hour)}:${pad(t.minute)}`
  }
  return String(t)
}

const viewUser = (user) => {
  selectedUser.value = user
  viewDialogVisible.value = true
}

const banUser = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要封禁用户【${user.username}】吗？封禁后该用户将无法登录。`, '安全警告', {
      confirmButtonText: '确定封禁',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    })
    
    await adminStore.updateUserStatus(user.id, 0)
    ElMessage.success(`用户 ${user.username} 已被限制访问`)
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('封禁操作执行失败')
  }
}

const unbanUser = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要解除对用户【${user.username}】的封禁吗？`, '恢复访问', {
      confirmButtonText: '解除限制',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await adminStore.updateUserStatus(user.id, 1)
    ElMessage.success(`用户 ${user.username} 账号已恢复正常`)
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('解封操作执行失败')
  }
}

onMounted(async () => {
  await adminStore.fetchUsers({ page: 1, limit: 500 })
})
</script>

<style scoped>
.users-management {
  padding: 20px;
  background-color: #f4f7fb;
  min-height: 100%;
}

/* 控制面板样式 */
.control-panel {
  background: #ffffff;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  margin-bottom: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 28px;
  color: #409eff;
}

.title-section h2 {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.subtitle {
  font-size: 13px;
  font-weight: normal;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 20px;
  letter-spacing: 1px;
}

.filters-section {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  width: 300px;
}

.refresh-btn {
  background-color: #f8fafc;
  border-color: #e2e8f0;
  color: #475569;
}
.refresh-btn:hover {
  background-color: #f1f5f9;
  color: #409eff;
  border-color: #bfdbfe;
}

/* 表格容器样式 */
.table-container {
  background: #ffffff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

:deep(.custom-header) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 600;
}

/* 用户信息列自定义 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(135deg, #e0e7ff 0%, #dbeafe 100%);
  color: #3b82f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
}

.admin-avatar {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #ef4444;
}

.user-name {
  font-weight: 600;
  color: #334155;
  font-size: 14px;
}

/* 邮箱/电话图标排版 */
.contact-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
}

.phone-text {
  font-family: monospace;
  font-size: 14px;
  color: #475569;
}

/* 状态指示灯 */
.status-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-weight: 600;
  font-size: 13px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}
.status-dot.green { background-color: #10b981; box-shadow: 0 0 4px #10b981; }
.status-dot.red { background-color: #ef4444; box-shadow: 0 0 4px #ef4444; }
.text-green { color: #10b981; }
.text-red { color: #ef4444; }

/* 操作按钮 */
.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.action-buttons .el-button {
  margin: 0;
  padding: 4px 8px;
}

/* 详情弹窗 */
.ban-alert {
  margin-bottom: 20px;
  border-radius: 8px;
}

:deep(.detail-descriptions .el-descriptions__label) {
  background-color: #f8fafc;
  font-weight: 600;
  color: #475569;
  width: 120px;
}
</style>