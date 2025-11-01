package com.github.provitaliy.userservice.service.email;

import com.github.provitaliy.common.event.SendEmailEvent;
import com.github.provitaliy.userservice.service.AppUserService;
import com.github.provitaliy.userservice.service.ProducerService;
import com.github.provitaliy.userservice.util.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailConfirmationService {
    private final ProducerService producerService;
    private final AppUserService userService;
    private final Encoder encoder;

    @Value("${service.user-activation.url}")
    private String activationUrl;

    @Value("${service.user-activation.endpoint}")
    private String endpoint;

    public void sendConfirmationEmail(Long userId, String email) {
        String confirmationLink = buildConfirmationLink(userId);
        String confirmationMessage = String.format("""
            Добро пожаловать!

            Для активации вашей учетной записи перейдите по ссылке:
            %s

            Если вы не регистрировались — просто игнорируйте это письмо.
        """, confirmationLink);

        SendEmailEvent emailEvent = new SendEmailEvent(email, "Подтверждение почты", confirmationMessage);
        producerService.produceSendMailEvent(emailEvent);
    }

    public void confirmUserAccount(String encodedId) {
        Long userId = encoder.decode(encodedId);
        userService.activateUser(userId);
    }

    private String buildConfirmationLink(Long userId) {
        var encodedId = encoder.encode(userId);
        return String.format("%s%s?id=%s", activationUrl, endpoint, encodedId);
    }
}
