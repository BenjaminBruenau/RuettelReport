<script setup lang="ts">

import ApiEndpointsManager from "~/components/queryBuilder/ApiEndpointsManager.vue";
import {ref} from "vue";

const default_api_endpoints = ref({
  api_endpoints: {
    'earthquake.usgs.gov': {
      url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
      method: 'GET',
      color:'#ff0000',
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
    },
    'earthquakes.eu.database': {
      url: 'https://earthquake.eu/query',
      method: 'GET',
      color: '#088F8F',
      params: {
        format: '_format',
        starttime: '_starttime',
        endtime: '_endtime',
        minmagnitude: '_minmagnitude',
        maxmagnitude: '_maxmagnitude',
        minlongitude: '_minlongitude',
        maxlongitude: '_maxlongitude',
        minlatitude: '_minlatitude',
        maxlatitude: '_maxlatitude',
      },
    },
  },
});

</script>

<template>
  <PrimePanel header="API Endpoint Manager">
    <ApiEndpointsManager :initialEndpoints="{ api_endpoints: default_api_endpoints.api_endpoints }" />
  </PrimePanel>
</template>

<style>
</style>

