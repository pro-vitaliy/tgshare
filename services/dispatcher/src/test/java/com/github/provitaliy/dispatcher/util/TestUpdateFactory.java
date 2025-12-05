package com.github.provitaliy.dispatcher.util;

import lombok.experimental.UtilityClass;
import net.datafaker.Faker;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;

import java.util.List;

@UtilityClass
public class TestUpdateFactory {
    private static final Faker faker = new Faker();

    public static Update textUpdate(String text) {
        var update = new Update();
        var message = randomTelegramMessage();
        message.setText(text);
        update.setMessage(message);
        return update;
    }

    public static Update docUpdate() {
        var update = new Update();
        var message = randomTelegramMessage();

        var document = Document.builder()
                .fileId("doc_" + faker.number().numberBetween(1, 1000))
                .fileName(faker.file().fileName())
                .mimeType("application/pdf")
                .build();

        message.setDocument(document);
        update.setMessage(message);
        return update;
    }

    public static Update photoUpdate() {
        var update = new Update();
        var message = randomTelegramMessage();

        var photo = PhotoSize.builder()
                .fileId("photo_" + faker.number().numberBetween(1, 1000))
                .width(faker.number().numberBetween(100, 2000))
                .height(faker.number().numberBetween(100, 2000))
                .fileSize(faker.number().numberBetween(1000, 500_000))
                .build();

        message.setPhoto(List.of(photo));
        update.setMessage(message);
        return update;
    }

    public static User randomTelegramUser() {
        return new User(
                faker.number().numberBetween(1L, 1000L),
                faker.name().firstName(),
                false
        );
    }

    public static Message randomTelegramMessage() {
        return Message.builder()
                .messageId(faker.number().numberBetween(1, 1000))
                .from(randomTelegramUser())
                .chat(Chat.builder()
                        .id(faker.number().numberBetween(1L, 1000L))
                        .type("private")
                        .build())
                .build();
    }
}
