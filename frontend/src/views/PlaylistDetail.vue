<template>
  <div class="playlist-detail-page">
    <el-header height="60px" class="navbar">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-icon">🎵</span>
        <span class="logo-text">Fly Music</span>
      </div>
      <el-menu :default-active="activeIndex" mode="horizontal" class="nav-menu" :router="true">
        <el-menu-item index="/" route="/">首页</el-menu-item>
        <el-menu-item index="/songs" route="/songs">音乐</el-menu-item>
        <el-menu-item index="/playlists" route="/playlists">歌单</el-menu-item>
        <el-menu-item index="/recommendations" route="/recommendations">推荐</el-menu-item>
        <el-menu-item index="/ai-lab" route="/ai-lab">AI实验室</el-menu-item>
        <el-menu-item index="/social" route="/social">社交</el-menu-item>
      </el-menu>
      <div class="user-menu">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar || defaultAvatar">
                {{ userStore.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="$router.push('/login')">登录</el-button>
        </template>
      </div>
    </el-header>

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
            <span><el-icon><VideoCamera /></el-icon> {{ formatPlayCount(playlist.playCount) }} 次播放</span>
            <span><el-icon><Collection /></el-icon> {{ playlist.songCount || 0 }} 首歌</span>
            <span><el-icon><Clock /></el-icon> 创建于 {{ playlist.createdAt || '未知' }}</span>
          </div>
          <div class="playlist-actions">
            <el-button type="primary" size="large" round @click="playAll">
              <el-icon><VideoPlay /></el-icon> 播放全部
            </el-button>
            <el-button size="large" round>
              <el-icon><FolderAdd /></el-icon> 收藏
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
import { playlistAPI } from '../api'
import { Search, VideoPlay, VideoCamera, Collection, Clock, FolderAdd, Share, Plus, Delete } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('3')
const searchKeyword = ref('')
const defaultCover = 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400'
const defaultAvatar = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'

const playlist = ref({
  name: '',
  description: '',
  cover: '',
  playCount: 0,
  songCount: 0,
  createdAt: ''
})

const songs = ref([
  { id: 1, title: '孤勇者', duration: 240, cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=100', artists: [{ name: '陈奕迅' }], album: { title: '孤勇者' } },
  { id: 2, title: '起风了', duration: 310, cover: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=100', artists: [{ name: '买辣椒也用券' }], album: { title: '起风了' } },
  { id: 3, title: '海阔天空', duration: 280, cover: 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=100', artists: [{ name: 'Beyond' }], album: { title: '海阔天空' } },
  { id: 4, title: '平凡之路', duration: 295, cover: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=100', artists: [{ name: '朴树' }], album: { title: '平凡之路' } },
  { id: 5, title: '晴天', duration: 265, cover: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=100', artists: [{ name: '周杰伦' }], album: { title: '叶惠美' } }
])

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

const loadPlaylist = async () => {
  try {
    const playlistId = route.params.id
    const res = await playlistAPI.getPlaylistById(playlistId)
    if (res.data) {
      playlist.value = res.data
    }
    
    const songsRes = await playlistAPI.getSongsInPlaylist(playlistId)
    if (songsRes.data) {
      songs.value = songsRes.data
    }
  } catch (error) {
    console.error('加载播放列表失败:', error)
    playlist.value = {
      name: '经典怀旧',
      description: '那些年我们一起听过的经典歌曲，每一首都值得回味',
      cover: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400',
      playCount: 125680,
      songCount: songs.value.length,
      createdAt: '2024-01-15'
    }
  }
}

onMounted(() => {
  userStore.init()
  loadPlaylist()
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
