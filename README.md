# Multi-Tenant Authentication System

A secure authentication system built with Spring Boot 3.5.5 and PostgreSQL that provides complete tenant isolation through schema separation. The system implements email/password authentication with optional OTP verification and uses BCrypt for password hashing.

## 🚀 Features

- **Schema-Isolated Multi-Tenancy**: Complete tenant isolation using PostgreSQL schemas
- **BCrypt Password Hashing**: Secure password hashing (Argon2id migration planned)
- **Email-based OTP Verification**: Two-factor authentication with email OTP codes
- **RESTful API Design**: Clean and consistent REST endpoints
- **Dynamic Schema Provisioning**: Automatic tenant schema creation and management
- **Stateless Authentication**: No session-based vulnerabilities
- **Input Validation**: Jakarta Bean Validation for robust data validation
- **PostgreSQL Database**: High-performance relational database with schema support

## 🏗️ Architecture

The system uses a **schema-per-tenant** approach where each tenant operates within its own PostgreSQL schema, ensuring complete data isolation while sharing the same database instance.

```
┌─────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                  │
├─────────────────┬─────────────────┬─────────────────────┤
│   Tenant A      │   Tenant B      │   Tenant C          │
│   Schema        │   Schema        │   Schema            │
│                 │                 │                     │
│ ┌─────────────┐ │ ┌─────────────┐ │ ┌─────────────────┐ │
│ │   users     │ │ │   users     │ │ │     users       │ │
│ │   projects  │ │ │   projects  │ │ │     projects    │ │
│ │   ...       │ │ │   ...       │ │ │     ...         │ │
│ └─────────────┘ │ └─────────────┘ │ └─────────────────┘ │
└─────────────────┴─────────────────┴─────────────────────┘
                           │
              ┌─────────────────────────┐
              │   Spring Boot App       │
              │   (Multi-Tenant Auth)   │
              └─────────────────────────┘
```

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot 3.5.5**
- **PostgreSQL 15**
- **BCrypt** (Password Hashing - Argon2id migration planned)
- **Jakarta Bean Validation**
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Lombok**

## 📋 Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL** (Docker recommended)
- **IDE with Lombok support** (IntelliJ IDEA, Eclipse with Lombok plugin)
- **Docker** (for containerized setup)

## ⚙️ Installation & Setup

### Option 1: Docker Setup (Recommended)

#### 1. Clone the Repository
```bash
git clone https://github.com/gauravgughanedev/multi-tenant-auth-system.git
cd multi-tenant-auth-system
```

#### 2. Docker Compose Setup
The project includes a Docker Compose configuration in the `docker-setup` directory:

```bash
cd docker-setup
docker-compose up -d
```

This will start:
- PostgreSQL database on port `5432`
- The Spring Boot application on port `8080`

### Option 2: Manual Setup

#### 1. Database Setup
```bash
# Start PostgreSQL container
docker run --name postgres-multitenant \
  -e POSTGRES_DB=multitenant_auth \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15
```

#### 2. Application Build & Run
```bash
git clone https://github.com/gauravgughanedev/multi-tenant-auth-system.git
cd multi-tenant-auth-system
mvn clean install
mvn spring-boot:run
```

The application will be available at **http://localhost:8080**

## 🔧 API Documentation

### Important: Tenant Header
All requests **must** include the tenant identifier in the header:
```
X-Project-ID: your-tenant-id
```

### Authentication Flow

#### Step 1: User Registration
```http
POST /auth/register
Content-Type: application/json
X-Project-ID: tenant-a

{
  "email": "user@domain.com",
  "password": "securepassword"
}
```

#### Step 2: Login Request
```http
POST /auth/login
Content-Type: application/json
X-Project-ID: tenant-a

{
  "email": "user@domain.com",
  "password": "securepassword"
}
```

**Response:**
```json
{
  "message": "OTP sent to email",
  "status": "success"
}
```

#### Step 3: OTP Verification
```http
POST /auth/verify-otp
Content-Type: application/json
X-Project-ID: tenant-a

{
  "email": "user@domain.com",
  "otp": "123456"
}
```

**Response:**
```json
{
  "message": "Authentication successful",
  "user": {
    "id": 1,
    "email": "user@domain.com",
    "tenantId": "tenant-a"
  },
  "status": "success"
}
```

### User Management Endpoints

#### List Tenant Users
```http
GET /auth/users
X-Project-ID: tenant-a
```

#### Get User Details
```http
GET /auth/users/{id}
X-Project-ID: tenant-a
```

### Project Management Endpoints

#### List Project Users
```http
GET /auth/projects/{id}/users
X-Project-ID: tenant-a
```

#### Add User to Project
```http
POST /auth/projects/{id}/users/{userId}
X-Project-ID: tenant-a
```

## 🔐 Security Features

### Password Security
The system currently uses **BCrypt** for secure password hashing with Spring Security's default configuration.

> **🔄 Migration Planned**: The system will be upgraded to use **Argon2id** with the following parameters:
> - **Memory**: 64 MB
> - **Iterations**: 3
> - **Parallelism**: 1
> - **Hash length**: 32 bytes

### Multi-Tenant Isolation
- Complete tenant data isolation through PostgreSQL schemas
- Cross-tenant data access prevented at application level
- Schema-based separation ensures data privacy

### OTP Security
- Email-based OTP verification
- Time-limited OTP codes
- Development: OTP codes printed to console
- Production: Integrate with SMTP service

### Additional Security Features
- Stateless authentication (no sessions)
- Input validation using Jakarta Bean Validation
- SQL injection prevention through JPA/Hibernate
- XSS protection through proper input/output handling

## 🧪 Testing Multi-Tenant Isolation

Verify tenant isolation by registering identical email addresses across different tenants:

```bash
# Register user in Tenant A
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: tenant-a" \
  -d '{
    "email": "test@example.com",
    "password": "password1"
  }'

# Register same user in Tenant B
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: tenant-b" \
  -d '{
    "email": "test@example.com", 
    "password": "password2"
  }'
```

Both registrations will succeed due to schema isolation! ✅

## 🧪 Testing & Development

### Running Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### Development Mode
During development, OTP codes are printed to the console for easy testing:
```
[INFO] OTP for user@example.com: 123456
```

### API Testing Examples

#### Complete Authentication Flow
```bash
# 1. Register user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: my-tenant" \
  -d '{
    "email": "john@example.com",
    "password": "mySecurePass123"
  }'

# 2. Login (triggers OTP)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: my-tenant" \
  -d '{
    "email": "john@example.com",
    "password": "mySecurePass123"
  }'

# 3. Verify OTP (check console for OTP code)
curl -X POST http://localhost:8080/auth/verify-otp \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: my-tenant" \
  -d '{
    "email": "john@example.com",
    "otp": "123456"
  }'
```

## 🐳 Docker Deployment

### Production Docker Compose
The `docker-setup` directory contains production-ready Docker Compose configuration:

```yaml
# docker-setup/docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: multitenant_auth
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ${DB_PASSWORD:-password}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - multitenant-network

  app:
    build:
      context: ..
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/multitenant_auth
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-password}
    depends_on:
      - postgres
    networks:
      - multitenant-network

volumes:
  postgres_data:

networks:
  multitenant-network:
    driver: bridge
```

### Deployment Commands
```bash
# Build and start services
cd docker-setup
docker-compose up --build -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Remove volumes (careful - this deletes data!)
docker-compose down -v
```

## 📁 Project Structure

```
multi-tenant-auth-system/
├── docker-setup/
│   ├── docker-compose.yml     # Docker Compose configuration
│   └── .env                   # Environment variables
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── dev/gauravgughane/multitenant/
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── controller/      # REST controllers  
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       ├── entity/         # JPA entities
│   │   │       ├── exception/      # Custom exceptions
│   │   │       ├── repository/     # Data repositories
│   │   │       ├── security/       # Security configurations
│   │   │       ├── service/        # Business logic
│   │   │       ├── tenant/         # Multi-tenant management
│   │   │       └── util/           # Utility classes
│   │   └── resources/
│   │       ├── application.yml     # Configuration
│   │       └── schema.sql          # Database schema
│   └── test/                       # Test classes
├── Dockerfile                      # Container definition
├── pom.xml                        # Maven dependencies
└── README.md                      # This file
```

## 🔧 Configuration

### Application Configuration
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/multitenant_auth
    username: postgres
    password: password
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

# Multi-tenant configuration
multitenant:
  tenant-header: X-Project-ID
  schema-prefix: tenant_
  default-schema: public

# Security configuration  
security:
  password:
    encoder: bcrypt    # Current implementation
    # Future Argon2id configuration (planned)
    # argon2:
    #   memory: 65536      # 64 MB
    #   iterations: 3
    #   parallelism: 1
    #   hash-length: 32

# Email/OTP configuration (development)
otp:
  expiry-minutes: 5
  length: 6
  email-enabled: false  # Set to true for production
```

### Environment Variables
```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/multitenant_auth
DB_USERNAME=postgres
DB_PASSWORD=password

# Email (for production)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password

# Application
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080
```

## 🚦 Health & Monitoring

### Health Check Endpoints
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity
curl http://localhost:8080/actuator/health/db

# Application info
curl http://localhost:8080/actuator/info
```

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check if PostgreSQL is running
   docker ps | grep postgres
   
   # Check connection
   psql -h localhost -p 5432 -U postgres -d multitenant_auth
   ```

2. **Schema Not Found Error**
    - Ensure tenant header `X-Project-ID` is included in requests
    - Check if tenant schema exists in database
    - Verify schema naming convention

3. **OTP Not Received**
    - Check console output during development
    - Verify email configuration for production
    - Check OTP expiry time (default: 5 minutes)

4. **Lombok Compilation Issues**
   ```bash
   # Enable annotation processing in your IDE
   # Or build with Maven
   mvn clean compile
   ```

### Debug Logging
```yaml
logging:
  level:
    dev.gauravgughane.multitenant: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Spring Boot best practices
- Write unit tests for new features
- Ensure multi-tenant isolation
- Update documentation

## 📄 License

This project is licensed under the **BSD 3-Clause License** - see the [LICENSE](LICENSE) file for details.

## 📞 Support & Contact

- **Developer**: Gaurav Gughane
- **Email**: [dev@gauravgughane.dev](mailto:dev@gauravgughane.dev)
- **Website**: [gauravgughane.dev](https://gauravgughane.dev)
- **LinkedIn**: [linkedin.com/in/gauravgughane](https://linkedin.com/in/gauravgughane)
- **GitHub Issues**: [Create an Issue](https://github.com/gauravgughanedev/multi-tenant-auth-system/issues)

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **PostgreSQL Community** for the robust database system
- **Argon2** team for secure password hashing
- **Jakarta Bean Validation** for input validation

---

**Made with ❤️ by [Gaurav Gughane](https://gauravgughane.dev)**