import { FusionAuthClient } from '@fusionauth/typescript-client';
import jwt from 'jsonwebtoken';
import { fusionAuthConfig } from './config';

export default eventHandler(async (event) => {

    const token = event.headers.authorization?.split(' ')[1];

    if (!token) {
        return { error: "Kein Token bereitgestellt" };
    }

    try {
        // Verifizieren des JWT
        const decoded = jwt.verify(token, fusionAuthConfig.jwtSecret);

        console.log("Token validiert:", decoded);
        return { message: "Benutzer ist eingeloggt" };
    } catch (error) {
        console.error("Token-Validierungsfehler:", error);
        return { error: "Benutzer ist nicht eingeloggt" };
    }
});