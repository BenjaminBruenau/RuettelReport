import io from 'socket.io-client'


export default defineNuxtPlugin(() => {
    const config = useRuntimeConfig().public
    // `${config.url}:${config.socketPort}`
    const socket = (auth: {token: string | null | undefined}) => {
        console.debug('LOCATION:', window.location);
        const hostname = window.location.hostname //`${hostname}:${config.socketPort}` in develop
        return io(config.wssDevelop ? `${hostname}:${config.socketPort}` : window.location.host, {
            autoConnect: false,
            //query: query,
            transports: ['websocket', 'polling'],
            auth: auth
        })
    }
    return {
        provide: {
            io: socket
        }
    }
})