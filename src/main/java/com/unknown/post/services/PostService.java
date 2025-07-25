package com.unknown.post.services;

import com.unknown.post.dtos.FullPostDTO;
import com.unknown.post.dtos.PostDTO;
import com.unknown.post.dtos.UserDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionService reactionService;
    private final UserService userService;

    public FullPostDTO match(Post post, UserDTO user) {
        return new FullPostDTO(
                post.getAuthor(),
                user.username(),
                user.avatar(),
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getDate()
        );
    }

    public List<FullPostDTO> matchGroup(List<Post> posts, List<UserDTO> users) {
        posts.sort(Comparator.comparing(Post::getAuthor));
        users.sort(Comparator.comparing(UserDTO::id));
        List<FullPostDTO> result = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) result.add(match(posts.get(i), users.get(i)));
        return result;
    }

    public FullPostDTO getPostById(String id) {
        var post = postRepository.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
        var user = userService.getFullUser(post.getAuthor());
        log.debug("Post data: {}", post);
        log.debug("User data: {}", user);
        return match(post, user);
    }

    public List<FullPostDTO> getAllPosts() {
        var posts = postRepository.findAll();
        var users = userService.getFullUsersGroup(posts.stream().map(Post::getAuthor).toList());
        return matchGroup(posts, users);
    }

    public List<FullPostDTO> getPostsByAuthor(String author) {
        var posts = postRepository.findPostsByAuthor(author);
        var user = userService.getFullUser(author);
        return posts.stream().map(el -> match(el, user)).toList();
    }

    public List<FullPostDTO> findPostsByTitle(String title) {
        var posts = postRepository.findPostsByTitleContainingIgnoreCase(title);
        log.debug("Posts data: {}", posts);
        var users = userService.getFullUsersGroup(posts.stream().map(Post::getAuthor).toList());
        log.debug("Users data: {}", users);
        return matchGroup(posts, users);
    }

    @Transactional
    public Post addPost(PostDTO data) {
        return postRepository.save(new Post(data.title(), data.content(), data.author()));
    }

    @Transactional
    public Post updatePost(String id, String title, String content) {
        var post = postRepository.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    @Transactional
    public Post delPostById(String id) {
        var post = postRepository.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
        if(post.getComments() != null && !post.getComments().isEmpty())
            commentRepository.deleteAllById(post.getComments());
        reactionService.deleteReactionById("post", List.of(id));
        reactionService.deleteReactionById("comment", post.getComments());
        postRepository.deletePostById(id);
        return post;
    }

    @Transactional
    public void delPostByAuthor(String user_id, String deleted) {
        if (deleted.equals("<->")) {
            log.debug("Deleting post by author");
            postRepository.deletePostsByAuthor(user_id);
            commentRepository.deleteCommentByAuthor(user_id);
            reactionService.deleteReactionByUser("post_reacts", user_id);
            reactionService.deleteReactionByUser("comment_reacts", user_id);
        } else {
            log.debug("Deleting author: {}", user_id);
            postRepository.updateAuthorByAuthor(user_id, deleted);
            commentRepository.updateAuthorByAuthor(user_id, deleted);
            reactionService.replaceUser("post_reacts", user_id, deleted);
            reactionService.replaceUser("comment_reacts", user_id, deleted);
        }
    }
}
