package com.zephyr.api.repository;

import com.zephyr.api.domain.Post;
import com.zephyr.api.request.PostSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class PostRepository {

    public Optional<Post> findById(Long id) {
        return Optional.empty();
    }

    public List<Post> getList(PostSearch postSearch) {
        return new ArrayList<>();
    }

    public Post save(Post post) {
        return null;
    }
}
