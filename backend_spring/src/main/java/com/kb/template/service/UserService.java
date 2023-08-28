package com.kb.template.service;

import com.kb.template.model.entity.User;
import com.kb.template.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final String NAME_ATTRIBUTE = "name";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String PROFILE_PICTURE_ATTRIBUTE = "picture";

    @Autowired
    private UserRepository userRepository;

    public long saveUser(User user) {
        return userRepository.save(user).getId();
    }

    public long onLogin(OAuth2User auth2User){
        Optional<User> user = userRepository.findByEmail(auth2User.getAttribute(EMAIL_ATTRIBUTE));
        if(user.isEmpty()){
            // persist new user
            User newUser = new User();
            newUser.setName(auth2User.getAttribute(NAME_ATTRIBUTE));
            newUser.setEmail(auth2User.getAttribute(EMAIL_ATTRIBUTE));
            newUser.setAvatarUrl(auth2User.getAttribute(PROFILE_PICTURE_ATTRIBUTE));
            return saveUser(newUser);
        }
        return -1;
    }

    public User getUserFromOAuth2User(OAuth2User auth2User) {
        Optional<User> optionalUser = userRepository.findByEmail(auth2User.getAttribute(EMAIL_ATTRIBUTE));
        return optionalUser.orElse(null);
    }

    public User getCurrentAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOAuth2User currentOAuthUser = (DefaultOAuth2User) authentication.getPrincipal();
            return getUserFromOAuth2User(currentOAuthUser);
        }
        else return null;
    }
}