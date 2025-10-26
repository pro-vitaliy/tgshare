package com.github.provitaliy.userservice.service;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.dto.AppUserDTO;
import com.github.provitaliy.userservice.entity.AppUser;
import com.github.provitaliy.userservice.exception.EmailAlreadyTakenException;
import com.github.provitaliy.userservice.exception.UserNotFoundException;
import com.github.provitaliy.userservice.mapper.AppUserMapper;
import com.github.provitaliy.userservice.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AppUserDTO getOrCreateAppUser(AppUserCreateDTO appUserData) {
        return appUserRepository.findByTelegramUserId(appUserData.getTelegramUserId())
                .map(appUserMapper::map)
                .orElseGet(() -> {
                    AppUser appUser = appUserMapper.map(appUserData);
                    appUser.setIsActive(false);
                    appUserRepository.save(appUser);
                    return appUserMapper.map(appUser);
                });
    }

    public void updateUnconfirmedEmail(Long telegramUserId, String email) {
        if (appUserRepository.existsByEmail(email)) {
            throw new EmailAlreadyTakenException(email);
        }

        AppUser appUser = appUserRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new UserNotFoundException(telegramUserId));

        appUser.setUnconfirmedEmail(email);
//        TODO: здесь будет отправка ссылки с подтверждением на почту
//        TODO: здесь будет логика публикации обновленного юзера (хотя если почта не потверждена то и не надо мб)
    }
}
