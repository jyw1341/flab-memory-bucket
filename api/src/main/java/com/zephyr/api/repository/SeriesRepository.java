package com.zephyr.api.repository;

import com.zephyr.api.domain.Album;
import com.zephyr.api.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {


    Optional<Series> findByAlbumAndName(Album album, String name);
}
