package com.github.provitaliy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final UpdateController updateController;

    @PostMapping("/")
    public void onUpdateReceived(@RequestBody Update update) {
        updateController.processUpdate(update);
    }
}
