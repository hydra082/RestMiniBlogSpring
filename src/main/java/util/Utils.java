package util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class Utils {
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String ERROR_RESPONSE = "{\"Error\": \"Something went wrong\"}";
    public static final String ID_NOT_SPECIFIED_ERROR = "{\"error\": \"ID not specified\"}";
    public static final String USER_WITH_ID = "User with ID ";
    public static final String NOT_FOUND = " not found";
    public static final String COMMENT_NOT_FOUND = "{\"error\": \"Comment was not found\"}";
    public static final String COMMENT_DELETED = "{\"message\": \"Comment deleted\"}";
    public static final String POST_NOT_FOUND = "{\"error\": \"The post was not found\"}";
    public static final String POST_DELETED = "{\"message\": \"Post deleted\"}";
    public static final String USER_NOT_FOUND = "{\"error\": \"The user was not found\"}";
    public static final String USER_DELETED = "{\"message\": \"User deleted\"}";
    private Utils() {
    }

    public static void writeErrorResponse(HttpServletResponse response) {
        try {
            response.getWriter().write(ERROR_RESPONSE);
        } catch (IOException ioException) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
