import apiClient from './axios'

export default {
  // 获取用户播放历史
  getUserPlayHistory(userId) {
    return apiClient.get(`/play-history/user/${userId}`)
  },

  // 获取用户最近播放历史
  getRecentPlayHistory(userId, limit = 20) {
    return apiClient.get(`/play-history/user/${userId}/recent`, { params: { limit } })
  },

  // 添加播放历史
  addPlayHistory(userId, songId, duration = 0) {
    return apiClient.post('/play-history', { userId, songId, duration })
  },

  // 获取用户播放统计
  getUserPlayStats(userId) {
    return apiClient.get(`/play-history/user/${userId}/stats`)
  },

  // 清空用户播放历史
  clearUserPlayHistory(userId) {
    return apiClient.delete(`/play-history/user/${userId}`)
  }
}
