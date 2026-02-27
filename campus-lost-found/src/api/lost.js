// src/api/lost.js - 适配后端Servlet接口
import request from '@/utils/request'

// 获取失物列表
export function getLostList(params) {
  return request({
    url: '/lostapi/list',
    method: 'get',
    params
  })
}

// 获取失物详情
export function getLostDetail(id) {
  return request({
    url: '/lostapi/detail',
    method: 'get',
    params: { id }  // 后端使用id参数，不是路径参数
  })
}

// 获取用户失物列表（需要userId）
export function getUserLostList(params) {
  return request({
    url: '/lostapi/my',
    method: 'get',
    params: {
      userId: params.userId,  // 后端需要userId参数
      keyword: params.keyword,
      status: params.status
    }
  })
}

// 发布失物
export function createLostItem(data) {
  return request({
    url: '/lostapi/publish',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    transformRequest: [function (data) {
      // 将JSON转换为URL编码格式
      let ret = ''
      for (let it in data) {
        ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
      }
      return ret.slice(0, -1)
    }]
  })
}

// 更新失物信息
export function updateLostItem(id, data) {
  return request({
    url: '/lostapi/update',
    method: 'post',
    data: {
      id,
      ...data
    },
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    transformRequest: [function (data) {
      let ret = ''
      for (let it in data) {
        ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
      }
      return ret.slice(0, -1)
    }]
  })
}

// 删除失物
export function deleteLostItem(id) {
  return request({
    url: '/lostapi/delete',
    method: 'post',
    data: { id },
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    transformRequest: [function (data) {
      let ret = ''
      for (let it in data) {
        ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
      }
      return ret.slice(0, -1)
    }]
  })
}

// 搜索失物
export function searchLostItems(keyword) {
  return request({
    url: '/lostapi/search',
    method: 'get',
    params: { keyword }
  })
}

// 注意：以下接口后端暂无实现，暂时注释
/*
export function getHomeStats() {
  return request({
    url: '/api/home/stats',
    method: 'get'
  })
}

export function getLatestLost() {
  return request({
    url: '/api/lost/latest',
    method: 'get'
  })
}

export function getLatestFound() {
  return request({
    url: '/api/found/latest',
    method: 'get'
  })
}
*/