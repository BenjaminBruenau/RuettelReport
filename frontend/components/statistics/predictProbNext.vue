<template>
  <div>
    <h1 style="font-size: 24px; color: gray;">Predict the probability of the next event occurring in the next {{ value }} seconds.</h1>
    <div class="slider-container">
      <div class="slider-label">0</div>
      <PrimeSlider v-model="value" id="slider" :min="0" :max="360" :step="1" :style="{ width: '50%' }" />
      <div class="slider-label">360</div>
    </div>
    <PrimeButton @click="submit">Predict</PrimeButton>
    <input v-model="probability" type="text" readonly class="probability-field" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const value = ref(50);
const probability = ref('');

const submit = () => {
  // You can calculate the probability here based on the value
  // For now, let's assume a simple calculation
  probability.value = `Probability: ${Math.random().toFixed(2) * 100}%`;
};
</script>

<style scoped>
.slider-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50%;
  margin: 0;
}

.slider-label {
  font-size: 16px;
  color: gray;
}

.probability-field {
  margin-left: 10px;
}
</style>
