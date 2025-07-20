package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.exception.UploadFileException;
import com.github.provitaliy.service.AppUserService;
import com.github.provitaliy.service.FileService;
import com.github.provitaliy.service.MainService;
import com.github.provitaliy.service.ProducerService;
import com.github.provitaliy.service.constants.BotResponses;
import com.github.provitaliy.service.enums.LinkType;
import com.github.provitaliy.service.enums.ServiceCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.github.provitaliy.entity.enums.UserState.BASIC_STATE;
import static com.github.provitaliy.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.github.provitaliy.service.enums.ServiceCommands.CANCEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final ProducerService producerService;
    private final UserAppDAO userAppDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    @Override
    public void processTextMessage(Update update) {

        var textMessage = update.getMessage();
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = textMessage.getText();
        String output = "";

        ServiceCommands serviceCommand = ServiceCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            var email = text.trim().toLowerCase();
            output = appUserService.setEmail(appUser, email);
        } else {
            log.error("Unknown user state: {}", userState);
            output = "Неизвестная ошибка. Введите /cancel и попробуйте снова";
        }

        sendAnswer(output, textMessage.getChatId());
    }

    private String processServiceCommand(AppUser appUser, String text) {
        ServiceCommands command = ServiceCommands.fromValue(text);

        return switch (command) {
            case REGISTRATION -> appUserService.registerUser(appUser);
            case HELP -> BotResponses.HELP_RESPONSE;
            case START -> BotResponses.START_RESPONSE;
            case null, default -> BotResponses.UNKNOWN_RESPONSE;
        };
    }

    @Override
    public void processDocMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(appDocument.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен. Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e.getMessage());
            var answer = "Загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(answer, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO);
            var answer = "Photo успешно загружен. Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e.getMessage());
            var answer = "Загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(answer, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();

        if (!appUser.getIsActive()) {
            var err = "Зарегистрируйтесь или активируйте "
                    + "свою учетную запись для загрузки контента.";
            sendAnswer(err, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var err = "Перед отправкой файла отмените текущую команду с помощью " + CANCEL;
            sendAnswer(err, chatId);
            return true;
        }
        return false;
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        userAppDAO.save(appUser);
        return BotResponses.CANCEL_RESPONSE;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(output)
                .build();
        producerService.produceAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> persistentAppUser = userAppDAO.findByTelegramUserId(telegramUser.getId());
        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .userState(BASIC_STATE)
                    .build();
            return userAppDAO.save(transientAppUser);
        }
        return persistentAppUser.get();
    }
}
