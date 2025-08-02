package com.unknown.post.services;

import com.unknown.post.dtos.FullCommentDTO;
import com.unknown.post.dtos.UserDTO;
import com.unknown.post.entities.Comment;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionService reactionService;
    private final UserService userService;

    public FullCommentDTO match(Comment comment, UserDTO user) {
        return new FullCommentDTO(
                comment.getAuthor(),
                user.username(),
                user.avatar(),
                comment.getId(),
                comment.getContent(),
                comment.getDate()
        );
    }

    public List<FullCommentDTO> matchGroup(List<Comment> comments, List<UserDTO> users) {
        comments.sort(Comparator.comparing(Comment::getAuthor));
        users.sort(Comparator.comparing(UserDTO::id));
        List<FullCommentDTO> result = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) result.add(match(comments.get(i), users.get(i)));
        return result;
    }

    public FullCommentDTO getCommentById(String id) {
        var comment = commentRepository.findCommentById(id).orElseThrow(() -> new NoSuchElementException("Comment not found"));
        var user = userService.getFullUser(comment.getAuthor());
        log.debug("Comment data: {}", comment);
        log.debug("User data: {}", user);
        return match(comment, user);
    }

    public List<FullCommentDTO> getCommentsByAuthor(String author_id) {
        var comments = commentRepository.findCommentsByAuthor(author_id);
        var users = userService.getFullUsersGroup(comments.stream().map(Comment::getAuthor).toList());
        log.debug("Comment data: {}", comments);
        log.debug("User data: {}", users);
        return matchGroup(comments, users);
    }

    public List<FullCommentDTO> getCommentsByPost(String post_id) {
        var post = postRepository.findPostById(post_id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
        log.debug("Post data: {}", post);
        if(post.getComments() == null || post.getComments().isEmpty()) return Collections.emptyList();
        var comments = commentRepository.findAllById(post.getComments());
        var users = userService.getFullUsersGroup(comments.stream().map(Comment::getAuthor).toList());
        log.debug("Comment data: {}", comments);
        log.debug("User data: {}", users);
        return matchGroup(comments, users);
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
        reactionService.deleteReactionById("comment", List.of(comment.getId()));
        postRepository.save(post);
        commentRepository.deleteCommentById(id);
        return comment;
    }
}
