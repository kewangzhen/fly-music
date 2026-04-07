import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let router = null

const getRouter = () => {
  if (!router) {
    router = useRouter()
  }
  return router
}

apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

apiClient.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message
    
    if (status === 401 || (message && message.includes('JWT'))) {
      localStorage.removeItem('token')
      ElMessage.warning('登录已过期，请重新登录')
      
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else {
      ElMessage.error(message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default apiClient
