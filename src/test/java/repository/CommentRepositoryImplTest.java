package repository;

import entity.Comment;
import entity.Post;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers
class CommentRepositoryImplTest extends TestDatabaseSetup {

    private CommentRepositoryImpl commentRepository;
    private UserRepositoryImpl userRepository;
    private PostRepositoryImpl postRepository;

    @BeforeEach
    void setup() {
        try (Connection conn = dataSourceUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE comments, posts, users RESTART IDENTITY CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to truncate tables", e);
        }

        userRepository = new UserRepositoryImpl(dataSourceUtil);
        postRepository = new PostRepositoryImpl(dataSourceUtil);
        commentRepository = new CommentRepositoryImpl(dataSourceUtil, userRepository, postRepository);
        postRepository.setUserRepository(userRepository);
        userRepository.setPostRepository(postRepository);
    }

    @Test
    void shouldSaveAndFindComment() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        Comment comment = new Comment();
        comment.setText("Test Comment");
        comment.setUser(user);
        comment.setPost(post);

        Comment saved = commentRepository.save(comment);
        Comment found = commentRepository.findById(saved.getId());

        assertThat(saved.getId(), notNullValue());
        assertThat(found, notNullValue());
        assertThat(found.getText(), is("Test Comment"));
    }

    @Test
    void shouldUpdateComment() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        Comment comment = new Comment();
        comment.setText("Initial Comment");
        comment.setUser(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);

        comment.setText("Updated Comment");
        commentRepository.update(comment);

        Comment updated = commentRepository.findById(comment.getId());
        assertThat(updated.getText(), is("Updated Comment"));
    }

    @Test
    void shouldDeleteComment() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        Comment comment = new Comment();
        comment.setText("Test Comment");
        comment.setUser(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);

        commentRepository.delete(comment.getId());
        assertThat(commentRepository.existsById(comment.getId()), is(false));
    }

    @Test
    void shouldReturnNullWhenCommentNotFound() {
        Comment found = commentRepository.findById(999L);
        assertThat(found, nullValue());
    }

    @Test
    void shouldCheckCommentExists() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        Comment comment = new Comment();
        comment.setText("Test Comment");
        comment.setUser(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);

        assertThat(commentRepository.existsById(comment.getId()), is(true));
        assertThat(commentRepository.existsById(999L), is(false));
    }
}
