<template>
    <PrimeSidebar v-model:visible="visible" :modal="false" :dismissable="false" :showCloseIcon="false" position="left" :pt="{
        root: { class: 'w-80 h-full shadow-2xl rounded-r-xl bg-gradient-to-r from-mainColor_1_1 to-mainColor_1_2' }
    }">
      <div class="flex gap-2 align-items-center">
        <PrimeInputSwitch v-model="darkMode"
                          @update:model-value="toggleDarkMode"
                          :pt="{
            root: ({ props }) => ({
              class: [
                  'inline-block relative',
                  'w-12 h-7',
                  {
                      'opacity-60 select-none pointer-events-none cursor-default': props.disabled
                  }
              ]
            }),
            slider: ({ props }) => ([
                    'absolute cursor-pointer top-0 left-0 right-0 bottom-0 border border-transparent',
                    'transition-colors duration-200 rounded-2xl',
                    'focus:outline-none focus:outline-offset-0 focus:shadow-[0_0_0_0.2rem_rgba(191,219,254,1)] dark:focus:shadow-[0_0_0_0.2rem_rgba(147,197,253,0.5)]',
                    `before:absolute before:content-'' before:top-1/2 before:bg-mainColor_1_1 before:dark:bg-mainColor_1_1 before:w-5 before:h-5 before:left-1 before:-mt-2.5 before:rounded-full before:transition-duration-200`,
                    {
                      'bg-textColor_dark dark:bg-textColor_dark hover:bg-textColor_dark hover:dark:bg-textColor_light ': !props.modelValue,
                      'bg-mainColor_1_2 before:transform before:translate-x-5': props.modelValue
                    }])}"></PrimeInputSwitch>
        <i class="pi pi-moon" :style="`font-size: 1.5rem; color: ${moonColor}`"></i>
      </div>
        <h2>Sidebar</h2>
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
    </PrimeSidebar>
    <PrimeButton icon="pi pi-arrow-right" @click="visible = true">test</PrimeButton>
</template>

<script setup lang="ts">

const visible = ref(true)
const moonColor = ref('slateblue')

const darkMode = ref(false)
const savedTheme = localStorage.getItem('theme')
console.log('DARK MODE: ', darkMode.value)
if (savedTheme) {
  console.log(savedTheme)
  useColorMode().preference = savedTheme
  moonColor.value = savedTheme === 'dark' ? 'goldenrod' : 'slateblue'
}

const changeTheme = (theme: string) => {
  console.log('THEME CHANGE: ', theme)
  if (theme === 'dark') {
    moonColor.value = 'goldenrod'
  } else {
    moonColor.value = 'slateblue'
  }

  useColorMode().preference = theme
  //localStorage.setItem('theme', theme)
}

const toggleDarkMode = (newValue: boolean) => {
  if (darkMode.value) {
    moonColor.value = 'goldenrod'
  } else {
    moonColor.value = 'slateblue'
  }
  useColorMode().preference = darkMode.value ? 'dark' : 'light'
}

</script>

<style scoped>

</style>