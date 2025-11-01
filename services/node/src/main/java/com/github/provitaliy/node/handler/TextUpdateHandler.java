package com.github.provitaliy.node.handler;

import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.bot.ServiceCommand;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.ProducerService;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static com.github.provitaliy.node.bot.ServiceCommand.CANCEL;
import static com.github.provitaliy.node.user.UserState.BASIC_STATE;
import static com.github.provitaliy.node.user.UserState.WAIT_FOR_EMAIL_STATE;

@Slf4j
@RequiredArgsConstructor
@Service
public class TextUpdateHandler {
    private final NodeUserService userService;
    private final ProducerService producerService;

    public void handleUpdate(Update update) {
        Message message = update.getMessage();

        if (message == null || message.getText() == null) {
            log.warn("Received update without text: {}", update);
            return;
        }

        NodeUser user = userService.getOrCreateAppUser(HandlerUtils.buildUserCreateDto(message));
        UserState userState = user.getState();

        String messageText = message.getText();
        ServiceCommand command = ServiceCommand.fromValue(messageText);
        String answer;

        if (CANCEL.equals(command)) {
            answer = cancelProcess(user);
        } else if (BASIC_STATE.equals(userState)) {
            answer = processServiceCommand(user, command);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            answer = setEmail(user, messageText);
        } else {
            log.error("Unknown user state: {}", userState);
            answer = "Неизвестная ошибка. Введите /cancel и попробуйте снова";
        }

        SendMessage answerMessage = HandlerUtils.prepareMessage(answer, user.getChatId());
        producerService.produceAnswer(answerMessage);
    }

    private String processServiceCommand(NodeUser nodeUser, ServiceCommand command) {
        return switch (command) {
            case REGISTRATION -> registerProcess(nodeUser);
            case HELP -> BotResponse.HELP_RESPONSE;
            case START -> BotResponse.START_RESPONSE;
            case null, default -> BotResponse.UNKNOWN_RESPONSE;
        };
    }

    private String cancelProcess(NodeUser nodeUser) {
        userService.changeState(nodeUser, BASIC_STATE);
        return BotResponse.CANCEL_RESPONSE;
    }

    private String registerProcess(NodeUser nodeUser) {
        if (nodeUser.getIsActive()) {
            return BotResponse.ALREADY_REGISTERED_RESPONSE;
        }
        userService.changeState(nodeUser, WAIT_FOR_EMAIL_STATE);
        return BotResponse.WAIT_FOR_EMAIL_RESPONSE;
    }

    private String setEmail(NodeUser nodeUser, String email) {
        if (!HandlerUtils.isValidEmail(email)) {
            return BotResponse.INCORRECT_EMAIL_ANSWER;
        }
        userService.setEmail(nodeUser, HandlerUtils.normalizeEmail(email));
        return BotResponse.EMAIL_CONFIRMATION_RESPONSE;
    }
}
