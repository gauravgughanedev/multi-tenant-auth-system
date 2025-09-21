Multi-Tenant Authentication System
A secure, schema-isolated multi-tenant authentication system built with Spring Boot 3.5.5 and PostgreSQL. Supports email/password login with optional email-based OTP verification and uses Argon2 for modern password hashing.

Features
Schema-per-Tenant Isolation: Each tenant operates in its own PostgreSQL schema
Argon2 Password Hashing: Industry-leading password security
Email + Password + OTP Auth: Optional second-factor verification via email
Stateless Design: No cookies or sessions; tenant identified via HTTP header
Dynamic Schema Creation: Schemas created automatically on first tenant access
RESTful API: Well-defined endpoints for registration, login, and user management
Header-Based Tenant Routing: Use X-Project-ID to route requests to correct tenant
Prerequisites
Java 17 or higher
Maven 3.6+
Docker (for PostgreSQL)
IDE with Lombok support
Quick Start
1. Clone Repository
bash


1
2
git clone https://github.com/yourusername/multitenant-auth.git
cd multitenant-auth
2. Start PostgreSQL
bash


1
2
3
4
5
6
docker run --name postgres-multitenant \
  -e POSTGRES_DB=multitenant_auth \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15
3. Build and Run
bash


1
2
mvn clean install
mvn spring-boot:run
Application starts at http://localhost:8080

Authentication Flow
Step 1: Login with Email and Password
http


1
2
3
4
5
6
7
8
POST /auth/login
Content-Type: application/json
X-Project-ID: projectA

{
  "email": "user@example.com",
  "password": "yourpassword"
}
→ If credentials are valid, system generates and sends OTP (printed to console in dev).

Step 2: Verify OTP
http


1
2
3
4
5
6
7
8
POST /auth/verify-otp
Content-Type: application/json
X-Project-ID: projectA

{
  "email": "user@example.com",
  "otp": "123456"
}
→ Returns temporary token on success. Replace with JWT in production.

API Endpoints
Authentication
POST
/auth/register
Register new user
POST
/auth/login
Initiate login (sends OTP)
POST
/auth/verify-otp
Verify OTP and complete login

User Management
GET
/auth/users
List all users in tenant
GET
/auth/users/{id}
Get user by ID

Project Management
GET
/auth/projects/{id}/users
List users in project
POST
/auth/projects/{id}/users/{userId}
Add user to project

Configuration
Tenant Identification
Use header X-Project-ID to specify tenant. Example:

http


1
X-Project-ID: acme
If omitted, defaults to public schema.

Password Hashing
Argon2id is used with parameters:

Memory: 64 MB
Iterations: 3
Parallelism: 1
Hash length: 32 bytes
Adjust in Argon2PasswordEncoder.java if needed.

Email OTP (Development)
OTP is printed to console. In production, integrate with SMTP service (JavaMailSender, SendGrid, etc.).

Project Structure


1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
src/
├── main/
│   ├── java/dev/gauravgughane/code/auth/
│   │   ├── config/
│   │   │   ├── Argon2PasswordEncoder.java
│   │   │   ├── CurrentTenantIdentifierResolver.java
│   │   │   ├── MultiTenantConnectionProvider.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── TenantContext.java
│   │   │   └── TenantFilter.java
│   │   ├── controller/
│   │   │   └── AuthController.java
│   │   ├── dto/
│   │   │   ├── AuthRequest.java
│   │   │   └── AuthResponse.java
│   │   ├── entity/
│   │   │   ├── BaseUser.java
│   │   │   └── ProjectUser.java
│   │   ├── repository/
│   │   │   ├── BaseUserRepository.java
│   │   │   └── ProjectUserRepository.java
│   │   └── service/
│   │       ├── EmailService.java
│   │       ├── UserService.java
│   │       └── ProjectUserService.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/...
Testing Multi-Tenancy
Register same email in two tenants — should succeed due to schema isolation.

bash


1
2
3
4
5
6
7
8
9
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: projectA" \
  -d '{"email":"test@example.com", "password":"pass1", ...}'

curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Project-ID: projectB" \
  -d '{"email":"test@example.com", "password":"pass2", ...}'
Security
Passwords hashed with Argon2id
Schema isolation prevents cross-tenant data leaks
Stateless design avoids session fixation attacks
Input validation via Jakarta Bean Validation
License
BSD-3-Clause
