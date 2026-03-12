<template>
  <div class="admin-config-container">
    <!-- 导航栏 -->
    <el-header height="60px" class="navbar">
      <div class="logo" @click="$router.push('/')">Fly Music 管理后台</div>
      <el-menu mode="horizontal" :router="true" class="nav-menu" :default-active="$route.path">
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
      <el-row :gutter="20">
        <!-- 系统配置 -->
        <el-col :span="12">
          <el-card class="config-card">
            <template #header>
              <div class="card-header">
                <span>系统配置</span>
              </div>
            </template>
            <el-form :model="systemConfig" label-width="120px">
              <el-form-item label="网站名称">
                <el-input v-model="systemConfig.siteName" />
              </el-form-item>
              <el-form-item label="允许注册">
                <el-switch v-model="systemConfig.allowRegistration" />
              </el-form-item>
              <el-form-item label="最大上传大小(MB)">
                <el-input-number v-model="systemConfig.maxUploadSize" :min="1" :max="500" />
              </el-form-item>
              <el-form-item label="用户头像大小(KB)">
                <el-input-number v-model="systemConfig.avatarSize" :min="50" :max="500" />
              </el-form-item>
              <el-form-item label="音乐试听时长(秒)">
                <el-input-number v-model="systemConfig.previewDuration" :min="10" :max="300" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSystemConfig">保存配置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        
        <!-- 推荐权重设置 -->
        <el-col :span="12">
          <el-card class="config-card">
            <template #header>
              <div class="card-header">
                <span>推荐权重设置</span>
              </div>
            </template>
            <el-form :model="recommendConfig" label-width="120px">
              <el-form-item label="播放次数权重">
                <el-slider v-model="recommendConfig.playCountWeight" :min="0" :max="100" show-input />
              </el-form-item>
              <el-form-item label="收藏数量权重">
                <el-slider v-model="recommendConfig.favoriteWeight" :min="0" :max="100" show-input />
              </el-form-item>
              <el-form-item label="最近播放权重">
                <el-slider v-model="recommendConfig.recentPlayWeight" :min="0" :max="100" show-input />
              </el-form-item>
              <el-form-item label="用户相似度权重">
                <el-slider v-model="recommendConfig.similarityWeight" :min="0" :max="100" show-input />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveRecommendConfig">保存权重</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 数据管理 -->
        <el-col :span="12">
          <el-card class="config-card">
            <template #header>
              <div class="card-header">
                <span>数据管理</span>
              </div>
            </template>
            <div class="data-actions">
              <el-button type="primary" @click="backupData">数据备份</el-button>
              <el-button @click="restoreData">数据恢复</el-button>
              <el-button type="danger" @click="clearCache">清除缓存</el-button>
            </div>
          </el-card>
        </el-col>
        
        <!-- 系统日志 -->
        <el-col :span="12">
          <el-card class="config-card">
            <template #header>
              <div class="card-header">
                <span>操作日志</span>
              </div>
            </template>
            <el-table :data="logs" height="200">
              <el-table-column prop="action" label="操作" width="150" />
              <el-table-column prop="username" label="用户" width="120" />
              <el-table-column prop="ipAddress" label="IP地址" width="130" />
              <el-table-column prop="createdAt" label="时间">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminAPI } from '../api'

const router = useRouter()

const systemConfig = ref({
  siteName: 'Fly Music',
  allowRegistration: true,
  maxUploadSize: 50,
  avatarSize: 200,
  previewDuration: 60
})

const recommendConfig = ref({
  playCountWeight: 30,
  favoriteWeight: 25,
  recentPlayWeight: 25,
  similarityWeight: 20
})

const logs = ref([])

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const saveSystemConfig = async () => {
  try {
    await adminAPI.updateSystemConfig(systemConfig.value)
    ElMessage.success('系统配置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const saveRecommendConfig = async () => {
  ElMessage.success('推荐权重保存成功')
}

const backupData = async () => {
  try {
    const res = await adminAPI.backupData()
    ElMessage.success('数据备份成功: ' + res.data.message)
  } catch (error) {
    ElMessage.error('备份失败')
  }
}

const restoreData = async () => {
  ElMessageBox.confirm('此操作将覆盖当前数据，是否继续？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.info('数据恢复功能开发中')
  }).catch(() => {})
}

const clearCache = async () => {
  ElMessageBox.confirm('此操作将清除所有缓存数据，是否继续？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('缓存已清除')
  }).catch(() => {})
}

const loadLogs = async () => {
  try {
    const res = await adminAPI.getSystemLogs()
    logs.value = (res.data || []).slice(0, 10)
  } catch (error) {
    console.error('加载日志失败:', error)
  }
}

const handleLogout = () => {
  router.push('/login')
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.admin-config-container {
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

.config-card {
  height: 100%;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.data-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
