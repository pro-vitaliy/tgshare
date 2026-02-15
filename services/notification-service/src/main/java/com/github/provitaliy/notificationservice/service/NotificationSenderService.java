package com.github.provitaliy.notificationservice.service;

public interface NotificationSenderService {
        void send(String recipient, String subject, String body);
}
