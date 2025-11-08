package com.github.provitaliy.fileservice.dto;

public record FileDownloadDTO(
        String fileName,
        String mimeType,
        byte[] fileBytes
) { }
