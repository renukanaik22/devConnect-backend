Architecture flow 
+-----------------------------+
|           Client            |
|     (Postman / Frontend)    |
+--------------+--------------+
               |
               v
+-----------------------------+
|    Security Filter Chain    |
|   (JWT, Permit /auth/**)   |
+--------------+--------------+
               |
               v
+-----------------------------+
|        AuthController       |
|     (REST Endpoints)        |
+--------------+--------------+
               |
               v
+-----------------------------+
|          AuthService        |
|     (Business Logic)        |
+----+-----------+-----------+
     |           |
     |           |
     v           v
+-----------+   +------------------+
|  BCrypt   |   |   JWT Service    |
| Password  |   | Token Generate   |
|  Encoder  |   | & Validation     |
+-----------+   +------------------+
     |
     |
     v
+-----------------------------+
|       UserRepository        |
|   (MongoRepository)         |
+--------------+--------------+
               |
               v
+-----------------------------+
|           MongoDB           |
|      User Collection        |
+-----------------------------+


# 🚀 Spring Boot JWT Authentication API

A simple **Spring Boot Authentication System** built with:

* Spring Boot
* Spring Web
* Spring Security
* JSON Web Tokens (JWT)
* MongoDB Repository
* Tested using Postman

This project demonstrates the **core backend login flow**:
Register → Login → Validate → Generate JWT → Return Token.

---
What is JWT token? when to use?
JWT is a secure, stateless way to authenticate users.
After user logs in once, server returns a signed token containing their identity and role.
For every request, client sends the token.
Server verifies signature instead of checking database every time.
Used widely in REST APIs, mobile apps, SPAs, and microservices.

## 📌 Features

### ✅ User Registration (`POST /register`)

* Accepts name, email, password, role
* Stores user in **MongoDB**

### ✅ User Login (`POST /auth/login`)

* Validates email + password
* Generates a signed **JWT token** containing:

    * email
    * role
    * issuedAt
    * expiration

### ✅ JWT Implementation

* Secure **HS256** algorithm
* Uses `jjwt` library
* Implements custom `JwtService`
* Includes proper cryptographic key generation

### ✅ Spring Security Integration

* Custom `SecurityConfig` using `SecurityFilterChain`
* CSRF disabled (for API + Postman testing)
* Public endpoints allowed:

    * `/register`
    * `/login`
* All other endpoints can later be secured with JWT

---

## 🧩 Project Structure

```
src/main/java/com/backend/devConnectBackend
 ├── controller
 │     └── AuthController.java
 ├── dto
 │     ├── LoginRequest.java
 │     └── RegisterRequest.java
 ├── model
 │     └── User.java
 ├── repository
 │     └── UserRepository.java   // MongoRepository
 ├── security
 │     ├── SecurityConfig.java
 │     └── JwtService.java
 ├── service
 │     └── AuthService.java
 └── DevConnectBackendApplication.java
```

---

## 🔥 Endpoints

### 📝 Register

**POST /register**

#### Request Body:

```json
{
  "name": "Renuka",
  "email": "renuka@gmail.com",
  "password": "12345",
  "role": "ADMIN"
}
```

#### Response:

```
"User registered successfully"
```

---

### 🔐 Login

**POST /login**

#### Request Body:

```json
{
  "email": "renuka@gmail.com",
  "password": "12345"
}
```

#### Response:

A signed JWT token:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🛡 JWT Generation Logic

You use:

```java
private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
```

This ensures:

* Secure 256-bit secret
* No Base64 or WeakKeyException
* Tokens are valid and safe

---

## 🔧 Security Configuration

`SecurityConfig` uses:

* `@EnableWebSecurity` to enable Spring Security
* `SecurityFilterChain` bean to configure:

    * CSRF disabled
    * Public endpoints
    * Stateless session management (JWT-ready)

---

## 🧠 Technologies Used

* **Java 21**
* **Spring Boot 3.x**
* **Spring Security 6**
* **jjwt (JSON Web Token library)**
* **Postman** for API testing

---

## 📦 How to Run

### 1️⃣ Clone the repository

```
git clone https://github.com/yourname/springboot-jwt-demo.git
```

### 2️⃣ Build & Run

```
mvn spring-boot:run
```

### 3️⃣ Test using Postman

* POST `/register`
* POST `/login`
* Copy the JWT token from login response

---

## 🧠 What is covered

* Building REST APIs with Spring Boot
* Using `@RestController`, `@Service`, `@Repository`
* Passing JSON using `@RequestBody`
* Configuring Spring Security with `SecurityFilterChain`
* Disabling CSRF for APIs
* Generating secure JWT tokens
* Using `MongoDB` 



---

## 🛠 API Reference (Current)
 
1. Register User
Method: POST
URL: http://localhost:8080/register
Body (JSON):
json
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "Password@123",
  "role": "USER"
}
(Note: Password likely needs 1 uppercase, 1 lowercase, 1 number, and min 8 chars based on your validation rules).
2. Login User
Method: POST
URL: http://localhost:8080/auth/login
Body (JSON):
json
{
  "email": "test@example.com",
  "password": "Password@123"
}
Response: You should receive a long text string starting with eyJ.... This is your signed JWT Token.