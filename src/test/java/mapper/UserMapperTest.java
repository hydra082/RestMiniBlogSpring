package mapper;

import dao.UserDAO;
import dto.UserDTO;
import entity.User;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class UserMapperTest {

    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void shouldMapUserDAOToEntity() {
        UserDAO dao = new UserDAO();
        dao.setId(1L);
        dao.setName("Test");

        User entity = mapper.toEntity(dao);

        assertThat(entity.getId(), is(1L));
        assertThat(entity.getName(), is("Test"));
    }

    @Test
    void shouldMapUserToDTO() {
        User entity = new User();
        entity.setId(1L);
        entity.setName("Test");

        UserDTO dto = mapper.toDTO(entity);

        assertThat(dto.getId(), is(1L));
        assertThat(dto.getName(), is("Test"));
    }
}