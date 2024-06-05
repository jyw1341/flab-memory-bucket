package com.zephyr.api.service;

import com.zephyr.api.domain.*;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.*;
import com.zephyr.api.repository.*;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.request.PostCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private SubscribeRepository subscribeRepository;
    @Mock
    private SeriesRepository seriesRepository;
    @Mock
    private MemoryRepository memoryRepository;

    private ResourceBundleMessageSource messageSource;

    private PostService postService;

    @BeforeEach
    void setUp() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "errors");
        messageSource.setDefaultEncoding("utf-8");

        postService = new PostService(postRepository, albumRepository, subscribeRepository, seriesRepository, memoryRepository, messageSource);
    }

    @Test
    @DisplayName("앨범 소유자 / 포스트 생성 성공")
    public void givenAlbumOwner_successCreatePost() {
        //given
        Long loginId = 1L;
        Long albumId = 1L;
        Long seriesId = 1L;

        //앨범 생성 DTO
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(albumId, seriesId, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);

        //앨범의 소유자를 현재 사용자와 같도록 설정
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(loginId);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(member);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //포스트에 지정될 시리즈가 존재하도록 설정
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(new Series("series name")));

        Post post = mock(Post.class);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        //when
        Post result = postService.create(loginId, postCreate);

        //then
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(seriesRepository, times(1)).findById(seriesId);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(memoryRepository, times(memoryCreateList.size())).save(any(Memory.class));
    }

    @Test
    @DisplayName("앨범 구독자 / 포스트 생성 성공")
    public void givenAlbumSubscriber_successCreatePost() {
        //given
        Long loginId = 1L;
        Long ownerId = 2L;
        Long albumId = 1L;
        Long seriesId = 1L;

        //앨범 생성 DTO
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(albumId, seriesId, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);

        //앨범의 소유자와 현재 사용자를 다르게 설정
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(ownerId);
        Album album = mock(Album.class);
        when(album.getId()).thenReturn(albumId);
        when(album.getOwner()).thenReturn(member);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //현재 사용자를 앨범 구독자로 설정
        Subscribe subscribe = mock(Subscribe.class);
        when(subscribe.getStatus()).thenReturn(SubscribeStatus.APPROVED);
        when(subscribe.getSubscriber()).thenReturn(Member.builder().build());
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.of(subscribe));

        //포스트에 지정될 시리즈가 존재하도록 설정
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(new Series("series name")));

        //when
        Post result = postService.create(loginId, postCreate);

        //then
        assertNotNull(result);
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(seriesRepository, times(1)).findById(seriesId);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(memoryRepository, times(memoryCreateList.size())).save(any(Memory.class));
    }

    @Test
    @DisplayName("잘못된 앨범 아이디 / 포스트 생성 / 포스트 생성 실패")
    public void givenInvalidAlbumId_whenCreatePost_thenFail() {
        //given
        Long loginId = 1L;
        Long albumId = 999L;
        Long seriesId = 1L;

        //앨범 생성 DTO
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(albumId, seriesId, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        //when then
        assertThrows(AlbumNotFoundException.class, () -> postService.create(loginId, postCreate));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(seriesRepository, times(0)).findById(seriesId);
        verify(postRepository, times(0)).save(any(Post.class));
        verify(memoryRepository, times(0)).save(any(Memory.class));
    }

    @Test
    @DisplayName("앨범 소유자나 구독자가 아닌 사용자 / 포스트 생성 / 포스트 생성 실패")
    public void givenForbiddenUser_whenCreatePost_thenFail() {
        //given
        Long loginId = 1L;
        Long ownerId = 2L;
        Long albumId = 1L;
        Long seriesId = 1L;

        //앨범 생성 DTO
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(albumId, seriesId, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);

        //앨범의 소유자와 현재 사용자를 다르게 설정
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(ownerId);
        Album album = mock(Album.class);
        when(album.getId()).thenReturn(albumId);
        when(album.getOwner()).thenReturn(member);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //구독 정보가 없도록 설정
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.empty());

        //when then
        assertThrows(SubscribeNotFoundException.class, () -> postService.create(loginId, postCreate));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(seriesRepository, times(0)).findById(seriesId);
        verify(postRepository, times(0)).save(any(Post.class));
        verify(memoryRepository, times(0)).save(any(Memory.class));
    }

    @Test
    @DisplayName("구독 승인 대기 상태인 사용자 / 포스트 생성 / 포스트 생성 실패")
    public void givenSubscribePendingUser_whenCreatePost_thenFail() {
        //given
        Long loginId = 1L;
        Long ownerId = 2L;
        Long albumId = 1L;
        Long seriesId = 1L;

        //앨범 생성 DTO
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(albumId, seriesId, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);

        //앨범의 소유자와 현재 사용자를 다르게 설정
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(ownerId);
        Album album = mock(Album.class);
        when(album.getId()).thenReturn(albumId);
        when(album.getOwner()).thenReturn(member);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //구독 대기 상태로 설정
        Subscribe subscribe = mock(Subscribe.class);
        when(subscribe.getStatus()).thenReturn(SubscribeStatus.PENDING);
        when(subscribeRepository.findByAlbumIdAndMemberId(albumId, loginId)).thenReturn(Optional.of(subscribe));

        //when then
        assertThrows(ForbiddenException.class, () -> postService.create(loginId, postCreate));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(1)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(seriesRepository, times(0)).findById(seriesId);
        verify(postRepository, times(0)).save(any(Post.class));
        verify(memoryRepository, times(0)).save(any(Memory.class));
    }

    @Test
    @DisplayName("시리즈 조회 결과 없음 / 포스트 생성 / 포스트 생성 실패")
    public void givenInvalidSeriesId_whenCreatePost_thenFail() {
        //given
        Long loginId = 1L;
        Long albumId = 1L;
        Long seriesId = 1L;

        //앨범 생성 DTO
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(albumId, seriesId, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);

        //앨범의 소유자를 현재 사용자와 같도록 설정
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(loginId);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(member);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //포스트에 지정될 시리즈가 존재하도록 설정
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.empty());

        //when then
        assertThrows(SeriesNotFoundException.class, () -> postService.create(loginId, postCreate));
        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(seriesRepository, times(1)).findById(seriesId);
        verify(postRepository, times(0)).save(any(Post.class));
        verify(memoryRepository, times(0)).save(any(Memory.class));
    }

    @Test
    @DisplayName("잘못된 썸네일 인덱스 / 포스트 생성 / 포스트 생성 실패")
    public void givenInvalidThumbnailIndex_whenCreatePost_thenFail() {
        //given
        Long loginId = 1L;
        Long albumId = 1L;
        Long seriesId = 1L;
        Integer invalidIndex = 999;

        //앨범 생성 DTO
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(albumId, seriesId, "Test Title", "Test Description", LocalDate.now(), invalidIndex, memoryCreateList);

        //앨범의 소유자를 현재 사용자와 같도록 설정
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(loginId);
        Album album = mock(Album.class);
        when(album.getOwner()).thenReturn(member);
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        //포스트에 지정될 시리즈가 존재하도록 설정
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(new Series("series name")));

        //when then
        BaseException exception = assertThrows(InvalidRequestException.class, () -> postService.create(loginId, postCreate));
        assertEquals(
                messageSource.getMessage("invalid_post_thumbnailIndex", null, Locale.KOREA),
                exception.getValidation().get("thumbnail_index")
        );

        verify(albumRepository, times(1)).findById(albumId);
        verify(subscribeRepository, times(0)).findByAlbumIdAndMemberId(albumId, loginId);
        verify(seriesRepository, times(1)).findById(seriesId);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(memoryRepository, times(memoryCreateList.size())).save(any(Memory.class));
    }
}
