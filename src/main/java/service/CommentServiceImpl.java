package service;

import dao.CommentDAO;
import dto.CommentDTO;
import entity.Comment;
import entity.Post;
import entity.User;
import mapper.CommentMapper;
import repository.CommentRepository;
import repository.PostRepository;
import repository.UserRepository;

import static util.Utils.NOT_FOUND;

public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository,
                              PostRepository postRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new IllegalArgumentException("Comment with id " + id + NOT_FOUND);
        }
        return commentMapper.toDTO(comment);
    }

    @Override
    public CommentDTO createComment(CommentDAO commentDAO) {
        Comment comment = commentMapper.toEntity(commentDAO);
        User user = userRepository.findById(commentDAO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User with id " + commentDAO.getUserId() + NOT_FOUND);
        }
        Post post = postRepository.findById(commentDAO.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("Post with id " + commentDAO.getPostId() + NOT_FOUND);
        }
        comment.setUser(user);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        if (savedComment == null) {
            throw new IllegalStateException("Failed to save comment");
        }
        return commentMapper.toDTO(savedComment);
    }

    @Override
    public CommentDTO updateComment(CommentDAO commentDAO) {
        Comment comment = commentMapper.toEntity(commentDAO);
        if (comment.getId() == null) {
            throw new IllegalArgumentException("Comment id must not be null for update");
        }
        commentRepository.update(comment);
        return commentMapper.toDTO(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.delete(id);
    }
}
