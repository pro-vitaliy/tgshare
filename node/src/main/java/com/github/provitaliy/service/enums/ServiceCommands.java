package com.github.provitaliy.service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    public static ServiceCommands fromValue(String text) {

        if (text == null) {
            return null;
        }

        for (var command : ServiceCommands.values()) {
            if (text.equalsIgnoreCase(command.value)) {
                return command;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
