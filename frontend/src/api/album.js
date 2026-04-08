import apiClient from './axios'

export default {
  // 获取所有专辑
  getAllAlbums() {
    return apiClient.get('/albums')
  },

  // 获取最新专辑
  getLatestAlbums(limit) {
    return apiClient.get('/albums/latest', { params: { limit } })
  },

  // 根据ID获取专辑
  getAlbumById(id) {
    return apiClient.get(`/albums/${id}`)
  },

  // 创建专辑
  createAlbum(albumData) {
    return apiClient.post('/albums', albumData)
  },

  // 更新专辑
  updateAlbum(id, albumData) {
    return apiClient.put(`/albums/${id}`, albumData)
  },

  // 删除专辑
  deleteAlbum(id) {
    return apiClient.delete(`/albums/${id}`)
  },

  // 搜索专辑
  searchAlbums(keyword) {
    return apiClient.get('/albums/search', { params: { keyword } })
  },

  // 获取专辑中的歌曲
  getAlbumSongs(albumId) {
    return apiClient.get(`/albums/${albumId}/songs`)
  },

  // 获取艺术家的专辑
  getAlbumsByArtist(artistId) {
    return apiClient.get(`/albums/artist/${artistId}`)
  }
}
