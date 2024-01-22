<template>
  <div style="width: 800px; height: 500px;">
    <Bar
        id="my-chart-id"
        :options="chartOptions"
        :data="chartData"
    />
  </div>
</template>

<script>
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {
  data() {
    return {
      dataArray: [
        {
          "_id": "65ad720ff3e20c3d3cab1d16",
          "mag_range": "0-2",
          "count": 2867
        },
        {
          "_id": "65ad720ff3e20c3d3cab1d1a",
          "mag_range": "2-4",
          "count": 1876
        },
        {
          "_id": "65ad720ff3e20c3d3cab1d14",
          "mag_range": "Unknown",
          "count": 11
        },
        {
          "_id": "65ad720ff3e20c3d3cab1d18",
          "mag_range": "4-6",
          "count": 175
        }
      ],
      chartData: {
        labels: ['0-2', '2-4', '4-6', '6-8', '8-10', 'Unknown'],
        datasets: [
          {
            label: 'Mag Range Count',
            backgroundColor: '#f87979',
            data: []
          }
        ]
      },
      chartOptions: {
        responsive: true,
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
          text: 'Bar Chart Example',
          font: {
            size: 18,
            weight: 'bold'
          }
        },
      }
    }
  },
  components: { Bar },
  methods: {
    processData() {
      const labelCounts = {
        '0-2': 0,
        '2-4': 0,
        '4-6': 0,
        '6-8': 0,
        '8-10': 0,
        'Unknown': 0
      };

      this.dataArray.forEach(item => {
        const magRange = item.mag_range;
        const count = parseInt(item.count);
        labelCounts[magRange] += count;
      });

      // Update the dataset with the counts
      this.chartData.datasets[0].data = Object.values(labelCounts);
    },
  },
  created() {
    // Call the method to process the data when the component is created
    this.processData();
  },
  name: 'BarChart',
}
</script>

<style scoped>
/* Add your custom styles here */
</style>
