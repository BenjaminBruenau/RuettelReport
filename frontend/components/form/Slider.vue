<template>



  <div class="grid grid-cols-3 lg:grid-cols-10 gap-2 text-center items-center justify-center pt-[2rem]">
    <!-- First Row -->
    <div class="col-span-2">
      min Latitude
    </div>
    <div class="flex-grow col-span-1 lg:col-span-8 relative">
      <div class="relative">
        <PrimeBadge class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :value="value[1]" :style="{ left: valuePosition1 }"></PrimeBadge>
        <PrimeBadge :value="value[0]" class="-top-[2rem] -translate-x-1/2 bg-primary-1 absolute" :style="{ left: valuePosition0 }"></PrimeBadge>
        <PrimeSlider v-model="value" :min="-90" :max="90" range />
      </div>
    </div>
  </div>




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