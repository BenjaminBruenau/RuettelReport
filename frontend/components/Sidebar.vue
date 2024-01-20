<template>

    <div class="h-screen w-1/3 shadow-2xl rounded-r-xl bg-gradient-to-r from-primary-1 to-primary-2  p-4" :class="{
      'hidden': hideSidebar
    }">
      <div class="flex justify-end">
        <PrimeButton icon="pi pi-arrow-right" @click="hideSidebar = true" class="mb-2"></PrimeButton>
      </div>
      <div class="flex gap-2 align-items-center">
        <PrimeInputSwitch v-model="darkMode"
                          @update:model-value="toggleDarkMode"
                          ></PrimeInputSwitch>
        <i class="pi" :class="darkMode ? 'pi-moon text-[#6A5ACD]' : 'pi-sun text-[#DAA520]'" style="font-size: 1.5rem"></i>
      </div>

      <ApiFilterContainer/>

    </div>
</template>

<script setup lang="ts">


const visible = ref(true)

const hideSidebar = ref(false)


const darkMode = ref(false)
const savedTheme = localStorage.getItem('theme')
console.log('DARK MODE: ', darkMode.value)
if (savedTheme) {
  console.log(savedTheme)
  useColorMode().preference = savedTheme
}

const changeTheme = (theme: string) => {
  console.log('THEME CHANGE: ', theme)
  useColorMode().preference = theme
  //localStorage.setItem('theme', theme)
}

const toggleDarkMode = (newValue: boolean) => {
  useColorMode().preference = darkMode.value ? 'dark' : 'light'
}

</script>

<style scoped>

</style>