<template>
  <div class="student-grades">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的成绩</span>
          <el-button @click="loadGradeList">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      
      <!-- 查询时段提示 -->
      <el-alert
        v-if="hasTimeRestrictedGrades"
        title="部分成绩暂未开放查询"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px"
      >
        <template #default>
          部分课程的成绩查询时段尚未开放，请在开放时段内查询成绩。
        </template>
      </el-alert>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.courseName"
            placeholder="请输入课程名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable style="width: 150px;">
            <el-option label="2024春季" value="2024春季" />
            <el-option label="2023秋季" value="2023秋季" />
            <el-option label="2023春季" value="2023春季" />
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
      
      <!-- 成绩表格 -->
      <el-table
        v-loading="loading"
        :data="gradeList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="courseType" label="课程类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.courseType === 1 ? 'primary' : 'success'">
              {{ row.courseType === 1 ? '必修' : '选修' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="{ row }">
            <span :class="getScoreClass(row.score)">
              {{ row.score || '未录入' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="auditStatusText" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">
              {{ row.auditStatusText || '未审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="160" />
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 成绩统计 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ totalCourses }}</h3>
              <p>总课程数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ completedCourses }}</h3>
              <p>已完成</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon gpa">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ gpa }}</h3>
              <p>平均绩点</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon credits">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ totalCredits }}</h3>
              <p>总学分</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getGradesByStudent } from '@/api/grade'
import { useUserStore } from '@/store/user'

// 用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)

const gradeList = ref([])

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  courseName: '',
  semester: ''
})

// 计算属性
const totalCourses = computed(() => gradeList.value.length)

const completedCourses = computed(() => {
  return gradeList.value.filter(grade => grade.score && grade.auditStatus === 2).length
})

const hasTimeRestrictedGrades = computed(() => {
  return gradeList.value.some(grade => 
    grade.queryStartTime && grade.queryEndTime && 
    (!grade.score || grade.auditStatusText === '查询未开放')
  )
})

const gpa = computed(() => {
  const validGrades = gradeList.value.filter(grade => grade.score && grade.auditStatus === 2)
  if (validGrades.length === 0) return '0.0'
  
  let totalPoints = 0
  let totalCredits = 0
  
  validGrades.forEach(grade => {
    const score = parseFloat(grade.score)
    const credits = parseFloat(grade.credits)
    
    // 成绩转绩点 (简化计算)
    let points = 0
    if (score >= 90) points = 4.0
    else if (score >= 80) points = 3.0
    else if (score >= 70) points = 2.0
    else if (score >= 60) points = 1.0
    else points = 0.0
    
    totalPoints += points * credits
    totalCredits += credits
  })
  
  return totalCredits > 0 ? (totalPoints / totalCredits).toFixed(2) : '0.0'
})

const totalCredits = computed(() => {
  return gradeList.value.reduce((sum, grade) => {
    return sum + (parseFloat(grade.credits) || 0)
  }, 0)
})

// 方法
const loadGradeList = async () => {
  loading.value = true
  try {
    const studentId = userStore.userInfo.id
    const response = await getGradesByStudent(studentId)
    gradeList.value = response.data || []
    pagination.total = gradeList.value.length
  } catch (error) {
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadGradeList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    courseName: '',
    semester: ''
  })
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadGradeList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadGradeList()
}

const getScoreClass = (score) => {
  if (!score) return 'score-empty'
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-medium'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const getAuditStatusType = (auditStatus) => {
  switch (auditStatus) {
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
  loadGradeList()
})
</script>

<style scoped>
.student-grades {
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

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background-color: #409eff;
}

.stat-icon.completed {
  background-color: #67c23a;
}

.stat-icon.gpa {
  background-color: #e6a23c;
}

.stat-icon.credits {
  background-color: #f56c6c;
}

.stat-info h3 {
  margin: 0;
  font-size: 28px;
  color: #303133;
}

.stat-info p {
  margin: 5px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.score-excellent {
  color: #67c23a;
  font-weight: bold;
}

.score-good {
  color: #409eff;
  font-weight: bold;
}

.score-medium {
  color: #e6a23c;
  font-weight: bold;
}

.score-pass {
  color: #909399;
}

.score-fail {
  color: #f56c6c;
  font-weight: bold;
}

.score-empty {
  color: #c0c4cc;
}
</style>
