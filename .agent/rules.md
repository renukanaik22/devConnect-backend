# DevConnect Backend – Java Spring Boot Coding & API Standards

## 1. Java Language Conventions

* Use meaningful, intention-revealing names (avoid abbreviations).
* Follow **camelCase** for variables and methods.
* Follow **PascalCase** for class, record, enum, and interface names.
* Constants must be `public static final` and **UPPER_SNAKE_CASE**.
* Prefer **immutability** wherever possible.
* Avoid field injection; use **constructor injection only**.
* Use Java `Optional` only for return types, never for fields or parameters.
* Do not use raw types; always use generics.
* Avoid unnecessary inheritance; prefer composition.

---

## 2. Project Structure & Layering

* Follow strict layered architecture:

  * `controller` → `service` → `repository`
* Do **NOT** skip layers (controller must not call repository directly).
* Package by **feature/domain**, not by technical type where possible.
* Do not expose JPA entities outside the service layer.
* Keep utility classes stateless and final with private constructors.

---

## 3. Spring Boot Best Practices

* Use **constructor injection** for all dependencies.
* Controllers must:

  * Contain only request validation and response mapping.
  * Never contain business logic.
* Services must:

  * Contain all business rules and transactional logic.
* Repositories must:

  * Only handle persistence logic.
* Use `@Transactional` at **service layer**, not controller.
* Prefer `@ConfigurationProperties` over hardcoded config values.
* Avoid circular dependencies.
* Enable actuator only for required endpoints.

---

## 4. API Design Standards

* Follow RESTful resource naming:

  * Nouns, plural (`/users`, `/posts`)
* HTTP methods:

  * `GET` → Read
  * `POST` → Create
  * `PUT` → Full update
  * `PATCH` → Partial update
  * `DELETE` → Delete
* Do not return `Object` or `Map` as API responses.
* Always return a **well-defined response DTO**.
* Use pagination for list endpoints (`page`, `size`, `sort`).
* Avoid breaking API changes; version APIs if needed (`/api/v1`).
* **Avoid magic strings**: Use enums or constants for string comparisons.
  * Example: Use `OwnerFilter.ME` enum instead of `"me"` string
  * Use `Role.ADMIN.name()` instead of `"ADMIN"` string
  * Create enums with `@JsonCreator` for query parameters

---

## 5. DTO & Mapping Rules

* Never expose JPA entities in API responses.
* Use separate DTOs for:

  * Request
  * Response
* DTOs must be immutable (use Java `record` where possible).
* Validation annotations belong on DTOs, not entities.
* Mapping logic must not live inside controllers.
* Prefer MapStruct or dedicated mapper classes.

---

## 6. Error Handling & Validation

* Use a centralized `@RestControllerAdvice`.
* Never expose stack traces or internal exceptions to clients.
* Use meaningful, user-friendly error messages.
* Standard error response format:

  * timestamp
  * status
  * errorCode
  * message
  * path
* Use custom exceptions for business errors.
* Do not use exceptions for normal control flow.
* Validate inputs using Bean Validation (`@NotNull`, `@Size`, etc.).

---

## 7. Security Standards

* Never rely on frontend for authorization.
* Perform ownership and access checks in service layer.
* Use Spring Security context to identify current user.
* Avoid hardcoded secrets or credentials.
* Sanitize and validate all external inputs.
* Follow least-privilege principle for roles and permissions.

---

## 8. Persistence & Database

* Use JPA repositories only for persistence operations.
* Avoid `findAll()` without pagination in production APIs.
* Prefer `existsBy…` checks instead of loading full entities.
* Use database constraints (unique, not null) in addition to validations.
* Avoid N+1 queries; use fetch joins when needed.
* Use proper indexing for frequently queried columns.

---

## 9. SOLID & Design Principles

* **Single Responsibility**: One class, one reason to change.
* **Open/Closed**: Extend behavior without modifying existing code.
* **Liskov Substitution**: Subtypes must be safely substitutable.
* **Interface Segregation**: Small, focused interfaces.
* **Dependency Inversion**: Depend on abstractions, not implementations.
* Avoid God classes and overly generic services.
* Prefer composition over inheritance.

---

## 10. Logging & Observability

* Use SLF4J with parameterized logging.
* Do not use `System.out.println`.
* Log:

  * Important state changes
  * Errors with correlation IDs
* Do not log sensitive data (passwords, tokens).
* Log at appropriate levels (`INFO`, `WARN`, `ERROR`).

---

## 11. Testing Standards

* Unit tests for all service layer logic.
* Repository tests using in-memory DB or test containers.
* Controller tests using MockMvc or WebTestClient.
* Mock external dependencies.
* Minimum **80% code coverage** (quality > quantity).
* Tests must be deterministic and independent.
* **CRITICAL: Always use a separate test database**:
  * Create `src/test/resources/application.properties` with test-specific database name
  * Example: `spring.data.mongodb.database=devConnect-test`
  * **NEVER** run tests against development or production databases
  * This prevents accidental data loss from test cleanup operations

---

## 12. Code Quality & Maintainability

* No commented-out code in commits.
* Avoid magic numbers; use constants.
* Methods should be short and readable.
* Avoid deep nesting; refactor into smaller methods.
* Follow clean code and readability over cleverness.
* Code must be self-documenting; comments only when necessary.

---

## 13. AI Code Editor Rules (Important)

* Do NOT generate boilerplate without business relevance.
* Do NOT violate layered architecture.
* Do NOT return entities from controllers.
* Always follow above standards even if not explicitly requested.
* Prefer clarity, maintainability, and correctness over brevity.


