<template>
  <div class="map-container">
    <div id="map-container-inner" ref="mapContainer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as d3 from 'd3';
import * as topojson from 'topojson-client';

const projection = d3.geoMercator().center([0, 45]);

const path = d3.geoPath().projection(projection);

const mapContainer = ref(null);

onMounted(() => {
  const container = mapContainer.value;

  if (container) {
    const parentElement = container.parentElement;
    const parentWidth = parentElement.clientWidth;
    const parentHeight = parentElement.clientHeight;

    projection
        .scale((150 * Math.min(parentWidth / 1200, parentHeight / 750)))
        .translate([parentWidth / 2, parentHeight / 4]);

    const svg = d3.select(container).append('svg').attr('width', '100%').attr('height', '100%');

    d3.json('https://gist.githubusercontent.com/d3noob/5193723/raw/world-110m2.json').then(topology => {
      svg
          .selectAll('path')
          .data(topojson.feature(topology, topology.objects.countries).features)
          .enter()
          .append('path')
          .attr('d', path);
    });

    const zoom = d3.zoom()
        .on('zoom', (event, d) => {
          console.log('TRANSFORM: ', event.transform);
          svg.attr('transform', 'translate(' + [event.transform.x, event.transform.y].join(',') + ')scale(' + event.transform.k + ')');

          // Aktualisiere die Dicke des Kreisrands basierend auf dem Zoomfaktor, beibehalte aber den ursprünglichen Kreisdurchmesser
          svg
              .selectAll('circle')
              .style('stroke-width', function (d) {
                return 1 / event.transform.k; // Ändere die Dicke des Kreisrands basierend auf dem Zoomfaktor
              });

          svg.selectAll('path').attr('d', path.projection(projection));
        });

    svg.call(zoom);
  }
});
</script>

<style scoped>
path {
  stroke: #4f546a;
  stroke-width: 0.1em;
  fill: #31364a;
}

.map-container {
  height: 100%;
  width: 100%;
  position: relative;
}

#map-container-inner {
  height: 100%;
  width: 100%;
  position: absolute;
}
</style>
