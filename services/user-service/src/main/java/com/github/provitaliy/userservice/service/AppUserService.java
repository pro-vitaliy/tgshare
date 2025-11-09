package com.github.provitaliy.userservice.service;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.dto.AppUserDTO;
import com.github.provitaliy.common.event.UserActivatedEvent;
import com.github.provitaliy.userservice.entity.AppUser;
import com.github.provitaliy.userservice.exception.EmailAlreadyTakenException;
import com.github.provitaliy.userservice.exception.UserNotFoundException;
import com.github.provitaliy.userservice.mapper.AppUserMapper;
import com.github.provitaliy.userservice.repository.AppUserRepository;
import com.github.provitaliy.userservice.service.email.EmailConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final EmailConfirmationService confirmationService;
    private final ProducerService producerService;

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

    public AppUserDTO getAppUserByTelegramId(Long telegramUserId) {
        return appUserRepository.findByTelegramUserId(telegramUserId)
                .map(appUserMapper::map)
                .orElseThrow(() -> new UserNotFoundException(telegramUserId));
    }

    public void updateUnconfirmedEmail(Long telegramUserId, String email) {
        if (appUserRepository.existsByEmail(email)) {
//            TODO: не забыть сделать обработчики этих исколючений в ноде
            throw new EmailAlreadyTakenException(email);
        }

        AppUser appUser = appUserRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new UserNotFoundException(telegramUserId));

        appUser.setUnconfirmedEmail(email);
        appUserRepository.save(appUser);
        confirmationService.sendConfirmationEmail(appUser.getId(), email);
    }

    public void activateUser(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setEmail(user.getUnconfirmedEmail());
        user.setUnconfirmedEmail(null);
        user.setIsActive(true);
        var event = new UserActivatedEvent(user.getTelegramUserId(), user.getEmail());
        producerService.produceUserActivatedEvent(event);
    }
}
