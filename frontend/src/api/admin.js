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
  }
}
