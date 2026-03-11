import { defineStore } from 'pinia'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: false,
    user: null,
    token: null
  }),
  
  actions: {
    async login(username, password) {
      try {
        const response = await axios.post(`${API_BASE_URL}/users/login`, {
          username,
          password
        })
        
        this.token = response.data
        this.isLoggedIn = true
        localStorage.setItem('token', this.token)
        
        // 获取用户信息
        await this.getUserProfile()
        
        return true
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },
    
    async register(userData) {
      try {
        await axios.post(`${API_BASE_URL}/users/register`, userData)
        return true
      } catch (error) {
        console.error('Registration failed:', error)
        return false
      }
    },
    
    async getUserProfile() {
      try {
        const token = localStorage.getItem('token')
        if (!token) return
        
        const response = await axios.get(`${API_BASE_URL}/users/profile`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        
        this.user = response.data
      } catch (error) {
        console.error('Failed to get user profile:', error)
      }
    },
    
    logout() {
      this.isLoggedIn = false
      this.user = null
      this.token = null
      localStorage.removeItem('token')
    },
    
    init() {
      const token = localStorage.getItem('token')
      if (token) {
        this.token = token
        this.isLoggedIn = true
        this.getUserProfile()
      }
    }
  }
})
