<script setup lang="ts">

/*
const props = defineProps({
  structure:{
    type: Object,
    default: () => ({ data: {} })
  },
  api_endpoint: {
    type: Object,
    default: () => ({ data: {} })
  }
});
*/

import {ref} from "vue";

const api_endpoint = ref({
  'earthquake.usgs.gov': {
    url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
    method: 'GET',
    color: '#000000',
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
      attr:{
        default: 'lastMonth',
        title: 'End time',
      }
    },
    starttime: {
      type: 'dateTime',
      include: true,
      attr:{
        default: 'today',
        title: 'Start time',
      }
    },
    minmagnitude: {
      type: 'float_span',
      include: true,
      value: 0.0,
      attr: {
        default: 0.0,
        title: 'min. Magnitude',
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
        title: 'max. Magnitude',
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
        title: 'min. Longitude',
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
        title: 'max. Longitude',
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
        title: 'min. Latitude',
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
        title: 'max. Latitude',
        min: -90.0,
        max: 90.0,
      },
    },
  },
});


const api_endpoint_name = computed(() => {
  const keys = Object.keys(api_endpoint.value);
  return keys.length > 0 ? keys[0] : '';
});

const getAttr = (key) => {
  return structure.value.requestOptions[key] ? structure.value.requestOptions[key].attr : null;
};

function getDate(key: string): Date {
  const today = new Date();

  switch(key) {
    case 'today':
      return today;

    case 'lastWeek':
      return new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7);

    case 'lastMonth':
      return new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());

    case 'lastYear':
      return new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());

    default:
      throw new Error("Invalid key");
  }
}

const formattedItems = computed(() => {
  const items = reactive([]);
  console.log(api_endpoint_name.value);
  for (const key in api_endpoint.value[api_endpoint_name.value].params) {
    const paramValue = api_endpoint.value[api_endpoint_name.value].params[key];
    if (paramValue !== '' && structure.value.requestOptions[key]) {
      const option = structure.value.requestOptions[key];
      if (option.attr && option.attr.title) {
        let item = [];
        // First column: title
        item.push(option.attr.title);

        // Second column: data based on type
        if (option.type === 'dateTime') {
          item.push({ type: option.type, value: option.value, default: option.attr.default});
        }

        else if (option.type === 'float_span') {
          item.push({
            type: option.type,
            value: option.attr.default,
            min: option.attr.min,
            max: option.attr.max
          });
        }

        else if (option.type === 'string') {
          item.push({
            type: option.type,
            value: option.attr.default,
          });
        }

        else {
          // Handle other types or add a placeholder if necessary
          item.push('Other data or placeholder');
        }

        items.push(item);
      }
    }
  }
  console.log(items)
  return items;
});

const maxWidth = ref('100px');

onMounted(() => {
  let maxContentLength = 0;
  formattedItems.value.forEach(item => {
    maxContentLength = Math.max(maxContentLength, item[0].length);
  });
  maxWidth.value = `${Math.min(10 * maxContentLength, 300)}px`;
});

</script>


<template>
  <PrimeAccordion>
    <PrimeAccordionTab>
      <template v-slot:header>
        <div class="header-content">
          {{ api_endpoint_name }}
        </div>
        <PrimeColorPicker v-model="api_endpoint[api_endpoint_name].color" />
      </template>
      <div class="grid-container">
        <div v-for="(item, index) in formattedItems" :key="index" class="flex align-items-center p-2 grid-row">
          <!-- First column: Title -->
          <div class="grid-column-title" :style="{ flexBasis: maxWidth, flexGrow: 0, flexShrink: 0 }"><b>{{ item[0] }}</b></div>
          <!-- Second column: Content -->
          <div class="grid-column-content" style="flex-grow: 1; flex-shrink: 1; padding-left: 10px;">
            <div v-if="item[1].type === 'dateTime'">
              <PrimeCalendar showIcon iconDisplay="input" v-model="item[1].value" :defaultDate="getDate(item[1].default )" />
            </div>
            <div v-else-if="item[1].type === 'float_span'">
              <PrimeSlider v-model="item[1].value" :min="item[1].min" :max="item[1].max" />
            </div>
            <div v-else-if="item[1].type === 'string'">
              <PrimeInputText :style="{ flexBasis: maxWidth }" v-model="item[1].value" />
            </div>
            <div v-else>
              {{ item[1] }}
            </div>
          </div>
        </div>
      </div>
    </PrimeAccordionTab>
  </PrimeAccordion>
</template>




<style scoped>
.grid-container {
  width: 100%;
}

.grid-row {
  display: flex;
  align-items: center;
  height: 50px;
}

.grid-column-title {
  flex-grow: 0;
  flex-shrink: 0;
}

.grid-column-content {
  flex-grow: 1;
  flex-shrink: 1;
  padding-left: 10px;
}

/* Adjusting input width */
.grid-column-content input {
  width: 100%;
  max-width: 100%;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.color-indicator {
  width: 30px; /* Adjust as needed */
  height: 30px; /* Adjust as needed */
  background-color: red; /* The color you want to display */
  border-radius: 7px;
}
</style>