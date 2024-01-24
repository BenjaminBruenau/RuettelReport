<script setup lang="ts">

import {onMounted} from "vue";
import {fusionAuthConfig} from "~/server/api/config";

onMounted(() => {

  //test_retrieveUserByEmail();
  //test_login();
  //test_logout();
  //test_registrationTenant();
  //test_registrationNewTenant();
  //test_validateJwt();

});

function getTokenFromSessionStorage() {
  const token = sessionStorage.getItem('rrAuthToken');
  if (token) {
    console.log('Token',token);
    return token;
  } else {
    console.log('Kein Token gefunden!');
    return null;
  }
}


async function test_retrieveUserByEmail() {
  const response = await $fetch('/api/retrieveUserByEmail', {
    method: 'post',
    body: {
      userEmail: 'joelBenni@gmail.com',
    }
  });
}

async function test_login() {
  const router = useRouter();

  try {
    const response = await $fetch('/api/login', {
      method: 'post',
      body: {
        username: 'andi@gmail.com',
        password: 'testtest',
        applicationId: '8c14990c-d1d6-4d1e-92e8-400b422c2149',
      }
    });

    if (response && response.token) {

      sessionStorage.setItem('rrAuthToken', response.token);

      //await router.push(fusionAuthConfig.routes.authorizedRedirectUri[0]);
    } else {
      console.error('Login fehlgeschlagen');
    }
  } catch (error) {
    // Fehlerbehandlung
    console.error('Fehler beim Login:', error);
  }
}

async function test_logout() {
  const response = await $fetch('/api/logout', {
    method: 'post',
    body: {
      token: getTokenFromSessionStorage(),
    }
  });
  sessionStorage.setItem('rrAuthToken', response.token);
}

/*
async function test_registration() {
  const response = await $fetch('/api/registerDefault', {
    method: 'post',
    body: {
      username: 'joeljoel',
      password: 'joeljoel',
      email: 'joeljoel@gmail.com',
    }
  });
}
 */

async function test_registrationTenant() {
  const response = await $fetch('/api/registerTenant', {
    method: 'post',
    body: {
      username: 'andi',
      password: 'testtest',
      email: 'andi@gmail.com',
      applicationId: '8c14990c-d1d6-4d1e-92e8-400b422c2149',
    }
  });
  sessionStorage.setItem('rrAuthToken', response.token);
}

async function test_registrationNewTenant() {
  const response = await $fetch('/api/registerNewTenant', {
    method: 'post',
    body: {
      username: 'joelNew4',
      password: 'testtest',
      email: 'joelNew4@gmail.com',
      tenantName: 'joelNew4',
      premium: true,
    }
  });
  sessionStorage.setItem('rrAuthToken', response.token);
}

async function test_validateJwt() {
  const response = await $fetch('/api/validateJwt', {
    method: 'post',
    body: {
      token: getTokenFromSessionStorage(),
    }
  });
}





</script>

<template>


</template>

<style scoped>

</style>