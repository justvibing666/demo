<template>
  <div class="home">
    <h1>Strona główna</h1>
    <p class="test-banner">TEST</p>

    <div v-if="store.loading">Ładowanie...</div>
    <div v-else-if="store.error" class="error">{{ store.error }}</div>

    <ul v-else>
      <li v-for="item in store.items" :key="item.id">
        {{ item.name }}
        <button @click="store.remove(item.id)">Usuń</button>
      </li>
    </ul>

    <button @click="store.fetchAll">Odśwież</button>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useExampleStore } from '@/store'

const store = useExampleStore()
onMounted(() => store.fetchAll())
</script>

<style scoped>
.home { max-width: 800px; }
.test-banner { color: red; font-weight: bold; font-size: 1.5rem; }
h1 { margin-bottom: 1rem; }
ul { list-style: none; margin: 1rem 0; }
li { display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid #e2e8f0; }
.error { color: red; }
button { cursor: pointer; }
</style>
