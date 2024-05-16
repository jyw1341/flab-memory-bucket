package com.zephyr.api.repository;

import com.zephyr.api.domain.Album;
import com.zephyr.api.request.AlbumSearch;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlbumRepository {
    public Album save(Album album) {
        return null;
    }

    public Album findById(Long albumId) {
        return null;
    }

    public List<Album>  getList(Long userId, AlbumSearch albumSearch) {
        return null;
    }
}
