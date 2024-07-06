package com.zephyr.api.controller;

import com.zephyr.api.utils.TestRestTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeriesControllerTest {

    @Autowired
    private TestRestTemplateUtils testRestTemplateUtils;

}
