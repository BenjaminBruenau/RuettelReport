import { FusionAuthClient } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';


export default eventHandler(async (event) => {
    const body = await readBody(event);
    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const clientResponse =
            await client.retrieveUserUsingJWT(body.token);

        return { user: clientResponse.response.user};

    } catch (error) {
        console.error("Couldnt acces user data", error);

        return { user: null };
    }
});