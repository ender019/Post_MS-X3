package com.unknown.post.services;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post getPostById(String id) {
        return postRepository.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> findPostsByTitle(String title) {
        return postRepository.findPostsByTitleContaining(title);
    }

    public Post addPost(PostDTO data) {
        return postRepository.save(new Post(data.title(), data.content(), data.author()));
    }

    public String delPostById(String id) {
        var post = postRepository.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
        postRepository.deletePostById(id);
        return post.toString();
    }
}
