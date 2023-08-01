package com.kb.template.service;

import com.kb.template.model.entity.User;
import com.kb.template.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        User userToSave = new User();
        userToSave.setEmail("testMail");
        userToSave.setName("testName");

        when(userRepository.save(userToSave)).thenReturn(userToSave);

        assertThat(userService.saveUser(userToSave)).isEqualTo(userToSave.getId());
    }

    @Test
    void shouldSaveNewUserOnLogin() {
        OAuth2User oAuth2User = new DefaultOAuth2User(null, Map.of("name", "testName",
                "email", "testEmail"), "email");

        User userToLogin = new User();
        userToLogin.setEmail("testEmail");
        userToLogin.setName("testName");

        when(userRepository.save(any(User.class))).thenReturn(userToLogin);
        when(userRepository.findByEmail("testEmail")).thenReturn(Optional.empty());

        assertThat(userService.onLogin(oAuth2User)).isEqualTo(userToLogin.getId());
    }

    @Test
    void shouldGetUserFromOAuth2User() {
        OAuth2User oAuth2User = new DefaultOAuth2User(null, Map.of("name", "testName",
                "email", "testEmail"), "email");

        User getUser = new User();
        getUser.setEmail("testEmail");
        getUser.setName("testName");

        when(userRepository.findByEmail("testEmail")).thenReturn(Optional.of(getUser));

        assertThat(userService.getUserFromOAuth2User(oAuth2User)).isEqualTo(getUser);
    }
}