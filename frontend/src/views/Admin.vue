<template>
  <div class="admin-container">
    <!-- 导航栏 -->
    <el-header height="60px" class="navbar">
      <div class="logo" @click="$router.push('/')">Fly Music 管理后台</div>
      <el-menu mode="horizontal" :router="true" class="nav-menu">
        <el-menu-item index="/admin" route="/admin">概览</el-menu-item>
        <el-menu-item index="/admin/users" route="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/songs" route="/admin/songs">音乐管理</el-menu-item>
        <el-menu-item index="/admin/config" route="/admin/config">系统配置</el-menu-item>
      </el-menu>
      <div class="user-menu">
        <el-dropdown>
          <span class="user-avatar">管理员</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/')">返回前台</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
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
import { adminAPI } from '../api'
import { User, Headset, Document, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()

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

const handleLogout = () => {
  router.push('/login')
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
  background-color: #f5f5f5;
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
  padding: 5px 10px;
  border-radius: 20px;
  background-color: #f0f0f0;
}

.main-content {
  flex: 1;
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  font-size: 40px;
  color: #667eea;
  margin-right: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.action-row {
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}
</style>
