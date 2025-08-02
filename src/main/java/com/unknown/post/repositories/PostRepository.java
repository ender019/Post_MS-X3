package com.unknown.post.repositories;

import com.unknown.post.entities.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, Long> {
    void deletePostById(String id);

    Optional<Post> findPostById(String id);

    List<Post> findPostsByTitleContainingIgnoreCase(String title);

    List<Post> findPostsByAuthor(String author);

    void deletePostsByAuthor(String userId);

    @Query("{ 'author' : ?0 }")
    @Update("{ $set: { 'author': ?1 } }")
    void updateAuthorByAuthor(String userId, String updatedAuthor);
}
