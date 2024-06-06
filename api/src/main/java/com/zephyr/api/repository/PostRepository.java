package com.zephyr.api.repository;

import com.zephyr.api.domain.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class PostRepository {

    public Optional<Post> findById(Long id) {
        return Optional.empty();
    }

    public Post save(Post post) {
        return null;
    }
}
