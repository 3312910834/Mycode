<template>
  <div class="home-container">
    <!-- 导航栏 -->
    <header class="home-header">
      <div class="container">
        <div class="header-content">
          <div class="logo">
            <h1>校园失物招领系统</h1>
            <p>Campus Lost & Found System</p>
          </div>
          <nav class="nav-menu">
            <router-link to="/" class="nav-link">首页</router-link>
            <router-link to="/lost" class="nav-link">失物公示</router-link>
            <router-link to="/found" class="nav-link">招领公示</router-link>
            <router-link to="/announcements" class="nav-link">系统公告</router-link>
            <router-link to="/contact" class="nav-link">联系我们</router-link>
          </nav>
          <div class="user-actions">
            <template v-if="isLogin">
              <el-dropdown>
                <span class="user-info">
                  <el-avatar :src="userStore.avatar || '/default-avatar.png'" size="small" />
                  <span class="username">{{ userStore.userName }}</span>
                  <el-icon><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="$router.push('/user/profile')">
                      <el-icon><User /></el-icon>个人中心
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/user/lost')">
                      <el-icon><List /></el-icon>我的失物
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/user/found')">
                      <el-icon><Collection /></el-icon>我的招领
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/user/messages')">
                      <el-icon><Message /></el-icon>我的消息
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="handleLogout">
                      <el-icon><SwitchButton /></el-icon>退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button type="primary" @click="$router.push('/login')">登录</el-button>
              <el-button @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- 搜索区域 -->
    <section class="search-section">
      <div class="container">
        <div class="search-box">
          <h2>快速查找失物/招领</h2>
          <div class="search-form">
            <el-input
              v-model="searchKeyword"
              placeholder="请输入物品名称、描述或地点..."
              class="search-input"
              size="large"
            >
              <template #append>
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>搜索
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </section>

    <!-- 统计信息 -->
    <section class="stats-section">
      <div class="container">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <el-icon class="stat-icon lost"><Lost /></el-icon>
              <div class="stat-content">
                <div class="stat-number">{{ stats.lostCount || 0 }}</div>
                <div class="stat-label">丢失物品</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <el-icon class="stat-icon found"><Collection /></el-icon>
              <div class="stat-content">
                <div class="stat-number">{{ stats.foundCount || 0 }}</div>
                <div class="stat-label">拾获物品</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <el-icon class="stat-icon success"><SuccessFilled /></el-icon>
              <div class="stat-content">
                <div class="stat-number">{{ stats.successCount || 0 }}</div>
                <div class="stat-label">成功找回</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card">
              <el-icon class="stat-icon user"><UserFilled /></el-icon>
              <div class="stat-content">
                <div class="stat-number">{{ stats.userCount || 0 }}</div>
                <div class="stat-label">注册用户</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 最新失物招领 -->
    <section class="latest-section">
      <div class="container">
        <div class="section-header">
          <h2>最新失物招领信息</h2>
          <div class="section-actions">
            <el-button type="primary" @click="$router.push('/lost')">
              查看更多失物 <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button type="success" @click="$router.push('/found')">
              查看更多招领 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
        
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <div class="list-container">
              <h3>最新失物</h3>
              <el-table :data="latestLost" style="width: 100%">
                <el-table-column prop="title" label="物品名称" width="180" />
                <el-table-column prop="lost_location" label="丢失地点" />
                <el-table-column prop="lost_time" label="丢失时间" width="120">
                  <template #default="{ row }">
                    {{ formatDate(row.lost_time) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template #default="{ row }">
                    <el-button type="text" @click="viewLostDetail(row.id)">查看</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
          
          <el-col :xs="24" :sm="12">
            <div class="list-container">
              <h3>最新招领</h3>
              <el-table :data="latestFound" style="width: 100%">
                <el-table-column prop="title" label="物品名称" width="180" />
                <el-table-column prop="found_location" label="拾获地点" />
                <el-table-column prop="found_time" label="拾获时间" width="120">
                  <template #default="{ row }">
                    {{ formatDate(row.found_time) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template #default="{ row }">
                    <el-button type="text" @click="viewFoundDetail(row.id)">查看</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 快速操作 -->
    <section class="quick-actions-section">
      <div class="container">
        <h2>快速操作</h2>
        <el-row :gutter="20" justify="center">
          <el-col :xs="12" :sm="6">
            <div class="action-card" @click="handleQuickAction('reportLost')">
              <el-icon class="action-icon lost"><Lost /></el-icon>
              <h3>发布失物</h3>
              <p>丢失了物品？立即发布</p>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="action-card" @click="handleQuickAction('reportFound')">
              <el-icon class="action-icon found"><Collection /></el-icon>
              <h3>发布招领</h3>
              <p>捡到物品？立即发布</p>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="action-card" @click="handleQuickAction('myItems')">
              <el-icon class="action-icon my"><List /></el-icon>
              <h3>我的发布</h3>
              <p>查看和管理我的发布</p>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="action-card" @click="handleQuickAction('suggestion')">
              <el-icon class="action-icon suggestion"><ChatDotRound /></el-icon>
              <h3>意见建议</h3>
              <p>为系统提出宝贵建议</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="home-footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>校园失物招领系统</h3>
            <p>致力于帮助师生找回失物，共建和谐校园</p>
          </div>
          <div class="footer-section">
            <h4>快速链接</h4>
            <ul>
              <li><router-link to="/">首页</router-link></li>
              <li><router-link to="/lost">失物公示</router-link></li>
              <li><router-link to="/found">招领公示</router-link></li>
              <li><router-link to="/announcement">系统公告</router-link></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>联系我们</h4>
            <p>地址：校园学生事务中心</p>
            <p>电话：XXX-XXXXXXX</p>
            <p>邮箱：lostfound@campus.edu</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2023 校园失物招领系统. 版权所有.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
// import {  getLatestLost } from '@/api/lost'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const isLogin = ref(false)
const searchKeyword = ref('')
const stats = ref({})
const latestLost = ref([])
const latestFound = ref([])

onMounted(async () => {
  isLogin.value = userStore.isLogin
  await loadHomeData()
})

const loadHomeData = async () => {
  try {
    const [statsRes, lostRes, foundRes] = await Promise.all([
      getHomeStats(),
      getLatestLost(),
      getLatestFound()
    ])
    stats.value = statsRes.data
    latestLost.value = lostRes.data
    latestFound.value = foundRes.data
  } catch (error) {
    console.error('加载首页数据失败:', error)
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString()
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  router.push({
    path: '/lost',
    query: { keyword: searchKeyword.value }
  })
}

const handleLogout = async () => {
  try {
    await userStore.logout()
    ElMessage.success('退出成功')
    router.push('/')
  } catch (error) {
    ElMessage.error('退出失败')
  }
}

const viewLostDetail = (id) => {
  router.push(`/lost/${id}`)
}

const viewFoundDetail = (id) => {
  router.push(`/found/${id}`)
}

const handleQuickAction = (action) => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  switch (action) {
    case 'reportLost':
      router.push('/lost')
      break
    case 'reportFound':
      router.push('/user/found/UserFoundAdd')
      break
    case 'myItems':
      router.push('/user')
      break
    case 'suggestion':
      router.push('/user/suggestions/UserFoundAdd')
      break
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 头部样式 */
.home-header {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 0;
}

.logo h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
}

.logo p {
  margin: 0;
  color: #7f8c8d;
  font-size: 12px;
}

.nav-menu {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: #34495e;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #3498db;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.username {
  font-weight: 500;
}

/* 搜索区域 */
.search-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 0;
  color: white;
  text-align: center;
}

.search-section h2 {
  margin-bottom: 30px;
  font-size: 32px;
}

.search-box {
  max-width: 800px;
  margin: 0 auto;
}

.search-form {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
}

/* 统计卡片 */
.stats-section {
  padding: 40px 0;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 40px;
  padding: 15px;
  border-radius: 10px;
}

.stat-icon.lost {
  background: #fde9e9;
  color: #f56c6c;
}

.stat-icon.found {
  background: #e9f7ef;
  color: #67c23a;
}

.stat-icon.success {
  background: #f0f9ff;
  color: #409eff;
}

.stat-icon.user {
  background: #fdf6ec;
  color: #e6a23c;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
}

.stat-label {
  color: #7f8c8d;
  margin-top: 5px;
}

/* 最新信息 */
.latest-section {
  padding: 40px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-header h2 {
  color: #2c3e50;
  margin: 0;
}

.section-actions {
  display: flex;
  gap: 10px;
}

.list-container {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.list-container h3 {
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f2f5;
}

/* 快速操作 */
.quick-actions-section {
  padding: 60px 0;
  background: white;
  text-align: center;
}

.quick-actions-section h2 {
  color: #2c3e50;
  margin-bottom: 40px;
}

.action-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 30px 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.action-card:hover {
  background: white;
  border-color: #3498db;
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.action-icon {
  font-size: 50px;
  margin-bottom: 15px;
}

.action-icon.lost {
  color: #f56c6c;
}

.action-icon.found {
  color: #67c23a;
}

.action-icon.my {
  color: #409eff;
}

.action-icon.suggestion {
  color: #e6a23c;
}

.action-card h3 {
  color: #2c3e50;
  margin: 15px 0 10px;
}

.action-card p {
  color: #7f8c8d;
  margin: 0;
}

/* 页脚 */
.home-footer {
  background: #2c3e50;
  color: white;
  padding: 50px 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-section h3,
.footer-section h4 {
  color: white;
  margin-bottom: 20px;
}

.footer-section p {
  color: #bdc3c7;
  line-height: 1.6;
  margin: 10px 0;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section ul li a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-section ul li a:hover {
  color: white;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #34495e;
  color: #95a5a6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .nav-menu {
    flex-wrap: wrap;
    justify-content: center;
    gap: 15px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .search-section h2 {
    font-size: 24px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .action-card {
    margin-bottom: 20px;
  }
}
</style>