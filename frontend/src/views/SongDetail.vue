<template>
  <div class="song-detail-container">
    <div class="song-detail-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
    </div>
    
    <div class="song-detail-content" v-if="song">
      <div class="song-header">
        <div class="song-cover">
          <img :src="song.cover || defaultCover" alt="封面" />
          <div class="cover-play" @click="playSong">
            <el-icon :size="50"><VideoPlay /></el-icon>
          </div>
        </div>
        
        <div class="song-info">
          <div class="song-tags">
            <el-tag v-if="song.isOriginal" type="success" size="small">原唱</el-tag>
            <el-tag type="info" size="small">{{ song.category?.name || '未知分类' }}</el-tag>
          </div>
          
          <h1 class="song-title">{{ song.title }}</h1>
          
          <div class="song-meta">
            <span class="meta-item">
              <el-icon><Microphone /></el-icon>
              <span 
                v-for="(artist, index) in (song.artists || [])" 
                :key="artist.id"
                class="artist-name-link"
                @click="goToArtistDetail(artist.id)"
              >
                {{ artist.name }}{{ index < song.artists.length - 1 ? ', ' : '' }}
              </span>
              <span v-if="!song.artists?.length">未知歌手</span>
            </span>
            <span class="meta-item">
              <el-icon><FolderOpened /></el-icon>
              {{ song.album?.name || '未知专辑' }}
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              {{ formatDuration(song.duration) }}
            </span>
          </div>
          
          <div class="song-stats">
            <span class="stat-item">
              <el-icon><Headset /></el-icon>
              {{ song.playCount || 0 }} 次播放
            </span>
            <span class="stat-item">
              <el-icon><Download /></el-icon>
              {{ song.downloadCount || 0 }} 次下载
            </span>
            <span class="stat-item">
              <el-icon><Share /></el-icon>
              {{ song.shareCount || 0 }} 次分享
            </span>
          </div>
          
          <div class="song-actions">
            <el-button type="primary" size="large" @click="playSong">
              <el-icon><VideoPlay /></el-icon>
              播放
            </el-button>
            <el-button size="large" @click="addToFavorite">
              <el-icon><Star /></el-icon>
              收藏
            </el-button>
            <el-button size="large" @click="addToPlaylist">
              <el-icon><Plus /></el-icon>
              添加到歌单
            </el-button>
            <el-button size="large" @click="shareSong">
              <el-icon><Share /></el-icon>
              分享
            </el-button>
          </div>
        </div>
      </div>
      
      <el-tabs v-model="activeTab" class="song-tabs">
        <el-tab-pane label="评论" name="comments">
          <div class="comments-section">
            <div class="comment-input">
              <el-input
                v-model="commentContent"
                type="textarea"
                :rows="3"
                placeholder="发表你的评论..."
                maxlength="500"
                show-word-limit
              />
              <el-button type="primary" @click="submitComment">发表评论</el-button>
            </div>
            
            <div class="comments-list" v-if="comments.length">
              <div class="comment-item" v-for="comment in comments" :key="comment.id">
                <el-avatar :size="40">{{ comment.user?.username?.charAt(0) || 'U' }}</el-avatar>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-user">{{ comment.user?.username || '用户' }}</span>
                    <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                </div>
              </div>
            </div>
            <el-empty v-else description="还没有评论，快来抢沙发吧！" />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="歌词" name="lyrics">
          <div class="lyrics-section">
            <div v-if="song.lyrics" class="lyrics-content">
              <pre>{{ song.lyrics }}</pre>
            </div>
            <el-empty v-else description="暂无歌词" />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="相似歌曲" name="similar">
          <div class="similar-songs">
            <el-empty description="暂无相似歌曲" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <div class="loading" v-else>
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    
    <div class="back-button">
      <el-button circle @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay, Microphone, FolderOpened, Clock, Headset, Download, Share, Star, Plus, Loading, ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'
import { usePlayerStore } from '../store/player'
import { useUserStore } from '../store/user'
import { DEFAULT_IMAGES } from '../assets/defaultImages'

const defaultCover = DEFAULT_IMAGES.cover

const router = useRouter()
const route = useRoute()
const playerStore = usePlayerStore()
const userStore = useUserStore()

const song = ref(null)
const activeTab = ref('comments')
const commentContent = ref('')
const comments = ref([])

const getSongDetail = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/songs/${route.params.id}`)
    if (response.data.code === 200) {
      song.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取歌曲详情失败')
    router.push('/songs')
  }
}

const getComments = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/comments/song/${route.params.id}`)
    if (response.data.code === 200) {
      comments.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取评论失败', error)
  }
}

const playSong = () => {
  if (song.value) {
    playerStore.playSong(song.value)
  }
}

const addToFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const response = await axios.post(
      'http://localhost:8080/api/favorites',
      { songId: song.value.id },
      {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      }
    )
    if (response.data.code === 200) {
      ElMessage.success('收藏成功')
    } else {
      ElMessage.warning(response.data.message || '收藏失败')
    }
  } catch (error) {
    ElMessage.error('收藏失败')
  }
}

const addToPlaylist = () => {
  ElMessage.info('功能开发中')
}

const shareSong = () => {
  ElMessage.info('分享功能开发中')
}

const submitComment = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  try {
    const response = await axios.post(
      'http://localhost:8080/api/comments',
      {
        songId: song.value.id,
        content: commentContent.value
      },
      {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      }
    )
    if (response.data.code === 200) {
      ElMessage.success('评论成功')
      commentContent.value = ''
      getComments()
    }
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const goBack = () => {
  router.back()
}

const goToArtistDetail = (id) => {
  router.push(`/artist/${id}`)
}

onMounted(() => {
  getSongDetail()
  getComments()
})
</script>

<style scoped>
.song-detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  padding: 40px 20px 80px;
}

.song-detail-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  pointer-events: none;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.shape-1 {
  width: 600px;
  height: 600px;
  background: #409eff;
  top: -200px;
  right: -200px;
}

.shape-2 {
  width: 500px;
  height: 500px;
  background: #67c23a;
  bottom: -200px;
  left: -200px;
}

.song-detail-content {
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.song-header {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

.song-cover {
  width: 280px;
  height: 280px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.song-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-play {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
}

.song-cover:hover .cover-play {
  opacity: 1;
}

.cover-play .el-icon {
  color: #fff;
}

.song-info {
  flex: 1;
  color: #fff;
}

.song-tags {
  margin-bottom: 15px;
  display: flex;
  gap: 8px;
}

.song-title {
  font-size: 32px;
  font-weight: bold;
  margin: 0 0 20px;
}

.song-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.7);
}

.artist-name-link {
  cursor: pointer;
  transition: color 0.2s;
}

.artist-name-link:hover {
  color: #409eff;
}

.song-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.song-actions {
  display: flex;
  gap: 15px;
}

.song-tabs {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
}

.comments-section {
  padding: 10px 0;
}

.comment-input {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.comment-input .el-textarea {
  flex: 1;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-user {
  font-weight: bold;
  color: #333;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-text {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.lyrics-content {
  padding: 20px;
}

.lyrics-content pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  font-size: 16px;
  line-height: 2;
  color: #333;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #fff;
}

.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
}

@media (max-width: 768px) {
  .song-header {
    flex-direction: column;
    align-items: center;
  }
  
  .song-cover {
    width: 200px;
    height: 200px;
  }
  
  .song-title {
    font-size: 24px;
    text-align: center;
  }
  
  .song-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
