<template>
  <div class="playlist-detail-page">
    <div class="playlist-header">
      <div class="header-bg" :style="{ backgroundImage: `url(${playlist.cover || defaultCover})` }"></div>
      <div class="header-content">
        <div class="playlist-cover-wrapper">
          <img :src="playlist.cover || defaultCover" alt="playlist" class="playlist-cover">
          <div class="cover-overlay">
            <el-button circle size="large" type="primary" class="play-all-btn">
              <el-icon size="32"><VideoPlay /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="playlist-info">
          <div class="playlist-tags">
            <el-tag type="primary" effect="dark">歌单</el-tag>
          </div>
          <h1 class="playlist-name">{{ playlist.name || '加载中...' }}</h1>
          <p class="playlist-desc">{{ playlist.description || '暂无描述' }}</p>
          <div class="playlist-stats">
            <span><el-icon><Headset /></el-icon> {{ formatPlayCount(playlist.playCount) }} 次播放</span>
            <span><el-icon><Collection /></el-icon> {{ playlist.songCount || 0 }} 首歌</span>
            <span><el-icon><Clock /></el-icon> 创建于 {{ playlist.createdAt || '未知' }}</span>
          </div>
          <div class="playlist-actions">
            <el-button type="primary" size="large" round @click="playAll">
              <el-icon><VideoPlay /></el-icon> 播放全部
            </el-button>
            <el-button size="large" round :type="isFavorited ? 'default' : 'primary'" @click="toggleFavorite">
              <el-icon><Star /></el-icon> {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button size="large" round>
              <el-icon><Share /></el-icon> 分享
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-main class="main-content">
      <div class="song-list-header">
        <h2 class="section-title">歌曲列表</h2>
        <div class="header-right">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索歌曲..."
            class="search-input"
            :prefix-icon="Search"
            clearable
          />
        </div>
      </div>

      <el-table 
        :data="filteredSongs" 
        stripe 
        class="song-table"
        @row-click="playSong"
      >
        <el-table-column type="index" width="60" label="">
          <template #default="scope">
            <div class="song-index">
              <span class="index-num">{{ scope.$index + 1 }}</span>
              <el-icon class="play-icon"><VideoPlay /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="歌曲名称" min-width="200">
          <template #default="scope">
            <div class="song-title-cell">
              <img :src="scope.row.cover || defaultCover" class="song-thumb">
              <div class="song-info">
                <span class="song-title">{{ scope.row.title }}</span>
                <span class="song-artist">{{ getArtistNames(scope.row) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="album" label="专辑" width="200">
          <template #default="scope">
            {{ scope.row.album?.title || '未知专辑' }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="scope">
            {{ formatDuration(scope.row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-button text type="primary" @click.stop="playSong(scope.row)">
                <el-icon><VideoPlay /></el-icon>
              </el-button>
              <el-button text type="primary" @click.stop="addToPlaylist(scope.row)">
                <el-icon><Plus /></el-icon>
              </el-button>
              <el-button text type="danger" @click.stop="removeSong(scope.row)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredSongs.length === 0" description="暂无歌曲" />
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { playlistAPI } from '../api'
import { Search, VideoPlay, Headset, Collection, Clock, FolderAdd, Share, Plus, Delete, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('3')
const searchKeyword = ref('')
const defaultCover = DEFAULT_IMAGES.cover
const defaultAvatar = DEFAULT_IMAGES.avatar

const playlist = ref({
  name: '',
  description: '',
  cover: '',
  playCount: 0,
  songCount: 0,
  createdAt: ''
})

const songs = ref([])
const isFavorited = ref(false)

const filteredSongs = computed(() => {
  if (!searchKeyword.value) return songs.value
  return songs.value.filter(song => 
    song.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
    song.artists.some(a => a.name.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  )
})

const getArtistNames = (song) => {
  if (song.artists && song.artists.length > 0) {
    return song.artists.map(a => a.name).join(', ')
  }
  return '未知艺术家'
}

const formatPlayCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count
}

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

const playAll = () => {
  if (songs.value.length > 0) {
    playSong(songs.value[0])
  }
}

const playSong = (song) => {
  playerStore.playSong(song)
}

const addToPlaylist = (song) => {
  console.log('添加到播放列表:', song)
}

const removeSong = (song) => {
  console.log('从播放列表移除:', song)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const toggleFavorite = async () => {
  if (!userStore.user?.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  const token = localStorage.getItem('token')
  const playlistId = route.params.id
  try {
    if (isFavorited.value) {
      await fetch(`http://localhost:8080/api/playlists/${playlistId}/favorite`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ userId: userStore.user.id })
      })
      ElMessage.success('取消收藏成功')
      isFavorited.value = false
    } else {
      await fetch(`http://localhost:8080/api/playlists/${playlistId}/favorite`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ userId: userStore.user.id })
      })
      ElMessage.success('收藏成功')
      isFavorited.value = true
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const checkFavoriteStatus = async () => {
  if (!userStore.user?.id) return
  const token = localStorage.getItem('token')
  const playlistId = route.params.id
  try {
    const res = await fetch(`http://localhost:8080/api/playlists/${playlistId}/favorite/check?userId=${userStore.user.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    isFavorited.value = data.data?.favorited || false
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

const loadPlaylist = async () => {
  const token = localStorage.getItem('token')
  try {
    const playlistId = route.params.id
    const res = await fetch(`http://localhost:8080/api/playlists/${playlistId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200 && data.data) {
      playlist.value = data.data
    }
    
    const songsRes = await fetch(`http://localhost:8080/api/playlists/${playlistId}/songs`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const songsData = await songsRes.json()
    if (songsData.code === 200) {
      songs.value = songsData.data || []
    }
  } catch (error) {
    console.error('加载播放列表失败:', error)
  }
}

onMounted(async () => {
  await userStore.init()
  loadPlaylist()
  checkFavoriteStatus()
})
</script>

<style scoped>
.playlist-detail-page {
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

.playlist-header {
  position: relative;
  padding: 60px;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  filter: blur(30px);
  opacity: 0.4;
  transform: scale(1.2);
}

.header-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 0%, var(--dark-bg) 100%);
}

.header-content {
  position: relative;
  display: flex;
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.playlist-cover-wrapper {
  position: relative;
  flex-shrink: 0;
}

.playlist-cover {
  width: 240px;
  height: 240px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
}

.playlist-cover-wrapper:hover .cover-overlay {
  opacity: 1;
}

.play-all-btn {
  transform: scale(1.5);
}

.playlist-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.playlist-tags {
  margin-bottom: 12px;
}

.playlist-name {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
}

.playlist-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.6;
}

.playlist-stats {
  display: flex;
  gap: 24px;
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 24px;
}

.playlist-stats span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.playlist-actions {
  display: flex;
  gap: 16px;
}

.main-content {
  flex: 1;
  padding: 20px 60px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.song-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
}

.search-input {
  width: 200px;
}

.song-table {
  border-radius: 12px;
  overflow: hidden;
}

.song-index {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.index-num {
  color: var(--text-secondary);
}

.play-icon {
  opacity: 0;
  color: var(--primary-color);
  transition: opacity 0.3s;
}

.el-table__row:hover .play-icon {
  opacity: 1;
}

.el-table__row:hover .index-num {
  display: none;
}

.song-title-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.song-thumb {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
}

.song-info {
  display: flex;
  flex-direction: column;
}

.song-title {
  font-weight: 500;
}

.song-artist {
  font-size: 12px;
  color: var(--text-secondary);
}

.table-actions {
  display: flex;
  gap: 8px;
}
</style>
