import request from '@/utils/request'

// 用户登录 - 使用GET方法，后端Servlet支持GET
export function login(data) {
  return request({
    url: '/userapi/login',
    method: 'get',  // 后端Servlet将POST转到GET处理
    params: data    // 使用params传递查询参数
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/userapi/register',
    method: 'get',  // 后端Servlet将POST转到GET处理
    params: data    // 使用params传递查询参数
  })
}

// 获取用户信息
export function getUserInfo(userId) {
  return request({
    url: '/userapi/info',
    method: 'get',
    params: { id: userId }  // 需要传递id参数
  })
}

// 测试接口
export function testApi() {
  return request({
    url: '/userapi/test',
    method: 'get'
  })
}
// 更新用户信息 - 后端暂无此接口
export function updateUserInfo(data) {
  return request({
    url: '/userapi/update',  // 根据后端接口调整URL
    method: 'post',
    data
  })
}
// ✅ 添加修改密码方法（UserPassword.vue需要）
export function updatePassword(data) {
  return request({
    url: '/userapi/updatePassword',
    method: 'post',
    data
  })
}

// 注意：以下接口后端Servlet暂不支持，如果需要请后端添加
/*
// 更新用户信息 - 后端暂无此接口

export function updateUserInfo(data) {
  return request({
    url: '/userapi/update',  // 根据后端接口调整URL
    method: 'post',
    data
  })
}
// 修改密码 - 后端暂无此接口
export function updatePassword(data) {
  return request({
    url: '/api/user/password',
    method: 'put',
    data
  })
}

// 退出登录 - 后端暂无此接口
export function logout() {
  return request({
    url: '/api/user/logout',
    method: 'post'
  })
}
*/