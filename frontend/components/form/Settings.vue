<script setup lang="ts">

import {onMounted, ref} from "vue";

const props = defineProps({
  project_settings: {
    type: Object,
    default: () => ({data: {}})
  },
  premium: {
    type: Boolean,
    default: false
  }
});





const myColor = ref('black');
watch(myColor, (color) => {
  const contrastColor = getContrastYIQ(color) === 'light' ? '#FFFFFF' : '#000000';
  document.documentElement.style.setProperty('--contrast-text', contrastColor);
});

const panelClass = (props, parent, index) => {
  return [
    {
      'border-primary bg-tile_color_light dark:bg-tile_color_dark text-primary': parent.state.d_activeIndex === index,
      'border-b_color_light dark:border-b_color_dark bg-tile_color_light dark:bg-tile_color_dark text-textColor_light dark:text-textColor_dark': parent.state.d_activeIndex !== index,
    }
  ];
};

function getContrastYIQ(hexcolor){
  hexcolor = hexcolor.replace('#', '');

  var r = parseInt(hexcolor.substr(0,2),16);
  var g = parseInt(hexcolor.substr(2,2),16);
  var b = parseInt(hexcolor.substr(4,2),16);

  var yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;

  console.log(yiq);

  return (yiq >= 90) ? 'light' : 'black';
}


const darkMode = ref(false)

const toggleDarkMode = (newValue: boolean) => {
  useColorMode().preference = darkMode.value ? 'dark' : 'light'
}


const color_primary_light = ref();
const color_primary_dark = ref();
const gradient_from_light = ref();
const gradient_to_light = ref();
const gradient_from_dark = ref();
const gradient_to_dark = ref();

function unifyHex(text){
  return '#'+text.replace("#", "")
}

onMounted(() => {
  console.log(JSON.stringify(props.project_settings,null,2));

  color_primary_light.value = props.project_settings.theme?.primary_color_light;
  color_primary_dark.value = props.project_settings.theme?.primary_color_dark;
  gradient_from_light.value = props.project_settings.theme?.gradient_from_light;
  gradient_to_light.value = props.project_settings.theme?.gradient_to_light;
  gradient_from_dark.value = props.project_settings.theme?.gradient_from_dark;
  gradient_to_dark.value = props.project_settings.theme?.gradient_to_dark;

  color_primary_light.value = unifyHex(document.documentElement.style.getPropertyValue('--color-primary_light'));
  color_primary_dark.value = unifyHex(document.documentElement.style.getPropertyValue('--color-primary_dark'));
  gradient_from_light.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_from_light'));
  gradient_to_light.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_from_light'));
  gradient_from_dark.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_from_dark'));
  gradient_to_dark.value = unifyHex(document.documentElement.style.getPropertyValue('--gradient_to_dark'));
});

watch(color_primary_light, (newColor) => {
  document.documentElement.style.setProperty('--color-primary_light', unifyHex(newColor));
  const contrastColor = getContrastYIQ(unifyHex(newColor)) === 'black' ? '#FFFFFF' : '#000000';
  document.documentElement.style.setProperty('--contrast-text_light', contrastColor);
});

watch(color_primary_dark, (newColor) => {
  document.documentElement.style.setProperty('--color-primary_dark', unifyHex(newColor));
  const contrastColor = getContrastYIQ(unifyHex(newColor)) === 'black' ? '#FFFFFF' : '#000000';
  document.documentElement.style.setProperty('var(--contrast-text_dark)', contrastColor);
  document.documentElement.style.setProperty('--color-primary', unifyHex(newColor));
});

watch(gradient_from_light, (newColor) => {
  document.documentElement.style.setProperty('--gradient_from_light', unifyHex(newColor));
});

watch(gradient_to_light, (newColor) => {
  document.documentElement.style.setProperty('--gradient_to_light', unifyHex(newColor));
});

watch(gradient_from_dark, (newColor) => {
  document.documentElement.style.setProperty('--gradient_from_dark', unifyHex(newColor));
});

watch(gradient_to_dark, (newColor) => {
  document.documentElement.style.setProperty('--gradient_to_dark', unifyHex(newColor));
});

watch(darkMode,(b)=>{
  if(b){
    document.documentElement.style.setProperty('--color-primary', unifyHex(color_primary_dark.value));
  } else{
    document.documentElement.style.setProperty('--color-primary', unifyHex(color_primary_light.value));
  }
});




//----------- Projekt Management --------------------------------

const users = ref([]); // Hier könnten Sie Ihre initialen Benutzerdaten laden
const selectedUsers = ref([]);
const userDialog = ref(false);
const user = ref({});

const openNew = () => {
  user.value = { id: null, name: '', email: '' };
  userDialog.value = true;
};

const editUser = (userData) => {
  user.value = { ...userData };
  userDialog.value = true;
};

const saveUser = () => {
  const index = users.value.findIndex(u => u.email === user.value.email);

  if (index !== -1) {
    users.value[index] = { ...user.value };
  } else {
    const emailExists = users.value.some(u => u.email === user.value.email);
    if (!emailExists) {
      users.value.push({ ...user.value });
    } else {
      alert('Diese E-Mail-Adresse wird bereits verwendet.');
      return;
    }
  }

  userDialog.value = false;
};

const hideDialog = () => {
  userDialog.value = false;
};

const confirmDeleteSelected = () => {
  users.value = users.value.filter(u => !selectedUsers.value.includes(u));
  selectedUsers.value = [];
};

function  btn_projectSettings_reload(){
  console.log("TODO: RELOAD")
}

const emit = defineEmits(['update-project-users'])

function  btn_projectSettings_save(){
  emit('update-project-users', { users: users });
}




</script>

<template>
  <div class="parent-container">
    <PrimeTabView class="container">
      <!--<PrimeTabPanel header="Account" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
      </PrimeTabPanel>-->
      <PrimeTabPanel header="Endpoint Manager" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
        <ApiEndpointsManager :initialEndpoints="{ api_endpoints: props.project_settings['api_endpoints'] }" />
      </PrimeTabPanel>
      <PrimeTabPanel header="★ Theming" :disabled="!props.premium" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 1)})}">
        <div style="margin:15px">
        <div class="grid-container">

            <div><b>Primary Color</b></div><div></div>
            <div>Dark/Light Mode</div>
            <PrimeInputSwitch v-model="darkMode" @update:model-value="toggleDarkMode"></PrimeInputSwitch>

            <div><b>Primary Color</b></div><div></div>
            <div>Light</div>
            <PrimeColorPicker v-model="color_primary_light"></PrimeColorPicker>
            <div>Dark</div>
            <PrimeColorPicker v-model="color_primary_dark"></PrimeColorPicker>

            <div><b>Gradiant Color</b></div><div></div>
            <div>From - Light</div>
            <PrimeColorPicker v-model="gradient_from_light"></PrimeColorPicker>
            <div>To - Light</div>
            <PrimeColorPicker v-model="gradient_to_light"></PrimeColorPicker>
            <div>From - Dark</div>
            <PrimeColorPicker v-model="gradient_from_dark"></PrimeColorPicker>
            <div>To - Dark</div>
            <PrimeColorPicker v-model="gradient_to_dark"></PrimeColorPicker>
          </div>
        </div>
      </PrimeTabPanel>
      <PrimeTabPanel header="★ Projects" :disabled="!props.premium" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 2)})}">

        <div>
          <div class="card">
            <PrimeToolbar>
              <template #start>
                <PrimeButton label="New" icon="pi pi-plus" @click="openNew" />
                <div style="width: 10px"></div>
                <PrimeButton label="Delete" icon="pi pi-trash" @click="confirmDeleteSelected" :disabled="!selectedUsers || !selectedUsers.length" />
              </template>
              <template #end>
                <PrimeButton label="" icon="pi pi-replay" @click="btn_projectSettings_reload"/>
                <div style="width: 10px"></div>
                <PrimeButton label="Save" icon="pi pi-save" @click="btn_projectSettings_save"/>
              </template>
            </PrimeToolbar>

            <PrimeDataTable :value="users" v-model:selection="selectedUsers" dataKey="id">
              <PrimeColumn selectionMode="multiple"></PrimeColumn>
              <PrimeColumn field="name" header="Name"></PrimeColumn>
              <PrimeColumn field="email" header="Email"></PrimeColumn>
              <PrimeColumn :exportable="false">
                <template #body="slotProps">
                  <PrimeButton icon="pi pi-pencil" @click="editUser(slotProps.data)" />
                </template>
              </PrimeColumn>
            </PrimeDataTable>
          </div>

          <PrimeDialog v-model:visible="userDialog" header="User Details" :modal="true">
            <div class="form-grid">
              <div class="field">
                <label for="name">Name</label>
                <PrimeInputText id="name" v-model="user.name" required />
              </div>
              <div class="field">
                <label for="email">Email</label>
                <PrimeInputText id="email" v-model="user.email" required />
              </div>
            </div>
            <template #footer>
              <PrimeButton label="Cancel" icon="pi pi-times" @click="hideDialog"/>
              <PrimeButton label="Save" icon="pi pi-check" @click="saveUser" />
            </template>
          </PrimeDialog>

        </div>



      </PrimeTabPanel>
    </PrimeTabView>
  </div>
</template>


<style scoped>

.parent-container {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  width: 100%;
}

.container {
  border-radius: 15px;
  width: 100%;
}

:root {
  --color-primary: #7a7a7a;
  --contrast-text: #7a7a7a;
}


.grid-container {
  width: 100%;
}

.grid-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 10px;
  align-items: center;
}

:root {
  --b_color_light:rgb(0,0,0);
  --b_color_dark:rgb(0,0,0);
  --color-primary_light: rgb(0,0,0);
  --color-primary_dark: rgb(0,0,0);
  --gradient_from_light: rgb(0,0,0);
  --gradient_to_light: rgb(0,0,0);
  --gradient_from_dark: rgb(0,0,0);
  --gradient_to_dark: rgb(0,0,0);
  --contrast-text_light: rgb(0,0,0);
  --contrast-text_dark: rgb(0,0,0);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.field {
  display: flex;
  flex-direction: column;
}

.field label {
  margin-bottom: 5px;
}
</style>

<style lang="postcss">

html {
  @apply bg-gradient-to-r from-mainColor_1_1_light to-mainColor_1_2_light
  dark:bg-gradient-to-r dark:from-mainColor_1_1_dark dark:to-mainColor_1_2_dark
  text-text-light dark:text-text-dark;
}



</style>