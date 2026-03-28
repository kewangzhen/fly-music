import { defineStore } from 'pinia'
import { ref } from 'vue'
import songApi from '../api/song'

export const usePlayerStore = defineStore('player', () => {
  const currentSong = ref(null)
  const playlist = ref([])
  const currentIndex = ref(-1)
  const isPlaying = ref(false)
  const audio = ref(null)

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
    }
  }

  const getFullUrl = (url) => {
    if (!url) return ''
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url
    }
    return `http://localhost:8080${url}`
  }

  const playSong = (song) => {
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
  }

  return {
    currentSong,
    playlist,
    currentIndex,
    isPlaying,
    audio,
    playSong,
    togglePlay,
    playNext,
    playPrev,
    setPlaylist,
    stop,
    getFullUrl
  }
})
