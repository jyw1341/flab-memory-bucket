package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.service.PostCreateServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostCreateMapper {

    PostCreateMapper INSTANCE = Mappers.getMapper(PostCreateMapper.class);

    PostCreateServiceDto toPostCreateServiceDto(Long memberId, PostCreateRequest request);
}
