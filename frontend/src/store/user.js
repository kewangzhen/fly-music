import { defineStore } from 'pinia'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: false,
    user: null,
    token: null
  }),
  
  getters: {
    isAuthenticated: (state) => state.isLoggedIn && state.token
  },
  
  actions: {
    async login(username, password) {
      try {
        const response = await axios.post(`${API_BASE_URL}/users/login`, {
          username,
          password
        })
        
        const data = response.data.data
        this.token = data.token
        this.isLoggedIn = true
        localStorage.setItem('token', this.token)
        
        this.user = data.user
        
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
        
        const response = await axios.get(`${API_BASE_URL}/users/current`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        
        this.user = response.data.data
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
    
    async resetPassword(email) {
      try {
        await axios.post(`${API_BASE_URL}/users/reset-password`, { email })
        return true
      } catch (error) {
        console.error('Reset password failed:', error)
        return false
      }
    },
    
    async updatePasswordByToken(token, newPassword) {
      try {
        await axios.post(`${API_BASE_URL}/users/update-password`, { token, newPassword })
        return true
      } catch (error) {
        console.error('Update password failed:', error)
        return false
      }
    },
    
    async verifyToken(token) {
      try {
        await axios.get(`${API_BASE_URL}/users/verify-token`, { params: { token } })
        return true
      } catch (error) {
        console.error('Verify token failed:', error)
        return false
      }
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
