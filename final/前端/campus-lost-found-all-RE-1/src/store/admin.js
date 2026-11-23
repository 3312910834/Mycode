import { defineStore } from 'pinia'
import {
  getAllItems,
  updateItemStatus,
  deleteItem,
  getAllUsers,
  updateUserStatus,
  getCategories,
  updateCategories,
  getSystemStats,
  getSystemSettings,
  updateSystemSettings
} from '@/api/admin'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    items: [],
    users: [],
    totalUsers: 0,
    categories: [],
    stats: {
      totalItems: 0,
      pendingItems: 0,
      resolvedItems: 0,
      totalUsers: 0
    },
    settings: {},
    loading: false,
    searchTerm: '',
    filterType: 'all', // all, lost, found
    filterStatus: 'all' // all, pending, approved, resolved
  }),

  getters: {
    filteredItems: (state) => {
      const q = (state.searchTerm || '').toLowerCase()
      return state.items.filter((item) => {
        const title = String(item.title ?? '')
        const loc = String(item.location ?? '')
        const poster = String(item.poster ?? '')
        const matchesSearch =
          !q ||
          title.toLowerCase().includes(q) ||
          loc.toLowerCase().includes(q) ||
          poster.toLowerCase().includes(q)
        const matchesType = state.filterType === 'all' || item.type === state.filterType
        const matchesStatus = state.filterStatus === 'all' || item.status === state.filterStatus

        return matchesSearch && matchesType && matchesStatus
      })
    },

    filteredUsers: (state) => {
      const q = (state.searchTerm || '').toLowerCase()
      return state.users.filter((user) => {
        const username = String(user.username ?? '')
        const email = String(user.email ?? '')
        return (
          !q ||
          username.toLowerCase().includes(q) ||
          email.toLowerCase().includes(q)
        )
      })
    }
  },

  actions: {
    // 获取所有信息
    async fetchItems() {
      this.loading = true
      try {
        const response = await getAllItems({
          page: 1,
          size: 50,
          type: this.filterType,
          status: this.filterStatus,
          keyword: this.searchTerm
        })
        if (response.code === 200) {
          this.items = Array.isArray(response.data?.items) ? response.data.items : []
        }
      } catch (error) {
        console.error('获取信息失败:', error)
      } finally {
        this.loading = false
      }
    },

    // 更新信息状态
    async updateItemStatus(itemId, newStatus, itemType) {
      try {
        const response = await updateItemStatus({ id: itemId, status: newStatus, type: itemType })
        if (response.code === 200) {
          const item = this.items.find(i => i.id === itemId)
          if (item) {
            item.status = newStatus
          }
        }
      } catch (error) {
        console.error('更新状态失败:', error)
        throw error
      }
    },

    // 删除信息
    async deleteItem(itemId, itemType) {
      try {
        const response = await deleteItem({ id: itemId, type: itemType })
        if (response.code === 200) {
          this.items = this.items.filter(i => !(i.id === itemId && i.type === itemType))
        }
      } catch (error) {
        console.error('删除信息失败:', error)
        throw error
      }
    },

    // 获取所有用户
    async fetchUsers(params = {}) {
      this.loading = true
      try {
        const response = await getAllUsers({
          page: params.page || 1,
          limit: params.limit || 200,
          username: params.username ?? '',
          role: params.role ?? '',
          status: params.status ?? ''
        })
        if (response.code === 200) {
          const d = response.data || {}
          const list = Array.isArray(d.list)
            ? d.list
            : Array.isArray(d.records)
              ? d.records
              : []
          this.users = list
          this.totalUsers = d.total ?? list.length
        }
      } catch (error) {
        console.error('获取用户失败:', error)
      } finally {
        this.loading = false
      }
    },

    // 更新用户状态
    async updateUserStatus(userId, newStatus) {
      try {
        const response = await updateUserStatus({ id: userId, status: newStatus })
        if (response.code === 200) {
          const user = this.users.find(u => u.id === userId)
          if (user) {
            user.status = newStatus
          }
        }
      } catch (error) {
        console.error('更新用户状态失败:', error)
        throw error
      }
    },

    // 获取分类
    async fetchCategories() {
      try {
        const response = await getCategories()
        if (response.code === 200) {
          const raw = response.data
          this.categories = Array.isArray(raw) ? raw : []
        }
      } catch (error) {
        console.error('获取分类失败:', error)
      }
    },

    // 更新分类
    async updateCategories(categories) {
      try {
        const response = await updateCategories({ categories })
        if (response.code === 200) {
          this.categories = categories
        }
      } catch (error) {
        console.error('更新分类失败:', error)
        throw error
      }
    },

    // 获取统计数据
    async fetchStats() {
      try {
        const response = await getSystemStats()
        if (response.code === 200 && response.data) {
          this.stats = { ...this.stats, ...response.data }
        }
      } catch (error) {
        console.error('获取统计失败:', error)
      }
    },

    // 获取设置
    async fetchSettings() {
      try {
        const response = await getSystemSettings()
        if (response.code === 200) {
          this.settings = response.data || {}
        }
      } catch (error) {
        console.error('获取设置失败:', error)
      }
    },

    // 更新设置
    async updateSettings(settings) {
      try {
        const response = await updateSystemSettings(settings)
        if (response.code === 200) {
          this.settings = settings
        }
      } catch (error) {
        console.error('更新设置失败:', error)
        throw error
      }
    },

    // 设置搜索和过滤
    setSearchTerm(term) {
      this.searchTerm = term
      this.fetchItems()
    },

    setFilterType(type) {
      this.filterType = type
      this.fetchItems()
    },

    setFilterStatus(status) {
      this.filterStatus = status
      this.fetchItems()
    }
  }
})
