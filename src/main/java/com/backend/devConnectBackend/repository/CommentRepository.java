package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.Comment;
import com.backend.devConnectBackend.model.Post;
import com.backend.devConnectBackend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    Optional<Comment> findByIdAndUser(String id, User user);
}
