<script setup lang="ts">

import {onMounted, ref} from "vue";

const props = defineProps({
  project_settings: {
    type: Object,
    default: () => ({data: {}})
  },
});





const myColor = ref('black');
watch(myColor, (color) => {
  const contrastColor = getContrastYIQ(color) === 'light' ? '#FFFFFF' : '#000000';
  document.documentElement.style.setProperty('--contrast-text', contrastColor);
});

const panelClass = (props, parent, index) => {
  return [
    {
      'border-primary bg-tile_color_light dark:bg-tile_color_dark text-primary': parent.state.d_activeIndex === index,
      'border-b_color_light dark:border-b_color_dark bg-tile_color_light dark:bg-tile_color_dark text-textColor_light dark:text-textColor_dark': parent.state.d_activeIndex !== index,
    }
  ];
};

function getContrastYIQ(hexcolor){
  hexcolor = hexcolor.replace('#', '');

  var r = parseInt(hexcolor.substr(0,2),16);
  var g = parseInt(hexcolor.substr(2,2),16);
  var b = parseInt(hexcolor.substr(4,2),16);

  var yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;

  console.log(yiq);

  return (yiq >= 90) ? 'light' : 'black';
}


const darkMode = ref(false)

const toggleDarkMode = (newValue: boolean) => {
  useColorMode().preference = darkMode.value ? 'dark' : 'light'
}


const color_primary_light = ref();
const color_primary_dark = ref();
const gradient_from_light = ref();
const gradient_to_light = ref();
const gradient_from_dark = ref();
const gradient_to_dark = ref();

function unifyHex(text){
  return '#'+text.replace("#", "")
}

onMounted(() => {
  console.log(JSON.stringify(props.project_settings,null,2));

  color_primary_light.value = props.project_settings.theme?.primary_color_light;
  color_primary_dark.value = props.project_settings.theme?.primary_color_dark;
  gradient_from_light.value = props.project_settings.theme?.gradient_from_light;
  gradient_to_light.value = props.project_settings.theme?.gradient_to_light;
  gradient_from_dark.value = props.project_settings.theme?.gradient_from_dark;
  gradient_to_dark.value = props.project_settings.theme?.gradient_to_dark;

  color_primary_light.value = unifyHex(document.documentElement.style.getPropertyValue('--color-primary_light'));
  color_primary_dark.value = unifyHex(document.documentElement.style.getPropertyValue('--color-primary_dark'));
  gradient_from_light.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_from_light'));
  gradient_to_light.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_from_light'));
  gradient_from_dark.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_from_dark'));
  gradient_to_dark.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_to_dark'));


});

watch(color_primary_light, (newColor) => {
  document.documentElement.style.setProperty('--color-primary_light', unifyHex(newColor));
  const contrastColor = getContrastYIQ(unifyHex(newColor)) === 'black' ? '#FFFFFF' : '#000000';
  document.documentElement.style.setProperty('--contrast-text_light', contrastColor);
});

watch(color_primary_dark, (newColor) => {
  document.documentElement.style.setProperty('--color-primary_dark', unifyHex(newColor));
  const contrastColor = getContrastYIQ(unifyHex(newColor)) === 'black' ? '#FFFFFF' : '#000000';
  document.documentElement.style.setProperty('var(--contrast-text_dark)', contrastColor);
});

watch(gradient_from_light, (newColor) => {
  document.documentElement.style.setProperty('--gradient_from_light', unifyHex(newColor));
});

watch(gradient_to_light, (newColor) => {
  document.documentElement.style.setProperty('--gradient_to_light', unifyHex(newColor));
});

watch(gradient_from_dark, (newColor) => {
  document.documentElement.style.setProperty('--gradient_from_dark', unifyHex(newColor));
});

watch(gradient_to_dark, (newColor) => {
  document.documentElement.style.setProperty('--gradient_to_dark', unifyHex(newColor));
});

</script>

<template>
  <div class="parent-container">
    <PrimeTabView class="container">
      <PrimeTabPanel header="Account" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
      </PrimeTabPanel>
      <PrimeTabPanel header="Endpoint Manager" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 1)})}">
        <ApiEndpointsManager :initialEndpoints="{ api_endpoints: props.project_settings['api_endpoints'] }" />
      </PrimeTabPanel>
      <PrimeTabPanel header="Theming" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 2)})}">
        <div style="margin:15px">
        <div class="grid-container">

            <div><b>Primary Color</b></div><div></div>
            <div>Dark/Light Mode</div>
            <PrimeInputSwitch v-model="darkMode" @update:model-value="toggleDarkMode"></PrimeInputSwitch>

            <div><b>Primary Color</b></div><div></div>
            <div>Light</div>
            <PrimeColorPicker v-model="color_primary_light"></PrimeColorPicker>
            <div>Dark</div>
            <PrimeColorPicker v-model="color_primary_dark"></PrimeColorPicker>

            <div><b>Gradiant Color</b></div><div></div>
            <div>From - Light</div>
            <PrimeColorPicker v-model="gradient_from_light"></PrimeColorPicker>
            <div>To - Light</div>
            <PrimeColorPicker v-model="gradient_to_light"></PrimeColorPicker>
            <div>From - Dark</div>
            <PrimeColorPicker v-model="gradient_from_dark"></PrimeColorPicker>
            <div>To - Dark</div>
            <PrimeColorPicker v-model="gradient_to_dark"></PrimeColorPicker>
          </div>
        </div>
      </PrimeTabPanel>
      <PrimeTabPanel header="Projects" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 3)})}">
      </PrimeTabPanel>
    </PrimeTabView>
  </div>
</template>


<style scoped>

.parent-container {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  width: 100%;
}

.container {
  border-radius: 15px;
  width: 100%;
}

:root {
  --color-primary: #7a7a7a;
  --contrast-text: #7a7a7a;
}


.grid-container {
  width: 100%;
}

.grid-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 10px;
  align-items: center;
}

:root {
  --b_color_light:rgb(0,0,0);
  --b_color_dark:rgb(0,0,0);
  --color-primary_light: rgb(0,0,0);
  --color-primary_dark: rgb(0,0,0);
  --gradient_from_light: rgb(0,0,0);
  --gradient_to_light: rgb(0,0,0);
  --gradient_from_dark: rgb(0,0,0);
  --gradient_to_dark: rgb(0,0,0);
  --contrast-text_light: rgb(0,0,0);
  --contrast-text_dark: rgb(0,0,0);
}

</style>