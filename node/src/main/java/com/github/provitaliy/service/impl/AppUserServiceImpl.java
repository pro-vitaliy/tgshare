package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.entity.enums.UserState;
import com.github.provitaliy.service.AppUserService;
import com.github.provitaliy.service.ProducerService;
import com.github.provitaliy.service.constants.BotResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.hashids.Hashids;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final UserAppDAO userAppDAO;
    private final ProducerService producerService;
    private final Hashids encoder;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return BotResponses.ALREADY_REGISTERED_RESPONSE;
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        userAppDAO.save(appUser);
        return BotResponses.WAIT_FOR_EMAIL_RESPONSE;
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        email = email.trim().toLowerCase();

        if (!isValidEmail(email)) {
            return BotResponses.INCORRECT_EMAIL_ANSWER;
        }

        if (userAppDAO.existsByEmail(email)) {
            return BotResponses.EMAIL_ALREADY_EXISTS;
        }

        appUser.setUnconfirmedEmail(email);
        appUser.setUserState(UserState.BASIC_STATE);
        userAppDAO.save(appUser);
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
