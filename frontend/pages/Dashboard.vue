<script setup lang="ts">

import {ref} from "vue";

const props = defineProps({
  project_settings: {
    type: Object,
    default: () => ({data: {}})
  },
});

const project_settings_struct = ref({
  'id': Number,
  'project':{},
  'api_endpoints':{},
  'theme':{
    'primary_color_light':'#fff',
    'primary_color_dark':'#fff',
    'default_theme': 'light',
  },
});

const project_settings = ref({
  'id': Number,
  'project':{},
  'api_endpoints':{
    'earthquake.usgs.gov': {
      url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
      method: 'GET',
      color: '#ff0000',
      params: {
        format: 'format',
        starttime: 'starttime',
        endtime: 'endtime',
        minmagnitude: 'minmagnitude',
        maxmagnitude: 'maxmagnitude',
        minlongitude: 'minlongitude',
        maxlongitude: 'maxlongitude',
        minlatitude: 'minlatitude',
        maxlatitude: 'maxlatitude',
      },
    }
  },
  'theme':{
    'primary_color_light':'#fff',
    'primary_color_dark':'#fff',
    'default_theme': 'light',
  },
});

const default_api_endpoints = ref({
  'api_endpoints':{
    'earthquake.usgs.gov': {
      url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
      method: 'GET',
      color: '#ff0000',
      params: {
        format: 'format',
        starttime: 'starttime',
        endtime: 'endtime',
        minmagnitude: 'minmagnitude',
        maxmagnitude: 'maxmagnitude',
        minlongitude: 'minlongitude',
        maxlongitude: 'maxlongitude',
        minlatitude: 'minlatitude',
        maxlatitude: 'maxlatitude',
      },
    }
  }
});

const activeWindow = ref(1);

const setActiveWindow = (windowNumber) => {
  activeWindow.value = windowNumber;
  console.log(activeWindow.value);
}

</script>

<template>
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
                <PrimeButton icon="pi pi-chart-bar" class="mr-2" label="Statistics" @click="setActiveWindow(2)" ></PrimeButton>
              </template>
              <template #end>
                <PrimeButton icon="pi pi-cog" class="mr-2" label="Settings" @click="setActiveWindow(3)"></PrimeButton>
              </template>
            </PrimeToolbar>
          </div>
          <div class="tile tile_right_2 map-content" v-if="activeWindow===1">
            <Map></Map>
          </div>
          <div class="tile tile_right_2 map-content" v-if="activeWindow===2">
          </div>
          <div class="tile tile_right_2 map-content" v-if="activeWindow===3">
            <Settings :project_settings="project_settings"></Settings>
          </div>
          <!--<div class="tile tile_right_3 second-row-content">
          </div>-->
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>