package repository;

import entity.User;
import entity.Post;
import entity.Comment;
import util.DataSourceUtil;
import util.RepositoryException;

import java.sql.*;

import static util.Utils.USER_WITH_ID;

public class UserRepositoryImpl implements UserRepository {
    private final DataSourceUtil dataSourceUtil;
    private PostRepository postRepository;

    public UserRepositoryImpl(DataSourceUtil dataSourceUtil) {
        this.dataSourceUtil = dataSourceUtil;
    }

    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public User findById(Long id) {
        User user = findByIdWithoutRelations(id);
        if (user != null) {
            loadPosts(user);
            loadComments(user);
        }
        return user;
    }

    @Override
    public User findByIdWithoutRelations(Long id) {
        String sql = "SELECT id, name FROM users WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error searching user by ID: " + id, e);
        }
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error checking user existence: " + id, e);
        }
    }

    private void loadPosts(User user) {
        String sql = "SELECT id, title, content, user_id FROM posts WHERE user_id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getLong("id"));
                    post.setTitle(rs.getString("title"));
                    post.setContent(rs.getString("content"));
                    Long userId = rs.getLong("user_id");
                    User postUser = findByIdWithoutRelations(userId);
                    if (postUser == null) {
                        throw new RepositoryException(USER_WITH_ID + userId +
                                "not found for post " + post.getId());
                    }
                    post.setUser(postUser);
                    postRepository.loadComments(post);
                    user.getPosts().add(post);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading user posts " + user.getId(), e);
        }
    }

    private void loadComments(User user) {
        String sql = "SELECT id, text, post_id, user_id FROM comments WHERE user_id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getLong("id"));
                    comment.setText(rs.getString("text"));
                    Long userId = rs.getLong("user_id");
                    Long postId = rs.getLong("post_id");
                    User commentUser = findByIdWithoutRelations(userId);
                    if (commentUser == null) {
                        throw new RepositoryException(USER_WITH_ID + userId +
                                " not found for comment " + comment.getId());
                    }
                    Post post = new Post();
                    post.setId(postId);
                    User postUser = findByIdWithoutRelations(userId);
                    if (postUser == null) {
                        throw new RepositoryException(USER_WITH_ID + userId +
                                " not found for post " + postId);
                    }
                    post.setUser(postUser);
                    comment.setUser(commentUser);
                    comment.setPost(post);
                    user.getComments().add(comment);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading user comments " + user.getId(), e);
        }
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (name) VALUES (?) RETURNING id";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error saving user " + user, e);
        }
        return null;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setLong(2, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating user " + user.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting user by id: " + id, e);
        }
    }
}
