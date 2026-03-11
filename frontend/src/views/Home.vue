<template>
  <div class="home-container">
    <!-- 导航栏 -->
    <el-header height="60px" class="navbar">
      <div class="logo">Fly Music</div>
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
            <span class="user-avatar">
              {{ userStore.user?.username || '用户' }}
            </span>
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
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    
    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 轮播图 -->
      <el-carousel :interval="5000" type="card" height="300px" class="carousel">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="carousel-item" @click="handleBannerClick(item)">
            <img :src="item.image" alt="banner" class="banner-image">
            <div class="banner-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      
      <!-- 热门推荐 -->
      <div class="section">
        <h2 class="section-title">热门歌曲</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="song in hotSongs" :key="song.id" class="song-card">
            <el-card shadow="hover" @click="playSong(song)">
              <img :src="song.cover || defaultCover" alt="song" class="song-cover">
              <div class="song-info">
                <h4>{{ song.title }}</h4>
                <p>{{ getArtistNames(song) }}</p>
                <el-icon><VideoPlay /></el-icon>
                <el-button type="primary" size="small" class="play-button">播放</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 心动歌单 -->
      <div class="section">
        <h2 class="section-title">心动歌单</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="playlist in heartPlaylists" :key="playlist.id" class="playlist-card">
            <el-card shadow="hover" @click="viewPlaylist(playlist)">
              <img :src="playlist.cover || defaultCover" alt="playlist" class="playlist-cover">
              <div class="playlist-info">
                <h4>{{ playlist.name }}</h4>
                <p>{{ playlist.songCount }}首歌</p>
                <el-button type="primary" size="small" class="play-button">播放</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 最新专辑 -->
      <div class="section">
        <h2 class="section-title">最新专辑</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="album in latestAlbums" :key="album.id" class="album-card">
            <el-card shadow="hover" @click="viewAlbum(album)">
              <img :src="album.cover || defaultCover" alt="album" class="album-cover">
              <div class="album-info">
                <h4>{{ album.title }}</h4>
                <p>{{ album.songCount }}首歌</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>
    
    <!-- 底部 -->
    <el-footer height="80px" class="footer">
      <div class="footer-content">
        <p>© 2026 Fly Music. All rights reserved.</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { songAPI, playlistAPI, albumAPI } from '../api'

const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('1')

const defaultCover = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20cover%20default&image_size=square'

const banners = ref([
  {
    id: 1,
    title: '热门推荐',
    description: '发现最新最热的音乐',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20banner%20hot%20recommendation&image_size=landscape_16_9',
    type: 'popular'
  },
  {
    id: 2,
    title: 'AI音乐实验室',
    description: '体验AI生成的音乐',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=AI%20music%20laboratory%20futuristic&image_size=landscape_16_9',
    type: 'ai-lab'
  },
  {
    id: 3,
    title: '心动歌单',
    description: '为你推荐最爱的歌曲',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=romantic%20playlist%20heart&image_size=landscape_16_9',
    type: 'heart'
  }
])

const hotSongs = ref([])
const heartPlaylists = ref([])
const latestAlbums = ref([])

const getArtistNames = (song) => {
  if (song.artists && song.artists.length > 0) {
    return song.artists.map(a => a.name).join(', ')
  }
  return '未知艺术家'
}

const handleBannerClick = (banner) => {
  if (banner.type === 'ai-lab') {
    router.push('/ai-lab')
  } else if (banner.type === 'heart') {
    router.push('/recommendations')
  }
}

const playSong = (song) => {
  console.log('播放歌曲:', song)
}

const viewPlaylist = (playlist) => {
  router.push(`/playlists/${playlist.id}`)
}

const viewAlbum = (album) => {
  console.log('查看专辑:', album)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const loadData = async () => {
  try {
    // 获取热门歌曲
    const songsRes = await songAPI.getPopularSongs(8)
    hotSongs.value = songsRes.data || []
    
    // 获取心动歌单
    const playlistsRes = await playlistAPI.getHeartPlaylists(4)
    heartPlaylists.value = playlistsRes.data || []
    
    // 获取最新专辑
    const albumsRes = await albumAPI.getAllAlbums()
    latestAlbums.value = (albumsRes.data || []).slice(0, 4)
  } catch (error) {
    console.error('加载数据失败:', error)
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
}

.navbar {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.nav-menu {
  flex: 1;
  margin: 0 20px;
  border: none;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  background-color: #f0f0f0;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f5f5;
}

.carousel {
  margin-bottom: 40px;
}

.carousel-item {
  position: relative;
  height: 100%;
  cursor: pointer;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.banner-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 20px;
  border-radius: 0 0 10px 10px;
}

.section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.song-card,
.playlist-card,
.album-card {
  margin-bottom: 20px;
}

.song-cover,
.playlist-cover,
.album-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 10px;
}

.song-info,
.playlist-info,
.album-info {
  text-align: center;
}

.song-info h4,
.playlist-info h4,
.album-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
}

.song-info p,
.playlist-info p,
.album-info p {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}

.play-button {
  width: 100%;
}

.footer {
  background-color: #333;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-content {
  text-align: center;
}
</style>
