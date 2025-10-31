import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})
  
  // 登录
  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      token.value = response.data.token
      localStorage.setItem('token', token.value)
      
      // 获取用户信息
      await getUserInfoAction()
      
      return response
    } catch (error) {
      throw error
    }
  }
  
  // 获取用户信息
  const getUserInfoAction = async () => {
    try {
      const response = await getUserInfo()
      userInfo.value = response.data
      return response
    } catch (error) {
      throw error
    }
  }
  
  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
  }
  
  // 角色权限检查
  const hasRole = (role) => {
    return userInfo.value.roleCode === role
  }
  
  const hasAnyRole = (roles) => {
    return roles.includes(userInfo.value.roleCode)
  }
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value.roleCode || '')
  const userName = computed(() => userInfo.value.realName || userInfo.value.username || '')
  
  return {
    token,
    userInfo,
    isLoggedIn,
    userRole,
    userName,
    loginAction,
    getUserInfoAction,
    logout,
    hasRole,
    hasAnyRole
  }
})
