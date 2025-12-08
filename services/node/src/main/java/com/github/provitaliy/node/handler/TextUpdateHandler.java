package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.bot.ServiceCommand;
import com.github.provitaliy.node.exception.NodeInternalProcessingException;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.UserResponseService;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.github.provitaliy.node.bot.ServiceCommand.CANCEL;
import static com.github.provitaliy.node.user.UserState.BASIC_STATE;
import static com.github.provitaliy.node.user.UserState.WAIT_FOR_EMAIL_STATE;

@Slf4j
@RequiredArgsConstructor
@Service
public class TextUpdateHandler {
    private final NodeUserService userService;
    private final UserResponseService userResponseService;

    public void handleUpdate(TelegramTextMessageDto textMessage) {
        if (textMessage == null || textMessage.text() == null) {
            log.warn("Received textMessage without text: {}", textMessage);
            return;
        }

        NodeUser user = userService.getOrCreateAppUser(HandlerUtils.buildUserCreateDto(textMessage));
        UserState userState = user.getState();

        String messageText = textMessage.text();
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
            throw new NodeInternalProcessingException("Unknown user state: " + userState);
        }

        userResponseService.sendUserResponse(user.getChatId(), answer);
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
            return BotResponse.INCORRECT_EMAIL_RESPONSE;
        }
        userService.setEmail(nodeUser, HandlerUtils.normalizeEmail(email));
        userService.changeState(nodeUser, BASIC_STATE);
        return BotResponse.EMAIL_CONFIRMATION_RESPONSE;
    }
}
