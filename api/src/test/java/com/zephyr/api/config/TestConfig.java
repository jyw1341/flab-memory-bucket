package com.zephyr.api.config;

import com.zephyr.api.utils.H2TableCleaner;
import com.zephyr.api.utils.TestRestTemplateUtils;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@TestConfiguration
public class TestConfig {

    private final EntityManager entityManager;
    private final WebServerApplicationContext webServerApplicationContext;

    public TestConfig(EntityManager entityManager, WebServerApplicationContext webServerApplicationContext) {
        this.entityManager = entityManager;
        this.webServerApplicationContext = webServerApplicationContext;
    }

    @Bean
    public H2TableCleaner jpaTableCleaner() {
        return new H2TableCleaner(entityManager);
    }

    @Bean
    public TestRestTemplate testRestTemplate() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return testRestTemplate;
    }

    @Bean
    public TestRestTemplateUtils testRestTemplateUtils() {
        return new TestRestTemplateUtils(testRestTemplate(), webServerApplicationContext.getWebServer().getPort());
    }
}
