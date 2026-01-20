# Crepusculum Loan Manager API

## Project Overview
A secure, enterprise-grade backend system for managing travel loans. This API powers the Crepusculum platform, enabling administrators to manage borrower lifecycles, track repayments, and enforce security policies while providing borrowers with transparent access to their loan status. It solves the critical problem of securely digitizing informal loan tracking with auditability and robust access control.

## Core Capabilities
*   **Secure Authentication**: Stateless, token-based authentication using JWT with distinct access for Admins and Borrowers.
*   **Role-Based Access Control (RBAC)**: Strict segregation of duties. Admins manage users and financial records; Borrowers have read-only access to their specific data.
*   **Loan Lifecycle Management**: End-to-end handling of borrower registration, guarantor association, loan disbursement, and payment recording.
*   **Hybrid Password Recovery**: 
    *   **Self-Service**: Secure, time-limited token generation for users with verified emails.
    *   **Admin-Assisted**: Secure workflow for offline users, enforcing temporary passwords and mandatory reset on next login.
*   **Reporting**: Automated generation of repayment schedules and PDF exports.

## Architecture & Design
*   **Framework**: Spring Boot 3 (Java 21) for robust, opinionated configuration and rapid development.
*   **Database**: PostgreSQL with Flyway for version-controlled schema migration.
*   **Security Architecture**: Spring Security configured for stateless session management.
    *   **Layering**: Strict Controller-Service-Repository separation. Controllers handle HTTP concerns (DTO mapping), Services contain business logic and transaction boundaries, Repositories handle data access.
    *   **Data Transfer**: Explicit DTOs (Records) used for all API I/O to decouple internal entities from the public API contract.

**Why this structure?**
The layered architecture ensures testability and separation of concerns. Java Records are used for DTOs to ensure immutability and reduce boilerplate.

## Security & Data Integrity
*   **Password Security**: All passwords are hashed using **BCrypt**. No plain-text credentials are ever stored or logged.
*   **Forced Password Rotation**: System enforces a "change password on next login" policy for administrative resets, mitigating the risk of shared temporary credentials.
*   **Explicit Authority Checks**: Security is enforced via `hasAuthority('ROLE_ADMIN')` checks in a central configuration, preferring explicit naming over implicit defaults.
*   **Resiliency**:
    *   **Async Operations**: Email delivery is asynchronous to prevent SMTP latency from blocking user requests or causing timeouts.
    *   **Graceful Degradation**: The system handles "no-email" scenarios gracefully with informative feedback rather than silent failures.
*   **Global Error Handling**: Centralized exception handling (`@ControllerAdvice`) prevents tech stack details from leaking to clients while ensuring consistent API error responses.

## Notable Engineering Decisions
1.  **Admin-Initiated Reset Flow**: To support the reality of borrowers without email addresses, we implemented a specific admin flow that generates a temporary credential and forces rotation. This balances usability with security.
2.  **DTO-First Approach**: We strictly avoid exposing JPA Entities directly. This prevents over-fetching and accidental exposure of sensitive fields (like password hashes).
3.  **Asynchronous Messaging**: Email notifications are decoupled from the request processing thread to ensure high throughput and responsiveness of the API.

## How to Run

### Prerequisites
*   Java 21+
*   PostgreSQL
*   Maven

### Environment Variables
Ensure the following variables are set in your environment or `.env` file:
```bash
DB_PASSWORD=your_db_password
JWT_SECRET=your_secure_256_bit_secret
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
ADMIN_PHONE=initial_admin_phone
ADMIN_PASSWORD=initial_admin_password
SMTP_USERNAME=your_smtp_user
SMTP_PASSWORD=your_smtp_password
```

### Run Command
```bash
./mvnw spring-boot:run
```

## What This Demonstrates
*   **Production Readiness**: Focus on error handling, security headers, and stateless architecture.
*   **Security First**: Implementation of least privilege, secure credential handling, and audit-ready access controls.
*   **Operational Awareness**: Design choices that prioritize system stability (async operations) and data integrity (transactional boundaries).
