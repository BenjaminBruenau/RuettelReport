import { FusionAuthClient } from '@fusionauth/typescript-client';
import { fusionAuthConfig } from './config';
import {readBody} from "h3";


export default eventHandler(async (event) => {

    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const response = await client.validateJWT(body.token);

        console.log(response);

    } catch (error) {
        console.error("Token-Validierungsfehler:", error);
        return {valid: false};
    }
});