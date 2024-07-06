package com.zephyr.api.utils;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
public class H2TableCleaner {

    private final EntityManager entityManager;
    private List<String> tableNames;

    public H2TableCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static String convertToUnderscore(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));

        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    @Transactional
    public void cleanTables(String... tableNames) {
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
