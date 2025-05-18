# Build
FROM node:20-alpine as builder
WORKDIR /app

COPY package.json package-lock.json ./

# Cache node_modules to avoid redownloading every time
RUN --mount=type=cache,target=/root/.npm \
    npm ci

COPY . .

# Production build
RUN npm run build -- --configuration=production

# Serve
FROM nginx:alpine
# RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /app/dist/nfse-frontend/browser /usr/share/nginx/html
COPY nginx/default.conf /etc/nginx/conf.d/default.conf