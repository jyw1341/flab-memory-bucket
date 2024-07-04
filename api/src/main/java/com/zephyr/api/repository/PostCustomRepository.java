package com.zephyr.api.repository;

import com.zephyr.api.domain.Post;
import com.zephyr.api.dto.PostSearchServiceDto;

import java.util.List;
import java.util.Optional;

public interface PostCustomRepository {

    List<Post> search(PostSearchServiceDto dto);

    Optional<Post> findByIdFetchMemories(Long postId);

    Optional<Post> findByIdFetchMemberAndSeriesAndMemories(Long postId);
}
