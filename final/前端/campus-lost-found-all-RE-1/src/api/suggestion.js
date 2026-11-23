import request from '@/utils/request'
import { getUserInfo } from '@/utils/auth'

/**
 * 建议反馈 API，与后端 `SuggestionApiServlet`（`/suggestionapi/*`）对齐。
 * 成功类 JSON 多为 `{ code:200, message, data }`；写操作部分返回 `{ success:true }`。
 */

function resolveUserId(explicit) {
  if (explicit != null && explicit !== '') return Number(explicit)
  const u = getUserInfo()
  return u?.id ?? null
}

// 提交建议（表单 POST，参数 userId、content）
export function submitSuggestion(data) {
  const userId = resolveUserId(data.userId)
  if (userId == null) {
    return Promise.reject(new Error('请先登录后再提交建议'))
  }

  const params = new URLSearchParams()
  let fullContent = data.content || ''
  if (data.name) {
    fullContent = `【提交人：${data.name}】\n${fullContent}`
  }
  if (data.contact) {
    fullContent += `\n【联系方式：${data.contact}】`
  }

  params.append('userId', String(userId))
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

// 获取某用户的建议列表（与 GET /suggestionapi/my 一致；后端不分页，仅按 userId 全量返回）
export function getUserSuggestions(userId) {
  const id = resolveUserId(userId)
  if (id == null) {
    return Promise.reject(new Error('缺少 userId'))
  }
  return request({
    url: '/suggestionapi/my',
    method: 'get',
    params: { userId: id }
  })
}

// 与提交页一致：拼装类型、联系方式、正文后调用 submit
export function createSuggestion(data) {
  const userId = resolveUserId(data.userId)
  if (userId == null) {
    return Promise.reject(new Error('请先登录'))
  }
  const suggestionContent = `
建议类型：${data.type || 'function'}
联系方式：${data.contact || '未提供'}
建议内容：
${data.content}
`.trim()

  return submitSuggestion({ userId, content: suggestionContent })
}

// 列表页用：须传入当前用户 id（与后端 `/my` 对齐，忽略 page/size）
export function getSuggestions(params = {}) {
  const userId = resolveUserId(params.userId)
  if (userId == null) {
    return Promise.reject(new Error('请先登录'))
  }
  return request({
    url: '/suggestionapi/my',
    method: 'get',
    params: { userId }
  })
}

// 删除建议（POST 表单：id）
export function deleteSuggestion(id) {
  const params = new URLSearchParams()
  params.append('id', String(id))
  return request({
    url: '/suggestionapi/delete',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export const getMySuggestions = getUserSuggestions

export function getSuggestionDetail(id) {
  return request({
    url: '/suggestionapi/detail',
    method: 'get',
    params: { id }
  })
}

// 管理员或全量：分页列表
export function getAllSuggestions(page = 1, pageSize = 10) {
  return request({
    url: '/suggestionapi/list',
    method: 'get',
    params: {
      page,
      size: pageSize
    }
  })
}

export function getSuggestionsByStatus(status) {
  return request({
    url: '/suggestionapi/byStatus',
    method: 'get',
    params: { status }
  })
}

export function getSuggestionStats() {
  return request({
    url: '/suggestionapi/stats',
    method: 'get'
  })
}

export const getSuggestionCount = getSuggestionStats

export function updateSuggestionStatus(id, status) {
  const params = new URLSearchParams()
  params.append('id', String(id))
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

export function addReply(id, reply) {
  const params = new URLSearchParams()
  params.append('id', String(id))
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

export function testSuggestionApi() {
  return request({
    url: '/suggestionapi/',
    method: 'get'
  })
}
