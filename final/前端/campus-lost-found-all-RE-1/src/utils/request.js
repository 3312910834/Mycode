import { useUserStore } from '@/store/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  // 默认 `/campus-lost-found` + Vite 代理，Cookie 会话与 AdminAuthFilter 联调可用；需要直连 Tomcat 时设置 VITE_API_BASE_URL
  baseURL: import.meta.env.VITE_API_BASE_URL || '/campus-lost-found',
  timeout: 10000,
  withCredentials: true
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    const token = userStore.token
    
    // 添加token到请求头（如果后端需要）
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器 - 适配后端Servlet返回格式
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 后端Servlet返回的格式检查
    if (res && typeof res === 'object') {
      // 如果有error字段，说明是错误
      if (res.error) {
        ElMessage.error(res.error || '请求失败')
        return Promise.reject(new Error(JSON.stringify(res)))
      }
      
      // 如果有success字段，直接返回
      if (res.success !== undefined) {
        return res
      }
      
      // 如果有code字段，按标准格式处理
      if (res.code && res.code !== 200) {
        const errText = res.message || res.msg || '请求失败'
        const reqUrl = response.config?.url || ''
        const isLoginCall = reqUrl.includes('/userapi/login')
        if (!isLoginCall) {
          ElMessage.error(errText)
        }

        // 401: 未登录或 token 过期（登录接口返回的业务 401 不清理会话）
        if (res.code === 401 && !isLoginCall) {
          const userStore = useUserStore()
          userStore.resetToken()
          window.location.href = '/login'
        }

        return Promise.reject(new Error(errText))
      }
    }
    
    // 成功返回数据
    return res
  },
  error => {
    console.error('Response error:', error)

    if (error.response) {
      const data = error.response.data
      const serverText =
        typeof data === 'string'
          ? data
          : (data && (data.msg || data.message)) || ''
      switch (error.response.status) {
        case 400:
          ElMessage.error(serverText || '请求参数错误')
          break
        case 401:
          ElMessage.error(serverText || '登录已过期，请重新登录')
          {
            const userStore = useUserStore()
            userStore.resetToken()
            window.location.href = '/login'
          }
          break
        case 403:
          ElMessage.error(serverText || '没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error('请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default service