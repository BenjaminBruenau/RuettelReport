import { defineStore } from "pinia";

export const useUserStore = defineStore('userStore', {
    state: () => ({
        userId: '',
        tenantId: '',
        type: '',
        isPremium: false
    }),
})