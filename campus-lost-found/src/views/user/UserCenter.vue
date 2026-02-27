<template>
  <div class="user-center-container">
    <!-- 用户中心头部 -->
    <div class="user-header">
      <div class="container">
        <div class="user-info-card">
          <!-- 返回首页按钮 -->
          <div class="back-home-btn">
            <el-button type="text" @click="goToHome" class="back-btn">
              <el-icon><House /></el-icon>
              <span>返回首页</span>
            </el-button>
          </div>
          
          <div class="user-avatar">
            <el-avatar :src="userStore.avatar || '/default-avatar.png'" :size="80" />
          </div>
          
          <div class="user-details">
            <h2>{{ userStore.realName || userStore.userName }}</h2>
            <p class="username">@{{ userStore.userName }}</p>
            
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-number">{{ userStats.lostCount || 0 }}</span>
                <span class="stat-label">失物发布</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userStats.foundCount || 0 }}</span>
                <span class="stat-label">招领发布</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userStats.messageCount || 0 }}</span>
                <span class="stat-label">未读消息</span>
              </div>
            </div>
          </div>
          
          <div class="user-actions">
            <el-button type="primary" @click="$router.push('/user/profile')">
              编辑资料
            </el-button>
            <el-button type="link" @click="$router.push('/user/password')">
              修改密码
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="user-main">
      <div class="container">
        <el-row :gutter="20">
          <!-- 左侧菜单 -->
          <el-col :xs="24" :sm="6">
            <div class="user-sidebar">
              <el-menu
                :default-active="activeMenu"
                class="user-menu"
                @select="handleMenuSelect"
              >
                <el-menu-item index="profile">
                  <el-icon><User /></el-icon>
                  <span>个人信息</span>
                </el-menu-item>
                
                <el-sub-menu index="lost">
                  <template #title>
                    <el-icon><Search /></el-icon>
                    <span>失物管理</span>
                  </template>
                  <el-menu-item index="lost-list">我的失物</el-menu-item>
                  <el-menu-item index="lost-add">发布失物</el-menu-item>
                </el-sub-menu>
                
                <el-sub-menu index="found">
                  <template #title>
                    <el-icon><Collection /></el-icon>
                    <span>招领管理</span>
                  </template>
                  <el-menu-item index="found-list">我的招领</el-menu-item>
                  <el-menu-item index="found-add">发布招领</el-menu-item>
                </el-sub-menu>
                
                <el-menu-item index="messages">
                  <el-icon><Message /></el-icon>
                  <span>我的消息</span>
                  <el-badge v-if="userStats.messageCount > 0" :value="userStats.messageCount" />
                </el-menu-item>
                
                <el-menu-item index="suggestions">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>我的建议</span>
                </el-menu-item>
                
                <el-menu-item index="password">
                  <el-icon><Lock /></el-icon>
                  <span>修改密码</span>
                </el-menu-item>
              </el-menu>
            </div>
          </el-col>

          <!-- 右侧内容 -->
          <el-col :xs="24" :sm="18">
            <div class="user-content">
              <router-view />
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 只导入真正需要的图标
import { User } from '@element-plus/icons-vue'
import { Search } from '@element-plus/icons-vue'
import { Collection } from '@element-plus/icons-vue'
import { Message } from '@element-plus/icons-vue'
import { Lock } from '@element-plus/icons-vue'
import { ChatDotRound } from '@element-plus/icons-vue'
import { House } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 用户统计数据
const userStats = ref({})

// 当前激活的菜单
const activeMenu = computed(() => {
  const path = route.path
  console.log('当前路径:', path)
  
  if (path.includes('/user/profile')) return 'profile'
  if (path.includes('/user/lost/UserLostAdd')) return 'lost-add'
  if (path.includes('/user/UserLostList')) return 'lost-list'
  if (path.includes('/user/lost/edit/')) return 'lost-list'
  if (path.includes('/user/found/add')) return 'found-add'
  if (path.includes('/user/found')) return 'found-list'
  if (path.includes('/user/found/edit/')) return 'found-list'
  if (path.includes('/user/messages')) return 'messages'
  if (path.includes('/user/suggestions')) return 'suggestions'
  if (path.includes('/user/suggestions/add')) return 'suggestions'
  if (path.includes('/user/password')) return 'password'
  return 'profile'
})

onMounted(async () => {
  await loadUserStats()
})

// 加载用户统计数据
const loadUserStats = async () => {
  try {
    userStats.value = {
      lostCount: 5,
      foundCount: 3,
      messageCount: 2
    }
  } catch (error) {
    console.error('加载用户统计数据失败:', error)
    userStats.value = {
      lostCount: 0,
      foundCount: 0,
      messageCount: 0
    }
  }
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  console.log('菜单选择:', index)
  
  switch (index) {
    case 'profile':
      router.push('/user/profile')
      break
      
    case 'lost-list':
      router.push('/user/UserLostList')
      break
      
    case 'lost-add':
      router.push('/user/lost/UserLostAdd')
      break
      
    case 'found-list':
      router.push('/user/found')
      break
      
    case 'found-add':
      router.push('/user/found/add')
      break
      
    case 'messages':
      router.push('/user/messages')
      break
      
    case 'suggestions':
      router.push('/user/suggestions')
      break
      
    case 'password':
      router.push('/user/password')
      break
      
    default:
      console.warn('未知的菜单项:', index)
  }
}

// 编辑头像
const handleEditAvatar = () => {
  ElMessage.info('头像上传功能开发中')
}

// 返回首页
const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.user-center-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 用户头部 */
.user-header {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  padding: 30px 0;
}

.user-info-card {
  background: white;
  border-radius: 10px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: relative;
}

.back-home-btn {
  position: absolute;
  top: 8px;
  left: 20px;
}

.back-btn {
  color: #3498db;
  font-weight: 500;
}

.back-btn .el-icon {
  margin-right: 5px;
}

.user-avatar {
  text-align: center;
}

.edit-avatar-btn {
  display: block;
  margin-top: 10px;
  color: #3498db;
}

.user-details {
  flex: 1;
}

.user-details h2 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 24px;
}

.username {
  margin: 0 0 20px 0;
  color: #7f8c8d;
}

.user-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #3498db;
}

.stat-label {
  display: block;
  font-size: 14px;
  color: #95a5a6;
  margin-top: 5px;
}

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 主要内容区域 */
.user-main {
  padding: 30px 0 50px;
}

/* 侧边栏 */
.user-sidebar {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.user-menu {
  border-right: none;
}

.user-menu .el-menu-item,
.user-menu .el-sub-menu__title {
  height: 50px;
  line-height: 50px;
}

.user-menu .el-menu-item.is-active {
  color: #3498db;
  background-color: #ecf5ff;
}

.user-menu .el-badge {
  margin-left: auto;
}

/* 内容区域 */
.user-content {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-height: 500px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-info-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
    padding-top: 60px; /* 为返回按钮留出空间 */
  }
  
  .back-home-btn {
    top: 15px;
    left: 15px;
    right: auto;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .user-actions {
    width: 100%;
  }
  
  .user-actions .el-button {
    width: 100%;
  }
  
  .user-content {
    padding: 20px;
  }
}
</style>