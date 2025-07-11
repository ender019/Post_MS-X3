package com.unknown.post.repositories;

import com.unknown.post.entities.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Optional<Comment> findCommentById(String id);

    List<Comment> findCommentsByAuthor(String authorId);

    void deleteCommentById(String id);
}
