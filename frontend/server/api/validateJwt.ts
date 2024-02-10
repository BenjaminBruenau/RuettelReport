import { readBody } from 'h3';
import FusionAuthService from "~/server/fusion-auth-service";

export default eventHandler(async (event) => {
    const body = await readBody(event);

    return FusionAuthService.validateToken(body.token)

});
