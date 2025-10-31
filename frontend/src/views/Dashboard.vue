<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon students">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ stats.totalStudents }}</h3>
              <p>在校学生</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon courses">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ stats.totalCourses }}</h3>
              <p>开设课程</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon teachers">
              <el-icon><Avatar /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ stats.totalTeachers }}</h3>
              <p>任课教师</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon selections">
              <el-icon><Select /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ stats.totalSelections }}</h3>
              <p>选课记录</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近公告</span>
            </div>
          </template>
          <div class="notice-list">
            <div class="notice-item" v-for="notice in notices" :key="notice.id">
              <div class="notice-title">{{ notice.title }}</div>
              <div class="notice-time">{{ notice.createTime }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button 
              v-for="action in quickActions" 
              :key="action.name"
              :type="action.type"
              @click="handleQuickAction(action)"
              class="action-btn"
            >
              {{ action.name }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const stats = reactive({
  totalStudents: 0,
  totalCourses: 0,
  totalTeachers: 0,
  totalSelections: 0
})

const notices = ref([
  {
    id: 1,
    title: '2024年春季学期选课通知',
    createTime: '2024-01-15'
  },
  {
    id: 2,
    title: '期末考试安排通知',
    createTime: '2024-01-10'
  },
  {
    id: 3,
    title: '寒假放假通知',
    createTime: '2024-01-05'
  }
])

const quickActions = ref([])

onMounted(() => {
  // 根据用户角色显示不同的快捷操作
  const roleCode = userStore.userInfo.roleCode
  
  switch (roleCode) {
    case 'student':
      quickActions.value = [
        { name: '浏览课程', type: 'primary', path: '/student/courses' },
        { name: '我的选课', type: 'success', path: '/student/selection' },
        { name: '查看成绩', type: 'info', path: '/student/grades' },
        { name: '我的课表', type: 'warning', path: '/student/schedule' }
      ]
      break
    case 'teacher':
      quickActions.value = [
        { name: '我的课程', type: 'primary', path: '/teacher/courses' },
        { name: '成绩录入', type: 'success', path: '/teacher/grades' },
        { name: '学生名单', type: 'info', path: '/teacher/students' }
      ]
      break
    case 'admin':
      quickActions.value = [
        { name: '学生管理', type: 'primary', path: '/admin/students' },
        { name: '开课计划', type: 'success', path: '/admin/schedules' },
        { name: '选课审核', type: 'info', path: '/admin/selections' },
        { name: '排课管理', type: 'warning', path: '/admin/arrangements' }
      ]
      break
    default:
      quickActions.value = []
  }
  
  // 模拟数据加载
  loadStats()
})

const loadStats = () => {
  // 这里应该调用API获取统计数据
  stats.totalStudents = 1250
  stats.totalCourses = 156
  stats.totalTeachers = 89
  stats.totalSelections = 3456
}

const handleQuickAction = (action) => {
  // 这里应该跳转到对应页面
  console.log('快捷操作:', action.name)
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
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

.stat-icon.students {
  background-color: #409eff;
}

.stat-icon.courses {
  background-color: #67c23a;
}

.stat-icon.teachers {
  background-color: #e6a23c;
}

.stat-icon.selections {
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

.card-header {
  font-weight: bold;
  color: #303133;
}

.notice-list {
  max-height: 300px;
  overflow-y: auto;
}

.notice-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
}

.notice-time {
  font-size: 12px;
  color: #909399;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.action-btn {
  flex: 1;
  min-width: 120px;
}
</style>
