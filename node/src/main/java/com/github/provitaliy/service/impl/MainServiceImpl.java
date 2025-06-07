package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.RawDataDAO;
import com.github.provitaliy.dao.UserAppDAO;
import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;
import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.entity.RawData;
import com.github.provitaliy.exception.UploadFileException;
import com.github.provitaliy.service.FileService;
import com.github.provitaliy.service.MainService;
import com.github.provitaliy.service.ProducerService;
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
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final UserAppDAO userAppDAO;
    private final FileService fileService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

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
            //TODO: add email process
        } else {
            log.error("Unknown user state: {}", userState);
            output = "Неизвестная ошибка. Введите /cancel и попробуйте снова";
        }

        sendAnswer(output, textMessage.getChatId());
    }

    private String processServiceCommand(AppUser appUser, String text) {
        ServiceCommands command = ServiceCommands.fromValue(text);

        return switch (command) {
            case REGISTRATION -> "Регистрация в разработке."; //TODO: добавить регистрацию
            case HELP -> help();
            case START -> "Приветствую! Чтобы посмотреть список доступных команд введите /help";
            case null, default -> "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        };
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            //TODO add link generation
            var answer = "Документ успешно загружен. Ссылка для скачивания http://test.io/get-doc/666";
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e.getMessage());
            var answer = "Загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(answer, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            //TODO: add link generation
            var answer = "Photo успешно загружен. Ссылка для скачивания http://test.io/get-photo/666";
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
            var err = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(err, chatId);
            return true;
        }
        return false;
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        userAppDAO.save(appUser);
        return "Команда отменена!";
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
        Optional<AppUser> persistentAppUser = userAppDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO: изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .userState(BASIC_STATE)
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
