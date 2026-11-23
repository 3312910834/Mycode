import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import { createApp } from 'vue'
import App from './App.vue'
import './assets/styles/element.css'
import './assets/styles/global.css'
import router from './router'
import { useUserStore } from '@/store/user'

const app = createApp(App)

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
app.use(pinia)

const userStore = useUserStore()
if (userStore.token && !userStore.userInfo?.id) {
  userStore.resetToken()
}

app.use(router)
app.use(ElementPlus)
app.mount('#app')