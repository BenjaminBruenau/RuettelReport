<script setup lang="ts">

import {ref} from "vue";
import { useUserStore } from "~/stores/user";
import { useCookie } from "#app";

const props = defineProps({
  currentData: {
    type: Object,
    default: () => ({data: {}})
  },
});

const projectSettingsStore = useProjectSettingsStore()
const projectSettings = storeToRefs(projectSettingsStore)

const structure = ref({
  requestOptions: {
    format: {
      type: 'string',
      include: true,
      value: 'geojson',
      attr: {
        default: 'geojson',
        title: 'Format',
      }
    },
    endtime: {
      type: 'dateTime',
      include: true,
      value: '',
      attr:{
        default: 'case_today',
        title: 'End time',
      }
    },
    starttime: {
      type: 'dateTime',
      include: true,
      value: '',
      attr:{
        default: 'case_lastMonth',
        title: 'Start time',
      }
    },
    minmagnitude: {
      type: 'float_span',
      include: true,
      value: 0.0,
      attr: {
        default: 0.0,
        title: 'Minimum Magnitude',
        min: 0.0,
        max: 10.0,
      },
    },
    maxmagnitude: {
      type: 'float_span',
      include: true,
      value: 10.0,
      attr: {
        default: 10.0,
        title: 'Maximum Magnitude',
        min: 0.0,
        max: 10.0,
      },
    },
    minlongitude: {
      type: 'float_span',
      include: true,
      value: -180.0,
      attr: {
        default: -180.0,
        title: 'Minimum Longitude',
        min: -180.0,
        max: 180.0,
      },
    },
    maxlongitude: {
      type: 'float_span',
      include: true,
      value: 180.0,
      attr: {
        default: 180.0,
        title: 'Maximum Longitude',
        min: -180.0,
        max: 180.0,
      },
    },
    minlatitude: {
      type: 'float_span',
      include: true,
      value: -90.0,
      attr: {
        default: -90.0,
        title: 'Minimum Latitude',
        min: -90.0,
        max: 90.0,
      },
    },
    maxlatitude: {
      type: 'float_span',
      include: true,
      value: 90.0,
      attr: {
        default: 90.0,
        title: 'Maximum Latitude',
        min: -90.0,
        max: 90.0,
      },
    },
  },
});

const apiFilterBlocks = ref([]);

const createApiFilterBlocks = () => {

  const newApiFilterBlocks = Object.keys(projectSettings.api_endpoints.value).map((apiKey, index) => {
    const existingBlock = apiFilterBlocks.value.find(block => block.index === index);

    const blockStructure = existingBlock ? existingBlock.structure : ref(JSON.parse(JSON.stringify(structure.value)));
    console.log("SETTINGS BLOCK: ", projectSettings)
    return {
      index,
      structure: blockStructure,
      project_settings: ref(projectSettings)
    };
  });

  if (JSON.stringify(apiFilterBlocks.value) !== JSON.stringify(newApiFilterBlocks)) {
    apiFilterBlocks.value = newApiFilterBlocks;
  }
};

watch(() => projectSettings.api_endpoints, () => {
  createApiFilterBlocks();
}, { deep: true });


function getApiFilterBlock(index) {
  const block = apiFilterBlocks.value[index];
  if (!block) {
    return null;
  }

  const { structure, project_settings } = block;
  const apiEndpoints = projectSettings.api_endpoints.value;
  const apiEndpointKeys = Object.keys(apiEndpoints);

  if (apiEndpointKeys.length === 0) {
    return null;
  }

  const endpointName = apiEndpointKeys[index % apiEndpointKeys.length];
  const apiEndpoint = apiEndpoints[endpointName];

  const mappingRules = apiEndpoint.mappingRules || '';

  return {
    queryStructure: {
      requestOptions:structure.requestOptions,
      api_endpoints: {
        [endpointName]: apiEndpoint
      },
    },
    endpoint: endpointName,
    mappingRules: mappingRules
  };
}

createApiFilterBlocks();

const handleUpdateRequestOptions = (payload) => {
  const { index, newValues } = payload;

  console.log(JSON.stringify(newValues,null,2));

  if (index >= 0 && index < apiFilterBlocks.value.length) {

    const block = apiFilterBlocks.value[index];
    const actualNewValues = newValues._rawValue || newValues._value;

    Object.entries(actualNewValues).forEach(([key, newValue]) => {
      if (block.structure.requestOptions[key]) {
        block.structure.requestOptions[key].value = newValue.value;
      }
    });
    createApiFilterBlocks();
  }
};


const queryStructure = {
  "queryStructure": {
    "requestOptions":{
      "format":{
        "type": "string",
        "include": true,
        "value": "geojson"
      },
      "starttime":{
        "type": "dateTime",
        "include": true,
        "value": "2023-09-10T00:00:00"
      },
      "endtime":{
        "type": "dateTime",
        "include": true,
        "value": "2023-09-10T00:10:00"
      },
      "minmagnitude":{
        "type": "float",
        "include": true,
        "value":0.0

      },
      "maxmagnitude":{
        "type": "float",
        "include": true,
        "value":10.0
      },
      "minlongitude":{
        "type": "float",
        "include": true,
        "value":-180.0

      },
      "maxlongitude":{
        "type": "float",
        "include": true,
        "value":180.0

      },
      "minlatitude":{
        "type": "float",
        "include": true,
        "value":-90.0

      },
      "maxlatitude":{
        "type": "float",
        "include": true,
        "value":90.0
      }

    },
    "api_endpoints":{
      "earthquake.usgs.gov": {
        "url": "https://earthquake.usgs.gov/fdsnws/event/1/query",
        "method": "GET",
        "params": {
          "format": "format",
          "starttime": "starttime",
          "endtime": "endtime",
          "minmagnitude": "minmagnitude",
          "maxmagnitude": "maxmagnitude",
          "minlongitude": "minlongitude",
          "maxlongitude": "maxlongitude",
          "minlatitude": "minlatitude",
          "maxlatitude": "maxlatitude"
        }
      },
      "earthquake.usgs.gov2":{
        "url": "https://earthquake.usgs.gov/fdsnws/event/1/query",
        "method": "GET",
        "params": {
          "format": "format",
          "starttime": "starttime",
          "endtime": "endtime"
        }
      }
    }
  },
  "endpoint": "earthquake.usgs.gov2",
  "mappingRules" : "\"type\" -> \"newType\""
}

const result = ref({})

/*
watch(() => result, () =>{
  console.log(JSON.stringify(result,null,2));
});
*/

const emit = defineEmits(['update-api-response']);
const userStore = useUserStore()
const sendRequest = async (queryParams,index,color) => {
  console.log("Trying to send!");
  const {
    data: features,
    pending,
    refresh,
    error,
    status,
  } = await useFetch(userStore.isPremium ? '/premium/query-service/api/query' : '/query-service/api/query', {
    key: 'features',
    method: 'POST',
    headers: {
      "Authorization": `Bearer ${useCookie('rrAuthToken').value}`,
      "X-TenantId": userStore.tenantId,
      "X-UserId": userStore.userId
    },
    body: queryParams, // Verwenden Sie den übergebenen queryParams-Parameter
    onRequest({ request, options }) {
      console.log('Send Request')
    },
    onResponseError({ request, response, options }) {
      console.debug('ERROR while loading data: ',response);

    },
    onResponse({ request, response, options }) {
      if (response._data) {
        //console.debug('Result: ', response._data);
        result.value = response._data;
        emit('update-api-response', { index: index, data: result,color: color });
      }
    },
  });
}

function getApiColors() {
  const apiEndpoints = projectSettings.api_endpoints.value;
  const colors = [];

  for (const key in apiEndpoints) {
    if (apiEndpoints[key].color) {
      colors.push(apiEndpoints[key].color);
    }
  }
  return colors;
}


async function runButton() {

  console.log("RUN!");
  for (let index = 0; index < apiFilterBlocks.value.length; index++) {
    const blockString = getApiFilterBlock(index);
    if (blockString) {
      console.log(JSON.stringify(blockString,null,2));
      await sendRequest(blockString,index,getApiColors()[index]);
    }
  }
}

// ---------------



</script>

<template>

  <PrimeToolbar>
    <template #start>
      <!--<PrimeButton icon="pi pi-replay" class="mr-2"></PrimeButton>-->
      <!--<PrimeButton icon="pi pi-cog" class="mr-2" label="APIs"></PrimeButton>-->
   </template>

   <template #end>

     <PrimeButton icon="pi pi-play" class="mr-2" label="RUN" @click="runButton"></PrimeButton>

   </template>
 </PrimeToolbar>
 <div style="height: 5px"/>
 <div class="virtual-scroller-container">
   <PrimeVirtualScroller :items="apiFilterBlocks" :itemSize="50">
     <template v-slot:item="{ item }">
       <ApiFilterBlock :structure="item.structure"
                       :project_settings="item.project_settings"
                       :index="item.index"
                       @update-request-options="handleUpdateRequestOptions"/>
     </template>
   </PrimeVirtualScroller>
 </div>
</template>



<style scoped>
.virtual-scroller-container{
 height:100%;
 border-radius:15px;
}

</style>