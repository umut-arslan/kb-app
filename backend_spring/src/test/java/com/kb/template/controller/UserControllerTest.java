package com.kb.template.controller;

import com.kb.template.model.entity.User;
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

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "test")
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserService userService;

    private User testUser;
    private DefaultOAuth2User oAuth2User;
    private OAuth2AuthenticationToken authToken;
    private final String testMail = "testEmail";
    private final String testName = "testUserName";


    @BeforeEach
    void setUp() {
        oAuth2User = new DefaultOAuth2User(null, Map.of("name", testName,
                "email", "testEmail"), "email");
        testUser = new User();
        testUser.setName(testName);
        testUser.setEmail(testMail);
        authToken = new OAuth2AuthenticationToken(oAuth2User, null, "authClientRegistrationId");
    }

    @Test
    void shouldGetUser() throws Exception {
        when(userService.getUserFromOAuth2User(oAuth2User)).thenReturn(testUser);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/user/me")
                .with(authentication(authToken))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testMail))
                .andExpect(jsonPath("$.name").value(testName));
    }

    @Test
    void shouldGetUserDetails() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/api/user/attributes")
                .with(authentication(authToken))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }
}