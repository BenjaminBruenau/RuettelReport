
import {FusionAuthClient} from '@fusionauth/typescript-client';
import { fusionAuthConfig } from './config';

export default eventHandler(async (event) => {
    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const clientResponse = await client.retrieveUserByEmail(body.userEmail);

        if(fusionAuthConfig.log){
            console.log("retrieveUserByEmail", JSON.stringify(clientResponse.response.user, null, 2));
        }

        return { user: clientResponse.response.user };

    } catch (error) {
        console.error(error);
        return { error: "Fehler beim Abrufen des Benutzers" };
    }
});

