package com.kb.template.model.mapper;

import com.kb.template.model.dto.ConfigDto;
import com.kb.template.model.entity.Config;
import com.kb.template.model.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ConfigMapperTest {

    private final User testUser = new User();
    ConfigMapper configMapper = new ConfigMapper();

    @Test
    void shouldMapToDto() {
        Config entity = new Config();
        ConfigDto dto = configMapper.mapToDto(entity);

        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getKey()).isEqualTo(entity.getKey());
        assertThat(dto.getValue()).isEqualTo(entity.getValue());
    }

    @Test
    void shouldMapToEntity() {
        testUser.setEmail("testMail");
        ConfigDto dto = new ConfigDto();
        Config entity = configMapper.mapToEntity(dto, testUser);

        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getKey()).isEqualTo(dto.getKey());
        assertThat(entity.getValue()).isEqualTo(dto.getValue());
    }
}