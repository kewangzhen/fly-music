<template>
  <div class="recommendations-page">
    <el-main class="main-content">
      <div class="page-header">
        <h1 class="page-title">个性化推荐</h1>
        <p class="page-desc">根据你的音乐品味，为你精选好歌</p>
      </div>

      <el-tabs v-model="activeTab" class="recommendation-tabs">
        <el-tab-pane label="心动歌单" name="heartbeat">
          <div class="recommendation-section">
            <div class="section-header">
              <div class="section-title-wrapper">
                <span class="section-icon heartbeat">💕</span>
                <h2>心动歌单</h2>
              </div>
              <el-button type="primary" round @click="playAllHeartbeat">
                <el-icon><VideoPlay /></el-icon> 播放全部
              </el-button>
            </div>
            <div class="recommendation-grid">
              <div 
                v-for="(song, index) in heartbeatSongs" 
                :key="song.id" 
                class="recommend-card"
                :style="{ animationDelay: `${index * 0.05}s` }"
                @click="playSong(song)"
              >
                <div class="card-cover">
                  <img :src="song.cover" :alt="song.title">
                  <div class="card-overlay">
                    <el-button circle size="large" type="primary">
                      <el-icon size="20"><VideoPlay /></el-icon>
                    </el-button>
                  </div>
                </div>
                <div class="card-info">
                  <h4 class="card-title">{{ song.title }}</h4>
                  <p class="card-subtitle">{{ song.artist }}</p>
                </div>
              </div>
            </div>
            <div v-if="heartbeatSongs.length === 0 && !heartbeatLoading" class="empty-state">
              <p>暂无心动歌曲</p>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="每日推荐" name="daily">
          <div class="recommendation-section">
            <div class="section-header">
              <div class="section-title-wrapper">
                <span class="section-icon daily">☀️</span>
                <h2>每日推荐</h2>
              </div>
              <el-button type="primary" round>
                <el-icon><VideoPlay /></el-icon> 播放全部
              </el-button>
            </div>
            <div class="recommendation-grid">
              <div 
                v-for="(song, index) in dailyRecommendations" 
                :key="song.id" 
                class="recommend-card"
                :style="{ animationDelay: `${index * 0.05}s` }"
                @click="playSong(song)"
              >
                <div class="card-cover">
                  <img :src="song.cover" :alt="song.title">
                  <div class="card-overlay">
                    <el-button circle size="large" type="primary">
                      <el-icon size="20"><VideoPlay /></el-icon>
                    </el-button>
                  </div>
                  <div class="card-rank" v-if="index < 3">{{ index + 1 }}</div>
                </div>
                <div class="card-info">
                  <h4 class="card-title">{{ song.title }}</h4>
                  <p class="card-subtitle">{{ song.artist }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="相似推荐" name="similar">
          <div class="recommendation-section">
            <div class="section-header">
              <div class="section-title-wrapper">
                <span class="section-icon similar">🎯</span>
                <h2>相似推荐</h2>
              </div>
            </div>
            <div class="recommendation-grid">
              <div 
                v-for="(song, index) in similarRecommendations" 
                :key="song.id" 
                class="recommend-card"
                :style="{ animationDelay: `${index * 0.05}s` }"
                @click="playSong(song)"
              >
                <div class="card-cover">
                  <img :src="song.cover" :alt="song.title">
                  <div class="card-overlay">
                    <el-button circle size="large" type="primary">
                      <el-icon size="20"><VideoPlay /></el-icon>
                    </el-button>
                  </div>
                </div>
                <div class="card-info">
                  <h4 class="card-title">{{ song.title }}</h4>
                  <p class="card-subtitle">{{ song.artist }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="热门推荐" name="hot">
          <div class="recommendation-section">
            <div class="section-header">
              <div class="section-title-wrapper">
                <span class="section-icon hot">🔥</span>
                <h2>热门推荐</h2>
              </div>
            </div>
            <div class="recommendation-grid">
              <div 
                v-for="(song, index) in hotRecommendations" 
                :key="song.id" 
                class="recommend-card"
                :style="{ animationDelay: `${index * 0.05}s` }"
                @click="playSong(song)"
              >
                <div class="card-cover">
                  <img :src="song.cover" :alt="song.title">
                  <div class="card-overlay">
                    <el-button circle size="large" type="primary">
                      <el-icon size="20"><VideoPlay /></el-icon>
                    </el-button>
                  </div>
                  <div class="card-badge">TOP {{ index + 1 }}</div>
                </div>
                <div class="card-info">
                  <h4 class="card-title">{{ song.title }}</h4>
                  <p class="card-subtitle">{{ song.artist }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="雷达榜单" name="radar">
          <div class="recommendation-section">
            <div class="section-header">
              <div class="section-title-wrapper">
                <span class="section-icon radar">📡</span>
                <h2>雷达榜单</h2>
              </div>
            </div>
            <div class="radar-list">
              <div 
                v-for="(song, index) in radarList" 
                :key="song.id" 
                class="radar-item"
                @click="playSong(song)"
              >
                <div class="radar-rank" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
                <img :src="song.cover" class="radar-cover">
                <div class="radar-info">
                  <h4>{{ song.title }}</h4>
                  <p>{{ song.artist }}</p>
                </div>
                <div class="radar-actions">
                  <el-button circle text><el-icon><VideoPlay /></el-icon></el-button>
                  <el-button circle text><el-icon><Plus /></el-icon></el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { VideoPlay, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('4')
const activeTab = ref('heartbeat')
const defaultAvatar = DEFAULT_IMAGES.avatar
const heartbeatSongs = ref([])
const heartbeatLoading = ref(false)

const dailyRecommendations = ref([
  { id: 1, title: '孤勇者', artist: '陈奕迅', cover: DEFAULT_IMAGES.cover },
  { id: 2, title: '起风了', artist: '买辣椒也用券', cover: DEFAULT_IMAGES.cover },
  { id: 3, title: '海阔天空', artist: 'Beyond', cover: DEFAULT_IMAGES.cover },
  { id: 4, title: '平凡之路', artist: '朴树', cover: DEFAULT_IMAGES.cover },
  { id: 5, title: '晴天', artist: '周杰伦', cover: DEFAULT_IMAGES.cover },
  { id: 6, title: '稻香', artist: '周杰伦', cover: DEFAULT_IMAGES.cover },
  { id: 7, title: '夜曲', artist: '周杰伦', cover: DEFAULT_IMAGES.cover },
  { id: 8, title: '青花瓷', artist: '周杰伦', cover: DEFAULT_IMAGES.cover }
])

const similarRecommendations = ref([
  { id: 9, title: '光年之外', artist: '邓紫棋', cover: DEFAULT_IMAGES.cover },
  { id: 10, title: '病变', artist: '鞠文娴', cover: DEFAULT_IMAGES.cover },
  { id: 11, title: '嚣张', artist: 'en', cover: DEFAULT_IMAGES.cover },
  { id: 12, title: '芒种', artist: '音阙诗听', cover: DEFAULT_IMAGES.cover },
  { id: 13, title: '盗将行', artist: '花粥', cover: DEFAULT_IMAGES.cover },
  { id: 14, title: '出山', artist: '花粥', cover: DEFAULT_IMAGES.cover },
  { id: 15, title: '卡点音乐', artist: 'DJ', cover: DEFAULT_IMAGES.cover },
  { id: 16, title: '评论歌曲', artist: 'Various', cover: DEFAULT_IMAGES.cover }
])

const hotRecommendations = ref([
  { id: 17, title: '野狼Disco', artist: '宝石Gem', cover: DEFAULT_IMAGES.cover },
  { id: 18, title: '下山', artist: '要不要买菜', cover: DEFAULT_IMAGES.cover },
  { id: 19, title: '江南', artist: '林俊杰', cover: DEFAULT_IMAGES.cover },
  { id: 20, title: '倒带', artist: '周杰伦', cover: DEFAULT_IMAGES.cover },
  { id: 21, title: '黑色幽默', artist: '周杰伦', cover: DEFAULT_IMAGES.cover },
  { id: 22, title: '彩虹', artist: '周杰伦', cover: DEFAULT_IMAGES.cover },
  { id: 23, title: '七里香', artist: '周杰伦', cover: DEFAULT_IMAGES.cover },
  { id: 24, title: '以父之名', artist: '周杰伦', cover: DEFAULT_IMAGES.cover }
])

const radarList = ref([
  { id: 25, title: '世界这么大还是遇见你', artist: '程响', cover: DEFAULT_IMAGES.cover },
  { id: 26, title: '万爱千恩', artist: '王琪', cover: DEFAULT_IMAGES.cover },
  { id: 27, title: '酒醉的蝴蝶', artist: '崔伟立', cover: DEFAULT_IMAGES.cover },
  { id: 28, title: '谁', artist: '小阿枫', cover: DEFAULT_IMAGES.cover },
  { id: 29, title: '少年', artist: '梦然', cover: DEFAULT_IMAGES.cover },
  { id: 30, title: '可不可以', artist: '张紫豪', cover: DEFAULT_IMAGES.cover },
  { id: 31, title: '广东爱情故事', artist: '广东雨神', cover: DEFAULT_IMAGES.cover },
  { id: 32, title: '陷阱', artist: '王北车', cover: DEFAULT_IMAGES.cover },
  { id: 33, title: '不只是 пох', artist: '木小雅', cover: DEFAULT_IMAGES.cover },
  { id: 34, title: '知否知否', artist: '胡夏/郁可唯', cover: DEFAULT_IMAGES.cover }
])

const playSong = (song) => {
  playerStore.playSong(song)
}

const loadHeartbeatSongs = async () => {
  heartbeatLoading.value = true
  try {
    const res = await fetch('http://localhost:8080/api/songs/heartbeat', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      heartbeatSongs.value = data.data || []
    }
  } catch (error) {
    console.error('获取心动歌曲失败:', error)
  } finally {
    heartbeatLoading.value = false
  }
}

const playAllHeartbeat = () => {
  if (heartbeatSongs.value.length > 0) {
    playerStore.setPlaylist(heartbeatSongs.value)
    playerStore.playSong(heartbeatSongs.value[0])
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.init()
  loadHeartbeatSongs()
})

watch(activeTab, (newTab) => {
  if (newTab === 'heartbeat') {
    loadHeartbeatSongs()
  }
})
</script>

<style scoped>
.recommendations-page {
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
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  animation: fadeIn 0.6s ease-out;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-desc {
  color: var(--text-secondary);
  font-size: 16px;
}

.recommendation-tabs {
  margin-top: 20px;
}

.recommendation-tabs :deep(.el-tabs__header) {
  margin-bottom: 30px;
}

.recommendation-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 24px;
}

.recommendation-section {
  animation: fadeIn 0.5s ease-out;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title-wrapper h2 {
  font-size: 22px;
  font-weight: 600;
}

.section-icon {
  font-size: 28px;
}

.section-icon.daily {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-icon.heartbeat {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-icon.similar {
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-icon.hot {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-icon.radar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.recommendation-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.recommend-card {
  animation: fadeIn 0.5s ease-out backwards;
  cursor: pointer;
  transition: transform 0.3s;
}

.recommend-card:hover {
  transform: translateY(-8px);
}

.card-cover {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 1;
  margin-bottom: 12px;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.recommend-card:hover .card-cover img {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.recommend-card:hover .card-overlay {
  opacity: 1;
}

.card-rank {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.6);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: bold;
}

.card-rank:nth-child(1),
.card-rank:nth-child(2),
.card-rank:nth-child(3) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.card-info {
  text-align: center;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
}

.radar-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.radar-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: var(--card-bg);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.radar-item:hover {
  background: var(--hover-bg);
  transform: translateX(8px);
}

.radar-rank {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  color: var(--text-secondary);
}

.radar-rank.top-three {
  color: var(--primary-color);
  background: var(--hover-bg);
  border-radius: 8px;
}

.radar-cover {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}

.radar-info {
  flex: 1;
}

.radar-info h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.radar-info p {
  font-size: 13px;
  color: var(--text-secondary);
}

.radar-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 1200px) {
  .recommendation-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .recommendation-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
