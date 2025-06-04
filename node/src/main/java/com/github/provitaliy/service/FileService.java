package com.github.provitaliy.service;

import com.github.provitaliy.entity.AppDocument;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
}
