package com.unknown.post.services;

import com.unknown.post.entities.Comment;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    void getCommentByIdTest() {
        log.info("getCommentByIdTest start.");
        String id = "id";
        var comment = new Comment("content", "author", "post");
        Mockito.doReturn(Optional.of(comment)).when(commentRepository).findCommentById(id);
        var res = commentService.getCommentById(id);
        Assertions.assertEquals(comment, res);
        Mockito.verify(commentRepository, Mockito.times(1)).findCommentById(id);
    }

    @Test
    void getCommentsByAuthorTest() {
        log.info("getCommentsByAuthorTest start.");
        String author = "author";
        var comment = List.of(new Comment("content", author, "post"));
        Mockito.doReturn(comment).when(commentRepository).findCommentsByAuthor(author);
        var res = commentService.getCommentsByAuthor(author);
        Assertions.assertEquals(comment, res);
        Mockito.verify(commentRepository, Mockito.times(1)).findCommentsByAuthor(author);
    }

    @Test
    void getCommentsByPostTest() {
        log.info("getCommentsByPostTest start.");
        String author = "author";
        var comment = List.of(new Comment("content", author, "post"));
        Mockito.doReturn(comment).when(commentRepository).findCommentsByAuthor(author);
        var res = commentService.getCommentsByAuthor(author);
        Assertions.assertEquals(comment, res);
        Mockito.verify(commentRepository, Mockito.times(1)).findCommentsByAuthor(author);
    }

    @Test
    void addCommentTest() {
        log.info("addCommentTest start.");
        String id = "id";
        String post_id = "post";
        var post = new Post("title", "content", "author");
        var comment = new Comment("content", "author", post_id);
        post.setId(post_id);
        comment.setId(id);
        Mockito.doReturn(Optional.of(post)).when(postRepository).findPostById(post_id);
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenAnswer(invocation -> {
                    Comment c = invocation.getArgument(0);
                    c.setId(id);
                    return c;
                });
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setId(post_id); // Устанавливаем ID как при реальном сохранении
            return p;
        });

        var res = commentService.addComment(comment.getContent(), comment.getAuthor(), comment.getPost_id());
        Assertions.assertEquals(comment.getContent(), res.getContent());
        Assertions.assertEquals(comment.getAuthor(), res.getAuthor());
        Assertions.assertEquals(comment.getPost_id(), res.getPost_id());

        Mockito.verify(commentRepository).save(Mockito.argThat(commented ->
                commented.getId().equals(comment.getId()) &&
                commented.getContent().equals(comment.getContent()) &&
                commented.getAuthor().equals(comment.getAuthor()) &&
                commented.getPost_id().equals(comment.getPost_id())
        ));
        Mockito.verify(postRepository).save(Mockito.argThat(posted ->
                posted.getTitle().equals(post.getTitle()) &&
                posted.getContent().equals(post.getContent()) &&
                posted.getAuthor().equals(post.getAuthor())&&
                posted.getComments().size() == 1 &&
                posted.getComments().getFirst().equals(id)
        ));
    }

    @Test
    void updateCommentTest() {
        log.info("updateCommentTest start.");
        String id = "id";
        String content = "content";
        var comment = new Comment(content, "author", "post");
        Mockito.doReturn(Optional.of(comment)).when(commentRepository).findCommentById(id);
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var res = commentService.updateComment(id, content);
        Assertions.assertEquals(comment.getContent(), res.getContent());
        Assertions.assertEquals(comment.getAuthor(), res.getAuthor());
        Assertions.assertEquals(comment.getPost_id(), res.getPost_id());

        Mockito.verify(commentRepository).save(Mockito.argThat(commented ->
                commented.getContent().equals(comment.getContent()) &&
                commented.getAuthor().equals(comment.getAuthor()) &&
                commented.getPost_id().equals(comment.getPost_id())
        ));

    }

    @Test
    void deleteCommentTest() {
        log.info("deleteCommentTest start.");
        String id = "id";
        String post_id = "post";
        var comment = new Comment("content", "author", post_id);
        var post = new Post("title", "content", "author");
        post.setId(post_id);
        comment.setId(id);
        post.setComments(new ArrayList<>(List.of(id)));
        Mockito.doReturn(Optional.of(comment)).when(commentRepository).findCommentById(id);
        Mockito.doReturn(Optional.of(post)).when(postRepository).findPostById(comment.getPost_id());
        var commented = commentService.deleteComment(id);
        Assertions.assertEquals(comment, commented);
        Assertions.assertTrue(post.getComments().isEmpty());
    }
}