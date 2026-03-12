<template>
  <div class="songs-page">
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
                  <span class="song-title">{{ scope.row.title }}</span>
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
                <el-button text type="primary" @click.stop="addToFavorite(scope.row)">
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { Search, VideoPlay, Plus, Star } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('2')
const defaultAvatar = 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100'

const searchKeyword = ref('')
const selectedCategory = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(50)

const categoryTags = ref([
  { id: null, name: '全部' },
  { id: 1, name: '流行' },
  { id: 2, name: '摇滚' },
  { id: 3, name: '民谣' },
  { id: 4, name: '电子' },
  { id: 5, name: '嘻哈' },
  { id: 6, name: '古典' },
  { id: 7, name: '爵士' },
  { id: 8, name: 'R&B' }
])

const selectedCategoryName = computed(() => {
  const tag = categoryTags.value.find(t => t.id === selectedCategory.value)
  return tag?.name !== '全部' ? tag?.name : null
})

const songs = ref([
  { id: 1, title: '孤勇者', artist: '陈奕迅', duration: 240, cover: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=100', album: '孤勇者', playCount: 125680 },
  { id: 2, title: '起风了', artist: '买辣椒也用券', duration: 260, cover: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=100', album: '起风了', playCount: 98234 },
  { id: 3, title: '海阔天空', artist: 'Beyond', duration: 300, cover: 'https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=100', album: '海阔天空', playCount: 87654 },
  { id: 4, title: '平凡之路', artist: '朴树', duration: 280, cover: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=100', album: '平凡之路', playCount: 76543 },
  { id: 5, title: '晴天', artist: '周杰伦', duration: 265, cover: 'https://images.unsplash.com/photo-1516280440614-6697288d5d38?w=100', album: '叶惠美', playCount: 65432 },
  { id: 6, title: '稻香', artist: '周杰伦', duration: 220, cover: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=100', album: '依然', playCount: 54321 },
  { id: 7, title: '七里香', artist: '周杰伦', duration: 295, cover: 'https://images.unsplash.com/photo-1501612780327-45045538702b?w=100', album: '七里香', playCount: 43210 },
  { id: 8, title: '青花瓷', artist: '周杰伦', duration: 240, cover: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=100', album: '依然', playCount: 32109 },
  { id: 9, title: '夜曲', artist: '周杰伦', duration: 265, cover: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=100', album: '十一月的萧邦', playCount: 21098 },
  { id: 10, title: '光年之外', artist: '邓紫棋', duration: 250, cover: 'https://images.unsplash.com/photo-1485579149621-3123dd979885?w=100', album: '光年之外', playCount: 19876 }
])

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

const selectCategory = (id) => {
  selectedCategory.value = id
  currentPage.value = 1
}

const handleSearch = () => {
  console.log('搜索:', searchKeyword.value)
}

const playAll = () => {
  if (songs.value.length > 0) {
    playSong(songs.value[0])
  }
}

const playSong = (song) => {
  console.log('播放歌曲:', song)
}

const addToPlaylist = (song) => {
  console.log('添加到播放列表:', song)
}

const addToFavorite = (song) => {
  console.log('添加到收藏:', song)
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
