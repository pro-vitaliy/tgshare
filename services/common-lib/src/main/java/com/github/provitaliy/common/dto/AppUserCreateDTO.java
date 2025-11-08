package com.github.provitaliy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
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
