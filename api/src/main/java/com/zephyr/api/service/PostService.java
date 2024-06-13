package com.zephyr.api.service;

import com.zephyr.api.domain.*;
import com.zephyr.api.exception.InvalidRequestException;
import com.zephyr.api.exception.PostNotFoundException;
import com.zephyr.api.repository.MemoryRepository;
import com.zephyr.api.repository.PostRepository;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.request.PostCreate;
import com.zephyr.api.request.PostSearch;
import com.zephyr.api.request.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemoryRepository memoryRepository;
    private final MessageSource messageSource;

    public Post create(Album album, Member author, Series series, PostCreate postCreate) {
        Post post = Post.builder()
                .album(album)
                .author(author)
                .series(series)
                .title(postCreate.getTitle())
                .description(postCreate.getDescription())
                .memoryDate(postCreate.getMemoryDate())
                .build();
        postRepository.save(post);

        for (MemoryCreate memoryCreate : postCreate.getMemoryCreates()) {
            Memory memory = Memory.builder()
                    .post(post)
                    .index(memoryCreate.getIndex())
                    .contentUrl(memoryCreate.getContentUrl())
                    .caption(memoryCreate.getCaption())
                    .build();
            memoryRepository.save(memory);
            if (postCreate.getThumbnailIndex().equals(memory.getIndex())) {
                post.setThumbnailMemory(memory);
            }
        }

        if (post.getThumbnailMemory() == null) {
            throw new InvalidRequestException("thumbnail_index",
                    messageSource.getMessage("invalid_post_thumbnailIndex", null, Locale.KOREA));
        }

        return post;
    }

    public Post get(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(messageSource));
    }

    public List<Post> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch);
    }

    public Post update(Long loginId, Long postId, PostUpdate postUpdate) {
        return null;
    }

}
