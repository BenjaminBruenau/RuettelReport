import { FusionAuthClient } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';


export default eventHandler(async (event) => {

    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const clientResponse = await client.login({
            loginId: body.username,
            password: body.password
        });

        if(fusionAuthConfig.log){
            console.log("Login Erfolg:", clientResponse.response);
        }

        return { login: true, user: clientResponse.response.user, token: clientResponse.response.token };

    } catch (error) {
        console.error("Login Fehler:", error);
        return {  login: false };
    }
});