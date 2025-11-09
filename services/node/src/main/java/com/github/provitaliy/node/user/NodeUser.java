package com.github.provitaliy.node.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NodeUser {
    private Long telegramUserId;
    private Long chatId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isActive;
    private UserState state;
}
