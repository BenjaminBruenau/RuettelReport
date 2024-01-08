<script setup lang="ts">
import { ref } from "vue";
const visible = ref(false);
</script>
<template>
  <div class="card flex justify-content-center">
    <PrimeSidebar v-model:visible="visible">
      <template #container="{ closeCallback }">
      </template>
    </PrimeSidebar>
    <PrimeButton icon="pi pi-bars" @click="visible = true" />
  </div>
  <div class="dashboard">
    <div class="container">
      <div class="tile first-column">
          <ApiFilterContainer></ApiFilterContainer>
      </div>
      <div class="second-column">
        <div class="second-column-content">
          <div class="tile map-content">
            <Map></Map>
          </div>
          <div class="tile second-row-content">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  width: 100%;
  height: calc(100vh - 50px);
  overflow: auto;
  margin: 20px;
}

.container {
  display: grid;
  grid-template-columns: 1fr 3fr;
  gap: 10px;
  height: 100%;
}

.tile {
  border-radius: 20px;
  padding: 15px;
  background-color: rgb(255, 255, 255);
  margin: 20px;
  box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.2);
}

.first-column {
  display: flex;
  flex-direction: column;
}

.second-column {
  display: flex;
  flex-direction: column;
}

.second-column-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.map-content {
  flex: 8; /* Erste Zeile mit 8/10 Höhe */
}

.second-row-content {
  flex: 1; /* Zweite Zeile mit 2/10 Höhe */
}
</style>
