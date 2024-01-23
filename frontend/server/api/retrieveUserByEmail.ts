import { FusionAuthClient } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';

export default eventHandler(async (event) => {
    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        const clientResponse = await client.retrieveUserByEmail(body.userEmail);

        if (fusionAuthConfig.log) {
            console.log("retrieveUserByEmail", JSON.stringify(clientResponse.response.user, null, 2));
        }

        return { user: clientResponse.response.user };

    } catch (error) {
        console.error("Fehler beim Abrufen des Benutzers:", error);

        // Detaillierte Fehlermeldung ausgeben
        if (error.response) {
            console.error("Fehlerstatus:", error.response.statusCode);
            console.error("Fehlerdetails:", error.response);
        }

        return { error: "Fehler beim Abrufen des Benutzers" };
    }
});