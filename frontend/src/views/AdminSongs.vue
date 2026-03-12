<template>
  <div class="admin-songs-container">
    <!-- 导航栏 -->
    <el-header height="60px" class="navbar">
      <div class="logo" @click="$router.push('/')">Fly Music 管理后台</div>
      <el-menu mode="horizontal" :router="true" class="nav-menu" :default-active="$route.path">
        <el-menu-item index="/admin" route="/admin">概览</el-menu-item>
        <el-menu-item index="/admin/users" route="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/songs" route="/admin/songs">音乐管理</el-menu-item>
        <el-menu-item index="/admin/config" route="/admin/config">系统配置</el-menu-item>
      </el-menu>
      <div class="user-menu">
        <el-dropdown>
          <span class="user-avatar">管理员</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/')">返回前台</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 搜索和操作栏 -->
      <el-card class="toolbar-card">
        <el-row :gutter="20" align="middle">
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索歌曲名/歌手名"
              clearable
              @clear="loadSongs"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="loadSongs">
              <el-option label="已发布" :value="1" />
              <el-option label="待审核" :value="0" />
              <el-option label="已下架" :value="2" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="loadSongs">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
            <el-button type="success" @click="showUploadDialog">上传歌曲</el-button>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 歌曲列表 -->
      <el-card class="table-card">
        <el-table :data="songs" stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="封面" width="80">
            <template #default="scope">
              <img :src="scope.row.cover || defaultCover" class="song-cover-mini" />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="歌曲名" width="200" />
          <el-table-column label="歌手" width="150">
            <template #default="scope">
              {{ getArtistNames(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="专辑" width="150">
            <template #default="scope">
              {{ scope.row.album?.title || '未知专辑' }}
            </template>
          </el-table-column>
          <el-table-column label="分类" width="100">
            <template #default="scope">
              {{ scope.row.category?.name || '未分类' }}
            </template>
          </el-table-column>
          <el-table-column prop="playCount" label="播放次数" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="isOriginal" label="原创" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.isOriginal === 1 ? 'warning' : 'info'">
                {{ scope.row.isOriginal === 1 ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="上传时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="playSong(scope.row)">播放</el-button>
              <el-button 
                v-if="scope.row.status === 0" 
                size="small" 
                type="success"
                @click="approveSong(scope.row)"
              >
                审核
              </el-button>
              <el-button 
                v-if="scope.row.status === 1" 
                size="small" 
                type="danger"
                @click="rejectSong(scope.row)"
              >
                下架
              </el-button>
              <el-button size="small" type="warning" @click="editSong(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadSongs"
            @current-change="loadSongs"
          />
        </div>
      </el-card>
      
      <!-- 上传歌曲对话框 -->
      <el-dialog v-model="uploadDialogVisible" title="上传歌曲" width="600px">
        <el-form :model="uploadForm" label-width="80px">
          <el-form-item label="歌曲名" required>
            <el-input v-model="uploadForm.title" placeholder="请输入歌曲名" />
          </el-form-item>
          <el-form-item label="歌手">
            <el-input v-model="uploadForm.artistName" placeholder="请输入歌手名" />
          </el-form-item>
          <el-form-item label="专辑">
            <el-input v-model="uploadForm.albumName" placeholder="请输入专辑名" />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="uploadForm.categoryId" placeholder="选择分类">
              <el-option 
                v-for="cat in categories" 
                :key="cat.id" 
                :label="cat.name" 
                :value="cat.id" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="歌曲URL" required>
            <el-input v-model="uploadForm.url" placeholder="请输入歌曲URL" />
          </el-form-item>
          <el-form-item label="封面">
            <el-input v-model="uploadForm.cover" placeholder="请输入封面URL" />
          </el-form-item>
          <el-form-item label="歌词">
            <el-input v-model="uploadForm.lyrics" type="textarea" :rows="4" placeholder="请输入歌词" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="uploadSong">上传</el-button>
        </template>
      </el-dialog>
      
      <!-- 编辑歌曲对话框 -->
      <el-dialog v-model="editDialogVisible" title="编辑歌曲" width="600px">
        <el-form :model="editForm" label-width="80px">
          <el-form-item label="歌曲名">
            <el-input v-model="editForm.title" />
          </el-form-item>
          <el-form-item label="歌曲URL">
            <el-input v-model="editForm.url" />
          </el-form-item>
          <el-form-item label="封面">
            <el-input v-model="editForm.cover" />
          </el-form-item>
          <el-form-item label="歌词">
            <el-input v-model="editForm.lyrics" type="textarea" :rows="4" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveSong">保存</el-button>
        </template>
      </el-dialog>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { songAPI, categoryAPI } from '../api'

const router = useRouter()

const loading = ref(false)
const songs = ref([])
const categories = ref([])
const searchKeyword = ref('')
const statusFilter = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const uploadDialogVisible = ref(false)
const editDialogVisible = ref(false)
const uploadForm = ref({
  title: '',
  artistName: '',
  albumName: '',
  categoryId: null,
  url: '',
  cover: '',
  lyrics: ''
})
const editForm = ref({})

const defaultCover = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20cover&image_size=square'

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const getArtistNames = (song) => {
  if (song.artists && song.artists.length > 0) {
    return song.artists.map(a => a.name).join(', ')
  }
  return '未知歌手'
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已发布', 2: '已下架' }
  return texts[status] || '未知'
}

const loadSongs = async () => {
  loading.value = true
  try {
    const res = await songAPI.getSongsByPage(currentPage.value - 1, pageSize.value, statusFilter.value || 1)
    let allSongs = res.data?.content || []
    
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      allSongs = allSongs.filter(s => 
        s.title.toLowerCase().includes(keyword) ||
        getArtistNames(s).toLowerCase().includes(keyword)
      )
    }
    
    songs.value = allSongs
    total.value = res.data?.totalElements || 0
  } catch (error) {
    console.error('加载歌曲失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await categoryAPI.getAllCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  statusFilter.value = null
  currentPage.value = 1
  loadSongs()
}

const showUploadDialog = () => {
  uploadForm.value = {
    title: '',
    artistName: '',
    albumName: '',
    categoryId: null,
    url: '',
    cover: '',
    lyrics: ''
  }
  uploadDialogVisible.value = true
}

const uploadSong = async () => {
  if (!uploadForm.value.title || !uploadForm.value.url) {
    ElMessage.error('请填写必填项')
    return
  }
  try {
    await songAPI.createSong(uploadForm.value)
    ElMessage.success('上传成功')
    uploadDialogVisible.value = false
    loadSongs()
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const editSong = (song) => {
  editForm.value = { ...song }
  editDialogVisible.value = true
}

const saveSong = async () => {
  try {
    await songAPI.updateSong(editForm.value.id, editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadSongs()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const approveSong = async (song) => {
  try {
    await songAPI.approveSong(song.id)
    ElMessage.success('审核通过')
    loadSongs()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const rejectSong = async (song) => {
  try {
    await songAPI.rejectSong(song.id)
    ElMessage.success('已下架')
    loadSongs()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const playSong = (song) => {
  console.log('播放歌曲:', song)
  ElMessage.info('播放: ' + song.title)
}

const handleLogout = () => {
  router.push('/login')
}

onMounted(() => {
  loadSongs()
  loadCategories()
})
</script>

<style scoped>
.admin-songs-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
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
  font-size: 20px;
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
}

.user-avatar {
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  background-color: #f0f0f0;
}

.main-content {
  flex: 1;
  padding: 20px;
}

.toolbar-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.song-cover-mini {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 5px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
