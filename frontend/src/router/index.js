import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/AboutView.vue')
  },
  {
    path: '/movies',
    name: 'movies',
    component: () => import('@/views/MovieGridView.vue')
  },
  {
    path: '/movies/new',
    name: 'movie-new',
    component: () => import('@/views/MovieFormView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
