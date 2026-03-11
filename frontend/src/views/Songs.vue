<template>
  <div class="songs-container">
    <el-card class="songs-card">
      <template #header>
        <div class="card-header">
          <h2>音乐列表</h2>
          <el-input v-model="searchQuery" placeholder="搜索歌曲" class="search-input">
            <template #append>
              <el-button @click="searchSongs">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </template>
      
      <el-table :data="songs" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80">
        </el-table-column>
        <el-table-column label="歌曲信息" min-width="300">
          <template #default="scope">
            <div class="song-info">
              <img :src="scope.row.cover" alt="cover" class="song-cover">
              <div class="song-details">
                <h4>{{ scope.row.title }}</h4>
                <p>{{ scope.row.artist }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="scope">
            {{ formatDuration(scope.row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="playSong(scope.row)">
              <el-icon><VideoPlay /></el-icon> 播放
            </el-button>
            <el-button size="small" @click="addToPlaylist(scope.row)">
              <el-icon><Plus /></el-icon> 收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          layout="prev, pager, next"
          :total="total"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, VideoPlay, Plus } from '@element-plus/icons-vue'

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(40)

// 模拟数据
const songs = ref([
  { id: 1, title: '孤勇者', artist: '陈奕迅', duration: 240, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20brave%20soul&image_size=square' },
  { id: 2, title: '起风了', artist: '买辣椒也用券', duration: 260, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20wind%20blowing&image_size=square' },
  { id: 3, title: '海阔天空', artist: 'Beyond', duration: 300, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20ocean%20sky&image_size=square' },
  { id: 4, title: '平凡之路', artist: '朴树', duration: 280, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20ordinary%20road&image_size=square' },
  { id: 5, title: '光年之外', artist: '邓紫棋', duration: 250, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20light%20year&image_size=square' },
  { id: 6, title: '病变', artist: '鞠文娴', duration: 230, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20sickness&image_size=square' },
  { id: 7, title: '嚣张', artist: 'en', duration: 245, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20arrogant&image_size=square' },
  { id: 8, title: '芒种', artist: '音阙诗听', duration: 210, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20芒种&image_size=square' },
  { id: 9, title: '野狼Disco', artist: '宝石Gem', duration: 265, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20wolf%20disco&image_size=square' },
  { id: 10, title: '下山', artist: '要不要买菜', duration: 220, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover%20mountain&image_size=square' }
])

const formatDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const searchSongs = () => {
  // 搜索逻辑
  console.log('Searching for:', searchQuery.value)
}

const playSong = (song) => {
  // 播放逻辑
  console.log('Playing song:', song.title)
}

const addToPlaylist = (song) => {
  // 添加到歌单逻辑
  console.log('Adding song to playlist:', song.title)
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  // 分页加载逻辑
  console.log('Current page:', page)
}
</script>

<style scoped>
.songs-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.songs-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.search-input {
  width: 300px;
}

.song-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.song-cover {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  object-fit: cover;
}

.song-details h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
}

.song-details p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
