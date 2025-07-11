package com.unknown.post.controllers;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.dtos.UPostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Post Controller", description = "Контроллер для работы с постами.")
@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Get post by id", description = "Возвращает пост по ИД.")
    @GetMapping("/{id}")
    public Post getPost(@PathVariable String id){
        log.info("Get Post Endpoint");
        log.debug("Getting post with id {}", id);
        return postService.getPostById(id);
    }

    @Operation(summary = "Get all posts", description = "Возвращает все посты.")
    @GetMapping("/all")
    public List<Post> getAllPosts() {
        log.info("Get All Posts Endpoint");
        return postService.getAllPosts();
    }

    @Operation(summary = "Get posts by author", description = "Возвращает посты конкретного автора.")
    @GetMapping("/author/{author_id}")
    public List<Post> getPostsByAuthor(@PathVariable String author_id) {
        log.info("Get Posts by Author Endpoint");
        log.debug("Getting post with author author_id {}", author_id);
        return postService.getPostsByAuthor(author_id);
    }

    @Operation(summary = "Get posts by title", description = "Возвращает посты с конкретным названием.")
    @GetMapping("/find")
    public List<Post> findPosts(@RequestParam String title) {
        log.info("Find Post Endpoint");
        log.debug("Finding post with title {}", title);
        return postService.findPostsByTitle(title);
    }

    @Operation(summary = "Create new post", description = "Создает новый пост.")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Post addPost(@RequestBody PostDTO data) {
        log.info("Add Post Endpoint");
        log.debug("Adding post {}", data);
        return postService.addPost(data);
    }

    @Operation(summary = "Update post by id", description = "Изменяет существующий пост по его ИД.")
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable String id, @RequestBody UPostDTO data) {
        log.info("Update Post Endpoint");
        log.debug("Updating post with id {}\nand data: {}", id, data);
        return postService.updatePost(id, data.title(), data.content());
    }

    @Operation(summary = "Delete post by id", description = "Удаляет пост по его ИД.")
    @DeleteMapping("/{id}")
    public Post deletePost(@PathVariable String id) {
        log.info("Delete Post Endpoint");
        log.debug("Deleting post with id {}", id);
        return postService.delPostById(id);
    }
}
