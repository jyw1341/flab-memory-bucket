package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Memory;
import com.zephyr.api.domain.Subscribe;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.AlbumNotFoundException;
import com.zephyr.api.exception.ForbiddenException;
import com.zephyr.api.exception.MemoryNotFoundException;
import com.zephyr.api.exception.SubscribeNotFoundException;
import com.zephyr.api.repository.AlbumRepository;
import com.zephyr.api.repository.ContentRepository;
import com.zephyr.api.repository.MemoryRepository;
import com.zephyr.api.repository.SubscribeRepository;
import com.zephyr.api.request.ContentCreate;
import com.zephyr.api.request.MemoryCreate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryServiceTest {

    @Mock
    private MemoryRepository memoryRepository;
    @Mock
    private ContentRepository contentRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private SubscribeRepository subscribeRepository;
    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private MemoryService memoryService;

    @Test
    @DisplayName("조건에 맞는 요청 / 기억 생성 / 기억 반환")
    void givenValidRequest_whenCreateMemory_success() {
        //given
        List<ContentCreate> contentCreates = new ArrayList<>();
        contentCreates.add(new ContentCreate("메모 제목1", "메모1", "url1", 1));
        contentCreates.add(new ContentCreate("메모 제목2", "메모2", "url2", 2));

        Member member = Member.builder().build();
        Album album = Album.builder().owner(member).build();

        MemoryCreate memoryCreate = new MemoryCreate(
                album.getId(),
                "제목",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(memoryCreate.getMemoryDate(), dateFormatter);

        Memory memory = Memory.builder()
                .album(album)
                .author(album.getOwner())
                .title(memoryCreate.getTitle())
                .description(memoryCreate.getDescription())
                .memoryDate(localDate.atStartOfDay())
                .build();
        when(albumRepository.findById(memoryCreate.getAlbumId()))
                .thenReturn(Optional.of(album));

        //when
        Long result = memoryService.create(memoryCreate, 1L);

        //then
        assertNotNull(result);
        verify(memoryRepository, times(1)).save(any(Memory.class));
        verify(contentRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("앨범이 존재 하지 않는 경우 / 기억 생성 / AlbumNotFoundException")
    void givenInvalidAlbumId_whenCreateMemory_returnAlbumNotFound() {
        //given
        List<ContentCreate> contentCreates = new ArrayList<>();
        contentCreates.add(new ContentCreate("메모 제목1", "메모1", "url1", 1));
        contentCreates.add(new ContentCreate("메모 제목2", "메모2", "url2", 2));

        Member member = Member.builder().build();
        Album album = Album.builder().owner(member).build();

        MemoryCreate memoryCreate = new MemoryCreate(
                album.getId(),
                "제목",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        when(albumRepository.findById(memoryCreate.getAlbumId()))
                .thenReturn(Optional.empty());

        //when then
        assertThrows(AlbumNotFoundException.class, () -> memoryService.create(memoryCreate, 1L));
    }

    @Test
    @DisplayName("앨범이 소유자, 구독자 x / 기억 생성 / SubscribeNotFoundException")
    void givenInvalidAlbumId_whenCreateMemory_returnSubscribeNotFoundException() {
        //given
        List<ContentCreate> contentCreates = new ArrayList<>();
        contentCreates.add(new ContentCreate("메모 제목1", "메모1", "url1", 1));
        contentCreates.add(new ContentCreate("메모 제목2", "메모2", "url2", 2));

        Long loginId = 100L;
        Member member = Member.builder().build();
        Album album = Album.builder().owner(member).build();

        MemoryCreate memoryCreate = new MemoryCreate(
                album.getId(),
                "제목",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );

        when(albumRepository.findById(memoryCreate.getAlbumId()))
                .thenReturn(Optional.of(album));
        when(subscribeRepository.findByAlbumIdAndMemberId(memoryCreate.getAlbumId(), loginId))
                .thenReturn(Optional.empty());

        //when then
        assertThrows(SubscribeNotFoundException.class, () -> memoryService.create(memoryCreate, loginId));
    }


    @Test
    @DisplayName("앨범 아이디, 메모리 아이디, 앨범 소유주 / 기억 조회 / 기억 반환")
    void givenValidRequestAndOwner_whenFindOneMemory_returnMemory() {
        //given
        Long ownerId = 1L;
        Long albumId = 2L;
        Long memoryId = 3L;

        Member owner = Member.builder().build();
        Album album = Album.builder().owner(owner).build();
        Memory memory = Memory.builder().id(memoryId).album(album).build();

        when(memoryRepository.findByAlbumIdAndMemoryId(albumId, memoryId))
                .thenReturn(Optional.of(memory));

        //when
        Memory result = memoryService.get(albumId, memoryId, ownerId);

        //then
        assertEquals(memory.getId(), result.getId());
    }

    @Test
    @DisplayName("앨범 아이디, 메모리 아이디, 앨범 구독자 / 기억 조회 / 기억 반환")
    void givenValidRequestAndSubscriber_whenFindOneMemory_returnMemory() {
        //given
        Long loginId = 100L;
        Long ownerId = 1L;
        Long albumId = 2L;
        Long memoryId = 3L;

        Member owner = Member.builder().build();
        Album album = Album.builder().owner(owner).build();
        Memory memory = Memory.builder().id(memoryId).album(album).build();
        Subscribe subscribe = Subscribe.builder().status(SubscribeStatus.APPROVED).build();

        when(memoryRepository.findByAlbumIdAndMemoryId(albumId, memoryId))
                .thenReturn(Optional.of(memory));
        when(subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId))
                .thenReturn(Optional.of(subscribe));
        //when
        Memory result = memoryService.get(albumId, memoryId, loginId);

        //then
        assertEquals(memory.getId(), result.getId());
    }

    @Test
    @DisplayName("소유자, 구독자 아닌 경우 / 기억 조회 / SubscribeNotFoundException")
    void givenNoAuthorityUser_whenFindOneMemory_returnSubscribeNotFoundException() {
        //given
        Long loginId = 100L;
        Long ownerId = 1L;
        Long albumId = 2L;
        Long memoryId = 3L;

        Member owner = Member.builder().build();
        Album album = Album.builder().owner(owner).build();
        Memory memory = Memory.builder().id(memoryId).album(album).build();

        when(memoryRepository.findByAlbumIdAndMemoryId(albumId, memoryId))
                .thenReturn(Optional.of(memory));
        when(subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId))
                .thenReturn(Optional.empty());
        //when then
        assertThrows(SubscribeNotFoundException.class, () -> memoryService.get(albumId, memoryId, loginId));
    }

    @Test
    @DisplayName("구독 승인 대기 중인 사용자 / 기억 조회 / ForbiddenException")
    void givenPendingMember_whenFindOneMemory_returnSubscribeNotFoundException() {
        //given
        Long loginId = 100L;
        Long ownerId = 1L;
        Long albumId = 2L;
        Long memoryId = 3L;

        Member owner = Member.builder().build();
        Album album = Album.builder().owner(owner).build();
        Memory memory = Memory.builder().id(memoryId).album(album).build();
        Subscribe subscribe = Subscribe.builder().status(SubscribeStatus.PENDING).build();

        when(memoryRepository.findByAlbumIdAndMemoryId(albumId, memoryId))
                .thenReturn(Optional.of(memory));
        when(subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId))
                .thenReturn(Optional.of(subscribe));
        //when then
        assertThrows(ForbiddenException.class, () -> memoryService.get(albumId, memoryId, loginId));
    }

    @Test
    @DisplayName("잘못된 메모리 아이디 / 기억 조회 / MemoryNotFoundException")
    void givenInValidMemoryId_whenFindOneMemory_returnMemoryNotFoundException() {
        //given
        Long loginId = 100L;
        Long albumId = 2L;
        Long memoryId = 3L;

        when(memoryRepository.findByAlbumIdAndMemoryId(albumId, memoryId))
                .thenReturn(Optional.empty());

        //when then
        assertThrows(MemoryNotFoundException.class, () -> memoryService.get(albumId, memoryId, loginId));
    }
}
