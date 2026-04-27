<template>
  <div id="app">
    <nav class="navbar">
      <RouterLink to="/">Strona główna</RouterLink>
      <RouterLink to="/about">O aplikacji</RouterLink>
    </nav>

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: Inter, system-ui, Avenir, Helvetica, Arial, sans-serif;
  color: #213547;
  background-color: #f9f9f9;
}

.navbar {
  display: flex;
  gap: 1rem;
  padding: 1rem 2rem;
  background-color: #fff;
  border-bottom: 1px solid #e2e8f0;
}

.navbar a {
  text-decoration: none;
  color: #213547;
  font-weight: 500;
}

.navbar a.router-link-active {
  color: #42b883;
}

.main-content {
  padding: 2rem;
}
</style>
