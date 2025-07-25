package com.unknown.post.services;

import com.unknown.post.dtos.FullCommentDTO;
import com.unknown.post.dtos.UserDTO;
import com.unknown.post.entities.Comment;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ReactionService reactionService;
    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    private final String testId = "commentId";
    private final String testPostId = "postId";
    private final String testAuthor = "authorId";
    private final Comment testComment = new Comment("Test content", testAuthor, testPostId);
    private final UserDTO testUser = new UserDTO(testAuthor, "username", "avatar");
    private final FullCommentDTO expectedFullComment = new FullCommentDTO(
            testAuthor,
            "username",
            "avatar",
            testId,
            "Test content",
            testComment.getDate()
    );

    @Test
    void getCommentByIdTest() {
        testComment.setId(testId);
        Mockito.doReturn(Optional.of(testComment)).when(commentRepository).findCommentById(testId);
        Mockito.doReturn(testUser).when(userService).getFullUser(testAuthor);

        FullCommentDTO result = commentService.getCommentById(testId);

        Mockito.verify(commentRepository).findCommentById(testId);
        Mockito.verify(userService).getFullUser(testAuthor);
        Assertions.assertEquals(expectedFullComment, result);
    }

    @Test
    void getCommentsByAuthorTest() {
        testComment.setId(testId);
        List<Comment> comments = Collections.singletonList(testComment);
        List<UserDTO> users = Collections.singletonList(testUser);

        Mockito.doReturn(comments).when(commentRepository).findCommentsByAuthor(testAuthor);
        Mockito.doReturn(users).when(userService).getFullUsersGroup(Collections.singletonList(testAuthor));

        List<FullCommentDTO> result = commentService.getCommentsByAuthor(testAuthor);

        Mockito.verify(commentRepository).findCommentsByAuthor(testAuthor);
        Mockito.verify(userService).getFullUsersGroup(Collections.singletonList(testAuthor));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedFullComment, result.getFirst());
    }

    @Test
    void getCommentsByPostTest() {
        testComment.setId(testId);
        Post testPost = new Post("Title", "Content", "postAuthor");
        testPost.setComments(Collections.singletonList(testId));

        Mockito.doReturn(Optional.of(testPost)).when(postRepository).findPostById(testPostId);
        Mockito.doReturn(Collections.singletonList(testComment)).when(commentRepository).findAllById(Collections.singletonList(testId));
        Mockito.doReturn(Collections.singletonList(testUser)).when(userService).getFullUsersGroup(Collections.singletonList(testAuthor));

        List<FullCommentDTO> result = commentService.getCommentsByPost(testPostId);

        Mockito.verify(postRepository).findPostById(testPostId);
        Mockito.verify(commentRepository).findAllById(Collections.singletonList(testId));
        Mockito.verify(userService).getFullUsersGroup(Collections.singletonList(testAuthor));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedFullComment, result.getFirst());
    }

    @Test
    void addCommentTest() {
        testComment.setId(testId);
        Post testPost = new Post("Title", "Content", "postAuthor");
        testPost.setComments(new ArrayList<>());

        Mockito.doReturn(Optional.of(testPost)).when(postRepository).findPostById(testPostId);
        Mockito.doReturn(testComment).when(commentRepository).save(Mockito.any(Comment.class));

        Comment result = commentService.addComment("Test content", testAuthor, testPostId);

        Mockito.verify(postRepository).findPostById(testPostId);
        Mockito.verify(commentRepository).save(Mockito.any(Comment.class));
        Mockito.verify(postRepository).save(testPost);
        Assertions.assertEquals(testComment, result);
        Assertions.assertTrue(testPost.getComments().contains(testId));
    }

    @Test
    void updateCommentTest() {
        testComment.setId(testId);
        String newContent = "Updated content";

        Mockito.doReturn(Optional.of(testComment)).when(commentRepository).findCommentById(testId);
        Mockito.doReturn(testComment).when(commentRepository).save(testComment);

        Comment result = commentService.updateComment(testId, newContent);

        Mockito.verify(commentRepository).findCommentById(testId);
        Mockito.verify(commentRepository).save(testComment);
        Assertions.assertEquals(newContent, result.getContent());
    }

    @Test
    void deleteCommentTest() {
        testComment.setId(testId);
        Post testPost = new Post("Title", "Content", "postAuthor");
        testPost.setComments(new ArrayList<>(Collections.singletonList(testId)));

        Mockito.doReturn(Optional.of(testComment)).when(commentRepository).findCommentById(testId);
        Mockito.doReturn(Optional.of(testPost)).when(postRepository).findPostById(testPostId);

        Comment result = commentService.deleteComment(testId);

        Mockito.verify(commentRepository).findCommentById(testId);
        Mockito.verify(postRepository).findPostById(testPostId);
        Mockito.verify(postRepository).save(testPost);
        Mockito.verify(reactionService).deleteReactionById("comment", Collections.singletonList(testId));
        Mockito.verify(commentRepository).deleteCommentById(testId);
        Assertions.assertEquals(testComment, result);
        Assertions.assertFalse(testPost.getComments().contains(testId));
    }
}