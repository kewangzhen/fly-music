<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
      </template>
      
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import userApi from '../api/user'

const userStore = useUserStore()
const loading = ref(false)
const pageLoading = ref(true)

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

const goToVip = () => {
  router.push('/vip')
}

const router = useRouter()

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

onMounted(async () => {
  if (!userStore.user || !userStore.user.id) {
    await userStore.getUserProfile()
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
</style>
