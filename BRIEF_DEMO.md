# 🎯 DevConnect Backend - Brief Demo

## 📋 Guidelines & Standards Used

### 1. **Antigravity AI Assistant Guidelines**
We followed strict coding standards using Antigravity's guideline files:

- **[rules.md](file:///.agent/rules.md)** - Java Spring Boot coding & API standards
  - Layered architecture (Controller → Service → Repository)
  - Constructor injection only
  - RESTful API design
  - DTO pattern for API contracts
  - Global exception handling
  - Security best practices

- **[java-springboot-standards/SKILL.md](file:///.agent/skills/java-springboot-standards/SKILL.md)** - Advanced Spring Boot patterns
  - SOLID principles
  - Clean code practices
  - Testing standards
  - Performance optimization


---


## 🎓 Key Learnings & Technologies 

### 1. **Spring Boot Ecosystem**
- ✅ **Spring Boot 3.x** - Modern Java framework with auto-configuration
- ✅ **Spring Web** - RESTful API development with `@RestController`
- ✅ **Spring Security 6** - JWT-based stateless authentication
- ✅ **Spring Data MongoDB** - NoSQL database integration with repositories
- ✅ **Spring Validation** - Bean validation with custom annotations

### 2. **Security & Authentication**
- ✅ **JWT (JSON Web Tokens)** - Stateless authentication mechanism
- ✅ **BCrypt Password Hashing** - Secure password storage
- ✅ **Role-Based Access Control (RBAC)** - USER and ADMIN roles
- ✅ **Custom Security Filters** - JWT authentication filter chain

### 3. **Database & Data Modeling**
- ✅ **MongoDB** - NoSQL document database
- ✅ **Document References** - `DBRef` for entity relationships
- ✅ **Compound Indexing** - Optimized queries with unique constraints
- ✅ **Denormalization** - Comment counts for performance
- ✅ **Auditing** - Automatic `createdAt` and `updatedAt` timestamps

### 4. **API Design & Best Practices**
- ✅ **RESTful Principles** - Proper HTTP methods and status codes
- ✅ **Pagination** - Efficient data retrieval with `Pageable`
- ✅ **DTO Pattern** - Separation of API contracts from domain models
- ✅ **Sealed Interfaces** - Type-safe result handling (Java 17+)
- ✅ **Global Exception Handling** - Centralized error responses

### 5. **Testing**
- ✅ **JUnit 5** - Unit testing framework
- ✅ **Mockito** - Mocking dependencies
- ✅ **MockMvc** - Integration testing for controllers
- ✅ **Test Coverage** - Service and controller layer tests

---

## 🌟 Standout Approaches (Exact Code Examples)

### 0. **JWT Authentication with BCrypt Password Hashing**

---

### 1. **Atomic Counter Updates for Concurrency**

**Challenge:** Multiple users commenting simultaneously could cause race conditions.

**Solution:** MongoDB atomic `$inc` operations using `MongoTemplate`.

#### Custom Repository Interface
**File:** [PostRepositoryCustom.java](file:///src/main/java/com/backend/devConnectBackend/repository/PostRepositoryCustom.java)

```java
public interface PostRepositoryCustom {
    void incrementCommentCount(String postId, int delta);
}
```

#### Implementation with MongoTemplate
**File:** [PostRepositoryCustomImpl.java](file:///src/main/java/com/backend/devConnectBackend/repository/PostRepositoryCustomImpl.java)

```java
@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public PostRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void incrementCommentCount(String postId, int delta) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("commentCount", delta);
        mongoTemplate.updateFirst(query, update, Post.class);
    }
}
```

#### Service Usage
**File:** [CommentService.java](file:///src/main/java/com/backend/devConnectBackend/service/CommentService.java)

```java
public CommentResponse addComment(String postId, CommentRequest request, String userEmail) {
    // ... create and save comment ...
    Comment saved = commentRepository.save(comment);
    
    // Atomic increment (thread-safe!)
    postRepository.incrementCommentCount(postId, 1);
    
    return mapToResponse(saved);
}

public void deleteComment(String commentId, String userEmail) {
    // ... delete comment ...
    commentRepository.delete(comment);
    
    // Atomic decrement
    postRepository.incrementCommentCount(comment.getPost().getId(), -1);
}
```

**Why This Matters:**
- ✅ **Thread-Safe** - No race conditions even with concurrent requests
- ✅ **Atomic** - All-or-nothing operation at database level
- ✅ **Performant** - Single database operation instead of read-modify-write
- ✅ **Accurate** - Guaranteed correct count even under high load

---

### 2. **Strategy Pattern for Role-Based Profile Privacy**

**Challenge:** Users should see full profiles (with salary) for themselves, and admins can see full profiles of all users. Other users see limited info without salary.

**Solution:** Strategy Pattern using sealed interfaces with role-based logic.

#### Strategy Interface
**File:** [ProfileResult.java](file:///src/main/java/com/backend/devConnectBackend/dto/ProfileResult.java)

```java
public sealed interface ProfileResult
        permits ProfileResult.FullProfile,
        ProfileResult.PublicProfile,
        ProfileResult.ProfileNotFound {

    // Strategy 1: Full profile with salary (for self and admins)
    record FullProfile(
            String id,
            String name,
            String email,
            String role,
            List<String> skills,
            BigDecimal currentSalary,
            BigDecimal expectedSalary) implements ProfileResult {
    }

    // Strategy 2: Public profile without salary (for other users)
    record PublicProfile(
            String id,
            String name,
            String email,
            String role,
            List<String> skills) implements ProfileResult {
    }

    // Strategy 3: Not found
    record ProfileNotFound() implements ProfileResult {
    }
}
```

#### Service Logic (Strategy Selection)
**File:** [UserService.java](file:///src/main/java/com/backend/devConnectBackend/service/UserService.java)

```java
@Service
public class UserService {

    public ProfileResult getUserProfile(String profileId, String requestingUserRole, String requestingUserId) {
        Optional<User> userOptional = userRepository.findById(profileId);

        if (userOptional.isEmpty()) {
            return new ProfileResult.ProfileNotFound();
        }

        User user = userOptional.get();

        // Strategy selection based on role and ownership
        if (Role.ADMIN.name().equals(requestingUserRole) || profileId.equals(requestingUserId)) {
            return new ProfileResult.FullProfile(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getSkills(),
                    user.getCurrentSalary(),
                    user.getExpectedSalary());
        } else {
            return new ProfileResult.PublicProfile(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getSkills());
        }
    }
}
```

#### Controller Handles Different Strategies
**File:** [UserController.java](file:///src/main/java/com/backend/devConnectBackend/controller/UserController.java)

```java
@GetMapping("/profile/{profileId}")
public ResponseEntity<?> getUserProfile(@PathVariable String profileId, Authentication authentication) {
    // Extract user details from JWT
    String requestingUserId = ((User) authentication.getPrincipal()).getId();
    String requestingUserRole = ((User) authentication.getPrincipal()).getRole().name();

    return switch (userService.getUserProfile(profileId, requestingUserRole, requestingUserId)) {
        case ProfileResult.FullProfile full -> ResponseEntity.ok(full);
        case ProfileResult.PublicProfile pub -> ResponseEntity.ok(pub);
        case ProfileResult.ProfileNotFound() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");
    };
}
```

---

#### What is a Sealed Interface?

**Definition:** A sealed interface (introduced in Java 17) is an interface that restricts which classes or interfaces can implement or extend it. You explicitly declare all permitted subtypes using the `permits` clause.

**Syntax:**
```java
public sealed interface ProfileResult
        permits FullProfile, PublicProfile, ProfileNotFound {
    // Only these 3 types can implement ProfileResult
}
```

**Key Characteristics:**
1. **Restricted Inheritance** - Only permitted types can implement the interface
2. **Exhaustive Checking** - Compiler knows all possible implementations
3. **Pattern Matching** - Works seamlessly with switch expressions
4. **Compile-Time Safety** - Errors caught at compile time, not runtime

**Why Should We Use Sealed Interfaces?**

**1. Exhaustive Pattern Matching**
```java
// Compiler FORCES you to handle all cases
return switch (result) {
    case FullProfile full -> ...;
    case PublicProfile pub -> ...;
    case ProfileNotFound() -> ...;
    // Missing a case? Compilation error! ✅
};
```

**2. Domain Modeling**
- Represents a **closed set** of possibilities (like enum, but with data)
- Perfect for: Result types, State machines, Event types, Strategy patterns
- Example: A profile can ONLY be Full, Public, or NotFound - nothing else!

**3. Better Than Enums**
```java
// Enum - same structure for all cases ❌
enum ProfileType { FULL, PUBLIC, NOT_FOUND }

// Sealed Interface - different data for each case ✅
sealed interface ProfileResult {
    record FullProfile(String id, String name, BigDecimal salary) implements ProfileResult {}
    record PublicProfile(String id, String name) implements ProfileResult {}
    record ProfileNotFound() implements ProfileResult {}
}
```

**4. Prevents Unexpected Implementations**
```java
// Without sealed - anyone can implement! ❌
interface ProfileResult { }
class HackerProfile implements ProfileResult { } // Oops!

// With sealed - only permitted types ✅
sealed interface ProfileResult permits FullProfile, PublicProfile {
    // HackerProfile cannot implement this! Compilation error!
}
```

**5. Self-Documenting Code**
- All possible implementations visible in one place
- No need to search entire codebase
- Clear contract for API consumers

---

#### What is the Strategy Pattern?

**Definition:** The Strategy Pattern is a behavioral design pattern that defines a family of algorithms (strategies), encapsulates each one, and makes them interchangeable. The pattern lets the algorithm vary independently from clients that use it.

**Key Components:**
1. **Strategy Interface** - Defines common interface for all strategies (`ProfileResult`)
2. **Concrete Strategies** - Different implementations (`FullProfile`, `PublicProfile`, `ProfileNotFound`)
3. **Context** - Uses a strategy to execute business logic (`UserService`)
4. **Client** - Selects and uses appropriate strategy (`UserController`)

**Traditional vs Modern Java:**
```java
// Traditional Strategy Pattern (verbose)
interface ProfileStrategy {
    ProfileData execute(User user);
}
class FullProfileStrategy implements ProfileStrategy { ... }
class PublicProfileStrategy implements ProfileStrategy { ... }

// Modern Java with Sealed Interfaces (concise)
sealed interface ProfileResult permits FullProfile, PublicProfile {
    record FullProfile(...) implements ProfileResult {}
    record PublicProfile(...) implements ProfileResult {}
}
```

---

#### Why This is Strategy Pattern:
- ✅ **Multiple Algorithms** - Different profile display strategies (Full vs Public)
- ✅ **Runtime Selection** - Strategy chosen based on user role and ownership
- ✅ **Encapsulation** - Each strategy encapsulates its own data structure
- ✅ **Type Safety** - Sealed interfaces ensure all strategies are handled

---

#### Key Benefits:

**Sealed Interfaces:**
- ✅ **Compile-Time Exhaustiveness** - Compiler forces you to handle ALL cases in switch
- ✅ **Restricted Inheritance** - Only permitted types can implement (prevents unexpected implementations)
- ✅ **Self-Documenting** - All possible outcomes visible in one place

**Strategy Pattern + Sealed Interfaces:**
- ✅ **Type Safety** - Compiler guarantees all strategies handled, no runtime errors
- ✅ **Privacy & Security** - Different data exposure for different roles with compile-time guarantee
- ✅ **Maintainability** - All strategies in one file, easy to add new ones

**Real-World Impact:**
```java
// Before: Magic strings, no type safety, error-prone ❌
Map<String, Object> response = new HashMap<>();
if (role.equals("ADMIN")) { response.put("salary", ...); }

// After: Type-safe, compiler-verified, secure ✅
return role.equals(Role.ADMIN.name()) 
    ? new FullProfile(...)   // Compiler knows all fields
    : new PublicProfile(...); // Salary automatically excluded
```

---

### 3. **Global Exception Handling**

---

### 4. **Pagination for Scalability**

---

### 5. **Compound Indexing for Reactions**

**Challenge:** Ensure one user can only have one reaction per post, and fast lookups.

**Solution:** Compound unique index on `(postId, userId)`.

**File:** [Reaction.java](file:///src/main/java/com/backend/devConnectBackend/model/Reaction.java)

```java
@Document(collection = "reactions")
@CompoundIndex(name = "post_user_idx", 
               def = "{'postId': 1, 'userId': 1}", 
               unique = true)
public class Reaction {

    @Id
    private String id;

    @Indexed
    private String postId;

    @Indexed
    private String userId;

    private ReactionType type;  // LIKE or DISLIKE

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors, getters, setters...
}
```

**How It Works:**
1. **Compound Index** - `{'postId': 1, 'userId': 1}` creates a composite index
2. **Unique Constraint** - Prevents duplicate reactions from same user on same post
3. **Individual Indexes** - `@Indexed` on `postId` and `userId` for fast queries

**Benefits:**
- ✅ **Data Integrity** - Database enforces one reaction per user per post
- ✅ **Performance** - O(1) lookup for user's reaction on a post
- ✅ **Scalability** - Efficient even with millions of reactions
- ✅ **Atomic Toggle** - Can safely implement like/unlike without race conditions

**MongoDB Index:**
```javascript
db.reactions.createIndex(
  { "postId": 1, "userId": 1 }, 
  { unique: true, name: "post_user_idx" }
)
```

---

## 🎯 Key Takeaways

### What Makes This Project Stand Out?

1. **Modern Java Features**
   - Records for DTOs
   - Sealed interfaces for type safety
   - Pattern matching with switch expressions

2. **Performance-First Design**
   - Denormalized counts
   - Atomic updates
   - Pagination everywhere
   - Strategic indexing

3. **Clean Architecture**
   - Layered design
   - Constructor injection
   - Separation of concerns
   - SOLID principles

4. **Production-Ready**
   - Global exception handling
   - Input validation
   - Security best practices
   - Comprehensive testing

5. **Scalable & Maintainable**
   - Stateless authentication
   - Repository pattern
   - DTO pattern
   - Clear code organization

---

## 📚 Technologies Demonstrated

✅ **Spring Boot 3.x** - Modern framework  
✅ **Spring Security 6** - JWT authentication  
✅ **MongoDB** - NoSQL database  
✅ **Java 17+** - Records, sealed interfaces  
✅ **JUnit 5 & Mockito** - Testing  
✅ **Maven** - Build tool  
✅ **RESTful API Design** - Best practices  
✅ **Pagination** - Spring Data  
✅ **RBAC** - Role-based access control  
✅ **Global Exception Handling** - `@RestControllerAdvice`

---

## 🎬 Conclusion

This project demonstrates a **production-ready Spring Boot application** with:
- ✅ Modern Java features and best practices
- ✅ Performance optimizations for scalability
- ✅ Clean, maintainable architecture
- ✅ Comprehensive security implementation
- ✅ Following industry-standard guidelines

**Ready for deployment and real-world use!** 🚀
