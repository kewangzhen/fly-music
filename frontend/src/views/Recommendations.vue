<template>
  <div class="recommendations-page">
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
    
    <el-main class="main-content">
      <div class="page-header">
        <h1 class="page-title">个性化推荐</h1>
        <p class="page-desc">根据你的音乐品味，为你精选好歌</p>
      </div>

      <el-tabs v-model="activeTab" class="recommendation-tabs">
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { VideoPlay, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('4')
const activeTab = ref('daily')
const defaultAvatar = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'

const dailyRecommendations = ref([
  { id: 1, title: '孤勇者', artist: '陈奕迅', cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400' },
  { id: 2, title: '起风了', artist: '买辣椒也用券', cover: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400' },
  { id: 3, title: '海阔天空', artist: 'Beyond', cover: 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=400' },
  { id: 4, title: '平凡之路', artist: '朴树', cover: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400' },
  { id: 5, title: '晴天', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=400' },
  { id: 6, title: '稻香', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400' },
  { id: 7, title: '夜曲', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1501612780327-45045538702b?w=400' },
  { id: 8, title: '青花瓷', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400' }
])

const similarRecommendations = ref([
  { id: 9, title: '光年之外', artist: '邓紫棋', cover: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400' },
  { id: 10, title: '病变', artist: '鞠文娴', cover: 'https://images.unsplash.com/photo-1485579149621-3123dd979885?w=400' },
  { id: 11, title: '嚣张', artist: 'en', cover: 'https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=400' },
  { id: 12, title: '芒种', artist: '音阙诗听', cover: 'https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=400' },
  { id: 13, title: '盗将行', artist: '花粥', cover: 'https://images.unsplash.com/photo-1483401757487-6c62c21f7a8a?w=400' },
  { id: 14, title: '出山', artist: '花粥', cover: 'https://images.unsplash.com/photo-1506157786151-b8491531f063?w=400' },
  { id: 15, title: '卡点音乐', artist: 'DJ', cover: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400' },
  { id: 16, title: '评论歌曲', artist: 'Various', cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400' }
])

const hotRecommendations = ref([
  { id: 17, title: '野狼Disco', artist: '宝石Gem', cover: 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=400' },
  { id: 18, title: '下山', artist: '要不要买菜', cover: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400' },
  { id: 19, title: '江南', artist: '林俊杰', cover: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=400' },
  { id: 20, title: '倒带', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400' },
  { id: 21, title: '黑色幽默', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1501612780327-45045538702b?w=400' },
  { id: 22, title: '彩虹', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400' },
  { id: 23, title: '七里香', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400' },
  { id: 24, title: '以父之名', artist: '周杰伦', cover: 'https://images.unsplash.com/photo-1485579149621-3123dd979885?w=400' }
])

const radarList = ref([
  { id: 25, title: '世界这么大还是遇见你', artist: '程响', cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400' },
  { id: 26, title: '万爱千恩', artist: '王琪', cover: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400' },
  { id: 27, title: '酒醉的蝴蝶', artist: '崔伟立', cover: 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=400' },
  { id: 28, title: '谁', artist: '小阿枫', cover: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400' },
  { id: 29, title: '少年', artist: '梦然', cover: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=400' },
  { id: 30, title: '可不可以', artist: '张紫豪', cover: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400' },
  { id: 31, title: '广东爱情故事', artist: '广东雨神', cover: 'https://images.unsplash.com/photo-1501612780327-45045538702b?w=400' },
  { id: 32, title: '陷阱', artist: '王北车', cover: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400' },
  { id: 33, title: '不只是 пох', artist: '木小雅', cover: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400' },
  { id: 34, title: '知否知否', artist: '胡夏/郁可唯', cover: 'https://images.unsplash.com/photo-1485579149621-3123dd979885?w=400' }
])

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
