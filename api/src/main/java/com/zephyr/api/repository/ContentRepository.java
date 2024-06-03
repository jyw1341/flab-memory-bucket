package com.zephyr.api.repository;

import com.zephyr.api.domain.Content;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ContentRepository {

    public List<Content> saveAll(List<Content> content) {
        return new ArrayList<>();
    }
}
