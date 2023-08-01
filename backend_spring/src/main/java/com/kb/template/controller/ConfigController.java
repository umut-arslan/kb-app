package com.kb.template.controller;

import com.kb.template.model.dto.ConfigDto;
import com.kb.template.service.ConfigService;
import com.kb.template.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configs")
public class ConfigController {

    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUserConfigById(@PathVariable("id") long id){
        try{
            configService.deleteUserConfigById(id, userService.getCurrentAuthenticatedUser());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping()
    public ResponseEntity<HttpStatus> updateConfig(@Valid @RequestBody ConfigDto configDto){
        configService.updateConfig(configDto, userService.getCurrentAuthenticatedUser());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping()
    public ResponseEntity<ConfigDto> createConfig(@Valid @RequestBody ConfigDto configDto){
        try{
            return new ResponseEntity<>(configService.saveConfig(configDto,
                    userService.getCurrentAuthenticatedUser()), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<ConfigDto> getConfigDtoByKey(@PathVariable("key") String key){
        try{
            return new ResponseEntity<>(configService.getConfigByKeyAndUser(key,
                    userService.getCurrentAuthenticatedUser()), HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<ConfigDto>> getConfigsByUser(@AuthenticationPrincipal OAuth2User user) {
        try{
            return new ResponseEntity<>(configService.getAllUserConfigDtos(userService.getUserFromOAuth2User(user)), HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
