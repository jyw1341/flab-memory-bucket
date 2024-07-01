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

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final SeriesService seriesService;
    private final AlbumService albumService;
    private final MemoryService memoryService;
    private final MemberService memberService;
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

        for (int i = 0; i < dto.getMemoryCreateRequests().size(); i++) {
            Memory memory = Memory.builder()
                    .index(dto.getMemoryCreateRequests().get(i).getIndex())
                    .caption(dto.getMemoryCreateRequests().get(i).getCaption())
                    .contentUrl(dto.getMemoryCreateRequests().get(i).getContentUrl())
                    .build();
            post.addMemory(memory);
        }

        postRepository.save(post);
        seriesService.update(post);

        return post;
    }

    public Post get(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
    }

    public List<Post> getList(PostListServiceDto dto) {
        return postRepository.findAll();
    }

    @Transactional
    public Post update(PostUpdateServiceDto dto) {
        Member member = memberService.get(dto.getMemberId());
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        Series series = seriesService.get(dto.getSeriesId());

        validPostAuthor(post, member);

        post.setSeries(series);
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setThumbnailUrl(dto.getThumbnailUrl());
        post.setMemoryDate(dto.getMemoryDate());

        for (MemoryUpdateServiceDto memoryUpdateServiceDto : dto.getMemoryUpdateServiceDtos()) {
            memoryService.update(memoryUpdateServiceDto);
        }

        return post;
    }

    @Transactional
    public void delete(PostDeleteServiceDto dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        Member member = memberService.get(dto.getMemberId());

        validPostAuthor(post, member);

        postRepository.delete(post);
    }

    private void validPostAuthor(Post post, Member member) {
        if (post.getAuthor().getId().equals(member.getId())) {
            return;
        }

        throw new ForbiddenException(messageSource);
    }
}
