package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.request.AlbumUpdateRequest;
import com.zephyr.api.dto.service.AlbumUpdateServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlbumUpdateMapper {

    AlbumUpdateMapper INSTANCE = Mappers.getMapper(AlbumUpdateMapper.class);

    AlbumUpdateServiceDto toAlbumUpdateServiceDto(Long memberId, Long albumId, AlbumUpdateRequest request);
}
