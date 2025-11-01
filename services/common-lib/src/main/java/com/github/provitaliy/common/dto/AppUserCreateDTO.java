package com.github.provitaliy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserCreateDTO {
    private Long telegramUserId;
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;
}
