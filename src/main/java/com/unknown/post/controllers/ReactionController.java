package com.unknown.post.controllers;

import com.unknown.post.dtos.RCollections;
import com.unknown.post.dtos.ReactDTO;
import com.unknown.post.dtos.ReactTypes;
import com.unknown.post.dtos.UReactDTO;
import com.unknown.post.entities.Reaction;
import com.unknown.post.services.ReactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Reaction Controller", description = "Контроллер для работы с реакциями.")
@RestController
@RequestMapping("/reaction/{collection}")
@AllArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @GetMapping("/{post_id}")
    public List<ReactDTO> getPostReactionsCount(@PathVariable RCollections collection, @PathVariable String post_id) {
        log.info("Get post reactions count from {} for {}", collection, post_id);
        return reactionService.getReactionsCountByPost(collection.name()+"_reacts", post_id);
    }

    @GetMapping("/user")
    public List<ReactDTO> getUserReactionsCount(@PathVariable RCollections collection, @RequestParam String user_id) {
        log.info("Get user reactions count from {} for {}", collection, user_id);
        return reactionService.getReactionsCountByUser(collection.name()+"_reacts", user_id);
    }

    @GetMapping("/user/{user_id}")
    public List<String> getUserReactions(@PathVariable RCollections collection,
                                         @PathVariable String user_id,
                                         @RequestParam ReactTypes type
    ) {
        log.info("Get user reactions from {} for {}", collection, user_id);
        return reactionService.getReactionsByUser(collection.name()+"_reacts", user_id, type.name());
    }

    @PostMapping("/")
    public Reaction procReaction(@PathVariable RCollections collection, UReactDTO UReactDTO) {
        log.info("Processing reaction from {} for {}", collection, UReactDTO);
        return reactionService.procReaction(collection.name()+"_reacts", UReactDTO);
    }
}
