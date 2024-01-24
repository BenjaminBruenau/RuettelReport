<template>
  <div>
    <PrimeDataTable :value="data">
      <PrimeColumn field="type" header="Event Type" :sortable="true"></PrimeColumn>
      <PrimeColumn field="count" header="Occurrences" :sortable="true"></PrimeColumn>
      <PrimeColumn field="probability" header="Probability (%)" :sortable="true"></PrimeColumn>
    </PrimeDataTable>
    <div style="height: 20px"></div>
    <div>
      <PrimePanel header="Event Occurrence Probability (in a Row)">

      <div class="input-grid">
        <label for="typeDropdown" class="grid-label">Choose an event type:</label>
        <PrimeDropdown v-model="selectedType" :options="data.map(item => item.type)" class="grid-input" />

        <label for="numberOfTimes" class="grid-label">Enter the number of occurrences:</label>
        <PrimeInputNumber v-model="numberOfTimes" :min="0" :max="1000" class="grid-input" />
      </div>
      <!--
      <label for="typeDropdown" style="font-size: 18px; color: gray;">Choose an event type:</label>
      <PrimeDropdown v-model="selectedType" :options="data.map(item => item.type)" style="padding: 5px; margin: 5px; color: gray; background: transparent;" />

      <label for="numberOfTimes" style="font-size: 18px; color: gray;">Enter the number of occurrences:</label>
      <PrimeInputNumber v-model="numberOfTimes" :min="0" :max="1000" style="padding: 5px; margin: 5px; color: gray; background: transparent;" />
    -->
      <div v-if="selectedType && numberOfTimes !== null && !isNaN(numberOfTimes)">
        <PrimePanel header="Result">
          <b>
            The probability of {{ selectedType }} occurring exactly {{ numberOfTimes }} times in a row is: {{ calculateProbability(selectedType, numberOfTimes) }}%
          </b>
        </PrimePanel>

      </div>
      </PrimePanel>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';



const data = ref([
  { type: "earthquake", count: 33083, probability: 97.3115 },
  { type: "ice quake", count: 355, probability: 1.0442 },
  { type: "quarry blast", count: 329, probability: 0.9677 },
  { type: "explosion", count: 200, probability: 0.5883 },
  { type: "mining explosion", count: 26, probability: 0.0765 },
  { type: "other event", count: 3, probability: 0.0088 },
]);

const columns = [
  { field: 'type', header: 'Type' },
  { field: 'count', header: 'Count' },
  { field: 'probability', header: 'Probability' },
];

const selectedType = ref(null);
const numberOfTimes = ref(5);

const calculateProbability = (type, times) => {
  const selectedRow = data.value.find(item => item.type === type);
  if (selectedRow && times !== null && !isNaN(times)) {
    const n = times;
    const p = selectedRow.probability / 100;
    const k = times;

    const binomialProbability = calculateBinomialProbability(n, k, p);
    return (binomialProbability * 100).toFixed(2);
  }
  return null;
};

const calculateBinomialCoefficient = (a, b) => {
  const factA = calculateFactorial(a);
  const factB = calculateFactorial(b);
  const factAminusB = calculateFactorial(a - b);
  return factA / (factB * factAminusB);
};

const calculateBinomialProbability = (n, k, p) => {
  const binomialCoefficient = calculateBinomialCoefficient(n, k);
  return binomialCoefficient * Math.pow(p, k) * Math.pow(1 - p, n - k);
};

const calculateFactorial = (num) => {
  if (num === 0 || num === 1) {
    return 1;
  }

  let result = 1;
  for (let i = 2; i <= num; i++) {
    result *= i;
  }

  return result;
};
</script>

<style scoped>
.input-grid {
  display: grid;
  grid-template-columns: auto auto;
  gap: 10px;
  align-items: center;
}

.grid-label {
  text-align: left;
  color: gray;
  font-size: 18px;
}

.grid-input {
  padding: 5px;
  margin: 5px;
  color: gray;
  background: transparent;
  width: 100%;
}
</style>