<template>
  <div class="schedule-adjustment-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>调课管理</span>
          <el-button type="primary" @click="handleApply">
            <el-icon><Plus /></el-icon>
            申请调课
          </el-button>
        </div>
      </template>
      
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入调课原因"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px;">
            <el-option label="待审批" :value="1" />
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
      
      <!-- 调课记录表格 -->
      <el-table
        v-loading="loading"
        :data="adjustmentList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="oldInfo" label="原排课信息" width="200" />
        <el-table-column prop="newInfo" label="新排课信息" width="200" />
        <el-table-column prop="reason" label="调课原因" width="150" />
        <el-table-column prop="operatorName" label="申请人" width="100" />
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="success"
              size="small"
              @click="handleApprove(row, 2)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleApprove(row, 3)"
            >
              驳回
            </el-button>
            <el-button
              v-if="row.status === 2"
              type="primary"
              size="small"
              @click="handleExecute(row)"
            >
              执行
            </el-button>
            <el-button
              v-if="row.status !== 1"
              type="info"
              size="small"
              disabled
            >
              已处理
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
    
    <!-- 申请调课对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="申请调课"
      width="600px"
      @close="handleApplyDialogClose"
    >
      <el-form
        ref="applyFormRef"
        :model="applyForm"
        :rules="applyFormRules"
        label-width="100px"
      >
        <el-form-item label="排课记录" prop="classScheduleId">
          <el-select v-model="applyForm.classScheduleId" placeholder="请选择排课记录">
            <el-option
              v-for="schedule in classScheduleList"
              :key="schedule.id"
              :label="`${schedule.courseName} - ${schedule.weekDayText} ${schedule.startTime}-${schedule.endTime}`"
              :value="schedule.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="调课原因" prop="reason">
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调课原因"
          />
        </el-form-item>
        
        <el-form-item label="新排课信息" prop="newInfo">
          <el-input
            v-model="applyForm.newInfo"
            type="textarea"
            :rows="3"
            placeholder="请输入新的排课信息（如：星期三 14:00-15:40 A103）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleApplySubmit" :loading="applySubmitLoading">
          申请调课
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getScheduleAdjustmentList, 
  applyScheduleAdjustment, 
  approveScheduleAdjustment, 
  executeScheduleAdjustment 
} from '@/api/scheduleAdjustment'
import { getClassScheduleList } from '@/api/classSchedule'

// 响应式数据
const loading = ref(false)
const applySubmitLoading = ref(false)
const applyDialogVisible = ref(false)
const applyFormRef = ref()

const adjustmentList = ref([])
const classScheduleList = ref([])

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: null
})

// 申请表单数据
const applyForm = reactive({
  classScheduleId: null,
  reason: '',
  newInfo: ''
})

// 申请表单验证规则
const applyFormRules = {
  classScheduleId: [
    { required: true, message: '请选择排课记录', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入调课原因', trigger: 'blur' }
  ],
  newInfo: [
    { required: true, message: '请输入新排课信息', trigger: 'blur' }
  ]
}

// 方法
const loadAdjustmentList = async () => {
  loading.value = true
  try {
    const response = await getScheduleAdjustmentList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    
    adjustmentList.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取调课记录失败')
  } finally {
    loading.value = false
  }
}

const loadClassScheduleList = async () => {
  try {
    const response = await getClassScheduleList({})
    classScheduleList.value = response.data.records || []
  } catch (error) {
    ElMessage.error('获取排课记录失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadAdjustmentList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: null
  })
  handleSearch()
}

const handleApply = () => {
  resetApplyForm()
  applyDialogVisible.value = true
}

const handleApprove = async (row, status) => {
  const action = status === 2 ? '通过' : '驳回'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该调课申请吗？`, '确认审批', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await approveScheduleAdjustment(row.id, status, '')
    ElMessage.success(`${action}成功`)
    loadAdjustmentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleExecute = async (row) => {
  try {
    await ElMessageBox.confirm('确定要执行该调课吗？', '确认执行', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await executeScheduleAdjustment(row.id)
    ElMessage.success('执行成功')
    loadAdjustmentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('执行失败')
    }
  }
}

const handleApplySubmit = async () => {
  if (!applyFormRef.value) return
  
  await applyFormRef.value.validate(async (valid) => {
    if (valid) {
      applySubmitLoading.value = true
      try {
        await applyScheduleAdjustment(applyForm)
        ElMessage.success('申请调课成功')
        applyDialogVisible.value = false
        loadAdjustmentList()
      } catch (error) {
        ElMessage.error(error.message || '申请调课失败')
      } finally {
        applySubmitLoading.value = false
      }
    }
  })
}

const handleApplyDialogClose = () => {
  resetApplyForm()
}

const resetApplyForm = () => {
  Object.assign(applyForm, {
    classScheduleId: null,
    reason: '',
    newInfo: ''
  })
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadAdjustmentList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadAdjustmentList()
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
  loadAdjustmentList()
  loadClassScheduleList()
})
</script>

<style scoped>
.schedule-adjustment-management {
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
