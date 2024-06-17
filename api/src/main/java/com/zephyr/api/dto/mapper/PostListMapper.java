package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.service.PostListServiceDto;
import com.zephyr.api.dto.request.PostListRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostListMapper {

    PostListMapper INSTANCE = Mappers.getMapper(PostListMapper.class);

    PostListServiceDto toPostListServiceDto(PostListRequest request);
}
