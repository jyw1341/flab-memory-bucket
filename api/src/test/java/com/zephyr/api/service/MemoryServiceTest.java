package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Memory;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    @DisplayName("")
    void test() {
        //given
        List<ContentCreate> contentCreates = new ArrayList<>();
        contentCreates.add(new ContentCreate("메모 제목1", "메모1", "url1", 1));
        contentCreates.add(new ContentCreate("메모 제목2", "메모2", "url2", 2));

        MemoryCreate memoryCreate = new MemoryCreate(
                1L,
                "제목",
                "설명",
                "2024-05-30",
                new ArrayList<>(),
                contentCreates
        );


        //when
        when(albumRepository.findById(memoryCreate.getAlbumId()))
                .thenReturn(Optional.of(Album.builder().build()));
        Memory memory = memoryService.create(memoryCreate, 1L);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //then
        assertEquals(memoryCreate.getAlbumId(), memory.getAlbum().getId());
        assertEquals(memoryCreate.getTitle(), memory.getTitle());
        assertEquals(memoryCreate.getDescription(), memory.getDescription());
        assertEquals(memoryCreate.getMemoryDate(), memory.getMemoryDate().format(dateFormatter));
    }


}
