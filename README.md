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
  <summary><strong>POST</strong> <code>/auth/bookmyride/management/login</code></summary>

#### 🛠 Endpoint Summary  
**Method:** POST  
**URL:** /auth/bookmyride/management/login  
**Authentication:** Not required (First step of admin login)  
**Authorized Roles:** PUBLIC  

#### 📝 Description
This API authenticates the **Management/Admin user** using the system-generated username and password.
It represents the **entry point** for all management-level administrative operations such as managing locations, buses, bookings, and master data.  

Key highlights:  

- Uses **Spring Security’s AuthenticationManager** to verify credentials.
- Supports **username-only login** for management users (regular users log in using mobile number, handled automatically by custom `UserDetailsService`).
- Returns a **JWT token**, required for all subsequent secure admin operations.
- Implements strict security: invalid credentials → immediate **401/404** responses.
- Backed by a **bootstrap mechanism (CommandLineRunner)** that automatically generates the first admin user at application startup.


#### 📤 Request Body
{  
&nbsp;&nbsp;&nbsp; "username": "adm_bookmyride_1234",  
&nbsp;&nbsp;&nbsp; "password": "BookMyRideAdmin@2025"  
} 
> 💡 Dummy Credentials (as configured in application.properties)



#### ⚙️ Backend Processing Workflow
**1. DTO Validation**  
- The request is first validated using `@Valid` in combination with `BindingResult`.
- If any field fails validation (e.g., missing username or password), the API immediately returns a **400 Bad Request** containing the list of validation errors. No authentication attempts happen unless the DTO passes validation.

**2. Fetch Management User**   
- The system performs an initial DB lookup using `managementService.fetchByUsername(...)`.
- If no management user exists for the provided `username`, the API responds with **401 Unauthorized**, ensuring no clues are leaked about whether the username is incorrect or the password is wrong.

**3. Authentication Pipeline Execution**  
- A `UsernamePasswordAuthenticationToken` is constructed using credentials from the DTO.
- This token is passed to `authenticationManager.authenticate()`, which internally:
   - Verifies the username exists in the system
   - Checks the password using BCrypt hashing
   - And validates that the management account is enabled and valid for authentication.  

**4. Error Handling During Authentication**   
- If credentials do not match, a `BadCredentialsException` triggers a **401 Unauthorized** response.
- If the username does not exist in the security layer, `UsernameNotFoundException` triggers a **404 Not Found** response.
- Any unexpected failure within the authentication pipeline results in a **500 Internal Server Error**, ensuring controlled and predictable error exposure.  

**5. Extracting the Authenticated Principal**   
- Once authentication succeeds, the system retrieves a fully populated `UserPrincipal` object.
- This principal includes the admin’s full name, username, role, and granted authorities, which are later used for role-based access control.  

**6. JWT Token Generation**   

A JWT token is generated using `jwtService.generateToken()`.
The token includes:  
- The username as the subject, ADMIN as role, issued & expiry timestamps
- And an internal “management flag” indicating this is a **Management-grade token**, separate from user tokens.

**7. Successful Authentication Response**   

The API returns a **200 OK** response containing:  
- A success message with admin’s full name
- And the generated **JWT token** required for subsequent protected admin operations.

#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Management Login Success]()
</details>

#### ❗ Error Response
> 400 BAD_REQUEST — DTO Validation Failed  
<details> 
  <summary>View screenshot</summary>
   ![Management Login Error]()
</details>

> 401 UNAUTHORIZED — Invalid Credentials  
 <details> 
  <summary>View screenshot</summary>
   ![Management Login Error]()
</details>

> 404 NOT_FOUND — Username Not Found  
 <details> 
  <summary>View screenshot</summary>
   ![Management Login Error]()
</details>

#### HTTP Status Code Table  

| HTTP Code | Status Name       | Meaning               | When It Occurs                                |
| --------- | ----------------- | --------------------- | --------------------------------------------- |
| 200       | SUCCESS           | Request succeeded     | Valid credentials → token returned            |
| 400       | BAD_REQUEST       | Validation Falied     | Missing/invalid fields in login DTO           |
| 401       | UNAUTHORIZED      | Authentication Failed | Username exists but password incorrect        |
| 404       | NOT_FOUND         | Resource Not Found    | Username does not exist in DB                 |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error | Unexpected server-side error                   |

#### ⚠️ Edge Cases & Developer Notes
**1. Automatic Creation of First Admin User**  
- When the application starts for the first time, the system checks whether any management accounts exist.
- If none are found, the `ManagementBootstrap` class (powered by `CommandLineRunner`) automatically creates the first admin user using credentials from `application.properties`.
- This ensures secure, zero-downtime setup without temporarily disabling Spring Security.

**2. Admin Token vs User Token Separation**   
- The custom `UserDetailsService` differentiates between mobile numbers (user accounts) and alphanumeric usernames (management accounts).
As a result:
 - A user token cannot access management endpoints.
 - A management token cannot be used in user flows.
 - Token roles and the internal “management flag” enforce this separation at authentication and authorization layers.  

**3. Invalid or Non-Regex Subjects**  
- If the login subject resembles a mobile number (via regex), the system routes the authentication attempt through the USER account flow.
- Otherwise, it is interpreted strictly as a MANAGEMENT login. This mechanism prevents cross-domain login attacks.

**4. Importance of Keeping Bootstrap Credentials Secure**  
- Since the initial admin account is generated from properties, those property values must be protected.
- Once logged in, the admin is expected to update default credentials to enforce better security hygiene.
</details>


### 🔐 2. Update Management Profile

<details> 
  <summary><strong>PUT</strong> <code>/management/profile</code></summary>

#### 🛠 Endpoint Summary  
**Method:** PUT  
**URL:** /management/profile  
**Authentication:** Required (JWT token)  
**Authorized Roles:** ADMIN  
  
#### 📝 Description
Updating personal profile information is a critical aspect of maintaining accurate and secure management data within the system. This endpoint allows `ADMIN`  to modify key details such as full name, email, mobile number, and gender while enforcing strict validation and uniqueness constraints.  
The API ensures that changes are applied safely and consistently, avoiding conflicts with regular user accounts and supporting seamless auditing.  

Key highlights:  

- Validates the currently authenticated **Management user** using `@AuthenticationPrincipal` through `UserPrincipalValidationUtils.validateUserPrincipal()` utility.
- Email and mobile numbers are checked against existing user accounts to avoid **duplication**.
- Initially, the system contains dummy placeholders for the admin account. This ensures replace those dummy placeholders values with real data.
- Only fields that have changed are modified, and full name changes trigger automatic username updates.
- **Security Implementation:**
    - Method is protected using @PreAuthorize("hasRole('ADMIN')").
    - Currently logged-in user details are retrieved via `@AuthenticationPrincipal`.
    - This ensures only the authenticated admin can update their own profile.

#### 📥 Request Body
{  
&nbsp;&nbsp;&nbsp; "fullName": "Your Full Name",  
&nbsp;&nbsp;&nbsp; "gender": "Your Gender",   
&nbsp;&nbsp;&nbsp; "email": "Your Email ID",  
&nbsp;&nbsp;&nbsp; "mobile": "Your Mobile Number"  
}  
> 💡 Tip: Replace the placeholder values with your own details.


#### ⚙️ Backend Processing Workflow (Must Read)  
**1. Validate Authenticated User**  
- The API retrieves the currently logged-in user from the security context using `@AuthenticationPrincipal`.
- `UserPrincipalValidationUtils.validateUserPrincipal()` ensures that the token is valid, the account exists in the database, and the user holds the ADMIN role.
- Any failure triggers an immediate HTTP error (**401**, **403**, or **404**), preventing **unauthorized access**.  

**2. DTO Validation**  
- Incoming request data is validated using `@Valid` along with `BindingResult`. Missing or invalid fields result in a **400 Bad Request**, with a structured list of validation errors returned to the client.  

**3. Check for Conflicting Credentials**  
- Before applying updates, the system checks whether the provided email or mobile number is already associated with any non-management user.
- Conflicting values result in a **403 Forbidden** response, ensuring management credentials remain unique.  

**4. Profile Field Updates**  
- Each provided field is compared against the existing record. Full name updates generate a new username, gender values are parsed and validated against enums, and email/mobile fields are updated only if changed.
- If no fields differ from the existing record, the entity remains unchanged.  

**5. Persistence and Response Construction**  
- Changes are saved to the database, and `profileUpdatedAt` is updated if modifications occurred.
- The response includes updated profile data and, when applicable, a note about the new username, prompting re-login for security purposes.  



#### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![Management Profile Update Success]()
</details>


#### ❗ Error Responses
> BAD_REQUEST — DTO Validation Failed  
 <details> 
  <summary>View screenshot</summary>
   ![Management Profile Update Error]()  
</details>  

> Invalid token / unauthorized access  
 
<details> 
  <summary>View screenshot</summary>
   ![Management Profile Update Error]()  
</details>  

> FORBIDDEN — Role or Access Denied  
 
<details> 
  <summary>View screenshot</summary>
   ![Management Profile Update Error]()  
</details>  

> NOT_FOUND — Account Not Found  

<details> 
  <summary>View screenshot</summary>
   ![Management Profile Update Error]()  
</details>  


> CONFLICT — Duplicate Email/Mobile
 
<details> 
  <summary>View screenshot</summary>
   ![Management Profile Update Error]()  
</details>  


#### HTTP Status Code Table
| HTTP Code | Status Name           | Meaning               | When It Occurs                                    |
| --------- | --------------------- | --------------------- | ------------------------------------------------- |
| 200       | SUCCESS               | Request succeeded     | Profile updated successfully                      |
| 400       | BAD_REQUEST           | Validation Failed     | Invalid/missing fields in DTO or invalid gender   |
| 401       | UNAUTHORIZED          | Authentication Failed | Token invalid, expired, or missing                |
| 403       | FORBIDDEN             | Access Denied         | Role mismatch or email/mobile conflict with users |
| 404       | NOT_FOUND             | Resource Not Found    | Management account not found                      |
| 409       | CONFLICT              | Duplicate Entry       | Email/mobile already exists                       |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error      | Any unexpected server-side error                  |



#### ⚠️ Edge Cases & Developer Notes
**1. Username Auto-Generation on Full Name Change**   
- When the full name is updated, a new username is automatically generated. The response includes a notification prompting the admin to re-login, ensuring continuous secure access.  

**2. Strict Separation Between Management and Regular Users**  
- The system enforces strict uniqueness of management credentials, preventing accidental overlaps with regular user accounts. This maintains secure and organized data management across all user types.  

**3. Selective Field Updates and Timestamping**  
- Only fields that differ from existing values are updated, avoiding unnecessary writes. `profileUpdatedAt` is recorded to support auditing and track profile modifications efficiently.  

**4. Gender & Role Enforcement**  
- Gender values are restricted to predefined enums such as MALE, FEMALE, and OTHER. Invalid entries result in a 400 Bad Request with a descriptive error message.
- Only ADMIN users are authorized to update their profile via this endpoint. Non-ADMIN or invalid token attempts are blocked immediately to maintain strict access control.  
          
**5. What You Must Do Next**  
- You must re-login using the new username to obtain a fresh valid JWT.  
- Only after re-login can you:  
     - Change your password
     - Manage locations
     - Perform any admin operation
     - Access any protected resource
      
 **6. Why This Matters**  
 This mechanism guarantees that:  
 - Token is always tied to the current, correct username.
 - Admin account cannot be used with outdated JWTs.
 - System integrity remains strong even after identity updates.  
</details>  


### 🔑 3. Change Password (Management user)

<details> 
  <summary><strong>PATCH</strong> <code>/management/change-password</code></summary>

#### 🛠 Endpoint Summary  
**Method:** PATCH  
**URL:** /auth/bookmyride/management/change-password   
**Authentication:** Required (Admin JWT token)  
**Authorized Roles:** ADMIN  


#### 📝 Description
This API allows a Management/Admin user to change their account password securely. After login and profile updates, admins are required to update their system-generated password to a personal, secure password.  

Key highlights:  

 - Validates the authenticated admin user before making any updates.
 - Even though the user is **authenticated**, this API performs a **second-level credential verification** using Spring Security to ensure the old password is correct.
 - Enforces strong password rules for the new password.
 - Updates the password and records the timestamp of the change.
 - This provides bank-level security, preventing unauthorized or session-hijacked password changes.


 #### 📥 Request Body
{  
&nbsp;&nbsp;&nbsp; "oldPassword": "Your Current Password",  
&nbsp;&nbsp;&nbsp; "newPassword": "Your New Password"  
}  
> 💡 Notes: Password rules are enforced using centralized validation (e.g., length, character variety).
> 💡 Tip: Replace the placeholder values with your own details.


#### ⚙️ How the Backend Validates This (Important) 
**1. Authenticate and Validate UserPrincipal**  
 - Extracts UserPrincipal from `@AuthenticationPrincipal`; returns **401 Unauthorized** ("Invalid token. Please try after re-login.") if null/expired.
 - Fetches live `Management` account via `managementService.fetchById(userPrincipal.getId())`; returns **404 Not Found** ("Account not found or no longer exists") if deleted/stale, blocking compromised tokens.​
  
**2. Role-Based Access Control (RBAC)**  
 - Verifies the role is `ADMIN` if not returns **403 Forbidden** ("Access denied for this role.") if mismatched.
 - Return **400 Bad Request** if validation fails.
 - Passes validated Management object to controller for `username` extraction, enabling secure downstream auth.

**3. DTO & Input Validation**  
- Applies `@Valid` to `ChangeManagementPasswordDto` (`oldPassword`, `newPassword`) with `BindingResult`; returns **400 Bad Request** listing constraint violations (non-blank, strength rules).
- Ensures no weak/invalid inputs reach core logic.

**4. Old Password Verification (Critical Security Layer)**  
- Builds `UsernamePasswordAuthenticationToken(username, oldPassword)`.Hash the new password securely (e.g., using `BCryptPasswordEncoder`) and store it in the database.
 - Calls `authenticationManager.authenticate()`; catches `BadCredentialsException` → **401** ("Invalid credentials. Given old password is incorrect.") or `UsernameNotFoundException` → **404**.
 - Forces proof of current knowledge, preventing token-only attacks even from authenticated sessions.

**5. Secure Password Update**  
 - On success, `managementService.changeNewPassword()` hashes newPassword (`BCryptPasswordEncoder`), updates DB with last-changed timestamp.
 - Returns **200 OK** via `ApiResponse`; generic exceptions → **500 Internal Server Error**. All responses use clean, standardized JSON hiding internals.  

**Enterprise Benefits:**  
- **Zero Trust:** Multi-factor proof (JWT + old password + role + DB fresh).
- **Audit-Ready:** Consistent error codes, no sensitive leaks.
- **Scalable:** Reusable UserPrincipalValidationUtils across endpoints
- **Demo note:** Production adds rate limiting, MFA prompts, session invalidation.

#### 📤 Success Response
<details>
  <summary>View screenshot</summary>
    <br>
![Management Password Update Success]()
</details>  


#### ❗ Error Responses
> Wrong old password  
<details>
  <summary>View screenshot</summary>
    <br>
  ![Management Profile Update Error]()
</details> 
 
> Empty/invalid new password
<details>
  <summary>View screenshot</summary>
    <br>
![Management Profile Update Error]()
</details>  
  
> Unauthorized (if old token invalid due to username update)  
<details>
  <summary>View screenshot</summary>
    <br>
![Management Profile Update Error]()
</details>  


#### HTTP Status Code Table
| HTTP Code | Status Name           | Meaning               | When It Occurs                       |
| --------- | --------------------- | --------------------- | ------------------------------------ |
| 200       | SUCCESS               | Request succeeded     | Password updated successfully        |
| 400       | BAD_REQUEST           | Validation Failed     | Invalid DTO or missing fields        |
| 401       | UNAUTHORIZED          | Authentication Failed | Old password incorrect               |
| 404       | NOT_FOUND             | Resource Not Found    | Admin account not found              |
| 403       | FORBIDDEN             | Access Denied         | User does not have ADMIN role        |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error      | Unexpected failure during processing |



#### ⚠️ Critical Notes & Security Flow
**1. Old Password Verification**  
- Malicious actors with stolen JWT tokens attempt password overwrite without knowing current credentials. The `AuthenticationManager.authenticate()` with `UsernamePasswordAuthenticationToken` catches `BadCredentialsException`, returning **401 Unauthorized** ("Invalid credentials. Given old password is incorrect.")—blocks token-only attacks.
- Username changes or account modifications since token issuance trigger `UsernameNotFoundException` → **404 Not Found**.  

**2. Strong Password Enforcement**  
- Weak submissions (blank, short, simple patterns) via `ChangeManagementPasswordDto` fail `@Valid` + `BindingResult` checks, returning **400 Bad Request** with detailed error list from `BindingResultUtils.getListOfStr()` utility class.
- Centralized validation logic in DTO annotations ensures consistency across endpoints; easily extensible for regex complexity, history checks, or breached password screening in production.  

**3. UserPrincipal Validation**  
- Null/expired JWT → 401 ("Invalid token. Please try after re-login."). Stale tokens for deleted accounts → 404 ("Account not found..."). Non-ADMIN roles → 403 ("Access denied for this role.").
- `UserPrincipalValidationUtils.validateUserPrincipal()` is reusable across sensitive endpoints, providing early exit with validated `Management` object—reduces controller bloat and enforces defense-in-depth.

**4. Role-Based Access Control**  
- Standard users exploit endpoint via valid **JWT** → blocked by `management.getRole() != Role.ADMIN` check in utility, returning **403 Forbidden**.
- **Granular RBAC** prevents privilege escalation; extend to enum hierarchies or permission matrices for microservices scaling.

**3. Timestamps for Auditing**
- Compliance audits require change history; `managementService.changeNewPassword()` updates `passwordLastUpdatedAt` timestamp on success.
</details>


### 👤 4. View Profile (Management/Admin)

<details> 
  <summary><strong>GET</strong> <code>/management/profile</code></summary>

#### 🛠 Endpoint Summary  
**Method:** GET  
**URL:** /management/profile  
**Authentication:** Required (Admin JWT token)  
**Authorized Roles:** ADMIN  

  
#### 📝 Description
This API allows a **Management user** to fetch their current profile information. After login and optional profile updates, admins can view details such as full name, gender, email, and mobile number.

Key highlights:  

- Fetches the **currently authenticated Management/Admin** user's profile information.
- This endpoint uses Spring Security’s `@AuthenticationPrincipal` to obtain the identity (UserPrincipal) of the logged-in Management user.
- It ensures that only **valid, authenticated, ADMIN-role** users can view their own profile.
- The API returns profile details as a **DTO**, hiding sensitive database fields such as password, internal identifiers, and security metadata.
- This is typically the first action an authenticated admin performs after updating profile or changing password.


#### ⚙️ Backend Processing Workflow
**1. Authenticate and Validate UserPrincipal**  
- Extracts `UserPrincipal` from `@AuthenticationPrincipal`.
- Validates the **JWT token** and ensures the user exists and has the **ADMIN** role via `UserPrincipalValidationUtils.validateUserPrincipal()`.
- Returns **401 Unauthorized** if the token is null/expired, **403 Forbidden** if the role is invalid, or **404 Not Found** if the user account is deleted/stale.  

**2. Fetch Profile Data**  
- Calls `managementService.fetchProfileData(management)` to convert the `Management` entity into a `ManagementProfileDto`.
- Ensures that only relevant, safe profile data is returned (no sensitive fields like passwords).

**3. Return Response**  
- Wraps the DTO in a standardized `ApiResponse.successStatusMsgData()` object.
- Returns HTTP **200 OK** with profile data and a success message.


#### 📤 Success Response
<details>
  <summary>View success response screenshot</summary>
  <br>
  ![Management Profile view Error]()
</details>  


#### ❗ Error Responses
<details>
  <summary>View error response screenshot</summary>
    <br>
  ![Management Profile view Error]()
</details>  

#### HTTP Status Code Table
| HTTP Code | Status Name           | Meaning               | When It Occurs                       |
| --------- | --------------------- | --------------------- | ------------------------------------ |
| 200       | SUCCESS               | Request succeeded     | Profile loaded successfully          |
| 401       | UNAUTHORIZED          | Authentication Failed | JWT token null/expired               |
| 403       | FORBIDDEN             | Access Denied         | User does not have ADMIN role        |
| 404       | NOT_FOUND             | Resource Not Found    | Admin account not found              |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error      | Unexpected failure during processing |



#### ⚠️ Edge Cases & Developer Notes  
**1. UserPrincipal Validation**  
- The system extracts `UserPrincipal` from the JWT token for every request. This ensures that only authenticated sessions can access sensitive endpoints.
- `UserPrincipalValidationUtils.validateUserPrincipal()` performs a multi-step check: verifies the token is valid, confirms the user exists in the database, and ensures the account has not been deleted or disabled.
- This prevents unauthorized access from stale tokens, deleted accounts, or tokens from compromised sessions. Even if a token is valid structurally, it won’t grant access if the underlying account is no longer active.

**2. Role-Based Access Control (RBAC)**  
- The endpoint is strictly limited to `ADMIN` users.
- The validation utility checks the role of the authenticated user. Any mismatch results in a **403 Forbidden**, ensuring that standard users—even with a valid JWT—cannot access administrative profile data.


**3. DTO Mapping & Data Privacy**  
- The `ManagementMapper.managementToDto()` converts the internal entity into a safe DTO for exposure. Sensitive fields such as `password`, internal system IDs, or audit metadata are excluded from the API response.
- This approach balances transparency for front-end integration while maintaining security and privacy standards.

**4. Scalability & Reusability**  
- By centralizing validation in `UserPrincipalValidationUtils`, the system reduces duplicate checks across multiple endpoints.
- This design makes it easy to extend future features, such as additional profile fields, audit logging, or multi-factor authentication hooks.
</details>  

### 🗺️ 5. Create New Location (City + State + Country Hierarchy)
<details> 
  <summary><strong>POST</strong> <code>/bookmyride/management/locations</code></summary>

#### 🛠 Endpoint Summary  
**Method:** POST  
**URL:** /bookmyride/management/locations  
**Authentication:** Required (Admin JWT token)  
**Authorized Roles:** ADMIN  

#### 📝 Description
This API allows a **Management/Admin user** to create a new location by adding a **City**, **State**, and **Country** in a hierarchical manner. It ensures that duplicate or conflicting entries are prevented using **multi-level validation** and database constraints.

Key highlights:  
- Validates the authenticated admin user before performing any database operations.
- Allows the authenticated **Management/Admin user** to create a complete Location set:
     - **city**
     - **state**
     - **country**
- All three values are provided in a **single DTO**, and the backend ensures the correct creation or reuse of **hierarchical entities** using a strict validation + enum-parsing system.
- Backend validates & parses StateEnum and CountryEnum using your custom `ParsingEnumUtils`.
- Ensures uniqueness and avoids duplication in:
    - Main tables (cities → states → countries)
    - **MasterLocation table** (string-based fast lookup)
- Automatically syncs the **MasterLocation** projection table after successful creation.
- The operation is fully protected with:
    - JWT authentication
    - ADMIN-role enforcement
    - DTO validation
    - Optimistic locking
    - MasterLocation projection sync

#### 📥 Request Body (Sample)
{  
&nbsp;&nbsp;&nbsp; "city": "Chennai",  
&nbsp;&nbsp;&nbsp; "state": "Tamil Nadu",  
&nbsp;&nbsp;&nbsp; "country": "India"  
}  
> 💡 Tips: Also can use your own values here.
 
 
#### ⚙️ How the Backend Processes This
**1. Authenticate and Validate Admin User**  
 - Extracts `UserPrincipal` from `@AuthenticationPrincipal`.
 - Uses `UserPrincipalValidationUtils.validateUserPrincipal()` to ensure the user exists, is active, and has the `ADMIN` role.
 - Returns **401 Unauthorized** or **403 Forbidden** if validation fails.
   
**2. DTO & Input Validation**  
 - Uses `@Valid` annotation on `LocationEntryDto` with `BindingResult` to check required fields.
 - Returns **400 Bad Request** listing validation failures if inputs are missing, malformed, or out-of-enum-range.

**3. Parse Enumerated Types**  
- Converts country and state strings into enum types via `ParsingEnumUtils`.
- Returns **400 Bad Request** if the provided country or state is invalid or unrecognized.  

**4. Check for Existing Entries**  
- Verifies if the combination of City + State + Country already exists in either:
    - Management-controlled locations (`cityAndStateAndCountryExists`)
    - Master location repository (`masterLocationService.cityStateCountryExists`)
- Returns 409 Conflict if the combination already exists to prevent duplicates.  

**5. Hierarchical Creation**  
- **Country:** Fetches or creates the country entity.
- **State:** Checks if state exists under the country; creates if absent.
- **City:** Checks for city uniqueness within the state; creates only if unique.
- Saves new location in **master location table** for global reference.
- Logs admin action with `management ID`, `username`, and created location details.

**6. Error Handling**  
- Handles `ObjectOptimisticLockingFailureException` and `OptimisticLockException` to prevent race conditions during concurrent additions.
- Returns **409 Conflict** with descriptive message if another admin inserts the same location simultaneously.
- Catches generic exceptions → **500 Internal Server Error** with standardized API response.  

#### 📤 Success Response
<details> <summary>View screenshot</summary> 
  <br>
  ![Location Insertion Success]()
</details>  

#### ❗ Error Responses
> Unauthorized (invalid/expired JWT)
<details> 
  <summary>View screenshot</summary> <br> 
  ![Location Insertion Error]()
</details> 

> Invalid state/country enum
<details> 
  <summary>View screenshot</summary> <br> 
  ![Location Insertion Error]()
</details>

> Location already exists (main tables or MasterLocation)
 <details> 
  <summary>View screenshot</summary> <br> 
  ![Location Insertion Error]()
</details>

> Concurrent modification (Optimistic Lock)
<details> 
  <summary>View screenshot</summary> <br> 
  ![Location Insertion Error]()
</details>


#### HTTP Status Code Table
| HTTP Code | Status Name           | Meaning               | When It Occurs                                        |
| --------- | --------------------- | --------------------- | ----------------------------------------------------- |
| 201       | CREATED               | Location created      | Successfully added new city/state/country combination |
| 400       | BAD_REQUEST           | Validation Failed     | Missing/invalid DTO, or country/state not recognized  |
| 401       | UNAUTHORIZED          | Authentication Failed | Token invalid or expired                              |
| 403       | FORBIDDEN             | Access Denied         | User does not have ADMIN role                         |
| 409       | CONFLICT              | Duplicate Entry       | City/State/Country combination already exists         |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error      | Any unhandled server-side exception                   |


#### ⚠️ Edge Cases & Developer Notes
**1. Hierarchical Data Integrity (Country → State → City Enforcement)**  

The API enforces a strict top-down structure to maintain clean, conflict-free geographical data.
 - A **City** cannot be created unless its State exists or is created during the same request.
 - A **State** cannot exist without a valid Country.
 - Each level is validated and resolved individually, guaranteeing **referential integrity**, and preventing corrupted structures (e.g., incorrect state–country pairing).
 - This ensures long-term consistency and prevents the messy “**partial location trees**” that commonly appear in poorly designed systems.

**2. Advanced Duplicate Prevention (Management + Master Repository Check)**   

Instead of checking only the City table, the service verifies duplicates across two different sources:  

**1. Management’s own hierarchical tables**  
**2. Master location table shared across the full application**  

This dual-layer validation prevents:  

  - Same location being inserted seconds apart by two admins.
  - City duplication under different states.
  - Cross-module inconsistency (e.g., user module has a location but admin module doesn’t).
     
This approach ensures your platform always has a **single source of truth** for locations.  

**3. Concurrency Protection with Optimistic Locking**  

The system uses `ObjectOptimisticLockingFailureException` / `OptimisticLockException` to guard against race conditions where multiple admins try to insert the same location.  
- If Admin A saves “Chennai, Tamil Nadu, India” at the same time Admin B does, one succeeds and the other cleanly receives **409 Conflict**.
- Prevents silent overwrites or duplicate states/cities.
- Ensures all inserts are atomic and conflict-free.
 
This mirrors real enterprise master-data systems where admins work simultaneously.  


**4. Enum Parsing & Input Normalization Pipeline**  

Before any database operations run, inputs like `country` and `state` are validated using `ParsingEnumUtils`. This layer ensures:  

 - Only valid, recognized enum values pass through (no misspellings or unknown regions).
 - Business logic remains independent of string inputs.
 - Future expansion (e.g., adding new states/countries) is controlled and predictable.

This provides a **strong validation firewall**, preventing invalid or unregulated data from entering your geographic hierarchy.  

**5. Transactional Safety, Audit Logging & Scalability**  

The entire creation process runs inside a `@Transactional` boundary, ensuring full rollback if anything fails in the chain — avoiding orphaned states or countries. Additionlly:  

- Every successful insertion logs admin ID, username, and full location details.
- Makes all actions traceable for auditing and debugging.
- Architecture supports easy scaling: additional fields (zone, pincode, coordinates) or new modules can integrate without rewriting the core flow.  

This combination of transaction safety + traceability + modular layering proves the system is designed for **real-world production readiness**, not just demonstration.
</details>  

### 📦 6. Bulk Location Creation (City + State + Country Hierarchy)
<details> 
  <summary><strong>POST</strong> <code>/bookmyride/management/locations/list</code></summary>

#### 🛠 Endpoint Summary  
**Method:** POST  
**URL:** /bookmyride/management/locations/list  
**Authentication:** Required (Admin JWT token)  
**Authorized Roles:** ADMIN    

#### 📝 Description
This API allows a Management/Admin user to **insert multiple locations** in bulk in a hierarchical manner: Country → State → City. It is designed to handle **hundreds of entries** efficiently, validating each entry independently while ensuring **data consistency, duplicate prevention, and transaction safety**.  

Key highlights:  

- Allows the authenticated **Management/Admin** user to insert **multiple locations** in a single request.
- Accepts a list of `LocationEntryDto` objects, iterates through each entry, and performs hierarchical validation and insertion.
- The backend processes **each entry independently** inside a loop — meaning one invalid location **does not stop** the others from being processed.
- For every DTO, the service records the outcome using clearly defined prefixes:
   - **SUCCESS:** Location inserted successfully
   - **DUPLICATE_ENTRY:** Conflict found (main tables or MasterLocation)
   - **ERROR:** Unexpected issue or server-side failure 
- At the end of processing, the backend:
   - Counts successful inserts
   - Counts failures
   - Returns a **201 status** with a consolidated summary + full result list
- This API is especially useful during initial data setup or location migration phases.


#### 📥 Request Body
[  
&nbsp;{  
&nbsp;&nbsp;&nbsp;&nbsp; "city": "Chennai",  
&nbsp;&nbsp;&nbsp;&nbsp; "state": "Tamil Nadu",   
&nbsp;&nbsp;&nbsp;&nbsp; "country": "India"  
&nbsp;},  
&nbsp;{   
&nbsp;&nbsp;&nbsp;&nbsp; "city": "Coimbatore",  
&nbsp;&nbsp;&nbsp;&nbsp; "state": "Tamil Nadu",  
&nbsp;&nbsp;&nbsp;&nbsp; "country": "India"  
&nbsp;}    
]  
> 💡 Each object in the list uses the same DTO rules as the single-location API. Also can use your own any other data related to location.


#### ⚙️ How the Backend Processes This (Step-by-Step)
**1. Authenticate and Validate Admin User**  
- Extracts `UserPrincipal` from `@AuthenticationPrincipal`.
- Uses `UserPrincipalValidationUtils.validateUserPrincipal()` to ensure the user exists, is active, and has the **ADMIN** role.
- Returns **401 Unauthorized** or **403 Forbidden** if validation fails.  

**2. Validate Request Body**  
- Checks if the list of DTOs is **empty**, returning **400 Bad Request** if true.
- Uses `@Valid` and `BindingResult` to ensure all DTOs meet required constraints.
- Any validation failures are accumulated and returned in a structured error response.  

**3. Enhanced For-Loop Processing**  
- Each `LocationEntryDto` is processed individually in a loop.
- Converts `country` and `state` strings to enum objects using `ParsingEnumUtils.getParsedEnumType()`.
- If parsing fails, the entry is immediately rejected with a **BAD_REQUEST** message explaining the invalid input.
- Checks if the City-State-Country combination already exists in **Management tables** using `cityEntityRepo.existsByNameAndState_NameAndState_Country_Name()`.
- Also checks the Master location table using `masterLocationService.cityStateCountryExists()`.
- Sync MasterLocation table at background.
- Duplicates are skipped and logged into the result list for reporting like:
   - `success: Chennai-Tamil Nadu-India added`
   - `duplicate: Mumbai-Maharashtra-India already exists`
   - `error: Unexpected issue while saving Delhi-Delhi-India`
 
    
**4. Hierarchical Creation & Persistence**  
- **Country:** Fetches existing or creates new `CountryEntity`.
- **State:** Ensures the state exists under the correct country; creates if missing.
- **City:** Checks if the city exists within the state; creates only if unique.
- Each successful city insertion is stored in the **master location table** to maintain a global reference.
- Every successful insertion is appended to the `results` list with a descriptive message including entity IDs.

**5. Concurrency Protection & Optimistic Locking**  
- Handles `ObjectOptimisticLockingFailureException` and `OptimisticLockException` to prevent race conditions.
- If another admin inserts the same location concurrently, the current entry is **skipped**, logged, and reported as a **duplicate conflict**.  


**6. Partial Success & Transaction Handling**  
- Tracks **success and failure** counts independently.
- Returns **201 Created** if all entries succeed.
- Returns **206 Partial Content** with details of skipped or failed entries if some entries could not be inserted.
- Uses `@Transactional` to ensure that each entry’s creation is **atomic**; failures in one entry do not roll back successful insertions.  
 
**7. No Early Return / No Loop Breaks**    
- Every input is processed
- No failures stop the batch
- Ensures maximum data insertion rate  



#### 📤 Success Response
<details> 
  <summary>View screenshot</summary> <br>
   ![Location Bulk Insertion Success]()
</details>

#### 📤 Partial Success Response
<details> 
  <summary>View screenshot</summary> <br>
   ![Location Bulk Insertion Partial Success]()
</details>

#### ❗ Error Responses
> Empty list or invalid structure  
<details> 
  <summary>View screenshot</summary> <br> 
   ![Location Bulk Insertion Error]()
</details>  

> Every element failed (but still 201 with error summary)  
<details> 
  <summary>View screenshot</summary> <br> 
   ![Location Bulk Insertion Error]()
</details>  

> Unauthorized (invalid/expired JWT)  
<details> 
  <summary>View screenshot</summary> <br> 
   ![Location Bulk Insertion Error]()
</details>  


#### HTTP Status Code Table  
| HTTP Code | Status Name           | Meaning               | When It Occurs                                   |
| --------- | --------------------- | --------------------- | ------------------------------------------------ |
| 201       | CREATED               | Locations created     | All entries successfully added                   |
| 206       | PARTIAL_CONTENT       | Partial Success       | Some entries skipped due to duplicates or errors |
| 400       | BAD_REQUEST           | Validation Failed     | Invalid DTO or empty list                        |
| 401       | UNAUTHORIZED          | Authentication Failed | Token invalid or expired                         |
| 403       | FORBIDDEN             | Access Denied         | User lacks ADMIN role                            |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error      | Any unhandled server-side exception              |



#### ⚠️ Edge Cases & Developer Notes
**1. Hierarchical Data Integrity (Country → State → City Enforcement)**  
- The API enforces a strict **top-down structure**.
- Each DTO entry is checked: **City cannot exist without State**, and **State cannot exist without Country**.
- Even in bulk operations, this ensures **referential integrity**, preventing incomplete or invalid location hierarchies.
- Long-term data consistency is guaranteed; avoids “**partial trees**” in production.

**2. Advanced Duplicate Prevention (Management + Master Repository Check)**   
- Every entry is checked against **two sources**:
    - Management’s own hierarchical tables.
    - Master location table shared across the application.

- This Prevents:
    - Same location inserted by two admins seconds apart.
    - City duplication under different states.
    - Cross-module inconsistencies.
     
- Results list contains detailed messages for skipped entries.  
 
**3. Concurrency Protection with Optimistic Locking**  
- Handles **simultaneous insertions** by multiple admins or other management users.
- If Admin A inserts a location at the same time as Admin B:
    - One succeeds.
    - The other receives **duplicate conflict** and is skipped.
     
- Ensures **atomic, conflict-free inserts** even at scale (600+ cities).  

**4. Enum Parsing & Input Normalization Pipeline**  
- Country and State strings are validated using `ParsingEnumUtils` utility class for enum parsing across my system.
- Ensures only **recognized values** pass through.
- Guards business logic from invalid inputs and misspellings or dirty data's.
- Supports controlled future expansion of countries/states.  

**5. Transactional Safety, Audit Logging & Scalability**  
- Each entry creation is **transactional**. Failures in one entry do not rollback the others.
- Logs every successful insertion with **Management ID, username, and full location details**.
- Designed for **real-world production usage** and massive datasets.
</details>  


### 🔍 7. View Location Records (Filter + Sorting + Pagination)
<details> 
  <summary><strong>GET</strong> <code>/bookmyride/management/locations</code></summary>
  
#### 🛠 Endpoint Summary
**Method:** GET  
**URL:** /bookmyride/management/locations  
**Authentication:** Required (JWT)  
**Roles Allowed:** ADMIN  

#### 📝 Description
This API allows `Management users` to **fetch paginated, filtered, and sorted lists of location records** (City → State → Country). It supports **dynamic filtering** by `country`, `state`, `city`, and `role` of the creator. The service ensures **input validation, enum parsing, pagination safety, sorting correctness**, and **detailed feedback** for empty or invalid queries.  

Key highlights:  

**Edge-case behaviors:**  
- Validates Admin user access using `@PreAuthorize` and `UserPrincipalValidationUtils` utility class.
- Accepts query parameters for **page, size, sorting,** and **filtering**.
- Uses `PaginationRequest` for **pagination input validation** and pageable construction.
- Filters are validated against **RegEx patterns** and parsed into enums (`Country`, `State`, `Role`) where applicable.
- Returns `ApiPageResponse` (custom class) wrapping the results along with pagination metadata.


#### 📥 Query Parameters
| Parameter | Type   | Default | Description                                                                                   | Required |
| --------- | ------ | ------- | --------------------------------------------------------------------------------------------- | -------- |
| page      | Integer| 1       | Page number (1-based from client)                                                             | No       |
| size      | Integer| 10      | Number of items per page                                                                      | No       |
| sortBy    | String | id      | Field to sort by (`id`, `name`, `state.name`, `state.country.name`, `createdAt`, `updatedAt`) | No       |
| sortDir   | String | asc     | Sort direction (`asc` or `desc`)                                                              | No       |
| country   | String | -       | Filter by Country (must match Country regex, capitalized)                                     | No       |
| state     | String | -       | Filter by State (must match State regex, capitalized)                                       | No       |
| city      | String | -       | Filter by City (starts with capital letter)                                                   | No       |
| role      | String | -       | Filter by Creator Role                                                                        | No       |

> Example url to try: `/bookmyride/management/locations?page=2&size=5&country=India&sortBy=city&sortDir=desc`

#### ⚙️ Backend Processing Flow
**1. Pagination & Sorting Validation**  
- Invokes `PaginationRequest.getRequestValidationForPagination()` to validate:
   - `page >= 1`
   - `size >= 1`
   - `sortBy` in allowed fields (`id`, `name`, `state.name`, `state.country.name`, `createdAt`, `updatedAt`)
   - `sortDir` as `ASC/DESC`  
- Returns **400 Bad Request** if any parameter is invalid.
- Converts validated request into `Pageable` object using `PageRequest.of(page-1, size, sortDir, sortBy)`.
  
**2.Filter Parsing & Enum Validation**  
- For `country`, `state`, and `role` filters:
    - Validates using regex patterns.
    - Parses string values into corresponding enums (`Country`, `State`, `Role`) via `ParsingEnumUtils.getParsedEnumType()`.
- Returns **400 Bad Request** for invalid enum values.

**3. Dynamic Query Selection**  
Based on which filters are provided, the backend selects the proper repository method:  

 - `country + state + city` → `cityEntityRepo.findByNameAndState_NameAndState_Country_Name()`
 - `country + state` → `cityEntityRepo.findByState_NameAndState_Country_Name()`
 - `state + city` → `cityEntityRepo.findByNameAndState_Name()`
 - Single filters (`country`, `state`, `city`) → respective methods
 - `role` filter → `cityEntityRepo.findByCreatedById_Role()`
 - No filters → `cityEntityRepo.findAll(pageable)`  

**4. Mapping & Response Construction**  
- Converts `CityEntity` results into `LocationResponseDto` via `LocationMapper.entityToLocationResponseDto()`.
- Wraps the result into `ApiPageResponse`, including:
   - `totalPages`, `totalElements`, `currentPage`, `pageSize`, `isFirst`, `isEmpty`.
- Returns **200 OK** with data or **404 Not Found** if the page has no results.
- Provides descriptive message: `"No data found"` vs `"Data found"` depending on result presence.

**5. Error Handling & Feedback**   
- Invalid regex or enum → **400 Bad Request** with precise guidance (capitalization, valid options).
- Non-existent filter combination → **404 Not Found**, but still provides empty page metadata.
- Internal server exceptions → **500 Internal Server Error** (rare due to defensive validations).

#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Location View Success]()
</details>


#### ❗ Error Responses
<details> 
  <summary>View screenshot</summary>
   ![Location View Success]()
</details>

#### HTTP Status Code Table
| HTTP Code | Status Name       | Meaning               | When It Occurs                                |
| --------- | ----------------- | --------------------- | --------------------------------------------- |
| 200       | SUCCESS           | Request succeeded     | Data found and returned successfully          |
| 400       | BAD_REQUESt       | Validation Failed     | Invalid ID / regex or enum fail               |


#### ⚠️ Edge Cases & Developer Notes  
**1. Hierarchical Filtering & Enum Safety**  
- Ensures **Country → State → City** hierarchy is respected in queries.
- Parsing enums prevents invalid or misspelled inputs from reaching the repository.
- Allows the system to scale safely when new countries or states are added without code modification.  

**2. Flexible Partial Filtering**  
- Supports any combination of filters (`country`, `state`, `city`, `role`).
- Each combination triggers a **specific repository method** to optimize database queries.
- Handles **missing intermediate levels gracefully**, e.g., querying `city + state` without `country`.  

**3. Pagination & Sorting Safety**  
- Page numbers are **1-based externally**, **0-based internally**.
- Returns **structured page metadata** even if the page is empty.
- Protects against invalid sort fields or directions with clear error messages.  

**4. Dynamic Query Behavior**  
- Multiple filter combinations are resolved **dynamically at runtime**.
- Prevents unnecessary full-table scans by delegating to filtered repository methods.
- Ensures **consistent, predictable pagination** even with partial data.

**5. Performance & Maintainability**  
- Mapping entities to DTOs ensures **API response decoupled from database structure**.
- Regex validation and enum parsing **prevents invalid queries**, reducing load and errors.
- Designed for **enterprise readiness**, supporting role-based access, filtered views, and large dataset pagination.  
- Query performance optimized for large datasets (~10k+ records).  
</details>


### 📝 8. Update Location Records (City + State + Country Hierarchy)
<details> 
  <summary><strong>PUT</strong> <code>/bookmyride/management/locations/{id}</code></summary>

#### 🛠 Endpoint Summary
**Method:** PUT  
**URL:** /bookmyride/management/locations/{id}  
**Authentication:** Required (JWT)  
**Roles Allowed:** ADMIN  

#### 📝 Description
This endpoint updates the complete hierarchical Location structure—**City** → **State** → **Country**—using a single validated DTO. The update process is entity-aware and selective, meaning each layer (`CountryEntity`, `StateEntity`, `CityEntity`, and `MasterLocation`) is updated **only when its incoming value differs**, ensuring minimal writes and preserving high data integrity.

It enforces strict cross-entity consistency between the 3-level relational structure and MasterLocation by performing:
- Performs **strict enum parsing** for both `State` and `Country` with descriptive error feedback.
- Applies **regex-based string validation** to ensure clean, predictable input values.
- Prevents duplicates across **two independent systems**:
    - The main relational location tables
    - The `MasterLocation` registry
- Ensures the existence and correctness of the **target MasterLocation record** before updating any entity.
- Executes **multi-level conditional updates** (Country → State → City) only if the new values differ from stored values.
- Uses **optimistic locking controls** to detect collisions when multiple authority-level users attempt to edit the same record simultaneously.
- Executes the entire operation within a **single transactional boundary**, guaranteeing atomic, consistent, and rollback-safe updates.

#### 📥 Request Body
{  
&nbsp;&nbsp;&nbsp; "city": "Chennai",  
&nbsp;&nbsp;&nbsp; "state": "Tamil Nadu",  
&nbsp;&nbsp;&nbsp; "country": "India"  
}  
> 💡 Also can use your own values here.  
> 💡 Uses the exact same DTO validation rules as creation APIs — including enum validation and required fields.  

#### ⚙️ Backend Processing Workflow (High-Density Version)
**1. Authorization & Management User Validation**  
- VExtracts `UserPrincipal` via `@AuthenticationPrincipal`.
- Uses `UserPrincipalValidationUtils.validateUserPrincipal()` to verify:      
     - **JWT** is valid
     - Management account exists
     - Account role is exactly `ADMIN`
- Returns **401**, **403**, or **404** depending on failure type.  

**2. Validate Path Variable & Request Body**  
- Ensures `{id}` is not null, zero, or negative.
- Uses `@Valid` + `BindingResult` to check DTO constraints.
- Any field-level errors return **400** with aggregated messages using `BindingResultUtils`.  
 
**3. Fetch Existing Location Record**    
- Attempts to load `CityEntity` for the provided ID.
- If absent → returns **404 NOT_FOUND** immediately.
- Fetches associated `StateEntity` and `CountryEntity` for contextual checks.

**4. Enum Parsing & Syntax Validation**  
- Uses `ParsingEnumUtils` to validate and convert input strings to enum types:
   - `Country`
   - `State`
- If parsing fails → returns **400 BAD_REQUEST** with a descriptive error message.  
-This ensures only clean, normalized, and recognized geographical enums enter the logic.

**5. Duplicate & Hierarchy Conflict Prevention (Two-Level)**  
- Before applying any update:  
- Checks if the new City-State-Country combination already exists in:   
   - **Level 1:** Location (`CityEntity`, `StateEntity`, `CountryEntity`) relational tables.     
   - **Level 2:** `MasterLocation` table.  
- If found and it differs from the current record: → returns **409 CONFLICT** (prevents creating invalid duplicates under new states/countries)  

**Additional duplicate check:**  
- If city name changes, ensures the same city does not already exist under the target state.  
- These validations guarantee:  
    - No duplicate cities across the state
    - No cross-hierarchy collisions
    - No conflicting MasterLocation records

**6. MasterLocation Consistency Guard**  
- Attempts to fetch the related MasterLocation record for the current location.
- If missing → returns **404 NOT_FOUND** with a clear inconsistency diagnostic.  

This protects against silent desynchronization between:  
 - Management hierarchy (City/State/Country Entities)
 - MasterLocation reference table

**7. Hierarchical Update Execution**  

Each entity updates **only if changed**:  
  - If country changed → update `CountryEntity`
  - If state changed → update `StateEntity` (linked to updated/new country)
  - If city changed → update `CityEntity` (linked to updated/new state)
Every update applies `updatedAt`, `updatedBy`, version increment. All changes are performed inside a `@Transactional` boundary ensuring entity-level atomicity.  

**8. MasterLocation Synchronization**  

After city, state, and country updates:  

- Updates the corresponding MasterLocation record.
- Ensures global location references remain consistent.
- Tracks modifying admin details for audit logs.

Critical for multi-module cross-referencing.  

**9. Concurrency Protection**  

If any other admin modified the location during update:
 - JPA throws `ObjectOptimisticLockingFailureException` or `OptimisticLockException`.
 - Returns **409 CONFLICT** with a message indicating that the record was modified elsewhere.
 - Prevents overwriting changes or causing corrupted hierarchies.  


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Location Update Success]()
</details>

#### ❗ Error Response
> Invalid ID / DTO / Parsing Errors  
<details> 
  <summary>View screenshot</summary>
   ![Location Update Error]()
</details>  

> Not Found (ID or MasterLocation Missing)  
<details> 
  <summary>View screenshot</summary>
   ![Location Update Error]()
</details>  

> Duplicate Conflict (Hierarchy / City / MasterLocation)  
<details> 
  <summary>View screenshot</summary>
   ![Location Update Error]()
</details>  

> Optimistic Lock / Concurrent Modification  
<details> 
  <summary>View screenshot</summary>
   ![Location Update Error]()
</details>   

> Unauthorized / Forbidden  
<details> 
  <summary>View screenshot</summary>
   ![Location Update Error]()
</details>  


#### HTTP Status Code Table
| HTTP Code | Status Name       | Meaning               | When It Occurs                                |
| --------- | ----------------- | --------------------- | --------------------------------------------- |
| 200       | OK                | Request succeeded     | Data found and returned successfully          |
| 400       | BAD_REQUEST       | Validation Falied     | Invalid ID / invalid DTO / regex or enum fail |
| 401       | UNAUTHORIZED      | Authentication Failed | Missing or invalid JWT                        |
| 404       | NOT_FOUND         | Resource Not Found    | CityEntity / MasterLocation entity missing    |
| 403       | FORBIDDEN         | Access Denied         | Only Authority users could modify             |
| 409       | CONFLICT          | Duplicate Entry       | New combination already exists / Optimistic lock conflict |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error | Unexpected server-side error              |


#### ⚠️ Edge Cases & Developer Notes
**1. Hierarchical Integrity & Cross-Level Data Consistency Enforcement**  

This API enforces end-to-end hierarchical integrity across `Country → State → City` during updates, even when only **one field value is changed**. Before applying updates, the system re-evaluates the entire hierarchy to ensure that the modified values still form a valid and consistent location structure.  

This mechanism prevents:  

- **Orphan States** (states pointing to incorrect or outdated countries).
- **City-State Mismatches** (cities detached from their intended state or shifted incorrectly).
- **Invalid Cross-Country Assignments** (states being reassigned across countries accidentally).
- **Partial or fragmented hierarchies** caused by updating one entity without synchronizing the others.  

 By enforcing strict re-validation rules across all layers, the system guarantees that every update maintains structural correctness, referential integrity, and long-term maintainability of the location hierarchy.  

**2. Multi-Layer Duplicate Detection Across Main Location & MasterLocation Domains**  

Duplicate prevention logic runs through two independent validation layers:  

**1. Management Hierarchy Tables**  
- `CountryEntity`
- `StateEntity`
- `CityEntity`  
 
**2. Global MasterLocation Repository**  
- Application-wide index used across modules
 
This dual-layer approach ensures:  
- **Zero conflicting entries** across different modules.
- **Prevention of multiple cities with the same name under the same state.**
- **Guarding against inconsistent or “hidden” duplicates** that may originate from older data or cross-service operations.
- **Full avoidance of state-level and country-level duplication** when values change during update

This is essential for large-scale environments handling **1000+ hierarchical entries**, ensuring that all updates respect existing relationships and global constraints.  

**3. MasterLocation Integrity Validation & Data Recovery Protection**  

Before performing any update, the system verifies the presence and correctness of the corresponding `MasterLocation` record for the current city–state–country.  

If the MasterLocation entry is missing:  
- The update is **immediately stopped**
- A **404 NOT_FOUND** is returned with a precision diagnostic

 This protects the system from severe data integrity failures, including:  
- **Silent reference** loss between Management and MasterLocation
- **Inconsistencies across modules** relying on MasterLocation identifiers
- **Broken relational mapping** that could corrupt downstream services
- **Partial updates** where hierarchy tables update, but global references do not

 This mechanism ensures that **no update proceeds unless both layers remain in perfect sync**, making the system highly resilient to corruption and misalignment.  

 
**4. Robust Concurrency Control with Optimistic Locking**  
- The update pipeline is fortified with optimistic locking to handle multi-admin, high-concurrency environments.  
- If two administrators update the same record simultaneously:
    - **First update succeeds**
    - **Second receives a deterministic** `409 CONFLICT` with an actionable message  

This prevents:  
- **Dirty writes**
- **Lost updates**
- **Overwritten changes from other admins**
- **Race-condition-related corruption of hierarchical data** 

By relying on entity versioning, the API guarantees consistent, predictable modification behavior even under heavy concurrency, ensuring that location data remains stable and conflict-free.  


**5. Transactional Update Pipeline with Enum Normalization & Atomic Hierarchical Persistence**  
- The entire update operation executes within a strict `@Transactional` block, ensuring atomicity and consistency at every layer of the hierarchy.  

**Transactional Guarantees:**
- **All-or-nothing persistence** for each hierarchical update
- **Rollback of only the current update attempt**, preserving previously correct data.
- Automatic consistency between updated country, updated state, and updated city.
- Safe updates to **MasterLocation synchronously** within the same transaction cycle.
- **Comprehensive audit logging**, capturing admin ID, username, and modified values.    

**Enum Normalization:**   

Input values for `country` and `state` undergo validation via `ParsingEnumUtils`, ensuring:  
- No typos or irregular formats.
- No unsupported enum values.
- No inconsistent naming across services.
- Fully standardized and sanitized input before reaching deeper logic.  

This combination ensures a highly scalable, error-resistant update pipeline capable of maintaining long-term hierarchical accuracy across all related domains.
</details>


### 🗑️ 9. Delete Location Record (City + State + Country Hierarchy)  
<details> 
  <summary><strong>DELETE</strong> <code>/bookmyride/management/locations/{id}</code></summary>
 
#### 🛠 Endpoint Summary
**Method:** DELETE  
**URL:** /bookmyride/management/locations/{id}  
**Authentication:** Required (JWT)  
**Roles Allowed:** ADMIN  

#### 📝 Description  
This API allows an authenticated Management/Admin user to permanently delete a location entry from the **hierarchical structure (City → State → Country)**.
The deletion ensures synchronized removal across both the Management location tables and the MasterLocation global repository.  
The API includes robust validation, optimistic locking protection, and detailed integrity checks to prevent inconsistent data states.  

Key highlights:  
- Deletes an existing `CityEntity` and its corresponding entry in `MasterLocation`.
- Ensures the MasterLocation reference for the city exists before deletion.
- Fully transactional — protects against partial or inconsistent deletions.
- Applies optimistic locking to prevent concurrent deletion conflicts.
- Provides clear, descriptive responses for missing IDs, missing records, or synchronization issues.  

#### 📥 Request Parameter  
**Path Variable:** `id` — The unique `City ID` to delete.  

#### ⚙️ How the Backend Processes This (Step-by-Step)
**1. Authenticate and Validate Admin User**  
- Extracts `UserPrincipal` from **JWT Token**.
- Validates admin identity using `UserPrincipalValidationUtils.validateUserPrincipal()`, Ensures:
   - Valid token
   - Management account exists
   - Role = ADMIN
- Returns **401, 403**, or **404** if validation fails.

**2. Validate Path Variable**  
- Checks if `id` is null, zero, or negative.
- Returns **400 BAD_REQUEST** for invalid IDs with a clear error message.

**3. Fetch CityEntity for the Given ID**   
- Queries `cityEntityRepo.findById(id)`.
- If no record exists → 404 NOT_FOUND with message.
- This avoids unnecessary operations and protects from invalid deletion attempts.

**4. Load Associated Hierarchy (State + Country)**  

Once the city exists, the system fetches:  
- `StateEntity` via `cityEntity.getState()`
- `CountryEntity` via `stateEntity.getCountry()` 

This provides the necessary hierarchical context for:
   - Logging
   - MasterLocation validation
   - Deletion consistency checks 


**5. Validate MasterLocation Consistency Before Deletion**  
- The system must confirm that the location also exists in the global MasterLocation index by using `masterLocationService.fetchByName(city, state, country)`.
- If missing: Logs a warning (indicating potential data inconsistency) & Returns **404 NOT_FOUND** with message.
- This prevents partial deletions that break system-wide consistency.

**6. Perform Hierarchy Deletion (Transactional & Atomic)**  

Deletion happens inside a `@Transactional` block to ensure atomicity:
 - After getting `CityEntity`, through that retrieved related `StateEntity` & `CountryEntity`
 - And removes the those entites from the location hierarchy.
 - Removes the `MasterLocation` record which Ensures global references remain clean and consistent.
 - Logs details such as: Management ID, username, Deleted city/state/country values, Deleted ID.
 - Ensures traceability for all destructive actions.

 **7. Concurrency Protection with Optimistic Locking**   
 
 If another admin modifies or deletes the same record concurrently:
   - The system throws `OptimisticLockException`.
   - Controller catches and returns → **409 CONFLICT** with message.
    
This prevents race-condition-based partial deletions or inconsistent states.  


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Location Delete Success]()
</details>  

#### ❗ Error Responses  
> Invalid ID
<details> 
  <summary>View screenshot</summary>
   ![Location Delete Error]()
</details>  

> Record Not Found (City or MasterLocation Missing)
<details> 
  <summary>View screenshot</summary>
   ![Location Delete Error]()
</details>  

> Unauthorized / Access Denied
<details> 
  <summary>View screenshot</summary>
   ![Location Delete Error]()
</details>  


#### 📊 HTTP Status Code Table
| HTTP Code | Status Name           | Meaning               | When It Occurs                              |
| --------- | --------------------- | --------------------- | ------------------------------------------- |
| 200       | OK                    | Location deleted      | Successful deletion                         |
| 400       | BAD_REQUEST           | Validation Failed     | Invalid ID                                  |
| 401       | UNAUTHORIZED          | Authentication Failed | Invalid/expired token                       |
| 403       | FORBIDDEN             | Access Denied         | Role not ADMIN                              |
| 404       | NOT_FOUND             | Record Missing        | City or MasterLocation not found            |
| 409       | CONFLICT              | Concurrency Issue     | Deleted by another admin / version conflict |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error      | Unhandled system failure                    |


#### ⚠️ Edge Cases & Developer Notes
**1. MasterLocation & Management Hierarchy Synchronization**  
- Deletion requires both:
   - `CityEntity`
   - `MasterLocation`
- If `CityEntity` is missing -> **404 NOT_FOUND**, Rather operation is blocked.
- If MasterLocation is missing, deletion is blocked to prevent:
   - Half-deleted records
   - Broken references across modules
   - Inconsistent relationships in analytics or downstream services
- This ensures data consistency across all layers.   

**2. Concurrency & Race-Condition Protection**  

Because deletion is destructive, optimistic locking prevents:  
- Two admins deleting the same record at the same time.
- Deleting a record immediately after another update.
- Partial cascade deletions.

The API ensures only one admin can modify that location at any given moment. 

**3. Transactional Atomic Deletion**  

 The entire delete operation is atomic:
   - `MasterLocation` and `CityEntity` deletions happen together.
   - Any exception automatically rolls back the operation.
   - Prevents leftover orphan records or partially removed hierarchies.  

**4. Defensive Design Against Inconsistent Historical Data**  

If older data becomes corrupted or out-of-sync:  
 - The API halts deletion
 - Gives a meaningful error
 - Logs the inconsistency  

 Allowing developers to diagnose and repair underlying data issues.
 </details> 


### 🗑️ 10. Delete ALL Location Data (Complete Hierarchical Wipe)
<details> 
  <summary><strong>DELETE</strong> <code>/bookmyride/management/locations/all</code></summary>

#### 🛠 Endpoint Summary  
**Method:** DELETE  
**URL:** /bookmyride/management/locations/all  
**Authentication:** Required (JWT)  
**Roles Allowed:** ADMIN  

#### 📝 Description
This API performs a complete hierarchical wipe of all location data stored in the system. The deletion is fully transactional, ensuring that all records are removed atomically without leaving behind partial or inconsistent data. Optimistic locking protects against concurrent admin-triggered mass deletions. Detailed audit logs are captured for accountability.
It deletes entries across:
- `CityEntity`
- `StateEntity`
- `CountryEntity`
- `MasterLocation` global reference table

#### 📥 Request Body
This endpoint **does not require any request body**.  

#### ⚙️ How the Backend Processes This (Step-by-Step)  
**1. Authenticate & Validate Admin User**  
- Extracts the `UserPrincipal` from **JWT** and pas through `UserPrincipalValidationUtils`.
- Validates that:
    - Token is valid
    - User exists
    - Role = ADMIN
      
- If validation fails → returns **401, 403, or 404** with structured JSON.  

**2. Trigger the Hierarchical Data Wipe (Transactional Scope)**  
- Inside a strict `@Transactional` block:
   - Deletes all data inside `CityEntity` through JPA method `cityEntityRepo.deleteAll()`.
   - Deletes all data inside `StateEntity` through JPA method `stateEntityRepo.deleteAll()`.
   - Deletes all data inside `CountryEntity` through JPA method `countryEntityRepo.deleteAll()`
- This maintains relational correctness — cities and states are removed before countries to avoid FK violations.  

**3. Wipe the MasterLocation Table**  
- Using masterLocation's repository JPA method `masterLocationService.deleteAllLocationData()` deletes all records in it. Synchronizes global reference indexing with the freshly cleared hierarchy.
- Therefore it Guarantees:
   - No leftover city/state/country entries
   - No orphaned global locations
   - Full dataset reset

**4. Concurrency Protection (Optimistic Locking)**  

If two admins attempt a full wipe simultaneously:
- First wipe succeeds
- Second receives -> **409 CONFLICT** with message “All location data was already deleted by other authority.”
- This prevents:
    - Duplicate operations
    - Race-condition induced partial deletions
    - Unnecessary system load
- Finally logs that include Logs include: **Management ID, Username, Time of deletion and Action description**.
- Ensures traceability for all large-scale destructive operations.


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Location Delete All Success]()
</details>    

#### ❗ Error Responses  
> Authentication / Authorization Failure
<details> 
  <summary>View screenshot</summary>
   ![Location Delete All Error]()
</details>    

> Deletion failed due to id
<details> 
  <summary>View screenshot</summary>
   ![Location Delete All Error]()
</details>  

#### 📊 HTTP Status Code Table
| HTTP Code | Status                | Meaning               | When It Occurs                   |
| --------- | --------------------- | --------------------- | -------------------------------- |
| 200       | OK                    | All data deleted      | Successful hierarchical wipe     |
| 401       | UNAUTHORIZED          | Authentication failed | JWT missing or invalid           |
| 403       | FORBIDDEN             | Access denied         | Role ≠ ADMIN                     |
| 404       | NOT_FOUND             | Invalid admin         | Principal validation failed      |
| 409       | CONFLICT              | Concurrent deletion   | Another admin already wiped data |
| 500       | INTERNAL_SERVER_ERROR | Server error          | Underlying system failure        |


#### ⚠️ Edge Cases & Developer Notes
**1. Atomic Hierarchical Reset With MasterLocation Synchronization**  

The deletion process removes **City → State → Country** records in the correct hierarchical order and synchronizes the `MasterLocation` index in the same transactional flow. This guarantees:
- No orphaned states or cities.
- No dangling MasterLocation entries.
- No mismatched global mappings.
- A perfectly clean reset of the entire location ecosystem.  

The hierarchy and its master index are always wiped together, maintaining strict cross-table integrity.

**2. Full Transactional Safety With Optimistic Lock Concurrency Control**   
- All delete operations occur inside a **single atomic transaction**:  
    - `cityEntityRepo.deleteAll()`
    - `stateEntityRepo.deleteAll()`
    - `countryEntityRepo.deleteAll()`
    - `masterLocation deleteAll()`

- If any step fails, the entire wipe is rolled back → ensuring **zero partial-deletion scenarios**. Optimistic locking protects against multi-admin conflicts:
   - First admin → deletion succeeds
   - Second admin → receives **409 CONFLICT**
   - Prevents destructive race conditions and duplicate mass-wipes
- This guarantees stability even in high-concurrency environments.

**3. Ideal for System Resets With Strong Audit Traceability**  
- This endpoint is designed for situations requiring a **clean slate**, such as:  
  - Environment resets
  - Test data cleanup
  - Re-initializing corrupted datasets

#### ➡️ What To Do Next  
Once all location data has been fully reset and re-initialized, the next operational step for a Management user is to begin **adding Bus records** into the system.  

With the Country → State → City hierarchy now clean and consistent:
 - Bus registration
 - Route assignments
 - Trip creation
 - Booking workflows

This ensures that the transportation module is built on a fully validated and conflict-free location foundation, allowing all downstream APIs to function reliably. 
</details>


### 🚌 11. Add New Bus Data (Bus Registration & Scheduling)
<details> 
  <summary><strong>POST</strong> <code>/management/buses</code></summary>

#### 🛠 Endpoint Summary  
**Method:** POST  
**URL:** /management/buses  
**Authentication:** Required (Admin JWT token)  
**Authorized Roles:** ADMIN  

#### 📝 Description
This API allows a **Management/Admin** user to register a **new bus** into the system. It captures the bus details including name, number, type, registration state, permit status, capacity, route, schedule, and fare. The service ensures **uniqueness of bus number, validation of enumerated inputs, and location integrity**. Optimistic locking and transactional safety prevent race conditions and ensure consistent state in the database.  

Key highlights:  
- Validates authenticated admin user before performing any operations.
- Ensures **bus number uniqueness** to prevent duplicates.
- Parses and validates enumerated inputs:
    - BusType (e.g., AC, Sleeper, AC_SEATER, etc..)
    - State of Registration (e.g., TAMIL_NADU, ANDRA_PRADESH, KARNATAKA, etc..)
    - Permit Status (eg., PERMITTED, NOT_PERMITTED)
- Validates route locations using `MasterLocation` repository (fromLocation → toLocation).
- Converts departure time from string → `LocalTime` with strict format checking.
- Sets available seats equal to total capacity automatically.
- Calculates arrival time from **departure + duration**.
- Audit logs all admin actions (Management ID, username, created bus details).
- Protected by:
    - JWT authentication
    - ADMIN role enforcement
    - DTO validation
    - Optimistic locking
    - Transactional safety

#### 📥 Request Body (Sample)
{
&nbsp;&nbsp;&nbsp; "busNumber": "TN01CC1234",
&nbsp;&nbsp;&nbsp; "busName": "Chennai Express",
&nbsp;&nbsp;&nbsp; "busType": "Sleeper",
&nbsp;&nbsp;&nbsp; "stateOfRegistration": "Tamil nadu",
&nbsp;&nbsp;&nbsp; "interStatePermitStatus": "Permitted",
&nbsp;&nbsp;&nbsp; "capacity": 50,
&nbsp;&nbsp;&nbsp; "fromLocation": "Chennai",
&nbsp;&nbsp;&nbsp; "toLocation": "Madurai",
&nbsp;&nbsp;&nbsp; "hours": 6,
&nbsp;&nbsp;&nbsp; "minutes": 30,
&nbsp;&nbsp;&nbsp; "departureAt": "21:00:00",
&nbsp;&nbsp;&nbsp; "fare": 650.00
}
> 💡 Tip: Substitute placeholders with your preferred values. But remember, my system will block entries that do not match its rules. For more info please refer the **BusDto class** under **dto package** in the application folder.  

#### ⚙️ How the Backend Processes This  
**1. Authenticate and Validate Admin User**  
- Extracts `UserPrincipal` from `@AuthenticationPrincipal`.
- Uses `UserPrincipalValidationUtils.validateUserPrincipal()` to ensure the user exists, is active, and has the **ADMIN role**.
- Returns **401 Unauthorized** or **403 Forbidden** if validation fails.  

**2. DTO & Input Validation**  
- Uses `@Valid` annotation on `BusDto` with `BindingResult`.
- Returns **400 Bad Request** listing validation failures if inputs are missing or malformed.  

**3. Bus Number Uniqueness Check**  
- Checks if `busService.existsBusNumber(busDto.getBusNumber())`.
- Returns **409 Conflict** if the bus number already exists.  

**4. Parse Enumerated Types**   
- Converts `busType`, `stateOfRegistration`, and `interStatePermitStatus` strings into enum types via `ParsingEnumUtils`.
- Returns **400 Bad Request** if invalid or unrecognized.  

**5. Validate Route Locations**  
- Uses `ValidateLocationUtils.validateLocation()` to ensure `fromLocation` and `toLocation` exist in `MasterLocation` repository.
- Returns **400 Bad Request** or **404 Not Found** if locations are invalid or missing.  

**6. Time & Duration Processing**  
- Parses `departureAt` into `LocalTime` using strict **HH:mm:ss format**.
- Computes `arrivalAt = departureAt + duration`.
- Returns **400 Bad Request** if time parsing fails.  

**7. Hierarchical Entity Mapping & Bus Creation**  
- Maps DTO → Entity via BusMapper.toEntity().
- Sets all fields:
    - AC type & seat type (derived from BusType)
    - Capacity & available seats
    - From → To MasterLocation
    - Fare, schedule, and duration
    - Audit info (createdBy, createdAt)
- Saves the bus entity in a transactional manner.  

**8. Error Handling**  
- `ObjectOptimisticLockingFailureException` or `OptimisticLockException` → **409 Conflict**, with descriptive message.
- Generic exceptions → **500 Internal Server Error** with standardized response.

#### 📤 Success Response







 
</details>  






