import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/client'

// Przykładowy store – Pinia (Composition API style)
export const useExampleStore = defineStore('example', () => {
  const items = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchAll() {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.get('/examples')
      items.value = data
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  async function create(payload) {
    const { data } = await api.post('/examples', payload)
    items.value.push(data)
    return data
  }

  async function remove(id) {
    await api.delete(`/examples/${id}`)
    items.value = items.value.filter(i => i.id !== id)
  }

  return { items, loading, error, fetchAll, create, remove }
})
