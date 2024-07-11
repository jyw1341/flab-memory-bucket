package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.PostSearchServiceDto;
import com.zephyr.api.dto.request.PostSearchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;

@Mapper
public interface PostListMapper {

    PostListMapper INSTANCE = Mappers.getMapper(PostListMapper.class);

    PostSearchServiceDto toPostListServiceDto(
            Long albumId,
            Pageable pageable,
            PostSearchRequest request
    );
}
