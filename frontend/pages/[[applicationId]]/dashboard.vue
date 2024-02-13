<script setup lang="ts">
import {onMounted, ref} from "vue";
import EventDistribution from "~/components/statistics/EventDistribution.vue";
import {useCookie} from "nuxt/app";

definePageMeta({
  middleware: ['auth'],
});
const userStore = useUserStore()

const projectSettings = useProjectSettingsStore()

const projectSettingsRef = storeToRefs(projectSettings)


// Fetch Analytics Data

const {
  pending: realtimePending,
  data: realtimeDataComplete,
  error: realtimeError,
  refresh: realtimeRefresh
} = useFetch('/api/analysis/realtime-complete', {
  key: 'realtimeData',
  lazy: false,
  default: () => {},
  onResponseError({ request, response, options }) {
    console.debug('ERROR while loading realtime data: ', response);
  },
  onResponse({ request, response, options }) {
    if (response._data && response._data) {
      console.debug('Loaded Real Time Data (Complete): ', response._data);
    }
  },
});

const realtimeAnalyticsMagDistrData = ref()
const realtimeAnalyticsData = ref()

const realtimeAnalyticsDataComplete = ref()
const realtimeAnalyticsMagDistrDataComplete = ref()

const token = useCookie('rrAuthToken').value
const { $io } = useNuxtApp()

const socket = $io({
  token: token
})

socket.connect()

socket.on(SocketEvent.analytics_magdistr, (data: any) => {
  console.debug('Received new magdistr data: ', data)
  realtimeAnalyticsMagDistrData.value = data
})

socket.on(SocketEvent.analytics, (data: any) => {
  console.debug('Received new analytics data: ', data)
  realtimeAnalyticsData.value = data
})

socket.on(SocketEvent.analytics_complete, (data: any) => {
  console.debug('Received new complete analytics data: ', data)
  realtimeAnalyticsDataComplete.value = data
})

socket.on(SocketEvent.analytics_complete_magdistr, (data: any) => {
  console.debug('Received new complete magdistr data: ', data)
  realtimeAnalyticsMagDistrDataComplete.value = data
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






onMounted(() => {
  getUserEmail();
});



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
        <ApiFilterContainer :current-data="currentData"
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
            <div class="tile flex flex-row items-center mt-6" v-if="activeWindow===1">

              <BarChart :data="realtimeAnalyticsMagDistrData ? realtimeAnalyticsMagDistrData : undefined" class="w-1/2"></BarChart>
              <div class="flex flex-col justify-center items-center w-1/2">
                <PrimeCard class="mb-4 w-2/3" >
                  <template #title>
                    <div class="text-center">Average Magnitude</div>
                  </template>
                  <template #content>
                    <div class="text-center" style="font-size: 1.25rem">
                      {{realtimeAnalyticsData ? realtimeAnalyticsData.avg_magnitude : '-'}}
                    </div>
                  </template>
                </PrimeCard>
                <PrimeCard class="w-2/3">
                  <template #title>
                    <div class="text-center">Event Count</div>
                  </template>
                  <template #content>
                    <div class="text-center" style="font-size: 1.25rem">
                      {{realtimeAnalyticsData ? realtimeAnalyticsData.count : '-'}}
                    </div>
                  </template>
                </PrimeCard>
              </div>
            </div>

            <div class="tile mt-6" v-if="activeWindow===1">
              <div><b class="text-textColor_light dark:text-textColor_dark">Total Aggregations (over all queried data)</b></div> <!-- v-tooltip.top="'test'"-->
              <div class="flex flex-row items-center">

                <BarChart class="w-1/2" :data="realtimeAnalyticsMagDistrDataComplete ? realtimeAnalyticsMagDistrDataComplete : realtimeDataComplete ? realtimeDataComplete.magDistribution : undefined"></BarChart>
                <div class="flex flex-col justify-center items-center w-1/2">
                  <PrimeCard class="mb-4 w-2/3" >
                    <template #title>
                      <div class="text-center">Average Magnitude</div>
                    </template>
                    <template #content>
                      <div class="text-center" style="font-size: 1.25rem">
                        {{realtimeAnalyticsDataComplete ? realtimeAnalyticsDataComplete.avg_magnitude : realtimeDataComplete && realtimeDataComplete.aggregations ? realtimeDataComplete.aggregations.avg_magnitude : '-'}}
                      </div>
                    </template>
                  </PrimeCard>
                  <PrimeCard class="w-2/3">
                    <template #title>
                      <div class="text-center">Event Count</div>
                    </template>
                    <template #content>
                      <div class="text-center" style="font-size: 1.25rem">
                        {{realtimeAnalyticsDataComplete ? realtimeAnalyticsDataComplete.count : realtimeDataComplete && realtimeDataComplete.aggregations ? realtimeDataComplete.aggregations.count : '-'}}
                      </div>
                    </template>
                  </PrimeCard>
                </div>
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
            <Settings :premium="userStore.isPremium"></Settings>
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
