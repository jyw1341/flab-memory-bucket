package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Member;
import com.zephyr.api.domain.Memory;
import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.request.MemoryUpdateRequest;
import com.zephyr.api.dto.request.PostCreateRequest;
import com.zephyr.api.dto.request.PostSearchRequest;
import com.zephyr.api.dto.request.PostUpdateRequest;
import com.zephyr.api.exception.ForbiddenException;
import com.zephyr.api.exception.PostNotFoundException;
import com.zephyr.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final SeriesService seriesService;
    private final AlbumService albumService;
    private final MemoryService memoryService;
    private final MessageSource messageSource;

    @Transactional
    public Post create(Member member, PostCreateRequest dto) {
        Album album = albumService.get(member, member.getId());

        Post post = Post.builder()
                .album(album)
                .author(member)
                .series(seriesService.get(album, dto.getSeries()))
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

        return post;
    }

    public Post get(Member member, Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
    }

    public List<Post> getList(PostSearchRequest dto) {
        return postRepository.findAll();
    }

    @Transactional
    public Post update(Member member, Long postId, PostUpdateRequest dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        validPostAuthor(post, member);

        post.setSeries(seriesService.get(post.getAlbum(), dto.getSeries()));
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setThumbnailUrl(dto.getThumbnailUrl());
        post.setMemoryDate(dto.getMemoryDate());

        for (MemoryUpdateRequest memoryUpdateRequest : dto.getMemoryUpdateRequests()) {
            memoryService.update(memoryUpdateRequest);
        }

        return post;
    }

    @Transactional
    public void delete(Member member, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
        validPostAuthor(post, member);

        postRepository.delete(post);
    }

    public void validPostAuthor(Post post, Member member) {
        if (post.getAuthor().equals(member)) {
            return;
        }

        throw new ForbiddenException(messageSource);
    }
}
