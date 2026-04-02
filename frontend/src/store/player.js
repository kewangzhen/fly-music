import { defineStore } from 'pinia'
import { ref } from 'vue'
import songApi from '../api/song'
import apiClient from '../api/axios'
import { ElMessage } from 'element-plus'

const getBaseURL = () => {
  return apiClient.defaults.baseURL || 'http://localhost:8080/api'
}

export const usePlayerStore = defineStore('player', () => {
  const currentSong = ref(null)
  const playlist = ref([])
  const currentIndex = ref(-1)
  const isPlaying = ref(false)
  const audio = ref(null)
  const currentTime = ref(0)
  const duration = ref(0)
  const isVip = ref(false)
  const hasShownVipTip = ref(false)

  const checkVipStatus = async () => {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        isVip.value = false
        return false
      }
      const response = await fetch(`${getBaseURL()}/users/current/vip`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      const data = await response.json()
      if (data.code === 200) {
        isVip.value = data.data.isVip
        return data.data.isVip
      }
    } catch (error) {
      console.error('检查VIP状态失败:', error)
    }
    isVip.value = false
    return false
  }

  const initAudio = () => {
    if (!audio.value) {
      audio.value = new Audio()
      audio.value.addEventListener('ended', () => {
        playNext()
      })
      audio.value.addEventListener('error', (e) => {
        console.error('播放错误:', e)
        isPlaying.value = false
      })
      audio.value.addEventListener('timeupdate', () => {
        currentTime.value = audio.value.currentTime
        
        if (!isVip.value && currentTime.value >= 10) {
          audio.value.pause()
          isPlaying.value = false
          if (!hasShownVipTip.value) {
            hasShownVipTip.value = true
            ElMessage.warning('试听结束，开通VIP畅听完整歌曲')
            setTimeout(() => {
              hasShownVipTip.value = false
            }, 3000)
          }
        }
      })
      audio.value.addEventListener('loadedmetadata', () => {
        duration.value = audio.value.duration
      })
      audio.value.addEventListener('ended', () => {
        isPlaying.value = false
        currentTime.value = 0
      })
    }
  }

  const getFullUrl = (url) => {
    if (!url) return ''
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }
    const baseURL = getBaseURL().replace('/api', '')
    return `${baseURL}${url}`
  }

  const playSong = async (song) => {
    await checkVipStatus()
    initAudio()

    currentSong.value = song

    const url = getFullUrl(song.url)
    if (url) {
      audio.value.src = url
      audio.value.play()
        .then(() => {
          isPlaying.value = true
        })
        .catch(err => {
          console.error('播放失败:', err)
          isPlaying.value = false
        })
    }
  }

  const togglePlay = () => {
    if (!audio.value || !currentSong.value) return

    if (isPlaying.value) {
      audio.value.pause()
      isPlaying.value = false
    } else {
      audio.value.play()
        .then(() => {
          isPlaying.value = true
        })
        .catch(err => {
          console.error('播放失败:', err)
        })
    }
  }

  const playNext = () => {
    if (playlist.value.length === 0 || currentIndex.value >= playlist.value.length - 1) {
      isPlaying.value = false
      return
    }
    currentIndex.value++
    playSong(playlist.value[currentIndex.value])
  }

  const playPrev = () => {
    if (playlist.value.length === 0 || currentIndex.value <= 0) {
      return
    }
    currentIndex.value--
    playSong(playlist.value[currentIndex.value])
  }

  const setPlaylist = (songs, startIndex = 0) => {
    playlist.value = [...songs]
    currentIndex.value = startIndex
    if (songs.length > 0) {
      playSong(songs[startIndex])
    }
  }

  const stop = () => {
    if (audio.value) {
      audio.value.pause()
      audio.value.currentTime = 0
    }
    isPlaying.value = false
    currentTime.value = 0
  }

  const seek = (time) => {
    if (audio.value) {
      audio.value.currentTime = time
      currentTime.value = time
    }
  }

  return {
    currentSong,
    playlist,
    currentIndex,
    isPlaying,
    audio,
    currentTime,
    duration,
    isVip,
    checkVipStatus,
    playSong,
    togglePlay,
    playNext,
    playPrev,
    setPlaylist,
    stop,
    seek,
    getFullUrl
  }
})
