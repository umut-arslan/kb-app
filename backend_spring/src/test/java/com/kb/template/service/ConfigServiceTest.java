package com.kb.template.service;

import com.kb.template.model.dto.ConfigDto;
import com.kb.template.model.entity.Config;
import com.kb.template.model.entity.User;
import com.kb.template.model.mapper.ConfigMapper;
import com.kb.template.repository.ConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @InjectMocks
    ConfigService configService;
    @Mock
    ConfigRepository configRepository;
    @Mock
    UserService userService;
    @Mock
    ConfigMapper configMapper;

    User testUser;
    @BeforeEach
    void setUp() {
       testUser = new User();
       testUser.setName("testUserName");
       testUser.setEmail("testEmail");
    }

    @Test
    void shouldGetAllUserConfigDtos() {
        List<Config> configs = new ArrayList<>();
        Config config1 = new Config("1", "1", testUser);
        Config config2 = new Config("2", "2", testUser);
        Config config3 = new Config("3", "3", testUser);
        configs.add(config1);
        configs.add(config2);
        configs.add(config3);

        ConfigDto configDto1 = new ConfigDto();
        ConfigDto configDto2 = new ConfigDto();
        ConfigDto configDto3 = new ConfigDto();

        when(configRepository.findAllByUser(testUser)).thenReturn(configs);
        when(configMapper.mapToDto(config1)).thenReturn(configDto1);
        when(configMapper.mapToDto(config2)).thenReturn(configDto2);
        when(configMapper.mapToDto(config3)).thenReturn(configDto3);

        assertThat(configService.getAllUserConfigDtos(testUser)).hasSize(3)
                .contains(configDto1, configDto2, configDto3);
    }

    @Test
    void shouldSaveConfig() {
        ConfigDto configDto = new ConfigDto();
        Config config = new Config();

        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(configRepository.save(config)).thenReturn(config);
        when(configMapper.mapToEntity(configDto, testUser)).thenReturn(config);
        when(configMapper.mapToDto(config)).thenReturn(configDto);

        assertThat(configService.saveConfig(configDto, userService.getCurrentAuthenticatedUser())).isEqualTo(configDto);
    }

    @Test
    void shouldNotSaveConfigWhenSameKey() {
        ConfigDto configDto = new ConfigDto();
        ConfigDto invalidDto = new ConfigDto();

        Config config = new Config("Key1","Val", testUser);
        Config invalidEntity = new Config("Key2","Val", testUser);

        when(configMapper.mapToEntity(configDto, testUser)).thenReturn(config);
        when(configMapper.mapToEntity(invalidDto, testUser)).thenReturn(invalidEntity);
        when(configRepository.save(config)).thenReturn(config);
        when(configRepository.save(invalidEntity)).thenThrow(IllegalArgumentException.class);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);

        configService.saveConfig(configDto, userService.getCurrentAuthenticatedUser());
        User user = userService.getCurrentAuthenticatedUser();
        assertThrows(IllegalArgumentException.class,
                () -> configService.saveConfig(invalidDto, user));
    }

    @Test
    void shouldUpdateConfig() {
        ConfigDto updatedDto = new ConfigDto();
        Config config = new Config();

        when(configRepository.findById(updatedDto.getId())).thenReturn(Optional.of(config));
        when(configRepository.save(config)).thenReturn(config);
        when(configMapper.mapToDto(config)).thenReturn(updatedDto);

        assertThat(configService.updateConfig(updatedDto, userService.getCurrentAuthenticatedUser())).isEqualTo(updatedDto);
    }

    @Test
    void shouldSaveNewConfigOnUpdate() {
        ConfigDto updatedDto = new ConfigDto();
        Config config = new Config();

        when(configRepository.findById(updatedDto.getId())).thenReturn(Optional.empty());
        when(configRepository.save(config)).thenReturn(config);
        when(configMapper.mapToDto(config)).thenReturn(updatedDto);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(configMapper.mapToEntity(any(ConfigDto.class), any(User.class))).thenReturn(config);

        assertThat(configService.updateConfig(updatedDto, userService.getCurrentAuthenticatedUser())).isEqualTo(updatedDto);
    }

    @Test
    void shouldDeleteConfig() {
        Config deleteEntity = new Config();
        when(configRepository.findByIdAndUser(deleteEntity.getId(), testUser)).thenReturn(Optional.of(deleteEntity));
        configService.deleteUserConfigById(deleteEntity.getId(), testUser);
        verify(configRepository).deleteById(deleteEntity.getId());
    }

    @Test
    void shouldGetConfigByKeyAndUser() {
        Config exampleConfig = new Config();
        ConfigDto exampleDto = new ConfigDto();
        exampleConfig.setKey("Hello");
        exampleConfig.setValue("World");

        when(configRepository.findByKeyAndUser("Hello", userService.getCurrentAuthenticatedUser())).thenReturn(exampleConfig);
        when(configMapper.mapToDto(exampleConfig)).thenReturn(exampleDto);

        assertThat(configService.getConfigByKeyAndUser("Hello", userService.getCurrentAuthenticatedUser())).isEqualTo(exampleDto);
    }
}