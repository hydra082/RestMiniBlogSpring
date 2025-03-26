package util;

import mapper.CommentMapper;
import mapper.PostMapper;
import mapper.UserMapper;
import repository.CommentRepository;
import repository.CommentRepositoryImpl;
import repository.PostRepository;
import repository.PostRepositoryImpl;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import service.CommentService;
import service.CommentServiceImpl;
import service.PostService;
import service.PostServiceImpl;
import service.UserService;
import service.UserServiceImpl;

public class ServiceFactory {

    private static final DataSourceUtil dataSourceUtil = new DataSourceUtil();
    private static final UserRepository userRepository = new UserRepositoryImpl(dataSourceUtil);
    private static final PostRepository postRepository = new PostRepositoryImpl(dataSourceUtil);
    private static final CommentRepository commentRepository = new CommentRepositoryImpl(dataSourceUtil, userRepository, postRepository);
    private static final UserService userService = new UserServiceImpl(userRepository, UserMapper.INSTANCE);
    private static final PostService postService = new PostServiceImpl(postRepository, userRepository, PostMapper.INSTANCE);
    private static final CommentService commentService = new CommentServiceImpl(commentRepository, userRepository, postRepository, CommentMapper.INSTANCE);

    static {
        postRepository.setUserRepository(userRepository);
        userRepository.setPostRepository(postRepository);
    }

    private ServiceFactory() {
        throw new AssertionError("Utility class. Cannot have instances.");
    }

    public static UserService getUserService() {
        return userService;
    }

    public static PostService getPostService() {
        return postService;
    }

    public static CommentService getCommentService() {
        return commentService;
    }

    public static void close() {
        dataSourceUtil.close();
    }
}
