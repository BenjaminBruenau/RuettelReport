<script setup lang="ts">

import {onMounted} from "vue";

onMounted(() => {

  //test_retrieveUserByEmail();
  //test_login();
  //test_logout();
  //test_registration();
  test_registrationApp();
  //test_registrationNewApp();

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
      userEmail: 'test@gmail.com',
    }
  });
}

async function test_login() {
  const response = await $fetch('/api/login', {
    method: 'post',
    body: {
      username: 'test@gmail.com',
      password: 'testtest'
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

async function test_registration() {
  const response = await $fetch('/api/registerDefault', {
    method: 'post',
    body: {
      username: 'newUser11',
      password: 'newUser11',
      email: 'newUser11@gmail.com',
    }
  });
}

async function test_registrationApp() {
  const response = await $fetch('/api/registerTenant', {
    method: 'post',
    body: {
      username: 'newUser3245678912',
      password: 'newUser3425678912',
      email: 'newUser3425678912@gmail.com',
      applicationId: '974a0448-f5ca-4095-9b50-94c97b3109d4',
    }
  });
}

async function test_registrationNewApp() {
  const response = await $fetch('/api/registerNewTenant', {
    method: 'post',
    body: {
      username: 'test_registrationNewTenant_1',
      password: 'testtest',
      email: 'test_registrationNewTenant_1@gmail.com',
      tenantName: 'test_registrationNewTenant_1',
    }
  });
}



</script>

<template>


</template>

<style scoped>

</style>