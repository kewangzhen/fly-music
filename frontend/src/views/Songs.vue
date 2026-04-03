<template>
  <div class="songs-page">
    <el-main class="main-content">
      <div class="page-header">
        <h1 class="page-title">音乐库</h1>
        <p class="page-desc">发现海量好音乐</p>
      </div>

      <div class="filters-section">
        <div class="filter-tags">
          <el-tag 
            v-for="tag in categoryTags" 
            :key="tag.id" 
            :type="selectedCategory === tag.id ? 'primary' : ''"
            class="filter-tag"
            @click="selectCategory(tag.id)"
          >
            {{ tag.name }}
          </el-tag>
        </div>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索歌曲、歌手..."
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          />
        </div>
      </div>

      <div class="songs-section">
        <div class="section-header">
          <h2 class="section-title">
            <span v-if="selectedCategoryName">{{ selectedCategoryName }}</span>
            <span v-else>全部歌曲</span>
            <span class="song-count">({{ total }}首)</span>
          </h2>
          <div class="header-actions">
            <el-button type="primary" round @click="playAll">
              <el-icon><VideoPlay /></el-icon> 播放全部
            </el-button>
          </div>
        </div>

        <el-table 
          :data="songs" 
          stripe 
          class="songs-table"
          @row-click="playSong"
        >
          <el-table-column type="index" width="60" label="">
            <template #default="scope">
              <div class="song-index">
                <span class="index-num">{{ (currentPage - 1) * pageSize + scope.$index + 1 }}</span>
                <el-icon class="play-icon"><VideoPlay /></el-icon>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="歌曲名称" min-width="200">
            <template #default="scope">
              <div class="song-title-cell">
                <img :src="scope.row.cover" class="song-thumb">
                <div class="song-info">
                  <span class="song-title" @click="goToSongDetail(scope.row.id)">{{ scope.row.title }}</span>
                  <span class="song-artist">{{ scope.row.artist }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="album" label="专辑" width="180">
            <template #default="scope">
              {{ scope.row.album || '未知专辑' }}
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="时长" width="80">
            <template #default="scope">
              {{ formatDuration(scope.row.duration) }}
            </template>
          </el-table-column>
          <el-table-column prop="playCount" label="播放次数" width="100">
            <template #default="scope">
              {{ formatPlayCount(scope.row.playCount) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="table-actions">
                <el-button text type="primary" @click.stop="playSong(scope.row)">
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
                <el-button text type="primary" @click.stop="addToPlaylist(scope.row)">
                  <el-icon><Plus /></el-icon>
                </el-button>
                <el-button text :type="isFavorite(scope.row.id) ? 'warning' : 'primary'" @click.stop="addToFavorite(scope.row)">
                  <el-icon><Star /></el-icon>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next, jumper"
            background
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { Search, VideoPlay, Plus, Star } from '@element-plus/icons-vue'
import songApi from '../api/song'
import categoryApi from '../api/category'

const router = useRouter()

const goToSongDetail = (id) => {
  router.push(`/song/${id}`)
}

const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('2')
const defaultAvatar = DEFAULT_IMAGES.avatar
const defaultCover = DEFAULT_IMAGES.cover

const searchKeyword = ref('')
const selectedCategory = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const songs = ref([])
const favoriteSongIds = ref(new Set())
const categoryTags = ref([])

const selectedCategoryName = computed(() => {
  const tag = categoryTags.value.find(t => t.id === selectedCategory.value)
  return tag?.name !== '全部' ? tag?.name : null
})

const loadCategories = async () => {
  try {
    const res = await categoryApi.getAllCategories()
    if (res.code === 200) {
      categoryTags.value = [
        { id: null, name: '全部' },
        ...res.data
      ]
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadSongs = async () => {
  loading.value = true
  try {
    let res
    const page = currentPage.value - 1
    const size = pageSize.value
    
    if (searchKeyword.value) {
      res = await songApi.searchSongs(searchKeyword.value)
      songs.value = res.data || []
      total.value = songs.value.length
    } else if (selectedCategory.value) {
      res = await songApi.getSongsByCategory(selectedCategory.value, page, size)
      songs.value = res.data.content || []
      total.value = res.data.totalElements || 0
    } else {
      res = await songApi.getSongsByPage(page, size)
      songs.value = res.data.content || []
      total.value = res.data.totalElements || 0
    }
    
    songs.value = songs.value.map(song => ({
      ...song,
      artist: song.artists?.map(a => a.name).join(', ') || '未知歌手',
      cover: song.cover || defaultCover,
      album: song.album?.title || '未知专辑'
    }))
    
    loadFavoriteStatus()
  } catch (error) {
    console.error('加载歌曲失败:', error)
    songs.value = []
  } finally {
    loading.value = false
  }
}

const selectCategory = (id) => {
  selectedCategory.value = id
  currentPage.value = 1
  searchKeyword.value = ''
  loadSongs()
}

const handleSearch = () => {
  if (searchKeyword.value) {
    selectedCategory.value = null
    currentPage.value = 1
    loadSongs()
  }
}

watch([selectedCategory, currentPage], () => {
  if (!searchKeyword.value) {
    loadSongs()
  }
})

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

const formatPlayCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count
}

const playAll = () => {
  if (songs.value.length > 0) {
    songs.value.forEach(song => {
      playerStore.addToQueue(song)
    })
    if (playerStore.currentSong) {
      const lastIndex = playerStore.playlist.length - songs.value.length
      playerStore.currentIndex = lastIndex
      playerStore.playSong(playerStore.playlist[lastIndex])
    } else {
      playerStore.currentIndex = 0
      playerStore.playSong(playerStore.playlist[0])
    }
    ElMessage.success('已添加到播放队列')
  }
}

const playSong = (song) => {
  playerStore.playSong(song)
}

const addToPlaylist = (song) => {
  playerStore.addToQueue(song)
  ElMessage.success('已添加到播放队列')
}

const loadFavoriteStatus = async () => {
  if (!userStore.isLoggedIn) return
  const token = localStorage.getItem('token')
  if (!token) return
  
  try {
    const songIds = songs.value.map(s => s.id).join(',')
    if (!songIds) return
    
    const res = await fetch(`http://localhost:8080/api/favorites/check-batch?userId=${userStore.user.id}&targetType=1&songIds=${songIds}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200 && data.data) {
      favoriteSongIds.value = new Set(Object.keys(data.data).filter(id => data.data[id]))
    }
  } catch (error) {
    console.error('获取收藏状态失败:', error)
  }
}

const isFavorite = (songId) => {
  return favoriteSongIds.value.has(songId)
}

const addToFavorite = async (song) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await playerStore.toggleFavorite(song.id)
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.init()
  loadCategories()
  loadSongs()
})
</script>

<style scoped>
.songs-page {
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

.main-content {
  flex: 1;
  padding: 30px 60px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  animation: fadeIn 0.6s ease-out;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-desc {
  color: var(--text-secondary);
  font-size: 16px;
}

.filters-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: var(--card-bg);
  border-radius: 12px;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tag:hover {
  transform: scale(1.05);
}

.search-input {
  width: 250px;
}

.songs-section {
  animation: fadeIn 0.5s ease-out;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
}

.song-count {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: normal;
  margin-left: 8px;
}

.songs-table {
  border-radius: 12px;
  overflow: hidden;
}

.song-index {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.index-num {
  color: var(--text-secondary);
}

.play-icon {
  opacity: 0;
  color: var(--primary-color);
  transition: opacity 0.3s;
}

.el-table__row:hover .play-icon {
  opacity: 1;
}

.el-table__row:hover .index-num {
  display: none;
}

.song-title-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.song-thumb {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
}

.song-info {
  display: flex;
  flex-direction: column;
}

.song-title {
  font-weight: 500;
  cursor: pointer;
}

.song-title:hover {
  color: #409eff;
}

.song-artist {
  font-size: 12px;
  color: var(--text-secondary);
}

.table-actions {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
