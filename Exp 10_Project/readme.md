# 🌍 YatraSathi - Spring Boot Backend

YatraSathi is a travel companion application designed to simplify trip planning, destination discovery, and travel inspiration.  
This backend, built using **Spring Boot**, provides secure user authentication, integrates with **Amadeus APIs** for travel data, and supports **Cloudinary** for image uploads.

---

## ⚙️ Tech Stack

- **Spring Boot 3.x**
- **Spring Security** with JWT Authentication
- **Spring Data JPA** (Hibernate)
- **MySQL / PostgreSQL**
- **Cloudinary API** – for image uploads
- **Amadeus API** – for travel data integration
- **Maven** – for dependency management

---

## 🔐 Key Features

✅ User Authentication using JWT (Register, Login, Protected Routes)  
✅ Travel data fetching via Amadeus API  
✅ Gallery management (upload, fetch, delete images) using Cloudinary  
✅ RESTful API structure for easy integration with Flutter frontend  
✅ Secure endpoints with role-based access control  

---

## 📂 Directory Overview

| Directory | Purpose |
|------------|----------|
| `model/` | Contains JPA entity classes like `User` and `Gallery` |
| `repository/` | Interfaces for database operations |
| `controller/` | REST API endpoints |
| `service/` | Business logic for authentication, travel, and image handling |
| `config/` | Contains Spring Security configuration |
| `security/` | Helper classes for JWT generation and validation |
| `resources/` | Application properties and configuration files |

