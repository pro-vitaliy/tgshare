package com.github.provitaliy.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailEvent {
    private String mailTo;
    private String subject;
    private String text;
}
