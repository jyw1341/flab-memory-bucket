package com.zephyr.api.service;

import com.zephyr.api.domain.*;
import com.zephyr.api.exception.InvalidRequestException;
import com.zephyr.api.exception.PostNotFoundException;
import com.zephyr.api.repository.MemoryRepository;
import com.zephyr.api.repository.PostRepository;
import com.zephyr.api.request.MemoryCreate;
import com.zephyr.api.request.PostCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private MemoryRepository memoryRepository;
    private ResourceBundleMessageSource messageSource;
    private PostService postService;

    @BeforeEach
    void setUp() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "errors");
        messageSource.setDefaultEncoding("utf-8");

        postService = new PostService(postRepository, memoryRepository, messageSource);
    }

    @Test
    @DisplayName("포스트 생성 성공")
    public void givenAlbumOwner_successCreatePost() {
        //given
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(1L, 1L, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);

        Album album = mock(Album.class);
        Member author = mock(Member.class);
        Series series = mock(Series.class);

        //when
        Post result = postService.create(album, author, series, postCreate);

        //then
        assertNotNull(result);
        assertEquals(memoryCreate1.getContentUrl(), result.getThumbnailMemory().getContentUrl());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(memoryRepository, times(memoryCreateList.size())).save(any(Memory.class));
    }

    @Test
    @DisplayName("잘못된 썸네일 인덱스 / 포스트 생성 / 포스트 생성 실패")
    public void givenInvalidThumbnailIndex_whenCreatePost_thenFail() {
        //given
        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
        PostCreate postCreate = new PostCreate(1L, 1L, "Test Title", "Test Description", LocalDate.now(), 999, memoryCreateList);

        Album album = mock(Album.class);
        Member author = mock(Member.class);
        Series series = mock(Series.class);

        //when then
        String ErrorMessage = assertThrows(
                InvalidRequestException.class,
                () -> postService.create(album, author, series, postCreate)
        ).getMessage();
        assertEquals(messageSource.getMessage("invalid_post_thumbnailIndex", null, Locale.KOREA), ErrorMessage);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(memoryRepository, times(memoryCreateList.size())).save(any(Memory.class));
    }

    @Test
    @DisplayName("포스트 조회 성공")
    void successGetPost() {
        //given
        Long postId = 1L;
        Post post = mock(Post.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        //when
        Post result = postService.get(postId);

        //then
        assertNotNull(result);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    @DisplayName("부적절한 포스트 아이디 / 포스트 단건 조회 / 404")
    void givenInvalidPostId_whenGetPost_then404() {
        //given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        assertThrows(PostNotFoundException.class, () -> postService.get(postId));
        verify(postRepository, times(1)).findById(postId);
    }
}
