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
  <summary>🛠 PATCH: /management/change-password</summary>

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


#### ⚠️ Critical Notes & Security Flow
- This API requires a valid JWT from the latest login
- If the admin previously updated their profile and their username was regenerated, the old JWT becomes invalid — and this API will not work until re-login
- Old password is validated using Spring Security's authentication pipeline, giving maximum protection
- Ensures:
    - No one can change password using a stolen token
    - No change is possible without verifying the old password
    - Only the true authenticated, verified Admin can modify credentials
</details>


### 🔐 4. Get Management Profile

<details>
  <summary>🛠 GET: /management/profile </summary>

#### 📝 Description
- Fetches the **currently authenticated Management/Admin** user's profile information.
- This endpoint uses Spring Security’s `@AuthenticationPrincipal` to obtain the identity (UserPrincipal) of the logged-in Management user.
- It ensures that only **valid, authenticated, ADMIN-role** users can view their own profile.
- The API returns profile details as a **DTO**, hiding sensitive database fields such as password, internal identifiers, and security metadata.
- This is typically the first action an authenticated admin performs after updating profile or changing password.

#### 🔑 Roles Allowed
> MANAGEMENT / ADMIN (Authorization is internally assigned — NOT modifiable via profile/update API)

#### ❗ Error Responses
<details>
  <summary>View error response screenshot</summary>
    <br>
  ![Management Profile view Error]()
</details>  

#### 📤 Success Response
<details>
  <summary>View success response screenshot</summary>
  <br>
  ![Management Profile view Error]()
</details>  

#### 🔐 Security Notes
- This endpoint uses the same authentication & authorization rules. For detailed security behavior, refer to:  
🔗 [API #2 – Update Profile](README.md#2-update-management-profile)  
🔗 [API #3 – Change Password](README.md#3-change-password-management-user)  
- These include:
   - JWT validation
   - UserPrincipal sanity checks
   - ADMIN role enforcement
   - Token invalidation after username change 
</details>  

### 🗺️ 5. Create New Location (City + State + Country Hierarchy)
<details>
  <summary>🛠 POST: /bookmyride/management/locations</summary>

#### 📝 Description
- Allows the authenticated **Management/Admin user** to create a complete Location set:
     - **city**
     - **state**
     - **country**
- All three values are provided in a **single DTO**, and the backend ensures the correct creation or reuse of **hierarchical entities** using a strict validation + enum-parsing system.
- Backend validates & parses StateEnum and CountryEnum using your custom `ParsingEnumUtils`.
- Ensures uniqueness and avoids duplication in:
    - Main tables (City → State → Country)
    - **MasterLocation table** (string-based fast lookup)
- Automatically syncs the **MasterLocation** projection table after successful creation.
- The operation is fully protected with:
    - JWT authentication
    - ADMIN-role enforcement
    - DTO validation
    - Optimistic locking
    - MasterLocation projection sync

#### 🔑 Roles Allowed
> MANAGEMENT / ADMIN (JWT required — extracted & validated via `@AuthenticationPrincipal`)


#### 📥 Request Body (Sample)
{  
&nbsp;&nbsp;&nbsp; "city": "Chennai",  
&nbsp;&nbsp;&nbsp; "state": "Tamil Nadu",  
&nbsp;&nbsp;&nbsp; "country": "India"  
}  

#### ⚙️ How the Backend Processes This
**1. Validates the input DTO**  
 - Empty/invalid values → return BAD_REQUEST
 - Parses `state` & `country` via `ParsingEnumUtils`
 - On failure → clean error response using `ServiceResponse` from service layer
   
**2. Performs strict duplication checks**  
 - Checks if the same (city + state + country) exists in the main tables
 - Checks the same combination exists in **MasterLocation**
 - On conflict → return 409 with appropriate message
  
**3. Resolves/creates hierarchical entities**  
 - **Country**
     - If exists → reuse
     - Else → create new
 - **State**
     - If exists under the chosen country → reuse
     - Else → create new 
 - **City**
     - If exists under the chosen state → return error
     - Else → create new CityEntity
      
**4. Syncs MasterLocation table**  
 - Adds a new projection entry:  
    `city (String), state (String), country (String)` 
 - Used for fast GET queries and user input validation.
  
**5. Handles optimistic locking & transactional safety**  
 - Wrapped inside `@Transactional`
 - If concurrency conflict occurs
     - Throws: `ObjectOptimisticLockingFailureException` or `OptimisticLockException`
     - Controller maps this to **409 Conflict**   

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

#### ⚠️ Important Notes
- This API is **strictly protected** under ADMIN role
- Enum parsing ensures that only valid, controlled master values enter the system
- **CityEntity ID** returned in response is used for:
    - Update Location
    - Delete Location
 
- `MasterLocation` is **not** a CRUD-exposed table — only auto-maintained.
- Ensures perfect consistency for:
    - Bus route creation
    - User bus search validation
    - Backend indexing & performance  
</details>  

### 🗺️ 6. Bulk Create Locations (City + State + Country Hierarchy)
<details>
  <summary>🛠 POST: /bookmyride/management/locations/list</summary>

#### 📝 Description
- Allows the authenticated **Management/Admin** user to insert **multiple locations** in a single request.
- Each item in the list follows the same validation and hierarchical creation logic used in the single-location API (API #5).
- The backend processes **each entry independently** inside a loop — meaning one invalid location **does not stop** the others from being processed.
- For every DTO, the service records the outcome using clearly defined prefixes:
   - SUCCESS: Location inserted successfully
   - DUPLICATE_ENTRY: Conflict found (main tables or MasterLocation)
   - ERROR: Unexpected issue or server-side failure 
- At the end of processing, the backend:
   - Counts successful inserts
   - Counts failures
   - Returns a 201 status with a consolidated summary + full result list
- This API is especially useful during initial data setup or location migration phases.

#### 🔑 Roles Allowed
> MANAGEMENT / ADMIN (JWT required — validated via `@AuthenticationPrincipal`)

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
> 💡 Each object in the list uses the same DTO rules as the single-location API.


#### ⚙️ How the Backend Processes This (Step-by-Step)
**1. Validates List Input**  
 - Checks for empty/null list
 - Each DTO validated individually
 - Enum parsing for state & country using your ParsingEnumUtils
 - If parsing fails → prefix result with:
   **duplicate**: or **error**: (based on reason)  


**2. Enhanced For-Loop Processing**  
- Perform **duplicate checks** (main tables + MasterLocation)
- Resolve or create:
   - `CountryEntity`
   - `StateEntity`
   - `CityEntity`
- Sync MasterLocation table at background
- Wrap each outcome as a string:
   - `"success: Chennai-Tamil Nadu-India added"`
   - `"duplicate: Mumbai-Maharashtra-India already exists"`
   - `"error: Unexpected issue while saving Delhi-Delhi-India"`  
 
**3. No Early Return / No Loop Breaks**  
- Every input is processed
- No failures stop the batch
- Ensures maximum data insertion rate  

**4. Final Aggregation**  
- After the loop, Make count of all “success:” & others based on prefix that get collected through a list.
- Build a consolidated ServiceResponse:
    - If all success → message: “All locations added successfully.”
    - If mixed → message: “X saved, Y failed.”
    - Include full result list for clarity  


**5. Concurrency + Transaction Handling**  
Inside each iteration:
   - Critical DB writes wrapped safely
   - Optimistic locking exceptions are caught and converted to "error:" messages
   - Bulk operation does not fail entirely due to one conflict


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


#### ⚠️ Important Notes
- This API **never stops midway** — designed for stability during large imports.
- The same **single-location validation & creation logic** (from the POST /management/locations API) is reused for each DTO, ensuring consistent behavior across all entries.
- All valid entries are synced into the MasterLocation table, ensuring fast and accurate location lookup during Bus Search and Management operations.
- **Status Codes**:
    - **201 (Created)** → When **all locations** are successfully inserted
    - **206 (Partial Content)** → When at least one location failed (duplicate/error), but others were saved successfully
</details>  


### 📍 7. View Location Records (Filter + Sorting + Pagination)
<details> 
  <summary>GET: /bookmyride/management/locations</summary>

#### 🛠 Endpoint Summary
**Method:** GET  
**URL:** /bookmyride/management/locations  
**Authentication:** Required (JWT)  
**Roles Allowed:** ADMIN  

#### 📝 Description
This endpoint allows management users to view, filter, sort, and paginate location records (City + State + Country).
- Supports optional filters: country, state, city, role.
- Supports pagination: page, size, sortBy, sortDir.
- Optimized for performance, with backend checks ensuring valid enum values for Country, State, and Role.  

**Edge-case behaviors:**  
- If an invalid enum value is passed → returns `400 BAD_REQUEST` with details.
- Pagination starts at `1` from client side; internally adjusted for zero-based indexing.
- Empty result sets return `404 NOT_FOUND` with proper page info.
- Role filtering validates against enum values and returns `400` if invalid.  


#### 📥 Query Parameters
| Parameter | Type   | Default | Description                                                                                   | Required |
| --------- | ------ | ------- | --------------------------------------------------------------------------------------------- | -------- |
| page      | Integer| 1       | Page number (1-based from client)                                                             | No       |
| size      | Integer| 10      | Number of items per page                                                                      | No       |
| sortBy    | String | id      | Field to sort by (`id`, `name`, `state.name`, `state.country.name`, `createdAt`, `updatedAt`) | No       |
| sortDir   | String | asc     | Sort direction (`asc` or `desc`)                                                              | No       |
| country   | String | -       | Filter by Country (starts with capital letter & must match with Country regEx pattern)        | No       |
| state     | String | -       | Filter by State (starts with capital letter & must match with State regEx pattern)            | No       |
| city      | String | -       | Filter by City (starts with capital letter)                                                   | No       |
| role      | String | -       | Filter by Creator Role                                                                        | No       |

> Example url to try: `/bookmyride/management/locations?page=2&size=5&country=India&sortBy=city&sortDir=desc`

#### ⚙️ Backend Processing Flow
**1. Validate Pagination Parameters**
- Handled by `PaginationRequest.getRequestValidationForPagination()`
- Validates `page`, `size`, `sortBy`, `sortDir`.
- Returns 400 if invalid.

**2. Validate Filters**  
- Country, State, City → regex validation
- Role → enum validation via `ParsingEnumUtils`  
  
**3. Repository Selection Logic**  
- Depending on which filters are provided, calls the correct JPA repository query (`findByNameAndState_NameAndState_Country_Name`, etc.)
- Supports partial or full filter combinations.  

**4. Pagination**  
- Converts validated params into `Pageable`
- Uses Spring Data JPA `Page<T>` for sorting + paging  

**5. Response Construction**  
- Maps `CityEntity` → `LocationResponseDto`
- Wraps results in `ApiPageResponse`
- Returns structured data with total pages, total elements, current page, size, isFirst, isEmpty  


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
| 400       | VALIDATION_FAILED | Bad Request           | Invalid ID / regex or enum fail               |


#### ⚠️ Edge Cases & Developer Notes
- Pagination page numbers are **1-based from client**, internally converted to 0-based.
- If multiple filters provided, all are validated **before DB query**.
- Invalid enum strings immediately return `400` (no DB hit).
- Even with empty results, API returns structured `ApiPageResponse` with empty content (not null).
- Query performance optimized for large datasets (~10k+ records).
- Role filter checks exact enum match; incorrect casing → 400.
   
</details>


### 📝 8. Update Location Records (City + State + Country Hierarchy)
<details> 
  <summary>PUT: /bookmyride/management/locations/{id}</summary>

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


#### ⚙️ Backend Processing Workflow (High-Density Version)
**1. Authorization & User Validation**  
- Validates JWT, Ensures authenticated user is ADMIN only
- Fetches management user via `UserPrincipalValidationUtils` for `updatedBy` tracking.  

**2. Preliminary Input Validation**  
- id must be a positive long.
- DTO undergoes Spring @Valid rules.
- Any violation → 400 with standardized ApiResponse.  

**3. Fetch Current Location Set**  

Loads the existing CityEntity (root) → retrieves linked StateEntity & CountryEntity.
If city not found → 404.  

**4. Enum Parsing & Syntax Validation**  
- Converts input state/country → Enum via `ParsingEnumUtils`  
- Validates name formats using strict RegEx.  
  If invalid → 400.  

**5. Duplicate Combination Check (Two-Level)**  

**Level 1:** Checks if (city, stateEnum, countryEnum) combo already exists in relational tables.    
**Level 2:** Checks same combo in MasterLocation.  
If either exists and it’s not the current record → **409 Conflict.**  


**6. MasterLocation Consistency Guard**  

Ensures MasterLocation entry for the current (old) city/state/country exists.  
If missing → marks data inconsistency → **404**.  

**7. Conditional Entity Updates**  

Each entity updates **only if changed**:  
  - If Country changed → update CountryEntity
  - If State changed → update StateEntity (linked to updated/new country)
  - If City changed → update CityEntity (linked to updated/new state)
Every update applies `updatedAt`, `updatedBy`, version increment.

**8. Secondary Duplicate Check for City Under State**  

When state changes: ensures no other City with same name exists in that state.
If conflict → **409**.  

**9. MasterLocation Synchronization**  

After updating relational entities:  
  - Updates MasterLocation with new city/state/country
  - Updates audit fields
  - Guarantees the consolidated table is always consistent  

**10. Optimistic Locking Protection**  

If another admin modified the location during update:
 - JPA throws `ObjectOptimisticLockingFailureException` or OptimisticLockException
 - Controller returns **409 MODIFIED_BY_OTHER**  

**11. Transaction Completion**  

Entire operation runs inside a single `@Transactional` block:  
- All changes commit only if every step succeeds
- Any exception → full rollback


#### 📤 Success Response
<details> 
  <summary>View screenshot</summary>
   ![Location Update Success]()
</details>

#### ❗ Error Response
<details> 
  <summary>View screenshot</summary>
   ![Location Update Success]()
</details>

#### HTTP Status Code Table
| HTTP Code | Status Name       | Meaning               | When It Occurs                                |
| --------- | ----------------- | --------------------- | --------------------------------------------- |
| 200       | SUCCESS           | Request succeeded     | Data found and returned successfully          |
| 400       | VALIDATION_FAILED | Bad Request           | Invalid ID / invalid DTO / regex or enum fail |
| 401       | UNAUTHORIZED      | Authentication Failed | Missing or invalid JWT                        |
| 404       | NOT_FOUND         | Resource Not Found    | CityEntity / MasterLocation entity missing    |
| 403       | FORBIDDEN         | Access Denied         | Only Authority users could modify             |
| 409       | CONFLICT          | Duplicate Entry       | New combination already exists / Optimistic lock conflict |
| 500       | INTERNAL_SERVER_ERROR | Unexpected Error | Unexpected server-side error              |


#### 🧪 Edge Case Rules (Refined)
- Case differences alone (e.g., "chennai" → "Chennai") do not trigger duplicates.
- State or Country change triggers cascaded re-evaluation of duplicates within new hierarchy.
- MasterLocation must always exist — no silent auto-create during update.
- Concurrent updates gracefully fail with explicit conflict messaging.
- Update operation is designed to be deterministic and free of side effects.
- Also, I faced more challenges while building this one, Let me describe that in **Challenge section.**
  
</details>











