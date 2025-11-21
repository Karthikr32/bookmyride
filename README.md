# BookMyRide 🚌 - *A Full-Featured Bus Booking System*
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; BookMyRide is a robust bus booking system built with Java 21 and Spring Boot, following industry-standard clean MVC architecture to ensure modularity and maintainability. It leverages Java core OOP principles for clear separation of concerns across well-defined modules such as user authentication, booking, bus, and location management. The system implements secure, role-based access control, optimistic locking for concurrency, and comprehensive logging for critical actions. It supports guest and registered user bookings with pagination, sorting, and validation utilities, prioritizing data integrity and scalability. This design approach delivers a clean, scalable backend with reusable components, aligned to enterprise-grade development best practices.

## Tech Used:
**Tech Stack:** Java 21, Spring Boot 3.2, MySQL 8, Maven, JWT, Lombok, Postman  

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge) 
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge)
![JWT](https://img.shields.io/badge/JWT-Security-yellow?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-3.9.0-red?style=for-the-badge)
![Lombok](https://img.shields.io/badge/Lombok-1.18.28-blueviolet?style=for-the-badge)
![Postman](https://img.shields.io/badge/Postman-API%20Testing-orange?style=for-the-badge)

## Features:
- **Booking Module:** Allows guests and registered users to book or cancel buses with seat availability checks, optimistic locking, and validation. Only registered users can view their own bookings, while Management users can access all bookings with pagination and sorting.  
- **AppUser Module:** Handles both guest and registered users, including registration, authentication, profile management, and secure JWT-based login. Supports password changes and profile updates.  
- **Management Module:** Contains all authority-level users who manage data across other modules (Bus, Booking, MasterLocation). Implements role-based access control and audit logging.  
- **Bus Module:** CRUD operations for bus details, with validation, audit logging, and concurrency handling using optimistic locking.  
- **MasterLocation & Location Data:** Stores routes and structured location data (CityEntity, StateEntity, CountryEntity) to support booking and management operations, ensuring data integrity and proper mapping.  

✅ Total of 31 fully functional REST APIs across all modules.

