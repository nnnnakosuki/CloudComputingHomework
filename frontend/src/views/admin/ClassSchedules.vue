<template>
  <div class="class-schedule-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>排课管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加排课
            </el-button>
            <el-button type="success" @click="handleBatchAdd">
              <el-icon><Upload /></el-icon>
              批量排课
            </el-button>
          </div>
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
        <el-form-item label="星期">
          <el-select v-model="searchForm.weekDay" placeholder="请选择星期" clearable>
            <el-option label="星期一" :value="1" />
            <el-option label="星期二" :value="2" />
            <el-option label="星期三" :value="3" />
            <el-option label="星期四" :value="4" />
            <el-option label="星期五" :value="5" />
            <el-option label="星期六" :value="6" />
            <el-option label="星期日" :value="7" />
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
      
      <!-- 排课表格 -->
      <el-table
        v-loading="loading"
        :data="scheduleList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="weekDayText" label="星期" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column prop="weeks" label="周次" width="120" />
        <el-table-column prop="maxStudents" label="容量" width="80" />
        <el-table-column prop="currentStudents" label="已选" width="80" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
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
    
    <!-- 添加/编辑排课对话框 -->
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
        <el-form-item label="开课计划" prop="courseScheduleId">
          <el-select v-model="form.courseScheduleId" placeholder="请选择开课计划" @change="handleCourseChange">
            <el-option
              v-for="schedule in courseScheduleList"
              :key="schedule.id"
              :label="schedule.courseName"
              :value="schedule.id"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="星期" prop="weekDay">
              <el-select v-model="form.weekDay" placeholder="请选择星期">
                <el-option label="星期一" :value="1" />
                <el-option label="星期二" :value="2" />
                <el-option label="星期三" :value="3" />
                <el-option label="星期四" :value="4" />
                <el-option label="星期五" :value="5" />
                <el-option label="星期六" :value="6" />
                <el-option label="星期日" :value="7" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教室" prop="classroom">
              <el-input v-model="form.classroom" placeholder="请输入教室" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="form.startTime"
                format="HH:mm"
                value-format="HH:mm:ss"
                placeholder="选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                v-model="form.endTime"
                format="HH:mm"
                value-format="HH:mm:ss"
                placeholder="选择结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="周次" prop="weeks">
          <el-input v-model="form.weeks" placeholder="如：1-16周" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 批量排课对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量排课"
      width="800px"
    >
      <el-table
        :data="batchScheduleList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseScheduleId" label="开课计划" width="150">
          <template #default="{ row, $index }">
            <el-select v-model="row.courseScheduleId" placeholder="请选择">
              <el-option
                v-for="schedule in courseScheduleList"
                :key="schedule.id"
                :label="schedule.courseName"
                :value="schedule.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="weekDay" label="星期" width="100">
          <template #default="{ row, $index }">
            <el-select v-model="row.weekDay" placeholder="请选择">
              <el-option label="星期一" :value="1" />
              <el-option label="星期二" :value="2" />
              <el-option label="星期三" :value="3" />
              <el-option label="星期四" :value="4" />
              <el-option label="星期五" :value="5" />
              <el-option label="星期六" :value="6" />
              <el-option label="星期日" :value="7" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="120">
          <template #default="{ row, $index }">
            <el-time-picker
              v-model="row.startTime"
              format="HH:mm"
              value-format="HH:mm:ss"
              placeholder="选择时间"
            />
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="120">
          <template #default="{ row, $index }">
            <el-time-picker
              v-model="row.endTime"
              format="HH:mm"
              value-format="HH:mm:ss"
              placeholder="选择时间"
            />
          </template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" width="120">
          <template #default="{ row, $index }">
            <el-input v-model="row.classroom" placeholder="教室" />
          </template>
        </el-table-column>
        <el-table-column prop="weeks" label="周次" width="100">
          <template #default="{ row, $index }">
            <el-input v-model="row.weeks" placeholder="周次" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row, $index }">
            <el-button type="danger" size="small" @click="removeBatchRow($index)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 20px;">
        <el-button type="primary" @click="addBatchRow">
          <el-icon><Plus /></el-icon>
          添加行
        </el-button>
      </div>
      
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit" :loading="batchSubmitLoading">
          批量添加
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getClassScheduleList, 
  addClassSchedule, 
  updateClassSchedule, 
  deleteClassSchedule, 
  batchAddClassSchedules 
} from '@/api/classSchedule'
import { getCourseScheduleList } from '@/api/courseSchedule'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const batchSubmitLoading = ref(false)
const dialogVisible = ref(false)
const batchDialogVisible = ref(false)
const isEdit = ref(false)

const scheduleList = ref([])
const courseScheduleList = ref([])
const batchScheduleList = ref([])

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
  weekDay: null
})

// 表单数据
const form = reactive({
  id: null,
  courseScheduleId: null,
  weekDay: null,
  startTime: null,
  endTime: null,
  classroom: '',
  weeks: ''
})

// 表单验证规则
const formRules = {
  courseScheduleId: [
    { required: true, message: '请选择开课计划', trigger: 'change' }
  ],
  weekDay: [
    { required: true, message: '请选择星期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  classroom: [
    { required: true, message: '请输入教室', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑排课' : '添加排课')

// 方法
const loadScheduleList = async () => {
  loading.value = true
  try {
    const response = await getClassScheduleList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    
    scheduleList.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取排课列表失败')
  } finally {
    loading.value = false
  }
}

const loadCourseScheduleList = async () => {
  try {
    const response = await getCourseScheduleList({})
    courseScheduleList.value = response.data.records || []
  } catch (error) {
    ElMessage.error('获取开课计划列表失败')
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
    weekDay: null
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
    await ElMessageBox.confirm('确定要删除该排课记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteClassSchedule(row.id)
    ElMessage.success('删除成功')
    loadScheduleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchAdd = () => {
  batchScheduleList.value = [createEmptyBatchRow()]
  batchDialogVisible.value = true
}

const addBatchRow = () => {
  batchScheduleList.value.push(createEmptyBatchRow())
}

const removeBatchRow = (index) => {
  batchScheduleList.value.splice(index, 1)
}

const createEmptyBatchRow = () => ({
  courseScheduleId: null,
  weekDay: null,
  startTime: null,
  endTime: null,
  classroom: '',
  weeks: ''
})

const handleCourseChange = (courseScheduleId) => {
  // 可以根据开课计划设置默认值
}

const formRef = ref()

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateClassSchedule(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await addClassSchedule(form)
          ElMessage.success('添加成功')
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

const handleBatchSubmit = async () => {
  batchSubmitLoading.value = true
  try {
    await batchAddClassSchedules(batchScheduleList.value)
    ElMessage.success('批量添加成功')
    batchDialogVisible.value = false
    loadScheduleList()
  } catch (error) {
    ElMessage.error(error.message || '批量添加失败')
  } finally {
    batchSubmitLoading.value = false
  }
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    courseScheduleId: null,
    weekDay: null,
    startTime: null,
    endTime: null,
    classroom: '',
    weeks: ''
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

// 生命周期
onMounted(() => {
  loadScheduleList()
  loadCourseScheduleList()
})
</script>

<style scoped>
.class-schedule-management {
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
