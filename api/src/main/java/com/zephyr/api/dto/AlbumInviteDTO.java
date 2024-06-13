package com.zephyr.api.dto;

import com.zephyr.api.enums.AlbumMemberRole;
import lombok.Data;

@Data
public class AlbumInviteDTO {

    private final Long albumId;
    private final Long memberId;
    private final String invitedUserName;
    private final AlbumMemberRole role;
}
