import {FusionAuthClient} from '@fusionauth/typescript-client';

export default eventHandler(async (event) => {
    const client = new FusionAuthClient('FbX31ng685J3e3Fcy4xWaDcDPUg-PMwgyin_RVHGPLnUKbXuG3ZxuUVT', 'https://34.65.19.16')

    client.retrieveUserByEmail('test@gmail.com')
        .then(clientResponse => {
            console.log("User:", JSON.stringify(clientResponse.response.user, null, 2));
        }).catch(console.error);

    return client.retrieveUserByEmail('test@gmail.com')
});


