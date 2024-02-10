import { FusionAuthClient } from "@fusionauth/typescript-client";
import { fusionAuthConfig } from "~/server/api/config";

class FusionAuthService {
    client = new FusionAuthClient(fusionAuthConfig.apiKey, fusionAuthConfig.baseURL)

    async validateToken(token: string | undefined) {
        try {
            if (!token) {
                throw createError({
                    statusCode: 401,
                    statusMessage: 'Unauthorized - Missing Token',
                });
            }
            const validateReponse = (await this.client.validateJWT(token)).response

            if (!validateReponse.jwt) {
                return { valid: false }
            }
            return { valid: true, jwt: validateReponse.jwt }

        } catch (error) {
          console.error("Error while validating Token via FusionAuth: ", error)
          if (error.response) {
              console.error("Error Details:", JSON.stringify(error.response, null, 2));
          }
          return { valid: false }
        }

    }

    async validateTokenAndReturnUserInformation(token: string | undefined) {
        if (!token) {
            throw createError({
                statusCode: 401,
                statusMessage: 'Unauthorized - Missing Token',
            });
        }

        // Throws Unauthorized Error if invalid
        const validateReponse = (await this.client.validateJWT(token)).response

        if (!validateReponse.jwt) {
            throw createError({
                statusCode: 500,
                statusMessage: 'Internal Server Error',
                message: 'Error while validating Token'
            });
        }

        if (!validateReponse.jwt.tid || !validateReponse.jwt.sub) {
            throw createError({
                statusCode: 500,
                statusMessage: 'Internal Server Error',
                message: 'Missing claims in token'
            });
        }

        const userInformation = {
            tenantId: validateReponse.jwt.tid,
            userId: validateReponse.jwt.sub
        }
        return userInformation
    }
}

export default new FusionAuthService()