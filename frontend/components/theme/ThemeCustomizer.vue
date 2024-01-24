<template>
  <div class="theme-customizer">
    <label for="primaryColor">Primary Color:</label>

    <PrimeColorPicker v-model="primaryColor" @update:model-value="onColorChange" inputId="cp-rgb" format="rgb" class="mb-3" />

    <div class="mt-4">
      <h1 class="text-primary-1">Hello, Tailwind!</h1>
    </div>
  </div>
</template>


<script setup lang="ts">
const emit = defineEmits(['update-primary-color'])


const pColor = getComputedStyle(document.documentElement).getPropertyValue('--color-primary-1')

const [r, g, b] = pColor.split(' ')
const primaryColor: Ref<{r: string, g: string, b: string}> = ref({ r, g, b })

console.log('VALUE: ', primaryColor)


const onColorChange = (newValue: {r: string, g: string, b: string}) => {
  emit('update-primary-color', newValue);
  console.log(newValue)
  document.documentElement.style.setProperty('--color-primary-1', `${newValue.r} ${newValue.g} ${newValue.b}`);
}
</script>

<style >

/*
const test = '255 255 255'
:root {
  --color-primary: v-bind(test);
  --color-secondary: 111 114 185;
}

 */

</style>