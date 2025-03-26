package mapper;

import dao.UserDAO;
import dto.UserDTO;
import entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {PostMapper.class, CommentMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "comments", ignore = true)
    User toEntity(UserDAO dao);

    UserDTO toDTO(User entity);
}
