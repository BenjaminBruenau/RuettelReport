import { FusionAuthClient, UserRegistration } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';
import { v4 as uuidv4 } from 'uuid';
export default eventHandler(async (event) => {
    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const user = {
            username: body.username,
            password: body.password,
            email: body.email,
        };

        const registration = {
            applicationId: body.applicationId,
        };

        const clientResponse = await client.register(uuidv4(), { user, registration });

        if(fusionAuthConfig.log){
            console.log("Registrierung Erfolg:", clientResponse.response);
        }

        return { register: true, user: clientResponse.response.user };

    } catch (error) {
        console.error("Registrierung Fehler:", error);
        return { register: false };
    }
});