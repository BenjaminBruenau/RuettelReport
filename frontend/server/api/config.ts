
export const fusionAuthConfig = {
    apiKey: useRuntimeConfig().fusionAuthApiKey,
    baseURL: `http://${useRuntimeConfig().fusionAuthUrl}`,

    defaultApplicationId: '9643e3b2-9f1e-4f21-8457-7d631bfde25e',

    issuer_free: 'free.ruettelreport.tech',
    issuer_premium: 'premium.ruettelreport.tech',

    key_free: useRuntimeConfig().accessTokenSigningKeyIdFree,
    key_premium: useRuntimeConfig().accessTokenSigningKeyIdPremium,
    log: true,

    routes: {
        authorizedRedirectUri: ["/dashboard", "/login"],
        logoutUri: '/login',
    },

    tokenTimeToLiveInSeconds: 3600,
};