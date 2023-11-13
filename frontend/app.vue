<template>
  <div>
    <Sidebar></Sidebar>
    <NuxtLayout>
      <NuxtPage/>
    </NuxtLayout>
  </div>
</template>
<script setup
        lang="ts">
</script>

<style lang="postcss">
body {
  @apply bg-gradient-to-r from-mainColor_2_1 to-mainColor_2_2 dark:text-textColor_dark text-textColor_light;
}
</style>