package com.zephyr.api.utils;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zephyr.api.utils.TestStringUtils.convertToUnderscore;

@Slf4j
public class H2TableCleaner {

    private final EntityManager entityManager;
    private List<String> tableNames;

    public H2TableCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void cleanAll() {
        entityManager.flush();
        // Disable foreign key checks
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // Truncate each table
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        // Enable foreign key checks
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    @Transactional
    public void clean(String... tableNames) {
        entityManager.flush();
        // Disable foreign key checks
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // Truncate each table
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        // Enable foreign key checks
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    @PostConstruct
    public void init() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
                .map(entityType -> convertToUnderscore(entityType.getName()))
                .toList();
    }
}
