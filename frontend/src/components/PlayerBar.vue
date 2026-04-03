<template>
  <div class="player-bar" v-if="playerStore.currentSong">
    <div class="player-left">
      <img 
        :src="playerStore.currentSong.cover || defaultCover" 
        class="song-cover" 
        @click="goToSongDetail"
      />
      <div class="song-info">
        <div class="song-title">{{ playerStore.currentSong.title }}</div>
        <div class="song-artist">
          <span 
            v-for="(artist, index) in (playerStore.currentSong.artists || [])" 
            :key="artist.id"
            class="artist-link"
            @click="goToArtistDetail(artist.id)"
          >
            {{ artist.name }}{{ index < (playerStore.currentSong.artists?.length || 0) - 1 ? ', ' : '' }}
          </span>
          <span v-if="!playerStore.currentSong.artists?.length">未知歌手</span>
        </div>
      </div>
    </div>

    <div class="player-center">
      <div class="player-controls">
        <el-button circle size="small" class="control-btn" @click="playerStore.playPrev">
          <el-icon><DArrowLeft /></el-icon>
        </el-button>
        <el-button 
          circle 
          size="default" 
          class="play-btn" 
          @click="playerStore.togglePlay"
        >
          <el-icon v-if="playerStore.isPlaying"><VideoPause /></el-icon>
          <el-icon v-else><VideoPlay /></el-icon>
        </el-button>
        <el-button circle size="small" class="control-btn" @click="playerStore.playNext">
          <el-icon><DArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="progress-bar">
        <span class="time">{{ formatTime(playerStore.currentTime) }}</span>
        <el-slider
          v-model="currentProgress"
          :max="playerStore.duration || 100"
          :show-tooltip="false"
          @change="handleSeek"
        />
        <span class="time">{{ formatTime(playerStore.duration) }}</span>
      </div>
    </div>

    <div class="player-right">
      <el-button circle size="small" @click="handleToggleFavorite" class="favorite-btn" :class="{ 'is-favorited': playerStore.currentSongFavorite }">
        <el-icon><Star /></el-icon>
      </el-button>
      <el-button circle size="small" @click="showPlaylist = true" class="playlist-btn">
        <el-icon><List /></el-icon>
        <span class="badge" v-if="playerStore.playlist.length">{{ playerStore.playlist.length }}</span>
      </el-button>
    </div>

    <el-drawer
      v-model="showPlaylist"
      title=""
      direction="rtl"
      size="380px"
      :with-header="false"
    >
      <div class="playlist-drawer">
        <div class="drawer-header">
          <div class="header-title">
            <h3>播放队列</h3>
            <span class="song-count">{{ playerStore.playlist.length }} 首</span>
          </div>
          <div class="header-actions">
            <el-button text size="small" @click="clearAll" class="clear-btn">
              <el-icon><Delete /></el-icon>
              清空
            </el-button>
          </div>
        </div>

        <div class="playlist-items">
          <div 
            v-for="(song, index) in playerStore.playlist" 
            :key="song.id"
            class="playlist-item"
            :class="{ active: index === playerStore.currentIndex }"
          >
            <div class="item-index">
              <span v-if="index !== playerStore.currentIndex">{{ index + 1 }}</span>
              <el-icon v-else class="playing-gif"><VideoPlay /></el-icon>
            </div>
            <div class="item-main" @click="playFromList(index)">
              <img :src="song.cover || defaultCover" class="item-cover" />
              <div class="item-info">
                <div class="item-title">{{ song.title }}</div>
                <div class="item-artist">
                  {{ song.artists?.map(a => a.name).join(', ') || '未知歌手' }}
                </div>
              </div>
            </div>
            <div class="item-actions">
              <el-icon class="action-btn" :size="20" @click.stop="playNextSong(song)"><DArrowRight /></el-icon>
              <el-icon class="action-btn" :size="20" :class="{ favorited: isSongFavorited(song.id) }" @click.stop="toggleQueueFavorite(song)"><Star /></el-icon>
              <el-icon class="action-btn remove" :size="20" @click.stop="removeFromList(index)"><Close /></el-icon>
            </div>
          </div>
          <el-empty v-if="!playerStore.playlist.length" description="播放列表为空" />
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { DArrowLeft, DArrowRight, VideoPlay, VideoPause, List, Close, Delete, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const playerStore = usePlayerStore()
const defaultCover = DEFAULT_IMAGES.cover

const showPlaylist = ref(false)
const currentProgress = computed({
  get: () => playerStore.currentTime,
  set: () => {}
})

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const handleSeek = (value) => {
  playerStore.seek(value)
}

const playFromList = (index) => {
  playerStore.currentIndex = index
  playerStore.playSong(playerStore.playlist[index])
}

const playNextSong = (song) => {
  playerStore.addToQueueNext(song)
  ElMessage.success('已添加到下一首')
}

const removeFromList = (index) => {
  playerStore.removeFromQueue(index)
}

const clearAll = () => {
  playerStore.clearQueue()
  showPlaylist.value = false
  ElMessage.success('已清空播放队列')
}

const goToSongDetail = () => {
  if (playerStore.currentSong?.id) {
    router.push(`/song/${playerStore.currentSong.id}`)
  }
}

const goToArtistDetail = (id) => {
  router.push(`/artist/${id}`)
}

const handleToggleFavorite = async () => {
  if (!playerStore.currentSong?.id) return
  await playerStore.toggleFavorite(playerStore.currentSong.id)
}

const isSongFavorited = (songId) => {
  return playerStore.currentSong?.id === songId && playerStore.currentSongFavorite
}

const toggleQueueFavorite = async (song) => {
  await playerStore.toggleFavorite(song.id)
}
</script>

<style scoped>
.player-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: linear-gradient(90deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  align-items: center;
  padding: 0 20px;
  z-index: 2000;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.3);
}

.player-left {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 250px;
}

.song-cover {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
}

.song-cover:hover {
  transform: scale(1.05);
}

.song-info {
  flex: 1;
  overflow: hidden;
}

.song-title {
  color: #fff;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-artist {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.artist-link {
  cursor: pointer;
}

.artist-link:hover {
  color: #409eff;
}

.player-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.player-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.control-btn {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.8);
}

.control-btn:hover {
  color: #fff;
}

.play-btn {
  background: #409eff;
  border: none;
  color: #fff;
}

.play-btn:hover {
  background: #66b1ff;
}

.progress-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  max-width: 600px;
}

.time {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  min-width: 40px;
}

.progress-bar :deep(.el-slider__runway) {
  background: rgba(255, 255, 255, 0.2);
}

.progress-bar :deep(.el-slider__bar) {
  background: #409eff;
}

.progress-bar :deep(.el-slider__button) {
  border-color: #409eff;
}

.player-right {
  width: 100px;
  display: flex;
  justify-content: flex-end;
}

.playlist-btn {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  position: relative;
}

.playlist-btn:hover {
  color: #fff;
}

.favorite-btn {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.7);
}

.favorite-btn:hover {
  color: #fff;
}

.favorite-btn.is-favorited {
  color: #f6ad55 !important;
}

.badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #409eff;
  color: #fff;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
}

.playlist-drawer {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-title h3 {
  color: #fff;
  margin: 0 0 5px;
  font-size: 18px;
}

.song-count {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

.clear-btn {
  color: rgba(255, 255, 255, 0.6) !important;
}

.clear-btn:hover {
  color: #f56c6c !important;
}

.playlist-items {
  flex: 1;
  overflow-y: auto;
  padding: 0 10px;
}

.playlist-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}

.playlist-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.playlist-item.active {
  background: rgba(64, 158, 255, 0.15);
}

.playlist-item.active .item-title {
  color: #409eff;
}

.item-index {
  width: 24px;
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
  font-size: 13px;
}

.playing-gif {
  color: #409eff;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.item-cover {
  width: 45px;
  height: 45px;
  border-radius: 6px;
  object-fit: cover;
}

.item-main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  overflow: hidden;
  min-width: 0;
}

.item-info {
  flex: 1;
  overflow: hidden;
  min-width: 0;
}

.item-title {
  color: #fff;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-artist {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.playlist-item:hover .item-actions {
  opacity: 1;
}

.action-btn {
  padding: 8px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  transition: all 0.2s;
  font-size: 20px !important;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.action-btn.remove:hover {
  color: #f56c6c;
}

.action-btn.favorited {
  color: #f6ad55;
}

:deep(.el-drawer__body) {
  padding: 0;
  background: transparent;
}

:deep(.el-empty__description) {
  color: rgba(255, 255, 255, 0.4);
}
</style>
