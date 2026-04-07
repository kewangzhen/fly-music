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
          <el-card>
            <h3>听歌大数据</h3>
            <div class="chart-placeholder">
              <p>暂无数据</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { adminAPI } from '../api'
import { User, Headset, Document, TrendCharts } from '@element-plus/icons-vue'
import AdminNavbar from '../components/AdminNavbar.vue'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  totalUsers: 0,
  totalSongs: 0,
  pendingSongs: 0,
  totalPlays: 0
})

const loadStats = async () => {
  try {
    const userStats = await adminAPI.getUserStats()
    const songStats = await adminAPI.getSongStats()
    const listeningStats = await adminAPI.getListeningStats()
    
    stats.value = {
      totalUsers: userStats.data?.totalUsers || 0,
      totalSongs: songStats.data?.totalSongs || 0,
      pendingSongs: songStats.data?.pendingSongs || 0,
      totalPlays: listeningStats.data?.totalPlays || 0
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
    console.error('备份失败:', error)
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
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.stat-icon {
  font-size: 40px;
  color: #667eea;
  margin-right: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.action-row {
  margin-bottom: 20px;
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
  border-radius: 10px;
}

.action-row :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-row :deep(.el-card) {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.chart-row :deep(.el-card h3) {
  color: #fff;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.5);
}
</style>
