package com.github.provitaliy.service;

import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
