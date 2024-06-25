package com.zephyr.api.config;

import com.zephyr.api.utils.H2TableCleaner;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    private final EntityManager entityManager;

    public TestConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    public H2TableCleaner jpaTableCleaner() {
        return new H2TableCleaner(entityManager);
    }
}
