<script setup lang="ts">
import {onMounted, ref} from "vue";
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

// Ruettel Report Generation
const {
  pending: reportDataPending,
  data: reportData,
  error: reportDataError,
  refresh: reportDataRefresh
} = useFetch('/api/analysis/ruettel-report', {
  key: 'realtimeData',
  lazy: false,
  default: () => [],
  onResponseError({ request, response, options }) {
    console.debug('ERROR while loading report data: ', response);
  },
  onResponse({ request, response, options }) {
    if (response._data && response._data) {
      console.debug('Loaded Ruettel Report Data: ', response._data);
    }
  },
});

const magDistrLabels = ['0-2', '2-4', '4-6', '6-8', '8-10', 'Unknown']

const reportDataExample =
  {
    "_id" : "65cfc143b8edfd6277d1138f",
    "distribution" : {
      "mean" : 165329.5,
      "std" : 99644.7804980271
    },
    "eventTypeData" : {
      "earthquake" : {
        "count" : 33083,
        "probability" : 0.973115
      },
      "ice quake" : {
        "count" : 355,
        "probability" : 0.010442
      },
      "quarry blast" : {
        "count" : 329,
        "probability" : 0.009677
      },
      "explosion" : {
        "count" : 200,
        "probability" : 0.005883
      },
      "mining explosion" : {
        "count" : 26,
        "probability" : 0.000765
      },
      "other event" : {
        "count" : 3,
        "probability" : 8.8e-05
      }
    },
    "time_range" : {
      "minTimestamp" : "2024-01-24T14:41:16.774+01:00",
      "maxTimestamp" : "2024-01-24T15:31:27.307+01:00"
    }
  }


const getCountsOnly = (obj: any) => {
  const result = {};
  for (const key in obj) {
    result[key] = obj[key].count;
  }
  return result;
}

const selectedReport = ref();

const selectExampleData = () => {
  selectedReport.value = reportDataExample
}

const reportOptionLabel = () => {
  return (report: any) => {
    const start = new Date(report.time_range.minTimestamp)
    const end = new Date(report.time_range.maxTimestamp)
    return `Data Range: ${start.toLocaleString()} - ${end.toLocaleString()}`
  }
}
import html2canvas from 'html2canvas';
import jsPDF from "jspdf";

const pendingDownload = ref(false)
const downloadAsPDF = async () => {

  const element = document.getElementById('ruettel-report-predictions');
  if (!element) return

  const chart = document.getElementById('ruettel-report-chart');
  if (!chart) return
  const chartWidth = chart.offsetWidth;
  const chartHeight = chart.offsetHeight;

  pendingDownload.value = true
  const pdf = new jsPDF({
    unit: 'px',
    format: [element.offsetWidth, element.offsetHeight]
  });
  html2canvas(chart, {scale: 1}).then(async canvas => {
    await pdf.html(element, {
      callback: (doc) => {
        doc = doc.addPage()
        doc.addImage({
          imageData: canvas,
          x: 5,
          y: 5,
          width: chartWidth,
          height: chartHeight,
          format: 'PNG'
        })
        .save(
          `RuettelReport_${selectedReport.value.time_range.minTimestamp}-${selectedReport.value.time_range.maxTimestamp}.pdf`
        );
        pendingDownload.value = false
      },
      margin: [ 10, 10, 10, 10 ],
    })
  })
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

              <BarChart :data="realtimeAnalyticsMagDistrData ? realtimeAnalyticsMagDistrData : undefined" :labels="magDistrLabels" class="w-1/2"></BarChart>
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

                <BarChart class="w-1/2" :data="realtimeAnalyticsMagDistrDataComplete ? realtimeAnalyticsMagDistrDataComplete : realtimeDataComplete ? realtimeDataComplete.magDistribution : undefined" :labels="magDistrLabels"></BarChart>
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
            <div class="mb-4 flex flex-row justify-between text-center items-center">
              <div class="flex items-center">
                <PrimeButton label="Example Data" severity="secondary" rounded @click="selectExampleData"/>
              </div>
              <div class="flex items-center">
                <PrimeDropdown class="mr-5"
                               v-model="selectedReport"
                               :options="reportData"
                               placeholder="Select a Report"
                               checkmark
                               :loading="reportDataPending"
                               :highlightOnSelect="false">
                  <template #value="slotProps">
                    <div v-if="slotProps.value" class="flex align-items-center">
                      <div>{{ reportOptionLabel()(slotProps.value) }}</div>
                    </div>
                    <span v-else>
                  {{ slotProps.placeholder }}
                  </span>
                  </template>
                  <template #option="slotProps">
                    <div class="flex align-items-center">
                      <div>{{ reportOptionLabel()(slotProps.option) }}</div>
                    </div>
                  </template>
                </PrimeDropdown>
                <PrimeButton class="mr-5" icon="pi pi-refresh" text rounded aria-label="Refresh" @click="reportDataRefresh"/>
                <PrimeButton :loading="pendingDownload" :disabled="!selectedReport" icon="pi pi-download" label="PDF"  @click="downloadAsPDF"/>
              </div>
            </div>
            <div class="flex justify-center items-center mt-10" v-if="!selectedReport">
              <h3>No Report Selected</h3>
            </div>
            <div id="ruettel-report"  v-if="selectedReport">
              <PrimeCard>
                <template #title>Event Distribution</template>
                <template #content>
                  <div id="ruettel-report-chart">
                    <BarChart class="w-1/2" :data="getCountsOnly(selectedReport.eventTypeData)" :labels="Object.keys(selectedReport.eventTypeData)" x-axis-text="Event Type"></BarChart>
                  </div>
                </template>
              </PrimeCard>
              <div id="ruettel-report-predictions">
                <PrimeCard class="mt-4">
                  <template #title>Event Prediction</template>
                  <template #content>
                    <EventDistribution :data="selectedReport.eventTypeData"/>

                  </template>
                </PrimeCard>
                <PrimeCard class="mt-4">
                  <template #title>Time Prediction</template>
                  <template #content>
                    <PredictProbNext :mean="selectedReport.distribution.mean" :std="selectedReport.distribution.std"/>

                  </template>
                </PrimeCard>
              </div>

            </div>

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
