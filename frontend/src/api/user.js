import apiClient from './axios'

export default {
  // 用户注册
  register(userData) {
    return apiClient.post('/users/register', userData)
  },

  // 用户登录
  login(username, password) {
    return apiClient.post('/users/login', { username, password })
  },

  // 获取用户信息
  getUserById(id) {
    return apiClient.get(`/users/${id}`)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return apiClient.get('/users/current')
  },

  // 更新用户信息
  updateUser(id, userData) {
    return apiClient.put(`/users/${id}`, userData)
  },

  // 修改密码
  changePassword(id, oldPassword, newPassword) {
    return apiClient.post(`/users/${id}/change-password`, {
      oldPassword,
      newPassword
    })
  },

  // 重置密码
  resetPassword(email) {
    return apiClient.post('/users/reset-password', { email })
  },

  // 获取所有用户（管理员）
  getAllUsers() {
    return apiClient.get('/users')
  },

  // 启用/禁用用户（管理员）
  updateUserStatus(id, status) {
    return apiClient.put(`/users/${id}/status`, {}, { params: { status } })
  },

  // 设置VIP（管理员）
  setVip(id, expireDate) {
    return apiClient.post(`/users/${id}/vip`, { expireDate })
  }
}
