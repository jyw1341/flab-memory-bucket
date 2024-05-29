package com.zephyr.api.repository;

import com.zephyr.api.domain.Subscribe;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscribeRepository {

    public void save(Subscribe subscribe) {

    }

    public Optional<Subscribe> findByAlbumIdAndMemberId(Long albumId, Long memberId) {
        return Optional.empty();
    }

    public List<Subscribe> findByMemberId(Long memberId) {
        return null;
    }

    public List<Subscribe> findByAlbumId(Long albumId) {
        return null;
    }

    public void delete(Long albumId, Long memberId) {

    }
}
