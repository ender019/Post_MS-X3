package com.unknown.post.controllers;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {
    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/get/{id}")
    public Post getPost(@PathVariable String id) throws NoSuchElementException {
        log.info("Get Post Endpoint");
        log.debug("Getting post with id {}", id);
        return postRepository.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
    }

    @GetMapping("/get/all")
    public List<Post> getAllPosts() {
        log.info("Get All Post Endpoint");
        return postRepository.findAll();
    }

    @PostMapping("/add")
    public Post addPost(@RequestBody PostDTO data) {
        log.info("Add Post Endpoint");
        log.debug("Adding post {}", data);
        var post = new Post(data.title(), data.content(), data.author());
        return postRepository.save(post);
    }

    @DeleteMapping("/delete/{id}")
    public String deletePost(@PathVariable String id) {
        log.info("Delete Post Endpoint");
        log.debug("Deleting post with id {}", id);
        var post = postRepository.findPostById(id).orElseThrow();
        postRepository.deletePostById(id);
        return post.toString();
    }
}
