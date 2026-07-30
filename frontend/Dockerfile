# syntax=docker/dockerfile:1
# 빌드 컨텍스트는 저장소 루트(docker-compose.yml 기준)를 사용합니다.

# ---- build stage ----
FROM node:20-alpine AS build
WORKDIR /workspace
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install
COPY frontend/. .
RUN npm run build

# ---- runtime stage ----
FROM nginx:1.27-alpine
COPY --from=build /workspace/dist /usr/share/nginx/html
COPY nginx/nginx.docker.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
