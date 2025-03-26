package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UserDAO;
import dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.UserService;
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
class UserServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserServlet servlet;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field userServiceField = UserServlet.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);
        userServiceField.set(servlet, userService);
    }

    @Test
    void doGetShouldReturnUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test");

        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(userService.getUserById(1L)).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(userDTO)));
        verify(userService).getUserById(1L);
    }

    @Test
    void doGetShouldReturnNotFound() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(userService.getUserById(1L)).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(Utils.USER_NOT_FOUND));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doPostShouldCreateUser() throws Exception {
        UserDAO userDAO = new UserDAO();
        userDAO.setName("Test");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test");

        StringWriter sw = new StringWriter();
        when(request.getReader()).thenReturn(new BufferedReader
                (new StringReader(mapper.writeValueAsString(userDAO))));
        when(userService.createUser(any(UserDAO.class))).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPost(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(userDTO)));
        verify(userService).createUser(any(UserDAO.class));
    }

    @Test
    void doPutShouldUpdateUser() throws Exception {
        UserDAO userDAO = new UserDAO();
        userDAO.setName("Updated");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Updated");

        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader
                (new StringReader(mapper.writeValueAsString(userDAO))));
        when(userService.updateUser(any(UserDAO.class))).thenReturn(userDTO);
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPut(request, response);

        assertThat(sw.toString(), is(mapper.writeValueAsString(userDTO)));
        verify(userService).updateUser(any(UserDAO.class));
    }

    @Test
    void doDeleteShouldRemoveUser() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/1");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        doNothing().when(userService).deleteUser(1L);

        servlet.doDelete(request, response);

        assertThat(sw.toString(), is(Utils.USER_DELETED));
        verify(userService).deleteUser(1L);
    }

    @Test
    void doGetShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doGet(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    void doPutShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPut(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(userService, never()).updateUser(any(UserDAO.class));
    }

    @Test
    void doDeleteShouldHandleMissingId() throws Exception {
        StringWriter sw = new StringWriter();
        when(request.getPathInfo()).thenReturn("/");
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doDelete(request, response);

        assertThat(sw.toString(), is(Utils.ID_NOT_SPECIFIED_ERROR));
        verify(userService, never()).deleteUser(anyLong());
    }
}
