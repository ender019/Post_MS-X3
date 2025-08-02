package com.unknown.post.integration;

import com.unknown.post.configs.BaseConfiguration;
import com.unknown.post.configs.WebClientConfig;
import com.unknown.post.dtos.FullCommentDTO;
import com.unknown.post.dtos.UserDTO;
import com.unknown.post.entities.Comment;
import com.unknown.post.services.CommentService;
import com.unknown.post.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = BaseConfiguration.class)
@ActiveProfiles("test")
@Transactional
public class CommentServiceITest {
    @Autowired
    private CommentService commentService;

    @MockitoBean
    private WebClientConfig webClient;

    @MockitoBean
    private UserService userService;

    @Test
    void getCommentByIdTest() {
        log.info("getCommentByIdTest start.");
        String id = "65d33a71b3a9c15e47b89c01";

        // Настройка мока для конкретного комментария
        Mockito.when(userService.getFullUser("TechEnthusiast"))
                .thenReturn(new UserDTO("TechEnthusiast", "TechEnthusiast", "tech_avatar"));

        FullCommentDTO res = commentService.getCommentById(id);

        Assertions.assertEquals(id, res.comment_id());
        Assertions.assertEquals("This technology will revolutionize healthcare diagnostics!", res.content());
        Assertions.assertEquals("TechEnthusiast", res.user_id());

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUser("TechEnthusiast");
    }

    @Test
    void getCommentsByAuthorTest() {
        log.info("getCommentsByAuthorTest start.");
        String author = "PhysicsStudent";

        // Настройка мока для списка пользователей
        List<String> authorIds = Collections.nCopies(6, "PhysicsStudent");
        Mockito.when(userService.getFullUsersGroup(authorIds))
                .thenReturn(new ArrayList<>(
                        Collections.nCopies(6, new UserDTO("PhysicsStudent", "PhysicsStudent", "physics_avatar"))
                ));

        List<FullCommentDTO> res = commentService.getCommentsByAuthor(author);

        Assertions.assertEquals(6, res.size());
        res.forEach(dto -> Assertions.assertEquals("PhysicsStudent", dto.user_id()));

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUsersGroup(authorIds);
    }

    @Test
    void getCommentsByPostTest() {
        log.info("getCommentsByPostTest start.");
        String postId = "65d33a71b3a9c15e47b89a01";

        // Настройка мока для пользователей комментариев
        List<String> authorIds = List.of("TechEnthusiast", "EthicsResearcher", "PhilosopherAI", "CommenterX", "PolicyMaker");
        List<UserDTO> users = authorIds.stream()
                .map(id -> new UserDTO(id, "user_" + id, "avatar_" + id))
                .collect(Collectors.toCollection(ArrayList::new));

        Mockito.when(userService.getFullUsersGroup(authorIds))
                .thenReturn(users);

        List<FullCommentDTO> res = commentService.getCommentsByPost(postId);

        Assertions.assertEquals(5, res.size());
        res.forEach(dto -> Assertions.assertNotNull(dto.user_id()));

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUsersGroup(authorIds);
    }

    @Test
    void addCommentTest() {
        log.info("addCommentTest start.");
        String postId = "65d33a71b3a9c15e47b89a01";
        String author = "TestAuthor";

        // Настройка мока для нового автора
        Mockito.when(userService.getFullUser(author))
                .thenReturn(new UserDTO(author, "Test User", "test_avatar"));

        // Настройка мока для списка авторов при получении комментариев поста
        List<String> existingAuthorIds = List.of("TechEnthusiast", "EthicsResearcher", "PhilosopherAI", "CommenterX", "PolicyMaker");
        Mockito.when(userService.getFullUsersGroup(Mockito.anyList()))
                .thenAnswer(invocation -> {
                    List<String> ids = invocation.getArgument(0);
                    return ids.stream()
                            .map(id -> new UserDTO(id, id, "avatar_" + id))
                            .collect(Collectors.toCollection(ArrayList::new));
                });

        int initialCount = commentService.getCommentsByPost(postId).size();
        Comment newComment = commentService.addComment("New content", author, postId);
        int updatedCount = commentService.getCommentsByPost(postId).size();

        Assertions.assertEquals("New content", newComment.getContent());
        Assertions.assertEquals(author, newComment.getAuthor());
        Assertions.assertEquals(initialCount + 1, updatedCount);
    }

    @Test
    void updateCommentTest() {
        log.info("updateCommentTest start.");
        String commentId = "65d33a71b3a9c15e47b89c04";
        String newContent = "Updated content";

        // Настройка мока для автора комментария
        Mockito.when(userService.getFullUser("EnergyExpert"))
                .thenReturn(new UserDTO("EnergyExpert", "Energy Expert", "energy_avatar"));

        Comment updated = commentService.updateComment(commentId, newContent);
        FullCommentDTO fullComment = commentService.getCommentById(commentId);

        Assertions.assertEquals(newContent, updated.getContent());
        Assertions.assertEquals(newContent, fullComment.content());
        Assertions.assertEquals("EnergyExpert", fullComment.user_id());

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUser("EnergyExpert");
    }

    @Test
    void deleteCommentTest() {
        log.info("deleteCommentTest start.");
        String commentId = "65d33a71b3a9c15e47b89c05";

        // Настройка мока для автора комментария
        Mockito.when(userService.getFullUser(Mockito.anyString()))
                .thenReturn(new UserDTO("Author", "Author", "author_avatar"));

        Comment deleted = commentService.deleteComment(commentId);

        Assertions.assertEquals(commentId, deleted.getId());
        Assertions.assertThrows(NoSuchElementException.class,
                () -> commentService.getCommentById(commentId));
    }
}
