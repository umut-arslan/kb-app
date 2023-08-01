package com.kb.template.model.mapper;

import com.kb.template.model.dto.ConfigDto;
import com.kb.template.model.entity.Config;
import com.kb.template.model.entity.User;

public class ConfigMapper {

    public ConfigDto mapToDto(Config config){
        return new ConfigDto(config.getId(), config.getKey(), config.getValue());
    }

    public Config mapToEntity(ConfigDto configDto, User user){
        return new Config(configDto.getKey(), configDto.getValue(), user);
    }
}
