# Spring Data Custom Repository - How It Works

## Your Question: "Where is PostRepositoryCustomImpl used?"

**You're right to be confused!** You won't see:
- ❌ `new PostRepositoryCustomImpl()` anywhere
- ❌ Any class extending `PostRepositoryCustomImpl`
- ❌ Explicit wiring in configuration

## The Answer: Spring Data's "Convention over Configuration"

Spring Data uses **naming conventions** and **runtime proxy generation** to automatically wire custom implementations.

### How Spring Data Discovers PostRepositoryCustomImpl

```
Step 1: Spring scans for repositories
   ↓
Step 2: Finds PostRepository interface
   ↓
Step 3: Sees it extends PostRepositoryCustom
   ↓
Step 4: Looks for a class named PostRepositoryCustomImpl
   ↓
Step 5: Creates a PROXY that combines:
        - MongoRepository methods (findById, save, etc.)
        - PostRepositoryCustom methods (incrementCommentCount)
   ↓
Step 6: Injects this proxy when you @Autowire PostRepository
```

### The Naming Convention Rule

Spring Data looks for implementations following this pattern:
```
{RepositoryInterfaceName}Impl
```

In our case:
- Interface: `PostRepository`
- Custom interface: `PostRepositoryCustom`
- Implementation: `PostRepositoryCustomImpl` ← Must end with "Impl"

### Where It's Actually Used

**File:** `CommentService.java`

```java
@Service
public class CommentService {
    private final PostRepository postRepository;  // ← Spring injects a PROXY here
    
    public CommentService(PostRepository postRepository) {
        this.postRepository = postRepository;  // ← This is NOT a simple interface!
    }
    
    public CommentResponse addComment(...) {
        // ...
        postRepository.incrementCommentCount(postId, 1);  // ← Calls PostRepositoryCustomImpl
    }
}
```

**What `postRepository` actually is:**
- It's a **Spring-generated proxy** (created at runtime)
- Combines methods from `MongoRepository` + `PostRepositoryCustom`
- Delegates `incrementCommentCount()` to `PostRepositoryCustomImpl`

### Proof: Let's Add Debug Logging

Want to see it in action? Add this to `PostRepositoryCustomImpl`:

```java
@Override
public void incrementCommentCount(String postId, int delta) {
    System.out.println("🔥 PostRepositoryCustomImpl.incrementCommentCount called!");
    System.out.println("   Post ID: " + postId);
    System.out.println("   Delta: " + delta);
    
    Query query = new Query(Criteria.where("_id").is(postId));
    Update update = new Update().inc("commentCount", delta);
    mongoTemplate.updateFirst(query, update, Post.class);
}
```

Then add a comment via API - you'll see the logs!

### The Spring Proxy Pattern

At runtime, Spring creates something like this (simplified):

```java
// This is what Spring generates internally (pseudocode)
class PostRepositoryProxy implements PostRepository {
    private MongoRepository mongoRepo;
    private PostRepositoryCustomImpl customImpl;
    
    // Standard methods delegated to MongoRepository
    public Optional<Post> findById(String id) {
        return mongoRepo.findById(id);
    }
    
    // Custom methods delegated to PostRepositoryCustomImpl
    public void incrementCommentCount(String postId, int delta) {
        customImpl.incrementCommentCount(postId, delta);  // ← HERE!
    }
}
```

### Why This Pattern?

**Benefits:**
- ✅ No boilerplate wiring code
- ✅ Clean separation of concerns
- ✅ Easy to test (can mock the interface)
- ✅ Follows Spring Data conventions

**Trade-offs:**
- ❓ "Magic" can be confusing at first
- ❓ Relies on naming conventions
- ❓ Harder to trace in debugger

### Alternative: Explicit Configuration

If you prefer explicit wiring, you could configure it manually:

```java
@Configuration
public class RepositoryConfig {
    @Bean
    public PostRepositoryCustom postRepositoryCustom(MongoTemplate mongoTemplate) {
        return new PostRepositoryCustomImpl(mongoTemplate);
    }
}
```

But Spring Data does this automatically!

### How to Verify It's Working

1. **Run the application**
2. **Add a comment** via POST `/posts/{postId}/comments`
3. **Check the database**: `db.posts.findOne({_id: ...})`
4. **Verify `commentCount` increased** ✅

The fact that the count updates proves `PostRepositoryCustomImpl` is being called!

### Summary

**You don't see explicit usage because:**
- Spring Data creates a **runtime proxy**
- The proxy combines `MongoRepository` + `PostRepositoryCustomImpl`
- When you inject `PostRepository`, you get this proxy
- Calling `incrementCommentCount()` delegates to `PostRepositoryCustomImpl`

**It's "convention over configuration"** - Spring does the wiring for you based on naming patterns!
