package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Subscribe;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.AlbumNotFoundException;
import com.zephyr.api.exception.SubscribeNotFoundException;
import com.zephyr.api.repository.AlbumRepository;
import com.zephyr.api.repository.MemberRepository;
import com.zephyr.api.repository.SubscribeRepository;
import com.zephyr.api.request.AlbumCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    public static final String DEFAULT_THUMBNAIL = "https://kr.object.ncloudstorage.com";
    private AlbumService albumService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private SubscribeRepository subscribeRepository;
    @Mock
    private S3Config s3Config;

    @BeforeEach
    public void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "errors");
        messageSource.setDefaultEncoding("UTF-8");
        albumService = new AlbumService(albumRepository, memberRepository, subscribeRepository, messageSource, s3Config);
    }

    @Test
    @DisplayName("앨범 생성 요청의 모든 필드가 있을 때 / 앨범 생성 / 요청 정보와 같은 앨범 생성")
    public void givenValidRequest_whenAlbumCreate_thenCreateSuccess() {
        //given
        Long memberId = 1L;
        AlbumCreate albumCreate = new AlbumCreate("제목", "설명", "썸네일");
        Member member = Member.builder().build();

        //when
        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        Album album = albumService.create(memberId, albumCreate);

        //then
        assertEquals(albumCreate.getTitle(), album.getTitle());
        assertEquals(albumCreate.getDescription(), album.getDescription());
        assertEquals(albumCreate.getThumbnailUrl(), album.getThumbnailUrl());

    }

    @Test
    @DisplayName("앨범이 존재 하고 사용자가 앨범을 구독 중일 때 / 앨범 단건 조회 / 앨범 정보 반환")
    public void givenValidId_whenFindOneAlbum_thenReturnAlbum() {
        //given\
        Long loginId = 200L;
        Member owner = Member.builder().id(100L).build();
        Album album = Album.builder()
                .id(1L)
                .owner(owner)
                .title("title")
                .description("description")
                .thumbnailUrl("thumbnailUrl")
                .build();
        Subscribe subscribe = Subscribe.builder().build();

        //when
        when(albumRepository.findById(album.getId()))
                .thenReturn(Optional.of(album));
        when(subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId))
                .thenReturn(Optional.of(subscribe));
        Album result = albumService.get(album.getId(), loginId);

        //then
        assertEquals(album.getId(), result.getId());
        assertEquals(album.getTitle(), result.getTitle());
        assertEquals(album.getDescription(), result.getDescription());
        assertEquals(album.getThumbnailUrl(), result.getThumbnailUrl());
    }

    @Test
    @DisplayName("앨범이 존재 x / 앨범 단건 조회 / NotFoundAlbumException")
    public void givenInValidAlbumId_whenFindOneAlbum_thenNotFoundAlbumException() {
        //given
        Long invalidAlbumId = 999L;
        Long memberId = 1L;

        when(albumRepository.findById(invalidAlbumId)).thenReturn(Optional.empty());

        //then
        assertThrows(AlbumNotFoundException.class, () -> albumService.get(invalidAlbumId, memberId));
    }

    @Test
    @DisplayName("사용자가 조회하려는 앨범을 구독 x / 앨범 단건 조회 / NotFoundAlbumMemberException")
    public void givenInValidMemberId_whenFindOneAlbum_thenNotFoundAlbumException() {
        //given
        Long invalidMemberId = 999L;
        Member owner = Member.builder().id(100L).build();
        Album album = Album.builder()
                .id(1L)
                .owner(owner)
                .build();

        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));
        when(subscribeRepository.findByAlbumIdAndMemberId(album.getId(), invalidMemberId))
                .thenReturn(Optional.empty());

        assertThrows(SubscribeNotFoundException.class, () -> albumService.get(album.getId(), invalidMemberId));
    }

    @Test
    @DisplayName("썸네일 url이 빈 문자열 / 앨범 단건 조회 / 기본 썸네일 url 반환")
    public void givenBlankThumbnailUrl_whenFindOneAlbum_thenReturnDefaultThumbnailUrl() {
        //given
        Long loginId = 1L;
        Member owner = Member.builder().id(100L).build();
        Album album = Album.builder()
                .id(1L)
                .owner(owner)
                .title("title")
                .thumbnailUrl("")
                .build();

        //when
        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));
        when(subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId))
                .thenReturn(Optional.of(Subscribe.builder().build()));
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(DEFAULT_THUMBNAIL);
        Album result = albumService.get(album.getId(), loginId);

        //then
        assertEquals(DEFAULT_THUMBNAIL, result.getThumbnailUrl());

    }

    @Test
    @DisplayName("썸네일 url이 null / 앨범 단건 조회 / 기본 썸네일 url 반환")
    public void givenNullThumbnailUrl_whenFindOneAlbum_thenReturnDefaultThumbnailUrl() {
        Long loginId = 1L;
        Member owner = Member.builder().id(100L).build();
        Album album = Album.builder()
                .id(1L)
                .owner(owner)
                .title("title")
                .build();

        //when
        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));
        when(subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId))
                .thenReturn(Optional.of(Subscribe.builder().build()));
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(DEFAULT_THUMBNAIL);
        Album result = albumService.get(album.getId(), loginId);

        //then
        assertEquals(DEFAULT_THUMBNAIL, result.getThumbnailUrl());
    }

    @Test
    @DisplayName("회원이 앨범 10개를 소유, 구독 중인 앨범 x / 앨범 목록 조회 / 소유 중인 앨범 10개 반환")
    void givenMemberHave10Albums_whenFindAlbumList_return10AlbumList() {
        //given
        List<Album> albums = new ArrayList<>();
        Member owner = Member.builder()
                .id(100L)
                .albums(albums)
                .build();
        for (long i = 1; i <= 10; i++) {
            Album album = Album.builder()
                    .id(i)
                    .owner(owner)
                    .title("title" + i)
                    .thumbnailUrl("url")
                    .build();
            albums.add(album);
        }

        //when
        when(memberRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        List<Album> result = albumService.getList(owner.getId());

        //then
        assertEquals(albums.size(), result.size());
    }

    @Test
    @DisplayName("회원이 앨범 10개를 구독, 소유 중인 앨범 x / 앨범 목록 조회 / 구독 중인 앨범 10개 반환")
    void givenMemberHave10Subscribes_whenFindAlbumList_return5AlbumList() {
        //given
        List<Subscribe> subscribes = new ArrayList<>();
        Member member = Member.builder()
                .id(100L)
                .subscribes(subscribes)
                .build();
        for (long i = 1; i <= 10; i++) {
            Member owner = Member.builder().id(i).build();
            Album album = Album.builder()
                    .id(i)
                    .owner(owner)
                    .title("album" + i)
                    .thumbnailUrl("url")
                    .build();
            Subscribe subscribe = Subscribe.builder()
                    .id(i)
                    .status(SubscribeStatus.APPROVED)
                    .album(album)
                    .build();

            subscribes.add(subscribe);
        }

        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        List<Album> result = albumService.getList(member.getId());

        //then
        assertEquals(subscribes.size(), result.size());
    }

    @Test
    @DisplayName("회원이 앨범 10개 소유 10개 구독 / 앨범 목록 조회 / 총 앨범 20개 반환")
    void givenMemberHave10AlbumsAnd10Subscribes_whenFindAlbumList_return5AlbumList() {
        //given
        List<Subscribe> subscribes = new ArrayList<>();
        List<Album> albums = new ArrayList<>();
        Member member = Member.builder()
                .id(100L)
                .subscribes(subscribes)
                .albums(albums)
                .build();
        for (long i = 1; i <= 10; i++) {
            Album album = Album.builder()
                    .id(i)
                    .owner(member)
                    .title("album" + i)
                    .thumbnailUrl("url")
                    .build();
            albums.add(album);
        }

        for (long i = 1; i <= 10; i++) {
            Member owner = Member.builder().id(i).build();
            Album album = Album.builder()
                    .id(i)
                    .owner(owner)
                    .title("album" + i)
                    .thumbnailUrl("url")
                    .build();
            Subscribe subscribe = Subscribe.builder()
                    .id(i)
                    .status(SubscribeStatus.APPROVED)
                    .album(album)
                    .build();

            subscribes.add(subscribe);
        }

        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        List<Album> result = albumService.getList(member.getId());

        //then
        assertEquals(subscribes.size() + albums.size(), result.size());
    }

    @Test
    @DisplayName("앨범의 썸네일 url 없을 때 / 앨범 목록 조회 / 기본 앨범 썸네일 url 반환")
    void givenNoThumbnail_whenFindAlbumList_returnAlbumsMappedByDefaultUrl() {
        //given
        List<Subscribe> subscribes = new ArrayList<>();
        List<Album> albums = new ArrayList<>();
        Member member = Member.builder()
                .id(100L)
                .subscribes(subscribes)
                .albums(albums)
                .build();
        Album albumWithoutThumbnail = Album.builder()
                .id(1L)
                .owner(member)
                .title("albumWithoutThumbnail")
                .build();
        Album albumWithThumbnail = Album.builder()
                .id(2L)
                .owner(member)
                .title("albumWith")
                .thumbnailUrl("url")
                .build();
        albums.add(albumWithThumbnail);
        albums.add(albumWithoutThumbnail);

        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(DEFAULT_THUMBNAIL);
        List<Album> result = albumService.getList(member.getId());

        //then
        result.forEach(album -> {
            if (album.getTitle().equals(albumWithoutThumbnail.getTitle()))
                assertEquals(DEFAULT_THUMBNAIL, album.getThumbnailUrl());
            else
                assertEquals(albumWithThumbnail.getThumbnailUrl(), album.getThumbnailUrl());
        });
    }

    
}
