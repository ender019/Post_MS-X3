package com.unknown.post.integration;

import com.unknown.post.configs.BaseConfiguration;
import com.unknown.post.dtos.PostDTO;
import com.unknown.post.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@SpringBootTest(classes = {BaseConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class PostServiceITest {
    @Autowired
    private PostService postService;

    @Test
    void getPostByIdTest() {
        log.info("getPostByIdTest start.");
        String id = "65d33a71b3a9c15e47b89a01";
        var res = postService.getPostById(id);
        Assertions.assertEquals(id, res.getId());
        Assertions.assertEquals("The Future of AI", res.getTitle());
        Assertions.assertEquals(
                "Exploring the latest advancements in artificial intelligence and machine learning.",
                res.getContent());
        Assertions.assertEquals("Alex Johnson", res.getAuthor());
        log.debug("Result is {}", res);
    }

    @Test
    void getUnexistPostByIdTest() {
        log.info("getUnexistPostByIdTest start.");
        String id = "id";
        Assertions.assertThrows(NoSuchElementException.class, () -> postService.getPostById(id));
    }

    @Test
    void findAllPostsTest() {
        log.info("findAllPostsTest start.");
        var res = postService.getAllPosts();
        Assertions.assertEquals(10, res.size());
        log.debug("Result is {}", res.getFirst().getId());
        log.debug("Result:\n{}", res);
        var post = postService.getPostById(res.getFirst().getId());
        Assertions.assertEquals(res.getFirst(), post);
    }

    @Test
    void findPostsByTitleTest() {
        log.info("The Future of AI");
        String title = "The Future of AI";
        var res = postService.findPostsByTitle(title).getFirst();
        Assertions.assertEquals(title, res.getTitle());
        Assertions.assertEquals(
                "Exploring the latest advancements in artificial intelligence and machine learning.",
                res.getContent()
        );
        Assertions.assertEquals("Alex Johnson", res.getAuthor());
    }

    @Test
    void addPostTest() {
        log.info("addPostTest start.");
        var data = new PostDTO("Added", "posted getContent()", "me");
        var res = postService.addPost(data);
        Assertions.assertEquals(data.title(), res.getTitle());
        Assertions.assertEquals(data.content(), res.getContent());
        Assertions.assertEquals(data.author(), res.getAuthor());
        Assertions.assertEquals(11, postService.getAllPosts().size());
        Assertions.assertEquals(res, postService.getPostsByAuthor("me").getFirst());
    }

    @Test
    void updatePostTest() {
        log.info("updatePostTest start.");
        String id = "65d33a71b3a9c15e47b89a04";
        var data = new PostDTO("data1", "data2", "author");
        var res = postService.updatePost(id, data.title(), data.content());
        Assertions.assertEquals(data.title(), res.getTitle());
        Assertions.assertEquals(data.content(), res.getContent());
        Assertions.assertEquals("Sarah Chen", res.getAuthor());
        Assertions.assertEquals(10, postService.getAllPosts().size());
    }

    @Test
    void updateUnexistPostTest() {
        log.info("updateUnexistPostTest start.");
        String id = "id";
        var data = new PostDTO("data1", "data2", "author");
        Assertions.assertThrows(NoSuchElementException.class,
                () -> postService.updatePost(id, data.title(), data.content()));
    }

    @Test
    void delPostByIdTest() {
        log.info("delPostByIdTest start.");
        String id = "65d33a71b3a9c15e47b89a04";
        var res = postService.delPostById(id);
        Assertions.assertEquals(id, res.getId());
        Assertions.assertEquals("Mental Health in Tech", res.getTitle());
        Assertions.assertEquals("Addressing burnout and stress in high-pressure industries.",
                res.getContent());
        Assertions.assertEquals("Sarah Chen", res.getAuthor());
        Assertions.assertEquals(9, postService.getAllPosts().size());
    }

    @Test
    void delUnexistPostByIdTest() {
        log.info("delUnexistPostByIdTest start.");
        String id = "id";
        Assertions.assertThrows(NoSuchElementException.class, () -> postService.delPostById(id));
    }

}
