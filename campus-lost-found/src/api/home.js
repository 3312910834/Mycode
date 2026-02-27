import request from '@/utils/request'

// 获取首页统计数据（先使用模拟数据，等后端新增接口）
export function getHomeStats() {
  return request({
    url: '/api/home/stats',
    method: 'get'
  }).catch(() => {
    // 如果接口不存在，返回模拟数据
    return Promise.resolve({
      data: {
        lostCount: 0,
        foundCount: 0,
        successCount: 0,
        userCount: 0
      }
    })
  })
}

// 获取最新失物列表
export function getLatestLost() {
  return request({
    url: '/api/lostapi/list',
    method: 'get',
    params: {
      page: 1,
      size: 10  // 限制10条
    }
  })
}

// 获取最新招领列表
export function getLatestFound() {
  return request({
    url: '/api/foundapi/list',
    method: 'get',
    params: {
      page: 1,
      size: 10  // 限制10条
    }
  })
}

// 搜索失物/招领
export function searchItems(keyword, type = 'lost') {
  const api = type === 'lost' ? '/lostapi/search' : '/foundapi/search'
  return request({
    url: api,
    method: 'get',
    params: { keyword }
  })
}