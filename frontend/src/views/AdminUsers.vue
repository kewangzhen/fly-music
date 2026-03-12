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
        <el-table :data="users" stripe v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="150" />
          <el-table-column prop="email" label="邮箱" width="200" />
          <el-table-column prop="phone" label="手机号" width="150" />
          <el-table-column prop="nickname" label="昵称" width="120" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.role === 1 ? 'danger' : 'success'">
                {{ scope.row.role === 1 ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="vipExpireAt" label="VIP到期" width="180">
            <template #default="scope">
              {{ scope.row.vipExpireAt ? formatDate(scope.row.vipExpireAt) : '非VIP' }}
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="注册时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
              <el-button 
                size="small" 
                :type="scope.row.status === 1 ? 'danger' : 'success'"
                @click="toggleUserStatus(scope.row)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="warning" @click="setVip(scope.row)">设置VIP</el-button>
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
