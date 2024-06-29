package com.zephyr.api.repository;

import com.zephyr.api.domain.AlbumMember;
import com.zephyr.api.dto.AlbumMemberListServiceDto;

import java.util.List;

public interface AlbumMemberCustomRepository {

    List<AlbumMember> findAlbumMembers(AlbumMemberListServiceDto dto);
}
