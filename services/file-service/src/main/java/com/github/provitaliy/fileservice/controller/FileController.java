package com.github.provitaliy.fileservice.controller;

import com.github.provitaliy.fileservice.entity.FileMetadata;
import com.github.provitaliy.fileservice.service.file.FileMetadataService;
import com.github.provitaliy.fileservice.service.file.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
@RequestMapping("${file-service.download-endpoint}")
@RestController
public class FileController {
    private final MinioStorageService storageService;
    private final FileMetadataService metadataService;

    @GetMapping("{uuid}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable String uuid) {
        FileMetadata metadata = metadataService.findMetadataByUuid(uuid);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metadata.getFileSize()))
                .contentType(MediaType.parseMediaType(metadata.getMimeType()))
                .body(outputStream -> {
                    try (var is = storageService.getFileStream(metadata.getObjectName())) {
                        is.transferTo(outputStream);
                    }
                });
    }
}
