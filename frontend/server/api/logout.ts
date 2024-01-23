import { FusionAuthClient } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';


export default eventHandler(async (event) => {

    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const clientResponse = await client.logout(true, body.token);

        if(fusionAuthConfig.log){
            console.log("Logout Erfolg:", clientResponse.response);
        }

        return { logout: true };

    } catch (error) {
        console.error("Login Fehler:", error);
        return {  logout: false };
    }

});