<template>
  <div>
    <h1 style="font-size: 24px; color: gray;">Predict the probability of the next event occurring in the next {{ value }} seconds.</h1>
    <div class="slider-container">
      <div class="slider-label">0</div>
      <PrimeSlider v-model="value" id="slider" :min="0" :max="3600" :step="1" :style="{ width: '50%' }" />
      <div class="slider-label">3600</div>
    </div>
    <PrimeButton @click="submit">Predict</PrimeButton>
    <PrimeInputText v-model="probability" type="text" readonly class="probability-field" />

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import {erf} from "mathjs";

const value = ref(50);
const probability = ref('');

const mean = ref(281.2525)
const std = ref(8296.2926)
const lambda = ref(1 / 281.2525); // Adjust the rate parameter as needed

const submit = () => {
  // Calculate the probability based on the exponential distribution
  const x = value.value;
  const cumulativeProbability = 1 - Math.exp(-lambda.value * x);

  probability.value = `Probability: ${(cumulativeProbability * 100).toFixed(2)}%`;
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
