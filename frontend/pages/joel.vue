<script setup
        lang="ts">



const selectedCities = ref();
const groupedCities = ref([
  {
    label: 'Germany',
    code: 'DE',
    items: [
      { label: 'Berlin', value: 'Berlin' },
      { label: 'Frankfurt', value: 'Frankfurt' },
      { label: 'Hamburg', value: 'Hamburg' },
      { label: 'Munich', value: 'Munich' }
    ]
  },
  {
    label: 'USA',
    code: 'US',
    items: [
      { label: 'Chicago', value: 'Chicago' },
      { label: 'Los Angeles', value: 'Los Angeles' },
      { label: 'New York', value: 'New York' },
      { label: 'San Francisco', value: 'San Francisco' }
    ]
  },
  {
    label: 'Japan',
    code: 'JP',
    items: [
      { label: 'Kyoto', value: 'Kyoto' },
      { label: 'Osaka', value: 'Osaka' },
      { label: 'Tokyo', value: 'Tokyo' },
      { label: 'Yokohama', value: 'Yokohama' }
    ]
  }
]);


const value = ref([-90,90]);

</script>

<template>
      <!--<PrimeMultiSelect v-model="selectedCities" :options="groupedCities" optionLabel="label" optionGroupLabel="label" optionGroupChildren="items" display="chip" placeholder="Select Cities" class="w-full md:w-20rem">
        <template #optiongroup="slotProps">
          <div class="flex align-items-center">
            <img :alt="slotProps.option.label" src="https://primefaces.org/cdn/primevue/images/flag/flag_placeholder.png" :class="`flag flag-${slotProps.option.code.toLowerCase()} mr-2`" style="width: 18px" />
            <div>{{ slotProps.option.label }}</div>
          </div>
        </template>
      </PrimeMultiSelect>-->



      <PrimeAccordion :activeIndex="0" :multiple="true">

        <PrimeAccordionTab header="Header I">







          <Slider></Slider>





        </PrimeAccordionTab>
        <PrimeAccordionTab header="Location">
          <PrimeAccordion>
          <PrimeAccordionTab header="Rectangle">

          <table>
            <colgroup>
              <col>
              <col>
              <col>
            </colgroup>
            <tbody>
            <tr>
              <td><label for="username">
                min Latitude
              </label></td>
              <td><PrimeSlider v-model="value" :min="-90" :max="90" range  /></td>
              <td><div>
                {{value[0]}}, {{value[1]}}
              </div></td>
            </tr>
            <tr>
              <td><label for="username">
                max Latitude
              </label></td>
              <td><PrimeSlider v-model="value" :min="-90" :max="90" range  /></td>
              <td><div>
                {{value[0]}}, {{value[1]}}
              </div></td>
            </tr>
            <tr>
              <td><label for="username">
                min Longitude
              </label></td>
              <td><PrimeSlider v-model="value" :min="-90" :max="90" range  /></td>
              <td><div>
                {{value[0]}}, {{value[1]}}
              </div></td>
            </tr>
            <tr>
              <td><label for="username">
                max Longitude
              </label></td>
              <td><PrimeSlider v-model="value" :min="-90" :max="90" range  /></td>
              <td><div>
                {{value[0]}}, {{value[1]}}
              </div></td>
            </tr>
            </tbody>
          </table>
          </PrimeAccordionTab>
            </PrimeAccordion>
        </PrimeAccordionTab>
      </PrimeAccordion>
</template>

<style>

table {
  width: 100%;
  border-collapse: collapse;
  table-layout: auto;
}
th, td {
  padding: 8px;
  text-align: left;
  white-space: nowrap;
}
col:first-child, col:nth-child(3) {
  width: 1%;
  white-space: nowrap;
}
col:nth-child(2) {
  width: 100%;
}

</style>