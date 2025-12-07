package com.github.provitaliy.userservice.util;

import com.github.provitaliy.common.event.UserEmailEnteredEvent;
import com.github.provitaliy.common.grpc.GetOrCreateAppUserRequest;
import com.github.provitaliy.userservice.entity.AppUser;
import lombok.experimental.UtilityClass;
import net.datafaker.Faker;

@UtilityClass
public class TestUtils {
    private static final Faker faker = new Faker();

    public static AppUser randomAppUser() {
        return AppUser.builder()
                .telegramUserId(faker.number().randomNumber())
                .chatId(faker.number().randomNumber())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.credentials().username())
                .unconfirmedEmail(faker.internet().emailAddress())
                .isActive(false)
                .build();
    }

    public static GetOrCreateAppUserRequest randomGetOrCreateAppUserRequest() {
        return GetOrCreateAppUserRequest.newBuilder()
                .setTelegramUserId(faker.number().randomNumber())
                .setChatId(faker.number().randomNumber())
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setUsername(faker.credentials().username())
                .build();
    }

    public static UserEmailEnteredEvent generateUserEmailEnteredEvent() {
        return new UserEmailEnteredEvent(
                faker.number().randomNumber(),
                faker.internet().emailAddress()
        );
    }
}
