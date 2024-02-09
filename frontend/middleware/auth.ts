
import { defineNuxtRouteMiddleware, useCookie } from 'nuxt/app';
import { useUserStore } from "~/stores/user";

export default defineNuxtRouteMiddleware((to, from) => {
    const token = useCookie('rrAuthToken');
    const applicationIdFromQuery = to.query.applicationId;

    console.log('TO',to.path);
    console.log('FROM',from.path);
    console.log('QUERY',applicationIdFromQuery);

    if (!token || !token.value) {
        navigateTo('/login');
    }

    test_validateJwt();

    async function test_validateJwt() {
        const response = await $fetch('/api/validateJwt', {
            method: 'post',
            body: {
                token: token.value,
            }
        });

        const userStore = useUserStore()

        if (response.valid) {
            if (response.jwt.tid && response.jwt.sub && response.jwt.iss) {
                userStore.tenantId = response.jwt.tid
                userStore.userId = response.jwt.sub
                userStore.type = response.jwt.iss.startsWith('premium') ? 'premium' : 'free'
                userStore.isPremium= response.jwt.iss.startsWith('premium')
                console.log("STORE USER: ", userStore)
            } else {
                console.log("JWT MISSING CLAIMS: ", response.jwt)
            }

            navigateTo(`/${response.jwt.applicationId}/dashboard`);
        } else {
            if(from.path.includes('/dashboard')) {
                navigateTo(`/login`);
            } else {
                if(applicationIdFromQuery){
                    navigateTo(`/login?applicationId=${applicationIdFromQuery}`);
                } else {
                    navigateTo('/login');
                }
            }

        }
    }


});

/*
import { defineNuxtRouteMiddleware, useCookie } from 'nuxt/app';

export default defineNuxtRouteMiddleware((to, from) => {
    const token = useCookie('rrAuthToken');
    const applicationIdFromQuery = to.query.applicationId;



    async function test_validateJwt() {

        if (!token || !token.value) {
            console.log("Token nicht vorhanden, Umleitung wird eingeleitet");
            if (from.path.includes('/dashboard')) {
                console.log("Umleitung von Dashboard zu /login");
                navigateTo('/login');
            } else {
                if(applicationIdFromQuery){
                    console.log("Umleitung mit applicationId", applicationIdFromQuery);
                    navigateTo(`/login?applicationId=${applicationIdFromQuery}`);
                } else {
                    console.log("Umleitung zu /login ohne applicationId");
                    navigateTo('/login');
                }
            }
            return;
        }


        const response = await $fetch('/api/validateJwt', {
            method: 'post',
            body: {
                token: token.value,
            }
        });

        if (response.valid) {
            navigateTo(`/${response.jwt.applicationId}/dashboard`);
        } else {
            navigateTo(`/login`);
        }
    }

    test_validateJwt();
});


import { defineNuxtRouteMiddleware, useCookie } from 'nuxt/app';

export default defineNuxtRouteMiddleware((to, from) => {
    const token = useCookie('rrAuthToken');
    const applicationIdFromQuery = to.query.applicationId;



    async function test_validateJwt() {

        if (!token || !token.value) {
            navigateTo('/def/login');
            return;
        }

        if (!token || !token.value) {

            if (from.path.includes('/dashboard')) {

                console.log('OJOJETOJEOJT0')
                navigateTo('/login');

            } else {

                console.log('OJOJETOJEOJT1')
                navigateTo(`/login${applicationIdFromQuery ? `?applicationId=${applicationIdFromQuery}` : ''}`);

            }
            return;
        } else {
            const response = await $fetch('/api/validateJwt', {
                method: 'post',
                body: {
                    token: token.value,
                }
            });

            if (response.valid) {
                console.log('OJOJETOJEOJT2')
                navigateTo(`/${response.jwt.applicationId}/dashboard`);
            } else {
                console.log('OJOJETOJEOJT3')
                navigateTo(`/login${applicationIdFromQuery ? `?applicationId=${applicationIdFromQuery}` : ''}`);
            }
        }


    }

    test_validateJwt();
});
*/