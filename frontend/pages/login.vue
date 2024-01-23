<script setup lang="ts">
import axios from 'axios';
import { useToast } from 'primevue/usetoast';
import { useField, useForm, defineRule } from 'vee-validate';
import { required, email } from '@vee-validate/rules';
import {onMounted, ref} from "vue";

defineRule('required', required);
defineRule('email', email);

const FA_BASE_URL = 'http://your-fusionauth-url123123.com';


defineRule('required', required);
defineRule('email', email);


// Login Form
const { handleSubmit: handleSubmitLogin, resetForm: resetLoginForm } = useForm();
const { value: emailLogin, errorMessage: emailLoginError } = useField('email', 'required|email');
const { value: passwordLogin, errorMessage: passwordLoginError } = useField('password', 'required');

// Sign Up Form
const { handleSubmit: handleSubmitSignup, resetForm: resetSignupForm } = useForm();
const { value: emailSignup, errorMessage: emailSignupError } = useField('email', 'required|email');
const { value: passwordSignup, errorMessage: passwordSignupError } = useField('password', 'required');
const { value: confirmPassword, errorMessage: confirmPasswordError } = useField('confirmPassword', value => {
  if (value !== passwordSignup.value) {
    return 'Passwords must match';
  }
  return true;
});


const savedTheme = localStorage.getItem('theme')

if (savedTheme) {
  console.log('savedTheme: '+savedTheme)
  useColorMode().preference = savedTheme
}

const toast = useToast();

/*
const onSubmitLogin = handleSubmitLogin(async (values) => {
  try {
    console.log("HIER!");
    const response = await axios.post('http://localhost:3000/login', {
      client_id: '9643e3b2-9f1e-4f21-8457-7d631bfde25e',
      redirect_uri: 'http://localhost:3000/dashboard',
      user_email:'test@test.de',
      user_password:'testtest'
    });

    console.log('RESPONSE:',response);

    // Hier sollten Sie die Antwort validieren und das Token sicher speichern
    // Zum Beispiel in einem HttpOnly Cookie

    toast.add({ severity: 'success', summary: 'Login Successful', detail: 'You are now logged in.', life: 3000 });
    resetLoginForm();
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Login Failed', detail: 'Invalid credentials.', life: 3000 });
  }
});

const onSubmitSignup = handleSubmitSignup(async values => {
  try {
    const response = await axios.post(`http://34.65.19.16/oauth2/authorize`, {
      user: {
        email: values.email,
        password: values.password,
      }
    });
    toast.add({ severity: 'success', summary: 'Registration Successful', detail: 'Account created successfully.', life: 3000 });
    resetSignupForm();
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Registration Failed', detail: 'Could not create account.', life: 3000 });
  }
});
*/



const panelClass = (props, parent, index) => {
  return [
    {
      'border-primary bg-tile_color_light dark:bg-tile_color_dark text-primary': parent.state.d_activeIndex === index,
      'border-b_color_light dark:border-b_color_dark bg-tile_color_light dark:bg-tile_color_dark text-textColor_light dark:text-textColor_dark': parent.state.d_activeIndex !== index,
    }
  ];
};

/*
GET/oauth2/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code&tenantId={tenantId}
 */




const project_settings = ref({
  /*'theme':{
    'primary_color_light':'#ffffff',
    'primary_color_dark':'#9e9e9e',
    'gradient_from_light':"#d8d8d8",
    'gradient_to_light':"#d8d8d8",
    'gradient_from_dark':"#1f1f1f",
    'gradient_to_dark':"#1f1f1f",
    'default_theme': 'light',
  },*/
  'theme':{
    'primary_color_light':'#009b91',
    'primary_color_dark':'#009b91',
    'gradient_from_light':"#dde6eb",
    'gradient_to_light':"#dde6eb",
    'gradient_from_dark':"#334152",
    'gradient_to_dark':"#334152",
    'default_theme': 'light',
  },

});


function setupTheme() {
  const themeSettings = project_settings.value.theme;
  document.documentElement.style.setProperty('--color-primary_light', themeSettings.primary_color_light);
  document.documentElement.style.setProperty('--color-primary_dark', themeSettings.primary_color_dark);
  document.documentElement.style.setProperty('--gradient_to_light', themeSettings.primary_color_dark);
  document.documentElement.style.setProperty('--gradient_from_light', themeSettings.gradient_from_light);
  document.documentElement.style.setProperty('--gradient_to_light', themeSettings.gradient_to_light);
  document.documentElement.style.setProperty('--gradient_from_dark', themeSettings.gradient_from_dark);
  document.documentElement.style.setProperty('--gradient_to_dark', themeSettings.gradient_to_dark);
  document.documentElement.style.setProperty('--b_color_light', adjustColorBrightness(themeSettings.gradient_from_light,1.1));
  document.documentElement.style.setProperty('--b_color_dark', adjustColorBrightness(themeSettings.gradient_from_dark, 0.9));
}

function adjustColorBrightness(hexColor: string, factor: number): string {
  if (!/^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(hexColor)) {
    throw new Error('Ungültiger Hex-Farbwert');
  }

  let r: number = parseInt(hexColor.substring(1, 3), 16);
  let g: number = parseInt(hexColor.substring(3, 5), 16);
  let b: number = parseInt(hexColor.substring(5, 7), 16);

  r = Math.min(255, Math.max(0, r * factor));
  g = Math.min(255, Math.max(0, g * factor));
  b = Math.min(255, Math.max(0, b * factor));

  return `#${Math.round(r).toString(16).padStart(2, '0')}${Math.round(g).toString(16).padStart(2, '0')}${Math.round(b).toString(16).padStart(2, '0')}`;
}

onMounted(() => {
  setupTheme();
});


const darkMode = ref(false)

const toggleDarkMode = (newValue: boolean) => {
  useColorMode().preference = darkMode.value ? 'dark' : 'light'
}



// --login--

const showResponseHTML = ref(false);
const responseHTML = ref('');


/*
async function loginRequest() {
  try {
    const params = new URLSearchParams({
      client_id: '9643e3b2-9f1e-4f21-8457-7d631bfde25e',
      response_type: 'code',
      redirect_uri: 'http://localhost:3000/dashboard',
    });

    const url = `http://localhost:3000/auth/oauth2/authorize?${params.toString()}`;

    console.log(url);

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    });

    console.log('HTTP-Statuscode:', response.status);

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const text = await response.text();

    if (response.status === 200) {
      showResponseHTML.value = true;
      responseHTML.value = text;
      console.log('TEXT:', text);

    }

  } catch (error) {
    console.error('Fehler bei der Authentifizierung:', error);
  }
}

loginRequest();
*/


import {FusionAuthClient} from '@fusionauth/typescript-client';

const client = new FusionAuthClient('FbX31ng685J3e3Fcy4xWaDcDPUg-PMwgyin_RVHGPLnUKbXuG3ZxuUVT', '/auth');

client.retrieveUserByEmail('test@test.com')
    .then(clientResponse => {
      console.log("User:", JSON.stringify(clientResponse.response.user, null, 2));
    }).catch(console.error);


import { LoginRequest } from '@fusionauth/typescript-client/build/src/FusionAuthClient';

const loginRequest: LoginRequest = {
  loginId: 'test@test.com',
  password: 'test',
};

client.login(loginRequest)
    .then(clientResponse => {
      console.log("Erfolgreich angemeldet", clientResponse);
    }).catch(error => {
  console.error("Anmeldefehler", error);
});


</script>

<template>
  <div v-if="showResponseHTML" v-html="responseHTML"></div>
  <div>
    <Map style="position:absolute"></Map>
  </div>



  <!---
  <div style="margin:20px; position:absolute"  class="switch">
    <PrimeInputSwitch v-model="darkMode" @update:model-value="toggleDarkMode"></PrimeInputSwitch>
  </div>
  <div class="card-container">
    <PrimeCard class="card-content">
      <template #header>
        <PrimeImage src="./assets/img/logo_dark.png"  alt=""/>
      </template>
      <template #title>Login</template>
      <template #subtitle>Please enter your credentials</template>
      <template #content>
        <PrimeTabView>
          <PrimeTabPanel header="Login" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
            <form @submit.prevent="onSubmitLogin" class="form">
              <div class="form-row">
                <label for="email">Email</label>
                <PrimeInputText id="email" v-model="emailLogin" class="full-width" required />
                <small class="p-error">{{ emailLoginError || '&nbsp;' }}</small>
              </div>
              <div class="form-row">
                <label for="password">Password</label>
                <PrimePassword id="password" v-model="passwordLogin" class="full-width" required :feedback="false" />
                <small class="p-error">{{ passwordLoginError || '&nbsp;' }}</small>
              </div>
              <PrimeButton label="Login" class="full-width" @click="onSubmitLogin" />
            </form>
          </PrimeTabPanel>
          <PrimeTabPanel header="Sign Up" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 1)})}">
            <form @submit.prevent="onSubmitSignup" class="form">
              <div class="form-row">
                <label for="emailSignup">Email</label>
                <PrimeInputText id="emailSignup" v-model="emailSignup" class="full-width" required />
                <small class="p-error">{{ emailSignupError || '&nbsp;' }}</small>
              </div>
              <div class="form-row">
                <label for="passwordSignup">Password</label>
                <PrimePassword id="passwordSignup" v-model="passwordSignup" class="full-width" required :feedback="false" />
                <small class="p-error">{{ passwordSignupError || '&nbsp;' }}</small>
              </div>
              <div class="form-row">
                <label for="confirmPassword">Confirm Password</label>
                <PrimePassword id="confirmPassword" v-model="confirmPassword" class="full-width"  required :feedback="false" />
                <small class="p-error">{{ confirmPasswordError || '&nbsp;' }}</small>
              </div>
              <PrimeButton label="Sign Up" class="full-width" />
            </form>
          </PrimeTabPanel>
        </PrimeTabView>

      </template>
    </PrimeCard>
  </div>
  -->

</template>

<style scoped>

.dashboard {
  width: 100%;
  height: calc(100vh);
  overflow: auto;
  margin: 0px;
}

.card-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  z-index: 10;
}

.card-content {
  max-width: 25em;
  backdrop-filter: blur(100px);
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1em;
}

.form-row {
  display: flex;
  flex-direction: column;
}

.full-width {
  width: 100%;
}

.switch{
  z-index:20;
}


</style>