<template>
  <div class="vip-page">
    <div class="vip-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
    </div>
    
    <div class="vip-container">
      <div class="vip-header">
        <div class="brand-logo">🎵</div>
        <h1 class="brand-name">Fly Music</h1>
        <h2 class="page-title">开通VIP会员</h2>
        <p class="page-desc">畅享全站音乐，体验极致音乐之旅</p>
      </div>
      
      <div class="vip-benefits">
        <div class="benefit-card">
          <div class="benefit-icon">🎵</div>
          <h3>海量音乐</h3>
          <p>畅听全站歌曲，无限制播放</p>
        </div>
        <div class="benefit-card">
          <div class="benefit-icon">🎧</div>
          <h3>无损音质</h3>
          <p>高品质音乐体验</p>
        </div>
        <div class="benefit-card">
          <div class="benefit-icon">⬇️</div>
          <h3>下载特权</h3>
          <p>支持歌曲离线下载</p>
        </div>
        <div class="benefit-card">
          <div class="benefit-icon">🎁</div>
          <h3>专属福利</h3>
          <p>VIP专属活动与礼品</p>
        </div>
      </div>
      
      <div class="vip-plans">
        <h3 class="plans-title">选择会员套餐</h3>
        <div class="plans-grid">
          <div 
            class="plan-card" 
            :class="{ selected: selectedPlan === 30 }"
            @click="selectedPlan = 30"
          >
            <div class="plan-name">月度会员</div>
            <div class="plan-price">¥30<span>/月</span></div>
            <div class="plan-desc">30天VIP特权</div>
            <div class="plan-check" v-if="selectedPlan === 30">✓</div>
          </div>
          
          <div 
            class="plan-card" 
            :class="{ selected: selectedPlan === 90 }"
            @click="selectedPlan = 90"
          >
            <div class="plan-tag">超值</div>
            <div class="plan-name">季度会员</div>
            <div class="plan-price">¥80<span>/季</span></div>
            <div class="plan-desc">立省¥10</div>
            <div class="plan-check" v-if="selectedPlan === 90">✓</div>
          </div>
          
          <div 
            class="plan-card" 
            :class="{ selected: selectedPlan === 365 }"
            @click="selectedPlan = 365"
          >
            <div class="plan-tag">最划算</div>
            <div class="plan-name">年度会员</div>
            <div class="plan-price">¥298<span>/年</span></div>
            <div class="plan-desc">立省¥62</div>
            <div class="plan-check" v-if="selectedPlan === 365">✓</div>
          </div>
        </div>
        
        <div class="subscribe-action">
          <el-button 
            type="warning" 
            size="large" 
            class="subscribe-btn"
            :loading="loading"
            :disabled="!selectedPlan"
            @click="handleSubscribe"
          >
            {{ selectedPlan ? `立即开通 ¥${getPrice()}` : '请选择套餐' }}
          </el-button>
        </div>
        
        <p class="subscribe-note">
          开通即表示您同意我们的服务条款<br>
          会员到期后自动续费，可随时取消
        </p>
      </div>
      
      <p class="back-link">
        <el-link type="primary" :underline="false" @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const selectedPlan = ref(null)

const getPrice = () => {
  const prices = { 30: 30, 90: 80, 365: 298 }
  return prices[selectedPlan.value] || 0
}

const handleSubscribe = async () => {
  if (!selectedPlan.value) return
  
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const response = await axios.post(
      'http://localhost:8080/api/users/subscribe-vip',
      { days: selectedPlan.value },
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    )
    
    if (response.data.code === 200) {
      ElMessage.success('VIP开通成功！')
      await userStore.getUserProfile()
      router.push('/profile')
    } else {
      ElMessage.error(response.data.message || '开通失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '开通失败，请重试')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.vip-page {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
}

.vip-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: #e6a23c;
  top: -150px;
  right: -100px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #f56c6c;
  bottom: -100px;
  left: -100px;
}

.vip-container {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

.vip-header {
  text-align: center;
  margin-bottom: 40px;
}

.brand-logo {
  font-size: 48px;
  margin-bottom: 10px;
}

.brand-name {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  margin: 0 0 10px;
}

.page-title {
  font-size: 36px;
  color: #e6a23c;
  margin: 0 0 10px;
}

.page-desc {
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
  margin: 0;
}

.vip-benefits {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.benefit-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px 16px;
  text-align: center;
  backdrop-filter: blur(10px);
  transition: transform 0.3s;
}

.benefit-card:hover {
  transform: translateY(-5px);
}

.benefit-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.benefit-card h3 {
  color: #fff;
  font-size: 16px;
  margin: 0 0 8px;
}

.benefit-card p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  margin: 0;
}

.vip-plans {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
}

.plans-title {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin: 0 0 30px;
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.plan-card {
  border: 2px solid #eee;
  border-radius: 16px;
  padding: 30px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.plan-card:hover {
  border-color: #e6a23c;
}

.plan-card.selected {
  border-color: #e6a23c;
  background: #fff7e6;
}

.plan-tag {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: #e6a23c;
  color: #fff;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 12px;
}

.plan-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.plan-price {
  font-size: 32px;
  font-weight: bold;
  color: #e6a23c;
  margin-bottom: 8px;
}

.plan-price span {
  font-size: 14px;
  font-weight: normal;
  color: #999;
}

.plan-desc {
  color: #999;
  font-size: 14px;
}

.plan-check {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  background: #e6a23c;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.subscribe-action {
  text-align: center;
  margin-bottom: 20px;
}

.subscribe-btn {
  width: 300px;
  height: 50px;
  font-size: 18px;
}

.subscribe-note {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin: 0;
  line-height: 1.8;
}

.back-link {
  text-align: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .vip-benefits {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .plans-grid {
    grid-template-columns: 1fr;
  }
}
</style>
