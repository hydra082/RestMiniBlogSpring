package com.myblog.service;

import com.myblog.dto.PostDTO;
import com.myblog.entity.Post;
import com.myblog.entity.User;
import com.myblog.mapper.PostMapper;
import com.myblog.repository.PostRepository;
import com.myblog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with id " + id + " not found"));
        return postMapper.toDTO(post);
    }

    public PostDTO createPost(PostDTO postDTO) {
        Post post = postMapper.toEntity(postDTO);
        User user = userRepository.findById(postDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + postDTO.getUserId() + " not found"));
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    public PostDTO updatePost(Long id, PostDTO postDTO) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post with id " + id + " not found"));
        existingPost.setTitle(postDTO.getTitle());
        existingPost.setContent(postDTO.getContent());
        User user = userRepository.findById(postDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + postDTO.getUserId() + " not found"));
        existingPost.setUser(user);
        Post updatedPost = postRepository.save(existingPost);
        return postMapper.toDTO(updatedPost);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post with id " + id + " not found");
        }
        postRepository.deleteById(id);
    }
}