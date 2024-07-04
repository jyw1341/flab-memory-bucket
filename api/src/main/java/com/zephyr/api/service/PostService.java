package com.zephyr.api.service;

import com.zephyr.api.domain.*;
import com.zephyr.api.dto.*;
import com.zephyr.api.exception.ForbiddenException;
import com.zephyr.api.exception.PostNotFoundException;
import com.zephyr.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FileService fileService;
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
        updateSeriesAggregation(series);

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
        Member member = memberService.get(dto.getMemberId());
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        Series prevSeries = post.getSeries();
        Series newSeries = seriesService.get(dto.getSeriesId());

        validPostAuthor(post, member);
        post.setSeries(newSeries);
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setThumbnailUrl(dto.getThumbnailUrl());
        post.setMemoryDate(dto.getMemoryDate());

        updateSeriesAggregation(prevSeries);
        updateSeriesAggregation(newSeries);
    }

    @Transactional
    public void delete(PostDeleteServiceDto dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        Member member = memberService.get(dto.getMemberId());
        Series series = post.getSeries();

        validPostAuthor(post, member);
        List<String> urls = post.getMemories().stream().map(Memory::getContentUrl).toList();
        postRepository.delete(post);
        updateSeriesAggregation(series);
        fileService.deleteObjects(urls);
    }

    @Transactional
    public void updateMemories(Long postId, List<MemoryUpdateServiceDto> dtos) {
        Post post = postRepository.findByIdFetchMemories(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        Map<Long, Memory> memories = post.getMemories()
                .stream()
                .collect(Collectors.toMap(Memory::getId, memory -> memory));

        post.getMemories().clear();
        for (MemoryUpdateServiceDto dto : dtos) {
            if (dto.getId() == null) {
                post.addMemory(Memory.builder()
                        .index(dto.getIndex())
                        .caption(dto.getCaption())
                        .contentUrl(dto.getContentUrl())
                        .build());
                continue;
            }
            Memory memory = memories.remove(dto.getId());
            memory.setIndex(dto.getIndex());
            memory.setCaption(dto.getCaption());
            post.addMemory(memory);
        }
        postRepository.save(post);
        fileService.deleteObjects(memories.values().stream().map(Memory::getContentUrl).toList());
    }

    private void validPostAuthor(Post post, Member member) {
        if (post.getAuthor().getId().equals(member.getId())) {
            return;
        }

        throw new ForbiddenException(messageSource);
    }

    private void updateSeriesAggregation(Series series) {
        if (series != null) {
            seriesService.updateAggregation(series.getId());
        }
    }
}
