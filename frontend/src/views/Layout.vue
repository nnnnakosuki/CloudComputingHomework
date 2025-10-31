<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo">
          <h3 v-if="!isCollapse">学生成绩查询系统</h3>
          <h3 v-else>成绩系统</h3>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="true"
          router
          class="sidebar-menu"
        >
          <!-- 首页 -->
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
          <!-- 学生菜单 -->
          <template v-if="userStore.userInfo.roleCode === 'student'">
            <el-menu-item index="/student/profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="/student/courses">
              <el-icon><Select /></el-icon>
              <span>课程选课</span>
            </el-menu-item>
            <el-menu-item index="/student/grades">
              <el-icon><Trophy /></el-icon>
              <span>我的成绩</span>
            </el-menu-item>
            <el-menu-item index="/student/schedule">
              <el-icon><Calendar /></el-icon>
              <span>我的课表</span>
            </el-menu-item>
          </template>
          
          <!-- 教师菜单 -->
          <template v-if="userStore.userInfo.roleCode === 'teacher'">
            <el-menu-item index="/teacher/courses">
              <el-icon><Reading /></el-icon>
              <span>我的课程</span>
            </el-menu-item>
            <el-menu-item index="/teacher/grades">
              <el-icon><Edit /></el-icon>
              <span>成绩录入</span>
            </el-menu-item>
            <el-menu-item index="/teacher/students">
              <el-icon><UserFilled /></el-icon>
              <span>学生名单</span>
            </el-menu-item>
            <el-menu-item index="/teacher/schedule">
              <el-icon><Calendar /></el-icon>
              <span>我的课表</span>
            </el-menu-item>
          </template>
          
          <!-- 教务员菜单 -->
          <template v-if="userStore.userInfo.roleCode === 'admin'">
            <el-sub-menu index="student-management">
              <template #title>
                <el-icon><UserFilled /></el-icon>
                <span>学生管理</span>
              </template>
              <el-menu-item index="/admin/students">学生信息</el-menu-item>
              <el-menu-item index="/admin/grades">成绩审核</el-menu-item>
            </el-sub-menu>
            
            <el-sub-menu index="course-management">
              <template #title>
                <el-icon><Reading /></el-icon>
                <span>课程管理</span>
              </template>
              <el-menu-item index="/admin/plans">教学计划</el-menu-item>
              <el-menu-item index="/admin/courses">课程管理</el-menu-item>
              <el-menu-item index="/admin/schedules">开课计划</el-menu-item>
              <el-menu-item index="/admin/selections">选课审核</el-menu-item>
            </el-sub-menu>
            
            <el-sub-menu index="schedule-management">
              <template #title>
                <el-icon><Calendar /></el-icon>
                <span>教学调度</span>
              </template>
              <el-menu-item index="/admin/class-schedules">排课管理</el-menu-item>
              <el-menu-item index="/admin/adjustments">调课管理</el-menu-item>
            </el-sub-menu>
          </template>
          
          <!-- 系统管理员菜单 -->
          <template v-if="userStore.userInfo.roleCode === 'admin'">
            <el-sub-menu index="system-management">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/system/users">用户管理</el-menu-item>
              <el-menu-item index="/system/roles">角色管理</el-menu-item>
              <el-menu-item index="/system/dict">数据字典</el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部栏 -->
        <el-header class="header">
          <div class="header-left">
            <el-button
              type="text"
              @click="toggleCollapse"
              class="collapse-btn"
            >
              <el-icon><Expand v-if="isCollapse" /><Fold v-else /></el-icon>
            </el-button>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-icon><Avatar /></el-icon>
                {{ userStore.userInfo.realName || userStore.userInfo.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => {
  return route.path
})

const currentPageTitle = computed(() => {
  return route.meta.title || '首页'
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中...')
      break
    case 'changePassword':
      ElMessage.info('修改密码功能开发中...')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        userStore.logout()
        ElMessage.success('退出成功')
        router.push('/login')
      } catch {
        // 用户取消
      }
      break
  }
}

onMounted(async () => {
  // 获取用户信息
  try {
    await userStore.getUserInfoAction()
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    router.push('/login')
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
  color: white;
}

.logo h3 {
  margin: 0;
  font-size: 18px;
}

.sidebar-menu {
  border: none;
  background-color: #304156;
}

.sidebar-menu .el-menu-item {
  color: #bfcbd9;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #263445;
  color: white;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #409eff;
  color: white;
}

.header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  margin-right: 20px;
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

.user-info:hover {
  color: #409eff;
}

.main-content {
  background-color: #f5f5f5;
  padding: 20px;
}
</style>
