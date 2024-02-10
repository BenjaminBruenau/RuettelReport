import FusionAuthService from "~/server/fusion-auth-service";
import MongoDataService from "~/server/mongo-data-service";

export default defineEventHandler(async (event) => {
    const token = getCookie(event, 'rrAuthToken')

    const userInformation = await FusionAuthService.validateTokenAndReturnUserInformation(token)

    try {
        return MongoDataService.getLastRuettelReport(userInformation.tenantId)
    } catch (e) {
        console.error(e);
        throw createError({
            statusCode: 500,
            statusMessage: 'Internal Server Error',
            message: 'Error while retrieving Analysis Report'
        });
    }
})