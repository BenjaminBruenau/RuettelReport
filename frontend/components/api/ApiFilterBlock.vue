<script setup lang="ts">


import {onMounted} from "vue";

const props = defineProps({
  structure:{
    type: Object,
    default: () => ({ data: {} })
  },
  project_settings: {
    type: Object,
    default: () => ({ data: {} })
  },
  index: {
    type: Number,
    default: 0
  }
});


import {ref} from "vue";
import {set} from "vue-demi";
import {EventBus} from "~/components/event-bus";

const selectedApiEndpoint = computed(() => {
  const keys = Object.keys(props.project_settings.api_endpoints);
  if (props.index < keys.length) {
    const key = keys[props.index];
    return { key, endpoint: props.project_settings.api_endpoints[key] };
  }
  return { key: null, endpoint: null };
});

const apiEndpointName = computed(() => selectedApiEndpoint.value.key);



function getDate(key: string): Date {
  const today = new Date();

  switch(key) {
    case 'case_today':
      return today;
    case 'case_lastWeek':
      return new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7);
    case 'case_lastMonth':
      return new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    case 'case_lastYear':
      return new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());
    default:
      console.error("Invalid key provided:", key);
      return today;
  }
}


const formattedItems = computed(() =>{

  const endpoint = selectedApiEndpoint.value.endpoint;

  const items = reactive([]);

  for (const key in endpoint.params) {
    const paramValue = endpoint.params[key];

    if (paramValue !== '' && props.structure.requestOptions[key]) {
      const option = props.structure.requestOptions[key];
      if (option.attr && option.attr.title) {
        let item = [];
        // First column: title
        item.push(option.attr.title);

        let api_index = props.index;
        let api_color = endpoint.color;

        // Second column: data based on type
        if (option.type === 'dateTime') {
          item.push({
            type: option.type,
            value: option.value,
            default: option.attr.default,
            index: api_index,
            color:api_color,
          });
        }

        else if (option.type === 'float_span') {
          item.push({
            type: option.type,
            value: option.attr.default,
            min: option.attr.min,
            max: option.attr.max,
            index: api_index,
            color:api_color,
          });
        }

        else if (option.type === 'string') {
          item.push({
            type: option.type,
            value: option.attr.default,
            index: api_index,
            color:api_color,
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
  return items;
});

const maxWidth = ref('100px');

onMounted(() => {

  let maxContentLength = 0;
  formattedItems.value.forEach(item => {
    maxContentLength = Math.max(maxContentLength, item[0].length);
  });
  maxWidth.value = `${Math.min(10 * maxContentLength, 300)}px`;

  setColor(props.index, selectedApiEndpoint.value.endpoint?.color);

  resetToDefault();


});



function entferne_rauten(text){
  return text.replace("#", "")
}



function setColor(index, color) {
  const colorVarName = `--api-color-${index + 1}`;
  document.documentElement.style.setProperty(colorVarName, '#' + entferne_rauten(color));
}

const rangeClass = (props, parent, index, color) => {
  setColor(index,color);
  return [
    {
      'bg-api_color_1': index === 0,
      'bg-api_color_2': index === 1,
      'bg-api_color_3': index === 2,
      'bg-api_color_4': index === 3,
      'bg-api_color_5': index === 4,
      'bg-api_color_6': index === 5,
      'bg-api_color_7': index === 6,
      'bg-api_color_8': index === 7,
      'bg-api_color_9': index === 8,
      'bg-api_color_10': index === 9,
    },
  ];
};

const startHandlerClass = (props, parent, index) => {
  return [
    {
      'bg-api_color_1 border-api_color_1': index === 0,
      'bg-api_color_2  border-api_color_2': index === 1,
      'bg-api_color_3  border-api_color_3': index === 2,
      'bg-api_color_4  border-api_color_4': index === 3,
      'bg-api_color_5 border-api_color_5': index === 4,
      'bg-api_color_6  border-api_color_6': index === 5,
      'bg-api_color_7 border-api_color_7': index === 6,
      'bg-api_color_8  border-api_color_8': index === 7,
      'bg-api_color_9  border-api_color_9': index === 8,
      'bg-api_color_10 border-api_color_10': index === 9,
    },
  ];
};


watch(
    () => selectedApiEndpoint.value.endpoint?.color,
    (newColor) => {
      setColor(props.index, newColor);
    }
);


const resetToDefault = () => {
  for (const key in props.structure.requestOptions) {
    const option = props.structure.requestOptions[key];
    if (option.attr && option.attr.default !== undefined) {
      if (option.type === 'dateTime') {
        set(option, 'value', getDate(option.attr.default));
      } else {
        set(option, 'value', option.attr.default);
      }
    }
  }
};

const handleReplayClick = (event: MouseEvent) => {
  event.stopPropagation();
  resetToDefault();
};

const minLat = ref(props.structure.requestOptions.minlatitude.value);
const maxLat = ref(props.structure.requestOptions.maxlatitude.value);
const minLon = ref(props.structure.requestOptions.minlongitude.value);
const maxLon = ref(props.structure.requestOptions.maxlongitude.value);

// Beobachten Sie die Werte der Slider
watch([minLat, maxLat, minLon, maxLon], ([newMinLat, newMaxLat, newMinLon, newMaxLon]) => {
  console.log('Slider-Werte geändert:', newMinLat, newMaxLat, newMinLon, newMaxLon);
  updateRectangle(newMinLat, newMaxLat, newMinLon, newMaxLon);
});


function updateRectangle(minLat, maxLat,minLon, maxLon){
  EventBus.emit('add-rectangle', {
    id: 'neuesRechteck',
    minLatitude: minLat,
    maxLatitude: maxLat,
    minLongitude: minLon,
    maxLongitude: maxLon,
    color: selectedApiEndpoint.value.endpoint?.color
  });
  console.log(minLat,maxLat,minLon, maxLon);
}


</script>


<template>
  <PrimeAccordion>
    <PrimeAccordionTab>
      <template v-slot:header>
        <div class="header-content">
          {{ apiEndpointName }}
        </div>
        <PrimeButton icon="pi pi-replay" class="mr-2" @click="handleReplayClick"></PrimeButton>

      </template>
      <div class="grid-container">
        <div v-for="(item, index) in formattedItems" :key="index" class="flex align-items-center p-2 grid-row">
          <!-- First column: Title -->
          <div class="grid-column-title" :style="{ flexBasis: maxWidth, flexGrow: 0, flexShrink: 0 }"><b>{{ item[0] }}</b></div>
          <!-- Second column: Content -->
          <div class="grid-column-content" style="flex-grow: 1; flex-shrink: 1; padding-left: 10px;">
            <div v-if="item[1].type === 'dateTime'">
              <PrimeCalendar showIcon iconDisplay="input" v-model="item[1].value" :defaultDate="getDate(item[1].default )"/>
            </div>
            <div v-else-if="item[1].type === 'float_span'">
              <PrimeInputText v-model.number="item[1].value" />
              <PrimeSlider v-model="item[1].value" :min="item[1].min" :max="item[1].max" :pt="
              {
                range: ({ props, parent }) => ({class: rangeClass(props, parent, item[1].index, item[1].color)}),
                handle: ({ props, parent }) => ({class: startHandlerClass(props, parent, item[1].index)})
              }
            "/>
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
  margin-bottom: 10px;
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
  width: 30px;
  height: 30px;
  background-color: red;
  border-radius: 7px;
}

:root {
  --api-color-1: #000000;
  --api-color-2: #000000;
  --api-color-3: #000000;
  --api-color-4: #000000;
  --api-color-5: #000000;
  --api-color-6: #000000;
  --api-color-7: #000000;
  --api-color-8: #000000;
  --api-color-9: #000000;
  --api-color-10: #000000;
}

</style>