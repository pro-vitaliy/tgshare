package com.github.provitaliy.node.bot;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    public static ServiceCommand fromValue(String text) {

        if (text == null) {
            return null;
        }

        for (var command : ServiceCommand.values()) {
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
