package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.PostDAO;
import dto.PostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.PostService;
import util.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostServlet servlet;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field postServiceField = PostServlet.class.getDeclaredField("postService");
        postServiceField.setAccessible(true);
        postServiceField.set(servlet, postService);
    }

    @Test
    void doGetShouldReturnPost() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("Test");

        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(postService.getPostById(1L)).thenReturn(postDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(postDTO)));
        verify(postService).getPostById(1L);
    }

    @Test
    void doGetShouldReturnNotFound() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(postService.getPostById(1L)).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(Utils.POST_NOT_FOUND));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doPostShouldCreatePost() throws Exception {
        PostDAO postDAO = new PostDAO();
        postDAO.setTitle("Test");

        PostDTO postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("Test");

        StringWriter sw = new StringWriter();
        when(request.getReader()).thenReturn(new BufferedReader
                (new StringReader(mapper.writeValueAsString(postDAO))));
        when(postService.createPost(any(PostDAO.class))).thenReturn(postDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPost(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(postDTO)));
        verify(postService).createPost(any(PostDAO.class));
    }

    @Test
    void doPutShouldUpdatePost() throws Exception {
        PostDAO postDAO = new PostDAO();
        postDAO.setTitle("Updated");

        PostDTO postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("Updated");

        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader
                (new StringReader(mapper.writeValueAsString(postDAO))));
        when(postService.updatePost(any(PostDAO.class))).thenReturn(postDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPut(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(postDTO)));
        verify(postService).updatePost(any(PostDAO.class));
    }

    @Test
    void doDeleteShouldRemovePost() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        doNothing().when(postService).deletePost(1L);

        servlet.doDelete(request, response);

        assertThat(sw.toString(), is(Utils.POST_DELETED));
        verify(postService).deletePost(1L);
    }

    @Test
    void doGetShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(postService, never()).getPostById(anyLong());
    }

    @Test
    void doPutShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPut(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(postService, never()).updatePost(any(PostDAO.class));
    }

    @Test
    void doDeleteShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doDelete(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(postService, never()).deletePost(anyLong());
    }
}
