<template>
  <div class="admin-categories-container">
    <!-- 导航栏 -->
    <AdminNavbar />

    <!-- 主要内容 -->
    <el-main class="main-content">
      <!-- 搜索和操作栏 -->
      <el-card class="toolbar-card">
        <el-row :gutter="20" align="middle">
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索分类名称"
              clearable
              @clear="loadCategories"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="16">
            <div class="toolbar-actions">
              <el-button type="primary" @click="handleAdd">
                <el-icon><Plus /></el-icon>
                新增分类
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 分类列表 -->
      <el-card class="table-card">
        <el-table :data="filteredCategories" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="分类名称" min-width="150">
            <template #default="scope">
              <span class="category-name">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="icon" label="图标" width="100">
            <template #default="scope">
              <span v-if="scope.row.icon" class="category-icon">{{ scope.row.icon }}</span>
              <span v-else class="no-icon">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="level" label="级别" width="80">
            <template #default="scope">
              <el-tag size="small">L{{ scope.row.level }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-tooltip content="编辑" placement="top">
                  <el-button circle size="small" type="primary" @click="handleEdit(scope.row)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="scope.row.status === 1 ? '禁用' : '启用'" placement="top">
                  <el-button 
                    circle 
                    size="small" 
                    :type="scope.row.status === 1 ? 'warning' : 'success'"
                    @click="toggleStatus(scope.row)"
                  >
                    <el-icon><Switch /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button circle size="small" type="danger" @click="handleDelete(scope.row)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 新增/编辑对话框 -->
      <el-dialog 
        v-model="dialogVisible" 
        :title="isEdit ? '编辑分类' : '新增分类'" 
        width="500px"
        :close-on-click-modal="false"
      >
        <el-form :model="form" label-width="80px" :rules="formRules" ref="formRef">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="图标">
            <el-input v-model="form.icon" placeholder="请输入图标emoji或URL" />
          </el-form-item>
          <el-form-item label="级别">
            <el-input-number v-model="form.level" :min="1" :max="3" />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="form.sortOrder" :min="0" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
        </template>
      </el-dialog>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Switch } from '@element-plus/icons-vue'
import { adminAPI } from '../api'
import AdminNavbar from '../components/AdminNavbar.vue'

const loading = ref(false)
const categories = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  name: '',
  icon: '',
  level: 1,
  sortOrder: 0,
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const filteredCategories = computed(() => {
  if (!searchKeyword.value) return categories.value
  return categories.value.filter(c => 
    c.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const loadCategories = async () => {
  loading.value = true
  try {
    const response = await adminAPI.getCategories()
    if (response.code === 200) {
      categories.value = response.data || []
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    icon: '',
    level: 1,
    sortOrder: 0,
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await adminAPI.updateCategory(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await adminAPI.createCategory(form.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadCategories()
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await adminAPI.updateCategory(row.id, { status: newStatus })
    ElMessage.success(`已${statusText}`)
    loadCategories()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除分类"${row.name}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await adminAPI.deleteCategory(row.id)
      ElMessage.success('删除成功')
      loadCategories()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.admin-categories-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
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

.toolbar-card :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toolbar-card :deep(.el-input__inner) {
  color: #fff;
}

.toolbar-card :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.toolbar-actions {
  display: flex;
  gap: 10px;
}

.toolbar-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
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
  transform: scale(1.1);
}

.category-name {
  font-weight: 500;
}

.category-icon {
  font-size: 20px;
}

.no-icon {
  color: rgba(255, 255, 255, 0.4);
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-dialog) {
  background: rgba(30, 30, 50, 0.95);
  border-radius: 16px;
}

:deep(.el-dialog__title) {
  color: #fff;
}

:deep(.el-dialog__body) {
  color: rgba(255, 255, 255, 0.9);
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.9);
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.el-input__inner) {
  color: #fff;
}
</style>
