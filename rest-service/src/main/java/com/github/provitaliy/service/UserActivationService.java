package com.github.provitaliy.service;

import com.github.provitaliy.repository.AppUserRepository;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.exception.ResourceNotFoundException;
import com.github.provitaliy.utils.Decoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivationService {
    private final AppUserRepository appUserRepository;
    private final Decoder decoder;

    public void activateUser(String encodedUserId) {
        var userId = decoder.decodeId(encodedUserId);
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует."));
        appUser.setEmail(appUser.getUnconfirmedEmail());
        appUser.setUnconfirmedEmail(null);
        appUser.setIsActive(true);
        appUserRepository.save(appUser);
    }
}
