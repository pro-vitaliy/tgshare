package com.github.provitaliy.userservice.controller;

import com.github.provitaliy.userservice.service.email.EmailConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserActivationController {
    private final EmailConfirmationService confirmationService;

    @GetMapping("${service.user-activation.endpoint}")
    public String activate(@RequestParam("id") String encodedId) {
        confirmationService.confirmUserAccount(encodedId);
        return "Запрос на активацию отправлен! Ответ придет в телеграм бота";
    }
}
