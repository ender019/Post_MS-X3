package com.unknown.post.controllers;

import com.unknown.post.dtos.ReactDTO;
import com.unknown.post.dtos.ReactTypes;
import com.unknown.post.dtos.UReactDTO;
import com.unknown.post.entities.Reaction;
import com.unknown.post.services.ReactionService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestPropertySource(properties = {"mongock.enabled=false"})
@WebMvcTest(ReactionController.class)
class ReactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReactionService reactionService;

    @Test
    void getReactionsCountTest() throws Exception {
        log.info("getReactionsCountTest start.");
        String id = "id";
        var res = List.of(new ReactDTO("LIKE", "1"), new ReactDTO("DISLIKE", "20"));
        Mockito.doReturn(res).when(reactionService).getReactionsCountByCollection("post_reacts", id);
        mockMvc.perform(get("/reaction/post/id")).andExpect(status().isOk());
        Mockito.verify(reactionService, Mockito.times(1))
                .getReactionsCountByCollection("post_reacts", id);
    }

    @Test
    void getUserReactionsCountTest() throws Exception {
        log.info("getUserReactionsCountTest start.");
        String id = "id";
        var res = List.of(new ReactDTO("LIKE", "1"), new ReactDTO("DISLIKE", "20"));
        Mockito.doReturn(res).when(reactionService).getReactionsCountByUser("post_reacts", id);
        mockMvc.perform(get("/reaction/post/user").param("user_id", id)).andExpect(status().isOk());
        Mockito.verify(reactionService, Mockito.times(1))
                .getReactionsCountByUser("post_reacts", id);
    }

    @Test
    void getUserReactionsTest() throws Exception {
        log.info("getUserReactionsTest start.");
        String id = "id";
        String type = "LIKE";
        var res = List.of("id1", "id2");
        Mockito.doReturn(res).when(reactionService).getReactionsByUser("post_reacts", id, type);
        mockMvc.perform(get("/reaction/post/user/id").param("type", type)).andExpect(status().isOk());
        Mockito.verify(reactionService, Mockito.times(1))
                .getReactionsByUser("post_reacts", id, type);
    }

    @Test
    void procReactionTest() throws Exception {
        log.info("procReactionsTest start.");
        String resp = """
                {
                  "user_id": "string1",
                  "reacted_id": "id",
                  "type": "LIKE"
                }
                """;
        var react = new UReactDTO("string1", "id", ReactTypes.LIKE);
        var res = new Reaction("string1", "id", ReactTypes.LIKE);
        Mockito.doReturn(res).when(reactionService).procReaction("post_reacts", react);
        mockMvc.perform(post("/reaction/post/").contentType(MediaType.APPLICATION_JSON).content(resp))
                .andExpect(status().isOk());
        Mockito.verify(reactionService, Mockito.times(1))
                .procReaction("post_reacts", react);
    }
}