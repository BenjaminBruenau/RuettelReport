<script setup lang="ts">
import { computed, ref } from 'vue';
import {set} from "vue-demi";

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

const originalEndpointName = ref('');

const temporaryEditedParams = ref([]);

const openEditDialog = (endpoint) => {
  editedEndpoint.value = { ...endpoint };
  originalEndpointName.value = endpoint.name;
  editDialogHeader.value = `Edit ${endpoint.name}`;
  editDialogVisible.value = true;

  // Initialisieren der temporären Parameter
  temporaryEditedParams.value = Object.entries(api_endpoint_mapping.value.api_endpoints[endpoint.name].params).map(([key, value]) => ({ key, value }));
};

const editedEndpointParams = computed(() => {
  if (!editedEndpoint.value || !api_endpoint_mapping.value.api_endpoints[editedEndpoint.value.name]) {
    return [];
  }
  return Object.entries(api_endpoint_mapping.value.api_endpoints[editedEndpoint.value.name].params).map(([key, value]) => ({ key, value }));
});

const deleteSelectedEndpoints = () => {
  selectedEndpoints.value.forEach((endpoint) => {
    delete api_endpoint_mapping.value.api_endpoints[endpoint.name];
  });
  selectedEndpoints.value = [];
};

const saveChanges = () => {
  if (editedEndpoint.value) {
    const newEndpointName = editedEndpoint.value.name;
    const endpointParamsPairs = temporaryEditedParams.value.map(param => [param.key, param.value]);

    // Prüfen, ob der Name geändert wurde
    if (originalEndpointName.value !== newEndpointName) {
      // Aktualisieren Sie die API-Endpunkte mit dem neuen Namen
      const updatedEndpoint = {
        ...api_endpoint_mapping.value.api_endpoints[originalEndpointName.value],
        url: editedEndpoint.value.url,
        params: Object.fromEntries(endpointParamsPairs)
      };

      // Fügen Sie den aktualisierten Endpunkt unter dem neuen Namen hinzu
      set(api_endpoint_mapping.value.api_endpoints, newEndpointName, updatedEndpoint);

      // Entfernen Sie den alten Endpunkt
      delete api_endpoint_mapping.value.api_endpoints[originalEndpointName.value];

      // Aktualisieren Sie auch die Liste der Endpunkte auf der linken Seite
      const endpointIndex = endpoints.value.findIndex(e => e.name === originalEndpointName.value);
      if (endpointIndex !== -1) {
        endpoints.value.splice(endpointIndex, 1, { ...updatedEndpoint, name: newEndpointName });
      }
    } else {
      // Aktualisieren Sie nur die URL und Parameter des bestehenden Endpunkts
      const endpoint = api_endpoint_mapping.value.api_endpoints[newEndpointName];
      endpoint.url = editedEndpoint.value.url;
      endpoint.params = Object.fromEntries(endpointParamsPairs);
    }

    // Setzen Sie den ursprünglichen Namen und die temporären Parameter zurück
    originalEndpointName.value = '';
    temporaryEditedParams.value = [];
  }
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
        <PrimeDataTable :value="temporaryEditedParams" dataKey="key" paginator :rows="5" :rowsPerPageOptions="[5, 10, 20, 50]" tableStyle="min-width: 50rem">
          <template #header>
            <div class="header-buttons">
              <PrimeInputGroup>
                <PrimeInputGroupAddon>Name</PrimeInputGroupAddon>
                <PrimeInputText v-model="editedEndpoint.name" />
              </PrimeInputGroup>
              <PrimeInputGroup>
                <PrimeInputGroupAddon>URL</PrimeInputGroupAddon>
                <PrimeInputText v-model="editedEndpoint.url" />
              </PrimeInputGroup>
              <div class="spacer"></div>
              <PrimeButton label="Save" @click="saveChanges" class="p-button-outlined" icon="pi pi-save" />
            </div>
          </template>
          <PrimeColumn field="key" header="RüttelReport API ref." sortable style="width: 25%"></PrimeColumn>
          <PrimeColumn header="Custom API ref.">
            <template #body="slotProps">
              <PrimeInputText v-model="slotProps.data.value" />
            </template>
          </PrimeColumn>
        </PrimeDataTable>
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
.options-table {
  width: 100%;
  border-collapse: collapse;
}

.param-key {
  white-space: nowrap;
  padding-right: 1em; /* Abstand zwischen den Spalten */
}

.param-value {
  width: 100%;
}

.header-buttons {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.spacer {
  flex: 1;
}

</style>
