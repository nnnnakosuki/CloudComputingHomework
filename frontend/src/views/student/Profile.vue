<template>
  <div class="student-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="profile-avatar">
            <el-avatar :size="120" :src="userInfo.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="avatar-upload">
              <el-button type="text" @click="handleUploadAvatar">
                更换头像
              </el-button>
            </div>
          </div>
        </el-col>
        
        <el-col :span="16">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学号">
              {{ userInfo.studentNo }}
            </el-descriptions-item>
            <el-descriptions-item label="姓名">
              {{ userInfo.name }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ userInfo.gender }}
            </el-descriptions-item>
            <el-descriptions-item label="出生日期">
              {{ userInfo.birthDate }}
            </el-descriptions-item>
            <el-descriptions-item label="院系">
              {{ userInfo.deptName }}
            </el-descriptions-item>
            <el-descriptions-item label="专业">
              {{ userInfo.major }}
            </el-descriptions-item>
            <el-descriptions-item label="班级">
              {{ userInfo.className }}
            </el-descriptions-item>
            <el-descriptions-item label="入学年份">
              {{ userInfo.enrollmentYear }}
            </el-descriptions-item>
            <el-descriptions-item label="学制">
              {{ userInfo.duration }}年
            </el-descriptions-item>
            <el-descriptions-item label="学籍状态">
              <el-tag :type="getStatusType(userInfo.status)">
                {{ userInfo.statusText }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ userInfo.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ userInfo.email }}
            </el-descriptions-item>
            <el-descriptions-item label="家庭住址" :span="2">
              {{ userInfo.address }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentStudentProfile } from '@/api/studentProfile'
import { useUserStore } from '@/store/user'

// 用户store
const userStore = useUserStore()

// 响应式数据
const userInfo = ref({})

// 方法
const loadUserInfo = async () => {
  try {
    const response = await getCurrentStudentProfile()
    userInfo.value = response.data || {}
  } catch (error) {
    ElMessage.error('获取个人信息失败')
  }
}

const handleUploadAvatar = () => {
  ElMessage.info('请联系管理员修改头像')
}

const getStatusType = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'warning'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

// 生命周期
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.student-profile {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-avatar {
  text-align: center;
  padding: 20px;
}

.avatar-upload {
  margin-top: 10px;
}
</style>
