package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.service.PostDeleteServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostDeleteMapper {

    PostDeleteMapper INSTANCE = Mappers.getMapper(PostDeleteMapper.class);

    PostDeleteServiceDto toPostDeleteMapper(Long memberId, Long postId);
}
