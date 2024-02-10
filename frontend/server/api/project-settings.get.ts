import FusionAuthService from "~/server/fusion-auth-service";
import MongoDataService from "~/server/mongo-data-service";

export default defineEventHandler(async (event) => {
    const token = getCookie(event, 'rrAuthToken')

    const userInformation = await FusionAuthService.validateTokenAndReturnUserInformation(token)

    try {
        // returns { "acknowledged": true, "insertedId": "65c78a4143b8b3aa92703b63"
        return MongoDataService.getProjectSettings(userInformation.tenantId)
    } catch (e) {
        console.error(e);
        throw createError({
            statusCode: 500,
            statusMessage: 'Internal Server Error',
            message: 'Error while retrieving ProjectSettings'
        });
    }
})