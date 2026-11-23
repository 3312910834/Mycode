<template>
  <div class="admin-container">
    <!-- 侧边栏 (高级深色主题) -->
    <el-aside width="260px" class="admin-sidebar">
      <div class="sidebar-header">
        <el-icon size="32" class="header-icon">
          <CircleCheckFilled />
        </el-icon>
        <div class="header-text">
          <h3>校招领管理后台</h3>
          <p>Campus L&F Admin</p>
        </div>
      </div>

      <el-menu
        :default-active="activeIndex"
        class="admin-menu"
        @select="handleSelect"
        background-color="transparent"
        text-color="#94a3b8"
        active-text-color="#ffffff"
      >
        <el-menu-item index="dashboard" class="menu-item">
          <el-icon><DataLine /></el-icon>
          <span>数据大屏</span>
        </el-menu-item>

        <el-menu-item index="items" class="menu-item">
          <el-icon><Box /></el-icon>
          <span>信息管理</span>
        </el-menu-item>

        <el-menu-item index="users" class="menu-item">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>

        <el-menu-item index="categories" class="menu-item">
          <el-icon><Folder /></el-icon>
          <span>分类配置</span>
        </el-menu-item>

        <el-menu-item index="settings" class="menu-item">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="user-info">
          <el-avatar :size="40" class="user-avatar">管</el-avatar>
          <div class="user-details">
            <p class="user-name">管理员</p>
            <p class="user-email">admin@test.com</p>
          </div>
        </div>
        <el-tooltip content="退出登录" placement="top" effect="dark">
          <button class="logout-btn" @click="handleLogout">
            <el-icon size="18"><SwitchButton /></el-icon>
          </button>
        </el-tooltip>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="admin-main">
      <!-- 悬浮头部 -->
      <el-header class="admin-header">
        <div class="header-left">
          <!-- 联动搜索框：输入后按回车触发全局搜索并跳转 -->
          <el-input
            v-model="searchTerm"
            placeholder="全站搜索... (输入后按回车)"
            class="global-search-input"
            clearable
            @keyup.enter="executeGlobalSearch"
            @clear="executeGlobalSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        
        <div class="header-right">
          <el-badge is-dot class="notify-badge">
            <button class="icon-btn">
              <el-icon size="20"><Bell /></el-icon>
            </button>
          </el-badge>
        </div>
      </el-header>

      <!-- 核心内容区域 (带有丝滑的页面切换动画) -->
      <el-main class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useAdminStore } from '@/store/admin'
import { useUserStore } from '@/store/user'
import {
  Bell, Box, CircleCheckFilled, DataLine, Folder, 
  Setting, SwitchButton, User, Search
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()
const userStore = useUserStore()

const activeIndex = ref('dashboard')
const searchTerm = ref('')

// 菜单点击跳转
const handleSelect = (index) => {
  activeIndex.value = index
  router.push(`/admin/${index}`)
}

// 核心逻辑：执行全局搜索并引流到大屏
const executeGlobalSearch = () => {
  const keyword = searchTerm.value.trim()
  
  // 1. 将搜索词存入全局 Store
  adminStore.setSearchTerm(keyword)
  
  // 2. 如果输入了内容，并且当前不在大屏页面，强制跳转过去看结果
  if (keyword && !route.path.includes('/dashboard')) {
    activeIndex.value = 'dashboard'
    router.push('/admin/dashboard')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.logout()
  } catch (_) {
    userStore.resetToken()
  }
  ElMessage.success('已安全退出')
  router.push('/login')
}

// 监听路由变化，保持左侧菜单高亮正确
watch(
  () => route.path,
  (newPath) => {
    if (newPath.includes('/dashboard')) activeIndex.value = 'dashboard'
    else if (newPath.includes('/items')) activeIndex.value = 'items'
    else if (newPath.includes('/users')) activeIndex.value = 'users'
    else if (newPath.includes('/categories')) activeIndex.value = 'categories'
    else if (newPath.includes('/settings')) activeIndex.value = 'settings'
  },
  { immediate: true }
)

// 监听 Store 里的词，如果在大屏里清空了，顶部的框也要跟着清空
watch(
  () => adminStore.searchTerm,
  (newVal) => {
    if (searchTerm.value !== newVal) {
      searchTerm.value = newVal || ''
    }
  }
)
</script>

<style scoped>
/* =========== 整体布局 =========== */
.admin-container {
  display: flex;
  height: 100vh;
  background-color: #f1f5f9; /* 更柔和的背景灰 */
  overflow: hidden;
}

/* =========== 侧边栏 (高级暗色) =========== */
.admin-sidebar {
  background-color: #0f172a; /* 更深邃的蓝黑 */
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.sidebar-header {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  color: #3b82f6;
  filter: drop-shadow(0 0 8px rgba(59, 130, 246, 0.4)); /* 发光效果 */
}

.header-text h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #f8fafc;
  letter-spacing: 0.5px;
}

.header-text p {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* =========== 导航菜单 =========== */
.admin-menu {
  flex: 1;
  border-right: none;
  padding: 10px;
}

.menu-item {
  height: 50px;
  line-height: 50px;
  margin-bottom: 8px;
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 悬浮效果 */
.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.05) !important;
  color: #f8fafc !important;
}

/* 激活状态：蓝色背景 + 左侧指示条 */
.admin-menu :deep(.el-menu-item.is-active) {
  background-color: #1e293b !important; /* 激活时底色变亮一点点 */
  color: #ffffff !important;
  font-weight: 600;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}
.admin-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 10%;
  height: 80%;
  width: 4px;
  background-color: #3b82f6;
  border-radius: 0 4px 4px 0;
}

.menu-item .el-icon {
  font-size: 18px;
  margin-right: 12px;
}

/* =========== 侧边栏底部 (用户信息) =========== */
.sidebar-footer {
  padding: 20px;
  background-color: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.user-name {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #e2e8f0;
}

.user-email {
  margin: 2px 0 0 0;
  font-size: 12px;
  color: #64748b;
}

/* 退出按钮 */
.logout-btn {
  background: transparent;
  border: none;
  color: #64748b;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}
.logout-btn:hover {
  background-color: rgba(239, 68, 68, 0.1);
  color: #ef4444; /* 悬停变红，警示效果 */
  transform: scale(1.05);
}

/* =========== 右侧主内容 =========== */
.admin-main {
  display: flex;
  flex-direction: column;
  flex: 1;
}

/* =========== 头部 (玻璃拟态) =========== */
.admin-header {
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px); /* 毛玻璃效果 */
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 1px 2px rgba(0,0,0,0.02);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 64px;
  z-index: 5;
}

/* 全局搜索框深度定制 */
.global-search-input {
  width: 320px;
  transition: width 0.3s ease;
}
.global-search-input:focus-within {
  width: 400px; /* 聚焦时长一点 */
}
:deep(.global-search-input .el-input__wrapper) {
  border-radius: 20px; /* 全圆角 */
  background-color: #f1f5f9;
  border: 1px solid transparent;
  box-shadow: none;
  padding: 0 16px;
  transition: all 0.3s ease;
}
:deep(.global-search-input .el-input__wrapper.is-focus) {
  background-color: #ffffff;
  border-color: #bfdbfe;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}
:deep(.global-search-input .el-input__inner) {
  height: 36px;
}

/* 右侧图标按钮 */
.icon-btn {
  background: transparent;
  border: none;
  color: #64748b;
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}
.icon-btn:hover {
  background-color: #f1f5f9;
  color: #3b82f6;
}

/* =========== 内容区域 =========== */
.admin-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 24px 32px;
}

/* =========== 路由切换平滑动画 =========== */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-15px); /* 从左边滑入 */
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(15px); /* 向右边滑出 */
}
</style>