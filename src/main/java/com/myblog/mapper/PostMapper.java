package com.myblog.mapper;

import com.myblog.dto.PostDTO;
import com.myblog.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface PostMapper {
    @Mapping(source = "user.id", target = "userId")
    PostDTO toDTO(Post entity);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDTO dto);
}