<template>
  <div class="reset-page">
    <div class="reset-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <div class="reset-container">
      <div class="reset-brand">
        <div class="brand-logo">🎵</div>
        <h1 class="brand-name">Fly Music</h1>
      </div>
      
      <el-card class="reset-card">
        <h2 class="card-title">重置密码</h2>
        
        <div v-if="!tokenValid" class="error-message">
          <el-icon :size="48"><WarningFilled /></el-icon>
          <p>链接无效或已过期</p>
          <el-button type="primary" @click="goToLogin">返回登录</el-button>
        </div>
        
        <el-form 
          v-else-if="!resetSuccess"
          :model="resetForm" 
          :rules="resetRules" 
          ref="resetFormRef" 
          class="reset-form"
        >
          <el-form-item prop="password">
            <el-input 
              v-model="resetForm.password" 
              type="password" 
              placeholder="请输入新密码"
              size="large"
              prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="resetForm.confirmPassword" 
              type="password" 
              placeholder="请再次输入新密码"
              size="large"
              prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-button 
            type="primary" 
            size="large" 
            class="submit-btn"
            @click="handleResetPassword"
            :loading="loading"
          >
            重置密码
          </el-button>
        </el-form>
        
        <div v-else class="success-message">
          <el-icon :size="48" color="#67c23a"><CircleCheckFilled /></el-icon>
          <p>密码重置成功</p>
          <el-button type="primary" @click="goToLogin">返回登录</el-button>
        </div>
        
        <p class="back-link">
          <el-link type="primary" :underline="false" @click="goToLogin">
            <el-icon><ArrowLeft /></el-icon> 返回登录
          </el-link>
        </p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { WarningFilled, CircleCheckFilled, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const tokenValid = ref(true)
const resetSuccess = ref(false)
const resetFormRef = ref(null)

const resetForm = reactive({
  password: '',
  confirmPassword: ''
})

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度至少6位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== resetForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const resetRules = {
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleResetPassword = async () => {
  const valid = await resetFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const token = route.query.token
    const res = await userStore.updatePasswordByToken(token, resetForm.password)
    if (res) {
      resetSuccess.value = true
    } else {
      ElMessage.error('重置失败，请重试')
    }
  } catch (error) {
    ElMessage.error('重置失败，请重试')
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}

onMounted(async () => {
  const token = route.query.token
  if (!token) {
    tokenValid.value = false
    return
  }
  
  const valid = await userStore.verifyToken(token)
  tokenValid.value = valid
})
</script>

<style scoped>
.reset-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  position: relative;
  overflow: hidden;
}

.reset-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.shape-1 {
  width: 600px;
  height: 600px;
  background: #409eff;
  top: -200px;
  left: -200px;
  animation: float 20s ease-in-out infinite;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #67c23a;
  bottom: -100px;
  right: -100px;
  animation: float 15s ease-in-out infinite reverse;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: #e6a23c;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float 18s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(10deg);
  }
}

.reset-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.reset-brand {
  text-align: center;
  margin-bottom: 30px;
}

.brand-logo {
  font-size: 48px;
  margin-bottom: 10px;
}

.brand-name {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.reset-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.card-title {
  text-align: center;
  margin: 0 0 24px;
  color: #303133;
  font-size: 20px;
}

.reset-form {
  margin-top: 20px;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
}

.back-link {
  text-align: center;
  margin-top: 20px;
}

.error-message,
.success-message {
  text-align: center;
  padding: 40px 20px;
}

.error-message p,
.success-message p {
  font-size: 16px;
  color: #606266;
  margin: 20px 0;
}
</style>
