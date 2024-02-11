// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  nitro: {
    plugins: [
      './plugins/socket.io.server'
    ]
  },
  plugins: [
    './plugins/socket.io'
  ],
  devtools: { enabled: true },
  runtimeConfig: {
    mongoConnectionUri: '', // can be overridden by NUXT_MONGO_CONNECTION_URI environment variable
    fusionAuthApiKey: '',
    fusionAuthUrl: '',
    accessTokenSigningKeyFree: '',
    accessTokenSigningKeyPremium: '',
    public: {
      socketPort: 3001,
      url: 'http://localhost'
    }
  },
  css: [
    'primeicons/primeicons.css',
    'primevue/resources/themes/soho-light/theme.css',
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
    /*
    '/api/**': {
      proxy: { to: `http://localhost:8080/api/**`, },
    },
    */

    '/auth/**': {
      proxy: { to: `http://${process.env.NUXT_FUSION_AUTH_URL}/**`, },
    }


  },
  modules: ['@nuxtjs/tailwindcss', 'nuxt-primevue', '@nuxtjs/color-mode','@pinia/nuxt'],
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
