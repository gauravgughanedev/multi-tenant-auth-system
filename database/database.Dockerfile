FROM docker.io/postgres:16.10-alpine3.21

COPY 01-init-schemas.sql /docker-entrypoint-initdb.d/

EXPOSE 5432