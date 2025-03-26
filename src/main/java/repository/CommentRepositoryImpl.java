package repository;

import entity.Comment;
import entity.Post;
import entity.User;
import util.DataSourceUtil;
import util.RepositoryException;

import java.sql.*;

import static util.Utils.USER_WITH_ID;

public class CommentRepositoryImpl implements CommentRepository {
    private final DataSourceUtil dataSourceUtil;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentRepositoryImpl(DataSourceUtil dataSourceUtil,
                                 UserRepository userRepository, PostRepository postRepository) {
        this.dataSourceUtil = dataSourceUtil;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment findById(Long id) {
        String sql = "SELECT id, text, user_id, post_id FROM comments WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getLong("id"));
                    comment.setText(rs.getString("text"));
                    Long userId = rs.getLong("user_id");
                    Long postId = rs.getLong("post_id");
                    User user = userRepository.findByIdWithoutRelations(userId);
                    if (user == null) {
                        throw new RepositoryException(USER_WITH_ID + userId +
                                " not found for comment " + id);
                    }
                    Post post = postRepository.findById(postId);
                    if (post == null) {
                        throw new RepositoryException("Post with ID " + postId +
                                " not found for comment " + id);
                    }
                    comment.setUser(user);
                    comment.setPost(post);
                    return comment;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error searching comment by id: " + id, e);
        }
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM comments WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error checking existence of comment: " + id, e);
        }
    }

    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (text, user_id, post_id) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment.getText());
            ps.setLong(2, comment.getUser().getId());
            ps.setLong(3, comment.getPost().getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    comment.setId(rs.getLong("id"));
                    return comment;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error saving comment: " + comment, e);
        }
        return null;
    }

    @Override
    public void update(Comment comment) {
        String sql = "UPDATE comments SET text = ?, user_id = ?, post_id = ? WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment.getText());
            ps.setLong(2, comment.getUser().getId());
            ps.setLong(3, comment.getPost().getId());
            ps.setLong(4, comment.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating comment: " + comment.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = dataSourceUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting comment by id: " + id, e);
        }
    }
}
