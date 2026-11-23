// 从localStorage获取token
export function getToken() {
  return localStorage.getItem('token')
}

// 设置token到localStorage
export function setToken(token) {
  localStorage.setItem('token', token)
}

// 移除token
export function removeToken() {
  localStorage.removeItem('token')
}

// 获取用户信息（与 Pinia 登录后 setUserInfo 写入的键一致）
export function getUserInfo() {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
}

/** 已登录且具备用户 id（与路由 requiresAuth 判定一致） */
export function isSessionComplete() {
  return !!(getToken() && getUserInfo()?.id)
}

// 设置用户信息
export function setUserInfo(userInfo) {
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

// 移除用户信息
export function removeUserInfo() {
  localStorage.removeItem('userInfo')
}