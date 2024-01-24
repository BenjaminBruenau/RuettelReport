<template>
  <div>
    <h1 style="font-size: 24px; color: gray;">Predict the probability of an event occurring between the selected dates.</h1>
    <div class="date-picker-container">
      <PrimeCalendar
          showIcon
          iconDisplay="input"
          v-model="startDate"
          :defaultDate="new Date()"
      />
      <PrimeCalendar
          showIcon
          iconDisplay="input"
          v-model="endDate"
          :defaultDate="new Date()"
      />
    </div>
    <div>
      Probability: <span>{{ probability }}</span>%
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const startDate = ref(new Date());
const endDate = ref(new Date());
const probability = ref('');

const mean = ref(281.2525);
const lambda = ref(1 / 281.2525); // Adjust the rate parameter as needed

// Function to calculate the difference in days
const calculateDaysDifference = (start, end) => {
  const MS_PER_DAY = 1000 * 60 * 60 * 24;
  const utcStart = Date.UTC(start.getFullYear(), start.getMonth(), start.getDate());
  const utcEnd = Date.UTC(end.getFullYear(), end.getMonth(), end.getDate());
  return (utcEnd - utcStart) / MS_PER_DAY;
};

// Function to calculate probability
const calculateProbability = () => {
  const days = calculateDaysDifference(startDate.value, endDate.value);
  const cumulativeProbability = 1 - Math.exp(-lambda.value * days);
  probability.value = (cumulativeProbability * 100).toFixed(2);
};

// Watchers to update probability when dates change
watch(startDate, calculateProbability);
watch(endDate, calculateProbability);

// Initial calculation
calculateProbability();
</script>

<style scoped>
.date-picker-container {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

/* Additional styles if needed */
</style>
