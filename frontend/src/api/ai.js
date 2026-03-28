import apiClient from './axios'

const AI_BASE_URL = '/ai'

export default {
  // AI文本生成音乐
  generateMusic(prompt, genre = '流行', mood = '欢快', duration = 60, userId = null) {
    const formData = new FormData()
    formData.append('prompt', prompt)
    formData.append('genre', genre)
    formData.append('mood', mood)
    formData.append('duration', duration)
    if (userId) {
      formData.append('userId', userId)
    }
    return apiClient.post(`${AI_BASE_URL}/generate`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取生成状态
  getGenerationStatus(id) {
    return apiClient.get(`${AI_BASE_URL}/generate/${id}`)
  },

  // 获取用户AI生成历史
  getUserGenerations(userId) {
    return apiClient.get(`${AI_BASE_URL}/user/${userId}`)
  },

  // AI听歌识别曲风（上传文件）
  recognizeMusicStyle(file) {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post(`${AI_BASE_URL}/recognize`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除AI生成记录
  deleteGeneration(id) {
    return apiClient.delete(`${AI_BASE_URL}/${id}`)
  },

  // 获取歌曲播放URL
  getSongUrl(url) {
    if (!url) return ''
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }
    const baseURL = apiClient.defaults.baseURL || 'http://localhost:8080/api'
    return baseURL.replace('/api', '') + url
  }
}
