package com.github.provitaliy.node.aspect;

import com.github.provitaliy.common.dto.telegram.TelegramMessage;
import com.github.provitaliy.node.exception.NodeInternalProcessingException;
import com.github.provitaliy.node.exception.UserServiceProcessingException;
import com.github.provitaliy.node.exception.UserServiceUnavailableException;
import com.github.provitaliy.node.service.UserResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class MessageHandlerExceptionAspect {
    private final UserResponseService userResponseService;

    @Around("@annotation(com.github.provitaliy.node.aspect.TelegramMessageListener) && args(message)")
    public Object wrapMessageListeners(ProceedingJoinPoint joinPoint, TelegramMessage message) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            handleException(e, message);
            return null;
        }
    }

    private void handleException(Exception e, TelegramMessage message) {
        log.error(
                "Error while processing message. userId={}, chatId={}. Exception: {}",
                message.from().id(),
                message.chatId(),
                e.getMessage(),
                e
        );

        String userMessage = mapExceptionToUserMessage(e);
        userResponseService.sendUserResponse(message.chatId(), userMessage);
        sendAlert(e);
    }

    private String mapExceptionToUserMessage(Exception e) {
        if (e instanceof UserServiceUnavailableException) {
            return "Сервис временно недоступен, попробуйте позже.";
        }
        if (e instanceof UserServiceProcessingException) {
            return "Произошла ошибка при обработке вашего запроса.";
        }
        if (e instanceof NodeInternalProcessingException) {
            return "Внутренняя ошибка сервиса.";
        }
        return "Неизвестная ошибка.";
    }

    private void sendAlert(Exception e) {
//        future integration with the audit/alert service
    }
}
