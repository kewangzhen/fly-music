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

  // 通过令牌更新密码
  updatePasswordByToken(token, newPassword) {
    return apiClient.post('/users/update-password', { token, newPassword })
  },

  // 验证重置令牌
  verifyToken(token) {
    return apiClient.get('/users/verify-token', { params: { token } })
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
  },

  // 获取用户收藏
  getFavorites(userId) {
    return apiClient.get(`/favorites/user/${userId}`)
  },

  // 添加收藏
  addFavorite(userId, targetType, targetId) {
    return apiClient.post('/favorites', { userId, targetType, targetId })
  },

  // 移除收藏
  removeFavorite(userId, targetType, targetId) {
    return apiClient.delete('/favorites/remove', { data: { userId, targetType, targetId } })
  },

  // 检查是否收藏
  checkFavorite(userId, targetType, targetId) {
    return apiClient.get(`/favorites/check`, { params: { userId, targetType, targetId } })
  }
}
