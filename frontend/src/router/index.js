import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue')
  },
  {
    path: '/songs',
    name: 'Songs',
    component: () => import('../views/Songs.vue')
  },
  {
    path: '/playlists',
    name: 'Playlists',
    component: () => import('../views/Playlists.vue')
  },
  {
    path: '/playlists/:id',
    name: 'PlaylistDetail',
    component: () => import('../views/PlaylistDetail.vue')
  },
  {
    path: '/recommendations',
    name: 'Recommendations',
    component: () => import('../views/Recommendations.vue')
  },
  {
    path: '/radar-playlist',
    name: 'RadarPlaylist',
    component: () => import('../views/RadarPlaylist.vue')
  },
  {
    path: '/ranking',
    name: 'Ranking',
    component: () => import('../views/Ranking.vue')
  },
  {
    path: '/category',
    name: 'Category',
    component: () => import('../views/Category.vue')
  },
  {
    path: '/report',
    name: 'Report',
    component: () => import('../views/Report.vue')
  },
  {
    path: '/ai-lab',
    name: 'AiLab',
    component: () => import('../views/AiLab.vue')
  },
  {
    path: '/social',
    name: 'Social',
    component: () => import('../views/Social.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue')
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../views/AdminUsers.vue')
  },
  {
    path: '/admin/songs',
    name: 'AdminSongs',
    component: () => import('../views/AdminSongs.vue')
  },
  {
    path: '/admin/config',
    name: 'AdminConfig',
    component: () => import('../views/AdminConfig.vue')
  },
  {
    path: '/admin/recommendation',
    name: 'AdminRecommendation',
    component: () => import('../views/AdminRecommendation.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
