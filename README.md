# Multi-Tenant Authentication System

[![Java 17+](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.java.com)
[![Spring Boot 3.5.5](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org)

Enterprise-grade multi-tenant authentication with PostgreSQL schema isolation, Argon2 password hashing, and OTP verification.

## Features

- Schema-per-tenant data isolation
- Argon2id password hashing
- Email OTP verification
- Stateless RESTful API
- Dynamic schema provisioning

## Quick Start

```bash
# Database
docker run --name postgres-multitenant \
  -e POSTGRES_DB=multitenant_auth \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 -d postgres:15

# Application
git clone https://github.com/gauravgughanedev/multi-tenant-auth-system.git
cd multi-tenant-auth-system
mvn clean install
mvn spring-boot:run
```

## API Usage

All requests require `X-Project-ID` header for tenant identification.

### Authentication Flow
```bash
# Register
POST /auth/register
{"email": "user@example.com", "password": "password"}

# Login
POST /auth/login
{"email": "user@example.com", "password": "password"}

# Verify OTP
POST /auth/verify-otp
{"email": "user@example.com", "otp": "123456"}
```

### User Management
```bash
GET /auth/users              # List users
GET /auth/users/{id}         # Get user
GET /auth/projects/{id}/users # List project users
```

## Multi-Tenant Testing

```bash
# Same email, different tenants
curl -X POST localhost:8080/auth/register \
  -H "X-Project-ID: tenant-a" \
  -d '{"email":"test@example.com", "password":"pass1"}'

curl -X POST localhost:8080/auth/register \
  -H "X-Project-ID: tenant-b" \
  -d '{"email":"test@example.com", "password":"pass2"}'
```

## Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/multitenant_auth
    username: postgres
    password: password
```

## Security

- **Password**: Argon2id (64MB, 3 iterations, 1 thread, 32 bytes)
- **Isolation**: Complete schema separation per tenant
- **Validation**: Jakarta Bean Validation
- **Design**: Stateless, header-based tenant identification

## Status

**Alpha Release** - Core functionality complete, production features in development.

### Completed
- Multi-tenant authentication
- Schema isolation
- OTP verification
- User management

### Coming Soon
- JWT tokens
- RBAC
- Password reset
- SMTP integration

## License

MIT
