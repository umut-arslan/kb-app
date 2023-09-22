<script setup lang='ts'>
import UserLogin from '@/components/UserLogin.vue'
import {subscribe} from "@/service/Notification/NotificationService";

subscribe()
</script>

<template>
  <header>
    <nav class='nav navbar-dark' style='padding: 1rem; background-color: var(--kb-red-dark);'>
      <a class='navbar-brand' href='/'>
        <img src='@/assets/logo.svg' width='50' height='50' alt=''>
      </a>
      <a class='navbar-nav nav-item nav-link' style='margin-left: 1rem'>
        <RouterLink class='nav-link' active-class="active" to='/'>Home</RouterLink>
      </a>
      <a class='navbar-nav nav-item nav-link'>
        <RouterLink class='nav-link' active-class="active" to='/InfoPage'>Info</RouterLink>
      </a>
      <UserLogin/>
    </nav>
  </header>
  <body>
    <RouterView />
  </body>
</template>

<style scoped>

header {
  position:fixed;
  top:0;
  width:100vw;
  z-index:200;
}
</style>
