package com.unknown.post.services;

import com.unknown.post.dtos.PostDTO;
import com.unknown.post.entities.Post;
import com.unknown.post.repositories.CommentRepository;
import com.unknown.post.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public Post getPostById(String id) {
        return postRepository.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found"));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByAuthor(String author) {
        return postRepository.findPostsByAuthor(author);
    }

    public List<Post> findPostsByTitle(String title) {
        return postRepository.findPostsByTitleContaining(title);
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
        postRepository.deletePostById(id);
        return post;
    }
}
