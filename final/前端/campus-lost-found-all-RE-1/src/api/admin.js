import request from '@/utils/request'

// 获取所有失物招领信息（管理员）
export function getAllItems(params) {
  return request({
    url: '/adminapi/items',
    method: 'get',
    params
  })
}

// 更新信息状态
export function updateItemStatus(data) {
  return request({
    url: '/adminapi/item/status',
    method: 'post',
    data: {
      id: String(data.id),   // ⭐️ 核心魔法：强制把数字 25 变成字符串 "25"
      status: data.status,
      type: data.type
    }
  })
}

// 删除信息
export function deleteItem(data) {
  return request({
    url: '/adminapi/item/delete',
    method: 'post',
    data: {
      id: String(data.id),   // ⭐️ 核心魔法：强制把数字变成字符串
      type: data.type
    }
  })
}

// ================= 新增：获取仪表盘所有图表统计数据 =================
export function getDashboardStats() {
  return request({
    url: '/api/admin/stat/dashboard', // 和 Java Servlet 的 @WebServlet 保持完全一致
    method: 'get'
  })
}
// 获取所有用户
export function getAllUsers(params = {}) {
  return request({
    url: '/admin/user',
    method: 'get',
    params: {
      action: 'list',
      page: params.page || 1,
      limit: params.limit || 200,
      username: params.username || '',
      role: params.role || '',
      status: params.status || ''
    }
  })
}

// 更新用户状态（参数走 Query，与 AdminUserServlet 一致）
export function updateUserStatus(data) {
  return request({
    url: '/admin/user',
    method: 'post',
    params: {
      action: 'updateStatus',
      id: data.id,
      status: data.status
    }
  })
}

// 更新用户角色（管理员提拔/降级）
export function updateUserRole(data) {
  return request({
    url: '/admin/user',
    method: 'post',
    params: {
      action: 'updateRole',
      id: data.id,
      role: data.role
    }
  })
}

// 获取分类配置
export function getCategories() {
  return request({
    url: '/adminapi/categories',
    method: 'get'
  })
}

// 更新分类配置
export function updateCategories(data) {
  return request({
    url: '/adminapi/categories',
    method: 'post',
    data
  })
}

// 获取系统统计
export function getSystemStats() {
  return request({
    url: '/adminapi/stats',
    method: 'get'
  })
}

// 获取系统设置
export function getSystemSettings() {
  return request({
    url: '/adminapi/settings',
    method: 'get'
  })
}

// 更新系统设置
export function updateSystemSettings(data) {
  return request({
    url: '/adminapi/settings',
    method: 'post',
    data
  })
}
