package com.zephyr.api.service;

import com.zephyr.api.domain.Album;
import com.zephyr.api.repository.AlbumRepository;
import com.zephyr.api.request.AlbumCreate;
import com.zephyr.api.request.AlbumSearch;
import com.zephyr.api.request.AlbumUpdate;
import com.zephyr.api.response.AlbumResponse;
import com.zephyr.api.s3.S3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final S3Manager s3Manager;

    public AlbumResponse create(AlbumCreate albumCreate, Long userId) {
        String url = null;
        if (!albumCreate.getAlbumCover().isEmpty()) {
            url = s3Manager.saveContent(albumCreate.getAlbumCover());
        }
        Album album = albumRepository.save(Album.builder().build());

        return AlbumResponse.builder().build();
    }

    public AlbumResponse get(Long albumId) {
        Album album = albumRepository.findById(albumId);

        return AlbumResponse.builder().build();
    }

    public List<AlbumResponse> getList(Long userId, AlbumSearch albumSearch) {
        List<Album> list = albumRepository.getList(userId, albumSearch);
        return null;
    }

    public AlbumResponse update(Long albumId, AlbumUpdate albumUpdate) {
        return null;
    }


}
