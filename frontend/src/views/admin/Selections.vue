<template>
  <div class="selection-audit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>选课审核</span>
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
          <el-select v-model="searchForm.status" placeholder="请选择审核状态" clearable>
            <el-option label="待确认" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="已退课" :value="3" />
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
      
      <!-- 批量操作 -->
      <div class="batch-actions">
        <el-button 
          type="success" 
          @click="handleBatchConfirm" 
          :disabled="selectedRows.length === 0"
        >
          <el-icon><Check /></el-icon>
          批量确认 ({{ selectedRows.length }})
        </el-button>
        <el-button 
          type="danger" 
          @click="handleBatchReject" 
          :disabled="selectedRows.length === 0"
        >
          <el-icon><Close /></el-icon>
          批量拒绝 ({{ selectedRows.length }})
        </el-button>
      </div>
      
      <!-- 选课记录表格 -->
      <el-table
        v-loading="loading"
        :data="selectionList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="选课时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="success"
              size="small"
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              拒绝
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseSelectionList, batchConfirmSelections, batchRejectSelections } from '@/api/courseSelection'

// 响应式数据
const loading = ref(false)

const selectionList = ref([])
const selectedRows = ref([])

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
  status: null
})

// 方法
const loadSelectionList = async () => {
  loading.value = true
  try {
    const response = await getCourseSelectionList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    
    selectionList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取选课记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadSelectionList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    studentName: '',
    courseName: '',
    status: null
  })
  handleSearch()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要确认学生"${row.studentName}"的选课申请吗？`, '确认选课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await batchConfirmSelections([row.id])
    ElMessage.success('确认成功')
    loadSelectionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要拒绝学生"${row.studentName}"的选课申请吗？`, '拒绝选课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await batchRejectSelections([row.id])
    ElMessage.success('拒绝成功')
    loadSelectionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('拒绝失败')
    }
  }
}

const handleBatchConfirm = async () => {
  const pendingSelections = selectedRows.value.filter(row => row.status === 1)
  if (pendingSelections.length === 0) {
    ElMessage.warning('没有待确认的选课记录')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要批量确认 ${pendingSelections.length} 个选课申请吗？`, '批量确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const selectionIds = pendingSelections.map(row => row.id)
    await batchConfirmSelections(selectionIds)
    ElMessage.success('批量确认成功')
    loadSelectionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量确认失败')
    }
  }
}

const handleBatchReject = async () => {
  const pendingSelections = selectedRows.value.filter(row => row.status === 1)
  if (pendingSelections.length === 0) {
    ElMessage.warning('没有待确认的选课记录')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要批量拒绝 ${pendingSelections.length} 个选课申请吗？`, '批量拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const selectionIds = pendingSelections.map(row => row.id)
    await batchRejectSelections(selectionIds)
    ElMessage.success('批量拒绝成功')
    loadSelectionList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量拒绝失败')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadSelectionList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadSelectionList()
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
  loadSelectionList()
})
</script>

<style scoped>
.selection-audit {
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

.batch-actions {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
