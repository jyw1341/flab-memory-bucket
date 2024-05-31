package com.zephyr.api.request;

import lombok.Data;

@Data
public class SubscribeRequest {

    private String username;

    public SubscribeRequest(String username) {
        this.username = username;
    }
}
