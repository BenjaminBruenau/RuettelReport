<script setup lang="ts">

import {ref} from "vue";

const props = defineProps({
  project_settings: {
    type: Object,
    default: () => ({data: {}})
  },
});

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

  const newApiFilterBlocks = Object.keys(props.project_settings.api_endpoints).map((apiKey, index) => {
    const existingBlock = apiFilterBlocks.value.find(block => block.index === index);

    const blockStructure = existingBlock ? existingBlock.structure : ref(JSON.parse(JSON.stringify(structure.value)));

    return {
      index,
      structure: blockStructure,
      project_settings: ref(JSON.parse(JSON.stringify(props.project_settings)))
    };
  });

  if (JSON.stringify(apiFilterBlocks.value) !== JSON.stringify(newApiFilterBlocks)) {
    apiFilterBlocks.value = newApiFilterBlocks;
  }
};

watch(() => props.project_settings.api_endpoints, () => {
  createApiFilterBlocks();
}, { deep: true });


createApiFilterBlocks();

const handleUpdateRequestOptions = (payload) => {
  const { index, newValues } = payload;

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

function runButton(){
  // HIER AN SERVER SCHICKEN UM REPORT ZU ERHALTEN!
  console.log(JSON.stringify(apiFilterBlocks.value,null,2));
}

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
   <PrimeVirtualScroller :items="apiFilterBlocks" itemSize="50">
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