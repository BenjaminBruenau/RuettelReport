<template>
  <div class="footer"></div>

  <input style="width: 40%" type="range" id="timeSlider" min="0" max="100" step="1" value="0" ref="timeSlider" @input="handleTimeSlider">
  <div style="color: whitesmoke; margin-bottom: .25em" v-if="currentTimeRange.startDate && currentTimeRange.endDate">{{currentTimeRange.startDate.toUTCString()}} - {{currentTimeRange.endDate.toUTCString()}}</div>
  <div style="color: whitesmoke; margin-bottom: .25em" v-if="!currentTimeRange.startDate && !currentTimeRange.endDate">Use the slider to view data</div>
</template>

<script setup lang="ts">
import * as d3 from "d3"
import * as topojson from "topojson-client"

// http://localhost:8080/api/event? or https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&minmagnitude=0&orderby=time&
const apiBaseUrl = 'http://localhost:8080/api/event?'

const getRequestURL = (start: string, end: string) => {
  return `${apiBaseUrl}starttime=${start}&endtime=${end}`
}

const timeSlider = ref(null)
const currentTimeRange = ref({} as { startDate: Date, endDate : Date })

const colors = {
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


// Erstellen einer Farbskala für die Darstellung von Magnituden
const colorScale = d3.scaleLinear()
    .domain([ 0, 9 ]) // Von Magnitude 0 bis 9
    .range([ colors.color_6, colors.color_6 ]); // Farbskala von color_1 zu color_4

// Aktuelles Datum im ISO-Format
const today = new Date().toISOString().split('T')[0];

// URLs für die Erdbeben- und Weltkarten-Daten
const urls = {
  earthquakeData: `https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2023-10-10T00:00:00&endtime=${today}T23:59:59&minmagnitude=0&orderby=time`,
  worldMap: "https://gist.githubusercontent.com/d3noob/5193723/raw/world-110m2.json",
};

// Bildschirmgröße und Skalierungsfaktor
const screenSize = {
  width: window.innerWidth,
  height: window.innerHeight - 10,
};
const scale = Math.min(screenSize.width / 1200, screenSize.height / 750);

// Erstellen einer Mercator-Projektion für die Weltkarte
const projection = d3.geoMercator()
    .center([ 0, 45 ])
    .scale(150 * scale)
    .translate([ screenSize.width / 2, screenSize.height / 4 ]);

// Pfadgenerator für die Projektion
const path = d3.geoPath()
    .projection(projection);




// SVG-Canvas erstellen
const canvas = d3.select("body").append("svg")
    .attr("width", screenSize.width)
    .attr("height", screenSize.height);
const g = canvas.append("g");

// Tooltip-Element für die Anzeige von Informationen zu den Erdbeben
const tip = d3.select("body").append("div");

// Funktionen zur Festlegung von Kreisfüllungen, Strichfarben und Opazität
function getCircleFill(d) {
  return colorScale(d.properties.mag); // Farbe basierend auf Magnitude
}
function getCircleStrokeColor(d) {
  return colorScale(d.properties.mag); // Stroke-Farbe entspricht der Füllfarbe
}
function getCircleOpacity(d) {
  // Die Opazität basiert auf der Magnitude exponentiell
  const minOpacity = 30; // Mindestopazität (5%)
  const maxOpacity = 100; // Maximale Opazität (80%)
  const exponent = 1.5; // Exponent zur Steuerung des Verlaufs
  const opacity = minOpacity + (maxOpacity - minOpacity) * (1 - Math.exp(-exponent * d.properties.mag));
  return opacity;
}

// Funktion zur Berechnung der Kreisgröße basierend auf dem Zoomfaktor
function getCircleRadius(d: unknown, zoomScale: number) {
  return (d.properties.mag * 2) / zoomScale;
}

// Funktion zum Anzeigen des Tooltips beim Mouseover
function showTooltip(event, d) {
  const circ = d3.select(this);
  const year = new Date(d.properties.time);
  circ.attr("class", "mouseover");
  tip.html(`<span>Place: ${d.properties.place}</span></br><span>Magnitude: ${d.properties.mag}</span></br><span>Time: ${year.toUTCString()}</span></br><span>Latitude: ${d.geometry.coordinates[1]}</span></br><span>Longitude: ${d.geometry.coordinates[0]}</span>`);
  tip.transition()
      .attr("class", "tooltip")
      .delay(100)
      .style("opacity", 0.9);
  tip.style("left", event.pageX + 5 + "px")
      .style("top", event.pageY - 25 + "px");
}

// Funktion zum Ausblenden des Tooltips beim Mouseout
function hideTooltip() {
  const circ = d3.select(this);
  circ.attr("class", "mouseout");
  tip.transition()
      .delay(100)
      .style("opacity", 0);
}

// Laden der Weltkarte-Daten und Darstellung der Ländergrenzen
d3.json(urls.worldMap).then(topology => {
  g.selectAll("path")
      .data(topojson.feature(topology, topology.objects.countries).features)
      .enter()
      .append("path")
      .attr("d", path);
});

// Cache für gecachte Daten
const dataCache: any = {};

// Zoom-Funktionalität hinzufügen
const zoom = d3.zoom()
    .on("zoom", function (event, d) {
      console.log('TRANSFORM: ', event.transform)
      g.attr("transform", "translate(" +
          [event.transform.x, event.transform.y].join(",") + ")scale(" + event.transform.k + ")");

      // Aktualisiere die Dicke des Kreisrands basierend auf dem Zoomfaktor, beibehalte aber den ursprünglichen Kreisdurchmesser
      g.selectAll("circle")
          .style("stroke-width", function (d) {
            return 1 / event.transform.k; // Ändere die Dicke des Kreisrands basierend auf dem Zoomfaktor
          });

      g.selectAll("path")
          .attr("d", path.projection(projection));
    });

canvas.call(zoom);

// Zeitschaltknopf-Handler
const handleTimeSlider = (event: any) => {
  const day = parseInt(event.target.value, 10);
  const startDate = new Date("2011-03-09T00:00:00");
  startDate.setDate(startDate.getDate() + day);
  const endDate = new Date(startDate);
  endDate.setDate(endDate.getDate() + 1);

  const startTime = startDate.toISOString();
  const endTime = endDate.toISOString();

  currentTimeRange.value = {
    startDate: startDate,
    endDate: endDate
  }

  // Überprüfen, ob Daten im Cache vorhanden sind
  if (dataCache[startTime]) {
    console.log('CACHE HIT')
    updateEarthquakes(dataCache[startTime]);
  } else {
    // API-Anfrage durchführen und Daten im Cache speichern
    const updatedUrl = getRequestURL(startTime, endTime);

    d3.json(updatedUrl).then( (data) => {
      dataCache[startTime] = data; // Daten im Cache speichern
      updateEarthquakes(data);
    });
  }
};


function updateEarthquakes(data) {
  // Sortieren Sie die Daten nach Magnitude in aufsteigender Reihenfolge
  data.features.sort(function (a, b) {
    return a.properties.mag - b.properties.mag;
  });

  // Vor dem Hinzufügen neuer Kreise, entferne die alten
  g.selectAll("circle").remove();

  // Füge die neuen Kreise ein
  g.selectAll("circle")
      .data(data.features)
      .enter()
      .append("circle")
      .attr("cx", function (d) {
        const coords = projection([ d.geometry.coordinates[0], d.geometry.coordinates[1] ]);
        return coords[0];
      })
      .attr("cy", function (d) {
        const coords = projection([ d.geometry.coordinates[0], d.geometry.coordinates[1] ]);
        return coords[1];
      })
      .attr("r", function (d) {
        return getCircleRadius(d, 1); // Startgröße entsprechend eines Zoomfaktors von 1
      })
      .style("fill", function (d) {
        const circleFill = getCircleFill(d);
        const opacity = 0.2; // 20% Opazität
        return `rgba(${d3.rgb(circleFill).r}, ${d3.rgb(circleFill).g}, ${d3.rgb(circleFill).b}, ${opacity})`;
      })
      .style("stroke", getCircleStrokeColor)
      .style("stroke-opacity", 1)
      .style("stroke-width", "0.1em")
      .style("opacity", function (d) {
        return getCircleOpacity(d) / 100; // Opazität als Bruchteil von 1 (0-1)
      });

  // Füge die Event-Handler für die Tooltip-Funktionalität hinzu
  g.selectAll("circle")
      .on("mouseover", showTooltip)
      .on("mouseout", hideTooltip);
}

</script>

<style>
body {
  text-align: center;
  background: linear-gradient(to bottom right, #292d3e, #292d3e);
}

path {
  stroke: #4f546a;
  stroke-width: 0.1em;
  fill: #31364a;
  /*filter: drop-shadow(0 0 0.2em rgba(0, 0, 0, 0.25)); */ /* Hier wird der Schatten hinzugefügt */
}

/*
  g {
    filter: drop-shadow(10px 10px 10px rgba(0, 0, 0,0.1));
  }
*/

.tooltip{
  position: absolute;
  background-color: #282d3f;
  text-align: left;
  padding: 5px;
  border-radius: 5px;
  border: 1px solid #4a4d6b;
  opacity: 0.8;
  color: #8a99c4;
}
</style>