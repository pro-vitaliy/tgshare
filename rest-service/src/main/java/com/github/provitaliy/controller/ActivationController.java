package com.github.provitaliy.controller;

import com.github.provitaliy.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActivationController {
    private final UserActivationService activationService;

    @GetMapping("/user/activateUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> userActivation(@RequestParam("id") String encodedId) {
        activationService.activateUser(encodedId);
        return ResponseEntity.ok().body("Регистрация успешно завершена!");
    }
}
