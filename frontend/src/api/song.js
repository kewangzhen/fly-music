import apiClient from './axios'

const API_BASE_URL = '/songs'

export default {
  // 分页获取歌曲
  getSongsByPage(page = 0, size = 10) {
    return apiClient.get(`${API_BASE_URL}/page`, { params: { page, size } })
  },

  // 根据ID获取歌曲
  getSongById(id) {
    return apiClient.get(`${API_BASE_URL}/${id}`)
  },

  // 创建歌曲
  createSong(songData) {
    return apiClient.post(API_BASE_URL, songData)
  },

  // 更新歌曲
  updateSong(id, songData) {
    return apiClient.put(`${API_BASE_URL}/${id}`, songData)
  },

  // 删除歌曲
  deleteSong(id) {
    return apiClient.delete(`${API_BASE_URL}/${id}`)
  },

  // 根据分类ID获取歌曲
  getSongsByCategory(categoryId, page = 0, size = 10) {
    return apiClient.get(`${API_BASE_URL}/category/${categoryId}`, { params: { page, size } })
  },

  // 根据艺术家ID获取歌曲
  getSongsByArtist(artistId) {
    return apiClient.get(`${API_BASE_URL}/artist/${artistId}`)
  },

  // 根据专辑ID获取歌曲
  getSongsByAlbum(albumId) {
    return apiClient.get(`${API_BASE_URL}/album/${albumId}`)
  },

  // 获取用户原创歌曲
  getOriginalSongs(userId) {
    return apiClient.get(`${API_BASE_URL}/user/${userId}`)
  },

  // 搜索歌曲
  searchSongs(keyword) {
    return apiClient.get(`${API_BASE_URL}/search`, { params: { keyword } })
  },

  // 获取热门歌曲
  getPopularSongs(limit = 10) {
    return apiClient.get(`${API_BASE_URL}/popular`, { params: { limit } })
  },

  // 获取最新歌曲
  getLatestSongs(limit = 10) {
    return apiClient.get(`${API_BASE_URL}/latest`, { params: { limit } })
  },

  // 获取推荐歌曲
  getRecommendedSongs(userId, limit = 10) {
    return apiClient.get(`${API_BASE_URL}/recommended/${userId}`, { params: { limit } })
  },

  // 播放歌曲
  playSong(id) {
    return apiClient.post(`${API_BASE_URL}/${id}/play`)
  },

  // 审核歌曲（管理员）
  approveSong(id) {
    return apiClient.post(`${API_BASE_URL}/${id}/approve`)
  },

  // 下架歌曲（管理员）
  rejectSong(id) {
    return apiClient.post(`${API_BASE_URL}/${id}/reject`)
  },

  // 从MP3提取元数据
  extractMetadata(id) {
    return apiClient.post(`${API_BASE_URL}/${id}/extract-metadata`)
  },

  // 管理员分页获取歌曲
  getAdminSongsByPage(page = 0, size = 10, status = 1) {
    return apiClient.get(`${API_BASE_URL}/page`, { params: { page, size, status } })
  },

  // 上传歌曲文件
  uploadSong(file, title = '') {
    const formData = new FormData()
    formData.append('file', file)
    if (title) {
      formData.append('title', title)
    }
    return apiClient.post(`${API_BASE_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取歌曲播放URL
  getSongUrl(url) {
    if (!url) return ''
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }
    const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    return `${baseURL}${url}`
  }
}
