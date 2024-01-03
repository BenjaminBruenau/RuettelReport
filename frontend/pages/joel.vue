<script setup lang="ts">
import { computed, ref } from 'vue';

const struct = ref({
  requestOptions: {
    format: {
      type: 'string',
      include: true,
      default: 'geojson',
      value: 'geojson',
    },
    endtime: {
      type: 'dateTime',
      include: true,
    },
    starttime: {
      type: 'dateTime',
      include: true,
    },
    minmagnitude: {
      type: 'float',
      include: true,
      value: 0.0,
      attr: {
        default: 0.0,
        min: 0.0,
        max: 10.0,
      },
    },
    maxmagnitude: {
      type: 'float',
      include: true,
      value: 10.0,
      attr: {
        default: 10.0,
        min: 0.0,
        max: 10.0,
      },
    },
    minlongitude: {
      type: 'float',
      include: true,
      value: -180.0,
      attr: {
        default: -180.0,
        min: -180.0,
        max: 180.0,
      },
    },
    maxlongitude: {
      type: 'float',
      include: true,
      value: 180.0,
      attr: {
        default: 180.0,
        min: -180.0,
        max: 180.0,
      },
    },
    minlatitude: {
      type: 'float',
      include: true,
      value: -90.0,
      attr: {
        default: -90.0,
        min: -90.0,
        max: 90.0,
      },
    },
    maxlatitude: {
      type: 'float',
      include: true,
      value: 90.0,
      attr: {
        default: 90.0,
        min: -90.0,
        max: 90.0,
      },
    },
  },
});

const api_endpoint_mapping = ref({
  api_endpoints: {
    'earthquake.usgs.gov': {
      url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
      method: 'GET',
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
    'earthquake.usgs.gov2': {
      url: 'https://earthquake.usgs.gov/fdsnws/event/1/query',
      method: 'GET',
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

const endpoints = computed(() => Object.entries(api_endpoint_mapping.value.api_endpoints).map(([name, value]) => ({
  name,
  ...value,
  checked: false,
})));

const selectedEndpoints = ref([]);
const editDialogVisible = ref(false);
const editDialogHeader = ref('');
const editedEndpoint = ref({});
const maxAddonLength = ref(0);
const editedRequestOptions = ref([]);

const openEditDialog = (endpoint) => {
  editedEndpoint.value = endpoint;
  editDialogHeader.value = `Edit ${endpoint.name}`;
  editDialogVisible.value = true;

  editedRequestOptions.value = Object.entries(struct.value.requestOptions).map(([key, value]) => ({
    key,
    ...value,
  }));

  maxAddonLength.value = Math.max(...editedRequestOptions.value.map(option => option.key.length));
};

const deleteSelectedEndpoints = () => {
  selectedEndpoints.value.forEach((endpoint) => {
    delete api_endpoint_mapping.value.api_endpoints[endpoint.name];
  });
  selectedEndpoints.value = [];
};

const addNewEndpoint = () => {
  // Logik zum Hinzufügen eines neuen Endpunkts
};
</script>

<template>
  <div>
    <PrimeSplitter>
      <h3>API - Endpoints</h3>
      <PrimeSplitterPanel size="20">
        <div class="splitter-container">
          <div class="left-panel">
            <PrimeDataTable v-model:selection="selectedEndpoints" :value="endpoints" dataKey="name" selectionMode="checkbox">
              <template #header>
                <div class="header-buttons">
                  <PrimeButton label="+ Endpoint" @click="addNewEndpoint" class="p-button-outlined" />
                  <div class="spacer"></div>
                  <PrimeButton label="Del" class="p-button-danger p-button-outlined" @click="deleteSelectedEndpoints" />
                </div>
              </template>
              <PrimeColumn selectionMode="multiple" style="width:3em"></PrimeColumn>
              <PrimeColumn field="name" header="Endpoint"></PrimeColumn>
              <PrimeColumn style="width:6em">
                <template #body="slotProps">
                  <PrimeButton label="Edit" icon="pi pi-pencil" @click="() => openEditDialog(slotProps.data)" />
                </template>
              </PrimeColumn>
            </PrimeDataTable>
          </div>
        </div>
      </PrimeSplitterPanel>
      <PrimeSplitterPanel size="80">
        <div class="right-panel-container" v-if="editedEndpoint">
          <h4>{{ editDialogHeader }}</h4>

          <PrimeInputGroup>
            <PrimeInputGroupAddon>Name</PrimeInputGroupAddon>
            <PrimeInputText placeholder="API Name" v-model="editedEndpoint.name" />
          </PrimeInputGroup>

          <PrimeInputGroup>
            <PrimeInputGroupAddon>URL</PrimeInputGroupAddon>
            <PrimeInputText placeholder="API URL" v-model="editedEndpoint.url" />
          </PrimeInputGroup>

          <div v-for="(option, index) in editedRequestOptions" :key="index">
            <PrimeInputGroup>
              <PrimeInputGroupAddon :style="{ minWidth: `${maxAddonLength}ch` }">{{ option.key }}</PrimeInputGroupAddon>
              <PrimeInputText placeholder="Enter value" v-model="option.value" />
            </PrimeInputGroup>
          </div>
        </div>
      </PrimeSplitterPanel>
    </PrimeSplitter>
  </div>
</template>

<style>
.header-buttons {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.spacer {
  flex: 1;
}

.splitter-container {
  display: flex;
  justify-content: space-between;
}

.left-panel {
  flex-grow: 1;
}

InputGroupAddon {
  flex-shrink: 0;
}

.right-panel-container {
  margin: 10px;
}
</style>
