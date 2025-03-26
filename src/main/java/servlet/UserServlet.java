package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UserDAO;
import dto.UserDTO;
import util.ServiceFactory;
import service.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static util.Utils.*;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserServlet() {
        this(ServiceFactory.getUserService());
    }
    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE_JSON);
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.getWriter().write(ID_NOT_SPECIFIED_ERROR);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String id = pathInfo.substring(1);
            UserDTO user = userService.getUserById(Long.parseLong(id));
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(USER_NOT_FOUND);
            } else {
                response.getWriter().write(objectMapper.writeValueAsString(user));
            }
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE_JSON);
        try {
            UserDAO userDAO = objectMapper.readValue(request.getReader(), UserDAO.class);
            UserDTO createdUser = userService.createUser(userDAO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(objectMapper.writeValueAsString(createdUser));
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE_JSON);
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.getWriter().write(ID_NOT_SPECIFIED_ERROR);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String id = pathInfo.substring(1);
            UserDAO userDAO = objectMapper.readValue(request.getReader(), UserDAO.class);
            userDAO.setId(Long.parseLong(id));
            UserDTO updatedUser = userService.updateUser(userDAO);
            response.getWriter().write(objectMapper.writeValueAsString(updatedUser));
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE_JSON);
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.getWriter().write(ID_NOT_SPECIFIED_ERROR);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String id = pathInfo.substring(1);
            userService.deleteUser(Long.parseLong(id));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.getWriter().write(USER_DELETED);
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    public void destroy() {
        ServiceFactory.close();
    }
}