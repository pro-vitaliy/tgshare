package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.entity.enums.UserState;
import com.github.provitaliy.service.AppUserService;
import com.github.provitaliy.service.ProducerService;
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
            return "Вы уже зарегистрированы!";
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        userAppDAO.save(appUser);
        return "Введите ваш email";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        email = email.trim().toLowerCase();

        if (!isValidEmail(email)) {
            return "Введен некорректный email. Попробуйте еще раз или введите /cancel для отмены.";
        }

        if (userAppDAO.existsByEmail(email)) {
            return "Пользователь с таким email уже существует. Введите другой email или /cancel для отмены.";
        }

        appUser.setUnconfirmedEmail(email);
        appUser.setUserState(UserState.BASIC_STATE);
        userAppDAO.save(appUser);
        producerService.produceRegistrationMail(createMailParams(appUser));

        return "Пройдите по ссылке в письме для завершения регистрации.";
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
