<template>

  <div>
    <NuxtLayout>
      <div class="sticky mr-12 flex h-full space-x-4">
      <Sidebar></Sidebar>

        <div class="ml-[25%] w-full">
          <NuxtPage/>
        </div>
      </div>
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