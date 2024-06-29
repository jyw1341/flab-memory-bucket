package com.zephyr.api.service;

import com.zephyr.api.config.S3ConfigurationProperties;
import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.dto.service.AlbumCreateServiceDto;
import com.zephyr.api.dto.service.AlbumMemberCreateServiceDto;
import com.zephyr.api.dto.service.AlbumUpdateServiceDto;
import com.zephyr.api.exception.AlbumNotFoundException;
import com.zephyr.api.exception.ForbiddenException;
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
    private S3ConfigurationProperties s3ConfigurationProperties;

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

    @DisplayName("앨범 단건 조회 성공")
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
    @DisplayName("앨범 썸네일 url이 없을 때 / 앨범 단건 조회 / 기본 썸네일 url 반환")

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
        when(albumService.getDefaultAlbumThumbnailUrl()).thenReturn(defaultThumbnail);

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

    @Test
    @DisplayName("앨범 목록 조회 성공")
    public void successGetAlbumList() {

    }

    @Test
    @DisplayName("앨범 수정 성공")
    public void successAlbumUpdate() {
        //given
        Long albumId = 1L;
        Long loginId = 1L;

        //현재 접속한 사람을 앨범 소유자와 같도록 설정
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(loginId);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(owner);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        AlbumUpdateServiceDto albumUpdateServiceDto = new AlbumUpdateServiceDto(
                albumId,
                loginId,
                "수정된 제목",
                "수정된 설명",
                "수정된 url"
        );

        //when
        albumService.update(albumUpdateServiceDto);

        //then
        verify(album, times(1)).setTitle(albumUpdateServiceDto.getTitle());
        verify(album, times(1)).setDescription(albumUpdateServiceDto.getDescription());
        verify(album, times(1)).setThumbnailUrl(albumUpdateServiceDto.getThumbnailUrl());
    }

    @Test
    @DisplayName("부적절한 앨범 아이디 / 앨범 수정 / 수정 실패")
    public void givenInvalidAlbumId_whenAlbumUpdate_thenFail() {
        //given
        Long albumId = 1L;
        Long loginId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());
        AlbumUpdateServiceDto albumUpdateServiceDto = new AlbumUpdateServiceDto(
                albumId,
                loginId,
                "수정된 제목",
                "수정된 설명",
                "수정된 url"
        );

        //when
        assertThrows(AlbumNotFoundException.class, () -> albumService.update(albumUpdateServiceDto));
    }

    @Test
    @DisplayName("현재 사용자가 앨범 소유자가 아닐 때 / 앨범 수정 / 수정 실패")
    public void givenNotAlbumOwner_whenAlbumUpdate_thenFail() {
        //given
        Long albumId = 1L;
        Long loginId = 1L;
        Long ownerId = 2L;

        //현재 접속한 사용자와 앨범 소유자가 다르게 설정
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(ownerId);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(owner);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        AlbumUpdateServiceDto albumUpdateServiceDto = new AlbumUpdateServiceDto(
                albumId,
                loginId,
                "수정된 제목",
                "수정된 설명",
                "수정된 url"
        );

        //when then
        assertThrows(ForbiddenException.class, () -> albumService.update(albumUpdateServiceDto));
        verify(album, times(0)).setTitle(albumUpdateServiceDto.getTitle());
        verify(album, times(0)).setDescription(albumUpdateServiceDto.getDescription());
        verify(album, times(0)).setThumbnailUrl(albumUpdateServiceDto.getThumbnailUrl());
    }
}
