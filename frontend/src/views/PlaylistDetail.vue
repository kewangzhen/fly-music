<template>
  <div class="playlist-detail-container">
    <!-- 导航栏 -->
    <el-header height="60px" class="navbar">
      <div class="logo" @click="$router.push('/')">Fly Music</div>
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
            <span class="user-avatar">
              {{ userStore.user?.username || '用户' }}
            </span>
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
    
    <!-- 播放列表信息 -->
    <div class="playlist-header">
      <img :src="playlist.cover || defaultCover" alt="playlist" class="playlist-cover">
      <div class="playlist-info">
        <h1>{{ playlist.name }}</h1>
        <p class="description">{{ playlist.description || '暂无描述' }}</p>
        <div class="stats">
          <span>歌曲数: {{ playlist.songCount || 0 }}</span>
          <span>播放次数: {{ playlist.playCount || 0 }}</span>
        </div>
        <el-button type="primary" size="large" @click="playAll">播放全部</el-button>
      </div>
    </div>
    
    <!-- 歌曲列表 -->
    <el-main class="song-list-container">
      <el-table :data="songs" stripe @row-click="playSong">
        <el-table-column prop="title" label="歌曲名称" />
        <el-table-column prop="artist" label="艺术家" width="200">
          <template #default="scope">
            {{ getArtistNames(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="album" label="专辑" width="200">
          <template #default="scope">
            {{ scope.row.album?.title || '未知专辑' }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="scope">
            {{ formatDuration(scope.row.duration) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click.stop="playSong(scope.row)">播放</el-button>
            <el-button type="danger" size="small" @click.stop="removeSong(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { playlistAPI } from '../api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('2')

const defaultCover = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20playlist%20cover&image_size=square'

const playlist = ref({})
const songs = ref([])

const getArtistNames = (song) => {
  if (song.artists && song.artists.length > 0) {
    return song.artists.map(a => a.name).join(', ')
  }
  return '未知艺术家'
}

const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

const playAll = () => {
  if (songs.value.length > 0) {
    playSong(songs.value[0])
  }
}

const playSong = (song) => {
  console.log('播放歌曲:', song)
}

const removeSong = (song) => {
  console.log('从播放列表移除:', song)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const loadPlaylist = async () => {
  try {
    const playlistId = route.params.id
    const res = await playlistAPI.getPlaylistById(playlistId)
    playlist.value = res.data || {}
    
    const songsRes = await playlistAPI.getSongsInPlaylist(playlistId)
    songs.value = songsRes.data || []
  } catch (error) {
    console.error('加载播放列表失败:', error)
  }
}

onMounted(() => {
  userStore.init()
  loadPlaylist()
})
</script>

<style scoped>
.playlist-detail-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  cursor: pointer;
}

.nav-menu {
  flex: 1;
  margin: 0 20px;
  border: none;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  background-color: #f0f0f0;
}

.playlist-header {
  display: flex;
  padding: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.playlist-cover {
  width: 200px;
  height: 200px;
  border-radius: 10px;
  object-fit: cover;
}

.playlist-info {
  margin-left: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.playlist-info h1 {
  font-size: 32px;
  margin: 0 0 10px 0;
}

.description {
  font-size: 14px;
  margin-bottom: 20px;
  opacity: 0.9;
}

.stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.song-list-container {
  flex: 1;
  padding: 20px;
}
</style>
