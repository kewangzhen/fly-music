<template>
  <div class="profile-container">
    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="个人资料" name="profile">
        <el-card class="profile-card">
          <div v-if="pageLoading" style="text-align: center; padding: 40px;">
            <p>加载中...</p>
          </div>
          
          <div v-else class="profile-content">
            <div class="avatar-section">
              <el-avatar :size="120" class="avatar" :src="userForm.avatar">
                {{ userForm.username?.charAt(0) || 'U' }}
              </el-avatar>
              <el-upload
                class="avatar-upload"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-button size="small" type="primary">更换头像</el-button>
              </el-upload>
              <h3>{{ userForm.nickname || userForm.username || '用户' }}</h3>
              <p>{{ userForm.email || '未设置邮箱' }}</p>
              <div class="vip-badge" v-if="userForm.isVip">
                <el-tag type="warning" size="large">VIP会员</el-tag>
                <p class="vip-expire">有效期至：{{ formatDate(userForm.vipExpireAt) }}</p>
              </div>
              <div class="vip-badge" v-else>
                <el-button type="warning" size="small" @click="goToVip">开通VIP</el-button>
              </div>
            </div>
            
            <el-form :model="userForm" label-width="100px" class="profile-form">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled></el-input>
              </el-form-item>
              
              <el-form-item label="昵称">
                <el-input v-model="userForm.nickname" placeholder="请输入昵称"></el-input>
              </el-form-item>
              
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
              </el-form-item>
              
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone" placeholder="请输入手机号"></el-input>
              </el-form-item>
              
              <el-form-item label="性别">
                <el-radio-group v-model="userForm.gender">
                  <el-radio :value="0">保密</el-radio>
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="生日">
                <el-date-picker
                  v-model="userForm.birthdate"
                  type="date"
                  placeholder="选择生日"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="个人简介">
                <el-input
                  v-model="userForm.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入个人简介"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="updateProfile" :loading="loading">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="我的收藏" name="favorites">
        <div class="tab-content">
          <h3>我的收藏</h3>
          <div v-if="favoritesLoading" class="loading">加载中...</div>
          <div v-else-if="favorites.length === 0" class="empty">
            <el-empty description="暂无收藏" />
          </div>
          <div v-else class="songs-list">
            <div 
              v-for="(song, index) in favorites" 
              :key="song.id"
              class="song-item"
            >
              <span class="song-index">{{ index + 1 }}</span>
              <img :src="song.cover || defaultCover" class="song-cover" @click="playSong(song)" />
              <div class="song-info" @click="goToSongDetail(song.id)">
                <div class="song-title">{{ song.title }}</div>
                <div class="song-artist">
                  {{ song.artists?.map(a => a.name).join(', ') || '未知歌手' }}
                </div>
              </div>
              <div class="song-actions">
                <el-button text @click="playSong(song)">
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
                <el-button text @click="removeFavorite(song.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="播放历史" name="history">
        <div class="tab-content">
          <div class="tab-header">
            <h3>播放历史</h3>
            <el-button text type="danger" @click="clearHistory" v-if="playHistory.length > 0">
              清空历史
            </el-button>
          </div>
          <div v-if="historyLoading" class="loading">加载中...</div>
          <div v-else-if="playHistory.length === 0" class="empty">
            <el-empty description="暂无播放历史" />
          </div>
          <div v-else class="songs-list">
            <div 
              v-for="(item, index) in playHistory" 
              :key="item.song?.id || index"
              class="song-item"
            >
              <span class="song-index">{{ index + 1 }}</span>
              <img :src="item.song?.cover || defaultCover" class="song-cover" @click="playSong(item.song)" />
              <div class="song-info" @click="goToSongDetail(item.song?.id)">
                <div class="song-title">{{ item.song?.title || '未知歌曲' }}</div>
                <div class="song-artist">
                  {{ item.song?.artists?.map(a => a.name).join(', ') || '未知歌手' }}
                </div>
              </div>
              <span class="play-time">{{ formatPlayTime(item.playedAt) }}</span>
              <div class="song-actions">
                <el-button text @click="playSong(item.song)">
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
                <el-button text @click="toggleHistoryFavorite(item.song)">
                  <el-icon><Star /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { ElMessage, ElMessageBox } from 'element-plus'
import userApi from '../api/user'
import { VideoPlay, Delete, Star } from '@element-plus/icons-vue'
import { DEFAULT_IMAGES } from '../assets/defaultImages'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const defaultCover = DEFAULT_IMAGES.cover

const activeTab = ref('profile')
const loading = ref(false)
const pageLoading = ref(true)
const favoritesLoading = ref(false)
const historyLoading = ref(false)

const favorites = ref([])
const playHistory = ref([])

const userForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  gender: 0,
  birthdate: '',
  description: '',
  isVip: false,
  vipExpireAt: ''
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatPlayTime = (date) => {
  if (!date) return ''
  const now = new Date()
  const playDate = new Date(date)
  const diff = now - playDate
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return playDate.toLocaleDateString('zh-CN')
}

const goToVip = () => {
  router.push('/vip')
}

const uploadUrl = computed(() => 'http://localhost:8080/api/upload/avatar')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    userForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const loadFavorites = async () => {
  if (!userStore.user?.id) return
  favoritesLoading.value = true
  try {
    const res = await userApi.getFavorites(userStore.user.id)
    if (res.data.code === 200) {
      favorites.value = res.data.data || []
    }
  } catch (error) {
    console.error('获取收藏失败:', error)
  } finally {
    favoritesLoading.value = false
  }
}

const loadHistory = async () => {
  if (!userStore.user?.id) return
  historyLoading.value = true
  try {
    const res = await fetch(`http://localhost:8080/api/play-history/user/${userStore.user.id}/recent?limit=50`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
    const data = await res.json()
    if (data.code === 200) {
      playHistory.value = data.data || []
    }
  } catch (error) {
    console.error('获取播放历史失败:', error)
  } finally {
    historyLoading.value = false
  }
}

const removeFavorite = async (songId) => {
  try {
    await userApi.removeFavorite(userStore.user.id, songId)
    favorites.value = favorites.value.filter(s => s.id !== songId)
    ElMessage.success('已取消收藏')
  } catch (error) {
    ElMessage.error('取消收藏失败')
  }
}

const clearHistory = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有播放历史吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await fetch(`http://localhost:8080/api/play-history/user/${userStore.user.id}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
    playHistory.value = []
    ElMessage.success('已清空播放历史')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

const playSong = (song) => {
  if (song) {
    playerStore.playSong(song)
  }
}

const goToSongDetail = (id) => {
  if (id) {
    router.push(`/song/${id}`)
  }
}

const toggleHistoryFavorite = async (song) => {
  if (!song?.id) return
  await playerStore.toggleFavorite(song.id)
}

onMounted(async () => {
  if (!userStore.user || !userStore.user.id) {
    await userStore.getUserProfile()
  }
  
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  
  if (userStore.user) {
    userForm.username = userStore.user.username || ''
    userForm.nickname = userStore.user.nickname || ''
    userForm.email = userStore.user.email || ''
    userForm.phone = userStore.user.phone || ''
    userForm.avatar = userStore.user.avatar || ''
    userForm.gender = userStore.user.gender || 0
    userForm.birthdate = userStore.user.birthdate || ''
    userForm.description = userStore.user.description || ''
    userForm.isVip = userStore.user.vip || userStore.user.isVip || false
    userForm.vipExpireAt = userStore.user.vipExpireAt || ''
  }
  pageLoading.value = false
  
  if (activeTab.value === 'favorites') {
    loadFavorites()
  } else if (activeTab.value === 'history') {
    loadHistory()
  }
})

const updateProfile = async () => {
  loading.value = true
  try {
    const res = await userApi.updateUser(userStore.user.id, {
      nickname: userForm.nickname,
      email: userForm.email,
      phone: userForm.phone,
      avatar: userForm.avatar,
      gender: userForm.gender,
      birthdate: userForm.birthdate,
      description: userForm.description
    })
    
    if (res.data.code === 200) {
      ElMessage.success('个人信息更新成功')
      userStore.user = { ...userStore.user, ...res.data.data }
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background-color: var(--dark-bg);
  min-height: 100vh;
}

.profile-tabs {
  max-width: 900px;
  margin: 0 auto;
}

.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.profile-tabs :deep(.el-tabs__item) {
  font-size: 16px;
}

.profile-card {
  border-radius: 10px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.avatar {
  margin-bottom: 15px;
  background-color: #667eea;
  font-size: 48px;
  font-weight: bold;
}

.avatar-upload {
  margin-bottom: 10px;
}

.avatar-section h3 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: bold;
}

.avatar-section p {
  margin: 0;
  color: #666;
}

.vip-badge {
  margin-top: 10px;
}

.vip-expire {
  font-size: 12px;
  color: #e6a23c;
  margin: 5px 0 0;
}

.profile-form {
  width: 100%;
  max-width: 500px;
}

.tab-content {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tab-header h3 {
  margin: 0;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.songs-list {
  display: flex;
  flex-direction: column;
}

.song-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
  border-radius: 8px;
  transition: background 0.2s;
}

.song-item:hover {
  background: #f5f5f5;
}

.song-index {
  width: 30px;
  color: #999;
  text-align: center;
}

.song-cover {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  cursor: pointer;
}

.song-info {
  flex: 1;
  cursor: pointer;
  min-width: 0;
}

.song-title {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-artist {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.play-time {
  color: #999;
  font-size: 12px;
  min-width: 60px;
}

.song-actions {
  display: flex;
  gap: 5px;
}
</style>
