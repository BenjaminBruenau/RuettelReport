// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  css: [
    'primeicons/primeicons.css',
  ],
  pages: true,
  ssr: false,
  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],
  modules: ['@nuxtjs/tailwindcss', 'nuxt-primevue', '@nuxtjs/color-mode'],
  primevue: {
    usePrimeVue: true,
    options: {
      unstyled: true,
      ripple: true,
      inputStyle: 'filled',
    },
    importPT: { as: 'Tailwind', from: 'primevue/passthrough/tailwind' },
    //cssLayerOrder: 'tailwind-base, primevue, tailwind-utilities', // this breaks dark/light mode switch
    components: {
      prefix: 'Prime',
      include: [ 'Button', 'Panel', 'Sidebar', 'InputSwitch' ]    /* Used as <PrimeButton /> and <PrimeDataTable /> */
    }
  },
  colorMode: {
    classSuffix: '',
  },
})