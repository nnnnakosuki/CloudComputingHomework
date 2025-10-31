<template>
  <div class="teacher-courses">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
          <el-button @click="loadCourseList">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期" @change="handleSearch" style="width: 150px;">
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2023秋季" value="2023秋季" />
            <el-option label="2023春季" value="2023春季" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.courseName"
            placeholder="请输入课程名称"
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
      
      <!-- 课程列表表格 -->
      <el-table
        v-loading="loading"
        :data="courseList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="courseTypeText" label="课程类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.courseType === 1 ? 'primary' : 'success'">
              {{ row.courseTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="maxStudents" label="容量" width="80" />
        <el-table-column prop="currentStudents" label="已选" width="80" />
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
            <el-button type="primary" size="small" @click="handleViewStudents(row)">
              学生名单
            </el-button>
            <el-button type="success" size="small" @click="handleInputGrades(row)">
              成绩录入
            </el-button>
            <el-button type="info" size="small" @click="handleViewSchedule(row)">
              查看课表
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
    
    <!-- 学生名单对话框 -->
    <el-dialog
      v-model="studentDialogVisible"
      :title="`${currentCourse?.courseName} - 学生名单`"
      width="900px"
    >
      <el-table
        v-loading="studentLoading"
        :data="studentList"
        stripe
        max-height="400"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="major" label="专业" width="180" />
        <el-table-column prop="genderText" label="性别" width="80" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-pagination
          v-model:current-page="studentPagination.current"
          v-model:page-size="studentPagination.size"
          :total="studentPagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleStudentSizeChange"
          @current-change="handleStudentCurrentChange"
          style="justify-content: flex-start;"
        />
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getCourseScheduleList } from '@/api/courseSchedule'
import { getCourseSelectionList } from '@/api/courseSelection'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const studentLoading = ref(false)

const courseList = ref([])
const studentList = ref([])
const currentCourse = ref(null)
const studentDialogVisible = ref(false)

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 学生名单分页数据
const studentPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  semester: '2024春季',
  courseName: ''
})

// 方法
const loadCourseList = async () => {
  loading.value = true
  try {
    const teacherId = userStore.userInfo.id
    const response = await getCourseScheduleList({
      current: pagination.current,
      size: pagination.size,
      semester: searchForm.semester,
      teacherId: teacherId
    })
    
    courseList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadCourseList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    semester: '2024春季',
    courseName: ''
  })
  handleSearch()
}

const handleViewStudents = async (row) => {
  currentCourse.value = row
  studentDialogVisible.value = true
  await loadStudentList(row.id)
}

const loadStudentList = async (courseScheduleId) => {
  studentLoading.value = true
  try {
    const response = await getCourseSelectionList({
      current: studentPagination.current,
      size: studentPagination.size,
      courseScheduleId: courseScheduleId,
      status: 2 // 已确认的选课
    })
    
    // 转换数据，添加性别文本和状态文本
    studentList.value = (response.data.records || []).map(item => ({
      ...item,
      genderText: item.gender === 1 ? '男' : '女',
      statusText: item.status === 1 ? '待确认' : item.status === 2 ? '已确认' : '已退课'
    }))
    
    studentPagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载学生名单失败: ' + (error.response?.data?.message || error.message))
  } finally {
    studentLoading.value = false
  }
}

const handleStudentSizeChange = (size) => {
  studentPagination.size = size
  studentPagination.current = 1
  if (currentCourse.value) {
    loadStudentList(currentCourse.value.id)
  }
}

const handleStudentCurrentChange = (current) => {
  studentPagination.current = current
  if (currentCourse.value) {
    loadStudentList(currentCourse.value.id)
  }
}

// 监听对话框关闭，重置分页
watch(studentDialogVisible, (newVal) => {
  if (!newVal) {
    studentPagination.current = 1
    studentPagination.size = 10
    studentList.value = []
  }
})

const handleInputGrades = (row) => {
  ElMessage.info(`录入课程"${row.courseName}"的成绩`)
  // 这里可以跳转到成绩录入页面
  router.push('/teacher/grades')
}

const handleViewSchedule = (row) => {
  ElMessage.info(`查看课程"${row.courseName}"的课表`)
  // 这里可以跳转到课表页面
  router.push('/teacher/schedule')
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadCourseList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadCourseList()
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
      return 'info'
  }
}

// 生命周期
onMounted(() => {
  loadCourseList()
})
</script>

<style scoped>
.teacher-courses {
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
