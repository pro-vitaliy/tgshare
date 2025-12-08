package com.github.provitaliy.node.util;

import com.github.provitaliy.common.event.UserActivatedEvent;
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
}
