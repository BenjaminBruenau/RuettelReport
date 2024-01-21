<script setup lang="ts">
import { computed, ref } from 'vue';
import {set} from "vue-demi";
import {FilterMatchMode} from "primevue/api";

const props = defineProps({
  initialEndpoints: {
    type: Object,
    default: () => ({ api_endpoints: {} })
  }
});

const toast = useToast();

const selectedEndpoints = ref([]);
const editDialogVisible = ref(false);
const editDialogHeader = ref('');
const editedEndpoint = ref({});
const originalEndpointName = ref('');
const temporaryEditedParams = ref([]);
const apiSelected = ref(false);

const default_api_endpointStructure =ref({
  name: 'New Endpoint',
  url: '',
  method:'',
  color:'#ffffff',
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

const endpoints = computed(() => {
  if (!props.initialEndpoints || !props.initialEndpoints.api_endpoints) {
    return [];
  }
  return Object.entries(props.initialEndpoints.api_endpoints).map(([name, value]) => ({
    name,
    ...value,
    checked: false,
  }));
});


onBeforeMount(() => {
  //copyDefaultEndpointsToMapping();
});

const openEditDialog = (endpoint) => {
  apiSelected.value = true;
  editedEndpoint.value = { ...endpoint };
  originalEndpointName.value = endpoint.name;
  editDialogHeader.value = `Edit ${endpoint.name}`;
  editDialogVisible.value = true;
  temporaryEditedParams.value = Object.entries(props.initialEndpoints.api_endpoints[endpoint.name].params).map(([key, value]) => ({ key, value }));
};

const deleteSelectedEndpoints = () => {
  selectedEndpoints.value.forEach((endpoint) => {
    delete props.initialEndpoints.api_endpoints[endpoint.name];
  });
  selectedEndpoints.value = [];
  toast.add({ severity: 'warn', summary: 'Gelöscht', detail: 'Ausgewählte Endpoints gelöscht', life: 3000 });
};

const saveChanges = () => {
  if (editedEndpoint.value) {

    const newEndpointName = editedEndpoint.value.name;
    const endpointParamsPairs = temporaryEditedParams.value.map(param => [param.key, param.value]);

    const colorEntry = temporaryEditedParams.value.find(entry => entry.key === 'Color');
    editedEndpoint.value.color = colorEntry ? colorEntry.value : editedEndpoint.value.color;

    console.log(editedEndpoint.value.color);

    if (originalEndpointName.value !== newEndpointName) {
      const updatedEndpoint = {
        ...props.initialEndpoints.api_endpoints[originalEndpointName.value],
        url: editedEndpoint.value.url,
        color: editedEndpoint.value.color,
        params: Object.fromEntries(endpointParamsPairs),
        mappingRules: editedEndpoint.value.mappingRules
      };

      set(props.initialEndpoints.api_endpoints, newEndpointName, updatedEndpoint);

      delete props.initialEndpoints.api_endpoints[originalEndpointName.value];

      const endpointIndex = endpoints.value.findIndex(e => e.name === originalEndpointName.value);
      if (endpointIndex !== -1) {
        endpoints.value.splice(endpointIndex, 1, { ...updatedEndpoint, name: newEndpointName });
      }
    } else {
      const endpoint = props.initialEndpoints.api_endpoints[newEndpointName];
      endpoint.url = editedEndpoint.value.url;
      endpoint.color = editedEndpoint.value.color;
      endpoint.params = Object.fromEntries(endpointParamsPairs);
      endpoint.mappingRules = editedEndpoint.value.mappingRules;
    }
    originalEndpointName.value = '';
    temporaryEditedParams.value = [];

    if (newEndpointName) {
      editedEndpoint.value = {...props.initialEndpoints.api_endpoints[newEndpointName]};
      temporaryEditedParams.value = Object.entries(editedEndpoint.value.params).map(([key, value]) => ({ key, value }));
    }
    toast.add({ severity: 'success', summary: 'Gespeichert', detail: 'Änderungen gespeichert', life: 3000 });
  }

  apiSelected.value = false;
  editedEndpoint.value = {};
  originalEndpointName.value = '';
  temporaryEditedParams.value = [];

  console.log(JSON.stringify(props.initialEndpoints,null,2));

};

const filters = ref({
  key: { value: null, matchMode: FilterMatchMode.STARTS_WITH },
  value: { value: null, matchMode: FilterMatchMode.STARTS_WITH }
});

const addNewEndpoint = () => {
  const newEndpoint = JSON.parse(JSON.stringify(default_api_endpointStructure.value));
  newEndpoint.name = `New Endpoint ${formatDate(Date.now())}`;
  set(props.initialEndpoints.api_endpoints, newEndpoint.name, newEndpoint);
  openEditDialog(newEndpoint);
  toast.add({ severity: 'success', summary: 'Erfolg', detail: 'Neuer Endpoint hinzugefügt', life: 3000 });
};

const formatDate = (date) => {
  const d = new Date(date);
  let month = '' + (d.getMonth() + 1);
  let day = '' + d.getDate();
  const year = d.getFullYear();
  const time = d.getTime();

  if (month.length < 2)
    month = '0' + month;
  if (day.length < 2)
    day = '0' + day;

  return [year, month, day, time].join('.');
};

const downloadSelectedEndpoints = () => {
  const dataToDownload = selectedEndpoints.value.reduce((acc, endpoint) => {
    const endpointName = endpoint.name; // API-Name
    acc[endpointName] = props.initialEndpoints.api_endpoints[endpointName];
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


const onAdvancedUpload = async (event) => {
  for (let file of event.files) {
    const reader = new FileReader();

    reader.onload = (e) => {
      const result = e.target.result;
      if (typeof result === "string") {
        try {
          const fileContent = JSON.parse(result);

          for (const [key, value] of Object.entries(fileContent)) {
            set(props.initialEndpoints.api_endpoints, key, value);
          }

          toast.add({ severity: 'info', summary: 'Success', detail: 'Endpoints Updated', life: 3000 });
        } catch (error) {
          toast.add({ severity: 'error', summary: 'Error', detail: 'Invalid JSON file', life: 3000 });
        }
      }
    };
    reader.readAsText(file);
  }
};


const updateColor = (endpointName, color) => {
  console.log(JSON.stringify(props.initialEndpoints.api_endpoints,null,2));
  if (props.initialEndpoints.api_endpoints[endpointName]) {
    props.initialEndpoints.api_endpoints[endpointName].color = color;
  }
};

</script>

<template>
  <div class="flex-container">
    <PrimeScrollPanel class="flex-item">
      <PrimeSplitter   layout="vertical">
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
                    <div class="spacer_vert"></div>
                    <PrimeButton label="Del" class="p-button-danger p-button-outlined" @click="deleteSelectedEndpoints" />
                  </div>
                </template>
                <PrimeColumn selectionMode="multiple" style="width:3em"></PrimeColumn>
                <!--
                <PrimeColumn header="Color" style="width:6em">
                  <template #body="slotProps">
                    <PrimeColorPicker
                        v-model="slotProps.data.color"
                        @input="() => updateColor(slotProps.data.name, slotProps.data.color)"
                    />
                  </template>
                </PrimeColumn>
                -->
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
          <div v-if="apiSelected">
            <PrimeDataTable :value="temporaryEditedParams" dataKey="key"  tableStyle="min-width: 50rem" v-model:filters="filters"> <!--paginator :rows="5" :rowsPerPageOptions="[5, 10, 20, 50]"-->
              <template #header>
                <div class="header-buttons">
                  <div class="input-fields">
                    <PrimeInputGroup>
                      <PrimeInputGroupAddon>API-Name</PrimeInputGroupAddon>
                      <PrimeInputText v-model="editedEndpoint.name" />
                    </PrimeInputGroup>
                    <PrimeInputGroup>
                      <PrimeInputGroupAddon>API-Endpoint</PrimeInputGroupAddon>
                      <PrimeInputText v-model="editedEndpoint.url" />
                    </PrimeInputGroup>
                    <PrimeInputGroup>
                      <PrimeInputGroupAddon>API-Color</PrimeInputGroupAddon>
                      <PrimeColorPicker v-model="editedEndpoint.color" />
                    </PrimeInputGroup>
                    <div style="height: 5px"></div>
                    <h3>Mapping Rules:</h3>
                    <PrimeTextarea v-model="editedEndpoint.mappingRules" autoResize rows="5" cols="30" />
                  </div>
                  <div class="spacer"></div>
                  <PrimeButton label="Save" @click="saveChanges" class="p-button-outlined" icon="pi pi-save" :disabled="!apiSelected" />
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
          </div>
        </PrimeSplitterPanel>
      </PrimeSplitter>
    </PrimeScrollPanel>

  </div>
</template>

<style>

.flex-container {
  display: flex;
  flex-direction: column;
  height: 100%; /* Nutzt die Höhe des übergeordneten Elements */
}

.flex-item {
  flex-grow: 1; /* Ermöglicht dem ScrollPanel, den verfügbaren Platz auszufüllen */
  overflow: auto; /* Stellt sicher, dass der Inhalt scrollbar ist, wenn er die Höhe übersteigt */
}

.scroll-panel-container {
  height: 100%; /* Verwenden Sie die volle Höhe des Viewports */
  width: 100%; /* Verwenden Sie die volle Breite */
}

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

/* Add this to make the left panel flexible */
.splitter-container > div:first-child {
  flex: 1;
}


InputGroupAddon {
  flex-shrink: 0;
}



.input-fields {
  display: flex;
  flex-direction: column;
}

.spacer {
  flex: 1;
}
.spacer_vert{
  width: 10px;
}

.header-buttons {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
}

.input-fields {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  margin-right: 1rem;
}

.placeholder-text {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  text-align: center;
}

.input-fields PrimeInputGroup {
  margin-bottom: 10px;
}

.input-fields PrimeInputGroup:last-child {
  margin-bottom: 0;
}

</style>
