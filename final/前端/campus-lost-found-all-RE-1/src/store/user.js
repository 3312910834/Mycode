
import { getUserInfo, login, logout, register, testApi } from '@/api/user'
import {
  getToken,
  removeToken,
  setToken,
  setUserInfo,
  removeUserInfo,
  getUserInfo as getStoredUserInfo
} from '@/utils/auth'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => {
    const token = getToken() || ''
    const storedUser = getStoredUserInfo()

    const validUser = token && storedUser?.id ? storedUser : null

    if (!validUser) {
      removeToken()
      removeUserInfo()
      localStorage.removeItem('portalMode')
    }

    return {
      token: validUser ? token : '',
      userInfo: validUser,
      permissions: [],
      portalMode: validUser ? (localStorage.getItem('portalMode') || 'user') : 'user'
    }
  },

  getters: {
    /** 仅当存在有效用户 id 时视为已登录，避免残留 token 被误判为“已登录” */
    isLogin: (state) => !!(state.token && state.userInfo?.id),
    userName: (state) => state.userInfo?.username || '',
    realName: (state) => state.userInfo?.real_name || state.userInfo?.username || '',
    avatar: (state) => state.userInfo?.avatar || '',
    userId: (state) => state.userInfo?.id || null,
    isAdmin: (state) => state.userInfo?.role === 'admin',
    hasAdminAccess: (state) => state.userInfo?.role === 'admin' && state.portalMode === 'admin'
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
        const response = await login({
          username: loginForm.username,
          password: loginForm.password
        })

        console.log('登录响应:', response)

        if (response.code === 200 && response.data) {
          const userData = response.data
          this.userInfo = userData

          // 后端未返回 token，这里沿用原有模拟 token 逻辑
          this.token = `user_${userData.id}_${Date.now()}`
          setToken(this.token)

          setUserInfo(userData)
          this.setPortalMode('user')
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
        if (!this.isLogin || !this.userInfo?.id) {
          throw new Error('当前为游客状态，请先登录')
        }

        const response = await getUserInfo(this.userInfo.id)
        console.log('用户信息响应:', response)

        const remoteUser = response?.data || response || {}
        const mergedUser = {
          ...(this.userInfo || {}),
          ...remoteUser,
          avatar: remoteUser.avatar ?? this.userInfo?.avatar,
          real_name: remoteUser.real_name ?? this.userInfo?.real_name,
          student_id: remoteUser.student_id ?? this.userInfo?.student_id,
          phone: remoteUser.phone ?? this.userInfo?.phone,
          email: remoteUser.email ?? this.userInfo?.email
        }

        if (mergedUser?.id) {
          this.userInfo = mergedUser
          setUserInfo(mergedUser)
        }

        return Promise.resolve(mergedUser)
      } catch (error) {
        console.error('获取用户信息失败:', error)
        return Promise.reject(error)
      }
    },

    // 退出登录
    async logout() {
      try {
        await logout()
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
      this.portalMode = 'user'
      this.permissions = []
      removeToken()
      removeUserInfo()
      localStorage.removeItem('portalMode')
    },

    // 更新用户信息（本地）
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      setUserInfo(this.userInfo)
    },

    setPortalMode(mode) {
      this.portalMode = mode === 'admin' ? 'admin' : 'user'
      localStorage.setItem('portalMode', this.portalMode)
    },

    enterAdminMode() {
      this.setPortalMode('admin')
    },

    enterUserMode() {
      this.setPortalMode('user')
    }
  }
})