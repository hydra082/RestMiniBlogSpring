package repository;

import entity.User;

public interface UserRepository {
    User findById(Long id);
    User findByIdWithoutRelations(Long id);
    boolean existsById(Long id);
    User save(User user);
    void update(User user);
    void delete(Long id);
    void setPostRepository(PostRepository postRepository);
}
