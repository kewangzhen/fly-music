<template>
  <div class="admin-users-container">
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
              placeholder="搜索用户名/邮箱"
              clearable
              @clear="loadUsers"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-select v-model="roleFilter" placeholder="角色筛选" clearable @change="loadUsers">
              <el-option label="普通用户" :value="0" />
              <el-option label="管理员" :value="1" />
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="loadUsers">
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="loadUsers">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 用户列表 -->
      <el-card class="table-card">
        <el-table :data="users" stripe v-loading="loading" :scrollbar-always-on="true">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="phone" label="手机号" min-width="120" />
          <el-table-column prop="nickname" label="昵称" min-width="100" />
          <el-table-column prop="role" label="角色" width="90">
            <template #default="scope">
              <el-tag :type="scope.row.role === 1 ? 'danger' : 'success'">
                {{ scope.row.role === 1 ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="vipExpireAt" label="VIP到期" width="150">
            <template #default="scope">
              {{ scope.row.vipExpireAt ? formatDate(scope.row.vipExpireAt) : '非VIP' }}
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="注册时间" width="150">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-button size="small" type="primary" link @click="editUser(scope.row)">编辑</el-button>
                <el-button 
                  size="small" 
                  :type="scope.row.status === 1 ? 'danger' : 'success'" 
                  link
                  @click="toggleUserStatus(scope.row)"
                >
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button size="small" type="warning" link @click="setVip(scope.row)">VIP</el-button>
              </div>
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
            @size-change="loadUsers"
            @current-change="loadUsers"
          />
        </div>
      </el-card>
      
      <!-- 编辑用户对话框 -->
      <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
        <el-form :model="editForm" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="editForm.username" disabled />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </template>
      </el-dialog>
      
      <!-- 设置VIP对话框 -->
      <el-dialog v-model="vipDialogVisible" title="设置VIP" width="400px">
        <el-form :model="vipForm" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="vipForm.username" disabled />
          </el-form-item>
          <el-form-item label="VIP时长">
            <el-select v-model="vipForm.duration" placeholder="选择时长">
              <el-option label="1个月" :value="30" />
              <el-option label="3个月" :value="90" />
              <el-option label="6个月" :value="180" />
              <el-option label="1年" :value="365" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="vipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSetVip">确认</el-button>
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
import { userAPI } from '../api'

const router = useRouter()

const loading = ref(false)
const users = ref([])
const searchKeyword = ref('')
const roleFilter = ref(null)
const statusFilter = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const editDialogVisible = ref(false)
const vipDialogVisible = ref(false)
const editForm = ref({})
const vipForm = ref({})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await userAPI.getAllUsers()
    let filteredUsers = res.data || []
    
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredUsers = filteredUsers.filter(u => 
        u.username.toLowerCase().includes(keyword) ||
        (u.email && u.email.toLowerCase().includes(keyword))
      )
    }
    
    if (roleFilter.value !== null) {
      filteredUsers = filteredUsers.filter(u => u.role === roleFilter.value)
    }
    
    if (statusFilter.value !== null) {
      filteredUsers = filteredUsers.filter(u => u.status === statusFilter.value)
    }
    
    total.value = filteredUsers.length
    const start = (currentPage.value - 1) * pageSize.value
    users.value = filteredUsers.slice(start, start + pageSize.value)
  } catch (error) {
    console.error('加载用户失败:', error)
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  roleFilter.value = null
  statusFilter.value = null
  currentPage.value = 1
  loadUsers()
}

const editUser = (user) => {
  editForm.value = { ...user }
  editDialogVisible.value = true
}

const saveUser = async () => {
  try {
    await userAPI.updateUser(editForm.value.id, editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadUsers()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const toggleUserStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await userAPI.updateUserStatus(user.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const setVip = (user) => {
  vipForm.value = { id: user.id, username: user.username, duration: 30 }
  vipDialogVisible.value = true
}

const confirmSetVip = async () => {
  try {
    const expireDate = new Date()
    expireDate.setDate(expireDate.getDate() + vipForm.value.duration)
    await userAPI.setVip(vipForm.value.id, expireDate)
    ElMessage.success('VIP设置成功')
    vipDialogVisible.value = false
    loadUsers()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

const handleLogout = () => {
  router.push('/login')
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.navbar {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  cursor: pointer;
  letter-spacing: 1px;
}

.nav-menu {
  flex: 1;
  margin: 0 30px;
  border: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-menu :deep(.el-menu-item:hover) {
  background: rgba(102, 126, 234, 0.2);
  color: #fff;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 8px;
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-avatar {
  cursor: pointer;
  padding: 8px 20px;
  border-radius: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 500;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.main-content {
  flex: 1;
  padding: 30px;
}

.toolbar-card {
  margin-bottom: 25px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.toolbar-card :deep(.el-card__body) {
  padding: 20px;
}

.table-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.table-card :deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(102, 126, 234, 0.2);
  --el-table-row-hover-bg-color: rgba(102, 126, 234, 0.15);
  --el-table-border-color: rgba(255, 255, 255, 0.1);
  --el-table-text-color: rgba(255, 255, 255, 0.9);
  --el-table-header-text-color: #fff;
  color: rgba(255, 255, 255, 0.9);
}

.table-card :deep(.el-table__header) {
  font-weight: 600;
}

.table-card :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.table-card :deep(.el-table__row:hover) {
  transform: scale(1.01);
}

.table-card :deep(.el-tag) {
  border: none;
  font-weight: 500;
}

.table-card :deep(.el-button) {
  border: none;
  transition: all 0.3s ease;
}

.table-card :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.pagination-container {
  margin-top: 25px;
  display: flex;
  justify-content: flex-end;
  padding: 15px 0;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.action-buttons :deep(.el-button) {
  padding: 4px 8px;
  font-size: 13px;
}

.table-card :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

.table-card :deep(.el-table__fixed-right) {
  background: rgba(30, 30, 50, 0.95);
}

.pagination-container :deep(.el-pagination) {
  --el-pagination-bg-color: rgba(255, 255, 255, 0.1);
  --el-pagination-text-color: rgba(255, 255, 255, 0.8);
  --el-pagination-button-bg-color: rgba(255, 255, 255, 0.1);
  --el-pagination-hover-color: #667eea;
}

.toolbar-card :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  box-shadow: none;
}

.toolbar-card :deep(.el-input__wrapper:hover),
.toolbar-card :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.toolbar-card :deep(.el-input__inner) {
  color: rgba(255, 255, 255, 0.9);
}

.toolbar-card :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.toolbar-card :deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  box-shadow: none;
}

.toolbar-card :deep(.el-button) {
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.toolbar-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.toolbar-card :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.toolbar-card :deep(.el-button:not(.el-button--primary)) {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toolbar-card :deep(.el-button:not(.el-button--primary):hover) {
  background: rgba(255, 255, 255, 0.2);
  border-color: #667eea;
}
</style>
