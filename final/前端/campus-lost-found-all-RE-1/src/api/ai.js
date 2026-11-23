import request from '@/utils/request'

export function chatAi(data) {
  return request({
    url: '/api/ai/chat',
    method: 'post',
    data
  })
}