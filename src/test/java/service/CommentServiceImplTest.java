package service;

import dao.CommentDAO;
import dto.CommentDTO;
import entity.Comment;
import entity.Post;
import entity.User;
import mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CommentRepository;
import repository.PostRepository;
import repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void shouldGetCommentById() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Test Comment");

        when(commentRepository.findById(1L)).thenReturn(comment);
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);

        CommentDTO result = commentService.getCommentById(1L);

        assertThat(result, notNullValue());
        assertThat(result.getText(), is("Test Comment"));
        verify(commentRepository).findById(1L);
        verify(commentMapper).toDTO(comment);
    }

    @Test
    void shouldCreateComment() {
        CommentDAO commentDAO = new CommentDAO();
        commentDAO.setText("Test");
        commentDAO.setUserId(1L);
        commentDAO.setPostId(1L);

        User user = new User();
        user.setId(1L);

        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setText("Test");
        comment.setUser(user);
        comment.setPost(post);

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("Test");
        savedComment.setUser(user);
        savedComment.setPost(post);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Test");

        when(commentMapper.toEntity(commentDAO)).thenReturn(comment);
        when(userRepository.findById(1L)).thenReturn(user);
        when(postRepository.findById(1L)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toDTO(savedComment)).thenReturn(commentDTO);

        CommentDTO result = commentService.createComment(commentDAO);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(1L));
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDTO(savedComment);
    }

    @Test
    void shouldUpdateComment() {
        CommentDAO commentDAO = new CommentDAO();
        commentDAO.setId(1L);
        commentDAO.setText("Updated");
        commentDAO.setUserId(1L);
        commentDAO.setPostId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Updated");

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Updated");

        when(commentMapper.toEntity(commentDAO)).thenReturn(comment);
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);
        doNothing().when(commentRepository).update(any(Comment.class));

        CommentDTO result = commentService.updateComment(commentDAO);

        assertThat(result.getText(), is("Updated"));
        verify(commentRepository).update(any(Comment.class));
        verify(commentMapper).toDTO(comment);
    }

    @Test
    void shouldDeleteComment() {
        doNothing().when(commentRepository).delete(1L);
        commentService.deleteComment(1L);
        verify(commentRepository).delete(1L);
    }
}
