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
//const rectangles = ref([]);

/*
const props = defineProps({
  project_settings: Object,
  index: Number
});
*/

const props = defineProps({
  currentData: {
    type: Object,
    default: () => ({data: {}})
  },
});

watch(() => props.currentData, (newValue, oldValue) => {
  if (Array.isArray(newValue)) {
    newValue.forEach(dataElement => {
      updateEarthquakes(dataElement);
    });
  }
}, {
  deep: true
});

/*
const selectedApiEndpoint = computed(() => {
  const keys = Object.keys(props.project_settings.api_endpoints);
  if (props.index < keys.length) {
    const key = keys[props.index];
    return {key, endpoint: props.project_settings.api_endpoints[key]};
  }
  return {key: null, endpoint: null};
});

const apiEndpointName = computed(() => selectedApiEndpoint.value.key);
*/

/*

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
*/

const g = ref(null);


onMounted(() => {
  /*
  EventBus.on('add-rectangle', (params) => {
    addOrUpdateRectangle(params.id, params.minLatitude, params.maxLatitude, params.minLongitude, params.maxLongitude, params.color);
  });
  */
  const container = mapContainer.value;
  if (container) {
    const parentElement = container.parentElement;
    const parentWidth = parentElement.clientWidth;
    const parentHeight = parentElement.clientHeight;

    projection.scale((150 * Math.min(parentWidth / 1200, parentHeight / 750)))
        .translate([parentWidth / 2, parentHeight / 4]);

    const svg = d3.select(container).append('svg').attr('width', '100%').attr('height', '100%');
    //const g = svg.append('g');
    g.value = svg.append('g');

    d3.json('https://gist.githubusercontent.com/d3noob/5193723/raw/world-110m2.json').then(topology => {
      svg.selectAll('path').data(topojson.feature(topology, topology.objects.countries).features).enter().append('path').attr('d', path);
    });

    const zoom = d3.zoom().scaleExtent([1, 8]).on("zoom", (event) => {
      g.value.attr('transform', event.transform);
    });
    svg.call(zoom);
  }
});

// Funktionen zur Festlegung von Kreisfüllungen, Strichfarben und Opazität
function getCircleFill(d) {
  return "#f07178"; // Farbe basierend auf Magnitude
}
function getCircleStrokeColor(d) {
  return "#f07178"; // Stroke-Farbe entspricht der Füllfarbe
}
function getCircleOpacity(d) {
  // Die Opazität basiert auf der Magnitude exponentiell
  var minOpacity = 30; // Mindestopazität (5%)
  var maxOpacity = 100; // Maximale Opazität (80%)
  var exponent = 1.5; // Exponent zur Steuerung des Verlaufs
  var opacity = minOpacity + (maxOpacity - minOpacity) * (1 - Math.exp(-exponent * d.properties.mag));
  return opacity;
}

// Funktion zur Berechnung der Kreisgröße basierend auf dem Zoomfaktor
function getCircleRadius(d, zoomScale) {
  return (d.properties.mag * 2) / zoomScale;
}

// Funktion zum Anzeigen des Tooltips beim Mouseover
function showTooltip(d) {
  var circ = d3.select(this);
  var year = new Date(d.properties.time);
  circ.attr("class", "mouseover");
  tip.html(`<span>Place: ${d.properties.place}</span></br><span>Magnitude: ${d.properties.mag}</span></br><span>Time: ${year.toUTCString()}</span></br><span>Latitude: ${d.geometry.coordinates[1]}</span></br><span>Longitude: ${d.geometry.coordinates[0]}</span>`);
  tip.transition()
      .attr("class", "tooltip")
      .delay(100)
      .style("opacity", 0.9);
  tip.style("left", d3.event.pageX + 5 + "px")
      .style("top", d3.event.pageY - 25 + "px");
}

// Funktion zum Ausblenden des Tooltips beim Mouseout
function hideTooltip() {
  var circ = d3.select(this);
  circ.attr("class", "mouseout");
  tip.transition()
      .delay(100)
      .style("opacity", 0);
}


var colors = {
  mainColor_1: "#282d3f",
  mainColor_2: "#30364c",
  color_1: "#f07178", //Rot
  color_2: "#c3e88d", //Grün
  color_3: "#ffcb6b", //Orange
  color_4: "#82aaff", //Blau
  color_5: "#c792ea", //Rosa
  color_6: "#89ddff", //Hellblau
  color_7: "#d53f51", //Magenta
  color_8: "#5cafa8", //Türkis
  color_9: "#ea7e62", //Strange Orange
};

function updateEarthquakes(data) {

  data.features.sort(function (a, b) {
    return a.properties.mag - b.properties.mag;
  });

  //g.value.selectAll("circle").remove();

  g.value.selectAll("circle")
      .data(data.features)
      .enter()
      .append("circle")
      .attr("cx", function (d) {
        var coords = projection([d.geometry.coordinates[0], d.geometry.coordinates[1]]);
        return coords[0];
      })
      .attr("cy", function (d) {
        var coords = projection([d.geometry.coordinates[0], d.geometry.coordinates[1]]);
        return coords[1];
      })
      .attr("r", function (d) {
        return getCircleRadius(d, 1);
      })
      .style("fill", function (d) {
        var circleFill = getCircleFill(d);
        var opacity = 0.2;
        return `rgba(${d3.rgb(circleFill).r}, ${d3.rgb(circleFill).g}, ${d3.rgb(circleFill).b}, ${opacity})`;
      })
      .style("stroke", getCircleStrokeColor)
      .style("stroke-opacity", 1)
      .style("stroke-width", "0.1em")
      .style("opacity", function (d) {
        return getCircleOpacity(d) / 100;
      });

  g.value.selectAll("circle")
      .on("mouseover", showTooltip)
      .on("mouseout", hideTooltip);


}



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
