package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CommentDAO;
import dto.CommentDTO;
import util.ServiceFactory;
import service.CommentService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static util.Utils.*;

@WebServlet("/comments/*")
public class CommentServlet extends HttpServlet {
    private final CommentService commentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CommentServlet() {
        this(ServiceFactory.getCommentService());
    }
    public CommentServlet(CommentService commentService) {
        this.commentService = commentService;
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
            CommentDTO comment = commentService.getCommentById(Long.parseLong(id));
            if (comment == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(COMMENT_NOT_FOUND);
            } else {
                response.getWriter().write(objectMapper.writeValueAsString(comment));
            }
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE_JSON);
        try {
            CommentDAO commentDAO = objectMapper.readValue(request.getReader(), CommentDAO.class);
            CommentDTO createdComment = commentService.createComment(commentDAO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(objectMapper.writeValueAsString(createdComment));
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
            CommentDAO commentDAO = objectMapper.readValue(request.getReader(), CommentDAO.class);
            commentDAO.setId(Long.parseLong(id));
            CommentDTO updatedComment = commentService.updateComment(commentDAO);
            response.getWriter().write(objectMapper.writeValueAsString(updatedComment));
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
            commentService.deleteComment(Long.parseLong(id));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.getWriter().write(COMMENT_DELETED);
        } catch (Exception e) {
            writeErrorResponse(response);
        }
    }

    @Override
    public void destroy() {
        ServiceFactory.close();
    }
}