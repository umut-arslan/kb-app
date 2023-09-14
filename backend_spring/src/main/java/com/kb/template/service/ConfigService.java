package com.kb.template.service;

import com.kb.template.model.dto.ConfigDto;
import com.kb.template.model.entity.Config;
import com.kb.template.model.entity.User;
import com.kb.template.model.mapper.ConfigMapper;
import com.kb.template.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {

    @Autowired
    private ConfigRepository configRepository;

    private ConfigMapper modelMapper = new ConfigMapper();

    public ConfigDto getConfigByKeyAndUser(String key, User user){
        return modelMapper.mapToDto(configRepository.findByKeyAndUser(key, user));
    }

    public ConfigDto saveConfig(ConfigDto configDto, User user){
        Config config = modelMapper.mapToEntity(configDto, user);
        return modelMapper.mapToDto(configRepository.save(configQ));
    }

    public ConfigDto updateConfig(ConfigDto configDto, User user){
        Optional<Config> configOptional = configRepository.findById(configDto.getId());
        if (configOptional.isEmpty()) return saveConfig(configDto, user);
        Config config = configOptional.get();
        config.setKey(configDto.getKey());
        config.setValue(configDto.getValue());
        return modelMapper.mapToDto(configRepository.save(config));
    }
    public void deleteUserConfigById(long id, User user){
        Optional<Config> configOptional = configRepository.findByIdAndUser(id, user);
        if(configOptional.isPresent())
            configRepository.deleteById(configOptional.get().getId());
        else throw new IllegalArgumentException();
    }

    public List<ConfigDto> getAllUserConfigDtos(User user) {
        List<Config> userConfigs = configRepository.findAllByUser(user);
        return userConfigs.stream().map(config -> modelMapper.mapToDto(config)).toList();
    }
}