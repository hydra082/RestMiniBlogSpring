package com.myblog.service;

import com.myblog.dto.CommentDTO;
import com.myblog.entity.Comment;
import com.myblog.entity.Post;
import com.myblog.entity.User;
import com.myblog.mapper.CommentMapper;
import com.myblog.repository.CommentRepository;
import com.myblog.repository.PostRepository;
import com.myblog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
                          PostRepository postRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment with id " + id + " not found"));
        return commentMapper.toDTO(comment);
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = commentMapper.toEntity(commentDTO);
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + commentDTO.getUserId() + " not found"));
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post with id " + commentDTO.getPostId() + " not found"));
        comment.setUser(user);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    public CommentDTO updateComment(Long id, CommentDTO commentDTO) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment with id " + id + " not found"));
        existingComment.setText(commentDTO.getText());
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + commentDTO.getUserId() + " not found"));
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post with id " + commentDTO.getPostId() + " not found"));
        existingComment.setUser(user);
        existingComment.setPost(post);
        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toDTO(updatedComment);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment with id " + id + " not found");
        }
        commentRepository.deleteById(id);
    }
}