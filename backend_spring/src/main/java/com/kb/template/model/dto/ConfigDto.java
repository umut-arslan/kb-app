package com.kb.template.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfigDto {

    private long id;

    @NotBlank(message = "Key is mandatory")
    private String key;

    @NotBlank(message = "Value is mandatory")
    private String value;

    public ConfigDto() {
    }

    public ConfigDto(long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public ConfigDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
