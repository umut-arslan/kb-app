package com.kb.template.controller;

import com.kb.template.service.MessageService;
import jakarta.annotation.Nonnull;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/publicKey")
    public @Nonnull String getPublicKey() {
        return messageService.getPublicKey();
    }

    @GetMapping("/subscribe")
    public void subscribe(Subscription subscription) {
        messageService.subscribe(subscription);
    }

    @GetMapping("/unsubscribe")
    public void unsubscribe(String endpoint) {
        messageService.unsubscribe(endpoint);
    }

}
