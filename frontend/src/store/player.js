import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import songApi from '../api/song'
import apiClient from '../api/axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from './user'

const getBaseURL = () => {
  return apiClient.defaults.baseURL || 'http://localhost:8080/api'
}

const STORAGE_KEY = 'fly_music_playlist'

const loadFromStorage = () => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      return JSON.parse(stored)
    }
  } catch (e) {
    console.error('加载播放队列失败:', e)
  }
  return { playlist: [], currentIndex: 0, currentSong: null }
}

const saveToStorage = (data) => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
  } catch (e) {
    console.error('保存播放队列失败:', e)
  }
}

export const usePlayerStore = defineStore('player', () => {
  const storedData = loadFromStorage()
  
  const currentSong = ref(storedData.currentSong || null)
  const playlist = ref(storedData.playlist || [])
  const currentIndex = ref(storedData.currentIndex ?? -1)
  const isPlaying = ref(false)
  const audio = ref(null)
  const currentTime = ref(0)
  const duration = ref(0)
  const isVip = ref(false)
  const hasShownVipTip = ref(false)
  const currentSongFavorite = ref(false)

  watch([playlist, currentIndex, currentSong], () => {
    saveToStorage({
      playlist: playlist.value,
      currentIndex: currentIndex.value,
      currentSong: currentSong.value
    })
  }, { deep: true })

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

  const recordPlayHistory = async (songId) => {
    const userStore = useUserStore()
    const token = localStorage.getItem('token')
    
    if (!userStore.user && token) {
      await userStore.getUserProfile()
    }
    
    console.log('recordPlayHistory called:', { songId, token: !!token, userId: userStore.user?.id })
    if (!token || !songId || !userStore.user?.id) return
    
    const songIdNum = Number(songId)
    if (isNaN(songIdNum)) return
    
    try {
      const response = await fetch(`${getBaseURL()}/play-history`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ userId: userStore.user.id, songId: songIdNum, duration: 0 })
      })
      const data = await response.json()
      console.log('Play history record response:', data)
    } catch (error) {
      console.error('记录播放历史失败:', error)
    }
  }

  const checkFavorite = async (songId) => {
    const userStore = useUserStore()
    const token = localStorage.getItem('token')
    
    if (!userStore.user && token) {
      await userStore.getUserProfile()
    }
    
    if (!token || !userStore.user?.id || !songId) {
      currentSongFavorite.value = false
      return false
    }
    
    try {
      const response = await fetch(`${getBaseURL()}/favorites/check?userId=${userStore.user.id}&targetType=1&targetId=${songId}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      const data = await response.json()
      if (data.code === 200) {
        currentSongFavorite.value = data.data
        return data.data
      }
    } catch (error) {
      console.error('检查收藏状态失败:', error)
    }
    return false
  }

  const toggleFavorite = async (songId) => {
    const userStore = useUserStore()
    const token = localStorage.getItem('token')
    if (!token || !userStore.user?.id || !songId) return
    
    const isFavorited = currentSongFavorite.value
    
    try {
      if (isFavorited) {
        await fetch(`${getBaseURL()}/favorites/remove`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify({ userId: userStore.user.id, targetType: 1, targetId: songId })
        })
        currentSongFavorite.value = false
        ElMessage.success('已取消收藏')
      } else {
        await fetch(`${getBaseURL()}/favorites`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify({ userId: userStore.user.id, targetType: 1, targetId: songId })
        })
        currentSongFavorite.value = true
        ElMessage.success('已添加到收藏')
      }
    } catch (error) {
      console.error('收藏操作失败:', error)
    }
  }

  const getFullUrl = (url) => {
    if (!url) return ''
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }
    // 确保URL以/开头，避免路径拼接问题
    const normalizedUrl = url.startsWith('/') ? url : `/${url}`
    const baseURL = getBaseURL().replace('/api', '')
    return `${baseURL}${normalizedUrl}`
  }

  const playSong = async (song, autoAddToQueue = true) => {
    await checkVipStatus()
    initAudio()

    if (autoAddToQueue) {
      const exists = playlist.value.some(s => s.id === song.id)
      if (!exists) {
        playlist.value.push(song)
        currentIndex.value = playlist.value.length - 1
      } else {
        currentIndex.value = playlist.value.findIndex(s => s.id === song.id)
      }
    }

    currentSong.value = song

    recordPlayHistory(song.id)
    checkFavorite(song.id)

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

  const setPlaylist = (songs, startIndex = 0, autoPlay = true) => {
    const newPlaylist = [...songs]
    const newIndex = startIndex
    
    playlist.value = newPlaylist
    currentIndex.value = newIndex
    
    if (autoPlay && songs.length > 0) {
      playSong(songs[startIndex])
    }
  }

  const addToQueue = (song) => {
    const exists = playlist.value.some(s => s.id === song.id)
    if (!exists) {
      playlist.value.push(song)
    }
  }

  const addToQueueNext = (song) => {
    const exists = playlist.value.some(s => s.id === song.id)
    if (!exists) {
      const insertIndex = currentIndex.value + 1
      playlist.value.splice(insertIndex, 0, song)
    }
  }

  const removeFromQueue = (index) => {
    if (index < 0 || index >= playlist.value.length) return
    
    playlist.value.splice(index, 1)
    
    if (index < currentIndex.value) {
      currentIndex.value--
    } else if (index === currentIndex.value) {
      if (playlist.value.length > 0) {
        if (currentIndex.value >= playlist.value.length) {
          currentIndex.value = playlist.value.length - 1
        }
        if (currentIndex.value >= 0) {
          playSong(playlist.value[currentIndex.value])
        } else {
          stop()
          currentSong.value = null
        }
      } else {
        stop()
        currentSong.value = null
        currentIndex.value = -1
      }
    }
  }

  const clearQueue = () => {
    stop()
    playlist.value = []
    currentIndex.value = -1
    currentSong.value = null
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
    addToQueue,
    addToQueueNext,
    removeFromQueue,
    clearQueue,
    stop,
    seek,
    getFullUrl,
    currentSongFavorite,
    checkFavorite,
    toggleFavorite
  }
})
