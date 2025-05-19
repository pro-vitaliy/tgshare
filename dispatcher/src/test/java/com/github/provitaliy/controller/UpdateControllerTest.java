package com.github.provitaliy.controller;

import com.github.provitaliy.configuration.QueueProperties;
import com.github.provitaliy.service.UpdateProducer;
import com.github.provitaliy.utils.MessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

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

    @Mock
    private QueueProperties queueProperties;

    @InjectMocks
    private UpdateController updateController;

    @Test
    void shouldProcessTextMessageAndSendResponse() throws Exception {
        Update update = new Update();
        Message message = new Message();
        message.setText("Hello");
        update.setMessage(message);

        SendMessage sendMessage = new SendMessage("123", "File received. Processing in progress");

        when(queueProperties.getTextMessageUpdate()).thenReturn("queue.text");
        when(messageUtils.generateSendMessageWithText(update, "File received. Processing in progress"))
                .thenReturn(sendMessage);

        updateController.processUpdate(update);

        verify(updateProducer).produce("queue.text", update);
        verify(telegramClient).execute(sendMessage);
    }

    @Test
    void shouldIgnoreNullUpdate() {

        updateController.processUpdate(null);

        verifyNoInteractions(updateProducer, messageUtils, telegramClient);
    }

}
