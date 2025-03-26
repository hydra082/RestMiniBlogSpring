package service;

import dao.CommentDAO;
import dto.CommentDTO;

public interface CommentService {
    CommentDTO getCommentById(Long id);
    CommentDTO createComment(CommentDAO commentDAO);
    CommentDTO updateComment(CommentDAO commentDAO);
    void deleteComment(Long id);
}
