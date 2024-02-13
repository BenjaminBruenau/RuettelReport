FROM node:21.6-alpine as builder
WORKDIR /app
COPY package.json ./
COPY nuxt.config.ts ./
RUN yarn set version 1.22.19
RUN yarn install
COPY . .
RUN yarn build

FROM node:21.6-alpine

COPY --from=builder /app/.output /ruettel-ui/.output

WORKDIR /ruettel-ui/.output

ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000
EXPOSE 3000
EXPOSE 3001

CMD ["node", "/ruettel-ui/.output/server/index.mjs"]