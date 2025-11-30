package com.github.provitaliy.common.messaging;

public class QueueNames {
    public static final String TEXT_MESSAGE_UPDATE_QUEUE = "rabbit.text.message.update.queue";
    public static final String DOC_MESSAGE_UPDATE_QUEUE = "rabbit.doc.message.update.queue";
    public static final String PHOTO_MESSAGE_UPDATE_QUEUE = "rabbit.photo.message.update.queue";

    public static final String ANSWER_MESSAGE_QUEUE = "rabbit.answer.message.queue";

    public static final String USER_EMAIL_ENTERED_QUEUE = "rabbit.user.email.entered.queue";
    public static final String USER_EMAIL_ENTERED_DLQ = "rabbit.user.email.entered.dlq";
    public static final String EMAIL_ALREADY_TAKEN_QUEUE = "rabbit.email.already.taken.queue";
    public static final String EMAIL_SEND_QUEUE = "rabbit.email.send.queue";

    public static final String FILE_UPLOAD_REQUEST_QUEUE = "rabbit.file.upload.request.queue";
    public static final String FiLE_UPLOAD_FAILED_QUEUE = "rabbit.file.upload.failed.queue";
    public static final String FILE_READY_QUEUE = "rabbit.file.ready.queue";
    public static final String FILE_READY_DLQ = "rabbit.file.ready.dlq";

    public static final String USER_ACTIVATED_QUEUE = "rabbit.user.activated.queue";
    public static final String USER_ACTIVATED_DLQ = "rabbit.user.activated.dlq";
}
