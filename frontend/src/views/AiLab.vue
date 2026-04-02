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
                      :class="{ active: textToMusicForm.genre === style.value }"
                      @click="textToMusicForm.genre = style.value"
                    >
                      {{ style.label }}
                    </el-tag>
                  </div>
                </el-form-item>

                <el-form-item label="音乐心情">
                  <div class="style-tags">
                    <el-tag 
                      v-for="m in musicMoods" 
                      :key="m.value"
                      :class="{ active: textToMusicForm.mood === m.value }"
                      @click="textToMusicForm.mood = m.value"
                      type="success"
                    >
                      {{ m.label }}
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
                  accept=".mp3,.wav,.flac,.m4a"
                >
                  <div class="upload-content">
                    <el-icon class="upload-icon"><Upload /></el-icon>
                    <div class="upload-text">拖拽音乐文件到此处</div>
                    <div class="upload-hint">支持 MP3、WAV、FLAC、M4A 格式</div>
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

              <div v-if="recognitionResult" class="recognition-result">
                <el-divider>识别结果</el-divider>
                <div class="result-grid">
                  <div class="result-item">
                    <span class="result-label">曲风</span>
                    <span class="result-value">{{ recognitionResult.genre }}</span>
                  </div>
                  <div class="result-item">
                    <span class="result-label">心情</span>
                    <span class="result-value">{{ recognitionResult.mood }}</span>
                  </div>
                  <div class="result-item">
                    <span class="result-label">BPM</span>
                    <span class="result-value">{{ recognitionResult.bpm }}</span>
                  </div>
                  <div class="result-item">
                    <span class="result-label">调式</span>
                    <span class="result-value">{{ recognitionResult.key }}</span>
                  </div>
                  <div class="result-item">
                    <span class="result-label">节拍</span>
                    <span class="result-value">{{ recognitionResult.timeSignature }}</span>
                  </div>
                  <div class="result-item">
                    <span class="result-label">乐器</span>
                    <span class="result-value">{{ recognitionResult.instruments?.join(', ') }}</span>
                  </div>
                </div>
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
                <img :src="item.cover || defaultCover" alt="cover">
                <el-button circle class="play-btn" type="primary" @click="playAiMusic(item)">
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
                <div v-if="item.status === 0" class="status-badge generating">生成中</div>
                <div v-if="item.status === 2" class="status-badge failed">失败</div>
              </div>
              <div class="history-info">
                <h4>{{ item.prompt?.slice(0, 20) || 'AI生成音乐' }}...</h4>
                <p>{{ item.genre }} · {{ item.mood }}</p>
                <span class="history-time">{{ formatTime(item.createdAt) }}</span>
              </div>
              <el-button text type="danger" @click="deleteAiRecord(item.id)">
                <el-icon><Delete /></el-icon>
              </el-button>
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
              <h3>{{ generatedResult.prompt?.slice(0, 30) }}...</h3>
              <p>{{ generatedResult.genre }} · {{ generatedResult.mood }} · {{ generatedResult.duration }}秒</p>
              <div class="result-tags">
                <el-tag>{{ generatedResult.genre }}</el-tag>
                <el-tag type="success">{{ generatedResult.duration }}秒</el-tag>
              </div>
              <div class="result-actions">
                <el-button type="primary" size="large" @click="playGeneratedMusic">
                  <el-icon><VideoPlay /></el-icon> 播放
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
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import aiApi from '../api/ai'
import { MagicStick, Edit, Microphone, Upload, Document, Delete, VideoPlay, Download, Share, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('5')
const defaultAvatar = DEFAULT_IMAGES.avatar
const defaultCover = DEFAULT_IMAGES.cover

const isGenerating = ref(false)
const isRecognizing = ref(false)
const uploadedFile = ref(null)
const generatedResult = ref(null)
const recognitionResult = ref(null)
const waveCanvas = ref(null)
const currentGenerationId = ref(null)
const pollingTimer = ref(null)

const textToMusicForm = reactive({
  prompt: '',
  genre: '流行',
  mood: '欢快',
  duration: '60'
})

const musicStyles = [
  { label: '流行', value: '流行' },
  { label: '摇滚', value: '摇滚' },
  { label: '电子', value: '电子' },
  { label: '古典', value: '古典' },
  { label: '爵士', value: '爵士' },
  { label: '民谣', value: '民谣' },
  { label: '说唱', value: '说唱' },
  { label: 'R&B', value: 'R&B' }
]

const musicMoods = [
  { label: '欢快', value: '欢快' },
  { label: '平静', value: '平静' },
  { label: '悲伤', value: '悲伤' },
  { label: '激昂', value: '激昂' },
  { label: '浪漫', value: '浪漫' },
  { label: '神秘', value: '神秘' }
]

const aiHistory = ref([])

const loadHistory = async () => {
  try {
    const userId = userStore.user?.id || 2
    const res = await aiApi.getUserGenerations(userId)
    aiHistory.value = res.data.data || []
  } catch (e) {
    console.error('加载历史失败:', e)
  }
}

const handleFileChange = (file) => {
  uploadedFile.value = file.raw
  recognitionResult.value = null
}

const removeFile = () => {
  uploadedFile.value = null
  recognitionResult.value = null
}

const generateMusic = async () => {
  if (!textToMusicForm.prompt) {
    ElMessage.warning('请输入音乐描述')
    return
  }
  
  isGenerating.value = true
  generatedResult.value = null
  
  try {
    const userId = userStore.user?.id || 2
    await aiApi.generateMusic(
      textToMusicForm.prompt,
      textToMusicForm.genre,
      textToMusicForm.mood,
      parseInt(textToMusicForm.duration),
      userId
    )
    
    ElMessage.success('AI音乐生成任务已提交，请稍后查看结果')
    
    await loadHistory()
    
  } catch (e) {
    ElMessage.error('生成失败: ' + (e.message || '未知错误'))
  } finally {
    isGenerating.value = false
  }
}

const checkGenerationStatus = async (id) => {
  try {
    const res = await aiApi.getGenerationStatus(id)
    const status = res.data.data
    
    if (status.status === 1) {
      generatedResult.value = status
      clearInterval(pollingTimer.value)
      ElMessage.success('音乐生成完成！')
      await loadHistory()
    } else if (status.status === 2) {
      clearInterval(pollingTimer.value)
      ElMessage.error('音乐生成失败: ' + status.errorMessage)
    }
  } catch (e) {
    console.error('查询状态失败:', e)
  }
}

const recognizeMusic = async () => {
  if (!uploadedFile.value) {
    ElMessage.warning('请先上传音乐文件')
    return
  }
  
  isRecognizing.value = true
  recognitionResult.value = null
  
  try {
    const res = await aiApi.recognizeMusicStyle(uploadedFile.value)
    recognitionResult.value = res.data.data
    ElMessage.success('识别成功')
  } catch (e) {
    ElMessage.error('识别失败: ' + (e.message || '未知错误'))
  } finally {
    isRecognizing.value = false
  }
}

const playAiMusic = (item) => {
  if (!item.musicUrl) {
    ElMessage.warning('音乐还在生成中，请稍后再试')
    return
  }
  
  const url = aiApi.getSongUrl(item.musicUrl)
  playerStore.playSong({
    id: item.id,
    title: item.prompt?.slice(0, 20) || 'AI生成音乐',
    url: url,
    cover: item.cover,
    duration: item.duration
  })
}

const playGeneratedMusic = () => {
  if (generatedResult.value && generatedResult.value.musicUrl) {
    const url = aiApi.getSongUrl(generatedResult.value.musicUrl)
    playerStore.playSong({
      id: generatedResult.value.id,
      title: generatedResult.value.prompt?.slice(0, 20) || 'AI生成音乐',
      url: url,
      cover: generatedResult.value.cover,
      duration: generatedResult.value.duration
    })
  }
}

const deleteAiRecord = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await aiApi.deleteGeneration(id)
    ElMessage.success('删除成功')
    await loadHistory()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString()
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.ai-lab-page {
  min-height: 100vh;
  background: #0a0a0a;
}

.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(20px);
  position: fixed;
  width: 100%;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-menu {
  border: none;
  background: transparent;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.ai-lab-hero {
  position: relative;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  overflow: hidden;
  margin-top: 60px;
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
  background: rgba(102, 126, 234, 0.6);
  border-radius: 50%;
  left: var(--x);
  top: var(--y);
  animation: float 15s infinite ease-in-out;
  animation-delay: var(--delay);
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.6; }
  50% { transform: translateY(-100px) scale(1.5); opacity: 0.3; }
}

.hero-content {
  position: relative;
  text-align: center;
  z-index: 10;
}

.ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: white;
  font-size: 14px;
  margin-bottom: 20px;
}

.hero-title {
  font-size: 48px;
  color: white;
  margin-bottom: 10px;
}

.hero-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
}

.main-content {
  padding: 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.labs-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.lab-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 30px;
}

.lab-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
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
  color: white;
  margin-bottom: 10px;
}

.lab-desc {
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 20px;
}

.style-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.style-tags .el-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.style-tags .el-tag.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.duration-group {
  margin-top: 10px;
}

.generate-btn, .recognize-btn {
  width: 100%;
  margin-top: 20px;
  height: 50px;
  font-size: 16px;
}

.music-uploader {
  width: 100%;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.file-name {
  flex: 1;
  color: white;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recognition-result {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-top: 15px;
}

.result-item {
  background: rgba(255, 255, 255, 0.05);
  padding: 15px;
  border-radius: 8px;
  text-align: center;
}

.result-label {
  display: block;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  margin-bottom: 5px;
}

.result-value {
  color: white;
  font-size: 16px;
  font-weight: bold;
}

.history-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  transition: all 0.3s;
}

.history-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.history-cover {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
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

.status-badge {
  position: absolute;
  bottom: 5px;
  right: 5px;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  color: white;
}

.status-badge.generating {
  background: #e6a23c;
}

.status-badge.failed {
  background: #f56c6c;
}

.history-info {
  flex: 1;
}

.history-info h4 {
  color: white;
  margin: 0 0 5px 0;
}

.history-info p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  margin: 0;
}

.history-time {
  color: rgba(255, 255, 255, 0.4);
  font-size: 12px;
}

.result-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
}

.result-content {
  display: flex;
  gap: 30px;
  align-items: center;
}

.result-wave {
  flex-shrink: 0;
}

.result-info h3 {
  color: white;
  margin: 0 0 10px 0;
}

.result-info p {
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 15px;
}

.result-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.result-actions {
  display: flex;
  gap: 10px;
}

:deep(.el-card) {
  background: transparent;
  border: none;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
}

:deep(.el-radio-button__inner) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}
</style>
