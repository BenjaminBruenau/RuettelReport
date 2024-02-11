import io from 'socket.io-client'


export default defineNuxtPlugin(() => {
    const config = useRuntimeConfig().public
    // `${config.url}:${config.socketPort}`
    const socket = (auth: {token: string | null | undefined}) => {
        console.debug('LOCATION:', window.location);
        const hostname = window.location.hostname
        return io(`${hostname}:${config.socketPort}`, {
            autoConnect: false,
            //query: query,
            auth: auth
        })
    }
    return {
        provide: {
            io: socket
        }
    }
})