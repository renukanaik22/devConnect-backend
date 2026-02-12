---
name: Java Spring Boot Standards & Best Practices
description: Enforce Java Spring Boot best practices, clean code principles, SOLID design, and code readability standards
---

# Java Spring Boot Standards & Best Practices

This skill ensures all code follows industry-standard Java Spring Boot practices, clean code principles, SOLID design patterns, and maintains high readability.

## When to Apply This Skill

Apply these standards when:
- Creating new Spring Boot components (Controllers, Services, Repositories, DTOs, Entities)
- Refactoring existing code
- Reviewing code for quality improvements
- Implementing new features
- Fixing bugs or technical debt

## Core Principles

### 1. SOLID Principles

#### Single Responsibility Principle (SRP)
- **Each class should have ONE reason to change**
- Controllers: Handle HTTP requests/responses only
- Services: Contain business logic only
- Repositories: Handle data access only
- DTOs: Transfer data only
- Entities: Represent domain models only

**Example:**
```java
// ❌ BAD: Service doing too much
public class UserService {
    public void registerUser(RegisterRequest request) {
        // Validation logic
        // Password encoding
        // Email sending
        // Database saving
        // Logging
    }
}

// ✅ GOOD: Separated responsibilities
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    public User registerUser(RegisterRequest request) {
        User user = createUserFromRequest(request);
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser);
        return savedUser;
    }
}
```

#### Open/Closed Principle (OCP)
- **Open for extension, closed for modification**
- Use interfaces and abstract classes
- Leverage polymorphism and strategy patterns

**Example:**
```java
// ✅ GOOD: Strategy pattern for different notification types
public interface NotificationStrategy {
    void send(String message, String recipient);
}

@Service
public class EmailNotificationStrategy implements NotificationStrategy {
    public void send(String message, String recipient) {
        // Email implementation
    }
}

@Service
public class SmsNotificationStrategy implements NotificationStrategy {
    public void send(String message, String recipient) {
        // SMS implementation
    }
}
```

#### Liskov Substitution Principle (LSP)
- **Subtypes must be substitutable for their base types**
- Ensure derived classes don't break parent class contracts

#### Interface Segregation Principle (ISP)
- **Clients shouldn't depend on interfaces they don't use**
- Create focused, specific interfaces

**Example:**
```java
// ❌ BAD: Fat interface
public interface UserOperations {
    void create();
    void update();
    void delete();
    void sendEmail();
    void generateReport();
}

// ✅ GOOD: Segregated interfaces
public interface UserCrudOperations {
    void create();
    void update();
    void delete();
}

public interface UserNotificationOperations {
    void sendEmail();
}

public interface UserReportOperations {
    void generateReport();
}
```

#### Dependency Inversion Principle (DIP)
- **Depend on abstractions, not concretions**
- Use constructor injection with interfaces
- Avoid `new` keyword for dependencies

**Example:**
```java
// ❌ BAD: Tight coupling
public class UserService {
    private UserRepository repository = new UserRepositoryImpl();
}

// ✅ GOOD: Dependency injection with interface
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### 2. Clean Code Principles

#### Meaningful Names
```java
// ❌ BAD
List<User> list1;
String d; // elapsed time in days
int x;

// ✅ GOOD
List<User> activeUsers;
int elapsedTimeInDays;
int commentCount;
```

#### Small Functions
- Functions should do ONE thing
- Keep functions under 20 lines when possible
- Extract complex logic into helper methods

```java
// ❌ BAD: Long function doing multiple things
public ResponseEntity<?> createPost(PostRequest request) {
    if (request.getTitle() == null || request.getTitle().isEmpty()) {
        return ResponseEntity.badRequest().body("Title required");
    }
    if (request.getContent() == null || request.getContent().length() < 10) {
        return ResponseEntity.badRequest().body("Content too short");
    }
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserNotFoundException("User not found"));
    Post post = new Post();
    post.setTitle(request.getTitle());
    post.setContent(request.getContent());
    post.setAuthor(user);
    post.setCreatedAt(LocalDateTime.now());
    Post saved = postRepository.save(post);
    return ResponseEntity.ok(saved);
}

// ✅ GOOD: Separated into focused functions
public PostResponse createPost(PostRequest request) {
    validatePostRequest(request);
    User author = getUserById(request.getUserId());
    Post post = buildPost(request, author);
    Post savedPost = postRepository.save(post);
    return PostMapper.toResponse(savedPost);
}

private void validatePostRequest(PostRequest request) {
    if (request.getTitle() == null || request.getTitle().isEmpty()) {
        throw new ValidationException("Title is required");
    }
    if (request.getContent() == null || request.getContent().length() < 10) {
        throw new ValidationException("Content must be at least 10 characters");
    }
}
```

#### Comments
- Code should be self-documenting
- Use comments only when necessary to explain WHY, not WHAT
- Prefer meaningful names over comments

```java
// ❌ BAD
// Check if user is active
if (user.getStatus() == 1) {

// ✅ GOOD
if (user.isActive()) {
```

#### Error Handling
- Use custom exceptions for domain-specific errors
- Global exception handler for consistent error responses
- Never swallow exceptions

```java
// ✅ GOOD: Custom exceptions
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// ✅ GOOD: Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### 3. Spring Boot Best Practices

#### Layered Architecture
```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database
```

**Rules:**
- Controllers should NOT contain business logic
- Services should NOT know about HTTP (no ResponseEntity in services)
- Repositories should only handle data access

#### Controller Standards
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor // Lombok for constructor injection
@Validated
public class UserController {
    
    private final UserService userService;
    
    // ✅ Use proper HTTP methods and status codes
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### Service Standards
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default to read-only
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // ✅ Return DTOs, not entities
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with id: " + id));
        return UserMapper.toResponse(user);
    }
    
    // ✅ Use @Transactional for write operations
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateUserDoesNotExist(request.getEmail());
        
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();
            
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
    
    private void validateUserDoesNotExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                "User already exists with email: " + email);
        }
    }
}
```

#### Repository Standards
```java
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    // ✅ Use descriptive method names
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    // ✅ Use @Query for complex queries
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<User> findUsersCreatedBetween(LocalDateTime start, LocalDateTime end);
}
```

#### DTO Standards

**Prefer Java Records for DTOs** (Java 16+)

Records are immutable, concise, and perfect for DTOs. They automatically provide:
- Constructor
- Getters
- `equals()`, `hashCode()`, `toString()`
- Immutability

```java
// ✅ PREFERRED: Use records for request DTOs
public record CreateUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,
        
        @NotNull(message = "Role is required")
        Role role) {
}

// ✅ PREFERRED: Use records for response DTOs
public record UserResponse(
        String id,
        String email,
        Role role,
        LocalDateTime createdAt) {
    // ❌ Never expose password in responses
}

// ✅ Use compact constructor for default values and validation
public record PostRequest(
        @NotBlank(message = "Title is required")
        String title,
        
        @NotBlank(message = "Description is required")
        String description,
        
        List<String> techStack,
        
        @NotNull(message = "Visibility is required")
        Boolean visibility) {
    
    // Compact constructor to handle null values with defaults
    public PostRequest {
        if (techStack == null) {
            techStack = new ArrayList<>();
        }
    }
}

// ✅ Separate records for different operations
public record UpdateUserRequest(
        String email,
        String bio,
        List<String> skills) {
    
    // Compact constructor for null-safe collections
    public UpdateUserRequest {
        if (skills == null) {
            skills = new ArrayList<>();
        }
    }
}
```

**When to Use Traditional Classes Instead of Records:**

Use traditional classes with Lombok only when you need:
- Mutable state (rare for DTOs)
- Builder pattern with many optional fields
- Inheritance (records can't extend other classes)

```java
// ⚠️ Use traditional class only when builder pattern is essential
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexSearchRequest {
    private String keyword;
    private List<String> tags;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer minExperience;
    private Integer maxExperience;
    private SortOrder sortOrder;
    // Many optional fields benefit from builder
}
```

**Record Best Practices:**

1. **Validation**: Use Bean Validation annotations directly on record components
2. **Null Safety**: Use compact constructors to provide defaults for collections
3. **Immutability**: Records are immutable by default - embrace it
4. **Naming**: Use descriptive names (e.g., `CreatePostRequest`, `PostResponse`)
5. **Separation**: Create separate records for requests and responses
6. **No Business Logic**: Keep records as pure data carriers

#### Entity Standards
```java
@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String password; // ✅ Never expose in DTOs
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // ✅ Business logic methods in entities
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
```

### 4. Code Readability Standards

#### Formatting
- Use consistent indentation (4 spaces)
- Maximum line length: 120 characters
- One blank line between methods
- Group related methods together

#### Ordering
```java
public class UserService {
    // 1. Constants
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    // 2. Fields
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 3. Constructor
    public UserService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // 4. Public methods
    public UserResponse createUser(CreateUserRequest request) {
        // ...
    }
    
    // 5. Private methods
    private void validateUser(CreateUserRequest request) {
        // ...
    }
}
```

#### Constants
```java
// ✅ Use meaningful constant names
public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
}
```

### 5. Testing Standards

#### Unit Test Structure
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    // ✅ Use descriptive test names
    @Test
    void createUser_WithValidRequest_ShouldReturnUserResponse() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email("test@example.com")
            .password("password123")
            .role(Role.USER)
            .build();
            
        User savedUser = User.builder()
            .id("123")
            .email(request.getEmail())
            .role(request.getRole())
            .build();
            
        when(userRepository.existsByEmail(request.getEmail()))
            .thenReturn(false);
        when(passwordEncoder.encode(request.getPassword()))
            .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
            .thenReturn(savedUser);
        
        // When
        UserResponse response = userService.createUser(request);
        
        // Then
        assertNotNull(response);
        assertEquals("123", response.getId());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .email("existing@example.com")
            .build();
            
        when(userRepository.existsByEmail(request.getEmail()))
            .thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class, 
            () -> userService.createUser(request));
    }
}
```

## Checklist for Code Review

Before submitting code, verify:

### Architecture
- [ ] Proper layer separation (Controller → Service → Repository)
- [ ] No business logic in controllers
- [ ] No HTTP concerns in services
- [ ] DTOs used for data transfer, not entities

### SOLID Principles
- [ ] Each class has single responsibility
- [ ] Dependencies injected via constructor
- [ ] Interfaces used for abstraction
- [ ] No tight coupling between components

### Clean Code
- [ ] Meaningful variable and method names
- [ ] Functions are small and focused
- [ ] No magic numbers (use constants)
- [ ] Proper error handling with custom exceptions
- [ ] No commented-out code

### Spring Boot Standards
- [ ] Proper annotations (@Service, @Repository, @RestController)
- [ ] @Transactional used correctly
- [ ] Validation annotations on DTOs
- [ ] Proper HTTP status codes
- [ ] Global exception handler in place

### Testing
- [ ] Unit tests for service layer
- [ ] Test coverage > 80%
- [ ] Tests follow Given-When-Then pattern
- [ ] Edge cases covered

### Security
- [ ] No passwords in responses
- [ ] Input validation in place
- [ ] SQL/NoSQL injection prevention
- [ ] Proper authentication/authorization

## Common Anti-Patterns to Avoid

### ❌ God Classes
Classes that do too much. Break them down.

### ❌ Primitive Obsession
Using primitives instead of value objects.
```java
// ❌ BAD
String email;

// ✅ GOOD
@Embeddable
public class Email {
    private String value;
    
    public Email(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.value = value;
    }
}
```

### ❌ Feature Envy
Methods that use more data from other classes than their own.

### ❌ Shotgun Surgery
One change requires modifications in many classes.

### ❌ Circular Dependencies
Classes depending on each other. Use interfaces to break cycles.

## Resources

- **Clean Code** by Robert C. Martin
- **Effective Java** by Joshua Bloch
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **SOLID Principles**: https://www.baeldung.com/solid-principles

## Implementation Workflow

When implementing new features:

1. **Design First**: Identify entities, DTOs, and layer responsibilities
2. **Write Tests**: TDD approach when possible
3. **Implement**: Follow layered architecture
4. **Refactor**: Apply SOLID and clean code principles
5. **Review**: Use checklist above
6. **Document**: Add JavaDoc for public APIs

---

**Remember**: Code is read more often than it's written. Prioritize readability and maintainability over cleverness.
