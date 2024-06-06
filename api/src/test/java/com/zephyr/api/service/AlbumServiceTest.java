package com.zephyr.api.service;

import com.zephyr.api.config.S3Config;
import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Subscribe;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.*;
import com.zephyr.api.repository.AlbumRepository;
import com.zephyr.api.repository.MemberRepository;
import com.zephyr.api.repository.SubscribeRepository;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.request.SubscribeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    public static final String THUMBNAIL_TEST_URL = "https://kr.object.ncloudstorage.com";

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private SubscribeRepository subscribeRepository;
    @Mock
    private MessageSource messageSource;
    @Mock
    private S3Config s3Config;
    @InjectMocks
    private AlbumService albumService;

    @Test
    @DisplayName("앨범 생성 성공")
    public void givenValidRequest_whenAlbumCreate_thenCreateSuccess() {
        //given
        AlbumCreate albumCreate = new AlbumCreate("제목", "설명", "썸네일");
        Long loginId = 1L;
        when(memberRepository.findById(loginId)).thenReturn(Optional.of(Member.builder().build()));

        //when
        albumService.create(loginId, albumCreate);

        //then
        verify(memberRepository, times(1)).findById(loginId);
        verify(albumRepository, times(1)).save(any(Album.class));
    }

    @Test
    @DisplayName("멤버 조회 실패 / 앨범 생성 / MemberNotFoundException")
    public void givenInvalidMember_whenAlbumCreate_thenMemberNotFound() {
        //given
        AlbumCreate albumCreate = new AlbumCreate("제목", "설명", "썸네일");
        Long loginId = 1L;
        when(memberRepository.findById(loginId)).thenReturn(Optional.empty());

        //when then
        assertThrows(MemberNotFoundException.class, () -> albumService.create(loginId, albumCreate));
        verify(memberRepository, times(1)).findById(loginId);
        verify(albumRepository, times(0)).save(any(Album.class));
    }

    @Test
    @DisplayName("현재 사용자 = 앨범 소유자 / 앨범 단건 조회 / 앨범 반환")
    public void givenAlbumOwner_whenFindOneAlbum_thenReturnAlbum() {
        //given
        Long loginId = 1L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(loginId);

        Long albumId = 2L;
        Album mockedAlbum = mock(Album.class);
        when(mockedAlbum.getOwner()).thenReturn(mockedMember);

        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(mockedAlbum));

        //when
        Album result = albumService.get(albumId, loginId);

        //then
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
    }

    @Test
    @DisplayName("현재 사용자 = 구독자 / 앨범 단건 조회 / 앨범 반환")
    public void givenSubscriber_whenFindOneAlbum_thenReturnAlbum() {
        //given
        Long loginId = 1L;
        Long ownerId = 2L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(ownerId);

        Long albumId = 2L;
        Album mockedAlbum = mock(Album.class);
        when(mockedAlbum.getOwner()).thenReturn(mockedMember);
        when(mockedAlbum.getId()).thenReturn(albumId);
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(mockedAlbum));

        Subscribe subscribe = Subscribe.builder().status(SubscribeStatus.APPROVED).build();
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.of(subscribe));

        //when
        Album result = albumService.get(albumId, loginId);

        //then
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
    }

    @Test
    @DisplayName("조회 하려는 앨범이 없음 / 앨범 단건 조회 / NotFoundAlbumException")
    public void givenInValidAlbumId_whenFindOneAlbum_thenNotFoundAlbumException() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        //when then
        assertThrows(AlbumNotFoundException.class, () -> albumService.get(albumId, loginId));
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
    }

    @Test
    @DisplayName("사용자가 앨범의 소유자, 구독자가 아닌 경우 / 앨범 단건 조회 / SubscribeNotFoundException")
    public void givenInValidMemberId_whenFindOneAlbum_thenSubscribeNotFoundException() {
        //given
        Long loginId = 1L;
        Long ownerId = 2L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(ownerId);

        Long albumId = 2L;
        Album mockedAlbum = mock(Album.class);
        when(mockedAlbum.getOwner()).thenReturn(mockedMember);
        when(mockedAlbum.getId()).thenReturn(albumId);
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(mockedAlbum));

        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.empty());

        //when then
        assertThrows(SubscribeNotFoundException.class, () -> albumService.get(albumId, loginId));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
    }

    @Test
    @DisplayName("앨범 구독 상태가 승인이 아닌 경우 / 앨범 단건 조회 / ForbiddenException")
    public void givenSubscribeNotApproved_whenFindOneAlbum_thenForbiddenException() {
        //given
        Long loginId = 1L;
        Long ownerId = 2L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(ownerId);

        Long albumId = 2L;
        Album mockedAlbum = mock(Album.class);
        when(mockedAlbum.getOwner()).thenReturn(mockedMember);
        when(mockedAlbum.getId()).thenReturn(albumId);
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(mockedAlbum));

        Subscribe subscribe = Subscribe.builder().status(SubscribeStatus.PENDING).build();
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.of(subscribe));

        //when then
        assertThrows(ForbiddenException.class, () -> albumService.get(albumId, loginId));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
    }

    @Test
    @DisplayName("썸네일 url 존재 / 앨범 단건 조회 / 앨범 url 그대로 반환")
    public void givenValidUrl_whenFindOneAlbum_thenReturnURL() {
        //given
        Long loginId = 1L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(loginId);

        Long albumId = 2L;
        Album album = Album.builder()
                .owner(mockedMember)
                .thumbnailUrl("test url")
                .build();
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(album));

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        Album result = albumService.get(albumId, loginId);

        //then
        assertNotNull(result);
        assertEquals(album.getThumbnailUrl(), result.getThumbnailUrl());
        verify(s3Config, times(0)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);

    }

    @Test
    @DisplayName("조회 앨범의 썸네일 문자열 길이가 0 / 앨범 단건 조회 / 기본 썸네일 url 반환")
    public void givenUrlLengthZero_whenFindOneAlbum_thenReturnDefaultThumbnailUrl() {
        //given
        Long loginId = 1L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(loginId);

        Long albumId = 2L;
        Album album = Album.builder()
                .owner(mockedMember)
                .thumbnailUrl("")
                .build();
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(album));

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(THUMBNAIL_TEST_URL);
        Album result = albumService.get(albumId, loginId);

        //then
        assertNotNull(result);
        assertEquals(THUMBNAIL_TEST_URL, result.getThumbnailUrl());
        verify(s3Config, times(1)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);

    }

    @Test
    @DisplayName("조회 앨범의 썸네일 문자열 공백/ 앨범 단건 조회 / 기본 썸네일 url 반환")
    public void givenUrlBlank_whenFindOneAlbum_thenReturnDefaultThumbnailUrl() {
        //given
        Long loginId = 1L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(loginId);

        Long albumId = 2L;
        Album album = Album.builder()
                .owner(mockedMember)
                .thumbnailUrl("    ")
                .build();
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(album));

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(THUMBNAIL_TEST_URL);
        Album result = albumService.get(albumId, loginId);

        //then
        assertNotNull(result);
        assertEquals(THUMBNAIL_TEST_URL, result.getThumbnailUrl());
        verify(s3Config, times(1)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);

    }

    @Test
    @DisplayName("조회 앨범의 썸네일 문자열 null / 앨범 단건 조회 / 기본 썸네일 url 반환")
    public void givenUrlNull_whenFindOneAlbum_thenReturnDefaultThumbnailUrl() {
        //given
        Long loginId = 1L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(loginId);

        Long albumId = 2L;
        Album album = Album.builder()
                .owner(mockedMember)
                .thumbnailUrl(null)
                .build();
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(album));

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(THUMBNAIL_TEST_URL);
        Album result = albumService.get(albumId, loginId);

        //then
        assertNotNull(result);
        assertEquals(THUMBNAIL_TEST_URL, result.getThumbnailUrl());
        verify(s3Config, times(1)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
    }

    @Test
    @DisplayName("사용자 = 구독자, 조회 앨범의 썸네일 문자열 null / 앨범 단건 조회 / 기본 썸네일 url 반환")
    public void givenSubscriberAndUrlNull_whenFindOneAlbum_thenReturnDefaultThumbnailUrl() {
        //given
        Long loginId = 1L;
        Long ownerId = 2L;
        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(ownerId);

        Long albumId = 2L;
        Album album = Album.builder()
                .owner(mockedMember)
                .thumbnailUrl(null)
                .build();
        Album spiedAlbum = spy(album);
        when(spiedAlbum.getId()).thenReturn(albumId);
        when(albumRepository.findById(albumId))
                .thenReturn(Optional.of(spiedAlbum));

        Subscribe subscribe = Subscribe.builder().status(SubscribeStatus.APPROVED).build();
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.of(subscribe));

        when(s3Config.getDefaultThumbnailUrl()).thenReturn(THUMBNAIL_TEST_URL);

        //when
        Album result = albumService.get(albumId, loginId);

        //then
        assertNotNull(result);
        assertEquals(THUMBNAIL_TEST_URL, result.getThumbnailUrl());
        verify(s3Config, times(1)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
    }


    @Test
    @DisplayName("앨범 10개를 소유, 구독 중인 앨범 x / 앨범 목록 조회 / 앨범 10개 반환")
    void givenMemberHave10Albums_whenFindAlbumList_return10AlbumList() {
        //given
        Long loginId = 1L;
        int totalAlbum = 10;

        List<Album> ownAlbums = new ArrayList<>();
        for (int i = 1; i <= totalAlbum; i++) {
            Album album = Album.builder()
                    .title("제목 " + i)
                    .owner(Member.builder().build())
                    .description("설명")
                    .thumbnailUrl("url")
                    .build();
            ownAlbums.add(album);
        }

        when(albumRepository.findByMemberId(loginId)).thenReturn(ownAlbums);
        when(subscribeRepository.findByMemberId(loginId)).thenReturn(List.of());

        //when
        List<Album> result = albumService.getList(loginId);

        //then
        assertNotNull(result);
        assertEquals(totalAlbum, result.size());
        verify(s3Config, times(0)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findByMemberId(loginId);
        verify(subscribeRepository, times(1)).findByMemberId(loginId);
    }

    @Test
    @DisplayName("소유 중인 앨범 x, 앨범 5개를 구독 / 앨범 목록 조회 / 앨범 5개 반환")
    void givenMemberHave5Subscribes_whenFindAlbumList_return5AlbumList() {
        //given
        Long loginId = 1L;
        int totalAlbum = 5;

        List<Subscribe> subscribes = new ArrayList<>();
        for (int i = 1; i <= totalAlbum; i++) {
            Album album = Album.builder()
                    .title("제목 " + i)
                    .owner(Member.builder().build())
                    .description("설명")
                    .thumbnailUrl("url")
                    .build();

            subscribes.add(Subscribe.builder().album(album).build());
        }

        when(albumRepository.findByMemberId(loginId)).thenReturn(List.of());
        when(subscribeRepository.findByMemberId(loginId)).thenReturn(subscribes);

        //when
        List<Album> result = albumService.getList(loginId);

        //given
        assertNotNull(result);
        assertEquals(totalAlbum, result.size());
        verify(s3Config, times(0)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findByMemberId(loginId);
        verify(subscribeRepository, times(1)).findByMemberId(loginId);
    }

    @Test
    @DisplayName("회원이 앨범 10개 소유 10개 구독 / 앨범 목록 조회 / 앨범 20개 반환")
    void givenMemberHave10AlbumsAnd10Subscribes_whenFindAlbumList_return20AlbumList() {
        //given
        Long loginId = 1L;
        int ownSize = 10;
        List<Album> ownAlbums = new ArrayList<>();
        for (int i = 1; i <= ownSize; i++) {
            Album album = Album.builder()
                    .title("제목 " + i)
                    .owner(Member.builder().build())
                    .description("설명")
                    .thumbnailUrl("url")
                    .build();
            ownAlbums.add(album);
        }
        when(albumRepository.findByMemberId(loginId)).thenReturn(ownAlbums);

        int subscribeSize = 5;
        List<Subscribe> subscribes = new ArrayList<>();
        for (int i = 1; i <= subscribeSize; i++) {
            Album album = Album.builder()
                    .title("제목 " + i)
                    .owner(Member.builder().build())
                    .description("설명")
                    .thumbnailUrl("url")
                    .build();
            subscribes.add(Subscribe.builder().album(album).build());
        }
        when(subscribeRepository.findByMemberId(loginId)).thenReturn(subscribes);

        //when
        List<Album> result = albumService.getList(loginId);

        //given
        assertNotNull(result);
        assertEquals(ownSize + subscribeSize, result.size());
        verify(s3Config, times(0)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findByMemberId(loginId);
        verify(subscribeRepository, times(1)).findByMemberId(loginId);

    }

    @Test
    @DisplayName("앨범의 썸네일 url 없음 / 앨범 목록 조회 / 기본 앨범 썸네일 url 반환")
    void givenNoThumbnail_whenFindAlbumList_returnAlbumsMappedByDefaultUrl() {
        //given
        Long loginId = 1L;
        int totalAlbum = 3;

        List<Album> ownAlbums = new ArrayList<>();
        for (int i = 1; i <= totalAlbum; i++) {
            Album album = Album.builder()
                    .title("제목 " + i)
                    .owner(Member.builder().build())
                    .description("설명")
                    .build();
            ownAlbums.add(album);
        }

        when(albumRepository.findByMemberId(loginId)).thenReturn(ownAlbums);
        when(subscribeRepository.findByMemberId(loginId)).thenReturn(List.of());
        when(s3Config.getDefaultThumbnailUrl()).thenReturn(THUMBNAIL_TEST_URL);

        //when
        List<Album> result = albumService.getList(loginId);

        //then
        assertNotNull(result);
        assertEquals(totalAlbum, result.size());
        result.forEach(album -> assertEquals(THUMBNAIL_TEST_URL, album.getThumbnailUrl()));
        verify(s3Config, times(totalAlbum)).getDefaultThumbnailUrl();
        verify(albumRepository, times(1)).findByMemberId(loginId);
        verify(subscribeRepository, times(1)).findByMemberId(loginId);
    }

    @Test
    @DisplayName("앨범 수정 성공")
    void successAlbumUpdate() {
        //given
        AlbumUpdate albumUpdate = new AlbumUpdate("수정된 제목", "수정된 내용", "수정된 url");
        Long loginId = 1L;
        Long albumId = 2L;

        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(loginId);
        Album album = Album.builder()
                .owner(mockedMember)
                .title("제목")
                .description("내용")
                .thumbnailUrl("url")
                .build();

        Album spiedAlbum = spy(album);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(spiedAlbum));

        //when
        Album result = albumService.update(albumId, loginId, albumUpdate);

        //then
        assertNotNull(result);
        assertEquals(result.getTitle(), albumUpdate.getTitle());
        assertEquals(result.getDescription(), albumUpdate.getDescription());
        assertEquals(result.getThumbnailUrl(), albumUpdate.getThumbnailUrl());
        verify(albumRepository, times(1)).findById(albumId);
        verify(spiedAlbum, times(1)).setTitle(albumUpdate.getTitle());
        verify(spiedAlbum, times(1)).setDescription(albumUpdate.getDescription());
        verify(spiedAlbum, times(1)).setThumbnailUrl(albumUpdate.getThumbnailUrl());
    }

    @Test
    @DisplayName("앨범 조회 실패 / 앨범 수정 / AlbumNotFoundException")
    void givenNoAlbum_whenAlbumUpdate_thenAlbumNotFoundException() {
        //given
        AlbumUpdate albumUpdate = new AlbumUpdate("수정된 제목", "수정된 내용", "수정된 url");
        Long loginId = 1L;
        Long albumId = 2L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        //when then
        assertThrows(AlbumNotFoundException.class, () -> albumService.update(albumId, loginId, albumUpdate));
        verify(albumRepository, times(1)).findById(albumId);
    }

    @Test
    @DisplayName("사용자 != 앨범 소유자 / 앨범 수정 / ForbiddenException")
    void givenUserNotOwner_whenAlbumUpdate_thenForbiddenException() {
        //given
        AlbumUpdate albumUpdate = new AlbumUpdate("수정된 제목", "수정된 내용", "수정된 url");
        Long loginId = 1L;
        Long albumId = 2L;
        Long ownerId = 3L;

        Member mockedMember = mock(Member.class);
        when(mockedMember.getId()).thenReturn(ownerId);
        Album album = Album.builder()
                .owner(mockedMember)
                .title("제목")
                .description("내용")
                .thumbnailUrl("url")
                .build();

        Album spiedAlbum = spy(album);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(spiedAlbum));

        //when then
        assertThrows(ForbiddenException.class, () -> albumService.update(albumId, loginId, albumUpdate));
        verify(albumRepository, times(1)).findById(albumId);
        verify(spiedAlbum, times(0)).setTitle(albumUpdate.getTitle());
        verify(spiedAlbum, times(0)).setDescription(albumUpdate.getDescription());
        verify(spiedAlbum, times(0)).setThumbnailUrl(albumUpdate.getThumbnailUrl());
    }

    @Test
    @DisplayName("앨범 구독자 초대 성공")
    void successCreateSubscribe() {
        //given
        SubscribeRequest request = new SubscribeRequest("test@gmail.com");
        Long loginId = 1L;
        Long albumId = 2L;
        Long candidateId = 3L;
        Member owner = mock(Member.class);
        Member candidate = mock(Member.class);
        Album album = mock(Album.class);

        when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(candidate));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(album.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(loginId);
        when(album.getId()).thenReturn(albumId);
        when(candidate.getId()).thenReturn(candidateId);
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, candidateId)).thenReturn(Optional.empty());

        //when then
        albumService.createSubscribe(albumId, loginId, request);
        verify(memberRepository, times(1)).findByUsername(request.getUsername());
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, candidateId);
        verify(subscribeRepository, times(1)).save(any(Subscribe.class));
    }

    @Test
    @DisplayName("초대 받은 사람 정보 없음 / 앨범 초대 / MemberNotFoundException")
    void givenInvalidSubscribeRequest_whenCreateSubscribe_thenMemberNotFoundException() {
        //given
        SubscribeRequest request = new SubscribeRequest("test@gmail.com");
        Long loginId = 1L;
        Long albumId = 2L;
        Long candidateId = 3L;

        when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        //when then
        assertThrows(MemberNotFoundException.class, () -> albumService.createSubscribe(albumId, loginId, request));
        verify(memberRepository, times(1)).findByUsername(request.getUsername());
        verify(albumRepository, times(0)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, candidateId);
        verify(subscribeRepository, times(0)).save(any(Subscribe.class));
    }

    @Test
    @DisplayName("앨범 정보 없음 / 앨범 초대 / MemberNotFoundException")
    void givenInvalidAlbumId_whenCreateSubscribe_thenAlbumNotFoundException() {
        //given
        SubscribeRequest request = new SubscribeRequest("test@gmail.com");
        Long loginId = 1L;
        Long albumId = 2L;
        Long candidateId = 3L;
        Member candidate = mock(Member.class);

        when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(candidate));
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        //when then
        assertThrows(AlbumNotFoundException.class, () -> albumService.createSubscribe(albumId, loginId, request));
        verify(memberRepository, times(1)).findByUsername(request.getUsername());
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, candidateId);
        verify(subscribeRepository, times(0)).save(any(Subscribe.class));
    }

    @Test
    @DisplayName("앨범 변경 권한 없음 / 앨범 초대 / MemberNotFoundException")
    void givenNoModificationPermission_whenCreateSubscribe_thenForbiddenException() {
        //given
        SubscribeRequest request = new SubscribeRequest("test@gmail.com");
        Long loginId = 1L;
        Long albumId = 2L;
        Long candidateId = 3L;
        Member owner = mock(Member.class);
        Member candidate = mock(Member.class);
        Album album = mock(Album.class);

        when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(candidate));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(album.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(999L);

        //when then
        assertThrows(ForbiddenException.class, () -> albumService.createSubscribe(albumId, loginId, request));
        verify(memberRepository, times(1)).findByUsername(request.getUsername());
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, candidateId);
        verify(subscribeRepository, times(0)).save(any(Subscribe.class));
    }

    @Test
    @DisplayName("이미 초대 받은 사용자 / 앨범 초대 / SubscribeFailException")
    void givenAlreadySubscribed_whenCreateSubscribe_thenSubscribeFailException() {
        //given
        SubscribeRequest request = new SubscribeRequest("test@gmail.com");
        Long loginId = 1L;
        Long albumId = 2L;
        Long candidateId = 3L;
        Member owner = mock(Member.class);
        Member candidate = mock(Member.class);
        Album album = mock(Album.class);

        when(memberRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(candidate));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(album.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(loginId);
        when(album.getId()).thenReturn(albumId);
        when(candidate.getId()).thenReturn(candidateId);
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, candidateId)).thenReturn(Optional.of(Subscribe.builder().build()));

        //when then
        assertThrows(SubscribeFailException.class, () -> albumService.createSubscribe(albumId, loginId, request));
        verify(memberRepository, times(1)).findByUsername(request.getUsername());
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, candidateId);
        verify(subscribeRepository, times(0)).save(any(Subscribe.class));
    }

    @Test
    @DisplayName("앨범 초대 수락 성공")
    void successApproveSubscribe() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Subscribe subscribe = spy(Subscribe.builder()
                .status(SubscribeStatus.PENDING)
                .build());

        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId))
                .thenReturn(Optional.of(subscribe));

        //when
        albumService.approveSubscribe(albumId, loginId);

        //then
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(subscribe, times(1)).setStatus(SubscribeStatus.APPROVED);
    }

    @Test
    @DisplayName("앨범 초대 정보 없음 / 앨범 초대 승인 / SubscribeNotFoundException")
    void givenNoSubscribeInvite_whenApproveSubscribe_thenSubscribeNotFoundException() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Subscribe subscribe = spy(Subscribe.builder()
                .status(SubscribeStatus.PENDING)
                .build());

        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId))
                .thenReturn(Optional.empty());

        //when then
        assertThrows(SubscribeNotFoundException.class, () -> albumService.approveSubscribe(albumId, loginId));
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(subscribe, times(0)).setStatus(SubscribeStatus.APPROVED);
    }

    @Test
    @DisplayName("앨범 초대 대기 상태가 아님 / 앨범 초대 승인 / SubscribeFailException")
    void givenStatusNotPending_whenApproveSubscribe_thenSubscribeFailException() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Subscribe subscribe = spy(Subscribe.builder()
                .status(SubscribeStatus.APPROVED)
                .build());

        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId))
                .thenReturn(Optional.of(subscribe));

        //when then
        assertThrows(SubscribeFailException.class, () -> albumService.approveSubscribe(albumId, loginId));
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(subscribe, times(0)).setStatus(SubscribeStatus.APPROVED);
    }

    @Test
    @DisplayName("앨범 구독자 목록 조회 성공")
    void successGetSubscribes() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(loginId);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(owner);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //when
        albumService.getSubscribers(albumId, loginId);

        //then
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(album, times(1)).getSubscribes();
    }

    @Test
    @DisplayName("앨범 정보 없음 / 앨범 구독자 조회 / AlbumNotFoundException")
    void givenNoAlbum_whenGetSubscribe_thenAlbumNotFoundException() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Album album = mock(Album.class);
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        //when then
        assertThrows(AlbumNotFoundException.class, () -> albumService.getSubscribers(albumId, loginId));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(album, times(0)).getSubscribes();
    }

    @Test
    @DisplayName("앨범 구독자 아님 / 앨범 구독자 조회 / SubscribeNotFoundException")
    void givenNoReadPermission_whenGetSubscribe_thenSubscribeNotFoundException() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(999L);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(owner);
        when(album.getId()).thenReturn(albumId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.empty());

        //when
        assertThrows(SubscribeNotFoundException.class, () -> albumService.getSubscribers(albumId, loginId));

        //then
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(album, times(0)).getSubscribes();
    }

    @Test
    @DisplayName("앨범 구독 대기 중 / 앨범 구독자 조회 / 403")
    void givenNotApprovedSubscriber_whenGetSubscribe_then403() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(999L);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(owner);
        when(album.getId()).thenReturn(albumId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        Subscribe subscribe = Subscribe.builder().status(SubscribeStatus.PENDING).build();
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId))
                .thenReturn(Optional.of(subscribe));

        //when
        assertThrows(ForbiddenException.class, () -> albumService.getSubscribers(albumId, loginId));

        //then
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(album, times(0)).getSubscribes();
    }

    @Test
    @DisplayName("앨범 구독자 삭제 성공")
    void successDeleteSubscribe() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Long targetId = 3L;

        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(loginId);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(owner);
        when(album.getId()).thenReturn(albumId);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //when
        albumService.deleteSubscribe(albumId, loginId, targetId);

        //then
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).delete(albumId, targetId);
    }

    @Test
    @DisplayName("앨범 조회 결과 없음 / 앨범 구독자 제거 / AlbumNotFoundException")
    void givenInvalidAlbumId_whenDeleteSubscribe_thenAlbumNotFoundException() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Long targetId = 3L;

        //when then
        assertThrows(AlbumNotFoundException.class, () -> albumService.deleteSubscribe(albumId, loginId, targetId));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).delete(albumId, targetId);
    }

    @Test
    @DisplayName("앨범 변경 권한 없음 / 앨범 구독자 제거 / ForbiddenException")
    void givenNoPermission_whenDeleteSubscribe_thenForbiddenException() {
        //given
        Long loginId = 1L;
        Long albumId = 2L;
        Long targetId = 3L;

        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(999L);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(owner);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //when then
        assertThrows(ForbiddenException.class, () -> albumService.deleteSubscribe(albumId, loginId, targetId));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).delete(albumId, targetId);
    }
}
