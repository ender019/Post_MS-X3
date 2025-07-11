package com.unknown.post.controllers;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.dtos.UPostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{id}")
    public Post getPost(@PathVariable String id){
        log.info("Get Post Endpoint");
        log.debug("Getting post with id {}", id);
        return postService.getPostById(id);
    }

    @GetMapping("/all")
    public List<Post> getAllPosts() {
        log.info("Get All Posts Endpoint");
        return postService.getAllPosts();
    }

    @GetMapping("/author/{id}")
    public List<Post> getPostsByAuthor(@PathVariable String id) {
        log.info("Get Posts by Author Endpoint");
        log.debug("Getting post with author id {}", id);
        return postService.getPostsByAuthor(id);
    }

    @GetMapping("/find")
    public List<Post> findPosts(@RequestParam String title) {
        log.info("Find Post Endpoint");
        log.debug("Finding post with title {}", title);
        return postService.findPostsByTitle(title);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Post addPost(@RequestBody PostDTO data) {
        log.info("Add Post Endpoint");
        log.debug("Adding post {}", data);
        return postService.addPost(data);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable String id, @RequestBody UPostDTO data) {
        log.info("Update Post Endpoint");
        log.debug("Updating post with id {}\nand data: {}", id, data);
        return postService.updatePost(id, data.title(), data.content());
    }

    @DeleteMapping("/{id}")
    public Post deletePost(@PathVariable String id) {
        log.info("Delete Post Endpoint");
        log.debug("Deleting post with id {}", id);
        return postService.delPostById(id);
    }
}
