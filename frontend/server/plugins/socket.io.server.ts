import { Server } from 'socket.io'
import { SocketEvent } from "~/server/utils/SocketEvent";
import MongoDataService from "~/server/mongo-data-service";
import FusionAuthService from "~/server/fusion-auth-service";

export default defineNitroPlugin((nitroApp) => {
    const socketServer = new Server(useRuntimeConfig().public.socketPort, {
        serveClient: false,
        cors: {
            origin: '*',
            credentials: true
        }
    })

    socketServer.on('connection', async (socket) => {
        console.log(socket.handshake.query)
        const query = socket.handshake.query
        //socket.emit(SocketEvent.new_analysis_data, 51)

        try {
            const userInformation = await FusionAuthService.validateTokenAndReturnUserInformation(query.token)
            console.log(userInformation)

            const insert_pipeline = [
                {$match: {operationType: 'insert', 'tenantId': userInformation.tenantId, 'userId': userInformation.userId}}
            ]

            const changeStream = MongoDataService.db.collection('analytics_magdistr').watch();
            changeStream.on('change', (change) => {
                console.log('Change in MongoDB collection for user', query.userId, ':', change);
                // Emit the updated data to the socket connection associated with the user
                socket.emit(SocketEvent.new_analysis_data, change.fullDocument);
            });
        } catch (e) {
            console.error('Error while setting up Socket Connection: ', e)
            socket.disconnect()
        }

    })
})