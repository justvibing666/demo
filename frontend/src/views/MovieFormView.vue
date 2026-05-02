<template>
  <div class="movie-form-page">
    <div class="page-header">
      <RouterLink to="/movies" class="back-link">← Filmy</RouterLink>
      <h1>Dodaj film</h1>
    </div>

    <form @submit.prevent="handleSubmit" class="form" novalidate>

      <div class="form-section">
        <h2>Podstawowe informacje</h2>

        <div class="field">
          <label for="title">Tytuł <span class="required">*</span></label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            placeholder="Np. Skazani na Shawshank"
            :class="{ invalid: errors.title }"
            @blur="validateTitle"
          />
          <span v-if="errors.title" class="error-msg">{{ errors.title }}</span>
        </div>

        <div class="row">
          <div class="field">
            <label for="productionDate">Data produkcji</label>
            <input id="productionDate" v-model="form.productionDate" type="date" />
          </div>
          <div class="field">
            <label for="duration">Czas trwania (min)</label>
            <input id="duration" v-model.number="form.durationMinutes" type="number" min="1" placeholder="Np. 142" />
          </div>
        </div>

        <div class="field">
          <label for="thumbnail">URL miniatury okładki</label>
          <input id="thumbnail" v-model="form.thumbnailUrl" type="url" placeholder="https://..." />
          <div v-if="form.thumbnailUrl" class="thumb-preview">
            <img :src="form.thumbnailUrl" alt="Podgląd" @error="onThumbError" />
          </div>
        </div>

        <div class="field">
          <label for="description">Opis</label>
          <textarea id="description" v-model="form.description" rows="4" placeholder="Krótki opis fabuły..." />
        </div>
      </div>

      <div class="form-section">
        <h2>Gatunki</h2>
        <div v-if="store.genres.length === 0" class="hint">Ładowanie gatunków...</div>
        <div v-else class="checkbox-grid">
          <label v-for="genre in store.genres" :key="genre.id" class="checkbox-item">
            <input type="checkbox" :value="genre.id" v-model="form.genreIds" />
            {{ genre.name }}
          </label>
        </div>
      </div>

      <div class="form-section">
        <h2>Platformy streamingowe</h2>
        <div v-if="store.streamingServices.length === 0" class="hint">Ładowanie platform...</div>
        <div v-else class="checkbox-grid">
          <label v-for="service in store.streamingServices" :key="service.id" class="checkbox-item">
            <input type="checkbox" :value="service.id" v-model="form.streamingServiceIds" />
            {{ service.name }}
          </label>
        </div>
      </div>

      <div class="form-section">
        <h2>Twórcy</h2>

        <div class="field">
          <label for="directors">Reżyserzy</label>
          <input
            id="directors"
            v-model="form.directorsRaw"
            type="text"
            placeholder="Np. Christopher Nolan, Ridley Scott"
          />
          <span class="hint">Oddziel przecinkami</span>
        </div>

        <div class="field">
          <label for="writers">Scenarzyści</label>
          <input
            id="writers"
            v-model="form.writersRaw"
            type="text"
            placeholder="Np. Jonathan Nolan"
          />
          <span class="hint">Oddziel przecinkami</span>
        </div>
      </div>

      <div class="form-section">
        <h2>Tagi</h2>
        <div class="field">
          <label for="tags">Tagi</label>
          <input
            id="tags"
            v-model="form.tagsRaw"
            type="text"
            placeholder="Np. cult-classic, oscar, remake"
          />
          <span class="hint">Oddziel przecinkami</span>
        </div>
      </div>

      <div v-if="submitError" class="submit-error">{{ submitError }}</div>

      <div class="form-actions">
        <RouterLink to="/movies" class="btn-cancel">Anuluj</RouterLink>
        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? 'Zapisywanie...' : 'Dodaj film' }}
        </button>
      </div>

    </form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useMovieStore } from '@/store/movieStore'

const store = useMovieStore()
const router = useRouter()

const form = reactive({
  title: '',
  productionDate: '',
  durationMinutes: null,
  thumbnailUrl: '',
  description: '',
  genreIds: [],
  streamingServiceIds: [],
  directorsRaw: '',
  writersRaw: '',
  tagsRaw: '',
})

const errors = reactive({ title: '' })
const submitting = ref(false)
const submitError = ref('')

onMounted(() => {
  store.fetchGenres()
  store.fetchStreamingServices()
})

function validateTitle() {
  errors.title = form.title.trim() ? '' : 'Tytuł jest wymagany'
}

function splitByComma(raw) {
  return raw
    .split(',')
    .map(s => s.trim())
    .filter(s => s.length > 0)
}

function onThumbError(e) {
  e.target.style.display = 'none'
}

async function handleSubmit() {
  validateTitle()
  if (errors.title) return

  submitting.value = true
  submitError.value = ''

  try {
    await store.createMovie({
      title: form.title.trim(),
      productionDate: form.productionDate || null,
      durationMinutes: form.durationMinutes || null,
      thumbnailUrl: form.thumbnailUrl.trim() || null,
      description: form.description.trim() || null,
      genreIds: form.genreIds,
      streamingServiceIds: form.streamingServiceIds,
      directorNames: splitByComma(form.directorsRaw),
      writerNames: splitByComma(form.writersRaw),
      tags: splitByComma(form.tagsRaw),
    })
    router.push('/movies')
  } catch {
    submitError.value = 'Nie udało się zapisać filmu. Spróbuj ponownie.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.movie-form-page {
  max-width: 720px;
}

.page-header {
  margin-bottom: 2rem;
}

.back-link {
  display: inline-block;
  margin-bottom: 0.5rem;
  color: #64748b;
  text-decoration: none;
  font-size: 0.875rem;
}

.back-link:hover {
  color: #42b883;
}

h1 {
  font-size: 1.75rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-section {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.form-section h2 {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 1.25rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e2e8f0;
  color: #475569;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 1rem;
}

.field:last-child {
  margin-bottom: 0;
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.required {
  color: #e53e3e;
}

input[type='text'],
input[type='url'],
input[type='date'],
input[type='number'],
textarea {
  padding: 0.5rem 0.75rem;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 0.9rem;
  font-family: inherit;
  color: #213547;
  background: #f8fafc;
  transition: border-color 0.15s;
  outline: none;
  width: 100%;
}

input:focus,
textarea:focus {
  border-color: #42b883;
  background: #fff;
}

input.invalid {
  border-color: #e53e3e;
}

textarea {
  resize: vertical;
}

.error-msg {
  font-size: 0.8rem;
  color: #e53e3e;
}

.hint {
  font-size: 0.8rem;
  color: #94a3b8;
}

.thumb-preview {
  margin-top: 0.5rem;
  width: 100px;
  height: 150px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.thumb-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 0.5rem;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.875rem;
  cursor: pointer;
}

.checkbox-item input {
  width: auto;
  accent-color: #42b883;
}

.submit-error {
  color: #e53e3e;
  font-size: 0.875rem;
  padding: 0.75rem;
  background: #fff5f5;
  border: 1px solid #fed7d7;
  border-radius: 6px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 0.5rem;
}

.btn-cancel {
  padding: 0.5rem 1.25rem;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  text-decoration: none;
  color: #374151;
  font-size: 0.9rem;
  font-weight: 500;
  background: #fff;
  transition: background 0.15s;
}

.btn-cancel:hover {
  background: #f1f5f9;
}

.btn-submit {
  padding: 0.5rem 1.5rem;
  background: #42b883;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}

.btn-submit:hover:not(:disabled) {
  background: #33a06f;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
