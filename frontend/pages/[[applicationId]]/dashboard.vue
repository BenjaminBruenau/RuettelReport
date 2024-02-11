<script setup lang="ts">
import {onMounted, ref} from "vue";
import EventDistribution from "~/components/statistics/EventDistribution.vue";
import {useCookie} from "nuxt/app";

definePageMeta({
  middleware: ['auth'],
});
const userStore = useUserStore()

const project_settings = {
  'id': Number,
  'project':{
    'users':{

    }
  },
  'api_endpoints':{
    'earthquake.usgs.gov': {
      url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
      method: 'GET',
      color: '#009b91',
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
      mappingRules: '',
    },

    'earthquake.usgs.gov2': {
      url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
      method: 'GET',
      color: '#e1967a',
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
      mappingRules: '',
    }

  },

  'theme':{
    'primary_color_light':'#009b91',
    'primary_color_dark':'#009b91',
    'gradient_from_light':"#dde6eb",
    'gradient_to_light':"#dde6eb",
    'gradient_from_dark':"#334152",
    'gradient_to_dark':"#334152",
    'default_theme': 'light',
  },
  /*
  'theme':{
    'primary_color_light':'#ffffff',
    'primary_color_dark':'#9e9e9e',
    'gradient_from_light':"#d8d8d8",
    'gradient_to_light':"#d8d8d8",
    'gradient_from_dark':"#1f1f1f",
    'gradient_to_dark':"#1f1f1f",
    'default_theme': 'light',
  },

   */

};

const {
  pending,
  data: projectSettings,
  error,
  refresh
} = useFetch('/api/project-settings', {
  key: 'projectSettings',
  lazy: false,
  // default settings (e.g. new tenant)
  default: () => project_settings,
  onResponseError({ request, response, options }) {
    console.debug('ERROR while loading project settings: ', response);
    //ToDo: Proper Error Handling, maybe display toast
  },
  onResponse({ request, response, options }) {
    if (response._data && response._data) {
      console.debug('Loaded ProjectSettings: ', response._data);
      setupTheme(response._data.theme)
    }
  },
});


// Fetch Analytics Data


const {
  pending: realtimePending,
  data: realtimeData,
  error: realtimeError,
  refresh: realtimeRefresh
} = useFetch('/api/analysis/realtime', {
  key: 'realtimeData',
  lazy: false,
  // default settings (e.g. new tenant)
  default: () => {},
  onResponseError({ request, response, options }) {
    console.debug('ERROR while loading realtime data: ', response);
  },
  onResponse({ request, response, options }) {
    if (response._data && response._data) {
      console.debug('Loaded Real Time Data: ', response._data);
    }
  },
});

const realtimeAnalyticsData = ref()
const token = useCookie('rrAuthToken').value
const { $io } = useNuxtApp()

const socket = $io({token: token})

socket.connect()

socket.on(SocketEvent.new_analysis_data, (data: any) => {
  console.log('Received Message: ', data)
  realtimeAnalyticsData.value = data
})

const userEmail = ref('<null>');

const currentData = ref([]);

watch(currentData,()=>{
  console.log('CURRENT',currentData.value);
});


const savedTheme = localStorage.getItem('theme')

if (savedTheme) {
  console.log('savedTheme: '+savedTheme)
  useColorMode().preference = savedTheme
}

const activeWindow = ref(1);

const setActiveWindow = (windowNumber) => {
  activeWindow.value = windowNumber;
  console.log(activeWindow.value);
}

function setupTheme(themeSettings: any) {
  console.log('SEETING UP THEME: ', themeSettings)
  //const themeSettings = projectSettings.value.theme;
  document.documentElement.style.setProperty('--color-primary', unifyHex(themeSettings.primary_color_light));
  document.documentElement.style.setProperty('--color-primary_light', unifyHex(themeSettings.primary_color_light));
  document.documentElement.style.setProperty('--color-primary_dark', unifyHex(themeSettings.primary_color_dark));
  document.documentElement.style.setProperty('--gradient_to_light', unifyHex(themeSettings.primary_color_dark));
  document.documentElement.style.setProperty('--gradient_from_light', unifyHex(themeSettings.gradient_from_light));
  document.documentElement.style.setProperty('--gradient_to_light', unifyHex(themeSettings.gradient_to_light));
  document.documentElement.style.setProperty('--gradient_from_dark', unifyHex(themeSettings.gradient_from_dark));
  document.documentElement.style.setProperty('--gradient_to_dark', unifyHex(themeSettings.gradient_to_dark));
  document.documentElement.style.setProperty('--b_color_light', adjustColorBrightness(themeSettings.gradient_from_light,1.1));
  document.documentElement.style.setProperty('--b_color_dark', adjustColorBrightness(themeSettings.gradient_from_dark, 0.9));
}

function unifyHex(text: string){
  return '#'+text.replace("#", "")
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
  setupTheme(projectSettings.value.theme);
  getUserEmail();
});


const resetProjectSettings = () => {
  refresh()
}


const updateProjectSettings = async (updatedSettings: any) => {
  console.log("SETTINGS:", updatedSettings)


  const response = await $fetch('/api/project-settings', {
    method: 'post',
    body: updatedSettings
  });

  if (!response.acknowledged) {
    console.error('Error while updating project settings')
  }
}

function to_color(text){
  return '#'+text.replace("#", "")
}

const update_api_response = (payload) => {
  const { index, data, color } = payload;
  const actualNewValues = data._rawValue || data._value;

  const existingElementIndex = currentData.value.findIndex(el => el.index === index);

  if (existingElementIndex !== -1) {
    currentData.value[existingElementIndex].color = to_color(color);
    currentData.value[existingElementIndex].data = actualNewValues;

  } else {
    currentData.value.push({ index, color:to_color(color), data: actualNewValues });
  }
}


async function logout(){
  const response = await $fetch('/api/logout', {
    method: 'post',
    body: {
      token: useCookie('rrAuthToken').value,
    }
  });
  window.location.reload();
}


async function getUserEmail(){
  const response = await $fetch('/api/retrieveUser', {
    method: 'post',
    body: {
      token: useCookie('rrAuthToken').value,
    }
  });
  try{
    userEmail.value = response.user.email;
  }catch (error){

  }

}


</script>
<template>
  <div class="dashboard">
    <div class="container">
      <div class="tile tile_left first-column">
        <ApiFilterContainer :project_settings="projectSettings"
                            :current-data="currentData"
                            @update-api-response="update_api_response"
        ></ApiFilterContainer>
      </div>
      <div class="second-column">
        <div class="second-column-content">
          <div class="tile tile_right_1 second-row-content2">
            <PrimeToolbar>
              <template #start>
                <PrimeButton icon="pi pi-map" class="mr-2" label="Map" @click="setActiveWindow(1)"></PrimeButton>
                <PrimeButton icon="pi pi-chart-bar" class="mr-2" label="★ Statistics" :disabled="!userStore.isPremium" @click="setActiveWindow(2)" ></PrimeButton>
              </template>
              <template #end>
                <b>{{userEmail}}</b>
                <div style="width: 20px"/>
                <PrimeButton icon="pi pi-sign-out" class="mr-2" @click="logout"></PrimeButton>
                <PrimeButton icon="pi pi-cog" class="mr-2" label="Settings" @click="setActiveWindow(3)"></PrimeButton>
              </template>
            </PrimeToolbar>
          </div>
          <div class="tile tile_right_2 map-content" v-if="activeWindow===1">
            <Map :currentData="currentData"></Map>
            <div class="tile flex flex-row mt-6" v-if="activeWindow===1">

              <BarChart :data="realtimeAnalyticsData ? realtimeAnalyticsData : realtimeData ? realtimeData.magDistribution : undefined" class="w-1/2"></BarChart>
              <BarChart class="w-1/2"></BarChart>

            </div>
            <div class="tile mt-6" v-if="activeWindow===1">
              <div><b >Total Aggregations (over all queried data)</b></div> <!-- v-tooltip.top="'test'"-->
              <div class="flex flex-row">
                <BarChart class="w-1/2"></BarChart>
                <BarChart class="w-1/2"></BarChart>
              </div>


            </div>
          </div>

          <div class="tile tile_right_2 map-content" v-if="activeWindow===2">
            <PrimeCard>
              <template #title>Event Distribution</template>
              <template #content>
              <!--<BarChart/>-->
              </template>
            </PrimeCard>
            <div style="height:20px"></div>
            <PrimeCard>
              <template #title>Event Prediction</template>
              <template #content>
                <EventDistribution/>
              </template>
            </PrimeCard>
          </div>
          <div class="tile tile_right_2 map-content" v-if="activeWindow===3">
            <Settings :project_settings="projectSettings"
                      :premium="userStore.isPremium"
                      @update-project-settings="updateProjectSettings"
                      @reset-project-settings="resetProjectSettings"></Settings>
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
  backdrop-filter: blur(100px);
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
  margin: 10px 15px 15px 5px;
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
  flex: 9;
}

.second-row-content2 {
  flex:0.5;
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
}
:root {
  --color-primary: #7a7a7a;
  --contrast-text: #7a7a7a;
}

</style>
