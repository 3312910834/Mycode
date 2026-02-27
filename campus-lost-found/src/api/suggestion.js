import request from '@/utils/request'

/**
 * 建议反馈API
 * 对应后端的 SuggestionApiServlet
 * 注意：后端路径是 /suggestionapi/*
 */

// 提交建议
export function submitSuggestion(data) {
  const params = new URLSearchParams()
  
  // 从 localStorage 获取用户信息
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  
  // 确定 userId：优先使用传入的 userId，否则使用 localStorage 中的 id，最后使用默认值 8
  const userId = data.userId || user.id || 8
  
  // 构建内容：包含姓名和联系方式
  let fullContent = data.content || ''
  if (data.name) {
    fullContent = `【提交人：${data.name}】\n${fullContent}`
  }
  if (data.contact) {
    fullContent += `\n【联系方式：${data.contact}】`
  }
  
  params.append('userId', userId)
  params.append('content', fullContent)
  
  return request({
    url: '/suggestionapi/submit',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// 获取用户的建议列表（对应后端的 /my 接口）
export function getUserSuggestions(userId) {
  return request({
    url: '/suggestionapi/my',
    method: 'get',
    params: { 
      userId: userId 
    }
  })
}

// 为 SuggestionAdd.vue 提供 createSuggestion 导出
export function createSuggestion(data) {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const userId = user.id || 8
  
  // 构建完整的建议内容
  const suggestionContent = `
建议类型：${data.type || 'function'}
联系方式：${data.contact || '未提供'}
建议内容：
${data.content}
`.trim()
  
  const suggestionData = {
    userId: userId,
    content: suggestionContent
  }
  
  return submitSuggestion(suggestionData)
}

// 为 SuggestionList.vue 提供 getSuggestions 导出
export function getSuggestions(params = {}) {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const userId = user.id || 8
  
  return request({
    url: '/suggestionapi/my',
    method: 'get',
    params: {
      userId: userId,
      page: params.page || 1,
      size: params.size || 10
    }
  })
}

// 为 SuggestionList.vue 提供 deleteSuggestion 导出
export function deleteSuggestion(id) {
  return request({
    url: '/suggestionapi/delete',
    method: 'post',
    params: { 
      id: id 
    }
  })
}

// 获取我的建议（getUserSuggestions 的别名）
export const getMySuggestions = getUserSuggestions

// 获取建议详情
export function getSuggestionDetail(id) {
  return request({
    url: '/suggestionapi/detail',
    method: 'get',
    params: { 
      id: id 
    }
  })
}

// 获取所有建议（分页）- 管理员用
export function getAllSuggestions(page = 1, pageSize = 10) {
  return request({
    url: '/suggestionapi/list',
    method: 'get',
    params: { 
      page: page, 
      size: pageSize 
    }
  })
}

// 根据状态获取建议
export function getSuggestionsByStatus(status) {
  return request({
    url: '/suggestionapi/byStatus',
    method: 'get',
    params: { 
      status: status 
    }
  })
}

// 获取建议统计
export function getSuggestionStats() {
  return request({
    url: '/suggestionapi/stats',
    method: 'get'
  })
}

// 获取建议数量 - 使用 getSuggestionStats 替代
export const getSuggestionCount = getSuggestionStats

// 更新建议状态
export function updateSuggestionStatus(id, status) {
  const params = new URLSearchParams()
  params.append('id', id)
  params.append('status', status)
  
  return request({
    url: '/suggestionapi/updateStatus',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// 添加回复
export function addReply(id, reply) {
  const params = new URLSearchParams()
  params.append('id', id)
  params.append('reply', reply)
  
  return request({
    url: '/suggestionapi/reply',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// 测试API连接
export function testSuggestionApi() {
  return request({
    url: '/suggestionapi/',
    method: 'get'
  })
}