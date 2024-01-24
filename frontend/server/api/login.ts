import { FusionAuthClient } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';


export default eventHandler(async (event) => {
    const body = await readBody(event);
    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const clientResponse = await client.login({
            loginId: body.username,
            password: body.password,
            applicationId: body.applicationId,
        });

        if(fusionAuthConfig.log){
            console.log("Login Erfolg:", clientResponse.response);
        }

        if (clientResponse.response.token) {
            setCookie(event,'rrAuthToken', clientResponse.response.token);
        }

        return { login: true, user: clientResponse.response.user, token: clientResponse.response.token };

    } catch (error) {
        console.error("Login Fehler:", error);
        if (error.response) {
            console.error("Fehlerdetails:", JSON.stringify(error.response, null, 2));
        }
        return { login: false };
    }
});