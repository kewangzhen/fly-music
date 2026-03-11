import apiClient from './axios'

export default {
  // 获取用户收藏列表
  getUserFavorites(userId) {
    return apiClient.get(`/favorites/user/${userId}`)
  },

  // 获取用户特定类型收藏
  getUserFavoritesByType(userId, targetType) {
    return apiClient.get(`/favorites/user/${userId}/type/${targetType}`)
  },

  // 添加收藏
  addFavorite(userId, targetType, targetId) {
    return apiClient.post('/favorites', { userId, targetType, targetId })
  },

  // 取消收藏
  removeFavorite(userId, targetType, targetId) {
    return apiClient.delete('/favorites', { data: { userId, targetType, targetId } })
  },

  // 检查是否已收藏
  checkFavorite(userId, targetType, targetId) {
    return apiClient.get('/favorites/check', { params: { userId, targetType, targetId } })
  },

  // 统计用户收藏数量
  countUserFavorites(userId) {
    return apiClient.get(`/favorites/user/${userId}/count`)
  }
}
