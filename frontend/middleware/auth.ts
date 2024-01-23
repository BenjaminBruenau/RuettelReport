import {defineNuxtRouteMiddleware, useCookie} from 'nuxt/app';

export default defineNuxtRouteMiddleware((to, from) => {

    const token = useCookie('rrAuthToken');

    if (!token || !token.value) {
        navigateTo('login');
    }
    async function test_validateJwt() {
        const response = await $fetch('/api/validateJwt', {
            method: 'post',
            body: {
                token: token.value,
            }
        });
        if(response.valid){
            navigateTo('dashboard');
        } else {
            navigateTo('login');
        }
    }

    test_validateJwt();

});