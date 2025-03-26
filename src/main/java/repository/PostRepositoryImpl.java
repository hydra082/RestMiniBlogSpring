package repository;

import entity.Post;
import entity.User;
import entity.Comment;
import util.DataSourceUtil;
import util.RepositoryException;

import java.sql.*;

import static util.Utils.USER_WITH_ID;

public class PostRepositoryImpl implements PostRepository {
    private final DataSourceUtil dataSourceUtil;
    private UserRepository userRepository;

    public PostRepositoryImpl(DataSourceUtil dataSourceUtil) {
        this.dataSourceUtil = dataSourceUtil;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Post findById(Long id) {
        String sql = "SELECT id, title, content, user_id FROM posts WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getLong("id"));
                    post.setTitle(rs.getString("title"));
                    post.setContent(rs.getString("content"));
                    Long userId = rs.getLong("user_id");
                    User user = userRepository.findByIdWithoutRelations(userId);
                    if (user == null) {
                        throw new RepositoryException(USER_WITH_ID + userId +
                                " not found for post " + id);
                    }
                    post.setUser(user);
                    loadComments(post);
                    return post;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error searching post by id: " + id, e);
        }
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM posts WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error checking post existence: " + id, e);
        }
    }

    @Override
    public void loadComments(Post post) {
        String sql = "SELECT id, text, user_id FROM comments WHERE post_id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, post.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getLong("id"));
                    comment.setText(rs.getString("text"));
                    comment.setPost(post);
                    Long userId = rs.getLong("user_id");
                    User user = userRepository.findByIdWithoutRelations(userId);
                    if (user == null) {
                        throw new RepositoryException(USER_WITH_ID + userId +
                                " not found for comment " + comment.getId());
                    }
                    comment.setUser(user);
                    post.getComments().add(comment);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading post comments " + post.getId(), e);
        }
    }

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO posts (title, content, user_id) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setLong(3, post.getUser().getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    post.setId(rs.getLong("id"));
                    return post;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error saving post: " + post, e);
        }
        return null;
    }

    @Override
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, content = ?, user_id = ? WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setLong(3, post.getUser().getId());
            ps.setLong(4, post.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating post: " + post.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting post by id: " + id, e);
        }
    }
}
