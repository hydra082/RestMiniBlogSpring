package service;

import dao.PostDAO;
import dto.PostDTO;
import entity.Post;
import entity.User;
import mapper.PostMapper;
import repository.PostRepository;
import repository.UserRepository;

import static util.Utils.NOT_FOUND;

public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    @Override
    public PostDTO createPost(PostDAO postDAO) {
        Post post = postMapper.toEntity(postDAO);
        User user = userRepository.findById(postDAO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User with id " + postDAO.getUserId() + NOT_FOUND);
        }
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        if (savedPost == null) {
            throw new IllegalStateException("Failed to save post");
        }
        return postMapper.toDTO(savedPost);
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id);
        if (post == null) {
            throw new IllegalArgumentException("Post with id " + id + NOT_FOUND);
        }
        return postMapper.toDTO(post);
    }

    @Override
    public PostDTO updatePost(PostDAO postDAO) {
        Post post = postMapper.toEntity(postDAO);
        if (post.getId() == null) {
            throw new IllegalArgumentException("Post id must not be null for update");
        }
        postRepository.update(post);
        return postMapper.toDTO(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.delete(id);
    }
}
