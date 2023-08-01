package com.kb.template.controller;

import com.kb.template.model.entity.User;
import com.kb.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getUser(@AuthenticationPrincipal OAuth2User user) {
        try{
            return new ResponseEntity<>(userService.getUserFromOAuth2User(user), HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // can be used to check given attributes from auth service api
    @GetMapping("/attributes")
    public Map<String, Object> getUserDetails(@AuthenticationPrincipal OAuth2User user) {
        return user.getAttributes();
    }
}
