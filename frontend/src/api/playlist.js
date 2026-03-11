import apiClient from './axios'

export default {
  // 获取所有播放列表
  getAllPlaylists() {
    return apiClient.get('/playlists')
  },

  // 根据ID获取播放列表
  getPlaylistById(id) {
    return apiClient.get(`/playlists/${id}`)
  },

  // 创建播放列表
  createPlaylist(playlistData) {
    return apiClient.post('/playlists', playlistData)
  },

  // 更新播放列表
  updatePlaylist(id, playlistData) {
    return apiClient.put(`/playlists/${id}`, playlistData)
  },

  // 删除播放列表
  deletePlaylist(id) {
    return apiClient.delete(`/playlists/${id}`)
  },

  // 获取用户播放列表
  getPlaylistsByUser(userId) {
    return apiClient.get(`/playlists/user/${userId}`)
  },

  // 向播放列表添加歌曲
  addSongToPlaylist(playlistId, songId) {
    return apiClient.post(`/playlists/${playlistId}/songs/${songId}`)
  },

  // 从播放列表移除歌曲
  removeSongFromPlaylist(playlistId, songId) {
    return apiClient.delete(`/playlists/${playlistId}/songs/${songId}`)
  },

  // 获取播放列表中的歌曲
  getSongsInPlaylist(playlistId) {
    return apiClient.get(`/playlists/${playlistId}/songs`)
  },

  // 搜索播放列表
  searchPlaylists(keyword) {
    return apiClient.get('/playlists/search', { params: { keyword } })
  },

  // 获取热门播放列表
  getPopularPlaylists(limit = 10) {
    return apiClient.get('/playlists/popular', { params: { limit } })
  },

  // 获取心动歌单
  getHeartPlaylists(limit = 10) {
    return apiClient.get('/playlists/heart', { params: { limit } })
  },

  // 获取雷达榜单
  getRadarPlaylists(limit = 10) {
    return apiClient.get('/playlists/radar', { params: { limit } })
  },

  // 播放播放列表
  playPlaylist(id) {
    return apiClient.post(`/playlists/${id}/play`)
  }
}
