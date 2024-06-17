package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.service.AlbumCreateServiceDto;
import com.zephyr.api.dto.request.AlbumCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlbumCreateMapper {
    AlbumCreateMapper INSTANCE = Mappers.getMapper(AlbumCreateMapper.class);

    AlbumCreateServiceDto toAlbumCreateServiceDto(String memberId, AlbumCreateRequest request);
}
