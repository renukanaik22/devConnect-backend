package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void incrementLikeCount(String postId, int delta) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("likeCount", delta);
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    @Override
    public void incrementDislikeCount(String postId, int delta) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Update update = new Update().inc("dislikeCount", delta);
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    @Override
    public Page<Post> searchPublicPosts(String techStack, String title, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        
        criteriaList.add(Criteria.where("visibility").is(true));
        
        if (techStack != null && !techStack.trim().isEmpty()) {
            criteriaList.add(Criteria.where("techStack").regex(techStack, "i"));
        }
        
        if (title != null && !title.trim().isEmpty()) {
            criteriaList.add(Criteria.where("title").regex(title, "i"));
        }
        
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        
        long total = mongoTemplate.count(query, Post.class);
        
        query.with(pageable);
        
        List<Post> posts = mongoTemplate.find(query, Post.class);
        
        return new PageImpl<>(posts, pageable, total);
    }
}
