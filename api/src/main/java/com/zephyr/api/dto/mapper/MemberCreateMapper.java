package com.zephyr.api.dto.mapper;

import com.zephyr.api.dto.request.MemberCreateRequest;
import com.zephyr.api.dto.MemberCreateServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberCreateMapper {

    MemberCreateMapper INSTANCE = Mappers.getMapper(MemberCreateMapper.class);

    MemberCreateServiceDto toMemberCreateServiceDto(MemberCreateRequest request);
}
