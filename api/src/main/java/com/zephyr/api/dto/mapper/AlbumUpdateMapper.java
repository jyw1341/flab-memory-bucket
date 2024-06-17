package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.service.AlbumUpdateServiceDto;
import com.zephyr.api.dto.request.AlbumUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlbumUpdateMapper {

    AlbumUpdateMapper INSTANCE = Mappers.getMapper(AlbumUpdateMapper.class);

    AlbumUpdateServiceDto toAlbumUpdateServiceDto(String memberId, Long albumId, AlbumUpdateRequest request);
}
