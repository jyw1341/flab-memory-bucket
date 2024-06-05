package com.zephyr.api.service;

import com.zephyr.api.domain.*;
import com.zephyr.api.enums.SubscribeStatus;
import com.zephyr.api.exception.*;
import com.zephyr.api.repository.*;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.request.PostCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final AlbumRepository albumRepository;
    private final SubscribeRepository subscribeRepository;
    private final SeriesRepository seriesRepository;
    private final MemoryRepository memoryRepository;
    private final MessageSource messageSource;

    public Post create(Long loginId, PostCreate postCreate) {
        Album album = albumRepository.findById(postCreate.getAlbumId())
                .orElseThrow(() -> new AlbumNotFoundException(messageSource));
        Member author = getAuthor(album, loginId);
        Series series = seriesRepository.findById(postCreate.getSeriesId())
                .orElseThrow(() -> new SeriesNotFoundException(messageSource));

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

        validSetThumbnail(post);

        return post;
    }

    private Member getAuthor(Album album, Long loginId) {
        if (album.getOwner().getId().equals(loginId)) {
            return album.getOwner();
        }

        Subscribe subscribe = subscribeRepository.findByAlbumIdAndMemberId(album.getId(), loginId)
                .orElseThrow(() -> new SubscribeNotFoundException(messageSource));
        if (!subscribe.getStatus().equals(SubscribeStatus.APPROVED)) {
            throw new ForbiddenException(messageSource);
        }

        return subscribe.getSubscriber();
    }

    private void validSetThumbnail(Post post) {
        if (post.getThumbnailMemory() == null) {
            throw new InvalidRequestException(
                    "thumbnail_index",
                    messageSource.getMessage("invalid_post_thumbnailIndex", null, Locale.KOREA)
            );
        }
    }
}
