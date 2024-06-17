package com.zephyr.api.dto.mapper;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.AlbumMemberCreateServiceDto;
import com.zephyr.api.enums.AlbumMemberRole;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlbumMemberCreateMapper {
    AlbumMemberCreateMapper INSTANCE = Mappers.getMapper(AlbumMemberCreateMapper.class);

    AlbumMemberCreateServiceDto toAlbumMemberCreateServiceDto(Album album, Member member, AlbumMemberRole role);
}
