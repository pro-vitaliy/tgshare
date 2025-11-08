package com.github.provitaliy.fileservice.controller;

import com.github.provitaliy.fileservice.dto.FileDownloadDTO;
import com.github.provitaliy.fileservice.service.file.FileAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("${file-service.download-endpoint}")
@RestController
public class FileController {
    private final FileAccessService accessService;

    @GetMapping("{uuid}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String uuid) {
        FileDownloadDTO downloadDto = accessService.getFileByUuid(uuid);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadDto.fileName() + "\"")
                .contentType(MediaType.parseMediaType(downloadDto.mimeType()))
                .body(downloadDto.fileBytes());
    }
}
