<template>
  <div class="radar-page">
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
                <el-dropdown-item @click="$router.push('/report')">听歌报告</el-dropdown-item>
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

    <el-main class="main-content">
      <div class="page-header">
        <h1 class="page-title">
          <span class="title-icon">📡</span>
          雷达歌单
        </h1>
        <p class="page-desc">根据你的音乐品味，智能推荐你喜欢的歌曲</p>
      </div>

      <!-- 兴趣画像 -->
      <el-card class="interest-card" v-if="interestData && interestData.topTags && interestData.topTags.length > 0">
        <template #header>
          <div class="card-header">
            <span>🎯 你的音乐品味</span>
          </div>
        </template>
        <div class="interest-tags">
          <el-tag 
            v-for="(tag, index) in interestData.topTags" 
            :key="tag.name"
            :type="getTagType(index)"
            size="large"
            class="interest-tag"
          >
            {{ tag.name }} · {{ Math.round(tag.score) }}分
          </el-tag>
        </div>
      </el-card>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-wrapper">
        <el-icon class="is-loading" size="40"><Loading /></el-icon>
        <p>正在生成你的雷达歌单...</p>
      </div>

      <!-- 雷达歌单内容 -->
      <el-card class="radar-card" v-else-if="radarData && radarData.songs && radarData.songs.length > 0">
        <template #header>
          <div class="card-header">
            <span>📋 推荐歌曲 · {{ radarData.songs.length }}首</span>
            <el-button type="primary" @click="playAll">
              <el-icon><VideoPlay /></el-icon> 播放全部
            </el-button>
          </div>
        </template>
        
        <div class="songs-list">
          <div 
            v-for="(song, index) in radarData.songs" 
            :key="song.id" 
            class="song-item"
            @click="playSong(song)"
          >
            <div class="song-rank" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
            <img :src="song.cover || defaultCover" class="song-cover">
            <div class="song-info">
              <div class="song-title">{{ song.title }}</div>
              <div class="song-artist">{{ song.artist }}</div>
            </div>
            <div class="song-duration">{{ formatDuration(song.duration) }}</div>
            <div class="song-actions">
              <el-button circle type="primary" @click.stop="playSong(song)">
                <el-icon><VideoPlay /></el-icon>
              </el-button>
              <el-button circle @click.stop="addToPlaylist(song)">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 空状态 -->
      <el-empty v-else description="暂无推荐，请先听一些歌曲来生成你的雷达歌单">
        <el-button type="primary" @click="$router.push('/songs')">
          去发现音乐
        </el-button>
      </el-empty>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { recommendationAPI } from '../api'
import { Loading, VideoPlay, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('4')
const defaultAvatar = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'
const defaultCover = 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=100'

const loading = ref(true)
const radarData = ref(null)
const interestData = ref(null)

const getTagType = (index) => {
  const types = ['', 'success', 'warning', 'info']
  return types[index % types.length]
}

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

const loadRadarPlaylist = async () => {
  loading.value = true
  try {
    const [radarRes, interestRes] = await Promise.all([
      recommendationAPI.getRadarPlaylist(),
      recommendationAPI.getUserInterest()
    ])
    
    if (radarRes.data.code === 200) {
      radarData.value = radarRes.data.data
    }
    
    if (interestRes.data.code === 200) {
      interestData.value = interestRes.data.data
    }
  } catch (error) {
    console.error('加载雷达歌单失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const playAll = () => {
  if (radarData.value && radarData.value.songs.length > 0) {
    playSong(radarData.value.songs[0])
  }
}

const playSong = (song) => {
  playerStore.playSong(song)
  recommendationAPI.recordBehavior(song.id, 1)
  ElMessage.success(`正在播放: ${song.title}`)
}

const addToPlaylist = (song) => {
  ElMessage.info('添加到我喜欢的音乐')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.init()
  loadRadarPlaylist()
})
</script>

<style scoped>
.radar-page {
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
  padding: 30px 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.title-icon {
  font-size: 40px;
}

.page-desc {
  color: var(--text-secondary);
  font-size: 16px;
}

.interest-card {
  margin-bottom: 24px;
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.interest-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.interest-tag {
  font-size: 16px;
  padding: 8px 16px;
}

.loading-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: var(--text-secondary);
}

.radar-card {
  border-radius: 16px;
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
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.song-item:hover {
  background: var(--hover-bg);
}

.song-rank {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  color: var(--text-secondary);
}

.song-rank.top-three {
  background: var(--primary-gradient);
  color: white;
  border-radius: 8px;
}

.song-cover {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}

.song-info {
  flex: 1;
}

.song-title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
}

.song-artist {
  font-size: 13px;
  color: var(--text-secondary);
}

.song-duration {
  color: var(--text-secondary);
  font-size: 13px;
  min-width: 50px;
}

.song-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.song-item:hover .song-actions {
  opacity: 1;
}
</style>
