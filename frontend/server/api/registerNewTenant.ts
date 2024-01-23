import { FusionAuthClient, TenantRequest } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';
import { v4 as uuidv4 } from 'uuid';

export default eventHandler(async (event) => {
    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    try {
        // Schritt 1: Erstellen eines neuen Tenants
        const newTenant: TenantRequest = {
            tenant: {
                name: body.tenantName,
            }
        };

        const tenantResponse = await client.createTenant(uuidv4(), newTenant);
        const tenantId = tenantResponse.response.tenant?.id;

        // Schritt 2: Registrieren des Benutzers unter dem neuen Tenant
        const user = {
            tenantId: tenantId,
            username: body.username,
            password: body.password,
            email: body.email,
        };

        const registration = {
            applicationId: fusionAuthConfig.defaultTenent,
        };

        const clientResponse = await client.register(uuidv4(), { user, registration });

        if (fusionAuthConfig.log) {
            console.log("Registrierung Erfolg:", clientResponse.response);
        }

        return { register: true, user: clientResponse.response.user };

    } catch (error) {
        console.error("Fehler bei der Verarbeitung:", error);
        return { register: false };
    }
});
