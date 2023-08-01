package com.kb.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kb.template.model.dto.ConfigDto;
import com.kb.template.model.entity.Config;
import com.kb.template.model.entity.User;
import com.kb.template.service.ConfigService;
import com.kb.template.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "test")
@WebMvcTest(ConfigController.class)
class ConfigControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ConfigService configService;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private DefaultOAuth2User oauthUser;
    private OAuth2AuthenticationToken authToken;
    private List<Config> testConfigs;
    private List<ConfigDto> testConfigDtos;

    @BeforeEach
    void setUp() {
        oauthUser = new DefaultOAuth2User(null, Map.of("name", "testUserName",
                "email", "testEmail"), "name");
        testUser = new User();
        authToken = new OAuth2AuthenticationToken(oauthUser, null, "authClientRegistrationId");
        testConfigs = Arrays.asList(new Config("Hello1", "World1", testUser),
                new Config("Hello2", "World2", testUser),
                new Config("Hello3", "World3", testUser));
        testConfigDtos = Arrays.asList(new ConfigDto(1, "Hello1", "World1"),
                new ConfigDto(2, "Hello2", "World2"),
                new ConfigDto(3, "Hello3", "World3"));
    }

    @Test
    void shouldGetAllUserConfigs() throws Exception {
        when(userService.getUserFromOAuth2User(oauthUser)).thenReturn(testUser);
        when(configService.getAllUserConfigDtos(testUser)).thenReturn(testConfigDtos);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/configs/user")
                .with(authentication(authToken))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldDeleteFirstConfigById() throws Exception {
        doNothing().when(configService).deleteUserConfigById(testConfigs.get(0).getId(), testUser);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete("/api/configs/" + testConfigs.get(0).getId())
                .with(authentication(authToken))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowExceptionOnDeleteUnknownConfig() throws Exception {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        doThrow(IllegalArgumentException.class).when(configService).deleteUserConfigById(4, testUser);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete("/api/configs/" + 4)
                .with(authentication(authToken))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldUpdateConfig() throws Exception {
        ConfigDto updateDto = new ConfigDto(1, "UpdatedHello","UpdatedWorld");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put("/api/configs")
                .with(authentication(authToken))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldThrowExceptionOnUpdateConfig() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put("/api/configs")
                .with(authentication(authToken))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateFirstConfigButThrowExceptionOnSecondSameKeyConfig() throws Exception {
        ConfigDto newConfigDto = testConfigDtos.get(0);
        ConfigDto sameKeyConfigDto = testConfigDtos.get(1);
        sameKeyConfigDto.setKey(newConfigDto.getKey());

        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(configService.saveConfig(any(ConfigDto.class), any(User.class))).thenReturn(newConfigDto);

        MockHttpServletRequestBuilder createFirstConfigRequest = MockMvcRequestBuilders
                .post("/api/configs")
                .with(authentication(authToken))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newConfigDto));

        mockMvc.perform(createFirstConfigRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value(newConfigDto.getKey()))
                .andExpect(jsonPath("$.value").value(newConfigDto.getValue()));

        when(configService.saveConfig(any(ConfigDto.class), any(User.class)))
                .thenThrow(IllegalArgumentException.class);

        MockHttpServletRequestBuilder createInvalidConfig = MockMvcRequestBuilders
                .post("/api/configs")
                .with(authentication(authToken))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sameKeyConfigDto));

        mockMvc.perform(createInvalidConfig)
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldThrowExceptionOnCreateConfig() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/configs")
                .with(authentication(authToken))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserConfigDtoByKey() throws Exception {
        ConfigDto configDto = new ConfigDto(0, "Hello", "World");

        when(configService.getConfigByKeyAndUser("Hello", userService.getCurrentAuthenticatedUser())).thenReturn(configDto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/configs/" + configDto.getKey())
                .with(authentication(authToken))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(configDto.getKey()))
                .andExpect(jsonPath("$.value").value(configDto.getValue()));
    }
    @Test
    void shouldThrowExceptionOnUnknownUserConfigDtoByKey() throws Exception {
        when(configService.getConfigByKeyAndUser("5", userService.getCurrentAuthenticatedUser()))
                .thenThrow(new IllegalArgumentException());
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/configs/" + 5)
                .with(authentication(authToken))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isInternalServerError());
    }
}