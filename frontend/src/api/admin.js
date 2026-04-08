import apiClient from './axios'

export default {
  // 获取用户统计数据
  getUserStats() {
    return apiClient.get('/admin/stats/users')
  },

  // 获取音乐统计数据
  getSongStats() {
    return apiClient.get('/admin/stats/songs')
  },

  // 获取系统操作日志
  getSystemLogs(page = 0, size = 20) {
    return apiClient.get('/admin/logs', { params: { page, size } })
  },

  // 获取听歌大数据
  getListeningStats() {
    return apiClient.get('/admin/stats/listening')
  },

  // 获取系统配置
  getSystemConfig() {
    return apiClient.get('/admin/config')
  },

  // 更新系统配置
  updateSystemConfig(config) {
    return apiClient.put('/admin/config', config)
  },

  // 数据备份
  backupData() {
    return apiClient.post('/admin/backup')
  },

  // 数据恢复
  restoreData(backupId) {
    return apiClient.post('/admin/restore', { backupId })
  },

  // 获取推荐决策看板数据
  getRecommendationDashboard() {
    return apiClient.get('/admin/recommendation/dashboard')
  },

  // 获取推荐权重配置
  getRecommendationConfig() {
    return apiClient.get('/admin/recommendation/config')
  },

  // 更新推荐权重配置
  updateRecommendationConfig(config) {
    return apiClient.put('/admin/recommendation/config', config)
  },

  // 重新计算推荐
  recalculateRecommendation() {
    return apiClient.post('/admin/recommendation/recalculate')
  },

  // 获取所有分类
  getCategories() {
    return apiClient.get('/admin/categories')
  },

  // 获取单个分类
  getCategory(id) {
    return apiClient.get(`/admin/categories/${id}`)
  },

  // 创建分类
  createCategory(category) {
    return apiClient.post('/admin/categories', category)
  },

  // 更新分类
  updateCategory(id, category) {
    return apiClient.put(`/admin/categories/${id}`, category)
  },

  // 删除分类
  deleteCategory(id) {
    return apiClient.delete(`/admin/categories/${id}`)
  }
}
