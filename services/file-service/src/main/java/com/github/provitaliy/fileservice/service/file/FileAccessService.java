package com.github.provitaliy.fileservice.service.file;

import com.github.provitaliy.fileservice.dto.FileDownloadDTO;
import com.github.provitaliy.fileservice.entity.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileAccessService {
    private final FileMetadataService metadataService;
    private final MinioStorageService storageService;

    public FileDownloadDTO getFileByUuid(String uuid) {

//        TODO: перейти на потоковую передачу файлов

        FileMetadata metadata = metadataService.findMetadataByUuid(uuid);
        byte[] file = storageService.downloadFile(metadata.getObjectName());
        return new FileDownloadDTO(
                metadata.getFileName(),
                metadata.getMimeType(),
                file
        );
    }
}
