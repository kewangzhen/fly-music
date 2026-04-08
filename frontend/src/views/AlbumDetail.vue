<template>
  <div class="album-detail-container">
    <div class="album-header" v-if="album">
      <div class="album-cover">
        <img :src="album.cover || defaultCover" :alt="album.title" />
      </div>
      <div class="album-info">
        <h1 class="album-title">{{ album.title }}</h1>
        <div class="album-stats">
          <span class="stat-item">
            <el-icon><User /></el-icon>
            {{ album.artist?.name || '未知艺术家' }}
          </span>
          <span class="stat-item">
            <el-icon><FolderOpened /></el-icon>
            {{ album.songCount || 0 }} 首歌曲
          </span>
          <span class="stat-item">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(album.releaseDate) }}
          </span>
        </div>
        <p class="album-desc" v-if="album.description">{{ album.description }}</p>
        <div class="album-actions">
          <el-button type="primary" @click="playAll">
            <el-icon><VideoPlay /></el-icon>
            播放全部
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="songs-section" v-if="album">
      <h3 class="section-title">歌曲列表</h3>
      <div class="songs-list">
        <div 
          class="song-item" 
          v-for="(song, index) in songs" 
          :key="song.id"
          @click="playSong(song)"
        >
          <span class="song-index">{{ index + 1 }}</span>
          <img :src="song.cover || defaultCover" class="song-cover" />
          <div class="song-info">
            <span class="song-title">{{ song.title }}</span>
            <span class="song-artist">{{ getArtistNames(song) }}</span>
          </div>
          <span class="song-duration">{{ formatDuration(song.duration) }}</span>
          <div class="song-actions">
            <el-button circle size="small" @click.stop="playSong(song)">
              <el-icon><VideoPlay /></el-icon>
            </el-button>
            <el-button circle size="small" @click.stop="addToFavorite(song)">
              <el-icon><Star /></el-icon>
            </el-button>
          </div>
        </div>
        <el-empty v-if="!songs.length" description="暂无歌曲" />
      </div>
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
import { User, FolderOpened, Calendar, VideoPlay, Star, Loading, ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'
import { usePlayerStore } from '../store/player'
import { useUserStore } from '../store/user'
import { DEFAULT_IMAGES } from '../assets/defaultImages'

const router = useRouter()
const route = useRoute()
const playerStore = usePlayerStore()
const userStore = useUserStore()

const defaultCover = DEFAULT_IMAGES.cover
const defaultAvatar = DEFAULT_IMAGES.avatar

const album = ref(null)
const songs = ref([])
const isFollowing = ref(false)

const getAlbumDetail = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/albums/${route.params.id}`)
    if (response.data.code === 200) {
      album.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取专辑信息失败')
    router.push('/')
  }
}

const getAlbumSongs = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/albums/${route.params.id}/songs`)
    if (response.data.code === 200) {
      songs.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取专辑歌曲失败', error)
  }
}

const getArtistNames = (song) => {
  if (song.artists && song.artists.length > 0) {
    return song.artists.map(a => a.name).join(', ')
  }
  return '未知艺术家'
}

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min}:${sec.toString().padStart(2, '0')}`
}

const formatDate = (timestamp) => {
  if (!timestamp) return '未知日期'
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const playSong = (song) => {
  playerStore.playSong(song)
}

const playAll = () => {
  if (songs.value.length > 0) {
    playerStore.setPlaylist(songs.value)
    playerStore.playSong(songs.value[0])
  }
}

const addToFavorite = async (song) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const token = localStorage.getItem('token')
    await fetch(`http://localhost:8080/api/favorites`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ songId: song.id })
    })
    ElMessage.success('已添加到收藏')
  } catch (error) {
    ElMessage.error('添加收藏失败')
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  getAlbumDetail()
  getAlbumSongs()
})
</script>

<style scoped>
.album-detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 40px 20px 80px;
}

.album-header {
  display: flex;
  gap: 40px;
  max-width: 1000px;
  margin: 0 auto 40px;
}

.album-cover {
  flex-shrink: 0;
  width: 250px;
  height: 250px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.album-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.album-title {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 16px;
}

.album-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.album-desc {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 20px;
}

.album-actions {
  margin-top: 10px;
}

.songs-section {
  max-width: 1000px;
  margin: 0 auto;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 20px;
}

.songs-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.song-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.song-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.song-index {
  width: 30px;
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
}

.song-cover {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
}

.song-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.song-title {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.song-artist {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

.song-duration {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  min-width: 50px;
}

.song-actions {
  display: flex;
  gap: 8px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: rgba(255, 255, 255, 0.5);
}

.loading p {
  margin-top: 16px;
}

.back-button {
  position: fixed;
  top: 100px;
  left: 20px;
}

.back-button .el-button {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: #fff;
}

.back-button .el-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

@media (max-width: 768px) {
  .album-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .album-cover {
    width: 200px;
    height: 200px;
  }
  
  .album-stats {
    justify-content: center;
    flex-wrap: wrap;
  }
}
</style>
