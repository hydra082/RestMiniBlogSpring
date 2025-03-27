package mapper;

import dao.CommentDAO;
import dto.CommentDTO;
import entity.Comment;
import entity.Post;
import entity.User;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class CommentMapperTest {

    private final CommentMapper mapper = CommentMapper.INSTANCE;

    @Test
    void shouldMapCommentDAOToEntity() {
        CommentDAO dao = new CommentDAO();
        dao.setId(1L);
        dao.setText("Test Comment");
        dao.setUserId(2L);
        dao.setPostId(3L);

        Comment entity = mapper.toEntity(dao);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), is(1L));
        assertThat(entity.getText(), is("Test Comment"));
        assertThat(entity.getUser(), notNullValue());
        assertThat(entity.getUser().getId(), is(2L));
        assertThat(entity.getPost(), notNullValue());
        assertThat(entity.getPost().getId(), is(3L));
    }

    @Test
    void shouldMapCommentToDTO() {
        User user = new User();
        user.setId(2L);

        Post post = new Post();
        post.setId(3L);

        Comment entity = new Comment();
        entity.setId(1L);
        entity.setText("Test Comment");
        entity.setUser(user);
        entity.setPost(post);

        CommentDTO dto = mapper.toDTO(entity);

        assertThat(dto, notNullValue());
        assertThat(dto.getId(), is(1L));
        assertThat(dto.getText(), is("Test Comment"));
        assertThat(dto.getUserId(), is(2L));
        assertThat(dto.getPostId(), is(3L));
    }
}