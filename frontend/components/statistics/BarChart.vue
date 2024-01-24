
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
            backgroundColor: '#009b91',
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
    downloadAsPDF(elementId) {
      const element = document.getElementById(elementId);
      const width = element.offsetWidth;
      const height = element.offsetHeight;
      const pdfWidth = width + 10; // Zusätzlicher Rand
      const pdfHeight = height + 10; // Zusätzlicher Rand

      html2canvas(element, {scale: 1}).then(canvas => {
        const imgData = canvas.toDataURL('image/png');
        const pdf = new jsPDF({
          orientation: 'landscape',
          unit: 'px',
          format: [pdfWidth, pdfHeight]
        });

        pdf.addImage({
          imageData: imgData,
          x: 5, // Ein wenig Rand
          y: 5, // Ein wenig Rand
          width: width,
          height: height,
          format: 'PNG'
        });

        pdf.save(`${elementId}.pdf`);
      });
    },
  },
  created() {
    // Call the method to process the data when the component is created
    this.processData();
  },
  name: 'BarChart',
}

import html2canvas from 'html2canvas';
import { jsPDF } from "jspdf";


</script>


<template>
  <div style="width: 100%; height: 100%;">
    <PrimePanel>
      <template #header>
        <div class="flex align-items-right gap-2">
          <PrimeButton icon="pi pi-download" label="PDF"  @click="downloadAsPDF('chart')"/>
        </div>
      </template>
      <template #default>
        <div id="chart">
          <Bar
              id="my-chart-id"
              :options="chartOptions"
              :data="chartData"
          />
        </div>
      </template>
    </PrimePanel>

  </div>
</template>


<style scoped>
/* Add your custom styles here */
</style>
