package com.github.provitaliy.service.enums;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    public static ServiceCommands fromValue(String text) {
        for (var command : ServiceCommands.values()) {
            if (text.equalsIgnoreCase(command.value)) {
                return command;
            }
        }
        return null;
    }
}
