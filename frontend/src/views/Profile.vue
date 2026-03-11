<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
      </template>
      
      <div class="profile-content">
        <div class="avatar-section">
          <el-avatar :size="120" class="avatar">
            {{ userStore.user?.username?.charAt(0) || 'U' }}
          </el-avatar>
          <h3>{{ userStore.user?.username || '用户' }}</h3>
          <p>{{ userStore.user?.email || '未设置邮箱' }}</p>
        </div>
        
        <el-form :model="userForm" label-width="100px" class="profile-form">
          <el-form-item label="用户名">
            <el-input v-model="userForm.username" disabled></el-input>
          </el-form-item>
          
          <el-form-item label="邮箱">
            <el-input v-model="userForm.email"></el-input>
          </el-form-item>
          
          <el-form-item label="手机号">
            <el-input v-model="userForm.phone"></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="updateProfile">更新信息</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userForm = reactive({
  username: '',
  email: '',
  phone: ''
})

onMounted(() => {
  if (userStore.user) {
    userForm.username = userStore.user.username
    userForm.email = userStore.user.email
    userForm.phone = userStore.user.phone
  }
})

const updateProfile = () => {
  // 这里可以添加更新个人信息的逻辑
  ElMessage.success('个人信息更新成功')
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #333;
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

.avatar-section h3 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: bold;
}

.avatar-section p {
  margin: 0;
  color: #666;
}

.profile-form {
  width: 100%;
  max-width: 500px;
}
</style>
