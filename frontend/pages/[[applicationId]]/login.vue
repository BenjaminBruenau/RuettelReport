<script setup lang="ts">
import axios from 'axios';
import { useToast } from 'primevue/usetoast';
import { useField, useForm, defineRule } from 'vee-validate';
import { required, email } from '@vee-validate/rules';
import {onMounted, ref} from "vue";
import { useRouter } from 'nuxt/app';


definePageMeta({
  middleware: ['auth'],
});

const route = useRoute();

const routeApplicationId_def = computed(() => {
  return route.params.applicationId === 'def' ? 'def' : route.params.applicationId;
});

// login either via /95370548-49aa-4f5f-aad9-ba6b6cfea3a0/login or /login
const applicationIdLogin = ref('');
const routeApplicationId_login = computed({
  get: () => {
    return route.params.applicationId || applicationIdLogin.value || '';
  },
  set: (newValue: any) => {
    applicationIdLogin.value = newValue;
  }
});

const routeApplicationId_empty = computed({
  get: () => {
    return route.params.applicationId || applicationIdLogin.value || '';
  },
  set: (newValue: any) => {
    applicationIdLogin.value = newValue;
  }
});

defineRule('required', required);
defineRule('email', email);

defineRule('required', required);
defineRule('email', email);

const emailLogin = ref('');
const passwordLogin = ref('');

const emailSignup = ref('');
const passwordSignup = ref('');
const confirmPassword = ref('');

const savedTheme = localStorage.getItem('theme')

if (savedTheme) {
  console.log('savedTheme: '+savedTheme)
  useColorMode().preference = savedTheme
}

const toast = useToast();

const panelClass = (props, parent, index) => {
  return [
    {
      'border-primary bg-tile_color_light dark:bg-tile_color_dark text-primary': parent.state.d_activeIndex === index,
      'border-b_color_light dark:border-b_color_dark bg-tile_color_light dark:bg-tile_color_dark text-textColor_light dark:text-textColor_dark': parent.state.d_activeIndex !== index,
    }
  ];
};

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


const activeTab = ref((routeApplicationId_def.value === 'def')?'login':'signup'); // Annahme, dass der Login-Tab standardmäßig aktiv ist

const setActiveTab = (tab) => {
  activeTab.value = tab;
}

const activeTabIndex = computed(() => {
  return activeTab.value === 'signup' ? 1 : 0;
});

// Funktion zum Handhaben von Tab-Click-Ereignissen
const handleTabClick = (index) => {
  setActiveTab(index === 1 ? 'signup' : 'login');
}

// -- FREE/PREMIUM --
const selectedTendancy = ref('Free'); // Standardmäßig auf "Free" setzen
const extraCardTab = ref('Free'); // Standardmäßig auf "Free" setzen

const handleTendancyChange = (value) => {
  selectedTendancy.value = value;
}


const config = ref({
  applicationId:'',
  tenantName:'',
  option:0,
});

const extraCardActiveIndex = ref(0);


const handleExtraCardTabClick = (index) => {
  config.value.option = index;
  console.log(config.value.option);
};


const checked = ref(false);

const isLoginButtonEnabled = computed(() => {
  console.log(emailLogin.value,passwordLogin.value,routeApplicationId_login.value);
  return emailLogin.value.trim() !== '' &&
      passwordLogin.value.trim() !== '' &&
      routeApplicationId_login.value !== '';
});

const isSignupButtonEnabled = computed(() => {

  const emailValid = emailSignup.value.trim() !== '';
  const passwordValid = passwordSignup.value.trim() !== '';
  const confirmPasswordValid = confirmPassword.value === passwordSignup.value;

  if (config.value.option === 0) {
    return emailValid && passwordValid && confirmPasswordValid && routeApplicationId_empty.value !== '';
  } else {
    return emailValid && passwordValid && confirmPasswordValid && config.value.tenantName.trim() !== '';
  }

});


// -------------------------------------------
//      FUSION AUTH
// -------------------------------------------

function register(){
  config.value.option === 0 ? registrationTenant() : registrationNewTenant()
}

async function login() {
  const router = useRouter();

  try {
    const response = await $fetch('/api/login', {
      method: 'post',
      body: {
        username: emailLogin.value,
        password: passwordLogin.value,
        applicationId: routeApplicationId_login.value
      }
    });

    window.location.reload();
    //await router.push(fusionAuthConfig.routes.authorizedRedirectUri[0]);

    toast.add({severity: 'success', summary: 'Login erfolgreich', detail: 'Sie sind jetzt eingeloggt.'});
  } catch (error) {
    toast.add({severity: 'error', summary: 'Login fehlgeschlagen', detail: 'Überprüfen Sie Ihre Anmeldeinformationen.'});
  }
}

async function registrationTenant() {
  console.log("RegistrationTenant()");
  try {
    const response = await $fetch('/api/registerTenant', {
      method: 'post',
      body: {
        email: emailSignup.value,
        password: passwordSignup.value,
        applicationId: routeApplicationId_empty.value
    }});

    window.location.reload();
    console.log('Registrierung erfolgreich:', response);
    toast.add({severity: 'success', summary: 'Registrierung erfolgreich', detail: 'Ihr Konto wurde erfolgreich erstellt.'});
    // Weiterer Code für erfolgreiche Registrierung
  } catch (error) {
    console.error('Registrierungsfehler:', error);
    toast.add({severity: 'error', summary: 'Registrierung fehlgeschlagen', detail: 'Registrierung konnte nicht abgeschlossen werden. Bitte versuchen Sie es erneut.'});
    // Fehlerbehandlung
  }
}

async function registrationNewTenant() {
  console.log("RegistrationNewTenant()");
  try {
    const response = await $fetch('/api/registerNewTenant', {
      method: 'post',
      body: {
      email: emailSignup.value,
      password: passwordSignup.value,
      tenantName: config.value.tenantName,
      premium: checked.value
    }});

    window.location.reload();
    console.log('Neue Mieterregistrierung erfolgreich:', response);
    toast.add({severity: 'success', summary: 'Registrierung erfolgreich', detail: 'Ihr Mieterkonto wurde erfolgreich erstellt.'});
    // Weiterer Code für erfolgreiche Registrierung
  } catch (error) {
    console.error('Fehler bei der Mieterregistrierung:', error);
    toast.add({severity: 'error', summary: 'Registrierung fehlgeschlagen', detail: 'Mieterregistrierung konnte nicht abgeschlossen werden. Bitte versuchen Sie es erneut.'});
    // Fehlerbehandlung
  }
}

</script>

<template>
  <div v-if="showResponseHTML" v-html="responseHTML"></div>
  <div>
    <Map style="position:absolute"></Map>
  </div>

  <div style="margin:20px; position:absolute" class="switch">
    <PrimeInputSwitch v-model="darkMode" @update:model-value="toggleDarkMode"></PrimeInputSwitch>
  </div>
  <div class="card-container">
    <PrimeCard class="card-content">
      <template #header>
        <!--<PrimeImage src="./logo_dark.png" alt="" />-->
        <div style="margin: 25px">
          <svg width="100%mm" height="100%" version="1.1" viewBox="0 0 98.267 39.688" xmlns="http://www.w3.org/2000/svg">
            <g transform="translate(-50.689 -87.699)">
              <path fill="#009b91" d="m68.424 127.34c-1.208-0.0997-3.3791-0.62585-4.8593-1.1776-2.742-1.0221-5.5778-2.9616-7.644-5.2281-3.1823-3.4906-4.9136-7.3922-5.2015-11.722-0.05132-0.77173-0.04239-0.87292 0.07473-0.84667 0.18358 0.0412 3.4351 3.5369 5.5302 5.9456 0.43083 0.4953 0.43083 0.4953 0.57611-0.93222 0.07991-0.78514 0.23862-2.3419 0.3527-3.4595s0.28274-2.775 0.3748-3.683c0.09207-0.90805 0.30146-2.9454 0.46531-4.5275 0.16385-1.5821 0.29792-2.9533 0.29792-3.0472 0-0.18199 1.047-1.2306 1.2287-1.2306 0.15328 0 0.14373-0.0827 0.67504 5.842 0.73115 8.1531 0.98347 10.922 1.0244 11.242 0.04639 0.36279 0.04639 0.36279 0.24271 0.21167 1.6613-1.2789 2.9522-2.2187 3.0037-2.1869 0.07717 0.0477 0.13425 1.0641 0.29977 5.3378 0.24922 6.4346-0.05532 5.9714 2.3222 3.5326 1.1457-1.1753 2.1308-2.1366 2.189-2.1362 0.08314 5.1e-4 0.10583 0.84278 0.10583 3.9273 0 3.0405-0.02388 3.9454-0.10583 4.0098-0.13987 0.10985-0.53383 0.16295-0.9525 0.1284zm3.66 2e-3c-0.46996-0.071-0.467-0.044-0.5091-4.6382-0.03498-3.8182-0.13826-6.4872-0.29903-7.7278-0.05786-0.44649-0.05786-0.44649-2.0615 1.5028-1.102 1.0721-2.0413 1.9493-2.0874 1.9493-0.11383 0-0.24589-2.6919-0.40081-8.1703-0.02634-0.93133-0.06495-1.7772-0.08581-1.8798-0.03389-0.1666-0.21402-0.0508-1.6919 1.0872-0.90968 0.7005-1.7016 1.2554-1.7598 1.233-0.0735-0.0282-0.16422-0.69679-0.29689-2.1879-0.10508-1.181-0.26471-2.9092-0.35475-3.8406-0.09003-0.93133-0.33683-3.7317-0.54843-6.223-0.2116-2.4913-0.41231-4.7306-0.44603-4.9761-0.0613-0.44643-0.0613-0.44643-0.8801 0.21167-0.99579 0.80034-3.3628 2.77-3.8624 3.214-0.32093 0.28521-0.37219 0.38189-0.44047 0.83078-0.08409 0.5528-0.62588 5.568-0.9741 9.017-0.33507 3.3188-0.18234 3.0978-1.3187 1.9079-3.4871-3.6515-3.2112-3.3236-3.2112-3.8161 0-1.0575 0.57923-3.2321 1.312-4.9257 1.4401-3.3283 3.6385-6.1083 6.5451-8.2768 2.7835-2.0767 5.4627-3.2033 8.969-3.7714 1.3061-0.21163 1.4817-0.21055 1.6816 0.0104 0.15636 0.17277 0.16258 0.50902 0.20362 11.015 0.04233 10.837 0.04233 10.837 0.77149 10.003 1.314-1.5031 2.1798-2.3675 2.2569-2.2532 0.06387 0.0946 0.41023 2.5607 1.2577 8.9544 0.61921 4.6719 0.54343 4.3384 1.1062 4.8683 1.1572 1.0898 3.1201 2.7093 3.2825 2.7081 0.29039-2e-3 1.5986-0.75925 2.777-1.6075 1.8807-1.3537 3.7599-3.3142 4.8716-5.0824 0.5328-0.84746 1.3625-2.6206 1.7583-3.7577 0.35629-1.0236 0.35629-1.0236 1.3489-1.0475 1.0795-0.026 1.2289 0.022 1.2273 0.39413-5.08e-4 0.12236-0.18903 0.78313-0.41888 1.4684-1.2839 3.8278-3.4204 6.9142-6.5386 9.4461-2.2506 1.8274-5.5086 3.387-8.3624 4.0032-0.73461 0.15862-2.4441 0.42982-2.5438 0.40358-0.02328-6e-3 -0.14794-0.0271-0.27702-0.0466zm44.108-2.4953c0.0297-0.14856 0.53636-2.8822 1.1259-6.0748 1.0719-5.8047 1.0719-5.8047 2.1112-5.8286 1.0393-0.0238 1.0393-0.0238 0.97836 0.41934-0.061 0.44318-0.061 0.44318 0.13985 0.29899 0.38238-0.27455 1.1432-0.63832 1.6325-0.78057 0.37277-0.10837 0.72961-0.13542 1.4772-0.11198 0.89858 0.0282 1.0365 0.0548 1.5594 0.30154 1.3871 0.65438 2.1053 2.0012 1.9767 3.7065-0.16749 2.2213-1.365 4.0594-3.2277 4.9544-0.85582 0.41118-1.507 0.53621-2.5562 0.49078-0.91526-0.0396-1.3902-0.19956-1.9802-0.6668-0.18256-0.14459-0.33193-0.22928-0.33193-0.1882 0 0.0728-0.54373 3.0528-0.64164 3.5167-0.0491 0.23283-0.0491 0.23283-1.1833 0.23283-1.1342 0-1.1342 0-1.0801-0.2701zm6.8358-4.9698c1.6377-0.80624 2.3459-3.0739 1.3648-4.3701-0.52045-0.68766-1.8037-0.8942-2.7741-0.44649-1.3001 0.59986-2.1879 2.1624-1.9312 3.3989 0.31353 1.5103 1.8381 2.1573 3.3404 1.4177zm-13.53 2.2671c-0.56481-0.13259-1.3483-0.55592-1.7613-0.95162-1.0565-1.0124-1.4161-2.613-0.94796-4.2197 0.16798-0.57651 0.70622-1.5833 1.1124-2.0806 0.75105-0.9197 2.0534-1.7451 3.1682-2.0079 0.61329-0.14458 1.8841-0.16495 2.4306-0.039 1.9288 0.44468 3.0179 2.211 2.7366 4.4383-0.17378 1.3759 0.27839 1.2293-3.7923 1.2293-3.5249 0-3.5249 0-3.4669 0.23283 0.10734 0.4306 0.31248 0.77336 0.58913 0.98437 0.87416 0.66676 2.3672 0.61201 3.2821-0.12036 0.31253-0.25017 0.31253-0.25017 1.5452-0.25017 0.67795 0 1.2323 0.0286 1.2318 0.0635-2e-3 0.16742-0.90811 1.2426-1.3366 1.5864-0.58394 0.46837-1.4887 0.92661-2.1728 1.1005-0.5565 0.14146-2.0764 0.16126-2.618 0.0341zm4.6444-5.8537c-0.15409-0.73118-0.55543-1.1657-1.2774-1.383-1.2143-0.36545-2.7726 0.23032-3.3959 1.2983-0.21 0.35984-0.21 0.35984 2.2607 0.35984s2.4707 0 2.4127-0.27517zm16.368 5.7844c-1.7456-0.55814-2.8238-2.1983-2.6568-4.0416 0.18436-2.0344 1.4622-3.8179 3.3936-4.7364 0.83977-0.39939 1.4748-0.54122 2.4232-0.54122 1.3172 0 2.2084 0.3527 2.9827 1.1805 0.80042 0.85571 1.1793 2.125 0.99806 3.3436-0.19847 1.3344-0.6446 2.1996-1.6626 3.2242-0.7686 0.77356-1.6134 1.2675-2.6676 1.5596-0.82331 0.22815-2.1162 0.23339-2.8105 0.0114zm2.9376-2.1683c0.62343-0.29493 1.235-0.90608 1.5721-1.571 1.0147-2.0013-0.40079-4.0008-2.4253-3.426-1.6719 0.47469-2.7566 2.2948-2.3152 3.8845 0.1523 0.54847 0.76149 1.1175 1.4327 1.3383 0.37367 0.12292 1.2354 0.0108 1.7357-0.22588zm-36.364 1.9691c0-0.0998 1.9172-10.53 2.1251-11.561 0.0909-0.4509 0.0909-0.4509 2.6871-0.42043 2.492 0.0293 2.6164 0.0385 3.0992 0.2302 0.60309 0.23947 1.1936 0.79818 1.4829 1.4031 0.27011 0.56478 0.33162 1.7002 0.13712 2.5313-0.37278 1.5928-1.6335 2.8894-3.266 3.3587-0.20955 0.0602-0.39327 0.11811-0.40826 0.12859-0.015 0.0105 0.42763 0.85133 0.98361 1.8686s1.0929 2.0114 1.1932 2.2094c0.18227 0.35983 0.18227 0.35983-1.2389 0.35983s-1.4212 0-2.5183-2.1994c-0.6034-1.2097-1.1213-2.1717-1.1509-2.1378-0.0296 0.0339-0.21425 0.9569-0.41033 2.0512-0.19609 1.0943-0.37548 2.0564-0.39866 2.1378-0.0374 0.13166-0.1688 0.14816-1.1794 0.14816-0.86879 0-1.1373-0.0256-1.1373-0.10859zm5.8692-6.4117c0.40411-0.11859 0.94436-0.59916 1.1822-1.0516 0.13576-0.25827 0.18189-0.49959 0.18437-0.96457 3e-3 -0.6059-4e-3 -0.62658-0.30257-0.90066-0.37777-0.34646-0.89436-0.46888-1.9786-0.46888-0.68387 0-0.80042 0.0206-0.83727 0.14817-0.0236 0.0815-0.16013 0.79587-0.30351 1.5875-0.14339 0.79163-0.28123 1.5307-0.30632 1.6424-0.0456 0.20302-0.0456 0.20302 0.99631 0.1528 0.57306-0.0276 1.1875-0.093 1.3654-0.14516zm35.028 6.4568c2e-3 -0.0349 0.37553-2.0638 0.83093-4.5085 0.82799-4.445 0.82799-4.445 1.871-4.4689 1.043-0.0239 1.043-0.0239 0.98622 0.33123-0.0568 0.35509-0.0568 0.35509 0.31937 8e-3 0.40435-0.37272 1.0288-0.61207 1.5968-0.61207 0.37269 0 0.36401-0.17331 0.0696 1.3914-0.14232 0.75642-0.14232 0.75642-0.60241 0.85119-0.59109 0.12174-0.92056 0.29967-1.2503 0.67521-0.49947 0.56887-0.50468 0.58823-1.2478 4.6387-0.32234 1.7568-0.32234 1.7568-1.4493 1.7568-0.61984 0-1.1257-0.0286-1.124-0.0635zm5.7544-5e-4c0-0.0661 1.1458-6.3154 1.234-6.7305 0.0495-0.23283 0.0495-0.23283-0.46524-0.23283-0.60261 0-0.57821 0.079-0.34565-1.1188 0.093-0.47896 0.16919-0.88036 0.16933-0.892 0-0.0116 0.24132-0.0212 0.53595-0.0212 0.5357 0 0.5357 0 0.63874-0.5715 0.49092-2.7229 0.33327-2.4765 1.5848-2.4765 1.1312 0 1.1312 0 1.087 0.23283-0.0243 0.12806-0.15056 0.81386-0.28053 1.524-0.23632 1.2912-0.23632 1.2912 0.42894 1.2912 0.64724 0 0.66399 5e-3 0.61869 0.1905-0.0256 0.10478-0.11493 0.55245-0.1985 0.99483-0.15195 0.80434-0.15195 0.80434-0.79398 0.82893-0.40513 0.0155-0.65589 0.0624-0.67962 0.127-0.0207 0.0563-0.31874 1.6359-0.66238 3.5102-0.62481 3.4078-0.62481 3.4078-1.7482 3.4078-0.61785 0-1.1234-0.0288-1.1234-0.064zm-65.935-1.7632c-0.38364-0.35301-0.69752-0.70747-0.69752-0.7877 0-0.1512 0.38558-1.3139 0.83934-2.531 0.26131-0.70091 0.26131-0.70091-0.22439-1.143-1.5251-1.3882-1.4267-1.2414-1.7246-2.5731-0.1476-0.65982-0.24526-1.2599-0.21702-1.3335 0.04578-0.11931 0.47399-0.13383 3.9471-0.13383 3.8957 0 3.8957 0 3.9391 0.1905 0.02386 0.10478 0.11774 0.5334 0.20862 0.9525 0.17699 0.81621 0.19458 0.84125 0.96665 1.3755 0.42184 0.29188 0.60124 0.60678 0.51306 0.90055-0.09488 0.31606-1.2387 1.7489-2.0248 2.5365-1.562 1.5649-3.9076 3.1849-4.6153 3.1877-0.14769 6e-4 -0.42572-0.19522-0.91017-0.641zm10.732-10.689c-0.21167-0.0643-0.21167-0.0643-0.22151-1.9231-0.01107-2.09-0.08483-2.723-0.50673-4.3488-0.96203-3.707-3.0992-6.8415-6.3786-9.3552-2.3987-1.8386-5.4627-3.1519-8.2178-3.5223-0.39582-0.0532-0.84562-0.11508-0.99956-0.13751-0.52178-0.076-0.60911-0.2565-0.60911-1.259 0-1.314 0.05398-1.3467 1.8141-1.0996 3.368 0.47278 6.4378 1.7915 9.5312 4.0945 0.95463 0.7107 3.1863 2.9703 3.9319 3.981 1.4053 1.9051 2.3756 3.8307 3.0463 6.0453 0.53773 1.7757 0.65934 2.6413 0.70437 5.0134 0.04991 2.6296 0.10284 2.5053-1.084 2.5473-0.4394 0.0155-0.89415-6.7e-4 -1.0106-0.0361zm-11.678-0.23724c-0.32216-0.40956-0.25082-0.55284 0.87431-1.7558 1.0498-1.1224 1.0498-1.1224 0.80798-2.3023-0.13299-0.64892-0.24345-1.2763-0.24545-1.3942-0.0028-0.16478 0.19504-0.39982 0.85524-1.016 1.6172-1.5094 1.7692-1.6223 2.1022-1.5608 0.41224 0.0761 3.4009 1.3686 3.5729 1.5452 0.07787 0.0799 0.23909 0.50731 0.35828 0.94969 0.2167 0.80434 0.2167 0.80434 1.065 1.6087 0.6243 0.59196 0.84826 0.85902 0.84822 1.0114-5e-5 0.24582-0.82536 2.8785-0.95029 3.0314-0.06722 0.0823-1.0917 0.10584-4.5996 0.10584-4.5132 0-4.5132 0-4.6886-0.22309zm31.859-3.5098c-0.54601-0.15824-1.0761-0.60264-1.3607-1.1408-0.53865-1.0184-0.52012-1.5296 0.19695-5.4345 0.48587-2.6458 0.48587-2.6458 1.6204-2.6458s1.1346 0 1.0871 0.23283c-0.0261 0.12806-0.23868 1.2868-0.47239 2.5749-0.60481 3.3335-0.57658 3.959 0.19451 4.309 0.72605 0.32964 1.6277 0.12678 2.2638-0.50933 0.3433-0.3433 0.44106-0.51818 0.62354-1.1155 0.16283-0.53296 1.0652-5.1433 1.0652-5.4422 0-0.0274 0.51848-0.0498 1.1522-0.0498 1.1522 0 1.1522 1e-5 1.1057 0.1905-0.0255 0.10478-0.39124 2.0574-0.81264 4.3392-0.4214 2.2818-0.78635 4.2344-0.811 4.3392-0.0435 0.18496-0.0755 0.1905-1.0989 0.1905-0.8042 0-1.0463-0.0251-1.0215-0.10583 0.0179-0.0582 0.0556-0.2106 0.0836-0.33865 0.051-0.2328 0.051-0.2328-0.4047 0.0587-0.73642 0.47108-1.3593 0.64097-2.3177 0.63214-0.45549-4e-3 -0.94762-0.0423-1.0936-0.0846zm20.595-2e-3c-0.56481-0.1326-1.3483-0.55593-1.7613-0.95163-1.0565-1.0124-1.4161-2.613-0.94796-4.2197 0.16798-0.57652 0.70622-1.5833 1.1124-2.0806 0.75106-0.9197 2.0534-1.745 3.1682-2.0079 0.6133-0.14459 1.8841-0.16496 2.4306-0.039 1.9289 0.44468 3.0179 2.211 2.7366 4.4383-0.17378 1.3759 0.27839 1.2293-3.7923 1.2293-3.5249 0-3.5249 0-3.4669 0.23284 0.10734 0.4306 0.31248 0.77336 0.58913 0.98437 0.87416 0.66676 2.3671 0.61201 3.2821-0.12036 0.31253-0.25018 0.31253-0.25018 1.5452-0.25018 0.67794 0 1.2323 0.0286 1.2318 0.0635-2e-3 0.16742-0.90811 1.2426-1.3366 1.5864-0.58394 0.46837-1.4887 0.9266-2.1728 1.1005-0.5565 0.14146-2.0764 0.16126-2.618 0.0341zm4.6444-5.8537c-0.1541-0.73119-0.55544-1.1657-1.2774-1.383-1.2143-0.36545-2.7726 0.23032-3.3959 1.2983-0.21 0.35984-0.21 0.35984 2.2607 0.35984s2.4707 0 2.4127-0.27516zm-36.87 5.5852c0-0.0998 1.9172-10.53 2.1251-11.561 0.0909-0.4509 0.0909-0.4509 2.6871-0.42043 2.492 0.0292 2.6164 0.0385 3.0992 0.2302 0.60309 0.23947 1.1936 0.79817 1.4829 1.4031 0.27011 0.56478 0.33162 1.7002 0.13712 2.5313-0.37278 1.5928-1.6335 2.8894-3.266 3.3587-0.20955 0.0603-0.39327 0.11811-0.40826 0.12858-0.015 0.0105 0.42763 0.85133 0.98361 1.8686 0.55598 1.0172 1.0929 2.0114 1.1932 2.2094 0.18227 0.35983 0.18227 0.35983-1.2389 0.35983s-1.4212 0-2.5183-2.1994c-0.6034-1.2096-1.1213-2.1717-1.1509-2.1378-0.0296 0.0339-0.21425 0.9569-0.41033 2.0512-0.19609 1.0943-0.37548 2.0563-0.39866 2.1378-0.0374 0.13167-0.1688 0.14817-1.1794 0.14817-0.86879 0-1.1373-0.0256-1.1373-0.10859zm5.8692-6.4117c0.40411-0.11859 0.94436-0.59916 1.1822-1.0516 0.13576-0.25827 0.18189-0.49959 0.18437-0.96458 3e-3 -0.60589-4e-3 -0.62657-0.30257-0.90066-0.37777-0.34645-0.89436-0.46887-1.9786-0.46887-0.68387 0-0.80042 0.0206-0.83727 0.14817-0.0236 0.0815-0.16013 0.79587-0.30351 1.5875-0.14339 0.79163-0.28123 1.5307-0.30632 1.6424-0.0456 0.20303-0.0456 0.20303 0.99631 0.15281 0.57306-0.0276 1.1875-0.0929 1.3654-0.14516zm13.604 6.4563c0-0.0661 1.1458-6.3153 1.234-6.7305 0.0495-0.23283 0.0495-0.23283-0.46524-0.23283-0.60261 0-0.57821 0.079-0.34566-1.1188 0.093-0.47896 0.1692-0.88036 0.16934-0.892 1.7e-4 -0.0116 0.24132-0.0212 0.53595-0.0212 0.5357 0 0.5357 0 0.63874-0.5715 0.49092-2.7229 0.33326-2.4765 1.5848-2.4765 1.1312 0 1.1312 0 1.087 0.23283-0.0243 0.12806-0.15055 0.81386-0.28052 1.524-0.23632 1.2912-0.23632 1.2912 0.42893 1.2912 0.64725 0 0.664 5e-3 0.6187 0.1905-0.0256 0.10477-0.11493 0.55245-0.1985 0.99483-0.15195 0.80433-0.15195 0.80433-0.79398 0.82892-0.40513 0.0155-0.6559 0.0624-0.67962 0.127-0.0207 0.0563-0.31874 1.6359-0.66238 3.5102-0.6248 3.4078-0.6248 3.4078-1.7482 3.4078-0.61785 0-1.1234-0.0288-1.1234-0.064zm5.08 0c0-0.0661 1.1458-6.3153 1.234-6.7305 0.0495-0.23283 0.0495-0.23283-0.46524-0.23283-0.60261 0-0.57821 0.079-0.34565-1.1188 0.093-0.47896 0.16919-0.88036 0.16933-0.892 1.7e-4 -0.0116 0.24132-0.0212 0.53595-0.0212 0.5357 0 0.5357 0 0.63874-0.5715 0.49092-2.7229 0.33327-2.4765 1.5848-2.4765 1.1312 0 1.1312 0 1.087 0.23283-0.0243 0.12806-0.15055 0.81386-0.28052 1.524-0.23632 1.2912-0.23632 1.2912 0.42893 1.2912 0.64725 0 0.664 5e-3 0.6187 0.1905-0.0256 0.10477-0.11493 0.55245-0.1985 0.99483-0.15195 0.80433-0.15195 0.80433-0.79398 0.82892-0.40513 0.0155-0.6559 0.0624-0.67962 0.127-0.0207 0.0563-0.31874 1.6359-0.66238 3.5102-0.6248 3.4078-0.6248 3.4078-1.7482 3.4078-0.61785 0-1.1234-0.0288-1.1234-0.064zm14.901 0.0235c0-0.0223 0.4953-2.7135 1.1007-5.9805 0.60537-3.267 1.1007-5.9729 1.1007-6.0131 0-0.0402 0.51848-0.0732 1.1522-0.0732 1.1516 0 1.1522 9e-5 1.1076 0.1905-0.0245 0.10478-0.53111 2.8289-1.1257 6.0537-1.0811 5.8632-1.0811 5.8632-2.2082 5.8632-0.61992 0-1.1271-0.0182-1.1271-0.0405zm-58.296-5.4251c-0.80082-0.71248-0.93307-0.7803-2.0515-1.0519-0.43124-0.10473-0.87602-0.26273-0.98838-0.35112-0.11237-0.0884-0.26972-0.36025-0.34967-0.60414-0.20114-0.61356-0.34661-0.79532-1.1284-1.4099-0.37488-0.2947-0.70348-0.60477-0.73023-0.68904-0.02675-0.0843 0.01014-0.4892 0.08197-0.89984s0.13035-0.80377 0.13006-0.87362c-3.38e-4 -0.0699-0.32331-0.508-0.7178-0.97367-0.3945-0.46566-0.71798-0.912-0.71885-0.99185-0.0041-0.37634 0.49458-0.37025 2.5341 0.031 1.7074 0.33585 3.5676 1.1269 5.1722 2.1995 1.115 0.74532 1.6901 1.3142 1.6901 1.6716 0 0.78835-0.6079 1.0082-1.9948 0.72134-0.81333-0.16821-1.2356-0.0785-1.4337 0.30451-0.28482 0.55078-0.12961 0.7644 0.74385 1.0238 1.0194 0.30272 1.0288 0.31137 1.234 1.1294 0.23069 0.91978 0.22736 1.016-0.04243 1.2283-0.40207 0.31627-0.64003 0.23904-1.4305-0.46425zm31.25-5.0118c0.26241-1.3989 0.36619-1.9028 0.39855-1.9352 0.0211-0.0211 0.53253-0.0277 1.1365-0.0147 1.0981 0.0237 1.0981 0.0237 0.89123 1.1032-0.20684 1.0795-0.20684 1.0795-1.3384 1.0795s-1.1316 0-1.0879-0.23283zm3.4219 0.17179c-8.5e-4 -0.0363 0.083-0.53268 0.18639-1.1031 0.18795-1.0372 0.18795-1.0372 1.3139-1.0372 0.84675 0 1.1248 0.0262 1.1214 0.10584-3e-3 0.0582-0.0887 0.54398-0.19151 1.0795-0.18694 0.97366-0.18694 0.97366-1.3078 0.99729-0.61646 0.013-1.1215-6e-3 -1.1224-0.0423z" stroke-width=".084667"/>
            </g>
          </svg>
        </div>


      </template>
      <template #title>Login</template>
      <template #subtitle>Please enter your credentials</template>
      <template #content>
        <PrimeTabView :activeIndex="activeTabIndex" @update:activeIndex="handleTabClick">
          <PrimeTabPanel  header="Login" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
            <form class="form">
              <div class="form-row">
                <label for="email">Email</label>
                <PrimeInputText id="email" v-model="emailLogin" class="full-width" required />
              </div>
              <div class="form-row">
                <label for="password">Password</label>
                <PrimePassword id="password" v-model="passwordLogin" class="full-width" required :feedback="false" />
              </div>
              <div class="form-row">
                <label for="applicationId">Application ID</label>
                <PrimeInputText id="applicationId" v-model="routeApplicationId_login" class="full-width" />
              </div>
              <PrimeButton label="Login" class="full-width" :disabled="!isLoginButtonEnabled" @click="login"/>
            </form>
          </PrimeTabPanel>
          <PrimeTabPanel header="Sign Up" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 1)})}">
            <form class="form">
              <div class="form-row">
                <label for="emailSignup">Email</label>
                <PrimeInputText id="emailSignup" v-model="emailSignup" class="full-width" required />
              </div>
              <div class="form-row">
                <label for="passwordSignup">Password</label>
                <PrimePassword id="passwordSignup" v-model="passwordSignup" class="full-width" required :feedback="false" />
              </div>
              <div class="form-row">
                <label for="confirmPassword">Confirm Password</label>
                <PrimePassword id="confirmPassword" v-model="confirmPassword" class="full-width"  required :feedback="false" />
              </div>
              <PrimeButton label="Sign Up" class="full-width" :disabled="!isSignupButtonEnabled" @click="register"/>
            </form>
          </PrimeTabPanel>
        </PrimeTabView>
      </template>
    </PrimeCard>
    <PrimeCard v-if="activeTab === 'signup'" class="card-content extra-card">
      <template #title>Tendancy</template>
      <template #content>
        <PrimeTabView :activeIndex="extraCardActiveIndex" @update:activeIndex="handleExtraCardTabClick">
          <PrimeTabPanel header="Join existing Tenant" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 0)})}">
            <PrimeInputGroup>
              <PrimeInputGroupAddon><b>Application ID</b></PrimeInputGroupAddon>
              <PrimeInputText v-model="routeApplicationId_empty" />
            </PrimeInputGroup>
          </PrimeTabPanel>
          <PrimeTabPanel header="New Tenant" :pt="{headeraction: ({ props, parent }) => ({class: panelClass(props, parent, 1)})}">

            <div class="grid-container">
            <PrimeInputGroup class="grid-item">
              <PrimeInputGroupAddon><b>Tenant Name</b></PrimeInputGroupAddon>
              <PrimeInputText v-model="config.tenantName" />
            </PrimeInputGroup>
            <b class="grid-item">
              Coose plan
            </b>
            <PrimeToggleButton  v-model="checked" onLabel="Premium" offLabel="Free"
                           class="grid-item" :pt="{
                root: {
                    class: ['w-8rem', {
                      'bg-primary_light border-b_color_light dark:bg-primary_dark dark:border-b_color_dark text-contrastText_dark': checked,
                    'bg-tile_color_light border-b_color_light dark:bg-tile_color_dark dark:border-b_color_dark text-contrastText_dark': !checked,
                    }]
                }
            }" />


            </div>
          </PrimeTabPanel>

        </PrimeTabView>
      </template>
    </PrimeCard>
  </div>

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
  min-width:20em;
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

.extra-card {
  flex: 1; /* Flexible Breite, um gleiche Höhe zu ermöglichen */
  max-width: 25em;
  margin-left: 10px; /* Abstand zur Hauptkarte */
}


.grid-container {
  display: grid;
  grid-template-columns: 1fr; /* 1 Spalte */
  grid-gap: 10px; /* Abstand von 10px zwischen den Elementen */
}

.grid-item {
  /* Stil für jedes Element im Grid */
}

</style>