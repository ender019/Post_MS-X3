package com.unknown.post.services;

import com.unknown.post.dtos.FullPostDTO;
import com.unknown.post.dtos.PostDTO;
import com.unknown.post.dtos.UserDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ReactionService reactionService;
    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private final String testId = "testId";
    private final String testAuthor = "authorId";
    private final Post testPost = new Post("Test Title", "Test Content", testAuthor);
    private final UserDTO testUser = new UserDTO(testAuthor, "username", "avatar");
    private final FullPostDTO expectedFullPost = new FullPostDTO(
            testAuthor,
            "username",
            "avatar",
            testId,
            "Test Title",
            "Test Content",
            testPost.getDate()
    );

    @BeforeEach
    void setup() {
        testPost.setId(testId);
    }

    @Test
    void getPostByIdTest() {
        log.info("getPostByIdTest started.");
        Mockito.doReturn(Optional.of(testPost)).when(postRepository).findPostById(testId);
        Mockito.doReturn(testUser).when(userService).getFullUser(testAuthor);

        FullPostDTO result = postService.getPostById(testId);

        Assertions.assertEquals(expectedFullPost, result);
        Mockito.verify(postRepository).findPostById(testId);
        Mockito.verify(userService).getFullUser(testAuthor);
    }

    @Test
    void getAllPostsTest() {
        log.info("getAllPostsTest started.");
        List<Post> posts = Collections.singletonList(testPost);
        List<UserDTO> users = Collections.singletonList(testUser);

        Mockito.doReturn(posts).when(postRepository).findAll();
        Mockito.doReturn(users).when(userService).getFullUsersGroup(Collections.singletonList(testAuthor));

        List<FullPostDTO> result = postService.getAllPosts();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedFullPost, result.getFirst());
        Mockito.verify(postRepository).findAll();
        Mockito.verify(userService).getFullUsersGroup(Collections.singletonList(testAuthor));
    }

    @Test
    void getPostsByAuthorTest() {
        log.info("getPostsByAuthorTest started.");
        List<Post> posts = Collections.singletonList(testPost);
        Mockito.doReturn(posts).when(postRepository).findPostsByAuthor(testAuthor);
        Mockito.doReturn(testUser).when(userService).getFullUser(testAuthor);

        List<FullPostDTO> result = postService.getPostsByAuthor(testAuthor);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedFullPost, result.getFirst());
        Mockito.verify(postRepository).findPostsByAuthor(testAuthor);
        Mockito.verify(userService).getFullUser(testAuthor);
    }

    @Test
    void findPostsByTitleTest() {
        log.info("findPostsByTitleTest started.");
        String title = "Test";
        List<Post> posts = Collections.singletonList(testPost);
        List<UserDTO> users = Collections.singletonList(testUser);

        Mockito.doReturn(posts).when(postRepository).findPostsByTitleContainingIgnoreCase(title);
        Mockito.doReturn(users).when(userService).getFullUsersGroup(Collections.singletonList(testAuthor));

        List<FullPostDTO> result = postService.findPostsByTitle(title);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedFullPost, result.getFirst());
        Mockito.verify(postRepository).findPostsByTitleContainingIgnoreCase(title);
        Mockito.verify(userService).getFullUsersGroup(Collections.singletonList(testAuthor));
    }

    @Test
    void addPostTest() {
        log.info("addPostTest started.");
        PostDTO dto = new PostDTO("New Title", "New Content", testAuthor);
        Post newPost = new Post(dto.title(), dto.content(), dto.author());

        Mockito.doReturn(newPost).when(postRepository).save(Mockito.any(Post.class));

        Post result = postService.addPost(dto);

        Assertions.assertEquals(newPost, result);
        Mockito.verify(postRepository).save(Mockito.any(Post.class));
    }

    @Test
    void updatePostTest() {
        log.info("updatePostTest started.");
        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        Mockito.doReturn(Optional.of(testPost)).when(postRepository).findPostById(testId);
        Mockito.doReturn(testPost).when(postRepository).save(testPost);

        Post result = postService.updatePost(testId, newTitle, newContent);

        Assertions.assertEquals(newTitle, result.getTitle());
        Assertions.assertEquals(newContent, result.getContent());
        Mockito.verify(postRepository).findPostById(testId);
        Mockito.verify(postRepository).save(testPost);
    }

    @Test
    void delPostByIdTest() {
        log.info("delPostByIdTest started.");
        List<String> comments = List.of("comment1", "comment2");
        testPost.setComments(comments);

        Mockito.doReturn(Optional.of(testPost)).when(postRepository).findPostById(testId);

        Post result = postService.delPostById(testId);

        Assertions.assertEquals(testPost, result);
        Mockito.verify(commentRepository).deleteAllById(comments);
        Mockito.verify(reactionService).deleteReactionById("post", Collections.singletonList(testId));
        Mockito.verify(reactionService).deleteReactionById("comment", comments);
        Mockito.verify(postRepository).deletePostById(testId);
    }

    @Test
    void delPostByAuthor_DeleteModeTest() {
        log.info("delPostByAuthor_DeleteModeTest started.");
        postService.delPostByAuthor(testAuthor);

        Mockito.verify(postRepository).deletePostsByAuthor(testAuthor);
        Mockito.verify(commentRepository).deleteCommentByAuthor(testAuthor);
        Mockito.verify(reactionService).deleteReactionByUser("post_reacts", testAuthor);
        Mockito.verify(reactionService).deleteReactionByUser("comment_reacts", testAuthor);
    }
}