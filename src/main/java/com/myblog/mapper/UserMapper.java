package com.myblog.mapper;

import com.myblog.dto.UserDTO;
import com.myblog.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PostMapper.class, CommentMapper.class})
public interface UserMapper {
    UserDTO toDTO(User entity);

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "comments", ignore = true)
    User toEntity(UserDTO dto);
}