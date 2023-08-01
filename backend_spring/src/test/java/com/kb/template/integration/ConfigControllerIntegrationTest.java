package com.kb.template.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kb.template.DatabaseTestBase;
import com.kb.template.model.dto.ConfigDto;
import com.kb.template.model.entity.Config;
import com.kb.template.model.entity.User;
import com.kb.template.repository.ConfigRepository;
import com.kb.template.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ConfigControllerIntegrationTest extends DatabaseTestBase {
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private OAuth2User testOAuthUserOne;
    private OAuth2User testOAuthUserTwo;
    private List<Config> testConfigs;
    private List<ConfigDto> testConfigDtos;
    @BeforeEach
    void setUp() {
        configRepository.deleteAll();
        testOAuthUserOne = new DefaultOAuth2User(null, Map.of("name", "testUserNameOne",
                "email", "testEmailOne"), "name");
        testOAuthUserTwo = new DefaultOAuth2User(null, Map.of("name", "testUserNameTwo",
                "email", "testEmailTwo"), "name");
        userService.onLogin(testOAuthUserOne);
        User testUser = userService.getUserFromOAuth2User(testOAuthUserOne);
        testConfigs = Arrays.asList(new Config("Hello1User1", "World1User1", testUser),
                new Config("Hello2User1", "World2User1", testUser),
                new Config("Hello3User1", "World3User1", testUser));
        testConfigDtos = Arrays.asList(new ConfigDto("Hello1User1", "World1User1"),
                new ConfigDto("Hello2User1", "World2User1"),
                new ConfigDto("Hello3User1", "World3User1"));
    }

    @Test
    void shouldGetAllUserOneConfigs() throws Exception {
        createNewConfigForOauthUserRequest(testConfigDtos.get(0), testOAuthUserOne);
        createNewConfigForOauthUserRequest(testConfigDtos.get(1), testOAuthUserOne);
        createNewConfigForOauthUserRequest(testConfigDtos.get(2), testOAuthUserOne);

        createNewConfigForOauthUserRequest(testConfigDtos.get(0), testOAuthUserTwo);
        createNewConfigForOauthUserRequest(testConfigDtos.get(1), testOAuthUserTwo);
        createNewConfigForOauthUserRequest(testConfigDtos.get(2), testOAuthUserTwo);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/configs/user")
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldDenyGetUserOneConfigsForUserTwoByKey() throws Exception {
        createNewConfigForOauthUserRequest(testConfigDtos.get(0), testOAuthUserOne);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/configs/" + testConfigDtos.get(0).getKey())
                .with(oauth2Login().oauth2User(testOAuthUserTwo))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldDeleteConfigById() throws Exception {
        createNewConfigForOauthUserRequest(testConfigDtos.get(0), testOAuthUserOne);

        MockHttpServletRequestBuilder mockRequestGetDto = MockMvcRequestBuilders
                .get("/api/configs/" + testConfigDtos.get(0).getKey())
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(mockRequestGetDto)
                .andExpect(status().isOk())
                .andReturn();

        Integer configDtoId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete("/api/configs/" + configDtoId)
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());

        assertFalse(configRepository.findAll().contains(testConfigs.get(0)));
    }

    @Test
    void shouldDenyDeleteUserOneConfigByIdByUserTwo() throws Exception {
        createNewConfigForOauthUserRequest(testConfigDtos.get(0), testOAuthUserOne);

        MockHttpServletRequestBuilder mockRequestGetDto = MockMvcRequestBuilders
                .get("/api/configs/" + testConfigDtos.get(0).getKey())
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(mockRequestGetDto)
                .andExpect(status().isOk())
                .andReturn();

        Integer configId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete("/api/configs/" + configId)
                .with(oauth2Login().oauth2User(testOAuthUserTwo))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isInternalServerError());

        assert(configRepository.findByIdAndUser(configId, userService.getUserFromOAuth2User(testOAuthUserOne)).isPresent());
    }

    @Test
    void shouldThrowExceptionOnDeleteUnknownConfig() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete("/api/configs/" + 4)
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldUpdateConfig() throws Exception {
        long entityId = testConfigs.get(1).getId();
        ConfigDto updateDto = new ConfigDto(entityId, "UpdatedHello","UpdatedWorld");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put("/api/configs")
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isAccepted());

        Optional<Config> optionalEntity = configRepository.findById(entityId);
        optionalEntity.ifPresent(configEntity -> assertThat(configEntity.getKey())
                .isEqualTo("UpdatedHello"));
        optionalEntity.ifPresent(configEntity -> assertThat(configEntity.getValue())
                .isEqualTo("UpdatedWorld"));
    }

    @Test
    void shouldThrowExceptionOnUpdateConfig() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put("/api/configs")
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateConfig() throws Exception {
        ConfigDto newConfigDto = new ConfigDto();
        newConfigDto.setKey("NewHello");
        newConfigDto.setValue("NewWorld");

        createNewConfigForOauthUserRequest(newConfigDto, testOAuthUserOne)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value(newConfigDto.getKey()))
                .andExpect(jsonPath("$.value").value(newConfigDto.getValue()));
    }

    @Test
    void shouldReturn500ErrorOnSameKeyConfigForSameUser() throws Exception {
        ConfigDto configDto = new ConfigDto(5, testConfigs.get(0).getKey(), "Example Val");

        createNewConfigForOauthUserRequest(configDto, testOAuthUserOne).andExpect(status().isCreated());
        createNewConfigForOauthUserRequest(configDto, testOAuthUserOne).andExpect(status().isInternalServerError());
    }

    @Test
    void shouldThrowExceptionOnCreateConfig() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/configs")
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetConfigDtoByKey() throws Exception {
        createNewConfigForOauthUserRequest(testConfigDtos.get(0), testOAuthUserOne);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/configs/" + testConfigs.get(0).getKey())
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(testConfigs.get(0).getKey()))
                .andExpect(jsonPath("$.value").value(testConfigs.get(0).getValue()));
    }
    @Test
    void shouldThrowExceptionOnUnknownGetConfigDtoByKey() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/configs/" + 5)
                .with(oauth2Login().oauth2User(testOAuthUserOne))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isInternalServerError());
    }
    private ResultActions createNewConfigForOauthUserRequest(ConfigDto config, OAuth2User oAuth2User) throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/configs")
                .with(oauth2Login().oauth2User(oAuth2User))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(config));

        return mockMvc.perform(mockRequest);
    }
}