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
    newValue.forEach((dataElement, index) => {
      if (dataElement && dataElement.data && dataElement.color) {
        updateEarthquakes(dataElement.data[0], dataElement.color, index);
      }
    });
  }
}, {
  deep: true
});
const g = ref(null);


onMounted(() => {
  const container = mapContainer.value;
  if (container) {
    const parentElement = container.parentElement;
    const parentWidth = parentElement.clientWidth;
    const parentHeight = parentElement.clientHeight;

    projection.scale((150 * Math.min(parentWidth / 1200, parentHeight / 750)))
        .translate([parentWidth / 2, parentHeight / 4]);

    const svg = d3.select(container).append('svg').attr('width', '100%').attr('height', '100%');
    g.value = svg.append('g');

    d3.json('https://gist.githubusercontent.com/d3noob/5193723/raw/world-110m2.json').then(topology => {
      g.value.selectAll('path').data(topojson.feature(topology, topology.objects.countries).features).enter().append('path').attr('d', path);
    });

    // Anwenden des Zooms auf das übergeordnete SVG-Element
    const zoom = d3.zoom().scaleExtent([1, 8]).on("zoom", (event) => {
      svg.selectAll('g').attr('transform', event.transform);
    });
    svg.call(zoom);
  }
});



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


/*

function updateEarthquakes(data, color, index) {
  const group = g.value.selectAll(`.group-${index}`)
    .data([data]) // Binden Sie die Daten an eine Gruppe
    .join("g") // Erstellen oder aktualisieren Sie die Gruppe
    .attr("class", `group-${index}`);

  group.selectAll("circle")
    .data(d => d.features)

* */

function updateEarthquakes(data, color, index) {
  g.value.selectAll(`.group-${index} circle`).remove();

  // Erstellen oder wählen Sie die Gruppe für diese spezielle Datenquelle
  const group = g.value.selectAll(`.group-${index}`)
      .data([data]) // Binden Sie die Daten an die Gruppe
      .join("g") // Erstellen Sie die Gruppe, falls sie nicht existiert
      .attr("class", `group-${index}`); // Weisen Sie eine einzigartige Klasse zu

  // Fügen Sie Kreise innerhalb dieser Gruppe basierend auf den Daten hinzu
  group.selectAll("circle")
      .data(d => d.features)
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
        var opacity = 0.2;
        return `rgba(${d3.rgb(color).r}, ${d3.rgb(color).g}, ${d3.rgb(color).b}, ${opacity})`;
      })
      .style("stroke", color)
      .style("stroke-opacity", 1)
      .style("stroke-width", "0.1em")
      .style("opacity", function (d) {
        return getCircleOpacity(d) / 100;
      });

  // Fügen Sie Interaktionen hinzu, falls benötigt
  group.selectAll("circle")
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
