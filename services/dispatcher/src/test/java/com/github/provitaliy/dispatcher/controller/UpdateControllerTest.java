package com.github.provitaliy.dispatcher.controller;

import com.github.provitaliy.dispatcher.service.UpdateProducer;
import com.github.provitaliy.dispatcher.utils.MessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateControllerTest {
    @Mock
    private MessageUtils messageUtils;

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private UpdateProducer updateProducer;

    @InjectMocks
    private UpdateController updateController;

    @Captor
    private ArgumentCaptor<Update> updateCaptor;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Test
    void shouldProduceTextMessageToQueue() throws Exception {
        Update textUpdate = new Update();
        Message textMessage = new Message();
        textMessage.setText("Hello");
        textUpdate.setMessage(textMessage);
        String textQueue = "text.queue";

        updateController.processUpdate(textUpdate);

        verify(updateProducer).produceTextMessageUpdate(updateCaptor.capture());

        Update updateToProduce = updateCaptor.getValue();

        assertEquals(textUpdate, updateToProduce);
    }

    @Test
    void shouldProduceDocMessageToQueue() {
        Update docUpdate = new Update();
        Message docMessage = new Message();
        docMessage.setDocument(new Document());
        docUpdate.setMessage(docMessage);

        String docQueue = "doc.queue";

        updateController.processUpdate(docUpdate);

        verify(updateProducer).produceDocMessageUpdate(updateCaptor.capture());

        Update updateToProduce = updateCaptor.getValue();

        assertEquals(docUpdate, updateToProduce);
    }

    @Test
    void shouldProducePhotoMessageToQueue() {
        Update photoUpdate = new Update();
        Message photoMessage = new Message();
        List<PhotoSize> photos = List.of(new PhotoSize());
        photoMessage.setPhoto(photos);
        photoUpdate.setMessage(photoMessage);

        String photoQueue = "photo.queue";

        updateController.processUpdate(photoUpdate);

        verify(updateProducer).producePhotoMessageUpdate(updateCaptor.capture());
        Update updateToProduce = updateCaptor.getValue();

        assertEquals(photoUpdate, updateToProduce);
    }

    @Test
    void shouldSendUnsupportedMessageWarningViaTelegram() throws Exception {
        Update unsupportedUpdate = new Update();
        Message message = new Message();
        unsupportedUpdate.setMessage(message);

        String response = "Неподдерживаемый тип сообщения";
        SendMessage sendMessage = SendMessage.builder()
                .text(response)
                .chatId(123L)
                .build();

        when(messageUtils.generateSendMessageWithText(unsupportedUpdate, response))
                .thenReturn(sendMessage);

        updateController.processUpdate(unsupportedUpdate);
        verify(telegramClient).execute(sendMessageCaptor.capture());
        SendMessage messageToSend = sendMessageCaptor.getValue();
        assertEquals(response, messageToSend.getText());
    }

    @Test
    void shouldIgnoreNullUpdate() {
        updateController.processUpdate(null);
        verifyNoInteractions(updateProducer, messageUtils, telegramClient);
    }
}
