import apiClient from './axios'

export default {
  // 获取所有歌曲
  getAllSongs() {
    return apiClient.get('/songs')
  },

  // 根据ID获取歌曲
  getSongById(id) {
    return apiClient.get(`/songs/${id}`)
  },

  // 创建歌曲
  createSong(songData) {
    return apiClient.post('/songs', songData)
  },

  // 更新歌曲
  updateSong(id, songData) {
    return apiClient.put(`/songs/${id}`, songData)
  },

  // 删除歌曲
  deleteSong(id) {
    return apiClient.delete(`/songs/${id}`)
  },

  // 根据分类ID获取歌曲
  getSongsByCategory(categoryId) {
    return apiClient.get(`/songs/category/${categoryId}`)
  },

  // 根据艺术家ID获取歌曲
  getSongsByArtist(artistId) {
    return apiClient.get(`/songs/artist/${artistId}`)
  },

  // 根据专辑ID获取歌曲
  getSongsByAlbum(albumId) {
    return apiClient.get(`/songs/album/${albumId}`)
  },

  // 获取用户原创歌曲
  getOriginalSongs(userId) {
    return apiClient.get(`/songs/user/${userId}`)
  },

  // 搜索歌曲
  searchSongs(keyword) {
    return apiClient.get('/songs/search', { params: { keyword } })
  },

  // 获取热门歌曲
  getPopularSongs(limit = 10) {
    return apiClient.get('/songs/popular', { params: { limit } })
  },

  // 获取最新歌曲
  getLatestSongs(limit = 10) {
    return apiClient.get('/songs/latest', { params: { limit } })
  },

  // 获取推荐歌曲
  getRecommendedSongs(userId, limit = 10) {
    return apiClient.get(`/songs/recommended/${userId}`, { params: { limit } })
  },

  // 播放歌曲
  playSong(id) {
    return apiClient.post(`/songs/${id}/play`)
  },

  // 审核歌曲（管理员）
  approveSong(id) {
    return apiClient.post(`/songs/${id}/approve`)
  },

  // 下架歌曲（管理员）
  rejectSong(id) {
    return apiClient.post(`/songs/${id}/reject`)
  },

  // 分页获取歌曲（管理员）
  getSongsByPage(page = 0, size = 10, status = 1) {
    return apiClient.get('/songs/page', { params: { page, size, status } })
  }
}
