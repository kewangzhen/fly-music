<template>
  <div class="admin-container">
    <!-- 导航栏 -->
    <AdminNavbar />

    <el-main class="main-content">
      <div class="page-header">
        <h1 class="page-title">推荐决策看板</h1>
        <p class="page-desc">实时监控推荐系统数据，动态调整推荐权重</p>
      </div>

      <div v-loading="loading" class="dashboard-content">
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-icon play-icon">
                <el-icon><VideoPlay /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ formatNumber(data.activityStats?.todayPlays || 0) }}</div>
                <div class="stat-label">今日播放次数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-icon time-icon">
                <el-icon><Timer /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ formatHours(data.activityStats?.todayHours || 0) }}</div>
                <div class="stat-label">今日听歌时长</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-icon user-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ data.activityStats?.activeUsers || 0 }}</div>
                <div class="stat-label">今日活跃用户</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-icon total-icon">
                <el-icon><Files /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ data.activityStats?.totalSongs || 0 }}</div>
                <div class="stat-label">总歌曲数</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="chart-row">
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>🎵 全站用户最喜欢的曲风分布</span>
                </div>
              </template>
              <div class="category-chart">
                <div
                  v-for="(item, index) in data.categoryDistribution?.categories || []"
                  :key="item.name"
                  class="category-item"
                  :style="{ '--delay': index * 0.1 + 's' }"
                >
                  <div class="category-info">
                    <span class="category-name">{{ item.name }}</span>
                    <span class="category-count">{{ item.playCount }}次</span>
                  </div>
                  <div class="category-bar-wrapper">
                    <div
                      class="category-bar"
                      :style="{
                        width: getCategoryMaxWidth(item.playCount) + '%',
                        backgroundColor: chartColors[index % chartColors.length]
                      }"
                    ></div>
                  </div>
                  <span class="category-percentage">{{ item.percentage }}%</span>
                </div>
                <el-empty v-if="!data.categoryDistribution?.categories?.length" description="暂无数据" :image-size="80" />
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>⭐ 全站最受欢迎的歌手 TOP5</span>
                </div>
              </template>
              <div class="artist-list">
                <div
                  v-for="(artist, index) in data.topArtists || []"
                  :key="artist.artistId"
                  class="artist-item"
                  :class="{ 'top-three': index < 3 }"
                >
                  <div class="artist-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
                  <img :src="artist.avatar || defaultAvatar" class="artist-avatar" @error="handleImageError">
                  <div class="artist-info">
                    <h4>{{ artist.name }}</h4>
                    <p>{{ artist.playCount }}次播放</p>
                  </div>
                </div>
                <el-empty v-if="!data.topArtists?.length" description="暂无数据" :image-size="80" />
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-card class="config-card">
              <template #header>
                <div class="card-header">
                  <span>⚙️ 推荐权重设置</span>
                  <el-button type="primary" size="small" @click="saveConfig" :loading="saving">
                    保存配置
                  </el-button>
                </div>
              </template>
              <div class="weight-config">
                <div class="weight-item">
                  <div class="weight-header">
                    <span class="weight-label">📡 雷达歌单权重</span>
                    <span class="weight-value">{{ weightConfig.radarWeight }}</span>
                  </div>
                  <el-slider
                    v-model="weightConfig.radarWeight"
                    :min="1"
                    :max="5"
                    :step="1"
                    :marks="{ 1: '1', 2: '2', 3: '3', 4: '4', 5: '5' }"
                    @change="onWeightChange"
                  />
                  <div class="weight-desc">基于用户行为的个性化推荐</div>
                </div>

                <div class="weight-item">
                  <div class="weight-header">
                    <span class="weight-label">🔥 热门歌单权重</span>
                    <span class="weight-value">{{ weightConfig.hotWeight }}</span>
                  </div>
                  <el-slider
                    v-model="weightConfig.hotWeight"
                    :min="1"
                    :max="5"
                    :step="1"
                    :marks="{ 1: '1', 2: '2', 3: '3', 4: '4', 5: '5' }"
                    @change="onWeightChange"
                  />
                  <div class="weight-desc">基于全站热度的推荐</div>
                </div>

                <div class="weight-item">
                  <div class="weight-header">
                    <span class="weight-label">🎼 分类推荐权重</span>
                    <span class="weight-value">{{ weightConfig.categoryWeight }}</span>
                  </div>
                  <el-slider
                    v-model="weightConfig.categoryWeight"
                    :min="1"
                    :max="5"
                    :step="1"
                    :marks="{ 1: '1', 2: '2', 3: '3', 4: '4', 5: '5' }"
                    @change="onWeightChange"
                  />
                  <div class="weight-desc">基于用户偏好的分类推荐</div>
                </div>

                <div class="weight-item">
                  <div class="weight-header">
                    <span class="weight-label">🎯 相似推荐权重</span>
                    <span class="weight-value">{{ weightConfig.similarWeight }}</span>
                  </div>
                  <el-slider
                    v-model="weightConfig.similarWeight"
                    :min="1"
                    :max="5"
                    :step="1"
                    :marks="{ 1: '1', 2: '2', 3: '3', 4: '4', 5: '5' }"
                    @change="onWeightChange"
                  />
                  <div class="weight-desc">基于相似歌曲的推荐</div>
                </div>

                <div class="config-tip">
                  <el-alert
                    title="权重设置实时生效"
                    type="info"
                    :closable="false"
                    show-icon
                  >
                    <template #default>
                      保存后将立即使用新权重重新计算推荐结果
                    </template>
                  </el-alert>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay, Timer, User, Files } from '@element-plus/icons-vue'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { adminAPI } from '../api'
import AdminNavbar from '../components/AdminNavbar.vue'

const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const defaultAvatar = DEFAULT_IMAGES.avatar

const chartColors = [
  '#667eea', '#f093fb', '#4facfe', '#00f2fe',
  '#fa709a', '#fee140', '#30cfd0', '#a8edea'
]

const data = ref({
  categoryDistribution: { categories: [] },
  topArtists: [],
  activityStats: {},
  config: {}
})

const weightConfig = reactive({
  radarWeight: 3,
  hotWeight: 4,
  categoryWeight: 2,
  similarWeight: 1
})

const maxCategoryPlays = ref(1)

const getCategoryMaxWidth = (value) => {
  return maxCategoryPlays.value > 0 ? (value / maxCategoryPlays.value) * 100 : 0
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num
}

const formatHours = (hours) => {
  if (hours >= 24) {
    return (hours / 24).toFixed(1) + '天'
  }
  return hours.toFixed(1) + '小时'
}

const loadDashboard = async () => {
  loading.value = true
  try {
    const response = await adminAPI.getRecommendationDashboard()
    if (response.data.code === 200) {
      data.value = response.data.data

      if (data.value.config) {
        weightConfig.radarWeight = data.value.config.radarWeight || 3
        weightConfig.hotWeight = data.value.config.hotWeight || 4
        weightConfig.categoryWeight = data.value.config.categoryWeight || 2
        weightConfig.similarWeight = data.value.config.similarWeight || 1
      }

      const categories = data.value.categoryDistribution?.categories || []
      if (categories.length > 0) {
        maxCategoryPlays.value = Math.max(...categories.map(c => c.playCount || 0))
      }
    }
  } catch (error) {
    console.error('加载推荐看板数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const saveConfig = async () => {
  saving.value = true
  try {
    const response = await adminAPI.updateRecommendationConfig(weightConfig)
    if (response.data.code === 200) {
      ElMessage.success(response.data.message || '配置保存成功')
    } else {
      ElMessage.error('保存失败')
    }
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const onWeightChange = () => {
  console.log('权重已更改:', weightConfig)
}

const handleImageError = (e) => {
  e.target.src = defaultAvatar
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--dark-bg);
}

.navbar {
  background: var(--card-bg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #667eea;
  cursor: pointer;
}

.nav-menu {
  flex: 1;
  margin: 0 20px;
  border: none;
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-avatar {
  cursor: pointer;
  color: #667eea;
  font-weight: 500;
}

.main-content {
  flex: 1;
  padding: 30px;
}

.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-desc {
  color: #909399;
  font-size: 14px;
}

.dashboard-content {
  animation: fadeIn 0.5s ease-out;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.stat-icon.play-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-icon.time-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-icon.user-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
}

.stat-icon.total-icon {
  background: linear-gradient(135deg, #30cfd0 0%, #a8edea 100%);
  color: #fff;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 12px;
  min-height: 350px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.category-chart {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
  animation: slideIn 0.5s ease-out backwards;
  animation-delay: var(--delay);
}

.category-info {
  width: 120px;
  display: flex;
  flex-direction: column;
}

.category-name {
  font-weight: 500;
  font-size: 14px;
  color: #303133;
}

.category-count {
  font-size: 12px;
  color: #909399;
}

.category-bar-wrapper {
  flex: 1;
  height: 20px;
  background: #f0f2f5;
  border-radius: 10px;
  overflow: hidden;
}

.category-bar {
  height: 100%;
  border-radius: 10px;
  transition: width 1s ease-out;
}

.category-percentage {
  width: 50px;
  text-align: right;
  font-weight: 600;
  color: #667eea;
}

.artist-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.artist-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  transition: background 0.3s;
}

.artist-item:hover {
  background: var(--hover-bg);
}

.artist-item.top-three {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
}

.artist-rank {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  border-radius: 8px;
  color: #909399;
}

.artist-rank.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffb800);
  color: #fff;
}

.artist-rank.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #a8a8a8);
  color: #fff;
}

.artist-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  color: #fff;
}

.artist-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.artist-info {
  flex: 1;
}

.artist-info h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #303133;
}

.artist-info p {
  font-size: 13px;
  color: #909399;
}

.config-card {
  border-radius: 12px;
}

.weight-config {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.weight-item {
  padding: 16px;
  background: #f8f9fc;
  border-radius: 10px;
}

.weight-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.weight-label {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.weight-value {
  font-size: 20px;
  font-weight: bold;
  color: #667eea;
}

.weight-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.config-tip {
  grid-column: 1 / -1;
  margin-top: 8px;
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

@media (max-width: 1200px) {
  .weight-config {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-row .el-col {
    margin-bottom: 16px;
  }

  .chart-row .el-col {
    margin-bottom: 16px;
  }
}
</style>
