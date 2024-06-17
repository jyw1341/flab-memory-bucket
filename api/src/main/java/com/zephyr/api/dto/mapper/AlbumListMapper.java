package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.service.AlbumListServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlbumListMapper {

    AlbumListMapper INSTANCE = Mappers.getMapper(AlbumListMapper.class);

    AlbumListServiceDto toAlbumUpdateServiceDto(String memberId);
}
