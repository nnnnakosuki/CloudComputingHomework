<template>
  <div class="course-schedule-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>开课计划管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            发布开课计划
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入课程名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable>
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2023秋季" value="2023秋季" />
            <el-option label="2023春季" value="2023春季" />
          </el-select>
        </el-form-item>
        <el-form-item label="任课教师">
          <el-select v-model="searchForm.teacherId" placeholder="请选择教师" clearable>
            <el-option label="王老师" :value="4" />
            <el-option label="刘老师" :value="5" />
            <el-option label="陈老师" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="开放选课" :value="1" />
            <el-option label="选课结束" :value="2" />
            <el-option label="取消开课" :value="0" />
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
      
      <!-- 开课计划表格 -->
      <el-table
        v-loading="loading"
        :data="scheduleList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="courseTypeText" label="课程类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.courseType === 1 ? 'primary' : 'success'">
              {{ row.courseTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="maxStudents" label="最大人数" width="100" />
        <el-table-column prop="currentStudents" label="已选人数" width="100" />
        <el-table-column prop="statusText" label="状态" width="100">
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
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '结束选课' : '开放选课' }}
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
    
    <!-- 添加/编辑开课计划对话框 -->
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
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择课程" @change="handleCourseChange">
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="form.semester" placeholder="请选择学期">
                <el-option label="2024春季" value="2024春季" />
                <el-option label="2023秋季" value="2023秋季" />
                <el-option label="2023春季" value="2023春季" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任课教师" prop="teacherId">
              <el-select v-model="form.teacherId" placeholder="请选择教师">
                <el-option label="王老师" :value="4" />
                <el-option label="刘老师" :value="5" />
                <el-option label="陈老师" :value="6" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="最大选课人数" prop="maxStudents">
          <el-input-number 
            v-model="form.maxStudents" 
            :min="1" 
            :max="200" 
            placeholder="请输入最大选课人数"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCourseScheduleList, 
  publishCourseSchedule, 
  updateCourseSchedule, 
  deleteCourseSchedule, 
  changeCourseScheduleStatus 
} from '@/api/courseSchedule'
import { getAllCourses } from '@/api/course'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const scheduleList = ref([])
const courseList = ref([])

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  semester: '',
  teacherId: null,
  status: null
})

// 表单数据
const form = reactive({
  id: null,
  courseId: null,
  semester: '',
  teacherId: null,
  maxStudents: null
})

// 表单验证规则
const formRules = {
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  semester: [
    { required: true, message: '请选择学期', trigger: 'change' }
  ],
  teacherId: [
    { required: true, message: '请选择任课教师', trigger: 'change' }
  ],
  maxStudents: [
    { required: true, message: '请输入最大选课人数', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑开课计划' : '发布开课计划')

// 方法
const loadScheduleList = async () => {
  loading.value = true
  try {
    const response = await getCourseScheduleList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    
    scheduleList.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取开课计划列表失败')
  } finally {
    loading.value = false
  }
}

const loadCourseList = async () => {
  try {
    const response = await getAllCourses()
    courseList.value = response.data
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadScheduleList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    semester: '',
    teacherId: null,
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
    await ElMessageBox.confirm('确定要删除该开课计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCourseSchedule(row.id)
    ElMessage.success('删除成功')
    loadScheduleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 2 : 1
  const action = newStatus === 1 ? '开放选课' : '结束选课'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该开课计划吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await changeCourseScheduleStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadScheduleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleCourseChange = (courseId) => {
  const course = courseList.value.find(c => c.id === courseId)
  if (course) {
    // 可以根据课程信息设置默认值
  }
}

const formRef = ref()

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateCourseSchedule(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await publishCourseSchedule(form)
          ElMessage.success('发布成功')
        }
        
        dialogVisible.value = false
        loadScheduleList()
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
    courseId: null,
    semester: '',
    teacherId: null,
    maxStudents: null
  })
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadScheduleList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadScheduleList()
}

const getStatusType = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'warning'
    case 0:
      return 'danger'
    default:
      return ''
  }
}

// 生命周期
onMounted(() => {
  loadScheduleList()
  loadCourseList()
})
</script>

<style scoped>
.course-schedule-management {
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
