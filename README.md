# BookMyRide 🚌 - *A Full-Featured Bus Booking System*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; BookMyRide is a robust bus booking system built with Java 21 and Spring Boot, following industry-standard clean MVC architecture to ensure modularity and maintainability. It leverages Java core OOP principles for clear separation of concerns across well-defined modules such as user authentication, booking, bus, and location management. The system implements secure, role-based access control, optimistic locking for concurrency, and comprehensive logging for critical actions. It supports guest and registered user bookings with pagination, sorting, and validation utilities, prioritizing data integrity and scalability. This design approach delivers a clean, scalable backend with reusable components, aligned to enterprise-grade development best practices.

## Tech Used
**Tech Stack:** Java 21, Spring Boot 3.2, MySQL 8, Maven, JWT, Lombok, Postman  

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge) 
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Security-yellow?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-3.9.0-red?style=for-the-badge)
![Lombok](https://img.shields.io/badge/Lombok-1.18.28-blueviolet?style=for-the-badge)
![Postman](https://img.shields.io/badge/Postman-API%20Testing-orange?style=for-the-badge)

## Key Features
- **Booking Module:** Allows guests and registered users to book or cancel buses with seat availability checks, optimistic locking, and validation. Only registered users can view their own bookings, while Management users can access all bookings with pagination and sorting.  
- **AppUser Module:** Handles both guest and registered users, including registration, authentication, profile management, and secure JWT-based login. Supports password changes and profile updates.  
- **Management Module:** Contains all authority-level users who manage data across other modules (Bus, Booking, MasterLocation). Implements role-based access control and audit logging.  
- **Bus Module:** CRUD operations for bus details, with validation, audit logging, and concurrency handling using optimistic locking.  
- **MasterLocation & Location Data:** Stores routes and structured location data (CityEntity, StateEntity, CountryEntity) to support booking and management operations, ensuring data integrity and proper mapping.  

✅ Total of 31 fully functional REST APIs across all modules.

## Technical Architecture

### 🏗 1. Application Layers  
The "BookMyRide" application is designed following layered architecture principles to separate concerns and promote maintainability:

### 🔹 Controller Layer
  - Exposes REST APIs, validates incoming requests, and retrieves logged-in user details via **@AuthenticationPrincipal**. It delegates business operations to the service layer and sends standardized responses wrapped in **ApiResponse**.

### 🔹 Service Layer
  - Encapsulates business logic, orchestrates workflows (such as booking preview → continue → confirm), and coordinates multiple repositories. Utilizes utilities for validation and formatting, applies transactional control via **@Transactional**, and maps between entities and DTOs.

### 🔹 Repository Layer
  - Implements data access using Spring Data JPA with CRUD operations. Ensures data integrity with optimistic locking (**@Version**), enforces constraints, and manages relationships across entities like Country, State, City, Bus, and Booking.

### 🔹 Security Layer
  - Implements JWT-based authentication and role-based authorization. The custom **JwtFilter** validates tokens, extracts **UserPrincipal** containing user identity and roles, and protects endpoints with **@PreAuthorize** annotations.
  - Separate login flows exist for Management/Admin users (username + password) and regular users (mobile + password).

### 🔹 Utilities Layer
 - Provides common functionalities such as date parsing in a format like either (dd-MM-yyyy / dd/MM/yyyy / yyyy-MM-dd), normalize, parse & validates request, pagination, locations, enums and userPrincipal.
 - Generate unique identifiers like username, ticket, transaction ID and also dummy placeholders.

### 🔹 Mapper Layer
  - Facilitates transformation between database entities and data transfer objects (DTOs), filtering data exposed to clients and ensuring structured, secure responses, especially for booking user response, bus search results, and location information.

### 🔄 2. Request Processing Flow
Here is the generalized request pipeline used across the application:  
**[Client Request]**  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;⮟  
**[Security Layer - JWT Filter]**  
&nbsp;&nbsp;&nbsp; - &nbsp;validates token  
&nbsp;&nbsp;&nbsp; - &nbsp;extracts UserPrincipal (if present)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;⮟  
**[Controller Layer]**  
&nbsp;&nbsp;&nbsp; - &nbsp;validates input  
&nbsp;&nbsp;&nbsp; - &nbsp;calls Service layer  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;⮟  
**[Service Layer]**  
&nbsp;&nbsp;&nbsp; - &nbsp;business logic  
&nbsp;&nbsp;&nbsp; - &nbsp;calls Utilities (date parsing, pagination, validation)  
&nbsp;&nbsp;&nbsp; - &nbsp;coordinates between multiple repositories  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;⮟  
**[Repository Layer]**  
&nbsp;&nbsp;&nbsp; - &nbsp;JPA/Hibernate DB operations  
&nbsp;&nbsp;&nbsp; - &nbsp;optimistic locking  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;⮟  
**[Service Layer - Post Processing]**  
&nbsp;&nbsp;&nbsp; - &nbsp;mapping Entity → DTO  
&nbsp;&nbsp;&nbsp; - &nbsp;assembling ApiResponse  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;⮟  
**[Controller Layer]**  
&nbsp;&nbsp;&nbsp; - &nbsp;return standardized success/failure/error response  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;⮟  
**[Client]**


### 🔐 3. Security Architecture
### 🔸 JWT Generation
 - JWTs include subject (username or mobile), roles, issue and expiration timestamps. Tokens are provided on successful login/sign-up.

### 🔸 JWT Filter Workflow
 - The **JwtFilter** extracts and validates tokens for tampering, expiration, signature integrity. Valid tokens build a **UserPrincipal** stored in the security context; invalid or missing tokens mark the user as unauthenticated.
   
### 🔸 Authorization
 - Enforced via **@PreAuthorize** annotations at the class and method level based on roles, leveraging claims extracted from JWT.

### 🔁 4. Transaction & Concurrency Management
### 🔸 @Transactional Usage
 - Critical modules like Booking and Location use Spring's **@Transactional** to ensure atomicity and rollback on exceptions including concurrency, internal, and network errors.
   
### 🔸 Optimistic Locking (Versioning)
 - Entities include a **@Version** field. Concurrent update attempts trigger exceptions allowing controller-level handling with standard HTTP 409 Conflict responses, preventing data inconsistencies like race conditions or double bookings.

### 🔸 Concurrency Exception Handling
 - Optimistic locking exceptions are caught, rethrown to trigger rollbacks, and mapped to meaningful error responses.


### 🔗 5. Module Interaction (High-Level)
**Client** → **Controller** → **Service** → **Repository** → **Database**

#### 🔑 Key Interactions:
- Booking → Bus → AppUser → Location
   - Validates journey inputs
   - Ensures city/state/country correctness
   - Checks seat availability
   - Generates preview → continues → confirmation

- Management/Admin → Bus/Location modules
   - Secure CRUD operations
   - Optimistic locking ensures safe updates
 
- Utility Modules
   - Integrated across controllers/services for repeated logic

- Mapper Modules
   - Ensures structured DTO responses instead of exposing raw entities
