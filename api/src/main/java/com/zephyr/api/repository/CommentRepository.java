package com.zephyr.api.repository;

import com.zephyr.api.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepository {

    public List<Comment> findByMemoryId(Long memoryId) {
        return new ArrayList<>();
    }
}
