package service;

import dao.PostDAO;
import dto.PostDTO;
import entity.Post;
import entity.User;
import mapper.PostMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.PostRepository;
import repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void shouldCreatePost() {
        PostDAO postDAO = new PostDAO();
        postDAO.setTitle("Test");
        postDAO.setUserId(1L);

        User user = new User();
        user.setId(1L);

        Post post = new Post();
        post.setTitle("Test");
        post.setUser(user);

        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setTitle("Test");
        savedPost.setUser(user);

        PostDTO postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("Test");

        when(postMapper.toEntity(postDAO)).thenReturn(post);
        when(userRepository.findById(1L)).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(postMapper.toDTO(savedPost)).thenReturn(postDTO);

        PostDTO result = postService.createPost(postDAO);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(1L));
        verify(postRepository).save(any(Post.class));
        verify(postMapper).toDTO(savedPost);
    }
    @Test
    void shouldGetPostById() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");

        PostDTO postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("Test Post");

        when(postRepository.findById(1L)).thenReturn(post);
        when(postMapper.toDTO(post)).thenReturn(postDTO);

        PostDTO result = postService.getPostById(1L);

        assertThat(result, notNullValue());
        assertThat(result.getTitle(), is("Test Post"));
        verify(postRepository).findById(1L);
        verify(postMapper).toDTO(post);
    }
    @Test
    void shouldUpdatePost() {
        PostDAO postDAO = new PostDAO();
        postDAO.setId(1L);
        postDAO.setTitle("Updated");
        postDAO.setUserId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Updated");

        PostDTO postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("Updated");

        when(postMapper.toEntity(postDAO)).thenReturn(post);
        when(postMapper.toDTO(post)).thenReturn(postDTO);
        doNothing().when(postRepository).update(any(Post.class));

        PostDTO result = postService.updatePost(postDAO);

        assertThat(result.getTitle(), is("Updated"));
        verify(postRepository).update(any(Post.class));
        verify(postMapper).toDTO(post);
    }
    @Test
    void shouldDeletePost() {
        doNothing().when(postRepository).delete(1L);
        postService.deletePost(1L);
        verify(postRepository).delete(1L);
    }
}
