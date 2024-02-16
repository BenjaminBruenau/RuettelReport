<template>
  <div>
    <h3>Predict the probability of an event occurring between the selected times.</h3>
    <div class="date-picker-container my-5">
      <PrimeCalendar
          showIcon
          iconDisplay="input"
          v-model="startDate"
          :defaultDate="new Date()"
          showTime
      />
      <PrimeCalendar
          showIcon
          iconDisplay="input"
          v-model="endDate"
          :defaultDate="new Date()"
          showTime
      />
    </div>
    <div v-if="probability">
      <PrimePanel header="Result">
        <b v-if="!(correctRange.start.getTime() === correctRange.end.getTime())">
          The probability of an event occurring between <i>{{correctRange.start.toLocaleString()}}</i> and <i>{{correctRange.end.toLocaleString()}}</i> is: {{ probability }}%
        </b>
        <b v-if="correctRange.start.getTime() === correctRange.end.getTime()">
          The probability of an event occurring at exactly <i>{{correctRange.start.toLocaleString()}}</i> is: {{ probability }}%
        </b>
      </PrimePanel>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps({
  mean: {
    type: Number,
    required: true
  },
  std: {
    type: Number,
    required: true
  }
})

const startDate = ref(new Date());
const endDate = ref(new Date());
const probability = ref('');


const lambda = ref(1 / props.mean);

const correctRange = computed(() => {
  return startDate.value.getTime() > endDate.value.getTime() ? { start: endDate.value, end: startDate.value } : { start: startDate.value, end: endDate.value }
})
const calculateTimeDifference = (start: Date, end: Date) => {
  const utcStart = Date.UTC(start.getFullYear(), start.getMonth(), start.getDate(), start.getHours(), start.getMinutes());
  const utcEnd = Date.UTC(end.getFullYear(), end.getMonth(), end.getDate(), end.getHours(), end.getMinutes());
  return Math.abs(utcEnd - utcStart);
};


const calculateProbability = () => {
  const timeDifference = calculateTimeDifference(startDate.value, endDate.value);
  const MS_PER_DAY = 1000 * 60 * 60 * 24;
  const days =  timeDifference / MS_PER_DAY
  const seconds = timeDifference / 1000
  console.debug('Time Difference in days: ', days)
  console.debug('Time Difference in seconds: ', seconds)

  const cumulativeProbability = 1 - Math.exp(-lambda.value * days);
  const probabilityDensity = calculatePDF(seconds, props.mean, props.std)
  const cumulativeDistribution = calculateCDF(seconds, props.mean, props.std)
  console.debug(`Probability density at ${seconds}s: ${probabilityDensity}`);
  console.debug(`Cumulative probability up to ${seconds}s: ${cumulativeDistribution}`);
  console.debug(`Probability of event happening in the next ${seconds} seconds is ${(cumulativeDistribution * 100).toFixed(4)} %`);
  //probability.value = (cumulativeProbability * 100).toFixed(2);
  probability.value = (cumulativeDistribution * 100).toFixed(4);
};

watch(startDate, calculateProbability);
watch(endDate, calculateProbability);

calculateProbability();



// probability density function (PDF)
function calculatePDF(x: number, mean: number, stdDev: number): number {
  const coefficient = 1 / (stdDev * Math.sqrt(2 * Math.PI));
  const exponent = -0.5 * Math.pow((x - mean) / stdDev, 2);
  return coefficient * Math.exp(exponent);
}

// cumulative distribution function (CDF)
function calculateCDF(x: number, mean: number, stdDev: number): number {
  return 0.5 * (1 + erf((x - mean) / (stdDev * Math.sqrt(2))));
}

function erf(x: number): number {
  // Error function approximation
  const t = 1 / (1 + 0.5 * Math.abs(x));
  const erf = 1 - t * Math.exp(-x * x - 1.26551223 +
      t * (1.00002368 +
          t * (0.37409196 +
              t * (0.09678418 +
                  t * (-0.18628806 +
                      t * (0.27886807 +
                          t * (-1.13520398 +
                              t * (1.48851587 +
                                  t * (-0.82215223 +
                                      t * (0.17087277))))))))));
  return x >= 0 ? erf : -erf;
}


</script>

<style scoped>
.date-picker-container {
  display: flex;
  justify-content: space-around;
}

/* Additional styles if needed */
</style>
