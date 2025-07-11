package com.unknown.post.controllers;

import com.unknown.post.dtos.CommentDTO;
import com.unknown.post.dtos.UCommentDTO;
import com.unknown.post.entities.Comment;
import com.unknown.post.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Comment Controller", description = "Контроллер для работы с комментариями к постам.")
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Get comment by id", description = "Возвращает комментарий по ИД.")
    @GetMapping("/{id}")
    public Comment getCommentByID(@PathVariable String id){
        log.info("Get Comment by Author Endpoint");
        log.debug("Getting comment with id {}", id);
        return commentService.getCommentById(id);
    }
    
    @Operation(summary = "Get comments by author id", description = "Возвращает комментарии конкретного автора.")
    @GetMapping("/author/{id}")
    public List<Comment> getCommentsByAuthor(@PathVariable String id){
        log.info("Get Comments by Author Endpoint");
        log.debug("Getting comment with author {}", id);
        return commentService.getCommentsByAuthor(id);
    }

    @Operation(summary = "Get comments by post id", description = "Возвращает данные комментария к конкретному посту.")
    @GetMapping("/post/{id}")
    public List<Comment> getCommentsByPost(@PathVariable String id){
        log.info("Get Comments by Post Endpoint");
        log.debug("Getting comment with post {}", id);
        return commentService.getCommentsByPost(id);
    }

    @Operation(summary = "Create new comment", description = "Создает новый комментарий.")
    @PostMapping("post/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@PathVariable String id, @RequestBody CommentDTO data){
        log.info("Add Comment Endpoint");
        log.debug("Adding comment with post id {}\nand data: {}", id, data);
        return commentService.addComment(data.content(), data.author(), id);
    }

    @Operation(summary = "Update comment by id", description = "Изменяет существующий комментарий по его ИД.")
    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable String id, @RequestBody UCommentDTO data){
        log.info("Update Comment Endpoint");
        log.debug("Updating comment with id {}\nand data: {}", id, data);
        return commentService.updateComment(id, data.content());
    }

    @Operation(summary = "Delete comment by id", description = "Удаляет комментарий по ИД.")
    @DeleteMapping("/{id}")
    public Comment deleteComment(@PathVariable String id){
        log.info("Delete Comment Endpoint");
        log.debug("Deleting comment with id {}", id);
        return commentService.deleteComment(id);
    }
}
