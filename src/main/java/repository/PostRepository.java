package repository;

import entity.Post;

public interface PostRepository {
    Post findById(Long id);
    boolean existsById(Long id);
    void loadComments(Post post);
    Post save(Post post);
    void update(Post post);
    void delete(Long id);
    void setUserRepository(UserRepository userRepository);
}
