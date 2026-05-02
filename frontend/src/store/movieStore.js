import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '@/api/client'

export const useMovieStore = defineStore('movies', () => {
  const movies = ref([])
  const genres = ref([])
  const streamingServices = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchMovies() {
    loading.value = true
    error.value = null
    try {
      const { data } = await apiClient.get('/movies')
      movies.value = data
    } catch {
      error.value = 'Nie udało się pobrać listy filmów'
    } finally {
      loading.value = false
    }
  }

  async function fetchGenres() {
    const { data } = await apiClient.get('/genres')
    genres.value = data
  }

  async function fetchStreamingServices() {
    const { data } = await apiClient.get('/streaming-services')
    streamingServices.value = data
  }

  async function createMovie(payload) {
    const { data } = await apiClient.post('/movies', payload)
    movies.value.push(data)
    return data
  }

  return {
    movies,
    genres,
    streamingServices,
    loading,
    error,
    fetchMovies,
    fetchGenres,
    fetchStreamingServices,
    createMovie,
  }
})
