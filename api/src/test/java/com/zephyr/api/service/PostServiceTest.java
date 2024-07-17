package com.zephyr.api.service;

import com.zephyr.api.domain.*;
import com.zephyr.api.dto.MemoryCreateServiceDto;
import com.zephyr.api.dto.PostCreateServiceDto;
import com.zephyr.api.enums.ContentType;
import com.zephyr.api.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private AlbumService albumService;

    @Mock
    private SeriesService seriesService;

    @InjectMocks
    private PostService postService;

    @Test
    public void shouldSaveAndFetchPost() {
        //given
        MemoryCreateServiceDto coverMemory = new MemoryCreateServiceDto("requestId1", ContentType.IMAGE, "https://example.com/image1.jpg", "First image", 1.0);
        MemoryCreateServiceDto memory = new MemoryCreateServiceDto("requestId2", ContentType.VIDEO, "https://example.com/video1.mp4", "First video", 2.0);
        PostCreateServiceDto dto = new PostCreateServiceDto(
                123L,
                456L,
                789L,
                "Sample Title",
                "Sample Description",
                LocalDate.of(2024, 7, 13),
                coverMemory.getRequestId(),
                List.of(coverMemory, memory)
        );
        given(albumService.get(dto.getAlbumId())).willReturn(Album.builder().id(dto.getAlbumId()).build());
        given(memberService.get(dto.getMemberId())).willReturn(Member.builder().id(dto.getMemberId()).build());
        given(seriesService.get(dto.getSeriesId())).willReturn(Series.builder().id(dto.getSeriesId()).build());

        //when
        Post result = postService.create(dto);

        //then
        assertThat(result.getAlbum().getId()).isEqualTo(dto.getAlbumId());
        assertThat(result.getAuthor().getId()).isEqualTo(dto.getMemberId());
        assertThat(result.getSeries().getId()).isEqualTo(dto.getSeriesId());
        assertThat(result.getTitle()).isEqualTo(dto.getTitle());
        assertThat(result.getDescription()).isEqualTo(dto.getDescription());
        assertThat(result.getMemoryDate()).isEqualTo(dto.getMemoryDate());
        assertThat(result.getCoverMemory().getContentUrl()).isEqualTo(coverMemory.getContentUrl());

        assertThat(result.getMemories().size()).isSameAs(dto.getMemoryCreateServiceDtos().size());
        assertThat(result.getMemories()).extracting(Memory::getContentUrl)
                .containsExactly(coverMemory.getContentUrl(), memory.getContentUrl());
        assertThat(result.getMemories()).extracting(Memory::getCaption)
                .containsExactly(coverMemory.getCaption(), memory.getCaption());

        verify(postRepository).save(any(Post.class));
    }

    @Test
    public void illegalCoverMemoryRequestId_shouldFail() {
        //given 
        String illegalCoverMemoryRequestId = "key";
        MemoryCreateServiceDto coverMemory = new MemoryCreateServiceDto("requestId1", ContentType.IMAGE, "https://example.com/image1.jpg", "First image", 1.0);
        MemoryCreateServiceDto memory = new MemoryCreateServiceDto("requestId2", ContentType.VIDEO, "https://example.com/video1.mp4", "First video", 2.0);
        PostCreateServiceDto dto = new PostCreateServiceDto(
                123L,
                456L,
                789L,
                "Sample Title",
                "Sample Description",
                LocalDate.of(2024, 7, 13),
                illegalCoverMemoryRequestId,
                List.of(coverMemory, memory)
        );
        given(albumService.get(dto.getAlbumId())).willReturn(Album.builder().id(dto.getAlbumId()).build());
        given(memberService.get(dto.getMemberId())).willReturn(Member.builder().id(dto.getMemberId()).build());
        given(seriesService.get(dto.getSeriesId())).willReturn(Series.builder().id(dto.getSeriesId()).build());

        //when then
        assertThatThrownBy(() -> postService.create(dto)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void duplicateMemoryRequestId_shouldFail() {
        //given
        String duplicatedKey = "key";
        MemoryCreateServiceDto coverMemory = new MemoryCreateServiceDto(duplicatedKey, ContentType.IMAGE, "https://example.com/image1.jpg", "First image", 1.0);
        MemoryCreateServiceDto memory = new MemoryCreateServiceDto(duplicatedKey, ContentType.VIDEO, "https://example.com/video1.mp4", "First video", 2.0);
        PostCreateServiceDto dto = new PostCreateServiceDto(
                123L,
                456L,
                789L,
                "Sample Title",
                "Sample Description",
                LocalDate.of(2024, 7, 13),
                duplicatedKey,
                List.of(coverMemory, memory)
        );
        given(albumService.get(dto.getAlbumId())).willReturn(Album.builder().id(dto.getAlbumId()).build());
        given(memberService.get(dto.getMemberId())).willReturn(Member.builder().id(dto.getMemberId()).build());
        given(seriesService.get(dto.getSeriesId())).willReturn(Series.builder().id(dto.getSeriesId()).build());

        //when then
        assertThatThrownBy(() -> postService.create(dto)).isInstanceOf(IllegalArgumentException.class);
    }
}
