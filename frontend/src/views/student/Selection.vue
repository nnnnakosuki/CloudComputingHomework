<template>
  <div class="selection-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的选课</span>
          <el-button type="primary" @click="loadSelections">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="selections" v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="teacherName" label="授课教师" />
        <el-table-column prop="scheduleTime" label="上课时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'PENDING'" 
              type="danger" 
              size="small" 
              @click="cancelSelection(row.id)"
            >
              取消选课
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMySelections, cancelCourseSelection } from '@/api/courseSelection'
import { useUserStore } from '@/store/user'

const selections = ref([])
const loading = ref(false)

// 用户store
const userStore = useUserStore()

const loadSelections = async () => {
  loading.value = true
  try {
    const response = await getMySelections()
    selections.value = response.data || []
  } catch (error) {
    ElMessage.error('加载选课信息失败')
  } finally {
    loading.value = false
  }
}

const cancelSelection = async (selectionId) => {
  try {
    await ElMessageBox.confirm('确定要取消选课吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelCourseSelection(selectionId)
    ElMessage.success('取消选课成功')
    loadSelections()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消选课失败')
    }
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  loadSelections()
})
</script>

<style scoped>
.selection-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
