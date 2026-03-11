<template>
  <div class="recommendations-container">
    <el-card class="recommendations-card">
      <template #header>
        <div class="card-header">
          <h2>个性化推荐</h2>
        </div>
      </template>
      
      <!-- 每日推荐 -->
      <div class="section">
        <h3 class="section-title">每日推荐</h3>
        <el-row :gutter="20">
          <el-col :span="6" v-for="song in dailyRecommendations" :key="song.id" class="song-card">
            <el-card shadow="hover">
              <img :src="song.cover" alt="song" class="song-cover">
              <div class="song-info">
                <h4>{{ song.title }}</h4>
                <p>{{ song.artist }}</p>
                <el-button type="primary" size="small" class="play-button">播放</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 相似推荐 -->
      <div class="section">
        <h3 class="section-title">相似推荐</h3>
        <el-row :gutter="20">
          <el-col :span="6" v-for="song in similarRecommendations" :key="song.id" class="song-card">
            <el-card shadow="hover">
              <img :src="song.cover" alt="song" class="song-cover">
              <div class="song-info">
                <h4>{{ song.title }}</h4>
                <p>{{ song.artist }}</p>
                <el-button type="primary" size="small" class="play-button">播放</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 热门推荐 -->
      <div class="section">
        <h3 class="section-title">热门推荐</h3>
        <el-row :gutter="20">
          <el-col :span="6" v-for="song in hotRecommendations" :key="song.id" class="song-card">
            <el-card shadow="hover">
              <img :src="song.cover" alt="song" class="song-cover">
              <div class="song-info">
                <h4>{{ song.title }}</h4>
                <p>{{ song.artist }}</p>
                <el-button type="primary" size="small" class="play-button">播放</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

// 模拟数据
const dailyRecommendations = ref([
  { id: 1, title: '孤勇者', artist: '陈奕迅', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20brave%20soul&image_size=square' },
  { id: 2, title: '起风了', artist: '买辣椒也用券', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20wind%20blowing&image_size=square' },
  { id: 3, title: '海阔天空', artist: 'Beyond', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20ocean%20sky&image_size=square' },
  { id: 4, title: '平凡之路', artist: '朴树', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20ordinary%20road&image_size=square' }
])

const similarRecommendations = ref([
  { id: 5, title: '光年之外', artist: '邓紫棋', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20light%20year&image_size=square' },
  { id: 6, title: '病变', artist: '鞠文娴', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20sickness&image_size=square' },
  { id: 7, title: '嚣张', artist: 'en', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20arrogant&image_size=square' },
  { id: 8, title: '芒种', artist: '音阙诗听', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20芒种&image_size=square' }
])

const hotRecommendations = ref([
  { id: 9, title: '野狼Disco', artist: '宝石Gem', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20wolf%20disco&image_size=square' },
  { id: 10, title: '下山', artist: '要不要买菜', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20mountain&image_size=square' },
  { id: 11, title: '江南', artist: '林俊杰', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20jiangnan&image_size=square' },
  { id: 12, title: '倒带', artist: '周杰伦', cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20rewind&image_size=square' }
])
</script>

<style scoped>
.recommendations-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.recommendations-card {
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

.song-card {
  margin-bottom: 20px;
}

.song-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 10px;
}

.song-info {
  text-align: center;
}

.song-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
}

.song-info p {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}

.play-button {
  width: 100%;
}
</style>
