package com.unknown.post.integration;

import com.unknown.post.configs.BaseConfiguration;
import com.unknown.post.entities.Comment;
import com.unknown.post.services.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@SpringBootTest(classes = BaseConfiguration.class)
@ActiveProfiles("test")
@Transactional
public class CommentServiceITest {
    @Autowired
    private CommentService commentService;

    @Test
    void getCommentByIdTest() {
        log.info("getCommentByIdTest start.");
        String id = "65d33a71b3a9c15e47b89c01";
        var res = commentService.getCommentById(id);
        Assertions.assertEquals(id, res.getId());
        Assertions.assertEquals("This technology will revolutionize healthcare diagnostics!", res.getContent());
        Assertions.assertEquals("TechEnthusiast", res.getAuthor());
    }

    @Test
    void getCommentsByAuthorTest() {
        log.info("getCommentsByAuthorTest start.");
        String author = "PhysicsStudent";
        var res = commentService.getCommentsByAuthor(author);
        Assertions.assertEquals(6, res.size());
        log.debug("Result:\n{}", res);
    }

    @Test
    void getCommentsByPostTest() {
        log.info("getCommentsByPostTest start.");
        String id = "65d33a71b3a9c15e47b89a01";
        var res = commentService.getCommentsByPost(id);
        Assertions.assertEquals(5, res.size());
    }

    @Test
    void addCommentTest() {
        log.info("addCommentTest start.");
        String post_id = "65d33a71b3a9c15e47b89a01";
        var res0 = commentService.getCommentsByPost(post_id);
        Assertions.assertEquals(5, res0.size());

        var comment = new Comment("content", "author", post_id);
        var res = commentService.addComment(comment.getContent(), comment.getAuthor(), comment.getPost_id());
        Assertions.assertEquals(comment.getContent(), res.getContent());
        Assertions.assertEquals(comment.getAuthor(), res.getAuthor());
        Assertions.assertEquals(comment.getPost_id(), res.getPost_id());

        var res1 = commentService.getCommentsByPost(post_id);
        Assertions.assertEquals(6, res1.size());

    }

    @Test
    void updateCommentTest() {
        log.info("updateCommentTest start.");
        String id = "65d33a71b3a9c15e47b89c04";
        String content = "content";
        var res = commentService.updateComment(id, content);
        Assertions.assertEquals(id, res.getId());
        Assertions.assertEquals(content, res.getContent());
        Assertions.assertEquals("EnergyExpert", res.getAuthor());
        Assertions.assertEquals("65d33a71b3a9c15e47b89a02", res.getPost_id());
    }

    @Test
    void deleteCommentTest() {
        log.info("deleteCommentTest start.");
        String id = "65d33a71b3a9c15e47b89c05";
        var commented = commentService.deleteComment(id);
        Assertions.assertEquals(id, commented.getId());
        Assertions.assertThrows(NoSuchElementException.class, () -> commentService.getCommentById(id));
    }
}
