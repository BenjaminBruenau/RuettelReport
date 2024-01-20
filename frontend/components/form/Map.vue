<template>
  <div class="map-container">
    <div id="map-container-inner" ref="mapContainer"></div>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import * as d3 from 'd3';
import * as topojson from 'topojson-client';
import {EventBus} from '~/components/event-bus.js';

const projection = d3.geoMercator().center([0, 45]);
const path = d3.geoPath().projection(projection);
const mapContainer = ref(null);
const rectangles = ref([]);

const props = defineProps({
  project_settings: Object,
  index: Number
});

const selectedApiEndpoint = computed(() => {
  const keys = Object.keys(props.project_settings.api_endpoints);
  if (props.index < keys.length) {
    const key = keys[props.index];
    return {key, endpoint: props.project_settings.api_endpoints[key]};
  }
  return {key: null, endpoint: null};
});

const apiEndpointName = computed(() => selectedApiEndpoint.value.key);

function drawRectangle(minLat, maxLat, minLon, maxLon, color) {
  const svg = d3.select('#map-container-inner svg');
  const g = svg.select('g');

  const topLeft = projection([minLon, maxLat]);
  const bottomRight = projection([maxLon, minLat]);
  const width = Math.abs(bottomRight[0] - topLeft[0]);
  const height = Math.abs(bottomRight[1] - topLeft[1]);

  g.append('rect')
      .attr('x', topLeft[0])
      .attr('y', topLeft[1])
      .attr('width', width)
      .attr('height', height)
      .attr('stroke', color)
      .attr('stroke-width', 2)
      .attr('fill', 'rgba(255,255,255,0)')

}

function addOrUpdateRectangle(id, minLatitude, maxLatitude, minLongitude, maxLongitude, color) {
  const existingIndex = rectangles.value.findIndex(r => r.id === id);
  const rectangle = {id, minLatitude, maxLatitude, minLongitude, maxLongitude, color};

  if (existingIndex > -1) {
    rectangles.value[existingIndex] = rectangle;
  } else {
    rectangles.value.push(rectangle);
  }

  drawRectangle(minLatitude, maxLatitude, minLongitude, maxLongitude, color);
}

onMounted(() => {
  EventBus.on('add-rectangle', (params) => {
    addOrUpdateRectangle(params.id, params.minLatitude, params.maxLatitude, params.minLongitude, params.maxLongitude, params.color);
  });

  const container = mapContainer.value;
  if (container) {
    const parentElement = container.parentElement;
    const parentWidth = parentElement.clientWidth;
    const parentHeight = parentElement.clientHeight;

    projection.scale((150 * Math.min(parentWidth / 1200, parentHeight / 750)))
        .translate([parentWidth / 2, parentHeight / 4]);

    const svg = d3.select(container).append('svg').attr('width', '100%').attr('height', '100%');
    const g = svg.append('g');

    d3.json('https://gist.githubusercontent.com/d3noob/5193723/raw/world-110m2.json').then(topology => {
      svg.selectAll('path').data(topojson.feature(topology, topology.objects.countries).features).enter().append('path').attr('d', path);
    });

    const zoom = d3.zoom().scaleExtent([1, 8]).on("zoom", (event) => {
      g.attr('transform', event.transform);
    });
    svg.call(zoom);
  }
});
</script>

<style scoped>
::v-deep path {
  stroke: #b9b9b978;
  stroke-width: 0.03em;
  fill: #ffffff5c;
  filter: drop-shadow(10px 10px 10px rgb(0, 0, 0, 0.1));
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
