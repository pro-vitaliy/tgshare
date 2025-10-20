package com.github.provitaliy.service;

import com.github.provitaliy.repository.AppUserRepository;
import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.entity.enums.UserState;
import com.github.provitaliy.service.constants.BotResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.hashids.Hashids;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.github.provitaliy.entity.enums.UserState.BASIC_STATE;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final ProducerService producerService;
    private final Hashids encoder;

    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return BotResponses.ALREADY_REGISTERED_RESPONSE;
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserRepository.save(appUser);
        return BotResponses.WAIT_FOR_EMAIL_RESPONSE;
    }

    public AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> persistentAppUser = appUserRepository.findByTelegramUserId(telegramUser.getId());
        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .userState(BASIC_STATE)
                    .build();
            return appUserRepository.save(transientAppUser);
        }
        return persistentAppUser.get();
    }

    public String setEmail(AppUser appUser, String email) {
        email = email.trim().toLowerCase();

        if (!isValidEmail(email)) {
            return BotResponses.INCORRECT_EMAIL_ANSWER;
        }

        if (appUserRepository.existsByEmail(email)) {
            return BotResponses.EMAIL_ALREADY_EXISTS;
        }

        appUser.setUnconfirmedEmail(email);
        appUser.setUserState(UserState.BASIC_STATE);
        appUserRepository.save(appUser);
        producerService.produceRegistrationMail(createMailParams(appUser));

        return BotResponses.EMAIL_CONFIRMATION_RESPONSE;
    }

    private boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    private MailParams createMailParams(AppUser appUser) {
        var encodedId = encoder.encode(appUser.getId());
        return new MailParams(encodedId, appUser.getUnconfirmedEmail());
    }
}

