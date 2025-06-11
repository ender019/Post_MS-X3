package com.unknown.post.controllers;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/get/{id}")
    public Post getPost(@PathVariable String id){
        log.info("Get Post Endpoint");
        log.debug("Getting post with id {}", id);
        return postService.getPostById(id);
    }

    @GetMapping("/get/all")
    public List<Post> getAllPosts() {
        log.info("Get All Post Endpoint");
        return postService.getAllPosts();
    }

    @GetMapping("/find")
    public List<Post> findPosts(@RequestParam String title) {
        log.info("Find Post Endpoint");
        log.debug("Finding post with title {}", title);
        return postService.findPostsByTitle(title);
    }

    @PostMapping("/add")
    public Post addPost(@RequestBody PostDTO data) {
        log.info("Add Post Endpoint");
        log.debug("Adding post {}", data);
        return postService.addPost(data);
    }

    @DeleteMapping("/delete/{id}")
    public String deletePost(@PathVariable String id) {
        log.info("Delete Post Endpoint");
        log.debug("Deleting post with id {}", id);
        return postService.delPostById(id);
    }
}
