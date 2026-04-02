<template>
  <div class="report-page">
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
        <h1 class="page-title">🎧 我的听歌报告</h1>
        <p class="page-desc">{{ currentYear }}年度音乐回顾</p>
        <div class="year-selector">
          <el-select v-model="selectedYear" placeholder="选择年份" @change="loadReport">
            <el-option v-for="year in availableYears" :key="year" :label="year + '年'" :value="year" />
          </el-select>
        </div>
      </div>

      <div v-loading="loading" class="report-content">
        <template v-if="report">
          <div class="summary-cards">
            <div class="summary-card">
              <div class="card-icon">🎵</div>
              <div class="card-value">{{ report.summary?.totalSongs || 0 }}</div>
              <div class="card-label">听歌曲数</div>
            </div>
            <div class="summary-card">
              <div class="card-icon">⏱️</div>
              <div class="card-value">{{ report.summary?.totalHours?.toFixed(1) || 0 }}</div>
              <div class="card-label">听歌时长(小时)</div>
            </div>
            <div class="summary-card">
              <div class="card-icon">📅</div>
              <div class="card-value">{{ report.summary?.totalDays || 0 }}</div>
              <div class="card-label">听歌天数</div>
            </div>
            <div class="summary-card">
              <div class="card-icon">▶️</div>
              <div class="card-value">{{ report.summary?.totalPlays || 0 }}</div>
              <div class="card-label">播放次数</div>
            </div>
          </div>

          <div class="charts-section">
            <div class="chart-card">
              <h3 class="chart-title">🎵 音乐流派分布</h3>
              <div class="pie-chart-container">
                <div v-if="report.categoryDistribution?.length" class="pie-chart">
                  <div 
                    v-for="(item, index) in report.categoryDistribution" 
                    :key="item.name"
                    class="pie-item"
                    :style="{
                      '--percentage': item.value + '%',
                      '--color': chartColors[index % chartColors.length],
                      '--delay': index * 0.1 + 's'
                    }"
                  >
                    <div class="pie-label">
                      <span class="pie-name">{{ item.name }}</span>
                      <span class="pie-value">{{ item.value }}%</span>
                    </div>
                    <div class="pie-bar">
                      <div class="pie-bar-fill" :style="{ width: item.value + '%' }"></div>
                    </div>
                  </div>
                </div>
                <div v-else class="empty-chart">
                  <span>暂无数据</span>
                </div>
              </div>
            </div>

            <div class="chart-card">
              <h3 class="chart-title">⏰ 听歌时间分布</h3>
              <div class="time-chart-container">
                <div v-if="report.timeDistribution && Object.keys(report.timeDistribution).length" class="time-chart">
                  <div 
                    v-for="(value, hour) in report.timeDistribution" 
                    :key="hour"
                    class="time-bar-wrapper"
                  >
                    <div class="time-bar" :style="{ height: (value / maxTimeValue * 100) + '%' }"></div>
                    <span class="time-label">{{ hour }}时</span>
                  </div>
                </div>
                <div v-else class="empty-chart">
                  <span>暂无数据</span>
                </div>
              </div>
            </div>
          </div>

          <div class="top-section">
            <div class="top-card">
              <h3 class="chart-title">🏆 我的Top10歌曲</h3>
              <div class="top-list">
                <div 
                  v-for="(song, index) in report.topSongs" 
                  :key="song.songId"
                  class="top-item"
                  :class="{ 'top-three': index < 3 }"
                >
                  <div class="top-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
                  <img :src="song.cover || defaultCover" class="top-cover" @error="handleImageError">
                  <div class="top-info">
                    <h4>{{ song.title }}</h4>
                    <p>{{ song.artist }}</p>
                  </div>
                  <div class="top-stat">
                    <span class="play-count">▶ {{ song.playCount }}次</span>
                  </div>
                </div>
                <div v-if="!report.topSongs?.length" class="empty-list">
                  <span>暂无听歌记录</span>
                </div>
              </div>
            </div>

            <div class="top-card">
              <h3 class="chart-title">⭐ 最常听的歌手</h3>
              <div class="artist-list">
                <div 
                  v-for="(artist, index) in report.topArtists" 
                  :key="artist.artistId"
                  class="artist-item"
                  :class="{ 'top-three': index < 3 }"
                >
                  <div class="artist-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
                  <img :src="artist.avatar || defaultCover" class="artist-avatar" @error="handleImageError">
                  <div class="artist-info">
                    <h4>{{ artist.name }}</h4>
                    <p>{{ artist.songCount }}首歌 · {{ artist.playCount }}次播放</p>
                  </div>
                </div>
                <div v-if="!report.topArtists?.length" class="empty-list">
                  <span>暂无听歌记录</span>
                </div>
              </div>
            </div>
          </div>

          <div class="tags-section" v-if="report.tagDistribution && Object.keys(report.tagDistribution).length">
            <div class="chart-card full-width">
              <h3 class="chart-title">🏷️ 音乐标签分布</h3>
              <div class="tag-cloud">
                <span 
                  v-for="(count, tag) in report.tagDistribution" 
                  :key="tag"
                  class="tag-item"
                  :style="{ 
                    '--size': Math.max(12, Math.min(32, 12 + count / 10)) + 'px',
                    '--delay': Math.random() * 0.5 + 's'
                  }"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>

          <div class="generated-time">
            报告生成时间：{{ report.generatedAt }}
          </div>
        </template>

        <template v-else-if="!loading">
          <el-empty description="暂无听歌数据，开始听歌后会生成报告">
            <el-button type="primary" @click="$router.push('/songs')">去听歌</el-button>
          </el-empty>
        </template>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import recommendationApi from '../api/recommendation'

const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('4')
const defaultAvatar = DEFAULT_IMAGES.avatar
const defaultCover = DEFAULT_IMAGES.cover

const currentYear = new Date().getFullYear()
const selectedYear = ref(currentYear)
const availableYears = computed(() => {
  const years = []
  for (let y = currentYear; y >= currentYear - 5; y--) {
    years.push(y)
  }
  return years
})

const loading = ref(false)
const report = ref(null)

const chartColors = [
  '#667eea', '#f093fb', '#4facfe', '#00f2fe',
  '#fa709a', '#fee140', '#30cfd0', '#a8edea'
]

const maxTimeValue = computed(() => {
  if (!report.value?.timeDistribution) return 1
  const values = Object.values(report.value.timeDistribution)
  return Math.max(...values, 1)
})

const loadReport = async () => {
  loading.value = true
  try {
    const response = await recommendationApi.getListeningReportByYear(selectedYear.value)
    if (response.data.code === 200) {
      report.value = response.data.data
    }
  } catch (error) {
    console.error('加载听歌报告失败:', error)
  } finally {
    loading.value = false
  }
}

const handleImageError = (e) => {
  e.target.src = defaultCover
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.init()
  loadReport()
})
</script>

<style scoped>
.report-page {
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
  margin-bottom: 20px;
}

.year-selector {
  display: inline-block;
}

.report-content {
  animation: fadeIn 0.5s ease-out;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.summary-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  transition: transform 0.3s, box-shadow 0.3s;
}

.summary-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.2);
}

.card-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.card-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.pie-chart-container {
  min-height: 200px;
}

.pie-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pie-item {
  animation: slideIn 0.5s ease-out backwards;
  animation-delay: var(--delay);
}

.pie-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 14px;
}

.pie-name {
  font-weight: 500;
}

.pie-value {
  color: var(--color);
  font-weight: 600;
}

.pie-bar {
  height: 8px;
  background: var(--bg-secondary);
  border-radius: 4px;
  overflow: hidden;
}

.pie-bar-fill {
  height: 100%;
  background: var(--color);
  border-radius: 4px;
  transition: width 1s ease-out;
  width: 0;
  animation: barGrow 1s ease-out forwards;
}

.time-chart-container {
  min-height: 200px;
}

.time-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 180px;
  padding-top: 20px;
}

.time-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  height: 100%;
}

.time-bar {
  width: 24px;
  background: linear-gradient(to top, #667eea, #f093fb);
  border-radius: 4px 4px 0 0;
  transition: height 0.5s ease-out;
  min-height: 4px;
}

.time-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 8px;
}

.empty-chart {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--text-secondary);
}

.top-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.top-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
}

.top-list, .artist-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-item, .artist-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  transition: background 0.3s;
}

.top-item:hover, .artist-item:hover {
  background: var(--hover-bg);
}

.top-rank, .artist-rank {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  border-radius: 6px;
  color: var(--text-secondary);
}

.top-rank.rank-1, .artist-rank.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffb800);
  color: #fff;
}

.top-rank.rank-2, .artist-rank.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #a8a8a8);
  color: #fff;
}

.top-rank.rank-3, .artist-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  color: #fff;
}

.top-cover {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
}

.artist-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.top-info, .artist-info {
  flex: 1;
  min-width: 0;
}

.top-info h4, .artist-info h4 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.top-info p, .artist-info p {
  font-size: 12px;
  color: var(--text-secondary);
}

.top-stat {
  text-align: right;
}

.play-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.empty-list {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
}

.tags-section {
  margin-bottom: 30px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  padding: 20px;
}

.tag-item {
  padding: 8px 16px;
  background: var(--bg-secondary);
  border-radius: 20px;
  font-size: var(--size);
  animation: tagPop 0.5s ease-out backwards;
  animation-delay: var(--delay);
  transition: transform 0.3s, background 0.3s;
}

.tag-item:hover {
  transform: scale(1.1);
  background: linear-gradient(135deg, #667eea, #f093fb);
  color: #fff;
}

.generated-time {
  text-align: center;
  color: var(--text-secondary);
  font-size: 12px;
  padding: 20px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes barGrow {
  from {
    width: 0;
  }
  to {
    width: var(--percentage);
  }
}

@keyframes tagPop {
  from {
    opacity: 0;
    transform: scale(0);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@media (max-width: 1200px) {
  .summary-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 900px) {
  .charts-section,
  .top-section {
    grid-template-columns: 1fr;
  }

  .summary-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
