<script setup lang="ts">
import ConfigCrud from '@/components/ConfigCrud.vue'
</script>

<template>
  <main>
    <ConfigCrud class='configFlex'/>
  </main>
</template>

<style scoped>

.configFlex {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 3rem
}
</style>
