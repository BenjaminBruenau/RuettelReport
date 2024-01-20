<script setup lang="ts">

import {onMounted, ref} from "vue";

const project_settings = ref({
  'theme':{
    'primary_color_light':'#ffffff',
    'primary_color_dark':'#9e9e9e',
    'gradient_from_light':"#d8d8d8",
    'gradient_to_light':"#d8d8d8",
    'gradient_from_dark':"#1f1f1f",
    'gradient_to_dark':"#1f1f1f",
    'default_theme': 'light',
  },
});

const savedTheme = localStorage.getItem('theme')

if (savedTheme) {
  console.log('savedTheme: '+savedTheme)
  useColorMode().preference = savedTheme
}

function setupTheme() {
  const themeSettings = project_settings.value.theme;
  document.documentElement.style.setProperty('--color-primary_light', themeSettings.primary_color_light);
  document.documentElement.style.setProperty('--color-primary_dark', themeSettings.primary_color_dark);
  document.documentElement.style.setProperty('--gradient_to_light', themeSettings.primary_color_dark);
  document.documentElement.style.setProperty('--gradient_from_light', themeSettings.gradient_from_light);
  document.documentElement.style.setProperty('--gradient_to_light', themeSettings.gradient_to_light);
  document.documentElement.style.setProperty('--gradient_from_dark', themeSettings.gradient_from_dark);
  document.documentElement.style.setProperty('--gradient_to_dark', themeSettings.gradient_to_dark);
  document.documentElement.style.setProperty('--b_color_light', adjustColorBrightness(themeSettings.gradient_from_light,1.1));
  document.documentElement.style.setProperty('--b_color_dark', adjustColorBrightness(themeSettings.gradient_from_dark, 0.9));
}

function adjustColorBrightness(hexColor: string, factor: number): string {
  if (!/^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(hexColor)) {
    throw new Error('Ungültiger Hex-Farbwert');
  }

  let r: number = parseInt(hexColor.substring(1, 3), 16);
  let g: number = parseInt(hexColor.substring(3, 5), 16);
  let b: number = parseInt(hexColor.substring(5, 7), 16);

  r = Math.min(255, Math.max(0, r * factor));
  g = Math.min(255, Math.max(0, g * factor));
  b = Math.min(255, Math.max(0, b * factor));

  return `#${Math.round(r).toString(16).padStart(2, '0')}${Math.round(g).toString(16).padStart(2, '0')}${Math.round(b).toString(16).padStart(2, '0')}`;
}

onMounted(() => {
  setupTheme();
});

const darkMode = ref(false)

const toggleDarkMode = (newValue: boolean) => {
  useColorMode().preference = darkMode.value ? 'dark' : 'light'
}

function setFreeAccount(){

}

function setPremiumAccount(){

}

</script>

<template>
  <div>
    <Map style="position:absolute"></Map>
  </div>
  <div style="margin:20px; position:absolute" class="switch">
    <PrimeInputSwitch v-model="darkMode" @update:model-value="toggleDarkMode"></PrimeInputSwitch>
  </div>
  <div class="card-container">
    <PrimeCard class="card-content">
      <template #title><span class="card-title">Free</span></template>
      <template #subtitle>Includes essential functionalities for small projects!</template>
      <template #content>
        <div class="list">
          <ul style="list-style-type: disc;">
            <li>Test</li>
            <li>Test</li>
            <li>Test</li>
            <li>Test</li>
          </ul>
        </div>
      </template>
      <template #footer>
        <PrimeButton label="Choose" class="full-width" />
      </template>
    </PrimeCard>
    <div style="width: 20px"></div>
    <PrimeCard class="card-content">
      <template #title><span class="card-title">Premium</span></template>
      <template #subtitle>Get full access to all features!</template>
      <template #content>
        <div class="list">
          <ul style="list-style-type: disc;">
            <li>Test</li>
            <li>Test</li>
            <li>Test</li>
            <li>Test</li>
          </ul>
        </div>
      </template>
      <template #footer>
        <PrimeButton label="Choose" class="full-width" />
      </template>
    </PrimeCard>
  </div>
</template>

<style scoped>
.card-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  z-index: 10;
}

.card-content {
  min-width: 25em;
  backdrop-filter: blur(100px);
}

.list{
  margin:20px;
  min-height:10em;
}

.switch{
  z-index:20;
}


</style>