import { Server } from 'socket.io'
import { SocketEvent } from "~/server/utils/SocketEvent";
import MongoDataService from "~/server/mongo-data-service";
import FusionAuthService from "~/server/fusion-auth-service";
import { ChangeStreamDocument } from "mongodb";
import { H3Error } from "h3";

export default defineNitroPlugin((nitroApp) => {
    const socketServer = new Server(useRuntimeConfig().public.socketPort, {
        serveClient: false,
        cors: {
            origin: '*',
            credentials: true
        },
        transports: ['websocket', 'polling'],
    })

    // authentication middleware
    socketServer.use(async (socket, next) => {
        try {
            const token = socket.handshake.auth.token;
            const userInformation = await FusionAuthService.validateTokenAndReturnUserInformation(token);

            // Attach the user information to the socket for future use
            socket.userInformation = userInformation;
            console.debug("Authenticated socket connection: ", socket.userInformation)
            next();
        } catch (e) {
            console.error('Socket authentication failed:', e);
            if (e instanceof H3Error) {
                next(e)
            }
            const error = createError({
                statusCode: 401,
                statusMessage: 'Unauthorized',
                data: e
            });
            next(error)
        }
    });

    socketServer.on('connection', async (socket) => {
        let realtimeAnalyticsChangeStream;
        try {
            const userInformation = socket.userInformation

            const collectionsToWatch = Object.values(SocketEvent)

            const change_stream_filter = [
                {
                    $match: {
                        operationType: 'insert',
                        'fullDocument.tenantId': userInformation.tenantId, 'fullDocument.userId': userInformation.userId,
                        'ns.coll': { $in: collectionsToWatch}
                    }
                }
            ]

            realtimeAnalyticsChangeStream = MongoDataService.db.watch(change_stream_filter)
            realtimeAnalyticsChangeStream.on('change', (change: ChangeStreamDocument<Document>) => {
                console.log('Change in Database for user ', socket.userInformation, ':', change);
                socket.emit(change.ns.coll, change.fullDocument);
            });
        } catch (e) {
            console.error('Error while setting up Socket Connection: ', e)
            socket.disconnect()

            if (realtimeAnalyticsChangeStream) {
                realtimeAnalyticsChangeStream.close()
            }
        }

    })
})

/*
Watch single collection:


const insert_filter = [
   {
       $match: {
           operationType: 'insert',
           'fullDocument.tenantId': userInformation.tenantId, 'fullDocument.userId': userInformation.userId,
       }
   } //, 'tenantId': userInformation.tenantId, 'userId': userInformation.userId
]
const changeStream = MongoDataService.db.collection('analytics_magdistr').watch(insert_filter);
changeStream.on('change', (change) => {
   console.log('Change in MongoDB collection for user', query.userId, ':', change);
   // Emit the updated data to the socket connection associated with the user
   socket.emit(SocketEvent.new_analysis_data, change.fullDocument);
});

 */