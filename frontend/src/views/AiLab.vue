<template>
  <div class="ai-lab-container">
    <el-card class="ai-lab-card">
      <template #header>
        <div class="card-header">
          <h2>AI音乐实验室</h2>
        </div>
      </template>
      
      <!-- AI文本生成音乐 -->
      <div class="section">
        <h3 class="section-title">AI文本生成音乐</h3>
        <el-form :model="textToMusicForm" label-width="100px" class="ai-form">
          <el-form-item label="输入文本">
            <el-input
              v-model="textToMusicForm.prompt"
              type="textarea"
              :rows="4"
              placeholder="请输入音乐描述，例如：一首欢快的电子舞曲，适合派对"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="音乐风格">
            <el-select v-model="textToMusicForm.style" placeholder="选择音乐风格">
              <el-option label="流行" value="pop"></el-option>
              <el-option label="摇滚" value="rock"></el-option>
              <el-option label="电子" value="electronic"></el-option>
              <el-option label="古典" value="classical"></el-option>
              <el-option label="爵士" value="jazz"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="时长">
            <el-select v-model="textToMusicForm.duration" placeholder="选择时长">
              <el-option label="30秒" value="30"></el-option>
              <el-option label="1分钟" value="60"></el-option>
              <el-option label="2分钟" value="120"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="generateMusic">生成音乐</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- AI听歌识别曲风 -->
      <div class="section">
        <h3 class="section-title">AI听歌识别曲风</h3>
        <el-form :model="musicRecognitionForm" label-width="100px" class="ai-form">
          <el-form-item label="上传音乐">
            <el-upload
              class="upload-demo"
              action="#"
              :auto-upload="false"
              :on-change="handleFileChange"
              :limit="1"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon> 选择文件
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  请上传MP3、WAV格式的音乐文件，大小不超过10MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="recognizeMusic">识别曲风</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 生成结果 -->
      <div class="section" v-if="generatedMusic">
        <h3 class="section-title">生成结果</h3>
        <el-card shadow="hover" class="result-card">
          <div class="result-info">
            <h4>{{ generatedMusic.title }}</h4>
            <p>{{ generatedMusic.description }}</p>
            <el-audio :src="generatedMusic.url" controls></el-audio>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Upload } from '@element-plus/icons-vue'

const textToMusicForm = reactive({
  prompt: '',
  style: '',
  duration: ''
})

const musicRecognitionForm = reactive({
  file: null
})

const generatedMusic = ref(null)

const handleFileChange = (file) => {
  musicRecognitionForm.file = file.raw
}

const generateMusic = () => {
  // 生成音乐逻辑
  console.log('Generating music with prompt:', textToMusicForm.prompt)
  // 模拟生成结果
  generatedMusic.value = {
    title: 'AI生成音乐',
    description: '基于您的描述生成的音乐',
    url: 'https://example.com/music.mp3'
  }
}

const recognizeMusic = () => {
  // 识别音乐逻辑
  console.log('Recognizing music:', musicRecognitionForm.file)
  // 模拟识别结果
  generatedMusic.value = {
    title: '识别结果',
    description: '电子舞曲，节奏明快，适合派对',
    url: 'https://example.com/music.mp3'
  }
}
</script>

<style scoped>
.ai-lab-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.ai-lab-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.section {
  margin-top: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.ai-form {
  max-width: 600px;
}

.result-card {
  margin-top: 20px;
}

.result-info {
  text-align: center;
}

.result-info h4 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: bold;
}

.result-info p {
  margin: 0 0 20px 0;
  color: #666;
}

.el-audio {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}
</style>
