<template>
  <div class="artists-container">
    <div class="page-header">
      <h1>歌手列表</h1>
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索歌手..."
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <div class="artists-grid" v-if="artists.length">
      <div 
        class="artist-card" 
        v-for="artist in artists" 
        :key="artist.id"
        @click="goToArtistDetail(artist.id)"
      >
        <div class="artist-avatar">
          <img :src="artist.avatar || defaultAvatar" :alt="artist.name" />
        </div>
        <div class="artist-info">
          <h3 class="artist-name">{{ artist.name }}</h3>
          <p class="artist-fans">{{ formatNumber(artist.fanCount) }} 粉丝</p>
        </div>
      </div>
    </div>
    
    <el-empty v-else-if="!loading" description="暂无歌手" />
    
    <div class="loading" v-if="loading">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Loading } from '@element-plus/icons-vue'
import axios from 'axios'
import { DEFAULT_IMAGES } from '../assets/defaultImages'

const router = useRouter()
const defaultAvatar = DEFAULT_IMAGES.artist

const artists = ref([])
const searchKeyword = ref('')
const loading = ref(false)
let searchTimer = null

const fetchArtists = async (keyword = '') => {
  loading.value = true
  try {
    let url = 'http://localhost:8080/api/artists'
    let params = {}
    
    if (keyword) {
      url = 'http://localhost:8080/api/artists/search'
      params = { keyword }
    }
    
    const response = await axios.get(url, { params })
    if (response.data.code === 200) {
      artists.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取歌手列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    fetchArtists(searchKeyword.value)
  }, 300)
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const goToArtistDetail = (id) => {
  router.push(`/artist/${id}`)
}

onMounted(() => {
  fetchArtists()
})
</script>

<style scoped>
.artists-container {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  color: #fff;
  font-size: 32px;
  margin-bottom: 20px;
}

.search-box {
  max-width: 400px;
  margin: 0 auto;
}

.search-box :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.search-box :deep(.el-input__inner) {
  color: #fff;
}

.search-box :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.artists-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.artist-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.artist-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.15);
}

.artist-avatar {
  width: 120px;
  height: 120px;
  margin: 0 auto 15px;
  border-radius: 50%;
  overflow: hidden;
}

.artist-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.artist-name {
  color: #fff;
  font-size: 16px;
  margin: 0 0 8px;
}

.artist-fans {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  margin: 0;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.loading .el-icon {
  color: #fff;
}
</style>
