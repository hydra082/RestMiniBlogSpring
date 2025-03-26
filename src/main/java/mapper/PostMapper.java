package mapper;

import dao.PostDAO;
import dto.PostDTO;
import entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CommentMapper.class})
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDAO dao);

    @Mapping(source = "user.id", target = "userId")
    PostDTO toDTO(Post entity);
}
