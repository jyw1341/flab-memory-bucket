package com.zephyr.api.dto.request;

import lombok.Data;

@Data
public class FileCreate {

    private final String name;
    private final String extension;

    public FileCreate(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public String createKeyName(String directory) {
        if (directory == null) {
            return this.getName() + "." + this.getExtension();
        }
        return directory + "/" + this.getName() + "." + this.getExtension();
    }
}
