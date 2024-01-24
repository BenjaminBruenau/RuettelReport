import FusionAuthVuePlugin from '@fusionauth/vue-sdk';

export default defineNuxtPlugin((nuxtApp) => {
    nuxtApp.vueApp.use(FusionAuthVuePlugin, {
        clientId: '9643e3b2-9f1e-4f21-8457-7d631bfde25e',
        serverUrl: 'http://localhost:3000/auth',
        redirectUri: 'http://localhost:3000/dashboard',
    });
});
