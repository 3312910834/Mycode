import { useUserStore } from '@/store/user'
import { createRouter, createWebHistory } from 'vue-router'

// 公共页面
const Home = () => import('@/views/home/Home.vue')
const Login = () => import('@/views/auth/Login.vue')
const Register = () => import('@/views/auth/Register.vue')
const LostList = () => import('@/views/public/LostList.vue')
const FoundList = () => import('@/views/public/FoundList.vue')
const AnnouncementList = () => import('@/views/public/AnnouncementList.vue')
const Contact = () => import('@/views/public/Contact.vue')
const SearchResults = () => import('@/views/public/SearchResults.vue')
const LostDetail = () => import('@/views/public/LostDetail.vue')
const FoundDetail = () => import('@/views/public/FoundDetail.vue')
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

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '校园失物招领系统' }
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
      },
      {
        path: '/search',
        name: 'SearchResults',
        component: SearchResults,
        meta: { title: '搜索结果' }
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
  const isAuthenticated = userStore.token
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 校园失物招领系统`
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
    return
  }
  
  // 检查是否只允许游客访问
  if (to.meta.guestOnly && isAuthenticated) {
    next('/')
    return
  }
  
  next()
})

export default router