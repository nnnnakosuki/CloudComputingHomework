<template>
  <div class="grade-audit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩审核</span>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchForm.studentName"
            placeholder="请输入学生姓名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.courseName"
            placeholder="请输入课程名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.auditStatus" placeholder="请选择审核状态" clearable>
            <el-option label="待审核" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
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
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">
            <span :class="getScoreClass(row.score)">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column prop="auditStatusText" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">
              {{ row.auditStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditorName" label="审核员" width="100" />
        <el-table-column prop="auditTime" label="审核时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.auditStatus === 1"
              type="success"
              size="small"
              @click="handleAudit(row, 2)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.auditStatus === 1"
              type="danger"
              size="small"
              @click="handleAudit(row, 3)"
            >
              驳回
            </el-button>
            <el-button
              v-if="row.auditStatus !== 1"
              type="info"
              size="small"
              disabled
            >
              已审核
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
    
    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      :title="auditDialogTitle"
      width="500px"
    >
      <el-form
        ref="auditFormRef"
        :model="auditForm"
        :rules="auditFormRules"
        label-width="100px"
      >
        <el-form-item label="学生姓名">
          <el-input v-model="currentGrade.studentName" disabled />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="currentGrade.courseName" disabled />
        </el-form-item>
        <el-form-item label="成绩">
          <el-input v-model="currentGrade.score" disabled />
        </el-form-item>
        <el-form-item label="任课教师">
          <el-input v-model="currentGrade.teacherName" disabled />
        </el-form-item>
        <el-form-item label="审核意见" prop="remark">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit" :loading="auditSubmitLoading">
          确认审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getGradeList, auditGrade } from '@/api/grade'

// 响应式数据
const loading = ref(false)
const auditSubmitLoading = ref(false)
const auditDialogVisible = ref(false)

const gradeList = ref([])
const currentGrade = ref({})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  studentName: '',
  courseName: '',
  auditStatus: null
})

// 审核表单
const auditForm = reactive({
  gradeId: null,
  auditStatus: null,
  remark: ''
})

// 表单验证规则
const auditFormRules = {
  remark: [
    { required: true, message: '请输入审核意见', trigger: 'blur' }
  ]
}

// 计算属性
const auditDialogTitle = computed(() => {
  return auditForm.auditStatus === 2 ? '审核通过' : '审核驳回'
})

const auditFormRef = ref()

// 方法
const loadGradeList = async () => {
  loading.value = true
  try {
    const response = await getGradeList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    
    gradeList.value = response.data.records || []
    pagination.total = response.data.total || 0
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
    studentName: '',
    courseName: '',
    auditStatus: null
  })
  handleSearch()
}

const handleAudit = (row, auditStatus) => {
  currentGrade.value = row
  auditForm.gradeId = row.id
  auditForm.auditStatus = auditStatus
  auditForm.remark = ''
  auditDialogVisible.value = true
}

const handleAuditSubmit = async () => {
  if (!auditFormRef.value) return
  
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      auditSubmitLoading.value = true
      try {
        await auditGrade(auditForm.gradeId, auditForm.auditStatus, auditForm.remark)
        ElMessage.success('审核成功')
        auditDialogVisible.value = false
        loadGradeList()
      } catch (error) {
        ElMessage.error(error.message || '审核失败')
      } finally {
        auditSubmitLoading.value = false
      }
    }
  })
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
      return ''
  }
}

// 生命周期
onMounted(() => {
  loadGradeList()
})
</script>

<style scoped>
.grade-audit {
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
</style>
