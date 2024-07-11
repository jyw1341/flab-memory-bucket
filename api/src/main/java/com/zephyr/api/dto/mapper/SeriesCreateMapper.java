package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.PresignedUrlCreateServiceDto;
import com.zephyr.api.dto.SeriesCreateServiceDto;
import com.zephyr.api.dto.request.PresignedUrlCreateRequest;
import com.zephyr.api.dto.request.SeriesCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SeriesCreateMapper {

    SeriesCreateMapper INSTANCE = Mappers.getMapper(SeriesCreateMapper.class);

    SeriesCreateServiceDto toSeriesCreateServiceDto(Long albumId, SeriesCreateRequest request);
}
