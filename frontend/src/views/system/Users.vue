<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="showAddDialog">添加用户</el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名或真实姓名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.roleId" placeholder="选择角色" clearable style="width: 120px;">
            <el-option label="管理员" :value="1" />
            <el-option label="教师" :value="3" />
            <el-option label="学生" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 120px;">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadUsers">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="users" v-loading="loading">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="roleCode" label="角色">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleCode)">
              {{ row.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editUser(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteUser(row.id)">删除</el-button>
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
        @size-change="loadUsers"
        @current-change="loadUsers"
        class="pagination"
      />
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" style="width: 100%;">
            <el-option label="管理员" :value="1" />
            <el-option label="教师" :value="3" />
            <el-option label="学生" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status" style="width: 100%;">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser as deleteUserApi } from '@/api/users'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const userFormRef = ref()

// 用户列表
const users = ref([])

// 搜索表单
const searchForm = ref({
  keyword: '',
  roleId: null,
  status: null
})

// 分页
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 用户表单
const userForm = ref({
  username: '',
  realName: '',
  password: '',
  roleId: null,
  status: 1
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const response = await getUserList({
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm.value
    })
    
    users.value = response.data.records
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    roleId: null,
    status: null
  }
  pagination.value.current = 1
  loadUsers()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  userForm.value = {
    username: '',
    realName: '',
    password: '',
    roleId: null,
    status: 1
  }
  dialogVisible.value = true
}

// 编辑用户
const editUser = (user) => {
  isEdit.value = true
  userForm.value = { ...user }
  dialogVisible.value = true
}

// 保存用户
const saveUser = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateUser(userForm.value.id, userForm.value)
          ElMessage.success('更新用户成功')
        } else {
          await addUser(userForm.value)
          ElMessage.success('添加用户成功')
        }
        
        dialogVisible.value = false
        loadUsers()
      } catch (error) {
        ElMessage.error(error.message || '保存用户失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 删除用户
const deleteUser = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteUserApi(userId)
    ElMessage.success('删除用户成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败')
    }
  }
}

// 获取角色类型
const getRoleType = (roleCode) => {
  const roleMap = {
    'admin': 'danger',
    'teacher': 'warning',
    'student': 'success'
  }
  return roleMap[roleCode] || 'info'
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users-container {
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
