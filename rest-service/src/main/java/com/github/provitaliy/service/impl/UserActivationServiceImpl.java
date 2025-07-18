package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.exception.ResourceNotFoundException;
import com.github.provitaliy.service.UserActivationService;
import com.github.provitaliy.utils.Decoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {
    private final UserAppDAO userAppDAO;
    private final Decoder decoder;

    @Override
    public void activateUser(String encodedUserId) {
        var userId = decoder.decodeId(encodedUserId);
        AppUser appUser = userAppDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует."));
        appUser.setEmail(appUser.getUnconfirmedEmail());
        appUser.setUnconfirmedEmail(null);
        appUser.setIsActive(true);
        userAppDAO.save(appUser);
    }
}
