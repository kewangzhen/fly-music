<template>
  <div class="home-container">
    <el-header height="60px" class="navbar">
      <div class="logo">
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
        <el-input
          v-model="searchKeyword"
          placeholder="搜索音乐、歌手..."
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <template v-if="userStore.isLoggedIn">
          <el-badge :value="3" class="notification-badge">
            <el-icon size="20"><Bell /></el-icon>
          </el-badge>
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.user?.avatar || defaultAvatar">
                {{ userStore.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/profile?tab=favorites')">
                  <el-icon><Star /></el-icon>我的收藏
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/profile?tab=history')">
                  <el-icon><Clock /></el-icon>播放历史
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button class="login-btn" @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" class="register-btn" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    
    <el-main class="main-content">
      <el-carousel :interval="4000" height="400px" class="hero-carousel" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="carousel-item" :style="{ backgroundImage: `url(${item.image})` }" @click="handleBannerClick(item)">
            <div class="carousel-overlay">
              <div class="carousel-content">
                <h2 class="carousel-title">{{ item.title }}</h2>
                <p class="carousel-desc">{{ item.description }}</p>
                <el-button type="primary" round class="carousel-btn">
                  <el-icon><ArrowRight /></el-icon>立即体验
                </el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <div class="quick-actions">
        <div class="action-item" @click="$router.push('/recommendations')">
          <div class="action-icon daily"></div>
          <span>每日推荐</span>
        </div>
        <div class="action-item" @click="$router.push('/songs')">
          <div class="action-icon ranking"></div>
          <span>排行榜</span>
        </div>
        <div class="action-item" @click="$router.push('/ai-lab')">
          <div class="action-icon ai"></div>
          <span>AI音乐</span>
        </div>
        <div class="action-item" @click="$router.push('/playlists')">
          <div class="action-icon playlist"></div>
          <span>歌单</span>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🔥</span>
            热门歌曲
          </h2>
          <el-button text @click="$router.push('/songs')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <el-row :gutter="24">
          <el-col :span="6" v-for="(song, index) in hotSongs" :key="song.id" class="song-card" :style="{ animationDelay: `${index * 0.1}s` }">
            <el-card shadow="hover" @click="playSong(song)">
              <div class="song-cover-wrapper">
                <img :src="song.cover || defaultCover" alt="song" class="song-cover">
                <div class="song-play-overlay">
                  <el-button circle size="large" type="primary" class="play-btn">
                    <el-icon size="24"><VideoPlay /></el-icon>
                  </el-button>
                </div>
                <div class="play-count" v-if="song.playCount">
                  <el-icon><VideoCamera /></el-icon> {{ formatPlayCount(song.playCount) }}
                </div>
              </div>
              <div class="song-info">
                <h4 class="song-title">{{ song.title }}</h4>
                <p class="song-artist">{{ getArtistNames(song) }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">💝</span>
            心动歌单
          </h2>
          <el-button text @click="$router.push('/recommendations')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <el-row :gutter="24">
          <el-col :span="6" v-for="(playlist, index) in heartPlaylists" :key="playlist.id" class="playlist-card" :style="{ animationDelay: `${index * 0.1}s` }">
            <el-card shadow="hover" @click="viewPlaylist(playlist)">
              <div class="playlist-cover-wrapper">
                <img :src="playlist.cover || defaultCover" alt="playlist" class="playlist-cover">
                <div class="playlist-play-overlay">
                  <el-button circle size="large" type="primary" class="play-btn">
                    <el-icon size="24"><VideoPlay /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="playlist-info">
                <h4 class="playlist-name">{{ playlist.name }}</h4>
                <p class="playlist-count">{{ playlist.songCount || 0 }}首歌</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🆕</span>
            最新专辑
          </h2>
          <el-button text>
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <el-row :gutter="24">
          <el-col :span="6" v-for="(album, index) in latestAlbums" :key="album.id" class="album-card" :style="{ animationDelay: `${index * 0.1}s` }">
            <el-card shadow="hover" @click="viewAlbum(album)">
              <div class="album-cover-wrapper">
                <img :src="album.cover || defaultCover" alt="album" class="album-cover">
              </div>
              <div class="album-info">
                <h4 class="album-title">{{ album.title }}</h4>
                <p class="album-artist">{{ album.artist?.name || '未知艺术家' }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="section artists-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🎤</span>
            热门歌手
          </h2>
        </div>
        <el-row :gutter="24">
          <el-col :span="4" v-for="(artist, index) in hotArtists" :key="artist.id" class="artist-card" :style="{ animationDelay: `${index * 0.05}s` }">
            <el-card shadow="hover" @click="viewArtist(artist)">
              <el-avatar :size="80" :src="artist.avatar" class="artist-avatar">
                {{ artist.name?.charAt(0) }}
              </el-avatar>
              <div class="artist-name">{{ artist.name }}</div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>

    <div class="player-bar" v-if="currentSong">
      <div class="player-progress">
        <div class="progress-bar" :style="{ width: `${playProgress}%` }"></div>
      </div>
      <div class="player-content">
        <div class="player-song-info">
          <img :src="currentSong.cover || defaultCover" class="player-cover">
          <div class="player-text">
            <div class="player-title">{{ currentSong.title }}</div>
            <div class="player-artist">{{ getArtistNames(currentSong) }}</div>
          </div>
          <el-button text @click="toggleFavorite">
            <el-icon size="20" :class="{ 'is-favorited': isFavorited }"><Star /></el-icon>
          </el-button>
        </div>
        
        <div class="player-controls">
          <el-button circle text @click="prevSong">
            <el-icon><DArrowLeft /></el-icon>
          </el-button>
          <el-button circle type="primary" @click="togglePlay">
            <el-icon v-if="isPlaying"><VideoPause /></el-icon>
            <el-icon v-else><VideoPlay /></el-icon>
          </el-button>
          <el-button circle text @click="nextSong">
            <el-icon><DArrowRight /></el-icon>
          </el-button>
        </div>
        
        <div class="player-extra">
          <div class="time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</div>
          <el-slider v-model="volume" :min="0" :max="100" class="volume-slider" />
          <el-button text>
            <el-icon><List /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
    
    <el-footer height="200px" class="footer">
      <div class="footer-content">
        <div class="footer-main">
          <div class="footer-brand">
            <span class="logo-icon">🎵</span>
            <span class="logo-text">Fly Music</span>
            <p class="footer-desc">让音乐飞入你心</p>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h4>产品</h4>
              <a href="#">音乐服务</a>
              <a href="#">VIP会员</a>
              <a href="#">AI音乐</a>
            </div>
            <div class="link-group">
              <h4>关于</h4>
              <a href="#">关于我们</a>
              <a href="#">联系方式</a>
              <a href="#">隐私政策</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 Fly Music. All rights reserved.</p>
        </div>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { songAPI, playlistAPI, albumAPI, artistAPI } from '../api'
import { Search, Bell, User, Star, Clock, VideoPlay, VideoPause, VideoCamera, ArrowRight, DArrowLeft, DArrowRight, List, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('1')
const searchKeyword = ref('')
const defaultCover = 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400'
const defaultAvatar = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'

const currentSong = ref(null)
const isPlaying = ref(false)
const isFavorited = ref(false)
const playProgress = ref(30)
const currentTime = ref(120)
const duration = ref(240)
const volume = ref(80)

const banners = ref([
  {
    id: 1,
    title: '发现好音乐',
    description: '探索最热门的歌曲和专辑',
    image: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=1600',
    type: 'popular'
  },
  {
    id: 2,
    title: 'AI音乐实验室',
    description: '用AI创造属于你的音乐',
    image: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=1600',
    type: 'ai-lab'
  },
  {
    id: 3,
    title: '个性化推荐',
    description: '为你量身定制的音乐推荐',
    image: 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=1600',
    type: 'heart'
  }
])

const hotSongs = ref([])
const heartPlaylists = ref([])
const latestAlbums = ref([])
const hotArtists = ref([
  { id: 1, name: '周杰伦', avatar: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=200' },
  { id: 2, name: '林俊杰', avatar: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=200' },
  { id: 3, name: '邓紫棋', avatar: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=200' },
  { id: 4, name: '陈奕迅', avatar: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=200' },
  { id: 5, name: '五月天', avatar: 'https://images.unsplash.com/photo-1501612780327-45045538702b?w=200' },
  { id: 6, name: '薛之谦', avatar: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200' }
])

const getArtistNames = (song) => {
  if (song.artists && song.artists.length > 0) {
    return song.artists.map(a => a.name).join(', ')
  }
  return '未知艺术家'
}

const formatPlayCount = (count) => {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count
}

const formatTime = (seconds) => {
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

const handleBannerClick = (banner) => {
  if (banner.type === 'ai-lab') {
    router.push('/ai-lab')
  } else if (banner.type === 'heart') {
    router.push('/recommendations')
  }
}

const handleSearch = () => {
  if (searchKeyword.value) {
    router.push(`/songs?keyword=${searchKeyword.value}`)
  }
}

const playSong = (song) => {
  currentSong.value = song
  isPlaying.value = true
}

const viewPlaylist = (playlist) => {
  router.push(`/playlists/${playlist.id}`)
}

const viewAlbum = (album) => {
  console.log('查看专辑:', album)
}

const viewArtist = (artist) => {
  console.log('查看歌手:', artist)
}

const togglePlay = () => {
  isPlaying.value = !isPlaying.value
}

const toggleFavorite = () => {
  isFavorited.value = !isFavorited.value
}

const prevSong = () => {
  console.log('上一首')
}

const nextSong = () => {
  console.log('下一首')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const loadData = async () => {
  try {
    const songsRes = await songAPI.getPopularSongs(8)
    hotSongs.value = songsRes.data?.map(s => ({
      ...s,
      cover: s.cover || defaultCover
    })) || []
    
    const playlistsRes = await playlistAPI.getHeartPlaylists(4)
    heartPlaylists.value = playlistsRes.data || []
    
    const albumsRes = await albumAPI.getAllAlbums()
    latestAlbums.value = (albumsRes.data || []).slice(0, 4).map(a => ({
      ...a,
      cover: a.cover || defaultCover
    }))
  } catch (error) {
    console.error('加载数据失败:', error)
    hotSongs.value = [
      { id: 1, title: '孤勇者', playCount: 100000, cover: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400' },
      { id: 2, title: '起风了', playCount: 80000, cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400' },
      { id: 3, title: '海阔天空', playCount: 75000, cover: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400' },
      { id: 4, title: '平凡之路', playCount: 60000, cover: 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=400' }
    ]
    heartPlaylists.value = [
      { id: 1, name: '经典怀旧', songCount: 50, cover: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400' },
      { id: 2, name: '轻松舒缓', songCount: 30, cover: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=400' },
      { id: 3, name: '动感节奏', songCount: 40, cover: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400' },
      { id: 4, name: '睡前必备', songCount: 25, cover: 'https://images.unsplash.com/photo-1501612780327-45045538702b?w=400' }
    ]
    latestAlbums.value = [
      { id: 1, title: '新专辑', artist: { name: '歌手A' }, cover: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400' },
      { id: 2, title: '最新EP', artist: { name: '歌手B' }, cover: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400' },
      { id: 3, title: '创作集', artist: { name: '歌手C' }, cover: 'https://images.unsplash.com/photo-1485579149621-3123dd979885?w=400' },
      { id: 4, title: '精选集', artist: { name: '歌手D' }, cover: 'https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=400' }
    ]
  }
}

onMounted(() => {
  userStore.init()
  loadData()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding-bottom: 80px;
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

.search-input {
  width: 200px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
}

.notification-badge {
  cursor: pointer;
  padding: 8px;
}

.user-info {
  cursor: pointer;
}

.login-btn {
  color: var(--text-secondary);
}

.register-btn {
  background: var(--primary-gradient);
}

.main-content {
  flex: 1;
  padding: 30px 60px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.hero-carousel {
  margin-bottom: 40px;
  border-radius: 20px;
  overflow: hidden;
}

.carousel-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  cursor: pointer;
}

.carousel-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(0, 0, 0, 0.8), transparent);
  display: flex;
  align-items: center;
  padding: 60px;
}

.carousel-content {
  max-width: 500px;
}

.carousel-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  animation: fadeIn 0.8s ease-out;
}

.carousel-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 24px;
  animation: fadeIn 1s ease-out;
}

.carousel-btn {
  animation: fadeIn 1.2s ease-out;
  padding: 12px 32px;
  font-size: 16px;
}

.quick-actions {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 50px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s;
}

.action-item:hover {
  transform: translateY(-5px);
}

.action-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

.action-icon.daily {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.action-icon.ranking {
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
}

.action-icon.ai {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.action-icon.playlist {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.section {
  margin-bottom: 50px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 28px;
}

.song-card,
.playlist-card,
.album-card {
  animation: fadeIn 0.5s ease-out backwards;
}

.song-cover-wrapper,
.playlist-cover-wrapper,
.album-cover-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  margin-bottom: 12px;
}

.song-cover,
.playlist-cover,
.album-cover {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  transition: transform 0.3s;
}

.song-play-overlay,
.playlist-play-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.song-cover-wrapper:hover .song-play-overlay,
.playlist-cover-wrapper:hover .playlist-play-overlay {
  opacity: 1;
}

.song-cover-wrapper:hover .song-cover,
.playlist-cover-wrapper:hover .playlist-cover {
  transform: scale(1.05);
}

.play-btn {
  transform: scale(1.5);
}

.play-count {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.6);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.song-info,
.playlist-info,
.album-info {
  text-align: center;
}

.song-title,
.playlist-name,
.album-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.song-artist,
.playlist-count,
.album-artist {
  font-size: 13px;
  color: var(--text-secondary);
}

.artists-section .section-header {
  justify-content: flex-start;
}

.artist-card {
  animation: fadeIn 0.5s ease-out backwards;
}

.artist-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.artist-avatar {
  margin-bottom: 12px;
  border: 3px solid var(--primary-color);
}

.artist-name {
  font-weight: 500;
}

.player-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(22, 33, 62, 0.98);
  backdrop-filter: blur(10px);
  border-top: 1px solid var(--border-color);
  z-index: 1000;
}

.player-progress {
  height: 3px;
  background: var(--border-color);
}

.progress-bar {
  height: 100%;
  background: var(--primary-gradient);
  transition: width 0.1s;
}

.player-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 40px;
  height: 70px;
}

.player-song-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.player-cover {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.player-text {
  flex: 1;
}

.player-title {
  font-weight: 600;
  font-size: 14px;
}

.player-artist {
  font-size: 12px;
  color: var(--text-secondary);
}

.is-favorited {
  color: #f093fb;
}

.player-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.player-extra {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  justify-content: flex-end;
}

.time-display {
  font-size: 12px;
  color: var(--text-secondary);
}

.volume-slider {
  width: 100px;
}

.footer {
  background: var(--card-bg);
  border-top: 1px solid var(--border-color);
  margin-top: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 60px 20px;
}

.footer-main {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.footer-brand {
  max-width: 300px;
}

.footer-brand .logo-icon,
.footer-brand .logo-text {
  font-size: 24px;
}

.footer-desc {
  margin-top: 10px;
  color: var(--text-secondary);
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group h4 {
  margin-bottom: 16px;
  color: var(--text-primary);
}

.link-group a {
  display: block;
  color: var(--text-secondary);
  margin-bottom: 8px;
  font-size: 14px;
}

.link-group a:hover {
  color: var(--primary-color);
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
  color: var(--text-secondary);
  font-size: 14px;
}
</style>
