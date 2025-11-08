package com.github.provitaliy.fileservice.service.file;

import com.github.provitaliy.fileservice.entity.FileMetadata;
import com.github.provitaliy.fileservice.exception.FileMetadataNotFoundException;
import com.github.provitaliy.fileservice.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileMetadataService {
    private final FileMetadataRepository repository;

    public FileMetadata findMetadataByUuid(String uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> new FileMetadataNotFoundException("No metadata found with uuid=" + uuid));
    }

    public FileMetadata saveMetadata(
            Long ownerId,
            String fileName,
            String mimeType,
            Long fileSize,
            String bucket,
            String objectName,
            Duration ttl
    ) {
        String uuid = UUID.randomUUID().toString();
        Instant uploadedAt = Instant.now();
        Instant expiresAt = uploadedAt.plus(ttl);
        FileMetadata metadata = FileMetadata.builder()
                .ownerId(ownerId)
                .fileName(fileName)
                .mimeType(mimeType)
                .fileSize(fileSize)
                .bucket(bucket)
                .objectName(objectName)
                .uploadedAt(uploadedAt)
                .expiresAt(expiresAt)
                .build();

        repository.save(metadata);
        log.info("Сохранена метадата файла: uuid={}, fileName={}", uuid, fileName);
        return metadata;
    }

    public void deleteMetadata(Long id) {
        if (!repository.existsById(id)) {
            log.warn("No metadata found with id={} for deletion", id);
            return;
        }
        repository.deleteById(id);
        log.info("Metadata with id={} deleted", id);
    }

}
