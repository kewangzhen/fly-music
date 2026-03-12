import apiClient from './axios'

export default {
  // ==================== 雷达歌单 ====================
  // 获取雷达歌单
  getRadarPlaylist() {
    return apiClient.get('/recommendation/radar')
  },

  // 获取用户兴趣画像
  getUserInterest() {
    return apiClient.get('/recommendation/interest')
  },

  // 记录用户行为
  recordBehavior(songId, behaviorType) {
    return apiClient.post('/recommendation/behavior', {
      songId,
      behaviorType
    })
  },

  // ==================== 热门榜单 ====================
  // 获取全局热门榜
  getGlobalRanking() {
    return apiClient.get('/ranking/global')
  },

  // 获取分类热门榜
  getCategoryRanking(categoryId) {
    return apiClient.get(`/ranking/category/${categoryId}`)
  },

  // 获取新歌榜
  getNewSongRanking() {
    return apiClient.get('/ranking/new')
  },

  // 获取飙升榜
  getSoaringRanking() {
    return apiClient.get('/ranking/soaring')
  },

  // 刷新榜单
  refreshRankings() {
    return apiClient.post('/ranking/refresh')
  },

  // ==================== 分类推荐 ====================
  // 获取分类推荐歌曲
  getCategorySongs(categoryId, limit = 20) {
    return apiClient.get(`/category/${categoryId}/songs`, { params: { limit } })
  },

  // 根据标签获取分类推荐歌曲
  getCategorySongsByTag(tag, limit = 20) {
    return apiClient.get(`/category/tag/${tag}/songs`, { params: { limit } })
  },

  // ==================== 听歌报告 ====================
  // 获取听歌报告
  getListeningReport() {
    return apiClient.get('/report/listening')
  },

  // 获取指定年份听歌报告
  getListeningReportByYear(year) {
    return apiClient.get(`/report/listening/${year}`)
  },

  // 获取简要统计
  getSummary() {
    return apiClient.get('/report/summary')
  }
}
