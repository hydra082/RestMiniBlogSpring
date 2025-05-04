package com.myblog.mapper;

import com.myblog.dto.CommentDTO;
import com.myblog.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "post.id", target = "postId")
    CommentDTO toDTO(Comment entity);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDTO dto);
}