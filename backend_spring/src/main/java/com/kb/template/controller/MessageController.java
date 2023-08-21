package com.kb.template.controller;

import com.kb.template.service.MessageService;
import jakarta.annotation.Nonnull;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/push")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/publicKey")
    public @Nonnull String getPublicKey() {
        return messageService.getPublicKey();
    }

    @GetMapping("/subscribe")
    public void subscribe(@RequestParam Subscription subscription) {
        messageService.subscribe(subscription);
    }

    @GetMapping("/unsubscribe")
    public void unsubscribe(String endpoint) {
        messageService.unsubscribe(endpoint);
    }

}
