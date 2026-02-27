import { getUserInfo, login, register, testApi } from '@/api/user'
import { getToken, removeToken, setToken, setUserInfo, removeUserInfo, getUserInfo as getStoredUserInfo } from '@/utils/auth'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getStoredUserInfo() || null,
    permissions: []
  }),
  
  getters: {
    isLogin: (state) => !!state.token,
    userName: (state) => state.userInfo?.username || '',
    realName: (state) => state.userInfo?.real_name || state.userInfo?.username || '',
    avatar: (state) => state.userInfo?.avatar || ''
  },
  
  actions: {
    // 测试连接
    async testConnection() {
      try {
        const result = await testApi()
        console.log('后端连接测试:', result)
        return Promise.resolve(result)
      } catch (error) {
        console.error('后端连接失败:', error)
        return Promise.reject(error)
      }
    },
    
    // 登录
    async login(loginForm) {
      try {
        // 调用登录API
        const response = await login({
          username: loginForm.username,
          password: loginForm.password
        })
        
        console.log('登录响应:', response)
        
        // 根据后端返回的格式处理
        // 后端返回格式可能是: {"code":200,"message":"登录成功","data":{...}}
        // 或直接是用户对象: {"id":1,"username":"admin","role":"user"}
        
        if (response.code === 200 && response.data) {
          // 标准格式
          const userData = response.data
          this.userInfo = userData
          
          // 生成token（后端没返回token，我们模拟一个）
          this.token = `user_${userData.id}_${Date.now()}`
          setToken(this.token)
          
          // 保存用户信息到localStorage
          setUserInfo(userData)
          
        } else if (response.id) {
          // 直接用户对象格式
          this.userInfo = response
          
          // 生成token
          this.token = `user_${response.id}_${Date.now()}`
          setToken(this.token)
          
          // 保存用户信息
          setUserInfo(response)
        } else {
          throw new Error('登录响应格式异常')
        }
        
        return Promise.resolve()
      } catch (error) {
        console.error('登录失败:', error)
        return Promise.reject(error)
      }
    },
    
    // 注册
    async register(registerForm) {
      try {
        // 准备注册数据，适配后端需要的字段
        const registerData = {
          username: registerForm.username,
          password: registerForm.password,
          phone: registerForm.phone || '',
          real_name: registerForm.real_name || '',
          student_id: registerForm.student_id || '',
          email: registerForm.email || (registerForm.username + '@example.com')
        }
        
        const response = await register(registerData)
        console.log('注册响应:', response)
        
        return Promise.resolve(response)
      } catch (error) {
        console.error('注册失败:', error)
        return Promise.reject(error)
      }
    },
    
    // 获取用户信息
    async getUserInfo() {
      try {
        // 需要先有用户ID
        if (!this.userInfo?.id) {
          // 先尝试从localStorage获取
          const storedUser = getStoredUserInfo()
          if (storedUser?.id) {
            this.userInfo = storedUser
            console.log('从localStorage恢复用户ID:', storedUser.id)
          } else {
            throw new Error('用户ID不存在，请先登录')
          }
        }
        
        const response = await getUserInfo(this.userInfo.id)
        console.log('用户信息响应:', response)
        
        // 更新用户信息
        if (response.id) {
          this.userInfo = response
          setUserInfo(response)
        }
        
        return Promise.resolve(response)
      } catch (error) {
        console.error('获取用户信息失败:', error)
        return Promise.reject(error)
      }
    },
    
    // 退出登录
    async logout() {
      try {
        // 后端暂无logout接口，前端直接清理状态
        this.resetToken()
        return Promise.resolve()
      } catch (error) {
        console.error('退出登录错误:', error)
        this.resetToken()
        return Promise.reject(error)
      }
    },
    
    // 重置token
    resetToken() {
      this.token = ''
      this.userInfo = null
      removeToken()
      removeUserInfo()
    },
    
    // 更新用户信息（本地）
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      setUserInfo(this.userInfo)
    }
  }
})