<template>
  <div class="grade-input">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩管理</span>
          <div>
            <el-button type="primary" @click="showManageMode = true" v-if="searchForm.courseScheduleId">
              <el-icon><DocumentCopy /></el-icon>
              查看已录入成绩
            </el-button>
            <el-button type="info" @click="showUploadDialog = true" v-if="searchForm.courseScheduleId">
              <el-icon><Upload /></el-icon>
              导入成绩文件
            </el-button>
            <el-button type="warning" @click="showTimeDialog = true" v-if="searchForm.courseScheduleId">
              <el-icon><Clock /></el-icon>
              设置查询时段
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 课程选择 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="选择课程">
          <el-select v-model="searchForm.courseScheduleId" placeholder="请选择课程" @change="handleCourseChange" style="width: 300px;" filterable>
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="`${course.courseName} (${course.semester || ''})`"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadStudentList" :disabled="!searchForm.courseScheduleId">
            <el-icon><Search /></el-icon>
            加载学生名单
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 学生成绩表格 -->
      <el-table
        v-loading="loading"
        :data="studentList"
        stripe
        style="width: 100%"
        v-if="searchForm.courseScheduleId"
      >
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column label="成绩" width="150">
          <template #default="{ row, $index }">
            <el-input
              v-model="gradeFormList[$index].score"
              placeholder="请输入成绩"
              type="number"
              :min="0"
              :max="100"
              @input="handleScoreInput(row, $index)"
            />
          </template>
        </el-table-column>
        <el-table-column label="备注" width="200">
          <template #default="{ row, $index }">
            <el-input
              v-model="gradeFormList[$index].remark"
              placeholder="请输入备注"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row, $index }">
            <el-button
              type="primary"
              size="small"
              @click="handleSingleSubmit(row, $index)"
              :disabled="!gradeFormList[$index].score"
            >
              提交
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div class="batch-actions" v-if="searchForm.courseScheduleId && studentList.length > 0">
        <el-button type="success" @click="handleBatchSubmit" :loading="batchSubmitLoading">
          <el-icon><Upload /></el-icon>
          批量提交成绩
        </el-button>
        <el-button @click="handleClearAll">
          <el-icon><Delete /></el-icon>
          清空所有
        </el-button>
      </div>
    </el-card>
    
    <!-- 成绩统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;" v-if="searchForm.courseScheduleId">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ studentList.length }}</h3>
              <p>总学生数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon submitted">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ submittedCount }}</h3>
              <p>已录入</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ pendingCount }}</h3>
              <p>待录入</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon average">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ averageScore }}</h3>
              <p>平均分</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
      <!-- 文件上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="导入成绩文件"
      width="600px"
      @close="handleUploadDialogClose"
    >
      <el-upload
        ref="uploadRef"
        class="upload-demo"
        drag
        :action="uploadAction"
        :auto-upload="true"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :on-change="handleFileChange"
        :file-list="fileList"
        :limit="1"
        accept=".xlsx,.csv"
        :headers="uploadHeaders"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持上传Excel(.xlsx)和CSV(.csv)文件<br>
            文件格式：学号、学生姓名、课程、分数、学期<br>
            <strong style="color: #e6a23c;">提示：</strong>请确保Excel文件是标准格式。如遇到问题，建议使用CSV格式上传
          </div>
        </template>
      </el-upload>
      
      <template #footer>
        <el-button @click="showUploadDialog = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 查询时段设置对话框 -->
    <el-dialog
      v-model="showTimeDialog"
      title="设置成绩查询时段"
      width="500px"
      @close="handleTimeDialogClose"
    >
      <el-form :model="timeForm" label-width="120px">
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="timeForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="timeForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showTimeDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSetTime" :loading="timeSetLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 成绩管理对话框（查看、编辑、删除） -->
    <el-dialog
      v-model="showManageMode"
      title="成绩管理"
      width="900px"
      @close="handleManageDialogClose"
    >
      <div style="margin-bottom: 20px;">
        <el-input
          v-model="manageSearchKeyword"
          placeholder="搜索学号、姓名"
          clearable
          style="width: 300px;"
          @input="loadGradeList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <el-table
        v-loading="manageLoading"
        :data="gradeList"
        stripe
        style="width: 100%"
        max-height="400"
      >
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="{ row }">
            <span :class="getScoreClass(row.score)">
              {{ row.score || '未录入' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="100" />
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column prop="auditStatusText" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">
              {{ row.auditStatusText || '未审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEditGrade(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteGrade(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-pagination
          v-model:current-page="managePagination.current"
          v-model:page-size="managePagination.size"
          :total="managePagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleManageSizeChange"
          @current-change="handleManageCurrentChange"
        />
      </template>
    </el-dialog>
    
    <!-- 编辑成绩对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑成绩"
      width="500px"
    >
      <el-form :model="editGradeForm" label-width="100px">
        <el-form-item label="学号">
          <el-input v-model="editGradeForm.studentNo" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editGradeForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="课程">
          <el-input v-model="editGradeForm.courseName" disabled />
        </el-form-item>
        <el-form-item label="成绩" required>
          <el-input-number
            v-model="editGradeForm.score"
            :min="0"
            :max="100"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="editSubmitLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { batchInputGrades, setQueryTime, getGradeList, deleteGrade } from '@/api/grade'
import { getCourseScheduleList } from '@/api/courseSchedule'
import { getCourseSelectionList } from '@/api/courseSelection'
import { useUserStore } from '@/store/user'

// 用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const batchSubmitLoading = ref(false)
const importLoading = ref(false)
const timeSetLoading = ref(false)

const courseList = ref([])
const studentList = ref([])
const gradeFormList = ref([])
const fileList = ref([])

// 对话框
const showUploadDialog = ref(false)
const showTimeDialog = ref(false)
const showManageMode = ref(false)
const showEditDialog = ref(false)

// 成绩管理相关
const gradeList = ref([])
const manageLoading = ref(false)
const manageSearchKeyword = ref('')
const managePagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 编辑成绩表单
const editGradeForm = ref({
  id: null,
  studentNo: '',
  studentName: '',
  courseName: '',
  score: null
})
const editSubmitLoading = ref(false)

// 搜索表单
const searchForm = reactive({
  courseScheduleId: null
})

// 查询时段表单
const timeForm = reactive({
  startTime: '',
  endTime: ''
})

// 上传配置
const uploadAction = computed(() => {
  return '/api/grades/import'
})

const uploadHeaders = computed(() => {
  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('token') || ''
  console.log('Upload headers:', { token: token ? 'has token' : 'no token', tokenLength: token?.length })
  return {
    'Authorization': token ? `Bearer ${token}` : ''
  }
})

// 计算属性
const submittedCount = computed(() => {
  return gradeFormList.value.filter(item => item.score).length
})

const pendingCount = computed(() => {
  return gradeFormList.value.filter(item => !item.score).length
})

const averageScore = computed(() => {
  const scores = gradeFormList.value.filter(item => item.score).map(item => parseFloat(item.score))
  if (scores.length === 0) return '0.0'
  const average = scores.reduce((sum, score) => sum + score, 0) / scores.length
  return average.toFixed(1)
})

// 方法
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

const loadStudentList = async () => {
  if (!searchForm.courseScheduleId) return
  
  loading.value = true
  try {
    // 获取该课程的所有选课记录
    const response = await getCourseSelectionList({
      courseScheduleId: searchForm.courseScheduleId,
      status: 2 // 已确认的选课
    })
    
    const records = response.data.records || []
    
    // 转换为学生列表
    studentList.value = records.map(record => ({
      studentId: record.studentId,
      studentNo: record.studentNo,
      studentName: record.studentName,
      className: record.className || '',
      major: record.major || '',
      score: record.score || ''
    }))
    
    // 初始化成绩表单
    gradeFormList.value = studentList.value.map(student => ({
      studentId: student.studentId,
      courseScheduleId: searchForm.courseScheduleId,
      score: student.score || '',
      remark: ''
    }))
  } catch (error) {
    console.error('加载学生名单失败:', error)
    ElMessage.error('加载学生名单失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const handleCourseChange = () => {
  studentList.value = []
  gradeFormList.value = []
}

const handleScoreInput = (row, index) => {
  const score = parseFloat(gradeFormList.value[index].score)
  if (score < 0 || score > 100) {
    ElMessage.warning('成绩应在0-100之间')
    gradeFormList.value[index].score = ''
  }
}

const handleSingleSubmit = async (row, index) => {
  const gradeData = gradeFormList.value[index]
  if (!gradeData.score) {
    ElMessage.warning('请输入成绩')
    return
  }
  
  try {
    await batchInputGrades(searchForm.courseScheduleId, [gradeData])
    ElMessage.success('成绩提交成功')
  } catch (error) {
    ElMessage.error('成绩提交失败')
  }
}

const handleBatchSubmit = async () => {
  const validGrades = gradeFormList.value.filter(item => item.score)
  if (validGrades.length === 0) {
    ElMessage.warning('没有可提交的成绩')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要提交 ${validGrades.length} 个学生的成绩吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    batchSubmitLoading.value = true
    await batchInputGrades(searchForm.courseScheduleId, validGrades)
    ElMessage.success('批量提交成绩成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量提交成绩失败')
    }
  } finally {
    batchSubmitLoading.value = false
  }
}

const handleClearAll = () => {
  gradeFormList.value.forEach(item => {
    item.score = ''
    item.remark = ''
  })
  ElMessage.success('已清空所有成绩')
}

const handleFileChange = (file) => {
  fileList.value = [file]
}

const handleUploadDialogClose = () => {
  fileList.value = []
}

const handleUploadSuccess = (response, file, fileList) => {
  ElMessage.success(response.message || '成绩导入成功')
  showUploadDialog.value = false
  
  // 刷新学生名单
  if (searchForm.courseScheduleId) {
    loadStudentList()
  }
}

const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('成绩导入失败，请检查文件格式或网络连接')
}

const handleImportFile = async () => {
  // 不再需要手动上传，由el-upload自动处理
}

const handleSetTime = async () => {
  if (!timeForm.startTime || !timeForm.endTime) {
    ElMessage.warning('请选择完整的时段')
    return
  }
  
  if (timeForm.startTime >= timeForm.endTime) {
    ElMessage.warning('开始时间必须早于结束时间')
    return
  }
  
  timeSetLoading.value = true
  try {
    await setQueryTime(searchForm.courseScheduleId, timeForm.startTime, timeForm.endTime)
    ElMessage.success('查询时段设置成功')
    showTimeDialog.value = false
  } catch (error) {
    ElMessage.error('设置失败: ' + (error.response?.data?.message || error.message))
  } finally {
    timeSetLoading.value = false
  }
}

const handleTimeDialogClose = () => {
  timeForm.startTime = ''
  timeForm.endTime = ''
}

const handleManageDialogClose = () => {
  manageSearchKeyword.value = ''
  gradeList.value = []
}

const loadGradeList = async () => {
  if (!searchForm.courseScheduleId) return
  
  manageLoading.value = true
  try {
    const response = await getGradeList({
      current: managePagination.current,
      size: managePagination.size,
      courseScheduleId: searchForm.courseScheduleId,
      keyword: manageSearchKeyword.value
    })
    
    gradeList.value = response.data.records || []
    managePagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载成绩列表失败')
  } finally {
    manageLoading.value = false
  }
}

const handleManageSizeChange = (size) => {
  managePagination.size = size
  loadGradeList()
}

const handleManageCurrentChange = (current) => {
  managePagination.current = current
  loadGradeList()
}

const handleEditGrade = (row) => {
  editGradeForm.value = {
    id: row.id,
    studentNo: row.studentNo,
    studentName: row.studentName,
    courseName: row.courseName,
    score: parseFloat(row.score)
  }
  showEditDialog.value = true
}

const handleSaveEdit = async () => {
  if (!editGradeForm.value.score || editGradeForm.value.score < 0 || editGradeForm.value.score > 100) {
    ElMessage.warning('成绩必须在0-100之间')
    return
  }
  
  editSubmitLoading.value = true
  try {
    // 更新成绩（使用录入接口，已包含更新逻辑）
    await batchInputGrades(searchForm.courseScheduleId, [{
      studentId: gradeList.value.find(g => g.id === editGradeForm.value.id)?.studentId,
      courseScheduleId: searchForm.courseScheduleId,
      score: editGradeForm.value.score
    }])
    
    ElMessage.success('成绩更新成功')
    showEditDialog.value = false
    loadGradeList()
  } catch (error) {
    ElMessage.error('更新失败: ' + (error.response?.data?.message || error.message))
  } finally {
    editSubmitLoading.value = false
  }
}

const handleDeleteGrade = (row) => {
  ElMessageBox.confirm('确定要删除这条成绩记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteGrade(row.id)
      ElMessage.success('删除成功')
      loadGradeList()
    } catch (error) {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
    }
  }).catch(() => {})
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

// 监听管理模式打开
watch(showManageMode, (newVal) => {
  if (newVal && searchForm.courseScheduleId) {
    loadGradeList()
  }
})

// 生命周期
onMounted(() => {
  loadCourseList()
})
</script>

<style scoped>
.grade-input {
  padding: 20px;
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.batch-actions {
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

.stat-icon.submitted {
  background-color: #67c23a;
}

.stat-icon.pending {
  background-color: #e6a23c;
}

.stat-icon.average {
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
</style>
