<template>
  <div class="social-page">
    <el-main class="main-content">
      <div class="page-container">
        <div class="left-sidebar">
          <el-card class="user-card">
            <div class="user-profile">
              <el-avatar :size="80" :src="currentUser.avatar" class="profile-avatar">
                {{ currentUser.username?.charAt(0) }}
              </el-avatar>
              <h3 class="profile-name">{{ currentUser.username }}</h3>
              <p class="profile-bio">{{ currentUser.bio || '还没有个人简介' }}</p>
              <div class="profile-stats">
                <div class="stat-item">
                  <span class="stat-value">{{ currentUser.followers }}</span>
                  <span class="stat-label">粉丝</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ currentUser.following }}</span>
                  <span class="stat-label">关注</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ currentUser.posts }}</span>
                  <span class="stat-label">动态</span>
                </div>
              </div>
            </div>
          </el-card>

          <el-card class="recommend-card">
            <template #header>
              <div class="card-header">
                <span>关注的歌手</span>
              </div>
            </template>
            <div class="recommend-list">
              <div v-for="artist in followingArtists" :key="artist.id" class="recommend-item">
                <el-avatar :size="40" :src="artist.artistAvatar || DEFAULT_IMAGES.avatar">{{ artist.artistName?.charAt(0) }}</el-avatar>
                <div class="recommend-info">
                  <span class="recommend-name">{{ artist.artistName }}</span>
                </div>
                <el-button 
                  size="small" 
                  round 
                  type="default"
                  @click="handleFollowArtist(artist)"
                >
                  已关注
                </el-button>
              </div>
              <div v-if="followingArtists.length === 0" class="empty-state">
                <p>暂无关注的歌手</p>
              </div>
            </div>
          </el-card>
        </div>

        <div class="center-content">
          <el-card class="create-post-card">
            <div class="create-post">
              <el-avatar :size="48" :src="currentUser.avatar">{{ currentUser.username?.charAt(0) }}</el-avatar>
              <div class="post-input" @click="showPostDialog">
                <span>分享你的音乐心情...</span>
              </div>
            </div>
            <div class="create-post-actions">
              <el-button text>
                <el-icon><Picture /></el-icon> 图片
              </el-button>
              <el-button text>
                <el-icon><Headset /></el-icon> 音乐
              </el-button>
              <el-button text>
                <el-icon><Location /></el-icon> 位置
              </el-button>
            </div>
          </el-card>

          <div class="posts-section">
            <div class="section-header">
              <h2>最新动态</h2>
            </div>
            
            <div class="posts-list">
              <el-card v-for="post in posts" :key="post.id" class="post-card" shadow="hover">
                <div class="post-header">
                  <el-avatar :size="48" :src="post.user.avatar" class="user-avatar">
                    {{ post.user.username.charAt(0) }}
                  </el-avatar>
                  <div class="user-info">
                    <h4>{{ post.user.username }}</h4>
                    <p>{{ formatDate(post.createdAt) }}</p>
                  </div>
                  <el-button text class="more-btn">
                    <el-icon><More /></el-icon>
                  </el-button>
                </div>
                
                <div class="post-content">
                  <p>{{ post.content }}</p>
                  <div v-if="post.song" class="post-song-card" @click="playSong(post.song)">
                    <img :src="post.song.cover" class="song-cover">
                    <div class="song-info">
                      <span class="song-title">{{ post.song.title }}</span>
                      <span class="song-artist">{{ post.song.artist }}</span>
                    </div>
                    <el-button circle type="primary" class="play-btn">
                      <el-icon><VideoPlay /></el-icon>
                    </el-button>
                  </div>
                  <div v-if="post.images && post.images.length > 0" class="post-images">
                    <img v-for="(img, idx) in post.images" :key="idx" :src="img" class="post-image">
                  </div>
                </div>
                
                <div class="post-actions">
                  <div class="action-item" :class="{ active: post.isLiked }" @click="likePost(post)">
                    <el-icon><Star /></el-icon>
                    <span>{{ post.likes }}</span>
                  </div>
                  <div class="action-item" @click="showComments(post)">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>{{ post.comments.length }}</span>
                  </div>
                  <div class="action-item">
                    <el-icon><Share /></el-icon>
                    <span>分享</span>
                  </div>
                </div>
                
                <div v-if="post.showComments" class="comments-section">
                  <div v-if="post.comments.length > 0" class="comments-list">
                    <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
                      <el-avatar :size="32" :src="comment.user.avatar">{{ comment.user.username.charAt(0) }}</el-avatar>
                      <div class="comment-content">
                        <div class="comment-header">
                          <span class="comment-username">{{ comment.user.username }}</span>
                          <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                        </div>
                        <p class="comment-text">{{ comment.content }}</p>
                      </div>
                    </div>
                  </div>
                  <div class="comment-input">
                    <el-avatar :size="32" :src="currentUser.avatar">{{ currentUser.username?.charAt(0) }}</el-avatar>
                    <el-input
                      v-model="commentInputs[post.id]"
                      placeholder="写下你的评论..."
                      @keyup.enter="submitComment(post)"
                    />
                    <el-button type="primary" @click="submitComment(post)">发送</el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </div>

        <div class="right-sidebar">
          <el-card class="trending-card">
            <template #header>
              <div class="card-header">
                <span>推荐关注</span>
              </div>
            </template>
            <div class="trending-list">
              <div v-for="user in recommendedUsers" :key="user.id" class="trending-item">
                <el-avatar :size="36" :src="user.avatar">{{ user.username?.charAt(0) }}</el-avatar>
                <div class="topic-info">
                  <span class="topic-name">{{ user.nickname || user.username }}</span>
                  <span class="topic-posts">{{ user.followers || 0 }} 粉丝</span>
                </div>
                <el-button 
                  size="small" 
                  round 
                  :type="user.isFollowing ? 'default' : 'primary'"
                  @click="handleFollow(user)"
                >
                  {{ user.isFollowing ? '已关注' : '关注' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>

    <el-dialog v-model="postDialogVisible" title="发布动态" width="600px" class="post-dialog">
      <el-form :model="postForm" label-position="top">
        <el-form-item label="">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="4"
            placeholder="分享你的音乐心情..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="关联音乐（可选）">
          <el-select v-model="postForm.songId" placeholder="选择歌曲" filterable clearable class="song-select">
            <el-option
              v-for="song in songOptions"
              :key="song.id"
              :label="song.title + ' - ' + song.artist"
              :value="song.id"
            >
              <div class="song-option">
                <img :src="song.cover" class="option-cover">
                <span>{{ song.title }} - {{ song.artist }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPost" :disabled="!postForm.content">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { ElMessage } from 'element-plus'
import { Edit, Star, ChatDotRound, Share, Picture, Headset, Location, More, VideoPlay } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('6')
const defaultAvatar = DEFAULT_IMAGES.avatar

const postDialogVisible = ref(false)
const postForm = reactive({
  content: '',
  songId: null
})
const commentInputs = ref({})

const currentUser = ref({
  username: '音乐爱好者',
  avatar: DEFAULT_IMAGES.avatar,
  bio: '热爱音乐，分享好歌',
  followers: 0,
  following: 0,
  posts: 0
})

const loadCurrentUserStats = async () => {
  if (!userStore.user?.id) return
  const token = localStorage.getItem('token')
  try {
    const [followersRes, followingRes, postsRes] = await Promise.all([
      fetch(`http://localhost:8080/api/social/followers/${userStore.user.id}/count`, { headers: { Authorization: `Bearer ${token}` } }),
      fetch(`http://localhost:8080/api/social/following/${userStore.user.id}/count`, { headers: { Authorization: `Bearer ${token}` } }),
      fetch(`http://localhost:8080/api/social/post/user/${userStore.user.id}`, { headers: { Authorization: `Bearer ${token}` } })
    ])
    const followersData = await followersRes.json()
    const followingData = await followingRes.json()
    const postsData = await postsRes.json()
    
    currentUser.value.followers = followersData.data?.count || 0
    currentUser.value.following = followingData.data?.count || 0
    currentUser.value.posts = postsData.data?.length || 0
    currentUser.value.username = userStore.user.username
    currentUser.value.avatar = userStore.user.avatar
    currentUser.value.bio = userStore.user.description || '热爱音乐，分享好歌'
  } catch (error) {
    console.error('加载用户数据失败:', error)
  }
}

const loadRecommendedUsers = async () => {
  if (!userStore.user?.id) return
  const token = localStorage.getItem('token')
  try {
    const res = await fetch('http://localhost:8080/api/users', {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      const users = (data.data || []).filter(u => u.id !== userStore.user.id).slice(0, 10)
      for (const user of users) {
        const followRes = await fetch(`http://localhost:8080/api/social/check-follow?followerId=${userStore.user.id}&followedId=${user.id}`, {
          headers: { Authorization: `Bearer ${token}` }
        })
        const followData = await followRes.json()
        user.isFollowing = followData.data?.following || false
      }
      recommendedUsers.value = users
    }
  } catch (error) {
    console.error('加载推荐用户失败:', error)
  }
}

const followingArtists = ref([])

const loadFollowingArtists = async () => {
  if (!userStore.user?.id) return
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`http://localhost:8080/api/social/artist/following/${userStore.user.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      followingArtists.value = data.data || []
    }
  } catch (error) {
    console.error('加载关注歌手失败:', error)
  }
}

const handleFollowArtist = async (artist) => {
  if (!userStore.user?.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  const token = localStorage.getItem('token')
  try {
    if (artist.isFollowing) {
      await fetch('http://localhost:8080/api/social/artist/follow', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          userId: userStore.user.id,
          artistId: artist.artistId
        })
      })
      ElMessage.success('取消关注成功')
      artist.isFollowing = false
      followingArtists.value = followingArtists.value.filter(a => a.artistId !== artist.artistId)
    } else {
      await fetch('http://localhost:8080/api/social/artist/follow', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          userId: userStore.user.id,
          artistId: artist.artistId,
          artistName: artist.artistName,
          artistAvatar: artist.artistAvatar
        })
      })
      ElMessage.success('关注成功')
      artist.isFollowing = true
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const recommendedUsers = ref([
  { id: 1, username: '周杰伦', avatar: DEFAULT_IMAGES.avatar, followers: 10000 },
  { id: 2, username: '林俊杰', avatar: DEFAULT_IMAGES.avatar, followers: 8000 },
  { id: 3, username: '邓紫棋', avatar: DEFAULT_IMAGES.avatar, followers: 6000 }
])

const trendingTopics = ref([
  { id: 1, rank: 1, name: '新歌推荐', posts: 12500 },
  { id: 2, rank: 2, name: '周杰伦新专辑', posts: 9800 },
  { id: 3, rank: 3, name: 'AI音乐', posts: 7500 },
  { id: 4, rank: 4, name: '校园歌手', posts: 5200 },
  { id: 5, rank: 5, name: '经典老歌', posts: 4100 }
])

const songOptions = ref([
  { id: 1, title: '孤勇者', artist: '陈奕迅', cover: DEFAULT_IMAGES.cover },
  { id: 2, title: '起风了', artist: '买辣椒也用券', cover: DEFAULT_IMAGES.cover },
  { id: 3, title: '晴天', artist: '周杰伦', cover: DEFAULT_IMAGES.cover }
])

const posts = ref([])

const loadPosts = async () => {
  const token = localStorage.getItem('token')
  try {
    const res = await fetch('http://localhost:8080/api/social/post/all?page=0&size=20', {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      const postList = data.data?.posts || []
      for (const post of postList) {
        post.showComments = false
        if (userStore.user?.id) {
          const likeRes = await fetch(`http://localhost:8080/api/social/post/${post.id}/like/status?userId=${userStore.user.id}`, {
            headers: { Authorization: `Bearer ${token}` }
          })
          const likeData = await likeRes.json()
          post.isLiked = likeData.data?.liked || false
          post.likes = likeData.data?.likes || post.likes
        }
      }
      posts.value = postList
    }
  } catch (error) {
    console.error('加载动态失败:', error)
  }
}

const formatDate = (date) => {
  const now = new Date()
  const diff = now - new Date(date)
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return new Date(date).toLocaleDateString()
}

const showPostDialog = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  postDialogVisible.value = true
}

const submitPost = async () => {
  if (!postForm.content || !userStore.user?.id) return
  
  const token = localStorage.getItem('token')
  try {
    const res = await fetch('http://localhost:8080/api/social/post', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        userId: userStore.user.id,
        content: postForm.content,
        songId: postForm.songId
      })
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('发布成功')
      postDialogVisible.value = false
      postForm.content = ''
      postForm.songId = null
      loadPosts()
    } else {
      ElMessage.error(data.message || '发布失败')
    }
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const likePost = async (post) => {
  if (!userStore.user?.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  const token = localStorage.getItem('token')
  try {
    if (post.isLiked) {
      await fetch(`http://localhost:8080/api/social/post/${post.id}/like?userId=${userStore.user.id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` }
      })
      post.isLiked = false
      post.likes = Math.max(0, post.likes - 1)
    } else {
      await fetch(`http://localhost:8080/api/social/post/${post.id}/like`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ userId: userStore.user.id })
      })
      post.isLiked = true
      post.likes = (post.likes || 0) + 1
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const showComments = async (post) => {
  post.showComments = !post.showComments
  if (post.showComments && (!post.comments || post.comments.length === 0)) {
    const token = localStorage.getItem('token')
    try {
      const res = await fetch(`http://localhost:8080/api/social/post/${post.id}/comments`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        post.comments = data.data || []
      }
    } catch (error) {
      console.error('加载评论失败:', error)
    }
  }
}

const submitComment = async (post) => {
  const content = commentInputs.value[post.id]
  if (!content || !userStore.user?.id) return
  
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`http://localhost:8080/api/social/post/${post.id}/comment`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        userId: userStore.user.id,
        content: content
      })
    })
    const data = await res.json()
    if (data.code === 200) {
      if (!post.comments) post.comments = []
      post.comments.push(data.data)
      post.commentsCount = (post.commentsCount || 0) + 1
      commentInputs.value[post.id] = ''
      ElMessage.success('评论成功')
    }
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const followUser = async (user) => {
  if (!userStore.user?.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  const token = localStorage.getItem('token')
  try {
    if (user.isFollowing) {
      await fetch('http://localhost:8080/api/social/follow', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          followerId: userStore.user.id,
          followedId: user.id
        })
      })
      ElMessage.success('取消关注成功')
      user.isFollowing = false
      user.followers = Math.max(0, (user.followers || 1) - 1)
    } else {
      const res = await fetch('http://localhost:8080/api/social/follow', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          followerId: userStore.user.id,
          followedId: user.id
        })
      })
      const data = await res.json()
      if (data.code === 200) {
        ElMessage.success('关注成功')
        user.isFollowing = true
        user.followers = (user.followers || 0) + 1
      } else {
        ElMessage.error(data.message || '关注失败')
      }
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleFollow = (user) => {
  followUser(user)
}

const playSong = (song) => {
  playerStore.playSong(song)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.init()
  loadCurrentUserStats()
  loadPosts()
  loadRecommendedUsers()
  loadFollowingArtists()
})
</script>

<style scoped>
.social-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: rgba(22, 33, 62, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  cursor: pointer;
}

.main-content {
  flex: 1;
  padding: 30px;
}

.page-container {
  display: grid;
  grid-template-columns: 280px 1fr 280px;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.left-sidebar,
.right-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-card {
  position: sticky;
  top: 90px;
}

.user-profile {
  text-align: center;
}

.profile-avatar {
  margin-bottom: 12px;
  border: 3px solid var(--primary-color);
}

.profile-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.profile-bio {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.card-header {
  font-weight: 600;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.recommend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.recommend-name {
  font-weight: 500;
  font-size: 14px;
}

.recommend-fans {
  font-size: 12px;
  color: var(--text-secondary);
}

.center-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.create-post-card {
  border-radius: 12px;
}

.create-post {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.post-input {
  flex: 1;
  padding: 12px 16px;
  background: var(--hover-bg);
  border-radius: 24px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.3s;
}

.post-input:hover {
  background: rgba(102, 126, 234, 0.2);
}

.create-post-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.section-header {
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
}

.post-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-info {
  flex: 1;
}

.user-info h4 {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.user-info p {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
}

.post-content {
  margin-bottom: 12px;
}

.post-content p {
  margin: 0 0 12px 0;
  line-height: 1.6;
}

.post-song-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--hover-bg);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.post-song-card:hover {
  background: rgba(102, 126, 234, 0.15);
}

.song-cover {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
}

.song-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.song-title {
  font-weight: 500;
  font-size: 14px;
}

.song-artist {
  font-size: 12px;
  color: var(--text-secondary);
}

.post-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-top: 12px;
}

.post-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.post-image:hover {
  transform: scale(1.02);
}

.post-actions {
  display: flex;
  gap: 32px;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: color 0.3s;
}

.action-item:hover,
.action-item.active {
  color: var(--primary-color);
}

.comments-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.comment-username {
  font-weight: 500;
  font-size: 13px;
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.comment-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.4;
}

.comment-input {
  display: flex;
  gap: 12px;
  align-items: center;
}

.comment-input .el-input {
  flex: 1;
}

.trending-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trending-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.trending-item:hover {
  transform: translateX(4px);
}

.topic-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.topic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.topic-name {
  font-weight: 500;
  font-size: 14px;
}

.topic-posts {
  font-size: 12px;
  color: var(--text-secondary);
}

.song-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.option-cover {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  object-fit: cover;
}

.song-select {
  width: 100%;
}

@media (max-width: 1200px) {
  .page-container {
    grid-template-columns: 1fr 280px;
  }
  
  .left-sidebar {
    display: none;
  }
}

@media (max-width: 900px) {
  .page-container {
    grid-template-columns: 1fr;
  }
  
  .right-sidebar {
    display: none;
  }
}
</style>
