package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CommentDAO;
import dto.CommentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CommentService;
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
class CommentServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentServlet servlet;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field commentServiceField = CommentServlet.class.getDeclaredField("commentService");
        commentServiceField.setAccessible(true);
        commentServiceField.set(servlet, commentService);
    }

    @Test
    void doGetShouldReturnComment() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Test");

        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(commentService.getCommentById(1L)).thenReturn(commentDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(commentDTO)));
        verify(commentService).getCommentById(1L);
    }

    @Test
    void doGetShouldReturnNotFound() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(commentService.getCommentById(1L)).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(Utils.COMMENT_NOT_FOUND));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doPostShouldCreateComment() throws Exception {
        CommentDAO commentDAO = new CommentDAO();
        commentDAO.setText("Test");

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Test");

        StringWriter sw = new StringWriter();
        when(request.getReader()).thenReturn(new BufferedReader
                (new StringReader(mapper.writeValueAsString(commentDAO))));
        when(commentService.createComment(any(CommentDAO.class))).thenReturn(commentDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPost(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(commentDTO)));
        verify(commentService).createComment(any(CommentDAO.class));
    }

    @Test
    void doPutShouldUpdateComment() throws Exception {
        CommentDAO commentDAO = new CommentDAO();
        commentDAO.setText("Updated");

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("Updated");

        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader
                (new StringReader(mapper.writeValueAsString(commentDAO))));
        when(commentService.updateComment(any(CommentDAO.class))).thenReturn(commentDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPut(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(commentDTO)));
        verify(commentService).updateComment(any(CommentDAO.class));
    }

    @Test
    void doDeleteShouldRemoveComment() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        doNothing().when(commentService).deleteComment(1L);

        servlet.doDelete(request, response);

        assertThat(sw.toString(), is(Utils.COMMENT_DELETED));
        verify(commentService).deleteComment(1L);
    }

    @Test
    void doGetShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(commentService, never()).getCommentById(anyLong());
    }

    @Test
    void doPutShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPut(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(commentService, never()).updateComment(any(CommentDAO.class));
    }

    @Test
    void doDeleteShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doDelete(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(commentService, never()).deleteComment(anyLong());
    }
}
