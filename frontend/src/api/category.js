import apiClient from './axios'

export default {
  // 获取所有分类
  getAllCategories() {
    return apiClient.get('/categories')
  },

  // 根据ID获取分类
  getCategoryById(id) {
    return apiClient.get(`/categories/${id}`)
  },

  // 创建分类
  createCategory(categoryData) {
    return apiClient.post('/categories', categoryData)
  },

  // 更新分类
  updateCategory(id, categoryData) {
    return apiClient.put(`/categories/${id}`, categoryData)
  },

  // 删除分类
  deleteCategory(id) {
    return apiClient.delete(`/categories/${id}`)
  },

  // 搜索分类
  searchCategories(keyword) {
    return apiClient.get('/categories/search', { params: { keyword } })
  },

  // 获取分类下的歌曲
  getCategorySongs(categoryId) {
    return apiClient.get(`/categories/${categoryId}/songs`)
  }
}
