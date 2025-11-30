package com.github.provitaliy.common.messaging;

public class RoutingKeys {
    public static final String ROUTING_KEY_TEXT_MESSAGE_UPDATE = "routing.text";
    public static final String ROUTING_KEY_DOC_MESSAGE_UPDATE = "routing.document";
    public static final String ROUTING_KEY_PHOTO_MESSAGE_UPDATE = "routing.photo";

    public static final String ROUTING_KEY_ANSWER_MESSAGE = "routing.answer";

    public static final String ROUTING_KEY_USER_EMAIL_ENTERED = "routing.user.email.entered";
    public static final String ROUTING_KEY_USER_EMAIL_ENTERED_DLQ = "routing.user.email.entered.dlq";
    public static final String ROUTING_KEY_EMAIL_ALREADY_TAKEN = "routing.email.already.taken";
    public static final String ROUTING_KEY_EMAIL_SEND = "routing.email.send";

    public static final String ROUTING_KEY_FILE_UPLOAD_REQUEST = "routing.file.upload.request";
    public static final String ROUTING_KEY_FILE_UPLOAD_FAILED = "routing.file.upload.failed";
    public static final String ROUTING_KEY_FILE_READY = "routing.file.ready";
    public static final String ROUTING_KEY_FILE_READY_DLQ = "routing.file.ready.dlq";

    public static final String ROUTING_KEY_USER_ACTIVATED = "routing.user.activated";
    public static final String ROUTING_KEY_USER_ACTIVATED_DLQ = "routing.user.activated.dlq";
}
