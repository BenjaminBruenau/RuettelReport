<template>
  <div>
    <table>
      <thead>
      <tr>
        <!-- Add table headers based on your Spark DataFrame columns -->
        <th v-for="column in columns" :key="column">{{ column }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="row in data" :key="row.type">
        <!-- Replace 'id', 'col1', 'col2', ... with your actual column names -->
        <td v-for="column in columns" :key="column" :style="{ color: 'black' }">
          <!-- Append '%' to the end of the 'probability' column -->
          {{ column === 'probability' ? `${row[column]}%` : row[column] }}
        </td>
      </tr>
      </tbody>
    </table>
    <div>
      <!-- Add text which says "What is the probability of Type x happening the next x times -->
      <h1 style="font-size: 24px; color: gray;">What is the probability of any Type happening the next x numbers of times</h1>

      <!-- Add a dropdown menu for selecting the type -->
      <label for="typeDropdown" style="font-size: 18px; color: gray;">Select the type:</label>
      <select id="typeDropdown" v-model="selectedType" style="padding: 5px; margin: 5px; color: gray; background: transparent;">
        <option v-for="item in data" :key="item.type" :value="item.type">{{ item.type }}</option>
      </select>

      <!-- Add an input field for the number with gray text and default value of 5 -->
      <label for="numberOfTimes" style="font-size: 18px; color: gray;">Enter the number of times:</label>
      <input type="number" id="numberOfTimes" v-model="numberOfTimes" style="padding: 5px; margin: 5px; color: gray; background: transparent;" :value="numberOfTimes || 5">

      <!-- Display the result based on the input values -->
      <div v-if="selectedType && numberOfTimes !== null && !isNaN(numberOfTimes)">
        <p style="font-size: 18px; color: gray;">
          The calculated probability for the next {{ numberOfTimes }} times for Type {{ selectedType }} is: {{ calculateProbability(selectedType, numberOfTimes) }}%
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Sample data, replace it with your actual Spark DataFrame data
const data = ref([
  { type: "earthquake", count: 1111, probability: 90 },
  { type: "explosion", count: 23, probability: 7 },
  { type: "tsunami", count: 2, probability: 3 },
  // Add more rows as needed
]);

// Extract column names from the first row, replace with your actual column names
const columns = Object.keys(data.value[0]);

// Function to check if a value is numeric
const isNumeric = (value) => !isNaN(parseFloat(value)) && isFinite(value);

// State for the selected type in the dropdown menu
const selectedType = ref(null);

// State for the input field with a default value of 5
const numberOfTimes = ref(5);

// Function to calculate probability based on the input values
const calculateProbability = (type, times) => {
  const selectedRow = data.value.find(item => item.type === type);
  if (selectedRow && times !== null && !isNaN(times)) {
    // Add your calculation logic here, for now, return a placeholder value
    return selectedRow.probability * times;
  }
  return null;
};
</script>

<style scoped>
/* Add your custom styles for the table if needed */
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background-color: gray; /* Set the background color to gray */
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}

/* Set the text color of numbers to gray */
td.gray {
  color: gray;
}
</style>
