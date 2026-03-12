<template>
  <div class="playlists-container">
    <el-card class="playlists-card">
      <template #header>
        <div class="card-header">
          <h2>歌单列表</h2>
          <el-button type="primary" @click="createPlaylist">
            <el-icon><Plus /></el-icon> 创建歌单
          </el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6" v-for="playlist in playlists" :key="playlist.id" class="playlist-card">
          <el-card shadow="hover">
            <img :src="playlist.cover" alt="playlist" class="playlist-cover">
            <div class="playlist-info">
              <h4>{{ playlist.name }}</h4>
              <p>{{ playlist.songCount }}首歌</p>
              <div class="playlist-actions">
                <el-button type="primary" size="small" @click="playPlaylist(playlist)">
                  <el-icon><VideoPlay /></el-icon> 播放
                </el-button>
                <el-button size="small" @click="editPlaylist(playlist)">
                  <el-icon><Edit /></el-icon> 编辑
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Plus, VideoPlay, Edit } from '@element-plus/icons-vue'

// 模拟数据
const playlists = ref([
  { id: 1, name: '流行热歌', songCount: 50, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20pop%20hits&image_size=square' },
  { id: 2, name: '经典老歌', songCount: 100, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20classic%20songs&image_size=square' },
  { id: 3, name: '轻音乐', songCount: 80, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20light%20music&image_size=square' },
  { id: 4, name: '摇滚精选', songCount: 60, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20rock%20music&image_size=square' },
  { id: 5, name: '电音舞曲', songCount: 40, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20electronic%20dance&image_size=square' },
  { id: 6, name: '华语金曲', songCount: 90, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20chinese%20golden%20songs&image_size=square' },
  { id: 7, name: '欧美流行', songCount: 70, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20western%20pop&image_size=square' },
  { id: 8, name: '日语歌曲', songCount: 30, cover: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20japanese%20songs&image_size=square' }
])

const createPlaylist = () => {
  // 创建歌单逻辑
  console.log('Create playlist')
}

const playPlaylist = (playlist) => {
  // 播放歌单逻辑
  console.log('Playing playlist:', playlist.name)
}

const editPlaylist = (playlist) => {
  // 编辑歌单逻辑
  console.log('Editing playlist:', playlist.name)
}
</script>

<style scoped>
.playlists-container {
  padding: 20px;
  background-color: var(--dark-bg);
  min-height: 100vh;
}

.playlists-card {
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

.playlist-card {
  margin-bottom: 20px;
}

.playlist-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 10px;
}

.playlist-info {
  text-align: center;
}

.playlist-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
}

.playlist-info p {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}

.playlist-actions {
  display: flex;
  gap: 5px;
}

.playlist-actions .el-button {
  flex: 1;
}
</style>
