package com.zephyr.api.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Album {

    /*
    앨범 아이디
    앨범 소유자 아이디
    앨범 제목
    앨범 짤막 설명 (옵션)

     */

    @Builder
    public Album() {
    }
}
