import apiClient from './axios'

export default {
  // 关注用户
  followUser(followerId, followedId) {
    return apiClient.post('/social/follow', { followerId, followedId })
  },

  // 取消关注
  unfollowUser(followerId, followedId) {
    return apiClient.delete('/social/follow', { data: { followerId, followedId } })
  },

  // 获取用户粉丝
  getFollowers(userId) {
    return apiClient.get(`/social/followers/${userId}`)
  },

  // 获取用户关注列表
  getFollowing(userId) {
    return apiClient.get(`/social/following/${userId}`)
  },

  // 统计粉丝数
  countFollowers(userId) {
    return apiClient.get(`/social/followers/${userId}/count`)
  },

  // 统计关注数
  countFollowing(userId) {
    return apiClient.get(`/social/following/${userId}/count`)
  },

  // 检查是否已关注
  checkFollow(followerId, followedId) {
    return apiClient.get('/social/check-follow', { params: { followerId, followedId } })
  },

  // 发布动态
  createPost(postData) {
    return apiClient.post('/social/post', postData)
  },

  // 获取用户动态
  getUserPosts(userId) {
    return apiClient.get(`/social/post/user/${userId}`)
  },

  // 获取关注用户动态
  getFollowingPosts(userId, limit = 20) {
    return apiClient.get(`/social/post/following/${userId}`, { params: { limit } })
  },

  // 获取热门动态
  getPopularPosts(limit = 20) {
    return apiClient.get('/social/post/popular', { params: { limit } })
  },

  // 点赞动态
  likePost(postId) {
    return apiClient.post(`/social/post/${postId}/like`)
  }
}
