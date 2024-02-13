<script setup lang="ts">

import {onMounted, ref} from "vue";
import {useCookie} from "nuxt/app";

const props = defineProps({
  premium: {
    type: Boolean,
    default: false
  }
});


const userStore = useUserStore()
const projectSettingsStore = useProjectSettingsStore()
const projectSettings = storeToRefs(projectSettingsStore)


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




const darkMode = computed({
  get() { return projectSettings.theme.value.default_theme === 'dark' },
  set(isDarkMode) {
    projectSettingsStore.theme.default_theme = isDarkMode ? 'dark' : 'light'
  }
})
const toggleDarkMode = (newValue: boolean) => {
  useColorMode().preference = darkMode.value ? 'dark' : 'light'
}


function unifyHex(text){
  return '#'+text.replace("#", "")
}

onMounted(() => {
  console.log(JSON.stringify(projectSettings,null,2));
  getShareUrl();

});



watch(darkMode,(b)=>{
  console.log('Changing Theme Mode - Dark Mode: ', b)
  if(!b){
    document.documentElement.style.setProperty('--color-primary', unifyHex(projectSettings.theme.value.primary_color_light));
  } else{
    document.documentElement.style.setProperty('--color-primary', unifyHex(projectSettings.theme.value.primary_color_dark));
  }
});


projectSettingsStore.$subscribe((mutation, state) => {
  if (mutation.events.key && Object.keys(projectSettingsStore.theme).includes(mutation.events.key)) {
    console.log('Theme Change')
    projectSettingsStore.setupTheme()
  }
})



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


const updateProjectSettings = async () => {
  console.debug("Updating SETTINGS:", projectSettingsStore)
  await projectSettingsStore.save()
}

const resetProjectSettings = async () => {
  await useAsyncData('project', () => projectSettingsStore.fetch().then(() => true))
}

const shareUrl = ref('');

async function getShareUrl() {
  const response = await $fetch('/api/validateJwt', {
    method: 'post',
    body: {
      token: useCookie('rrAuthToken').value,
    }
  });
  try{
    shareUrl.value = '/login?applicationId='+response.jwt.applicationId;
  }catch (error){
    console.log("ABFUCK!")
  }
}



</script>

<template>
  <div class="parent-container">
    <div class="p-2 mb-2 flex justify-end">
      <PrimeButton  class="mr-2" label="Reset" icon="pi pi-replay" @click="resetProjectSettings"/>
      <PrimeButton :disabled="!userStore.roles.includes('tenant-admin')" label="Save" icon="pi pi-save" @click="updateProjectSettings"/>
    </div>

    <PrimeTabView class="container">
      <!--<PrimeTabPanel header="Account" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
      </PrimeTabPanel>-->
      <PrimeTabPanel header="Endpoint Manager" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
        <ApiEndpointsManager :initialEndpoints="{ api_endpoints: projectSettings['api_endpoints'] }" />
      </PrimeTabPanel>
      <PrimeTabPanel header="★ Theming" :disabled="!props.premium" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 1)})}">
        <div style="margin:15px">
        <div class="grid-container">

            <div><b>Primary Color</b></div><div></div>
            <div>Light/Dark Mode</div>
            <PrimeInputSwitch v-model="darkMode" @update:model-value="toggleDarkMode"></PrimeInputSwitch>

            <div><b>Primary Color</b></div><div></div>
            <div>Light</div>
            <PrimeColorPicker v-model="projectSettings.theme.value.primary_color_light"></PrimeColorPicker>
            <div>Dark</div>
            <PrimeColorPicker v-model="projectSettings.theme.value.primary_color_dark"></PrimeColorPicker>

            <div><b>Gradiant Color</b></div><div></div>
            <div>From - Light</div>
            <PrimeColorPicker v-model="projectSettings.theme.value.gradient_from_light"></PrimeColorPicker>
            <div>To - Light</div>
            <PrimeColorPicker v-model="projectSettings.theme.value.gradient_to_light"></PrimeColorPicker>
            <div>From - Dark</div>
            <PrimeColorPicker v-model="projectSettings.theme.value.gradient_from_dark"></PrimeColorPicker>
            <div>To - Dark</div>
            <PrimeColorPicker v-model="projectSettings.theme.value.gradient_to_dark"></PrimeColorPicker>
          </div>
        </div>
      </PrimeTabPanel>
      <PrimeTabPanel header="★ Share" :disabled="!props.premium" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 2)})}">

        <PrimeCard>
          <template #title>
            <b>Invite others to join your tenancy!</b>
          </template>
          <template #content>
            Share the following link with those you would like to invite to your tenancy!
          </template>
          <template #footer>
            <div class="footer-container">
              <b class="footer-text">Link to your tenancy:</b>
              <PrimeInputText v-model="shareUrl" class="flex-grow" placeholder="Vote" />
              <PrimeButton icon="pi pi-copy" class="footer-button" />
            </div>
          </template>
        </PrimeCard>

        <div>
          <!--
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
          -->
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

.footer-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-text {
  margin-right: 1em; /* Etwas Abstand zwischen Text und Eingabefeld */
}

.flex-grow {
  flex-grow: 1; /* Ermöglicht der Textbox, den verbleibenden Raum auszufüllen */
}

.footer-button {
  margin-left: 1em; /* Etwas Abstand zwischen Eingabefeld und Button */
}


</style>