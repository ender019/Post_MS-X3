package com.unknown.post.integration;

import com.unknown.post.configs.BaseConfiguration;
import com.unknown.post.configs.WebClientConfig;
import com.unknown.post.dtos.FullPostDTO;
import com.unknown.post.dtos.PostDTO;
import com.unknown.post.dtos.UserDTO;
import com.unknown.post.entities.Comment;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import com.unknown.post.services.PostService;
import com.unknown.post.services.ReactionService;
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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = BaseConfiguration.class)
@ActiveProfiles("test")
@Transactional
public class PostServiceITest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @MockitoBean
    private WebClientConfig webClientConfig;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReactionService reactionService;

    @Test
    void getPostByIdTest() {
        log.info("getPostByIdTest start.");
        // Создаем тестовый пост
        Post post = postRepository.save(new Post("Test Title", "Test Content", "TestAuthor"));

        // Мокируем UserService
        Mockito.when(userService.getFullUser("TestAuthor"))
                .thenReturn(new UserDTO("TestAuthor", "Test User", "test_avatar"));

        FullPostDTO result = postService.getPostById(post.getId());

        Assertions.assertEquals(post.getId(), result.post_id());
        Assertions.assertEquals("Test Title", result.title());
        Assertions.assertEquals("Test Content", result.content());
        Assertions.assertEquals("TestAuthor", result.user_id());
        Assertions.assertEquals("Test User", result.username());

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUser("TestAuthor");
    }

    @Test
    void getAllPostsTest() {
        log.info("getAllPostsTest start.");

        // Мокируем UserService
        List<String> authorIds = Arrays.asList("Alex Johnson", "Maria Garcia", "James Wilson", "Sarah Chen", "Robert Kim", "Emily Davis", "Thomas Moore", "Olivia Brown", "Daniel Lee", "Sophia Miller");
        List<UserDTO> users = authorIds.stream().map(el -> new UserDTO(el, "user_" + el, "avatar" + el))
                .collect(Collectors.toCollection(ArrayList::new));

        Mockito.when(userService.getFullUsersGroup(authorIds))
                .thenReturn(users);

        List<FullPostDTO> result = postService.getAllPosts();

        Assertions.assertEquals(10, result.size());

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUsersGroup(authorIds);
    }

    @Test
    void getPostsByAuthorTest() {
        log.info("getPostsByAuthorTest start.");
        String author = "Alex Johnson";

        // Мокируем UserService
        UserDTO userDTO = new UserDTO(author, "Test User", "test_avatar");
        Mockito.when(userService.getFullUser(author))
                .thenReturn(userDTO);

        List<FullPostDTO> result = postService.getPostsByAuthor(author);

        Assertions.assertEquals(1, result.size());
        result.forEach(post -> {
            Assertions.assertEquals(author, post.user_id());
            Assertions.assertEquals("Test User", post.username());
        });

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUser(author);
    }

    @Test
    void findPostsByTitleTest() {
        log.info("findPostsByTitleTest start.");
        String searchTerm = "The Future of AI";

        // Мокируем UserService
        List<String> authorIds = Arrays.asList("Alex Johnson");
        List<UserDTO> users = Arrays.asList(
                new UserDTO("Alex Johnson", "User One", "avatar1")
        );

        Mockito.when(userService.getFullUsersGroup(authorIds))
                .thenReturn(users);

        List<FullPostDTO> result = postService.findPostsByTitle(searchTerm);

        Assertions.assertEquals(1, result.size());
        result.forEach(post -> {
            Assertions.assertTrue(post.title().toLowerCase().contains(searchTerm.toLowerCase()));
        });

        // Проверка вызова UserService
        Mockito.verify(userService, Mockito.times(1)).getFullUsersGroup(authorIds);
    }

    @Test
    void addPostTest() {
        log.info("addPostTest start.");
        PostDTO postDTO = new PostDTO("New Title", "New Content", "TestAuthor");

        Post result = postService.addPost(postDTO);

        Assertions.assertEquals("New Title", result.getTitle());
        Assertions.assertEquals("New Content", result.getContent());
        Assertions.assertEquals("TestAuthor", result.getAuthor());

        // Проверяем сохранение в БД
        Optional<Post> savedPost = postRepository.findPostById(result.getId());
        Assertions.assertTrue(savedPost.isPresent());
    }

    @Test
    void updatePostTest() {
        log.info("updatePostTest start.");
        // Создаем тестовый пост
        Post post = postRepository.save(new Post("Original Title", "Original Content", "TestAuthor"));

        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        Post result = postService.updatePost(post.getId(), newTitle, newContent);

        Assertions.assertEquals(newTitle, result.getTitle());
        Assertions.assertEquals(newContent, result.getContent());

        // Проверяем обновление в БД
        Optional<Post> updatedPost = postRepository.findPostById(post.getId());
        Assertions.assertTrue(updatedPost.isPresent());
        Assertions.assertEquals(newTitle, updatedPost.get().getTitle());
    }

    @Test
    void delPostByIdTest() {
        log.info("delPostByIdTest start.");
        // Создаем тестовый пост с комментариями
        Post post = postRepository.save(new Post("Title", "Content", "Author"));
        List<String> comments = Arrays.asList(
                commentRepository.save(new Comment("Comment 1", "Author", post.getId())).getId(),
                commentRepository.save(new Comment("Comment 2", "Author", post.getId())).getId()
        );
        post.setComments(comments);
        postRepository.save(post);

        // Вызываем тестируемый метод
        Post deleted = postService.delPostById(post.getId());

        // Проверки
        Assertions.assertEquals(post.getId(), deleted.getId());
        Assertions.assertFalse(postRepository.findPostById(post.getId()).isPresent());

        // Проверка вызовов ReactionService
        Mockito.verify(reactionService, Mockito.times(1))
                .deleteReactionById("post", List.of(post.getId()));
        Mockito.verify(reactionService, Mockito.times(1))
                .deleteReactionById("comment", comments);

        // Проверка удаления комментариев
        comments.forEach(commentId ->
                Assertions.assertFalse(commentRepository.findCommentById(commentId).isPresent())
        );
    }

    @Test
    void delPostByAuthor_DeleteModeTest() {
        log.info("delPostByAuthor_DeleteModeTest start.");
        String author = "TestAuthor";

        // Вызываем тестируемый метод в режиме удаления
        postService.delPostByAuthor(author, "<->");

        // Проверки
        Assertions.assertEquals(0, postRepository.findPostsByAuthor(author).size());
        Assertions.assertEquals(0, commentRepository.findCommentsByAuthor(author).size());

        // Проверка вызовов ReactionService
        Mockito.verify(reactionService, Mockito.times(1))
                .deleteReactionByUser("post_reacts", author);
        Mockito.verify(reactionService, Mockito.times(1))
                .deleteReactionByUser("comment_reacts", author);
    }

    @Test
    void delPostByAuthor_ReplaceModeTest() {
        log.info("delPostByAuthor_ReplaceModeTest start.");
        String author = "TestAuthor";
        String replacement = "DeletedUser";

        // Создаем тестовые данные
        Post post = postRepository.save(new Post("Title", "Content", author));
        Comment comment = commentRepository.save(new Comment("Comment", author, post.getId()));

        // Вызываем тестируемый метод в режиме замены
        postService.delPostByAuthor(author, replacement);

        // Проверки
        Optional<Post> updatedPost = postRepository.findPostById(post.getId());
        Assertions.assertTrue(updatedPost.isPresent());
        Assertions.assertEquals(replacement, updatedPost.get().getAuthor());

        Optional<Comment> updatedComment = commentRepository.findCommentById(comment.getId());
        Assertions.assertTrue(updatedComment.isPresent());
        Assertions.assertEquals(replacement, updatedComment.get().getAuthor());

        // Проверка вызовов ReactionService
        Mockito.verify(reactionService, Mockito.times(1))
                .replaceUser("post_reacts", author, replacement);
        Mockito.verify(reactionService, Mockito.times(1))
                .replaceUser("comment_reacts", author, replacement);
    }

    @Test
    void getPostByIdNotFoundTest() {
        log.info("getPostByIdNotFoundTest start.");
        String nonExistingId = "non_existing_id";

        Assertions.assertThrows(NoSuchElementException.class,
                () -> postService.getPostById(nonExistingId));
    }
}
