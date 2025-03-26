package repository;

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
class PostRepositoryImplTest extends TestDatabaseSetup {

    private PostRepositoryImpl postRepository;
    private UserRepositoryImpl userRepository;

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
        postRepository.setUserRepository(userRepository);
        userRepository.setPostRepository(postRepository);
    }

    @Test
    void shouldSaveAndFindPost() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);

        Post saved = postRepository.save(post);
        Post found = postRepository.findById(saved.getId());

        assertThat(saved.getId(), notNullValue());
        assertThat(found, notNullValue());
        assertThat(found.getTitle(), is("Test Post"));
    }

    @Test
    void shouldUpdatePost() {
        User user = new User();
        user.setName("User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Initial");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        post.setTitle("Updated");
        postRepository.update(post);

        Post updated = postRepository.findById(post.getId());
        assertThat(updated.getTitle(), is("Updated"));
    }

    @Test
    void shouldDeletePost() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        postRepository.delete(post.getId());
        assertThat(postRepository.existsById(post.getId()), is(false));
    }

    @Test
    void shouldReturnNullWhenPostNotFound() {
        Post found = postRepository.findById(999L);
        assertThat(found, nullValue());
    }

    @Test
    void shouldCheckPostExists() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        assertThat(postRepository.existsById(post.getId()), is(true));
        assertThat(postRepository.existsById(999L), is(false));
    }

    @Test
    void shouldLoadComments() {
        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setUser(user);
        post = postRepository.save(post);

        try (Connection conn = dataSourceUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO comments (text, user_id, post_id) VALUES " +
                    "('Test Comment', " + user.getId() + ", " + post.getId() + ")");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert comment", e);
        }

        postRepository.loadComments(post);
        assertThat(post.getComments(), hasSize(1));
        assertThat(post.getComments().get(0).getText(), is("Test Comment"));
    }
}
