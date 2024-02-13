<template>
  <!--
      <theme-customizer></theme-customizer>
      <div>{{ result}}</div>
     <button @click="sendRequest">Send</button>-->
</template>

<script setup lang="ts">

definePageMeta({
  middleware: ['auth'],
});

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

onBeforeMount(async () => await navigateTo('/dashboard'))

/*
// Needs to be post request, body will be omitted otherwise by nuxt internally
const getFeatures = async () => {
  const { data, pending, error } = await useFetch(
      '/api/query',
      {
        method: 'POST',
        body: queryStructure
      }
  )
  result.value = data || {  }
}

onMounted(getFeatures)



const sendRequest = () => {
  const {
    data: features,
    pending,
    refresh,
    error,
    status,
  } = useFetch('/api/query', {
    key: 'features',
    method: 'POST',
    body: queryStructure,
    onRequest({ request, options }) {
      console.log('Send Request')
    },
    onResponseError({ request, response, options }) {
      console.debug('ERROR while loading data: ', response);

    },
    onResponse({ request, response, options }) {
      if (response._data) {
        console.debug('Result: ', response._data);
        result.value = response._data;
      }
    },
  });
}

*/




</script>

<style lang="postcss" scoped>
</style>