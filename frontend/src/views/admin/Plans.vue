<template>
  <div class="teaching-plan-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教学计划管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加教学计划
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入计划名称、专业或年级"
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
        <el-form-item label="专业">
          <el-input
            v-model="searchForm.major"
            placeholder="请输入专业"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="年级">
          <el-input
            v-model="searchForm.grade"
            placeholder="请输入年级"
            clearable
            @keyup.enter="handleSearch"
          />
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
      
      <!-- 教学计划表格 -->
      <el-table
        v-loading="loading"
        :data="planList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="planName" label="计划名称" width="200" />
        <el-table-column prop="deptName" label="院系" width="120" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="totalCredits" label="总学分" width="100" />
        <el-table-column prop="statusText" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
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
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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
    
    <!-- 添加/编辑教学计划对话框 -->
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
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入计划名称" />
        </el-form-item>
        
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
            <el-form-item label="年级" prop="grade">
              <el-input v-model="form.grade" placeholder="请输入年级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总学分" prop="totalCredits">
              <el-input-number 
                v-model="form.totalCredits" 
                :min="0" 
                :max="200" 
                placeholder="请输入总学分"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="计划描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入计划描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getTeachingPlanList, 
  addTeachingPlan, 
  updateTeachingPlan, 
  deleteTeachingPlan, 
  changeTeachingPlanStatus 
} from '@/api/teachingPlan'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const planList = ref([])

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
  major: '',
  grade: ''
})

// 表单数据
const form = reactive({
  id: null,
  planName: '',
  deptId: null,
  major: '',
  grade: '',
  totalCredits: null,
  description: ''
})

// 表单验证规则
const formRules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' }
  ],
  totalCredits: [
    { required: true, message: '请输入总学分', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑教学计划' : '添加教学计划')

// 方法
const loadPlanList = async () => {
  loading.value = true
  try {
    const response = await getTeachingPlanList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    
    planList.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取教学计划列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadPlanList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    deptId: null,
    major: '',
    grade: ''
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
    await ElMessageBox.confirm('确定要删除该教学计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteTeachingPlan(row.id)
    ElMessage.success('删除成功')
    loadPlanList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该教学计划吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await changeTeachingPlanStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadPlanList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateTeachingPlan(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await addTeachingPlan(form)
          ElMessage.success('添加成功')
        }
        
        dialogVisible.value = false
        loadPlanList()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
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
    planName: '',
    deptId: null,
    major: '',
    grade: '',
    totalCredits: null,
    description: ''
  })
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadPlanList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadPlanList()
}

// 生命周期
onMounted(() => {
  loadPlanList()
})
</script>

<style scoped>
.teaching-plan-management {
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
