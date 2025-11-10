package com.github.provitaliy.node.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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
