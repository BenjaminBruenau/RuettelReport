<template>



  <div class="grid grid-cols-3 xl:grid-cols-10 gap-2 items-center justify-center pt-[2rem]">
    <div class="col-span-1 xl:col-span-2 text-left mr-2">
      min Latitude
    </div>
    <div class="col-span-2 xl:col-span-8">
      <div class="relative">
        <PrimeBadge class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :value="value[1]" :style="{ left: valuePosition1 }"></PrimeBadge>
        <PrimeBadge :value="value[0]" class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :style="{ left: valuePosition0 }"></PrimeBadge>
        <PrimeSlider v-model="value" :min="-90" :max="90" range />
      </div>
    </div>
  </div>

  <table class="border-separate border-spacing-2">
    <colgroup>
      <col>
      <col>
      <col>
    </colgroup>
    <tbody>
    <tr>
      <td class="w-1/4 mr-2"><label for="username">
        min Latitude
      </label></td>
      <td>
        <div class="relative">
          <PrimeBadge class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :value="value[1]" :style="{ left: valuePosition1 }"></PrimeBadge>
          <PrimeBadge :value="value[0]" class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :style="{ left: valuePosition0 }"></PrimeBadge>
          <PrimeSlider v-model="value" :min="-90" :max="90" range />
        </div>
      </td>
    </tr>
    <tr>
      <td><label for="username">
        max Latitude
      </label></td>
      <td class="w-3/4">
        <div class="relative">
          <PrimeBadge class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :value="value[1]" :style="{ left: valuePosition1 }"></PrimeBadge>
          <PrimeBadge :value="value[0]" class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :style="{ left: valuePosition0 }"></PrimeBadge>
          <PrimeSlider v-model="value" :min="-90" :max="90" range />
        </div>
      </td>
    </tr>
    </tbody>
  </table>




</template>

<script setup lang="ts">
import { computed } from "vue";

const props = defineProps({
  min: {
    type: Number,
    default: -90,
  },
  max: {
    type: Number,
    default: 90,
  },
})


const value = ref([-90,90]);

const valuePosition0 = computed(() => {
  const percentage = ((value.value[0] - props.min) / (props.max - props.min)) * 100;
  return `${percentage}%`;
});

const valuePosition1 = computed(() => {
  const percentage = ((value.value[1] - props.min) / (props.max - props.min)) * 100;
  return `${percentage}%`;
});

</script>


<style scoped>

</style>