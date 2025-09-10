# multi-tenant-auth-system

Multi-Tenant Authentication System 🔐
A robust Spring Boot-based authentication system supporting multiple tenants/projects with separate user management, built with PostgreSQL and Hibernate multi-tenancy.

🌟 Features
Multi-Tenant Architecture - Separate user data for different projects/tenants

Spring Security Integration - Secure authentication and authorization

RESTful APIs - Clean API endpoints for user management

Hibernate Multi-Tenancy - Table-per-tenant strategy implementation

PostgreSQL Support - Robust database backend with Docker support

Password Encryption - BCrypt password hashing for security

Header-Based Tenant Identification - Easy integration with frontend applications

🏗️ Architecture
text
Client → API Gateway → Auth Service → PostgreSQL
            │               │
            │               ├── Tenant 1 Users
            │               ├── Tenant 2 Users
            │               └── Tenant N Users
            │
            └── Headers: X-Tenant-ID, X-Project-ID
📦 Tech Stack
Java 17 - Programming language

Spring Boot 3.2.0 - Application framework

Spring Security - Authentication & authorization

Spring Data JPA - Database operations

Hibernate - ORM with multi-tenancy support

PostgreSQL - Database

Maven - Dependency management

Docker - Containerization

🚀 Quick Start
Prerequisites
Java 17+

Maven 3.6+

Docker & Docker Compose

PostgreSQL 15+
