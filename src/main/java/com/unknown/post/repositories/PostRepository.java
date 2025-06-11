package com.unknown.post.repositories;

import com.unknown.post.entities.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, Long> {
    void deletePostById(String id);

    Optional<Post> findPostById(String id);
}
