<script setup lang="ts">
import { computed, ref } from 'vue';
import {set} from "vue-demi";
import {FilterMatchMode} from "primevue/api";

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
    'earthquakes.eu.database': {
      url: 'https://earthquake.eu/query',
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

const default_api_endpointStructure =ref({
  name: 'New Endpoint',
  url: '',
  method:'',
  params: {
    format: '',
    starttime: '',
    endtime: '',
    minmagnitude: '',
    maxmagnitude: '',
    minlongitude: '',
    maxlongitude: '',
    minlatitude: '',
    maxlatitude: '',
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
const originalEndpointName = ref('');
const temporaryEditedParams = ref([]);

const openEditDialog = (endpoint) => {
  editedEndpoint.value = { ...endpoint };
  originalEndpointName.value = endpoint.name;
  editDialogHeader.value = `Edit ${endpoint.name}`;
  editDialogVisible.value = true;
  temporaryEditedParams.value = Object.entries(api_endpoint_mapping.value.api_endpoints[endpoint.name].params).map(([key, value]) => ({ key, value }));
};

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

    if (originalEndpointName.value !== newEndpointName) {
      const updatedEndpoint = {
        ...api_endpoint_mapping.value.api_endpoints[originalEndpointName.value],
        url: editedEndpoint.value.url,
        params: Object.fromEntries(endpointParamsPairs)
      };

      set(api_endpoint_mapping.value.api_endpoints, newEndpointName, updatedEndpoint);

      delete api_endpoint_mapping.value.api_endpoints[originalEndpointName.value];

      const endpointIndex = endpoints.value.findIndex(e => e.name === originalEndpointName.value);
      if (endpointIndex !== -1) {
        endpoints.value.splice(endpointIndex, 1, { ...updatedEndpoint, name: newEndpointName });
      }
    } else {
      const endpoint = api_endpoint_mapping.value.api_endpoints[newEndpointName];
      endpoint.url = editedEndpoint.value.url;
      endpoint.params = Object.fromEntries(endpointParamsPairs);
    }
    originalEndpointName.value = '';
    temporaryEditedParams.value = [];

    if (newEndpointName) {
      editedEndpoint.value = {...api_endpoint_mapping.value.api_endpoints[newEndpointName]};
      temporaryEditedParams.value = Object.entries(editedEndpoint.value.params).map(([key, value]) => ({ key, value }));
    }
  }

};

const filters = ref({
  key: { value: null, matchMode: FilterMatchMode.STARTS_WITH },
  value: { value: null, matchMode: FilterMatchMode.STARTS_WITH }
});

const addNewEndpoint = () => {
  const newEndpoint = JSON.parse(JSON.stringify(default_api_endpointStructure.value));
  newEndpoint.name = `New Endpoint ${formatDate(Date.now())}`;
  set(api_endpoint_mapping.value.api_endpoints, newEndpoint.name, newEndpoint);
  openEditDialog(newEndpoint);
};

const formatDate = (date) => {
  const d = new Date(date);
  let month = '' + (d.getMonth() + 1);
  let day = '' + d.getDate();
  const year = d.getFullYear();

  if (month.length < 2)
    month = '0' + month;
  if (day.length < 2)
    day = '0' + day;

  return [year, month, day].join('.');
};

const downloadSelectedEndpoints = () => {
  // Objekt erstellen, das die API-Namen als Schlüssel und die Daten als Werte enthält
  const dataToDownload = selectedEndpoints.value.reduce((acc, endpoint) => {
    const endpointName = endpoint.name; // API-Name
    acc[endpointName] = api_endpoint_mapping.value.api_endpoints[endpointName];
    return acc;
  }, {});

  const jsonString = JSON.stringify(dataToDownload, null, 2);
  const blob = new Blob([jsonString], { type: 'application/json' });
  const url = URL.createObjectURL(blob);

  const link = document.createElement('a');
  link.href = url;
  link.download = 'selected_endpoints.json';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};


const toast = useToast();
const uploadedFiles = ref([]);

const onAdvancedUpload = (event) => {
  uploadedFiles.value = event.files;
  toast.add({ severity: 'info', summary: 'Success', detail: 'File Uploaded', life: 3000 });
};

</script>

<template>
  <div>
    <PrimeSplitter>
      <h3>API - Endpoints</h3>
      <PrimeSplitterPanel size="50">
        <div class="splitter-container">
          <div class="left-panel">
            <PrimeDataTable v-model:selection="selectedEndpoints" :value="endpoints" dataKey="name" selectionMode="checkbox">
              <template #header>
                <div class="header-buttons">
                  <PrimeButton label="+ Endpoint" @click="addNewEndpoint" class="p-button-outlined" />
                  <div class="spacer"></div>
                  <PrimeButton label="Download" icon="pi pi-download" @click="downloadSelectedEndpoints" class="p-button-outlined" />
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
            <div class="file-upload-container">
              <PrimeToast />
              <PrimeFileUpload name="demo[]" url="/api/upload" @upload="onAdvancedUpload" :multiple="true" accept="application/json" :maxFileSize="1000000">
                <template #empty>
                  <p>Drag and drop JSON files to here to upload.</p>
                </template>
              </PrimeFileUpload>
            </div>
          </div>
        </div>
      </PrimeSplitterPanel>
      <PrimeSplitterPanel size="50">
          <PrimeDataTable :value="temporaryEditedParams" dataKey="key" paginator :rows="5" :rowsPerPageOptions="[5, 10, 20, 50]" tableStyle="min-width: 50rem" v-model:filters="filters">          <template #header>
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
            <PrimeColumn field="key" header="RüttelReport API ref." :filter="true" filterPlaceholder="Filter" filterMatchMode="startsWith">
              <template #filter="{ filterModel, filterCallback }">
                <PrimeInputText v-model="filterModel.value" type="text" @keydown.enter="filterCallback()" class="p-column-filter" />
              </template>
            </PrimeColumn>
            <PrimeColumn header="Custom API ref." :filter="true" filterField="value" filterMatchMode="startsWith">
              <template #filter="{ filterModel, filterCallback }">
                <PrimeInputText v-model="filterModel.value" type="text" @keydown.enter="filterCallback()" class="p-column-filter" />
              </template>
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

.header-buttons {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.spacer {
  flex: 1;
}

</style>
