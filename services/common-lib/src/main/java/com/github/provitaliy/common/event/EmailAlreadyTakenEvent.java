package com.github.provitaliy.common.event;

public record EmailAlreadyTakenEvent(
    Long telegramUserId,
    String email
) { }
