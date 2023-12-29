<template>
  <header class="contain py-[15px] flex items-center justify-between backdrop-blur-3xl">
    <button @click="toggleDarkMode($colorMode.preference === 'dark' ? 'light' : 'dark')">
      toggle
    </button>
  </header>
  <section
      class="
      bg-white
      dark:bg-mainColor_1_1
      p-10
      rounded-xl
      flex flex-col
      gap-8
      max-w-3xl
      w-full
    "
  >
    <h1 class="text-4xl text-black dark:text-white font-bold text-center">
      Tailwind CSS + PrimeVue
    </h1>
    <PrimePanel header="Default Preset">
      <p>
        First panel component uses the global pass through preset from the
        Tailwind CSS based implementation of PrimeOne Design 2023.
      </p>
    </PrimePanel>

    <PrimePanel
        header="Custom Header"
        :pt="{
        header:
          'p-5 flex items-center justify-between border border-indigo-300 bg-indigo-500 text-indigo-50 rounded-tl-lg rounded-tr-lg dark:bg-indigo-900 dark:border-indigo-900/40 dark:text-white/80',
      }"
    >
      <p>
        Second panel overrides the header section with custom a custom style.
      </p>
    </PrimePanel>

    <PrimePanel
        header="Custom Design"
        :ptOptions="{ mergeSections: false }"
        :pt="{
        header:
          'flex items-center justify-center p-5 bg-gradient-to-r from-rose-400 via-fuchsia-500 to-indigo-500 rounded-tl-2xl rounded-tr-2xl text-white',
        title: 'leading-none font-bold uppercase text-2xl',
        content:
          'bg-gradient-to-r from-rose-400 via-fuchsia-500 to-indigo-500 px-5 pb-8 pt-3 text-white text-center rounded-bl-2xl rounded-br-2xl text-xl',
      }"
    >
      <p>
        Third panel ignores the default preset with
        <b>mergeSections: false</b> and applies a custom style to all elements
        of the panel instead.
      </p>
    </PrimePanel>
  </section>


  <div>
    <p class="p-4 pb-2">
      <select
          v-model="$colorMode.preference"
          class="border w-24 h-8 dark:bg-gray-900 dark:text-white dark:border-gray-700"
      >
        <option value="system">System</option>
        <option value="light">Light</option>
        <option value="dark">Dark</option>
      </select>
    </p>
  </div>
  <div class="p-4">
    <div class="max-w-sm rounded overflow-hidden shadow-lg mb-4 bg-white dark:bg-gray-900">
      <img
          class="w-full"
          src="https://tailwindcss.com/img/card-top.jpg"
          alt="Sunset in the mountains"
      />
      <div class="px-6 py-4 pb-2">
        <div class="font-bold text-xl mb-2 dark:text-white">The Coldest Sunset</div>
        <p class="text-gray-700 dark:text-gray-300">
          Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus
          quia, nulla! Maiores et perferendis eaque, exercitationem praesentium
          nihil.
        </p>
      </div>
      <div class="px-6 py-4">
        <span class="badge mr-2">#photography</span>
        <span class="badge mr-2">#travel</span>
        <span class="badge">#winter</span>
      </div>
    </div>
  </div>
</template>

<script setup
        lang="ts">
const toggleDarkMode = (theme: string) => {
  useColorMode().preference = theme
}
</script>


<style scoped>

</style>