<template>
  <div class="category-page">
    <el-main class="main-content">
      <div class="page-header">
        <h1 class="page-title">
          <span class="title-icon">🎸</span>
          分类推荐
        </h1>
        <p class="page-desc">按音乐分类探索你喜欢的歌曲</p>
      </div>

      <!-- 分类标签 -->
      <div class="category-grid">
        <div 
          v-for="cat in categories" 
          :key="cat.id"
          class="category-card"
          :class="{ active: selectedCategory === cat.id }"
          @click="selectCategory(cat)"
        >
          <div class="category-icon" :style="{ background: cat.gradient }">
            {{ cat.icon }}
          </div>
          <div class="category-name">{{ cat.name }}</div>
          <div class="category-count">{{ cat.songCount || 0 }}首</div>
        </div>
      </div>

      <!-- 歌曲列表 -->
      <el-card class="songs-card" v-if="selectedCategory && songs.length > 0">
        <template #header>
          <div class="card-header">
            <span>{{ selectedCategoryName }} - {{ songs.length }}首</span>
            <el-button type="primary" @click="playAll">
              <el-icon><VideoPlay /></el-icon> 播放全部
            </el-button>
          </div>
        </template>
        
        <div class="songs-list">
          <div 
            v-for="(song, index) in songs" 
            :key="song.id" 
            class="song-item"
            @click="playSong(song)"
          >
            <div class="song-index">{{ index + 1 }}</div>
            <img :src="song.cover || defaultCover" class="song-cover">
            <div class="song-info">
              <div class="song-title">{{ song.title }}</div>
              <div class="song-artist">{{ song.artist }}</div>
            </div>
            <div class="song-category">{{ song.categoryName }}</div>
            <div class="song-plays">{{ formatPlayCount(song.playCount) }}</div>
            <div class="song-duration">{{ formatDuration(song.duration) }}</div>
          </div>
        </div>
      </el-card>

      <!-- 空状态 -->
      <el-empty v-else-if="selectedCategory" description="该分类暂无歌曲" />
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { recommendationAPI } from '../api'
import { VideoPlay } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('3')
const defaultAvatar = DEFAULT_IMAGES.avatar
const defaultCover = DEFAULT_IMAGES.cover

const selectedCategory = ref(null)
const songs = ref([])

const categories = ref([
  { id: 1, name: '流行', icon: '🎤', gradient: 'linear-gradient(135deg, #667eea, #764ba2)', songCount: 0 },
  { id: 2, name: '摇滚', icon: '🎸', gradient: 'linear-gradient(135deg, #f093fb, #f5576c)', songCount: 0 },
  { id: 3, name: '民谣', icon: '🪕', gradient: 'linear-gradient(135deg, #4facfe, #00f2fe)', songCount: 0 },
  { id: 4, name: '电子', icon: '🎹', gradient: 'linear-gradient(135deg, #43e97b, #38f9d7)', songCount: 0 },
  { id: 5, name: '古典', icon: '🎻', gradient: 'linear-gradient(135deg, #fa709a, #fee140)', songCount: 0 },
  { id: 6, name: '爵士', icon: '🎷', gradient: 'linear-gradient(135deg, #a8edea, #fed6e3)', songCount: 0 },
  { id: 7, name: '嘻哈', icon: '🎧', gradient: 'linear-gradient(135deg, #ff9a9e, #fecfef)', songCount: 0 },
  { id: 8, name: 'R&B', icon: '🎵', gradient: 'linear-gradient(135deg, #ffecd2, #fcb69f)', songCount: 0 }
])

const selectedCategoryName = computed(() => {
  const cat = categories.value.find(c => c.id === selectedCategory.value)
  return cat ? cat.name : ''
})

const selectCategory = async (cat) => {
  selectedCategory.value = cat.id
  try {
    const res = await recommendationAPI.getCategorySongs(cat.id)
    if (res.data.code === 200) {
      songs.value = res.data.data || []
    }
  } catch (error) {
    console.error('加载分类歌曲失败:', error)
  }
}

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

const formatPlayCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count
}

const playAll = () => {
  if (songs.value.length > 0) playSong(songs.value[0])
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
})
</script>

<style scoped>
.category-page {
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

.logo-icon { font-size: 28px; }

.logo-text {
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-menu { flex: 1; margin: 0 40px; }
.user-menu { display: flex; align-items: center; gap: 20px; }
.user-info { cursor: pointer; }

.main-content {
  flex: 1;
  padding: 30px 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header { text-align: center; margin-bottom: 30px; }

.page-title {
  font-size: 36px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.title-icon { font-size: 40px; }
.page-desc { color: var(--text-secondary); font-size: 16px; }

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.category-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.category-card:hover { transform: translateY(-5px); }

.category-card.active {
  border-color: var(--primary-color);
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.3);
}

.category-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin: 0 auto 12px;
}

.category-name { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.category-count { color: var(--text-secondary); font-size: 13px; }

.songs-card { border-radius: 16px; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.songs-list { display: flex; flex-direction: column; gap: 8px; }

.song-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.song-item:hover { background: var(--hover-bg); }

.song-index {
  width: 32px;
  text-align: center;
  color: var(--text-secondary);
}

.song-cover {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
}

.song-info { flex: 1; }
.song-title { font-weight: 600; font-size: 14px; }
.song-artist { font-size: 12px; color: var(--text-secondary); }

.song-category { color: var(--text-secondary); font-size: 13px; min-width: 60px; }
.song-plays { color: var(--text-secondary); font-size: 13px; min-width: 60px; text-align: right; }
.song-duration { color: var(--text-secondary); font-size: 13px; min-width: 50px; }
</style>
