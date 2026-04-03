<template>
  <div id="app">
    <Navbar v-if="!isAdminPage" />
    <div :class="{ 'with-player': playerStore.currentSong }">
      <router-view />
    </div>
    <PlayerBar v-if="!isAdminPage" />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import PlayerBar from './components/PlayerBar.vue'
import { usePlayerStore } from './store/player'
import { useUserStore } from './store/user'

const route = useRoute()
const playerStore = usePlayerStore()
const userStore = useUserStore()

const isAdminPage = computed(() => {
  return route.path.startsWith('/admin')
})

onMounted(() => {
  userStore.init()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  min-height: 100vh;
}

.with-player {
  padding-bottom: 70px;
}
</style>
