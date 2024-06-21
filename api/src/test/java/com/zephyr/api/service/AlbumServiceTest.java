package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.service.AlbumCreateServiceDto;
import com.zephyr.api.dto.service.AlbumMemberCreateServiceDto;
import com.zephyr.api.exception.AlbumNotFoundException;
import com.zephyr.api.repository.AlbumRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private AlbumMemberService albumMemberService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private S3Config s3Config;

    @InjectMocks
    private AlbumService albumService;

    @Test
    @DisplayName("앨범 생성 성공")
    public void successCreateAlbum() {
        //given
        AlbumCreateServiceDto serviceDto = new AlbumCreateServiceDto(1L, "", "", "");
        when(memberService.get(serviceDto.getMemberId())).thenReturn(Member.builder().build());

        //when
        albumService.create(serviceDto);

        //then
        verify(memberService, times(1)).get(serviceDto.getMemberId());
        verify(albumRepository, times(1)).save(any(Album.class));
        verify(albumMemberService, times(1)).create(any(AlbumMemberCreateServiceDto.class));
    }

    @Test
    @DisplayName("앨범 조회 성공")
    public void successGetAlbum() {
        //given
        Long albumId = 1L;
        Album album = Album.builder()
                .title("제목")
                .description("설명")
                .thumbnailUrl("url")
                .build();
        Album spy = spy(album);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(spy));

        //when
        Album result = albumService.get(albumId);

        //then
        verify(albumRepository, times(1)).findById(albumId);
        assertEquals(album.getThumbnailUrl(), result.getThumbnailUrl());
    }

    @Test
    @DisplayName("앨범 썸네일 url이 없을 때 / 앨범 조회 / 기본 썸네일 url 반환")
    public void givenNoThumbnailUrl_whenGetAlbum_thenReturnAlbumWithDefaultThumbnail() {
        //given
        Long albumId = 1L;
        String defaultThumbnail = "default url";
        Album album = Album.builder()
                .title("제목")
                .description("설명")
                .build();
        Album spy = spy(album);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(spy));
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(defaultThumbnail);

        //when
        Album result = albumService.get(albumId);

        //then
        verify(albumRepository, times(1)).findById(albumId);
        assertEquals(defaultThumbnail, result.getThumbnailUrl());
    }

    @Test
    @DisplayName("조회 하려는 앨범이 없음 / 앨범 단건 조회 / AlbumNotFoundException")
    public void givenInValidAlbumId_whenFindOneAlbum_thenNotFoundAlbumException() {
        //given
        Long albumId = 2L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        //when then
        assertThrows(AlbumNotFoundException.class, () -> albumService.get(albumId));

        verify(albumRepository, times(1)).findById(albumId);
    }

}
