<template>
  <div class="admin-container">
    <!-- 导航栏 -->
    <AdminNavbar />

    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon"><el-icon><Headset /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalSongs }}</div>
              <div class="stat-label">总歌曲数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon"><el-icon><Document /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingSongs }}</div>
              <div class="stat-label">待审核歌曲</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon"><el-icon><TrendCharts /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalPlays }}</div>
              <div class="stat-label">总播放次数</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 快捷操作 -->
      <el-row :gutter="20" class="action-row">
        <el-col :span="24">
          <el-card>
            <h3>快捷操作</h3>
            <el-space wrap>
              <el-button type="primary" @click="backupData">数据备份</el-button>
              <el-button @click="refreshStats">刷新统计</el-button>
              <el-button type="success" @click="$router.push('/admin/users')">用户管理</el-button>
              <el-button type="warning" @click="$router.push('/admin/songs')">歌曲审核</el-button>
            </el-space>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 听歌大数据 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card class="listening-card">
            <h3>听歌大数据</h3>
            
            <!-- 今日数据 -->
            <el-row :gutter="20" class="today-stats">
              <el-col :span="6">
                <div class="today-stat">
                  <div class="today-value">{{ listeningStats.todayPlays || 0 }}</div>
                  <div class="today-label">今日播放次数</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="today-stat">
                  <div class="today-value">{{ formatDuration(listeningStats.todayDuration) }}</div>
                  <div class="today-label">今日听歌时长</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="today-stat">
                  <div class="today-value">{{ listeningStats.todayActiveUsers || 0 }}</div>
                  <div class="today-label">今日活跃用户</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="today-stat">
                  <div class="today-value">{{ listeningStats.totalPlays || 0 }}</div>
                  <div class="today-label">总播放次数</div>
                </div>
              </el-col>
            </el-row>
            
            <!-- 图表 -->
            <el-row :gutter="20" class="charts-section">
              <!-- 曲风分布饼图 -->
              <el-col :span="12">
                <div class="chart-container">
                  <h4>全站用户最喜欢的曲风分布</h4>
                  <v-chart :option="genreChartOption" autoresize style="height: 300px" />
                </div>
              </el-col>
              <!-- TOP歌手 -->
              <el-col :span="12">
                <div class="chart-container">
                  <h4>全站用户最喜欢的歌手 TOP5</h4>
                  <v-chart :option="artistChartOption" autoresize style="height: 300px" />
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { User, Headset, Document, TrendCharts } from '@element-plus/icons-vue'
import { adminAPI } from '../api'
import AdminNavbar from '../components/AdminNavbar.vue'

use([CanvasRenderer, PieChart, BarChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

const router = useRouter()

const stats = ref({
  totalUsers: 0,
  totalSongs: 0,
  pendingSongs: 0,
  totalPlays: 0
})

const listeningStats = ref({
  totalPlays: 0,
  todayPlays: 0,
  todayDuration: 0,
  todayActiveUsers: 0,
  genreStats: {},
  topArtists: []
})

const formatDuration = (seconds) => {
  if (!seconds) return '0秒'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}

const genreChartOption = computed(() => {
  const genreData = listeningStats.value.genreStats || {}
  const data = Object.entries(genreData).map(([name, value]) => ({ name, value }))
  
  const colors = [
    '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
    '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#ff9f7f'
  ]
  
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: { color: '#fff' }
    },
    color: colors,
    series: [
      {
        name: '曲风',
        type: 'pie',
        radius: '60%',
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          color: '#fff'
        }
      }
    ]
  }
})

const artistChartOption = computed(() => {
  const artists = listeningStats.value.topArtists || []
  const data = artists.map(a => ({
    name: a.name,
    value: a.playCount
  }))
  
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: { color: '#fff' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
    },
    yAxis: {
      type: 'category',
      data: data.map(d => d.name).reverse(),
      axisLabel: { color: '#fff' }
    },
    series: [
      {
        name: '播放次数',
        type: 'bar',
        data: data.map(d => d.value).reverse(),
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]
          },
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          color: '#fff'
        }
      }
    ]
  }
})

const loadStats = async () => {
  try {
    const userStats = await adminAPI.getUserStats()
    const songStats = await adminAPI.getSongStats()
    const listeningData = await adminAPI.getListeningStats()
    
    stats.value = {
      totalUsers: userStats.data?.totalUsers || 0,
      totalSongs: songStats.data?.totalSongs || 0,
      pendingSongs: songStats.data?.pendingSongs || 0,
      totalPlays: listeningData.data?.totalPlays || 0
    }
    
    listeningStats.value = listeningData.data || {
      totalPlays: 0,
      todayPlays: 0,
      todayDuration: 0,
      todayActiveUsers: 0,
      genreStats: {},
      topArtists: []
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const backupData = async () => {
  try {
    await adminAPI.backupData()
    alert('数据备份成功')
  } catch (error) {
    alert('备份失败: ' + error.message)
  }
}

const refreshStats = () => {
  loadStats()
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.main-content {
  flex: 1;
  padding: 30px;
}

.stats-row {
  margin-bottom: 25px;
}

.stat-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 5px;
}

.action-row {
  margin-bottom: 25px;
}

.action-row :deep(.el-card) {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.action-row :deep(.el-card h3) {
  color: #fff;
  margin-bottom: 15px;
}

.action-row :deep(.el-button) {
  border: none;
}

.listening-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.listening-card :deep(.el-card__body) {
  padding: 20px;
}

.listening-card h3 {
  color: #fff;
  margin-bottom: 20px;
}

.today-stats {
  margin-bottom: 30px;
}

.today-stat {
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.today-value {
  font-size: 28px;
  font-weight: bold;
  color: #667eea;
}

.today-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 8px;
}

.charts-section {
  margin-top: 20px;
}

.chart-container {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.chart-container h4 {
  color: #fff;
  margin-bottom: 15px;
  text-align: center;
}
</style>
