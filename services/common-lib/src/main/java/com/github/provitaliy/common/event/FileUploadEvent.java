package com.github.provitaliy.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadEvent {
    private String telegramFileId;
    private Long ownerId;
    private String fileName;
    private String mimeType;
    private Long fileSize;
}
