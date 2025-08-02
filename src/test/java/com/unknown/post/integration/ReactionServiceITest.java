package com.unknown.post.integration;

import com.unknown.post.configs.BaseConfiguration;
import com.unknown.post.configs.WebClientConfig;
import com.unknown.post.dtos.ReactTypes;
import com.unknown.post.dtos.UReactDTO;
import com.unknown.post.entities.Reaction;
import com.unknown.post.services.ReactionService;
import com.unknown.post.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Slf4j
@SpringBootTest(classes = {BaseConfiguration.class})
@ActiveProfiles("test")
@Transactional
class ReactionServiceITest {
    @MockitoBean
    private WebClientConfig webClientConfig;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Reaction getReaction(String reacted_id, String user_id, String collection) {
        return mongoTemplate.findOne(new Query(
                Criteria.where("reacted_id").is(reacted_id).and("user_id").is(user_id)
        ), Reaction.class, collection);
    }

    @Test
    void getReactionsCountByCollectionTest() {
        log.info("getReactionsCountByCollectionTest start.");
        String id = "65d33a71b3a9c15e47b89a01";
        var res = reactionService.getReactionsCountByCollection("post_reacts", id);
        log.debug("Result is {}", res);
        Assertions.assertEquals(2, res.size());
        Assertions.assertEquals("2", res.stream().filter(el -> el.name().equals("LIKE")).findFirst().get().count());
        Assertions.assertEquals("1",res.stream().filter(el -> el.name().equals("DISLIKE")).findFirst().get().count());
    }

    @Test
    void getReactionsByUserTest() {
        log.info("getReactionsByUserTest start.");
        String user_id = "Maria Garcia";
        var res = reactionService.getReactionsByUser("post_reacts", user_id, "LIKE");
        log.debug("Result is {}", res);
        Assertions.assertEquals(5, res.size());
    }

    @Test
    void getReactionsCountByUserTest() {
        log.info("getReactionsCountByUserTest start.");
        String user_id = "Maria Garcia";
        var res = reactionService.getReactionsCountByUser("post_reacts", user_id);
        Assertions.assertEquals(2, res.size());
        log.debug("Result is {}", res);
        Assertions.assertEquals("5", res.stream().filter(el -> el.name().equals("LIKE")).findFirst().get().count());
        Assertions.assertEquals("1",res.stream().filter(el -> el.name().equals("DISLIKE")).findFirst().get().count());

    }

    @Test
    void addReactionTest() {
        log.info("getReactionsCountByUserTest start.");
        String reacted_id = "65d33a71b3a9c15e47b89a11";
        String user_id = "Daniel Lee";
        var like = new UReactDTO(user_id, reacted_id, ReactTypes.LIKE);
        var ans = new Reaction(user_id, reacted_id, ReactTypes.LIKE);
        var res = reactionService.procReaction("post_reacts", like);
        log.debug("Result is {}", res);
        Assertions.assertEquals(ans.getUser_id(), res.getUser_id());
        Assertions.assertEquals(ans.getReacted_id(), res.getReacted_id());
        Assertions.assertEquals(ans.getType(), res.getType());
        Assertions.assertEquals(res, getReaction(reacted_id, user_id, "post_reacts"));
    }

    @Test
    void setReactionTest() {
        log.info("getReactionsCountByUserTest start.");
        String reacted_id = "65d33a71b3a9c15e47b89a11";
        String user_id = "Daniel Lee";
        var like = new UReactDTO(user_id, reacted_id, ReactTypes.LIKE);
        var dislike = new UReactDTO(user_id, reacted_id, ReactTypes.DISLIKE);
        var ans = new Reaction(user_id, reacted_id, ReactTypes.LIKE);

        var res = reactionService.procReaction("post_reacts", like);
        log.debug("Result is {}", res);
        Assertions.assertEquals(ans.getUser_id(), res.getUser_id());
        Assertions.assertEquals(ans.getReacted_id(), res.getReacted_id());
        Assertions.assertEquals(ans.getType(), res.getType());
        Assertions.assertEquals(res, getReaction(reacted_id, user_id, "post_reacts"));
        ans.setType(ReactTypes.DISLIKE);

        res = reactionService.procReaction("post_reacts", dislike);
        log.debug("Result is {}", res);
        Assertions.assertEquals(ans.getUser_id(), res.getUser_id());
        Assertions.assertEquals(ans.getReacted_id(), res.getReacted_id());
        Assertions.assertEquals(ans.getType(), res.getType());
        Assertions.assertEquals(res, getReaction(reacted_id, user_id, "post_reacts"));
    }

    @Test
    void delReactionTest() {
        log.info("getReactionsCountByUserTest start.");
        String reacted_id = "65d33a71b3a9c15e47b89a11";
        String user_id = "Daniel Lee";
        var like = new UReactDTO(user_id, reacted_id, ReactTypes.LIKE);
        var ans = new Reaction(user_id, reacted_id, ReactTypes.LIKE);

        var res = reactionService.procReaction("post_reacts", like);
        log.debug("Result is {}", res);
        Assertions.assertEquals(ans.getUser_id(), res.getUser_id());
        Assertions.assertEquals(ans.getReacted_id(), res.getReacted_id());
        Assertions.assertEquals(ans.getType(), res.getType());
        Assertions.assertEquals(res, getReaction(reacted_id, user_id, "post_reacts"));

        res = reactionService.procReaction("post_reacts", like);
        log.debug("Result is {}", res);
        Assertions.assertEquals(ans.getUser_id(), res.getUser_id());
        Assertions.assertEquals(ans.getReacted_id(), res.getReacted_id());
        Assertions.assertEquals(ans.getType(), res.getType());
        Assertions.assertNull(getReaction(reacted_id, user_id, "post_reacts"));
    }
}