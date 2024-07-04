package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.MemoryCreateServiceDto;
import com.zephyr.api.dto.request.MemoryCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemoryCreateMapper {

    MemoryCreateMapper INSTANCE = Mappers.getMapper(MemoryCreateMapper.class);

    MemoryCreateServiceDto toMemoryCreateServiceDto(MemoryCreateRequest request);
}
