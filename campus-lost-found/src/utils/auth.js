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

// 获取用户信息
export function getUserInfo() {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
}

// 设置用户信息
export function setUserInfo(userInfo) {
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

// 移除用户信息
export function removeUserInfo() {
  localStorage.removeItem('userInfo')
}