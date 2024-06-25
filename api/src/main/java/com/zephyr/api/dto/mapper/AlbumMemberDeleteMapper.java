package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.service.AlbumMemberDeleteServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlbumMemberDeleteMapper {

    AlbumMemberDeleteMapper INSTANCE = Mappers.getMapper(AlbumMemberDeleteMapper.class);

    AlbumMemberDeleteServiceDto toAlbumMemberDeleteServiceDto(Long albumId, Long targetId);
}
