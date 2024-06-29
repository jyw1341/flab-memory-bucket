package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.request.PresignedUrlCreateRequest;
import com.zephyr.api.dto.PresignedUrlCreateServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PresignedUrlCreateMapper {

    PresignedUrlCreateMapper INSTANCE = Mappers.getMapper(PresignedUrlCreateMapper.class);

    PresignedUrlCreateServiceDto toFileCreateServiceDto(Long memberId, PresignedUrlCreateRequest presignedUrlCreateRequest);
}
