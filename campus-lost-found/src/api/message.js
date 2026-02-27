import request from '@/utils/request'

/**
 * 获取消息列表
 * @param {Object} params 参数
 * @returns Promise
 */
export function getMessages(params) {
  return request({
    url: '/messageapi/received',
    method: 'get',
    params: {
      userId: params.userId,
      // 后端不支持分页和状态筛选，需要在客户端处理
    }
  })
}

/**
 * 获取消息详情
 * @param {Number} id 消息ID
 * @returns Promise
 */
export function getMessageDetail(id) {
  return request({
    url: '/messageapi/detail',
    method: 'get',
    params: { id }
  })
}

/**
 * 发送消息
 * @param {Object} data 消息数据
 * @returns Promise
 */
export function sendMessage(data) {
  return request({
    url: '/messageapi/send',
    method: 'post',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: {
      fromUserId: data.from_user_id || data.fromUserId,
      toUserId: data.to_user_id || data.toUserId,
      itemId: data.item_id,
      itemType: data.item_type,
      content: data.content
    },
    transformRequest: [(data) => {
      const formData = new URLSearchParams()
      for (const key in data) {
        if (data[key] !== undefined) {
          formData.append(key, data[key])
        }
      }
      return formData
    }]
  })
}

/**
 * 标记消息为已读
 * @param {Number} id 消息ID
 * @returns Promise
 */
export function markMessageAsRead(id) {
  return request({
    url: '/messageapi/read',
    method: 'post',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: { id },
    transformRequest: [(data) => {
      const formData = new URLSearchParams()
      formData.append('id', data.id)
      return formData
    }]
  })
}

/**
 * 删除消息
 * @param {Number} id 消息ID
 * @returns Promise
 */
export function deleteMessage(id) {
  return request({
    url: '/messageapi/delete',
    method: 'post',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: { id },
    transformRequest: [(data) => {
      const formData = new URLSearchParams()
      formData.append('id', data.id)
      return formData
    }]
  })
}

/**
 * 获取未读消息数量
 * @param {Number} userId 用户ID
 * @returns Promise
 */
export function getUnreadCount(userId) {
  return request({
    url: '/messageapi/unread',
    method: 'get',
    params: { userId }
  })
}

/**
 * 获取发送的消息
 * @param {Number} userId 用户ID
 * @returns Promise
 */
export function getSentMessages(userId) {
  return request({
    url: '/messageapi/sent',
    method: 'get',
    params: { userId }
  })
}

/**
 * 获取对话记录
 * @param {Number} user1Id 用户1ID
 * @param {Number} user2Id 用户2ID
 * @returns Promise
 */
export function getConversation(user1Id, user2Id) {
  return request({
    url: '/messageapi/conversation',
    method: 'get',
    params: { user1Id, user2Id }
  })
}