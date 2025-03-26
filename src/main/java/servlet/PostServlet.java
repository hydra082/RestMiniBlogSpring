package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.PostDAO;
import dto.PostDTO;
import util.ServiceFactory;
import service.PostService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static util.Utils.*;

@WebServlet("/posts/*")
public class PostServlet extends HttpServlet {
    private final PostService postService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostServlet() {
        this(ServiceFactory.getPostService());
    }

    public PostServlet(PostService postService) {
        this.postService = postService;
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
            PostDTO post = postService.getPostById(Long.parseLong(id));
            if (post == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(POST_NOT_FOUND);
            } else {
                response.getWriter().write(objectMapper.writeValueAsString(post));
            }
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE_JSON);
        try {
            PostDAO postDAO = objectMapper.readValue(request.getReader(), PostDAO.class);
            PostDTO createdPost = postService.createPost(postDAO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(objectMapper.writeValueAsString(createdPost));
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
            PostDAO postDAO = objectMapper.readValue(request.getReader(), PostDAO.class);
            postDAO.setId(Long.parseLong(id));
            PostDTO updatedPost = postService.updatePost(postDAO);
            response.getWriter().write(objectMapper.writeValueAsString(updatedPost));
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
            postService.deletePost(Long.parseLong(id));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.getWriter().write(POST_DELETED);
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    public void destroy() {
        ServiceFactory.close();
    }
}