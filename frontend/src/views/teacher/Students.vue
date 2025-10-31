<template>
  <div class="teacher-students">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生名单</span>
          <el-button @click="loadStudentList">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseScheduleId" placeholder="请选择课程" @change="handleCourseChange" style="width: 300px;">
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchForm.studentName"
            placeholder="请输入学生姓名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="学号">
          <el-input
            v-model="searchForm.studentNo"
            placeholder="请输入学号"
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
      
      <!-- 学生名单表格 -->
      <el-table
        v-loading="loading"
        :data="studentList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="100" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="selectTime" label="选课时间" width="160" />
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewGrades(row)">
              查看成绩
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCourseScheduleList } from '@/api/courseSchedule'
import { getCourseSelectionList } from '@/api/courseSelection'
import { useUserStore } from '@/store/user'

// 用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)

const studentList = ref([])
const courseList = ref([])

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  courseScheduleId: null,
  studentName: '',
  studentNo: ''
})

// 方法
const loadStudentList = async () => {
  loading.value = true
  try {
    const response = await getCourseSelectionList({
      current: pagination.current,
      size: pagination.size,
      courseScheduleId: searchForm.courseScheduleId,
      status: 2 // 已确认的选课
    })
    
    studentList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取学生名单失败')
  } finally {
    loading.value = false
  }
}

const loadCourseList = async () => {
  try {
    const teacherId = userStore.userInfo.id
    const response = await getCourseScheduleList({
      teacherId: teacherId
    })
    courseList.value = response.data.records || []
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadStudentList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    courseScheduleId: null,
    studentName: '',
    studentNo: ''
  })
  handleSearch()
}

const handleCourseChange = () => {
  handleSearch()
}

const handleViewGrades = (row) => {
  ElMessage.info(`查看学生${row.studentName}的成绩`)
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
      return 'warning'
    case 2:
      return 'success'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

// 生命周期
onMounted(() => {
  loadStudentList()
  loadCourseList()
})
</script>

<style scoped>
.teacher-students {
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
