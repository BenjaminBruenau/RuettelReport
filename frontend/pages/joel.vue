<script setup lang="ts">
import { ref } from "vue";
const visible = ref(false);

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

const activeWindow = ref(1);

const setActiveWindow = (windowNumber) => {
  activeWindow.value = windowNumber;
  console.log(activeWindow.value);
}

</script>
<template>

  <div class="card flex justify-content-center">
    <PrimeSidebar v-model:visible="visible">
      <template #container="{ closeCallback }">
        <div style="margin: 10px">
          <PrimeInputSwitch v-model="darkMode"
                            @update:model-value="toggleDarkMode"
          ></PrimeInputSwitch>
        </div>

      </template>
    </PrimeSidebar>
    <PrimeButton icon="pi pi-bars" @click="visible = true" />
  </div>
  <div class="dashboard">
    <div class="container">
      <div class="tile tile_left first-column">
          <ApiFilterContainer></ApiFilterContainer>
      </div>
      <div class="second-column">
        <div class="second-column-content">
          <div class="tile tile_right_1 second-row-content2">
            <PrimeToolbar>
              <template #start>
                <PrimeButton icon="pi pi-map" class="mr-2" label="Map" @click="setActiveWindow(1)"></PrimeButton>
                <PrimeButton icon="pi pi-chart-bar" class="mr-2" label="Statistics" @click="setActiveWindow(2)"></PrimeButton>
              </template>
              <template #end>
                <PrimeButton icon="pi pi-cog" class="mr-2" label="Settings"></PrimeButton>
                <PrimeAvatar label="V" class="mr-2" size="large"  shape="circle"  />
              </template>
            </PrimeToolbar>
          </div>
          <div class="tile tile_right_2 map-content" v-if="activeWindow===1">
              <Map></Map>
          </div>
          <div class="tile tile_right_2 map-content" v-if="activeWindow===2">
            <Settings></Settings>
          </div>
          <div class="tile tile_right_3 second-row-content">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  width: 100%;
  height: calc(100vh);
  overflow: auto;
  margin: 0px;
}

.container {
  display: grid;
  grid-template-columns: 1fr 3fr;
  gap: 10px;
  height: 100%;
}

.tile{
  @apply bg-tile_color_light dark:bg-tile_color_dark;
  border-radius: 20px;
  padding: 15px;
}

.tile_left {
  margin: 15px 5px 15px 15px;
}

.tile_right {
  margin: 15px 15px 15px 5px;
}

.tile_right_1{
  margin: 15px 15px 10px 5px;
}
.tile_right_2{
  margin: 10px 15px 10px 5px;
}
.tile_right_3{
  margin: 10px 15px 15px 5px;
}


.first-column {
  display: flex;
  flex-direction: column;
}

.second-column {
  display: flex;
  flex-direction: column;
}

.second-column-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.map-content {
  flex: 9; /* Erste Zeile mit 8/10 Höhe */
}

.second-row-content {
  flex: 1; /* Zweite Zeile mit 2/10 Höhe */
}
.second-row-content2 {
  flex:0.5; /* Zweite Zeile mit 2/10 Höhe */
}
</style>
