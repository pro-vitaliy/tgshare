package com.github.provitaliy.dispatcher.controller;

import com.github.provitaliy.dispatcher.service.UpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@RestController
public class WebhookController {

    private final UpdateService updateService;

    @PostMapping("/")
    public void onUpdatesReceived(@RequestBody Update update) {
        updateService.processUpdate(update);
    }
}
