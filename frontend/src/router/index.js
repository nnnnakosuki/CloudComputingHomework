import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      // 学生路由
      {
        path: 'student',
        name: 'Student',
        meta: { title: '学生功能', roles: ['student'] },
        children: [
          {
            path: 'profile',
            name: 'StudentProfile',
            component: () => import('@/views/student/Profile.vue'),
            meta: { title: '个人信息' }
          },
          {
            path: 'courses',
            name: 'StudentCourses',
            component: () => import('@/views/student/Courses.vue'),
            meta: { title: '课程浏览' }
          },
          {
            path: 'selection',
            name: 'StudentSelection',
            component: () => import('@/views/student/Selection.vue'),
            meta: { title: '我的选课' }
          },
          {
            path: 'grades',
            name: 'StudentGrades',
            component: () => import('@/views/student/Grades.vue'),
            meta: { title: '我的成绩' }
          },
          {
            path: 'schedule',
            name: 'StudentSchedule',
            component: () => import('@/views/common/Schedule.vue'),
            meta: { title: '我的课表' }
          }
        ]
      },
      // 教师路由
      {
        path: 'teacher',
        name: 'Teacher',
        meta: { title: '教师功能', roles: ['teacher'] },
        children: [
          {
            path: 'courses',
            name: 'TeacherCourses',
            component: () => import('@/views/teacher/Courses.vue'),
            meta: { title: '我的课程' }
          },
          {
            path: 'grades',
            name: 'TeacherGrades',
            component: () => import('@/views/teacher/Grades.vue'),
            meta: { title: '成绩录入' }
          },
          {
            path: 'students',
            name: 'TeacherStudents',
            component: () => import('@/views/teacher/Students.vue'),
            meta: { title: '学生名单' }
          },
          {
            path: 'schedule',
            name: 'TeacherSchedule',
            component: () => import('@/views/common/Schedule.vue'),
            meta: { title: '我的课表' }
          }
        ]
      },
      // 教务员路由
      {
        path: 'admin',
        name: 'Admin',
        meta: { title: '教务管理', roles: ['admin'] },
        children: [
          {
            path: 'students',
            name: 'AdminStudents',
            component: () => import('@/views/admin/Students.vue'),
            meta: { title: '学生管理' }
          },
          {
            path: 'grades',
            name: 'AdminGrades',
            component: () => import('@/views/admin/Grades.vue'),
            meta: { title: '成绩审核' }
          },
          {
            path: 'plans',
            name: 'AdminPlans',
            component: () => import('@/views/admin/Plans.vue'),
            meta: { title: '教学计划' }
          },
          {
            path: 'schedules',
            name: 'AdminSchedules',
            component: () => import('@/views/admin/Schedules.vue'),
            meta: { title: '开课计划' }
          },
          {
            path: 'selections',
            name: 'AdminSelections',
            component: () => import('@/views/admin/Selections.vue'),
            meta: { title: '选课审核' }
          },
          {
            path: 'courses',
            name: 'AdminCourses',
            component: () => import('@/views/admin/Courses.vue'),
            meta: { title: '课程管理' }
          },
          {
            path: 'class-schedules',
            name: 'AdminClassSchedules',
            component: () => import('@/views/admin/ClassSchedules.vue'),
            meta: { title: '排课管理' }
          },
          {
            path: 'adjustments',
            name: 'AdminAdjustments',
            component: () => import('@/views/admin/Adjustments.vue'),
            meta: { title: '调课管理' }
          }
        ]
      },
      // 管理员路由
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', roles: ['admin'] },
        children: [
          {
            path: 'users',
            name: 'SystemUsers',
            component: () => import('@/views/system/Users.vue'),
            meta: { title: '用户管理' }
          },
          {
            path: 'roles',
            name: 'SystemRoles',
            component: () => import('@/views/system/Roles.vue'),
            meta: { title: '角色管理' }
          },
          {
            path: 'dict',
            name: 'SystemDict',
            component: () => import('@/views/system/Dict.vue'),
            meta: { title: '数据字典' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth !== false) {
    if (!userStore.token) {
      next('/login')
      return
    }
    
    // 检查角色权限
    if (to.meta.roles && !to.meta.roles.includes(userStore.userInfo.roleCode)) {
      next('/dashboard')
      return
    }
  }
  
  next()
})

export default router
