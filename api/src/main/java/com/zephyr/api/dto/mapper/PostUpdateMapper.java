package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.service.MemoryUpdateServiceDto;
import com.zephyr.api.dto.service.PostUpdateServiceDto;
import com.zephyr.api.dto.request.PostUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PostUpdateMapper {

    PostUpdateMapper INSTANCE = Mappers.getMapper(PostUpdateMapper.class);

    PostUpdateServiceDto toPostUpdateServiceDto(
            String memberId,
            Long postId,
            PostUpdateRequest postUpdateRequest,
            List<MemoryUpdateServiceDto> memoryUpdateServiceDtos
    );
}
