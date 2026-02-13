# DevConnect Backend - Technical Documentation

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [Database Schema](#database-schema)
6. [Security Implementation](#security-implementation)
7. [API Endpoints](#api-endpoints)
8. [Design Patterns](#design-patterns)
9. [Error Handling](#error-handling)
10. [Setup & Installation](#setup--installation)
11. [Testing](#testing)
12. [Deployment](#deployment)

---

## 📖 Project Overview

**DevConnect Backend** is a RESTful API built with Spring Boot that provides a platform for developers to connect, share posts, and engage through comments. The application features robust authentication, role-based access control, and a comprehensive content management system.

### Key Features
- ✅ User authentication with JWT tokens
- ✅ Role-based authorization (USER, ADMIN)
- ✅ User profile management with privacy controls
- ✅ Post creation and management with visibility settings
- ✅ Comment system with ownership validation
- ✅ Reaction system (Like/Dislike) with atomic counters
- ✅ MongoDB integration for data persistence
- ✅ Global exception handling
- ✅ Input validation
- ✅ Pagination support for posts, comments, and reactions

---

## 🏗 Architecture

### Layered Architecture

The application follows a clean **3-tier layered architecture**:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers + DTOs + Exception        │
│   Handling)                             │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Business Logic Layer            │
│  (Services + Domain Logic + Validation) │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Data Access Layer               │
│  (Repositories + MongoDB + Models)      │
└─────────────────────────────────────────┘
```

### Request Flow Diagram

```
┌──────────┐     ┌─────────────────┐     ┌──────────────┐
│  Client  │────▶│ Security Filter │────▶│  Controller  │
│ (Postman)│     │  Chain (JWT)    │     │              │
└──────────┘     └─────────────────┘     └──────┬───────┘
                                                 │
                 ┌───────────────────────────────┘
                 │
                 ▼
        ┌────────────────┐
        │    Service     │
        │  (Business     │
        │   Logic)       │
        └────────┬───────┘
                 │
                 ▼
        ┌────────────────┐
        │   Repository   │
        │   (MongoDB)    │
        └────────┬───────┘
                 │
                 ▼
        ┌────────────────┐
        │    MongoDB     │
        │   Database     │
        └────────────────┘
```

### Security Flow

```
┌────────────┐
│   Client   │
└──────┬─────┘
       │ POST /auth/login
       ▼
┌──────────────────┐
│ AuthController   │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐      ┌─────────────┐
│  AuthService     │─────▶│ BCrypt      │
│                  │      │ Validation  │
└──────┬───────────┘      └─────────────┘
       │
       ▼
┌──────────────────┐
│   JwtService     │
│ Generate Token   │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ Return JWT Token │
└──────────────────┘

       Subsequent Requests:
       
┌────────────┐
│   Client   │
└──────┬─────┘
       │ Header: Authorization: Bearer {token}
       ▼
┌─────────────────────────┐
│ JwtAuthenticationFilter │
│ • Extract Token         │
│ • Validate Signature    │
│ • Extract User Email    │
│ • Set Authentication    │
└──────┬──────────────────┘
       │
       ▼
┌──────────────────┐
│   Controller     │
│ (Secured Route)  │
└──────────────────┘
```

---

## 🛠 Technology Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ | Programming language |
| **Spring Boot** | 3.5.7 | Application framework |
| **Spring Web** | 3.5.7 | RESTful API development |
| **Spring Security** | 6.x | Authentication & Authorization |
| **Spring Data MongoDB** | 3.5.7 | MongoDB integration |
| **MongoDB** | 4.x+ | NoSQL database |
| **JWT (jjwt)** | 0.11.5 | Token-based authentication |
| **Maven** | 3.x | Build tool & dependency management |

### Key Dependencies

```xml
<!-- Web & REST -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MongoDB -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 📂 Project Structure

```
src/main/java/com/backend/devConnectBackend/
│
├── DevConnectBackendApplication.java    # Main application entry point
│
├── config/                               # Configuration classes
│   ├── ApplicationConfig.java            # Bean configurations (BCrypt, AuthManager)
│   └── MongoConfig.java                  # MongoDB auditing configuration
│
├── constants/                            # Application constants
│   └── OwnerFilter.java                  # Enum for filtering posts by owner
│
├── controller/                           # REST Controllers
│   ├── AuthController.java               # Authentication endpoints
│   ├── UserController.java               # User profile endpoints
│   ├── PostController.java               # Post CRUD endpoints
│   └── CommentController.java            # Comment CRUD endpoints
│
├── dto/                                  # Data Transfer Objects
│   ├── RegisterRequest.java              # User registration request
│   ├── LoginRequest.java                 # User login request
│   ├── LoginResult.java                  # Sealed interface for login results
│   ├── ProfileResult.java                # Sealed interface for profile results
│   ├── PostRequest.java                  # Create/Update post request
│   ├── PostResponse.java                 # Post response with author details
│   ├── CommentRequest.java               # Create comment request
│   └── CommentResponse.java              # Comment response with author details
│
├── exception/                            # Custom exceptions & handlers
│   ├── GlobalExceptionHandler.java       # Centralized exception handling
│   ├── UserAlreadyExistsException.java
│   ├── InvalidCredentialsException.java
│   ├── PostNotFoundException.java
│   ├── CommentNotFoundException.java
│   └── UnauthorizedAccessException.java
│
├── model/                                # MongoDB document models
│   ├── User.java                         # User entity (implements UserDetails)
│   ├── Post.java                         # Post entity
│   ├── Comment.java                      # Comment entity with DBRef
│   └── Role.java                         # Enum for user roles (USER, ADMIN)
│
├── repository/                           # MongoDB repositories
│   ├── UserRepository.java               # User data access
│   ├── PostRepository.java               # Post data access
│   └── CommentRepository.java            # Comment data access
│
├── security/                             # Security components
│   ├── SecurityConfig.java               # Security filter chain configuration
│   ├── JwtService.java                   # JWT token generation & validation
│   └── JwtAuthenticationFilter.java      # JWT filter for request authentication
│
└── service/                              # Business logic layer
    ├── AuthService.java                  # Authentication & registration logic
    ├── UserService.java                  # User profile logic with role-based access
    ├── PostService.java                  # Post CRUD logic with authorization
    └── CommentService.java               # Comment CRUD logic with authorization
```

---

## 🗄 Database Schema

### Collections Overview

The application uses **3 main MongoDB collections**:

1. **users** - User account information
2. **posts** - User-generated posts
3. **comments** - Comments on posts

### User Collection

```javascript
{
  "_id": ObjectId("65a1b2c3d4e5f6g7h8i9j0k1"),
  "name": "Renuka Raut",
  "email": "renuka@example.com",              // Unique index
  "password": "$2a$10$...",                    // BCrypt hashed
  "role": "USER",                              // Enum: USER, ADMIN
  "skills": ["Java", "Spring Boot", "MongoDB"],
  "currentSalary": NumberDecimal("50000"),
  "expectedSalary": NumberDecimal("70000"),
  "_class": "com.backend.devConnectBackend.model.User"
}
```

**Indexes:**
- `email` - Unique index for user lookup

### Post Collection

```javascript
{
  "_id": ObjectId("65b2c3d4e5f6g7h8i9j0k1l2"),
  "title": "Looking for Spring Boot Developer",
  "description": "We are hiring experienced developers",
  "techStack": ["Java", "Spring Boot", "Docker"],
  "visibility": true,                          // true = public, false = private
  "userId": "65a1b2c3d4e5f6g7h8i9j0k1",       // Reference to User
  "commentCount": 5,                           // Number of comments (denormalized)
  "likeCount": 42,                             // Number of likes (denormalized)
  "dislikeCount": 3,                           // Number of dislikes (denormalized)
  "createdAt": ISODate("2026-02-09T13:06:39Z"),
  "updatedAt": ISODate("2026-02-09T13:06:39Z"),
  "_class": "com.backend.devConnectBackend.model.Post"
}
```

**Indexes:**
- `userId` - Index for filtering user's posts
- `visibility` - Index for public post queries

### Comment Collection

```javascript
{
  "_id": ObjectId("65c3d4e5f6g7h8i9j0k1l2m3"),
  "content": "Great opportunity! I'm interested.",
  "post": DBRef("posts", "65b2c3d4e5f6g7h8i9j0k1l2"),   // Reference to Post
  "user": DBRef("users", "65a1b2c3d4e5f6g7h8i9j0k1"),   // Reference to User
  "createdAt": ISODate("2026-02-09T13:30:00Z"),
  "updatedAt": ISODate("2026-02-09T13:30:00Z"),
  "_class": "com.backend.devConnectBackend.model.Comment"
}
```

**Indexes:**
- `post` - Index for fetching comments by post

### Reaction Collection

```javascript
{
  "_id": ObjectId("65d4e5f6g7h8i9j0k1l2m3n4"),
  "postId": "65b2c3d4e5f6g7h8i9j0k1l2",         // Reference to Post
  "userId": "65a1b2c3d4e5f6g7h8i9j0k1",          // Reference to User
  "type": "LIKE",                                // LIKE or DISLIKE
  "createdAt": ISODate("2026-02-13T13:20:00Z"),
  "updatedAt": ISODate("2026-02-13T13:20:00Z"),
  "_class": "com.backend.devConnectBackend.model.Reaction"
}
```

**Indexes:**
- `postId` - Index for fetching reactions by post
- Compound unique index on `(postId, userId)` - Ensures one reaction per user per post

**Note:** Post model includes denormalized counters:
- `likeCount` - Total number of likes (updated atomically)
- `dislikeCount` - Total number of dislikes (updated atomically)

### Entity Relationships

```
User (1) ─────────> (*) Post
  │                      │
  │                      │
  ├──────> (*) Comment <─┘
  │                      │
  └──────> (*) Reaction <┘
```

- One **User** can create many **Posts**
- One **User** can create many **Comments**
- One **User** can create many **Reactions**
- One **Post** can have many **Comments**
- One **Post** can have many **Reactions**
- Each **User** can have only one **Reaction** per **Post** (enforced by compound index)

---

## 🔐 Security Implementation

### JWT Authentication

The application uses **stateless JWT authentication** with the following components:

#### 1. JWT Token Structure

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJyZW51a2FAZXhhbXBsZS5jb20iLCJyb2xlIjoiVVNFUiIsImlhdCI6MTcwOTk5OTk5OSwiZXhwIjoxNzEwMDg2Mzk5fQ.
signature
```

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "renuka@example.com",    // User email
  "role": "USER",                 // User role
  "iat": 1709999999,              // Issued at
  "exp": 1710086399               // Expiration (24 hours)
}
```

**Signature:** HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)

#### 2. JwtService Components

**Key Generation:**
```java
private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
```
- Uses secure 256-bit secret key
- Automatically generated using `jjwt` library

**Token Generation:**
```java
public String generateToken(String email, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + 86400000); // 24 hours
    
    return Jwts.builder()
        .setSubject(email)
        .claim("role", role)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key)
        .compact();
}
```

**Token Validation:**
```java
public boolean isTokenValid(String token) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
        return true;
    } catch (JwtException e) {
        return false;
    }
}
```

#### 3. Security Filter Chain

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf(csrf -> csrf.disable())                    // Disabled for API
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/register", "/auth/login").permitAll()
            .requestMatchers("/profile", "/profile/**").authenticated()
            .requestMatchers("/posts", "/posts/**").authenticated()
            .anyRequest().permitAll()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

#### 4. JWT Authentication Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            // Validate and authenticate
            if (jwtService.isTokenValid(token)) {
                String email = jwtService.extractEmail(token);
                UserDetails user = userDetailsService.loadUserByUsername(email);
                
                // Set authentication in security context
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Password Security

- **Algorithm:** BCrypt with default strength (10 rounds)
- **Password Requirements:**
  - Minimum 8 characters
  - At least 1 uppercase letter
  - At least 1 lowercase letter
  - At least 1 digit
- **Validation Regex:** `^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$`

### Role-Based Access Control (RBAC)

**Roles:**
- `USER` - Standard user with limited access
- `ADMIN` - Administrative user with elevated privileges

**Access Control:**

| Feature | USER | ADMIN |
|---------|------|-------|
| View own profile (full details) | ✅ | ✅ |
| View other's profile (full details) | ❌ | ✅ |
| View other's profile (public only) | ✅ | ✅ |
| Create posts | ✅ | ✅ |
| Edit own posts | ✅ | ✅ |
| Delete own posts | ✅ | ✅ |
| Add comments | ✅ | ✅ |
| Delete own comments | ✅ | ✅ |

---

## 🌐 API Endpoints

### Authentication APIs

#### 1. Register User
```
POST /register
Content-Type: application/json

{
  "name": "Renuka Raut",
  "email": "renuka@example.com",
  "password": "Password123",
  "role": "USER",
  "skills": ["Java", "Spring Boot"],
  "currentSalary": 50000,
  "expectedSalary": 70000
}

Response: 201 CREATED
"User registered!"
```

#### 2. Login
```
POST /auth/login
Content-Type: application/json

{
  "email": "renuka@example.com",
  "password": "Password123"
}

Response: 200 OK
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### User Profile APIs

#### 3. Get Current User Profile
```
GET /profile
Authorization: Bearer {token}

Response: 200 OK
{
  "id": "65a1b2c3d4e5f6g7h8i9j0k1",
  "name": "Renuka Raut",
  "email": "renuka@example.com",
  "role": "USER",
  "skills": ["Java", "Spring Boot"],
  "currentSalary": 50000,
  "expectedSalary": 70000
}
```

#### 4. Get User Profile by ID
```
GET /profile/{profileId}
Authorization: Bearer {token}

Response: 200 OK (if ADMIN or own profile)
{
  "id": "...",
  "name": "...",
  "email": "...",
  "role": "...",
  "skills": [...],
  "currentSalary": ...,
  "expectedSalary": ...
}

Response: 200 OK (if USER viewing other's profile)
{
  "id": "...",
  "name": "...",
  "email": "...",
  "role": "...",
  "skills": [...]
  // No salary fields
}
```

### Post APIs

#### 5. Create Post
```
POST /posts
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Looking for Spring Boot Developer",
  "description": "We are hiring",
  "techStack": ["Java", "Spring Boot"],
  "visibility": true
}

Response: 201 CREATED
{
  "id": "65b2c3d4e5f6g7h8i9j0k1l2",
  "title": "Looking for Spring Boot Developer",
  "description": "We are hiring",
  "techStack": ["Java", "Spring Boot"],
  "visibility": true,
  "userId": "renuka@example.com",
  "commentCount": 0,
  "createdAt": "2026-02-09T13:06:39Z",
  "updatedAt": "2026-02-09T13:06:39Z"
}
```

#### 6. Get All Public Posts (Paginated)
```
GET /posts?page=0&size=10
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 50,
  "totalPages": 5,
  "last": false
}
```

#### 7. Get My Posts
```
GET /posts?owner=ME
Authorization: Bearer {token}

Response: 200 OK
{
  "content": [...],
  "totalElements": 5,
  "totalPages": 1
}
```

#### 8. Update Post
```
PUT /posts/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Updated Title",
  "description": "Updated description",
  "techStack": ["Java"],
  "visibility": false
}

Response: 200 OK
{...updated post...}
```

#### 9. Delete Post
```
DELETE /posts/{id}
Authorization: Bearer {token}

Response: 204 NO CONTENT
```

### Comment APIs

#### 10. Add Comment
```
POST /posts/{postId}/comments
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "Great opportunity!"
}

Response: 201 CREATED
{
  "id": "65c3d4e5f6g7h8i9j0k1l2m3",
  "content": "Great opportunity!",
  "postId": "65b2c3d4e5f6g7h8i9j0k1l2",
  "authorEmail": "renuka@example.com",
  "authorName": "Renuka Raut",
  "createdAt": "2026-02-09T13:30:00Z"
}
```

#### 11. Get Comments for Post
```
GET /posts/{postId}/comments
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": "...",
    "content": "...",
    "postId": "...",
    "authorEmail": "...",
    "authorName": "...",
    "createdAt": "..."
  }
]
```

#### 12. Delete Comment
```
DELETE /comments/{commentId}
Authorization: Bearer {token}

Response: 204 NO CONTENT
```

#### 13. Toggle Reaction (Like/Dislike)
```
POST /posts/{postId}/reactions
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "LIKE"
}

Response: 201 CREATED (when reaction is added/updated)
{
  "id": "65d4e5f6g7h8i9j0k1l2m3n4",
  "postId": "65b2c3d4e5f6g7h8i9j0k1l2",
  "userId": "user123",
  "userName": "Renuka Raut",
  "type": "LIKE",
  "createdAt": "2026-02-13T13:20:00Z",
  "updatedAt": "2026-02-13T13:20:00Z"
}

Response: 204 NO CONTENT (when reaction is removed)
```

**Behavior:**
- First call with `LIKE`: Creates like reaction, increments `likeCount`
- Second call with `LIKE`: Removes like reaction, decrements `likeCount`
- Call with `DISLIKE` after `LIKE`: Removes like, adds dislike, updates both counts

#### 14. Get Reactions for Post (Paginated)
```
GET /posts/{postId}/reactions?page=0&size=20&type=LIKE
Authorization: Bearer {token}

Query Parameters:
- type (optional): LIKE or DISLIKE - filter by reaction type
- page (optional): Page number (default: 0)
- size (optional): Page size (default: 20)
- sort (optional): Sort field and direction (default: createdAt,desc)

Response: 200 OK
{
  "content": [
    {
      "id": "65d4e5f6g7h8i9j0k1l2m3n4",
      "postId": "65b2c3d4e5f6g7h8i9j0k1l2",
      "userId": "user123",
      "userName": "Renuka Raut",
      "type": "LIKE",
      "createdAt": "2026-02-13T13:20:00Z",
      "updatedAt": "2026-02-13T13:20:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 42,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

**Note:** Post responses now include reaction data:
- `likeCount`: Total number of likes
- `dislikeCount`: Total number of dislikes
- `userReaction`: Current user's reaction (`"LIKE"`, `"DISLIKE"`, or `null`)

---

## 🎨 Design Patterns

### 1. **Layered (N-Tier) Architecture**
- **Controller Layer:** REST endpoints, request/response handling
- **Service Layer:** Business logic, validation, authorization
- **Repository Layer:** Data access, MongoDB operations

### 2. **Dependency Injection**
- Constructor-based dependency injection throughout
- Spring manages bean lifecycle and dependencies

```java
@RestController
public class PostController {
    private final PostService postService;
    
    public PostController(PostService postService) {
        this.postService = postService;
    }
}
```

### 3. **DTO Pattern**
- Separation of API contracts from domain models
- Request DTOs for input validation
- Response DTOs for controlled data exposure

### 4. **Repository Pattern**
- Abstraction over data access layer
- Uses Spring Data MongoDB repositories

```java
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
```

### 5. **Strategy Pattern**
- Used with Sealed Interfaces for type-safe result handling

```java
public sealed interface LoginResult 
    permits Success, UserNotFound, InvalidPassword {
    
    record Success(String token) implements LoginResult {}
    record UserNotFound() implements LoginResult {}
    record InvalidPassword() implements LoginResult {}
}
```

### 6. **Filter Chain Pattern**
- JWT authentication filter in security chain
- Processes each request before controller

### 7. **Builder Pattern**
- JWT token construction
- MongoDB query builders

### 8. **Singleton Pattern**
- Spring beans are singletons by default
- Services, repositories, configurations

---

## ⚠️ Error Handling

### Global Exception Handler

The application uses `@RestControllerAdvice` for centralized exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    // ... other handlers
}
```

### Standard Error Responses

| HTTP Status | Exception | Response |
|-------------|-----------|----------|
| 400 BAD REQUEST | `MethodArgumentNotValidException` | `{"field": "error message"}` |
| 400 BAD REQUEST | `HttpMessageNotReadableException` | `{"message": "Invalid role..."}` |
| 401 UNAUTHORIZED | `InvalidCredentialsException` | `"Invalid password"` |
| 403 FORBIDDEN | `UnauthorizedAccessException` | `"Not authorized..."` |
| 404 NOT FOUND | `PostNotFoundException` | `"Post not found"` |
| 404 NOT FOUND | `CommentNotFoundException` | `"Comment not found"` |
| 409 CONFLICT | `UserAlreadyExistsException` | `"User already exists..."` |
| 500 INTERNAL SERVER ERROR | `Exception` | `"An unexpected error occurred..."` |

---

## 🚀 Setup & Installation

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **MongoDB 4.x+**
- **Git**

### Installation Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/your-repo/devConnectBackend.git
cd devConnectBackend
```

#### 2. Configure MongoDB

Update `src/main/resources/application.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/devConnect
```

#### 3. Install Dependencies
```bash
mvn clean install
```

#### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

#### 5. Verify Installation
```bash
# Should return 201 Created
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "Password123",
    "role": "USER"
  }'
```

### Configuration Options

**application.yml:**
```yaml
spring:
  application:
    name: devConnectBackend
  data:
    mongodb:
      uri: mongodb://localhost:27017/devConnect
  jackson:
    deserialization:
      fail-on-unknown-properties: true

server:
  port: 8080                    # Change if needed
  error:
    include-message: always
    include-binding-errors: always
```

---

## 🧪 Testing

### Manual Testing with Postman

1. **Import Postman Collection** (refer to `postman_api_guide.md`)
2. **Setup Environment Variables:**
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: (set after login)

3. **Test Flow:**
   ```
   1. POST /register         → Register user
   2. POST /auth/login       → Get JWT token
   3. GET /profile           → Verify authentication
   4. POST /posts            → Create post
   5. GET /posts             → List posts
   6. POST /posts/{id}/comments → Add comment
   7. DELETE /comments/{id}  → Delete comment
   ```

### Unit Testing

Run unit tests:
```bash
mvn test
```

Test coverage includes:
- ✅ Repository tests
- ✅ Service layer tests
- ✅ Authentication tests
- ✅ Authorization tests

---

## 🌍 Deployment

### Option 1: Docker Deployment

**Dockerfile:**
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/devConnectBackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build and Run:**
```bash
# Build application
mvn clean package

# Build Docker image
docker build -t devconnect-backend .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/devConnect \
  devconnect-backend
```

### Option 2: JAR Deployment

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/devConnectBackend-0.0.1-SNAPSHOT.jar
```

### Option 3: Cloud Deployment

**For Heroku:**
1. Create `Procfile`:
   ```
   web: java -jar target/devConnectBackend-0.0.1-SNAPSHOT.jar
   ```

2. Set environment variables:
   ```bash
   heroku config:set SPRING_DATA_MONGODB_URI=mongodb+srv://...
   ```

3. Deploy:
   ```bash
   git push heroku main
   ```

**For AWS/Azure/GCP:**
- Package as JAR
- Deploy to EC2/App Service/Compute Engine
- Configure MongoDB connection string
- Set up load balancer and auto-scaling

---

## 📊 Performance Considerations

### Database Indexing
- Email field indexed (unique) for fast user lookup
- UserId and PostId indexed for relationship queries
- Consider compound indexes for complex queries

### Caching Recommendations
- Implement Redis caching for frequently accessed data
- Cache JWT tokens (with TTL matching token expiration)
- Cache user profiles

### Pagination
- Default page size: 2 (configurable)
- Sort by `createdAt` descending
- Use cursor-based pagination for large datasets

---

## 🔒 Security Best Practices

1. ✅ Passwords hashed with BCrypt
2. ✅ JWT tokens with 24-hour expiration
3. ✅ Stateless session management
4. ✅ CSRF disabled (API-only)
5. ✅ Input validation on all endpoints
6. ✅ Role-based authorization
7. ✅ Sensitive data hidden in responses
8. ⚠️ **TODO:** Implement rate limiting
9. ⚠️ **TODO:** Add CORS configuration for production
10. ⚠️ **TODO:** Implement refresh tokens

---

## 📝 Future Enhancements

- [ ] Implement refresh token mechanism
- [ ] Add email verification for registration
- [ ] Implement password reset functionality
- [ ] Add file upload support for user avatars
- [ ] Implement real-time notifications (WebSocket)
- [ ] Add search functionality for posts
- [ ] Implement like/reaction system
- [ ] Add API rate limiting
- [ ] Implement OpenAPI/Swagger documentation
- [ ] Add comprehensive integration tests
- [ ] Implement audit logging

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Contributors

- **Renuka Raut** - Initial development

---

## 📞 Support

For issues or questions, please contact:
- Email: raut.renuka@gmail.com
- GitHub Issues: [Create an issue](https://github.com/your-repo/devConnectBackend/issues)

---

**Last Updated:** February 10, 2026
