<template>
  <div class="movie-grid-page">
    <div class="page-header">
      <h1>Filmy</h1>
      <RouterLink to="/movies/new" class="btn-primary">+ Dodaj film</RouterLink>
    </div>

    <div v-if="store.loading" class="state-info">Ładowanie...</div>
    <div v-else-if="store.error" class="state-error">{{ store.error }}</div>
    <div v-else-if="store.movies.length === 0" class="state-info">
      Brak filmów w bazie. <RouterLink to="/movies/new">Dodaj pierwszy film.</RouterLink>
    </div>

    <div v-else class="grid">
      <div v-for="movie in store.movies" :key="movie.id" class="card">
        <div class="card-thumb">
          <img v-if="movie.thumbnailUrl" :src="movie.thumbnailUrl" :alt="movie.title" />
          <div v-else class="thumb-placeholder">
            <span>{{ initials(movie.title) }}</span>
          </div>
        </div>
        <div class="card-info">
          <p class="card-title" :title="movie.title">{{ movie.title }}</p>
          <p class="card-year">{{ movie.productionYear ?? '—' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useMovieStore } from '@/store/movieStore'

const store = useMovieStore()

onMounted(() => store.fetchMovies())

function initials(title) {
  return title
    .split(' ')
    .slice(0, 2)
    .map(w => w[0]?.toUpperCase() ?? '')
    .join('')
}
</script>

<style scoped>
.movie-grid-page {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

h1 {
  font-size: 1.75rem;
}

.btn-primary {
  padding: 0.5rem 1.25rem;
  background: #42b883;
  color: #fff;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.9rem;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #33a06f;
}

.state-info {
  color: #64748b;
}

.state-error {
  color: #e53e3e;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 1.5rem;
}

.card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  transition: transform 0.15s, box-shadow 0.15s;
  cursor: pointer;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.card-thumb {
  width: 100%;
  aspect-ratio: 2 / 3;
  overflow: hidden;
  background: #e2e8f0;
}

.card-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.thumb-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #cbd5e1;
  font-size: 2rem;
  font-weight: 700;
  color: #475569;
}

.card-info {
  padding: 0.6rem 0.75rem;
}

.card-title {
  font-weight: 600;
  font-size: 0.875rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0.2rem;
}

.card-year {
  font-size: 0.8rem;
  color: #64748b;
}
</style>
