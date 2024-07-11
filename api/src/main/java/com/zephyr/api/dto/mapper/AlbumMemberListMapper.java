package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.AlbumMemberListServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlbumMemberListMapper {

    AlbumMemberListMapper INSTANCE = Mappers.getMapper(AlbumMemberListMapper.class);

    AlbumMemberListServiceDto toAlbumMemberListDto(Long memberId, Long albumId);
}
