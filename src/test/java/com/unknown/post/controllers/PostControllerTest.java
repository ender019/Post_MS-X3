package com.unknown.post.controllers;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.dtos.UPostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestPropertySource(properties = {"mongock.enabled=false"})
@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void getPostTest() throws Exception {
        log.info("getPostTest start.");
        String id = "id";
        var post = new Post("title", "content", "author");
        Mockito.doReturn(post).when(postService).getPostById(id);
        mockMvc.perform(get("/post/id")).andExpect(status().isOk());
        Mockito.verify(postService, Mockito.times(1)).getPostById(id);
    }

    @Test
    void getUnexistPostTest() throws Exception {
        log.info("getUnexistPostTest start.");
        String id = "id";
        Mockito.doThrow(new NoSuchElementException("Post not found")).when(postService).getPostById(id);
        mockMvc.perform(get("/post/id")).andExpect(status().isBadRequest());
        Mockito.verify(postService, Mockito.times(1)).getPostById(id);
    }

    @Test
    void getAllPostsTest() throws Exception {
        log.info("getAllPostsTest start.");
        var post = new Post("title", "content", "author");
        Mockito.doReturn(List.of(post)).when(postService).getAllPosts();
        mockMvc.perform(get("/post/all")).andExpect(status().isOk());
        Mockito.verify(postService, Mockito.times(1)).getAllPosts();
    }

    @Test
    void findPostsTest() throws Exception {
        log.info("findPostsTest start.");
        var post = new Post("title", "content", "author");
        Mockito.doReturn(List.of(post)).when(postService).findPostsByTitle("tit");
        mockMvc.perform(get("/post/find").param("title", "tit")).andExpect(status().isOk());
        Mockito.verify(postService, Mockito.times(1)).findPostsByTitle("tit");
    }

    @Test
    void addPostTest() throws Exception {
        log.info("addPostTest start.");
        var post = new Post("title", "content", "author");
        var data = new PostDTO(post.getTitle(), post.getContent(), post.getAuthor());
        String resp = """
                {
                  "title": "title",
                  "content": "content",
                  "author": "author"
                }
                """;
        Mockito.doReturn(post).when(postService).addPost(data);
        mockMvc.perform(post("/post/").contentType(MediaType.APPLICATION_JSON).content(resp))
                .andExpect(status().isCreated());
        Mockito.verify(postService, Mockito.times(1)).addPost(data);
    }

    @Test
    void updatePostTest() throws Exception {
        log.info("updatePostTest start.");
        String id = "id";
        var data = new UPostDTO("title", "content");
        var post = new Post(data.title(), data.content(), "author");
        String resp = """
                {
                  "title": "title",
                  "content": "content"
                }
                """;
        Mockito.doReturn(post).when(postService).updatePost(id, data.title(), data.content());
        mockMvc.perform(put("/post/id").contentType(MediaType.APPLICATION_JSON).content(resp))
                .andExpect(status().isOk());
        Mockito.verify(postService, Mockito.times(1))
                .updatePost(id, data.title(), data.content());
    }

    @Test
    void deletePostTest() throws Exception {
        log.info("deletePostTest start.");
        String id = "id";
        var post = new Post("title", "content", "author");
        Mockito.doReturn(post).when(postService).delPostById(id);
        mockMvc.perform(delete("/post/id")).andExpect(status().isOk());
        Mockito.verify(postService, Mockito.times(1)).delPostById(id);
    }

    @Test
    void deleteUnexistPostTest() throws Exception {
        log.info("deleteUnexistPostTest start.");
        String id = "id";
        Mockito.doThrow(new NoSuchElementException("Post not found")).when(postService).delPostById(id);
        mockMvc.perform(delete("/post/id")).andExpect(status().isBadRequest());
        Mockito.verify(postService, Mockito.times(1)).delPostById(id);
    }
}