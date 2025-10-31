<template>
  <div class="roles-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="showAddDialog">添加角色</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="角色名称、代码或描述" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRoles">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="roles" v-loading="loading">
        <el-table-column prop="roleCode" label="角色代码" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editRole(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteRole(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRoles"
        @current-change="loadRoles"
        class="pagination"
      />
    </el-card>

    <!-- 添加/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '添加角色'" width="500px">
      <el-form :model="roleForm" :rules="rules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色代码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole as deleteRoleApi } from '@/api/roles'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const roleFormRef = ref()

// 角色列表
const roles = ref([])

// 搜索表单
const searchForm = ref({
  keyword: ''
})

// 分页
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 角色表单
const roleForm = ref({
  roleCode: '',
  roleName: '',
  description: ''
})

// 表单验证规则
const rules = {
  roleCode: [{ required: true, message: '请输入角色代码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

// 加载角色列表
const loadRoles = async () => {
  loading.value = true
  try {
    const response = await getRoleList({
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm.value
    })
    
    roles.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: ''
  }
  pagination.value.current = 1
  loadRoles()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  roleForm.value = {
    roleCode: '',
    roleName: '',
    description: ''
  }
  dialogVisible.value = true
}

// 编辑角色
const editRole = (role) => {
  isEdit.value = true
  roleForm.value = { ...role }
  dialogVisible.value = true
}

// 保存角色
const saveRole = async () => {
  if (!roleFormRef.value) return
  
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateRole(roleForm.value.id, roleForm.value)
          ElMessage.success('更新角色成功')
        } else {
          await addRole(roleForm.value)
          ElMessage.success('添加角色成功')
        }
        
        dialogVisible.value = false
        loadRoles()
      } catch (error) {
        ElMessage.error(error.message || '保存角色失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 删除角色
const deleteRole = async (roleId) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteRoleApi(roleId)
    ElMessage.success('删除角色成功')
    loadRoles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除角色失败')
    }
  }
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.roles-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
