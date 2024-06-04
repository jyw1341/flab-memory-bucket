package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Content;
import com.zephyr.api.domain.Memory;
import com.zephyr.api.domain.Subscribe;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.AlbumNotFoundException;
import com.zephyr.api.exception.ForbiddenException;
import com.zephyr.api.exception.MemoryNotFoundException;
import com.zephyr.api.exception.SubscribeNotFoundException;
import com.zephyr.api.repository.*;
import com.zephyr.api.request.MemoryCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final ContentRepository contentRepository;
    private final AlbumRepository albumRepository;
    private final SubscribeRepository subscribeRepository;
    private final CommentRepository commentRepository;
    private final MessageSource messageSource;

    public Long create(MemoryCreate memoryCreate, Long loginId) {
        Album album = albumRepository.findById(memoryCreate.getAlbumId())
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));

        if (!album.getOwner().getId().equals(loginId)) {
            validSubscribe(album, loginId);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(memoryCreate.getMemoryDate(), dateFormatter);

        Memory memory = Memory.builder()
                .album(album)
                .author(album.getOwner())
                .title(memoryCreate.getTitle())
                .description(memoryCreate.getDescription())
                .memoryDate(localDate.atStartOfDay())
                .build();
        memoryRepository.save(memory);

        List<Content> contents = memoryCreate.getContents().stream().map(request -> Content.builder()
                .memory(memory)
                .title(request.getTitle())
                .description(request.getDescription())
                .index(request.getIndex())
                .url(request.getUrl()).build()
        ).toList();
        contentRepository.saveAll(contents);

        return memory.getId();
    }

    public Memory get(Long albumId, Long memoryId, Long loginId) {
        Memory memory = memoryRepository.findByAlbumIdAndMemoryId(albumId, memoryId)
                .orElseThrow(() -> new MemoryNotFoundException(messageSource));

        Album album = memory.getAlbum();
        if (!album.getOwner().getId().equals(loginId)) {
            validSubscribe(album, loginId);
        }

        return memory;
    }

    private void validSubscribe(Album album, Long loginId) {
        Subscribe subscribe = subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId)
                .orElseThrow(() -> new SubscribeNotFoundException(messageSource));

        if (!subscribe.getStatus().equals(SubscribeStatus.APPROVED)) {
            throw new ForbiddenException(messageSource);
        }
    }
}
