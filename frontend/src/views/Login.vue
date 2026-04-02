<template>
  <div class="login-page">
    <div class="login-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <div class="login-container">
      <div class="login-brand">
        <div class="brand-logo">🎵</div>
        <h1 class="brand-name">Fly Music</h1>
        <p class="brand-slogan">让音乐飞入你心</p>
      </div>
      
      <el-card class="login-card">
        <div class="card-tabs">
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'login' }"
            @click="activeTab = 'login'"
          >
            登录
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'register' }"
            @click="activeTab = 'register'"
          >
            注册
          </div>
        </div>
        
        <el-form 
          v-if="activeTab === 'login'" 
          :model="loginForm" 
          :rules="loginRules" 
          ref="loginFormRef" 
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="用户名 / 邮箱 / 手机号"
              size="large"
              :prefix-icon="User"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            ></el-input>
          </el-form-item>
          
          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false" @click="showForgotDialog = true">忘记密码？</el-link>
          </div>
          
          <el-button 
            type="primary" 
            size="large" 
            class="submit-btn"
            @click="handleLogin"
            :loading="loading"
          >
            登录
          </el-button>
          
          <div class="social-login">
            <span class="divider">
              <span>或</span>
            </span>
            <div class="social-icons">
              <div class="social-icon wechat">
                <svg viewBox="0 0 24 24" width="24" height="24"><path fill="currentColor" d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.045c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 0 1-.023-.156.49.49 0 0 1 .201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89c-.135-.01-.27-.027-.407-.03zm-2.53 3.274c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.969-.982z"/></svg>
              </div>
              <div class="social-icon qq">
                <svg viewBox="0 0 24 24" width="24" height="24"><path fill="currentColor" d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 3.6c1.324 0 2.4 1.076 2.4 2.4s-1.076 2.4-2.4 2.4S9.6 7.324 9.6 6 10.676 3.6 12 3.6zm4.8 9.6c-1.596-1.392-3.818-2.4-4.8-2.4s-3.204 1.008-4.8 2.4A6.972 6.972 0 0 1 12 18c1.512 0 3.228-.672 4.8-1.8zm-4.8 2.4c-2.4 0-6-1.2-6-3.6V10.8h12v4.8c0 2.4-3.6 3.6-6 3.6z"/></svg>
              </div>
              <div class="social-icon weibo">
                <svg viewBox="0 0 24 24" width="24" height="24"><path fill="currentColor" d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.739 5.443zM9.05 17.219c-.384.616-1.208.884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.194.573zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm9.143-.495c-.362-.097-.611-.166-.424-.458.18-.282.414-.523.77-.688.089.195.19.396.307.599-.28.2-.441.41-.653.547zm-1.311-1.534c.186-.146.276-.232.264-.352-.011-.119-.129-.228-.147-.144-.029.139.04.313.085.438.126.176.206.267.176.347-.025.068-.067.06-.142.004-.062-.054-.135-.146-.236-.293zm1.051-2.115c-1.296-.322-2.906-.165-4.224.615-1.165.689-1.743 1.741-1.566 2.833.177 1.092.989 1.912 2.167 2.165 1.18.254 2.531-.102 3.565-.894.141-.108.276-.221.404-.34.089-.085.177-.175.265-.257l.146-.134c.282-.263.443-.461.269-.687-.055-.071-.139-.094-.224-.094-.211 0-.461.141-.677.293-.229.161-.425.339-.585.499-.135.136-.252.252-.352.352l-.146.146c-.089.094-.201.177-.352.268l-.078.05A12.683 12.683 0 0 1 14.57 8.91c.053-.054.115-.104.177-.153.226-.179.449-.312.611-.401l.146-.078c.09-.039.168-.063.231-.073.049-.007.087.003.119.007.021.009.036.019.055.033.108.074.168.131.199.162.072.041.116.072.16.102.135.102.226.182.315.283.047.058.089.112.135.17l.063.086s0 .003-.001.004l-.001.003c.071.095.151.183.239.263.146.138.292.241.387.313l.102.074c.128.094.213.152.236.166.187.113.336.202.47.307.119.096.21.176.302.268.089.084.166.164.243.257l.061.078c.096.134.17.24.226.331.063.095.11.174.158.273.018.042.048.103.094.18l.019.031c.059.119.129.235.2.354.065.12.138.234.203.354l.011.02c.094.17.164.332.236.5l.001.003v.002l.002.008c.074.177.135.36.203.555l.004.011c.063.19.122.37.17.53.003.009.006.013.009.021.059.2.109.38.156.562.023.089.043.175.063.262l.002.008c.02.089.037.172.054.257.023.119.041.221.052.31.012.093.019.18.023.26l.001.016c.004.093.004.165.004.239v.016c0 .08-.002.16-.007.244l-.001.023c-.005.091-.014.186-.025.283-.011.091-.024.19-.041.29-.016.089-.036.182-.058.277l-.004.016c-.022.095-.048.19-.074.283-.028.094-.057.177-.086.261l-.006.016c-.03.083-.064.172-.1.264l-.015.036c-.036.085-.075.17-.115.253l-.009.019c-.04.083-.085.166-.129.247l-.015.027c-.044.077-.091.154-.14.229l-.009.015c-.052.078-.102.148-.158.217l-.009.012c-.052.068-.108.136-.161.195l-.011.012c-.059.063-.116.117-.176.172l-.015.014c-.06.055-.12.104-.18.152l-.014.011a7.33 7.33 0 0 1-.194.144l-.013.008a7.444 7.444 0 0 1-.39.263l-.016.009c-.063.036-.124.064-.188.097l-.016.006a7.798 7.798 0 0 1-.39.177l-.017.005a9.143 9.143 0 0 1-.4.145l-.019.005c-.064.019-.125.032-.19.048l-.019.003a9.248 9.248 0 0 1-.78.127l-.029.001a9.132 9.132 0 0 1-.78.023h-.015c-.255-.002-.51-.017-.765-.047l-.013-.002c-.243-.024-.487-.064-.72-.127l-.011-.003a9.088 9.088 0 0 1-.68-.196c-.214-.072-.42-.159-.624-.255l-.012-.006c-.189-.091-.37-.199-.551-.311l-.009-.006c-.17-.106-.333-.225-.495-.35l-.009-.007c-.154-.122-.3-.256-.446-.392l-.004-.003c-.14-.135-.272-.279-.404-.425l-.003-.003c-.127-.143-.246-.296-.364-.451v-.002l-.003-.005c-.111-.152-.214-.314-.315-.477l-.001-.002c-.094-.159-.18-.326-.266-.494l-.003-.006c-.085-.174-.159-.342-.237-.523v-.001l-.001-.002c-.083-.19-.152-.37-.227-.56l-.001-.005c-.059-.15-.114-.296-.164-.439l-.004-.013c-.046-.136-.089-.268-.124-.392l-.003-.011c-.033-.124-.059-.239-.088-.36l-.002-.011c-.023-.121-.04-.234-.059-.354l-.001-.012c-.018-.124-.028-.24-.04-.363v-.014c-.009-.119-.014-.23-.018-.346l-.001-.016c-.002-.111-.002-.214.002-.321v-.015c.004-.106.014-.213.025-.319l.001-.009c.014-.119.034-.235.056-.352l.002-.012c.025-.124.052-.239.084-.36l.003-.014c.033-.116.075-.235.113-.342l.005-.016c.044-.104.092-.202.138-.297l.006-.015c.051-.095.104-.186.161-.282l.005-.011c.059-.096.124-.191.185-.279l.008-.011c.064-.09.132-.177.197-.259l.009-.011c.069-.084.14-.165.213-.247l.007-.008c.074-.08.152-.158.227-.232l.009-.009c.079-.073.159-.145.242-.214l.009-.007c.084-.069.172-.137.259-.2l.01-.006c.09-.063.181-.124.275-.183l.012-.006c.095-.057.191-.111.289-.166l.012-.005c.099-.052.199-.101.301-.15l.014-.005c.104-.045.208-.088.315-.129l.015-.003c.108-.04.217-.075.328-.11l.016-.003c.11-.033.222-.063.335-.091l.018-.002c.112-.028.226-.052.341-.075l.02-.001c.115-.022.23-.04.347-.058l.022-.001c.117-.016.233-.028.352-.038l.025-.001c.118-.01.236-.015.355-.018l.028-.001h.05l.028.001c.119.002.237.01.354.021l.024.002a8.434 8.434 0 0 1 .674.095l.019.004c.104.018.206.04.308.064l.018.004a8.314 8.314 0 0 1 .576.167l.016.005c.091.031.18.064.269.099l.017.007c.087.034.172.07.257.108l.016.007c.085.038.167.078.25.119l.016.009c.081.04.159.082.237.126l.015.008c.079.044.154.089.229.136l.015.01c.075.047.147.095.219.144l.015.011c.071.048.139.097.207.147l.014.011c.069.049.135.099.201.15l.015.012c.064.05.125.101.187.153l.014.012c.06.051.118.103.175.155l.014.013c.056.05.108.102.161.154l.014.014c.051.049.099.099.147.149l.014.015c.046.048.089.097.133.146l.014.016c.041.047.079.094.118.142l.015.018c.037.046.071.092.106.139l.016.02c.033.045.064.09.096.136l.016.022c.029.044.056.088.083.132l.016.025c.024.043.046.086.069.129l.017.028c.019.042.036.084.054.126l.017.031c.014.04.027.081.04.121l.019.034c.009.038.018.076.025.114l.02.039c.006.034.011.069.016.103l.022.045.001.002c.007.034.011.067.017.101l.023.05.001.002c.007.033.011.066.017.098l.025.055.001.003c.007.031.014.063.019.094l.027.058v.001l.001.003c.006.029.011.058.016.087l.029.063.001.002c.006.027.011.055.016.082l.031.065.001.003c.006.025.011.05.016.076l.033.067.001.003c.006.023.011.046.015.069l.035.069.001.003c.005.021.009.042.014.063l.037.07.001.003c.005.019.009.038.012.057l.039.07.001.003.004.016.041.067.002.003.006.014.043.065.002.003.006.013.046.064.002.004.005.012.048.063.003.003.005.011.051.061.003.004.004.01.054.059.003.004.004.009.057.057.003.004.004.008.06.055.003.004.003.007.064.052.003.003.003.007.067.049.003.003.002.006.071.046.003.002.002.006.075.042.003.002.001.005.078.038.004.001.001.004.082.034.003.001.001.004.085.03.004.001.001.003.089.025.004.001 0 .003.092.021.004.001 0 .002.095.016.005 0 0 .002.099.011.004 0-.001.001.102.006.005-.001-.001.001.105.001.005-.002-.001 0 .021-1.6-1.073-3.129-1.596-4.826-1.596h-.017z"/></svg>
              </div>
            </div>
          </div>
        </el-form>
        
        <el-form 
          v-else 
          :model="registerForm" 
          :rules="registerRules" 
          ref="registerFormRef" 
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input 
              v-model="registerForm.email" 
              placeholder="请输入邮箱"
              size="large"
              :prefix-icon="Message"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="请设置密码（至少6位）"
              size="large"
              :prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              placeholder="请确认密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone" 
              placeholder="请输入手机号"
              size="large"
              :prefix-icon="Phone"
            ></el-input>
          </el-form-item>
          
          <el-button 
            type="primary" 
            size="large" 
            class="submit-btn"
            @click="handleRegister"
            :loading="loading"
          >
            注册
          </el-button>
          
          <p class="agreement">
            注册即表示同意
            <el-link type="primary" :underline="false">《用户协议》</el-link>
            和
            <el-link type="primary" :underline="false">《隐私政策》</el-link>
          </p>
        </el-form>
      </el-card>
      
      <p class="back-home">
        <el-link type="primary" :underline="false" @click="$router.push('/')">
          <el-icon><ArrowLeft /></el-icon> 返回首页
        </el-link>
      </p>
    </div>
  </div>

  <el-dialog v-model="showForgotDialog" title="找回密码" width="400px" :close-on-click-modal="false">
    <el-form :model="forgotForm" :rules="forgotRules" ref="forgotFormRef">
      <el-form-item prop="email">
        <el-input 
          v-model="forgotForm.email" 
          placeholder="请输入注册邮箱"
          size="large"
          :prefix-icon="Message"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showForgotDialog = false">取消</el-button>
      <el-button type="primary" :loading="forgotLoading" @click="handleForgotPassword">发送重置链接</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { User, Lock, Message, Phone, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)
const rememberMe = ref(false)

const showForgotDialog = ref(false)
const forgotLoading = ref(false)
const forgotFormRef = ref(null)
const forgotForm = reactive({
  email: ''
})

const forgotRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleForgotPassword = async () => {
  const valid = await forgotFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  forgotLoading.value = true
  try {
    const res = await userStore.resetPassword(forgotForm.email)
    if (res) {
      ElMessage.success('重置链接已发送到您的邮箱，请注意查收')
      showForgotDialog.value = false
    } else {
      ElMessage.error('该邮箱未注册')
    }
  } catch (error) {
    ElMessage.error('发送失败，请重试')
  } finally {
    forgotLoading.value = false
  }
}

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.login(loginForm.username, loginForm.password)
        if (success) {
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error('用户名或密码错误')
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.register({
          username: registerForm.username,
          email: registerForm.email,
          password: registerForm.password,
          phone: registerForm.phone
        })
        if (success) {
          ElMessage.success('注册成功，请登录')
          activeTab.value = 'login'
          loginForm.username = registerForm.username
        } else {
          ElMessage.error('注册失败，请稍后重试')
        }
      } catch (error) {
        ElMessage.error('注册失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s infinite;
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -200px;
  left: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  bottom: -150px;
  right: -100px;
  animation-delay: -5s;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(20px, -20px) rotate(5deg);
  }
  50% {
    transform: translate(-10px, 20px) rotate(-5deg);
  }
  75% {
    transform: translate(-20px, -10px) rotate(3deg);
  }
}

.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
}

.login-brand {
  text-align: center;
  margin-bottom: 40px;
}

.brand-logo {
  font-size: 64px;
  margin-bottom: 16px;
  animation: pulse 2s infinite;
}

.brand-name {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.brand-slogan {
  color: var(--text-secondary);
  font-size: 16px;
}

.login-card {
  width: 420px;
  background: rgba(22, 33, 62, 0.9) !important;
  backdrop-filter: blur(20px);
  border: 1px solid var(--border-color) !important;
  border-radius: 20px !important;
  padding: 20px;
}

.card-tabs {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 1px solid var(--border-color);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px;
  font-size: 18px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
}

.tab-item.active {
  color: var(--primary-color);
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--primary-gradient);
  border-radius: 3px 3px 0 0;
}

.login-form {
  margin-top: 20px;
}

.login-form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: 12px !important;
  padding: 4px 12px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color) !important;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2) !important;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.form-options :deep(.el-checkbox__label) {
  color: var(--text-secondary);
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 12px;
  background: var(--primary-gradient);
  border: none;
  margin-bottom: 20px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.social-login {
  margin-top: 20px;
}

.divider {
  display: flex;
  align-items: center;
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border-color);
}

.divider span {
  padding: 0 16px;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.social-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.social-icon:hover {
  transform: translateY(-3px);
}

.social-icon.wechat {
  background: #07c160;
}

.social-icon.qq {
  background: #12b7f5;
}

.social-icon.weibo {
  background: #e6162d;
}

.agreement {
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
  margin-top: 16px;
}

.back-home {
  margin-top: 24px;
}
</style>
