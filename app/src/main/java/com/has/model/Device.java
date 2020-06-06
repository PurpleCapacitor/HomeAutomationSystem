package com.has.model;

import java.io.Serializable;

public class Device implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Long versionTimestamp;

    public Device(Long id, String name, String description, Long versionTimestamp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.versionTimestamp = versionTimestamp;
    }

    public Device() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getVersionTimestamp() {
        return versionTimestamp;
    }

    public void setVersionTimestamp(Long versionTimestamp) {
        this.versionTimestamp = versionTimestamp;
    }
}
