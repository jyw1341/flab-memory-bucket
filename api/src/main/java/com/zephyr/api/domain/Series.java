package com.zephyr.api.domain;

public class Series {

    private Long id;
    private String name;

    public Series(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
