package com.unknown.post.controllers;

import com.unknown.post.dtos.CommentDTO;
import com.unknown.post.dtos.FullCommentDTO;
import com.unknown.post.dtos.UCommentDTO;
import com.unknown.post.entities.Comment;
import com.unknown.post.services.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestPropertySource(properties = {"mongock.enabled=false"})
@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void getCommentByIDTest() throws Exception {
        log.info("getCommentTest start.");
        String id = "id";
        LocalDateTime now = LocalDateTime.now();
        var comment = new FullCommentDTO("1", "name", "ava", "content", "author", now);
        Mockito.doReturn(comment).when(commentService).getCommentById(id);
        mockMvc.perform(get("/comment/{id}", id)).andExpect(status().isOk());
        Mockito.verify(commentService, Mockito.times(1)).getCommentById(id);
    }

    @Test
    void getCommentsByAuthorTest() throws Exception {
        log.info("getCommentsByAuthorTest start.");
        String author = "author";
        LocalDateTime now = LocalDateTime.now();
        var comment = new FullCommentDTO("1", "name", "ava", "content", "author", now);
        Mockito.doReturn(List.of(comment)).when(commentService).getCommentsByAuthor(author);
        mockMvc.perform(get("/comment/author/{id}", author)).andExpect(status().isOk());
        Mockito.verify(commentService, Mockito.times(1)).getCommentsByAuthor(author);
    }

    @Test
    void getCommentsByPostTest() throws Exception {
        log.info("getCommentsByCommentTest start.");
        String post = "post";
        LocalDateTime now = LocalDateTime.now();
        var comment = new FullCommentDTO("1", "name", "ava", "content", "author", now);
        Mockito.doReturn(List.of(comment)).when(commentService).getCommentsByPost(post);
        mockMvc.perform(get("/comment/post/{id}", post)).andExpect(status().isOk());
        Mockito.verify(commentService, Mockito.times(1)).getCommentsByPost(post);
    }

    @Test
    void addCommentTest() throws Exception {
        log.info("addCommentTest start.");
        String post = "post";
        var data = new CommentDTO("content", "author");
        var comment = new Comment(data.content(), data.author(), post);
        String resp = """
                {
                  "content": "content",
                  "author": "author"
                }
                """;
        Mockito.doReturn(comment).when(commentService).addComment(data.author(), data.content(), post);
        mockMvc.perform(post("/comment/post/{id}", post)
                        .contentType(MediaType.APPLICATION_JSON).content(resp))
                .andExpect(status().isCreated());
        Mockito.verify(commentService, Mockito.times(1))
                .addComment(data.content(), data.author(), post);
    }

    @Test
    void updateCommentTest() throws Exception {
        log.info("updateCommentTest start.");
        String id = "id";
        var data = new UCommentDTO("content");
        var comment = new Comment(data.content(), "author", "post");
        String resp = """
                {
                  "content": "content"
                }
                """;
        Mockito.doReturn(comment).when(commentService).updateComment(id, data.content());
        mockMvc.perform(put("/comment/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(resp))
                .andExpect(status().isOk());
        Mockito.verify(commentService, Mockito.times(1))
                .updateComment(id, data.content());
    }

    @Test
    void deleteCommentTest() throws Exception {
        log.info("deleteCommentTest start.");
        String id = "id";
        var data = new CommentDTO("content", "author");
        var comment = new Comment(data.content(), data.author(), "post");
        Mockito.doReturn(comment).when(commentService).deleteComment(id);
        mockMvc.perform(delete("/comment/{id}", id)).andExpect(status().isOk());
        Mockito.verify(commentService, Mockito.times(1))
                .deleteComment(id);
    }
}