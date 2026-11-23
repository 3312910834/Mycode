import { useUserStore } from '@/store/user'
import { createRouter, createWebHistory } from 'vue-router'

// 公共页面
const Home = () => import('@/views/home/Home.vue')
const Login = () => import('@/views/auth/Login.vue')
const Register = () => import('@/views/auth/Register.vue')
const ForgotPassword = () => import('@/views/auth/ForgotPassword.vue')
const LostList = () => import('@/views/public/LostList.vue')
const FoundList = () => import('@/views/public/FoundList.vue')
const AnnouncementList = () => import('@/views/public/AnnouncementList.vue')
const Contact = () => import('@/views/public/Contact.vue')
const SearchResults = () => import('@/views/public/SearchResults.vue')
const SuccessStories = () => import('@/views/public/SuccessStories.vue')
const SuccessCases = () => import('@/views/public/SuccessCases.vue')
const AiDescribeSearch = () => import('@/views/public/AiDescribeSearch.vue')
const LostDetail = () => import('@/views/public/LostDetail.vue')
const FoundDetail = () => import('@/views/public/FoundDetail.vue')
const PrivacyPolicy = () => import('@/views/public/PrivacyPolicy.vue')
const TermsOfService = () => import('@/views/public/TermsOfService.vue')
// 用户页面
const UserCenter = () => import('@/views/user/UserCenter.vue')
const UserProfile = () => import('@/views/user/UserProfile.vue')
const UserPassword = () => import('@/views/user/UserPassword.vue')
const UserLostList = () => import('@/views/user/lost/UserLostList.vue')
const UserLostAdd = () => import('@/views/user/lost/UserLostAdd.vue')
const UserLostEdit = () => import('@/views/user/lost/UserLostEdit.vue')
const UserFoundList = () => import('@/views/user/found/UserFoundList.vue')
const UserFoundAdd = () => import('@/views/user/found/UserFoundAdd.vue')
const UserFoundEdit = () => import('@/views/user/found/UserFoundEdit.vue')
const MessageList = () => import('@/views/user/message/MessageList.vue')
const MessageDetail = () => import('@/views/user/message/MessageDetail.vue')
const SuggestionList = () => import('@/views/user/suggestion/SuggestionList.vue')
const SuggestionAdd = () => import('@/views/user/suggestion/SuggestionAdd.vue')

// 管理员页面
const Admin = () => import('@/views/admin/Admin.vue')
const AdminDashboard = () => import('@/views/admin/AdminDashboard.vue')
const AdminItems = () => import('@/views/admin/AdminItems.vue')
const AdminUsers = () => import('@/views/admin/AdminUsers.vue')
const AdminCategories = () => import('@/views/admin/AdminCategories.vue')
const AdminSettings = () => import('@/views/admin/AdminSettings.vue')

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '校园失物招领系统' }
  },
  {
    path: '/terms-of-service',
    name: 'TermsOfService',
    component: TermsOfService,
    meta: { title: '服务条款' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册', guestOnly: true }
  },
  {
    path: '/privacy-policy',
    name: 'PrivacyPolicy',
    component: PrivacyPolicy,
    meta: { title: '隐私政策' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword,
    meta: { title: '忘记密码', guestOnly: true }
  },
  {
    path: '/lost',
    name: 'LostList',
    component: LostList,
    meta: { title: '失物公示' }
  },
  {
    path: '/found',
    name: 'FoundList',
    component: FoundList,
    meta: { title: '招领公示' }
  },
  {
    path: '/announcements',
    name: 'Announcement',
    component: AnnouncementList,
    meta: { title: '系统公告' }
  },
  {
    path: '/contact',
    name: 'Contact',
    component: Contact,
    meta: { title: '联系我们' }
  },
  {
    path: '/search',
    name: 'SearchResults',
    component: SearchResults,
    meta: { title: '搜索结果' }
  },
  {
    path: '/success-stories',
    name: 'SuccessStories',
    component: SuccessStories,
    meta: { title: '成功案例' }
  },
  {
    path: '/success-cases',
    name: 'SuccessCases',
    component: SuccessCases,
    meta: { title: '成功归还案例' }
  },
  {
    path: '/ai-search',
    name: 'AiDescribeSearch',
    component: AiDescribeSearch,
    meta: { title: 'AI描述找物' }
  },
  {
    path: '/lost/:id',
    name: 'LostDetail',
    component: LostDetail,
    meta: { title: '失物详情' }
  },
  {
    path: '/found/:id',
    name: 'FoundDetail',
    component: FoundDetail,
    meta: { title: '招领详情' }
  },

  // 用户中心路由
  {
    path: '/user',
    name: 'UserCenter',
    component: UserCenter,
    meta: { title: '用户中心', requiresAuth: true },
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: UserProfile,
        meta: { title: '个人信息' }
      },
      {
        path: 'password',
        name: 'UserPassword',
        component: UserPassword,
        meta: { title: '修改密码' }
      },
      {
        path: 'UserLostList',
        name: 'UserLostList',
        component: UserLostList,
        meta: { title: '我的失物' }
      },
      {
        path: 'lost/UserLostAdd',
        name: 'UserLostAdd',
        component: UserLostAdd,
        meta: { title: '发布失物' }
      },
      {
        path: 'lost/edit/:id',
        name: 'UserLostEdit',
        component: UserLostEdit,
        meta: { title: '编辑失物' }
      },
      {
        path: 'found',
        name: 'UserFoundList',
        component: UserFoundList,
        meta: { title: '我的招领' }
      },
      {
        path: 'found/add',
        name: 'UserFoundAdd',
        component: UserFoundAdd,
        meta: { title: '发布招领' }
      },
      {
        path: 'found/edit/:id',
        name: 'UserFoundEdit',
        component: UserFoundEdit,
        meta: { title: '编辑招领' }
      },
      {
        path: 'messages',
        name: 'MessageList',
        component: MessageList,
        meta: { title: '我的消息' }
      },
      {
        path: 'messages/:id',
        name: 'MessageDetail',
        component: MessageDetail,
        meta: { title: '消息详情' }
      },
      {
        path: 'suggestions',
        name: 'SuggestionList',
        component: SuggestionList,
        meta: { title: '我的建议' }
      },
      {
        path: 'suggestions/add',
        name: 'SuggestionAdd',
        component: SuggestionAdd,
        meta: { title: '提交建议' }
      }
    ]
  },

  // 管理员路由
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    redirect: { name: 'AdminDashboard' },
    meta: { title: '管理员中心', requiresAuth: true, isAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { title: '仪表盘', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'items',
        name: 'AdminItems',
        component: AdminItems,
        meta: { title: '物品管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers,
        meta: { title: '用户管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: AdminCategories,
        meta: { title: '分类管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: AdminSettings,
        meta: { title: '系统设置', requiresAuth: true, isAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.isLogin
  const isAdmin = userStore.isAdmin
  const isAdminRoute = to.path.startsWith('/admin')

  if (to.meta.title) {
    document.title = `${to.meta.title} - 校园失物招领系统`
  }

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
    return
  }

  if (to.meta.guestOnly && isAuthenticated) {
    next('/')
    return
  }

  if (isAuthenticated && isAdmin) {
    userStore.setPortalMode(isAdminRoute ? 'admin' : 'user')
  }

  if (to.meta.isAdmin && (!isAdmin || !userStore.hasAdminAccess)) {
    next('/')
    return
  }

  next()
})

export default router