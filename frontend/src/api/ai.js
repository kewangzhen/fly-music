import apiClient from './axios'

export default {
  // AI文本生成音乐
  generateMusic(prompt, genre = '', mood = '') {
    return apiClient.post('/ai/generate', { prompt, genre, mood })
  },

  // 获取生成状态
  getGenerationStatus(id) {
    return apiClient.get(`/ai/generate/${id}`)
  },

  // 获取用户AI生成历史
  getUserGenerations(userId) {
    return apiClient.get(`/ai/user/${userId}`)
  },

  // AI听歌识别曲风
  recognizeMusicStyle(musicUrl) {
    return apiClient.post('/ai/recognize', { musicUrl })
  },

  // 删除AI生成记录
  deleteGeneration(id) {
    return apiClient.delete(`/ai/${id}`)
  }
}
