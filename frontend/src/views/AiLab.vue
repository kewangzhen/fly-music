<template>
  <div class="ai-lab-page">
    <el-header height="60px" class="navbar">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-icon">🎵</span>
        <span class="logo-text">Fly Music</span>
      </div>
      <el-menu :default-active="activeIndex" mode="horizontal" class="nav-menu" :router="true">
        <el-menu-item index="/" route="/">首页</el-menu-item>
        <el-menu-item index="/songs" route="/songs">音乐</el-menu-item>
        <el-menu-item index="/playlists" route="/playlists">歌单</el-menu-item>
        <el-menu-item index="/recommendations" route="/recommendations">推荐</el-menu-item>
        <el-menu-item index="/ai-lab" route="/ai-lab">AI实验室</el-menu-item>
        <el-menu-item index="/social" route="/social">社交</el-menu-item>
      </el-menu>
      <div class="user-menu">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar || defaultAvatar">
                {{ userStore.user?.username?.charAt(0)?.toUpperCase() || 'U' }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="$router.push('/login')">登录</el-button>
        </template>
      </div>
    </el-header>

    <div class="ai-lab-hero">
      <div class="hero-bg">
        <div class="hero-particles">
          <span v-for="i in 20" :key="i" class="particle" :style="{ '--delay': `${i * 0.5}s`, '--x': `${Math.random() * 100}%`, '--y': `${Math.random() * 100}%` }"></span>
        </div>
      </div>
      <div class="hero-content">
        <div class="ai-badge">
          <el-icon><MagicStick /></el-icon>
          AI 实验室
        </div>
        <h1 class="hero-title">用AI创造你的音乐</h1>
        <p class="hero-desc">输入文字描述，AI为你创作独特的音乐作品</p>
      </div>
    </div>

    <el-main class="main-content">
      <div class="labs-container">
        <el-row :gutter="30">
          <el-col :span="12">
            <el-card class="lab-card text-to-music" shadow="hover">
              <div class="lab-icon">
                <el-icon size="40"><Edit /></el-icon>
              </div>
              <h2 class="lab-title">文本生成音乐</h2>
              <p class="lab-desc">用文字描述你的音乐想法，AI将为你创作独特的旋律</p>
              
              <el-form :model="textToMusicForm" label-position="top" class="lab-form">
                <el-form-item label="音乐描述">
                  <el-input
                    v-model="textToMusicForm.prompt"
                    type="textarea"
                    :rows="3"
                    placeholder="描述你想要的音乐，如：一首欢快的电子舞曲，节奏明快，充满活力..."
                    maxlength="500"
                    show-word-limit
                  ></el-input>
                </el-form-item>
                
                <el-form-item label="音乐风格">
                  <div class="style-tags">
                    <el-tag 
                      v-for="style in musicStyles" 
                      :key="style.value"
                      :class="{ active: textToMusicForm.style === style.value }"
                      @click="textToMusicForm.style = style.value"
                    >
                      {{ style.label }}
                    </el-tag>
                  </div>
                </el-form-item>
                
                <el-form-item label="音乐时长">
                  <el-radio-group v-model="textToMusicForm.duration" class="duration-group">
                    <el-radio-button label="30">30秒</el-radio-button>
                    <el-radio-button label="60">1分钟</el-radio-button>
                    <el-radio-button label="120">2分钟</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                
                <el-button type="primary" size="large" class="generate-btn" @click="generateMusic" :loading="isGenerating">
                  <el-icon v-if="!isGenerating"><MagicStick /></el-icon>
                  {{ isGenerating ? '生成中...' : '生成音乐' }}
                </el-button>
              </el-form>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card class="lab-card music-recognition" shadow="hover">
              <div class="lab-icon">
                <el-icon size="40"><Microphone /></el-icon>
              </div>
              <h2 class="lab-title">AI听歌识曲</h2>
              <p class="lab-desc">上传音乐文件，AI智能识别曲风、节拍和音乐元素</p>
              
              <div class="upload-section">
                <el-upload
                  class="music-uploader"
                  drag
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  accept=".mp3,.wav,.flac"
                >
                  <div class="upload-content">
                    <el-icon class="upload-icon"><Upload /></el-icon>
                    <div class="upload-text">拖拽音乐文件到此处</div>
                    <div class="upload-hint">支持 MP3、WAV、FLAC 格式</div>
                  </div>
                </el-upload>
                
                <div v-if="uploadedFile" class="file-info">
                  <el-icon><Document /></el-icon>
                  <span class="file-name">{{ uploadedFile.name }}</span>
                  <el-button text type="danger" @click="removeFile">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
                
                <el-button type="primary" size="large" class="recognize-btn" @click="recognizeMusic" :disabled="!uploadedFile" :loading="isRecognizing">
                  <el-icon v-if="!isRecognizing"><Microphone /></el-icon>
                  {{ isRecognizing ? '识别中...' : '开始识别' }}
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-card class="history-card" v-if="aiHistory.length > 0">
          <template #header>
            <div class="card-header">
              <span>生成历史</span>
            </div>
          </template>
          <div class="history-list">
            <div v-for="item in aiHistory" :key="item.id" class="history-item">
              <div class="history-cover">
                <img :src="item.cover" alt="cover">
                <el-button circle class="play-btn" type="primary">
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
              </div>
              <div class="history-info">
                <h4>{{ item.title }}</h4>
                <p>{{ item.description }}</p>
                <span class="history-time">{{ item.time }}</span>
              </div>
              <el-button text>下载</el-button>
            </div>
          </div>
        </el-card>

        <el-card class="result-card" v-if="generatedResult" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>生成结果</span>
              <el-button text @click="generatedResult = null">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="result-content">
            <div class="result-wave">
              <canvas ref="waveCanvas" width="600" height="100"></canvas>
            </div>
            <div class="result-info">
              <h3>{{ generatedResult.title }}</h3>
              <p>{{ generatedResult.description }}</p>
              <div class="result-tags">
                <el-tag>{{ generatedResult.style }}</el-tag>
                <el-tag type="success">{{ generatedResult.duration }}</el-tag>
              </div>
              <div class="result-actions">
                <el-button type="primary" size="large">
                  <el-icon><VideoPlay /></el-icon> 播放
                </el-button>
                <el-button size="large">
                  <el-icon><Download /></el-icon> 下载
                </el-button>
                <el-button size="large">
                  <el-icon><Share /></el-icon> 分享
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { MagicStick, Edit, Microphone, Upload, Document, Delete, VideoPlay, Download, Share, Close } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('5')
const defaultAvatar = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'

const isGenerating = ref(false)
const isRecognizing = ref(false)
const uploadedFile = ref(null)
const generatedResult = ref(null)
const waveCanvas = ref(null)

const textToMusicForm = reactive({
  prompt: '',
  style: '',
  duration: '60'
})

const musicStyles = [
  { label: '流行', value: 'pop' },
  { label: '摇滚', value: 'rock' },
  { label: '电子', value: 'electronic' },
  { label: '古典', value: 'classical' },
  { label: '爵士', value: 'jazz' },
  { label: '民谣', value: 'folk' },
  { label: '嘻哈', value: 'hiphop' },
  { label: 'R&B', value: 'rnb' }
]

const aiHistory = ref([
  { id: 1, title: 'AI生成-001', description: '欢快的电子音乐', cover: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=100', time: '2分钟前' },
  { id: 2, title: 'AI生成-002', description: '舒缓的钢琴曲', cover: 'https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=100', time: '1小时前' },
  { id: 3, title: 'AI生成-003', description: '激昂的摇滚乐', cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=100', time: '昨天' }
])

const handleFileChange = (file) => {
  uploadedFile.value = file.raw
}

const removeFile = () => {
  uploadedFile.value = null
}

const generateMusic = async () => {
  if (!textToMusicForm.prompt) return
  
  isGenerating.value = true
  
  setTimeout(() => {
    const styleLabel = musicStyles.find(s => s.value === textToMusicForm.style)?.label || '混合'
    generatedResult.value = {
      title: `AI生成音乐 - ${new Date().toLocaleTimeString()}`,
      description: textToMusicForm.prompt,
      style: styleLabel,
      duration: `${textToMusicForm.duration}秒`
    }
    isGenerating.value = false
    
    aiHistory.value.unshift({
      id: Date.now(),
      title: 'AI生成-' + String(Date.now()).slice(-3),
      description: textToMusicForm.prompt.slice(0, 30) + '...',
      cover: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=100',
      time: '刚刚'
    })
  }, 3000)
}

const recognizeMusic = async () => {
  isRecognizing.value = true
  
  setTimeout(() => {
    generatedResult.value = {
      title: '识别结果',
      description: '该音乐为电子舞曲风格，节奏明快，BPM约128，适合派对场景',
      style: '电子舞曲',
      duration: '3:45'
    }
    isRecognizing.value = false
  }, 2000)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.init()
})
</script>

<style scoped>
.ai-lab-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: rgba(22, 33, 62, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  cursor: pointer;
}

.ai-lab-hero {
  position: relative;
  padding: 80px 40px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.hero-particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: var(--primary-color);
  border-radius: 50%;
  left: var(--x);
  top: var(--y);
  opacity: 0.3;
  animation: float 15s infinite ease-in-out;
  animation-delay: var(--delay);
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translate(100px, -100px) scale(1.5);
    opacity: 0.6;
  }
}

.hero-content {
  position: relative;
  text-align: center;
  max-width: 800px;
  margin: 0 auto;
}

.ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 24px;
  animation: pulse 2s infinite;
}

.hero-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 50%, #667eea 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient 3s linear infinite;
}

@keyframes gradient {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

.hero-desc {
  font-size: 18px;
  color: var(--text-secondary);
}

.main-content {
  flex: 1;
  padding: 40px;
}

.labs-container {
  max-width: 1200px;
  margin: 0 auto;
}

.lab-card {
  height: 100%;
  border-radius: 20px !important;
  background: var(--card-bg) !important;
  border: 1px solid var(--border-color) !important;
  padding: 30px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.lab-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.lab-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.text-to-music .lab-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.music-recognition .lab-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.lab-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.lab-desc {
  color: var(--text-secondary);
  margin-bottom: 24px;
}

.lab-form :deep(.el-form-item__label) {
  color: var(--text-primary);
}

.style-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.style-tags .el-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.style-tags .el-tag.active {
  background: var(--primary-gradient) !important;
  color: white !important;
}

.duration-group {
  width: 100%;
}

.duration-group :deep(.el-radio-button__inner) {
  width: 100%;
}

.generate-btn,
.recognize-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  background: var(--primary-gradient);
  border: none;
  margin-top: 20px;
}

.generate-btn:hover,
.recognize-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.music-uploader {
  width: 100%;
}

.music-uploader :deep(.el-upload-dragger) {
  background: var(--hover-bg);
  border: 2px dashed var(--border-color);
  border-radius: 12px;
  padding: 40px;
  transition: all 0.3s;
}

.music-uploader :deep(.el-upload-dragger:hover) {
  border-color: var(--primary-color);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-icon {
  font-size: 48px;
  color: var(--primary-color);
}

.upload-text {
  font-size: 16px;
  font-weight: 500;
}

.upload-hint {
  font-size: 13px;
  color: var(--text-secondary);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--hover-bg);
  border-radius: 8px;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-card {
  margin-top: 30px;
  border-radius: 20px !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: var(--hover-bg);
  border-radius: 12px;
  transition: all 0.3s;
}

.history-item:hover {
  background: rgba(102, 126, 234, 0.15);
}

.history-cover {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
}

.history-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.history-cover .play-btn {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s;
}

.history-cover:hover .play-btn {
  opacity: 1;
}

.history-info {
  flex: 1;
}

.history-info h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.history-info p {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.history-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.result-card {
  margin-top: 30px;
  border-radius: 20px !important;
}

.result-content {
  display: flex;
  gap: 30px;
  align-items: center;
}

.result-wave {
  flex-shrink: 0;
  background: var(--dark-bg);
  border-radius: 12px;
  padding: 20px;
}

.result-wave canvas {
  display: block;
}

.result-info h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.result-info p {
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.result-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.result-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 900px) {
  .labs-container .el-col {
    margin-bottom: 24px;
  }
  
  .result-content {
    flex-direction: column;
  }
  
  .hero-title {
    font-size: 32px;
  }
}
</style>
