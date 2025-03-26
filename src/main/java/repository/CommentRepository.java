package repository;

import entity.Comment;

public interface CommentRepository {
    Comment findById(Long id);
    boolean existsById(Long id);
    Comment save(Comment comment);
    void update(Comment comment);
    void delete(Long id);
}
