<template>
  <div class="playlists-container">
    <el-card class="playlists-card">
      <template #header>
        <div class="card-header">
          <h2>我的歌单</h2>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon> 创建歌单
          </el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我创建的歌单" name="created">
          <el-row :gutter="20" v-if="createdPlaylists.length > 0">
            <el-col :span="6" v-for="playlist in createdPlaylists" :key="playlist.id" class="playlist-card">
              <el-card shadow="hover" @click="goToPlaylistDetail(playlist)">
                <img :src="playlist.cover || defaultCover" alt="playlist" class="playlist-cover">
                <div class="playlist-info">
                  <h4>{{ playlist.name }}</h4>
                  <p>{{ playlist.songCount || 0 }}首歌</p>
                  <div class="playlist-actions">
                    <el-button type="primary" size="small" @click.stop="playPlaylist(playlist)">
                      <el-icon><VideoPlay /></el-icon> 播放
                    </el-button>
                    <el-button size="small" @click.stop="editPlaylist(playlist)">
                      <el-icon><Edit /></el-icon> 编辑
                    </el-button>
                    <el-button size="small" type="danger" @click.stop="deletePlaylistConfirm(playlist)">
                      <el-icon><Delete /></el-icon> 删除
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-else description="还没有创建歌单" />
        </el-tab-pane>
        
        <el-tab-pane label="我收藏的歌单" name="favorited">
          <el-row :gutter="20" v-if="favoritedPlaylists.length > 0">
            <el-col :span="6" v-for="playlist in favoritedPlaylists" :key="playlist.id" class="playlist-card">
              <el-card shadow="hover" @click="goToPlaylistDetail(playlist)">
                <img :src="playlist.cover || defaultCover" alt="playlist" class="playlist-cover">
                <div class="playlist-info">
                  <h4>{{ playlist.name }}</h4>
                  <p>{{ playlist.songCount || 0 }}首歌</p>
                  <div class="playlist-actions">
                    <el-button type="primary" size="small" @click.stop="playPlaylist(playlist)">
                      <el-icon><VideoPlay /></el-icon> 播放
                    </el-button>
                    <el-button size="small" @click.stop="toggleFavorite(playlist)">
                      <el-icon><Star /></el-icon> 取消收藏
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-else description="还没有收藏歌单" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑歌单' : '创建歌单'" width="500px">
      <el-form :model="playlistForm" label-position="top">
        <el-form-item label="歌单名称">
          <el-input v-model="playlistForm.name" placeholder="请输入歌单名称" />
        </el-form-item>
        <el-form-item label="歌单描述">
          <el-input v-model="playlistForm.description" type="textarea" :rows="3" placeholder="请输入歌单描述" />
        </el-form-item>
        <el-form-item label="封面图片URL">
          <el-input v-model="playlistForm.cover" placeholder="请输入封面图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPlaylist">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, VideoPlay, Edit, Delete, Star } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { usePlayerStore } from '../store/player'

const router = useRouter()
const userStore = useUserStore()
const playerStore = usePlayerStore()

const defaultCover = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=playlist%20cover%20music&image_size=square'

const activeTab = ref('created')
const dialogVisible = ref(false)
const isEdit = ref(false)
const createdPlaylists = ref([])
const favoritedPlaylists = ref([])

const playlistForm = reactive({
  id: null,
  name: '',
  description: '',
  cover: ''
})

const loadPlaylists = async () => {
  if (!userStore.user?.id) return
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`http://localhost:8080/api/playlists/user/${userStore.user.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      createdPlaylists.value = data.data || []
    }
  } catch (error) {
    console.error('加载歌单失败:', error)
  }
}

const loadFavoritedPlaylists = async () => {
  if (!userStore.user?.id) return
  const token = localStorage.getItem('token')
  try {
    const res = await fetch(`http://localhost:8080/api/playlists/favorite/${userStore.user.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      favoritedPlaylists.value = data.data || []
    }
  } catch (error) {
    console.error('加载收藏歌单失败:', error)
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  playlistForm.id = null
  playlistForm.name = ''
  playlistForm.description = ''
  playlistForm.cover = ''
  dialogVisible.value = true
}

const editPlaylist = (playlist) => {
  isEdit.value = true
  playlistForm.id = playlist.id
  playlistForm.name = playlist.name
  playlistForm.description = playlist.description || ''
  playlistForm.cover = playlist.cover || ''
  dialogVisible.value = true
}

const submitPlaylist = async () => {
  if (!playlistForm.name) {
    ElMessage.warning('请输入歌单名称')
    return
  }
  
  const token = localStorage.getItem('token')
  try {
    if (isEdit.value) {
      await fetch(`http://localhost:8080/api/playlists/${playlistForm.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          name: playlistForm.name,
          description: playlistForm.description,
          cover: playlistForm.cover
        })
      })
      ElMessage.success('更新成功')
    } else {
      await fetch('http://localhost:8080/api/playlists', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          name: playlistForm.name,
          description: playlistForm.description,
          cover: playlistForm.cover,
          userId: userStore.user.id
        })
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadPlaylists()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deletePlaylistConfirm = async (playlist) => {
  try {
    await ElMessageBox.confirm(`确定要删除歌单"${playlist.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const token = localStorage.getItem('token')
    await fetch(`http://localhost:8080/api/playlists/${playlist.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` }
    })
    ElMessage.success('删除成功')
    loadPlaylists()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const toggleFavorite = async (playlist) => {
  const token = localStorage.getItem('token')
  try {
    await fetch(`http://localhost:8080/api/playlists/${playlist.id}/favorite`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ userId: userStore.user.id })
    })
    ElMessage.success('取消收藏成功')
    loadFavoritedPlaylists()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const playPlaylist = (playlist) => {
  playerStore.setPlaylist(createdPlaylists.value)
  playerStore.playById(playlist.songs?.[0]?.id)
}

const goToPlaylistDetail = (playlist) => {
  router.push(`/playlists/${playlist.id}`)
}

onMounted(async () => {
  await userStore.init()
  if (userStore.user?.id) {
    loadPlaylists()
    loadFavoritedPlaylists()
  }
})
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
  cursor: pointer;
}

.playlist-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
}

.playlist-info {
  padding: 12px 0;
}

.playlist-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.playlist-info p {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.playlist-actions {
  display: flex;
  gap: 8px;
}
</style>
