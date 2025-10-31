<template>
  <div class="student-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加学生
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入学号、姓名或用户名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="院系">
          <el-select v-model="searchForm.deptId" placeholder="请选择院系" clearable>
            <el-option label="计算机学院" :value="1" />
            <el-option label="软件学院" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="在校" :value="1" />
            <el-option label="休学" :value="2" />
            <el-option label="退学" :value="3" />
            <el-option label="毕业" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 学生表格 -->
      <el-table
        v-loading="loading"
        :data="studentList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="genderText" label="性别" width="80" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="100" />
        <el-table-column prop="statusText" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" @click="handleStatusChange(row)">
              学籍变动
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑学生对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="院系" prop="deptId">
              <el-select v-model="form.deptId" placeholder="请选择院系">
                <el-option label="计算机学院" :value="1" />
                <el-option label="软件学院" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学年份" prop="enrollmentYear">
              <el-date-picker
                v-model="form.enrollmentYear"
                type="year"
                placeholder="请选择入学年份"
                format="YYYY"
                value-format="YYYY"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 学籍变动对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="学籍变动"
      width="500px"
    >
      <el-form
        ref="statusFormRef"
        :model="statusForm"
        :rules="statusFormRules"
        label-width="100px"
      >
        <el-form-item label="学生姓名">
          <el-input v-model="currentStudent.name" disabled />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="currentStudent.studentNo" disabled />
        </el-form-item>
        <el-form-item label="变动类型" prop="statusType">
          <el-select v-model="statusForm.statusType" placeholder="请选择变动类型">
            <el-option label="注册" :value="1" />
            <el-option label="转学" :value="2" />
            <el-option label="毕业" :value="3" />
            <el-option label="休学" :value="4" />
            <el-option label="退学" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="变动原因" prop="reason">
          <el-input
            v-model="statusForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入变动原因"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSubmit" :loading="statusSubmitLoading">
          确认变动
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { getStudentList, addStudent, updateStudent, deleteStudent, changeStudentStatus } from '@/api/student'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const statusSubmitLoading = ref(false)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const isEdit = ref(false)

const studentList = ref([])
const currentStudent = ref({})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  deptId: null,
  status: null
})

// 表单数据
const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  username: '',
  password: '',
  realName: '',
  gender: null,
  deptId: null,
  major: '',
  className: '',
  enrollmentYear: null
})

// 学籍变动表单
const statusForm = reactive({
  studentId: null,
  statusType: null,
  reason: ''
})

// 表单验证规则
const formRules = {
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  deptId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  className: [
    { required: true, message: '请输入班级', trigger: 'blur' }
  ],
  enrollmentYear: [
    { required: true, message: '请选择入学年份', trigger: 'change' }
  ]
}

const statusFormRules = {
  statusType: [
    { required: true, message: '请选择变动类型', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入变动原因', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑学生' : '添加学生')

const formRef = ref()
const statusFormRef = ref()

// 方法
const loadStudentList = async () => {
  loading.value = true
  try {
    const response = await getStudentList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    
    studentList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadStudentList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    deptId: null,
    status: null
  })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学生吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    loadStudentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = (row) => {
  currentStudent.value = row
  statusForm.studentId = row.id
  statusForm.statusType = null
  statusForm.reason = ''
  statusDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 确保enrollmentYear只包含年份部分
        const submitData = { ...form }
        if (submitData.enrollmentYear) {
          // 如果包含日期格式，只取年份部分
          submitData.enrollmentYear = submitData.enrollmentYear.substring(0, 4)
        }
        
        if (isEdit.value) {
          await updateStudent(form.id, submitData)
          ElMessage.success('更新成功')
        } else {
          await addStudent(submitData)
          ElMessage.success('添加成功')
        }
        
        dialogVisible.value = false
        loadStudentList()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleStatusSubmit = async () => {
  if (!statusFormRef.value) return
  
  await statusFormRef.value.validate(async (valid) => {
    if (valid) {
      statusSubmitLoading.value = true
      try {
        await changeStudentStatus(statusForm.studentId, statusForm)
        ElMessage.success('学籍变动处理成功')
        statusDialogVisible.value = false
        loadStudentList()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        statusSubmitLoading.value = false
      }
    }
  })
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    studentNo: '',
    name: '',
    username: '',
    password: '',
    realName: '',
    gender: null,
    deptId: null,
    major: '',
    className: '',
    enrollmentYear: null
  })
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadStudentList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadStudentList()
}

const getStatusType = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'warning'
    case 3:
      return 'danger'
    case 4:
      return 'info'
    default:
      return ''
  }
}

// 生命周期
onMounted(() => {
  loadStudentList()
})
</script>

<style scoped>
.student-management {
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
