package com.zephyr.api.service;

import com.zephyr.api.repository.SeriesRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private AlbumService albumService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private SeriesService seriesService;


}
