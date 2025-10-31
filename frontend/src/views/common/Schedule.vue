<template>
  <div class="schedule-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课表查看</span>
          <div>
            <el-select v-model="selectedSemester" placeholder="请选择学期" @change="handleSemesterChange">
              <el-option label="2024春季" value="2024春季" />
              <el-option label="2023秋季" value="2023秋季" />
              <el-option label="2023春季" value="2023春季" />
            </el-select>
            <el-button type="primary" @click="loadSchedule" style="margin-left: 10px;">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 课表 -->
      <div class="schedule-table">
        <el-table
          v-loading="loading"
          :data="scheduleData"
          stripe
          border
          style="width: 100%"
        >
          <el-table-column prop="timeSlot" label="时间" width="120" fixed="left" />
          <el-table-column prop="monday" label="星期一" width="200">
            <template #default="{ row }">
              <div v-if="row.monday" class="schedule-cell">
                <div class="course-name">{{ row.monday.courseName }}</div>
                <div class="course-info">{{ row.monday.teacherName }} | {{ row.monday.classroom }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="tuesday" label="星期二" width="200">
            <template #default="{ row }">
              <div v-if="row.tuesday" class="schedule-cell">
                <div class="course-name">{{ row.tuesday.courseName }}</div>
                <div class="course-info">{{ row.tuesday.teacherName }} | {{ row.tuesday.classroom }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="wednesday" label="星期三" width="200">
            <template #default="{ row }">
              <div v-if="row.wednesday" class="schedule-cell">
                <div class="course-name">{{ row.wednesday.courseName }}</div>
                <div class="course-info">{{ row.wednesday.teacherName }} | {{ row.wednesday.classroom }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="thursday" label="星期四" width="200">
            <template #default="{ row }">
              <div v-if="row.thursday" class="schedule-cell">
                <div class="course-name">{{ row.thursday.courseName }}</div>
                <div class="course-info">{{ row.thursday.teacherName }} | {{ row.thursday.classroom }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="friday" label="星期五" width="200">
            <template #default="{ row }">
              <div v-if="row.friday" class="schedule-cell">
                <div class="course-name">{{ row.friday.courseName }}</div>
                <div class="course-info">{{ row.friday.teacherName }} | {{ row.friday.classroom }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="saturday" label="星期六" width="200">
            <template #default="{ row }">
              <div v-if="row.saturday" class="schedule-cell">
                <div class="course-name">{{ row.saturday.courseName }}</div>
                <div class="course-info">{{ row.saturday.teacherName }} | {{ row.saturday.classroom }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="sunday" label="星期日" width="200">
            <template #default="{ row }">
              <div v-if="row.sunday" class="schedule-cell">
                <div class="course-name">{{ row.sunday.courseName }}</div>
                <div class="course-info">{{ row.sunday.teacherName }} | {{ row.sunday.classroom }}</div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWeeklySchedule, getStudentSchedule, getTeacherSchedule } from '@/api/classSchedule'
import { useUserStore } from '@/store/user'

// 用户store
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const selectedSemester = ref('2024春季')

const scheduleData = ref([])

// 时间槽配置
const timeSlots = [
  { time: '08:00-09:40', start: '08:00:00', end: '09:40:00' },
  { time: '10:00-11:40', start: '10:00:00', end: '11:40:00' },
  { time: '14:00-15:40', start: '14:00:00', end: '15:40:00' },
  { time: '16:00-17:40', start: '16:00:00', end: '17:40:00' },
  { time: '19:00-20:40', start: '19:00:00', end: '20:40:00' }
]

// 方法
const loadSchedule = async () => {
  loading.value = true
  try {
    let response
    const userRole = userStore.userRole
    const userId = userStore.userInfo.id
    
    // 根据用户角色调用不同的API
    if (userRole === 'STUDENT') {
      response = await getStudentSchedule(userId, selectedSemester.value)
    } else if (userRole === 'TEACHER') {
      response = await getTeacherSchedule(userId, selectedSemester.value)
    } else {
      // 管理员查看全校课表
      response = await getWeeklySchedule(selectedSemester.value)
    }
    
    const schedules = response.data || []
    buildScheduleData(schedules)
  } catch (error) {
    ElMessage.error('获取课表失败')
  } finally {
    loading.value = false
  }
}

const buildScheduleData = (schedules) => {
  scheduleData.value = timeSlots.map(slot => {
    const row = {
      timeSlot: slot.time,
      monday: null,
      tuesday: null,
      wednesday: null,
      thursday: null,
      friday: null,
      saturday: null,
      sunday: null
    }
    
    // 为每个时间槽查找对应的课程
    schedules.forEach(schedule => {
      if (schedule.startTime === slot.start && schedule.endTime === slot.end) {
        const dayKey = getDayKey(schedule.weekDay)
        row[dayKey] = schedule
      }
    })
    
    return row
  })
}

const getDayKey = (weekDay) => {
  const dayKeys = ['', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday']
  return dayKeys[weekDay] || ''
}

const handleSemesterChange = () => {
  loadSchedule()
}

// 生命周期
onMounted(() => {
  loadSchedule()
})
</script>

<style scoped>
.schedule-view {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-table {
  margin-top: 20px;
}

.schedule-cell {
  padding: 8px;
  background: #f0f9ff;
  border-radius: 4px;
  border: 1px solid #e1f5fe;
}

.course-name {
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 4px;
}

.course-info {
  font-size: 12px;
  color: #666;
}

:deep(.el-table td) {
  padding: 8px 0;
}

:deep(.el-table .cell) {
  padding: 0 8px;
}
</style>
