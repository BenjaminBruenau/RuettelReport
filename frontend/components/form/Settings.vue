<script setup lang="ts">

</script>

<template>
    <PrimeTabView>
      <PrimeTabPanel header="Account">
      </PrimeTabPanel>
      <PrimeTabPanel header="Endpoint Manager">
        <ApiEndpointsManager/>
      </PrimeTabPanel>
      <PrimeTabPanel header="Theming">
      </PrimeTabPanel>

    </PrimeTabView>

</template>

<style scoped>
.container{
  border-radius: 15px;
}
</style>