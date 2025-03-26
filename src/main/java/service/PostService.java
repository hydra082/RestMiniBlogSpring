package service;

import dao.PostDAO;
import dto.PostDTO;

public interface PostService {
    PostDTO getPostById(Long id);
    PostDTO createPost(PostDAO postDAO);
    PostDTO updatePost(PostDAO postDAO);
    void deletePost(Long id);
}
