<template>
  <div class="navbar">
    <div class="logo">
      <span class="logo-icon">🎵</span>
      <span class="logo-text" @click="$router.push('/')">Fly Music</span>
    </div>
    <el-menu :default-active="activeIndex" mode="horizontal" class="nav-menu" :router="true">
      <el-menu-item index="/" route="/">首页</el-menu-item>
      <el-menu-item index="/songs" route="/songs">音乐</el-menu-item>
      <el-menu-item index="/artists" route="/artists">歌手</el-menu-item>
      <el-menu-item index="/playlists" route="/playlists">歌单</el-menu-item>
      <el-menu-item index="/recommendations" route="/recommendations">推荐</el-menu-item>
      <el-menu-item index="/ai-lab" route="/ai-lab">AI实验室</el-menu-item>
      <el-menu-item index="/social" route="/social">社交</el-menu-item>
    </el-menu>
    <div class="user-menu">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索音乐、歌手..."
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <template v-if="userStore.isLoggedIn">
        <el-badge :value="3" class="notification-badge">
          <el-icon size="20"><Bell /></el-icon>
        </el-badge>
        <el-dropdown>
          <div class="user-info">
            <el-avatar :size="36" :src="userStore.user?.avatar || defaultAvatar">
              {{ userStore.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
            </el-avatar>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/profile')">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/profile?tab=favorites')">
                <el-icon><Star /></el-icon>我的收藏
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/profile?tab=history')">
                <el-icon><Clock /></el-icon>播放历史
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <template v-else>
        <el-button text @click="$router.push('/login')">登录</el-button>
        <el-button type="primary" @click="$router.push('/register')">注册</el-button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { Search, Bell, User, Star, Clock, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const defaultAvatar = DEFAULT_IMAGES.avatar

const searchKeyword = ref('')

const activeIndex = computed(() => {
  const path = route.path
  if (path === '/') return '/'
  return '/' + path.split('/')[1]
})

const handleSearch = () => {
  if (!searchKeyword.value.trim()) return
  router.push({ path: '/songs', query: { search: searchKeyword.value } })
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

onMounted(() => {
  if (userStore.isLoggedIn && !userStore.user) {
    userStore.getUserProfile()
  }
})
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background: linear-gradient(90deg, #1a1a2e 0%, #16213e 100%);
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 30px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
}

.nav-menu {
  flex: 1;
  background: transparent;
  border: none;
}

.nav-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.7);
  font-size: 15px;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.nav-menu :deep(.el-menu--horizontal) {
  border: none;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.search-input {
  width: 200px;
}

.search-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.search-input :deep(.el-input__inner) {
  color: #fff;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.search-input :deep(.el-icon) {
  color: rgba(255, 255, 255, 0.5);
}

.notification-badge {
  cursor: pointer;
}

.notification-badge :deep(.el-icon) {
  color: #fff;
}

.user-info {
  cursor: pointer;
}

.user-info :deep(.el-avatar) {
  background: #409eff;
}
</style>
