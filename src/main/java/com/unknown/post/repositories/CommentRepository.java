package com.unknown.post.repositories;

import com.unknown.post.entities.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Optional<Comment> findCommentById(String id);

    List<Comment> findCommentsByAuthor(String authorId);

    void deleteCommentById(String id);

    void deleteCommentByAuthor(String userId);

    @Query("{ 'author' : ?0 }")
    @Update("{ $set: { 'author': ?1 } }")
    void updateAuthorByAuthor(String userId, String updatedAuthor);

}
