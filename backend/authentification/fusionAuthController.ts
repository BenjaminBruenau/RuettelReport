
import { FusionAuthClient } from '@fusionauth/typescript-client';
import { Request, Response } from 'express';

class FusionAuthController {
    private client: FusionAuthClient;

    constructor() {
        const apiKey = 'FbX31ng685J3e3Fcy4xWaDcDPUg-PMwgyin_RVHGPLnUKbXuG3ZxuUVT';
        const fusionAuthURL = '/auth';
        this.client = new FusionAuthClient(apiKey, fusionAuthURL);
    }

    public async login(req: Request, res: Response) {
        const loginRequest = {
            loginId: req.body.loginId,
            password: req.body.password,
        };

        try {
            const response = await this.client.login(loginRequest);
            res.json(response.response.user);
        } catch (error) {
            res.status(500).send('Anmeldefehler');
        }
    }

}

export default FusionAuthController;
