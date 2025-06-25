package com.unknown.post.controllers;

import com.unknown.post.dtos.CommentDTO;
import com.unknown.post.entities.Comment;
import com.unknown.post.services.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public Comment getCommentByAuthor(@PathVariable String id){
        log.info("Get Comment by Author Endpoint");
        log.debug("Getting comment with id {}", id);
        return commentService.getComment(id);
    }


    @GetMapping("/author/{id}")
    public List<Comment> getCommentsByAuthor(@PathVariable String id){
        log.info("Get Comments by Author Endpoint");
        log.debug("Getting comment with author {}", id);
        return commentService.getCommentsByAuthor(id);
    }

    @GetMapping("/post/{id}")
    public List<Comment> getCommentsByPost(@PathVariable String id){
        log.info("Get Comments by Post Endpoint");
        log.debug("Getting comment with post {}", id);
        return commentService.getCommentsByPost(id);
    }

    @PostMapping("post/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@PathVariable String id, @RequestBody CommentDTO data){
        log.info("Add Comment Endpoint");
        log.debug("Adding comment with post id {}\nand data: {}", id, data);
        return commentService.addComment(data.author(), data.content(), id);
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable String id, @RequestBody CommentDTO data){
        log.info("Update Comment Endpoint");
        log.debug("Updating comment with id {}\nand data: {}", id, data);
        return commentService.updateComment(id, data.content());
    }

    @DeleteMapping("/{id}")
    public Comment deleteComment(@PathVariable String id){
        log.info("Delete Comment Endpoint");
        log.debug("Deleting comment with id {}", id);
        return commentService.deleteComment(id);
    }
}
