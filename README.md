# 🔐 Multi-Tenant Authentication System

A robust multi-tenant authentication system built with Spring Boot 3.5.5 and PostgreSQL, featuring schema-based tenant isolation using Hibernate's multi-tenancy capabilities.

## ✨ Features

- **🏢 Multi-Tenant Architecture**: Schema-per-tenant isolation using Hibernate
- **🔒 Secure Authentication**: BCrypt password encryption with Spring Security
- **🌐 RESTful APIs**: Complete user management and authentication endpoints
- **🚀 Auto Schema Creation**: Dynamic database schema creation per tenant
- **📊 Tenant Isolation**: Complete data separation between projects/tenants
- **⚡ Header-Based Routing**: Tenant identification via HTTP headers
- **📝 Comprehensive Logging**: Debug-level logging for troubleshooting
- **✅ Input Validation**: Jakarta validation with custom error messages

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Project A     │    │   Project B     │    │   Project C     │
│   (Schema: A)   │    │   (Schema: B)   │    │   (Schema: C)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │  Application    │
                    │  Layer          │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │  PostgreSQL     │
                    │  Database       │
                    └─────────────────┘
```

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **ORM**: Hibernate with Multi-Tenancy
- **Security**: Spring Security 6
- **Build Tool**: Maven
- **Utilities**: Lombok, Jakarta Validation
- **Containerization**: Docker (for PostgreSQL)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (for PostgreSQL)
- IDE with Lombok support (IntelliJ IDEA, VS Code, Eclipse)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/multitenant-auth.git
cd multitenant-auth
```

### 2. Start PostgreSQL Database

```bash
docker run --name postgres-multitenant \
  -e POSTGRES_DB=multitenant_auth \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15
```

### 3. Build and Run the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```bash
POST /auth/register
Headers: 
  Content-Type: application/json
  X-Project-ID: your-project-id

Body:
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securepassword",
  "firstName": "John",
  "lastName": "Doe",
  "projectId": "project1",
  "role": "USER"
}
```

#### Login User
```bash
POST /auth/login
Headers:
  Content-Type: application/json
  X-Project-ID: your-project-id

Body:
{
  "username": "john_doe",
  "password": "securepassword"
}
```

#### Get All Users (Tenant-specific)
```bash
GET /auth/users
Headers:
  X-Project-ID: your-project-id
```

#### Get User by ID
```bash
GET /auth/users/{userId}
Headers:
  X-Project-ID: your-project-id
```

### Project Management Endpoints

#### Get Project Users
```bash
GET /auth/projects/{projectId}/users
Headers:
  X-Project-ID: your-project-id
```

#### Add User to Project
```bash
POST /auth/projects/{projectId}/users/{userId}?role=ADMIN
Headers:
  X-Project-ID: your-project-id
```

#### Health Check
```bash
GET /auth/health
Headers:
  X-Project-ID: your-project-id
```

## 🧪 Testing the Multi-Tenancy

### Test Tenant Isolation

```bash
# 1. Register user in Project A
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: projectA" \
  -d '{
    "username": "testuser",
    "email": "test@projecta.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# 2. Register same username in Project B (should work due to isolation)
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: projectB" \
  -d '{
    "username": "testuser",
    "email": "test@projectb.com",
    "password": "differentpass",
    "firstName": "Test",
    "lastName": "UserB"
  }'

# 3. Login to Project A
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: projectA" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 4. Try to login to Project B with Project A password (should fail)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: projectB" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 5. Get users from each project (should show different results)
curl -X GET http://localhost:8080/auth/users -H "X-Project-ID: projectA"
curl -X GET http://localhost:8080/auth/users -H "X-Project-ID: projectB"
```

## 📊 Database Schema Verification

Connect to PostgreSQL to verify schema creation:

```bash
# Connect to database
docker exec -it postgres-multitenant psql -U postgres -d multitenant_auth

# List all schemas
\dn

# You should see schemas like: public, projectA, projectB, etc.

# Check tables in a specific schema
\dt "projectA".*

# View data
SELECT * FROM "projectA".users;
SELECT * FROM "projectA".project_users;
```

## ⚙️ Configuration

### Application Properties
The system uses `application.yml` for configuration:

```yaml
spring:
  application:
    name: auth
  datasource:
    url: jdbc:postgresql://localhost:5432/multitenant_auth
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        multiTenancy: SCHEMA
        tenant_identifier_resolver: dev.gauravgughane.code.auth.config.CurrentTenantIdentifierResolver
        multi_tenant_connection_provider: dev.gauravgughane.code.auth.config.MultiTenantConnectionProvider

server:
  port: 8080
```

### Tenant Headers
The system supports multiple ways to specify tenants:
- `X-Tenant-ID`: Basic tenant identification
- `X-Project-ID`: Project-based tenant identification (takes precedence)

If no header is provided, the system defaults to the `public` schema.

## 📁 Project Structure

```
src/
├── main/
│   ├── java/dev/gauravgughane/code/auth/
│   │   ├── AuthApplication.java
│   │   ├── config/
│   │   │   ├── TenantContext.java
│   │   │   ├── TenantFilter.java
│   │   │   ├── CurrentTenantIdentifierResolver.java
│   │   │   ├── MultiTenantConnectionProvider.java
│   │   │   └── SecurityConfig.java
│   │   ├── entity/
│   │   │   ├── BaseUser.java
│   │   │   └── ProjectUser.java
│   │   ├── repository/
│   │   │   ├── BaseUserRepository.java
│   │   │   └── ProjectUserRepository.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── ProjectUserService.java
│   │   ├── controller/
│   │   │   └── AuthController.java
│   │   └── dto/
│   │       ├── AuthRequest.java
│   │       └── AuthResponse.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── (test classes)
```

## 🚨 Troubleshooting

### Common Issues

1. **Application won't start**
   - Check if PostgreSQL is running: `docker ps`
   - Verify port 8080 is available: `lsof -i :8080`
   - Check database connection settings in `application.yml`

2. **Schema not created**
   - Enable SQL logging to see schema creation statements
   - Check application logs for Hibernate multi-tenancy initialization

3. **Tenant isolation not working**
   - Verify HTTP headers are being sent correctly
   - Check `TenantFilter` logs to ensure tenant context is set
   - Confirm schema switching in database logs

4. **Maven build fails**
   - Ensure Java 17 is installed: `java -version`
   - Clean Maven cache: `mvn clean install -U`
   - Check Lombok plugin is enabled in your IDE

### Enable Debug Logging

Add to `application.yml`:
```yaml
logging:
  level:
    dev.gauravgughane.code.auth: DEBUG
    org.hibernate.SQL: DEBUG
    org.springframework.security: DEBUG
```

## 🔒 Security Features

- **Password Encryption**: BCrypt with configurable strength
- **SQL Injection Protection**: JPA/Hibernate parameterized queries
- **CSRF Protection**: Disabled for stateless API (can be enabled)
- **Input Validation**: Jakarta validation annotations
- **Schema Isolation**: Complete data separation between tenants

## 🚀 Performance Considerations

- Connection pooling configured via Spring Boot defaults
- Schema-per-tenant provides better isolation than shared schemas
- Indexes automatically created by Hibernate DDL
- Consider connection pool sizing for high-tenant scenarios

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the BSD-3-Clause License - see the [LICENSE](LICENSE) file for details.

### BSD-3-Clause License Summary
- ✅ Commercial use
- ✅ Modification  
- ✅ Distribution
- ✅ Private use
- ❌ Liability
- ❌ Warranty

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Troubleshooting](#-troubleshooting) section
2. Review the application logs
3. Open an issue on GitHub with:
   - Error message
   - Steps to reproduce
   - Environment details (Java version, OS, etc.)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Hibernate team for multi-tenancy support
- PostgreSQL community for the robust database

---

**⭐ If this project helps you, please consider giving it a star!**
