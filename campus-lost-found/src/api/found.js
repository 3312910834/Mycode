// src/api/found.js - 适配后端Servlet接口
import request from '@/utils/request'

// 获取招领列表
export function getFoundList(params) {
  return request({
    url: '/foundapi/list',
    method: 'get',
    params
  })
}

// 获取招领详情
export function getFoundDetail(id) {
  return request({
    url: '/foundapi/detail',
    method: 'get',
    params: { id }  // 后端使用id参数，不是路径参数
  })
}

// 获取用户招领列表（需要userId）
export function getUserFoundList(params) {
  return request({
    url: '/foundapi/my',
    method: 'get',
    params: {
      userId: params.userId,  // 后端需要userId参数
      keyword: params.keyword,
      status: params.status
    }
  })
}

// 创建招领
export function createFoundItem(data) {
  return request({
    url: '/foundapi/publish',
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

// 更新招领信息
export function updateFoundItem(id, data) {
  return request({
    url: '/foundapi/update',
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

// 删除招领
export function deleteFoundItem(id) {
  return request({
    url: '/foundapi/delete',
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

// 搜索招领
export function searchFoundItems(keyword) {
  return request({
    url: '/foundapi/search',
    method: 'get',
    params: { keyword }
  })
}

// 注意：以下接口后端暂无实现，暂时注释
/*
export function getLatestFound() {
  return request({
    url: '/api/found/latest',
    method: 'get'
  })
}
*/