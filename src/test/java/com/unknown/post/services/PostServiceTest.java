package com.unknown.post.services;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void getPostByIdTest() {
        log.info("getPostByIdTest start.");
        String id = "id";
        var post = new Post("title", "content", "author");
        Mockito.doReturn(Optional.of(post)).when(postRepository).findPostById(id);
        var res = postService.getPostById(id);
        Assertions.assertEquals(post, res);
        Mockito.verify(postRepository, Mockito.times(1)).findPostById(id);
    }

    @Test
    void getUnexistPostByIdTest() {
        log.info("getUnexistPostByIdTest start.");
        String id = "id";
        Mockito.doReturn(Optional.empty()).when(postRepository).findPostById(id);
        Assertions.assertThrows(NoSuchElementException.class, () -> postService.getPostById(id));
        Mockito.verify(postRepository, Mockito.times(1)).findPostById(id);
    }

    @Test
    void findAllPostsTest() {
        log.info("findAllPostsTest start.");
        var post = new Post("title", "content", "author");
        Mockito.doReturn(List.of(post)).when(postRepository).findAll();
        var res = postService.getAllPosts();
        Assertions.assertEquals(List.of(post), res);
        Mockito.verify(postRepository, Mockito.times(1)).findAll();
    }

    @Test
    void findPostsByTitleTest() {
        log.info("findPostsByTitleTest start.");
        String title = "tit";
        var post = new Post("title", "content", "author");
        Mockito.doReturn(List.of(post)).when(postRepository).findPostsByTitleContaining(title);
        var res = postService.findPostsByTitle(title);
        Assertions.assertEquals(List.of(post), res);
        Mockito.verify(postRepository, Mockito.times(1)).findPostsByTitleContaining(title);
    }

    @Test
    void addPostTest() {
        log.info("addPostTest start.");
        var posted = new Post("title", "content", "author");
        var data = new PostDTO(posted.getTitle(), posted.getContent(), posted.getAuthor());
        Mockito.when(postRepository.save(Mockito.any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var res = postService.addPost(data);
        Assertions.assertEquals(data.title(), res.getTitle());
        Assertions.assertEquals(data.content(), res.getContent());
        Assertions.assertEquals(data.author(), res.getAuthor());

        Mockito.verify(postRepository).save(Mockito.argThat(post ->
                "title".equals(post.getTitle()) &&
                "content".equals(post.getContent()) &&
                "author".equals(post.getAuthor())
        ));
    }

    @Test
    void delPostByIdTest() {
        log.info("delPostByIdTest start.");
        String id = "id";
        var post = new Post("title", "content", "author");
        Mockito.doReturn(Optional.of(post)).when(postRepository).findPostById(id);
        Mockito.doNothing().when(postRepository).deletePostById(id);
        var res = postService.delPostById(id);
        Assertions.assertEquals(post.toString(), res);
        Mockito.verify(postRepository, Mockito.times(1)).findPostById(id);
        Mockito.verify(postRepository, Mockito.times(1)).deletePostById(id);
    }

    @Test
    void delUnexistPostByIdTest() {
        log.info("delUnexistPostByIdTest start.");
        String id = "id";
        Mockito.doReturn(Optional.empty()).when(postRepository).findPostById(id);
        Assertions.assertThrows(NoSuchElementException.class, () -> postService.delPostById(id));
        Mockito.verify(postRepository, Mockito.times(1)).findPostById(id);
        Mockito.verifyNoMoreInteractions(postRepository);
    }
}