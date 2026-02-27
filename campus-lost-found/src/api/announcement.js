// src/api/announcement.js
import request from '@/utils/request'

// 获取公告列表
export function getAnnouncements(params) {
  return request({
    url: '/announcementapi/list',  // 修正：去掉 /api 前缀
    method: 'get',
    params
  })
}

// 获取置顶公告
export function getTopAnnouncements() {
  return request({
    url: '/announcementapi/top',  // 修正：去掉 /api 前缀
    method: 'get'
  })
}

// 获取显示公告
export function getVisibleAnnouncements(params) {
  return request({
    url: '/announcementapi/visible',  // 修正：去掉 /api 前缀
    method: 'get',
    params
  })
}

// 获取公告详情
export function getAnnouncementDetail(id) {
  return request({
    url: '/announcementapi/detail',  // 修正：去掉 /api 前缀
    method: 'get',
    params: { id }
  })
}