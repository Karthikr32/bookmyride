# BookMyRide 🚍 v1 - *A Full-Featured Bus Booking System*
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

### 🔸 Authentication & JWT Token Usage  

**_BookMyRide_** uses **JWT (JSON Web Token)** for authenticating users across all passengers and management endpoints. This centralized mechanism ensures secure, stateless authentication and consistent client integration.  

#### 1. JWT Issuance  
- Tokens are issued upon successful signup or login.
- Returned in the custom response using `ApiResponse` as a string under the data field:

##### Response Body  
{  
&nbsp;&nbsp;&nbsp; "status": 201,  
&nbsp;&nbsp;&nbsp; "message": "<Custom_Message_According_To_Request>",  
&nbsp;&nbsp;&nbsp; "data": "<JWT_TOKEN>"  
}  
> Frontend/client extracts the token from `data` for subsequent requests.   

#### 2. Using the JWT Token  
- Include the token in the Authorization header of all protected API requests:
> **`Authorization: Bearer <JWT_TOKEN>`**.
- **Scope:** Provides access to endpoints requiring authentication (profile, data creation/updation, higher authority actions, etc.).
- **Validity:** Each token is valid for **6 hours**. Requests with expired tokens will return: **401 UNAUTHORIZED**.  

#### 3. Best Practices  
- **Store securely:** Use secure storage on mobile apps or HTTP-only cookies/localStorage for web apps.
- **Always include in requests:** Any request that is misses or invalid tokens are rejected.
- **Renewal:** After expiration, users must login again to obtain a new token.
- **Consistency:** This mechanism is uniform for all passengers (USER/GUEST) and management APIs.  

#### 4. Reference in API Documentation  

All Signup/Login API endpoints (user or management) return JWT in the same format. Rather than repeating usage instructions, each API doc should reference this section:
> “Upon successful signup/login, a JWT token is returned. Refer to Authentication & JWT Usage for details on how to use it.”


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
**Authorized Roles:** PUBLIC  
**Authentication:** Not Required (First step of admin login)  
(This endpoint issues a new JWT Token. For further details, See **Authentication & JWT Usage** inside Security Architecture under Technical Architecture.)  

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


### 🖋️ 2. Update Management Profile

<details> 
  <summary><strong>PUT</strong> <code>/management/profile</code></summary>

#### 🛠 Endpoint Summary  
**Method:** PUT  
**URL:** /management/profile  
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  
  
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
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  


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
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

  
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
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  


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
**Authorized Roles:** ADMIN    
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  


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
**Roles Allowed:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

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
- After fetching the paginated list of city entities, the system maps each entity into a structured LocationResponseDto using `LocationMapper.entityToLocationResponseDto()`.
- This DTO organizes location data into three clearly defined sections—**City**, **State**, and **Country**—each including metadata such as creation and update timestamps, as well as the responsible user and role.
- This structure ensures that future updates can target any level individually (city, state, or country) without breaking the API contract.
- The mapped results are then wrapped into a standard `ApiPageResponse`, which includes: `totalPages`, `totalElements`, `currentPage`, `pageSize`, `isFirst`, `isEmpty`
- The API returns **200 OK** when data is present, providing a complete, paginated snapshot of location information. If the requested page has no results, the API returns **404 NOT_FOUND** with a clear, descriptive message—**“No data found”**—to distinguish empty results from successful retrieval.

**Response Includes (via `LocationResponseDto`):**  
- **City** — Contains the city ID, name, creation and update metadata (user name, role, timestamps).
- **State** — Mirrors the City structure, providing state-level identification and metadata for administrative clarity.
- **Country** — Encapsulates country-level details in the same structured format, ensuring uniformity across the location hierarchy.  

This design delivers a **consistent, audit-ready, and future-proof response** that supports both paginated listing and granular updates at any hierarchical level (City/State/Country), making it suitable for enterprise-grade applications.   

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


### 🖋️ 8. Update Location Records (City + State + Country Hierarchy)
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


#### 📥 Request Parameter
| Parameter | Type | Description                                         | Required |
| --------- | ---- | --------------------------------------------------- | -------- |
| `id`      | Long | ID of the existing location(CityEntity) to update. Must be positive. | Yes      |


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
**Roles Allowed:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

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
| Parameter | Type | Description                                         | Required |
| --------- | ---- | --------------------------------------------------- | -------- |
| `id`      | Long | ID of the existing location(CityEntity) to delete. Must be positive. | Yes      |


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
**Roles Allowed:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

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
- Extracts the `UserPrincipal` from **JWT** and pas through `UserPrincipalValidationUtils`. This validates:
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
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

#### 📝 Description
This API allows an authorized **Management/Admin** user to add a **brand-new bus entry** into the enterprise bus registry. It serves as the core provisioning endpoint used to enrich the fleet with new travel buses, fully capable of:  

Key highlights:   
  - Strict validation of Management user identity and role before any processing.
  - Deep validation of all submitted bus attributes.
  - Enum-driven data normalization for:  
       - BusType (e.g., AC, Sleeper, AC_SEATER, etc..)
       - State of registration (e.g., TAMIL_NADU, ANDRA_PRADESH, KARNATAKA, etc..)
       - Inter-state permit status (eg., PERMITTED, NOT_PERMITTED)
  - Location validation for both **origin (fromLocation)** and **destination (toLocation)** using centralized `MasterLocation`.
  - Intelligent attribute derivation (AC type, seat type) based on the BusType
  - Converts departure time from string → `LocalTime` with strict format checking.
  - Sets available seats equal to total capacity automatically.
  - Calculates arrival time from **departure + duration**.
  - Audit logs all admin actions (Management ID, username, created bus details).
  - Protected by:
     - **JWT authentication**
     - **Role-based access enforcement**
     - **DTO validation**
     - **Optimistic locking**
     - **Transactional safety**
     - **enterprise-grade error safety**

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
> 💡 Departure time must strictly follow HH:mm:ss format.  
> 💡 Tip: Substitute placeholders with your preferred values. But remember, My system will block entries that do not match its rules.  
> 💡 Tip: For more info please refer the **BusDto class** under **dto package** in the application folder.  

#### ⚙️ How the Backend Processes This  
**1. Authenticate and Validate Admin User**  

Before the system touches any bus logic, it verifies:
 
**1.** Authenticated JWT is provided  
**2.** UserPrincipal exists  
**3.** Management account is active  
**4.** Role == ADMIN 

Failure triggers:  

  - **401 Unauthorized** if token invalid
  - **404 Not Found** if account missing
  - **403 Forbidden** if non-admin
 
This ensures only valid authority accounts can register a new bus.  

**2. DTO Validation & Syntax Checking**  

The incoming BusDto is validated using:
- `@Valid` annotation on `BusDto` with `BindingResult`.
- Mandatory fields
- Numeric ranges (capacity, fare)
- Non-empty string fields
- Time formatting
 
Returns **400 Bad Request** listing validation failures if inputs are missing or malformed.  

**3. Bus Number Uniqueness Check**  

Before deeper validations, the system performs a **fast duplicate lookup** using `existsBusNumber(busDto.getBusNumber())` JPA Method.
If the bus number already exists:  
- Operation immediately stops
- Returns 409 CONFLICT
- Prevents two buses from being assigned the same registration number.
 
This step enforces real-world fleet data uniqueness.

**4. Enum Parsing & Data Normalization Pipeline**   

The API converts incoming string values into domain enums:
 - BusType
 - State (state of registration)
 - PermitStatus  
  
Enum parsing uses centralized strict utilities:
 - Rejects misspellings
 - Rejects unsupported types
 - Ensures canonical system-wide naming  

If any input fails parsing → Returns **400 BAD_REQUEST** with precise error details.  
This avoids unregulated string inputs flowing deeper into business logic.

**5. Validate Route Locations**  
- Uses `ValidateLocationUtils.validateLocation()` to ensure `fromLocation` and `toLocation` exist in `MasterLocation` registry:
    - Ensures both endpoints exist
    - Ensures locations are globally consistent
    - Ensures no missing or malformed location strings
- Returns **400 Bad Request** or **404 Not Found** if locations are invalid or missing.
- This guarantees that every scheduled bus route uses valid, standardized, cross-module location references.

**6. Time & Duration Processing**  
- Parses `departureAt` into `LocalTime` using strict **HH:mm:ss ((zero-tolerant, strict resolver))**.
- Returns **400 Bad Request** if time parsing fails.
- This ensures predictable daily scheduling behavior.

**7. Entity Construction & Intelligent Attribute Derivation**  
- Maps DTO → Entity via BusMapper.toEntity().
- Sets all fields:
    - AcType/Seat type (derived from `BusType`)
    - Capacity & available seats
    - From → To locations via `MasterLocation`
    - Fare, schedule, and duration
    - ArrivalTime = departureAt + duration
    - Registration details
    - Audit info (createdBy, createdAt)
- Saves the bus entity in a transactional manner.
- This ensures every bus is stored with a **complete, ready-to-use operational profile**.

**8. Optimistic Locking (Concurrency Safety)**  
If two admins attempt to add a bus with identical details:
 - One succeeds
 - The other receives a deterministic **409 CONFLICT**  

Triggered by:  
- `ObjectOptimisticLockingFailureException` or `OptimisticLockException` → **409 Conflict**, with descriptive message.
- Generic exceptions → **500 Internal Server Error** with standardized response.
 
Prevents conflicted or duplicate entries in high-concurrency admin environments.

#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Bus Added Success]()
</details>  

#### ❗ Error Responses  
> Duplicate Bus Number  
<details> 
  <summary>View screenshot</summary>
   ![Bus Added Success]()
</details>  

> Invalid Enum / Time Format  
<details> 
  <summary>View screenshot</summary>
   ![Bus Added Success]()
</details>  

> Invalid/Missing Location   
<details> 
  <summary>View screenshot</summary>
   ![Bus Added Success]()
</details>  

#### 📊 HTTP Status Code Table
| HTTP Code | Status                | Meaning               | When It Occurs                   |
| --------- | --------------------- | --------------------- | -------------------------------- |
| 201       | CREATED               | Bus Created           | All validations passed, save successful|
| 400       | BAD_REQUEST           | Validation Error      | Bad DTO, invalid enum, invalid time, or location error|
| 401       | UNAUTHORIZED          | Auth Failed           | oken missing/invalid Or UserPrincipal null|
| 403       | FORBIDDEN             | Access denied         | Role mismatch                    |
| 404       | NOT_FOUND             | Invalid admin         | Admin not found OR master location/ DB entities missing|
| 409       | CONFLICT              | Concurrent deletion   | Bus number exists OR optimistic lock triggered |
| 500       | INTERNAL_SERVER_ERROR | Server error          | Underlying system failure        |


#### ⚠️ Edge Cases & Developer Notes  
**1. Multi-Layer Validation Pipeline Ensuring Full Data Integrity**   
- Bus creation passes through a highly structured validation chain: **DTO → Enums → Locations → Time Format → Derived Attributes**.
- Each layer **stops** the pipeline immediately **upon failure**, guaranteeing that no malformed or partially resolved bus record enters the system.
- This protective sequence preserves operational correctness across booking engines, schedule generators, and fleet analytics by ensuring that every bus stored is structurally valid, complete, and routable.  


**2. Centralized Route Validation via MasterLocation for Cross-Module Consistency**  
- Both the `fromLocation` and `toLocation` are validated against the `MasterLocation` registry, ensuring consistent geography across all modules such as booking, pricing, dispatching, and user search.
- This prevents buses from being assigned to nonexistent or stale routes, eliminates **cross-country/state mismatches**, and ensures that every bus route is globally recognized by the platform.
- Developers integrating future modules can trust that all route endpoints are canonical and clean.  

**3. Enum Normalization & Attribute Derivation to Prevent Client-Side Misconfiguration**  
- Critical enum fields like `BusType`, `State`, and `PermitStatus` undergo strict enum parsing to eliminate typos and non-standard formats.
- Additionally, key characteristics such as **AC Type** and **Seat Type** are derived automatically on the server using BusType mapping logic, preventing clients from injecting incorrect operational attributes.
- This ensures uniform behavior across all interfaces—web, mobile, admin dashboards—and maintains predictable system-wide fleet semantics.  


**4. Time Parsing, Duration Logic & Arrival Calculation Ensuring Scheduling Accuracy**   
- **Departure time** is parsed with **strict resolver** rules (“**HH:mm:ss**” mandatory), and arrival times are computed using validated durations, not client-supplied values.
- This removes ambiguity around day rollovers, invalid time formats, and timing misalignment in timetable generation.
- By controlling time calculations internally, the system maintains reliable departure–arrival consistency essential for fare calculations, ticket availability windows, and real-time journey tracking.  

**5. Robust Duplicate & Concurrency Protection via Pre-Checks + Optimistic Locking**  
- The system performs a fast **duplicate bus-number check** before deeper validations to save substantial processing cost.
- For high-concurrency admin environments, optimistic locking prevents race conditions where two managers attempt to register similar buses.
- One insertion succeeds while others fail cleanly with a conflict response, ensuring that the fleet registry remains free from duplicates and unresolved partial writes.  

**6. Transactional Atomicity, Audit Logging & Strict Referential Integrity**  
- All write operations occur within a single transactional boundary, guaranteeing atomic persistence—either all components of a bus entry are stored, or none are.
- Each successful insertion logs management identity and bus ID, forming a consistent audit trail essential for operations, troubleshooting, and compliance.
- Dependence on MasterLocation and strict mapping rules ensures no orphaned references or mismatched relationships appear even if the platform grows with new modules or more complex fleet management features.
</details>  


### 🚌 12. Viewing Bus Entries (Management View)
<details> 
  <summary><strong>GET</strong> <code>/management/buses</code></summary>


#### 🛠 Endpoint Summary   
**Method:** GET  
**URL:** /management/buses  
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

#### 📝 Description  
This API endpoint facilitates paginated and filtered retrieval of bus records from the system, intended exclusively for administrative purposes. It supports operational tasks such as viewing, updating, or deleting bus entries within the management dashboard.  

Key functionalities:  
- **Strict Role-Based Access Control** 
Only `ADMIN` users can access this endpoint. Unauthorized or inactive tokens result in **401** or **403** with precise error descriptions.  

- **Pagination + Sorting**  
Supports page-wise retrieval and sorting via a controlled whitelist of allowed fields. Prevents excessive payloads and unsafe `ORDER BY` usage.  

- **Advanced Filtering System**  
A versatile keyword parser supports:  
    - Prefix-based filters (`id_`, `fare_`, `bus_`, `location_`)
    - Regex-driven matching for complex formats (bus numbers, times, dates)
    - Enum-safe validation of values such as bus type, booking status, seat type, AC/non-AC, and state of registration
    - Case-insensitive matching for all fields except bus number
    - Multi-format date/time parsing with strict validation  

- **DTO-Driven Output**  
Internal bus entities are transformed into `ManagementBusDataDto`, exposing only safe, relevant fields while protecting sensitive operational data.

- **Robust Error Handling**
Returns highly descriptive error responses for pagination, date/time formats, invalid enums, or unmatched keyword searches. 

- **Extensible Architecture**  
Built using a modular filtering pipeline, enabling new filters or enum types to be added without modifying core logic.


#### 📥 Query Parameters  
| Parameter | Type    | Default | Description                                                                                                                                                                    | Required |
| --------- | ------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------- |
| page      | Integer | 1       | The page number for paginated results (1-based indexing)                                                                                                                       | No       |
| size      | Integer | 10      | Number of records per page                                                                                                                                                     | No       |
| sortBy    | String  | id      | Field by which to sort the results—options include: `id`, `busName`, `busNumber`, `busType`, `stateOfRegistration`, `fare`, `departureAt`, `arrivalAt`, `createdAt`, `updatedAt`                   | No       |
| sortDir   | String  | ASC     | Sort direction, either `ASC` (ascending) or `DESC` (descending)                                                                                                                    | No       |
| keyword   | String  | -       | Optional filter string supporting multiple formats and patterns including prefixed filters (id_, fare_, bus_, location_), regex-based enums, and dates/times in strict formats | No       |


#### ⚙️ Backend Processing Flow  
**1. Authentication & Authorization**  
- Validates JWT token integrity, extracts `UserPrincipal`, confirms user exists and is active, then strictly enforces **ADMIN role requirement**. Invalid tokens trigger **401 Unauthorized**; non-admin users receive **403 Forbidden** with precise role deficiency messaging. Comprehensive token revocation list checks prevent compromised access.  

**2. Pagination Validation**  
- Applies rigorous bounds checking: `page >= 1`, `size >= 1 and <= 100`, `sortBy` restricted to whitelist of 10 canonical fields, `sortDir` limited to **ASC/DESC** variants. Utilizes `PaginationRequest.getRequestValidationForPagination()` utility for atomic validation.
- Any violation immediately returns **400 Bad Request** with field-specific error diagnostics.  

**3. Sort & Pageable Construction**  
- Transforms validated parameters into **Spring Data JPA Pageable** object with `Sort.Direction` mapping.
- Ensures database query efficiency through indexed field prioritization and avoids N+1 query pitfalls.
- Constructs optimized **PageRequest** for repository layer consumption.  

**4. Keyword Processing & Filtering**  
- Executes hierarchical filter resolution: empty keyword bypasses to busRepo.findAll(pageable).

**Prefix-Based Filters**  
   - id_ → numeric ID search
   - fare_ → fare-based filtering
   - bus_ → bus name or type
   - location_ → validated against MasterLocation  

**Other Search Capabilities**  
   - Bus number (`TN01AB1234`)
   - Departure/arrival dates and times
   - Booking statuses (`pending`, `processing`, `confirmed`, etc.)
   - Bus types (AC Sleeper, Non-AC Seater, etc.)
   - State of registration (Tamil Nadu, Kerala, etc.)
   - Seat types / AC normalization

  **Additional Notes**  
   - All fields except bus number are **case-insensitive**.
   - Enum validations performed through `ParsingEnumUtils.getParsedEnumType()`.
   - Matched results return **200 OK with pagination**.
   - If no matches → **404 Not Found with empty paginated structure**.

**5. Entity → DTO Mapping**  
- Applies BusMapper to transform `Page<Bus>` into `List<ManagementBusDataDto>`, deliberately excluding sensitive operational fields. Preserves audit trail via `createdAt`/`updatedAt` while formatting `departureAt`/`arrivalAt` as standardized `LocalTime` strings.
- Ensures frontend receives type-safe, consistent response schema.  


**6. Date & Time Parsing**  
- Date formats supported: `dd/MM/yyyy`, `dd-MM-yyyy`, `yyyy-MM-dd`.
- Time formats supported: `HH:mm` or `HH:mm:ss`.
- Employs `DateTimeFormatter with ResolverStyle.STRICT` for unambiguous resolution across supported formats.
     - Rejects invalid dates (30/02/2025)
     - Rejects invalid times (25:00, malformed seconds)
- Parsing errors return **400 Bad Request** with corrective guidance.


**7. Service Layer Logic**  
- Orchestrates dynamic query composition through **Spring Data JPA Specifications** for compound filtering. Wraps `Page<Bus>` in `ApiPageResponse` with complete pagination metadata including: `totalPages`, `totalElements`, `pageSize`, `isFirst`, `isEmpty`, etc.
- Implements circuit breaker protection for repository stress scenarios.

**8. Response Structure**  
 - Upon successful retrieval of bus details and associated bookings, the API returns a **standardized HTTP 200 OK** response. The payload is structured to provide a **comprehensive snapshot** of both bus metadata and its bookings, wrapped in a JSON envelope that includes status, code, and contextual messages for better client-side handling.
 - Pagination metadata can be incorporated for large datasets, enabling **infinite scrolling or dashboard** rendering without losing context.  

**Response Includes (via ManagementBusDataDto):**  
- **BusInfo** — Captures full bus details including identification (bus number, name, type), operational attributes (AC type, seat type, capacity, available seats), registration and permit status, route (from/to locations), duration, fare, and metadata about who created or last updated the bus (`ManagementInfo`). This ensures auditability and clear operational tracking.
- **BookingInfo (List)** — Contains all active bookings associated with the bus, including booking ID, passenger details (ID, name, mobile), travel date, timestamps (booked, departure, arrival), number of seats booked, booking and payment statuses, payment method, ticket, transaction ID, and final cost. This allows management or administrative clients to **track occupancy, fare collection, and booking lifecycle** directly alongside bus details.
- **ManagementInfo** — Provides audit metadata for each bus, including the management user’s ID, username, mobile, role, and action timestamp, supporting compliance and operational traceability.  

This structure ensures that clients receive a **single, holistic view** of a bus and its bookings in a format optimized for dashboards, reporting, and operational decision-making. The DTO design also supports easy future extension for additional attributes, filtering, or hierarchical updates without breaking existing contracts.

#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Bus View Success]()
</details>  

#### ❗ Error Responses  
> Duplicate Bus Number  
<details> 
  <summary>View screenshot</summary>
   ![Bus View Error]()
</details>  
  

#### 📊 HTTP Status Code Table
| HTTP Code | Status                | Meaning               | When It Occurs                   |
| --------- | --------------------- | --------------------- | -------------------------------- |
| 401       | UNAUTHORIZED          | Auth Failed           | Role mismatch due to token invalid|
| 403       | FORBIDDEN             | Access denied         | Role mismatch                    |
| 400       | BAD_REQUEST           | Validation Error      | Bad DTO, invalid enum, invalid time, or location error|
| 200       | OK                    | Success               | Results a paginated output        |



#### ⚠️ Edge Cases & Developer Notes
**1. Keyword Parsing Pipeline Robustness**  
- Prefix priority eliminates collision risks (`id_123` never matches bus number `TN01CC1234`).
- Centralized Regex patterns prevents incorrect matches (bus number regex > time regex).
- Enum validation uses reflection-based valueOf() with fallback to contains() for partial AC/Non-AC detection.
- Location_ prefix performs existence check against MasterLocation before query execution (prevents ghost location queries).  

**2. Pagination & Sorting Security**  
- Negative/zero page/size values **blocked** at validation gate (no database roundtrip).
- Whitelisted `sortBy`prevents `ORDER BY` injection attempts on unindexed fields.
- Max size limit prevents memory-heavy queries.


**3. Date & Time Safety Measures**  
- **STRICT resolver rejects 30/02/2025, 2025-13-01, or 24:00 time inputs**.
- Timezone-agnostic `LocalTime/LocalDate` processing prevents DST schedule corruption.
- Cross-field date filtering (`createdAt` vs `departureAt.date`) uses temporal range expansion (±1 day tolerance).

**4. Performance & Scalability Considerations**  
- Composite indexes recommended: (`busType`, `stateOfRegistration`, `departureAt`) for common admin filters. Query timeout set to 30s prevents long-running regex scans on massive datasets.
- Response caching disabled due to real-time availableSeats mutation by booking engine.  

**5. DTO Security & Compliance**  
- Excludes operational fields & Sensitive fields for future safety precision.
- `availableSeats = capacity – bookedSeats` computed on demand.
- Audit logging captures adminId, full keyword, page/sort params, execution time, result count.

**6. Extensibility Architecture**   
- New filters added via FilterStrategy interface (prefixHandler, regexHandler) without core logic changes.
- Enum expansion handled automatically through reflection scanning.
- Pagination metadata supports client-side caching strategies (ETag, Last-Modified headers recommended).   
 
**7. Error Transparency & Debugging**   
- Distinguishes parsing failures (400) from business logic failures (404). Structured error payloads include validation path, expected format, and correction hints.
- Slow query logging (>500ms) with execution plan capture for index optimization.
</details>


### 🖋️ 13. Updating an Existing Bus Entry (Management Action)
<details> 
  <summary><strong>PUT</strong> <code>/management/buses/{id}</code></summary>

#### 🛠 Endpoint Summary   
**Method:** POST  
**URL:** /management/buses  
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  


#### 📝 Description
This endpoint allows **Management/Admin** to update an existing bus entry with new operational data such as bus number, type, registration state, permit status, timing, locations, and fare. It enforces **strict validation, robust enum parsing, safe location checks**, and **optimistic locking** to maintain data consistency.  

Key Functionalities:  
- **Role-Secured Update:** Only ADMIN users may modify bus records.
- **Strict ID & DTO Validation:** Ensures data integrity and avoids malformed updates.
- **Deep Enum & Location Parsing:** Prevents invalid bus types, states, or city mappings.
- **Departure Time Validation (STRICT):** Accepts only HH:mm:ss format.
- **Optimistic Locking Protection:** Prevents overwriting changes made by other admins.
- **Atomic Update via Mapper:** Ensures consistent transformation and recalculation of dependent fields such as duration, arrival time, AC/seat type, and availability.
- **Clear Response Messaging:** Provides granular **400/404/409 responses** for each failure type.

#### 📥 Request Parameter
| Parameter | Type | Description                                         | Required |
| --------- | ---- | --------------------------------------------------- | -------- |
| `id`      | Long | ID of the existing bus to update. Must be positive. | Yes      |


#### 📥 Request Body
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
> 💡 Departure time must strictly follow HH:mm:ss format.  
> 💡 Tip: Substitute these values with your preferred values. But remember, My system will block entries that do not match its rules.  
> 💡 Tip: For more info please refer the **BusDto class** under **dto package** in the application folder.  


#### ⚙️ Backend Processing Flow
**1. Authentication & Authorization Phase**  

Upon receiving the request, the first checkpoint is validating the UserPrincipal bound to the JWT token.
This step ensures:  
  - The system verifies whether the supplied JWT token is cryptographically valid, not tampered with, and not expired.
  - It performs a lookup against a token revocation registry to detect manually invalidated or blacklisted tokens (ex: logout, credential rotation, detected compromise).
  - Using `UserPrincipalValidationUtils.validateUserPrincipal(...)` is invoked to confirm:
       - The management user exists in the database.
       - The user record is active (not deactivated or suspended).
       - The user belongs to the ADMIN role.
  - If any failure/mismatch that leads to:
       - **401 Unauthorized** – if the token is invalid or missing.
       - **403 Forbidden** – if the user is authenticated but lacks the ADMIN role.
  - This ensures that only **authorized management authorities** can modify core operational data.  

**2. Input Validation Layer**   

My system then validates **Path Variable (id)**  
- The bus ID must be present and positive (id > 0).
- This prevents unnecessary DB round-trips for invalid identifiers.
- Invalid IDs immediately return a **400 Bad Request** with a user-friendly message.  
Then go for DTO Validation (@Valid BusDto)  
- Spring’s validation framework checks the entire payload (bus number, hours, minutes, fare, etc.).
- The system ensures structural correctness before touching any internal business logic.
- If violations exist, the controller aggregates all error messages using `BindingResultUtils.getListOfStr()`.
- Returns a structured **400 Bad Request** containing all field-level errors.
DTO validation ensures:
 - No missing required fields
 - Correct data shapes
 - Correct types and acceptable ranges
   
This protects the downstream business logic from malformed payloads.

**3. Database Lookup for Existing Bus**  

Once the request is validated, the system performs a lookup: `Optional<Bus> existingBus = busRepo.findById(id)`. Two outcomes are possible:  
- Bus Exists -> The Bus instance is retrieved for update.
- Bus Does Not Exist -> **404 NOT FOUND** with message: `"Bus ID X is not existed in database."`  
This avoids ambiguous updates and ensures admins don't update deleted or nonexistent entries.

**4. Enum Parsing and Business-Rule Validation**   
- Perform controlled parsing on each string-based field in the DTO using specific utility methods to interpret and validate enum values accurately.
- For Bus Type, use `ParsingEnumUtils.getParsedEnumType()` which validates the input text, attempts mapping to the **BusType enum** in a case-insensitive manner, and returns structured validation failures for invalid values.
- **Invalid busType** values are rejected immediately, responding with a **400 BAD REQUEST** status and precise error messages, preventing incorrect values from reaching the entity.
- State of Registration is validated against the State enum to ensure only properly formatted and standard state names are accepted and stored, avoiding loosely formatted or invalid entries.
- Permit Status values are restricted to a fixed set of accepted enum values such as PERMITTED and NOT PERMITTED, enforcing strict permit status validation according to business rules.  

**5. Location Validation (Route Consistency Enforcement)**  
- Enforce location validation by verifying that `fromLocation` and `toLocation` values correspond exactly to authoritative entries in the `MasterLocation table`, ensuring route consistency and referential integrity.
- Reject malformed spellings, nonexistent entries, or invalid formats immediately, preventing the creation of invalid routes that could disrupt downstream processes such as scheduling, seat aggregation, price computation, and tracking analytics.
- Respond with appropriate `HTTP status codes` for failures: **400 BAD_REQUEST** for format or validation errors, and **404 NOT_FOUND** when locations do not exist in the master data   

**6. Departure Time Parsing (STRICT Mode)**  
- Departure time parsing is performed using `LocalTime` and `DateTimeFormatter` with `ResolverStyle.STRICT` mode to ensure strict adherence to the "HH:mm:ss" format.
- This strictly enforces inclusion of hours, minutes, and seconds, preventing invalid times such as 25:99:00 or 12:60:30, and avoids ambiguous representations like 9:3 being interpreted incorrectly.
- Strict parsing guarantees accurate downstream computations related to arrival times and durations, maintaining data integrity in scheduling.
- On parsing failure, the system responds with **400 BAD_REQUEST** along with a precise corrective message, thereby preventing corrupted or invalid scheduling data.  

**7. Optimistic Locking Protection**  
- Implement optimistic locking protection for the update operation using a version field on the bus entity to prevent concurrent modification conflicts.
- When an admin fetches a bus entity, it includes the current version; if another admin modifies and saves it first, a subsequent save attempt by the original admin triggers an `ObjectOptimisticLockingFailureException` or `OptimisticLockException` due to **version mismatch**.
- The controller converts this exception into a **409 CONFLICT** response with a message indicating recent modification by another admin, ensuring no data overwrites, no stale updates, and dashboard consistency during **multi-admin operations**.

**8. Entity Update via Mapper (Business Cascade Update)**  

The BusMapper.updateExistingByDto() method executes a structured, field-by-field transformation from DTO to entity, implementing business cascade updates while maintaining a single source of truth for mappings.
 - Maps core fields including `busNumber`, `busName`, `fare`, and `capacity` directly from the DTO.
 - Re-derives `AC type` and seat type based on the resolved `BusType` enum value.
 - Applies updated `fromLocation` and `toLocation` values from the validated DTO.
 - Recomputes `arrivalAt` as **departureAt + duration**, converting provided hours and minutes into a proper Duration object.
 - Resets `availableSeats` to match the updated capacity.
 - Updates audit fields `updatedAt` and `updatedBy` with current **timestamp** and user context.

This separation ensures clean decoupling of data transformation from business logic, keeps controller and service layers lightweight, and provides consistent mapping reusable across operations.  


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Bus Update Success]()
</details>

#### ❗ Error Response  
> Invalid ID  
<details> 
  <summary>View screenshot</summary>
   ![Bus Update Error]()
</details>  

> Not Found (ID or MasterLocation Missing)  
<details> 
  <summary>View screenshot</summary>
   ![Bus Update Error]()
</details>  

> Duplicate Conflict 
<details> 
  <summary>View screenshot</summary>
   ![Bus Update Error]()
</details>  
  
> Unauthorized / Forbidden  
<details> 
  <summary>View screenshot</summary>
   ![Bus Update Error]()
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
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error |  Unexpected server-side error occured          |


#### ⚠️ Edge Cases & Developer Notes  
**1. Enum Parsing Edge Cases**  

String-based enums like busType, stateOfRegistration, and permitStatus handle diverse user input errors through structured validation. The system trims whitespace, normalizes casing, and rejects partial matches or aliases, providing detailed feedback on invalid values and valid alternatives to aid developers in debugging client issues.  
- Defends against partial spellings (e.g., "VOLV" for VOLVO), incorrect casing ("volvo"), abbreviations ("NAT" for NATIONAL), and leading/trailing whitespace.
- Returns structured error messages listing the invalid value received and complete accepted enum names/patterns.
- Prevents storage of invalid data that causes inconsistent filtering, API unpredictability, and downstream master data corruption.  

**2. Location Validation Sensitivity**  

Route definitions enforce referential integrity by cross-referencing fromLocation and toLocation against MasterLocation table entries exclusively. This rejects malformed, deprecated, or case-mismatched locations, safeguarding the scheduling engine from ghost routes that disrupt booking flows.  
 - Rejects locations absent from MasterLocation, mismatched city/state combinations, inconsistent casing, and deprecated/removed entries.
 - Ensures fromLocation ≠ toLocation and prevents unrelated location pairs that break pricing models and cancellation tracking.
 - Critical for maintaining valid route graphs used in seat aggregation, analytics, and real-time operations.


**3. Time Parsing Strictness**  

STRICT resolver mode in LocalTime.parse rejects incomplete, malformed, or logically invalid time formats to guarantee cascading computation accuracy. This prevents subtle timing errors that cascade through arrival calculations, journey durations, and schedule alignments.  
- Fails on missing seconds ("12:30"), incorrect separators ("12.30.00"), non-numeric segments ("12:AB:00"), and logical errors ("24:00:00", "22:75:10").
- Ensures precise alignment for seat availability timelines, fare algorithms, and departure reminders.
- Absolute correctness required since departureAt drives multiple downstream temporal computations.  

**4. Capacity Recalculation Risks**  

Updating `capacity` triggers `availableSeats = capacity` reset to reflect new bus configuration, eliminating stale values. Developers must note booking conflicts where existing reservations exceed new capacity, requiring defensive checks to avoid overbooking scenarios. 
- Resets `availableSeats` outright, preventing leftover stale counts from prior configurations.
- **Risky if bus has active bookings:** capacity reduction could create negative availability.
- **Consider pre-validation:** compare existing bookings against new capacity before reset.   

**5. Optimistic Locking & Audit Integrity**   

Version-based optimistic locking detects concurrent admin updates, while audit fields capture all changes for traceability. Combined with internal error handling, this maintains data consistency and compliance across multi-admin environments.  
- Race condition: Admin A (v1) vs Admin B (saves v2) → Admin A gets 409 CONFLICT with refresh instruction.
- Records updatedAt, updatedBy, and affected bus ID for fraud detection, accountability, and audits.
- Catches repository/mapper failures as 500 INTERNAL_SERVER_ERROR with masked details to prevent info leaks.
</details>  


### 🗑️ 14. Deleting an Existing Bus Entry (Management Action)
<details> 
  <summary><strong>DELETE</strong> <code>/management/buses/{id}</code></summary>

#### 🛠 Endpoint Summary   
**Method:** POST  
**URL:** /management/buses/{id}  
**Authorized Roles:** ADMIN  
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  


#### 📝 Description
This endpoint enables **Management/Admin** users to **safely delete existing bus entries** from the system, incorporating strict role-based authorization, ID validation, existence verification, and `@Version` **Optimistic locking** for concurrency protection. It utilizes **entity-based deletion (delete(bus))** rather than ID-only methods to ensure version-aware safety, preventing race conditions and double-deletes. Comprehensive audit logging tracks all deletion events for compliance and traceability.  

Key Functionalities:  
- **Role-Secured Delete:** Restricts operations to authenticated ADMIN users only.
- **Path Variable Validation:** Rejects null, negative, or zero IDs with immediate **400 BAD_REQUEST**.
- **Two-Phase Existence Check:** Verifies bus record exists before attempting deletion to avoid ambiguous failures.
- **Optimistic Locking Protection:** Uses `@Version` field to detect concurrent modifications, returning **409 CONFLICT** on mismatches.
- **Version-Aware Entity Deletion:** Employs delete(bus) method carrying current version for race-condition resistance.
- **Granular Response Messaging:** Delivers precise **400/404/409/500 status codes** with contextual error details.

#### 📥 Request Parameter
| Parameter | Type | Description                                | Required |
| --------- | ---- | ------------------------------------------ | -------- |
| `id`      | Long | ID of the bus to delete. Must be positive. | Yes      |


#### ⚙️ Backend Processing Flow  
**1. Security & Authorization Gate**  

Every delete request first passes through a multi-step security pipeline:  
- JWT validation (signature, expiry, revocation list)
- UserPrincipal validation (ensures user exists, active, and not suspended)
- Role enforcement (ADMIN required)    

Failures yield:
- 401 for invalid token
- 403 for insufficient privileges  

This ensures that only authenticated, authorized management users can perform destructive operations.  


**2. Input Sanity Checks**  

Although Spring handles path variable type conversion, the controller adds a safety check `if(id == null || id <= 0)` for a solid reasons:
- Prevents useless DB calls on impossible IDs.
- Avoids malformed requests entering business logic.
- Ensures predictable error semantics (400 Bad Request).

**3. Entity Existence Validation**  

The system performs `Optional<Bus> existingBus = busRepo.findById(id)`. If absent → **404 Not Found**. This prevents:
- Attempting to delete a ghost/non-existent bus.
- Incorrect audit trails.
- Front-end mismatch where “Delete Success” appears for a missing record.

Existence validation is mandatory before destructive operations.  

**4. Versioned Deletion with Optimistic Locking**  

Instead of using `deleteById()`, the system intentionally uses: `busRepo.delete(bus)`, because of some reasons like,
- Ensures the delete operation includes the `@Version` column.
- Detects concurrent modifications or earlier deletions.
- Guarantees strong consistency in multi-admin environments.
- Prevents stale delete attempts from succeeding silently.

If version mismatch → Spring throws: `ObjectOptimisticLockingFailureException` → controller returns **409 Conflict**.  

This maintains concurrency correctness and dashboard accuracy.  

**5. Transactional Delete & Audit Logging**  

Inside a transactional boundary:  
  - The bus is deleted atomically
  - On success, an audit log entry records _Management ID, username, bus ID, timestamp_.
This guarentees:
 - Non-repudiation
 - Forensic traceability
 - Clear operational records  

Any unexpected internal error triggers a rollback and returns **500 Internal Server Error** to the client.  


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Bus Delete Success]()
</details>

#### ❗ Error Response  
> Invalid ID  
<details> 
  <summary>View screenshot</summary>
   ![Bus Delete Error]()
</details>   

> Bus Not Found
<details> 
  <summary>View screenshot</summary>
   ![Bus Delete Error]()
</details> 

> Unauthorized / Forbidden
<details> 
  <summary>View screenshot</summary>
   ![Bus Delete Error]()
</details> 


#### 📊 HTTP Status Code Table
| HTTP Code | Status Name           | Meaning                 | When It Occurs                   |
| --------- | --------------------- | ----------------------- | -------------------------------- |
| 200       | OK                    | Successfully deleted    | Bus existed & deletion succeeded |
| 400       | BAD_REQUEST           | Invalid data            | Invalid ID                       |
| 401       | UNAUTHORIZED          | Authentication failed   | Missing/invalid JWT              |
| 403       | FORBIDDEN             | Access denied           | Non-admin                        |
| 404       | NOT_FOUND             | Bus not found           | ID not in DB                     |
| 409       | CONFLICT              | Concurrent modification | Another admin deleted same bus   |
| 500       | INTERNAL_SERVER_ERROR | Unexpected server issue | Repo/DB/transaction failures     |


#### ⚠️ Edge Cases & Developer Notes  
**1. Input Boundary Condition Defense**  
- Preemptive rejection of null, zero, or negative IDs (ID ≤ 0) averts Hibernate **InvalidIdentifierException**, malformed SQL execution paths, and superfluous connection pool exhaustion.
- This foundational check maintains API robustness under malformed client inputs without propagating errors downstream.  


**2. Concurrent Deletion Races**  
- Multiple admins targeting the same bus entity simultaneously represent the primary concurrency threat. The optimistic locking mechanism ensures linearizable semantics where the first delete(bus) succeeds with current version, while subsequent attempts detect version mismatch or row absence, triggering `ObjectOptimisticLockingFailureException` or `OptimisticLockException` converted to **409 CONFLICT** with explicit refresh guidance.​  

**3. Critical Distinction**  

Deciding which one to use while under process:   
- **Option 1:** `deleteById(id)` executes **blind primary key deletion** bypassing `@Version` validation, entity state verification, and optimistic locking entirely—enabling stale/phantom deletions even after concurrent modifications.
- **Option 2:** `delete(bus)` mandates **full entity load including version field**, enforcing concurrency controls as a deliberate architectural safeguard rather than stylistic preference.  


**4. Audit Log Sequential Integrity**   
- Audit entries execute **post-successful transactional boundary** only, guaranteeing chronological consistency and eliminating phantom "success" logs from rollback scenarios.
- This preserves non-repudiation for **compliance audits**, forensic reconstruction, and managerial accountability even during rapid delete/restore sequences.  

**5. Exception Masking & Security Hardening**  
- **Repository failures, transactional violations**, or **mapper exceptions** receive comprehensive server-side logging while clients receive generic **500 INTERNAL_SERVER_ERROR** with sanitized messaging. This prevents schema enumeration, infrastructure fingerprinting, and attack surface expansion through error-based reconnaissance.
</details>    


### 🚌 15. Public Bus Search API (Public + Registered + Management Access)
<details> 
  <summary><strong>GET</strong> <code>/public/buses</code></summary>

#### 🛠 Endpoint Summary   
**Method:** GET  
**URL:** /public/buses  
**Authentication:** Not Required  
**Authorized Roles:** PUBLIC    

#### 📝 Description  
This API endpoint powers the **core discovery engine** of the bus reservation system, enabling seamless bus availability queries across **all user categories**—guests passengers, registered passengers, and management authorities. Users specify **from & to locations**, **travel dates**, and optional filters like,  
   - AC type(AC/NON-AC)
   - Seat type(SEATER/SLEEPER) preferences
   - Departure time windows (e.g., 06:00–12:00)
   - Sorting by fare, departure time, or bus ID.
    
As a **business-critical interface** feeding search pipelines, booking workflows, and fare comparisons, it enforces strict parameter validation, deterministic filter logic, and uniform response contracts for functional accuracy and operational predictability.  

Key highlights:  
- **Universal Consistency:** Identical response structure, error semantics, and query behavior across guest/registered/management users.
- **Deterministic Filtering:** Every filter combination maps to traceable repository calls, eliminating unpredictable results.
- **Zero-Ambiguity Rules:** Fully defined business logic ensures schedule accuracy, fare integrity, and reliable booking handoffs.


#### 📥 Query Parameters
| Parameter    | Type   | Default | Description                                                                                                                                                    | Required |
| ------------ | ------ | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| `from`       | String | —       | Source location name. Must exactly match a valid entry in **MasterLocation**. Case differences & spacing are normalized, but semantic mismatches are rejected. | Yes      |
| `to`         | String | —       | Destination location name. Same validation rules as `from`. Cannot equal `from`.                                                                               | Yes      |
| `travelDate` | String | —       | Travel date in strict `dd-MM-yyyy`, `dd/MM/yyyy`, or `yyyy-MM-dd`. Automatically detects format and rejects invalid past dates.                                | Yes      |
| `sortBy`     | String | `id`    | Sorting field. Restricted to: `id`, `departureAt`, `fare`. Prevents unindexed queries and ORDER-BY attacks.                                                    | No       |
| `sortDir`    | String | `ASC`   | Sort direction. Case-insensitive. Accepted values: `ASC` / `DESC`.                                                                                             | No       |
| `acType`     | String | —       | AC filter. Must strictly match regex-validated AC enumeration (AC / NON-AC). Partial matches or ambiguous forms are rejected.                                  | No       |
| `seatType`   | String | —       | Seat configuration filter. Valid values: `SEATER` / `SLEEPER`.                                                                                                 | No       |
| `timeRange`  | String | —       | Departure time window in `HH-HH` 24-hour format. Interprets integer hours only. Strict boundaries prevent semantic drift.                                      | No       |


#### ⚙️ Backend Processing Flow  
**1. Multi-Stage Request Parameter Normalization (The First Guard Rail)**  

The controller enforces parameter correctness before any domain logic runs. This step is not limited to checking empty strings; it performs semantic validation to ensure the request is meaningful and processable.  

**Input Coherence Validation**    
  
Using `RequestParamValidationUtils.listOfErrors()` utility class which verifies:  
   
  - All required parameters (from, to, travelDate) are present.  
  - Values are non-empty, non-null, and not just whitespace.  
  - Inputs follow acceptable structural patterns so that later parsing is safe.    
      
   This guards the service layer from malformed data and ensures the system never attempts expensive database queries on invalid input.  

**Fail-Fast Strategy**
  - A unified validation error is returned immediately.
  - No parsing, business logic, or database work is performed.

This design conserves resources, reduces CPU load, protects downstream logic, and promotes predictable API behavior.   
 

**2. Travel Date Interpretation & Temporal Integrity Rules**   

Travel date is a mission-critical parameter because time-dependent availability is computed from it. Date validation follows a multi-layered strategy.

**Format Auto-Detection**
 
The system detects date format automatically:
   - Presence of `/` or `-` → interprets input as human-readable formats (**dd-MM-yyyy or dd/MM/yyyy**).
   - Absence of symbols → treats it as HTML-standard **yyyy-MM-dd**.
      
This gives clients flexibility while maintaining strict interpretation consistency.

**Strict ResolverStyle Enforcement**
 
The parser uses `ResolverStyle.STRICT` to reject:  
   - Invalid dates such as `31-02-2025`.
   - Silent rollovers where invalid values auto-correct.
   - Leap-year misinterpretations.
      
Strict parsing guarantees temporal correctness, a fundamental requirement for reliable scheduling.  

**Past-Date Rejection**   

The system rejects any travelDate earlier than the current day. This is crucial because:  
  - Routes cannot be searched for past dates.
  - Availability logic depends on future schedule windows.
  - Prevents misleading user expectations.  

**3. Sorting Field Whitelisting & Query Safety Enforcement**  

Sorting is strictly controlled to protect the database from high-cost operations and unsafe ordering conditions. Only three fields are permitted for client-side sorting:  
  - `id`
  - `departureAt`
  - `fare`  

These fields are explicitly whitelisted because they are indexed for performance, directly relevant to business operations, and safe from injection or malformed-input risks. Any attempt to sort by a non-authorized field (such as `busName`, `busType`, etc.) results in a **400 BAD REQUEST**, ensuring the database never executes unstable or unbounded sort operations.  


**4. Master Location Validation (Route Semantics Validation)**  

Route-level validation ensures that all search parameters map to canonical, authoritative locations stored in the `MasterLocation` table. This guarantees consistent routing, eliminates invalid **“ghost routes,”** and maintains referential integrity across the system.  

**Normalization**  

Using `ValidateLocationUtils.validateLocation(...)` performs robust normalization by:  
  - Applying case-insensitive matching
  - Trimming unnecessary whitespace
  - Mapping input values to authoritative database entities  

This ensures uniformity in user-provided route data regardless of input variations.  

**Layered Error Behavior**  

The validation system produces two distinct error outcomes:  
 - **Malformed format → 400 BAD REQUEST**, Returned when the input structure itself is invalid.
 - **Valid format but location not found → 404 NOT FOUND**, Returned when the input is structurally correct but does not exist within the master dataset.  

This dual-layer approach offers clearer guidance for client applications and prevents unpredictable fallback behaviors.  

**Route Integrity Rule**  
 - The system enforces the rule that routes `from` must not equal `to`. This ensures coherent route queries and eliminates degenerate travel paths that would otherwise produce nonsensical search results.   


**5. Deterministic Multi-Filter Query Handling**  

Filter processing is designed around a **Deterministic Filter Combination Algorithm**, avoiding generic dynamic query builders in favor of explicit, predefined pathways. This guarantees predictable system behavior, eliminates overlapping conditions, and prevents filter bleed-over.  

**Why Deterministic Branching?**  
- Predictable and testable execution paths.
- Zero ambiguity between filter combinations.
- Strong maintainability through explicit branching.
- Optimal performance using precompiled JPQL queries.
- Ensures exactly one repository query is ever executed per request.  

**Filter Precedence Hierarchy**  

Filters are evaluated in the following strict order:
 - AC + SeatType + TimeRange
 - AC + SeatType
 - AC + TimeRange
 - SeatType + TimeRange
 - AC only
 - SeatType only
 - TimeRange only
 - No filters → location-only search  

Within every branch:  
 - Regex patterns ensure filter structure correctness.
 - Enum values are parsed using safe, normalized mapper utilities.
 - Time-range boundaries convert into validated, integer hour segments.
 - The exact repository method mapped to that filter combination is executed.  

This provides a deterministic, conflict-free search pipeline with maximal control and reliability.  

**6. Structured Repository Query Execution**  

Every valid filter combination resolves to a **dedicated repository function**, ensuring:  
- Zero ambiguity in query selection
- High performance through index-friendly queries
- Predictable JPQL structure
- Efficient database planning and execution  

For example query like `filterBusByLocationWithBothTypeAndTime()`, `filterBusByAcTypeAndTimeRange()`, etc. The system deliberately avoids “one giant dynamic query” approaches because predictable query planning is a core performance requirement for large-scale traffic.   


**7. DTO Transformation & Travel-Date Awareness**  
- `BusMapper.busToBusUserDto()` transforms raw entity objects into structured, user-facing DTOs. Responsibilities include:
    - Formatting departure and arrival timestamps
    - Exposing enum values as canonical client-safe strings
    - Computing seat availability based on requested travel date
    - Producing standardized, predictable response objects

- The DTO intentionally hides internal attributes, including:
    - Internal database IDs
    - Optimistic-locking version fields
    - Audit attributes (createdBy, updatedBy, timestamps)
    - Management-specific metadata  
- This prevents information leakage and ensures the response remains strictly user-facing.   

**8. Response Semantics & Behavioral Guarantees**  
- Successful operations return a consistent response structure that includes:
   - Status code **200 OK**
   - A **message** explicitly describing the filters applied
   - A curated list of **user-ready Bus DTOs**
 
- Routes exist but no buses match the applied filters. Filters eliminate all available candidates. This is deliberately separated from validation errors:
  -  Status code **404 NOT FOUND**
  -  A **message** explicitly describing the filters applied

- If any input violates structural or semantic rules, such as: Malformed dates, Invalid enums, Incorrect filter syntax, Sorting field violations, Time-range format errors, then:
  - Status code **400 BAD REQUEST**
  - A **message** explicitly describing the filters applied
    
- Used `ApiResponse` with this format is stable, predictable, and easy for clients to consume.  


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Bus Public View Success]()
</details>

#### ❗ Error Response 
> Invaid input for page, size
<details> 
  <summary>View screenshot</summary>
   ![Bus Public View Error]()
</details> 

> No bus data found
<details> 
  <summary>View screenshot</summary>
   ![Bus Public View Error]()
</details>   



#### 📊 HTTP Status Code Table
| HTTP Code | Status Name           | Meaning                 | When It Occurs                   |
| --------- | --------------------- | ----------------------- | -------------------------------- |
| 200       | OK                    | Successfully delivered  | Bus existed & delivered succeeded|
| 400       | BAD_REQUEST           | Invalid data            | Invalid pagination input / enum values|
| 404       | NOT_FOUND             | Bus not found           | No bus data found                |


#### ⚠️ Edge Cases & Developer Notes  
**1. Temporal Edge Cases**  

The system rejects logically impossible or ambiguous time ranges (e.g., `22–05`, unless future circular-range support is added). It enforces:
  - `travelDate >= systemDate` (To avoid past date input).
  - Normalized 24-hour boundaries (For stable & flexible use).
  - Strict hour-based formatting — partial formats like `"5-8"` must be `"05-08"`.  

**2. Filter Collision & Logical Integrity**  

The deterministic branching ensures no two branches ever overlap. For example:
 - (AC + TimeRange)
 - (SeatType + TimeRange)

The above are mutually exclusive execution paths. This eliminates unpredictable combinations, double filtering, and precedence ambiguity.  

**3. Regex-Driven Safety Controls**  

**Centralized regular expression pattern** in utils package for **Strict regex validation** safeguards all string-based filters, preventing:
- SQL injection attempts
- Numeric/date reinterpretation attacks.
- Unexpected character patterns.
- Cross-contamination between AC, seat type, and bus-type filters.  


**4. Repository-Level Performance & Master Data Coupling**   
- Time-range filtering uses boundary-only checks for index-friendly performance
- AC and SeatType filters rely on normalized canonical strings, ensuring uniform DB lookup patterns
- The search engine depends on `MasterLocation` as the authoritative routing source, ensuring:
    - Guaranteed referential consistency
    - Elimination of typo-driven mismatches
    - Reliable downstream booking flows  


**5. Extension-Ready Architecture**  

The deterministic filter architecture allows effortless future expansion — e.g.:
- Bus operator filters
- Amenity filters (WiFi, Charging, Water Bottle, etc.)
- Dynamic fare filtering
- Real-time seat-availability windows
- Multi-date search
 
New filters can be added without invasive changes to the core logic.


#### ➡️ What To Do Next (MUST READ)   

With `API #15 – Public Bus Search API` completed, the next major milestone is the implementation of the **Booking Module**, which governs how a passenger transitions from viewing bus availability to completing a confirmed reservation. This module is **critical** because **a successful booking can only be achieved by following a strict, sequential workflow** designed to ensure data integrity, pricing accuracy, and seat-locking consistency.  


**Mandatory Booking Workflow**  

The booking flow consists of four controlled stages:  

**1. Start Booking**  
 - The initial step where a passenger initiates the booking process for a specific bus and travel date.
 - This stage typically includes basic passenger details, seat count, preliminary fare calculation, and temporary seat-locking mechanisms.   

**2. Edit Booking (If Required)**  
- Allows the user to make modifications before continue booking.
- This may include updating passenger details, changing existing seat count, adjusting contact information, or revisiting add-on options.  

**3. Continue Booking**  
- The checkpoint stage that validates the revised information, rechecks seat availability, and prepares the booking for confirmation.
- The system ensures the booking remains consistent with current bus status and fare structures.  

**4. Confirm Booking**  
- The final step where the booking is validated, committed to the system, and officially reserved.
- Payment processing or final locking of seats usually occurs here.
- System starts **generated travel ticket & transaction ID** for successful confirmation.  

**Sequential Enforcement**  

This four-step workflow **must be followed in order**.
Skipping, bypassing, or directly jumping to a later stage will always result in a failed or invalid booking operation. This strict sequence guarantees:  
- Data consistency
- Accurate seat availability
- Prevention of ghost or partial bookings
- A stable and predictable booking lifecycle
</details>  


### 📘 Booking Workflow Overview (High-Level Booking Lifecycle)  
<details> 
  <summary><strong>Start → Edit → Continue → Confirm</strong></summary>
  <br>

The Booking Module in **BookMyRide** is a controlled, state-driven reservation system designed to guarantee accurate seat management, safe concurrency handling, and a consistent user flow. Every booking must progress through a fixed, forward-only sequence:   

**Start → Edit → Continue → Confirm**   

Additional operations like cancellation remain possible, but only when the booking has not yet reached the confirmed state. This workflow ensures predictable outcomes for both users and the system, preventing invalid transitions, expired operations, or seat conflicts during high-traffic scenarios.   


### 📌 Core Design Principles  

**Deterministic Progression**  

The booking process always unfolds in the same order. No step can be skipped, repeated incorrectly, or executed out of turn. This prevents incomplete or inconsistent reservations.  

**State-Based Permissions**  

Each action is allowed only when the booking’s status and payment state match the required conditions. For example:  
 - Editing is allowed only when the booking is **PENDING & UNPAID**.
 - Confirmation is allowed only when the booking is **PROCESSING & PENDING**.
 - Cancellation is allowed only until confirmation.  

**Immediate Seat Locking**  

Seats are deducted the moment a booking is started. This ensures accurate availability even during simultaneous booking attempts. Optimistic locking is used to detect and gracefully handle conflicts when multiple users select the last remaining seats.  

**Automatic Expiry Enforcement**  

Every booking includes an expiration timestamp (`bookingExpiresAt`). Once expired, the booking is invalidated automatically, seat locks are released, and further operations are blocked. This prevents abandoned bookings from holding seats indefinitely.  


### Start Booking  

**Endpoint:** **POST** `/public/bookings`   

This is the entry point to the booking lifecycle. When a user initiates a booking:  
- Input validation is performed (mobile, seat count, date format, etc.)
- The bus is verified for existence
- Seat availability is checked and locked
- User information is created or updated
- A new booking is created in a `PENDING` state with `UNPAID` status
- Temporary hold time (`bookingExpiresAt`) is set  

If another user books the same seats milliseconds earlier, optimistic locking ensures the conflict is detected and the user receives a graceful retry message.  

**Output:** A **Booking Preview**, displaying passenger details, trip details, and seat info.  


### Edit Booking (Optional)  

**Endpoint:** **PUT** `/public/bookings/{id}/edit`

Editing allows the user to update passenger details, travel date, or seat count — but **only before moving to payment**. This step is allowed only when the booking is: `PENDING`, and `UNPAID`, and Not expired. When the seat count changes, the system calculates the seat delta and checks whether the bus has enough remaining seats before applying the update.  

Editing is blocked if the booking is already: `PROCESSING`, `CANCELLED`, `EXPIRED` & `CONFIRMED`.

**Output:** An updated **Booking Preview**.  

### Continue Booking  

**Endpoint:** **PATCH** `/public/bookings/{id}/continue`  

This step transitions the booking into the payment phase. When continued:
- The system checks whether the booking is still valid (not expired).
- Status moves from PENDING → PROCESSING.
- Payment status moves from UNPAID → PENDING.
- Any existing ticket or transaction data is cleared.
- The booking is prepared for payment and confirmation.  

If the booking has already expired, the system marks it as EXPIRED, releases the seats, and prompts the user to start a fresh booking.

**Output:** A **Booking Summary** ready for payment.  

### Confirm Booking  

**Endpoint:** **PATCH** `/public/bookings/{id}/confirm`  

This finalizes the booking after payment details are provided. Confirmation is allowed only when the booking is:  
- `PROCESSING`
- `PENDING` (payment)
- Not expired  

Once validated:  
- Payment method is recorded
- Payment status becomes PAID
- Booking status becomes `CONFIRMED`
- Unique Ticket ID and Transaction ID are generated
- Expiry timestamp is cleared (booking no longer expires)   

A confirmed booking becomes immutable — no edits, no cancellation.  

**Output:** The **Final Booking** Information including ticket data and payment details.

### Cancel Booking (Additional Operation)  

**Endpoint:** **PATCH** `/public/bookings/{id}/cancel`  

Cancellation is supported as a user-friendly operation but is intentionally restricted to maintain system integrity. A booking can be cancelled **at any point before it is confirmed**. A booking is eligible for cancellation only when:  
- Status is `PENDING` or `PROCESSING`
- Payment status is `UNPAID` or `PENDING`
- Booking has not expired  

Upon cancellation:  
- Booking is marked as `CANCELLED`
- Payment method is reset (`NONE`)
- Ticket and transaction data are cleared
- Seats reserved earlier are immediately released back to the bus
- A cancellation timestamp is recorded  

Attempts to cancel a booking that is already **CONFIRMED, EXPIRED**, or **CANCELLED** are rejected.  

**Output:** A success message confirming the cancellation.  

### Booking State Lifecycle  

The system strictly controls transitions between states to avoid invalid operations. A simplified lifecycle representation:  

START BOOKING →  PENDING  → EDIT (optional)   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; → CONTINUE  →  PROCESSING  →  CONFIRM  →  CONFIRMED  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CANCEL   

Expired or cancelled bookings exit the flow and cannot re-enter.  

### Final Outcome  

The Booking Workflow in BookMyRide offers a predictable, secure, and user-friendly process that balances ease-of-use with strict backend consistency. The architecture is engineered to:  
- Prevent seat conflicts
- Avoid stale or expired bookings
- Maintain strict ordering of steps
- Provide controlled editing
- Allow cancellations safely
- Ensure reliable confirmation
- Protect all operations through concurrency & state validation mechanisms

This delivers a world-class ticketing flow that mirrors real-world transport booking systems and scales reliably during peak traffic. 
</details>  


### ▶ 16. Start a New Booking (Seat Locking & Booking Initialization)
<details> 
  <summary><strong>POST</strong> <code>/public/bookings</code></summary>

#### 🛠 Endpoint Summary   
**Method:** POST  
**URL:** /public/bookings  
**Authentication:** Not Required  
**Authorized Roles:** PUBLIC(Both Registered & Non-Registered Users)       

#### 📝 Description  
This API is the **Step 1** of the Booking Workflow initializes a brand-new booking for a selected bus, creates or updates the passenger profile, ensures seat availability, and securely locks the requested seats. A booking created through this endpoint enters the system in a PENDING state and becomes the foundation for subsequent operations:  
- Edit Booking (Optional)
- Continue Booking
- Confirm Booking  

Key highlights:  
- Strict DTO validation for all booking inputs
- Robust date format parsing (dd-MM-yyyy or dd/MM/yyyy) with strict resolver
- Automatic user creation or profile update (based on mobile number)
- Bus existence validation using canonical busNumber
- Seat availability check + immediate seat locking
- Centralized discount calculation
- Travel/arrival time computation using schedule metadata
- Booking expiration timestamp setup (10-minute hold window)
- Optimistic locking to prevent seat conflicts during high traffic
- Fully atomic booking creation using transactional boundaries
- Returns a full Booking Preview object, ready for Review/Continue  

The entire operation is engineered for real-time, high-concurrency environments where multiple users may attempt to book the last few seats simultaneously.

#### 📥 Request Body  
{  
&nbsp;&nbsp;&nbsp; "name": "Your Name",  
&nbsp;&nbsp;&nbsp; "mobile": "Your Mobile Number",   
&nbsp;&nbsp;&nbsp; "email": "Your email ID",  
&nbsp;&nbsp;&nbsp; "busNumber": "TN01CC1234",  
&nbsp;&nbsp;&nbsp; "seatsBooked": 2,  
&nbsp;&nbsp;&nbsp; "travelAt": "17-12-2024"  
}  
> 💡 Notes:
- You can replace the placeholders with your details or any dummy details for testing.
- The value given for `busNumber` is also a placeholder/dummy. Can use any bus number that you want to book. But that must present/active in DB.   
- travelAt can be dd-MM-yyyy or dd/MM/yyyy — strict mode parsing ensures no invalid dates slip through.  


#### ⚙️ How the Backend Processes  

**1. DTO Validation & Syntax Enforcement**   

The request payload undergoes strict validation via @Valid + BindingResult. The system checks:
- Mandatory fields (name, mobile, busNumber, travelAt, seatsBooked)
- Mobile number format
- Email format
- seatsBooked ≥ 1
- Clean string values  

If any validation fails → **400 BAD_REQUEST** with a detailed list of validation errors.  

**2. Travel Date Parsing (Strict Resolver Mode)**  

The system identifies format by checking the delimiter `dd-MM-yyyy` and `dd/MM/yyyy`. It then parses the date using: `DateTimeFormatter + ResolverStyle.STRICT`. This ensures:  
- No invalid dates (31-02-2024)
- No partial dates
- No rollover quirks
- No ambiguous inputs  

If parsing fails → **400 BAD_REQUEST**.  

**3. Mobile Number Access-Control Check**   

Before proceeding, the system checks if the provided mobile number belongs to:  
- A management/admin user
- Any restricted user category  

If yes → **403 FORBIDDEN** – **Access Denied**, preventing unauthorized users (like Admins) from booking seats meant for public users.  

**4. Passenger Profile Handling**  

The system checks if the mobile number already exists in AppUser:
- If existing: user profile is updated with new details (name/email).
- If not existing: a new passenger profile is created.   

Strict business validation ensures:  
- No duplicate emails
- No conflicting user roles
- No malformed user attributes  

Failure cases return:
- **400 BAD_REQUEST** (invalid profile data)
- **409 CONFLICT** (duplicate email or conflicting user state)  

On success, a clean **AppUser** object is obtained for downstream booking logic.   

**5. Bus Lookup & Route Validation**   

The bus is fetched using busNumber:
- If bus not found → **404 NOT_FOUND**.
- If bus exists, its schedule, fare, locations, and timing metadata are loaded.  

This ensures every booking is tied to a valid, operational bus entity.  

**6. Seat Availability Verification & Seat Locking**  
- This step is the heart of Start Booking. The system checks: If booked seats is lesser than `availableSeats` in Bus data.  
- If insufficient: → **409 CONFLICT** – `INSUFFICIENT_SEATS`    

If valid:  
- Remaining seats are computed.
- Bus.availableSeats is updated immediately.
- Seat lock is enforced inside a transactional + optimistic locking boundary.  

This prevents:
- Overbooking
- Parallel request conflicts
- Race conditions during peak traffic  

Any optimistic lock exception is turned into a user-friendly: **409 CONFLICT** – Seats just got booked by others.

**7. Booking Entity Construction (BookingMapper.newBooking)**    

The system computes and assigns:  
- fromLocation / toLocation (copied from the bus)
- travelAt
- departureAt = travelAt + bus.departureAt
- arrivalAt = departureAt + duration
- cost breakdown
- discount (5% for regular USER role)
- UNPAID / PENDING statuses
- bookingExpiresAt = now + 10 minutes  

This ensures **consistent, centrally-derived booking properties** with ZERO reliance on user-supplied cost or timing.  

**8. Transactional Save & Audit Logging**  

Inside a single atomic transaction:  
- The booking is saved
- Bus seat count is simultaneously adjusted
- Audit logs record booking ID, user ID, and metadata  

If any unexpected failure occurs → **500 INTERNAL_SERVER_ERROR**


**9. Successful Output – Booking Preview DTO**  
- When a new booking is successfully created, the system compiles all relevant details into a structured `BookingPreviewDto` and returns it with **201 CREATED**. This DTO acts as the initial snapshot of the freshly created booking, containing all information the user needs before moving forward to the Continue step.
- It encapsulates booking metadata, seat and fare calculations, bus schedule, and passenger details—presented in a clean, grouped format for easy client-side rendering.  
  
**Response Includes (via `BookingPreviewDto`):**  
- **BookingInfo** — Contains the system-generated booking ID, creation timestamp, intended travel date, applied discount details, cost breakdown (total and final cost), and the computed bookingExpiresAt timestamp that defines how long the user has to continue the booking.
- **BusInfo** — Provides all route and schedule details including bus identification, type, origin/destination, standard fare, and computed departure/arrival timestamps based on the selected travel date.
- **PassengerInfo** — Includes the passenger’s submitted profile and the number of seats initially reserved in this booking.  

This preview serves as the **starting point of the booking workflow**, giving the user complete clarity on the reservation they just created and indicating that the next step is to proceed with **Continue Booking** to lock the booking into the processing stage.  


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Booking Preview Success]()
</details>

#### ❗ Error Response 
> Invaid input
<details> 
  <summary>View screenshot</summary>
   ![Booking Preview Error]()
</details> 

> No bus data found
<details> 
  <summary>View screenshot</summary>
   ![Booking Preview Error]()
</details>     

#### 📊 HTTP Status Code Table   
| HTTP Code | Status                | Meaning                                 | When It Occurs                                     |
| --------- | --------------------- | --------------------------------------- | -------------------------------------------------- |
| 201       | CREATED               | Booking Created successful              | Booking + seat lock succeeded                      |
| 400       | BAD_REQUEST           | Validation Error                        | DTO invalid, malformed date, seats < 1             |
| 403       | FORBIDDEN             | Role mismatch                           | Mobile belongs to restricted/management role       |
| 404       | NOT_FOUND             | Bus not found                           | ID not present in DB                               |
| 409       | CONFLICT              | Insufficient Seats                      | availableSeats < seatsBooked or Capacity limitation|
| 500       | INTERNAL_SERVER_ERROR | System failure                          | Unexpected exception                               |


#### ⚠️ Edge Cases & Developer Notes  

**1. High-Traffic Seat Collision & Optimistic Lock Behavior**  
- When multiple passengers attempt to book the **last few seats**, all requests may initially pass validation but only one will commit successfully.
- The losing transactions will trigger database-level optimistic locking, ensuring **zero overbooking** regardless of concurrency volume.
- This scenario most commonly occurs during weekends, holidays, or flash-discount events where seat demand spikes sharply.
- The API converts such collisions into a predictable **409 CONFLICT: Seats booked by others** to maintain a clean, user-friendly experience.  
  
**2. Expired Passenger Profiles & Auto-Update Logic**  
- If a returning user enters updated name/email during booking, the system **automatically refreshes** their profile.
- This avoids the need for separate profile management steps and ensures real-time identity accuracy.
- Email conflicts (e.g., reused in another account) are detected immediately, preventing downstream inconsistencies.
- This mechanism is essential for long-term users whose details change (new email, corrected spelling, etc.).  

**3. Time-Based Booking Expiry & Seat Replenishment**  
- Every new booking receives a strict 10-minute expiry window, during which seats remain locked but not confirmed.
- If the user abandons or delays payment, these seats automatically return to availability, protecting inventory accuracy.
- This prevents “ghost locks” — a common issue in ticketing systems where incomplete transactions block seats indefinitely.
- The expiry mechanism is critical during peak loads, ensuring a continuous flow of available inventory.  


**4. Cross-Version Mobile Number Restrictions**  
- If a mobile number belongs to a **Management/Admin account**, booking attempts are blocked for security and role isolation.
- This prevents privileged accounts from engaging in regular passenger transactions, which could create audit distortions.
- It also ensures clean separation between operational roles and consumer-facing flows.
- This rule evolves from real-world enterprise systems where internal staff are restricted from booking through public channels.
</details>   



### 🖋️ 17. Edit an Existing Booking (Passenger Update, Seat Modification & Recalculation)  
<details> 
  <summary><strong>PUT</strong> <code>/public/bookings/{id}/edit</code></summary>

#### 🛠 Endpoint Summary   
**Method:** PUT  
**URL:** /public/bookings/{id}/edit  
**Authentication:** Not Required  
**Authorized Roles:** PUBLIC(Both Registered & Non-Registered Users) 

#### 📝 Description  

This API handles the optional **Edit** step in the **booking workflow (Start → Edit → Continue → Confirm)**, allowing users to adjust passenger details, travel date, and seat count while strictly enforcing validation, status checks `Booking Status = PENDING & Payment Status = UNPAID`, expiration rules, and seat availability; it ensures safe concurrent updates through optimistic locking and transactions, recalculates fare and discounts, auto-refreshes passenger data, and returns an updated Booking Preview reflecting all modifications.

Key highlights:  
- Only bookings in **PENDING + UNPAID** status can be edited.
- Strict DTO validation for all editable fields.
- Robust date format parsing (`dd-MM-yyyy` or `dd/MM/yyyy`).
- Automatic user profile update if passenger details have changed.
- Recalculates seat availability and ensures no overbooking.
- Provides a **Booking Preview** reflecting updated passenger details, seat count, and trip summary.
- Handles expired bookings gracefully, returning a clear timeout message.
- Atomic transactional updates to prevent partial edits.  

#### 📥 Request Body
{  
&nbsp;&nbsp;&nbsp; "name": "Updated Name",  
&nbsp;&nbsp;&nbsp; "mobile": "Updated Mobile Number",   
&nbsp;&nbsp;&nbsp; "email": "Updated email ID",    
&nbsp;&nbsp;&nbsp; "seatsBooked": 4,  
&nbsp;&nbsp;&nbsp; "travelAt": "20-12-2024"  
}  
> 💡 Notes:
- Ypu can replace the placeholders with your details or any dummy details for testing.
- Editing can only done at/for same bus.   
- travelAt can be dd-MM-yyyy or dd/MM/yyyy — strict mode parsing ensures no invalid dates slip through. 


#### ⚙️ How the Backend Processes  

**1. DTO Validation & Field Checks**  

Using `@Valid` + `BindingResult`, the system first enforces:
- Mandatory fields (name, email, seatsBooked, travelAt)
- Mobile format rules
- Email format correctness
- Seat count ≥ 1
- Clean input strings
 
Invalid inputs → **400 BAD_REQUEST** containing the full list of validation issues.   

**2. Travel Date Parsing (Strict Dual-Format Logic)**   

The system identifies the correct format: **dd-MM-yyyy or dd/MM/yyyy or yyyy-MM-dd** parsed via: `ResolverStyle.STRICT` using a `DateParser.validateAndParseDate()` utility class which ensures:
  - No invalid dates like 31-02-2024
  - No rollover quirks
  - Full 4-digit year
  - No ambiguous or auto-adjusted inputs  

If parsing fails → **400 BAD_REQUEST** with relevant parsing message.  

**3.Booking Lookup & State Validation**   

The system fetches the existing booking by ID, responding with **404 NOT_FOUND** if absent. It enforces edit eligibility by requiring  
`bookingStatus = PENDING &&
paymentStatus = UNPAID &&
bookingExpiresAt > now()`   

This editing feature is **rejected** for:

| Status     | Reason                                       |
| ---------- | -------------------------------------------- |
| PROCESSING | Already moved to payment; no changes allowed |
| CONFIRMED  | Booking is final & immutable                 |
| CANCELLED  | Closed booking cannot be edited              |
| EXPIRED    | Auto-expired; seats already released         |  

Each invalid state returns → **403 FORBIDDEN** with contextual messages.


**4. Seat Modification Logic (Delta-Based Update)**   

The real power of this API lies in its **delta computation**: `modifiedCount = newSeatCount – originalSeatCount`
  - If the user **reduces** seats → modifiedCount is negative → bus seats increase automatically.
  - If the user **adds** seats → modifiedCount is positive → system verifies availability.  

This proccess would further proceeds only if: `bus.availableSeats >= modifiedCount`. Otherwise check If the seat addition exceeds availability → **409 CONFLICT (INSUFFICIENT_SEATS)**. This ensures:
  - No overbooking
  - No race conditions
  - No invalid seat deductions


**5. Passenger Profile Update (Real-Time User Sync)**  

The passenger record (AppUser) associated with the booking is updated:
- Updated name
- Updated email
- Updated gender  

The AppUser update step itself includes strict validation:   
- Duplicate email detection
- Conflict checks
- Data consistency rules  

If any of the field failed due to validation or causes then return: **400 BAD_REQUEST** (invalid data) Or **409 CONFLICT** (duplicate email).  


**6. Optimistic Lock Protection (Expiry Detection)**   

If another process (background expiry job or parallel request) modifies the booking concurrently: `OptimisticLockException` or `ObjectOptimisticLockingFailureException` is thrown by the **JPA entity version field**. When this happens, the system returns: **408 REQUEST_TIMEOUT** with a clear message: the booking expired before edit completion.  

This ensures atomicity and accuracy.  
 
**7. Booking Recalculation & Business Rule Application**   

Using `BookingMapper.editedBooking()`, the system recomputes:   
- New travel date
- Departure & arrival timestamps
- Fare breakdown
- Discount eligibility (5% for USER role only)
- TotalCost, DiscountAmount, FinalCost
- Updated seat counts
- `userEditedAt` timestamp
- `Bus.availableSeats` adjusted by modifiedCount  

All calculations use centralized **BigDecimal utilities** to avoid rounding issues. This ensures 100% consistency with the Start Booking logic.  

**8. Transactional Save & Audit Logging**  

Within a single `@Transactional` boundary:  
- Updated passenger profile is saved
- Booking record is modified
- Bus seat availability is adjusted
- Audit logs capture who edited what and when  

If any backend issue occurs → **500 INTERNAL_SERVER_ERROR**.  

**9. Final Update and Response**  
- Once all edit validations pass, the system applies the modifications (seats, travel date, recalculated timings, and fare adjustments) using `BookingMapper.editedBooking()`.
- These updates are executed inside a single transactional boundary with optimistic locking to ensure consistency under concurrency.
- After the changes are persisted, the system logs the booking ID, user ID, updated seat count, travel date adjustments, and metadata related to the edit operation.
- The finalized state is then mapped into a `BookingPreviewDto` and returned with **200 OK**, providing a complete and recalculated snapshot of the booking before proceeding to the Continue step.

**Response Includes (via `BookingPreviewDto`):**     
- **BookingInfo** — Reflects the updated booking details including the travel date, recalculated cost totals, discount information, final fare, and a refreshed `bookingExpiresAt` value to indicate the new validity window after editing.
- **BusInfo** — Contains the updated schedule information—departure and arrival timestamps recalculated based on the new travel date—along with bus metadata, route details, type, and per-seat fare used for cost recomputation.
- **PassengerInfo** — Provides the passenger’s identity along with the updated number of seats booked after the edit operation, ensuring full clarity before continuation.

**Failure Handling**  
- **Optimistic lock conflicts** (due to concurrent edits or near-expiry collisions) → **408 REQUEST_TIMEOUT**
- **Unexpected failures** during update or persistence → **500 INTERNAL_SERVER_ERROR**

This response serves as the **post-edit recalculated snapshot**, ensuring the client has an accurate and up-to-date view of the booking before proceeding further in the workflow. 

#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Booking Edit Success]()
</details>

#### ❗ Error Response 
> Invaid booking id
<details> 
  <summary>View screenshot</summary>
   ![Booking Edit Error]()
</details> 

> No booking data found
<details> 
  <summary>View screenshot</summary>
   ![Booking Edit Error]()
</details>  

> Seat conflict or insufficient seats
<details> 
  <summary>View screenshot</summary>
   ![Booking Edit Error]()
</details> 

> Attempt to edit after expiry  
<details> 
  <summary>View screenshot</summary>
   ![Booking Edit Error]()
</details> 

> Attempt to edit confirmed/cancelled/processing booking
<details> 
  <summary>View screenshot</summary>
   ![Booking Edit Error]()
</details> 

> Validation errors
<details> 
  <summary>View screenshot</summary>
   ![Booking Edit Error]()
</details> 


#### 📊 HTTP Status Code Table
| HTTP Code | Status                | Meaning                                 | When It Occurs                                     |
| --------- | --------------------- | --------------------------------------- | -------------------------------------------------- |
| 200       | OK                    | Booking edited successful               | All validations pass, saved                        |
| 400       | BAD_REQUEST           | Validation Error                        | Invalid DTO, seats < 1, invalid travelAt           |
| 403       | FORBIDDEN             | Edit Not Allowed                        | Booking status not editable                        |
| 404       | NOT_FOUND             | Booking not found                       | ID not present in DB                               |
| 408       | REQUEST_TIMEOUT       | Booking expired                         | Optimistic lock detects expiration                 |
| 409       | CONFLICT              | Seats unavailable / duplicate user info | Requested seats > available, conflicting user data |
| 500       | INTERNAL_SERVER_ERROR | System failure                          | Unexpected exception                               |


#### ⚠️ Edge Cases & Developer Notes    

**1. High-Concurrency Edit Operations**    
- When a booking is close to expiry or multiple users interact with the same bus simultaneously, parallel edits may conflict.
- This system employs **optimistic locking** to guarantee data integrity, reject stale updates, and provide users with clear guidance to restart the process.
This safeguard is especially critical during **peak weekends, holidays, and festival traffic**, where contention is high.  

**2. Seat Addition vs. Seat Reduction Rules**  

**1. Seat Reduction**  
- Frees seats immediately.
- Instantly updates bus availability.
- Never introduces concurrency conflicts.
  
**2. Seat Addition**  
- Requires strict seat-availability validation.
- Executed inside a concurrency-safe transaction.
- Prevents overbooking caused by simultaneous edits submitted seconds apart.  

**3. Travel Date Changes & Schedule Consistency**   

When the travel date is modified, the system automatically:  
- Recalculates **departure time** using `bus.departureAt`.
- Recomputes **arrival time** based on bus duration.
- Ensures all schedule values remain consistent and trustworthy.

User-provided date/time values are never directly trusted, protecting against invalid or manipulated inputs.  


**4. Passenger Profile Update Integrity**  

During profile updates, the system enforces strict checks to:  
- Prevent duplicate email usage across passengers.
- Maintain long-term identity uniqueness
- Ensures that no stale booking edits corrupt inventory or seat counts.
- Encourages users to re-initiate booking for expired reservations.
- If the booking expires during editing (optimistic lock), API returns **408 REQUEST_TIMEOUT**.

**5. Interaction with Auto-Expiry**  

If the system auto-expires a booking before the user submits their edit:
- The edit attempt fails with a controlled timeout response.
- Seats have already been released to inventory.
- Expired bookings cannot be revived or manipulated.   

This preserves the integrity of seat allocation and avoids accidental resurrection of invalid bookings.   

**6. Immutable Booking State Enforcement**  

Once a booking transitions into **PROCESSING, CONFIRMED, CANCELLED,** or **EXPIRED**, it becomes **permanently immutable**. This prevents:
- Mid-payment alterations
- Invalid fare recalculations
- Reconciliation inconsistencies.
- Post-confirmation modification loopholes.

It ensures that the booking lifecycle remains stable, auditable, and tamper-proof.
</details>


### ⏭️ 18. Continue a Pending Booking (Move to Processing & Prepare for Payment)
<details> 
  <summary><strong>PATCH</strong> <code>/public/bookings/{id}/continue</code></summary>

#### 🛠 Endpoint Summary   
**Method:** PATCH  
**URL:** /public/bookings/{id}/continue  
**Authentication:** Not Required  
**Authorized Roles:** PUBLIC(Both Registered & Non-Registered Users)    

#### 📝 Description  

This API represents the **Continue booking** step in the **booking workflow (Start → Edit → Continue → Confirm)** where a user advances a **PENDING + UNPAID** booking to the **PROCESSING** stage in preparation for payment. It ensures that only eligible, unexpired bookings proceed while maintaining full transactional safety, backend consistency, and precise user feedback. This API validates booking existence, checks for expiration, updates booking and payment statuses, resets transactional fields, and handles automatic seat release for expired bookings. Concurrent modifications are safely managed through optimistic locking, and immutable or already processed bookings are strictly blocked, making this step essential for preserving the integrity of the booking lifecycle.  

Key highlights:  
- Validates booking existence and eligibility before proceeding.
- Checks for booking expiration and handles expired bookings safely.
- Resets transactional fields such as `busTicket` and `transactionId.
- Updates booking status to **PROCESSING** and **payment status to** **PENDING**.
- Automatically releases seats if the booking has expired.
- Returns an updated **Booking Summary DTO** ready for payment.
- Handles concurrent modifications via **optimistic locking**.
- Blocks immutable or already processed bookings.  

It guarantees **transactional safety, backend consistency, and precise user feedback**, making it a critical stage in the booking lifecycle.  


#### 📥 Request Parameter
| Parameter | Type | Description                                | Required |
| --------- | ---- | ------------------------------------------ | -------- |
| `id`      | Long | ID of the booking to find & do continue proccess . Must be positive. | Yes      |
> 💡 Notes:  
- The id is the booking ID obtained from Start Booking or Edit Booking.
- Booking must be **PENDING + UNPAID**; otherwise, the API returns **FORBIDDEN**.


#### ⚙️ How the Backend Processes   

**1. Booking ID Validation**  
- The API first validates the `id`.
- If the ID is `null` or `≤ 0`, the request is immediately rejected with: **400 BAD_REQUEST → "Invalid Booking ID. Please check and try again"**.

   
**2. Booking Lookup & Eligibility Check**  

The system fetches the booking by ID and evaluates its status:
 
| Booking Status | Payment Status | Expired?    | Outcome                      |
| -------------- | -------------- | ----------- | ---------------------------- |
| PENDING        | UNPAID         | Not Expired | Eligible to continue         |
| PROCESSING     | Any            | Any         | Forbidden; already processed |
| CANCELLED      | Any            | Any         | Forbidden; cancelled         |
| EXPIRED        | Any            | Any         | Forbidden; expired           |
| CONFIRMED      | Any            | Any         | Forbidden; already confirmed |

If the booking is **not found** → `404 NOT_FOUND`.  

If the booking is **ineligible** → `403 FORBIDDEN` with a contextual message.  

**3. Expiration Handling & Optimistic Lock Protection**  
- If the booking has expired `(bookingExpiresAt <= now())`:  
    - Marks the booking as **EXPIRED** using `BookingMapper.bookingExpiredBeforeContinue()`.
    - Releases booked seats back to the bus inventory.
    - Clears transactional fields (`busTicket`, `transactionId`)
    - Saves the updated booking **transactionally**.   
- In the case of a concurrent modification (optimistic lock failure):
    - Throws `ObjectOptimisticLockingFailureException` or `OptimisticLockException`.
    - API returns **408 REQUEST_TIMEOUT** → "Oops! Your booking has expired, and couldn't continue the process. Please make a new booking to continue."  

This mechanism ensures **atomicity, consistency, and correct seat inventory management**.  

**4. Status Update for Valid Booking**  
For the eligible bookings:
- Updates `bookingStatus` → **PROCESSING**
- Updates `paymentStatus` → **PENDING**
- Resets `busTicket` → **null** and `transactionId` → **null**
- Saves the booking **transactionally**
- Logs a detailed info message: `"Booking with Booking ID: X for User ID: Y, Name: Z was continued successfully."`

**5. Response Construction**   
- After the booking is successfully advanced to the PROCESSING stage, the system maps the updated entity into a structured `BookingSummaryDto`.
- This summary groups all relevant booking, bus, and passenger information into three well-defined sections—ensuring the client receives a complete and accurate representation of the booking before proceeding to payment.
- The API returns this DTO with **200 OK**, providing the exact details required for the upcoming **Confirm/Pay** step.  
   
**Response Includes (via `BookingSummaryDto`):**     
- **BookingInfo** — Contains all booking-level details including the booking ID, original creation timestamp, travel date, updated statuses (**PROCESSING/PENDING**), discount information, fare breakdown, final cost, and the refreshed `bookingExpiresAt` value that reflects the remaining window for confirmation.   

- **BusInfo** — Provides the associated bus metadata and trip schedule such as bus number, bus name, type, route details, duration, departure and arrival timestamps, and the per-seat fare used in cost calculation.  

- **PassengerInfo** — Includes the passenger’s profile along with the updated count of seats booked after any previous edit step.  

This DTO serves as the **intermediate, payment-ready snapshot** of the booking, ensuring the frontend has all necessary information to display a clear summary and guide the user seamlessly into the confirmation and payment stage.  
 

#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Success]()
</details>

#### ❗ Error Response 
> Invaid booking id
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details> 

> No booking data found
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details>  

> Access denied for this stage
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details> 

> Request timeout
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details> 

#### 📊 HTTP Status Code Table
| HTTP Code | Status                | When It Occurs                                              | Message                                                                                                     |
| --------- | --------------------- | ----------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| 400       | BAD_REQUEST           | Invalid booking ID                                          | "Invalid Booking ID. Please check and try again"                                                            |
| 403       | FORBIDDEN             | Booking already processed, cancelled, confirmed, or expired | Contextual message depending on status                                                                      |
| 404       | NOT_FOUND             | Booking not found                                           | "Booking data not found for given Booking ID"                                                               |
| 408       | REQUEST_TIMEOUT       | Booking expired before continue or optimistic lock detected | "Oops! Your booking has expired, and couldn't continue the process. Please make a new booking to continue." |
| 500       | INTERNAL_SERVER_ERROR | Unexpected backend error                                    | "Booking failed to continue due to internal server problem. Please try again later."                        |


#### ⚠️ Edge Cases & Developer Notes  

**1. Expired Bookings**  
- If `bookingExpiresAt <= now()`, the booking is automatically marked as **EXPIRED**.
- Seats are released back to inventory, and transactional fields (`busTicket`, `transactionId`) are cleared.
- Users cannot continue expired bookings and must initiate a **new booking**.
- **Fallback:** The API returns **408 REQUEST_TIMEOUT** with guidance to restart booking.

**2. Immutable Booking States**     
- Bookings in **PROCESSING, CONFIRMED, CANCELLED, or EXPIRED** are strictly immutable.
- Any attempt to continue such bookings is blocked with `403 FORBIDDEN`.
- This prevents mid-payment tampering, incorrect fare recalculation, and broken payment reconciliation.  

**3. Transactional Safety**  
- All operations (status updates, seat allocation adjustments, transactional field resets) occur **within a single transactional boundary**.
- Guarantees **data consistency**, prevents partial updates, and ensures that seat inventory and booking details remain synchronized even under high concurrency or system failures.
- **Fallback:** In case of transaction rollback (e.g., database deadlock or unexpected exception), the booking remains in its original state, and users receive an error with guidance to retry.  

**4. Logging & Audit**  
- Every continue operation logs: **Booking ID, User ID, Passenger Name, action, and timestamp**.
- Expiry handling, optimistic lock exceptions, and transaction rollbacks generate **warning/error logs** for system transparency and operational audit.
- Supports troubleshooting, monitoring of peak traffic, and regulatory or compliance audits.  

**5. Subtle Edge Cases / Production Considerations**  
- **Near-Expiry Race Condition:** A booking may expire during the exact moment a user hits continue. Optimistic locking ensures atomic handling and returns clear guidance to retry.
- **Concurrent Seat Requests:** Multiple users trying to modify the same booking or seat allocations are safely serialized by transactions and optimistic locking.
- **Partial System Failures:** If downstream services (payment gateway, bus inventory service) fail after the booking is set to PROCESSING, the transactional boundary ensures rollback, preventing inconsistent booking or seat states.
- **Clock Skew / Distributed Systems:** Expiry checks rely on server time; distributed deployments should synchronize clocks to avoid inconsistent expiration handling.
</details>  


### ☑️ 19. Confirm a Booking (Finalize Payment & Issue Ticket)  
<details> 
  <summary><strong>PATCH</strong> <code>/public/bookings/{id}/confirm</code></summary>

#### 🛠 Endpoint Summary   
**Method:** PATCH  
**URL:** /public/bookings/{id}/confirm  
**Authentication:** Not Required  
**Authorized Roles:** PUBLIC(Both Registered & Non-Registered Users)  

#### 📝 Description  
The **Confirm Booking API** represents the final step in the **booking workflow (Start → Edit (optional) → Continue → Confirm)**, allowing users to **finalize a PROCESSING booking, complete payment, and receive a bus ticket and transaction ID**. It ensures that only eligible bookings are confirmed while maintaining **full transactional integrity, backend consistency, and accurate user feedback**. The API validates booking existence, checks eligibility, verifies the chosen payment method, performs expiration checks, updates booking and payment statuses, generates a unique `busTicket` and `transactionId`, clears `bookingExpiresAt`, and returns a detailed **Booking Final Info DTO**. Concurrent modifications are safely managed via optimistic locking, and immutable, canceled, expired, or already confirmed bookings are strictly blocked, making this step essential for the integrity of the booking lifecycle.  

Key highlights:  
- Validates booking existence and eligibility for confirmation.
- Verifies the selected payment method (Card, UPI, Bank Transfer, Net Banking, QR Code)
- Performs expiration checks and marks expired bookings as EXPIRED.
- Updates `bookingStatus` to **CONFIRMED** and `paymentStatus` to **PAID**.
- Generates unique `busTicket` and `transactionId` for each confirmed booking.
- Clears `bookingExpiresAt` to finalize the booking window.
- Returns a detailed **Booking Final Info DTO** with seat allocation, fare breakdown, and trip details.
- Handles concurrent modifications via **optimistic locking**.
- Blocks immutable, canceled, expired, or already confirmed bookings to maintain transactional safety.  


#### 📥 Request Parameter
| Parameter | Type | Description                                | Required |
| --------- | ---- | ------------------------------------------ | -------- |
| `id`      | Long | ID of the booking to find & do confirm proccess . Must be positive. | Yes      |
> 💡 Notes:  
- The id is the booking ID obtained from Start Booking or Edit Booking.
- Booking must be **PROCESSING + PENDING**; otherwise, the API returns **FORBIDDEN**.    


#### 📥 Request Body  
{  
&nbsp;&nbsp;&nbsp; "paymentMethod": "Your Option",   
}   
> 💡 Tips: Only UPI or Card or QR Code or Bank Transfer, Net Banking are allowed. You can any of these values.  

#### ⚙️ How the Backend Processes  

**1. Booking ID & Request Validation**  
- `id` must be positive & not be 0; otherwise → **400 BAD_REQUEST**.
- Request body (`ConfirmBookingDto`) is validated via` @Valid` + `BindingResult` for:
    - Mandatory paymentMethod field
    - Accepted enum values
    - Rejecting NONE → only Card/UPI/Bank Transfer/Net Banking/QR accepted  

- Invalid inputs → **400 BAD_REQUEST** with descriptive messages.   

**2. Payment Method Parsing & Validation**  
- Converts payment method input to enum (PaymentMethod) using `ParsingEnumUtils.getParsedEnumType` utility class.
- If parsing fails or NONE is provided → **400 BAD_REQUEST** with clear error message.
- Only valid payment methods are accepted for confirmation.  

**3. Booking Lookup & Eligibility Check**  

The system retrieves the booking by ID and evaluates its current state to determine whether it can proceed to confirmation.  

| Booking Status | Payment Status | Expired?    | Outcome                       |
| -------------- | -------------- | ----------- | ----------------------------- |
| PROCESSING     | PENDING        | Not Expired | Eligible for confirmation     |
| PENDING        | Any            | Any         | Forbidden; cannot confirm yet |
| CANCELLED      | Any            | Any         | Forbidden; cancelled          |
| EXPIRED        | Any            | Any         | Forbidden; expired            |
| CONFIRMED      | Any            | Any         | Forbidden; already confirmed  |

If the booking does not exist → **404 NOT_FOUND** or If the booking fails any eligibility requirement → **403 FORBIDDEN** with a context-specific message explaining why confirmation is not allowed.  

**4. Expiration Handling & Optimistic Lock Protection**  

If the booking has expired (bookingExpiresAt <= now()):  
- Mark booking as EXPIRED using `BookingMapper.bookingExpired()`
- Set paymentStatus → FAILED, paymentMethod → NONE
- Clear transactional fields: `busTicket` and `transactionId`
- Release seats back to bus inventory
- Save transactionally

**Optimistic lock** failures → **408 REQUEST_TIMEOUT** with message: "Oops! Your booking has expired, and the payment couldn’t be processed. Please make a new booking to continue."   


**5. Confirm Booking & Generate Ticket**  

For eligible bookings, The booking status will be set to:  
- `paymentMethod` → user-selected payment method
- `paymentStatus` → PAID
- `bookingStatus` → CONFIRMED
- `busTicket` → generated via `UniqueGenerationUtils.generateTravelTicket()`
- `transactionId` → `generated via UniqueGenerationUtils.generateTransactionId()`
- `bookingExpiresAt` → null  

Saved **transactionally**, and logs detailed info: "CONFIRM BOOKING: Booking ID X has been confirmed successfully for User Y-Z".   

**6. Response Construction**  

Once the booking is successfully confirmed, the system maps the persisted entity into a `BookingFinalInfoDto`, which aggregates all relevant details of the finalized booking. The DTO is composed of three clearly structured blocks:  
 - **BookingInfo** — includes confirmation details such as booking ID, timestamps, payment status, payment method, generated `busTicket`, `transactionId`, applied discounts, and fare totals.
 - **BusInfo** — provides the associated bus and trip information including bus metadata, route, timings, duration, and fare.
 - **PassengerInfo** — contains the passenger profile and the final number of seats booked.

 The API returns this DTO with Response code -> **200 OK** a String message + DTO data, ensuring the client receives a complete, ready-to-display summary of the confirmed booking for ticket viewing, receipt generation, and post-confirmation workflows.  



#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Success]()
</details>

#### ❗ Error Response 
> Invaid booking id
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details> 

> No booking data found
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details>  

> Access denied for this stage
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details> 

> Request timeout
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details> 


#### 📊 HTTP Status Code Table  
| HTTP Code | Status                | When It Occurs                                                  | Message                                                                                                         |
| --------- | --------------------- | --------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------- |
| 400       | BAD_REQUEST           | Invalid booking ID or payment method                            | "Invalid Booking ID. Please check and try again" / "Invalid payment method"                                     |
| 403       | FORBIDDEN             | Booking not eligible (PENDING, CANCELLED, EXPIRED, CONFIRMED)   | Contextual message                                                                                              |
| 404       | NOT_FOUND             | Booking not found                                               | "No booking data found for given Booking ID"                                                                    |
| 408       | REQUEST_TIMEOUT       | Booking expired before confirmation or optimistic lock detected | "Oops! Your booking has expired, and the payment couldn’t be processed. Please make a new booking to continue." |
| 500       | INTERNAL_SERVER_ERROR | Unexpected backend error                                        | "Booking failed to confirm due to internal server problem. Please try again later."                             |


#### ⚠️ Edge Cases & Developer Notes  

**1. Expired Bookings**  
- The Confirm API strictly validates booking eligibility before performing any confirmation-related updates.
- If `bookingExpiresAt ≤ now()`, the booking is immediately marked **EXPIRED**, reserved seats are released to inventory, all transactional fields are cleared, and the user must create a **new booking**.
- Immutable states such as **CONFIRMED**, **CANCELLED**, **EXPIRED**, or any booking not in **PROCESSING** + **PENDING** are rejected with **403 FORBIDDEN**, preventing double payments, stale confirmations, fare mismatches, and seat corruption.  


**2. Immutable Booking States**   
- All confirmation-related updates — payment status change, status transition, ticket generation, clearing expiry, and seat validation — occur within a **single atomic transactional boundary**.
- If any component fails (DB conflict, ID generation failure, payment mismatch, or internal exception), the entire operation **rolls back**, ensuring:
    - No half-confirmed bookings
    - No duplicate or orphaned payments
    - No seat inconsistencies
    - No invalid or partially saved transaction IDs  

This guarantees a fully consistent booking lifecycle even during system faults or payment delays.  


**3. Concurrency Control & Optimistic Lock Handling**  

To handle real-world concurrency scenarios (double-clicking payment, client retries, delayed UI requests, or parallel backend checks), the API uses **JPA optimistic locking**. 
Only the first confirmation attempt succeeds; all subsequent overlapping attempts receive:
**408 REQUEST_TIMEOUT** → stale or conflicting update. This prevents:
 - Double confirmation
 - Duplicate tickets
 - Duplicate transaction IDs
 - Race conditions around expiry and seat reallocation  

It also ensures that the booking always transitions consistently and only once into a confirmed state.  


**4. Ticket & Transaction ID Generation (Precision & Uniqueness Logic)**  

Both identifiers are generated through a structured, collision-resistant algorithm designed for long-term uniqueness and traceability.  

**Ticket Generation (TK)**    
> `public static String generateTravelTicket(Integer digits, Long bookingId)`  
- Prefix "**TK**" identifies it as a travel ticket.
- Booking ID is formatted to **2 digits** (`%02d`) ensuring consistent length (e.g., 3 → “03”).
- Remaining length is filled with a **UUID-based random segment**, ensuring high entropy
- **Result:** Compact, unique, non-sequential tickets safe for public display.  

**Transaction ID Generation (TNX)**  
> `public static String generateTransactionId(Integer digits, Long bookingId)`
- Prefix "TNX" denotes a financial transaction.
- Uses formatted booking ID (`%02d`).
- Appends high-precision timestamp (`HHmmssSSS`), capturing hour, minute, second, millisecond.
- Fills remaining characters using a **UUID-derived random segment**.
- Result: Highly traceable, time-embedded, semi-deterministic ID ideal for payment reconciliation.   

**Why This Design Is Excellent**  
- Booking ID + Timestamp + UUID guarantees **very high uniqueness** even under heavy concurrency.
- Ticket IDs remain short and user-friendly, while transaction IDs carry detailed timing for audits.
- Prefix isolation (TK / TNX) prevents classification ambiguity.
- No predictable sequential pattern → secure and tamper-resistant.  

**5. Logging, Auditing & Failure Transparency**  

Every confirmation event logs key identifiers including **Booking ID, User ID, Passenger Name, action, timestamp, and payment method**.
Advanced logs are produced for:  
- Expiry transitions
- Optimistic lock conflicts
- Failed confirmations
- Payment mismatches
- Internal rollback scenarios  

These logs support operational monitoring, fraud detection, reconciliation workflows, chargeback investigations, and regulatory compliance (tax/GST/reporting).
</details>



### 🛑 20. Cancel a Booking (Revert Payment & Release Seats)
<details> 
  <summary><strong>PATCH</strong> <code>/public/bookings/{id}/cancel</code></summary>

#### 🛠 Endpoint Summary   
**Method:** PATCH  
**URL:** /public/bookings/{id}/cancel  
**Authentication:** Not Required  
**Authorized Roles:** PUBLIC(Both Registered & Non-Registered Users)  

#### 📝 Description  

The **Cancel Booking API** enables users to safely cancel a booking that has not yet been confirmed, ensuring transactional integrity and accurate seat management. This endpoint allows bookings in **PENDING** or **PROCESSING** status to be reverted, automatically releasing reserved seats, clearing transactional fields, and providing precise feedback to the user.  

Key features:  
- **Booking Validation:** Ensures the booking exists before proceeding.
- **Eligibility Check:** Confirms that only bookings eligible for cancellation (not CONFIRMED, EXPIRED, or already CANCELLED) can be reverted.
- **Concurrency Management:** Handles optimistic lock conflicts gracefully in case of simultaneous modifications.
- **Atomic Updates:** Applies all booking and seat adjustments within a single transactional boundary to guarantee consistency.
- **Logging & Auditing:** Captures detailed logs for booking ID, user actions, timestamps, and status changes to support traceability and regulatory compliance.  

This design ensures that cancellations are **safe, consistent, and fully auditable**, preventing double cancellations, payment inconsistencies, and seat allocation errors, while maintaining a seamless user experience.   

#### 📥 Request Parameter  
| Parameter | Type | Description                                    | Required |
| --------- | ---- | ---------------------------------------------- | -------- |
| id        | Long | ID of the booking to cancel. Must be positive. | Yes      |
> 💡 Notes:
- Only bookings in **PENDING** or **PROCESSING** status are eligible for cancellation.
- Once a booking is either **CONFIRMED**, **CANCELLED**, or **EXPIRED**, for those cancellation is forbidden.


#### ⚙️ How the Backend Processes  

**1. Booking ID Validation**  
- Validates that the provided booking ID is **non-null and positive**.
- Invalid IDs immediately return **400 BAD_REQUEST** with the message: "Invalid Booking ID. Please check and try again".

**2. Booking Lookup & Eligibility Check**  

Retrieves the booking from the repository using `bookingRepo.findById(bookingId)`. Eligibility rules are below:  

| Booking Status | Payment Status | Outcome                                         |
| -------------- | -------------- | ----------------------------------------------- |
| PENDING        | UNPAID         | Eligible for cancellation                       |
| PROCESSING     | PENDING        | Eligible for cancellation                       |
| CONFIRMED      | PAID           | Forbidden; cannot cancel confirmed bookings     |
| CANCELLED      | Any            | Forbidden; already cancelled                    |
| EXPIRED        | Any            | Forbidden; expired bookings cannot be cancelled |

Booking not found → **404 NOT_FOUND** & Ineligible booking → **403 FORBIDDEN** with contextual message: "You can only cancel bookings that haven’t been confirmed yet. Booking ID X is no longer eligible for cancellation."  

**3. Cancellation & Transactional Updates**  

For eligible bookings, the system performs atomic updates within a transactional boundary:  
- **Booking fields updated:**
    - `bookingStatus` → CANCELLED
    - `paymentStatus` → UNPAID
    - `paymentMethod` → NONE
    - `busTicket` → null
    - `transactionId` → null
    - `canceledAt` → current timestamp   
- **Bus seat release:** Increments `bus.availableSeats` by `booking.seatsBooked`.
- **Transactional save:** Ensures **all updates succeed together** or none are applied.  

**4. Concurrency & Optimistic Lock Handling**  
- Parallel modifications or auto-expiry detection are safely managed via **optimistic locking**.
- Conflicts trigger **408 REQUEST_TIMEOUT** with message: "This booking has expired and can no longer be cancelled."
- All exceptions are logged with context for auditing.  

**5. Logging & Audit**  
- Every cancellation is logged for traceability including Booking ID, User ID, User Name, and timestamp: "CANCEL BOOKING: Booking ID X has been cancelled successfully for User Y-Z".
- Optimistic lock warnings and internal errors are logged with timestamps and booking metadata.

**6. Response Construction**  
- On successful cancellation: **200 OK** with a user-friendly message confirming the cancellation.
- On errors: Returns appropriate HTTP status (**400, 403, 404, 408, 500**) with **descriptive messages** for the client.  


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Booking Cancel Success]()
</details>

#### ❗ Error Response 
> Invaid booking id
<details> 
  <summary>View screenshot</summary>
   ![Booking Cancel Error]()
</details> 

> No booking data found
<details> 
  <summary>View screenshot</summary>
   ![Booking Cancel Error]()
</details>  

> Access denied for this stage
<details> 
  <summary>View screenshot</summary>
   ![Booking Cancel Error]()
</details> 

> Request timeout
<details> 
  <summary>View screenshot</summary>
   ![Booking Continue Error]()
</details>   


#### 📊 HTTP Status Code Table 
| HTTP Code | Status                | When It Occurs                              | Message                                                                                                                |
| --------- | --------------------- | ------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| 400       | BAD_REQUEST           | Invalid booking ID                          | `"Invalid Booking ID. Please check and try again"`                                                                     |
| 403       | FORBIDDEN             | Booking not eligible for cancellation       | `"You can only cancel bookings that haven’t been confirmed yet. Booking ID X is no longer eligible for cancellation."` |
| 404       | NOT_FOUND             | Booking not found                           | `"No booking data found for given Booking ID X"`                                                                       |
| 408       | REQUEST_TIMEOUT       | Optimistic lock detected or booking expired | `"This booking has expired and can no longer be cancelled."`                                                           |
| 500       | INTERNAL_SERVER_ERROR | Unexpected backend error                    | `"Booking failed to cancel due to internal server problem. Please try again later."`                                   |


#### ⚠️ Edge Cases & Developer Notes  

**1. Eligibility Constraints**  
- Only bookings with status **PENDING** or **PROCESSING** are eligible for cancellation.
- Immutable states (**CONFIRMED, CANCELLED, EXPIRED**) are strictly blocked to maintain **data integrity and payment consistency**.
- Attempting to cancel ineligible bookings returns **403 FORBIDDEN** with a clear, contextual message.  

**2. Transactional Safety**  
- All updates—including **booking status, payment status, transactional fields, and bus seat adjustments**—are executed within a **single transactional boundary**.
- Guarantees **atomicity**, preventing partial cancellations such as:
    - Bookings marked cancelled while seats remain blocked.
    - Duplicate or orphaned bus tickets or transaction IDs.
    - Incorrect payment or seat reconciliation.  

**3. Concurrency Control**  
- **Optimistic locking** protects against simultaneous cancellations or modifications by multiple requests.
- Race conditions or bookings nearing expiry trigger **408 REQUEST_TIMEOUT**, prompting users to retry safely.
- Ensures no conflicting modifications corrupt booking data or seat availability.   

**4. Seat Management**  
- Upon cancellation, all booked seats are **immediately released** back to the bus inventory.
- Ensures **real-time seat availability** for other users without manual intervention.
- Guarantees accurate seat counts even under high concurrency scenarios.

**5. Logging & Auditing**    
- Detailed logs capture: `Booking ID, Management User ID and Username, Cancellation timestamp, Action type and metadata`.
- Supports **monitoring, troubleshooting, and regulatory audits**, providing full visibility into the booking lifecycle.  
</details>  


### 🧾 21. View Booking Records (Management View — Paginated & Filterable Search)  
<details> 
  <summary><strong>GET</strong> <code>/management/bookings</code></summary>


#### 🛠 Endpoint Summary   
**Method:** GET  
**URL:** /management/bookings    
**Authorized Roles:** Management/ADMIN    
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

#### 📝 Description  

This endpoint serves as the **central access point for management-level users (Admin role)** to view, audit, analyze, and filter every booking in the system. It functions as the **primary operational dashboard API**, supporting large-scale administrative workflows with precision and reliability. Designed for **high-volume environments**, the endpoint ensures **predictable, safe execution** through strict validation and **deterministic request handling**. It allows admins to efficiently review daily transactions, conduct investigations, and perform business analytics without compromising data integrity or performance.  

A key strength of this API is its **collision-free filter resolution strategy**, where the backend guarantees that **exactly one repository call** is executed per search pattern. This ensures **consistent query behavior and optimized performance**, even under heavy load. This endpoint enforces **strict role-based access control** — only administrative users can access it. **Passengers or regular users are completely restricted**, ensuring that sensitive operational data always remains secure.  

Overall, the endpoint delivers **high integrity, high observability, and high performance**, making it essential for system-wide operational monitoring, auditing, and analytical workloads.  

Key Features:  
- Full pagination for efficient navigation of large booking datasets.
- Role-secured visibility restricted to Admin-level users only.
- Rich filtering and search patterns supporting a wide range of operational queries.
- Multi-field sorting for flexible analysis workflows.
- Prefix-based keyword search engine enabling fast, focused lookups.
- Strict validation mechanisms ensuring predictable, safe execution.
- Deterministic, collision-free filter resolution for consistent and optimized performance.
- Optimized for daily transaction review, audits, customer support investigations, fraud detection, peak-travel monitoring, and revenue/cost analytics.  

#### 🔍 Search & Filter Logic Summary  
This API operates in two clear modes, ensuring efficient, predictable, and conflict-free search behavior:  
- **1. Default Fetch (No Keyword Provided)** When no keyword is supplied, the API returns **all bookings** in a **fully paginated, sorted**, and **structured format**. This is ideal for general browsing, auditing, and dashboard views.
- **2. Keyword-Based Filtered Search** When a keyword is present, the API switches to a **prefix-driven filtering engine**. This system ensures **no ambiguity, no filter collisions**, and **no overlapping search paths**, producing clean and deterministic results every time.  

**Supported Keyword Prefixes & Patterns**  
| Prefix / Pattern      | Meaning                 | Example                    |
| --------------------- | ----------------------- | -------------------------- |
| **id_**               | Booking ID              | `id_120`                   |
| **mobile_**           | User’s Mobile Number    | `mobile_9876543210`        |
| **cost_**             | Booking Cost            | `cost_560.50`              |
| **bus_**              | Bus Name                | `bus_VelExpress`           |
| **user_**             | User Name               | `user_John Doe`            |
| **location_**         | From & To Location      | `location_Chennai`         |
| **Email Regex**       | User Email              | `john@mail.com`            |
| **Bus Ticket Regex**  | Ticket ID               | `TK011b67c429`             |
| **Transaction Regex** | Transaction ID          | `TNX01131916642ba9a6b`     |
| **Bus Number Regex**  | Bus Registration Number | `TN10AB1234`               |
| **Date Patterns**     | Travel or Booking Date  | `01-01-2024`, `2024-01-01` |


**Validation & Safety**  

All search inputs undergo strict, centralized regex validation. This ensures:
- Stable and predictable filtering.
- No malformed query execution.
- Protection against query-level vulnerabilities.

#### 📥 Query Parameters  
| Parameter   | Type    | Default | Description                                                                                                                                                                                                  | Required |
| ----------- | ------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------- |
| **page**    | Integer | 1       | Page number (must be ≥1)                                                                                                                                                                                     | No       |
| **size**    | Integer | 10      | Page size (must be ≥1)                                                                                                                                                                                       | No       |
| **sortBy**  | String  | `id`  | Field by which to sort the results—options include: `id`, `fromLocation`, `toLocation`, `seatsBooked`, `bookedAt`, `travelAt`, `departureAt`, `arrivalAt`, `bookingExpiresAt`, `discountPct`, `totalCost`, `discountAmount`, `finalCost` | No       |
| **sortDir** | String  | `ASC` | Sorting direction (`ASC`/`DESC`, case-insensitive)                                                                                                                                                           | No       |
| **keyword** | String  | -    | Flexible, prefix-based search field                                                                                                                                                                          | No       |


#### ⚙️ Backend Processing Flow   
**1. Centralized Input Validation & Safety Gate**      

All requests pass through a strict validation layer `PaginationRequest.getRequestValidationForPagination()`, checking:  
- `page ≥ 1`, `size ≥ 1`
- `sortBy` must match a controlled whitelist
- `sortDir` must match **ASC/DESC**
-   keyword` must pass **prefix/regex rules**    

This ensures:  
- No invalid offsets
- No negative pagination
- No ORDER-BY or injection risks
- No unindexed field queries  

Invalid input returns **400 BAD_REQUEST** status code with message & timestrap immediately in a proper structure using `ApiResponse`.   


**2. Default Retrieval Mode (No Keyword Provided)**  

When no keyword is provided:  
- The full booking list is fetched.
- Pagination and sorting are applied.
- Results are mapped using `ManagementBookingDataDto`.
- The page is wrapped in an `ApiPageResponse`, which is then wrapped in an `ApiResponse` and returned by the controller.  

If the resulting page is empty, return **404 NOT_FOUND** with an empty list and page-level metadata.  

**3. Deterministic Prefix-Based Search Engine**  

The keyword search engine uses **rigid prefix isolation**:
- Eliminates ambiguous routes
- Ensures predictable execution
- Avoids conflicts between email/name/ticket formats
- Strengthens regex-driven validation
- Avoids fuzzy search performance overhead

Prefixes are evaluated in **strict sequence order**, ensuring stable behavior.  

**4. Identity & User-Based Filters (ID, Mobile, User Name, Email)**  

Grouped identity-oriented filters:
- **Booking ID (id_)** filter extracts the numeric identifier, validates that it is greater than zero, and performs a direct `findById(...)` lookup. Invalid IDs result in a **400**, while non-existing records return **404**.
- **Mobile Number (mobile_)** filter enforces a strict India-format regex (starting with 6/7/8/9) before executing `findByAppUser_Mobile(...)`.
- **User Name (user_)** filter allows only alphabets and spaces, preventing numbers or symbols from affecting identity-based searches.
- **Email** filter uses an RFC-compliant email regex and resolves queries via `findByAppUser_Email(...)`, ensuring accurate, standards-based email lookups.  

**5. Cost & Financial Filters (Cost, Transaction IDs)**  
- **Cost (cost_)** filter accepts both integer and decimal values, performs safe BigDecimal parsing, validates the format using financial regex rules, and queries via `findByBookingCost(...)`.
- **Transaction ID** filter applies a strictly structured regex format to maintain financial identity integrity, ensuring clear separation from ticket numbers or bus registration formats.  

**6. Transport Entity Filters (Bus Name, Bus Number, Ticket ID)**  
- **Bus Name (bus_)** filter enforces a controlled naming pattern and resolves the booking list using `findByBus_BusName(...)`.
- **Bus Number** filter follows the Tamil Nadu registration format (`TN10AB1234`) while remaining extensible for future state patterns.
- **Ticket ID** filter validates against a pre-approved ticket structure, guaranteeing format correctness and preventing collisions with transaction or vehicle identifiers.  

**7. Location-Based Filtering (location_)**    
- Location filtering follows a structured multi-step pipeline. The input is first validated using a location-style regex, then cross-checked against the `MasterLocation` table. If the location is not found, the system returns **404**. 
- Valid locations are used to fetch all bookings tied to the corresponding From/To routes, after which results are mapped into uniform DTO structures. This ensures strict route-level semantic accuracy.  

**8. Date-Based Filtering (Travel or Booked Date)**  
- Date-based queries support multiple formats (**dd-MM-yyyy, dd/MM/yyyy,** and **yyyy-MM-dd**).
- The system automatically detects the provided format, parses it using `ResolverStyle.STRICT` to eliminate invalid or ambiguous dates, converts the input into a `LocalDate`, and finally retrieves matching bookings via `getBookingByDate(...)`.
- This guarantees temporal precision and consistent date handling across all search paths.  

**9. Fallback Keyword Search Layer**  
- When a keyword does not match any known prefix or regex category, it is processed by the `getByAnyKeyword()` fallback mechanism.
- This allows extended or non-standard search scenarios while keeping the deterministic prefix pipeline intact, ensuring flexibility without compromising architectural predictability.  

**10. Response Structure**   

On successful retrieval, the API returns an HTTP **200 OK** response using the `ManagementBookingDataDto`. The payload delivers a **complete administrative snapshot** of each booking, combining booking-level details, bus metadata, and passenger information into a single, dashboard-optimized structure. Pagination metadata is added through `ApiPageResponse`, supporting smooth table rendering and large-volume navigation.  

**Response Includes (via `ManagementBookingDataDto`):**   
- **BookingInfo**  Contains the full lifecycle and financial details of a booking. Fields include the booking ID, timestamps (booked, edited, expired, canceled), travel date, booking/payment statuses, payment method, ticket number, transaction ID, discount details, and cost breakdown (`discountAmount`, `totalCost`, `finalCost`). This ensures complete traceability for audits, financial checks, and booking-status investigations.
- **BusInfo**  Provides essential metadata about the bus tied to the booking. Includes identifiers (id, busNumber, busName), classification (`busType`, `acType`, `seatType`), route information (fromLocation, toLocation), schedule timing (`departureAt`, `arrivalAt`), duration, and base fare. This allows administrators to correlate bookings with routes, schedules, and fare logic for operational reviews.
- **PassengerInfo**  Represents the passenger linked to the booking, including ID, name, email, mobile, gender, role, and seats booked. These details support identity validation, customer support handling, and booking-pattern analysis.  

**Design Intent**  
- The DTO merges **booking, bus**, and **passenger** data to deliver a unified, management-friendly response suitable for dashboards, audits, revenue analytics, and operational monitoring.
- Its layered design is fully extensible, allowing new attributes or metadata to be added in the future without breaking existing clients.
  

#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Booking View Success]()
</details>

#### ❗ Error Response 
> Invaid pagination input
<details> 
  <summary>View screenshot</summary>
   ![Booking View Error]()
</details> 

> Invaid keyword input
<details> 
  <summary>View screenshot</summary>
   ![Booking Cancel Error]()
</details>  

> Access denied for this stage
<details> 
  <summary>View screenshot</summary>
   ![Booking Cancel Error]()
</details> 

#### 📊 HTTP Status Code Table  
| HTTP Code | Status Name           | Meaning               | When It Occurs                                   |
| --------- | --------------------- | --------------------- | ------------------------------------------------ |
| **200**   | OK                    | Success               | Valid query, data returned                       |
| **400**   | BAD_REQUEST           | Validation Failed     | Invalid input, regex fail, pagination error      |
| **404**   | NOT_FOUND             | No results            | Page empty / no data for filter                  |
| **401**   | UNAUTHORIZED          | Authentication Failed | Token invalid or expired                         |
| **403**   | FORBIDDEN             | Access Denied         | User lacks ADMIN role                            |


#### ⚠️ Edge Cases & Developer Notes   

**1. Deterministic Keyword Namespace & Collision-Free Search Model**  
- The keyword system is built on **explicit prefix segmentation** (`id_`, `mobile_`, `bus_`, `user_`, `location_`, `cost_`, etc.), ensuring that each input lands in a uniquely defined namespace. Once a prefix is identified, **no additional interpretation layers** (email, ticket, bus number, etc.) are evaluated.
- Regex-based namespace fencing guarantees that malformed postfixes (e.g., `mobile_123ABC``, bus_AB12`, partial IDs) fail early and never leak into unrelated domains. This design delivers **strict, non-overlapping search semantics**, eliminating ambiguity, multi-match failures, and unpredictable fall-through behavior even under complex input scenarios.  

**2. Temporal, Numeric, and Format-Validation Integrity**  
- Date values are parsed using **ResolverStyle.STRICT**, ensuring zero silent coercion and rejecting outputs such as 32/01/2024, 29/02/2023, or invalid month/day combinations. Supported formats (`dd-MM-yyyy`, `dd/MM/yyyy`, `yyyy-MM-dd`) are auto-detected and normalized into a canonical ISO representation to maintain consistent repository queries and prevent DST or timezone drift.
- Cost inputs follow a **hardened monetary validation pipeline**, accepting only plain integer or fixed-decimal structures while rejecting scientific notation, comma-grouped numbers, and malformed hybrids. All values are promoted to deterministic `BigDecimal` representations, protecting the financial layer from precision inconsistencies and coercion-based attacks.

**3. Deterministic Repository Resolution & Query Path Stability**  
- The search engine uses a **one-condition → one-repository-method architecture**, explicitly avoiding dynamic predicates or runtime-generated specifications. Every normalized keyword can activate only one code path, guaranteeing stable, index-aligned queries and preventing unintended full-table scans.
- Branch isolation ensures that once a prefix passes validation, no other path can override its semantics, providing a strictly acyclic, deterministic execution tree. Invalid formats fail before any query execution, maintaining consistent behavior under load and eliminating backend-side noise.  

**4. Data Exposure Control, DTO Discipline & Privacy Guarantees**  

The response DTO exposes only operationally relevant booking, bus, and passenger attributes, intentionally excluding internal audit fields, schema identifiers, authentication metadata, and internal relational structures.
This achieves:
 - **Privacy protection** (no passwords, auth states, or privileged user flags)
 - **Schema abstraction** (no exposure of joins, table layouts, or FK design)
 - **Contract stability**, ensuring future schema refinements will not break API consumers
The DTO is optimized for administrative insight while preventing information leakage or backend-coupling risks.

**5. Failure-Injection Safety & Defensive Message Boundaries**  

Error responses are crafted with:
- **Explicit cause identification**
- **No internal exception leakage**
- **Context-aware messages** (e.g., “page 5”, “Booking ID 40”, “given date”, etc.)  

This ensures:  
- Client applications receive actionable, domain-specific error feedback.
- No exposure of repository method names, SQL patterns, or backend stack details.   

**6. Scalability, Future Extension, and Maintenance Guarantees**  

The entire design supports future extensions with minimal risk:  
- Adding a new search capability requires **one prefix, one validation rule, one isolated branch**, and **one repository method**.
- Each new prefix-based or any keyword can be added as an isolated deterministic branch.
- Repository paths remain explicitly bound → predictable code review and debugging.
- DTO expansion is forward-compatible, allowing additional fields or nested metadata without breaking current consumers.  

This architecture is intentionally geared toward **enterprise-grade observability, testability, and long-term maintenance**.
</details>  


### ➕ 22. User Registration / Sign-Up (New User + Guest Upgrade Flow)  
<details> 
  <summary><strong>POST</strong> <code>/auth/bookmyride/public/signup</code></summary>


#### 🛠 Endpoint Summary   
**Method:** POST  
**URL:** /auth/bookmyride/public/signup  
**Authorized Roles:** Public (Anyone can access except management user)  
**Authentication:** Not Required   
(This endpoint issues a new JWT Token. For further details, See **Authentication & JWT Usage** inside Security Architecture under Technical Architecture.)   

#### 📝 Description  

The **User Registration / Sign-Up API** is responsible for creating new customer accounts and upgrading existing guest users to full registered users within the **_BookMyRide_** platform. This API plays a core role in user identity management, ensuring that every customer can transition smoothly from lightweight guest usage to a fully authenticated, privilege-enabled account. Designed with a premium experience in mind, this endpoint supports both first-time users and returning customers who previously used the system as guests.  

BookMyRide implements a **tiered user architecture**, allowing different levels of access depending on each user’s account type. By default, new visitors enter the system as **Guest Users**, giving them the freedom to browse services and make bookings without upfront registration. However, their abilities remain restricted until they choose to sign up as **Registered Users**, unlocking essential features that enhance the long-term booking experience. This API is responsible for bridging this gap—converting temporary guest accounts into full-featured user profiles when needed.  

**User Type Capabilities**  

1️⃣ **Guest Users (Non-Registered Customers)**  
- **Guest users** can make bookings without needing an account, enabling fast first-time usage.
- However, they cannot:
    - View profile
    - Update profile
    - Change password
    - View booking history
    - Receive loyalty or discount benefits  

2️⃣ **Registered Users (USER Role)**  
- After signing up, customers gain full access to premium features, including:
 
    ✔ View & edit profile  
    ✔ Change password   
    ✔ View current & past bookings  
    ✔ Receive **5% discount** on every booking (details in Booking API)   

This API handles **all scenarios**:  

**1. Brand new user signs up** → Creates a new USER  

**2. Existing Guest User signs up** → Guest is upgraded to USER  

**3. Existing registered User signs up** → Sign-up is blocked   

💡 Note: Upon successful registration or guest upgrade, a JWT token is generated and returned in the data field of the response. Clients must use this token in the `Authorization: Bearer <JWT_TOKEN>` header for all subsequent protected API requests.   


#### 📥 Request Body  
{  
&nbsp;&nbsp;&nbsp; "mobile": "Your Mobile Number",   
&nbsp;&nbsp;&nbsp; "password": "MySecurePassword123"    
}  
> 💡 Tips: You can replace the above values with your own existing or new mobile number & password. My system will handle this gracefully.  
> 💡 Note: If you give the mobile number that **already registered** as `Management` user account return **403** with message.     

#### ⚙️ Backend Processing Flow  

**1. Validate Incoming Request**  
- The request body is mapped to `SignUpLoginDto`. Bean Validation annotations (`@Valid`) are applied, with `BindingResult` capturing validation errors.
- If validation failed which returns HTTP Status: **400 BAD_REQUEST**, Response: List of validation errors extracted via `BindingResultUtils` custom utility class.  

**2. Check Mobile Number in Management Layer**    
- Before proceeding, the system verifies whether the mobile number is restricted at the management level using `managementService.ifMobileNumberExists(signUpDto.getMobile())`.
- **If the number exists in the management layer** returns HTTP Status: **403 FORBIDDEN**, message as "Access denied for this role."
- This ensures blacklisted or restricted numbers cannot register, maintaining system security.   

**3. Check Existing AppUser Record (Most Important flow)**  
- The system then checks if the mobile number already exists in the application’s user table using `Optional<AppUser> existedAppUser = appUserService.fetchByMobile(...)`. Based on the query result, the flow splits into three distinct scenarios:  

**1. Existing USER**    
- **Condition:** Mobile number exists and the account is already a fully registered `USER`.
- **Action**: Reject the sign-up attempt.
- **Response**: Returns **403 FORBIDDEN** with message.  

This prevents duplicate accounts and ensures data integrity.  

**2 Existing GUEST**  
- **Condition:** Mobile number exists and the account is a `GUEST` (role = `GUEST`, `isUser = false`).
- **Action:** Upgrade the guest account to a fully registered `USER`.
- **Upgrade Steps:**
  
     1. Promote role → `USER`
     2. Encode and set password
     3. Mark profile as completed
     4. Set `registeredAt` timestamp as current System dtate & time
     5. Generate a new **JWT token** for fast & secure access  
- **Response:** Returns **200 OK** with message + newly generted JWT token.
- **Important to note:** Guest data, including booking history, saved trips, and personal information, is preserved during the upgrade.  

**3 Completely New User**  
- **Condition:** Mobile number does not exist in `AppUser` table/database.
- **Action:** Create a new `AppUser` record.
- **Initialization Steps:**  

     1. Assign role → `USER`
     2. Set `isUser = true` and `isProfileCompleted = false` because there are some more information is missing that can fill through making a booking or update profile API.
     3. Generate placeholder name/email using `MockDataUtils` utils
     4. Encrypt password for security reason
     5. Save the user record in the database  
- **Response:** Returns **201 CREATED** with message + newly generted JWT token.   

**4. Generate JWT Token**  

After a successful registration or guest upgrade, the system generates a **JWT token** to authenticate the user immediately using `jwtService.generateToken(mobile, role, true)`. Which results the token,
- **Token Payload Includes:**
    - `mobile` → Serves as the unique identifier for the user
    - `role` → The user role (`USER`)
    - `isAuthenticatedUser` → Flag indicating the user is fully authenticated
- The newly registered or upgraded user is considered fully authenticated immediately.
- The token can be used to access all customer-specific endpoints without requiring an additional login.  

**Note:** Management users follow a separate authentication flow and are not handled by this endpoint. Their token creation logic is documented in the Admin Authentication API.    

**5. Summary Of Logic Flow**  
| Scenario          | Condition                  | Action             | Response        |
| ----------------- | -------------------------- | ------------------ | --------------- |
| New Mobile        | Not found in AppUser       | Create new USER    | 201 CREATED     |
| Existing Guest    | Exists as GUEST            | Upgrade to USER    | 200 OK          |
| Existing USER     | Exists as USER             | Block registration | 403 FORBIDDEN   |
| Restricted Mobile | Exists in Management Layer | Block registration | 403 FORBIDDEN   |
| Invalid Request   | Fails validation           | Return errors      | 400 BAD_REQUEST |


#### 📤 Success Response  
> New User register 
<details> 
  <summary>View screenshot</summary>
   ![User Auth Success]()
</details>  

> Guest → User Upgrade
<details> 
  <summary>View screenshot</summary>
   ![User Auth Success]()
</details>  


#### ❗ Error Response 
> DTO Validation Failed
<details> 
  <summary>View screenshot</summary>
   ![User Auth Error]()
</details> 

> Mobile Restricted by Management
<details> 
  <summary>View screenshot</summary>
   ![User Auth Error]()
</details>  

> Already Registered User
<details> 
  <summary>View screenshot</summary>
   ![User Auth Error]()
</details>  

#### 📊 HTTP Status Code Table
| HTTP Code | Status Name           | Meaning               | When It Occurs                                   |
| --------- | --------------------- | --------------------- | ------------------------------------------------ |
| **201**   | CREATED               | New User Registered   | A completely new account is created              |
| **400**   | BAD_REQUEST           | Validation Failed     | Missing required fields as per DTO               |
| **403**   | FORBIDDEN             | Access Denied         | Management block / Duplicate signup              |  

#### ⚠️ Edge Cases & Developer Notes  

**1. Guest User Upgrade Instead of Duplicate Registration**  

If a Guest User signs up:
  - System **does NOT** create a new user
  - Instead performs a seamless upgrade
  - Maintains guest’s previous bookings & metadata
  - Prevents duplicate data in AppUser table
  - Keeps analytics/reporting consistent
   
This ensures BookMyRide maintains long-term user history integrity.  

**2. Automatic Dummy Data for New Users**  

For newly registered users:
- **Dummy Name:** Generated using the `MockDataUtils` utility class. The format uses a `USER_` prefix followed by the last five digits of the user’s mobile number.
- **Dummy Email:** Generated using the `MockDataUtils` utility class. The entire mobile number is used as the local part, with `@dummy.com` as the domain.
- User can update these after signup. This avoids null values in BI reports, booking summaries, or notifications.
 
> Users can update both name, email & other info after signup. This approach prevents null values in BI reports, booking summaries, and system notifications, ensuring data consistency across the platform. 

**3. Secure Password & Consistent JWT Issuance**    
- All passwords are encoded using **BCrypt**, ensuring they are never stored in plain text.
- For guest upgrades, a new password is always required, as guest accounts initially have no password.
- Password updates automatically record a timestamp for auditing and compliance.
- Both new users and upgraded guests receive a **JWT token immediately** after signup.
- Eliminates the need for a **separate login step**, simplifying client-side workflows for mobile and web apps.
- Guarantees that the user is fully authenticated and ready to access customer-specific endpoints.  

**4. Profile Initialization & Data Integrity**  
- **Guest Upgrades:** Profile is marked as fully completed to preserve existing booking history and personal data.
- **New Users:** Profile is initially incomplete (only mobile and password provided), enabling onboarding flows and prompting optional field completion. Can be done via making 1st booking or using update user profile API.
- **Automatic Dummy Data:** Names and emails are generated via `MockDataUtils` to avoid null values in BI reports, booking summaries, or notifications.  

**5. Mobile Uniqueness & State Consistency**     
- Each mobile number maps to a **single user account** (guest or registered); duplicates are strictly prevented.
- Restricted or blacklisted numbers are blocked at signup to prevent unauthorized or fraudulent registrations.
- The flow ensures atomic upgrades:
    - No partial upgrades
    - No race conditions on the same mobile
    - Correct role promotion
    - Accurate registration timestamping
- This guarantees data consistency, security, and reliable handling of edge cases across all user scenarios.
</details>  

### 🔐 23. User Login  
<details> 
  <summary><strong>POST</strong> <code>/auth/bookmyride/public/login</code></summary>

#### 🛠 Endpoint Summary    
**Method:** POST  
**URL:** /auth/bookmyride/public/login  
**Authorized Roles:** Public (Guest + Registered Users)  
**Authentication:** Not Required   
(This endpoint issues a new JWT Token. For further details, See **Authentication & JWT Usage** inside Security Architecture under Technical Architecture.) 

#### 📝 Description  

The **User Login API** allows users to sign in to **_BookMyRide_** using their mobile number and password. No authentication is required to call this endpoint, so it can be accessed by both unauthenticated users and users who already hold a JWT and want to obtain a fresh token. This ensures flexibility while maintaining secure access to protected resources. The request payload is strictly validated using Spring’s `@Valid` annotation combined with `BindingResult` to capture any errors. Utility classes are used to process and format validation messages, ensuring that clients receive clear, structured error responses for any missing or malformed fields.   

Once validated, the system checks the user’s eligibility, blocking Guest or unregistered accounts and denying access to restricted users with appropriate HTTP status codes. Authentication is performed via Spring Security’s `AuthenticationManager`, which invokes the custom `MyUserDetailsService` to securely verify credentials and enforce role-based access control.  

Upon successful login, a JWT token containing the user’s mobile number, role, and an isLoginFlow = true flag is returned in the data field, and this token must be included in the Authorization: Bearer <JWT_TOKEN> header for all subsequent protected API requests.  

Key Features:  
- **No Pre-Authentication Required:** Can be called by unauthenticated users or users requesting a fresh JWT token.
- **DTO Validation:** Uses `@Valid` and `BindingResult` to ensure payload integrity.
- **Structured Validation Messages:** Utility classes format errors clearly for client consumption.
- **User Eligibility Checks:** Blocks Guest users and restricted accounts from logging in.
- **Secure Password Authentication:** Handled through Spring Security and custom `MyUserDetailsService`.
- **Layered Error Handling:** Returns appropriate HTTP responses for Unauthorized, Forbidden, and Validation errors.
- **JWT Token Generation:** Returns a fresh token containing mobile number, role, and login flow flag.
- **Seamless Client Integration:** Token returned in data field; to be used in `Authorization: Bearer <JWT_TOKEN>` header for protected endpoints.   

#### 📥 Request Body  
{  
&nbsp;&nbsp;&nbsp; "mobile": "9876543210",  
&nbsp;&nbsp;&nbsp; "password": "mypassword123"   
}  
> 💡 Tips: You can replace the above values with your sign-up mobile number & password. My system will handle this gracefully.    
> 💡 Note: If you give the mobile number that **already registered** as `Management` user account return **403** with message.  

#### ⚙️ How the Backend Processes  

**1. DTO + Input Validation**  

The request payload is validated using Spring’s `@Valid` annotation combined with `BindingResult`. If validation errors are detected, the system immediately responds with:  
  - HTTP 400 Bad Request
  - Detailed validation messages highlighting the invalid or missing fields   

This ensures that null, empty, or malformed mobile numbers and password fields are caught before reaching the authentication layer.  

**2. Check if Mobile Number Exists in Management Table**  

The system verifies whether the mobile number is restricted in the management layer via `managementService.ifMobileNumberExists(loginDto.getMobile())`. If the number exists then the system returns **403 Forbidden** with a message "Access denied for this role." & timestamp.  

This step enforces strict role separation and prevents restricted management accounts from logging in via public endpoints.  

**3. Fetch User by Mobile Number & Role Blocking**  
- The system checks the `AppUser` table to determine if the mobile number is registered via `appUserService.fetchByMobile(loginDto.getMobile())`.
- If no match found then the system returns **401 Unauthorized** with timestamp & message "Invalid mobile number or password." This prevents user enumeration by avoiding explicit messages indicating whether the user exists.
- If the retrieved user has a `GUEST` role then the system returns **403 Forbidden** with timestamp & message "Access denied. Please complete registration before logging in."
 
This ensures that guest accounts cannot access protected endpoints without completing registration.   

**4. Authenticate Using Spring Security**  

The controller prepares the user credentials and passes them to Spring Security’s `AuthenticationManager` for authentication. Internally, Spring Security:
- Loads user details using the custom class `MyUserDetailsService`.
- Verifies the password against the stored encrypted hash.
- Checks account status (active/inactive)  

Error handling is standardized:  
| Exception                   | HTTP Response      | Meaning                                |
| --------------------------- | ------------------ | -------------------------------------- |
| `BadCredentialsException`   | 401 Unauthorized   | Incorrect password                     |
| `UsernameNotFoundException` | 404 Not Found      | User not found in authentication phase |
| Generic Exception           | 500 Internal Error | Unexpected authentication failure      |  

All errors are returned in a consistent JSON structure (ApiResponse) for clear client understanding.  

**5. On Successful Authentication → Generate JWT Token**  
- After successful login, the system generates a **JWT token** using the user’s mobile number and role, along with a **login-flow** flag to indicate this token originated from a login request.
- The token is returned in the response along with **HTTP 200 OK**, message in a proper structure through `ApiResponse`.
- This ensures the client has a fully authenticated token for all protected API operations.    


#### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![User Auth Login Success]()
</details>  


#### ❗ Error Response 
> DTO Validation Failed
<details> 
  <summary>View screenshot</summary>
   ![User Auth Login Error]()
</details> 

> Mobile belongs to management user
<details> 
  <summary>View screenshot</summary>
   ![User Auth Login Error]()
</details>  

> User not registered
<details> 
  <summary>View screenshot</summary>
   ![User Auth Login Error]()
</details>  

> Guest role attempting login
<details> 
  <summary>View screenshot</summary>
   ![User Auth Login Error]()
</details>  

> Wrong password
<details> 
  <summary>View screenshot</summary>
   ![User Auth Login Error]()
</details>  

#### 📊 HTTP Status Code Table
| HTTP Code | Status Name           | Meaning                      | When It Occurs                 |
| --------- | --------------------- | ---------------------------- | ------------------------------ |
| **200**   | OK                    | Login successful             | Valid credentials              |
| **400**   | Bad Request           | DTO / input validation error | Missing fields, invalid format |
| **401**   | Unauthorized          | Authentication failed        | Invalid mobile or password     |
| **403**   | Forbidden             | Role not allowed             | Guest user, management user    |
| **404**   | Not Found             | User not found               | Username missing in auth phase |
| **500**   | Internal Server Error | Unexpected backend error     | Any unhandled exception        |


#### ⚠️ Edge Cases & Developer Notes  

**1. Guest Account Enforcement**   

The system strictly blocks `GUEST` users from logging in until they complete registration.
- Ensures partial account usage is prevented.
- Forces users to upgrade to a fully registered profile before accessing protected endpoints.
- Response: **403 Forbidden** with clear guidance message.  

**2. Custom UserDetailsService for Flexible Authentication**  

Authentication leverages a custom `MyUserDetailsService`, allowing:
- Loading users from **both `AppUser` and `Management` tables** based on login identifier.
- Password comparison using **BCrypt** for strong security.
- Role and mobile injection into `UserPrincipal` for downstream access control.  

This ensures a clean separation between public users and management accounts while maintaining a single authentication entry point.  

**3. Mobile Number as Primary Login Identifier**  
- The login system uses **mobile numbers exclusively** as the username for users.
- Simplifies onboarding and login flows.
- Avoids confusion or duplication that could occur with email/username-based authentication.
- Mobile number serves as a **unique key** for user identity across the system.   

**4. Standardized Error Handling & API Response Structure**

All login-related exceptions are mapped into a **consistent JSON structure** via `ApiResponse`:  
- Validation errors → **400 Bad Request**
- Unauthorized credentials → **401 Unauthorized**
- Blocked access → **403 Forbidden**
- User not found → **404 Not Found**
- Unexpected errors → **500 Internal Server Error**   

This makes debugging and client-side error handling predictable and developer-friendly.  

**5. JWT Structure Supports Role-Based Access Control**  

The generated JWT token includes:  
- `username` (mobile number)
- `role` (USER, GUEST, ADMIN)
- `isLoginFlow` flag

**Benefits:**  
- Downstream APIs can reliably enforce **role-based access control**.
- Clients immediately receive a token ready for all protected requests.
- Login flow flag differentiates token issuance origin (login vs signup).
- Token standardization ensures seamless integration across mobile and web clients.
</details>  

### 👤 24. View User Profile
<details> 
  <summary><strong>GET</strong> <code>/users/profile</code></summary>  

#### 🛠 Endpoint Summary   
**Method:** GET  
**URL:** /users/profile       
**Authorized Roles:** USER    
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)    

#### 📝 Description  

The View **User Profile API** allows a fully authenticated user to retrieve their personal profile information after logging into the system. This endpoint is designed to serve as the central reference point for user-specific details and is typically called immediately after login, after profile update, or during user dashboard initialization.  

From a business standpoint, this API ensures users have full visibility of their account details, such as:  
- Personal identification information (Name, Gender)
- Contact information (Email, Mobile)
- Account metadata (Role, Profile Status)
- Security metadata (Password last updated timestamp)
- Engagement metrics (Total booking count)   

The `UserProfileDto` intentionally includes only safe-to-expose fields, excluding:   
- Password hashes
- Account flags
- Audit logs
- System metadata
- Internal relations  

This protects sensitive DB structure and ensures the API contract remains stable and front-end-friendly. And allows to build a personalized experience, validate user identity, show profile progress bars, and enable conditional UI logic (e.g., disabling booking features until the profile is completed).  

#### ⚙️ Backend Processing Workflow  

**1. Authenticate and Validate UserPrincipal**  
- Extracts `UserPrincipal` from `@AuthenticationPrincipal`.
- Validates the **JWT token** and ensures the user exists and has the `USER` role via `UserPrincipalValidationUtils.validateUserPrincipal()`.
- Possible validation failures:  
    - **401 Unauthorized** → Missing/Expired JWT
    - **403 Forbidden** → Role mismatch or invalid access
    - **404 Not Found** → User not found in DB  

Only after passing this validation is the authenticated `AppUser` entity retrieved.   

**2. Fetch User Profile**  
- The service layer executes `appUserService.fetchUserProfile(user)` JPA method using `AppUser` repository. This includes:
    - Checking if the user’s profile is **completed**.
    - If yes → Convert entity to DTO using appropriate mapper method: `AppUserMapper.appUserToUserProfileDto(appUser)`
    - If not → Return **FORBIDDEN** with proper message: "Your profile is not completed yet. Please complete your profile by using update profile API in order to view."
 
**3. Return Standard API Response**  
- The controller formats the final response using: `ApiResponse.successStatusMsgData()`. This ensures:
     - Uniform API response structure
     - Predictable fields for the frontend
     - Consistent semantics across the entire system  
- On failure, the controller returns standardized error models:
     - `ApiResponse.profileStatus(...)` for profile-related restrictions
     - Structured forbidden messages for business-rule violations
     - Unified error codes (e.g., Code.ACCESS_DENIED)   


#### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![User Profile View Success]()
</details>  


#### ❗ Error Response 
> Missing/Invalid/Expired JWT token.
<details> 
  <summary>View screenshot</summary>
   ![User Profile View Error]()
</details>  

> Profile not completed Or invalid role trying to access this endpoint.  
<details> 
  <summary>View screenshot</summary>
   ![User Profile View Error]()
</details>  

> User does not exist in DB
<details> 
  <summary>View screenshot</summary>
   ![User Profile View Error]()
</details>    

#### 📊 HTTP Status Code Table  
| HTTP Code | Status Name           | Meaning               | When It Occurs                     |
| --------- | --------------------- | --------------------- | ---------------------------------- |
| **200**   | SUCCESS               | Request succeeded     | Profile loaded successfully        |
| **401**   | UNAUTHORIZED          | Authentication failed | JWT missing/expired                |
| **403**   | FORBIDDEN             | Access denied         | Profile incomplete or invalid role |
| **404**   | NOT_FOUND             | Resource missing      | User entity not found              |
| **500**   | INTERNAL_SERVER_ERROR | Unexpected crash      | Server/logic error                 |


#### ⚠️ Edge Cases & Developer Notes (Must Read)  

**1. UserPrincipal Validation**  

The system performs multi-layer validation to ensure only authenticated, valid, and active users can access this endpoint:  
- JWT token must be valid and unexpired.
- The user must exist in the database.
- The account must be active.
- Deleted or disabled accounts cannot access the API.
- Role mismatch results in access denial.    

This protects against stale, compromised, or unauthorized sessions and guarantees robust authorization security.  

**2. Role-Based Access Control (RBAC)**  

This endpoint is **strictly for authenticated USER-role accounts**.  
- `ADMIN`, or other `Management` roles have their **own dedicated profile endpoints**.  
- Even if a token structurally contains a USER role, the role is verified against the **actual DB role**, preventing forged tokens or escalated privileges.  

**3. Scalability & Maintainability**  
- Validation logic is centralized in `UserPrincipalValidationUtils`, avoiding duplicated checks across controllers.
- The mapper stays focused on transforming entities → DTO safely.
- Service-level profile checks make it easy to extend future features like:  
    - Additional profile fields
    - Booking-based badges
    - Account completion trackers
    - MFA enrollment states   

The architecture is clean, modular, and easy to scale.      

**4. Why Profile Completion Is Strictly Enforced Before Returning Profile Data**   

In service layer, I placed the “profile completed” condition check before converting entity → DTO is not just technical — it’s strong business logic.  

**Because of My Smart Signup Feature**  

This system integrates signup flow is purposely designed to **minimize friction** and **maximize conversion**, similar to modern apps like Swiggy, Zomato, Ola, Uber, etc. Here’s how the logic works:   
  1. A new user can **register instantly** with only: **Mobile number & Password**
  2. The system auto-generates dummy placeholder values for:
       - Name
       - Gender
       - Email
       - Other profile fields
  3. This allows users to enter the system quickly and frictionlessly, without forcing them to fill tedious forms during signup.  

**When does the system collect real data?**   

I intentionally designed two gentle touchpoints:  
 - **Update Profile API** — user updates details when they choose.
 - **First booking flow** — users naturally enter details while making their first booking.     

> This mirrors real-world UX patterns: `“Let the user start using the app, and collect useful data organically along the way.”`  

**5. Why Users Must Not See Dummy/Placeholder Data**  

Because new accounts contain **auto-generated placeholder data**, showing this to the user would be confusing and unprofessional. In my case, How I handle:  
- Name: "USER_<Registered_Mobile_Number>"  
- Email: "<Registered_Mobile_Number>@dummy.com"  
- Gender: "Not Specified"   

This is backend precision data, not user-facing identity data. Therefore:   

**The API blocks access until profile is completed**    
- Users do not see meaningless placeholder values.
- UI always displays real personal information.
- Business processes downstream (bookings, communication, billing) always have validated identity info.  

**This is common in real-world apps**  
- My architecture is aligned with: IRCTC, Ola/Uber, Swiggy/Zomato, Amazon, Flipkart
- These apps allow account creation → then collect full profile details only when required.
- So, My implementation follows this proven **low-friction** onboarding strategy.  


**6. Developer Insight: Why this decision matters long-term**  

Future developers need to understand that:  
 - The “profile completeness” gate is not a random rule.
 - It is the backbone of your **frictionless onboarding strategy**.
 - It prevents exposure of placeholder data to users.
 - It ensures all meaningful profile views always return fully validated identity.  

Failing to enforce this could lead to:   
 - Incorrect user identity representation in UI.
 - Broken booking flows & Incorrect analytics.
 - Risk of incomplete records affecting business logic.   

 Hence, keeping this logic in the service layer is the correct and most scalable place.   
</details>   

### 🖋️ 25. Update User Profile (User Account Personalization)   
<details> 
  <summary><strong>PUT</strong> <code>/users/profile</code></summary>

#### 🛠 Endpoint Summary    
**Method:** PUT  
**URL:** /users/profile    
**Authorized Roles:** USER   
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)    

#### 📝 Description  

The **Update User Profile API** allows authenticated users to update their personal information.
This endpoint supports **_BookMyRide_**’s Smart Signup Architecture, where users initially register only with a mobile number and password. Because of this, newly registered users begin with placeholder profile data (name, gender, email) until they complete their profile for the first time. The API handles profile updates for two user types:  

**1️⃣ New User Updating Profile for the First Time** (These users only signed up with mobile + password)    
- Profile is incomplete (`isProfileCompleted` = false)
- Have placeholder name, gender, and email
- Email uniqueness is checked globally (no previous email exists for this user)  
- On successful update:
     - Profile fields are validated
     - Updates are applied atomically
     - `isProfileCompleted` is set to `true`
     - A clean `UserProfileDto` is returned   

**2️⃣ Existing User Updating an Already Completed Profile** (These users have real data already)  
- Profile is already completed. Since can update thier profile for some reasons.
- Email may be changed, but only if new email is unique.
- Mobile number cannot be changed. Because mobile is the primary unique identity in **_BookMyRide_**.
- Apply updates using shared method, then save and return updated profile DTO.  

Key highligths:  
- Consistent validation
- Email uniqueness handling for both new and existing users
- Atomic update operations
- Proper marking of completed profiles
- Clean mapping to DTOs for frontend use  

#### 📥 Request Body  
{  
&nbsp;&nbsp;&nbsp; "name": "Your Updated Name",   
&nbsp;&nbsp;&nbsp; "gender": "Your Updated Gender",   
&nbsp;&nbsp;&nbsp; "email": "Your Updated Email"      
}    
> 💡 Tip: Substitute these placeholders with your own values. But ensure by using unique values.


#### ⚙️ Backend Processing Flow   

**1. Authentication & User Validation**  
- Spring Security handles token validation, constructs a `UserPrincipal`, and injects it into the controller via `@AuthenticationPrincipal`. The system then validates the principal using: `UserPrincipalValidationUtils.validateUserPrincipal()`. Checks performed:
  
   - **Token validity** – verifies signature, expiration, and structure
   - **User existence** – ensures the account exists in the database
   - **Role validation** – ensures requester is a standard `USER`
   - **Account active check** – blocks disabled or revoked accounts
- If any failure occurs, then system returns error **401, 403, 404** accordingly. Only a validated AppUser proceeds to the profile update logic.  

**2. Input Validation (DTO Validation)**  
- The incoming request is bound to `UpdateUserProfileDto` and validated using standard bean validation `@Valid` along with `BindingResult`.
- If any validation constraints fail, the controller wrap the response into `ApiResponse` and that contains: **400 BAD_REQUEST** with a structured list of errors, extracted using `BindingResultUtils.getListOfStr()`.  
- This ensures that frontend clients receive clear, actionable feedback on invalid inputs before any processing occurs.   


**3. Email Uniqueness & Scenario-Based Handling**   

The system enforces email uniqueness before applying updates via JPA method `appUserRepo.existsByEmail()`. Behavior differs depending on the user state:  

**New Users (isProfileCompleted = false)**  
- Placeholder profile values exist (name, email, gender)
- Email must be unique globally → otherwise **409 CONFLICT**
- On successful update:
    - `isProfileCompleted` is set to true
    - Placeholders are replaced with actual user data  

**Existing Users (Profile Completed)**  
- Email changes are allowed only if unique or same as the current email (case-insensitive)
- Conflicting emails → **409 CONFLICT**
- Mobile number is immutable to preserve system integrity  

This ensures system-wide uniqueness while supporting legitimate updates.   

**4. Applying Profile Updates (Centralized Logic)**  
- All updates are processed through custom `applyProfileUpdates()` by passing existed `AppUser` entity & DTO. Which would proccess:
   - Parse and validate enums (e.g., gender) using `ParsingEnumUtils.getParsedEnumType`
   - Update name, email, gender
   - Update `profileUpdatedAt` timestamp  
- Atomic updates prevent inconsistent states. Enum parsing failures return **400 BAD_REQUEST** with a descriptive message.  

**5. Persisting Updates & Returning Response**  
- After update completion & saved the updated entity successfully. Then the data is transformed into a DTO by safely exclude the credentials info by using a mapper:  `AppUserMapper.appUserToUserProfileDto(updatedUser)` & controller retuns: **200 OK** with a success message & Updated profile data (`UserProfileDto`).    
- This approach ensures consistent, safe, and developer-friendly profile updates for both new and existing users.  


 #### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![User Profile Update Success]()
</details>  


#### ❗ Error Response 
> From DTO validation or enum parsing.
<details> 
  <summary>View screenshot</summary>
   ![User Profile Update Error]()
</details>  

> Email already exists for another user.  
<details> 
  <summary>View screenshot</summary>
   ![User Profile Update Error]()
</details>  

> Access denied by role (if tampered JWT)
<details> 
  <summary>View screenshot</summary>
   ![User Profile Update Error]()
</details> 

> User account missing in DB (stale token)  
<details> 
  <summary>View screenshot</summary>
   ![User Profile Update Error]()
</details>  


#### 📊 HTTP Status Code Table  
| Code    | Status      | Meaning                    | When Triggered                        |
| ------- | ----------- | -------------------------- | ------------------------------------- |
| **200** | SUCCESS     | Profile updated            | Valid update, email uniqueness passed |
| **400** | BAD_REQUEST | DTO/enum validation failed | Missing or invalid fields             |
| **403** | FORBIDDEN   | Role mismatch              | Admin/Management attempting access    |
| **404** | NOT_FOUND   | User missing               | Token tied to nonexistent account     |
| **409** | CONFLICT    | Duplicate email            | Another user already uses email       |


#### ⚠️ Edge Cases & Developer Notes  

**1. Strict Email Uniqueness Rules**  

My system uses case-insensitive comparison for both cases: 
 - **New user** → simple global email uniqueness check
 - **Existing user** → unique check unless the user is reusing their own email   

This avoids false conflicts when users re-save their current email.  

**2. Why Mobile Number Cannot Be Updated**  

This API **intentionally NEVER** updates mobile number because:  
- Mobile is the **primary unique identifier** in **_BookMyRide_**
- Booking logic depends on mobile
- Bookings are linked to users by mobile
- Changing mobile would break historic bookings & relations  

This design is absolutely correct for India, where mobile numbers are widely treated as unique identifiers. Future versions may include OTP-based mobile update flows.  

**3. Smart Signup Architecture (Why this API is Important)**   

My signup system is intentionally frictionless:  
 - **New users register with only:** Mobile number & Password, The other field values are placed with **System auto-generates placeholder** values. Why because:
     - Prevent nulls in DB
     - Prevent breaks in bookings, invoices, analytics
     - Faster onboarding
     - Modern user experience (used by Ola, Zomato, Swiggy)   
    
 - **Users then update profile:** Using this API or also can making thier 1st booking. This is a brilliant real-world UX technique — reduce friction, then progressively collect data.  


**4. Why Profile Completion Check at Service Layer Is Crucial**  

My intention enforces: `if (!appUser.getIsProfileCompleted()) {}`, That ensures:
  - Users must update real details before seeing or editing full profile.
  - Dummy placeholder fields are never shown to end-users.
  - System always maintains correct identity information.  

**5. Centralized Update & Persistence Logic**   

Profile updates are applied through a centralized method (`applyProfileUpdates`) and persisted atomically. This approach provides several key benefits:  
 - **Consistency & Reusability:** All update logic is centralized, avoiding duplication and ensuring uniform validation across fields.
 - **Atomicity & Transaction Safety:** Updates, persistence, and DTO transformation happen in a single, atomic operation, preventing partial updates or inconsistent states.
 - **Clean Controller Design:** The controller focuses solely on request handling and response formatting, keeping business logic encapsulated.
 - **Future-Proof & Extensible:** New profile fields (e.g., DOB, address, city) can be added easily without altering controller or endpoint structure.
</details>    

### 🔑 26. Change Password (Authenticated USER)   
<details> 
  <summary><strong>PATCH</strong> <code>/users/change-password</code></summary>

#### 🛠 Endpoint Summary    
**Method:** PATCH    
**URL:** /users/change-password      
**Authorized Roles:** USER   
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)    

#### 📝 Description   

The **Change Password API** enables authenticated users to securely update their account credentials, combining strong verification, modern hashing techniques, and audit-ready tracking to protect against unauthorized access and maintain system integrity. This endpoint ensures that password changes are safe, verifiable, and aligned with production-grade security standards.  

Key Features:  
- **Two-step verification:** Requires the current password for proof-of-ownership, even for authenticated sessions.
- **Secure hashing:** New passwords are hashed using BCrypt before storage.
- **Audit-ready tracking:** Updates `passwordLastUpdatedAt` timestamp for compliance and monitoring.
- **Zero-trust design:** Protects against token-only attacks, mitigating risks from stolen or compromised sessions.
- **Production-ready security:** Follows industry-standard best practices for secure password management.   

#### 📥 Request Body    
{  
&nbsp;&nbsp;&nbsp; "oldPassword": "Your_Current_Password",  
&nbsp;&nbsp;&nbsp; "newPassword": "Your_New_Password"  
}  
> 💡 Notes:
- `oldPassword` must match the current password stored in the system.
- `newPassword` must comply with centralized password rules (length, character variety).  


#### ⚙️ Backend Processing Flow  

**1. Authenticate & Validate UserPrincipal**  
- Spring Security extracts the JWT token and injects `UserPrincipal` via: `@AuthenticationPrincipal`. Then validation occurs using: `UserPrincipalValidationUtils.validateUserPrincipal()` utils, Which performes checks:
  
  **1. JWT token validity** – signature, expiry  
  **2. User existence** – blocks deleted or inactive users  
  **3. Role verification** – ensures only USERs can proceed  
  **4. Account active check** – prevents blocked or compromised accounts   

- If validation fails → early return:
  - **401 Unauthorized** (token expired/invalid)
  - **403 Forbidden** (wrong role)
  - **404 Not Found** (deleted user)  
- Validated `AppUser` is then used for password verification.  

**2. DTO Validation**  

`ChangeUserPasswordDto` is validated with `@Valid` and `BindingResult`:
- Checks for non-empty `oldPassword` and `newPassword`  
- Centralized password policy enforcement (length, complexity)  

If validation fails → **400 BAD_REQUEST** with detailed error list that get through `BindingResultUtils.getListOfStr(bindingResult)`.  

**3. Old Password Verification (Critical Security Layer)**    

Before updating a password, the system verifies that the user knows their current password. This ensures that even if a JWT token is stolen or compromised, unauthorized actors cannot change account credentials.  

**Security outcomes:**  
- Invalid old password could cause `BadCredentialsException`: Returns **401 Unauthorized** with a clear message.
- User not found could cause `UsernameNotFoundException`: Returns **404 Not Found** if the account does not exist.
- Other errors: Returns **500 Internal Server Error** for unexpected issues.  

**Importance**
  
  This step enforces a **defense-in-depth strategy** and aligns with industry-standard security practices, safeguarding accounts against token-only attacks and unauthorized access.   
 
**4. Secure Password Update**  

After old-password verification succeeds:  
1. Hash the new password using **BCrypt**
2. Update `passwordLastUpdatedAt = LocalDateTime.now()`
3. Save the AppUser entity: `appUserRepo.save()`  

This make ensures:
 - Only valid passwords are stored.
 - Audit timestamp is recorded.
 - The system remains fully secure even under multi-session conditions.    


 #### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![User Password Update Success]()
</details>  


#### ❗ Error Response 
> DTO constraints or weak password
<details> 
  <summary>View screenshot</summary>
   ![User Password Update Error]()
</details>  

> Old password incorrect  
<details> 
  <summary>View screenshot</summary>
   ![User Password Update Error]()
</details>  

> User not found
<details> 
  <summary>View screenshot</summary>
   ![User Password Update Error]()
</details> 


#### 📊 HTTP Status Code Table  
|   Code   | Status                | Meaning               | When Triggered                            |
| -------- | --------------------- | --------------------- | ----------------------------------------- |
| **200**  | SUCCESS               | Password updated      | Old password verified, new password valid |
| **400**  | BAD_REQUEST           | Validation failed     | DTO constraints or weak password          |
| **401**  | UNAUTHORIZED          | Authentication failed | Old password incorrect                    |
| **403**  | FORBIDDEN             | Access denied         | JWT valid, but wrong role                 |
| **404**  | NOT_FOUND             | User missing          | Token tied to deleted/stale account       |
| **500**  | INTERNAL_SERVER_ERROR | Unexpected error      | Any other system failure                  |


#### ⚠️ Edge Cases & Developer Notes  

**1. Old Password Verification – Critical Layer**  
- Even with a valid JWT, the API requires users to provide their current password to authorize a password change.
- This adds a critical protection layer against stolen-token scenarios, where attackers could misuse a compromised session.
- Verifying the old password through Spring Security ensures consistent, standardized authentication across the platform.  

**2. Strong Validation & Password Policy Enforcement**  
- Password rules are enforced at the DTO level using `@Valid`, ensuring uniform validation across controllers and services.
- This design supports future enhancements such as regex complexity rules, breached-password screening, and password history checks.
- Centralized & regex patterns & validation reduces duplication and makes the security posture easier to maintain long-term.  

**3. UserPrincipal Validation & Access Control**  
- `UserPrincipal` validation is executed before entering the business logic, rejecting invalid, expired, or unauthorized identities early in the flow.
- The endpoint is strictly limited to authenticated users with the `USER` role, preventing admins or privileged accounts from unintentionally accessing the c**onsumer-facing password change** path. This supports a clean, role-scoped architecture.    

**4. Security Auditing & Future-Ready Architecture**  
- Every successful password update refreshes the `passwordLastUpdatedAt` timestamp, enabling audit trails, compliance reporting, and proactive security notifications.
- The separation of **old-password verification** and hashing within the service layer allows the introduction of additional future security factors—such as **OTP, MFA**, or **step-up authentication**—without impacting the controller design.     
</details>    


### 📖 27. View Own Bookings (Passenger Dashboard – Paginated History)
<details> 
  <summary><strong>GET</strong> <code>/users/bookings</code></summary>

#### 🛠 Endpoint Summary    
**Method:** GET  
**URL:** /users/bookings      
**Authorized Roles:** USER   
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)    

#### 📝 Description  

The **View Own Bookings API** enables authenticated passengers to securely access their complete booking history, including upcoming trips, past journeys, and cancelled records. Designed for real-world, high-traffic user dashboards, this endpoint provides a **structured, paginated**, and **privacy-focused** data flow that exposes only the passenger’s own records. Its response model cleanly separates booking details and bus information, ensuring predictable rendering across modern web and mobile UIs while maintaining strong access control and data-ownership guarantees.  

Key Features:  
- **Full booking visibility:** Access past bookings, upcoming trips, and cancelled journeys.
- **Secure user-scoped data:** Strictly returns bookings belonging to the authenticated passenger (`USER` role).
- **Structured DTO response:** Clear separation of `BookingInfo` and `BusInfo` for **frontend-friendly** rendering.
- **Comprehensive details:** Includes payment status, ticket details, fare breakdown, route data, and schedule info.
- **Stable pagination:** Optimized for dashboards where users frequently browse through large booking histories.
- **Privacy-first design:** No management-level fields exposed; strictly **passenger-owned data only**.
- **Scalable & mobile-ready:** Suitable for continuous review across mobile apps, web dashboards, and multi-device access.  

#### 📥 Query Parameters  
| Parameter | Type    | Default | Description                                 | Required |
| --------- | ------- | ------- | ------------------------------------------- | -------- |
| `page`    | Integer | 1       | Page number (must be ≥ 1)                   | No       |
| `size`    | Integer | 10      | Page size (must be ≥ 1)                     | No       |
| `sortBy`  | String  | `id`    | Field by which to sort the results—options include: `id`, `fromLocation`, `toLocation`, `seatsBooked`, `bookedAt`, `travelAt`, `departureAt`, `arrivalAt`, `bookingStatus`, `paymentStatus`, `paymentMethod`, `bookingExpiresAt`, `discountPct`, `totalCost`, `discountAmount`, `finalCost` | No       |
| `sortDir` | String  | `ASC`   | Sorting direction (`ASC`/`DESC`)            | No       |


#### ⚙️ Backend Processing Workflow  

**1. Authentication & UserPrincipal Validation**  
- The endpoint extracts the logged-in user via `@AuthenticationPrincipal UserPrincipal`.
- If the token is invalid, expired, or malformed → **401 Unauthorized** is returned.
- The system validates that the account still exists (not deleted or disabled).
- The validated user ID becomes the key for fetching user-specific bookings.    

**2. Pagination & Sorting Validation (Safety Layer)**  

The following validations are enforced through `PaginationRequest.getRequestValidationForPagination(...)` a method from custom `PaginationRequest`. Which validates:
  - `page` must be ≥ 1
  - `size` must be ≥ 1
  - `sortBy` must belong to bookingFields whitelist
  - `sortDir` must be either `ASC` or `DESC`  

If any input fails → **400 BAD_REQUEST** with structured error response via `ApiResponse.errorStatusMsgErrors`. This prevents negative offsets, invalid sorting, and dangerous unindexed queries that may degrade performance.   

**3. Pageable Construction**  

After validating the request parameters, pagination is constructed using the project’s standardized utility: `PaginationRequest.getPageable(request)`. This ensures that all listing endpoints follow a consistent, safe, and predictable pagination model. The underlying implementation provides:  
 -  Deterministic ordering to avoid shifting items between pages.
 -  Stable page boundaries that remain consistent across repeated calls.
 -  Backend-aligned indexing optimized for database pagination and performance.  

This guarantees reliable behavior for both UI scrolling and server-side data fetching.  

**4. Fetching User-Specific Bookings**  

The service layer retrieves the bookings using JPA method `bookingService.fetchUserBooking(userId, pageable)`. This operation is strictly scoped to the authenticated passenger, ensuring:
- **Complete user isolation** — only bookings belonging to the logged-in user are ever returned.
- **Zero leakage of other users data**, reinforcing privacy and data-ownership rules.
- **Database-level pagination and sorting**, allowing efficient retrieval even for large booking histories

This provides a secure and scalable approach suitable for high-volume dashboards.  

**5. Handling Empty Results Gracefully**  

If the authenticated user has no booking records, the service returns:  
- **A NOT_FOUND status** with a clear, user-friendly message.
- **An empty list** to maintain consistent response structure.
- **Complete pagination metadata**, ensuring the frontend can still render pagination components safely.  

This approach provides a welcoming, onboarding-style experience for first-time users while maintaining strict API contract consistency.  

**6. Response Construction**    

On successful retrieval, the API returns a standardized response: `ApiResponse.successStatusMsgData(200, message, ApiPageResponse<List<BookingListDto>)`. The payload contains both the paginated metadata and a list of `BookingListDto` entries. This follows a **nested DTO response pattern**, commonly used in modern large-scale applications (e.g., YouTube, Netflix, Uber) to provide highly structured, UI-optimized data in a single request.  

**Nested DTO Structure (BookingListDto)**   

The response uses a hierarchical DTO (`BookingListDto`) that groups related data into two logical sections:  
- **BookingInfo** — Contains booking-specific details such as booking identifiers, timestamps, statuses, fares, and payment information.
- **BusInfo** — Contains bus-level metadata like bus name, type, route information, and fare details.  

This structure avoids flat, unorganized JSON blobs and instead delivers a **clean, component-ready object**, enabling frontends to directly map sections into UI cards, table rows, trip summaries, or schedule views with minimal transformation.

**Pagination Metadata**  
- Along with the nested list, the response includes a full pagination object (`ApiPageResponse`): total pages, total elements, current page number, page size, isFirstPage, isEmpty.
- This ensures that the frontend receives a **complete paginated envelope**, allowing infinite-scroll, page-navigation, and timeline-style booking history experiences without additional API calls.
 

 #### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![User Booking View Success]()
</details>   

> User has no bookings
<details> 
  <summary>View screenshot</summary>
   ![User Booking View Success]()
</details>  

#### ❗ Error Response 
> Invalid pagination inputs  
<details> 
  <summary>View screenshot</summary>
   ![User Booking View Error]()
</details>  

> User token expired / invalid    
<details> 
  <summary>View screenshot</summary>
   ![User Booking View Error]()
</details>  


#### 📊 HTTP Status Code Table  
| **Code** | **Status**            | **Meaning**           | **When Triggered**                             |
| -------- | --------------------- | --------------------- | ---------------------------------------------- |
| **200**  | OK                    | Success               | Valid request; bookings returned successfully  |
| **400**  | BAD_REQUEST           | Validation failed     | Invalid pagination, sorting, or request params |
| **401**  | UNAUTHORIZED          | Authentication failed | Token missing, invalid, or expired             |
| **404**  | NOT_FOUND             | No data               | User has no booking records                    |
| **500**  | INTERNAL_SERVER_ERROR | Unexpected error      | Any unhandled exception in backend processing  |


#### ⚠️ Edge Cases & Developer Notes  

**1. Strict Data Ownership**  
- Bookings are always fetched using the **authenticated user ID** extracted from the JWT.
- The service layer ensures **complete isolation**, so no cross-user data leakage is possible.
- This aligns with strict **privacy and compliance requirements**, preventing unauthorized access to other users’ bookings.  

**2. Safe Pagination & Sorting**   
- Sorting is restricted to a **whitelisted set of fields**, preventing SQL injection or invalid ordering errors.
- Page numbers, sizes, and directions are validated at the controller layer before constructing the `Pageable` object.
- This ensures **consistent pagination behavior** and avoids server-side errors or malformed requests.  

**3. Nested, Frontend-Friendly DTOs**  
- The API returns a **structured nested DTO** (`BookingListDto)`, separating `BookingInfo` and `BusInfo`.
- This design reduces frontend transformation logic, allowing UI components to render booking and bus details directly.
- Future additions (like seat layout or driver info) can be added to the DTO without breaking existing clients.  

**4. Graceful Handling of Empty Results**  
- If the user has no bookings, the system returns a **NOT_FOUND** response with an **empty list**.
- Pagination metadata is still included, allowing frontend components to render consistent UI states.
- A friendly, domain-aligned message encourages users to **start their first journey**, supporting a seamless onboarding experience.  

**5. Consistent and Extensible Service Layer**  
- Mapping from `Booking` entities to `BookingListDto` is centralized in the mapper, ensuring consistent data formatting.
- All business logic (fetching, pagination, mapping) resides in the service layer, making future features like **filtering, additional fields, or reporting** easier to implement.
- Controllers remain clean, focusing solely on request validation and response construction.
</details>   

### 🧾 28. View Passengers (Management View — Paginated & Filterable Search)  

<details> 
  <summary><strong>GET</strong> <code>/management/passengers</code></summary>

#### 🛠 Endpoint Summary   
**Method:** GET  
**URL:** /management/passengers    
**Authorized Roles:** Management/ADMIN    
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)  

#### 📝 Description  

This API functions as the centralized passenger directory for the system’s management layer, providing authorized Admin-level users with full visibility into all registered and guest passengers. It is purpose-built for operational dashboards, administrative audits, user-behavior analysis, fraud detection, and large-scale data intelligence workflows. Designed for high-volume environments, the API ensures predictable and secure execution through strict validation, typed parsing, and deterministic request handling.  

The endpoint supports rich passenger inspection with optional booking activity, backed by stable pagination, multi-field sorting, and a precise prefix-based search engine. Its structured DTO responses prevent overexposure of internal models while maintaining clarity and performance for high-volume environments. A major strength of this API is its collision-free filtering engine, which guarantees that every request resolves to exactly one definitive query path. This ensures consistent performance, avoids ambiguity, and maintains predictable runtime behavior even under heavy administrative workloads. Access is strictly restricted — only Management level authorities are permitted; passenger-level users are fully blocked to protect sensitive operational data.  

Overall, the API delivers high integrity, strong observability, and operational efficiency, making it an essential component for system-wide monitoring, audits, and intelligence-driven workflows.    

Key Features:
- **Full pagination** structure using `ApiPageResponse` for scalable navigation across large passenger datasets.
- Deterministic filter resolution (one code path, one repo call)
- Precise **prefix-based search engine** enabling fast, unambiguous lookups.
- Multi-field sorting for flexible administrative analysis.
- **Collision-free filtering engine** ensuring deterministic backend behavior.
- Strong validation rules including regex, prefix enforcement, and typed parsing.
- **Rich nested DTO** responses that protect internal models while presenting clean, structured data.
- Optimized for audits, behavior monitoring, fraud detection, support operations, and system intelligence initiatives.  

#### 🔍 Search & Filter Logic Summary  

This endpoint supports **two execution modes**, each deterministic and conflict-free:  

**1. Default Mode — No Keyword**  

If `keyword` is absent, the API returns all passengers:   
- Sorted by the chosen sort field
- Paginated according to `page` and `size`
- Cleanly shaped response using `ManagementAppUserDataDto`
- If the page has no results, returns **404 NOT_FOUND**  

**2. Keyword-driven Mode — Prefix-based Search Engine**   

**Supported Prefixes & Regex Patterns**   
| Pattern / Prefix | Meaning         | Example                    |
| ---------------- | --------------- | -------------------------- |
|  **id_**         | Passenger ID    | `id_42`                    |
|  **mobile_**     | Mobile number   | `mobile_9876543210`        |
| **user_**        | Passenger Name  | `user_John Doe`            |
| **Email Regex**  | Passenger Email | `john@demo.com`            |
| **Gender Regex** | Passenger Gender| `MALE`, `FEMALE`           |
| **Role Regex**   | Passenger Role  | `USER`, `GUEST`            |

**Why Prefix Search?**  

Prefixes eliminate:    
- Collisions between plural search types.
- Fuzzy interpretations.
- Cross-branch conflicts (e.g., mobile vs. ID)
- Unpredictable repository call chains  
- Only **one** branch is ever chosen — guaranteeing deterministic execution.  

#### 📥 Query Parameters  
| Parameter   | Type    | Default | Description                                                            | Required |
| ----------- | ------- | ------- | ---------------------------------------------------------------------- | -------- |
| **page**    | Integer | 1       | Page index (must be ≥ 1).                                              | No       |
| **size**    | Integer | 10      | Page size (must be ≥ 1).                                               | No       |
| **sortBy**  | String  | `id`    | Sort field. Allowed: `id`, `name`, `mobile`, `gender`, `email`, `role` | No       |
| **sortDir** | String  | `ASC`   | Sorting direction (`ASC` / `DESC`).                                    | No       |
| **keyword** | String  | –       | Prefix or regex-based search input.                                    | No       |


#### ⚙️ Backend Processing Flow  

**1. Centralized Input Validation & Safety Gate**  

All requests pass through a strict validation layer `PaginationRequest.getRequestValidationForPagination()`, checking:  
- `page` ≥ 1, size ≥ 1
- `sortBy` must match a controlled whitelist
- `sortDir` must match `ASC/DESC`
- `Keyword` must pass **prefix/regex rules**  
 
This ensures:   
- No invalid offsets
- No negative pagination
- No ORDER-BY or injection risks
- No unindexed field queries   
 
Invalid input returns **400 BAD_REQUEST** status code with message & timestrap immediately in a proper structure using `ApiResponse`.   

**2. Default Retrieval (No Keyword Supplied)**  

When no keyword is provided:  
- The full booking list is fetched.
- Pagination and sorting are applied.
- Results are mapped using `ManagementAppUserDataDto`.
- The page is wrapped in an `ApiPageResponse`, which is then wrapped in an `ApiResponse` and returned by the controller.  

If the resulting page is empty, return **404 NOT_FOUND** with an empty list and page-level metadata.  

**3. Identity Filters (ID, Mobile, Email, User Name)**    

These filters target unique or identity-bearing attributes of a passenger:  
- **Passenger ID (id_)** — Extracts the numeric identifier, validates it as a positive integer, and retrieves the user via `findById(...)`. Invalid IDs return **400**, and non-existent entries return **404**.
- **Mobile Number (mobile_)** — Enforces a strict India-format regex (starting with 6/7/8/9). Once validated, the system resolves the user through `findByMobile(...)`.
- **User Name (user_)** — Accepts only alphabetic characters and spaces, ensuring clean, human-readable identity resolution. Fetches records via `findByName(...)`.
- **Email (Regex)** — Uses an RFC-compliant email pattern to ensure **standards-based email validation**.
Queries resolve via `findByEmail(...)`.  

**4. Classification Filters (Gender, Role)**   

These filters operate on controlled enumerations and structured domain characteristics:  
- **Gender (MALE / FEMALE / OTHERS)** — Parsed and validated through ParsingEnumUtils.getParsedEnumType, ensuring type-safe input. Results are fetched using `findByGender(...)`.
- **Role (GUESR / USER)** — Follows the same enum-validation pipeline before querying backend data via `findByRole(...)`.    

**5. Response Structure**  

Upon successful processing, the API returns an HTTP **200 OK** response using the `ManagementAppUserDataDto`. This DTO provides a consolidated administrative view of each passenger, enriched with their booking activity to support audits, behavioral analysis, and system-wide monitoring. The payload is encapsulated within `ApiPageResponse` to include pagination metadata, ensuring smooth table rendering and scalable navigation across large passenger datasets.  

**Response Includes (via `ManagementAppUserDataDto`):**  
- **PassengerInfo**  Delivers the complete administrative identity profile of the passenger. It includes core user attributes such as ID, name, email, mobile number, gender, and role, along with operational metadata like profile status (Completed / Pending), account creation timestamp, and the last profile update. Additionally, the DTO exposes `totalBookingsCount`, enabling administrators to quickly assess engagement levels, behavioral patterns, and historical activity.
- **BookingInfo (List)**  Provides an optional, flattened view of the passenger’s booking history. Each entry contains essential booking attributes, including: ID, bus info, seat count, ticket & transaction identifiers, booking & payment statuses and finalCost. This offers administrators full traceability of a passenger’s travel activity, allowing them to analyze travel behavior, resolve disputes, verify payments, and perform fraud checks.  

**Design Intent**  
- The DTO is engineered to merge passenger identity data with booking-level insights, creating a **single, management-optimized response model** suitable for dashboards, audits, analytics, and support operations.
- Its layered, modular design ensures clarity, extensibility, and forward compatibility — enabling future fields or metadata to be introduced with zero impact to existing consumers & avoid overexposure of internal booking models while still providing a complete operational snapshot.   


#### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![Management Passenger's info View Success]()
</details>   

#### ❗ Error Response 
> Invalid pagination inputs  
<details> 
  <summary>View screenshot</summary>
   ![Management Passenger's info View Error]()
</details>  

> Invalid Keyword Format    
<details> 
  <summary>View screenshot</summary>
   ![Management Passenger's info View Error]()
</details> 

> Unauthorized Access     
<details> 
  <summary>View screenshot</summary>
   ![Management Passenger's info View Error]()
</details>  

#### 📊 HTTP Status Code Table  
| Code    | Status       | Meaning           | When Triggered             |
| ------- | ------------ | ----------------- | -------------------------- |
| **200** | OK           | Success           | Valid query, data returned |
| **400** | BAD_REQUEST  | Validation failed | Invalid pagination/keyword |
| **404** | NOT_FOUND    | No results        | Page empty / no matches    |
| **401** | UNAUTHORIZED | Auth failure      | Token missing/invalid      |
| **403** | FORBIDDEN    | Access denied     | Non-admin access           |

#### ⚠️ Edge Cases & Developer Notes  

**1. Deterministic Prefix Architecture & Collision-Free Identity Namespace**    
- The passenger search engine employs an explicitly segmented, prefix-governed namespace (id_, mobile_, email_, user_, gender, role). Each prefix corresponds to a uniquely isolated execution branch, guaranteeing that a keyword is never reinterpreted across multiple domains.
- Regex-bound namespace validation ensures malformed postfixes (e.g., id_xyz, mobile_12345abc, user_John#, invalid enum names) are rejected before routing, preventing accidental overlap between search channels.
- This deterministic design eliminates **multi-branch collisions**, ambiguous parsing, and fall-through logic. Even under complex or near-collision input patterns, the engine consistently resolves **exactly one search path**, ensuring predictable behavior for audits, dashboards, and operational analytics.  

**2. Numeric, Textual & Semantic Validation Integrity**  

All identity fields undergo strict validation to protect repository layers and preserve input correctness:  
- **ID values** must be strictly positive integers; zero, negatives, or alphanumeric hybrids are rejected.
- **Mobile numbers** are validated against a hardened India-format regex (starts with 6/7/8/9, fixed 10 digits), eliminating partial numbers and carrier-invalid sequences.
- **Email addresses** are enforced through RFC-compliant patterns to prevent syntactic drift and ensure canonical match behavior.
- **User names** follow a controlled character policy (alphabets + spaces), avoiding injection vectors, numeric contamination, and symbolic noise.
- **Enum-based fields** (gender, role) undergo typed parsing to ensure semantic correctness and case-normalized consistency.  

Invalid inputs trigger immediate **400 BAD_REQUEST**, guaranteeing that malformed data never reaches repository execution or indexing layers.   

**3. Deterministic Repository Resolution & Query-Path Stability**  

The entire filtering engine follows a **one-prefix → one-branch → one-repository-method** execution model. There are no dynamic predicates, reflection-based resolvers, or runtime-generated specifications. This ensures:  
- Strictly stable repository invocation
- Index-aligned lookups
- No accidental full-table scans
- Fully traceable query paths for audits  

Branch isolation guarantees that once a prefix validates, no secondary rule can override, reinterpret, or mutate the selected query semantics. Invalid formats fail during preprocessing, before any database interaction, protecting both performance and backend observability.   


**4. DTO Data Exposure Boundaries, Privacy Discipline & Schema Abstraction**  

`ManagementAppUserDataDto` is intentionally hardened to expose only high-level administrative attributes:  
- No passwords
- No token metadata
- No internal audit columns
- No database identifiers or relational wiring
- No persistence-layer structures  

The DTO provides only what administrators require: passenger identity, profile metadata, booking summaries, and operationally relevant fields. This prevents schema leakage, protects sensitive identity information, and maintains a stable contract even as internal entity structures evolve.  

**5. Defensive Error Modeling & Safe Failure Boundaries**  

All failure responses are designed to be:  
- Explicit about the user-facing cause.
- Free from internal stack traces
- Context-aware (e.g., invalid page index, missing passenger ID, malformed email)
- Aligned with REST semantics (400 vs 404 vs 200)  

This ensures clients receive actionable feedback without revealing backend method names, SQL constructs, or exception internals. Such guards prevent debugging noise, reduce support overhead, and maintain strong operational safety under erroneous inputs.  


**6. Extensible, Modular & Future-Proof Filter Architecture**  

The filtering design is intentionally modular:  
- Adding a new search capability requires **one prefix, one validation rule, one isolated branch**, and **one repository method**.
- No existing logic is affected, because namespace boundaries are **collision-free and deterministic**.
- DTO expansion is forward-compatible, allowing additional fields or **nested metadata** without breaking current consumers.   

This architecture supports long-term scalability, predictable maintenance, and enterprise-level observability—ensuring the system remains resilient as new search patterns, roles, or administrative insights evolve.   
</details>    


### 🧾 29. View Bus Statistics (Management View — Paginated, Filterable & Bus Performance Report)  

<details>  
  <summary><strong>GET</strong> <code>/management/stats/buses</code></summary>

#### 🛠 Endpoint Summary   
**Method:** GET  
**URL:** /management/stats/buses    
**Authorized Roles:** Management/ADMIN    
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)   

#### 📝 Description  

This API is the authoritative management-level reporting endpoint for the Bus module. It provides comprehensive statistical insights for all buses within a given date range, including operational usage and financial metrics. Specifically, it aggregates:  
 - Total bookings per bus
 - Total revenue per bus
 - Occupancy percentage (% of seats booked)
 - Availability percentage (% of seats unbooked)   

 Designed for management dashboards, financial audits, and operational analysis, this API serves as the **single source of truth** for Bus module performance. The API leverages a **JOIN query on the Bus and Booking entities** in the repository layer to efficiently compute aggregated statistics directly in the database. This ensures high performance even for large datasets and eliminates the need for costly in-memory calculations at the service layer.   

The design intentionally calculates percentages, counts, and sums **at the repository/JPQL level**, because the Booking entity contains transactional data (`finalCost`, `bookedAt`), whereas the Bus entity contains static metadata (`capacity`, `availableSeats`). This approach allows precise, atomic aggregation and avoids DTO mapping inconsistencies. By returning a structured `BookedBusReportDto`, the API exposes exactly the fields management needs, without overexposing internal entities, maintaining both performance and security.  

Key Features:  
 - **Date-based filtering:** Fetch statistics within a `startDate` and `endDate` window.
 - **Category filtering (AC / Non AC):** Allows segmentation of buses by AC type for operational analysis.
 - **Full pagination support** using `PaginationRequest` and `ApiPageResponse` utility classes.
 - **Multi-field sorting** (`totalBookings`, `totalRevenue`, `occupancy`, `availability`).
 - **Repository-level aggregation:** `COUNT`, `SUM`, and `FLOOR` computations are executed in **JPQL queries** for deterministic performance.
 - **Typed DTO mapping:** Manual constructor ensures computed fields (percentages, sums) are correctly set.
 - **Strict validation:** Dates, pagination, sorting, and category inputs are validated for correctness.    

#### 🔍 Query & Filter Logic Summary   

**1. Default Mode — No Category Specified**   

When no `category` is provided, the API retrieves all buses with bookings made between `startDate` and `endDate`.
- Pagination and sorting are applied via PaginationRequest.
- Repository method executed: `findByBookedBusData(startDate, endDate, pageable)`
- The query performs JOIN operations between **Bus** and **Booking**, computing aggregated metrics such as:
    - `totalBookings` → `COUNT(bk.id)`
    - `totalRevenue` → `SUM(bk.finalCost)`
    - `occupancy`% and `availability`% derived from bus capacity and booked seats
- Results are projected directly into `BookedBusReportDto` for a lightweight, structured response.
- If the result page has no entries → **404 NOT_FOUND**.  

**2. Category-Driven Mode — AC / Non-AC Filtering**  

When a `category` is absent, the value is strictly validated against the `AcType` enum using `ParsingEnumUtils.getParsedEnumType`. if any Invalid values → **400 BAD_REQUEST**.  

Upon successful validation:
- Repository method executed: `findBookedBusReportByAcType(startDate, endDate, acType, pageable)`.
- Aggregation logic remains identical to Default Mode, but restricted to the specified AC type.
- If the filtered page has no data → **404 NOT_FOUND** with clear category context.  

This guarantees predictable, partitioned query paths without dynamic branching or ambiguous routing.  

**Supported Keyword & Patterns**  
|    Pattern          | Meaning                      | Example                                    |
| ------------------- | ---------------------------- | ------------------------------------------ |
| **startDate RegEx** | Valid date formats only      | `2024-11-10` / `10-11-2024` / `10/11/2024` |
| **endDate RegEx**   | Valid date formats only      | `2024-12-01` / `10-11-2024` / `10/11/2024` |
| **AC_Type RegEx**   | AC type domain (AC / NON_AC) | `AC`, `NON_AC`                             |  



#### 📥 Query Parameters  
|   Parameter   | Type    | Default       | Description                                                                  | Required |
| ------------- | ------- | ------------- | ---------------------------------------------------------------------------- | -------- |
| **page**      | Integer | 1             | Page index (must be ≥ 1)                                                     | No       |
| **size**      | Integer | 10            | Page size (must be ≥ 1)                                                      | No       |
| **sortBy**    | String  | `totalBookings` | Sorting field. Allowed: `totalBookings`, `totalRevenue`, `occupancy, `availability` | No |
| **sortDir**   | String  | `DESC`          | Sorting direction: `ASC` or `DESC`                                               | No |
| **startDate** | String  | –             | Start date of booking window (05-12-2025 or 05/12/2025 or 2025-12-05)        | No       |
| **endDate**   | String  | –             | End date of booking window (05-12-2025 or 05/12/2025 or 2025-12-05)          | No       |
| **category**  | String  | –             | Bus AC type filter case-insensitive (`AC` or `Non AC`)                       | No       |


#### ⚙️ Backend Processing Flow  

**1. Centralized Input & Pagination Validation**  

All request-driven structural inputs—`page`, `size`, `sortBy`, `sortDir`—are validated through `PaginationRequest.getRequestValidationForPagination()`, which performs:
 - Boundary validation on `page`/`size`.
 - Whitelisting of allowed sorting fields (`totalBookings`, `totalRevenue`, `occupancy`, `availability`).
 - Strict direction enforcement (`ASC`/`DESC`).
 - Prevention of illegal or injection-prone sort values.

Any violation triggers **400 BAD_REQUEST**, before the system ever reaches service or repository layers.
This ensures both safety and deterministic pagination behaviour.   

**2. Temporal Input Integrity & Strict Date Normalization**  

Date inputs (`startDate`, `endDate`) undergo a two-stage validation pipeline:  

**A. Syntactic & Format Screening**  

`RequestParamValidationUtils.listOfErrors()` validates both date strings from the request. It detects:
- Missing start or end date
- Both dates missing
- Malformed formats using centralized regex pattern.
- Non-parseable date strings (not matching the allowed patterns)

This method returns a list of descriptive error messages based on the issues found.  

**B. Logical Parsing and Canonicalization**  

`DateParser.getBothDateTime()` converts raw strings into normalized LocalDateTime objects, internally handling:
- Strict parsing against supported patterns.
- Validation of chronological boundaries.
- Rejection of ambiguous or invalid date combinations.  

Any parsing deviation yields **400 BAD_REQUEST** with precise diagnostic messaging. Only canonicalized dates progress to the service layer.  

**3. Optional Category (AC / NON_AC) Validation**  
If a category filter is provided:
- It is first matched against `RegExPatterns.AC_TYPE_REGEX`.  
- Then mapped into the typed `AcType` enum using `ParsingEnumUtils.getParsedEnumType()`  

Invalid semantic or syntactic values fail early with 400 BAD_REQUEST, guaranteeing that only valid AC types reach repository query generation. This prevents malformed predicates and ensures stable query selection.    

**4. Deterministic Repository Execution & Inline Aggregation**   

Once validation succeeds, the service resolves a **single deterministic repository path** based on the presence of the category filter:  
- **Default Execution Path — No Category** — `findByBookedBusData(startDate, endDate, pageable)` This method retrieves all buses with bookings inside the provided date range and computes their aggregated statistics.
- **Category-Specific Execution Path** — `findBookedBusReportByAcType(startDate, endDate, acType, pageable)` This variant applies an additional predicate on b.acType, ensuring the results are segmented by the requested bus category.  

**Database-Level Aggregation & JOIN Strategy**  
Both repository methods share the same analytical structure:   
- **Explicit JOINs** between `Bus` and its associated `Booking` records.
- **Inline computation** of statistical metrics:
   - `COUNT(bk.id)` → total bookings
   - `SUM(bk.finalCost)` → total revenue
   - occupancy percentage based on `(capacity - availableSeats)`
   - availability percentage based on `availableSeats`  
- **GROUP BY** applied at the bus level to ensure accurate roll-ups.
- **Constructor-based projection** into `BookedBusReportDto` for immediate shaping of the response payload.  

This design ensures that all heavy computation occurs directly inside the database, producing:
- Minimal memory usage in the service layer.
- Stable performance even under high data volumes.
- Predictable query execution paths without runtime dynamic filtering.    

As a result, the service receives a **fully aggregated, analytics-ready dataset** that requires no further transformation.   

**5. Service-Layer Result Construction & Page-Oriented Semantics**  
The service transforms the query results into an `ApiPageResponse`, encapsulating: aggregated DTO results (by using mapper), total pages, total elements, current page index, page size, first-page / empty-state flags.   

A page with no content results in a **404 NOT_FOUND**, with contextual messaging (e.g., “page 3”, “given category”). This ensures precise feedback for UI analytics dashboards and pagination-driven clients.  

**6. Controller-Level Response Orchestration**  
The controller consolidates all upstream validations and service results into consistent HTTP outputs:  
- **200 OK** for successful analytics retrieval.
- **400 BAD_REQUEST** for validation or semantic failures.
- **404 NOT_FOUND** for valid requests that contain no data.  

Responses are delivered via the unified `ApiResponse` format, ensuring consistent structure across the system's management endpoints.   


#### 📤 Success Response  
<details> 
  <summary>View screenshot</summary>
   ![Management Bus Stats info View Success]()
</details>   

#### ❗ Error Response 
> Invalid pagination inputs  
<details> 
  <summary>View screenshot</summary>
   ![Management Bus Stats info View Error]()
</details>  

> Invalid/Malformed Date Format
<details> 
  <summary>View screenshot</summary>
   ![Management Bus Stats info View Error]()
</details> 

> Invalid Category (not AC/NON_AC)     
<details> 
  <summary>View screenshot</summary>
   ![Management Bus Stats info View Error]()
</details>  

> Unauthorized Access     
<details> 
  <summary>View screenshot</summary>
   ![Management Bus Stats info View Error]()
</details>  

> Non-admin Access     
<details> 
  <summary>View screenshot</summary>
   ![Management Bus Stats info View Error]()
</details>  

#### 📊 HTTP Status Code Table  
|   Code   | Status       | Meaning                     | When Triggered                                 |
| -------- | ------------ | --------------------------- | ---------------------------------------------- |
| **200**  | OK           | Data successfully retrieved | Valid request, non-empty page                  |
| **400**  | BAD_REQUEST  | Validation failed           | Invalid page, sortBy, sortDir, category, date  |
| **404**  | NOT_FOUND    | No data found               | Empty page / no buses in date range / category |
| **401**  | UNAUTHORIZED | JWT token missing/invalid   | Authentication failure                         |
| **403**  | FORBIDDEN    | Access denied               | Non-admin access                               |


#### ⚠️ Edge Cases & Developer Notes  

**1. Strict Pagination, Sorting, and Metric-Safety Enforcement**  

This API uses `PaginationRequest.getRequestValidationForPagination(...)` to validate page, size, sort field, and sort direction before any query is executed. Key protections include:
- `sortBy` restricted to **metrics only** (`totalBookings`, `totalRevenue`, `occupancy`, `availability`).
- Prevents sorting on non-aggregated columns, which would otherwise break **JPQL constructor** queries.
- Rejects invalid sorting direction (only `ASC`/`DESC`).
- Avoids malformed page/size values that could trigger large-offset scans.

 These checks eliminate accidental high-cost queries and maintain deterministic repository behavior.  

 **2. Hardened Date Validation & Range Normalization**  
 
 Requested date inputs (`startDate`, `endDate`) pass through two independent validation layers:  
 1. **Syntactic checks** via `RequestParamValidationUtils.listOfErrors()` to perform basic validation for the requested input String.
 2. **Semantic parsing** via `DateParser.getBothDateTime()` → validate & parse the requested String date to precise `LocalDateTime`.   

This prevents:   
- Invalid date formats.
- Logically reversed date ranges.
- Missing/partial dates.
- Database full-scan scenarios caused by NULL or malformed parameters.  

Errors are returned with **exact cause messages**, ensuring clarity during client-side debugging.   

**3. Controlled Category Filtering & Enum Parsing Fail-Safe**  

If a category (Ac / Non Ac) is provided, the system applies:
- Centralized regex pattern `AC_TYPE_REGEX` for structural validation.
- Then using enum parsing utils `ParsingEnumUtils.getParsedEnumType()` for enum-level safety.
- This prevents:
    - Case mismatches
    - Invalid AC categories
    - String injection attempts
    - Query corruption from uncontrolled predicates   

Any deviation results in an immediate **400 BAD_REQUEST**, before repository execution.   

**4. Deterministic Repository Resolution — Zero Ambiguity**  

The service enforces a **single, unambiguous execution path:**  
- Without category → `findByBookedBusData(...)`
- With valid category → `findBookedBusReportByAcType(...)`  

This guarantees: 
 - No fallback logic.
 - No multi-branch evaluation.
 - No dynamic query building.   

**5. Database-Driven Aggregation & High-Volume Performance Stability**    

Both repository queries:  
- Used JOIN `Bus` ↔ `Booking` to perform at DB level.
- Compute **COUNT, SUM**, and percentage math inside the DB.
- Use **constructor-based projection** to return `BookedBusReportDto` directly.
 
This design prevents:
    - High-memory dataset processing in the service layer.
    - Repeated hydration of full entity graphs.
    - Unnecessary streaming or filtering in Java.   

The DB returns a **fully aggregated, lightweight dataset**, optimized for dashboard-scale analytics.

**6. DTO Privacy & Controlled Exposure**    

I designed this DTO: `BookedBusReportDto` that exposes only computed operational metrics:
- No entity identifiers beyond what is required.
- No internal audit fields.
- No relationships or sensitive attributes.
- No raw capacity or available seat counts.  

All percentages are formatted (`"75%"`), ensuring a **clean, management-ready response** without leaking structural or relational metadata.     

**7. Predictable Error Modeling & Zero Internal Leakage**   

Every failure—pagination, date parsing, category validation, repository NOT_FOUND—returns:
- Structured `ApiResponse`
- Domain-readable messages
- **No stack traces**
- **No repository or SQL hints**   

This keeps operational logs clean and prevents accidental exposure of internal behavior to API consumers.  
</details>   

### 🧾 30. View Passengers Statistics (Management View — Paginated, Filterable & Passenger Report)  

<details> 
  <summary><strong>GET</strong> <code>/management/stats/passengers</code></summary>  

#### 🛠 Endpoint Summary   
**Method:** GET  
**URL:** /management/stats/passengers      
**Authorized Roles:** Management/ADMIN    
**Authentication:** JWT Required (See **“Authentication & JWT Usage”** in Technical Architecture)   

#### 📝 Description  

This API serves as the definitive management-level reporting endpoint for the **Passenger / AppUser module**. It provides a comprehensive, per-user statistical view of booking activity within a specified date range, aggregating:  
- Total bookings (`totalBookings`)
- Total revenue generated (`totalRevenue`)
- Most recent booking timestamp (`recentBookedAt`)

Additionally, the API supports optional **gender** and **role** filters, allowing management to segment users by demographic or user type (e.g., Guest vs User, Male vs Female).    

Designed for dashboards, operational audits, and executive oversight, the API ensures a **single source of truth** for passenger-level booking analytics. Data aggregation occurs directly in the repository via **JPQL constructor projections** (`BookedAppUserReportDto`) on the `AppUser` and `Booking` entities, providing:   
- **Deterministic, high-performance aggregation** without memory-intensive service-layer processing.
- **Accurate computation** of total bookings, revenue, and latest booking timestamp.
- **Separation of entity internals** from API-facing DTOs.
- **Safe, strict filtering** by gender and role through enum validation.  

The API fully supports **pagination** and **multi-field sorting**, ensuring scalable, real-time analytics across thousands of passengers while maintaining consistent UI behavior.  

Key Features:  
- **Date-based filtering:** Retrieve statistics within `startDate` and `endDate`
- **Optional demographic filters:** Filter by `gender` and/or `role`.
- **Full pagination & sorting:** Powered by `PaginationRequest` and `ApiPageResponse`
- **Repository-level aggregation:** `COUNT`, `SUM`, `MAX` executed via **JPQL for deterministic performance**.
- **Sorting fields:** Includes `totalBookings`, `totalRevenue`, `recentBookedAt`.
- **DTO mapping:** `BookedAppUserReportDto` provides formatted, lightweight output.
- **Strict validation:** Enforces correct dates, pagination, sorting, gender, and role inputs.
- **Robust error handling:** Clear messaging for empty results or invalid input.    

#### 🔍 Search & Filter Logic Summary  

**1. Default Mode — No Gender / Role Provided**  

When no other filters were provided, the API retrieves all passengers with bookings made between `startDate` and `endDate`.
- Pagination and sorting are applied via `PaginationRequest`.
- Repository method executed: `findByBookedAppUserData(startDateTime, endDateTime, pageable)`
- The query performs JOIN operations between `AppUser` and `Booking`, computing aggregated metrics such as:
    - `totalBookings → COUNT(bk.id)`
    - `totalRevenue → SUM(bk.finalCost)`
    - `recentBookedAt → MAX(bk.bookedAt)`
- Results are projected directly into `BookedAppUserReportDto` for a lightweight, structured response.
- If the result page has no entries → **404 NOT_FOUND**.  

**2. Category-driven Mode — Gender & Role-based Search Engine**   

**Supported Prefixes & Regex Patterns**   
| Pattern / Prefix | Meaning         | Example                    |
| ---------------- | --------------- | -------------------------- |
| **Gender Regex** | Passenger Gender| `MALE`, `FEMALE`           |
| **Role Regex**   | Passenger Role  | `USER`, `GUEST`            |  

#### 📥 Query Parameters  
|   Parameter   | Type    | Default       | Description                                                           | Required |
| ------------- | ------- | ------------- | --------------------------------------------------------------------- | -------- |
| **page**      | Integer | 1             | Page index (must be ≥ 1)                                              | No       |
| **size**      | Integer | 10            | Page size (must be ≥ 1)                                               | No       |
| **sortBy**    | String  | `totalBookings` | Sorting field. Allowed: `totalBookings`, `totalRevenue`, `recentBookedAt`   | No       |
| **sortDir**   | String  | `DESC`          | Sorting direction: `ASC` or `DESC`                                        | No       |
| **startDate** | String  | –             | Start date of booking window (05-12-2025 or 05/12/2025 or 2025-12-05) | No       |
| **endDate**   | String  | –             | End date of booking window (05-12-2025 or 05/12/2025 or 2025-12-05)   | No       |
| **gender**    | String  | –             | Optional gender filter: `male` / `female`                                 | No       |
| **role**      | String  | –             | Optional role filter: `USER` / `GUEST`                                    | No       |










  
</details> 


