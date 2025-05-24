package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.RawDataDAO;
import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.entity.RawData;
import com.github.provitaliy.entity.enums.UserState;
import com.github.provitaliy.service.MainService;
import com.github.provitaliy.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final UserAppDAO userAppDAO;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveAppUser(telegramUser);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(textMessage.getChatId())
                .text("Hello from NODE")
                .build();

        producerService.produceAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(User telegramUser) {
        Optional<AppUser> persistentAppUser = userAppDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO: изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .userState(UserState.BASIC_STATE)
                    .build();
            return userAppDAO.save(transientAppUser);
        }
        return persistentAppUser.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
