package com.unknown.post.controllers;

import com.unknown.post.dtos.RCollections;
import com.unknown.post.dtos.ReactDTO;
import com.unknown.post.dtos.ReactTypes;
import com.unknown.post.dtos.UReactDTO;
import com.unknown.post.entities.Reaction;
import com.unknown.post.services.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get reactions count by types", description = "Возвращает количество реакций каждого типа.")
    @GetMapping("/{reacted_id}")
    public List<ReactDTO> getReactionsCount(@PathVariable RCollections collection, @PathVariable String reacted_id) {
        log.info("Get post reactions count from {} for {}", collection, reacted_id);
        return reactionService.getReactionsCountByCollection(collection.name() + "_reacts", reacted_id);
    }

    @Operation(summary = "Get reactions count by types for user",
            description = "Возвращает количество реакций каждого типа для конкретного пользователя.")
    @GetMapping("/user")
    public List<ReactDTO> getUserReactionsCount(@PathVariable RCollections collection, @RequestParam String user_id) {
        log.info("Get user reactions count from {} for {}", collection, user_id);
        return reactionService.getReactionsCountByUser(collection.name() + "_reacts", user_id);
    }

    @Operation(summary = "Get posts or comments id by user id and reaction type",
            description = "Возвращает посты или комментарии для конкретной реакции конкретного пользователя.")
    @GetMapping("/user/{user_id}")
    public List<String> getUserReactions(@PathVariable RCollections collection,
                                         @PathVariable String user_id,
                                         @RequestParam ReactTypes type
    ) {
        log.info("Get user reactions from {} for {}", collection, user_id);
        return reactionService.getReactionsByUser(collection.name() + "_reacts", user_id, type.name());
    }

    @Operation(summary = "Processing user reaction", description = "Обработка реакции пользователя.")
    @PostMapping("/")
    public Reaction procReaction(@PathVariable RCollections collection, @RequestBody UReactDTO data) {
        log.info("Processing reaction from {} for {}", collection, data);
        return reactionService.procReaction(collection.name() + "_reacts", data);
    }
}
