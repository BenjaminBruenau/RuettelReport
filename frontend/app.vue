<template>

  <div>
    <NuxtLayout>
      <div class="w-screen h-screen flex"><!--sticky flex h-full space-x-4-->
      <!--<Sidebar></Sidebar>-->

        <!--<div class="ml-[25%] w-full">-->
          <NuxtPage/>
        <!--</div>-->
      </div>
    </NuxtLayout>
  </div>
</template>

<script setup
        lang="ts">

import {onMounted} from "vue";

function resetHeight(){
  document.body.style.height = window.innerHeight + "px";
}
window.addEventListener("resize", resetHeight);
resetHeight();

onMounted(() => {
  document.documentElement.style.setProperty('--color-primary', '#838383');
});

</script>


<style lang="postcss">

body {
  @apply bg-gradient-to-r from-mainColor_1_1_light to-mainColor_1_2_light
          dark:bg-gradient-to-r dark:from-mainColor_1_1_dark dark:to-mainColor_1_2_dark
          text-text-light dark:text-text-dark;
}



</style>