import apiClient from './axios'

export default {
  // 获取所有艺术家
  getAllArtists() {
    return apiClient.get('/artists')
  },

  // 根据ID获取艺术家
  getArtistById(id) {
    return apiClient.get(`/artists/${id}`)
  },

  // 创建艺术家
  createArtist(artistData) {
    return apiClient.post('/artists', artistData)
  },

  // 更新艺术家
  updateArtist(id, artistData) {
    return apiClient.put(`/artists/${id}`, artistData)
  },

  // 删除艺术家
  deleteArtist(id) {
    return apiClient.delete(`/artists/${id}`)
  },

  // 搜索艺术家
  searchArtists(keyword) {
    return apiClient.get('/artists/search', { params: { keyword } })
  },

  // 获取艺术家的歌曲
  getArtistSongs(artistId) {
    return apiClient.get(`/artists/${artistId}/songs`)
  }
}
