import request from '@/utils/request'

// 获取首页统计数据
export function getHomeStats() {
  return request({
    url: '/homeapi/stats',
    method: 'get'
  })
}

/** 公开物品分类（与管理员分类配置同源，无需登录） */
export function getHomeCategories() {
  return request({
    url: '/homeapi/categories',
    method: 'get'
  })
}

// 获取最新失物列表
export function getLatestLost(params = {}) {
  return request({
    url: '/lostapi/list',
    method: 'get',
    params: {
      page: 1,
      size: 10,  // 默认10条
      ...params
    }
  })
}

// 获取最新招领列表
export function getLatestFound(params = {}) {
  return request({
    url: '/foundapi/list',
    method: 'get',
    params: {
      page: 1,
      size: 10,  // 默认10条
      ...params
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