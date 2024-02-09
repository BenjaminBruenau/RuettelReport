
export const fusionAuthConfig = {
    apiKey: 'FbX31ng685J3e3Fcy4xWaDcDPUg-PMwgyin_RVHGPLnUKbXuG3ZxuUVT',
    baseURL: `http://${useRuntimeConfig().fusionAuthUrl}`,

    defaultApplicationId: '9643e3b2-9f1e-4f21-8457-7d631bfde25e',

    issuer_free: 'free.ruettelreport.tech',
    issuer_premium: 'premium.ruettelreport.tech',

    key_free: 'ee459c36-fec3-4c58-9ec0-9de12c33731c',
    key_premium: '8cba34fd-a522-4301-a469-8b9a1a8e6249',
    log: true,

    routes: {
        authorizedRedirectUri: ["/dashboard", "/login"],
        logoutUri: '/login',
    },

    tokenTimeToLiveInSeconds: 3600,
};