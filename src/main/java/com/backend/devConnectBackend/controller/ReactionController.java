package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.constants.ReactionType;
import com.backend.devConnectBackend.dto.ReactionRequest;
import com.backend.devConnectBackend.dto.ReactionResponse;
import com.backend.devConnectBackend.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for handling reaction-related endpoints.
 */
@RestController
@RequestMapping("/posts/{postId}/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    /**
     * Toggle a reaction on a post (like/dislike).
     * - First call: Creates reaction
     * - Second call with same type: Removes reaction
     * - Call with different type: Updates reaction
     *
     * @param postId         Post ID
     * @param request        Reaction request with type
     * @param authentication Current user authentication
     * @return ReactionResponse if created/updated, 204 No Content if removed
     */
    @PostMapping
    public ResponseEntity<ReactionResponse> toggleReaction(
            @PathVariable String postId,
            @Valid @RequestBody ReactionRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        Optional<ReactionResponse> response = reactionService.toggleReaction(postId, request, userEmail);

        return response
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * Get paginated list of reactions for a post.
     * Can be filtered by reaction type (LIKE or DISLIKE).
     *
     * @param postId   Post ID
     * @param type     Optional reaction type filter
     * @param pageable Pagination parameters
     * @return Page of reactions
     */
    @GetMapping
    public ResponseEntity<Page<ReactionResponse>> getReactions(
            @PathVariable String postId,
            @RequestParam(required = false) ReactionType type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReactionResponse> reactions = reactionService.getReactions(postId, type, pageable);
        return ResponseEntity.ok(reactions);
    }
}
