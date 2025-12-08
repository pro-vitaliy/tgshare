package com.github.provitaliy.node.util;

import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramUserDto;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import lombok.experimental.UtilityClass;
import net.datafaker.Faker;

@UtilityClass
public class TestUtils {
    private static final Faker faker = new Faker();

    public static NodeUser getActivatedNodeUser() {
        return NodeUser.builder()
                .telegramUserId(faker.number().randomNumber())
                .chatId(faker.number().randomNumber())
                .username(faker.credentials().username())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .isActive(true)
                .state(UserState.BASIC_STATE)
                .build();
    }

    public TelegramTextMessageDto generateTextMessageDto(NodeUser user, String text) {
        return new TelegramTextMessageDto(
                user.getChatId(),
                generateTelegramUserDto(user),
                text
        );
    }

    public TelegramDocumentMessageDto generateDocMessageDto(NodeUser user) {
        return new TelegramDocumentMessageDto(
                user.getChatId(),
                generateTelegramUserDto(user),
                String.valueOf(faker.number().randomNumber()),
                faker.file().fileName(),
                faker.file().mimeType(),
                faker.number().randomNumber()
        );
    }

    public TelegramPhotoMessageDto generatePhotoMessageDto(NodeUser user) {
        return new TelegramPhotoMessageDto(
                user.getChatId(),
                generateTelegramUserDto(user),
                String.valueOf(faker.number().randomNumber()),
                faker.number().randomNumber()
        );
    }

    public static TelegramUserDto generateTelegramUserDto(NodeUser user) {
        return new TelegramUserDto(
                user.getTelegramUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername()
        );
    }
}
