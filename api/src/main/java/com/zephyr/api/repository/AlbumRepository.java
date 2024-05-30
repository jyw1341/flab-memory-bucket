package com.zephyr.api.repository;

import com.zephyr.api.domain.Album;
import com.zephyr.api.response.AlbumListResponse;
import com.zephyr.api.response.AlbumResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AlbumRepository {
    public Optional<Album> save(Album album) {
        return null;
    }

    public void delete(Album album) {

    }

    public Optional<Album> findByOwnerIdAndAlbumId(Long memberId, Long albumId) {
        return Optional.empty();
    }

    public Optional<Album> findById(Long albumId) {
        return Optional.empty();
    }

    public AlbumResponse findWithSubscribeAndMemory(Long albumId) {
        return null;
    }

    public List<Album> findAll() {
        return null;
    }

    public List<AlbumListResponse> findAllWithSubscribeAndMemory(Long memberId) {
        return null;
    }
}
