package com.zephyr.api.service;

import com.zephyr.api.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private MemoryService memoryService;
    private ResourceBundleMessageSource messageSource;
    private PostService postService;

    @BeforeEach
    void setUp() {
    }

//    @Test
//    @DisplayName("포스트 생성 성공")
//    public void givenAlbumOwner_successCreatePost() {
//        //given
//        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
//        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
//        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
//        PostCreateRequest postCreateRequest = new PostCreateRequest(1L, 1L, "Test Title", "Test Description", LocalDate.now(), 0, memoryCreateList);
//
//        Album album = mock(Album.class);
//        Member author = mock(Member.class);
//        Series series = mock(Series.class);
//
//        //when
//        Post result = postService.create(album, author, series, postCreateRequest);
//
//        //then
//        assertNotNull(result);
//        assertEquals(memoryCreate1.getContentUrl(), result.getThumbnailMemory().getContentUrl());
//        verify(postRepository, times(1)).save(any(Post.class));
//        verify(memoryService, times(1)).create(any(Post.class), any(MemoryCreate.class));
//    }
//
//    @Test
//    @DisplayName("잘못된 썸네일 인덱스 / 포스트 생성 / 포스트 생성 실패")
//    public void givenInvalidThumbnailIndex_whenCreatePost_thenFail() {
//        //given
//        MemoryCreate memoryCreate1 = new MemoryCreate(0, "Content URL 1", "Caption 1");
//        MemoryCreate memoryCreate2 = new MemoryCreate(1, "Content URL 2", "Caption 2");
//        List<MemoryCreate> memoryCreateList = Arrays.asList(memoryCreate1, memoryCreate2);
//        PostCreateRequest postCreateRequest = new PostCreateRequest(1L, 1L, "Test Title", "Test Description", LocalDate.now(), 999, memoryCreateList);
//
//        Album album = mock(Album.class);
//        Member author = mock(Member.class);
//        Series series = mock(Series.class);
//
//        //when then
//        assertThrows(InvalidRequestException.class,
//                () -> postService.create(album, author, series, postCreateRequest));
//        verify(postRepository, times(1)).save(any(Post.class));
//        verify(memoryService, times(1)).create(any(Post.class), any(MemoryCreate.class));
//    }
//
//    @Test
//    @DisplayName("포스트 조회 성공")
//    void successGetPost() {
//        //given
//        Long postId = 1L;
//        Post post = mock(Post.class);
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        //when
//        Post result = postService.get(postId);
//
//        //then
//        assertNotNull(result);
//        verify(postRepository, times(1)).findById(postId);
//    }
//
//    @Test
//    @DisplayName("부적절한 포스트 아이디 / 포스트 단건 조회 / 404")
//    void givenInvalidPostId_whenGetPost_then404() {
//        //given
//        Long postId = 1L;
//        when(postRepository.findById(postId)).thenReturn(Optional.empty());
//
//        //when
//        assertThrows(PostNotFoundException.class, () -> postService.get(postId));
//        verify(postRepository, times(1)).findById(postId);
//    }
}
