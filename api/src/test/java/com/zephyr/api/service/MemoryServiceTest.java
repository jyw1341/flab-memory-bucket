package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Memory;
import com.zephyr.api.exception.AlbumNotFoundException;
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

        Member member = Member.builder().id(1L).build();
        Album album = Album.builder().id(1L).owner(member).build();

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
        when(memoryRepository.save(any(Memory.class))).thenReturn(memory);

        //when
        Memory result = memoryService.create(memoryCreate, 1L);

        //then
        assertNotNull(result);
        assertEquals(memoryCreate.getAlbumId(), result.getAlbum().getId());
        assertEquals(memoryCreate.getTitle(), result.getTitle());
        assertEquals(memoryCreate.getDescription(), result.getDescription());
        assertEquals(memoryCreate.getMemoryDate(), result.getMemoryDate().format(dateFormatter));

        verify(memoryRepository, times(1)).save(any(Memory.class));
        verify(contentRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("앨범이 존재 하지 않는 경우 / 기억 생성 / 기억 반환")
    void givenInvalidAlbumId_whenCreateMemory_returnAlbumNotFound() {
        //given
        List<ContentCreate> contentCreates = new ArrayList<>();
        contentCreates.add(new ContentCreate("메모 제목1", "메모1", "url1", 1));
        contentCreates.add(new ContentCreate("메모 제목2", "메모2", "url2", 2));

        Member member = Member.builder().id(1L).build();
        Album album = Album.builder().id(1L).owner(member).build();

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

}
