<template>
  <div class="admin-navbar">
    <div class="logo" @click="$router.push('/')">FlyMusic</div>
    <el-menu
      mode="horizontal"
      :default-active="currentRoute"
      class="nav-menu"
      background-color="transparent"
      text-color="rgba(255, 255, 255, 0.8)"
      active-text-color="#fff"
      router
    >
      <el-menu-item index="/admin">概览</el-menu-item>
      <el-menu-item index="/admin/users">用户管理</el-menu-item>
      <el-menu-item index="/admin/songs">音乐管理</el-menu-item>
      <el-menu-item index="/admin/categories">分类管理</el-menu-item>
      <el-menu-item index="/admin/recommendation">推荐管理</el-menu-item>
      <el-menu-item index="/admin/config">系统配置</el-menu-item>
    </el-menu>
    <div class="user-menu">
      <el-dropdown @command="handleCommand">
        <span class="user-avatar">
          <el-icon><User /></el-icon>
          {{ userStore.user?.username || '管理员' }}
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="home">返回前台</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const currentRoute = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    router.push('/login')
  } else if (command === 'home') {
    router.push('/')
  }
}
</script>

<style scoped>
.admin-navbar {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  height: 60px;
}

.logo {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  cursor: pointer;
  letter-spacing: 1px;
  flex-shrink: 0;
}

.nav-menu {
  flex: 1;
  margin: 0 30px;
  border: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-menu :deep(.el-menu-item:hover) {
  background: rgba(102, 126, 234, 0.2);
  color: #fff;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 8px;
}

.user-menu {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.user-avatar {
  cursor: pointer;
  padding: 8px 20px;
  border-radius: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
</style>
