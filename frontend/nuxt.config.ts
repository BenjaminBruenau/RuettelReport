// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  css: [
    'primeicons/primeicons.css',
    'primevue/resources/themes/soho-light/theme.css'
  ],
  pages: true,
  ssr: false,
  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],
  routeRules: {
    '/api/**': {
      proxy: { to: `http://localhost:8080/api/**`, },
    }
  },
  modules: ['@nuxtjs/tailwindcss', 'nuxt-primevue', '@nuxtjs/color-mode'],
  primevue: {
    usePrimeVue: true,
    options: {
      unstyled: false,
      ripple: true,
      inputStyle: 'filled',
    },
    //importPT: { as: 'Tailwind', from: 'primevue/passthrough/tailwind' },
    importPT: { as: 'PrimevueDesignPreset', from: './assets/presets/primevue-preset.js' },
    cssLayerOrder: 'tailwind-base, primevue, tailwind-utilities', // this breaks dark/light mode switch
    components: {
      prefix: 'Prime',
      include: '*'   /* Used as <PrimeButton /> and <PrimeDataTable /> */
    },
    directives: {
      prefix: '',
      include: [ 'Tooltip' ],
    },
  },
  colorMode: {
    classSuffix: '',
  },
})