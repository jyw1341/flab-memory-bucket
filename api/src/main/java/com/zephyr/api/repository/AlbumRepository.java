package com.zephyr.api.repository;

import com.zephyr.api.domain.Album;
import com.zephyr.api.response.AlbumListResponse;
import com.zephyr.api.response.AlbumResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlbumRepository {
    public void save(Album album) {

    }

    public void delete(Long albumId) {

    }

    public Album findById(Long albumId) {
        return null;
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
