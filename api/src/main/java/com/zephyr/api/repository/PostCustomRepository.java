package com.zephyr.api.repository;

import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.PostSearchServiceDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface PostCustomRepository {

    Page<Post> search(PostSearchServiceDto dto);

    Optional<Post> findByIdFetchMemories(Long postId);

    Optional<Post> findByIdFetchMemberAndSeriesAndMemories(Long postId);
}
