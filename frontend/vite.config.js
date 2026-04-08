import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api/songs/file': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/api/categories': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/api/covers': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/api/artists': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/api/albums': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
