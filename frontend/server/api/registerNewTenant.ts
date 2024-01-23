/*
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

        // Loggen der Tenant-Erstellung
        if (fusionAuthConfig.log) {
            console.log("Tenant Erstellung Erfolg:", tenantResponse.response);
            if (tenantId) {
                console.log("Neue Tenant-ID:", tenantId);
            }
        }

        console.log("YOOOOOO",tenantId);

        // Schritt 2: Registrieren des Benutzers unter dem neuen Tenant
        const user = {
            tenantId: tenantId,
            username: body.username,
            password: body.password,
            email: body.email,
        };

        const registration = {
            applicationId: fusionAuthConfig.defaultApplicationId,
        };

        const clientResponse = await client.register(uuidv4(), { user, registration });

        // Loggen der Benutzerregistrierung
        if (fusionAuthConfig.log) {
            console.log("Registrierung Erfolg:", clientResponse.response);
        }

        return { register: true, user: clientResponse.response.user };

    } catch (error) {
        console.error("Fehler bei der Verarbeitung:", error);
        return { register: false };
    }
});

*/


import { FusionAuthClient, TenantRequest, ApplicationRequest } from '@fusionauth/typescript-client';
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

        if (fusionAuthConfig.log && tenantId) {
            console.log("Tenant Erstellung Erfolg:", tenantResponse.response);
            console.log("Neue Tenant-ID:", tenantId);
        }

        if (!tenantId) {
            throw new Error('Tenant-Erstellung fehlgeschlagen. Tenant-ID ist undefined.');
        }

        // Schritt 2: Erstellen einer neuen Application im neuen Tenant
        const newApplication: ApplicationRequest = {
            application: {
                name: body.tenantName+'_Application',
            }
        };

        // Setzen des Tenant-Id im Client für die Erstellung der Application
        client.setTenantId(tenantId);
        const applicationResponse = await client.createApplication(uuidv4(), newApplication);
        const applicationId = applicationResponse.response.application?.id;

        if (fusionAuthConfig.log && applicationId) {
            console.log("Application Erstellung Erfolg:", applicationResponse.response);
            console.log("Neue Application-ID:", applicationId);
        }

        if (!applicationId) {
            throw new Error('Application-Erstellung fehlgeschlagen. Application-ID ist undefined.');
        }

        // Schritt 3: Registrieren des Benutzers unter der neuen Application
        const user = {
            tenantId: tenantId,
            username: body.username,
            password: body.password,
            email: body.email,
        };

        const registration = {
            applicationId: applicationId,
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
