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
- **Management Module:** Contains all authority-level users who manage data across other modules (Bus, AppUser, Booking, MasterLocation). Implements role-based access control and audit logging.  
- **Bus Module:** CRUD operations for bus details, with validation, audit logging, and concurrency handling using optimistic locking.  
- **MasterLocation & Location Data:** Stores routes and structured location data (CityEntity, StateEntity, CountryEntity) to support booking and management operations, ensuring data integrity and proper mapping.  

✅ Total of 31 fully functional REST APIs across all modules.

## Technical Architecture

### 🏗 1. Application Layers  
> The "BookMyRide" application is designed following layered architecture principles to separate concerns and promote maintainability:

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
> Here is the generalized request pipeline used across the application:
 
<details>
  <summary>📄 View Request flow</summary>
<br>  
  
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

</details>


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
    

## Folder Structure
> Here is the folder structure of my application "**BookMyRide**".

<details>
  <summary><b>📁 Folder Structure (Click to Expand)</b></summary> 
  
<br>
  
src/  
└─ main/  
   ├─ java/  
   │&nbsp; &nbsp; &nbsp;&nbsp;└─ com.BusReservation  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ config &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # CORS, security configs, beans, filters, bootstrap model  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ constants &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # App-wide constants, enums, names  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ controller &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # REST API endpoints   
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ debug &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Internal testing utilities (e.g., generate system tokens)   
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ dto &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Request & Response DTOs  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ mapper &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;# Entity ↔ DTO converters  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ model &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # JPA entities / domain models  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ repository &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Spring Data JPA interfaces  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ security &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # JWT, auth handlers, UserPrincipal  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; ├─ service &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Business logic layer  
   │ &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; └─ utils &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Helpers: validation, parsing, pagination, generators  
   └─ resources/  
    &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;├─ application.properties &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # DB configs & app settings  
    &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;└─ static/ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; # Static assets (if any)  
  
</details>

## Database Design / ER Diagram  
> Here is the 🗄️ ER Diagram of BookMyRide database:

<details>
  <summary>📄 View ER Diagram</summary>
  <br>
  
![BookMyRide ER Diagram]()

</details>

## Comprehensive API Reference (31 APIs)
> Detailed documentation of all backend endpoints with sample request bodies, roles, and success/error responses.

### 🔐 1. Management Login (Admin Login)

<details>
  <summary>🛠 POST: /auth/bookmyride/management/login </summary>

#### 📝 DESCRIPTION
- Authenticates a Management user using username + password.
- This returns a JWT token.
- This token is required for all secure admin operations (Locations, Bus CRUD, Booking insights, etc.).

#### 🔑 ROLES ALLOWED
> PUBLIC (No token required)


#### 🔐 Dummy Credentials (as configured in application.properties)
username = adm_bookmyride_1234,  
password = BookMyRideAdmin@2025 

#### 📥 Request Body
{  
&nbsp;&nbsp;&nbsp; "username": "adm_bookmyride_1234",  
&nbsp;&nbsp;&nbsp; "password": "BookMyRideAdmin@2025"  
}  

#### 📤 SUCCESS RESPONSE  
![Management Login Success]()  

#### ❗ ERROR RESPONSES
![Management Login Error]()

#### 🔐 HOW TO USE THE TOKEN
- After successful login, take the JWT from the response and include it in all **secure admin requests** using the header:  
  `Authorization: Bearer <your-token-here>`


#### 📝 NOTES
- This is the first API to start the entire Management/Administration flow.
- **Currently, only a single ADMIN user exists** to control all authority-level operations.
- Without this token, the admin cannot perform any restricted action.
- JWT contains username, role, issued time, expiration time.
</details>


### 🔐 2. Update Management Profile

<details>
  <summary>🛠 PUT: /management/profile </summary>

#### 📝 Description
- Allows the logged-in Management/Admin user to update their profile information.
- Initially, the system contains dummy placeholders for the admin account. This ensures replace those dummy placeholder values with real data.
- Requires **JWT token** from Management Login.
- **Security Implementation:**
    - Method is protected using @PreAuthorize("hasRole('ADMIN')").
    - Currently logged-in user details are retrieved via @AuthenticationPrincipal.
    - This ensures only the authenticated admin can update their own profile.

#### 🔑 Roles Allowed
> MANAGEMENT / ADMIN (JWT required & Roles are **internally assigned**, Not through login or profile update)

#### 📥 Request Body
{  
&nbsp;&nbsp;&nbsp; "fullName": "Your Full Name",  
&nbsp;&nbsp;&nbsp; "gender": "Your Gender",   
&nbsp;&nbsp;&nbsp; "email": "Your Email ID",  
&nbsp;&nbsp;&nbsp; "mobile": "Your Mobile Number"  
}  
> 💡 Tip: Replace the placeholder values with your own details.

#### 📤 Success Response
![Management Profile Update Success]()  

#### ❗ Error Responses
- Invalid token / unauthorized access
![Management Profile Update Error]()   

- Validation errors (email/mobile)
 ![Management Profile Update Error]()

#### ⚠️ Critical Security Behavior (Must Read)
> This API **does not return a new token**.  
> But the operation **directly affects the validity of the existing JWT**.

**What Actually Happens Behind the Scenes**  
- If `fullName` is updated, the backend regenerates your username automatically.
- Your current JWT instantly becomes invalid — because the username stored inside the token no longer matches the updated username in DB.
- As a result:
     - **All protected APIs (including the next one Change Password API)** start returning Unauthorized.
     - This is intentional and enforced by:
         - `@AuthenticationPrincipal` → extracts username from JWT.
         - `@PreAuthorize` → verifies the identity + role.
         - JWT filter → checks DB username match for every request.
          
**What You Must Do Next**  
- You must re-login using the new username to obtain a fresh valid JWT.  
- Only after re-login can you:  
     - Change your password
     - Manage locations
     - Perform any admin operation
     - Access any protected resource
      
 **Why This Matters**  
This mechanism guarantees that:  
    - Token is always tied to the current, correct username.
    - Admin account cannot be used with outdated JWTs.
    - System integrity remains strong even after identity updates.
</details>  


### 🔐 3. Change Password (Management User)

<details>
  <summary>PUT: /management/change-password</summary>

#### 📝 Description
 - Allows the logged-in Management user to securely change their password.
 - Even though the user is authenticated, this API performs a second-level credential verification using Spring Security to ensure the old password is correct.
 - This provides bank-level security, preventing unauthorized or session-hijacked password changes.

#### 🔑 Roles Allowed
 > MANAGEMENT / ADMIN (Internally assigned — cannot be changed via API)

 #### 📥 Request Body
{  
&nbsp;&nbsp;&nbsp; "oldPassword": "Your Current Password",  
&nbsp;&nbsp;&nbsp; "newPassword": "Your New Password"  
}  
> 💡 Tip: Replace the placeholder values with your own details.

#### ⚙️ How the Backend Validates This (Important) 
**1.** Extracts the **currently authenticated username** using:  
&nbsp;&nbsp;&nbsp; `@AuthenticationPrincipal UserPrincipal principal` 

**2.** Uses `AuthenticationManager` to re-validate credentials:  
&nbsp;&nbsp;&nbsp; `new UsernamePasswordAuthenticationToken(username, oldPassword)`

**3.** If old password is incorrect →  
&nbsp;&nbsp;&nbsp; Spring Security throws:
 - `BadCredentialsException` 
 - `UsernameNotFoundException` 

**4.** These are caught and returned as **clean JSON errors** using `ApiResponse`.  
**5.** If old password is valid →  
&nbsp;&nbsp;&nbsp; The service layer updates the password securely (BCrypt hashing).  

This ensures:  
✔ Even authenticated users cannot change password without proving old one  
✔ Zero shortcuts  
✔ Consistent with enterprise-grade security flows  

#### 📤 Success Response
![Management Password Update Success]()

#### ❗ Error Responses
> Wrong old password  
![Management Profile Update Error]()
 
> Empty/invalid new password  
![Management Profile Update Error]()
  
> Unauthorized (if old token invalid due to username update)  
![Management Profile Update Error]()


#### ⚠️ Critical Notes & Security Flow
- This API requires a valid JWT from the latest login
- If the admin previously updated their profile and their username was regenerated, the old JWT becomes invalid — and this API will not work until re-login
- Old password is validated using Spring Security's authentication pipeline, giving maximum protection
- Ensures:
    - No one can change password using a stolen token
    - No change is possible without verifying the old password
    - Only the true authenticated, verified Admin can modify credentials
</details>
