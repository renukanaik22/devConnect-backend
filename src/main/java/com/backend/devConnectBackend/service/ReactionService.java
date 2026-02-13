package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.constants.ReactionType;
import com.backend.devConnectBackend.dto.ReactionRequest;
import com.backend.devConnectBackend.dto.ReactionResponse;
import com.backend.devConnectBackend.exception.PostNotFoundException;
import com.backend.devConnectBackend.model.Post;
import com.backend.devConnectBackend.model.Reaction;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.PostRepository;
import com.backend.devConnectBackend.repository.ReactionRepository;
import com.backend.devConnectBackend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service layer for handling reaction business logic.
 */
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReactionService(ReactionRepository reactionRepository,
            PostRepository postRepository,
            UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Toggle a reaction on a post.
     * - If user hasn't reacted: Create new reaction
     * - If user has same reaction: Remove reaction
     * - If user has different reaction: Update to new reaction
     *
     * @param postId    Post ID
     * @param request   Reaction request with type
     * @param userEmail Current user's email
     * @return Optional containing ReactionResponse if created/updated, empty if
     *         removed
     */
    @Transactional
    public Optional<ReactionResponse> toggleReaction(String postId, ReactionRequest request, String userEmail) {
        // Verify post exists
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Get current user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has a reaction on this post
        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndUserId(postId, user.getId());

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();

            // Same reaction type - remove it (toggle off)
            if (reaction.getType() == request.type()) {
                reactionRepository.delete(reaction);

                // Decrement the appropriate counter
                if (request.type() == ReactionType.LIKE) {
                    postRepository.incrementLikeCount(postId, -1);
                } else {
                    postRepository.incrementDislikeCount(postId, -1);
                }

                return Optional.empty(); // Reaction removed
            }

            // Different reaction type - update it
            ReactionType oldType = reaction.getType();
            reaction.setType(request.type());
            Reaction updated = reactionRepository.save(reaction);

            // Update counters: decrement old, increment new
            if (oldType == ReactionType.LIKE) {
                postRepository.incrementLikeCount(postId, -1);
                postRepository.incrementDislikeCount(postId, 1);
            } else {
                postRepository.incrementDislikeCount(postId, -1);
                postRepository.incrementLikeCount(postId, 1);
            }

            return Optional.of(mapToResponse(updated, user));
        }

        // No existing reaction - create new one
        Reaction newReaction = new Reaction();
        newReaction.setPostId(postId);
        newReaction.setUserId(user.getId());
        newReaction.setType(request.type());

        Reaction saved = reactionRepository.save(newReaction);

        // Increment the appropriate counter
        if (request.type() == ReactionType.LIKE) {
            postRepository.incrementLikeCount(postId, 1);
        } else {
            postRepository.incrementDislikeCount(postId, 1);
        }

        return Optional.of(mapToResponse(saved, user));
    }

    /**
     * Get paginated list of reactions for a post, optionally filtered by type.
     *
     * @param postId   Post ID
     * @param type     Optional reaction type filter
     * @param pageable Pagination parameters
     * @return Page of reaction responses
     */
    public Page<ReactionResponse> getReactions(String postId, ReactionType type, Pageable pageable) {
        // Verify post exists
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        Page<Reaction> reactions;
        if (type != null) {
            reactions = reactionRepository.findByPostIdAndType(postId, type, pageable);
        } else {
            reactions = reactionRepository.findByPostId(postId, pageable);
        }

        return reactions.map(reaction -> {
            User user = userRepository.findById(reaction.getUserId())
                    .orElse(null);
            return mapToResponse(reaction, user);
        });
    }

    /**
     * Get user's reaction for a specific post.
     *
     * @param postId Post ID
     * @param userId User ID
     * @return Optional containing the reaction type if user has reacted
     */
    public Optional<ReactionType> getUserReaction(String postId, String userId) {
        return reactionRepository.findByPostIdAndUserId(postId, userId)
                .map(Reaction::getType);
    }

    /**
     * Map Reaction entity to ReactionResponse DTO.
     */
    private ReactionResponse mapToResponse(Reaction reaction, User user) {
        return new ReactionResponse(
                reaction.getId(),
                reaction.getPostId(),
                reaction.getUserId(),
                user != null ? user.getName() : "Unknown User",
                reaction.getType(),
                reaction.getCreatedAt(),
                reaction.getUpdatedAt());
    }
}
