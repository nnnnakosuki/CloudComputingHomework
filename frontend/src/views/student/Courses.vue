<template>
  <div class="course-selection">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程浏览与选课</span>
          <el-button @click="loadAvailableCourses">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      
      <!-- 筛选表单 -->
      <el-form :model="filterForm" inline class="filter-form">
        <el-form-item label="学期">
          <el-select v-model="filterForm.semester" placeholder="请选择学期" @change="handleFilterChange" style="width: 150px;">
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2023秋季" value="2023秋季" />
            <el-option label="2023春季" value="2023春季" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="filterForm.courseType" placeholder="请选择课程类型" @change="handleFilterChange" style="width: 150px;">
            <el-option label="全部" :value="null" />
            <el-option label="必修" :value="1" />
            <el-option label="选修" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="filterForm.keyword"
            placeholder="请输入课程名称"
            clearable
            @keyup.enter="handleFilterChange"
          />
        </el-form-item>
      </el-form>
      
      <!-- 可选课程表格 -->
      <el-table
        v-loading="loading"
        :data="filteredCourses"
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
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="maxStudents" label="容量" width="80" />
        <el-table-column prop="currentStudents" label="已选" width="80" />
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1 && row.currentStudents < row.maxStudents"
              type="primary"
              size="small"
              @click="handleSelectCourse(row)"
              :disabled="isCourseSelected(row.id)"
            >
              {{ isCourseSelected(row.id) ? '已选择' : '选择' }}
            </el-button>
            <el-button
              v-else
              type="info"
              size="small"
              disabled
            >
              {{ row.status !== 1 ? '未开放' : '已满' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 我的选课 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>我的选课</span>
        </div>
      </template>
      
      <el-table
        v-loading="selectionLoading"
        :data="mySelections"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getSelectionStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="选课时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleDropCourse(row)"
            >
              退课
            </el-button>
            <el-button
              v-else
              type="info"
              size="small"
              disabled
            >
              已确认
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableCourses } from '@/api/courseSchedule'
import { selectCourse, dropCourse, getMySelections } from '@/api/courseSelection'
import { useUserStore } from '@/store/user'

// 用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const selectionLoading = ref(false)

const availableCourses = ref([])
const mySelections = ref([])

// 筛选表单
const filterForm = reactive({
  semester: '2024春季',
  courseType: null,
  keyword: ''
})

// 计算属性
const filteredCourses = computed(() => {
  let courses = availableCourses.value
  
  if (filterForm.courseType !== null) {
    courses = courses.filter(course => course.courseType === filterForm.courseType)
  }
  
  if (filterForm.keyword) {
    courses = courses.filter(course => 
      course.courseName.includes(filterForm.keyword) || 
      course.courseCode.includes(filterForm.keyword)
    )
  }
  
  return courses
})

// 方法
const loadAvailableCourses = async () => {
  if (!filterForm.semester) return
  
  loading.value = true
  try {
    const studentId = userStore.userInfo.id
    const response = await getAvailableCourses(studentId, filterForm.semester)
    availableCourses.value = response.data || []
  } catch (error) {
    ElMessage.error('获取可选课程失败')
  } finally {
    loading.value = false
  }
}

const loadMySelections = async () => {
  selectionLoading.value = true
  try {
    const response = await getMySelections(filterForm.semester)
    mySelections.value = response.data || []
  } catch (error) {
    ElMessage.error('获取我的选课失败')
  } finally {
    selectionLoading.value = false
  }
}

const handleFilterChange = () => {
  loadAvailableCourses()
}

const handleSelectCourse = async (course) => {
  try {
    await ElMessageBox.confirm(`确定要选择课程"${course.courseName}"吗？`, '确认选课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const selectionData = {
      courseScheduleId: course.id
    }
    
    await selectCourse(selectionData)
    ElMessage.success('选课成功')
    
    // 刷新数据
    loadAvailableCourses()
    loadMySelections()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('选课失败')
    }
  }
}

const handleDropCourse = async (selection) => {
  try {
    await ElMessageBox.confirm(`确定要退选课程"${selection.courseName}"吗？`, '确认退课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await dropCourse(selection.id)
    ElMessage.success('退课成功')
    
    // 刷新数据
    loadAvailableCourses()
    loadMySelections()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退课失败')
    }
  }
}

const isCourseSelected = (courseScheduleId) => {
  return mySelections.value.some(selection => selection.courseScheduleId === courseScheduleId)
}

const getSelectionStatusType = (status) => {
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
  loadAvailableCourses()
  loadMySelections()
})
</script>

<style scoped>
.course-selection {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 20px;
}
</style>
