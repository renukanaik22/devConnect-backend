package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.Post;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * Custom implementation of PostRepositoryCustom using MongoTemplate for atomic
 * operations.
 */
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
