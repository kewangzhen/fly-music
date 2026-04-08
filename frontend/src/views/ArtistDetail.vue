<template>
  <div class="artist-detail-container">
    <div class="artist-header" v-if="artist">
      <div class="artist-cover">
        <img :src="artist.cover || artist.avatar || defaultAvatar" :alt="artist.name" />
      </div>
      <div class="artist-info">
        <h1 class="artist-name">{{ artist.name }}</h1>
        <div class="artist-stats">
          <span class="stat-item">
            <el-icon><User /></el-icon>
            {{ formatNumber(artist.fanCount) }} 粉丝
          </span>
          <span class="stat-item">
            <el-icon><Headset /></el-icon>
            {{ artist.songCount }} 首歌曲
          </span>
          <span class="stat-item">
            <el-icon><FolderOpened /></el-icon>
            {{ artist.albumCount }} 张专辑
          </span>
        </div>
        <p class="artist-desc" v-if="artist.description">{{ artist.description }}</p>
        <div class="artist-actions">
          <el-button type="primary" @click="followArtist" v-if="!isFollowing">
            <el-icon><Plus /></el-icon>
            关注
          </el-button>
          <el-button @click="unfollowArtist" v-else>
            已关注
          </el-button>
        </div>
      </div>
    </div>
    
    <el-tabs v-model="activeTab" class="artist-tabs" v-if="artist">
      <el-tab-pane label="热门歌曲" name="songs">
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
              <span class="song-album">{{ song.album?.name || '未知专辑' }}</span>
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
      </el-tab-pane>
      
      <el-tab-pane label="所有专辑" name="albums">
        <div class="albums-grid">
          <div 
            class="album-card" 
            v-for="album in albums" 
            :key="album.id"
            @click="viewAlbum(album)"
          >
            <img :src="album.cover || defaultCover" class="album-cover" />
            <div class="album-info">
              <h4 class="album-title">{{ album.title }}</h4>
              <p class="album-date">{{ album.releaseDate }}</p>
            </div>
          </div>
          <el-empty v-if="!albums.length" description="暂无专辑" />
        </div>
      </el-tab-pane>
    </el-tabs>
    
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
import { User, Headset, FolderOpened, Plus, VideoPlay, Star, Loading, ArrowLeft } from '@element-plus/icons-vue'
import axios from 'axios'
import { usePlayerStore } from '../store/player'
import { useUserStore } from '../store/user'
import { DEFAULT_IMAGES } from '../assets/defaultImages'

const router = useRouter()
const route = useRoute()
const playerStore = usePlayerStore()
const userStore = useUserStore()

const defaultAvatar = DEFAULT_IMAGES.artist
const defaultCover = DEFAULT_IMAGES.cover

const artist = ref(null)
const songs = ref([])
const albums = ref([])
const activeTab = ref('songs')
const isFollowing = ref(false)

const getArtistDetail = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/artists/${route.params.id}`)
    if (response.data.code === 200) {
      artist.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取歌手信息失败')
    router.push('/artists')
  }
}

const getArtistSongs = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/artists/${route.params.id}/songs`)
    if (response.data.code === 200) {
      songs.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取歌手歌曲失败', error)
  }
}

const getArtistAlbums = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/artists/${route.params.id}/albums`)
    if (response.data.code === 200) {
      albums.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取歌手专辑失败', error)
  }
}

const followArtist = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  const token = localStorage.getItem('token')
  try {
    const res = await fetch('http://localhost:8080/api/social/artist/follow', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        userId: userStore.user.id,
        artistId: artist.value.id,
        artistName: artist.value.name,
        artistAvatar: artist.value.avatar
      })
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('关注成功')
      isFollowing.value = true
    } else {
      ElMessage.error(data.message || '关注失败')
    }
  } catch (error) {
    ElMessage.error('关注失败')
  }
}

const unfollowArtist = async () => {
  const token = localStorage.getItem('token')
  try {
    await fetch('http://localhost:8080/api/social/artist/follow', {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        userId: userStore.user.id,
        artistId: artist.value.id
      })
    })
    ElMessage.success('取消关注成功')
    isFollowing.value = false
  } catch (error) {
    ElMessage.error('取消关注失败')
  }
}

const playSong = (song) => {
  playerStore.playSong(song)
}

const addToFavorite = async (song) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  ElMessage.info('收藏功能开发中')
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const goBack = () => {
  router.back()
}

const viewAlbum = (album) => {
  router.push(`/album/${album.id}`)
}

const checkFollowStatus = async () => {
  if (!userStore.user?.id) return
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`http://localhost:8080/api/social/artist/check-follow?userId=${userStore.user.id}&artistId=${route.params.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    isFollowing.value = data.data?.following || false
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

onMounted(() => {
  getArtistDetail()
  getArtistSongs()
  getArtistAlbums()
  checkFollowStatus()
})
</script>

<style scoped>
.artist-detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 40px 20px 80px;
}

.artist-header {
  display: flex;
  gap: 40px;
  max-width: 1000px;
  margin: 0 auto 40px;
}

.artist-cover {
  width: 250px;
  height: 250px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.artist-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.artist-info {
  flex: 1;
  color: #fff;
}

.artist-name {
  font-size: 36px;
  margin: 0 0 20px;
}

.artist-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.7);
}

.artist-desc {
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  margin-bottom: 20px;
}

.artist-actions {
  margin-top: 20px;
}

.artist-tabs {
  max-width: 1000px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
}

.songs-list {
  display: flex;
  flex-direction: column;
}

.song-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.song-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

.song-index {
  width: 30px;
  color: #999;
  text-align: center;
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
}

.song-title {
  font-weight: 500;
  color: #333;
}

.song-album {
  font-size: 12px;
  color: #999;
}

.song-duration {
  color: #999;
  font-size: 14px;
}

.song-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.song-item:hover .song-actions {
  opacity: 1;
}

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}

.album-card {
  cursor: pointer;
}

.album-cover {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 8px;
  object-fit: cover;
}

.album-info {
  padding: 10px 0;
}

.album-title {
  font-size: 14px;
  margin: 0 0 5px;
  color: #333;
}

.album-date {
  font-size: 12px;
  color: #999;
  margin: 0;
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
  .artist-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .artist-cover {
    width: 200px;
    height: 200px;
  }
  
  .artist-stats {
    justify-content: center;
    flex-wrap: wrap;
  }
}
</style>
