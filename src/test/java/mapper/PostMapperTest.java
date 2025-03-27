package mapper;

import dao.PostDAO;
import dto.PostDTO;
import entity.Post;
import entity.User;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class PostMapperTest {

    private final PostMapper mapper = PostMapper.INSTANCE;

    @Test
    void shouldMapPostDAOToEntity() {
        PostDAO dao = new PostDAO();
        dao.setId(1L);
        dao.setTitle("Test Post");
        dao.setContent("Test Content");
        dao.setUserId(2L);

        Post entity = mapper.toEntity(dao);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), is(1L));
        assertThat(entity.getTitle(), is("Test Post"));
        assertThat(entity.getContent(), is("Test Content"));
        assertThat(entity.getUser(), notNullValue());
        assertThat(entity.getUser().getId(), is(2L));
    }

    @Test
    void shouldMapPostToDTO() {
        User user = new User();
        user.setId(2L);

        Post entity = new Post();
        entity.setId(1L);
        entity.setTitle("Test Post");
        entity.setContent("Test Content");
        entity.setUser(user);

        PostDTO dto = mapper.toDTO(entity);

        assertThat(dto, notNullValue());
        assertThat(dto.getId(), is(1L));
        assertThat(dto.getTitle(), is("Test Post"));
        assertThat(dto.getContent(), is("Test Content"));
        assertThat(dto.getUserId(), is(2L));
    }
}