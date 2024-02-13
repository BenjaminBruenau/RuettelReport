

<template>
    <Chart type="bar" :data="chartData" :options="chartOptions" />
</template>


<script setup
        lang="ts">

import Chart from 'primevue/chart';
import { watch } from "vue";

const props = defineProps({
  data: {
    type: Object,
    default: () => {},
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
});

onMounted(() => {
  chartData.value = setChartData();
  chartOptions.value = setChartOptions();
  processData()
});

const chartData = ref();
const chartOptions = ref();

watch(
    () => props.data,
    () => {
      processData()
    }
);
const processData = () => {
  if (!props.data) return


  const labelCounts = {
    '0-2': 0,
    '2-4': 0,
    '4-6': 0,
    '6-8': 0,
    '8-10': 0,
    'Unknown': 0
  };

  Object.keys(labelCounts).forEach(key => {
    const data = props.data[key]
    if (data) {
      labelCounts[key] = data
    }
  })


  // Update the dataset with the counts
  chartData.value.datasets[0].data = Object.values(labelCounts);
}

const setChartData = () => {
  return {
    labels: ['0-2', '2-4', '4-6', '6-8', '8-10', 'Unknown'],
    datasets: [
      {
        label: 'Mag Range Count',
        backgroundColor: '#009b91',
        data: []
      }
    ]
  };
};
const setChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement);
  const textColor = documentStyle.getPropertyValue('--text-color');
  const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
  const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

  return {

    scales: {
      x: {
        type: 'category',
        position: 'bottom',
        title: {
          display: true,
          text: 'Magnitude Range'
        }
      },
      y: {
        type: 'linear',
        position: 'left',
        title: {
          display: true,
          text: 'Count of Events'
        }
      }
    },
    legend: {
      display: false,
    },
    title: {
      display: true,
      text: '',
      font: {
        size: 18,
        weight: 'bold'
      }
    },
    responsive: true,
  };
}
</script>

<style scoped>

</style>