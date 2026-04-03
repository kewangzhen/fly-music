import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import pinia from './store'
import { useUserStore } from './store/user'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.use(pinia)

const userStore = useUserStore()
userStore.init()

app.mount('#app')
