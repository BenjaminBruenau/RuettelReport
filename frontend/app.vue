<template>

  <div>
    <NuxtLayout>
      <div class="w-screen h-screen flex">
          <NuxtPage/>
      </div>
    </NuxtLayout>
  </div>
</template>

<script setup
        lang="ts">

import {onMounted, ref} from "vue";

function resetHeight(){
  document.body.style.height = window.innerHeight + "px";
}
window.addEventListener("resize", resetHeight);
resetHeight();


</script>


<style lang="postcss">

body {
  @apply bg-gradient-to-r from-mainColor_1_1_light to-mainColor_1_2_light
          dark:bg-gradient-to-r dark:from-mainColor_1_1_dark dark:to-mainColor_1_2_dark
          text-text-light dark:text-text-dark;
}


:root {
  --color-primary: #7a7a7a;
  --contrast-text: #7a7a7a;
}


</style>