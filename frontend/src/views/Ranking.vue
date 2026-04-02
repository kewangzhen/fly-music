<template>
  <div class="ranking-page">
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
        <h1 class="page-title">
          <span class="title-icon">🔥</span>
          热门榜单
        </h1>
        <p class="page-desc">最热门的歌曲排行榜单</p>
      </div>

      <!-- 榜单Tab切换 -->
      <el-tabs v-model="activeTab" class="ranking-tabs">
        <el-tab-pane label="全局热门" name="global">
          <RankingList :rankingData="rankingData.global" type="global" @play="playSong" />
        </el-tab-pane>
        <el-tab-pane label="新歌榜" name="new">
          <RankingList :rankingData="rankingData.new" type="new" @play="playSong" />
        </el-tab-pane>
        <el-tab-pane label="飙升榜" name="soaring">
          <RankingList :rankingData="rankingData.soaring" type="soaring" @play="playSong" />
        </el-tab-pane>
      </el-tabs>

      <!-- 分类热门 -->
      <div class="category-section">
        <h2 class="section-title">分类热门</h2>
        <div class="category-tags">
          <el-tag 
            v-for="cat in categories" 
            :key="cat.id"
            :class="{ active: selectedCategory === cat.id }"
            @click="selectCategory(cat)"
            size="large"
          >
            {{ cat.name }}
          </el-tag>
        </div>
        <RankingList 
          v-if="rankingData.category && rankingData.category.length > 0"
          :rankingData="rankingData.category" 
          type="category" 
          @play="playSong" 
        />
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'
import { DEFAULT_IMAGES } from '../assets/defaultImages'
import { recommendationAPI, categoryAPI } from '../api'
import RankingList from '../components/RankingList.vue'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()
const activeIndex = ref('3')
const defaultAvatar = DEFAULT_IMAGES.avatar

const activeTab = ref('global')
const selectedCategory = ref(null)
const categories = ref([
  { id: 1, name: '流行' },
  { id: 2, name: '摇滚' },
  { id: 3, name: '民谣' },
  { id: 4, name: '电子' },
  { id: 5, name: '古典' },
  { id: 6, name: '爵士' },
  { id: 7, name: '嘻哈' },
  { id: 8, name: 'R&B' }
])

const rankingData = reactive({
  global: [],
  new: [],
  soaring: [],
  category: []
})

const loadRankings = async () => {
  try {
    const [globalRes, newRes, soaringRes] = await Promise.all([
      recommendationAPI.getGlobalRanking(),
      recommendationAPI.getNewSongRanking(),
      recommendationAPI.getSoaringRanking()
    ])
    
    if (globalRes.data.code === 200) {
      rankingData.global = globalRes.data.data?.songs || []
    }
    if (newRes.data.code === 200) {
      rankingData.new = newRes.data.data?.songs || []
    }
    if (soaringRes.data.code === 200) {
      rankingData.soaring = soaringRes.data.data?.songs || []
    }
  } catch (error) {
    console.error('加载榜单失败:', error)
  }
}

const selectCategory = async (cat) => {
  selectedCategory.value = cat.id
  try {
    const res = await recommendationAPI.getCategorySongs(cat.id)
    if (res.data.code === 200) {
      rankingData.category = res.data.data || []
    }
  } catch (error) {
    console.error('加载分类榜单失败:', error)
  }
}

const playSong = (song) => {
  playerStore.playSong(song)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.init()
  loadRankings()
})
</script>

<style scoped>
.ranking-page {
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

.logo-icon { font-size: 28px; }

.logo-text {
  background: linear-gradient(135deg, #667eea 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-menu { flex: 1; margin: 0 40px; }
.user-menu { display: flex; align-items: center; gap: 20px; }
.user-info { cursor: pointer; }

.main-content {
  flex: 1;
  padding: 30px 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.title-icon { font-size: 40px; }
.page-desc { color: var(--text-secondary); font-size: 16px; }

.ranking-tabs { margin-bottom: 40px; }

.category-section { margin-top: 40px; }

.section-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
}

.category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}

.category-tags .el-tag {
  cursor: pointer;
  padding: 12px 20px;
  font-size: 14px;
  transition: all 0.3s;
}

.category-tags .el-tag.active {
  background: var(--primary-gradient);
  color: white;
}
</style>
