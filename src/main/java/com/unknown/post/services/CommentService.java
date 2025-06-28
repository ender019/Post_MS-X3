package com.unknown.post.services;

import com.unknown.post.entities.Comment;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public Comment getCommentById(String id) {
        return commentRepository.findCommentById(id).orElseThrow(() -> new NoSuchElementException("Comment not found"));
    }

    public List<Comment> getCommentsByAuthor(String author_id) {
        return commentRepository.findCommentsByAuthor(author_id);
    }

    public List<Comment> getCommentsByPost(String post_id) {
        var post = postRepository.findPostById(post_id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        if(post.getComments() == null || post.getComments().isEmpty()) return Collections.emptyList();
        return commentRepository.findAllById(post.getComments());
    }

    @Transactional
    public Comment addComment(String content, String author_id, String post_id) {
        var post = postRepository.findPostById(post_id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        Comment comment = commentRepository.save(new Comment(content, author_id, post_id));
        if(post.getComments() == null) post.setComments(new ArrayList<>());
        post.getComments().add(comment.getId());
        postRepository.save(post);
        return comment;
    }

    @Transactional
    public Comment updateComment(String id, String content) {
        var comment = commentRepository.findCommentById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment deleteComment(String id) {
        var comment = commentRepository.findCommentById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        var post = postRepository.findPostById(comment.getPost_id())
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        post.getComments().remove(comment.getId());
        postRepository.save(post);
        commentRepository.deleteCommentById(id);
        return comment;
    }
}
