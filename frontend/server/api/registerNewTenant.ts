
import { FusionAuthClient, TenantRequest, ApplicationRequest } from '@fusionauth/typescript-client';
import { readBody } from 'h3';
import { fusionAuthConfig } from './config';
import { v4 as uuidv4 } from 'uuid';

export default eventHandler(async (event) => {
    const body = await readBody(event);

    const client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL);

    if (!body || !body.tenantName || !body.email || !body.password || (body.premium === undefined || body.premium === null)) {
        throw createError({
            statusCode: 400,
            statusMessage: 'Bad Request',
            message: 'Invalid Tenant Parameters'
        });
    }

    try {
        // Schritt 1: Erstellen eines neuen Tenants
        const newTenant: TenantRequest = {
            tenant: {
                name: (body.premium)?body.tenantName+'_premium':body.tenantName+'_free',
                issuer: (body.premium)?fusionAuthConfig.issuer_premium:fusionAuthConfig.issuer_free,
                jwtConfiguration: {
                    timeToLiveInSeconds: fusionAuthConfig.tokenTimeToLiveInSeconds,
                }
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
                jwtConfiguration: {
                    enabled: true,
                    accessTokenKeyId: body.premium ? fusionAuthConfig.key_premium : fusionAuthConfig.key_free,
                    timeToLiveInSeconds: fusionAuthConfig.tokenTimeToLiveInSeconds
                },
                roles: [
                    {
                        description: "Tenant Administrator",
                        name: "tenant-admin",
                        isDefault: false,
                        isSuperRole: false // Only tenant specific rights, no fusionauth rights
                    },
                    {
                        description: "Tenant User",
                        name: "tenant-user",
                        isDefault: true,
                        isSuperRole: false
                    }
                ]
            }
        }

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
            roles: [
                "tenant-admin",
            ],
        };

        const clientResponse = await client.register(uuidv4(), { user, registration });

        if (fusionAuthConfig.log) {
            console.log("Registrierung Erfolg:", clientResponse.response);
        }

        if (clientResponse.response.token) {
            setCookie(event,'rrAuthToken', clientResponse.response.token);
        }

        return { register: true, token: clientResponse.response.token };

    } catch (error) {
        console.error("Fehler bei der Verarbeitung:", error);
        return { register: false };
    }
});
