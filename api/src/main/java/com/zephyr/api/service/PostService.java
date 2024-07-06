package com.zephyr.api.service;

import com.zephyr.api.domain.*;
import com.zephyr.api.dto.*;
import com.zephyr.api.exception.PostNotFoundException;
import com.zephyr.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final AlbumService albumService;
    private final SeriesService seriesService;
    private final MessageSource messageSource;

    @Transactional
    public Post create(PostCreateServiceDto dto) {
        Album album = albumService.get(dto.getAlbumId());
        Member member = memberService.get(dto.getMemberId());
        Series series = seriesService.get(dto.getSeriesId());
        Post post = Post.builder()
                .album(album)
                .author(member)
                .series(series)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .memoryDate(dto.getMemoryDate())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();

        for (MemoryCreateServiceDto memoryCreateServiceDto : dto.getMemoryCreateServiceDtos()) {
            post.addMemory(Memory.builder()
                    .index(memoryCreateServiceDto.getIndex())
                    .caption(memoryCreateServiceDto.getCaption())
                    .contentUrl(memoryCreateServiceDto.getContentUrl())
                    .build());
        }
        postRepository.save(post);

        return post;
    }

    public Post get(Long postId) {
        return postRepository.findByIdFetchMemberAndSeriesAndMemories(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
    }

    public List<Post> getList(PostSearchServiceDto dto) {
        return postRepository.search(dto);
    }

    @Transactional
    public void update(PostUpdateServiceDto dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        Series series = seriesService.get(dto.getSeriesId());

        post.setSeries(series);
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setMemoryDate(dto.getMemoryDate());

        post.getMemories().stream()
                .filter(memory -> memory.getContentUrl().equals(dto.getThumbnailUrl()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        post.setThumbnailUrl(dto.getThumbnailUrl());
    }

    public void delete(PostDeleteServiceDto dto) {
        postRepository.deleteById(dto.getPostId());
    }

    @Transactional
    public void updateMemories(Long postId, List<MemoryUpdateServiceDto> dtos) {
        Post post = postRepository.findByIdFetchMemories(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        Map<Long, Memory> memories = post.getMemories()
                .stream()
                .collect(Collectors.toMap(Memory::getId, memory -> memory));

        post.getMemories().clear();
        dtos.sort(Comparator.comparing(MemoryUpdateServiceDto::getIndex));
        for (MemoryUpdateServiceDto dto : dtos) {
            if (dto.getId() == null) {
                post.addMemory(Memory.builder()
                        .index(dto.getIndex())
                        .caption(dto.getCaption())
                        .contentUrl(dto.getContentUrl())
                        .build()
                );
                continue;
            }
            Memory updatedMemory = memories.remove(dto.getId());
            updatedMemory.setIndex(dto.getIndex());
            updatedMemory.setCaption(dto.getCaption());
            post.addMemory(updatedMemory);
        }

        for (Memory removedMemory : memories.values()) {
            if (post.getThumbnailUrl().equals(removedMemory.getContentUrl())) {
                post.setThumbnailUrl(post.getMemories().get(0).getContentUrl());
            }
            removedMemory.setPost(null);
        }

        postRepository.save(post);
    }
}
