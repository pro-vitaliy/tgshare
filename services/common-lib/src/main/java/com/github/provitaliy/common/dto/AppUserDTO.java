package com.github.provitaliy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDTO {
    private Long id;
    private Long telegramUserId;
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime firstLoginDate;
}
