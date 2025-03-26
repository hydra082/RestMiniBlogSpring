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
}
