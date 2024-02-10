import FusionAuthService from "~/server/fusion-auth-service";
import MongoDataService from "~/server/mongo-data-service";

export default defineEventHandler(async (event) => {
    const token = getCookie(event, 'rrAuthToken')

    const userInformation = await FusionAuthService.validateTokenAndReturnUserInformation(token)
    console.log(userInformation)
    try {
        const [aggregations, magDistribution] =
            await MongoDataService.getRealtimeAnalysisResultsPerQuery(userInformation.tenantId, userInformation.userId)
        return {
            aggregations: aggregations,
            magDistribution: magDistribution
        }
    } catch (e) {
        console.error(e);
        throw createError({
            statusCode: 500,
            statusMessage: 'Internal Server Error',
            message: 'Error while retrieving Analytics Data'
        });
    }
})