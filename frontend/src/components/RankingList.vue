<template>
  <div class="ranking-list">
    <!-- 加载状态 -->
    <div v-if="!rankingData || rankingData.length === 0" class="empty-wrapper">
      <el-empty description="暂无数据" />
    </div>

    <!-- 歌曲列表 -->
    <div v-else class="songs-list">
      <div 
        v-for="(song, index) in rankingData" 
        :key="song.songId || song.id" 
        class="song-item"
        :class="{ 'top-item': index < 3 }"
        @click="$emit('play', song)"
      >
        <!-- 排名 -->
        <div class="song-rank" :class="getRankClass(index)">
          <span v-if="index < 3" class="rank-icon">{{ getRankIcon(index) }}</span>
          <span v-else>{{ index + 1 }}</span>
        </div>
        
        <!-- 封面 -->
        <img :src="song.cover || defaultCover" class="song-cover">
        
        <!-- 信息 -->
        <div class="song-info">
          <div class="song-title">
            <span v-if="type === 'soaring' && song.riseRate" class="rise-tag">↑{{ Math.round(song.riseRate) }}%</span>
            {{ song.title }}
          </div>
          <div class="song-artist">{{ song.artist }}</div>
        </div>
        
        <!-- 热度得分 -->
        <div class="song-score" v-if="song.hotScore">
          <span class="score-value">{{ Math.round(song.hotScore) }}</span>
          <span class="score-label">热度</span>
        </div>
        
        <!-- 播放次数 -->
        <div class="song-plays" v-if="song.playCount">
          <span>{{ formatPlayCount(song.playCount) }}</span>
        </div>
        
        <!-- 操作 -->
        <div class="song-actions">
          <el-button circle type="primary" @click.stop="$emit('play', song)">
            <el-icon><VideoPlay /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { VideoPlay } from '@element-plus/icons-vue'
import { DEFAULT_IMAGES } from '../assets/defaultImages'

defineProps({
  rankingData: {
    type: Array,
    default: () => []
  },
  type: {
    type: String,
    default: 'global'
  }
})

defineEmits(['play'])

const defaultCover = DEFAULT_IMAGES.cover

const getRankClass = (index) => {
  if (index === 0) return 'rank-first'
  if (index === 1) return 'rank-second'
  if (index === 2) return 'rank-third'
  return ''
}

const getRankIcon = (index) => {
  const icons = ['🥇', '🥈', '🥉']
  return icons[index]
}

const formatPlayCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count
}
</script>

<style scoped>
.ranking-list {
  width: 100%;
}

.empty-wrapper {
  padding: 40px;
}

.songs-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.song-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.song-item:hover {
  background: var(--hover-bg);
}

.song-item.top-item {
  background: rgba(102, 126, 234, 0.1);
}

.song-rank {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: var(--text-secondary);
}

.rank-first { color: #FFD700; }
.rank-second { color: #C0C0C0; }
.rank-third { color: #CD7F32; }

.rank-icon { font-size: 24px; }

.song-cover {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}

.song-info {
  flex: 1;
  min-width: 0;
}

.song-title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.rise-tag {
  background: linear-gradient(135deg, #ff6b6b, #ffa500);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.song-artist {
  font-size: 13px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.song-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 60px;
}

.score-value {
  font-size: 16px;
  font-weight: bold;
  color: var(--primary-color);
}

.score-label {
  font-size: 11px;
  color: var(--text-secondary);
}

.song-plays {
  min-width: 80px;
  text-align: right;
  color: var(--text-secondary);
  font-size: 13px;
}

.song-actions {
  opacity: 0;
  transition: opacity 0.3s;
}

.song-item:hover .song-actions {
  opacity: 1;
}
</style>
