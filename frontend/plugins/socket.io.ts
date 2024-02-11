import io from 'socket.io-client'


export default defineNuxtPlugin(() => {
    const config = useRuntimeConfig().public

    const socket = (query: {tenantId: string, userId: string}) =>  io(`${config.url}:${config.socketPort}`, {
        autoConnect: false,
        query: query
    })
    return {
        provide: {
            io: socket
        }
    }
})