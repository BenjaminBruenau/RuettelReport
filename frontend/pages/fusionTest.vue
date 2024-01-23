<script setup lang="ts">

import {onMounted} from "vue";

onMounted(() => {

  //test_retrieveUserByEmail();
  //test_login();
  //test_logout();
  //test_registrationTenant();
  //test_registrationNewTenant();
  //test_validateToken();

});

function getTokenFromSessionStorage() {
  const token = sessionStorage.getItem('rrAuthToken');
  if (token) {
    return token;
  } else {
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
  const response = await $fetch('/api/login', {
    method: 'post',
    body: {
      username: 'andi@gmail.com',
      password: 'testtest',
      applicationId: '8c14990c-d1d6-4d1e-92e8-400b422c2149',
    }
  });
  sessionStorage.setItem('rrAuthToken', response.token);
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
}

async function test_registrationNewTenant() {
  const response = await $fetch('/api/registerNewTenant', {
    method: 'post',
    body: {
      username: 'Timmm2',
      password: 'testtest',
      email: 'timmm2@gmail.com',
      tenantName: 'timmm2',
      premium: true,
    }
  });
}

async function test_validateToken() {
  const response = await $fetch('/api/validateToken', {
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