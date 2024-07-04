package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.MemoryUpdateServiceDto;
import com.zephyr.api.dto.request.MemoryUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemoryUpdateMapper {

    MemoryUpdateMapper INSTANCE = Mappers.getMapper(MemoryUpdateMapper.class);

    MemoryUpdateServiceDto toMemoryUpdateServiceDto(
            MemoryUpdateRequest request
    );
}
