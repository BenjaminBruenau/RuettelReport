import { FusionAuthClient } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';

export default eventHandler(async (event) => {
    const body = await readBody(event);
    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const validateResponse = await client.validateJWT(body.token);

        console.log('VALIDATERESPONSE',validateResponse);

        if (validateResponse.response.jwt) {
            console.log("JWT Validierung erfolgreich", validateResponse.response);
            return { valid: true, jwt: validateResponse.response.jwt };
        } else {
            console.log("JWT Validierung fehlgeschlagen");
            return { valid: false };
        }

    } catch (error) {
        console.error("Validierungsfehler:", error);
        if (error.response) {
            console.error("Fehlerdetails:", JSON.stringify(error.response, null, 2));
        }
        return { valid: false };
    }
});
