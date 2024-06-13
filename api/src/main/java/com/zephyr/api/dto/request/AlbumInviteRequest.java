package com.zephyr.api.dto.request;

import com.zephyr.api.enums.AlbumMemberRole;
import lombok.Data;

@Data
public class AlbumInviteRequest {

    private final String username;
    private final AlbumMemberRole role;
}
